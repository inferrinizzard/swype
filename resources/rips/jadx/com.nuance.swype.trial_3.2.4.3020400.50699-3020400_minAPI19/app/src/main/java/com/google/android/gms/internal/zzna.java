package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.text.TextUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zznc;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class zzna extends com.google.android.gms.common.internal.zzk<zznc> {
    private final Bundle dp;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final Bundle zzaeu() {
        return this.dp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzqz() {
        return "com.google.android.gms.auth.service.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzra() {
        return "com.google.android.gms.auth.api.internal.IAuthService";
    }

    public zzna(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, com.google.android.gms.auth.api.zzb zzbVar, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 16, zzgVar, connectionCallbacks, onConnectionFailedListener);
        this.dp = zzbVar == null ? new Bundle() : new Bundle((Bundle) null);
    }

    @Override // com.google.android.gms.common.internal.zzd, com.google.android.gms.common.api.Api.zze
    public final boolean zzafk() {
        Set<Scope> set;
        com.google.android.gms.common.internal.zzg zzgVar = this.tN;
        if (!TextUtils.isEmpty(zzgVar.aL != null ? zzgVar.aL.name : null)) {
            zzg.zza zzaVar = zzgVar.yk.get(com.google.android.gms.auth.api.zza.API);
            if (zzaVar == null || zzaVar.dT.isEmpty()) {
                set = zzgVar.rX;
            } else {
                HashSet hashSet = new HashSet(zzgVar.rX);
                hashSet.addAll(zzaVar.dT);
                set = hashSet;
            }
            if (!set.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final /* synthetic */ IInterface zzbb(IBinder iBinder) {
        return zznc.zza.zzch(iBinder);
    }
}
