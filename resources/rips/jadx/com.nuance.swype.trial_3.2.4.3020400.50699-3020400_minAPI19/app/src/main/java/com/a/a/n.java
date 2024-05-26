package com.a.a;

import java.io.IOException;

/* loaded from: classes.dex */
public interface n extends o {

    /* loaded from: classes.dex */
    public interface a extends o, Cloneable {
        n build();

        a mergeFrom(d dVar, f fVar) throws IOException;
    }

    p<? extends n> getParserForType();

    int getSerializedSize();

    a newBuilderForType();

    a toBuilder();

    byte[] toByteArray();

    void writeTo(e eVar) throws IOException;
}
