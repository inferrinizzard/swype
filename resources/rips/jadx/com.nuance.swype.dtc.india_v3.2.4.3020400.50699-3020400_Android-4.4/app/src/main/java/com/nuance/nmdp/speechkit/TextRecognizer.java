package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface TextRecognizer {

    /* loaded from: classes.dex */
    public interface Listener {
        void onError(TextRecognizer textRecognizer, SpeechError speechError);

        void onResults(TextRecognizer textRecognizer, GenericRecognition genericRecognition);
    }

    void cancel();

    void setListener(Listener listener);

    void setPrompt(int i, Prompt prompt);

    void start();
}
