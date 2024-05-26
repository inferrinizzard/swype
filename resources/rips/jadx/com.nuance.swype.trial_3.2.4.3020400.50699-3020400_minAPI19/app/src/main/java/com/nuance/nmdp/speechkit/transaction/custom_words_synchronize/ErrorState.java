package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.transaction.ErrorStateBase;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;

/* loaded from: classes.dex */
public final class ErrorState extends ErrorStateBase {
    public ErrorState(TransactionBase transaction, int errorCode, String errorString, String suggestion, boolean reportImmediately) {
        super(transaction, errorCode, errorString, suggestion, reportImmediately);
    }
}
