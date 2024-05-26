package com.google.android.gms.internal;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzgf {
    public static List<String> zza(JSONObject jSONObject, String str) throws JSONException {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        if (optJSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(optJSONArray.length());
        for (int i = 0; i < optJSONArray.length(); i++) {
            arrayList.add(optJSONArray.getString(i));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static void zza(Context context, String str, zzju zzjuVar, String str2, boolean z, List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String str3 = z ? "1" : "0";
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String replaceAll = it.next().replaceAll("@gw_adlocid@", str2).replaceAll("@gw_adnetrefresh@", str3).replaceAll("@gw_qdata@", zzjuVar.zzcig.zzbnr).replaceAll("@gw_sdkver@", str).replaceAll("@gw_sessid@", com.google.android.gms.ads.internal.zzu.zzft().zzcjm).replaceAll("@gw_seqnum@", zzjuVar.zzcau).replaceAll("@gw_adnetstatus@", zzjuVar.zzcih);
            if (zzjuVar.zzbon != null) {
                replaceAll = replaceAll.replaceAll("@gw_adnetid@", zzjuVar.zzbon.zzbmv).replaceAll("@gw_allocid@", zzjuVar.zzbon.zzbmx);
            }
            new zzkq(context, str, replaceAll).zzpy();
        }
    }
}
