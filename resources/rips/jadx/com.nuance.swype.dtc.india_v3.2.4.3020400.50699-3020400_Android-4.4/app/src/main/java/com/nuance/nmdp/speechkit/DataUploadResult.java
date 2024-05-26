package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface DataUploadResult {

    /* loaded from: classes.dex */
    public interface DataResult {
        int getChecksum();

        int getDataCount();

        int getForceUpload();

        String getId();

        String getStatus();

        String getType();
    }

    DataResult getDataResult(int i);

    int getDataResultCount();

    boolean isVocRegenerated();
}
