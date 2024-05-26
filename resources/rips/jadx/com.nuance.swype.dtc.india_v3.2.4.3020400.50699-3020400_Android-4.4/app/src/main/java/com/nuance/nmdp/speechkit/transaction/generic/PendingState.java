package com.nuance.nmdp.speechkit.transaction.generic;

import com.nuance.nmdp.speechkit.transaction.TransactionStateBase;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
final class PendingState extends TransactionStateBase {
    private final GenericTransaction _transaction;

    public PendingState(GenericTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        try {
            this._transaction.createNmasResource();
            this._transaction.createPdxCommand(this._transaction.getName(), 30000);
            this._transaction.sendParams();
            this._transaction.endPdxCommand();
        } catch (Throwable tr) {
            Logger.error(this, "Error starting PendingState", tr);
            error(0);
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void commandEvent(short code) {
        error(1);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void createCommandFailed() {
        error(0);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryError(int code, String description) {
        error(0, description);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryResult(QueryResult result) {
        IPdxParser<?> parser = this._transaction.getResultParser();
        if (parser != null && parser.parse(result) && !parser.expectMore()) {
            this._transaction.switchToState(new FinishedState(this._transaction));
        } else {
            error(0);
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryRetry(String prompt, String name) {
        error(2, null, prompt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase
    public final void error(int code, String error, String suggestion) {
        this._transaction.switchToState(new ErrorState(this._transaction, code, error, suggestion));
    }
}
