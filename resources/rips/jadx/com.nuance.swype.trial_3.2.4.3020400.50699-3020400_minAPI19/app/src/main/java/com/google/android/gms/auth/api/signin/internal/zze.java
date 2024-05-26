package com.google.android.gms.auth.api.signin.internal;

/* loaded from: classes.dex */
public final class zze {
    static int ef = 31;
    public int eg = 1;

    public final zze zzba(boolean z) {
        this.eg = (z ? 1 : 0) + (this.eg * ef);
        return this;
    }

    public final zze zzq(Object obj) {
        this.eg = (obj == null ? 0 : obj.hashCode()) + (this.eg * ef);
        return this;
    }
}
