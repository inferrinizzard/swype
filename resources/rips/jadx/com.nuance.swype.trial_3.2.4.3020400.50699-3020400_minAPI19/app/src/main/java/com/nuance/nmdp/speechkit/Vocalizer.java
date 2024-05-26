package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
public interface Vocalizer {

    /* loaded from: classes.dex */
    public interface Listener {
        void onSpeakingBegin(Vocalizer vocalizer, String str, Object obj);

        void onSpeakingDone(Vocalizer vocalizer, String str, SpeechError speechError, Object obj);
    }

    void cancel();

    void setLanguage(String str);

    void setListener(Listener listener);

    void setVoice(String str);

    void speakMarkupString(String str, Object obj);

    void speakString(String str, Object obj);
}
