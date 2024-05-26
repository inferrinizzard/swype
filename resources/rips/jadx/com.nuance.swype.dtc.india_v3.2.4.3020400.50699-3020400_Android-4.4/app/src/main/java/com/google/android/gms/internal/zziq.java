package com.google.android.gms.internal;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import com.facebook.applinks.AppLinkData;
import com.facebook.internal.AnalyticsEvents;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.SearchAdRequestParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.nuance.sns.ScraperStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zziq {
    private static final SimpleDateFormat zzcel = new SimpleDateFormat("yyyyMMdd", Locale.US);

    /* JADX WARN: Removed duplicated region for block: B:51:0x014c A[Catch: JSONException -> 0x0248, TryCatch #0 {JSONException -> 0x0248, blocks: (B:2:0x0000, B:4:0x0034, B:7:0x003c, B:9:0x0048, B:11:0x0054, B:12:0x005e, B:14:0x0075, B:15:0x0085, B:17:0x009a, B:18:0x00a2, B:20:0x00a9, B:22:0x00af, B:24:0x00ca, B:27:0x00ea, B:31:0x00f8, B:32:0x00fc, B:36:0x010a, B:37:0x010e, B:41:0x011c, B:43:0x0122, B:45:0x0127, B:46:0x012b, B:48:0x0133, B:49:0x0135, B:51:0x014c, B:52:0x0156, B:55:0x0271, B:56:0x026d, B:57:0x0269, B:59:0x00d8, B:61:0x00e1), top: B:1:0x0000 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.google.android.gms.ads.internal.request.AdResponseParcel zza(android.content.Context r45, com.google.android.gms.ads.internal.request.AdRequestInfoParcel r46, java.lang.String r47) {
        /*
            Method dump skipped, instructions count: 641
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zziq.zza(android.content.Context, com.google.android.gms.ads.internal.request.AdRequestInfoParcel, java.lang.String):com.google.android.gms.ads.internal.request.AdResponseParcel");
    }

    private static List<String> zza(JSONArray jSONArray, List<String> list) throws JSONException {
        if (jSONArray == null) {
            return null;
        }
        if (list == null) {
            list = new LinkedList<>();
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            list.add(jSONArray.getString(i));
        }
        return list;
    }

    private static void zza(HashMap<String, Object> hashMap, Location location) {
        HashMap hashMap2 = new HashMap();
        Float valueOf = Float.valueOf(location.getAccuracy() * 1000.0f);
        Long valueOf2 = Long.valueOf(location.getTime() * 1000);
        Long valueOf3 = Long.valueOf((long) (location.getLatitude() * 1.0E7d));
        Long valueOf4 = Long.valueOf((long) (location.getLongitude() * 1.0E7d));
        hashMap2.put("radius", valueOf);
        hashMap2.put("lat", valueOf3);
        hashMap2.put("long", valueOf4);
        hashMap2.put("time", valueOf2);
        hashMap.put("uule", hashMap2);
    }

    private static Integer zzab(boolean z) {
        return Integer.valueOf(z ? 1 : 0);
    }

    private static String zzau(int i) {
        return String.format(Locale.US, "#%06x", Integer.valueOf(16777215 & i));
    }

    public static JSONObject zzc(AdResponseParcel adResponseParcel) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        if (adResponseParcel.zzbto != null) {
            jSONObject.put("ad_base_url", adResponseParcel.zzbto);
        }
        if (adResponseParcel.zzccb != null) {
            jSONObject.put("ad_size", adResponseParcel.zzccb);
        }
        jSONObject.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_SHOW_NATIVE, adResponseParcel.zzauu);
        if (adResponseParcel.zzauu) {
            jSONObject.put("ad_json", adResponseParcel.body);
        } else {
            jSONObject.put("ad_html", adResponseParcel.body);
        }
        if (adResponseParcel.zzccd != null) {
            jSONObject.put("debug_dialog", adResponseParcel.zzccd);
        }
        if (adResponseParcel.zzcbx != -1) {
            jSONObject.put("interstitial_timeout", adResponseParcel.zzcbx / 1000.0d);
        }
        if (adResponseParcel.orientation == com.google.android.gms.ads.internal.zzu.zzfs().zztk()) {
            jSONObject.put("orientation", "portrait");
        } else if (adResponseParcel.orientation == com.google.android.gms.ads.internal.zzu.zzfs().zztj()) {
            jSONObject.put("orientation", "landscape");
        }
        if (adResponseParcel.zzbnm != null) {
            jSONObject.put("click_urls", zzk(adResponseParcel.zzbnm));
        }
        if (adResponseParcel.zzbnn != null) {
            jSONObject.put("impression_urls", zzk(adResponseParcel.zzbnn));
        }
        if (adResponseParcel.zzcca != null) {
            jSONObject.put("manual_impression_urls", zzk(adResponseParcel.zzcca));
        }
        if (adResponseParcel.zzccg != null) {
            jSONObject.put("active_view", adResponseParcel.zzccg);
        }
        jSONObject.put("ad_is_javascript", adResponseParcel.zzcce);
        if (adResponseParcel.zzccf != null) {
            jSONObject.put("ad_passback_url", adResponseParcel.zzccf);
        }
        jSONObject.put("mediation", adResponseParcel.zzcby);
        jSONObject.put("custom_render_allowed", adResponseParcel.zzcch);
        jSONObject.put("content_url_opted_out", adResponseParcel.zzcci);
        jSONObject.put("prefetch", adResponseParcel.zzccj);
        if (adResponseParcel.zzbns != -1) {
            jSONObject.put("refresh_interval_milliseconds", adResponseParcel.zzbns);
        }
        if (adResponseParcel.zzcbz != -1) {
            jSONObject.put("mediation_config_cache_time_milliseconds", adResponseParcel.zzcbz);
        }
        if (!TextUtils.isEmpty(adResponseParcel.zzccm)) {
            jSONObject.put("gws_query_id", adResponseParcel.zzccm);
        }
        jSONObject.put("fluid", adResponseParcel.zzauv ? "height" : "");
        jSONObject.put("native_express", adResponseParcel.zzauw);
        if (adResponseParcel.zzcco != null) {
            jSONObject.put("video_start_urls", zzk(adResponseParcel.zzcco));
        }
        if (adResponseParcel.zzccp != null) {
            jSONObject.put("video_complete_urls", zzk(adResponseParcel.zzccp));
        }
        if (adResponseParcel.zzccn != null) {
            jSONObject.put("rewards", adResponseParcel.zzccn.zzrw());
        }
        jSONObject.put("use_displayed_impression", adResponseParcel.zzccq);
        jSONObject.put("auto_protection_configuration", adResponseParcel.zzccr);
        jSONObject.put("render_in_browser", adResponseParcel.zzbnq);
        return jSONObject;
    }

    private static JSONArray zzk(List<String> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next());
        }
        return jSONArray;
    }

    public static JSONObject zza$7edf9512$5c31bcc4(AdRequestInfoParcel adRequestInfoParcel, zziv zzivVar, Location location, String str, List<String> list, Bundle bundle) {
        String str2;
        String str3;
        String str4;
        try {
            HashMap hashMap = new HashMap();
            if (list.size() > 0) {
                hashMap.put("eid", TextUtils.join(",", list));
            }
            if (adRequestInfoParcel.zzcaq != null) {
                hashMap.put("ad_pos", adRequestInfoParcel.zzcaq);
            }
            AdRequestParcel adRequestParcel = adRequestInfoParcel.zzcar;
            String zzsy = zzkb.zzsy();
            if (zzsy != null) {
                hashMap.put("abf", zzsy);
            }
            if (adRequestParcel.zzatm != -1) {
                hashMap.put("cust_age", zzcel.format(new Date(adRequestParcel.zzatm)));
            }
            if (adRequestParcel.extras != null) {
                hashMap.put(AppLinkData.ARGUMENTS_EXTRAS_KEY, adRequestParcel.extras);
            }
            if (adRequestParcel.zzatn != -1) {
                hashMap.put("cust_gender", Integer.valueOf(adRequestParcel.zzatn));
            }
            if (adRequestParcel.zzato != null) {
                hashMap.put("kw", adRequestParcel.zzato);
            }
            if (adRequestParcel.zzatq != -1) {
                hashMap.put("tag_for_child_directed_treatment", Integer.valueOf(adRequestParcel.zzatq));
            }
            if (adRequestParcel.zzatp) {
                hashMap.put("adtest", "on");
            }
            if (adRequestParcel.versionCode >= 2) {
                if (adRequestParcel.zzatr) {
                    hashMap.put("d_imp_hdr", 1);
                }
                if (!TextUtils.isEmpty(adRequestParcel.zzats)) {
                    hashMap.put("ppid", adRequestParcel.zzats);
                }
                if (adRequestParcel.zzatt != null) {
                    SearchAdRequestParcel searchAdRequestParcel = adRequestParcel.zzatt;
                    if (Color.alpha(searchAdRequestParcel.zzawz) != 0) {
                        hashMap.put("acolor", zzau(searchAdRequestParcel.zzawz));
                    }
                    if (Color.alpha(searchAdRequestParcel.backgroundColor) != 0) {
                        hashMap.put("bgcolor", zzau(searchAdRequestParcel.backgroundColor));
                    }
                    if (Color.alpha(searchAdRequestParcel.zzaxa) != 0 && Color.alpha(searchAdRequestParcel.zzaxb) != 0) {
                        hashMap.put("gradientto", zzau(searchAdRequestParcel.zzaxa));
                        hashMap.put("gradientfrom", zzau(searchAdRequestParcel.zzaxb));
                    }
                    if (Color.alpha(searchAdRequestParcel.zzaxc) != 0) {
                        hashMap.put("bcolor", zzau(searchAdRequestParcel.zzaxc));
                    }
                    hashMap.put("bthick", Integer.toString(searchAdRequestParcel.zzaxd));
                    switch (searchAdRequestParcel.zzaxe) {
                        case 0:
                            str3 = ScraperStatus.SCRAPING_STATUS_NONE;
                            break;
                        case 1:
                            str3 = "dashed";
                            break;
                        case 2:
                            str3 = "dotted";
                            break;
                        case 3:
                            str3 = "solid";
                            break;
                        default:
                            str3 = null;
                            break;
                    }
                    if (str3 != null) {
                        hashMap.put("btype", str3);
                    }
                    switch (searchAdRequestParcel.zzaxf) {
                        case 0:
                            str4 = "light";
                            break;
                        case 1:
                            str4 = "medium";
                            break;
                        case 2:
                            str4 = "dark";
                            break;
                        default:
                            str4 = null;
                            break;
                    }
                    if (str4 != null) {
                        hashMap.put("callbuttoncolor", str4);
                    }
                    if (searchAdRequestParcel.zzaxg != null) {
                        hashMap.put("channel", searchAdRequestParcel.zzaxg);
                    }
                    if (Color.alpha(searchAdRequestParcel.zzaxh) != 0) {
                        hashMap.put("dcolor", zzau(searchAdRequestParcel.zzaxh));
                    }
                    if (searchAdRequestParcel.zzaxi != null) {
                        hashMap.put("font", searchAdRequestParcel.zzaxi);
                    }
                    if (Color.alpha(searchAdRequestParcel.zzaxj) != 0) {
                        hashMap.put("hcolor", zzau(searchAdRequestParcel.zzaxj));
                    }
                    hashMap.put("headersize", Integer.toString(searchAdRequestParcel.zzaxk));
                    if (searchAdRequestParcel.zzaxl != null) {
                        hashMap.put("q", searchAdRequestParcel.zzaxl);
                    }
                }
            }
            if (adRequestParcel.versionCode >= 3 && adRequestParcel.zzatv != null) {
                hashMap.put("url", adRequestParcel.zzatv);
            }
            if (adRequestParcel.versionCode >= 5) {
                if (adRequestParcel.zzatx != null) {
                    hashMap.put("custom_targeting", adRequestParcel.zzatx);
                }
                if (adRequestParcel.zzaty != null) {
                    hashMap.put("category_exclusions", adRequestParcel.zzaty);
                }
                if (adRequestParcel.zzatz != null) {
                    hashMap.put("request_agent", adRequestParcel.zzatz);
                }
            }
            if (adRequestParcel.versionCode >= 6 && adRequestParcel.zzaua != null) {
                hashMap.put("request_pkg", adRequestParcel.zzaua);
            }
            if (adRequestParcel.versionCode >= 7) {
                hashMap.put("is_designed_for_families", Boolean.valueOf(adRequestParcel.zzaub));
            }
            if (adRequestInfoParcel.zzapa.zzaut == null) {
                hashMap.put("format", adRequestInfoParcel.zzapa.zzaur);
                if (adRequestInfoParcel.zzapa.zzauv) {
                    hashMap.put("fluid", "height");
                }
            } else {
                boolean z = false;
                boolean z2 = false;
                for (AdSizeParcel adSizeParcel : adRequestInfoParcel.zzapa.zzaut) {
                    if (!adSizeParcel.zzauv && !z2) {
                        hashMap.put("format", adSizeParcel.zzaur);
                        z2 = true;
                    }
                    if (adSizeParcel.zzauv && !z) {
                        hashMap.put("fluid", "height");
                        z = true;
                    }
                    if (!z2 || !z) {
                    }
                }
            }
            if (adRequestInfoParcel.zzapa.width == -1) {
                hashMap.put("smart_w", "full");
            }
            if (adRequestInfoParcel.zzapa.height == -2) {
                hashMap.put("smart_h", "auto");
            }
            if (adRequestInfoParcel.zzapa.zzaut != null) {
                StringBuilder sb = new StringBuilder();
                boolean z3 = false;
                for (AdSizeParcel adSizeParcel2 : adRequestInfoParcel.zzapa.zzaut) {
                    if (adSizeParcel2.zzauv) {
                        z3 = true;
                    } else {
                        if (sb.length() != 0) {
                            sb.append("|");
                        }
                        sb.append(adSizeParcel2.width == -1 ? (int) (adSizeParcel2.widthPixels / zzivVar.zzcbd) : adSizeParcel2.width);
                        sb.append("x");
                        sb.append(adSizeParcel2.height == -2 ? (int) (adSizeParcel2.heightPixels / zzivVar.zzcbd) : adSizeParcel2.height);
                    }
                }
                if (z3) {
                    if (sb.length() != 0) {
                        sb.insert(0, "|");
                    }
                    sb.insert(0, "320x50");
                }
                hashMap.put("sz", sb);
            }
            if (adRequestInfoParcel.zzcax != 0) {
                hashMap.put("native_version", Integer.valueOf(adRequestInfoParcel.zzcax));
                if (!adRequestInfoParcel.zzapa.zzauw) {
                    hashMap.put("native_templates", adRequestInfoParcel.zzaps);
                    NativeAdOptionsParcel nativeAdOptionsParcel = adRequestInfoParcel.zzapo;
                    switch (nativeAdOptionsParcel != null ? nativeAdOptionsParcel.zzbgq : 0) {
                        case 1:
                            str2 = "portrait";
                            break;
                        case 2:
                            str2 = "landscape";
                            break;
                        default:
                            str2 = "any";
                            break;
                    }
                    hashMap.put("native_image_orientation", str2);
                    if (!adRequestInfoParcel.zzcbi.isEmpty()) {
                        hashMap.put("native_custom_templates", adRequestInfoParcel.zzcbi);
                    }
                }
            }
            hashMap.put("slotname", adRequestInfoParcel.zzaou);
            hashMap.put("pn", adRequestInfoParcel.applicationInfo.packageName);
            if (adRequestInfoParcel.zzcas != null) {
                hashMap.put("vc", Integer.valueOf(adRequestInfoParcel.zzcas.versionCode));
            }
            hashMap.put("ms", str);
            hashMap.put("seq_num", adRequestInfoParcel.zzcau);
            hashMap.put("session_id", adRequestInfoParcel.zzcav);
            hashMap.put("js", adRequestInfoParcel.zzaow.zzcs);
            Bundle bundle2 = adRequestInfoParcel.zzcbv;
            hashMap.put("am", Integer.valueOf(zzivVar.zzcgd));
            hashMap.put("cog", zzab(zzivVar.zzcge));
            hashMap.put("coh", zzab(zzivVar.zzcgf));
            if (!TextUtils.isEmpty(zzivVar.zzcgg)) {
                hashMap.put("carrier", zzivVar.zzcgg);
            }
            hashMap.put("gl", zzivVar.zzcgh);
            if (zzivVar.zzcgi) {
                hashMap.put("simulator", 1);
            }
            if (zzivVar.zzcgj) {
                hashMap.put("is_sidewinder", 1);
            }
            hashMap.put("ma", zzab(zzivVar.zzcgk));
            hashMap.put("sp", zzab(zzivVar.zzcgl));
            hashMap.put("hl", zzivVar.zzcgm);
            if (!TextUtils.isEmpty(zzivVar.zzcgn)) {
                hashMap.put("mv", zzivVar.zzcgn);
            }
            hashMap.put("muv", Integer.valueOf(zzivVar.zzcgo));
            if (zzivVar.zzcgp != -2) {
                hashMap.put("cnt", Integer.valueOf(zzivVar.zzcgp));
            }
            hashMap.put("gnt", Integer.valueOf(zzivVar.zzcgq));
            hashMap.put("pt", Integer.valueOf(zzivVar.zzcgr));
            hashMap.put("rm", Integer.valueOf(zzivVar.zzcgs));
            hashMap.put("riv", Integer.valueOf(zzivVar.zzcgt));
            Bundle bundle3 = new Bundle();
            bundle3.putString("build", zzivVar.zzcgy);
            Bundle bundle4 = new Bundle();
            bundle4.putBoolean("is_charging", zzivVar.zzcgv);
            bundle4.putDouble("battery_level", zzivVar.zzcgu);
            bundle3.putBundle("battery", bundle4);
            Bundle bundle5 = new Bundle();
            bundle5.putInt("active_network_state", zzivVar.zzcgx);
            bundle5.putBoolean("active_network_metered", zzivVar.zzcgw);
            bundle3.putBundle("network", bundle5);
            Bundle bundle6 = new Bundle();
            bundle6.putBoolean("is_browser_custom_tabs_capable", zzivVar.zzcgz);
            bundle3.putBundle("browser", bundle6);
            if (bundle2 != null) {
                Bundle bundle7 = new Bundle();
                bundle7.putString("runtime_free", Long.toString(bundle2.getLong("runtime_free_memory", -1L)));
                bundle7.putString("runtime_max", Long.toString(bundle2.getLong("runtime_max_memory", -1L)));
                bundle7.putString("runtime_total", Long.toString(bundle2.getLong("runtime_total_memory", -1L)));
                Debug.MemoryInfo memoryInfo = (Debug.MemoryInfo) bundle2.getParcelable("debug_memory_info");
                if (memoryInfo != null) {
                    bundle7.putString("debug_info_dalvik_private_dirty", Integer.toString(memoryInfo.dalvikPrivateDirty));
                    bundle7.putString("debug_info_dalvik_pss", Integer.toString(memoryInfo.dalvikPss));
                    bundle7.putString("debug_info_dalvik_shared_dirty", Integer.toString(memoryInfo.dalvikSharedDirty));
                    bundle7.putString("debug_info_native_private_dirty", Integer.toString(memoryInfo.nativePrivateDirty));
                    bundle7.putString("debug_info_native_pss", Integer.toString(memoryInfo.nativePss));
                    bundle7.putString("debug_info_native_shared_dirty", Integer.toString(memoryInfo.nativeSharedDirty));
                    bundle7.putString("debug_info_other_private_dirty", Integer.toString(memoryInfo.otherPrivateDirty));
                    bundle7.putString("debug_info_other_pss", Integer.toString(memoryInfo.otherPss));
                    bundle7.putString("debug_info_other_shared_dirty", Integer.toString(memoryInfo.otherSharedDirty));
                }
                bundle3.putBundle("android_mem_info", bundle7);
            }
            hashMap.put("device", bundle3);
            Bundle bundle8 = new Bundle();
            bundle8.putString("doritos", null);
            hashMap.put("pii", bundle8);
            hashMap.put("platform", Build.MANUFACTURER);
            hashMap.put("submodel", Build.MODEL);
            if (location != null) {
                zza((HashMap<String, Object>) hashMap, location);
            } else if (adRequestInfoParcel.zzcar.versionCode >= 2 && adRequestInfoParcel.zzcar.zzatu != null) {
                zza((HashMap<String, Object>) hashMap, adRequestInfoParcel.zzcar.zzatu);
            }
            if (adRequestInfoParcel.versionCode >= 2) {
                hashMap.put("quality_signals", adRequestInfoParcel.zzcaw);
            }
            if (adRequestInfoParcel.versionCode >= 4 && adRequestInfoParcel.zzcaz) {
                hashMap.put("forceHttps", Boolean.valueOf(adRequestInfoParcel.zzcaz));
            }
            if (bundle != null) {
                hashMap.put("content_info", bundle);
            }
            if (adRequestInfoParcel.versionCode >= 5) {
                hashMap.put("u_sd", Float.valueOf(adRequestInfoParcel.zzcbd));
                hashMap.put("sh", Integer.valueOf(adRequestInfoParcel.zzcbc));
                hashMap.put("sw", Integer.valueOf(adRequestInfoParcel.zzcbb));
            } else {
                hashMap.put("u_sd", Float.valueOf(zzivVar.zzcbd));
                hashMap.put("sh", Integer.valueOf(zzivVar.zzcbc));
                hashMap.put("sw", Integer.valueOf(zzivVar.zzcbb));
            }
            if (adRequestInfoParcel.versionCode >= 6) {
                if (!TextUtils.isEmpty(adRequestInfoParcel.zzcbe)) {
                    try {
                        hashMap.put("view_hierarchy", new JSONObject(adRequestInfoParcel.zzcbe));
                    } catch (JSONException e) {
                        zzkd.zzd("Problem serializing view hierarchy to JSON", e);
                    }
                }
                hashMap.put("correlation_id", Long.valueOf(adRequestInfoParcel.zzcbf));
            }
            if (adRequestInfoParcel.versionCode >= 7) {
                hashMap.put("request_id", adRequestInfoParcel.zzcbg);
            }
            if (adRequestInfoParcel.versionCode >= 11 && adRequestInfoParcel.zzcbk != null) {
                hashMap.put("capability", adRequestInfoParcel.zzcbk.toBundle());
            }
            if (adRequestInfoParcel.versionCode >= 12 && !TextUtils.isEmpty(adRequestInfoParcel.zzcbl)) {
                hashMap.put("anchor", adRequestInfoParcel.zzcbl);
            }
            if (adRequestInfoParcel.versionCode >= 13) {
                hashMap.put("android_app_volume", Float.valueOf(adRequestInfoParcel.zzcbm));
            }
            if (adRequestInfoParcel.versionCode >= 18) {
                hashMap.put("android_app_muted", Boolean.valueOf(adRequestInfoParcel.zzcbs));
            }
            if (adRequestInfoParcel.versionCode >= 14 && adRequestInfoParcel.zzcbn > 0) {
                hashMap.put("target_api", Integer.valueOf(adRequestInfoParcel.zzcbn));
            }
            if (adRequestInfoParcel.versionCode >= 15) {
                hashMap.put("scroll_index", Integer.valueOf(adRequestInfoParcel.zzcbo == -1 ? -1 : adRequestInfoParcel.zzcbo));
            }
            if (adRequestInfoParcel.versionCode >= 16) {
                hashMap.put("_activity_context", Boolean.valueOf(adRequestInfoParcel.zzcbp));
            }
            if (adRequestInfoParcel.versionCode >= 18) {
                if (!TextUtils.isEmpty(adRequestInfoParcel.zzcbt)) {
                    try {
                        hashMap.put("app_settings", new JSONObject(adRequestInfoParcel.zzcbt));
                    } catch (JSONException e2) {
                        zzkd.zzd("Problem creating json from app settings", e2);
                    }
                }
                hashMap.put("render_in_browser", Boolean.valueOf(adRequestInfoParcel.zzbnq));
            }
            if (adRequestInfoParcel.versionCode >= 18) {
                hashMap.put("android_num_video_cache_tasks", Integer.valueOf(adRequestInfoParcel.zzcbu));
            }
            if (zzkd.zzaz(2)) {
                String valueOf = String.valueOf(com.google.android.gms.ads.internal.zzu.zzfq().zzam(hashMap).toString(2));
                zzkd.v(valueOf.length() != 0 ? "Ad Request JSON: ".concat(valueOf) : new String("Ad Request JSON: "));
            }
            return com.google.android.gms.ads.internal.zzu.zzfq().zzam(hashMap);
        } catch (JSONException e3) {
            String valueOf2 = String.valueOf(e3.getMessage());
            zzkd.zzcx(valueOf2.length() != 0 ? "Problem serializing ad request to JSON: ".concat(valueOf2) : new String("Problem serializing ad request to JSON: "));
            return null;
        }
    }
}
