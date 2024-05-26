package com.nuance.nmdp.speechkit.transaction.recognize;

/* loaded from: classes.dex */
final class FinishedState extends RecognizeTransactionStateBase {
    public FinishedState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        this._transaction.freeNmasResource();
        getListener().transactionDone(this._transaction);
        this._transaction.playResultPrompt();
    }
}
