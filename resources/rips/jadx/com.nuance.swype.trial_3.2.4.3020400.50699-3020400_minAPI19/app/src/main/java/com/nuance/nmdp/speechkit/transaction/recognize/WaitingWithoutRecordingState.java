package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
public class WaitingWithoutRecordingState extends ActiveStateBase {
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void promptError() {
        super.promptError();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void promptStopped() {
        super.promptStopped();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void recordingSignalEnergy(float x0) {
        super.recordingSignalEnergy(x0);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void recordingStarted() {
        super.recordingStarted();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void recordingStopped() {
        super.recordingStopped();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public /* bridge */ /* synthetic */ void stop() {
        super.stop();
    }

    public WaitingWithoutRecordingState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void enter() {
        try {
            this._transaction.createNmasResource();
            this._transaction.createPdxCommand(this._transaction.getCommandName(), 30000);
            this._transaction.sendParams();
            this._transaction.endPdxCommand();
        } catch (Throwable tr) {
            Logger.error(this, "Error starting WaitingWithoutRecordingState", tr);
            this._transaction.switchToState(new ErrorState(this._transaction, 3, null, null, false));
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public void recordingError() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryResult(QueryResult result) {
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
