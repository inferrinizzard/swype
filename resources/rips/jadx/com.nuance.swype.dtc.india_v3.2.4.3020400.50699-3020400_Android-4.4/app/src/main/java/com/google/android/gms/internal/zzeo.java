package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import com.facebook.share.internal.ShareConstants;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzeo {
    public static final zzep zzbhn = new zzep() { // from class: com.google.android.gms.internal.zzeo.1
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
        }
    };
    public static final zzep zzbho = new zzep() { // from class: com.google.android.gms.internal.zzeo.5
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            String str = map.get("urls");
            if (TextUtils.isEmpty(str)) {
                zzkd.zzcx("URLs missing in canOpenURLs GMSG.");
                return;
            }
            String[] split = str.split(",");
            HashMap hashMap = new HashMap();
            PackageManager packageManager = zzlhVar.getContext().getPackageManager();
            for (String str2 : split) {
                String[] split2 = str2.split(";", 2);
                hashMap.put(str2, Boolean.valueOf(packageManager.resolveActivity(new Intent(split2.length > 1 ? split2[1].trim() : "android.intent.action.VIEW", Uri.parse(split2[0].trim())), 65536) != null));
            }
            zzlhVar.zza("openableURLs", hashMap);
        }
    };
    public static final zzep zzbhp = new zzep() { // from class: com.google.android.gms.internal.zzeo.6
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            PackageManager packageManager = zzlhVar.getContext().getPackageManager();
            try {
                try {
                    JSONArray jSONArray = new JSONObject(map.get(ShareConstants.WEB_DIALOG_PARAM_DATA)).getJSONArray("intents");
                    JSONObject jSONObject = new JSONObject();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        try {
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String optString = jSONObject2.optString("id");
                            String optString2 = jSONObject2.optString("u");
                            String optString3 = jSONObject2.optString("i");
                            String optString4 = jSONObject2.optString("m");
                            String optString5 = jSONObject2.optString("p");
                            String optString6 = jSONObject2.optString("c");
                            jSONObject2.optString("f");
                            jSONObject2.optString("e");
                            Intent intent = new Intent();
                            if (!TextUtils.isEmpty(optString2)) {
                                intent.setData(Uri.parse(optString2));
                            }
                            if (!TextUtils.isEmpty(optString3)) {
                                intent.setAction(optString3);
                            }
                            if (!TextUtils.isEmpty(optString4)) {
                                intent.setType(optString4);
                            }
                            if (!TextUtils.isEmpty(optString5)) {
                                intent.setPackage(optString5);
                            }
                            if (!TextUtils.isEmpty(optString6)) {
                                String[] split = optString6.split("/", 2);
                                if (split.length == 2) {
                                    intent.setComponent(new ComponentName(split[0], split[1]));
                                }
                            }
                            try {
                                jSONObject.put(optString, packageManager.resolveActivity(intent, 65536) != null);
                            } catch (JSONException e) {
                                zzkd.zzb("Error constructing openable urls response.", e);
                            }
                        } catch (JSONException e2) {
                            zzkd.zzb("Error parsing the intent data.", e2);
                        }
                    }
                    zzlhVar.zzb("openableIntents", jSONObject);
                } catch (JSONException e3) {
                    zzlhVar.zzb("openableIntents", new JSONObject());
                }
            } catch (JSONException e4) {
                zzlhVar.zzb("openableIntents", new JSONObject());
            }
        }
    };
    public static final zzep zzbhq = new zzep() { // from class: com.google.android.gms.internal.zzeo.7
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            Uri uri;
            zzas zzul;
            String str = map.get("u");
            if (str == null) {
                zzkd.zzcx("URL missing from click GMSG.");
                return;
            }
            Uri parse = Uri.parse(str);
            try {
                zzul = zzlhVar.zzul();
            } catch (zzat e) {
                String valueOf = String.valueOf(str);
                zzkd.zzcx(valueOf.length() != 0 ? "Unable to append parameter to URL: ".concat(valueOf) : new String("Unable to append parameter to URL: "));
            }
            if (zzul != null && zzul.zzc(parse)) {
                uri = zzul.zzb(parse, zzlhVar.getContext());
                new zzkq(zzlhVar.getContext(), zzlhVar.zzum().zzcs, uri.toString()).zzpy();
            }
            uri = parse;
            new zzkq(zzlhVar.getContext(), zzlhVar.zzum().zzcs, uri.toString()).zzpy();
        }
    };
    public static final zzep zzbhr = new zzep() { // from class: com.google.android.gms.internal.zzeo.8
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            com.google.android.gms.ads.internal.overlay.zzd zzuh = zzlhVar.zzuh();
            if (zzuh != null) {
                zzuh.close();
                return;
            }
            com.google.android.gms.ads.internal.overlay.zzd zzui = zzlhVar.zzui();
            if (zzui != null) {
                zzui.close();
            } else {
                zzkd.zzcx("A GMSG tried to close something that wasn't an overlay.");
            }
        }
    };
    public static final zzep zzbhs = new zzep() { // from class: com.google.android.gms.internal.zzeo.9
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            com.google.android.gms.ads.internal.overlay.zzm zzmVar;
            if (!"checkSupport".equals(map.get(NativeProtocol.WEB_DIALOG_ACTION))) {
                com.google.android.gms.ads.internal.overlay.zzd zzuh = zzlhVar.zzuh();
                if (zzuh != null) {
                    zzuh.zzf(zzlhVar, map);
                    return;
                }
                return;
            }
            zzkd.zzcw("Received support message, responding.");
            boolean z = false;
            com.google.android.gms.ads.internal.zzd zzug = zzlhVar.zzug();
            if (zzug != null && (zzmVar = zzug.zzakl) != null) {
                z = zzmVar.zzr(zzlhVar.getContext());
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("event", "checkSupport");
                jSONObject.put("supports", z);
                zzlhVar.zzb("appStreaming", jSONObject);
            } catch (Throwable th) {
            }
        }
    };
    public static final zzep zzbht = new zzep() { // from class: com.google.android.gms.internal.zzeo.10
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            zzlhVar.zzai("1".equals(map.get("custom_close")));
        }
    };
    public static final zzep zzbhu = new zzep() { // from class: com.google.android.gms.internal.zzeo.11
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            String str = map.get("u");
            if (str == null) {
                zzkd.zzcx("URL missing from httpTrack GMSG.");
            } else {
                new zzkq(zzlhVar.getContext(), zzlhVar.zzum().zzcs, str).zzpy();
            }
        }
    };
    public static final zzep zzbhv = new zzep() { // from class: com.google.android.gms.internal.zzeo.12
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            String valueOf = String.valueOf(map.get("string"));
            zzkd.zzcw(valueOf.length() != 0 ? "Received log message: ".concat(valueOf) : new String("Received log message: "));
        }
    };
    public static final zzep zzbhw = new zzep() { // from class: com.google.android.gms.internal.zzeo.2
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            String str = map.get("tx");
            String str2 = map.get("ty");
            String str3 = map.get("td");
            try {
                int parseInt = Integer.parseInt(str);
                int parseInt2 = Integer.parseInt(str2);
                int parseInt3 = Integer.parseInt(str3);
                zzas zzul = zzlhVar.zzul();
                if (zzul != null) {
                    zzul.zzafz.zza(parseInt, parseInt2, parseInt3);
                }
            } catch (NumberFormatException e) {
                zzkd.zzcx("Could not parse touch parameters from gmsg.");
            }
        }
    };
    public static final zzep zzbhx = new zzep() { // from class: com.google.android.gms.internal.zzeo.3
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbba)).booleanValue()) {
                zzlhVar.zzaj(!Boolean.parseBoolean(map.get("disabled")));
            }
        }
    };
    public static final zzep zzbhy = new zzep() { // from class: com.google.android.gms.internal.zzeo.4
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            String str = map.get(NativeProtocol.WEB_DIALOG_ACTION);
            if ("pause".equals(str)) {
                zzlhVar.zzef();
            } else if ("resume".equals(str)) {
                zzlhVar.zzeg();
            }
        }
    };
    public static final zzep zzbhz = new zzez();
    public static final zzep zzbia = new zzfa();
    public static final zzep zzbib = new zzfe();
    public static final zzep zzbic = new zzen();
    public static final zzex zzbid = new zzex();
}
