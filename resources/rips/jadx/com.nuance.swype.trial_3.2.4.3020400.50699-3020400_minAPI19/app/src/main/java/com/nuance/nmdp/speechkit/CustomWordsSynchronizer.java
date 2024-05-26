package com.nuance.nmdp.speechkit;

import java.util.Set;

/* loaded from: classes.dex */
public interface CustomWordsSynchronizer {

    /* loaded from: classes.dex */
    public static class CustomWordsSynchronizerActionType {
        public static final int ADD_CUSTOM_WORDS = 0;
        public static final int CLEAR_ALL_CUSTOM_WORDS = 2;
        public static final int DELETE_ALL_USER_INFORMATION = 3;
        public static final int REMOVE_CUSTOM_WORDS = 1;
    }

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(CustomWordsSynchronizer customWordsSynchronizer, int i, SpeechError speechError);

        void onResults(CustomWordsSynchronizer customWordsSynchronizer, int i, CustomWordsSynchronizeResult customWordsSynchronizeResult);
    }

    void addCustomWordsSet(Set<String> set, boolean z);

    void cancel();

    void clearAllCustomWords();

    void deleteAllUserInformation();

    void removeCustomWordsSet(Set<String> set);

    void setDictationType(String str);

    void setLanguage(String str);
}
