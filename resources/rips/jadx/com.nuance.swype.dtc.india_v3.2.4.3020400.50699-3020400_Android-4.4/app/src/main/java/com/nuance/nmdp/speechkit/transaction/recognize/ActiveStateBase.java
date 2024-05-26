package com.nuance.nmdp.speechkit.transaction.recognize;

/* loaded from: classes.dex */
abstract class ActiveStateBase extends RecognizeTransactionStateBase {
    public ActiveStateBase(RecognizeTransaction transaction) {
        super(transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void commandEvent(short event) {
        error(1);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void createCommandFailed() {
        error(3);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryError(int code, String description) {
        error(3, description);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryRetry(String prompt, String name) {
        error(2, null, prompt);
    }
}
