package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class zzaa {

    /* loaded from: classes.dex */
    public static final class zza {
        private final List<String> zg;
        private final Object zzcmu;

        private zza(Object obj) {
            this.zzcmu = zzab.zzy(obj);
            this.zg = new ArrayList();
        }

        /* synthetic */ zza(Object obj, byte b) {
            this(obj);
        }

        public final String toString() {
            StringBuilder append = new StringBuilder(100).append(this.zzcmu.getClass().getSimpleName()).append('{');
            int size = this.zg.size();
            for (int i = 0; i < size; i++) {
                append.append(this.zg.get(i));
                if (i < size - 1) {
                    append.append(", ");
                }
            }
            return append.append('}').toString();
        }

        public final zza zzg(String str, Object obj) {
            List<String> list = this.zg;
            String str2 = (String) zzab.zzy(str);
            String valueOf = String.valueOf(String.valueOf(obj));
            list.add(new StringBuilder(String.valueOf(str2).length() + 1 + String.valueOf(valueOf).length()).append(str2).append("=").append(valueOf).toString());
            return this;
        }
    }

    public static boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static zza zzx(Object obj) {
        return new zza(obj, (byte) 0);
    }
}
