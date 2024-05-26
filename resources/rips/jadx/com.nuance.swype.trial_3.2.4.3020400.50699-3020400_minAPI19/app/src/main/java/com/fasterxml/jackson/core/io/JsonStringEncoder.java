package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public final class JsonStringEncoder {
    protected ByteArrayBuilder _byteBuilder;
    protected final char[] _quoteBuffer = new char[6];
    private static final char[] HEX_CHARS = CharTypes.copyHexChars();
    private static final byte[] HEX_BYTES = CharTypes.copyHexBytes();
    protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _threadEncoder = new ThreadLocal<>();

    public JsonStringEncoder() {
        this._quoteBuffer[0] = '\\';
        this._quoteBuffer[2] = '0';
        this._quoteBuffer[3] = '0';
    }

    public static JsonStringEncoder getInstance() {
        SoftReference<JsonStringEncoder> ref = _threadEncoder.get();
        JsonStringEncoder enc = ref == null ? null : ref.get();
        if (enc == null) {
            JsonStringEncoder enc2 = new JsonStringEncoder();
            _threadEncoder.set(new SoftReference<>(enc2));
            return enc2;
        }
        return enc;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x003e, code lost:            if (r7 < r6) goto L55;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0040, code lost:            r5 = r0.finishCurrentSegment();        r6 = r5.length;        r8 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0049, code lost:            if (r1 >= 2048) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x004b, code lost:            r7 = r8 + 1;        r5[r8] = (byte) ((r1 >> 6) | com.nuance.swype.input.R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop);        r3 = r4;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0055, code lost:            if (r7 < r6) goto L63;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0057, code lost:            r5 = r0.finishCurrentSegment();        r6 = r5.length;        r7 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x006c, code lost:            if (r1 < 55296) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0071, code lost:            if (r1 <= 57343) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0095, code lost:            if (r1 <= 56319) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0097, code lost:            _throwIllegalSurrogate(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x009a, code lost:            if (r4 < r2) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x009c, code lost:            _throwIllegalSurrogate(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x009f, code lost:            r3 = r4 + 1;        r9 = r14.charAt(r4);     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00a8, code lost:            if (r9 < 56320) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00ad, code lost:            if (r9 <= 57343) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00df, code lost:            r1 = (65536 + ((r1 - 55296) << 10)) + (r9 - 56320);     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00f2, code lost:            if (r1 <= 1114111) goto L45;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00f4, code lost:            _throwIllegalSurrogate(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00f7, code lost:            r7 = r8 + 1;        r5[r8] = (byte) ((r1 >> 18) | com.nuance.swype.input.R.styleable.ThemeTemplate_pressableBackgroundHighlight);     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0100, code lost:            if (r7 < r6) goto L48;     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0102, code lost:            r5 = r0.finishCurrentSegment();        r6 = r5.length;        r7 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0108, code lost:            r8 = r7 + 1;        r5[r7] = (byte) (((r1 >> 12) & 63) | 128);     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0113, code lost:            if (r8 < r6) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0115, code lost:            r5 = r0.finishCurrentSegment();        r6 = r5.length;        r7 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x011b, code lost:            r5[r7] = (byte) (((r1 >> 6) & 63) | 128);        r7 = r7 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0139, code lost:            r7 = r8;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00de, code lost:            throw new java.lang.IllegalArgumentException("Broken surrogate pair: first char 0x" + java.lang.Integer.toHexString(r1) + ", second 0x" + java.lang.Integer.toHexString(r9) + "; illegal combination");     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0073, code lost:            r7 = r8 + 1;        r5[r8] = (byte) ((r1 >> 12) | 224);     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x007c, code lost:            if (r7 < r6) goto L30;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x007e, code lost:            r5 = r0.finishCurrentSegment();        r6 = r5.length;        r7 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0084, code lost:            r5[r7] = (byte) (((r1 >> 6) & 63) | 128);        r7 = r7 + 1;        r3 = r4;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0134, code lost:            r8 = r7;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final byte[] encodeAsUTF8(java.lang.String r14) {
        /*
            Method dump skipped, instructions count: 315
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.encodeAsUTF8(java.lang.String):byte[]");
    }

    private static void _throwIllegalSurrogate(int code) {
        if (code > 1114111) {
            throw new IllegalArgumentException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
        }
        if (code >= 55296) {
            if (code <= 56319) {
                throw new IllegalArgumentException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IllegalArgumentException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IllegalArgumentException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}
