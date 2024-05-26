package com.nuance.swypeconnect.ac;

/* loaded from: classes.dex */
public class ACException extends Exception {
    public static final int ACTIVITY_NOT_AVAILABLE = 102;
    public static final int CORE_VERSIONS_NOT_SPECIFIED = 106;
    public static final int DEPENDENCIES_NOT_MET = 132;
    public static final int DISABLED_FEATURE = 135;
    public static final int DLM_ALREADY_BOUND = 115;
    public static final int DOCUMENT_NOT_RENDERED = 121;
    public static final int DOCUMENT_OUT_OF_DATE = 136;
    public static final int FEATURE_NOT_AVAILABLE_IN_CORE = 123;
    public static final int ILLEGAL_STATE = 109;
    public static final int INVALID_APPLICATION_ID = 107;
    public static final int INVALID_CALL = 128;
    public static final int INVALID_CONNECTION_LIMIT = 113;
    public static final int INVALID_CORE = 123;
    public static final int INVALID_IDLE_TIMEOUT = 112;
    public static final int INVALID_LIBRARY = 103;
    public static final int INVALID_PROPERTY_NAME = 134;
    public static final int INVALID_REFRESH_INTERVAL = 114;
    public static final int INVALID_VALUE = 122;
    public static final int LANGUAGE_NOT_AVAILABLE = 110;
    public static final int LANGUAGE_NOT_SUPPORTED = 111;
    public static final int MISSING_PERMISSION = 116;
    public static final int NETWORK_PERMISSION_MISSING = 100;
    public static final int OUT_OF_BOUNDS = 131;
    public static final int PROPERTY_NOT_FOUND = 133;
    public static final int REASON_UNSPECIFIED = 0;
    public static final int REQUIRED_LEGAL_DOCUMENT_NOT_ACCEPTED = 126;
    public static final int REQUIRES_LINKED_ACCOUNT = 127;
    public static final int SDK_ALREADY_STARTED = 118;
    public static final int SDK_LICENSE_EXPIRED = 108;
    public static final int SDK_SHUTDOWN = 119;
    public static final int SDK_STARTED = 120;
    public static final int SDK_TRIAL_EXPIRED = 117;
    public static final int SERVICE_NOT_AVAILABLE = 101;
    public static final int SERVICE_NOT_STARTED = 105;
    public static final int TOS_NOT_ACCEPTED = 104;
    public static final int TOS_NOT_RENDERED = 114;
    public static final int UNLICENSED = 129;
    public static final int UPDATE_UNAVAILABLE = 130;
    private static final long serialVersionUID = 1;
    private Throwable cause;
    private int reason;

    public ACException(int i) {
        this.reason = 0;
        this.reason = i;
    }

    public ACException(int i, String str) {
        super(str);
        this.reason = 0;
        this.reason = i;
    }

    public ACException(int i, Throwable th) {
        this.reason = 0;
        this.reason = i;
        this.cause = th;
    }

    public ACException(Throwable th) {
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
        return "ACException reason: (" + this.reason + ") Exception: " + super.getMessage();
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
