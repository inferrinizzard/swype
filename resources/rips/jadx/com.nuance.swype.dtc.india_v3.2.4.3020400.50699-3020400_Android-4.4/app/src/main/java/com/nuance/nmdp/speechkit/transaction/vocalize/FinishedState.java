package com.nuance.nmdp.speechkit.transaction.vocalize;

/* loaded from: classes.dex */
final class FinishedState extends VocalizeTransactionStateBase {
    public FinishedState(VocalizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        this._transaction.freeNmasResource();
        this._transaction.getListener().transactionDone(this._transaction);
    }
}
