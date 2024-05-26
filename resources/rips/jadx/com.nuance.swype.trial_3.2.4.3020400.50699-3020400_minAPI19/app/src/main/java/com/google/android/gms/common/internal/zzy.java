package com.google.android.gms.common.internal;

import java.util.Iterator;

/* loaded from: classes.dex */
public final class zzy {
    private final String separator;

    public zzy(String str) {
        this.separator = str;
    }

    private static CharSequence zzw(Object obj) {
        return obj instanceof CharSequence ? (CharSequence) obj : obj.toString();
    }

    public final StringBuilder zza(StringBuilder sb, Iterable<?> iterable) {
        Iterator<?> it = iterable.iterator();
        if (it.hasNext()) {
            sb.append(zzw(it.next()));
            while (it.hasNext()) {
                sb.append(this.separator);
                sb.append(zzw(it.next()));
            }
        }
        return sb;
    }
}
