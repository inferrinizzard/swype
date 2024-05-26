package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
final class RecordingState extends ActiveStateBase {
    public RecordingState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        try {
            this._transaction.startCapturing();
            getListener().recordingStarted(this._transaction);
        } catch (Exception ex) {
            Logger.error(this, "Error starting RecordingState", ex);
            error(3);
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void leave() {
        this._transaction.stopRecording();
        getListener().recordingStopped(this._transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void stop() {
        this._transaction.stopRecording();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStopped() {
        this._transaction.playStopPrompt();
        this._transaction.switchToState(new WaitingState(this._transaction));
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
            }
        } else {
            error(3);
        }
    }
}
