package com.bumptech.glide.load.resource.bitmap;

import com.nuance.swype.input.MotionEventWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public final class ImageHeaderParser {
    private static final int[] BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};
    static final byte[] JPEG_EXIF_SEGMENT_PREAMBLE_BYTES;
    final StreamReader streamReader;

    /* loaded from: classes.dex */
    public enum ImageType {
        GIF(true),
        JPEG(false),
        PNG_A(true),
        PNG(false),
        UNKNOWN(false);

        private final boolean hasAlpha;

        ImageType(boolean hasAlpha) {
            this.hasAlpha = hasAlpha;
        }

        public final boolean hasAlpha() {
            return this.hasAlpha;
        }
    }

    static {
        byte[] bytes = new byte[0];
        try {
            bytes = "Exif\u0000\u0000".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        JPEG_EXIF_SEGMENT_PREAMBLE_BYTES = bytes;
    }

    public ImageHeaderParser(InputStream is) {
        this.streamReader = new StreamReader(is);
    }

    public final ImageType getType() throws IOException {
        int firstTwoBytes = this.streamReader.getUInt16();
        if (firstTwoBytes == 65496) {
            return ImageType.JPEG;
        }
        int firstFourBytes = ((firstTwoBytes << 16) & (-65536)) | (this.streamReader.getUInt16() & 65535);
        if (firstFourBytes == -1991225785) {
            this.streamReader.skip(21L);
            return this.streamReader.is.read() >= 3 ? ImageType.PNG_A : ImageType.PNG;
        }
        if ((firstFourBytes >> 8) == 4671814) {
            return ImageType.GIF;
        }
        return ImageType.UNKNOWN;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int parseExifSegment(com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.RandomAccessReader r14) {
        /*
            r13 = 3
            r11 = 6
            short r2 = r14.getInt16(r11)
            r11 = 19789(0x4d4d, float:2.773E-41)
            if (r2 == r11) goto L50
            r11 = 18761(0x4949, float:2.629E-41)
            if (r2 != r11) goto L4a
            java.nio.ByteOrder r1 = java.nio.ByteOrder.LITTLE_ENDIAN
        L10:
            java.nio.ByteBuffer r11 = r14.data
            r11.order(r1)
            r11 = 10
            int r11 = r14.getInt32(r11)
            int r4 = r11 + 6
            short r7 = r14.getInt16(r4)
            r6 = 0
        L22:
            if (r6 >= r7) goto Lf2
            int r11 = r4 + 2
            int r12 = r6 * 12
            int r8 = r11 + r12
            short r9 = r14.getInt16(r8)
            r11 = 274(0x112, float:3.84E-43)
            if (r9 != r11) goto L47
            int r11 = r8 + 2
            short r5 = r14.getInt16(r11)
            if (r5 <= 0) goto L3e
            r11 = 12
            if (r5 <= r11) goto L53
        L3e:
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L47
        L47:
            int r6 = r6 + 1
            goto L22
        L4a:
            java.lang.String r11 = "ImageHeaderParser"
            android.util.Log.isLoggable(r11, r13)
        L50:
            java.nio.ByteOrder r1 = java.nio.ByteOrder.BIG_ENDIAN
            goto L10
        L53:
            int r11 = r8 + 4
            int r3 = r14.getInt32(r11)
            if (r3 >= 0) goto L65
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L47
            goto L47
        L65:
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L9a
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            java.lang.String r12 = "Got tagIndex="
            r11.<init>(r12)
            java.lang.StringBuilder r11 = r11.append(r6)
            java.lang.String r12 = " tagType="
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.StringBuilder r11 = r11.append(r9)
            java.lang.String r12 = " formatCode="
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.StringBuilder r11 = r11.append(r5)
            java.lang.String r12 = " componentCount="
            java.lang.StringBuilder r11 = r11.append(r12)
            r11.append(r3)
        L9a:
            int[] r11 = com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.BYTES_PER_FORMAT
            r11 = r11[r5]
            int r0 = r3 + r11
            r11 = 4
            if (r0 <= r11) goto Lad
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L47
            goto L47
        Lad:
            int r10 = r8 + 8
            if (r10 < 0) goto Lb7
            int r11 = r14.length()
            if (r10 <= r11) goto Ld8
        Lb7:
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L47
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            java.lang.String r12 = "Illegal tagValueOffset="
            r11.<init>(r12)
            java.lang.StringBuilder r11 = r11.append(r10)
            java.lang.String r12 = " tagType="
            java.lang.StringBuilder r11 = r11.append(r12)
            r11.append(r9)
            goto L47
        Ld8:
            if (r0 < 0) goto Le2
            int r11 = r10 + r0
            int r12 = r14.length()
            if (r11 <= r12) goto Led
        Le2:
            java.lang.String r11 = "ImageHeaderParser"
            boolean r11 = android.util.Log.isLoggable(r11, r13)
            if (r11 == 0) goto L47
            goto L47
        Led:
            short r11 = r14.getInt16(r10)
        Lf1:
            return r11
        Lf2:
            r11 = -1
            goto Lf1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.parseExifSegment(com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$RandomAccessReader):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean handles(int imageMagicNumber) {
        return (imageMagicNumber & 65496) == 65496 || imageMagicNumber == 19789 || imageMagicNumber == 18761;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RandomAccessReader {
        final ByteBuffer data;

        public RandomAccessReader(byte[] data) {
            this.data = ByteBuffer.wrap(data);
            this.data.order(ByteOrder.BIG_ENDIAN);
        }

        public final int length() {
            return this.data.array().length;
        }

        public final int getInt32(int offset) {
            return this.data.getInt(offset);
        }

        public final short getInt16(int offset) {
            return this.data.getShort(offset);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StreamReader {
        final InputStream is;

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public final int getUInt16() throws IOException {
            return ((this.is.read() << 8) & MotionEventWrapper.ACTION_POINTER_INDEX_MASK) | (this.is.read() & 255);
        }

        public final short getUInt8() throws IOException {
            return (short) (this.is.read() & 255);
        }

        public final long skip(long total) throws IOException {
            if (total < 0) {
                return 0L;
            }
            long toSkip = total;
            while (toSkip > 0) {
                long skipped = this.is.skip(toSkip);
                if (skipped > 0) {
                    toSkip -= skipped;
                } else {
                    if (this.is.read() == -1) {
                        break;
                    }
                    toSkip--;
                }
            }
            return total - toSkip;
        }

        public final int read(byte[] buffer) throws IOException {
            int toRead = buffer.length;
            while (toRead > 0) {
                int read = this.is.read(buffer, buffer.length - toRead, toRead);
                if (read == -1) {
                    break;
                }
                toRead -= read;
            }
            return buffer.length - toRead;
        }
    }
}
