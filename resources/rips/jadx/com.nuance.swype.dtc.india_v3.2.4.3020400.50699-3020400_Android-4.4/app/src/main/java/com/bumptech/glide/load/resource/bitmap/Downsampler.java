package com.bumptech.glide.load.resource.bitmap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class Downsampler implements BitmapDecoder<InputStream> {
    private static final Set<ImageHeaderParser.ImageType> TYPES_THAT_USE_POOL = EnumSet.of(ImageHeaderParser.ImageType.JPEG, ImageHeaderParser.ImageType.PNG_A, ImageHeaderParser.ImageType.PNG);
    private static final Queue<BitmapFactory.Options> OPTIONS_QUEUE = Util.createQueue(0);
    public static final Downsampler AT_LEAST = new Downsampler() { // from class: com.bumptech.glide.load.resource.bitmap.Downsampler.1
        @Override // com.bumptech.glide.load.resource.bitmap.Downsampler
        protected final int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
            return Math.min(inHeight / outHeight, inWidth / outWidth);
        }

        @Override // com.bumptech.glide.load.resource.bitmap.BitmapDecoder
        public final String getId() {
            return "AT_LEAST.com.bumptech.glide.load.data.bitmap";
        }
    };
    public static final Downsampler AT_MOST = new Downsampler() { // from class: com.bumptech.glide.load.resource.bitmap.Downsampler.2
        @Override // com.bumptech.glide.load.resource.bitmap.Downsampler
        protected final int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
            int maxIntegerFactor = (int) Math.ceil(Math.max(inHeight / outHeight, inWidth / outWidth));
            int max = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
            return max << (max >= maxIntegerFactor ? 0 : 1);
        }

        @Override // com.bumptech.glide.load.resource.bitmap.BitmapDecoder
        public final String getId() {
            return "AT_MOST.com.bumptech.glide.load.data.bitmap";
        }
    };
    public static final Downsampler NONE = new Downsampler() { // from class: com.bumptech.glide.load.resource.bitmap.Downsampler.3
        @Override // com.bumptech.glide.load.resource.bitmap.Downsampler
        protected final int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
            return 0;
        }

        @Override // com.bumptech.glide.load.resource.bitmap.BitmapDecoder
        public final String getId() {
            return "NONE.com.bumptech.glide.load.data.bitmap";
        }
    };

    protected abstract int getSampleSize(int i, int i2, int i3, int i4);

    /* JADX WARN: Code restructure failed: missing block: B:103:0x02c2, code lost:            r10 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x02cc, code lost:            if (android.util.Log.isLoggable("Downsampler", 5) != false) goto L83;     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x02ce, code lost:            android.util.Log.w("Downsampler", "Cannot reset the input stream", r10);     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x02b6, code lost:            r21 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x01b2, code lost:            r23 = r22.streamReader.getUInt8();     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x024e, code lost:            r0 = new byte[r24];        r22 = r22.streamReader.read(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x0266, code lost:            if (r22 == r24) goto L76;     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x0275, code lost:            if (android.util.Log.isLoggable("ImageHeaderParser", 3) == false) goto L75;     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x0277, code lost:            new java.lang.StringBuilder("Unable to read segment data, type: ").append((int) r23).append(", length: ").append(r24).append(", actually read: ").append(r22);     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x02ac, code lost:            r23 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x02b2, code lost:            r23 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x01d2, code lost:            android.util.Log.isLoggable("ImageHeaderParser", 3);        r23 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x01c2, code lost:            if (r23 != 218) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x01c4, code lost:            r23 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x0052, code lost:            android.util.Log.isLoggable("ImageHeaderParser", 3);        r23 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x02be, code lost:            r17 = -1;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x01d0, code lost:            if (r23 != 217) goto L62;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x01e0, code lost:            r24 = r22.streamReader.getUInt16() - 2;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x01f2, code lost:            if (r23 == 225) goto L146;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x01f4, code lost:            r26 = r22.streamReader.skip(r24);     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x020e, code lost:            if (r26 == r24) goto L149;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0219, code lost:            if (android.util.Log.isLoggable("ImageHeaderParser", 3) == false) goto L69;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x021b, code lost:            new java.lang.StringBuilder("Unable to skip enough data, type: ").append((int) r23).append(", wanted to skip: ").append(r24).append(", but actually skipped: ").append(r26);     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0248, code lost:            r23 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x005e, code lost:            if (r23 == null) goto L77;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0070, code lost:            if (r23.length <= com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length) goto L77;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0072, code lost:            r21 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0074, code lost:            if (r21 == false) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0076, code lost:            r22 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0083, code lost:            if (r22 >= com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length) goto L150;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x008f, code lost:            if (r23[r22] == com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.JPEG_EXIF_SEGMENT_PREAMBLE_BYTES[r22]) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x02ba, code lost:            r22 = r22 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0091, code lost:            r21 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0093, code lost:            if (r21 == false) goto L79;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0095, code lost:            r17 = com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.parseExifSegment(new com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.RandomAccessReader(r23));     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00a2, code lost:            r11.reset();     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x003e, code lost:            if (com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.handles(r22.streamReader.getUInt16()) != false) goto L7;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0050, code lost:            if (r22.streamReader.getUInt8() == 255) goto L56;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.graphics.Bitmap decode(java.io.InputStream r31, com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool r32, int r33, int r34, com.bumptech.glide.load.DecodeFormat r35) {
        /*
            Method dump skipped, instructions count: 1128
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.Downsampler.decode(java.io.InputStream, com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool, int, int, com.bumptech.glide.load.DecodeFormat):android.graphics.Bitmap");
    }

    private static boolean shouldUsePool(InputStream is) {
        if (19 <= Build.VERSION.SDK_INT) {
            return true;
        }
        is.mark(1024);
        try {
            try {
                ImageHeaderParser.ImageType type = new ImageHeaderParser(is).getType();
                boolean contains = TYPES_THAT_USE_POOL.contains(type);
                try {
                    is.reset();
                    return contains;
                } catch (IOException e) {
                    if (Log.isLoggable("Downsampler", 5)) {
                        Log.w("Downsampler", "Cannot reset the input stream", e);
                        return contains;
                    }
                    return contains;
                }
            } catch (IOException e2) {
                if (Log.isLoggable("Downsampler", 5)) {
                    Log.w("Downsampler", "Cannot determine the image type from header", e2);
                }
                return false;
            }
        } finally {
            try {
                is.reset();
            } catch (IOException e3) {
                if (Log.isLoggable("Downsampler", 5)) {
                    Log.w("Downsampler", "Cannot reset the input stream", e3);
                }
            }
        }
    }

    private static Bitmap.Config getConfig(InputStream is, DecodeFormat format) {
        if (format == DecodeFormat.ALWAYS_ARGB_8888 || format == DecodeFormat.PREFER_ARGB_8888 || Build.VERSION.SDK_INT == 16) {
            return Bitmap.Config.ARGB_8888;
        }
        boolean hasAlpha = false;
        is.mark(1024);
        try {
            try {
                hasAlpha = new ImageHeaderParser(is).getType().hasAlpha();
            } finally {
                try {
                    is.reset();
                } catch (IOException e) {
                    if (Log.isLoggable("Downsampler", 5)) {
                        Log.w("Downsampler", "Cannot reset the input stream", e);
                    }
                }
            }
        } catch (IOException e2) {
            if (Log.isLoggable("Downsampler", 5)) {
                Log.w("Downsampler", "Cannot determine whether the image has alpha or not from header for format " + format, e2);
            }
            try {
                is.reset();
            } catch (IOException e3) {
                if (Log.isLoggable("Downsampler", 5)) {
                    Log.w("Downsampler", "Cannot reset the input stream", e3);
                }
            }
        }
        return hasAlpha ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
    }

    private static Bitmap decodeStream(MarkEnforcingInputStream is, RecyclableBufferedInputStream bufferedStream, BitmapFactory.Options options) {
        if (options.inJustDecodeBounds) {
            is.mark(5242880);
        } else {
            bufferedStream.fixMarkLimit();
        }
        Bitmap result = BitmapFactory.decodeStream(is, null, options);
        try {
            if (options.inJustDecodeBounds) {
                is.reset();
            }
        } catch (IOException e) {
            if (Log.isLoggable("Downsampler", 6)) {
                Log.e("Downsampler", "Exception loading inDecodeBounds=" + options.inJustDecodeBounds + " sample=" + options.inSampleSize, e);
            }
        }
        return result;
    }

    @TargetApi(11)
    private static synchronized BitmapFactory.Options getDefaultOptions() {
        BitmapFactory.Options decodeBitmapOptions;
        synchronized (Downsampler.class) {
            synchronized (OPTIONS_QUEUE) {
                decodeBitmapOptions = OPTIONS_QUEUE.poll();
            }
            if (decodeBitmapOptions == null) {
                decodeBitmapOptions = new BitmapFactory.Options();
                resetOptions(decodeBitmapOptions);
            }
        }
        return decodeBitmapOptions;
    }

    private static void releaseOptions(BitmapFactory.Options decodeBitmapOptions) {
        resetOptions(decodeBitmapOptions);
        synchronized (OPTIONS_QUEUE) {
            OPTIONS_QUEUE.offer(decodeBitmapOptions);
        }
    }

    @TargetApi(11)
    private static void resetOptions(BitmapFactory.Options decodeBitmapOptions) {
        decodeBitmapOptions.inTempStorage = null;
        decodeBitmapOptions.inDither = false;
        decodeBitmapOptions.inScaled = false;
        decodeBitmapOptions.inSampleSize = 1;
        decodeBitmapOptions.inPreferredConfig = null;
        decodeBitmapOptions.inJustDecodeBounds = false;
        decodeBitmapOptions.outWidth = 0;
        decodeBitmapOptions.outHeight = 0;
        decodeBitmapOptions.outMimeType = null;
        if (11 <= Build.VERSION.SDK_INT) {
            decodeBitmapOptions.inBitmap = null;
            decodeBitmapOptions.inMutable = true;
        }
    }
}
