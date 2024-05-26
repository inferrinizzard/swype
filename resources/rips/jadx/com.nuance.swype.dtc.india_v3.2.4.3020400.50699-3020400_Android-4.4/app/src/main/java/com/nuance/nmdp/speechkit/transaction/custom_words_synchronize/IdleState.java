package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public class IdleState extends CustomWordsSynchronizeTransactionStateBase {
    public IdleState(CustomWordsSynchronizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void enter() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void start() {
        Logger.info(this, "Starting custom words synchronize transaction");
        this._transaction.switchToState(new CustomWordsSynchronizeStartingState(this._transaction));
    }
}
