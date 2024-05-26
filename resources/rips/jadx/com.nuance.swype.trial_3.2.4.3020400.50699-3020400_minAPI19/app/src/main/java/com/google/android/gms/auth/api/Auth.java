package com.google.android.gms.auth.api;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.internal.zze;
import com.google.android.gms.auth.api.credentials.internal.zzg;
import com.google.android.gms.auth.api.proxy.ProxyApi;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zzc;
import com.google.android.gms.auth.api.signin.internal.zzd;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.internal.zzmu;
import com.google.android.gms.internal.zzmv;
import com.google.android.gms.internal.zzmw;
import com.google.android.gms.internal.zzne;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class Auth {
    public static final Api.zzf<zzg> cy = new Api.zzf<>();
    public static final Api.zzf<zzmw> cz = new Api.zzf<>();
    public static final Api.zzf<zzd> cA = new Api.zzf<>();
    private static final Api.zza<zzg, AuthCredentialsOptions> cB = new Api.zza<zzg, AuthCredentialsOptions>() { // from class: com.google.android.gms.auth.api.Auth.1
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* bridge */ /* synthetic */ zzg zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, AuthCredentialsOptions authCredentialsOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzg(context, looper, zzgVar, authCredentialsOptions, connectionCallbacks, onConnectionFailedListener);
        }
    };
    private static final Api.zza<zzmw, Object> cC = new Api.zza<zzmw, Object>() { // from class: com.google.android.gms.auth.api.Auth.2
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* synthetic */ zzmw zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, Object obj, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzmw(context, looper, zzgVar, connectionCallbacks, onConnectionFailedListener);
        }
    };
    private static final Api.zza<zzd, GoogleSignInOptions> cD = new Api.zza<zzd, GoogleSignInOptions>() { // from class: com.google.android.gms.auth.api.Auth.3
        @Override // com.google.android.gms.common.api.Api.zza
        public final /* bridge */ /* synthetic */ zzd zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, GoogleSignInOptions googleSignInOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return new zzd(context, looper, zzgVar, googleSignInOptions, connectionCallbacks, onConnectionFailedListener);
        }

        @Override // com.google.android.gms.common.api.Api.zzd
        public final /* synthetic */ List zzp(Object obj) {
            GoogleSignInOptions googleSignInOptions = (GoogleSignInOptions) obj;
            return googleSignInOptions == null ? Collections.emptyList() : googleSignInOptions.zzafq();
        }
    };
    public static final Api<zzb> PROXY_API = zza.API;
    public static final Api<AuthCredentialsOptions> CREDENTIALS_API = new Api<>("Auth.CREDENTIALS_API", cB, cy);
    public static final Api<GoogleSignInOptions> GOOGLE_SIGN_IN_API = new Api<>("Auth.GOOGLE_SIGN_IN_API", cD, cA);
    public static final Api<Object> cE = new Api<>("Auth.ACCOUNT_STATUS_API", cC, cz);
    public static final ProxyApi ProxyApi = new zzne();
    public static final CredentialsApi CredentialsApi = new zze();
    public static final zzmu cF = new zzmv();
    public static final GoogleSignInApi GoogleSignInApi = new zzc();

    /* loaded from: classes.dex */
    public static final class AuthCredentialsOptions implements Api.ApiOptions.Optional {
    }
}
