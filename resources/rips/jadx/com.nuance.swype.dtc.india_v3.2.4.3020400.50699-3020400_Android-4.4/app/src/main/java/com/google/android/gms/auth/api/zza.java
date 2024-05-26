package com.google.android.gms.auth.api;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzna;

/* loaded from: classes.dex */
public final class zza {
    public static final Api.zzf<zzna> cH = new Api.zzf<>();
    private static final Api.zza<zzna, zzb> cI = new Api.zza<zzna, zzb>() { // from class: com.google.android.gms.auth.api.zza.1
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* bridge */ /* synthetic */ zzna zza(Context context, Looper looper, zzg zzgVar, zzb zzbVar, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzna(context, looper, zzgVar, zzbVar, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Api<zzb> API = new Api<>("Auth.PROXY_API", cI, cH);
}
