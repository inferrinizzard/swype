package com.nuance.swype.inapp.util;

/* loaded from: classes.dex */
public final class IabException extends Exception {
    IabResult mResult;

    private IabException(IabResult r) {
        this(r, (Exception) null);
    }

    public IabException(int response, String message) {
        this(new IabResult(response, message));
    }

    public IabException(int response, String message, Exception cause) {
        this(new IabResult(response, message), cause);
    }

    private IabException(IabResult r, Exception cause) {
        super(r.mMessage, cause);
        this.mResult = r;
    }
}
