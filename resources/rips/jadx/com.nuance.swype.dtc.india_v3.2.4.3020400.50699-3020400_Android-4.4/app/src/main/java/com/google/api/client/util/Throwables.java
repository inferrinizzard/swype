package com.google.api.client.util;

/* loaded from: classes.dex */
public final class Throwables {
    public static RuntimeException propagate(Throwable throwable) {
        Throwable th = (Throwable) com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(throwable);
        com.google.api.client.repackaged.com.google.common.base.Throwables.propagateIfInstanceOf(th, Error.class);
        com.google.api.client.repackaged.com.google.common.base.Throwables.propagateIfInstanceOf(th, RuntimeException.class);
        throw new RuntimeException(throwable);
    }
}
