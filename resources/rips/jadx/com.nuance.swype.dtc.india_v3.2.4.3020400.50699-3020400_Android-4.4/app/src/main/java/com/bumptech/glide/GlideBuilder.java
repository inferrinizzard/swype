package com.bumptech.glide;

import android.content.Context;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public final class GlideBuilder {
    BitmapPool bitmapPool;
    final Context context;
    public DecodeFormat decodeFormat;
    DiskCache.Factory diskCacheFactory;
    ExecutorService diskCacheService;
    Engine engine;
    MemoryCache memoryCache;
    ExecutorService sourceService;

    public GlideBuilder(Context context) {
        this.context = context.getApplicationContext();
    }
}
