package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;

/* loaded from: classes.dex */
public final class GifBitmapWrapperTransformation implements Transformation<GifBitmapWrapper> {
    private final Transformation<Bitmap> bitmapTransformation;
    private final Transformation<GifDrawable> gifDataTransformation;

    public GifBitmapWrapperTransformation(BitmapPool bitmapPool, Transformation<Bitmap> bitmapTransformation) {
        this(bitmapTransformation, new GifDrawableTransformation(bitmapTransformation, bitmapPool));
    }

    private GifBitmapWrapperTransformation(Transformation<Bitmap> bitmapTransformation, Transformation<GifDrawable> gifDataTransformation) {
        this.bitmapTransformation = bitmapTransformation;
        this.gifDataTransformation = gifDataTransformation;
    }

    @Override // com.bumptech.glide.load.Transformation
    public final Resource<GifBitmapWrapper> transform(Resource<GifBitmapWrapper> resource, int outWidth, int outHeight) {
        Resource<Bitmap> bitmapResource = resource.get().bitmapResource;
        Resource<GifDrawable> gifResource = resource.get().gifResource;
        if (bitmapResource != null && this.bitmapTransformation != null) {
            Resource<Bitmap> transformed = this.bitmapTransformation.transform(bitmapResource, outWidth, outHeight);
            if (!bitmapResource.equals(transformed)) {
                GifBitmapWrapper gifBitmap = new GifBitmapWrapper(transformed, resource.get().gifResource);
                return new GifBitmapWrapperResource(gifBitmap);
            }
            return resource;
        }
        if (gifResource != null && this.gifDataTransformation != null) {
            Resource<GifDrawable> transformed2 = this.gifDataTransformation.transform(gifResource, outWidth, outHeight);
            if (!gifResource.equals(transformed2)) {
                GifBitmapWrapper gifBitmap2 = new GifBitmapWrapper(resource.get().bitmapResource, transformed2);
                return new GifBitmapWrapperResource(gifBitmap2);
            }
            return resource;
        }
        return resource;
    }

    @Override // com.bumptech.glide.load.Transformation
    public final String getId() {
        return this.bitmapTransformation.getId();
    }
}
