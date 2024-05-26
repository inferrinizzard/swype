package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.RecognizerConstants;

/* loaded from: classes.dex */
public interface Recognizer extends IRecognizer {

    /* loaded from: classes.dex */
    public static final class EndOfSpeechDetection extends RecognizerConstants.EndOfSpeechDetection {
    }

    /* loaded from: classes.dex */
    public interface Listener extends IRecognizerListener<Recognizer, Recognition> {
        void onError(Recognizer recognizer, SpeechError speechError);

        void onRecordingBegin(Recognizer recognizer);

        void onRecordingDone(Recognizer recognizer);

        void onResults(Recognizer recognizer, Recognition recognition);
    }

    /* loaded from: classes.dex */
    public static final class PromptType extends RecognizerConstants.PromptType {
    }

    /* loaded from: classes.dex */
    public static final class RecognizerType extends RecognizerConstants.RecognizerType {
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    void cancel();

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    float getAudioLevel();

    void setListener(Listener listener);

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    void setPrompt(int i, Prompt prompt);

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    void start();

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    void stopRecording();
}
