package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
public abstract class TransactionStateBase implements ITransactionState {
    public final TransactionBase _transaction;

    public TransactionStateBase(TransactionBase transaction) {
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void start() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void leave() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void cancel() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void commandCreated(String sessionId) {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void createCommandFailed() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void commandEvent(short code) {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryError(int code, String description) {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryResult(QueryResult result) {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryRetry(String prompt, String name) {
    }

    public void error(int code, String error, String suggestion) {
    }

    public final void error(int code, String error) {
        error(code, error, null);
    }

    public final void error(int code) {
        error(code, null, null);
    }
}
