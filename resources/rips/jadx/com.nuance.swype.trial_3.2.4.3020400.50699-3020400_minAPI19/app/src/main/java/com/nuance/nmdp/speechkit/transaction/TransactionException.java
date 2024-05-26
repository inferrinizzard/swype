package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public final class TransactionException extends Exception {
    private static final long serialVersionUID = -2252622018258547681L;

    public TransactionException(String error) {
        super(error);
    }

    public TransactionException(String error, Throwable tr) {
        super(error);
        Logger.error(this, error, tr);
    }
}
