package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzpm;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class zzpx implements zzpz {
    private final zzqa tw;

    public zzpx(zzqa zzqaVar) {
        this.tw = zzqaVar;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final boolean disconnect() {
        return true;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnected(Bundle bundle) {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void onConnectionSuspended(int i) {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, R extends Result, T extends zzpm.zza<R, A>> T zzc(T t) {
        this.tw.th.uc.add(t);
        return t;
    }

    @Override // com.google.android.gms.internal.zzpz
    public final <A extends Api.zzb, T extends zzpm.zza<? extends Result, A>> T zzd(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void begin() {
        Iterator<Api.zze> it = this.tw.ui.values().iterator();
        while (it.hasNext()) {
            it.next().disconnect();
        }
        this.tw.th.uj = Collections.emptySet();
    }

    @Override // com.google.android.gms.internal.zzpz
    public final void connect() {
        zzqa zzqaVar = this.tw;
        zzqaVar.tr.lock();
        try {
            zzqaVar.uy = new zzpw(zzqaVar, zzqaVar.tN, zzqaVar.tO, zzqaVar.tz, zzqaVar.si, zzqaVar.tr, zzqaVar.mContext);
            zzqaVar.uy.begin();
            zzqaVar.uv.signalAll();
        } finally {
            zzqaVar.tr.unlock();
        }
    }
}
