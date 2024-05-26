package com.google.android.gms.ads.internal;

import android.content.Context;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzep;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzft;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzjw;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzla;
import com.google.android.gms.internal.zzlh;
import java.util.Map;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzg {
    private Context mContext;
    private final Object zzail = new Object();
    public final zzep zzaku = new zzep() { // from class: com.google.android.gms.ads.internal.zzg.1
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            zzlhVar.zzb("/appSettingsFetched", this);
            synchronized (zzg.this.zzail) {
                if (map != null) {
                    if (ServerProtocol.DIALOG_RETURN_SCOPES_TRUE.equalsIgnoreCase(map.get("isSuccessful"))) {
                        zzu.zzft().zzd(zzg.this.mContext, map.get("appSettingsJson"));
                    }
                }
            }
        }
    };

    public void zza(final Context context, VersionInfoParcel versionInfoParcel, final boolean z, zzjw zzjwVar, final String str, final String str2) {
        boolean z2;
        if (zzjwVar == null) {
            z2 = true;
        } else {
            z2 = (((zzu.zzfu().currentTimeMillis() - zzjwVar.zzcjc) > ((Long) zzu.zzfz().zzd(zzdc.zzbcv)).longValue() ? 1 : ((zzu.zzfu().currentTimeMillis() - zzjwVar.zzcjc) == ((Long) zzu.zzfz().zzd(zzdc.zzbcv)).longValue() ? 0 : -1)) > 0) || !zzjwVar.zzcjh;
        }
        if (z2) {
            if (context == null) {
                zzkd.zzcx("Context not provided to fetch application settings");
                return;
            }
            if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                zzkd.zzcx("App settings could not be fetched. Required parameters missing");
                return;
            }
            this.mContext = context;
            final zzfs zzc = zzu.zzfq().zzc(context, versionInfoParcel);
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzg.2
                @Override // java.lang.Runnable
                public final void run() {
                    zzc.zzc(null).zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.ads.internal.zzg.2.1
                        @Override // com.google.android.gms.internal.zzla.zzc
                        public final /* synthetic */ void zzd(zzft zzftVar) {
                            zzft zzftVar2 = zzftVar;
                            zzftVar2.zza("/appSettingsFetched", zzg.this.zzaku);
                            try {
                                JSONObject jSONObject = new JSONObject();
                                if (!TextUtils.isEmpty(str)) {
                                    jSONObject.put("app_id", str);
                                } else if (!TextUtils.isEmpty(str2)) {
                                    jSONObject.put("ad_unit_id", str2);
                                }
                                jSONObject.put("is_init", z);
                                jSONObject.put("pn", context.getPackageName());
                                zzftVar2.zza("AFMA_fetchAppSettings", jSONObject);
                            } catch (Exception e) {
                                zzftVar2.zzb("/appSettingsFetched", zzg.this.zzaku);
                                zzkd.zzb("Error requesting application settings", e);
                            }
                        }
                    }, new zzla.zzb());
                }
            });
        }
    }
}
