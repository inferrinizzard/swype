package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public final class zze implements zzn {
    private final Executor zzr;

    public zze(final Handler handler) {
        this.zzr = new Executor() { // from class: com.google.android.gms.internal.zze.1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    @Override // com.google.android.gms.internal.zzn
    public final void zza(zzk<?> zzkVar, zzm<?> zzmVar) {
        zza(zzkVar, zzmVar, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zza implements Runnable {
        private final zzk zzu;
        private final zzm zzv;
        private final Runnable zzw;

        public zza(zzk zzkVar, zzm zzmVar, Runnable runnable) {
            this.zzu = zzkVar;
            this.zzv = zzmVar;
            this.zzw = runnable;
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (this.zzv.zzbg == null) {
                this.zzu.zza((zzk) this.zzv.result);
            } else {
                zzk zzkVar = this.zzu;
                zzr zzrVar = this.zzv.zzbg;
                if (zzkVar.zzag != null) {
                    zzkVar.zzag.zze(zzrVar);
                }
            }
            if (this.zzv.zzbh) {
                this.zzu.zzc("intermediate-response");
            } else {
                this.zzu.zzd("done");
            }
            if (this.zzw != null) {
                this.zzw.run();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzn
    public final void zza(zzk<?> zzkVar, zzm<?> zzmVar, Runnable runnable) {
        zzkVar.zzal = true;
        zzkVar.zzc("post-response");
        this.zzr.execute(new zza(zzkVar, zzmVar, runnable));
    }

    @Override // com.google.android.gms.internal.zzn
    public final void zza(zzk<?> zzkVar, zzr zzrVar) {
        zzkVar.zzc("post-error");
        this.zzr.execute(new zza(zzkVar, new zzm(zzrVar), null));
    }
}
