package com.google.android.gms.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzfc implements Iterable<zzfb> {
    public final List<zzfb> zzbje = new LinkedList();

    public static boolean zzd(zzlh zzlhVar) {
        zzfb zzf = zzf(zzlhVar);
        if (zzf == null) {
            return false;
        }
        zzf.zzbjb.abort();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static zzfb zzf(zzlh zzlhVar) {
        Iterator<zzfb> it = com.google.android.gms.ads.internal.zzu.zzgj().iterator();
        while (it.hasNext()) {
            zzfb next = it.next();
            if (next.zzbgf == zzlhVar) {
                return next;
            }
        }
        return null;
    }

    @Override // java.lang.Iterable
    public final Iterator<zzfb> iterator() {
        return this.zzbje.iterator();
    }
}
