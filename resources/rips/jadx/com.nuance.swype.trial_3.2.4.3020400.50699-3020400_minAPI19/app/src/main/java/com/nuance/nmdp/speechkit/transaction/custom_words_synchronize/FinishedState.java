package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.CustomWordsSynchronizeResult;

/* loaded from: classes.dex */
public final class FinishedState extends CustomWordsSynchronizeTransactionStateBase {
    private final CustomWordsSynchronizeResult _result;

    public FinishedState(CustomWordsSynchronizeTransaction transaction, CustomWordsSynchronizeResult result) {
        super(transaction);
        this._result = result;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        this._transaction.freeNmasResource();
        if (this._result.Status) {
            this._transaction.setNewChecksum(this._result.NewChecksum);
        }
        getListener().result(this._transaction, this._result);
        getListener().transactionDone(this._transaction);
    }
}
