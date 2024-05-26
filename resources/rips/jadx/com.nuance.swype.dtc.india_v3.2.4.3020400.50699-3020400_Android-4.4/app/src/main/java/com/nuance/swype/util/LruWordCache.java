package com.nuance.swype.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class LruWordCache {
    public final Set<String> words;

    public LruWordCache(final int maxWords) {
        this.words = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() { // from class: com.nuance.swype.util.LruWordCache.1
            @Override // java.util.LinkedHashMap
            protected final boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                return size() > maxWords;
            }
        });
    }
}
