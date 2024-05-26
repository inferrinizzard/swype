package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.util.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class IdleState extends RecognizeTransactionStateBase {
    public IdleState(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void start() {
        Logger.info(this, "Starting recognize transaction");
        this._transaction.switchToState(this._transaction.supportsRecording() ? new RecordStartingState(this._transaction) : new WaitingWithoutRecordingState(this._transaction));
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
    }
}
