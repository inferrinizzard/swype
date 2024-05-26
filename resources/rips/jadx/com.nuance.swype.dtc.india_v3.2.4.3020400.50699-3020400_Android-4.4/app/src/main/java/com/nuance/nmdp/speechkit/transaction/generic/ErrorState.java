package com.nuance.nmdp.speechkit.transaction.generic;

import com.nuance.nmdp.speechkit.transaction.ErrorStateBase;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;

/* loaded from: classes.dex */
class ErrorState extends ErrorStateBase {
    public ErrorState(TransactionBase transaction, int errorCode, String errorString, String suggestion) {
        super(transaction, errorCode, errorString, suggestion, true);
    }
}
