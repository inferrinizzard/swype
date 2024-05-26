package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;

/* loaded from: classes.dex */
public class PDXDictParam extends PDXParam implements Parameter {
    private PDXDictionary a;

    public PDXDictParam(String str, PDXDictionary pDXDictionary) {
        super(str, (byte) 5);
        this.a = pDXDictionary;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getContent() {
        return this.a.getContent();
    }
}
