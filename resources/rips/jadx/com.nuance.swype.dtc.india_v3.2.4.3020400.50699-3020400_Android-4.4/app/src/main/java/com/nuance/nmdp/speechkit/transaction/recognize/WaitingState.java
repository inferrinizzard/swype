package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
final class WaitingState extends ActiveStateBase {
    public WaitingState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingError() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryResult(QueryResult result) {
        IPdxParser<?> parser = this._transaction.getResultParser();
        if (parser.parse(result)) {
            getListener().results(this._transaction);
            if (!parser.expectMore()) {
                this._transaction.switchToState(new FinishedState(this._transaction));
                return;
            }
            return;
        }
        error(3);
    }
}
