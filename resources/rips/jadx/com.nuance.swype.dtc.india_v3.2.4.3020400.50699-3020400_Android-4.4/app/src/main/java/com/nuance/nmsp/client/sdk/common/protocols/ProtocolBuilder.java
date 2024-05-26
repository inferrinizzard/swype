package com.nuance.nmsp.client.sdk.common.protocols;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class ProtocolBuilder {
    private static final LogFactory.Log a = LogFactory.getLog(ProtocolBuilder.class);

    public static byte[] appendBCPSessionUUID(byte[] bArr, byte[] bArr2) {
        if (a.isDebugEnabled()) {
            a.debug("Appending session ID");
            a.traceBuffer(bArr2);
            a.traceBuffer(bArr);
        }
        byte[] bArr3 = new byte[bArr.length + 16];
        System.arraycopy(bArr2, 0, bArr3, 0, 16);
        System.arraycopy(bArr, 0, bArr3, 16, bArr.length);
        return bArr3;
    }

    public static byte[] buildXModeBuf(byte b, byte b2, short s, byte[] bArr) {
        if (a.isDebugEnabled()) {
            a.debug("Building XMode buffer: protocol=" + ((int) b) + " version=" + ((int) b2) + " cmd=" + ((int) s) + " payload len:" + bArr.length);
            a.traceBuffer(bArr);
        }
        int length = bArr.length;
        byte[] bArr2 = new byte[length + 8];
        System.arraycopy(new XModeMsgHeader(b, b2, s, length).getBytes(), 0, bArr2, 0, 8);
        System.arraycopy(bArr, 0, bArr2, 8, length);
        return bArr2;
    }
}
