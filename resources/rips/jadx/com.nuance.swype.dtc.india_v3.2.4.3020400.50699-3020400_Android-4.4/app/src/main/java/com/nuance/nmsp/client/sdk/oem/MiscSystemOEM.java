package com.nuance.nmsp.client.sdk.oem;

import com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class MiscSystemOEM extends MiscSystem {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem
    public byte[] HMAC_SHA1_impl(byte[] bArr, byte[] bArr2) {
        return HMAC_SHA1_impl(bArr, bArr2, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem
    public byte[] HMAC_SHA1_impl(byte[] bArr, byte[] bArr2, int i) {
        if (bArr == null || bArr2 == null) {
            return new byte[0];
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            mac.update(bArr2, i, bArr2.length - i);
            return mac.doFinal();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem
    public String newUUID_impl() {
        return UUID.randomUUID().toString();
    }
}
