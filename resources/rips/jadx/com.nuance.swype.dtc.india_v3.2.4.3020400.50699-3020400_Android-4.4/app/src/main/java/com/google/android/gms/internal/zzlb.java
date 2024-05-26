package com.google.android.gms.internal;

import com.google.android.gms.internal.zzla;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@zzin
/* loaded from: classes.dex */
public class zzlb<T> implements zzla<T> {
    private final Object zzail = new Object();
    protected int zzblv = 0;
    protected final BlockingQueue<zza> zzcob = new LinkedBlockingQueue();
    protected T zzcoc;

    /* loaded from: classes.dex */
    class zza {
        public final zzla.zzc<T> zzcod;
        public final zzla.zza zzcoe;

        public zza(zzla.zzc<T> zzcVar, zzla.zza zzaVar) {
            this.zzcod = zzcVar;
            this.zzcoe = zzaVar;
        }
    }

    public int getStatus() {
        return this.zzblv;
    }

    public void reject() {
        synchronized (this.zzail) {
            if (this.zzblv != 0) {
                throw new UnsupportedOperationException();
            }
            this.zzblv = -1;
            Iterator it = this.zzcob.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzcoe.run();
            }
            this.zzcob.clear();
        }
    }

    @Override // com.google.android.gms.internal.zzla
    public void zza(zzla.zzc<T> zzcVar, zzla.zza zzaVar) {
        synchronized (this.zzail) {
            if (this.zzblv == 1) {
                zzcVar.zzd(this.zzcoc);
            } else if (this.zzblv == -1) {
                zzaVar.run();
            } else if (this.zzblv == 0) {
                this.zzcob.add(new zza(zzcVar, zzaVar));
            }
        }
    }

    @Override // com.google.android.gms.internal.zzla
    public void zzg(T t) {
        synchronized (this.zzail) {
            if (this.zzblv != 0) {
                throw new UnsupportedOperationException();
            }
            this.zzcoc = t;
            this.zzblv = 1;
            Iterator it = this.zzcob.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzcod.zzd(t);
            }
            this.zzcob.clear();
        }
    }
}
