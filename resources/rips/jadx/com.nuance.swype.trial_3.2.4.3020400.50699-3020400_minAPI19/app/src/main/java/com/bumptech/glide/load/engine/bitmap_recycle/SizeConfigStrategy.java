package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@TargetApi(19)
/* loaded from: classes.dex */
public final class SizeConfigStrategy implements LruPoolStrategy {
    private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS = {Bitmap.Config.ARGB_8888, null};
    private static final Bitmap.Config[] RGB_565_IN_CONFIGS = {Bitmap.Config.RGB_565};
    private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS = {Bitmap.Config.ARGB_4444};
    private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS = {Bitmap.Config.ALPHA_8};
    private final KeyPool keyPool = new KeyPool();
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();
    private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final void put(Bitmap bitmap) {
        int size = Util.getBitmapByteSize(bitmap);
        Key key = this.keyPool.get(size, bitmap.getConfig());
        this.groupedMap.put(key, bitmap);
        NavigableMap<Integer, Integer> sizes = getSizesForConfig(bitmap.getConfig());
        Integer current = (Integer) sizes.get(Integer.valueOf(key.size));
        sizes.put(Integer.valueOf(key.size), Integer.valueOf(current == null ? 1 : current.intValue() + 1));
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final Bitmap get(int width, int height, Bitmap.Config config) {
        Bitmap.Config[] configArr;
        Key bestKey;
        int i = 0;
        int size = Util.getBitmapByteSize(width, height, config);
        Key targetKey = this.keyPool.get(size, config);
        switch (AnonymousClass1.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()]) {
            case 1:
                configArr = ARGB_8888_IN_CONFIGS;
                break;
            case 2:
                configArr = RGB_565_IN_CONFIGS;
                break;
            case 3:
                configArr = ARGB_4444_IN_CONFIGS;
                break;
            case 4:
                configArr = ALPHA_8_IN_CONFIGS;
                break;
            default:
                configArr = new Bitmap.Config[]{config};
                break;
        }
        int length = configArr.length;
        while (true) {
            if (i < length) {
                Bitmap.Config config2 = configArr[i];
                Integer ceilingKey = getSizesForConfig(config2).ceilingKey(Integer.valueOf(size));
                if (ceilingKey == null || ceilingKey.intValue() > size * 8) {
                    i++;
                } else if (ceilingKey.intValue() != size || (config2 != null ? !config2.equals(config) : config != null)) {
                    this.keyPool.offer(targetKey);
                    bestKey = this.keyPool.get(ceilingKey.intValue(), config2);
                }
            }
        }
        bestKey = targetKey;
        Bitmap result = this.groupedMap.get(bestKey);
        if (result != null) {
            decrementBitmapOfSize(Integer.valueOf(Util.getBitmapByteSize(result)), result.getConfig());
            result.reconfigure(width, height, result.getConfig() != null ? result.getConfig() : Bitmap.Config.ARGB_8888);
        }
        return result;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final Bitmap removeLast() {
        Bitmap removed = this.groupedMap.removeLast();
        if (removed != null) {
            int removedSize = Util.getBitmapByteSize(removed);
            decrementBitmapOfSize(Integer.valueOf(removedSize), removed.getConfig());
        }
        return removed;
    }

    private void decrementBitmapOfSize(Integer size, Bitmap.Config config) {
        NavigableMap<Integer, Integer> sizes = getSizesForConfig(config);
        Integer current = (Integer) sizes.get(size);
        if (current.intValue() == 1) {
            sizes.remove(size);
        } else {
            sizes.put(size, Integer.valueOf(current.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForConfig(Bitmap.Config config) {
        NavigableMap<Integer, Integer> sizes = this.sortedSizes.get(config);
        if (sizes == null) {
            NavigableMap<Integer, Integer> sizes2 = new TreeMap<>();
            this.sortedSizes.put(config, sizes2);
            return sizes2;
        }
        return sizes;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final String logBitmap(Bitmap bitmap) {
        return getBitmapString(Util.getBitmapByteSize(bitmap), bitmap.getConfig());
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final String logBitmap(int width, int height, Bitmap.Config config) {
        return getBitmapString(Util.getBitmapByteSize(width, height, config), config);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy
    public final int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("SizeConfigStrategy{groupedMap=").append(this.groupedMap).append(", sortedSizes=(");
        for (Map.Entry<Bitmap.Config, NavigableMap<Integer, Integer>> entry : this.sortedSizes.entrySet()) {
            sb.append(entry.getKey()).append('[').append(entry.getValue()).append("], ");
        }
        if (!this.sortedSizes.isEmpty()) {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        return sb.append(")}").toString();
    }

    /* loaded from: classes.dex */
    static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        public final Key get(int size, Bitmap.Config config) {
            Key result = get();
            result.size = size;
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
    public static final class Key implements Poolable {
        Bitmap.Config config;
        private final KeyPool pool;
        int size;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        @Override // com.bumptech.glide.load.engine.bitmap_recycle.Poolable
        public final void offer() {
            this.pool.offer(this);
        }

        public final String toString() {
            return SizeConfigStrategy.getBitmapString(this.size, this.config);
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            if (this.size != other.size) {
                return false;
            }
            if (this.config == null) {
                if (other.config != null) {
                    return false;
                }
            } else if (!this.config.equals(other.config)) {
                return false;
            }
            return true;
        }

        public final int hashCode() {
            int result = this.size;
            return (this.config != null ? this.config.hashCode() : 0) + (result * 31);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getBitmapString(int size, Bitmap.Config config) {
        return "[" + size + "](" + config + ")";
    }

    /* renamed from: com.bumptech.glide.load.engine.bitmap_recycle.SizeConfigStrategy$1, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Bitmap.Config.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.RGB_565.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ALPHA_8.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }
}
