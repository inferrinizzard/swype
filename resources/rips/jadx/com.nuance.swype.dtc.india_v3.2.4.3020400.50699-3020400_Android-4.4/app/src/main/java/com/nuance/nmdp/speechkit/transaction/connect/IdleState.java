package com.nuance.nmdp.speechkit.transaction.connect;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;
import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public final class IdleState extends TransactionStateBase {
    public IdleState(ConnectTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void start() {
        Logger.info(this, "Starting connect transaction");
        this._transaction.switchToState(new FinishedState(this._transaction));
    }
}
