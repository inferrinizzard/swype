package com.bumptech.glide.load.engine.cache;

import android.annotation.SuppressLint;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.util.LruCache;

/* loaded from: classes.dex */
public final class LruResourceCache extends LruCache<Key, Resource<?>> implements MemoryCache {
    private MemoryCache.ResourceRemovedListener listener;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.util.LruCache
    public final /* bridge */ /* synthetic */ int getSize(Resource<?> resource) {
        return resource.getSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.util.LruCache
    public final /* bridge */ /* synthetic */ void onItemEvicted(Key key, Resource<?> resource) {
        Resource<?> resource2 = resource;
        if (this.listener == null) {
            return;
        }
        this.listener.onResourceRemoved(resource2);
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    public final /* bridge */ /* synthetic */ Resource put(Key x0, Resource x1) {
        return (Resource) super.put((LruResourceCache) x0, (Key) x1);
    }

    public LruResourceCache(int size) {
        super(size);
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    public final void setResourceRemovedListener(MemoryCache.ResourceRemovedListener listener) {
        this.listener = listener;
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    @SuppressLint({"InlinedApi"})
    public final void trimMemory(int level) {
        if (level < 60) {
            if (level >= 40) {
                trimToSize(this.currentSize / 2);
                return;
            }
            return;
        }
        trimToSize(0);
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache
    public final /* bridge */ /* synthetic */ Resource remove(Key x0) {
        Object remove = this.cache.remove(x0);
        if (remove != null) {
            this.currentSize -= getSize(remove);
        }
        return (Resource) remove;
    }
}
