package com.a.a;

import com.a.a.g.a;
import com.a.a.i;
import com.a.a.x;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class g<FieldDescriptorType extends a<FieldDescriptorType>> {
    private static final g d = new g((byte) 0);
    private boolean b;
    private boolean c = false;
    private final r<FieldDescriptorType, Object> a = r.a(16);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.a.a.g$1, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] b = new int[x.a.values().length];

        static {
            try {
                b[x.a.a.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                b[x.a.b.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                b[x.a.c.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                b[x.a.d.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                b[x.a.e.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                b[x.a.f.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                b[x.a.g.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                b[x.a.h.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                b[x.a.i.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                b[x.a.l.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                b[x.a.m.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                b[x.a.o.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                b[x.a.p.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                b[x.a.q.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                b[x.a.r.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                b[x.a.j.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                b[x.a.k.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                b[x.a.n.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            a = new int[x.b.values().length];
            try {
                a[x.b.INT.ordinal()] = 1;
            } catch (NoSuchFieldError e19) {
            }
            try {
                a[x.b.LONG.ordinal()] = 2;
            } catch (NoSuchFieldError e20) {
            }
            try {
                a[x.b.FLOAT.ordinal()] = 3;
            } catch (NoSuchFieldError e21) {
            }
            try {
                a[x.b.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError e22) {
            }
            try {
                a[x.b.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e23) {
            }
            try {
                a[x.b.STRING.ordinal()] = 6;
            } catch (NoSuchFieldError e24) {
            }
            try {
                a[x.b.BYTE_STRING.ordinal()] = 7;
            } catch (NoSuchFieldError e25) {
            }
            try {
                a[x.b.ENUM.ordinal()] = 8;
            } catch (NoSuchFieldError e26) {
            }
            try {
                a[x.b.MESSAGE.ordinal()] = 9;
            } catch (NoSuchFieldError e27) {
            }
        }
    }

    /* loaded from: classes.dex */
    public interface a<T extends a<T>> extends Comparable<T> {
        x.a a();

        boolean b();
    }

    private g() {
    }

    public final void a(FieldDescriptorType fielddescriptortype, Object obj) {
        if (!fielddescriptortype.b()) {
            a(fielddescriptortype.a(), obj);
        } else {
            if (!(obj instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll((List) obj);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                a(fielddescriptortype.a(), it.next());
            }
            obj = arrayList;
        }
        if (obj instanceof k) {
            this.c = true;
        }
        this.a.a((r<FieldDescriptorType, Object>) fielddescriptortype, (FieldDescriptorType) obj);
    }

    public final void b(FieldDescriptorType fielddescriptortype, Object obj) {
        List list;
        if (!fielddescriptortype.b()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        a(fielddescriptortype.a(), obj);
        Object a2 = a(fielddescriptortype);
        if (a2 == null) {
            list = new ArrayList();
            this.a.a((r<FieldDescriptorType, Object>) fielddescriptortype, (FieldDescriptorType) list);
        } else {
            list = (List) a2;
        }
        list.add(obj);
    }

    private g(byte b) {
        if (this.b) {
            return;
        }
        this.a.a();
        this.b = true;
    }

    public final Object a(FieldDescriptorType fielddescriptortype) {
        Object obj = this.a.get(fielddescriptortype);
        if (!(obj instanceof k)) {
            return obj;
        }
        k kVar = (k) obj;
        kVar.b();
        return kVar.d;
    }

    private static void a(x.a aVar, Object obj) {
        boolean z = false;
        if (obj == null) {
            throw new NullPointerException();
        }
        switch (aVar.s) {
            case INT:
                z = obj instanceof Integer;
                break;
            case LONG:
                z = obj instanceof Long;
                break;
            case FLOAT:
                z = obj instanceof Float;
                break;
            case DOUBLE:
                z = obj instanceof Double;
                break;
            case BOOLEAN:
                z = obj instanceof Boolean;
                break;
            case STRING:
                z = obj instanceof String;
                break;
            case BYTE_STRING:
                z = obj instanceof c;
                break;
            case ENUM:
                z = obj instanceof i.a;
                break;
            case MESSAGE:
                if ((obj instanceof n) || (obj instanceof k)) {
                    z = true;
                    break;
                }
                break;
        }
        if (!z) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(x.a aVar, boolean z) {
        if (z) {
            return 2;
        }
        return aVar.t;
    }

    public static Object a(d dVar, x.a aVar) throws IOException {
        switch (AnonymousClass1.b[aVar.ordinal()]) {
            case 1:
                return Double.valueOf(Double.longBitsToDouble(dVar.v()));
            case 2:
                return Float.valueOf(Float.intBitsToFloat(dVar.u()));
            case 3:
                return Long.valueOf(dVar.t());
            case 4:
                return Long.valueOf(dVar.t());
            case 5:
                return Integer.valueOf(dVar.s());
            case 6:
                return Long.valueOf(dVar.v());
            case 7:
                return Integer.valueOf(dVar.u());
            case 8:
                return Boolean.valueOf(dVar.s() != 0);
            case 9:
                int s = dVar.s();
                if (s > dVar.b - dVar.d || s <= 0) {
                    return new String(dVar.f(s), "UTF-8");
                }
                String str = new String(dVar.a, dVar.d, s, "UTF-8");
                dVar.d = s + dVar.d;
                return str;
            case 10:
                return dVar.l();
            case 11:
                return Integer.valueOf(dVar.m());
            case 12:
                return Integer.valueOf(dVar.u());
            case 13:
                return Long.valueOf(dVar.v());
            case 14:
                return Integer.valueOf(dVar.q());
            case 15:
                return Long.valueOf(dVar.r());
            case 16:
                throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
            case 17:
                throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
            case 18:
                throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        g gVar = new g();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.a.c()) {
                break;
            }
            Map.Entry<FieldDescriptorType, Object> b = this.a.b(i2);
            gVar.a((g) b.getKey(), b.getValue());
            i = i2 + 1;
        }
        for (Map.Entry<FieldDescriptorType, Object> entry : this.a.d()) {
            gVar.a((g) entry.getKey(), entry.getValue());
        }
        gVar.c = this.c;
        return gVar;
    }
}
