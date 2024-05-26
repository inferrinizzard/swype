package com.a.a;

import com.a.a.c;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class m extends c {
    protected final byte[] c;
    private int d = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public m(byte[] bArr) {
        this.c = bArr;
    }

    @Override // com.a.a.c, java.lang.Iterable
    /* renamed from: a */
    public final c.a iterator() {
        return new a(this, (byte) 0);
    }

    public final byte b(int i) {
        return this.c[i];
    }

    @Override // com.a.a.c
    public final int b() {
        return this.c.length;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final int b(int i, int i2, int i3) {
        byte[] bArr = this.c;
        int i4 = i2 + 0;
        int i5 = i4 + i3;
        while (i4 < i5) {
            i = (i * 31) + bArr[i4];
            i4++;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final void b(byte[] bArr, int i, int i2, int i3) {
        System.arraycopy(this.c, i, bArr, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final int i() {
        return this.d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class a implements c.a {
        private int b;
        private final int c;

        private a() {
            this.b = 0;
            this.c = m.this.b();
        }

        /* synthetic */ a(m mVar, byte b) {
            this();
        }

        @Override // com.a.a.c.a
        public final byte a() {
            try {
                byte[] bArr = m.this.c;
                int i = this.b;
                this.b = i + 1;
                return bArr[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.b < this.c;
        }

        @Override // java.util.Iterator
        public final void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Iterator
        public final /* synthetic */ Byte next() {
            return Byte.valueOf(a());
        }
    }

    @Override // com.a.a.c
    public final String b(String str) throws UnsupportedEncodingException {
        return new String(this.c, 0, this.c.length, str);
    }

    @Override // com.a.a.c
    public final boolean f() {
        return w.b(this.c, 0, this.c.length + 0) == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x001d, code lost:            if (r5[r1] > (-65)) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0048, code lost:            if (r5[r1] > (-65)) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0083, code lost:            if (r5[r1] > (-65)) goto L47;     */
    @Override // com.a.a.c
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final int a(int r11, int r12, int r13) {
        /*
            r10 = this;
            r9 = -32
            r4 = -96
            r2 = -1
            r8 = -65
            int r1 = r12 + 0
            byte[] r5 = r10.c
            int r6 = r1 + r13
            if (r11 == 0) goto L87
            if (r1 < r6) goto L12
        L11:
            return r11
        L12:
            byte r7 = (byte) r11
            if (r7 >= r9) goto L21
            r0 = -62
            if (r7 < r0) goto L1f
            int r0 = r1 + 1
            r1 = r5[r1]
            if (r1 <= r8) goto L88
        L1f:
            r11 = r2
            goto L11
        L21:
            r0 = -16
            if (r7 >= r0) goto L4c
            int r0 = r11 >> 8
            r0 = r0 ^ (-1)
            byte r0 = (byte) r0
            if (r0 != 0) goto L38
            int r3 = r1 + 1
            r0 = r5[r1]
            if (r3 < r6) goto L37
            int r11 = com.a.a.w.a(r7, r0)
            goto L11
        L37:
            r1 = r3
        L38:
            if (r0 > r8) goto L4a
            if (r7 != r9) goto L3e
            if (r0 < r4) goto L4a
        L3e:
            r3 = -19
            if (r7 != r3) goto L44
            if (r0 >= r4) goto L4a
        L44:
            int r0 = r1 + 1
            r1 = r5[r1]
            if (r1 <= r8) goto L88
        L4a:
            r11 = r2
            goto L11
        L4c:
            int r0 = r11 >> 8
            r0 = r0 ^ (-1)
            byte r3 = (byte) r0
            r0 = 0
            if (r3 != 0) goto L5f
            int r3 = r1 + 1
            r1 = r5[r1]
            if (r3 < r6) goto L8d
            int r11 = com.a.a.w.a(r7, r1)
            goto L11
        L5f:
            int r0 = r11 >> 16
            byte r0 = (byte) r0
            r4 = r3
            r3 = r1
        L64:
            if (r0 != 0) goto L71
            int r1 = r3 + 1
            r0 = r5[r3]
            if (r1 < r6) goto L72
            int r11 = com.a.a.w.a(r7, r4, r0)
            goto L11
        L71:
            r1 = r3
        L72:
            if (r4 > r8) goto L85
            int r3 = r7 << 28
            int r4 = r4 + 112
            int r3 = r3 + r4
            int r3 = r3 >> 30
            if (r3 != 0) goto L85
            if (r0 > r8) goto L85
            int r0 = r1 + 1
            r1 = r5[r1]
            if (r1 <= r8) goto L88
        L85:
            r11 = r2
            goto L11
        L87:
            r0 = r1
        L88:
            int r11 = com.a.a.w.b(r5, r0, r6)
            goto L11
        L8d:
            r4 = r1
            goto L64
        */
        throw new UnsupportedOperationException("Method not decompiled: com.a.a.m.a(int, int, int):int");
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof c) && this.c.length == ((c) obj).b()) {
            if (this.c.length == 0) {
                return true;
            }
            if (obj instanceof m) {
                return a((m) obj, 0, this.c.length);
            }
            if (obj instanceof q) {
                return obj.equals(this);
            }
            throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + obj.getClass());
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean a(m mVar, int i, int i2) {
        if (i2 > mVar.c.length) {
            throw new IllegalArgumentException("Length too large: " + i2 + this.c.length);
        }
        if (i + i2 > mVar.c.length) {
            throw new IllegalArgumentException("Ran off end of other: " + i + ", " + i2 + ", " + mVar.c.length);
        }
        byte[] bArr = this.c;
        byte[] bArr2 = mVar.c;
        int i3 = i2 + 0;
        int i4 = i + 0;
        int i5 = 0;
        while (i5 < i3) {
            if (bArr[i5] != bArr2[i4]) {
                return false;
            }
            i5++;
            i4++;
        }
        return true;
    }

    public final int hashCode() {
        int i = this.d;
        if (i == 0) {
            int length = this.c.length;
            i = b(length, 0, length);
            if (i == 0) {
                i = 1;
            }
            this.d = i;
        }
        return i;
    }

    @Override // com.a.a.c
    public final InputStream g() {
        return new ByteArrayInputStream(this.c, 0, this.c.length);
    }

    @Override // com.a.a.c
    public final d h() {
        return d.a(this.c, 0, this.c.length);
    }
}
