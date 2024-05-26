package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

/* loaded from: classes.dex */
public final class zzvt {
    public static final Api.zzf<com.google.android.gms.signin.internal.zzg> bJ = new Api.zzf<>();
    public static final Api.zzf<com.google.android.gms.signin.internal.zzg> atP = new Api.zzf<>();
    public static final Api.zza<com.google.android.gms.signin.internal.zzg, zzvv> bK = new Api.zza<com.google.android.gms.signin.internal.zzg, zzvv>() { // from class: com.google.android.gms.internal.zzvt.1
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* bridge */ /* synthetic */ com.google.android.gms.signin.internal.zzg zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, zzvv zzvvVar, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            if (zzvvVar == null) {
                zzvv zzvvVar2 = zzvv.atR;
            }
            return new com.google.android.gms.signin.internal.zzg(context, looper, zzgVar, connectionCallbacks, onConnectionFailedListener);
        }
    };
    static final Api.zza<com.google.android.gms.signin.internal.zzg, Object> atQ = new Api.zza<com.google.android.gms.signin.internal.zzg, Object>() { // from class: com.google.android.gms.internal.zzvt.2
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* bridge */ /* synthetic */ com.google.android.gms.signin.internal.zzg zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, Object obj, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new com.google.android.gms.signin.internal.zzg(context, looper, false, zzgVar, null, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Scope dK = new Scope("profile");
    public static final Scope dL = new Scope("email");
    public static final Api<zzvv> API = new Api<>("SignIn.API", bK, bJ);
    public static final Api<Object> Dz = new Api<>("SignIn.INTERNAL_API", atQ, atP);
}
