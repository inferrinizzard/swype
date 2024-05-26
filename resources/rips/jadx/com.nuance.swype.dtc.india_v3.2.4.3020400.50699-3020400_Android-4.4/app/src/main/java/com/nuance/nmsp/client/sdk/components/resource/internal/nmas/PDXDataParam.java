package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;

/* loaded from: classes.dex */
public class PDXDataParam extends PDXParam implements Parameter {
    private byte[] a;

    public PDXDataParam(String str, byte[] bArr) {
        super(str, (byte) 4);
        this.a = new byte[bArr.length];
        System.arraycopy(bArr, 0, this.a, 0, this.a.length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getData() {
        return this.a;
    }
}
