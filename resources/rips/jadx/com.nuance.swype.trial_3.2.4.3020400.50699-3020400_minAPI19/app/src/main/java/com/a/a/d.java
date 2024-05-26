package com.a.a;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class d {
    final byte[] a;
    int b;
    private int c;
    int d;
    private final InputStream e;
    private int f;
    private int g;
    private int h;
    int i;
    int j;
    private int k;

    private d(InputStream inputStream) {
        this.h = Integer.MAX_VALUE;
        this.j = 64;
        this.k = 67108864;
        this.a = new byte[HardKeyboardManager.META_CTRL_ON];
        this.b = 0;
        this.d = 0;
        this.g = 0;
        this.e = inputStream;
    }

    private d(byte[] bArr, int i, int i2) {
        this.h = Integer.MAX_VALUE;
        this.j = 64;
        this.k = 67108864;
        this.a = bArr;
        this.b = i + i2;
        this.d = i;
        this.g = -i;
        this.e = null;
    }

    public static int a(int i, InputStream inputStream) throws IOException {
        if ((i & 128) != 0) {
            i &= 127;
            int i2 = 7;
            while (true) {
                if (i2 >= 32) {
                    while (i2 < 64) {
                        int read = inputStream.read();
                        if (read == -1) {
                            throw j.b();
                        }
                        if ((read & 128) != 0) {
                            i2 += 7;
                        }
                    }
                    throw j.d();
                }
                int read2 = inputStream.read();
                if (read2 != -1) {
                    i |= (read2 & 127) << i2;
                    if ((read2 & 128) == 0) {
                        break;
                    }
                    i2 += 7;
                } else {
                    throw j.b();
                }
            }
        }
        return i;
    }

    public static d a(InputStream inputStream) {
        return new d(inputStream);
    }

    public static d a(byte[] bArr, int i, int i2) {
        d dVar = new d(bArr, i, i2);
        try {
            dVar.d(i2);
            return dVar;
        } catch (j e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean a(boolean z) throws IOException {
        if (this.d < this.b) {
            throw new IllegalStateException("refillBuffer() called when buffer wasn't empty.");
        }
        if (this.g + this.b == this.h) {
            if (z) {
                throw j.b();
            }
            return false;
        }
        this.g += this.b;
        this.d = 0;
        this.b = this.e == null ? -1 : this.e.read(this.a);
        if (this.b == 0 || this.b < -1) {
            throw new IllegalStateException("InputStream#read(byte[]) returned invalid result: " + this.b + "\nThe InputStream implementation is buggy.");
        }
        if (this.b == -1) {
            this.b = 0;
            if (z) {
                throw j.b();
            }
            return false;
        }
        z();
        int i = this.g + this.b + this.c;
        if (i > this.k || i < 0) {
            throw j.i();
        }
        return true;
    }

    private void g(int i) throws IOException {
        if (i < 0) {
            throw j.c();
        }
        if (this.g + this.d + i > this.h) {
            g((this.h - this.g) - this.d);
            throw j.b();
        }
        if (i <= this.b - this.d) {
            this.d += i;
            return;
        }
        int i2 = this.b - this.d;
        this.d = this.b;
        a(true);
        while (i - i2 > this.b) {
            i2 += this.b;
            this.d = this.b;
            a(true);
        }
        this.d = i - i2;
    }

    private byte y() throws IOException {
        if (this.d == this.b) {
            a(true);
        }
        byte[] bArr = this.a;
        int i = this.d;
        this.d = i + 1;
        return bArr[i];
    }

    private void z() {
        this.b += this.c;
        int i = this.g + this.b;
        if (i <= this.h) {
            this.c = 0;
        } else {
            this.c = i - this.h;
            this.b -= this.c;
        }
    }

    public final <T extends n> T a(p<T> pVar, f fVar) throws IOException {
        int s = s();
        if (this.i >= this.j) {
            throw j.h();
        }
        int d = d(s);
        this.i++;
        T parsePartialFrom = pVar.parsePartialFrom(this, fVar);
        a(0);
        this.i--;
        e(d);
        return parsePartialFrom;
    }

    public final void a(int i) throws j {
        if (this.f != i) {
            throw j.f();
        }
    }

    public final int d(int i) throws j {
        if (i < 0) {
            throw j.c();
        }
        int i2 = this.g + this.d + i;
        int i3 = this.h;
        if (i2 > i3) {
            throw j.b();
        }
        this.h = i2;
        z();
        return i3;
    }

    public final void e(int i) {
        this.h = i;
        z();
    }

    public final byte[] f(int i) throws IOException {
        if (i < 0) {
            throw j.c();
        }
        if (this.g + this.d + i > this.h) {
            g((this.h - this.g) - this.d);
            throw j.b();
        }
        if (i <= this.b - this.d) {
            byte[] bArr = new byte[i];
            System.arraycopy(this.a, this.d, bArr, 0, i);
            this.d += i;
            return bArr;
        }
        if (i < 4096) {
            byte[] bArr2 = new byte[i];
            int i2 = this.b - this.d;
            System.arraycopy(this.a, this.d, bArr2, 0, i2);
            this.d = this.b;
            a(true);
            while (i - i2 > this.b) {
                System.arraycopy(this.a, 0, bArr2, i2, this.b);
                i2 += this.b;
                this.d = this.b;
                a(true);
            }
            System.arraycopy(this.a, 0, bArr2, i2, i - i2);
            this.d = i - i2;
            return bArr2;
        }
        int i3 = this.d;
        int i4 = this.b;
        this.g += this.b;
        this.d = 0;
        this.b = 0;
        ArrayList arrayList = new ArrayList();
        int i5 = i - (i4 - i3);
        while (i5 > 0) {
            byte[] bArr3 = new byte[Math.min(i5, HardKeyboardManager.META_CTRL_ON)];
            int i6 = 0;
            while (i6 < bArr3.length) {
                int read = this.e == null ? -1 : this.e.read(bArr3, i6, bArr3.length - i6);
                if (read == -1) {
                    throw j.b();
                }
                this.g += read;
                i6 += read;
            }
            int length = i5 - bArr3.length;
            arrayList.add(bArr3);
            i5 = length;
        }
        byte[] bArr4 = new byte[i];
        int i7 = i4 - i3;
        System.arraycopy(this.a, i3, bArr4, 0, i7);
        Iterator it = arrayList.iterator();
        while (true) {
            int i8 = i7;
            if (!it.hasNext()) {
                return bArr4;
            }
            byte[] bArr5 = (byte[]) it.next();
            System.arraycopy(bArr5, 0, bArr4, i8, bArr5.length);
            i7 = bArr5.length + i8;
        }
    }

    public final c l() throws IOException {
        int s = s();
        if (s == 0) {
            return c.a;
        }
        if (s > this.b - this.d || s <= 0) {
            return c.a(f(s));
        }
        c a = c.a(this.a, this.d, s);
        this.d = s + this.d;
        return a;
    }

    public final int m() throws IOException {
        return s();
    }

    public final int s() throws IOException {
        byte y = y();
        if (y >= 0) {
            return y;
        }
        int i = y & Byte.MAX_VALUE;
        byte y2 = y();
        if (y2 >= 0) {
            return i | (y2 << 7);
        }
        int i2 = i | ((y2 & Byte.MAX_VALUE) << 7);
        byte y3 = y();
        if (y3 >= 0) {
            return i2 | (y3 << 14);
        }
        int i3 = i2 | ((y3 & Byte.MAX_VALUE) << 14);
        byte y4 = y();
        if (y4 >= 0) {
            return i3 | (y4 << 21);
        }
        int i4 = i3 | ((y4 & Byte.MAX_VALUE) << 21);
        byte y5 = y();
        int i5 = i4 | (y5 << 28);
        if (y5 >= 0) {
            return i5;
        }
        for (int i6 = 0; i6 < 5; i6++) {
            if (y() >= 0) {
                return i5;
            }
        }
        throw j.d();
    }

    public final long t() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            j |= (r3 & Byte.MAX_VALUE) << i;
            if ((y() & 128) == 0) {
                return j;
            }
        }
        throw j.d();
    }

    public final int u() throws IOException {
        return (y() & 255) | ((y() & 255) << 8) | ((y() & 255) << 16) | ((y() & 255) << 24);
    }

    public final long v() throws IOException {
        return ((y() & 255) << 8) | (y() & 255) | ((y() & 255) << 16) | ((y() & 255) << 24) | ((y() & 255) << 32) | ((y() & 255) << 40) | ((y() & 255) << 48) | ((y() & 255) << 56);
    }

    public final int w() {
        if (this.h == Integer.MAX_VALUE) {
            return -1;
        }
        return this.h - (this.g + this.d);
    }

    public final int a() throws IOException {
        if (this.d == this.b && !a(false)) {
            this.f = 0;
            return 0;
        }
        this.f = s();
        if (x.b(this.f) == 0) {
            throw j.e();
        }
        return this.f;
    }

    public final boolean b(int i) throws IOException {
        int a;
        switch (x.a(i)) {
            case 0:
                s();
                return true;
            case 1:
                v();
                return true;
            case 2:
                g(s());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                u();
                return true;
            default:
                throw j.g();
        }
        do {
            a = a();
            if (a != 0) {
            }
            a(x.a(x.b(i), 4));
            return true;
        } while (b(a));
        a(x.a(x.b(i), 4));
        return true;
    }

    public final int q() throws IOException {
        int s = s();
        return (-(s & 1)) ^ (s >>> 1);
    }

    public final long r() throws IOException {
        long t = t();
        return (-(t & 1)) ^ (t >>> 1);
    }
}
