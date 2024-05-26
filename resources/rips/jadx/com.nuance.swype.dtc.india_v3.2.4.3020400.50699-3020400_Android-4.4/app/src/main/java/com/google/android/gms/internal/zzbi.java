package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public final class zzbi implements Callable {
    private final zzax zzaey;
    private final zzae.zza zzaha;

    public zzbi(zzax zzaxVar, zzae.zza zzaVar) {
        this.zzaey = zzaxVar;
        this.zzaha = zzaVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // java.util.concurrent.Callable
    /* renamed from: zzcx, reason: merged with bridge method [inline-methods] */
    public Void call() throws Exception {
        if (this.zzaey.zzagn != null) {
            this.zzaey.zzagn.get();
        }
        zzae.zza zzaVar = this.zzaey.zzagm;
        if (zzaVar == null) {
            return null;
        }
        try {
            synchronized (this.zzaha) {
                zzae.zza zzaVar2 = this.zzaha;
                byte[] zzf = zzapv.zzf(zzaVar);
                zzapv.zzb$16844d7a(zzaVar2, zzf, zzf.length);
            }
            return null;
        } catch (zzapu e) {
            return null;
        }
    }
}
