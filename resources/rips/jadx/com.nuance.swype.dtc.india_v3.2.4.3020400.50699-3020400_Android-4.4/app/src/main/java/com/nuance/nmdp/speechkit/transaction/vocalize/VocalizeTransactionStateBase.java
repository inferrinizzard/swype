package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;

/* loaded from: classes.dex */
abstract class VocalizeTransactionStateBase extends TransactionStateBase implements IVocalizeTransactionState {
    protected final VocalizeTransaction _transaction;

    public VocalizeTransactionStateBase(VocalizeTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    public void audioError() {
    }

    public void audioStarted() {
    }

    public void audioStopped() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final IVocalizeTransactionListener getListener() {
        return (IVocalizeTransactionListener) this._transaction.getListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase
    public final void error(int code, String error, String suggestion) {
        this._transaction.switchToState(new ErrorState(this._transaction, code, error, suggestion, true));
    }
}
