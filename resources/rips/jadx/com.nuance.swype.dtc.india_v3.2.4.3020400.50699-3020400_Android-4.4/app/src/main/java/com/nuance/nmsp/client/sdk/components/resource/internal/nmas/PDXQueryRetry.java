package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryRetry;

/* loaded from: classes.dex */
public class PDXQueryRetry extends PDXMessage implements QueryRetry {
    private static final LogFactory.Log a = LogFactory.getLog(PDXQueryRetry.class);

    public PDXQueryRetry(byte[] bArr) {
        super((short) 29189, bArr);
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryRetry
    public int getCause() {
        a.debug("PDXQueryRetry.getCause()");
        try {
            return getInteger("cause");
        } catch (Exception e) {
            return 0;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryRetry
    public String getName() {
        a.debug("PDXQueryRetry.getName()");
        String uTF8String = getUTF8String("name");
        return uTF8String != null ? uTF8String : "";
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryRetry
    public String getPrompt() {
        String str;
        a.debug("PDXQueryRetry.getPrompt()");
        try {
            str = getUTF8String("prompt");
        } catch (Exception e) {
            str = "";
        }
        return str != null ? str : "";
    }
}
