package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.JobRunner;

/* loaded from: classes.dex */
final class PromptingState extends ActiveStateBase {
    private final int RECORDED_BUFFER_DELAYED_TIME;

    public PromptingState(RecognizeTransaction transaction) {
        super(transaction);
        this.RECORDED_BUFFER_DELAYED_TIME = 200;
    }

    private void promptDone() {
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.PromptingState.1
            @Override // java.lang.Runnable
            public void run() {
                PromptingState.this._transaction.switchToState(new RecordingState(PromptingState.this._transaction));
            }
        }, 200);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void promptError() {
        promptDone();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void promptStopped() {
        promptDone();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        if (!this._transaction.playStartPrompt()) {
            promptDone();
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingStopped() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionState
    public final void recordingError() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }
}
