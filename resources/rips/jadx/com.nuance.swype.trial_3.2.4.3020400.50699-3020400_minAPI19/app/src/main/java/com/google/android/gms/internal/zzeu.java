package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzeu implements zzep {
    public final HashMap<String, zzkv<JSONObject>> zzbis = new HashMap<>();

    public final void zzax(String str) {
        zzkv<JSONObject> zzkvVar = this.zzbis.get(str);
        if (zzkvVar == null) {
            zzkd.e("Could not find the ad request for the corresponding ad response.");
            return;
        }
        if (!zzkvVar.isDone()) {
            zzkvVar.cancel(true);
        }
        this.zzbis.remove(str);
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        String str = map.get("request_id");
        String str2 = map.get("fetched_ad");
        zzkd.zzcv("Received ad from the cache.");
        zzkv<JSONObject> zzkvVar = this.zzbis.get(str);
        if (zzkvVar == null) {
            zzkd.e("Could not find the ad request for the corresponding ad response.");
            return;
        }
        try {
            zzkvVar.zzh(new JSONObject(str2));
        } catch (JSONException e) {
            zzkd.zzb("Failed constructing JSON object from value passed from javascript", e);
            zzkvVar.zzh(null);
        } finally {
            this.zzbis.remove(str);
        }
    }
}
