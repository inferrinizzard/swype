package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import java.util.Set;

/* loaded from: classes.dex */
public final class LruBitmapPool implements BitmapPool {
    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
    private final Set<Bitmap.Config> allowedConfigs;
    private int currentSize;
    private int evictions;
    private int hits;
    private final int initialMaxSize;
    private int maxSize;
    private int misses;
    private int puts;
    private final LruPoolStrategy strategy;
    private final BitmapTracker tracker;

    /* loaded from: classes.dex */
    private interface BitmapTracker {
    }

    private LruBitmapPool(int maxSize, LruPoolStrategy strategy, Set<Bitmap.Config> allowedConfigs) {
        this.initialMaxSize = maxSize;
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.tracker = new NullBitmapTracker((byte) 0);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final synchronized void setSizeMultiplier(float sizeMultiplier) {
        this.maxSize = Math.round(this.initialMaxSize * sizeMultiplier);
        evict();
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final synchronized boolean put(Bitmap bitmap) {
        boolean z;
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        if (!bitmap.isMutable() || this.strategy.getSize(bitmap) > this.maxSize || !this.allowedConfigs.contains(bitmap.getConfig())) {
            if (Log.isLoggable("LruBitmapPool", 2)) {
                Log.v("LruBitmapPool", "Reject bitmap from pool, bitmap: " + this.strategy.logBitmap(bitmap) + ", is mutable: " + bitmap.isMutable() + ", is allowed config: " + this.allowedConfigs.contains(bitmap.getConfig()));
            }
            z = false;
        } else {
            int size = this.strategy.getSize(bitmap);
            this.strategy.put(bitmap);
            this.puts++;
            this.currentSize += size;
            if (Log.isLoggable("LruBitmapPool", 2)) {
                Log.v("LruBitmapPool", "Put bitmap in pool=" + this.strategy.logBitmap(bitmap));
            }
            dump();
            evict();
            z = true;
        }
        return z;
    }

    private void evict() {
        trimToSize(this.maxSize);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final synchronized Bitmap get(int width, int height, Bitmap.Config config) {
        Bitmap result;
        result = getDirty(width, height, config);
        if (result != null) {
            result.eraseColor(0);
        }
        return result;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    @TargetApi(12)
    public final synchronized Bitmap getDirty(int width, int height, Bitmap.Config config) {
        Bitmap result;
        result = this.strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            if (Log.isLoggable("LruBitmapPool", 3)) {
                new StringBuilder("Missing bitmap=").append(this.strategy.logBitmap(width, height, config));
            }
            this.misses++;
        } else {
            this.hits++;
            this.currentSize -= this.strategy.getSize(result);
            if (Build.VERSION.SDK_INT >= 12) {
                result.setHasAlpha(true);
            }
        }
        if (Log.isLoggable("LruBitmapPool", 2)) {
            Log.v("LruBitmapPool", "Get bitmap=" + this.strategy.logBitmap(width, height, config));
        }
        dump();
        return result;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final void clearMemory() {
        Log.isLoggable("LruBitmapPool", 3);
        trimToSize(0);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    @SuppressLint({"InlinedApi"})
    public final void trimMemory(int level) {
        Log.isLoggable("LruBitmapPool", 3);
        if (level >= 60) {
            clearMemory();
        } else if (level >= 40) {
            trimToSize(this.maxSize / 2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0015, code lost:            if (android.util.Log.isLoggable("LruBitmapPool", 5) == false) goto L10;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0017, code lost:            android.util.Log.w("LruBitmapPool", "Size mismatch, resetting");        dumpUnchecked();     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0023, code lost:            r3.currentSize = 0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized void trimToSize(int r4) {
        /*
            r3 = this;
            monitor-enter(r3)
        L1:
            int r1 = r3.currentSize     // Catch: java.lang.Throwable -> L5b
            if (r1 <= r4) goto L26
            com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy r1 = r3.strategy     // Catch: java.lang.Throwable -> L5b
            android.graphics.Bitmap r0 = r1.removeLast()     // Catch: java.lang.Throwable -> L5b
            if (r0 != 0) goto L28
            java.lang.String r1 = "LruBitmapPool"
            r2 = 5
            boolean r1 = android.util.Log.isLoggable(r1, r2)     // Catch: java.lang.Throwable -> L5b
            if (r1 == 0) goto L23
            java.lang.String r1 = "LruBitmapPool"
            java.lang.String r2 = "Size mismatch, resetting"
            android.util.Log.w(r1, r2)     // Catch: java.lang.Throwable -> L5b
            r3.dumpUnchecked()     // Catch: java.lang.Throwable -> L5b
        L23:
            r1 = 0
            r3.currentSize = r1     // Catch: java.lang.Throwable -> L5b
        L26:
            monitor-exit(r3)
            return
        L28:
            int r1 = r3.currentSize     // Catch: java.lang.Throwable -> L5b
            com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy r2 = r3.strategy     // Catch: java.lang.Throwable -> L5b
            int r2 = r2.getSize(r0)     // Catch: java.lang.Throwable -> L5b
            int r1 = r1 - r2
            r3.currentSize = r1     // Catch: java.lang.Throwable -> L5b
            r0.recycle()     // Catch: java.lang.Throwable -> L5b
            int r1 = r3.evictions     // Catch: java.lang.Throwable -> L5b
            int r1 = r1 + 1
            r3.evictions = r1     // Catch: java.lang.Throwable -> L5b
            java.lang.String r1 = "LruBitmapPool"
            r2 = 3
            boolean r1 = android.util.Log.isLoggable(r1, r2)     // Catch: java.lang.Throwable -> L5b
            if (r1 == 0) goto L57
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L5b
            java.lang.String r2 = "Evicting bitmap="
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L5b
            com.bumptech.glide.load.engine.bitmap_recycle.LruPoolStrategy r2 = r3.strategy     // Catch: java.lang.Throwable -> L5b
            java.lang.String r2 = r2.logBitmap(r0)     // Catch: java.lang.Throwable -> L5b
            r1.append(r2)     // Catch: java.lang.Throwable -> L5b
        L57:
            r3.dump()     // Catch: java.lang.Throwable -> L5b
            goto L1
        L5b:
            r1 = move-exception
            monitor-exit(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool.trimToSize(int):void");
    }

    private void dump() {
        if (Log.isLoggable("LruBitmapPool", 2)) {
            dumpUnchecked();
        }
    }

    private void dumpUnchecked() {
        Log.v("LruBitmapPool", "Hits=" + this.hits + ", misses=" + this.misses + ", puts=" + this.puts + ", evictions=" + this.evictions + ", currentSize=" + this.currentSize + ", maxSize=" + this.maxSize + "\nStrategy=" + this.strategy);
    }

    /* loaded from: classes.dex */
    private static class NullBitmapTracker implements BitmapTracker {
        private NullBitmapTracker() {
        }

        /* synthetic */ NullBitmapTracker(byte b) {
            this();
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public LruBitmapPool(int r5) {
        /*
            r4 = this;
            r3 = 19
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L2b
            com.bumptech.glide.load.engine.bitmap_recycle.SizeConfigStrategy r0 = new com.bumptech.glide.load.engine.bitmap_recycle.SizeConfigStrategy
            r0.<init>()
        Lb:
            java.util.HashSet r1 = new java.util.HashSet
            r1.<init>()
            android.graphics.Bitmap$Config[] r2 = android.graphics.Bitmap.Config.values()
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.addAll(r2)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r3) goto L23
            r2 = 0
            r1.add(r2)
        L23:
            java.util.Set r1 = java.util.Collections.unmodifiableSet(r1)
            r4.<init>(r5, r0, r1)
            return
        L2b:
            com.bumptech.glide.load.engine.bitmap_recycle.AttributeStrategy r0 = new com.bumptech.glide.load.engine.bitmap_recycle.AttributeStrategy
            r0.<init>()
            goto Lb
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool.<init>(int):void");
    }
}
