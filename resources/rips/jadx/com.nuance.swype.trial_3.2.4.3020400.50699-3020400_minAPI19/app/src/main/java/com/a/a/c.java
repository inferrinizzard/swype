package com.a.a;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class c implements Iterable<Byte> {
    public static final c a;
    static final /* synthetic */ boolean b;

    /* loaded from: classes.dex */
    public interface a extends Iterator<Byte> {
        byte a();
    }

    /* loaded from: classes.dex */
    static final class b {
        final e a;
        final byte[] b;

        private b(int i) {
            this.b = new byte[i];
            this.a = e.a(this.b);
        }

        /* synthetic */ b(int i, byte b) {
            this(i);
        }
    }

    static {
        b = !c.class.desiredAssertionStatus();
        a = new m(new byte[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static b a(int i) {
        return new b(i, (byte) 0);
    }

    public static c a(String str) {
        try {
            return new m(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported?", e);
        }
    }

    public static c a(byte[] bArr) {
        return a(bArr, 0, bArr.length);
    }

    public static c a(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return new m(bArr2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int a(int i, int i2, int i3);

    @Override // java.lang.Iterable
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public abstract a iterator();

    public final void a(byte[] bArr, int i, int i2, int i3) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("Source offset < 0: " + i);
        }
        if (i2 < 0) {
            throw new IndexOutOfBoundsException("Target offset < 0: " + i2);
        }
        if (i3 < 0) {
            throw new IndexOutOfBoundsException("Length < 0: " + i3);
        }
        if (i + i3 > b()) {
            throw new IndexOutOfBoundsException("Source end offset < 0: " + (i + i3));
        }
        if (i2 + i3 > bArr.length) {
            throw new IndexOutOfBoundsException("Target end offset < 0: " + (i2 + i3));
        }
        if (i3 > 0) {
            b(bArr, i, i2, i3);
        }
    }

    public abstract int b();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int b(int i, int i2, int i3);

    public abstract String b(String str) throws UnsupportedEncodingException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void b(byte[] bArr, int i, int i2, int i3);

    public final byte[] d() {
        int b2 = b();
        byte[] bArr = new byte[b2];
        b(bArr, 0, 0, b2);
        return bArr;
    }

    public final String e() {
        try {
            return b("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported?", e);
        }
    }

    public abstract boolean f();

    public abstract InputStream g();

    public abstract d h();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int i();

    public String toString() {
        return String.format("<ByteString@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(b()));
    }
}
