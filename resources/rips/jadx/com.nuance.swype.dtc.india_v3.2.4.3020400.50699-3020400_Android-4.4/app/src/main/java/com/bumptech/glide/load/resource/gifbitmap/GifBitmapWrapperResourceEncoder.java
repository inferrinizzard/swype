package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class GifBitmapWrapperResourceEncoder implements ResourceEncoder<GifBitmapWrapper> {
    private final ResourceEncoder<Bitmap> bitmapEncoder;
    private final ResourceEncoder<GifDrawable> gifEncoder;
    private String id;

    @Override // com.bumptech.glide.load.Encoder
    public final /* bridge */ /* synthetic */ boolean encode(Object x0, OutputStream x1) {
        GifBitmapWrapper gifBitmapWrapper = (GifBitmapWrapper) ((Resource) x0).get();
        Resource<Bitmap> resource = gifBitmapWrapper.bitmapResource;
        if (resource != null) {
            return this.bitmapEncoder.encode(resource, x1);
        }
        return this.gifEncoder.encode(gifBitmapWrapper.gifResource, x1);
    }

    public GifBitmapWrapperResourceEncoder(ResourceEncoder<Bitmap> bitmapEncoder, ResourceEncoder<GifDrawable> gifEncoder) {
        this.bitmapEncoder = bitmapEncoder;
        this.gifEncoder = gifEncoder;
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        if (this.id == null) {
            this.id = this.bitmapEncoder.getId() + this.gifEncoder.getId();
        }
        return this.id;
    }
}
