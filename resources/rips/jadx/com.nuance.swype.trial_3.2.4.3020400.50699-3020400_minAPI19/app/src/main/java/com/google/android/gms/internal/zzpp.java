package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

/* loaded from: classes.dex */
public final class zzpp implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final Api<?> pN;
    private final int tf;
    zzqa tg;

    public zzpp(Api<?> api, int i) {
        this.pN = api;
        this.tf = i;
    }

    private void zzapa() {
        com.google.android.gms.common.internal.zzab.zzb(this.tg, "Callbacks must be attached to a GoogleApiClient instance before connecting the client.");
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public final void onConnectionSuspended(int i) {
        zzapa();
        this.tg.onConnectionSuspended(i);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    public final void onConnected(Bundle bundle) {
        zzapa();
        zzqa zzqaVar = this.tg;
        zzqaVar.tr.lock();
        try {
            zzqaVar.uy.onConnected(bundle);
        } finally {
            zzqaVar.tr.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
    public final void onConnectionFailed(ConnectionResult connectionResult) {
        zzapa();
        zzqa zzqaVar = this.tg;
        Api<?> api = this.pN;
        int i = this.tf;
        zzqaVar.tr.lock();
        try {
            zzqaVar.uy.zza(connectionResult, api, i);
        } finally {
            zzqaVar.tr.unlock();
        }
    }
}
