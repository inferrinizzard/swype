package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class InputDecorator implements Serializable {
    public abstract InputStream decorate$44b83b11() throws IOException;

    public abstract Reader decorate$6b9cf12f() throws IOException;
}
