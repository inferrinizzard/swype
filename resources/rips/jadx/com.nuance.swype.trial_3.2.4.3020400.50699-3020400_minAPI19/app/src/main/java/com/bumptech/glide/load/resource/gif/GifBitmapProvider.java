package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/* loaded from: classes.dex */
final class GifBitmapProvider implements GifDecoder.BitmapProvider {
    private final BitmapPool bitmapPool;

    public GifBitmapProvider(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    public final Bitmap obtain(int width, int height, Bitmap.Config config) {
        return this.bitmapPool.getDirty(width, height, config);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider
    public final void release(Bitmap bitmap) {
        if (!this.bitmapPool.put(bitmap)) {
            bitmap.recycle();
        }
    }
}
