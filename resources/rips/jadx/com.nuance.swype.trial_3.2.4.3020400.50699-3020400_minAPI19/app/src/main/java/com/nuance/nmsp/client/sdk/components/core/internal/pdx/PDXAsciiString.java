package com.nuance.nmsp.client.sdk.components.core.internal.pdx;

/* loaded from: classes.dex */
public class PDXAsciiString extends PDXClass {
    private String a;

    public PDXAsciiString(String str) {
        super((short) 22);
        this.a = str;
    }

    public PDXAsciiString(byte[] bArr) {
        super((short) 22);
        this.a = new String(bArr);
    }

    public String getValue() {
        return this.a;
    }

    public byte[] toByteArray() {
        return super.toByteArray(this.a.getBytes());
    }
}
