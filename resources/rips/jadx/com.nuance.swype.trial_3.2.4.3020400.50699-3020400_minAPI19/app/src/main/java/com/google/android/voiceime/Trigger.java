package com.google.android.voiceime;

/* loaded from: classes.dex */
public interface Trigger {
    boolean hasRecognitionResultToCommit();

    void onDestroy();

    void onStartInputView();

    void startVoiceRecognition(String str);
}
