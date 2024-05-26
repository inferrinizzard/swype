package com.google.android.gms.ads.internal.request;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.zza;
import com.google.android.gms.ads.internal.request.zzc;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.internal.zzas;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzga;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkc;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkg;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzkj;
import com.google.android.gms.internal.zzlb;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzb extends zzkc implements zzc.zza {
    final Context mContext;
    private final zzas zzbgd;
    zzga zzboe;
    private AdRequestInfoParcel zzbot;
    AdResponseParcel zzbxs;
    private Runnable zzbxt;
    private final Object zzbxu = new Object();
    private final zza.InterfaceC0027zza zzcae;
    private final AdRequestInfoParcel.zza zzcaf;
    zzkj zzcag;

    /* JADX INFO: Access modifiers changed from: package-private */
    @zzin
    /* loaded from: classes.dex */
    public static final class zza extends Exception {
        final int zzbyi;

        public zza(String str, int i) {
            super(str);
            this.zzbyi = i;
        }
    }

    public zzb(Context context, AdRequestInfoParcel.zza zzaVar, zzas zzasVar, zza.InterfaceC0027zza interfaceC0027zza) {
        this.zzcae = interfaceC0027zza;
        this.mContext = context;
        this.zzcaf = zzaVar;
        this.zzbgd = zzasVar;
    }

    private AdSizeParcel zzb(AdRequestInfoParcel adRequestInfoParcel) throws zza {
        if (this.zzbxs.zzauv) {
            for (AdSizeParcel adSizeParcel : adRequestInfoParcel.zzapa.zzaut) {
                if (adSizeParcel.zzauv) {
                    return new AdSizeParcel(adSizeParcel, adRequestInfoParcel.zzapa.zzaut);
                }
            }
        }
        if (this.zzbxs.zzccb == null) {
            throw new zza("The ad response must specify one of the supported ad sizes.", 0);
        }
        String[] split = this.zzbxs.zzccb.split("x");
        if (split.length != 2) {
            String valueOf = String.valueOf(this.zzbxs.zzccb);
            throw new zza(valueOf.length() != 0 ? "Invalid ad size format from the ad response: ".concat(valueOf) : new String("Invalid ad size format from the ad response: "), 0);
        }
        try {
            int parseInt = Integer.parseInt(split[0]);
            int parseInt2 = Integer.parseInt(split[1]);
            for (AdSizeParcel adSizeParcel2 : adRequestInfoParcel.zzapa.zzaut) {
                float f = this.mContext.getResources().getDisplayMetrics().density;
                int i = adSizeParcel2.width == -1 ? (int) (adSizeParcel2.widthPixels / f) : adSizeParcel2.width;
                int i2 = adSizeParcel2.height == -2 ? (int) (adSizeParcel2.heightPixels / f) : adSizeParcel2.height;
                if (parseInt == i && parseInt2 == i2) {
                    return new AdSizeParcel(adSizeParcel2, adRequestInfoParcel.zzapa.zzaut);
                }
            }
            String valueOf2 = String.valueOf(this.zzbxs.zzccb);
            throw new zza(valueOf2.length() != 0 ? "The ad size from the ad response was not one of the requested sizes: ".concat(valueOf2) : new String("The ad size from the ad response was not one of the requested sizes: "), 0);
        } catch (NumberFormatException e) {
            String valueOf3 = String.valueOf(this.zzbxs.zzccb);
            throw new zza(valueOf3.length() != 0 ? "Invalid ad size number from the ad response: ".concat(valueOf3) : new String("Invalid ad size number from the ad response: "), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zzd(int i, String str) {
        if (i == 3 || i == -1) {
            zzkd.zzcw(str);
        } else {
            zzkd.zzcx(str);
        }
        if (this.zzbxs == null) {
            this.zzbxs = new AdResponseParcel(i);
        } else {
            this.zzbxs = new AdResponseParcel(i, this.zzbxs.zzbns);
        }
        this.zzcae.zza(new zzju.zza(this.zzbot != null ? this.zzbot : new AdRequestInfoParcel(this.zzcaf, null, -1L), this.zzbxs, this.zzboe, null, i, -1L, this.zzbxs.zzccc, null));
    }

    @Override // com.google.android.gms.internal.zzkc
    public void onStop() {
        synchronized (this.zzbxu) {
            if (this.zzcag != null) {
                this.zzcag.cancel();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public void zzew() {
        zzkd.zzcv("AdLoaderBackgroundTask started.");
        this.zzbxt = new Runnable() { // from class: com.google.android.gms.ads.internal.request.zzb.1
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (zzb.this.zzbxu) {
                    if (zzb.this.zzcag == null) {
                        return;
                    }
                    zzb.this.onStop();
                    zzb.this.zzd(2, "Timed out waiting for ad response.");
                }
            }
        };
        zzkh.zzclc.postDelayed(this.zzbxt, ((Long) zzu.zzfz().zzd(zzdc.zzbbg)).longValue());
        final zzlb zzlbVar = new zzlb();
        long elapsedRealtime = zzu.zzfu().elapsedRealtime();
        zzkg.zza(new Runnable() { // from class: com.google.android.gms.ads.internal.request.zzb.2
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (zzb.this.zzbxu) {
                    zzb zzbVar = zzb.this;
                    zzb zzbVar2 = zzb.this;
                    zzbVar.zzcag = zzc.zza(zzbVar2.mContext, zzb.this.zzcaf.zzaow, zzlbVar, zzbVar2);
                    if (zzb.this.zzcag == null) {
                        zzb.this.zzd(0, "Could not start the ad request service.");
                        zzkh.zzclc.removeCallbacks(zzb.this.zzbxt);
                    }
                }
            }
        });
        this.zzbot = new AdRequestInfoParcel(this.zzcaf, this.zzbgd.zzafz.zzb(this.mContext), elapsedRealtime);
        zzlbVar.zzg(this.zzbot);
    }

    @Override // com.google.android.gms.ads.internal.request.zzc.zza
    public void zzb(AdResponseParcel adResponseParcel) {
        JSONObject jSONObject;
        zzkd.zzcv("Received ad response.");
        this.zzbxs = adResponseParcel;
        long elapsedRealtime = zzu.zzfu().elapsedRealtime();
        synchronized (this.zzbxu) {
            this.zzcag = null;
        }
        zzu.zzft().zzd(this.mContext, this.zzbxs.zzcbq);
        try {
            if (this.zzbxs.errorCode != -2 && this.zzbxs.errorCode != -3) {
                throw new zza(new StringBuilder(66).append("There was a problem getting an ad response. ErrorCode: ").append(this.zzbxs.errorCode).toString(), this.zzbxs.errorCode);
            }
            if (this.zzbxs.errorCode != -3) {
                if (TextUtils.isEmpty(this.zzbxs.body)) {
                    throw new zza("No fill from ad server.", 3);
                }
                zzu.zzft().zzc(this.mContext, this.zzbxs.zzcaz);
                if (this.zzbxs.zzcby) {
                    try {
                        this.zzboe = new zzga(this.zzbxs.body);
                        zzu.zzft().zzcjz = this.zzboe.zzbnq;
                    } catch (JSONException e) {
                        zzkd.zzb("Could not parse mediation config.", e);
                        String valueOf = String.valueOf(this.zzbxs.body);
                        throw new zza(valueOf.length() != 0 ? "Could not parse mediation config: ".concat(valueOf) : new String("Could not parse mediation config: "), 0);
                    }
                } else {
                    zzu.zzft().zzcjz = this.zzbxs.zzbnq;
                }
                if (!TextUtils.isEmpty(this.zzbxs.zzcbr)) {
                    if (((Boolean) zzu.zzfz().zzd(zzdc.zzbdn)).booleanValue()) {
                        zzkd.zzcv("Received cookie from server. Setting webview cookie in CookieManager.");
                        CookieManager zzao = zzu.zzfs().zzao(this.mContext);
                        if (zzao != null) {
                            zzao.setCookie("googleads.g.doubleclick.net", this.zzbxs.zzcbr);
                        }
                    }
                }
            }
            AdSizeParcel zzb = this.zzbot.zzapa.zzaut != null ? zzb(this.zzbot) : null;
            zzu.zzft().zzae(this.zzbxs.zzcci);
            if (!TextUtils.isEmpty(this.zzbxs.zzccg)) {
                try {
                    jSONObject = new JSONObject(this.zzbxs.zzccg);
                } catch (Exception e2) {
                    zzkd.zzb("Error parsing the JSON for Active View.", e2);
                }
                this.zzcae.zza(new zzju.zza(this.zzbot, this.zzbxs, this.zzboe, zzb, -2, elapsedRealtime, this.zzbxs.zzccc, jSONObject));
                zzkh.zzclc.removeCallbacks(this.zzbxt);
            }
            jSONObject = null;
            this.zzcae.zza(new zzju.zza(this.zzbot, this.zzbxs, this.zzboe, zzb, -2, elapsedRealtime, this.zzbxs.zzccc, jSONObject));
            zzkh.zzclc.removeCallbacks(this.zzbxt);
        } catch (zza e3) {
            zzd(e3.zzbyi, e3.getMessage());
            zzkh.zzclc.removeCallbacks(this.zzbxt);
        }
    }
}
