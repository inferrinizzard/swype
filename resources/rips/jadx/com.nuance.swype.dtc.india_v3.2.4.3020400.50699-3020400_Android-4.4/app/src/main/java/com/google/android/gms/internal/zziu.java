package com.google.android.gms.internal;

import com.facebook.internal.ServerProtocol;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zziu {
    String zzae;
    final String zzbvq;
    int zzbyi;
    final List<String> zzcfu;
    private final List<String> zzcfv;
    private final String zzcfw;
    private final String zzcfx;
    final String zzcfy;
    final String zzcfz;
    final boolean zzcga;
    private final boolean zzcgb;
    final String zzcgc;

    public zziu(int i, Map<String, String> map) {
        this.zzae = map.get("url");
        this.zzcfx = map.get("base_uri");
        this.zzcfy = map.get("post_parameters");
        this.zzcga = parseBoolean(map.get("drt_include"));
        this.zzcgb = parseBoolean(map.get("pan_include"));
        this.zzcfw = map.get("activation_overlay_url");
        this.zzcfv = zzce(map.get("check_packages"));
        this.zzbvq = map.get("request_id");
        this.zzcfz = map.get("type");
        this.zzcfu = zzce(map.get("errors"));
        this.zzbyi = i;
        this.zzcgc = map.get("fetched_ad");
    }

    private static boolean parseBoolean(String str) {
        return str != null && (str.equals("1") || str.equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE));
    }

    private static List<String> zzce(String str) {
        if (str == null) {
            return null;
        }
        return Arrays.asList(str.split(","));
    }
}
