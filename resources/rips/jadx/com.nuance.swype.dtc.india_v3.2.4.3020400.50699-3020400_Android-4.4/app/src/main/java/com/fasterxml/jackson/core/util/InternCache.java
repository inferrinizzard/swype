package com.fasterxml.jackson.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class InternCache extends LinkedHashMap<String, String> {
    public static final InternCache instance = new InternCache();

    private InternCache() {
        super(100, 0.8f, true);
    }

    @Override // java.util.LinkedHashMap
    protected final boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return size() > 100;
    }

    public final synchronized String intern(String input) {
        String result;
        result = get(input);
        if (result == null) {
            result = input.intern();
            put(result, result);
        }
        return result;
    }
}
