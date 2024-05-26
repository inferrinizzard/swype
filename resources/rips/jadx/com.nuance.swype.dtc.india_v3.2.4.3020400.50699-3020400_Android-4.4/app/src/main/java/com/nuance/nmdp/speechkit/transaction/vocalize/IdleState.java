package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.util.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class IdleState extends VocalizeTransactionStateBase {
    public IdleState(VocalizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void start() {
        Logger.info(this, "Starting vocalize transaction");
        this._transaction.switchToState(new PlayingState(this._transaction));
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
    }
}
