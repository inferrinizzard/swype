package com.nuance.connect.comm;

/* loaded from: classes.dex */
public class ConnectorException extends Exception {
    protected int delaySeconds;
    protected int statusCode;

    public ConnectorException() {
    }

    public ConnectorException(String str) {
        super(str);
    }

    public ConnectorException(String str, int i) {
        super(str);
        this.statusCode = i;
    }

    public ConnectorException(String str, int i, Throwable th) {
        super(str, th);
    }

    public ConnectorException(Throwable th) {
        super(th);
    }

    public int getDelaySeconds() {
        return this.delaySeconds;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setDelaySeconds(int i) {
        this.delaySeconds = i;
    }
}
