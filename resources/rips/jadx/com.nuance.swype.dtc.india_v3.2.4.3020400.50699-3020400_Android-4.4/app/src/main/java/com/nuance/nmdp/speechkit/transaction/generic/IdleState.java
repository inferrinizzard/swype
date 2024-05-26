package com.nuance.nmdp.speechkit.transaction.generic;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;
import com.nuance.nmdp.speechkit.util.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class IdleState extends TransactionStateBase {
    private final GenericTransaction _transaction;

    public IdleState(GenericTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void start() {
        Logger.info(this, "Starting " + this._transaction.getName() + " transaction");
        this._transaction.switchToState(new PendingState(this._transaction));
    }
}
