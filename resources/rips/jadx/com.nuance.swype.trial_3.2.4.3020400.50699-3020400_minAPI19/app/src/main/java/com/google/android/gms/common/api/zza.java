package com.google.android.gms.common.api;

import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.common.ConnectionResult;

/* loaded from: classes.dex */
public final class zza extends zzb {
    private final ConnectionResult rF;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zza(Status status) {
        super(status);
        SimpleArrayMap simpleArrayMap = null;
        this.rF = (ConnectionResult) simpleArrayMap.get(simpleArrayMap.keyAt(0));
    }
}
