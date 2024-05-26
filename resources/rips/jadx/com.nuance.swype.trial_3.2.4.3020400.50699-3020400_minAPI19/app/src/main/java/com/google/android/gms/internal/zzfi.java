package com.google.android.gms.internal;

import android.os.Handler;
import android.os.RemoteException;
import java.util.LinkedList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
final class zzfi {
    final List<zza> zzalc = new LinkedList();

    /* loaded from: classes.dex */
    interface zza {
        void zzb(zzfj zzfjVar) throws RemoteException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zza(final zzfj zzfjVar) {
        Handler handler = zzkh.zzclc;
        for (final zza zzaVar : this.zzalc) {
            handler.post(new Runnable() { // from class: com.google.android.gms.internal.zzfi.7
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzaVar.zzb(zzfjVar);
                    } catch (RemoteException e) {
                        zzkd.zzd("Could not propagate interstitial ad event.", e);
                    }
                }
            });
        }
    }
}
