package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public final class BitmapPoolAdapter implements BitmapPool {
    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final void setSizeMultiplier(float sizeMultiplier) {
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final boolean put(Bitmap bitmap) {
        return false;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final Bitmap get(int width, int height, Bitmap.Config config) {
        return null;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final Bitmap getDirty(int width, int height, Bitmap.Config config) {
        return null;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final void clearMemory() {
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
    public final void trimMemory(int level) {
    }
}
