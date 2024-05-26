package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class PDXQueryEnd extends PDXMessage {
    private static final LogFactory.Log a = LogFactory.getLog(PDXQueryEnd.class);

    public PDXQueryEnd() {
        super((short) 516);
        if (a.isDebugEnabled()) {
            a.debug("PDXQueryEnd()");
        }
    }
}
