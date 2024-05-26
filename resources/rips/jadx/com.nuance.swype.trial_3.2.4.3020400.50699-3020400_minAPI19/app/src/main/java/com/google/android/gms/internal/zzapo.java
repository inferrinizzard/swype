package com.google.android.gms.internal;

import com.nuance.swype.input.R;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

/* loaded from: classes.dex */
public final class zzapo {
    final ByteBuffer bjw;

    /* loaded from: classes.dex */
    public static class zza extends IOException {
        zza(int i, int i2) {
            super(new StringBuilder(108).append("CodedOutputStream was writing to a flat byte array and ran out of space (pos ").append(i).append(" limit ").append(i2).append(").").toString());
        }
    }

    private zzapo(ByteBuffer byteBuffer) {
        this.bjw = byteBuffer;
        this.bjw.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzapo(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, 0, i2));
    }

    private static int zza(CharSequence charSequence, byte[] bArr, int i, int i2) {
        int i3;
        int length = charSequence.length();
        int i4 = 0;
        int i5 = i + i2;
        while (i4 < length && i4 + i < i5) {
            char charAt = charSequence.charAt(i4);
            if (charAt >= 128) {
                break;
            }
            bArr[i + i4] = (byte) charAt;
            i4++;
        }
        if (i4 == length) {
            return i + length;
        }
        int i6 = i + i4;
        while (i4 < length) {
            char charAt2 = charSequence.charAt(i4);
            if (charAt2 < 128 && i6 < i5) {
                i3 = i6 + 1;
                bArr[i6] = (byte) charAt2;
            } else if (charAt2 < 2048 && i6 <= i5 - 2) {
                int i7 = i6 + 1;
                bArr[i6] = (byte) ((charAt2 >>> 6) | 960);
                i3 = i7 + 1;
                bArr[i7] = (byte) ((charAt2 & '?') | 128);
            } else {
                if ((charAt2 >= 55296 && 57343 >= charAt2) || i6 > i5 - 3) {
                    if (i6 > i5 - 4) {
                        throw new ArrayIndexOutOfBoundsException(new StringBuilder(37).append("Failed writing ").append(charAt2).append(" at index ").append(i6).toString());
                    }
                    if (i4 + 1 != charSequence.length()) {
                        i4++;
                        char charAt3 = charSequence.charAt(i4);
                        if (Character.isSurrogatePair(charAt2, charAt3)) {
                            int codePoint = Character.toCodePoint(charAt2, charAt3);
                            int i8 = i6 + 1;
                            bArr[i6] = (byte) ((codePoint >>> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
                            int i9 = i8 + 1;
                            bArr[i8] = (byte) (((codePoint >>> 12) & 63) | 128);
                            int i10 = i9 + 1;
                            bArr[i9] = (byte) (((codePoint >>> 6) & 63) | 128);
                            i3 = i10 + 1;
                            bArr[i10] = (byte) ((codePoint & 63) | 128);
                        }
                    }
                    throw new IllegalArgumentException(new StringBuilder(39).append("Unpaired surrogate at index ").append(i4 - 1).toString());
                }
                int i11 = i6 + 1;
                bArr[i6] = (byte) ((charAt2 >>> '\f') | 480);
                int i12 = i11 + 1;
                bArr[i11] = (byte) (((charAt2 >>> 6) & 63) | 128);
                i3 = i12 + 1;
                bArr[i12] = (byte) ((charAt2 & '?') | 128);
            }
            i4++;
            i6 = i3;
        }
        return i6;
    }

    private static void zza(CharSequence charSequence, ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (!byteBuffer.hasArray()) {
            zzb(charSequence, byteBuffer);
            return;
        }
        try {
            byteBuffer.position(zza(charSequence, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining()) - byteBuffer.arrayOffset());
        } catch (ArrayIndexOutOfBoundsException e) {
            BufferOverflowException bufferOverflowException = new BufferOverflowException();
            bufferOverflowException.initCause(e);
            throw bufferOverflowException;
        }
    }

    public static int zzafx(int i) {
        if (i >= 0) {
            return zzagc(i);
        }
        return 10;
    }

    public static int zzag(int i, int i2) {
        return zzaga(i) + zzafx(i2);
    }

    public static int zzaga(int i) {
        return zzagc(zzapy.zzaj(i, 0));
    }

    public static int zzagc(int i) {
        if ((i & (-128)) == 0) {
            return 1;
        }
        if ((i & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & i) == 0) {
            return 3;
        }
        return ((-268435456) & i) == 0 ? 4 : 5;
    }

    public static int zzb(int i, byte[] bArr) {
        return zzaga(i) + zzbg(bArr);
    }

    private static void zzb(CharSequence charSequence, ByteBuffer byteBuffer) {
        int length = charSequence.length();
        int i = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < 128) {
                byteBuffer.put((byte) charAt);
            } else if (charAt < 2048) {
                byteBuffer.put((byte) ((charAt >>> 6) | 960));
                byteBuffer.put((byte) ((charAt & '?') | 128));
            } else {
                if (charAt >= 55296 && 57343 >= charAt) {
                    if (i + 1 != charSequence.length()) {
                        i++;
                        char charAt2 = charSequence.charAt(i);
                        if (Character.isSurrogatePair(charAt, charAt2)) {
                            int codePoint = Character.toCodePoint(charAt, charAt2);
                            byteBuffer.put((byte) ((codePoint >>> 18) | R.styleable.ThemeTemplate_pressableBackgroundHighlight));
                            byteBuffer.put((byte) (((codePoint >>> 12) & 63) | 128));
                            byteBuffer.put((byte) (((codePoint >>> 6) & 63) | 128));
                            byteBuffer.put((byte) ((codePoint & 63) | 128));
                        }
                    }
                    throw new IllegalArgumentException(new StringBuilder(39).append("Unpaired surrogate at index ").append(i - 1).toString());
                }
                byteBuffer.put((byte) ((charAt >>> '\f') | 480));
                byteBuffer.put((byte) (((charAt >>> 6) & 63) | 128));
                byteBuffer.put((byte) ((charAt & '?') | 128));
            }
            i++;
        }
    }

    public static int zzbg(byte[] bArr) {
        return zzagc(bArr.length) + bArr.length;
    }

    public static zzapo zzc$715daad5(byte[] bArr, int i) {
        return new zzapo(bArr, 0, i);
    }

    public static int zzdc(long j) {
        if (((-128) & j) == 0) {
            return 1;
        }
        if (((-16384) & j) == 0) {
            return 2;
        }
        if (((-2097152) & j) == 0) {
            return 3;
        }
        if (((-268435456) & j) == 0) {
            return 4;
        }
        if (((-34359738368L) & j) == 0) {
            return 5;
        }
        if (((-4398046511104L) & j) == 0) {
            return 6;
        }
        if (((-562949953421312L) & j) == 0) {
            return 7;
        }
        if (((-72057594037927936L) & j) == 0) {
            return 8;
        }
        return (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzde(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzs(int i, String str) {
        return zzaga(i) + zztx(str);
    }

    public static int zztx(String str) {
        int zzd = zzd(str);
        return zzd + zzagc(zzd);
    }

    public final void zza(int i, zzapv zzapvVar) throws IOException {
        zzai(i, 2);
        zzc(zzapvVar);
    }

    public final void zzagb(int i) throws IOException {
        while ((i & (-128)) != 0) {
            zzafz((i & 127) | 128);
            i >>>= 7;
        }
        zzafz(i);
    }

    public final void zzai(int i, int i2) throws IOException {
        zzagb(zzapy.zzaj(i, i2));
    }

    public final void zzc(zzapv zzapvVar) throws IOException {
        zzagb(zzapvVar.aL());
        zzapvVar.zza(this);
    }

    public final void zzdb(long j) throws IOException {
        while (((-128) & j) != 0) {
            zzafz((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzafz((int) j);
    }

    public final void zzb(int i, long j) throws IOException {
        zzai(i, 0);
        zzdb(j);
    }

    public final void zzae(int i, int i2) throws IOException {
        zzai(i, 0);
        if (i2 >= 0) {
            zzagb(i2);
        } else {
            zzdb(i2);
        }
    }

    public final void zzj(int i, boolean z) throws IOException {
        zzai(i, 0);
        zzafz(z ? 1 : 0);
    }

    public final void zzr(int i, String str) throws IOException {
        zzai(i, 2);
        try {
            int zzagc = zzagc(str.length());
            if (zzagc != zzagc(str.length() * 3)) {
                zzagb(zzd(str));
                zza(str, this.bjw);
                return;
            }
            int position = this.bjw.position();
            if (this.bjw.remaining() < zzagc) {
                throw new zza(zzagc + position, this.bjw.limit());
            }
            this.bjw.position(position + zzagc);
            zza(str, this.bjw);
            int position2 = this.bjw.position();
            this.bjw.position(position);
            zzagb((position2 - position) - zzagc);
            this.bjw.position(position2);
        } catch (BufferOverflowException e) {
            zza zzaVar = new zza(this.bjw.position(), this.bjw.limit());
            zzaVar.initCause(e);
            throw zzaVar;
        }
    }

    public final void zza(int i, byte[] bArr) throws IOException {
        zzai(i, 2);
        zzagb(bArr.length);
        zzbh(bArr);
    }

    private static int zzd(CharSequence charSequence) {
        int i;
        int i2 = 0;
        int length = charSequence.length();
        int i3 = 0;
        while (i3 < length && charSequence.charAt(i3) < 128) {
            i3++;
        }
        int i4 = length;
        while (true) {
            if (i3 >= length) {
                i = i4;
                break;
            }
            char charAt = charSequence.charAt(i3);
            if (charAt < 2048) {
                i4 += (127 - charAt) >>> 31;
                i3++;
            } else {
                int length2 = charSequence.length();
                while (i3 < length2) {
                    char charAt2 = charSequence.charAt(i3);
                    if (charAt2 < 2048) {
                        i2 += (127 - charAt2) >>> 31;
                    } else {
                        i2 += 2;
                        if (55296 <= charAt2 && charAt2 <= 57343) {
                            if (Character.codePointAt(charSequence, i3) < 65536) {
                                throw new IllegalArgumentException(new StringBuilder(39).append("Unpaired surrogate at index ").append(i3).toString());
                            }
                            i3++;
                        }
                    }
                    i3++;
                }
                i = i4 + i2;
            }
        }
        if (i >= length) {
            return i;
        }
        throw new IllegalArgumentException(new StringBuilder(54).append("UTF-8 length does not fit in int: ").append(i + 4294967296L).toString());
    }

    public static int zze(int i, long j) {
        return zzaga(i) + zzdc(j);
    }

    public static int zzc(int i, zzapv zzapvVar) {
        int zzaga = zzaga(i);
        int aM = zzapvVar.aM();
        return zzaga + aM + zzagc(aM);
    }

    private void zzafz(int i) throws IOException {
        byte b = (byte) i;
        if (!this.bjw.hasRemaining()) {
            throw new zza(this.bjw.position(), this.bjw.limit());
        }
        this.bjw.put(b);
    }

    public final void zzbh(byte[] bArr) throws IOException {
        int length = bArr.length;
        if (this.bjw.remaining() < length) {
            throw new zza(this.bjw.position(), this.bjw.limit());
        }
        this.bjw.put(bArr, 0, length);
    }
}
