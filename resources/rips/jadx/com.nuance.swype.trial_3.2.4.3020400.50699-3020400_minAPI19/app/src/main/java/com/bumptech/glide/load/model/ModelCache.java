package com.bumptech.glide.load.model;

import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.util.Queue;

/* loaded from: classes.dex */
public final class ModelCache<A, B> {
    private final LruCache<ModelKey<A>, B> cache;

    public ModelCache() {
        this(250);
    }

    public ModelCache(int size) {
        this.cache = new LruCache<ModelKey<A>, B>(size) { // from class: com.bumptech.glide.load.model.ModelCache.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.bumptech.glide.util.LruCache
            public final /* bridge */ /* synthetic */ void onItemEvicted(Object x0, Object x1) {
                ((ModelKey) x0).release();
            }
        };
    }

    public final B get(A model, int width, int height) {
        ModelKey<A> key = ModelKey.get(model, width, height);
        B result = this.cache.get(key);
        key.release();
        return result;
    }

    public final void put(A model, int width, int height, B value) {
        ModelKey<A> key = ModelKey.get(model, width, height);
        this.cache.put(key, value);
    }

    /* loaded from: classes.dex */
    static final class ModelKey<A> {
        private static final Queue<ModelKey<?>> KEY_QUEUE = Util.createQueue(0);
        private int height;
        private A model;
        private int width;

        static <A> ModelKey<A> get(A model, int width, int height) {
            ModelKey<A> modelKey = (ModelKey) KEY_QUEUE.poll();
            if (modelKey == null) {
                modelKey = new ModelKey<>();
            }
            ((ModelKey) modelKey).model = model;
            ((ModelKey) modelKey).width = width;
            ((ModelKey) modelKey).height = height;
            return modelKey;
        }

        private ModelKey() {
        }

        public final void release() {
            KEY_QUEUE.offer(this);
        }

        public final boolean equals(Object o) {
            if (!(o instanceof ModelKey)) {
                return false;
            }
            ModelKey other = (ModelKey) o;
            return this.width == other.width && this.height == other.height && this.model.equals(other.model);
        }

        public final int hashCode() {
            int result = this.height;
            return (((result * 31) + this.width) * 31) + this.model.hashCode();
        }
    }
}
