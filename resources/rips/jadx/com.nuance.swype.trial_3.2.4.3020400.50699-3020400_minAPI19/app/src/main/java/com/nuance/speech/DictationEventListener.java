package com.nuance.speech;

/* loaded from: classes.dex */
public interface DictationEventListener {

    /* loaded from: classes.dex */
    public enum DictationEvent {
        Connection_Failed,
        Connection_InProgress,
        Connection_Connected,
        Connection_DisConnect,
        Dictation_Started,
        Dictation_UpdateResult,
        Dictation_Failed,
        Dictation_Canceled,
        Dictation_Stopped,
        Speech_Started,
        Speech_Start_Of_Speech,
        Speech_End_Of_Speech,
        Speech_AudioLevel,
        Speech_Language_Change,
        Speech_Session_Ended
    }

    void handleDictationEvent(DictationEvent dictationEvent, Object obj);
}
