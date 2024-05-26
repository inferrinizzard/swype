package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class zzpr extends zzpn {
    @Override // com.google.android.gms.internal.zzpn
    protected final void zza(ConnectionResult connectionResult, int i) {
        zzqc zzqcVar = null;
        zzqcVar.zza(connectionResult, i);
    }

    @Override // com.google.android.gms.internal.zzpn
    protected final void zzaoo() {
        zzqc zzqcVar = null;
        zzqcVar.zzaoo();
    }

    @Override // com.google.android.gms.internal.zzpn, com.google.android.gms.internal.zzqj
    public final void onStop() {
        com.google.android.gms.common.util.zza zzaVar = null;
        super.onStop();
        Iterator it = zzaVar.iterator();
        while (it.hasNext()) {
            com.google.android.gms.common.api.zzc zzcVar = (com.google.android.gms.common.api.zzc) it.next();
            if (!zzcVar.rT.getAndSet(true)) {
                zzcVar.rO.release();
                zzqc zzqcVar = zzcVar.rR;
                zzqcVar.mHandler.sendMessage(zzqcVar.mHandler.obtainMessage(7, zzcVar.mId, zzcVar.rU.get() > 0 ? 1 : 2));
            }
        }
        zzaVar.clear();
        synchronized (zzqc.zzamr) {
            if (this == null) {
                zzqc zzqcVar2 = null;
                zzqcVar2.uL = null;
                zzqc zzqcVar3 = null;
                zzqcVar3.uM.clear();
            }
        }
    }
}
