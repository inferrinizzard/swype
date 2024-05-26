package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.transaction.ErrorStateBase;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;

/* loaded from: classes.dex */
final class ErrorState extends ErrorStateBase implements IVocalizeTransactionState {
    public ErrorState(TransactionBase transaction, int errorCode, String errorString, String suggestion, boolean reportImmediately) {
        super(transaction, errorCode, errorString, suggestion, reportImmediately);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioError() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioStarted() {
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioStopped() {
    }
}
