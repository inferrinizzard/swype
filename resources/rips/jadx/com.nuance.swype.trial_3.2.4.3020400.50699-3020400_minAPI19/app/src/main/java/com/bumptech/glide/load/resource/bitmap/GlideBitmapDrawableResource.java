package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.util.Util;

/* loaded from: classes.dex */
public final class GlideBitmapDrawableResource extends DrawableResource<GlideBitmapDrawable> {
    private final BitmapPool bitmapPool;

    public GlideBitmapDrawableResource(GlideBitmapDrawable drawable, BitmapPool bitmapPool) {
        super(drawable);
        this.bitmapPool = bitmapPool;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final int getSize() {
        return Util.getBitmapByteSize(((GlideBitmapDrawable) this.drawable).state.bitmap);
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final void recycle() {
        this.bitmapPool.put(((GlideBitmapDrawable) this.drawable).state.bitmap);
    }
}
