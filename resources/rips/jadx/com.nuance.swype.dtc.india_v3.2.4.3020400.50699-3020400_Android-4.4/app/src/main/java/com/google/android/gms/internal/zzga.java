package com.google.android.gms.internal;

import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import com.nuance.swype.input.UserPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzga {
    public final List<zzfz> zzbnk;
    public final long zzbnl;
    public final List<String> zzbnm;
    public final List<String> zzbnn;
    public final List<String> zzbno;
    public final List<String> zzbnp;
    public final boolean zzbnq;
    public final String zzbnr;
    public final long zzbns;
    public final String zzbnt;
    public final int zzbnu;
    public final int zzbnv;
    public final long zzbnw;
    public final boolean zzbnx;
    public int zzbny;
    public int zzbnz;

    public zzga(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (zzkd.zzaz(2)) {
            String valueOf = String.valueOf(jSONObject.toString(2));
            zzkd.v(valueOf.length() != 0 ? "Mediation Response JSON: ".concat(valueOf) : new String("Mediation Response JSON: "));
        }
        JSONArray jSONArray = jSONObject.getJSONArray("ad_networks");
        ArrayList arrayList = new ArrayList(jSONArray.length());
        int i = -1;
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            zzfz zzfzVar = new zzfz(jSONArray.getJSONObject(i2));
            arrayList.add(zzfzVar);
            if (i < 0 && zza(zzfzVar)) {
                i = i2;
            }
        }
        this.zzbny = i;
        this.zzbnz = jSONArray.length();
        this.zzbnk = Collections.unmodifiableList(arrayList);
        this.zzbnr = jSONObject.getString("qdata");
        this.zzbnv = jSONObject.optInt("fs_model_type", -1);
        this.zzbnw = jSONObject.optLong("timeout_ms", -1L);
        JSONObject optJSONObject = jSONObject.optJSONObject(UserPreferences.SETTINGS_CATEGORY);
        if (optJSONObject == null) {
            this.zzbnl = -1L;
            this.zzbnm = null;
            this.zzbnn = null;
            this.zzbno = null;
            this.zzbnp = null;
            this.zzbns = -1L;
            this.zzbnt = null;
            this.zzbnu = 0;
            this.zzbnx = false;
            this.zzbnq = false;
            return;
        }
        this.zzbnl = optJSONObject.optLong("ad_network_timeout_millis", -1L);
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbnm = zzgf.zza(optJSONObject, "click_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbnn = zzgf.zza(optJSONObject, "imp_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbno = zzgf.zza(optJSONObject, "nofill_urls");
        com.google.android.gms.ads.internal.zzu.zzgf();
        this.zzbnp = zzgf.zza(optJSONObject, "remote_ping_urls");
        this.zzbnq = optJSONObject.optBoolean("render_in_browser", false);
        long optLong = optJSONObject.optLong("refresh", -1L);
        this.zzbns = optLong > 0 ? optLong * 1000 : -1L;
        RewardItemParcel zza = RewardItemParcel.zza(optJSONObject.optJSONArray("rewards"));
        if (zza == null) {
            this.zzbnt = null;
            this.zzbnu = 0;
        } else {
            this.zzbnt = zza.type;
            this.zzbnu = zza.zzcid;
        }
        this.zzbnx = optJSONObject.optBoolean("use_displayed_impression", false);
    }

    public zzga(List<zzfz> list, List<String> list2, List<String> list3, List<String> list4, List<String> list5, String str) {
        this.zzbnk = list;
        this.zzbnl = -1L;
        this.zzbnm = list2;
        this.zzbnn = list3;
        this.zzbno = list4;
        this.zzbnp = list5;
        this.zzbnq = false;
        this.zzbnr = str;
        this.zzbns = -1L;
        this.zzbny = 0;
        this.zzbnz = 1;
        this.zzbnt = null;
        this.zzbnu = 0;
        this.zzbnv = -1;
        this.zzbnw = -1L;
        this.zzbnx = false;
    }

    private static boolean zza(zzfz zzfzVar) {
        Iterator<String> it = zzfzVar.zzbmw.iterator();
        while (it.hasNext()) {
            if (it.next().equals("com.google.ads.mediation.admob.AdMobAdapter")) {
                return true;
            }
        }
        return false;
    }
}
