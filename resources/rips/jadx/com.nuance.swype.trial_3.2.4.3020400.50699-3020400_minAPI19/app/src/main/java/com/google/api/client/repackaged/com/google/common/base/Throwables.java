package com.google.api.client.repackaged.com.google.common.base;

/* loaded from: classes.dex */
public final class Throwables {
    public static <X extends Throwable> void propagateIfInstanceOf(Throwable throwable, Class<X> declaredType) throws Throwable {
        if (throwable != null && declaredType.isInstance(throwable)) {
            throw declaredType.cast(throwable);
        }
    }
}
