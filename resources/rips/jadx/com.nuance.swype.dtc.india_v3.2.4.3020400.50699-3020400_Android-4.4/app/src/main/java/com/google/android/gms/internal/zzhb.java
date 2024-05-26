package com.google.android.gms.internal;

import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzhb {
    final zzlh zzbgf;
    final boolean zzbqs;
    final String zzbqt;

    public zzhb(zzlh zzlhVar, Map<String, String> map) {
        this.zzbgf = zzlhVar;
        this.zzbqt = map.get("forceOrientation");
        if (map.containsKey("allowOrientationChange")) {
            this.zzbqs = Boolean.parseBoolean(map.get("allowOrientationChange"));
        } else {
            this.zzbqs = true;
        }
    }
}
