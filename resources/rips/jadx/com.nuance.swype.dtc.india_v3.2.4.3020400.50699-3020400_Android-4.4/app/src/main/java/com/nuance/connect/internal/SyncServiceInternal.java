package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.api.SyncService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class SyncServiceInternal extends AbstractService implements SyncService {
    private volatile boolean backupAndSyncEnabled;
    private ConnectServiceManagerInternal connectService;
    private static final long SPAM_PROTECTION = TimeUnit.SECONDS.toMillis(30);
    private static final long DISABLE_SYNC_SPAM_PROTECTION = TimeUnit.MINUTES.toMillis(2);
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_DLM_RECEIVED_EVENTS, InternalMessages.MESSAGE_HOST_DLM_BACKUP_REQUEST, InternalMessages.MESSAGE_HOST_DLM_BACKUP_COMPLETE, InternalMessages.MESSAGE_HOST_DLM_RESTORE, InternalMessages.MESSAGE_HOST_GET_DLM_STATUS, InternalMessages.MESSAGE_HOST_DLM_PUSH_COMPLETE};
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
    private long allowNextUserSync = Long.MIN_VALUE;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final AtomicInteger backdownCount = new AtomicInteger(0);
    private final ConcurrentCallbackSet<SyncService.DLMSyncCallback> callbacks = new ConcurrentCallbackSet<>();
    private final Runnable delaySendDLMSyncStatusRunnable = new Runnable() { // from class: com.nuance.connect.internal.SyncServiceInternal.1
        @Override // java.lang.Runnable
        public void run() {
            if (!SyncServiceInternal.this.backupAndSyncEnabled) {
                SyncServiceInternal.this.sendDLMSyncStatus();
            }
            SyncServiceInternal.this.backdownCount.set(0);
        }
    };
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.SyncServiceInternal.2
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.SYNC_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[SyncServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < SyncServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = SyncServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            int i = 0;
            switch (AnonymousClass3.$SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.fromInt(message.what).ordinal()]) {
                case 1:
                    SyncServiceInternal.this.log.v("MESSAGE_DLM_RECEIVED_EVENTS");
                    int i2 = message.getData().getInt(Strings.DLM_EVENT_COUNT);
                    int i3 = message.getData().getInt(Strings.DLM_EVENT_CORE);
                    SyncService.DLMSyncCallback[] callbacks = SyncServiceInternal.this.getCallbacks();
                    int length = callbacks.length;
                    while (i < length) {
                        callbacks[i].receivedEvents(i3, i2);
                        i++;
                    }
                    return;
                case 2:
                    SyncServiceInternal.this.log.v("MESSAGE_HOST_DLM_BACKUP_REQUEST");
                    DLMConnectorInternal dLMConnectorInternal = (DLMConnectorInternal) SyncServiceInternal.this.connectService.getFeatureService(ConnectFeature.DLM);
                    if (!SyncServiceInternal.this.dlmSyncEnabled() || dLMConnectorInternal == null) {
                        SyncServiceInternal.this.log.d("The dlm is not available or is not enabled.");
                        return;
                    } else {
                        dLMConnectorInternal.backupDlm(message.getData().getInt(Strings.DLM_EVENT_CORE));
                        return;
                    }
                case 3:
                    SyncServiceInternal.this.log.v("MESSAGE_HOST_DLM_BACKUP_COMPLETE");
                    int i4 = message.getData().getInt(Strings.DEFAULT_KEY);
                    int i5 = message.getData().getInt(Strings.DLM_EVENT_CORE);
                    SyncService.DLMSyncCallback[] callbacks2 = SyncServiceInternal.this.getCallbacks();
                    int length2 = callbacks2.length;
                    while (i < length2) {
                        callbacks2[i].backupOccurred(i5, i4);
                        i++;
                    }
                    return;
                case 4:
                    SyncServiceInternal.this.log.v("MESSAGE_HOST_DLM_RESTORE");
                    DLMConnectorInternal dLMConnectorInternal2 = (DLMConnectorInternal) SyncServiceInternal.this.connectService.getFeatureService(ConnectFeature.DLM);
                    if (!SyncServiceInternal.this.dlmSyncEnabled() || dLMConnectorInternal2 == null) {
                        return;
                    }
                    Bundle data = message.getData();
                    String string = data.getString(Strings.DLM_EVENT_FILE);
                    if (string == null) {
                        SyncServiceInternal.this.log.e("No events file found");
                        return;
                    } else {
                        final int i6 = data.getInt(Strings.DLM_EVENT_CORE);
                        dLMConnectorInternal2.restoreDlm(i6, string, new DLMConnector.EventNotificationCallback() { // from class: com.nuance.connect.internal.SyncServiceInternal.2.1
                            @Override // com.nuance.connect.api.DLMConnector.EventNotificationCallback
                            public void onEventProcessComplete(int i7, int i8) {
                                for (SyncService.DLMSyncCallback dLMSyncCallback : SyncServiceInternal.this.getCallbacks()) {
                                    dLMSyncCallback.restoreOccurred(i6, i8);
                                }
                            }
                        });
                        return;
                    }
                case 5:
                    SyncServiceInternal.this.log.v("MESSAGE_HOST_GET_DLM_STATUS");
                    SyncServiceInternal.this.sendDLMSyncStatus();
                    return;
                case 6:
                    SyncServiceInternal.this.log.v("MESSAGE_HOST_DLM_PUSH_COMPLETE");
                    int i7 = message.getData().getInt(Strings.DEFAULT_KEY);
                    int i8 = message.getData().getInt(Strings.DLM_EVENT_CORE);
                    SyncService.DLMSyncCallback[] callbacks3 = SyncServiceInternal.this.getCallbacks();
                    int length3 = callbacks3.length;
                    while (i < length3) {
                        callbacks3[i].sentEvents(i8, i7);
                        i++;
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* renamed from: com.nuance.connect.internal.SyncServiceInternal$3, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];

        static {
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_DLM_RECEIVED_EVENTS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_DLM_BACKUP_REQUEST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_DLM_BACKUP_COMPLETE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_DLM_RESTORE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_GET_DLM_STATUS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_DLM_PUSH_COMPLETE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SyncServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.backupAndSyncEnabled = true;
        this.connectService = connectServiceManagerInternal;
        this.backupAndSyncEnabled = connectServiceManagerInternal.getUserSettings().isDlmSyncEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SyncService.DLMSyncCallback[] getCallbacks() {
        return (SyncService.DLMSyncCallback[]) this.callbacks.toArray(new SyncService.DLMSyncCallback[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDLMSyncStatus() {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_SET_DLM_STATUS, Boolean.valueOf(this.backupAndSyncEnabled));
    }

    @Override // com.nuance.connect.api.SyncService
    public void backupRequest(int i) {
        this.oemLog.d("backupRequest core=", Integer.valueOf(i));
        Bundle bundle = new Bundle();
        bundle.putInt(Strings.DLM_EVENT_CORE, i);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_COMMAND_DLM_BACKUP_REQUIRED, bundle);
    }

    @Override // com.nuance.connect.api.SyncService
    public void cleanRestore(int i) {
        this.log.d("cleanRestore cat=", Integer.valueOf(i));
        DLMConnectorInternal dLMConnectorInternal = (DLMConnectorInternal) this.connectService.getFeatureService(ConnectFeature.DLM);
        dLMConnectorInternal.recordDLMDeleteCategory(i);
        dLMConnectorInternal.processDLMCore(i);
        restoreRequest(i);
    }

    @Override // com.nuance.connect.api.SyncService
    public boolean dlmSyncEnabled() {
        return this.backupAndSyncEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.SYNC.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.SYNC.name();
    }

    @Override // com.nuance.connect.api.SyncService
    public void registerCallback(SyncService.DLMSyncCallback dLMSyncCallback) {
        this.callbacks.add(dLMSyncCallback);
    }

    @Override // com.nuance.connect.api.SyncService
    public void restoreRequest(int i) {
        this.oemLog.d("restoreRequest core=", Integer.valueOf(i));
        Bundle bundle = new Bundle();
        bundle.putInt(Strings.DLM_EVENT_CORE, i);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_DLM_RESTORE, bundle);
    }

    @Override // com.nuance.connect.api.SyncService
    public void setDLMSyncStatus(boolean z) {
        this.log.d("setDLMSyncStatus(", Boolean.valueOf(z), ") old value: ", Boolean.valueOf(this.backupAndSyncEnabled));
        if (z == this.backupAndSyncEnabled) {
            return;
        }
        this.backupAndSyncEnabled = z;
        this.connectService.getUserSettings().setDlmSyncEnabled(this.backupAndSyncEnabled);
        if (z && this.backdownCount.getAndIncrement() == 0) {
            sendDLMSyncStatus();
            return;
        }
        this.backdownCount.getAndIncrement();
        this.mHandler.removeCallbacks(this.delaySendDLMSyncStatusRunnable);
        this.mHandler.postDelayed(this.delaySendDLMSyncStatusRunnable, DISABLE_SYNC_SPAM_PROTECTION);
    }

    @Override // com.nuance.connect.api.SyncService
    public void setSyncInterval(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("hours must be a positive integer.");
        }
        if (i > 596523) {
            throw new IllegalArgumentException("hours cannot exceed 596523");
        }
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_SET_DLM_SYNC_FREQUECY, Integer.valueOf(i * ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL));
    }

    @Override // com.nuance.connect.api.SyncService
    public void syncNow() {
        this.log.d("syncNow");
        if (this.allowNextUserSync <= System.currentTimeMillis()) {
            this.oemLog.d("Sending sync request");
            this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_DLM_SYNC_NOW);
            this.allowNextUserSync = System.currentTimeMillis() + SPAM_PROTECTION;
            return;
        }
        this.oemLog.d("DLM sync request ignored, last one was recent.");
        for (SyncService.DLMSyncCallback dLMSyncCallback : getCallbacks()) {
            dLMSyncCallback.onError(301, "Sync request ignored due to recent sync request. Next request allowed after " + this.allowNextUserSync);
        }
    }

    @Override // com.nuance.connect.api.SyncService
    public void unregisterCallback(SyncService.DLMSyncCallback dLMSyncCallback) {
        this.callbacks.remove(dLMSyncCallback);
    }

    @Override // com.nuance.connect.api.SyncService
    public void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
