package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;

/* loaded from: classes.dex */
abstract class RecognizeTransactionStateBase extends TransactionStateBase implements IRecognizeTransactionState {
    protected final RecognizeTransaction _transaction;

    public RecognizeTransactionStateBase(RecognizeTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    public void recordingError() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public void recordingStarted() {
    }

    public void recordingStopped() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public void recordingSignalEnergy(float energy) {
    }

    public void promptError() {
    }

    public void promptStopped() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public void stop() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final IRecognizeTransactionListener getListener() {
        return (IRecognizeTransactionListener) this._transaction.getListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase
    public final void error(int code, String error, String suggestion) {
        this._transaction.switchToState(new ErrorState(this._transaction, code, error, suggestion, true));
    }
}
