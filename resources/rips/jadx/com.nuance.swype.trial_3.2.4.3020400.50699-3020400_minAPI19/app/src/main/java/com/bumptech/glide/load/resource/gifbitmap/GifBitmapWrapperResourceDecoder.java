package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.util.ByteArrayPool;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class GifBitmapWrapperResourceDecoder implements ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> {
    private static final ImageTypeParser DEFAULT_PARSER = new ImageTypeParser();
    private static final BufferedStreamFactory DEFAULT_STREAM_FACTORY = new BufferedStreamFactory();
    private final ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder;
    private final BitmapPool bitmapPool;
    private final ResourceDecoder<InputStream, GifDrawable> gifDecoder;
    private String id;
    private final ImageTypeParser parser;
    private final BufferedStreamFactory streamFactory;

    public GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder, ResourceDecoder<InputStream, GifDrawable> gifDecoder, BitmapPool bitmapPool) {
        this(bitmapDecoder, gifDecoder, bitmapPool, DEFAULT_PARSER, DEFAULT_STREAM_FACTORY);
    }

    private GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder, ResourceDecoder<InputStream, GifDrawable> gifDecoder, BitmapPool bitmapPool, ImageTypeParser parser, BufferedStreamFactory streamFactory) {
        this.bitmapDecoder = bitmapDecoder;
        this.gifDecoder = gifDecoder;
        this.bitmapPool = bitmapPool;
        this.parser = parser;
        this.streamFactory = streamFactory;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<GifBitmapWrapper> decode(ImageVideoWrapper source, int width, int height) throws IOException {
        GifBitmapWrapper wrapper;
        GifBitmapWrapper gifBitmapWrapper;
        Resource<GifDrawable> decode;
        ByteArrayPool pool = ByteArrayPool.get();
        byte[] tempBytes = pool.getBytes();
        try {
            if (source.streamData == null) {
                wrapper = decodeBitmapWrapper(source, width, height);
            } else {
                RecyclableBufferedInputStream recyclableBufferedInputStream = new RecyclableBufferedInputStream(source.streamData, tempBytes);
                recyclableBufferedInputStream.mark(HardKeyboardManager.META_SELECTING);
                ImageHeaderParser.ImageType type = new ImageHeaderParser(recyclableBufferedInputStream).getType();
                recyclableBufferedInputStream.reset();
                if (type != ImageHeaderParser.ImageType.GIF || (decode = this.gifDecoder.decode(recyclableBufferedInputStream, width, height)) == null) {
                    gifBitmapWrapper = null;
                } else {
                    GifDrawable gifDrawable = decode.get();
                    if (gifDrawable.decoder.header.frameCount > 1) {
                        gifBitmapWrapper = new GifBitmapWrapper(null, decode);
                    } else {
                        gifBitmapWrapper = new GifBitmapWrapper(new BitmapResource(gifDrawable.state.firstFrame, this.bitmapPool), null);
                    }
                }
                if (gifBitmapWrapper == null) {
                    gifBitmapWrapper = decodeBitmapWrapper(new ImageVideoWrapper(recyclableBufferedInputStream, source.fileDescriptor), width, height);
                }
                wrapper = gifBitmapWrapper;
            }
            if (wrapper != null) {
                return new GifBitmapWrapperResource(wrapper);
            }
            return null;
        } finally {
            pool.releaseBytes(tempBytes);
        }
    }

    private GifBitmapWrapper decodeBitmapWrapper(ImageVideoWrapper toDecode, int width, int height) throws IOException {
        Resource<Bitmap> bitmapResource = this.bitmapDecoder.decode(toDecode, width, height);
        if (bitmapResource == null) {
            return null;
        }
        GifBitmapWrapper result = new GifBitmapWrapper(bitmapResource, null);
        return result;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        if (this.id == null) {
            this.id = this.gifDecoder.getId() + this.bitmapDecoder.getId();
        }
        return this.id;
    }

    /* loaded from: classes.dex */
    static class BufferedStreamFactory {
        BufferedStreamFactory() {
        }
    }

    /* loaded from: classes.dex */
    static class ImageTypeParser {
        ImageTypeParser() {
        }
    }
}
