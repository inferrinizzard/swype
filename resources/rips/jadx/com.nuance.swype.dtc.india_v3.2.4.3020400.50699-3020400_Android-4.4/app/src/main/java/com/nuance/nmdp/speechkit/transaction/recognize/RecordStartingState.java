package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.components.resource.nmas.AudioParam;

/* loaded from: classes.dex */
final class RecordStartingState extends ActiveStateBase {
    public RecordStartingState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        try {
            this._transaction.createNmasResource();
            this._transaction.createPdxCommand(this._transaction.getCommandName(), 30000);
            this._transaction.sendParams();
            AudioParam param = this._transaction.sendAudioParam();
            this._transaction.createRecorder(param);
            this._transaction.startRecording();
            this._transaction.endPdxCommand();
        } catch (Throwable tr) {
            Logger.error(this, "Error starting RecordStartingState", tr);
            this._transaction.switchToState(new ErrorState(this._transaction, 3, null, null, false));
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void stop() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStarted() {
        this._transaction.switchToState(new PromptingState(this._transaction));
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStopped() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingError() {
        error(3);
    }
}
