package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzl;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class zzk<T extends IInterface> extends zzd<T> implements Api.zze, zzl.zza {
    private final Account aL;
    private final Set<Scope> dT;
    public final zzg tN;

    public zzk(Context context, Looper looper, int i, zzg zzgVar, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, zzm.zzce(context), GoogleApiAvailability.getInstance(), i, zzgVar, (GoogleApiClient.ConnectionCallbacks) zzab.zzy(connectionCallbacks), (GoogleApiClient.OnConnectionFailedListener) zzab.zzy(onConnectionFailedListener));
    }

    @Override // com.google.android.gms.common.internal.zzd
    public final Account getAccount() {
        return this.aL;
    }

    @Override // com.google.android.gms.common.internal.zzd
    protected final Set<Scope> zzasc() {
        return this.dT;
    }

    private zzk(Context context, Looper looper, zzm zzmVar, GoogleApiAvailability googleApiAvailability, int i, zzg zzgVar, final GoogleApiClient.ConnectionCallbacks connectionCallbacks, final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, zzmVar, googleApiAvailability, i, connectionCallbacks == null ? null : new zzd.zzb() { // from class: com.google.android.gms.common.internal.zzk.1
            @Override // com.google.android.gms.common.internal.zzd.zzb
            public final void onConnected(Bundle bundle) {
                GoogleApiClient.ConnectionCallbacks.this.onConnected(bundle);
            }

            @Override // com.google.android.gms.common.internal.zzd.zzb
            public final void onConnectionSuspended(int i2) {
                GoogleApiClient.ConnectionCallbacks.this.onConnectionSuspended(i2);
            }
        }, onConnectionFailedListener == null ? null : new zzd.zzc() { // from class: com.google.android.gms.common.internal.zzk.2
            @Override // com.google.android.gms.common.internal.zzd.zzc
            public final void onConnectionFailed(ConnectionResult connectionResult) {
                GoogleApiClient.OnConnectionFailedListener.this.onConnectionFailed(connectionResult);
            }
        }, zzgVar.sb);
        this.tN = zzgVar;
        this.aL = zzgVar.aL;
        Set<Scope> set = zzgVar.yj;
        Iterator<Scope> it = set.iterator();
        while (it.hasNext()) {
            if (!set.contains(it.next())) {
                throw new IllegalStateException("Expanding scopes is not permitted, use implied scopes instead");
            }
        }
        this.dT = set;
    }
}
