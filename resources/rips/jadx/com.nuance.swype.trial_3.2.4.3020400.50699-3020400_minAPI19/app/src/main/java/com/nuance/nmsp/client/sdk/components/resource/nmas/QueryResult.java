package com.nuance.nmsp.client.sdk.components.resource.nmas;

import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;

/* loaded from: classes.dex */
public interface QueryResult extends Dictionary {
    byte[] getRawInput();

    String getResultType();

    boolean isFinalResponse();
}
