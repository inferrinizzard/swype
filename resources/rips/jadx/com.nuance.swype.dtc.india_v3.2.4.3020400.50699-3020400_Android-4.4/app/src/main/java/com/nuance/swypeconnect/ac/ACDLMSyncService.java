package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.AccountService;
import com.nuance.connect.api.SyncService;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;

/* loaded from: classes.dex */
public final class ACDLMSyncService extends ACService {
    public static final int ERROR_SYNC_IGNORED = 301;
    private final AccountService accountService;
    private final ACManager manager;
    private final SyncService syncService;
    private final ConcurrentCallbackSet<ACDLMSyncCallback> callbacks = new ConcurrentCallbackSet<>();
    private Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private Logger.Log customerLog = Logger.getLog(Logger.LoggerType.CUSTOMER);
    private final SyncService.DLMSyncCallback syncCallback = new SyncService.DLMSyncCallback() { // from class: com.nuance.swypeconnect.ac.ACDLMSyncService.1
        private void updateFeatureLastUsed() {
            ACDLMSyncService.this.manager.getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.BACKUP_SYNC, System.currentTimeMillis());
        }

        @Override // com.nuance.connect.api.SyncService.DLMSyncCallback
        public void backupOccurred(int i, int i2) {
            for (ACDLMSyncCallback aCDLMSyncCallback : (ACDLMSyncCallback[]) ACDLMSyncService.this.callbacks.toArray(new ACDLMSyncCallback[0])) {
                aCDLMSyncCallback.backupOccurred(i, i2);
            }
            updateFeatureLastUsed();
        }

        @Override // com.nuance.connect.api.SyncService.DLMSyncCallback
        public void onError(int i, String str) {
            for (ACDLMSyncCallback aCDLMSyncCallback : (ACDLMSyncCallback[]) ACDLMSyncService.this.callbacks.toArray(new ACDLMSyncCallback[0])) {
                aCDLMSyncCallback.onError(i, str);
            }
        }

        @Override // com.nuance.connect.api.SyncService.DLMSyncCallback
        public void receivedEvents(int i, int i2) {
            for (ACDLMSyncCallback aCDLMSyncCallback : (ACDLMSyncCallback[]) ACDLMSyncService.this.callbacks.toArray(new ACDLMSyncCallback[0])) {
                aCDLMSyncCallback.receivedEvents(i, i2);
            }
            updateFeatureLastUsed();
        }

        @Override // com.nuance.connect.api.SyncService.DLMSyncCallback
        public void restoreOccurred(int i, int i2) {
            for (ACDLMSyncCallback aCDLMSyncCallback : (ACDLMSyncCallback[]) ACDLMSyncService.this.callbacks.toArray(new ACDLMSyncCallback[0])) {
                aCDLMSyncCallback.restoreOccurred(i, i2);
            }
            updateFeatureLastUsed();
        }

        @Override // com.nuance.connect.api.SyncService.DLMSyncCallback
        public void sentEvents(int i, int i2) {
            for (ACDLMSyncCallback aCDLMSyncCallback : (ACDLMSyncCallback[]) ACDLMSyncService.this.callbacks.toArray(new ACDLMSyncCallback[0])) {
                aCDLMSyncCallback.sentEvents(i, i2);
            }
            updateFeatureLastUsed();
        }
    };

    /* loaded from: classes.dex */
    public interface ACDLMSyncCallback {
        void backupOccurred(int i, int i2);

        void onError(int i, String str);

        void receivedEvents(int i, int i2);

        void restoreOccurred(int i, int i2);

        void sentEvents(int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACDLMSyncService(ACManager aCManager, SyncService syncService, AccountService accountService) {
        this.manager = aCManager;
        this.syncService = syncService;
        this.accountService = accountService;
        this.syncService.registerCallback(this.syncCallback);
    }

    private void checkAccount() throws ACException {
        if (!this.accountService.isAccountRegisteredAndVerified()) {
            throw new ACException(127, "An account must be linked to use DLM sync services");
        }
    }

    public final void disableSync() {
        this.syncService.setDLMSyncStatus(false);
        this.customerLog.v("Backup and Sync disabled");
    }

    public final void enableSync() throws ACException {
        checkAccount();
        this.syncService.setDLMSyncStatus(true);
        this.customerLog.v("Backup and Sync enabled");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.DLM_SYNC_SERVICE;
    }

    public final boolean isSyncEnabled() {
        return this.syncService.dlmSyncEnabled() && this.accountService.isAccountRegisteredAndVerified();
    }

    public final void registerCallback(ACDLMSyncCallback aCDLMSyncCallback) {
        if (aCDLMSyncCallback != null) {
            this.callbacks.add(aCDLMSyncCallback);
        }
    }

    public final void requestBackup(int i) throws ACException {
        checkAccount();
        this.log.v("Backup requested for core ", Integer.valueOf(i));
        if (i != 1 && i != 2) {
            throw new ACException(123, "Supplied core does not support backup and sync");
        }
        this.syncService.backupRequest(i);
    }

    public final void requestCleanRestore(int i) throws ACException {
        this.log.v("Clean and restore for core ", Integer.valueOf(i));
        if (i != 1 && i != 2) {
            throw new ACException(123, "Supplied core does not support backup and sync");
        }
        this.syncService.cleanRestore(i);
    }

    public final void requestRestore(int i) throws ACException {
        checkAccount();
        this.log.v("Restore requested for core ", Integer.valueOf(i));
        if (i != 1 && i != 2) {
            throw new ACException(123, "Supplied core does not support backup and sync");
        }
        this.syncService.restoreRequest(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 4 || i == 1;
    }

    public final void setSyncInterval(int i) {
        this.log.v("Sync interval set to ", Integer.valueOf(i), "hours.");
        this.syncService.setSyncInterval(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
    }

    public final void sync() throws ACException {
        this.customerLog.v("Sync requested");
        checkAccount();
        this.syncService.syncNow();
    }

    public final void unregisterCallback(ACDLMSyncCallback aCDLMSyncCallback) {
        this.callbacks.remove(aCDLMSyncCallback);
    }

    public final void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
