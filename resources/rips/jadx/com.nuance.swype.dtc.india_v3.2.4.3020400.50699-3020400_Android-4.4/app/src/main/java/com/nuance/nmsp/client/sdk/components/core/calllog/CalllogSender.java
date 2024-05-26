package com.nuance.nmsp.client.sdk.components.core.calllog;

/* loaded from: classes.dex */
public interface CalllogSender {
    public static final short CALLLOG_CONNECTION_FAILED = 4;
    public static final short CALLLOG_INVALID_DATA = 3;
    public static final short CALLLOG_NETWORK_ERROR = 2;

    /* loaded from: classes.dex */
    public interface SenderListener {
        void failed(short s, byte[] bArr);

        void succeeded(byte[] bArr);
    }

    void send(byte[] bArr);
}
