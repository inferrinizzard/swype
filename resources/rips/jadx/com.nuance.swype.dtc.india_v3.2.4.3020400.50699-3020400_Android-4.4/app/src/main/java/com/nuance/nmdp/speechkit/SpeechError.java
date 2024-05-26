package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface SpeechError {

    /* loaded from: classes.dex */
    public static final class Codes {
        public static final int CanceledError = 5;
        public static final int CustomWordsNeedClearAllError = 7;
        public static final int CustomWordsSynchronizerError = 6;
        public static final int QueryResultError = 8;
        public static final int RecognizerError = 3;
        public static final int ServerConnectionError = 1;
        public static final int ServerRetryError = 2;
        public static final int UnknownError = 0;
        public static final int VocalizerError = 4;
    }

    int getErrorCode();

    String getErrorDetail();

    String getSuggestion();
}
