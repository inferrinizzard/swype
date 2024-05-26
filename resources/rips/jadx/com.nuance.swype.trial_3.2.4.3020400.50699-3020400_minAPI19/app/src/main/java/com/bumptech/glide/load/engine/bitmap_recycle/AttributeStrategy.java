package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import com.bumptech.glide.util.Util;

/* loaded from: classes.dex */
final class AttributeStrategy implements LruPoolStrategy {
    private final KeyPool keyPool = new KeyPool();
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final void put(Bitmap bitmap) {
        Key key = this.keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        this.groupedMap.put(key, bitmap);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final Bitmap get(int width, int height, Bitmap.Config config) {
        Key key = this.keyPool.get(width, height, config);
        return this.groupedMap.get(key);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final Bitmap removeLast() {
        return this.groupedMap.removeLast();
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final String logBitmap(int width, int height, Bitmap.Config config) {
        return getBitmapString(width, height, config);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public final String toString() {
        return "AttributeStrategy:\n  " + this.groupedMap;
    }

    static String getBitmapString(int width, int height, Bitmap.Config config) {
        return "[" + width + "x" + height + "], " + config;
    }

    /* loaded from: classes.dex */
    static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        public final Key get(int width, int height, Bitmap.Config config) {
            Key result = get();
            result.width = width;
            result.height = height;
            result.config = config;
            return result;
        }

        @Override // com.bumptech.glide.load.engine.bitmap_recycle.BaseKeyPool
        protected final /* bridge */ /* synthetic */ Key create() {
            return new Key(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Key implements Poolable {
        Bitmap.Config config;
        int height;
        private final KeyPool pool;
        int width;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            return this.width == other.width && this.height == other.height && this.config == other.config;
        }

        public final int hashCode() {
            int result = this.width;
            return (this.config != null ? this.config.hashCode() : 0) + (((result * 31) + this.height) * 31);
        }

        public final String toString() {
            return AttributeStrategy.getBitmapString(this.width, this.height, this.config);
        }

        @Override // com.bumptech.glide.load.engine.bitmap_recycle.Poolable
        public final void offer() {
            this.pool.offer(this);
        }
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final String logBitmap(Bitmap bitmap) {
        return getBitmapString(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }
}
