package com.nuance.nmsp.client.sdk.components.core.internal.pdx;

import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource;

/* loaded from: classes.dex */
public class PDXInteger extends PDXClass {
    private int a;

    public PDXInteger(int i) {
        super(NMASResource.PDX_DATA_TYPE_INT);
        this.a = i;
    }

    public PDXInteger(byte[] bArr) {
        super(NMASResource.PDX_DATA_TYPE_INT);
        if (bArr.length == 1) {
            this.a = bArr[0] & 255;
            return;
        }
        if (bArr.length == 2) {
            this.a = ((bArr[1] & 255) << 8) + (bArr[0] & 255);
        } else if (bArr.length == 3) {
            this.a = ((bArr[2] & 255) << 16) + ((bArr[1] & 255) << 8) + (bArr[0] & 255);
        } else {
            this.a = ((bArr[3] & 255) << 24) + ((bArr[2] & 255) << 16) + ((bArr[1] & 255) << 8) + (bArr[0] & 255);
        }
    }

    public int getValue() {
        return this.a;
    }

    public byte[] toByteArray() {
        return super.toByteArray(Math.abs(this.a) < 128 ? new byte[]{(byte) this.a} : Math.abs(this.a) < 32768 ? new byte[]{(byte) this.a, (byte) (this.a >> 8)} : new byte[]{(byte) this.a, (byte) (this.a >> 8), (byte) (this.a >> 16), (byte) (this.a >>> 24)});
    }
}
