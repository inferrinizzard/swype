package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;

/* loaded from: classes.dex */
public class PDXTextParam extends PDXParam implements Parameter {
    private String a;

    public PDXTextParam(String str, String str2) {
        super(str, (byte) 2);
        this.a = str2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getText() {
        return this.a;
    }
}
