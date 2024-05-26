package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzah;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzqa;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class zzpv implements zzpz {
    final zzqa tw;
    boolean tx = false;

    public zzpv(zzqa zzqaVar) {
        this.tw = zzqaVar;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void begin() {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void connect() {
        if (this.tx) {
            this.tx = false;
            this.tw.zza(new zzqa.zza(this) { // from class: com.google.android.gms.internal.zzpv.2
                @Override // com.google.android.gms.internal.zzqa.zza
                public final void zzapl() {
                    zzpv.this.tw.uB.zzm(null);
                }
            });
        }
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnected(Bundle bundle) {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnectionSuspended(int i) {
        this.tw.zzi(null);
        this.tw.uB.zzc(i, this.tx);
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        return (T) zzd(t);
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        try {
            this.tw.th.uo.zzg(t);
            zzpy zzpyVar = this.tw.th;
            Api.zze zzeVar = zzpyVar.ui.get(t.sJ);
            com.google.android.gms.common.internal.zzab.zzb(zzeVar, "Appropriate Api was not requested.");
            if (zzeVar.isConnected() || !this.tw.ux.containsKey(t.sJ)) {
                boolean z = zzeVar instanceof zzah;
                A a = zzeVar;
                if (z) {
                    a = ((zzah) zzeVar).zn;
                }
                t.zzb(a);
            } else {
                t.zzz(new Status(17));
            }
        } catch (DeadObjectException e) {
            this.tw.zza(new zzqa.zza(this) { // from class: com.google.android.gms.internal.zzpv.1
                @Override // com.google.android.gms.internal.zzqa.zza
                public final void zzapl() {
                    zzpv.this.onConnectionSuspended(1);
                }
            });
        }
        return t;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final boolean disconnect() {
        if (this.tx) {
            return false;
        }
        if (!this.tw.th.zzapx()) {
            this.tw.zzi(null);
            return true;
        }
        this.tx = true;
        Iterator<zzqx> it = this.tw.th.un.iterator();
        while (it.hasNext()) {
            it.next().vy = null;
        }
        return false;
    }
}
