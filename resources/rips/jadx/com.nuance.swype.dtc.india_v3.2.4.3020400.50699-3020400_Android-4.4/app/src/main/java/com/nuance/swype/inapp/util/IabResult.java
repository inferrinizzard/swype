package com.nuance.swype.inapp.util;

/* loaded from: classes.dex */
public final class IabResult {
    public String mMessage;
    public int mResponse;

    public IabResult(int response, String message) {
        this.mResponse = response;
        if (message == null || message.trim().length() == 0) {
            this.mMessage = IabHelper.getResponseDesc(response);
        } else {
            this.mMessage = IabHelper.getResponseDetailedDesc(message, response);
        }
    }

    public final boolean isSuccess() {
        return this.mResponse == 0;
    }

    public final String toString() {
        return "IabResult: " + this.mMessage;
    }
}
