package com.nuance.connect.api;

/* loaded from: classes.dex */
public class ConnectException extends Exception {
    public static final int ACTIVITY_NOT_AVAILABLE = 102;
    public static final int CORE_VERSIONS_NOT_SPECIFIED = 106;
    public static final int ILLEGAL_STATE = 109;
    public static final int INVALID_APPLICATION_ID = 107;
    public static final int INVALID_LIBRARY = 103;
    public static final int INVALID_VALUE = 112;
    public static final int LANGUAGE_NOT_AVAILABLE = 110;
    public static final int LANGUAGE_NOT_SUPPORTED = 111;
    public static final int NETWORK_PERMISSION_MISSING = 100;
    public static final int REASON_UNSPECIFIED = 0;
    public static final int SDK_LICENSE_EXPIRED = 108;
    public static final int SERVICE_NOT_AVAILABLE = 101;
    public static final int SERVICE_NOT_STARTED = 105;
    public static final int TOS_NOT_ACCEPTED = 104;
    private static final long serialVersionUID = 1;
    private Throwable cause;
    private int reason;

    public ConnectException(int i) {
        this.reason = 0;
        this.reason = i;
    }

    public ConnectException(int i, String str) {
        super(str);
        this.reason = 0;
        this.reason = i;
    }

    public ConnectException(int i, Throwable th) {
        this.reason = 0;
        this.reason = i;
        this.cause = th;
    }

    public ConnectException(Throwable th) {
        this.reason = 0;
        this.reason = 0;
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "ConnectException reason: (" + this.reason + ") Exception: " + super.getMessage();
    }

    public int getReasonCode() {
        return this.reason;
    }

    @Override // java.lang.Throwable
    public String toString() {
        String message = getMessage();
        return this.cause != null ? message + " - " + this.cause.toString() : message;
    }
}
