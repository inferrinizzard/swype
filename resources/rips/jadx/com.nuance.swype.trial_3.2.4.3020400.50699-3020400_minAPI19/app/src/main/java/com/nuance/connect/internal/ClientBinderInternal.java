package com.nuance.connect.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.util.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ClientBinderInternal implements ClientBinder {
    private static final String SERVICE_NAME = "com.nuance.connect.service.ConnectClient";
    private final Context context;
    private final ClientBinderLifecycleCallback lifecycleCallback;
    private final Handler mInboundHandler;
    private final Messenger mInboundMessenger;
    private volatile Messenger mOutboundMessenger;
    private final Intent service;
    private final ConnectServiceManager serviceManager;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ClientBinderInternal.class.getSimpleName());
    private final AtomicBoolean connectBound = new AtomicBoolean(false);
    private final AtomicBoolean connectBinding = new AtomicBoolean(false);
    private final AtomicBoolean restart = new AtomicBoolean(false);
    private final AtomicBoolean serviceReady = new AtomicBoolean(false);
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue();
    private final BlockingQueue<Message> priorityMessageQueue = new LinkedBlockingQueue();
    private volatile long lastMessageSent = Long.MIN_VALUE;
    private final int[] rwLock = new int[0];
    private ServiceConnection mConnectConnection = new ServiceConnection() { // from class: com.nuance.connect.internal.ClientBinderInternal.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null) {
                ClientBinderInternal.this.setConnectBoundState(false);
                return;
            }
            try {
                ClientBinderInternal.this.mOutboundMessenger = new Messenger(iBinder);
                ClientBinderInternal.this.setConnectBoundState(true);
            } catch (NullPointerException e) {
                ClientBinderInternal.this.setConnectBoundState(false);
            }
            if (ClientBinderInternal.this.connectBound.get()) {
                ClientBinderInternal.this.lifecycleCallback.onBound();
                ClientBinderInternal.this.restart.set(false);
                ClientBinderInternal.this.processPriorityQueuedMessages();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            ClientBinderInternal.this.mOutboundMessenger = null;
            ClientBinderInternal.this.setConnectBoundState(false);
            ClientBinderInternal.this.lifecycleCallback.onUnbound();
        }
    };

    public ClientBinderInternal(Context context, ConnectServiceManager connectServiceManager, Handler handler, ClientBinderLifecycleCallback clientBinderLifecycleCallback) {
        this.context = context;
        this.serviceManager = connectServiceManager;
        this.service = new Intent().setClassName(context, "com.nuance.connect.service.ConnectClient");
        this.mInboundHandler = handler;
        this.mInboundMessenger = new Messenger(this.mInboundHandler);
        this.lifecycleCallback = clientBinderLifecycleCallback;
    }

    private void bind() {
        boolean z = true;
        if (this.connectBound.get() || this.connectBinding.get()) {
            return;
        }
        this.connectBinding.set(true);
        try {
            this.context.startService(this.service);
            this.context.bindService(this.service, this.mConnectConnection, 0);
            PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 4);
            if (packageInfo != null && packageInfo.services != null) {
                ServiceInfo[] serviceInfoArr = packageInfo.services;
                for (ServiceInfo serviceInfo : serviceInfoArr) {
                    if ("com.nuance.connect.service.ConnectClient".equals(serviceInfo.name)) {
                        break;
                    }
                }
            }
            z = false;
            if (z) {
                return;
            }
            this.log.e("Did not find the connect service.  Please verify the service is your AndroidManifest.xml");
        } catch (PackageManager.NameNotFoundException e) {
            this.log.w("Failure in binding: " + e.getMessage());
        } catch (SecurityException e2) {
            setConnectBoundState(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processPriorityQueuedMessages() {
        ArrayList arrayList = new ArrayList();
        this.priorityMessageQueue.drainTo(arrayList);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            sendConnectPriorityMessage((Message) it.next());
        }
    }

    private void sendUnregisterClient() {
        if (this.connectBound.get()) {
            sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_UNREGISTER_CLIENT);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setConnectBoundState(boolean z) {
        this.connectBound.set(z);
        this.connectBinding.set(false);
        this.serviceReady.set(false);
    }

    private void unbind() {
        if (this.connectBound.get() || this.connectBinding.get()) {
            this.context.unbindService(this.mConnectConnection);
            setConnectBoundState(false);
            this.log.d("unbind() serviceWasRunning=", Boolean.valueOf(this.context.stopService(this.service)));
        }
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public long getLastMessageSent() {
        return this.lastMessageSent;
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public int[] getLock() {
        return this.rwLock;
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean isBound() {
        return this.connectBound.get();
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public void pause() {
        this.restart.set(true);
        sendUnregisterClient();
        unbind();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processQueuedMessages() {
        if (!this.priorityMessageQueue.isEmpty()) {
            this.log.w("priorityMessageQueue is not empty... processing queue");
            processPriorityQueuedMessages();
        }
        ArrayList arrayList = new ArrayList();
        this.messageQueue.drainTo(arrayList);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            sendConnectMessage((Message) it.next());
        }
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public void restart() {
        if (this.connectBound.get()) {
            return;
        }
        bind();
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectMessage(Message message) {
        return sendMessage(message, false);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectMessage(InternalMessages internalMessages) {
        return sendMessage(internalMessages, null, Integers.STATUS_SUCCESS, Integers.STATUS_SUCCESS, false);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectMessage(InternalMessages internalMessages, Object obj) {
        return sendMessage(internalMessages, obj, Integers.STATUS_SUCCESS, Integers.STATUS_SUCCESS, false);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectMessage(InternalMessages internalMessages, Object obj, int i, int i2) {
        return sendMessage(internalMessages, obj, i, i2, false);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectPriorityMessage(Message message) {
        return sendMessage(message, true);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectPriorityMessage(InternalMessages internalMessages) {
        return sendMessage(internalMessages, null, Integers.STATUS_SUCCESS, Integers.STATUS_SUCCESS, true);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectPriorityMessage(InternalMessages internalMessages, Object obj) {
        return sendMessage(internalMessages, obj, Integers.STATUS_SUCCESS, Integers.STATUS_SUCCESS, true);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public boolean sendConnectPriorityMessage(InternalMessages internalMessages, Object obj, int i, int i2) {
        return sendMessage(internalMessages, obj, i, i2, true);
    }

    protected boolean sendMessage(Message message, boolean z) {
        boolean z2 = false;
        synchronized (getLock()) {
            this.lastMessageSent = System.currentTimeMillis();
            this.log.v("sendMessage: ", InternalMessages.fromInt(message.what).toString(), " priority:", Boolean.valueOf(z));
            if (this.restart.get() && (!this.connectBound.get() || this.mOutboundMessenger == null)) {
                bind();
            }
            if (!this.serviceManager.isLicensed()) {
                if (z) {
                    this.log.v("Priority Message allowed, but, build is unlicensed");
                    this.serviceManager.notifyConnectionStatus(14, "Build is unlicensed.");
                } else {
                    this.log.v("Message dropped, build is unlicensed");
                }
            }
            if ((this.serviceReady.get() || z) && this.connectBound.get() && this.mOutboundMessenger != null) {
                try {
                    message.replyTo = this.mInboundMessenger;
                    this.mOutboundMessenger.send(message);
                    z2 = true;
                } catch (RemoteException e) {
                    this.log.e("RemoteException re=" + e.getMessage());
                    setConnectBoundState(false);
                } catch (NullPointerException e2) {
                    this.log.e("NullPointerException npe=" + e2.getMessage());
                } catch (Exception e3) {
                    setConnectBoundState(false);
                    this.log.e("Exception e=" + e3.getMessage());
                }
            }
            if (!z2) {
                if (z) {
                    this.priorityMessageQueue.add(message);
                } else {
                    this.messageQueue.add(message);
                }
            }
        }
        return z2;
    }

    protected boolean sendMessage(InternalMessages internalMessages, Object obj, int i, int i2, boolean z) {
        Message obtain = Message.obtain((Handler) null, internalMessages.ordinal());
        try {
            if (obj instanceof HashMap) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Strings.DEFAULT_KEY, (HashMap) obj);
                obtain.setData(bundle);
            } else if (obj instanceof Map) {
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(Strings.DEFAULT_KEY, (Serializable) obj);
                obtain.setData(bundle2);
            } else if (obj instanceof String) {
                Bundle bundle3 = new Bundle();
                bundle3.putString(Strings.DEFAULT_KEY, (String) obj);
                obtain.setData(bundle3);
            } else if (obj instanceof Boolean) {
                Bundle bundle4 = new Bundle();
                bundle4.putBoolean(Strings.DEFAULT_KEY, ((Boolean) obj).booleanValue());
                obtain.setData(bundle4);
            } else if (obj instanceof Integer) {
                Bundle bundle5 = new Bundle();
                bundle5.putInt(Strings.DEFAULT_KEY, ((Integer) obj).intValue());
                obtain.setData(bundle5);
            } else if (obj instanceof Bundle) {
                obtain.setData((Bundle) obj);
            }
        } catch (Exception e) {
        }
        obtain.arg1 = i;
        obtain.arg2 = i2;
        return sendMessage(obtain, z);
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public void setClientComplete() {
        if (this.connectBound.get()) {
            this.serviceReady.set(true);
        }
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public void start() {
        bind();
    }

    @Override // com.nuance.connect.internal.ClientBinder
    public void stop() {
        this.restart.set(false);
        sendUnregisterClient();
        unbind();
    }
}
