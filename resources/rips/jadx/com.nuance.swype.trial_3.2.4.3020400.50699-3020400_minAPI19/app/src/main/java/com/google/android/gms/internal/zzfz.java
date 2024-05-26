package com.google.android.gms.internal;

import com.facebook.share.internal.ShareConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzfz {
    public final String zzbmu;
    public final String zzbmv;
    public final List<String> zzbmw;
    public final String zzbmx;
    public final String zzbmy;
    public final List<String> zzbmz;
    public final List<String> zzbna;
    public final List<String> zzbnb;
    public final String zzbnc;
    public final List<String> zzbnd;
    public final List<String> zzbne;
    public final String zzbnf;
    public final String zzbng;
    public final String zzbnh;
    public final List<String> zzbni;
    public final String zzbnj;

    public zzfz(String str, List<String> list, List<String> list2, List<String> list3, String str2, List<String> list4, List<String> list5, List<String> list6) {
        this.zzbmu = str;
        this.zzbmv = null;
        this.zzbmw = list;
        this.zzbmx = null;
        this.zzbmy = null;
        this.zzbmz = list2;
        this.zzbna = list3;
        this.zzbnc = str2;
        this.zzbnd = list4;
        this.zzbne = list5;
        this.zzbnf = null;
        this.zzbng = null;
        this.zzbnh = null;
        this.zzbni = null;
        this.zzbnj = null;
        this.zzbnb = list6;
    }

    public zzfz(JSONObject jSONObject) throws JSONException {
        this.zzbmv = jSONObject.getString("id");
        JSONArray jSONArray = jSONObject.getJSONArray("adapters");
        ArrayList arrayList = new ArrayList(jSONArray.length());
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(jSONArray.getString(i));
        }
        this.zzbmw = Collections.unmodifiableList(arrayList);
        this.zzbmx = jSONObject.optString("allocation_id", null);
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbmz = zzgf.zza(jSONObject, "clickurl");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbna = zzgf.zza(jSONObject, "imp_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbnb = zzgf.zza(jSONObject, "fill_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbnd = zzgf.zza(jSONObject, "video_start_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbne = zzgf.zza(jSONObject, "video_complete_urls");
        JSONObject optJSONObject = jSONObject.optJSONObject("ad");
        this.zzbmu = optJSONObject != null ? optJSONObject.toString() : null;
        JSONObject optJSONObject2 = jSONObject.optJSONObject(ShareConstants.WEB_DIALOG_PARAM_DATA);
        this.zzbnc = optJSONObject2 != null ? optJSONObject2.toString() : null;
        this.zzbmy = optJSONObject2 != null ? optJSONObject2.optString("class_name") : null;
        this.zzbnf = jSONObject.optString("html_template", null);
        this.zzbng = jSONObject.optString("ad_base_url", null);
        JSONObject optJSONObject3 = jSONObject.optJSONObject("assets");
        this.zzbnh = optJSONObject3 != null ? optJSONObject3.toString() : null;
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbni = zzgf.zza(jSONObject, "template_ids");
        JSONObject optJSONObject4 = jSONObject.optJSONObject("ad_loader_options");
        this.zzbnj = optJSONObject4 != null ? optJSONObject4.toString() : null;
    }
}
