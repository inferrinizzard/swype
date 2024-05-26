package com.nuance.nmsp.client.sdk.common.util;

/* loaded from: classes.dex */
public abstract class Enum {
    private int a;

    public Enum(int i) {
        this.a = i;
    }

    public boolean equals(Object obj) {
        return obj.getClass().getName().equals(getClass().getName()) && ((Enum) obj).a == this.a;
    }

    public int getId() {
        return this.a;
    }
}
