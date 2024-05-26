package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzpj;

/* loaded from: classes.dex */
public class zzb implements Result {
    private final Status bY;
    private final ArrayMap<zzpj<?>, ConnectionResult> rG = null;

    public zzb(Status status) {
        this.bY = status;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.bY;
    }
}
