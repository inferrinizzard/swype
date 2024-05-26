package com.a.a;

/* loaded from: classes.dex */
public final class x {
    static final int a = 11;
    static final int b = 12;
    static final int c = 16;
    static final int d = 26;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: classes.dex */
    public static class a {
        public static final a a = new a("DOUBLE", 0, b.DOUBLE, 1);
        public static final a b = new a("FLOAT", 1, b.FLOAT, 5);
        public static final a c = new a("INT64", 2, b.LONG, 0);
        public static final a d = new a("UINT64", 3, b.LONG, 0);
        public static final a e = new a("INT32", 4, b.INT, 0);
        public static final a f = new a("FIXED64", 5, b.LONG, 1);
        public static final a g = new a("FIXED32", 6, b.INT, 5);
        public static final a h = new a("BOOL", 7, b.BOOLEAN, 0);
        public static final a i = new y("STRING", b.STRING);
        public static final a j = new z("GROUP", b.MESSAGE);
        public static final a k = new aa("MESSAGE", b.MESSAGE);
        public static final a l = new ab("BYTES", b.BYTE_STRING);
        public static final a m = new a("UINT32", 12, b.INT, 0);
        public static final a n = new a("ENUM", 13, b.ENUM, 0);
        public static final a o = new a("SFIXED32", 14, b.INT, 5);
        public static final a p = new a("SFIXED64", 15, b.LONG, 1);
        public static final a q = new a("SINT32", 16, b.INT, 0);
        public static final a r = new a("SINT64", 17, b.LONG, 0);
        private static final /* synthetic */ a[] u = {a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r};
        final b s;
        final int t;

        private a(String str, int i2, b bVar, int i3) {
            this.s = bVar;
            this.t = i3;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public /* synthetic */ a(String str, int i2, b bVar, int i3, byte b2) {
            this(str, i2, bVar, i3);
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) u.clone();
        }

        public boolean c() {
            return true;
        }
    }

    /* loaded from: classes.dex */
    public enum b {
        INT(0),
        LONG(0L),
        FLOAT(Float.valueOf(0.0f)),
        DOUBLE(Double.valueOf(0.0d)),
        BOOLEAN(false),
        STRING(""),
        BYTE_STRING(c.a),
        ENUM(null),
        MESSAGE(null);

        private final Object j;

        b(Object obj) {
            this.j = obj;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(int i) {
        return i & 7;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(int i, int i2) {
        return (i << 3) | i2;
    }

    public static int b(int i) {
        return i >>> 3;
    }
}
