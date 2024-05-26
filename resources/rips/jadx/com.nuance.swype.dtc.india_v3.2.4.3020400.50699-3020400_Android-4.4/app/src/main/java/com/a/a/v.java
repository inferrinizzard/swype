package com.a.a;

import java.util.List;

/* loaded from: classes.dex */
public final class v extends RuntimeException {
    private final List<String> a;

    public v() {
        super("Message was missing required fields.  (Lite runtime could not determine which fields were missing).");
        this.a = null;
    }
}
