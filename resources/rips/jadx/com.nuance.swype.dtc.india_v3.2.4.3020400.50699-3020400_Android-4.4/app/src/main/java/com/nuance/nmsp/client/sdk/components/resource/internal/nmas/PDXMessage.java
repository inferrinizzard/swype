package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class PDXMessage extends PDXDictionary {
    protected static final short EnrollementSegment = 1024;
    protected static final short EnrollmentAudio = 1026;
    protected static final short QueryBegin = 514;
    protected static final short QueryEnd = 516;
    protected static final short QueryError = 29186;
    protected static final short QueryParameter = 515;
    protected static final short QueryResult = 29185;
    protected static final short QueryRetry = 29189;
    private static final LogFactory.Log a = LogFactory.getLog(PDXMessage.class);
    private short b;

    public PDXMessage(short s) {
        this.b = s;
    }

    public PDXMessage(short s, byte[] bArr) {
        super(bArr, true);
        this.b = s;
    }

    public short getCommandCode() {
        return this.b;
    }

    public byte[] getMessage() {
        return super.toByteArray();
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary
    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(this.b & 255);
        byteArrayOutputStream.write((this.b >> 8) & 255);
        try {
            byteArrayOutputStream.write(super.toByteArray());
        } catch (IOException e) {
            a.error("PDXMessage.toByteArray() " + e.toString() + ". ");
        }
        return byteArrayOutputStream.toByteArray();
    }
}
