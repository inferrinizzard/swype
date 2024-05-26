package com.nuance.nmsp.client.sdk.components.core.internal.pdx;

/* loaded from: classes.dex */
public class PDXByteString extends PDXClass {
    private byte[] a;

    public PDXByteString(byte[] bArr) {
        super((short) 4);
        this.a = bArr;
    }

    public byte[] getValue() {
        return this.a;
    }

    public byte[] toByteArray() {
        return super.toByteArray(this.a);
    }
}
