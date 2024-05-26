package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;

/* loaded from: classes.dex */
public class PDXChoiceParam extends PDXParam implements Parameter {
    private String a;

    public PDXChoiceParam(String str, String str2) {
        super(str, (byte) 3);
        this.a = str2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getChoicename() {
        return this.a;
    }
}
