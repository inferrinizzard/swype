package com.google.android.gms.ads.internal.request;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.zza;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.internal.zzcv;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzep;
import com.google.android.gms.internal.zzeq;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzfp;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzft;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zziq;
import com.google.android.gms.internal.zziv;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkc;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzkl;
import com.google.android.gms.internal.zzkv;
import com.google.android.gms.internal.zzla;
import com.google.android.gms.internal.zzlh;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzn extends zzkc {
    private final Context mContext;
    private final Object zzbxu;
    private final zza.InterfaceC0027zza zzcae;
    private final AdRequestInfoParcel.zza zzcaf;
    private zzfs.zzc zzcdk;
    static final long zzcdf = TimeUnit.SECONDS.toMillis(10);
    private static final Object zzamr = new Object();
    static boolean zzcdg = false;
    private static zzfs zzbyv = null;
    private static zzeq zzcdh = null;
    private static zzeu zzcdi = null;
    private static zzep zzcdj = null;

    /* loaded from: classes.dex */
    public static class zza implements zzkl<zzfp> {
        @Override // com.google.android.gms.internal.zzkl
        /* renamed from: zza, reason: merged with bridge method [inline-methods] */
        public void zzd(zzfp zzfpVar) {
            zzn.zzc(zzfpVar);
        }
    }

    /* loaded from: classes.dex */
    public static class zzb implements zzkl<zzfp> {
        @Override // com.google.android.gms.internal.zzkl
        /* renamed from: zza, reason: merged with bridge method [inline-methods] */
        public void zzd(zzfp zzfpVar) {
            zzn.zzb(zzfpVar);
        }
    }

    /* loaded from: classes.dex */
    public static class zzc implements zzep {
        @Override // com.google.android.gms.internal.zzep
        public void zza(zzlh zzlhVar, Map<String, String> map) {
            String str = map.get("request_id");
            String valueOf = String.valueOf(map.get("errors"));
            zzkd.zzcx(valueOf.length() != 0 ? "Invalid request: ".concat(valueOf) : new String("Invalid request: "));
            zzn.zzcdi.zzax(str);
        }
    }

    protected static void zzb(zzfp zzfpVar) {
        zzfpVar.zza("/loadAd", zzcdi);
        zzfpVar.zza("/fetchHttpRequest", zzcdh);
        zzfpVar.zza("/invalidRequest", zzcdj);
    }

    protected static void zzc(zzfp zzfpVar) {
        zzfpVar.zzb("/loadAd", zzcdi);
        zzfpVar.zzb("/fetchHttpRequest", zzcdh);
        zzfpVar.zzb("/invalidRequest", zzcdj);
    }

    @Override // com.google.android.gms.internal.zzkc
    public void onStop() {
        synchronized (this.zzbxu) {
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.ads.internal.request.zzn.3
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzn.this.zzcdk != null) {
                        zzn.this.zzcdk.release();
                        zzn.this.zzcdk = null;
                    }
                }
            });
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public void zzew() {
        zzkd.zzcv("SdkLessAdLoaderBackgroundTask started.");
        AdRequestInfoParcel adRequestInfoParcel = new AdRequestInfoParcel(this.zzcaf, null, -1L);
        AdResponseParcel zze = zze(adRequestInfoParcel);
        final zzju.zza zzaVar = new zzju.zza(adRequestInfoParcel, zze, null, null, zze.errorCode, zzu.zzfu().elapsedRealtime(), zze.zzccc, null);
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.ads.internal.request.zzn.1
            @Override // java.lang.Runnable
            public final void run() {
                zzn.this.zzcae.zza(zzaVar);
                if (zzn.this.zzcdk != null) {
                    zzn.this.zzcdk.release();
                    zzn.this.zzcdk = null;
                }
            }
        });
    }

    public zzn(Context context, AdRequestInfoParcel.zza zzaVar, zza.InterfaceC0027zza interfaceC0027zza) {
        super(true);
        this.zzbxu = new Object();
        this.zzcae = interfaceC0027zza;
        this.mContext = context;
        this.zzcaf = zzaVar;
        synchronized (zzamr) {
            if (!zzcdg) {
                zzcdi = new zzeu();
                zzcdh = new zzeq(context.getApplicationContext(), zzaVar.zzaow);
                zzcdj = new zzc();
                zzbyv = new zzfs(this.mContext.getApplicationContext(), this.zzcaf.zzaow, (String) zzu.zzfz().zzd(zzdc.zzaxy), new zzb(), new zza());
                zzcdg = true;
            }
        }
    }

    private AdResponseParcel zze(AdRequestInfoParcel adRequestInfoParcel) {
        zzu.zzfq();
        final String zzte = zzkh.zzte();
        final JSONObject zza2 = zza(adRequestInfoParcel, zzte);
        if (zza2 == null) {
            return new AdResponseParcel(0);
        }
        long elapsedRealtime = zzu.zzfu().elapsedRealtime();
        zzeu zzeuVar = zzcdi;
        zzkv<JSONObject> zzkvVar = new zzkv<>();
        zzeuVar.zzbis.put(zzte, zzkvVar);
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.ads.internal.request.zzn.2
            @Override // java.lang.Runnable
            public final void run() {
                zzn.this.zzcdk = zzn.zzbyv.zzc(null);
                zzn.this.zzcdk.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.ads.internal.request.zzn.2.1
                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzft zzftVar) {
                        try {
                            zzftVar.zza("AFMA_getAdapterLessMediationAd", zza2);
                        } catch (Exception e) {
                            zzkd.zzb("Error requesting an ad url", e);
                            zzn.zzcdi.zzax(zzte);
                        }
                    }
                }, new zzla.zza() { // from class: com.google.android.gms.ads.internal.request.zzn.2.2
                    @Override // com.google.android.gms.internal.zzla.zza
                    public final void run() {
                        zzn.zzcdi.zzax(zzte);
                    }
                });
            }
        });
        try {
            JSONObject jSONObject = zzkvVar.get(zzcdf - (zzu.zzfu().elapsedRealtime() - elapsedRealtime), TimeUnit.MILLISECONDS);
            if (jSONObject == null) {
                return new AdResponseParcel(-1);
            }
            AdResponseParcel zza3 = zziq.zza(this.mContext, adRequestInfoParcel, jSONObject.toString());
            return (zza3.errorCode == -3 || !TextUtils.isEmpty(zza3.body)) ? zza3 : new AdResponseParcel(3);
        } catch (InterruptedException e) {
            return new AdResponseParcel(-1);
        } catch (CancellationException e2) {
            return new AdResponseParcel(-1);
        } catch (ExecutionException e3) {
            return new AdResponseParcel(0);
        } catch (TimeoutException e4) {
            return new AdResponseParcel(2);
        }
    }

    private JSONObject zza(AdRequestInfoParcel adRequestInfoParcel, String str) {
        AdvertisingIdClient.Info info;
        Bundle bundle = adRequestInfoParcel.zzcar.extras.getBundle("sdk_less_server_data");
        String string = adRequestInfoParcel.zzcar.extras.getString("sdk_less_network_id");
        if (bundle == null) {
            return null;
        }
        zziv zzy = zzu.zzfw().zzy(this.mContext);
        new zzcv((String) zzu.zzfz().zzd(zzdc.zzaxy));
        JSONObject zza$7edf9512$5c31bcc4 = zziq.zza$7edf9512$5c31bcc4(adRequestInfoParcel, zzy, null, null, new ArrayList(), null);
        if (zza$7edf9512$5c31bcc4 == null) {
            return null;
        }
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(this.mContext);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException | IllegalStateException e) {
            zzkd.zzd("Cannot get advertising id info", e);
            info = null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("request_id", str);
        hashMap.put("network_id", string);
        hashMap.put("request_param", zza$7edf9512$5c31bcc4);
        hashMap.put(ShareConstants.WEB_DIALOG_PARAM_DATA, bundle);
        if (info != null) {
            hashMap.put("adid", info.getId());
            hashMap.put("lat", Integer.valueOf(info.isLimitAdTrackingEnabled() ? 1 : 0));
        }
        try {
            return zzu.zzfq().zzam(hashMap);
        } catch (JSONException e2) {
            return null;
        }
    }
}
