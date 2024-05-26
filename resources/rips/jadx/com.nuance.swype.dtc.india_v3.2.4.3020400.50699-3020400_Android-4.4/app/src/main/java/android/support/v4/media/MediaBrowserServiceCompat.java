package android.support.v4.media;

import android.app.Service;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class MediaBrowserServiceCompat extends Service {
    private static final boolean DEBUG = Log.isLoggable("MBServiceCompat", 3);
    private ConnectionRecord mCurConnection;
    MediaSessionCompat.Token mSession;
    private final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap<>();
    private final ServiceHandler mHandler = new ServiceHandler(this, 0);

    /* loaded from: classes.dex */
    public static final class BrowserRoot {
        final Bundle mExtras;
        final String mRootId;
    }

    /* loaded from: classes.dex */
    private interface ServiceCallbacks {
        IBinder asBinder();

        void onConnect(String str, MediaSessionCompat.Token token, Bundle bundle) throws RemoteException;

        void onConnectFailed() throws RemoteException;

        void onLoadChildren(String str, List<Object> list, Bundle bundle) throws RemoteException;
    }

    public abstract BrowserRoot onGetRoot$16f11348();

    /* loaded from: classes.dex */
    private final class ServiceHandler extends Handler {
        private final ServiceBinderImpl mServiceBinderImpl;

        private ServiceHandler() {
            this.mServiceBinderImpl = new ServiceBinderImpl(MediaBrowserServiceCompat.this, (byte) 0);
        }

        /* synthetic */ ServiceHandler(MediaBrowserServiceCompat x0, byte b) {
            this();
        }

        @Override // android.os.Handler
        public final void handleMessage(Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    final ServiceBinderImpl serviceBinderImpl = this.mServiceBinderImpl;
                    final String string = data.getString("data_package_name");
                    final int i = data.getInt("data_calling_uid");
                    final Bundle bundle = data.getBundle("data_root_hints");
                    final ServiceCallbacksCompat serviceCallbacksCompat = new ServiceCallbacksCompat(msg.replyTo);
                    if (MediaBrowserServiceCompat.access$600(MediaBrowserServiceCompat.this, string, i)) {
                        MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                IBinder b = serviceCallbacksCompat.asBinder();
                                MediaBrowserServiceCompat.this.mConnections.remove(b);
                                ConnectionRecord connection = new ConnectionRecord(MediaBrowserServiceCompat.this, (byte) 0);
                                connection.pkg = string;
                                connection.rootHints = bundle;
                                connection.callbacks = serviceCallbacksCompat;
                                connection.root = MediaBrowserServiceCompat.this.onGetRoot$16f11348();
                                if (connection.root != null) {
                                    try {
                                        MediaBrowserServiceCompat.this.mConnections.put(b, connection);
                                        if (MediaBrowserServiceCompat.this.mSession != null) {
                                            serviceCallbacksCompat.onConnect(connection.root.mRootId, MediaBrowserServiceCompat.this.mSession, connection.root.mExtras);
                                            return;
                                        }
                                        return;
                                    } catch (RemoteException e) {
                                        Log.w("MBServiceCompat", "Calling onConnect() failed. Dropping client. pkg=" + string);
                                        MediaBrowserServiceCompat.this.mConnections.remove(b);
                                        return;
                                    }
                                }
                                Log.i("MBServiceCompat", "No root for client " + string + " from service " + getClass().getName());
                                try {
                                    serviceCallbacksCompat.onConnectFailed();
                                } catch (RemoteException e2) {
                                    Log.w("MBServiceCompat", "Calling onConnectFailed() failed. Ignoring. pkg=" + string);
                                }
                            }
                        });
                        return;
                    }
                    throw new IllegalArgumentException("Package/uid mismatch: uid=" + i + " package=" + string);
                case 2:
                    final ServiceBinderImpl serviceBinderImpl2 = this.mServiceBinderImpl;
                    final ServiceCallbacksCompat serviceCallbacksCompat2 = new ServiceCallbacksCompat(msg.replyTo);
                    MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.2
                        @Override // java.lang.Runnable
                        public final void run() {
                            IBinder b = serviceCallbacksCompat2.asBinder();
                            MediaBrowserServiceCompat.this.mConnections.remove(b);
                        }
                    });
                    return;
                case 3:
                    final ServiceBinderImpl serviceBinderImpl3 = this.mServiceBinderImpl;
                    final String string2 = data.getString("data_media_item_id");
                    final IBinder binder = BundleCompat.getBinder(data, "data_callback_token");
                    final Bundle bundle2 = data.getBundle("data_options");
                    final ServiceCallbacksCompat serviceCallbacksCompat3 = new ServiceCallbacksCompat(msg.replyTo);
                    MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.3
                        /* JADX WARN: Multi-variable type inference failed */
                        @Override // java.lang.Runnable
                        public final void run() {
                            IBinder b = serviceCallbacksCompat3.asBinder();
                            ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(b);
                            if (connection == null) {
                                Log.w("MBServiceCompat", "addSubscription for callback that isn't registered id=" + string2);
                            } else {
                                MediaBrowserServiceCompat.access$800(MediaBrowserServiceCompat.this, string2, connection, binder, bundle2);
                            }
                        }
                    });
                    return;
                case 4:
                    final ServiceBinderImpl serviceBinderImpl4 = this.mServiceBinderImpl;
                    final String string3 = data.getString("data_media_item_id");
                    final IBinder binder2 = BundleCompat.getBinder(data, "data_callback_token");
                    final ServiceCallbacksCompat serviceCallbacksCompat4 = new ServiceCallbacksCompat(msg.replyTo);
                    MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.4
                        /* JADX WARN: Multi-variable type inference failed */
                        @Override // java.lang.Runnable
                        public final void run() {
                            IBinder b = serviceCallbacksCompat4.asBinder();
                            ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(b);
                            if (connection == null) {
                                Log.w("MBServiceCompat", "removeSubscription for callback that isn't registered id=" + string3);
                            } else if (!MediaBrowserServiceCompat.access$900$5dd6c079(string3, connection, binder2)) {
                                Log.w("MBServiceCompat", "removeSubscription called for " + string3 + " which is not subscribed");
                            }
                        }
                    });
                    return;
                case 5:
                    final ServiceBinderImpl serviceBinderImpl5 = this.mServiceBinderImpl;
                    final String string4 = data.getString("data_media_item_id");
                    final ResultReceiver resultReceiver = (ResultReceiver) data.getParcelable("data_result_receiver");
                    final ServiceCallbacksCompat serviceCallbacksCompat5 = new ServiceCallbacksCompat(msg.replyTo);
                    if (!TextUtils.isEmpty(string4) && resultReceiver != null) {
                        MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.5
                            /* JADX WARN: Multi-variable type inference failed */
                            @Override // java.lang.Runnable
                            public final void run() {
                                IBinder b = serviceCallbacksCompat5.asBinder();
                                ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(b);
                                if (connection == null) {
                                    Log.w("MBServiceCompat", "getMediaItem for callback that isn't registered id=" + string4);
                                } else {
                                    MediaBrowserServiceCompat.access$1000(MediaBrowserServiceCompat.this, string4, connection, resultReceiver);
                                }
                            }
                        });
                        return;
                    }
                    return;
                case 6:
                    final ServiceBinderImpl serviceBinderImpl6 = this.mServiceBinderImpl;
                    final ServiceCallbacksCompat serviceCallbacksCompat6 = new ServiceCallbacksCompat(msg.replyTo);
                    final Bundle bundle3 = data.getBundle("data_root_hints");
                    MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.6
                        @Override // java.lang.Runnable
                        public final void run() {
                            IBinder b = serviceCallbacksCompat6.asBinder();
                            MediaBrowserServiceCompat.this.mConnections.remove(b);
                            ConnectionRecord connection = new ConnectionRecord(MediaBrowserServiceCompat.this, (byte) 0);
                            connection.callbacks = serviceCallbacksCompat6;
                            connection.rootHints = bundle3;
                            MediaBrowserServiceCompat.this.mConnections.put(b, connection);
                        }
                    });
                    return;
                case 7:
                    final ServiceBinderImpl serviceBinderImpl7 = this.mServiceBinderImpl;
                    final ServiceCallbacksCompat serviceCallbacksCompat7 = new ServiceCallbacksCompat(msg.replyTo);
                    MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() { // from class: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinderImpl.7
                        @Override // java.lang.Runnable
                        public final void run() {
                            IBinder b = serviceCallbacksCompat7.asBinder();
                            MediaBrowserServiceCompat.this.mConnections.remove(b);
                        }
                    });
                    return;
                default:
                    Log.w("MBServiceCompat", "Unhandled message: " + msg + "\n  Service version: 1\n  Client version: " + msg.arg1);
                    return;
            }
        }

        @Override // android.os.Handler
        public final boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            Bundle data = msg.getData();
            data.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            data.putInt("data_calling_uid", Binder.getCallingUid());
            return super.sendMessageAtTime(msg, uptimeMillis);
        }

        private void postOrRun(Runnable r) {
            if (Thread.currentThread() == getLooper().getThread()) {
                r.run();
            } else {
                post(r);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectionRecord {
        ServiceCallbacks callbacks;
        String pkg;
        BrowserRoot root;
        Bundle rootHints;
        HashMap<String, List<Pair<IBinder, Bundle>>> subscriptions;

        private ConnectionRecord() {
            this.subscriptions = new HashMap<>();
        }

        /* synthetic */ ConnectionRecord(MediaBrowserServiceCompat x0, byte b) {
            this();
        }
    }

    /* loaded from: classes.dex */
    public static class Result<T> {
        Object mDebug;
        private boolean mDetachCalled;
        int mFlags;
        boolean mSendResultCalled;

        Result(Object debug) {
            this.mDebug = debug;
        }

        final boolean isDone() {
            return this.mDetachCalled || this.mSendResultCalled;
        }

        void onResultSent$4cfcfd12(int flags) {
        }
    }

    /* loaded from: classes.dex */
    private class ServiceBinderImpl {
        private ServiceBinderImpl() {
        }

        /* synthetic */ ServiceBinderImpl(MediaBrowserServiceCompat x0, byte b) {
            this();
        }
    }

    /* loaded from: classes.dex */
    private class ServiceCallbacksCompat implements ServiceCallbacks {
        final Messenger mCallbacks;

        ServiceCallbacksCompat(Messenger callbacks) {
            this.mCallbacks = callbacks;
        }

        @Override // android.support.v4.media.MediaBrowserServiceCompat.ServiceCallbacks
        public final IBinder asBinder() {
            return this.mCallbacks.getBinder();
        }

        @Override // android.support.v4.media.MediaBrowserServiceCompat.ServiceCallbacks
        public final void onConnect(String root, MediaSessionCompat.Token session, Bundle extras) throws RemoteException {
            if (extras == null) {
                extras = new Bundle();
            }
            extras.putInt("extra_service_version", 1);
            Bundle data = new Bundle();
            data.putString("data_media_item_id", root);
            data.putParcelable("data_media_session_token", session);
            data.putBundle("data_root_hints", extras);
            sendRequest(1, data);
        }

        @Override // android.support.v4.media.MediaBrowserServiceCompat.ServiceCallbacks
        public final void onConnectFailed() throws RemoteException {
            sendRequest(2, null);
        }

        @Override // android.support.v4.media.MediaBrowserServiceCompat.ServiceCallbacks
        public final void onLoadChildren(String mediaId, List<Object> list, Bundle options) throws RemoteException {
            Bundle data = new Bundle();
            data.putString("data_media_item_id", mediaId);
            data.putBundle("data_options", options);
            if (list != null) {
                data.putParcelableArrayList("data_media_item_list", list instanceof ArrayList ? (ArrayList) list : new ArrayList<>(list));
            }
            sendRequest(3, data);
        }

        private void sendRequest(int what, Bundle data) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = what;
            msg.arg1 = 1;
            msg.setData(data);
            this.mCallbacks.send(msg);
        }
    }

    static /* synthetic */ boolean access$600(MediaBrowserServiceCompat x0, String x1, int x2) {
        if (x1 == null) {
            return false;
        }
        for (String str : x0.getPackageManager().getPackagesForUid(x2)) {
            if (str.equals(x1)) {
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ void access$800(MediaBrowserServiceCompat x0, final String x1, final ConnectionRecord x2, IBinder x3, final Bundle x4) {
        boolean z;
        List<Pair<IBinder, Bundle>> list = x2.subscriptions.get(x1);
        List<Pair<IBinder, Bundle>> arrayList = list == null ? new ArrayList() : list;
        for (Pair<IBinder, Bundle> pair : arrayList) {
            if (x3 == pair.first) {
                Bundle bundle = pair.second;
                if (x4 == bundle) {
                    z = true;
                } else if (x4 == null) {
                    z = bundle.getInt("android.media.browse.extra.PAGE", -1) == -1 && bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1;
                } else if (bundle == null) {
                    z = x4.getInt("android.media.browse.extra.PAGE", -1) == -1 && x4.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1;
                } else {
                    z = x4.getInt("android.media.browse.extra.PAGE", -1) == bundle.getInt("android.media.browse.extra.PAGE", -1) && x4.getInt("android.media.browse.extra.PAGE_SIZE", -1) == bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
                }
                if (z) {
                    return;
                }
            }
        }
        arrayList.add(new Pair<>(x3, x4));
        x2.subscriptions.put(x1, arrayList);
        Result<List<Object>> result = new Result<List<Object>>(x1) { // from class: android.support.v4.media.MediaBrowserServiceCompat.1
            @Override // android.support.v4.media.MediaBrowserServiceCompat.Result
            final /* bridge */ /* synthetic */ void onResultSent$4cfcfd12(int i) {
                if (MediaBrowserServiceCompat.this.mConnections.get(x2.callbacks.asBinder()) != x2) {
                    if (MediaBrowserServiceCompat.DEBUG) {
                        new StringBuilder("Not sending onLoadChildren result for connection that has been disconnected. pkg=").append(x2.pkg).append(" id=").append(x1);
                    }
                } else {
                    try {
                        x2.callbacks.onLoadChildren(x1, (i & 1) != 0 ? MediaBrowserServiceCompat.access$1200$1100aaef(null, x4) : null, x4);
                    } catch (RemoteException e) {
                        Log.w("MBServiceCompat", "Calling onLoadChildren() failed for id=" + x1 + " package=" + x2.pkg);
                    }
                }
            }
        };
        x0.mCurConnection = x2;
        if (x4 != null) {
            result.mFlags = 1;
        }
        x0.mCurConnection = null;
        if (!result.isDone()) {
            throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + x2.pkg + " id=" + x1);
        }
    }

    static /* synthetic */ boolean access$900$5dd6c079(String x1, ConnectionRecord x2, IBinder x3) {
        boolean z;
        boolean z2 = false;
        if (x3 == null) {
            return x2.subscriptions.remove(x1) != null;
        }
        List<Pair<IBinder, Bundle>> list = x2.subscriptions.get(x1);
        if (list != null) {
            Iterator<Pair<IBinder, Bundle>> it = list.iterator();
            while (true) {
                z = z2;
                if (!it.hasNext()) {
                    break;
                }
                Pair<IBinder, Bundle> next = it.next();
                if (x3 == next.first) {
                    list.remove(next);
                    z2 = true;
                } else {
                    z2 = z;
                }
            }
            if (list.size() == 0) {
                x2.subscriptions.remove(x1);
            }
        } else {
            z = false;
        }
        return z;
    }

    static /* synthetic */ void access$1000(MediaBrowserServiceCompat x0, String x1, ConnectionRecord x2, final ResultReceiver x3) {
        Result<Object> result = new Result<Object>(x1) { // from class: android.support.v4.media.MediaBrowserServiceCompat.2
            @Override // android.support.v4.media.MediaBrowserServiceCompat.Result
            final /* bridge */ /* synthetic */ void onResultSent$4cfcfd12(int i) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("media_item", null);
                ResultReceiver resultReceiver = x3;
                if (resultReceiver.mLocal) {
                    if (resultReceiver.mHandler != null) {
                        resultReceiver.mHandler.post(new ResultReceiver.MyRunnable(0, bundle));
                    }
                } else if (resultReceiver.mReceiver != null) {
                    try {
                        resultReceiver.mReceiver.send(0, bundle);
                    } catch (RemoteException e) {
                    }
                }
            }
        };
        x0.mCurConnection = x2;
        if (result.mSendResultCalled) {
            throw new IllegalStateException("sendResult() called twice for: " + result.mDebug);
        }
        result.mSendResultCalled = true;
        result.onResultSent$4cfcfd12(result.mFlags);
        x0.mCurConnection = null;
        if (result.isDone()) {
        } else {
            throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + x1);
        }
    }

    static /* synthetic */ List access$1200$1100aaef(List x1, Bundle x2) {
        return null;
    }
}
