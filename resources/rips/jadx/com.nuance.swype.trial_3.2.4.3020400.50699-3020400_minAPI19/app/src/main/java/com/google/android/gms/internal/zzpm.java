package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzqy;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class zzpm {

    /* loaded from: classes.dex */
    public static abstract class zza<R extends Result, A extends Api.zzb> extends zzpo<R> implements zzb<R> {
        final Api<?> pN;
        final Api.zzc<A> sJ;
        private AtomicReference<zzqy.zzb> sK;

        public zza(Api<?> api, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) com.google.android.gms.common.internal.zzab.zzb(googleApiClient, "GoogleApiClient must not be null"));
            this.sK = new AtomicReference<>();
            this.sJ = (Api.zzc<A>) api.zzans();
            this.pN = api;
        }

        private void zza(RemoteException remoteException) {
            zzz(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.android.gms.internal.zzpm.zzb
        public final /* synthetic */ void setResult(Object obj) {
            super.zzc((zza<R, A>) obj);
        }

        public abstract void zza(A a) throws RemoteException;

        public final void zza(zzqy.zzb zzbVar) {
            this.sK.set(zzbVar);
        }

        @Override // com.google.android.gms.internal.zzpo
        protected final void zzaos() {
            zzqy.zzb andSet = this.sK.getAndSet(null);
            if (andSet != null) {
                andSet.zzh(this);
            }
        }

        public final void zzb(A a) throws DeadObjectException {
            try {
                zza((zza<R, A>) a);
            } catch (DeadObjectException e) {
                zza(e);
                throw e;
            } catch (RemoteException e2) {
                zza(e2);
            }
        }

        public final void zzz(Status status) {
            com.google.android.gms.common.internal.zzab.zzb(!status.isSuccess(), "Failed result must not be success");
            zzc((zza<R, A>) zzc(status));
        }
    }

    /* loaded from: classes.dex */
    public interface zzb<R> {
        void setResult(R r);
    }
}
