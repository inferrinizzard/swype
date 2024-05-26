package com.bumptech.glide.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class LruCache<T, Y> {
    public final LinkedHashMap<T, Y> cache = new LinkedHashMap<>(100, 0.75f, true);
    public int currentSize = 0;
    private final int initialMaxSize;
    private int maxSize;

    public LruCache(int size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    public final void setSizeMultiplier(float multiplier) {
        if (multiplier < 0.0f) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        this.maxSize = Math.round(this.initialMaxSize * multiplier);
        evict();
    }

    public int getSize(Y item) {
        return 1;
    }

    public void onItemEvicted(T key, Y item) {
    }

    public final Y get(T key) {
        return this.cache.get(key);
    }

    public final Y put(T key, Y item) {
        if (getSize(item) >= this.maxSize) {
            onItemEvicted(key, item);
            return null;
        }
        Y result = this.cache.put(key, item);
        if (item != null) {
            this.currentSize += getSize(item);
        }
        if (result != null) {
            this.currentSize -= getSize(result);
        }
        evict();
        return result;
    }

    public final void clearMemory() {
        trimToSize(0);
    }

    public final void trimToSize(int size) {
        while (this.currentSize > size) {
            Map.Entry<T, Y> last = this.cache.entrySet().iterator().next();
            Y toRemove = last.getValue();
            this.currentSize -= getSize(toRemove);
            T key = last.getKey();
            this.cache.remove(key);
            onItemEvicted(key, toRemove);
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }
}
