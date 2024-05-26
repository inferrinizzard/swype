package com.nuance.nmsp.client.sdk.common.oem.api;

import com.nuance.nmsp.client.sdk.oem.MiscSystemOEM;

/* loaded from: classes.dex */
public abstract class MiscSystem {
    private static MiscSystem a = new MiscSystemOEM();

    public static byte[] HMAC_SHA1(byte[] bArr, byte[] bArr2) {
        return a.HMAC_SHA1_impl(bArr, bArr2);
    }

    public static byte[] HMAC_SHA1(byte[] bArr, byte[] bArr2, int i) {
        return a.HMAC_SHA1_impl(bArr, bArr2, i);
    }

    public static String newUUID() {
        return a.newUUID_impl();
    }

    public abstract byte[] HMAC_SHA1_impl(byte[] bArr, byte[] bArr2);

    public abstract byte[] HMAC_SHA1_impl(byte[] bArr, byte[] bArr2, int i);

    public abstract String newUUID_impl();
}
