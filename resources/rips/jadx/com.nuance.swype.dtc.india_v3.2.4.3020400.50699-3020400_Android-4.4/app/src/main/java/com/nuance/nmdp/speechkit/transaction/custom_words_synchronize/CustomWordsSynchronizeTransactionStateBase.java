package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;

/* loaded from: classes.dex */
public abstract class CustomWordsSynchronizeTransactionStateBase extends TransactionStateBase {
    protected final CustomWordsSynchronizeTransaction _transaction;

    public CustomWordsSynchronizeTransactionStateBase(CustomWordsSynchronizeTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void createCommandFailed() {
        error(6);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void commandEvent(short code) {
        error(1);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryError(int code, String description) {
        error(6, description);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryRetry(String prompt, String name) {
        error(2, null, prompt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase
    public final void error(int code, String error, String suggestion) {
        this._transaction.switchToState(new ErrorState(this._transaction, code, error, suggestion, true));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ICustomWordsSynchronizerTransactionListener getListener() {
        return (ICustomWordsSynchronizerTransactionListener) this._transaction.getListener();
    }
}
