package com.nuance.connect.api;

/* loaded from: classes.dex */
public interface LivingLanguageService {
    public static final int HOTWORDS_CATEGORY_ID = 1024;
    public static final int LLUDA_CATEGORY_ID = 1025;

    /* loaded from: classes.dex */
    public interface Callback {
        void downloadProgress(int i, int i2, int i3, String str, String str2, int i4);

        void subscriptionAdded(int i, int i2, int i3, String str, String str2, int i4);

        void subscriptionRemoved(int i, int i2, int i3, String str, String str2);

        void subscriptionUpdated(int i, int i2, int i3, String str, String str2, int i4);

        void updatesAvailable(boolean z);
    }

    void cancelLivingDownloads();

    void disableLivingLanguage();

    void enableLivingLanguage();

    void forcePendingLivingToForeground();

    int getMaxNumberOfLivingEvents();

    boolean isHotWordsEnabled();

    boolean isLivingLanguageEnabled();

    boolean isUDAEnabled();

    boolean livingLanguageAvailable();

    void registerLivingCallback(Callback callback);

    void setLivingLanguageAvailable(boolean z);

    void setLivingLanguageStatus(boolean z, boolean z2);

    void setMaxNumberOfLivingEvents(int i);

    void unregisterLivingCallback(Callback callback);

    void unregisterLivingCallbacks();
}
