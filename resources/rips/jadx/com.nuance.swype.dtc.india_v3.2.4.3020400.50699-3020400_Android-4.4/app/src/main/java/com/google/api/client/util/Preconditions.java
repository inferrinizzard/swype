package com.google.api.client.util;

/* loaded from: classes.dex */
public final class Preconditions {
    public static void checkArgument(boolean expression, Object errorMessage) {
        if (expression) {
        } else {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (expression) {
        } else {
            throw new IllegalArgumentException(com.google.api.client.repackaged.com.google.common.base.Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference != null) {
            return reference;
        }
        throw new NullPointerException(String.valueOf(errorMessage));
    }
}
