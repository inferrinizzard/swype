package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class StreamBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private BitmapPool bitmapPool;
    private DecodeFormat decodeFormat;
    private final Downsampler downsampler;
    private String id;

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final /* bridge */ /* synthetic */ Resource<Bitmap> decode(InputStream inputStream, int x1, int x2) throws IOException {
        return BitmapResource.obtain(this.downsampler.decode(inputStream, this.bitmapPool, x1, x2, this.decodeFormat), this.bitmapPool);
    }

    public StreamBitmapDecoder(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this(Downsampler.AT_LEAST, bitmapPool, decodeFormat);
    }

    private StreamBitmapDecoder(Downsampler downsampler, BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this.downsampler = downsampler;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        if (this.id == null) {
            this.id = "StreamBitmapDecoder.com.bumptech.glide.load.resource.bitmap" + this.downsampler.getId() + this.decodeFormat.name();
        }
        return this.id;
    }
}
