package com.bumptech.glide.load.resource.gif;

import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.util.Util;

/* loaded from: classes.dex */
public final class GifDrawableResource extends DrawableResource<GifDrawable> {
    public GifDrawableResource(GifDrawable drawable) {
        super(drawable);
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final int getSize() {
        return Util.getBitmapByteSize(((GifDrawable) this.drawable).state.firstFrame) + ((GifDrawable) this.drawable).state.data.length;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final void recycle() {
        ((GifDrawable) this.drawable).stop();
        GifDrawable gifDrawable = (GifDrawable) this.drawable;
        gifDrawable.isRecycled = true;
        gifDrawable.state.bitmapPool.put(gifDrawable.state.firstFrame);
        gifDrawable.frameLoader.clear();
        gifDrawable.frameLoader.isRunning = false;
    }
}
