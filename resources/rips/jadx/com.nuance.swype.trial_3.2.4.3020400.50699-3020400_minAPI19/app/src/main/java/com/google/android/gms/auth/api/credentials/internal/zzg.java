package com.google.android.gms.auth.api.credentials.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.internal.zzk;
import com.google.android.gms.common.api.GoogleApiClient;

/* loaded from: classes.dex */
public final class zzg extends com.google.android.gms.common.internal.zzk<zzk> {

    /* renamed from: do, reason: not valid java name */
    final Auth.AuthCredentialsOptions f0do;

    public zzg(Context context, Looper looper, com.google.android.gms.common.internal.zzg zzgVar, Auth.AuthCredentialsOptions authCredentialsOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 68, zzgVar, connectionCallbacks, onConnectionFailedListener);
        this.f0do = authCredentialsOptions;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzqz() {
        return "com.google.android.gms.auth.api.credentials.service.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzra() {
        return "com.google.android.gms.auth.api.credentials.internal.ICredentialsService";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final Bundle zzaeu() {
        if (this.f0do == null) {
            return new Bundle();
        }
        Bundle bundle = new Bundle();
        bundle.putString("consumer_package", null);
        bundle.putParcelable("password_specification", null);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final /* synthetic */ IInterface zzbb(IBinder iBinder) {
        return zzk.zza.zzce(iBinder);
    }
}
