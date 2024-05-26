package com.nuance.connect.api;

/* loaded from: classes.dex */
public interface SyncService {
    public static final int ERROR_SYNC_IGNORED = 301;

    /* loaded from: classes.dex */
    public interface DLMSyncCallback {
        void backupOccurred(int i, int i2);

        void onError(int i, String str);

        void receivedEvents(int i, int i2);

        void restoreOccurred(int i, int i2);

        void sentEvents(int i, int i2);
    }

    void backupRequest(int i);

    void cleanRestore(int i);

    boolean dlmSyncEnabled();

    void registerCallback(DLMSyncCallback dLMSyncCallback);

    void restoreRequest(int i);

    void setDLMSyncStatus(boolean z);

    void setSyncInterval(int i);

    void syncNow();

    void unregisterCallback(DLMSyncCallback dLMSyncCallback);

    void unregisterCallbacks();
}
