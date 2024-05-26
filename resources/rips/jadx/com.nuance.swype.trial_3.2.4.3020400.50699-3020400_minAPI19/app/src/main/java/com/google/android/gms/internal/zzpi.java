package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.util.SparseArray;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpm;

/* loaded from: classes.dex */
public abstract class zzpi {
    public final int iq;
    public final int sx;

    /* loaded from: classes.dex */
    public static final class zza extends zzpi {
        public final zzpm.zza<? extends Result, Api.zzb> sy;

        @Override // com.google.android.gms.internal.zzpi
        public final boolean cancel() {
            return this.sy.zzaov();
        }

        @Override // com.google.android.gms.internal.zzpi
        public final void zza(SparseArray<zzqy> sparseArray) {
            zzqy zzqyVar = sparseArray.get(this.sx);
            if (zzqyVar != null) {
                zzqyVar.zzg(this.sy);
            }
        }

        @Override // com.google.android.gms.internal.zzpi
        public final void zzb(Api.zzb zzbVar) throws DeadObjectException {
            this.sy.zzb(zzbVar);
        }

        @Override // com.google.android.gms.internal.zzpi
        public final void zzx(Status status) {
            this.sy.zzz(status);
        }
    }

    public boolean cancel() {
        return true;
    }

    public void zza(SparseArray<zzqy> sparseArray) {
    }

    public abstract void zzb(Api.zzb zzbVar) throws DeadObjectException;

    public abstract void zzx(Status status);
}
