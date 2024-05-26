package com.nuance.connect.api;

import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public interface DLMConnector {

    /* loaded from: classes.dex */
    public static class DLMInfo implements DLMInformation {
        private int coreId;
        private int reserved;
        private int size;

        public DLMInfo(int i, int i2, int i3) {
            this.coreId = i;
            this.size = i2;
            this.reserved = i3;
        }

        @Override // com.nuance.connect.api.DLMConnector.DLMInformation
        public int getCoreId() {
            return this.coreId;
        }

        @Override // com.nuance.connect.api.DLMConnector.DLMInformation
        public int getReserved() {
            return this.reserved;
        }

        @Override // com.nuance.connect.api.DLMConnector.DLMInformation
        public int getSize() {
            return this.size;
        }
    }

    /* loaded from: classes.dex */
    public interface DLMInformation {
        int getCoreId();

        int getReserved();

        int getSize();
    }

    /* loaded from: classes.dex */
    public interface DlmEventCallback {
        boolean backupDlm();

        boolean processDlmDelete(int i);

        boolean processDlmDelete(int i, int i2);

        boolean processEvent(String str);

        boolean scan(char[] cArr, int i, int i2, boolean z);

        void scanBegin(String str, String str2, String str3, int i, int i2);

        boolean scanCategory(int i, char[] cArr, int i2, int i3, boolean z);

        void scanEnd();
    }

    /* loaded from: classes.dex */
    public interface EventNotificationCallback {
        void onEventProcessComplete(int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface ScannerBucket {
        void close();

        void scan(String str);
    }

    /* loaded from: classes.dex */
    public static class ScannerBucketSet implements ScannerBucket {
        private HashSet<ScannerBucket> buckets = new HashSet<>();

        public void addBucket(ScannerBucket scannerBucket) {
            this.buckets.add(scannerBucket);
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public void close() {
            Iterator<ScannerBucket> it = this.buckets.iterator();
            while (it.hasNext()) {
                it.next().close();
            }
        }

        @Override // com.nuance.connect.api.DLMConnector.ScannerBucket
        public void scan(String str) {
            Iterator<ScannerBucket> it = this.buckets.iterator();
            while (it.hasNext()) {
                it.next().scan(str);
            }
        }
    }

    ScannerBucket getScannerBucket(String str, String str2, String str3, int i, int i2, int i3, int i4, boolean z, int i5, boolean z2);

    ScannerBucket getScannerBucket(String str, String str2, String str3, int i, int i2, int[] iArr, int i3, boolean z, int i4, boolean z2);

    boolean isSupportedCore(int i);

    void onBeginBackup(int i);

    void onDlmEvent(int i, String str, long j, boolean z);

    void onEndBackup();

    void onReset(int i, boolean z);

    void registerDlmCallback(int i, DlmEventCallback dlmEventCallback);

    void resume();

    void resume(int i);

    void setDLMInfo(DLMInformation dLMInformation);

    void unregisterDlmCallback(int i);

    void yield();

    void yield(int i);
}
