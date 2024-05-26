package com.nuance.nmdp.speechkit.transaction.recognize;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface IRecognizeTransactionState {
    void promptError();

    void promptStopped();

    void recordingError();

    void recordingSignalEnergy(float f);

    void recordingStarted();

    void recordingStopped();

    void stop();
}
