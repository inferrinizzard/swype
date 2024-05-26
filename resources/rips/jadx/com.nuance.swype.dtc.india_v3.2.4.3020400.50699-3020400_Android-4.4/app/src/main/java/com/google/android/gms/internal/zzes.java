package com.google.android.gms.internal;

import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzes implements zzep {
    private final zzet zzbir;

    public zzes(zzet zzetVar) {
        this.zzbir = zzetVar;
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        float f;
        boolean equals = "1".equals(map.get("transparentBackground"));
        boolean equals2 = "1".equals(map.get("blur"));
        try {
        } catch (NumberFormatException e) {
            zzkd.zzb("Fail to parse float", e);
        }
        if (map.get("blurRadius") != null) {
            f = Float.parseFloat(map.get("blurRadius"));
            this.zzbir.zzg(equals);
            this.zzbir.zza(equals2, f);
        }
        f = 0.0f;
        this.zzbir.zzg(equals);
        this.zzbir.zza(equals2, f);
    }
}
