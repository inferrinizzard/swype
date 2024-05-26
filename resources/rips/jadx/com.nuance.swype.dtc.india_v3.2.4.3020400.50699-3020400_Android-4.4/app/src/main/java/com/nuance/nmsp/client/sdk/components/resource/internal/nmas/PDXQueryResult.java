package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
public class PDXQueryResult extends PDXMessage implements QueryResult {
    private static final LogFactory.Log a = LogFactory.getLog(PDXQueryResult.class);
    private final byte[] b;

    public PDXQueryResult(byte[] bArr) {
        super((short) 29185, bArr);
        this.b = bArr;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult
    public byte[] getRawInput() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult
    public String getResultType() {
        a.debug("PDXQueryResult.getResultType()");
        String uTF8String = getUTF8String("result_type");
        return uTF8String != null ? uTF8String : "";
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult
    public boolean isFinalResponse() {
        boolean z = true;
        try {
            if (containsKey("final_response")) {
                if (getInteger("final_response") == 0) {
                    z = false;
                }
            } else if (a.isInfoEnabled()) {
                a.info("final_response does not exist. ");
            }
        } catch (Exception e) {
        }
        return z;
    }
}
