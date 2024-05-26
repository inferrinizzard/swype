package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface VocabularyUploadResult {

    /* loaded from: classes.dex */
    public interface VocabularyResult {
        int getChecksum();

        int getDataCount();

        int getForceUpload();

        String getId();

        String getStatus();

        String getType();
    }

    VocabularyResult getDataResult(int i);

    int getDataResultCount();

    boolean isVocRegenerated();
}
