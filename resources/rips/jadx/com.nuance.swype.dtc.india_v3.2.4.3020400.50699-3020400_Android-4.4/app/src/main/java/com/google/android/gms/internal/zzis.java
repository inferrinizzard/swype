package com.google.android.gms.internal;

import android.net.Uri;
import android.text.TextUtils;
import com.facebook.internal.AnalyticsEvents;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.AutoClickProtectionConfigurationParcel;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzis {
    String zzbfi;
    final AdRequestInfoParcel zzbot;
    List<String> zzbzf;
    String zzcet;
    String zzceu;
    List<String> zzcev;
    String zzcew;
    String zzcex;
    List<String> zzcey;
    RewardItemParcel zzcfj;
    List<String> zzcfk;
    List<String> zzcfl;
    AutoClickProtectionConfigurationParcel zzcfn;
    String zzcfp;
    List<String> zzcfq;
    String zzcfr;
    boolean zzcfs;
    String zzcft;
    long zzcez = -1;
    boolean zzcfa = false;
    private final long zzcfb = -1;
    long zzcfc = -1;
    int mOrientation = -1;
    boolean zzcfd = false;
    boolean zzcfe = false;
    boolean zzcff = false;
    boolean zzcfg = true;
    String zzcfh = "";
    boolean zzcfi = false;
    boolean zzawn = false;
    boolean zzcfm = false;
    boolean zzcfo = false;

    public zzis(AdRequestInfoParcel adRequestInfoParcel) {
        this.zzbot = adRequestInfoParcel;
    }

    private static String zzd(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private static long zze(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        if (list != null && !list.isEmpty()) {
            String str2 = list.get(0);
            try {
                return Float.parseFloat(str2) * 1000.0f;
            } catch (NumberFormatException e) {
                zzkd.zzcx(new StringBuilder(String.valueOf(str).length() + 36 + String.valueOf(str2).length()).append("Could not parse float from ").append(str).append(" header: ").append(str2).toString());
            }
        }
        return -1L;
    }

    private static List<String> zzf(Map<String, List<String>> map, String str) {
        String str2;
        List<String> list = map.get(str);
        if (list == null || list.isEmpty() || (str2 = list.get(0)) == null) {
            return null;
        }
        return Arrays.asList(str2.trim().split("\\s+"));
    }

    private static boolean zzg(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        return (list == null || list.isEmpty() || !Boolean.valueOf(list.get(0)).booleanValue()) ? false : true;
    }

    public final void zzj(Map<String, List<String>> map) {
        this.zzcet = zzd(map, "X-Afma-Ad-Size");
        this.zzcft = zzd(map, "X-Afma-Ad-Slot-Size");
        List<String> zzf = zzf(map, "X-Afma-Click-Tracking-Urls");
        if (zzf != null) {
            this.zzcev = zzf;
        }
        List<String> list = map.get("X-Afma-Debug-Dialog");
        if (list != null && !list.isEmpty()) {
            this.zzcew = list.get(0);
        }
        List<String> zzf2 = zzf(map, "X-Afma-Tracking-Urls");
        if (zzf2 != null) {
            this.zzcey = zzf2;
        }
        long zze = zze(map, "X-Afma-Interstitial-Timeout");
        if (zze != -1) {
            this.zzcez = zze;
        }
        this.zzcfa |= zzg(map, "X-Afma-Mediation");
        List<String> zzf3 = zzf(map, "X-Afma-Manual-Tracking-Urls");
        if (zzf3 != null) {
            this.zzbzf = zzf3;
        }
        long zze2 = zze(map, "X-Afma-Refresh-Rate");
        if (zze2 != -1) {
            this.zzcfc = zze2;
        }
        List<String> list2 = map.get("X-Afma-Orientation");
        if (list2 != null && !list2.isEmpty()) {
            String str = list2.get(0);
            if ("portrait".equalsIgnoreCase(str)) {
                this.mOrientation = com.google.android.gms.ads.internal.zzu.zzfs().zztk();
            } else if ("landscape".equalsIgnoreCase(str)) {
                this.mOrientation = com.google.android.gms.ads.internal.zzu.zzfs().zztj();
            }
        }
        this.zzcex = zzd(map, "X-Afma-ActiveView");
        List<String> list3 = map.get("X-Afma-Use-HTTPS");
        if (list3 != null && !list3.isEmpty()) {
            this.zzcff = Boolean.valueOf(list3.get(0)).booleanValue();
        }
        this.zzcfd |= zzg(map, "X-Afma-Custom-Rendering-Allowed");
        this.zzcfe = AnalyticsEvents.PARAMETER_SHARE_DIALOG_SHOW_NATIVE.equals(zzd(map, "X-Afma-Ad-Format"));
        List<String> list4 = map.get("X-Afma-Content-Url-Opted-Out");
        if (list4 != null && !list4.isEmpty()) {
            this.zzcfg = Boolean.valueOf(list4.get(0)).booleanValue();
        }
        List<String> list5 = map.get("X-Afma-Gws-Query-Id");
        if (list5 != null && !list5.isEmpty()) {
            this.zzcfh = list5.get(0);
        }
        String zzd = zzd(map, "X-Afma-Fluid");
        if (zzd != null && zzd.equals("height")) {
            this.zzcfi = true;
        }
        this.zzawn = "native_express".equals(zzd(map, "X-Afma-Ad-Format"));
        this.zzcfj = RewardItemParcel.zzch(zzd(map, "X-Afma-Rewards"));
        if (this.zzcfk == null) {
            this.zzcfk = zzf(map, "X-Afma-Reward-Video-Start-Urls");
        }
        if (this.zzcfl == null) {
            this.zzcfl = zzf(map, "X-Afma-Reward-Video-Complete-Urls");
        }
        this.zzcfm |= zzg(map, "X-Afma-Use-Displayed-Impression");
        this.zzcfo |= zzg(map, "X-Afma-Auto-Collect-Location");
        this.zzcfp = zzd(map, "Set-Cookie");
        String zzd2 = zzd(map, "X-Afma-Auto-Protection-Configuration");
        if (zzd2 == null || TextUtils.isEmpty(zzd2)) {
            Uri.Builder buildUpon = Uri.parse("https://pagead2.googlesyndication.com/pagead/gen_204").buildUpon();
            buildUpon.appendQueryParameter("id", "gmob-apps-blocked-navigation");
            if (!TextUtils.isEmpty(this.zzcew)) {
                buildUpon.appendQueryParameter("debugDialog", this.zzcew);
            }
            boolean booleanValue = ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzayg)).booleanValue();
            String valueOf = String.valueOf(buildUpon.toString());
            String valueOf2 = String.valueOf("navigationURL");
            this.zzcfn = new AutoClickProtectionConfigurationParcel(booleanValue, Arrays.asList(new StringBuilder(String.valueOf(valueOf).length() + 18 + String.valueOf(valueOf2).length()).append(valueOf).append("&").append(valueOf2).append("={NAVIGATION_URL}").toString()));
        } else {
            try {
                this.zzcfn = AutoClickProtectionConfigurationParcel.zzh(new JSONObject(zzd2));
            } catch (JSONException e) {
                zzkd.zzd("Error parsing configuration JSON", e);
                this.zzcfn = new AutoClickProtectionConfigurationParcel();
            }
        }
        List<String> zzf4 = zzf(map, "X-Afma-Remote-Ping-Urls");
        if (zzf4 != null) {
            this.zzcfq = zzf4;
        }
        this.zzcfr = zzd(map, "X-Afma-Safe-Browsing");
        this.zzcfs |= zzg(map, "X-Afma-Render-In-Browser");
    }
}
