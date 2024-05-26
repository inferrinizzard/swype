package com.a.a;

import com.a.a.h;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class f {
    private static volatile boolean a = false;
    private static final f c = new f((byte) 0);
    final Map<a, h.c<?, ?>> b;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class a {
        private final Object a;
        private final int b;

        /* JADX INFO: Access modifiers changed from: package-private */
        public a(Object obj, int i) {
            this.a = obj;
            this.b = i;
        }

        public final boolean equals(Object obj) {
            if (!(obj instanceof a)) {
                return false;
            }
            a aVar = (a) obj;
            return this.a == aVar.a && this.b == aVar.b;
        }

        public final int hashCode() {
            return (System.identityHashCode(this.a) * 65535) + this.b;
        }
    }

    f() {
        this.b = new HashMap();
    }

    private f(byte b) {
        this.b = Collections.emptyMap();
    }

    public static f a() {
        return c;
    }
}
