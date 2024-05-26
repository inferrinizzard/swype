package com.google.android.gms.internal;

import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzfe implements zzep {
    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        com.google.android.gms.ads.internal.zzu.zzgj();
        if (map.containsKey("abort")) {
            if (zzfc.zzd(zzlhVar)) {
                return;
            }
            zzkd.zzcx("Precache abort but no preload task running.");
            return;
        }
        String str = map.get("src");
        if (str == null) {
            zzkd.zzcx("Precache video action is missing the src parameter.");
            return;
        }
        try {
            Integer.parseInt(map.get("player"));
        } catch (NumberFormatException e) {
        }
        if (map.containsKey("mimetype")) {
            map.get("mimetype");
        }
        if (zzfc.zzf(zzlhVar) != null) {
            zzkd.zzcx("Precache task already running.");
        } else {
            com.google.android.gms.common.internal.zzb.zzu(zzlhVar.zzug());
            new zzfb(zzlhVar, zzlhVar.zzug().zzakj.zza$630c6172(zzlhVar), str).zzpy();
        }
    }
}
