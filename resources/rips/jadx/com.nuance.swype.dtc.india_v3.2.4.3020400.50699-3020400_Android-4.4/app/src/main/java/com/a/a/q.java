package com.a.a;

import com.a.a.c;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class q extends com.a.a.c {
    private static final int[] c;
    private final int d;
    private final com.a.a.c e;
    private final com.a.a.c f;
    private final int g;
    private int h;

    /* loaded from: classes.dex */
    private class c extends InputStream {
        private a b;
        private m c;
        private int d;
        private int e;
        private int f;
        private int g;

        public c() {
            a();
        }

        private int a(byte[] bArr, int i, int i2) {
            int i3 = i2;
            int i4 = i;
            while (true) {
                if (i3 <= 0) {
                    break;
                }
                b();
                if (this.c != null) {
                    int min = Math.min(this.d - this.e, i3);
                    if (bArr != null) {
                        this.c.a(bArr, this.e, i4, min);
                        i4 += min;
                    }
                    this.e += min;
                    i3 -= min;
                } else if (i3 == i2) {
                    return -1;
                }
            }
            return i2 - i3;
        }

        private void a() {
            this.b = new a(q.this, (byte) 0);
            this.c = this.b.next();
            this.d = this.c.b();
            this.e = 0;
            this.f = 0;
        }

        private void b() {
            if (this.c == null || this.e != this.d) {
                return;
            }
            this.f += this.d;
            this.e = 0;
            if (this.b.hasNext()) {
                this.c = this.b.next();
                this.d = this.c.b();
            } else {
                this.c = null;
                this.d = 0;
            }
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            return q.this.b() - (this.f + this.e);
        }

        @Override // java.io.InputStream
        public final void mark(int i) {
            this.g = this.f + this.e;
        }

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            b();
            if (this.c == null) {
                return -1;
            }
            m mVar = this.c;
            int i = this.e;
            this.e = i + 1;
            return mVar.b(i) & 255;
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) {
            if (bArr == null) {
                throw new NullPointerException();
            }
            if (i < 0 || i2 < 0 || i2 > bArr.length - i) {
                throw new IndexOutOfBoundsException();
            }
            return a(bArr, i, i2);
        }

        @Override // java.io.InputStream
        public final synchronized void reset() {
            a();
            a(null, 0, this.g);
        }

        @Override // java.io.InputStream
        public final long skip(long j) {
            if (j < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (j > 2147483647L) {
                j = 2147483647L;
            }
            return a(null, 0, (int) j);
        }
    }

    static {
        int i = 1;
        ArrayList arrayList = new ArrayList();
        int i2 = 1;
        while (i > 0) {
            arrayList.add(Integer.valueOf(i));
            int i3 = i2 + i;
            i2 = i;
            i = i3;
        }
        arrayList.add(Integer.MAX_VALUE);
        c = new int[arrayList.size()];
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= c.length) {
                return;
            }
            c[i5] = ((Integer) arrayList.get(i5)).intValue();
            i4 = i5 + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final int a(int i, int i2, int i3) {
        if (i2 + i3 <= this.g) {
            return this.e.a(i, i2, i3);
        }
        if (i2 >= this.g) {
            return this.f.a(i, i2 - this.g, i3);
        }
        int i4 = this.g - i2;
        return this.f.a(this.e.a(i, i2, i4), 0, i3 - i4);
    }

    @Override // com.a.a.c, java.lang.Iterable
    /* renamed from: a */
    public final c.a iterator() {
        return new b(this, (byte) 0);
    }

    @Override // com.a.a.c
    public final int b() {
        return this.d;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final int b(int i, int i2, int i3) {
        if (i2 + i3 <= this.g) {
            return this.e.b(i, i2, i3);
        }
        if (i2 >= this.g) {
            return this.f.b(i, i2 - this.g, i3);
        }
        int i4 = this.g - i2;
        return this.f.b(this.e.b(i, i2, i4), 0, i3 - i4);
    }

    @Override // com.a.a.c
    public final String b(String str) throws UnsupportedEncodingException {
        return new String(d(), str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final void b(byte[] bArr, int i, int i2, int i3) {
        if (i + i3 <= this.g) {
            this.e.b(bArr, i, i2, i3);
        } else {
            if (i >= this.g) {
                this.f.b(bArr, i - this.g, i2, i3);
                return;
            }
            int i4 = this.g - i;
            this.e.b(bArr, i, i2, i4);
            this.f.b(bArr, 0, i2 + i4, i3 - i4);
        }
    }

    @Override // com.a.a.c
    public final boolean f() {
        return this.f.a(this.e.a(0, 0, this.g), 0, this.f.b()) == 0;
    }

    @Override // com.a.a.c
    public final InputStream g() {
        return new c();
    }

    @Override // com.a.a.c
    public final d h() {
        return d.a(new c());
    }

    public final int hashCode() {
        int i = this.h;
        if (i == 0) {
            i = b(this.d, 0, this.d);
            if (i == 0) {
                i = 1;
            }
            this.h = i;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.a.a.c
    public final int i() {
        return this.h;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class a implements Iterator<m> {
        private final Stack<q> a;
        private m b;

        private a(com.a.a.c cVar) {
            this.a = new Stack<>();
            this.b = a(cVar);
        }

        /* synthetic */ a(com.a.a.c cVar, byte b) {
            this(cVar);
        }

        private m a(com.a.a.c cVar) {
            com.a.a.c cVar2 = cVar;
            while (cVar2 instanceof q) {
                q qVar = (q) cVar2;
                this.a.push(qVar);
                cVar2 = qVar.e;
            }
            return (m) cVar2;
        }

        @Override // java.util.Iterator
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public final m next() {
            if (this.b == null) {
                throw new NoSuchElementException();
            }
            m mVar = this.b;
            this.b = b();
            return mVar;
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.b != null;
        }

        @Override // java.util.Iterator
        public final void remove() {
            throw new UnsupportedOperationException();
        }

        private m b() {
            while (!this.a.isEmpty()) {
                m a = a(this.a.pop().f);
                if (!(a.b() == 0)) {
                    return a;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class b implements c.a {
        int a;
        private final a c;
        private c.a d;

        private b() {
            this.c = new a(q.this, (byte) 0);
            this.d = this.c.next().iterator();
            this.a = q.this.b();
        }

        /* synthetic */ b(q qVar, byte b) {
            this();
        }

        @Override // com.a.a.c.a
        public final byte a() {
            if (!this.d.hasNext()) {
                this.d = this.c.next().iterator();
            }
            this.a--;
            return this.d.a();
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.a > 0;
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

    public final boolean equals(Object obj) {
        int i;
        byte b2 = 0;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof com.a.a.c)) {
            return false;
        }
        com.a.a.c cVar = (com.a.a.c) obj;
        if (this.d != cVar.b()) {
            return false;
        }
        if (this.d == 0) {
            return true;
        }
        if (this.h != 0 && (i = cVar.i()) != 0 && this.h != i) {
            return false;
        }
        a aVar = new a(this, b2);
        m next = aVar.next();
        a aVar2 = new a(cVar, b2);
        m next2 = aVar2.next();
        int i2 = 0;
        m mVar = next;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            int b3 = mVar.b() - i3;
            int b4 = next2.b() - i2;
            int min = Math.min(b3, b4);
            if (!(i3 == 0 ? mVar.a(next2, i2, min) : next2.a(mVar, i3, min))) {
                return false;
            }
            int i5 = i4 + min;
            if (i5 >= this.d) {
                if (i5 == this.d) {
                    return true;
                }
                throw new IllegalStateException();
            }
            if (min == b3) {
                mVar = aVar.next();
                i3 = 0;
            } else {
                i3 += min;
            }
            if (min == b4) {
                next2 = aVar2.next();
                i2 = 0;
                i4 = i5;
            } else {
                i2 += min;
                i4 = i5;
            }
        }
    }
}
