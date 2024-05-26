package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.gif.GifDrawable;

/* loaded from: classes.dex */
public final class GifBitmapWrapperResource implements Resource<GifBitmapWrapper> {
    private final GifBitmapWrapper data;

    public GifBitmapWrapperResource(GifBitmapWrapper data) {
        if (data == null) {
            throw new NullPointerException("Data must not be null");
        }
        this.data = data;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final int getSize() {
        GifBitmapWrapper gifBitmapWrapper = this.data;
        if (gifBitmapWrapper.bitmapResource != null) {
            return gifBitmapWrapper.bitmapResource.getSize();
        }
        return gifBitmapWrapper.gifResource.getSize();
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final void recycle() {
        Resource<Bitmap> bitmapResource = this.data.bitmapResource;
        if (bitmapResource != null) {
            bitmapResource.recycle();
        }
        Resource<GifDrawable> gifDataResource = this.data.gifResource;
        if (gifDataResource != null) {
            gifDataResource.recycle();
        }
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final /* bridge */ /* synthetic */ GifBitmapWrapper get() {
        return this.data;
    }
}
