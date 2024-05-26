package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.transaction.ErrorStateBase;

/* loaded from: classes.dex */
final class ErrorState extends ErrorStateBase implements IRecognizeTransactionState {
    private final RecognizeTransaction _transaction;

    public ErrorState(RecognizeTransaction transaction, int errorCode, String errorString, String suggestion, boolean reportImmediately) {
        super(transaction, errorCode, errorString, suggestion, reportImmediately);
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ErrorStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        this._transaction.stopRecording();
        this._transaction.stopStartPrompt();
        super.enter();
        if (this._code != 5) {
            this._transaction.playErrorPrompt();
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingError() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingSignalEnergy(float energy) {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStarted() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStopped() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void stop() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void promptError() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void promptStopped() {
    }
}
