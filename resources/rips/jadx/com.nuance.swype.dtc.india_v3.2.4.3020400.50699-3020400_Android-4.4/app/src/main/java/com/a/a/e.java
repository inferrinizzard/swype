package com.a.a;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class e {
    private final byte[] a;
    private final int b;
    private int c;
    private final OutputStream d;

    /* loaded from: classes.dex */
    public static class a extends IOException {
        a() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }
    }

    private e(OutputStream outputStream, byte[] bArr) {
        this.d = outputStream;
        this.a = bArr;
        this.c = 0;
        this.b = bArr.length;
    }

    private e(byte[] bArr, int i) {
        this.d = null;
        this.a = bArr;
        this.c = 0;
        this.b = i + 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(int i) {
        return i > 4096 ? HardKeyboardManager.META_CTRL_ON : i;
    }

    public static e a(OutputStream outputStream, int i) {
        return new e(outputStream, new byte[i]);
    }

    private void d() throws IOException {
        if (this.d == null) {
            throw new a();
        }
        this.d.write(this.a, 0, this.c);
        this.c = 0;
    }

    public static int e(int i) {
        return g(x.a(i, 0));
    }

    public static int g(int i) {
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

    public final void a() throws IOException {
        if (this.d != null) {
            d();
        }
    }

    public final void c(int i, int i2) throws IOException {
        f(x.a(i, i2));
    }

    public final void f(int i) throws IOException {
        while ((i & (-128)) != 0) {
            d((i & 127) | 128);
            i >>>= 7;
        }
        d(i);
    }

    public static e a(byte[] bArr) {
        return new e(bArr, bArr.length);
    }

    public final void a$3eface71(n nVar) throws IOException {
        c(2, 2);
        f(nVar.getSerializedSize());
        nVar.writeTo(this);
    }

    public final void a(int i, c cVar) throws IOException {
        c(i, 2);
        f(cVar.b());
        int b = cVar.b();
        if (this.b - this.c >= b) {
            cVar.a(this.a, 0, this.c, b);
            this.c = b + this.c;
            return;
        }
        int i2 = this.b - this.c;
        cVar.a(this.a, 0, this.c, i2);
        int i3 = i2 + 0;
        int i4 = b - i2;
        this.c = this.b;
        d();
        if (i4 <= this.b) {
            cVar.a(this.a, i3, 0, i4);
            this.c = i4;
            return;
        }
        InputStream g = cVar.g();
        if (i3 != g.skip(i3)) {
            throw new IllegalStateException("Skip failed? Should never happen.");
        }
        while (i4 > 0) {
            int min = Math.min(i4, this.b);
            int read = g.read(this.a, 0, min);
            if (read != min) {
                throw new IllegalStateException("Read failed? Should never happen");
            }
            this.d.write(this.a, 0, read);
            i4 -= read;
        }
    }

    public final void a(int i, int i2) throws IOException {
        c(i, 0);
        f(i2);
    }

    public static int b$3eface7e(n nVar) {
        int e = e(2);
        int serializedSize = nVar.getSerializedSize();
        return e + serializedSize + g(serializedSize);
    }

    public static int b(int i, c cVar) {
        return e(i) + g(cVar.b()) + cVar.b();
    }

    public static int b(int i, int i2) {
        return e(i) + g(i2);
    }

    public final void c() {
        if (this.d != null) {
            throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array.");
        }
        if (this.b - this.c != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public final void d(int i) throws IOException {
        byte b = (byte) i;
        if (this.c == this.b) {
            d();
        }
        byte[] bArr = this.a;
        int i2 = this.c;
        this.c = i2 + 1;
        bArr[i2] = b;
    }
}
