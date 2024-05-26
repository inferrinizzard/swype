package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
interface IRecognizerListener<RecognizerType, ResultType> {
    void onError(RecognizerType recognizertype, SpeechError speechError);

    void onRecordingBegin(RecognizerType recognizertype);

    void onRecordingDone(RecognizerType recognizertype);

    void onResults(RecognizerType recognizertype, ResultType resulttype);
}
