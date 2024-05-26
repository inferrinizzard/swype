package com.nuance.nmsp.client.sdk.components.core.internal.pdx;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class PDXUTF8String extends PDXClass {
    private static final LogFactory.Log a = LogFactory.getLog(PDXUTF8String.class);
    private String b;

    public PDXUTF8String(String str) {
        super((short) 193);
        this.b = str;
    }

    public PDXUTF8String(byte[] bArr) {
        super((short) 193);
        try {
            this.b = new String(bArr, "UTF-8");
        } catch (Exception e) {
            a.error("PDXUTF8String() " + e.getMessage());
            this.b = new String(bArr);
        }
    }

    public String getValue() {
        return this.b;
    }

    public byte[] toByteArray() {
        try {
            return super.toByteArray(this.b.getBytes("UTF-8"));
        } catch (Exception e) {
            a.error("PDXUTF8String().toByteArray() " + e.getMessage());
            return super.toByteArray(this.b.getBytes());
        }
    }
}
