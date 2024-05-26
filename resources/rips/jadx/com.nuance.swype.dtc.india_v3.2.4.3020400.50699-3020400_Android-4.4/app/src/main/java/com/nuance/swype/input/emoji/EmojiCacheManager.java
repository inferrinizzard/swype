package com.nuance.swype.input.emoji;

import android.content.Context;
import com.nuance.android.util.LruCache;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class EmojiCacheManager<K, V> {
    protected static final LogManager.Log log = LogManager.getLog("EmojiCacheManager");
    private final LruCache<K, V> bufferCache;
    int cacheSize = HardKeyboardManager.META_SCROLL_LOCK_ON;

    public static EmojiCacheManager from(Context context) {
        return IMEApplication.from(context).getEmojiCacheManager();
    }

    public EmojiCacheManager() {
        log.d("", "EmojiCacheManager...object created>>>>>>: ");
        this.bufferCache = new LruCache<>(this.cacheSize);
    }

    public V getObjectFromCache(K key) {
        if (this.bufferCache == null) {
            return null;
        }
        return this.bufferCache.get(key);
    }

    public void addObjectToCache(K key, V value) {
        log.d("addObjectToCache...key: ", key, "...value: ", value);
        if (key != null && value != null) {
            this.bufferCache.put(key, value);
        }
    }

    public String toString() {
        return this.bufferCache.toString();
    }

    public void removeObjectFromCache(K key) {
        log.d("removeObjectFromCache...key: ", key);
        if (key != null && this.bufferCache != null) {
            this.bufferCache.remove(key);
        }
    }

    public void evictAll() {
        log.d("evictAll...");
        if (this.bufferCache != null) {
            this.bufferCache.evictAll();
        }
    }
}
