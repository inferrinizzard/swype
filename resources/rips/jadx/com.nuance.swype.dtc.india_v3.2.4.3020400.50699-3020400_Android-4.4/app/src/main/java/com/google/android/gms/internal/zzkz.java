package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzkz {
    final Object zzcnx = new Object();
    private final List<Runnable> zzcny = new ArrayList();
    final List<Runnable> zzcnz = new ArrayList();
    boolean zzcoa = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zzf(Runnable runnable) {
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(runnable);
    }

    public final void zzc(Runnable runnable) {
        synchronized (this.zzcnx) {
            if (this.zzcoa) {
                zzkg.zza(runnable);
            } else {
                this.zzcny.add(runnable);
            }
        }
    }

    public final void zztz() {
        synchronized (this.zzcnx) {
            if (this.zzcoa) {
                return;
            }
            Iterator<Runnable> it = this.zzcny.iterator();
            while (it.hasNext()) {
                zzkg.zza(it.next());
            }
            Iterator<Runnable> it2 = this.zzcnz.iterator();
            while (it2.hasNext()) {
                zzf(it2.next());
            }
            this.zzcny.clear();
            this.zzcnz.clear();
            this.zzcoa = true;
        }
    }
}
