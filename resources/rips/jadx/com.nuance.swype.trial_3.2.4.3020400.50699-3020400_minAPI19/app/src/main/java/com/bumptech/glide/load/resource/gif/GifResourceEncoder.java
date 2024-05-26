package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifencoder.AnimatedGifEncoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.LogTime;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class GifResourceEncoder implements ResourceEncoder<GifDrawable> {
    private static final Factory FACTORY = new Factory();
    private final BitmapPool bitmapPool;
    private final Factory factory;
    private final GifDecoder.BitmapProvider provider;

    public GifResourceEncoder(BitmapPool bitmapPool) {
        this(bitmapPool, FACTORY);
    }

    private GifResourceEncoder(BitmapPool bitmapPool, Factory factory) {
        this.bitmapPool = bitmapPool;
        this.provider = new GifBitmapProvider(bitmapPool);
        this.factory = factory;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.bumptech.glide.load.Encoder
    public boolean encode(Resource<GifDrawable> resource, OutputStream os) {
        long startTime = LogTime.getLogTime();
        GifDrawable drawable = resource.get();
        Transformation<Bitmap> transformation = drawable.state.frameTransformation;
        if (transformation instanceof UnitTransformation) {
            return writeDataDirect(drawable.state.data, os);
        }
        byte[] bArr = drawable.state.data;
        GifHeaderParser gifHeaderParser = new GifHeaderParser();
        gifHeaderParser.setData(bArr);
        GifHeader parseHeader = gifHeaderParser.parseHeader();
        GifDecoder decoder = new GifDecoder(this.provider);
        decoder.setData(parseHeader, bArr);
        decoder.advance();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        if (!encoder.start(os)) {
            return false;
        }
        for (int i = 0; i < decoder.header.frameCount; i++) {
            Bitmap currentFrame = decoder.getNextFrame();
            BitmapResource bitmapResource = new BitmapResource(currentFrame, this.bitmapPool);
            Resource<Bitmap> transformedResource = transformation.transform(bitmapResource, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            if (!bitmapResource.equals(transformedResource)) {
                bitmapResource.recycle();
            }
            try {
                if (!encoder.addFrame(transformedResource.get())) {
                    transformedResource.recycle();
                    return false;
                }
                int currentFrameIndex = decoder.framePointer;
                int delay = decoder.getDelay(currentFrameIndex);
                encoder.delay = Math.round(delay / 10.0f);
                decoder.advance();
                transformedResource.recycle();
            } catch (Throwable th) {
                transformedResource.recycle();
                throw th;
            }
        }
        boolean finish = encoder.finish();
        if (Log.isLoggable("GifEncoder", 2)) {
            Log.v("GifEncoder", "Encoded gif with " + decoder.header.frameCount + " frames and " + drawable.state.data.length + " bytes in " + LogTime.getElapsedMillis(startTime) + " ms");
            return finish;
        }
        return finish;
    }

    private static boolean writeDataDirect(byte[] data, OutputStream os) {
        try {
            os.write(data);
            return true;
        } catch (IOException e) {
            Log.isLoggable("GifEncoder", 3);
            return false;
        }
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        return "";
    }

    /* loaded from: classes.dex */
    static class Factory {
        Factory() {
        }
    }
}
