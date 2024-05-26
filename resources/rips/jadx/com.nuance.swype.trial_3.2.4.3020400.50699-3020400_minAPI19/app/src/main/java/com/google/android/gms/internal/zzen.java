package com.google.android.gms.internal;

import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzen implements zzep {
    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        String str = map.get(NativeProtocol.WEB_DIALOG_ACTION);
        if ("tick".equals(str)) {
            String str2 = map.get("label");
            String str3 = map.get("start_label");
            String str4 = map.get("timestamp");
            if (TextUtils.isEmpty(str2)) {
                zzkd.zzcx("No label given for CSI tick.");
                return;
            }
            if (TextUtils.isEmpty(str4)) {
                zzkd.zzcx("No timestamp given for CSI tick.");
                return;
            }
            try {
                long parseLong = (Long.parseLong(str4) - com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis()) + com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime();
                if (TextUtils.isEmpty(str3)) {
                    str3 = "native:view_load";
                }
                zzdj zzus = zzlhVar.zzus();
                zzdk zzdkVar = zzus.zzajn;
                zzdi zzdiVar = zzus.zzbee.get(str3);
                String[] strArr = {str2};
                if (zzdkVar != null && zzdiVar != null) {
                    zzdkVar.zza(zzdiVar, parseLong, strArr);
                }
                Map<String, zzdi> map2 = zzus.zzbee;
                zzdk zzdkVar2 = zzus.zzajn;
                map2.put(str2, zzdkVar2 == null ? null : zzdkVar2.zzc(parseLong));
                return;
            } catch (NumberFormatException e) {
                zzkd.zzd("Malformed timestamp for CSI tick.", e);
                return;
            }
        }
        if ("experiment".equals(str)) {
            String str5 = map.get("value");
            if (TextUtils.isEmpty(str5)) {
                zzkd.zzcx("No value given for CSI experiment.");
                return;
            }
            zzdk zzdkVar3 = zzlhVar.zzus().zzajn;
            if (zzdkVar3 == null) {
                zzkd.zzcx("No ticker for WebView, dropping experiment ID.");
                return;
            } else {
                zzdkVar3.zzh("e", str5);
                return;
            }
        }
        if ("extra".equals(str)) {
            String str6 = map.get("name");
            String str7 = map.get("value");
            if (TextUtils.isEmpty(str7)) {
                zzkd.zzcx("No value given for CSI extra.");
                return;
            }
            if (TextUtils.isEmpty(str6)) {
                zzkd.zzcx("No name given for CSI extra.");
                return;
            }
            zzdk zzdkVar4 = zzlhVar.zzus().zzajn;
            if (zzdkVar4 == null) {
                zzkd.zzcx("No ticker for WebView, dropping extra parameter.");
            } else {
                zzdkVar4.zzh(str6, str7);
            }
        }
    }
}
