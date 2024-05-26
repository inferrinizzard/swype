package com.google.android.gms.internal;

import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzek implements zzep {
    private final zzel zzbhm;

    public zzek(zzel zzelVar) {
        this.zzbhm = zzelVar;
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        String str = map.get("name");
        if (str == null) {
            zzkd.zzcx("App event with no name parameter.");
        } else {
            this.zzbhm.onAppEvent(str, map.get("info"));
        }
    }
}
