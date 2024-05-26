package com.google.android.gms.common.api;

import com.google.android.gms.internal.zzpo;

/* loaded from: classes.dex */
public final class PendingResults {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class zzc<R extends Result> extends zzpo<R> {
        public zzc(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.internal.zzpo
        public final R zzc(Status status) {
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }
}
