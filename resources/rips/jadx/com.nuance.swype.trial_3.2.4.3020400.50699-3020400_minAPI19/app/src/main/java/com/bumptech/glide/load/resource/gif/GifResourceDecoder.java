package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.util.Util;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

/* loaded from: classes.dex */
public final class GifResourceDecoder implements ResourceDecoder<InputStream, GifDrawable> {
    private final BitmapPool bitmapPool;
    private final Context context;
    private final GifDecoderPool decoderPool;
    private final GifHeaderParserPool parserPool;
    private final GifBitmapProvider provider;
    private static final GifHeaderParserPool PARSER_POOL = new GifHeaderParserPool();
    private static final GifDecoderPool DECODER_POOL = new GifDecoderPool();

    public GifResourceDecoder(Context context, BitmapPool bitmapPool) {
        this(context, bitmapPool, PARSER_POOL, DECODER_POOL);
    }

    private GifResourceDecoder(Context context, BitmapPool bitmapPool, GifHeaderParserPool parserPool, GifDecoderPool decoderPool) {
        this.context = context.getApplicationContext();
        this.bitmapPool = bitmapPool;
        this.decoderPool = decoderPool;
        this.provider = new GifBitmapProvider(bitmapPool);
        this.parserPool = parserPool;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.bumptech.glide.load.ResourceDecoder
    public GifDrawableResource decode(InputStream source, int width, int height) {
        GifDrawableResource gifDrawableResource = null;
        byte[] data = inputStreamToBytes(source);
        GifHeaderParser parser = this.parserPool.obtain(data);
        GifDecoder decoder = this.decoderPool.obtain(this.provider);
        try {
            GifHeader parseHeader = parser.parseHeader();
            if (parseHeader.frameCount > 0 && parseHeader.status == 0) {
                decoder.setData(parseHeader, data);
                decoder.advance();
                Bitmap nextFrame = decoder.getNextFrame();
                if (nextFrame != null) {
                    gifDrawableResource = new GifDrawableResource(new GifDrawable(this.context, this.provider, this.bitmapPool, UnitTransformation.get(), width, height, parseHeader, data, nextFrame));
                }
            }
            return gifDrawableResource;
        } finally {
            this.parserPool.release(parser);
            this.decoderPool.release(decoder);
        }
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        return "";
    }

    private static byte[] inputStreamToBytes(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(HardKeyboardManager.META_CTRL_RIGHT_ON);
        try {
            byte[] data = new byte[HardKeyboardManager.META_CTRL_RIGHT_ON];
            while (true) {
                int nRead = is.read(data);
                if (nRead == -1) {
                    break;
                }
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            Log.w("GifResourceDecoder", "Error reading data from stream", e);
        }
        return buffer.toByteArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GifDecoderPool {
        private final Queue<GifDecoder> pool = Util.createQueue(0);

        GifDecoderPool() {
        }

        public final synchronized GifDecoder obtain(GifDecoder.BitmapProvider bitmapProvider) {
            GifDecoder result;
            result = this.pool.poll();
            if (result == null) {
                result = new GifDecoder(bitmapProvider);
            }
            return result;
        }

        public final synchronized void release(GifDecoder decoder) {
            decoder.header = null;
            decoder.data = null;
            decoder.mainPixels = null;
            decoder.mainScratch = null;
            if (decoder.previousImage != null) {
                decoder.bitmapProvider.release(decoder.previousImage);
            }
            decoder.previousImage = null;
            decoder.rawData = null;
            this.pool.offer(decoder);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GifHeaderParserPool {
        private final Queue<GifHeaderParser> pool = Util.createQueue(0);

        GifHeaderParserPool() {
        }

        public final synchronized GifHeaderParser obtain(byte[] data) {
            GifHeaderParser result;
            result = this.pool.poll();
            if (result == null) {
                result = new GifHeaderParser();
            }
            return result.setData(data);
        }

        public final synchronized void release(GifHeaderParser parser) {
            parser.rawData = null;
            parser.header = null;
            this.pool.offer(parser);
        }
    }
}
