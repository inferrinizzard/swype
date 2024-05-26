package com.nuance.connect.comm;

/* loaded from: classes.dex */
public class TransactionException extends RuntimeException {
    public TransactionException() {
    }

    public TransactionException(String str) {
        super(str);
    }

    public TransactionException(String str, Throwable th) {
        super(str, th);
    }

    public TransactionException(Throwable th) {
        super(th);
    }
}
