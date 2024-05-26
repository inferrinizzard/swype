package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpm;

/* loaded from: classes.dex */
abstract class zzrh<R extends Result> extends zzpm.zza<R, zzri> {

    /* loaded from: classes.dex */
    static abstract class zza extends zzrh<Status> {
        public zza(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        @Override // com.google.android.gms.internal.zzpo
        public final /* synthetic */ Result zzc(Status status) {
            return status;
        }
    }

    public zzrh(GoogleApiClient googleApiClient) {
        super(zzre.API, googleApiClient);
    }
}
