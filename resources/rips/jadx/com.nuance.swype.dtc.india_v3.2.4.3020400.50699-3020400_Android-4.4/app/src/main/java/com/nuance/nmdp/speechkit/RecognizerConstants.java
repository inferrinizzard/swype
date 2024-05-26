package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public final class RecognizerConstants {

    /* loaded from: classes.dex */
    public static class EndOfSpeechDetection {
        public static final int Long = 2;
        public static final int None = 0;
        public static final int Short = 1;
        public static final int VeryLong = 3;
    }

    /* loaded from: classes.dex */
    public static class PromptType {
        public static final int ERROR = 3;
        public static final int RECORDING_START = 0;
        public static final int RECORDING_STOP = 1;
        public static final int RESULT = 2;
    }

    /* loaded from: classes.dex */
    public static class RecognizerType {
        public static final String Dictation = "dictation";
        public static final String Search = "websearch";
        public static final String Tv = "DTV";
    }
}
