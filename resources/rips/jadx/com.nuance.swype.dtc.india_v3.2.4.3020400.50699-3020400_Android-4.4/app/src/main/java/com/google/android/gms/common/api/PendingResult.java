package com.google.android.gms.common.api;

import com.google.android.gms.common.api.Result;

/* loaded from: classes.dex */
public abstract class PendingResult<R extends Result> {

    /* loaded from: classes.dex */
    public interface zza {
        void zzv$e184e5d();
    }

    public abstract void setResultCallback(ResultCallback<? super R> resultCallback);

    public void zza(zza zzaVar) {
        throw new UnsupportedOperationException();
    }
}
