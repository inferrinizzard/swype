package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class zzpj<O extends Api.ApiOptions> {
    final Api<O> pN;
    private final O rP;

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzpj)) {
            return false;
        }
        zzpj zzpjVar = (zzpj) obj;
        return com.google.android.gms.common.internal.zzaa.equal(this.pN, zzpjVar.pN) && com.google.android.gms.common.internal.zzaa.equal(this.rP, zzpjVar.rP);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.pN, this.rP});
    }
}
