package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpm;
import com.google.android.gms.internal.zzrh;

/* loaded from: classes.dex */
public final class zzrg implements zzrf {

    /* loaded from: classes.dex */
    private static class zza extends zzrd {
        private final zzpm.zzb<Status> zv;

        public zza(zzpm.zzb<Status> zzbVar) {
            this.zv = zzbVar;
        }

        @Override // com.google.android.gms.internal.zzrd, com.google.android.gms.internal.zzrj
        public final void zzgn(int i) throws RemoteException {
            this.zv.setResult(new Status(i));
        }
    }

    @Override // com.google.android.gms.internal.zzrf
    public final PendingResult<Status> zzg(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzrh.zza(googleApiClient) { // from class: com.google.android.gms.internal.zzrg.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.internal.zzpm.zza
            public final /* bridge */ /* synthetic */ void zza(zzri zzriVar) throws RemoteException {
                ((zzrk) zzriVar.zzasa()).zza(new zza(this));
            }
        });
    }
}
