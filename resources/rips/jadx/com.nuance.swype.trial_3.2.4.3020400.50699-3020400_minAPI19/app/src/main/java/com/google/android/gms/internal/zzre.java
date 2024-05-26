package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

/* loaded from: classes.dex */
public final class zzre {
    public static final Api.zzf<zzri> bJ = new Api.zzf<>();
    private static final Api.zza<zzri, Object> bK = new Api.zza<zzri, Object>() { // from class: com.google.android.gms.internal.zzre.1
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* synthetic */ zzri zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, Object obj, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzri(context, looper, zzgVar, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Api<Object> API = new Api<>("Common.API", bK, bJ);
    public static final zzrf zt = new zzrg();
}
