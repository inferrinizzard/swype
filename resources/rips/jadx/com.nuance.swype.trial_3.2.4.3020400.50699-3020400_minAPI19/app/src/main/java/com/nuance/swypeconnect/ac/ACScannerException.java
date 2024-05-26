package com.nuance.swypeconnect.ac;

/* loaded from: classes.dex */
public class ACScannerException extends Exception {
    public static final int DOES_NOT_EXIST = 105;
    public static final int EMPTY_SCAN_TYPES = 101;
    public static final int FEATURE_NOT_AVAILABLE_IN_CORE = 124;
    public static final int INVALID_CORE = 123;
    public static final int INVALID_SETTINGS = 104;
    public static final int INVALID_VALUE = 122;
    public static final int NECESSARY_SETTINGS_NOT_PROVIDED = 103;
    public static final int NO_CORE_SET = 102;
    public static final int PERMISSION_MISSING = 100;
    public static final int REASON_UNSPECIFIED = 0;
    public static final int REQUIRED_DEPENDENCIES_MISSING = 106;
    private static final long serialVersionUID = 1;
    private Throwable cause;
    private int reason;

    public ACScannerException(int i) {
        this.reason = 0;
        this.reason = i;
    }

    public ACScannerException(int i, String str) {
        super(str);
        this.reason = 0;
        this.reason = i;
    }

    public ACScannerException(int i, Throwable th) {
        this.reason = 0;
        this.reason = i;
        this.cause = th;
    }

    public ACScannerException(Throwable th) {
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
        return "ACScannerException reason: (" + this.reason + ") Exception: " + super.getMessage();
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
