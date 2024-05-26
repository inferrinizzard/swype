package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface IRecognizer {
    void cancel();

    float getAudioLevel();

    void setEndOfSpeechDuration(int i);

    void setPrompt(int i, Prompt prompt);

    void setStartOfSpeechTimeout(int i);

    void start();

    void stopRecording();
}
