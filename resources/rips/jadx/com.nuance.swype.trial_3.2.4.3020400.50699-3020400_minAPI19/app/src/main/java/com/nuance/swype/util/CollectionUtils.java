package com.nuance.swype.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/* loaded from: classes.dex */
public final class CollectionUtils {

    /* loaded from: classes.dex */
    public static class FiniteSet<T> {
        public final LinkedHashSet<T> list = new LinkedHashSet<>();
        public final int max = 10;
    }

    public static <E> ArrayList<E> newArrayList(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }
}
