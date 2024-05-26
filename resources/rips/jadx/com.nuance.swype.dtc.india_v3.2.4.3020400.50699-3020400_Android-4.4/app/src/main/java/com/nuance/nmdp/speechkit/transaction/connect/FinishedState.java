package com.nuance.nmdp.speechkit.transaction.connect;

import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;

/* loaded from: classes.dex */
public final class FinishedState extends TransactionStateBase {
    public FinishedState(TransactionBase transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        this._transaction.createNmasResource();
        this._transaction.freeNmasResource();
        this._transaction.getListener().transactionDone(this._transaction);
    }
}
