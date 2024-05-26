package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzdj {
    final zzdk zzajn;
    final Map<String, zzdi> zzbee = new HashMap();

    public zzdj(zzdk zzdkVar) {
        this.zzajn = zzdkVar;
    }

    public final void zza(String str, zzdi zzdiVar) {
        this.zzbee.put(str, zzdiVar);
    }
}
