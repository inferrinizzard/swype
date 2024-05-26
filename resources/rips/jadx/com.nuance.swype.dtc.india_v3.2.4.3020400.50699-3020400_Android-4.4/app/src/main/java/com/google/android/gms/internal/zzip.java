package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.ads.internal.request.zzk;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzla;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzip extends zzk.zza {
    private static final Object zzamr = new Object();
    private static zzip zzcdx;
    private final Context mContext;
    private final zzio zzcdy;
    private final zzcv zzcdz;
    private final zzfs zzcea;

    /* renamed from: com.google.android.gms.internal.zzip$3 */
    /* loaded from: classes.dex */
    public final class AnonymousClass3 implements Runnable {
        final /* synthetic */ Context zzala;
        final /* synthetic */ AdRequestInfoParcel zzcec;
        final /* synthetic */ zzir zzcee;

        AnonymousClass3(Context context, zzir zzirVar, AdRequestInfoParcel adRequestInfoParcel) {
            r2 = context;
            r3 = zzirVar;
            r4 = adRequestInfoParcel;
        }

        @Override // java.lang.Runnable
        public final void run() {
            zzix zzixVar = zzio.this.zzcdr;
            zzir zzirVar = r3;
            VersionInfoParcel versionInfoParcel = r4.zzaow;
            zzixVar.zza$26a23421(zzirVar);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzip$5 */
    /* loaded from: classes.dex */
    final class AnonymousClass5 implements Runnable {
        final /* synthetic */ AdRequestInfoParcel zzcec;
        final /* synthetic */ com.google.android.gms.ads.internal.request.zzl zzcek;

        AnonymousClass5(AdRequestInfoParcel adRequestInfoParcel, com.google.android.gms.ads.internal.request.zzl zzlVar) {
            r2 = adRequestInfoParcel;
            r3 = zzlVar;
        }

        @Override // java.lang.Runnable
        public final void run() {
            AdResponseParcel adResponseParcel;
            try {
                adResponseParcel = zzip.this.zzd(r2);
            } catch (Exception e) {
                com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e, true);
                zzkd.zzd("Could not fetch ad response due to an Exception.", e);
                adResponseParcel = null;
            }
            if (adResponseParcel == null) {
                adResponseParcel = new AdResponseParcel(0);
            }
            try {
                r3.zzb(adResponseParcel);
            } catch (RemoteException e2) {
                zzkd.zzd("Fail to forward ad response.", e2);
            }
        }
    }

    public static zzip zza(Context context, zzcv zzcvVar, zzio zzioVar) {
        zzip zzipVar;
        synchronized (zzamr) {
            if (zzcdx == null) {
                if (context.getApplicationContext() != null) {
                    context = context.getApplicationContext();
                }
                zzcdx = new zzip(context, zzcvVar, zzioVar);
            }
            zzipVar = zzcdx;
        }
        return zzipVar;
    }

    private static void zza(String str, Map<String, List<String>> map, String str2, int i) {
        if (zzkd.zzaz(2)) {
            zzkd.v(new StringBuilder(String.valueOf(str).length() + 39).append("Http Response: {\n  URL:\n    ").append(str).append("\n  Headers:").toString());
            if (map != null) {
                for (String str3 : map.keySet()) {
                    zzkd.v(new StringBuilder(String.valueOf(str3).length() + 5).append("    ").append(str3).append(":").toString());
                    Iterator<String> it = map.get(str3).iterator();
                    while (it.hasNext()) {
                        String valueOf = String.valueOf(it.next());
                        zzkd.v(valueOf.length() != 0 ? "      ".concat(valueOf) : new String("      "));
                    }
                }
            }
            zzkd.v("  Body:");
            if (str2 != null) {
                for (int i2 = 0; i2 < Math.min(str2.length(), ACReportingService.MAXIMUM_DATABASE_ENTRIES); i2 += 1000) {
                    zzkd.v(str2.substring(i2, Math.min(str2.length(), i2 + 1000)));
                }
            } else {
                zzkd.v("    null");
            }
            zzkd.v(new StringBuilder(34).append("  Response Code:\n    ").append(i).append("\n}").toString());
        }
    }

    @Override // com.google.android.gms.ads.internal.request.zzk
    public final void zza(AdRequestInfoParcel adRequestInfoParcel, com.google.android.gms.ads.internal.request.zzl zzlVar) {
        com.google.android.gms.ads.internal.zzu.zzft().zzb(this.mContext, adRequestInfoParcel.zzaow);
        zzkg.zza(new Runnable() { // from class: com.google.android.gms.internal.zzip.5
            final /* synthetic */ AdRequestInfoParcel zzcec;
            final /* synthetic */ com.google.android.gms.ads.internal.request.zzl zzcek;

            AnonymousClass5(AdRequestInfoParcel adRequestInfoParcel2, com.google.android.gms.ads.internal.request.zzl zzlVar2) {
                r2 = adRequestInfoParcel2;
                r3 = zzlVar2;
            }

            @Override // java.lang.Runnable
            public final void run() {
                AdResponseParcel adResponseParcel;
                try {
                    adResponseParcel = zzip.this.zzd(r2);
                } catch (Exception e) {
                    com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e, true);
                    zzkd.zzd("Could not fetch ad response due to an Exception.", e);
                    adResponseParcel = null;
                }
                if (adResponseParcel == null) {
                    adResponseParcel = new AdResponseParcel(0);
                }
                try {
                    r3.zzb(adResponseParcel);
                } catch (RemoteException e2) {
                    zzkd.zzd("Fail to forward ad response.", e2);
                }
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.request.zzk
    public final AdResponseParcel zzd(AdRequestInfoParcel adRequestInfoParcel) {
        return zza$3b3f878a(this.mContext, this.zzcea, this.zzcdy, adRequestInfoParcel);
    }

    /* renamed from: com.google.android.gms.internal.zzip$2 */
    /* loaded from: classes.dex */
    public final class AnonymousClass2 implements Runnable {
        final /* synthetic */ zzdk zzakg;
        final /* synthetic */ zzir zzcee;
        final /* synthetic */ zzdi zzcef;
        final /* synthetic */ String zzceg;

        /* renamed from: com.google.android.gms.internal.zzip$2$2 */
        /* loaded from: classes.dex */
        final class C00992 implements zzla.zza {
            C00992() {
            }

            @Override // com.google.android.gms.internal.zzla.zza
            public final void run() {
            }
        }

        AnonymousClass2(zzir zzirVar, zzdk zzdkVar, zzdi zzdiVar, String str) {
            r2 = zzirVar;
            r3 = zzdkVar;
            r4 = zzdiVar;
            r5 = str;
        }

        /* renamed from: com.google.android.gms.internal.zzip$2$1 */
        /* loaded from: classes.dex */
        final class AnonymousClass1 implements zzla.zzc<zzft> {
            final /* synthetic */ zzdi zzceh;

            AnonymousClass1(zzdi zzdiVar) {
                r2 = zzdiVar;
            }

            @Override // com.google.android.gms.internal.zzla.zzc
            public final /* synthetic */ void zzd(zzft zzftVar) {
                zzft zzftVar2 = zzftVar;
                r3.zza(r2, "jsf");
                zzdk zzdkVar = r3;
                synchronized (zzdkVar.zzail) {
                    zzdkVar.zzbei = zzdkVar.zzkg();
                }
                zzftVar2.zza("/invalidRequest", r2.zzcep);
                zzftVar2.zza("/loadAdURL", r2.zzceq);
                zzftVar2.zza("/loadAd", r2.zzcer);
                try {
                    zzftVar2.zzj("AFMA_getAd", r5);
                } catch (Exception e) {
                    zzkd.zzb("Error requesting an ad url", e);
                }
            }
        }

        @Override // java.lang.Runnable
        public final void run() {
            zzfs.zzc zzc = zzfs.this.zzc(null);
            r2.zzceo = zzc;
            r3.zza(r4, "rwc");
            zzc.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzip.2.1
                final /* synthetic */ zzdi zzceh;

                AnonymousClass1(zzdi zzdiVar) {
                    r2 = zzdiVar;
                }

                @Override // com.google.android.gms.internal.zzla.zzc
                public final /* synthetic */ void zzd(zzft zzftVar) {
                    zzft zzftVar2 = zzftVar;
                    r3.zza(r2, "jsf");
                    zzdk zzdkVar = r3;
                    synchronized (zzdkVar.zzail) {
                        zzdkVar.zzbei = zzdkVar.zzkg();
                    }
                    zzftVar2.zza("/invalidRequest", r2.zzcep);
                    zzftVar2.zza("/loadAdURL", r2.zzceq);
                    zzftVar2.zza("/loadAd", r2.zzcer);
                    try {
                        zzftVar2.zzj("AFMA_getAd", r5);
                    } catch (Exception e) {
                        zzkd.zzb("Error requesting an ad url", e);
                    }
                }
            }, new zzla.zza() { // from class: com.google.android.gms.internal.zzip.2.2
                C00992() {
                }

                @Override // com.google.android.gms.internal.zzla.zza
                public final void run() {
                }
            });
        }
    }

    /* renamed from: com.google.android.gms.internal.zzip$1 */
    /* loaded from: classes.dex */
    public final class AnonymousClass1 implements Callable<Void> {
        final /* synthetic */ Context zzala;
        final /* synthetic */ AdRequestInfoParcel zzcec;
        final /* synthetic */ Bundle zzced;

        AnonymousClass1(Context context, AdRequestInfoParcel adRequestInfoParcel, Bundle bundle) {
            r2 = context;
            r3 = adRequestInfoParcel;
            r4 = bundle;
        }

        @Override // java.util.concurrent.Callable
        public final /* synthetic */ Void call() throws Exception {
            String str = r3.zzcas.packageName;
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.android.gms.internal.zzip$4 */
    /* loaded from: classes.dex */
    public final class AnonymousClass4 implements zzkl<zzfp> {
        AnonymousClass4() {
        }

        @Override // com.google.android.gms.internal.zzkl
        public final /* synthetic */ void zzd(zzfp zzfpVar) {
            zzfpVar.zza("/log", zzeo.zzbhv);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static AdResponseParcel zza$3b3f878a(Context context, zzfs zzfsVar, zzio zzioVar, AdRequestInfoParcel adRequestInfoParcel) {
        Bundle bundle;
        zzky zzkyVar;
        String string;
        zzkd.zzcv("Starting ad request from service using: AFMA_getAd");
        zzdc.initialize(context);
        zzdk zzdkVar = new zzdk(((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzaze)).booleanValue(), "load_ad", adRequestInfoParcel.zzapa.zzaur);
        if (adRequestInfoParcel.versionCode > 10 && adRequestInfoParcel.zzcbj != -1) {
            zzdkVar.zza(zzdkVar.zzc(adRequestInfoParcel.zzcbj), "cts");
        }
        zzdi zzkg = zzdkVar.zzkg();
        Bundle bundle2 = (adRequestInfoParcel.versionCode < 4 || adRequestInfoParcel.zzcay == null) ? null : adRequestInfoParcel.zzcay;
        zzky zzkyVar2 = null;
        if (!((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazn)).booleanValue() || zzioVar.zzcdw == null) {
            bundle = bundle2;
        } else {
            if (bundle2 == null && ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazo)).booleanValue()) {
                zzkd.v("contentInfo is not present, but we'll still launch the app index task");
                bundle2 = new Bundle();
            }
            if (bundle2 != null) {
                zzkyVar2 = zzkg.zza(new Callable<Void>() { // from class: com.google.android.gms.internal.zzip.1
                    final /* synthetic */ Context zzala;
                    final /* synthetic */ AdRequestInfoParcel zzcec;
                    final /* synthetic */ Bundle zzced;

                    AnonymousClass1(Context context2, AdRequestInfoParcel adRequestInfoParcel2, Bundle bundle22) {
                        r2 = context2;
                        r3 = adRequestInfoParcel2;
                        r4 = bundle22;
                    }

                    @Override // java.util.concurrent.Callable
                    public final /* synthetic */ Void call() throws Exception {
                        String str = r3.zzcas.packageName;
                        return null;
                    }
                });
                bundle = bundle22;
            } else {
                bundle = bundle22;
            }
        }
        zzky zzkwVar = new zzkw(null);
        Bundle bundle3 = adRequestInfoParcel2.zzcar.extras;
        boolean z = (bundle3 == null || bundle3.getString("_ad") == null) ? false : true;
        if (!adRequestInfoParcel2.zzcbq || z) {
            zzkyVar = zzkwVar;
        } else {
            zzfw zzfwVar = zzioVar.zzcds;
            ApplicationInfo applicationInfo = adRequestInfoParcel2.applicationInfo;
            zzkyVar = zzfwVar.zza$5b6906ad();
        }
        zziv zzy = com.google.android.gms.ads.internal.zzu.zzfw().zzy(context2);
        if (zzy.zzcgp == -1) {
            zzkd.zzcv("Device is offline.");
            return new AdResponseParcel(2);
        }
        String uuid = adRequestInfoParcel2.versionCode >= 7 ? adRequestInfoParcel2.zzcbg : UUID.randomUUID().toString();
        zzir zzirVar = new zzir(uuid, adRequestInfoParcel2.applicationInfo.packageName);
        if (adRequestInfoParcel2.zzcar.extras != null && (string = adRequestInfoParcel2.zzcar.extras.getString("_ad")) != null) {
            return zziq.zza(context2, adRequestInfoParcel2, string);
        }
        List<String> zza = zzioVar.zzcdq.zza(adRequestInfoParcel2);
        String zzf = zzioVar.zzcdt.zzf(adRequestInfoParcel2);
        if (zzkyVar2 != null) {
            try {
                zzkd.v("Waiting for app index fetching task.");
                zzkyVar2.get(((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazp)).longValue(), TimeUnit.MILLISECONDS);
                zzkd.v("App index fetching task completed.");
            } catch (InterruptedException e) {
                e = e;
                zzkd.zzd("Failed to fetch app index signal", e);
            } catch (ExecutionException e2) {
                e = e2;
                zzkd.zzd("Failed to fetch app index signal", e);
            } catch (TimeoutException e3) {
                zzkd.zzcv("Timed out waiting for app index fetching task");
            }
        }
        String str = adRequestInfoParcel2.zzcas.packageName;
        JSONObject zza$7edf9512$5c31bcc4 = zziq.zza$7edf9512$5c31bcc4(adRequestInfoParcel2, zzy, zzb(zzkyVar), zzf, zza, bundle);
        if (zza$7edf9512$5c31bcc4 == null) {
            return new AdResponseParcel(0);
        }
        if (adRequestInfoParcel2.versionCode < 7) {
            try {
                zza$7edf9512$5c31bcc4.put("request_id", uuid);
            } catch (JSONException e4) {
            }
        }
        try {
            zza$7edf9512$5c31bcc4.put("prefetch_mode", "url");
        } catch (JSONException e5) {
            zzkd.zzd("Failed putting prefetch parameters to ad request.", e5);
        }
        String jSONObject = zza$7edf9512$5c31bcc4.toString();
        zzdkVar.zza(zzkg, "arc");
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzip.2
            final /* synthetic */ zzdk zzakg;
            final /* synthetic */ zzir zzcee;
            final /* synthetic */ zzdi zzcef;
            final /* synthetic */ String zzceg;

            /* renamed from: com.google.android.gms.internal.zzip$2$2 */
            /* loaded from: classes.dex */
            final class C00992 implements zzla.zza {
                C00992() {
                }

                @Override // com.google.android.gms.internal.zzla.zza
                public final void run() {
                }
            }

            AnonymousClass2(zzir zzirVar2, zzdk zzdkVar2, zzdi zzdiVar, String jSONObject2) {
                r2 = zzirVar2;
                r3 = zzdkVar2;
                r4 = zzdiVar;
                r5 = jSONObject2;
            }

            /* renamed from: com.google.android.gms.internal.zzip$2$1 */
            /* loaded from: classes.dex */
            final class AnonymousClass1 implements zzla.zzc<zzft> {
                final /* synthetic */ zzdi zzceh;

                AnonymousClass1(zzdi zzdiVar) {
                    r2 = zzdiVar;
                }

                @Override // com.google.android.gms.internal.zzla.zzc
                public final /* synthetic */ void zzd(zzft zzftVar) {
                    zzft zzftVar2 = zzftVar;
                    r3.zza(r2, "jsf");
                    zzdk zzdkVar = r3;
                    synchronized (zzdkVar.zzail) {
                        zzdkVar.zzbei = zzdkVar.zzkg();
                    }
                    zzftVar2.zza("/invalidRequest", r2.zzcep);
                    zzftVar2.zza("/loadAdURL", r2.zzceq);
                    zzftVar2.zza("/loadAd", r2.zzcer);
                    try {
                        zzftVar2.zzj("AFMA_getAd", r5);
                    } catch (Exception e) {
                        zzkd.zzb("Error requesting an ad url", e);
                    }
                }
            }

            @Override // java.lang.Runnable
            public final void run() {
                zzfs.zzc zzc = zzfs.this.zzc(null);
                r2.zzceo = zzc;
                r3.zza(r4, "rwc");
                zzc.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzip.2.1
                    final /* synthetic */ zzdi zzceh;

                    AnonymousClass1(zzdi zzdiVar) {
                        r2 = zzdiVar;
                    }

                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzft zzftVar) {
                        zzft zzftVar2 = zzftVar;
                        r3.zza(r2, "jsf");
                        zzdk zzdkVar2 = r3;
                        synchronized (zzdkVar2.zzail) {
                            zzdkVar2.zzbei = zzdkVar2.zzkg();
                        }
                        zzftVar2.zza("/invalidRequest", r2.zzcep);
                        zzftVar2.zza("/loadAdURL", r2.zzceq);
                        zzftVar2.zza("/loadAd", r2.zzcer);
                        try {
                            zzftVar2.zzj("AFMA_getAd", r5);
                        } catch (Exception e6) {
                            zzkd.zzb("Error requesting an ad url", e6);
                        }
                    }
                }, new zzla.zza() { // from class: com.google.android.gms.internal.zzip.2.2
                    C00992() {
                    }

                    @Override // com.google.android.gms.internal.zzla.zza
                    public final void run() {
                    }
                });
            }
        });
        try {
            zziu zziuVar = zzirVar2.zzcen.get(10L, TimeUnit.SECONDS);
            if (zziuVar == null) {
                return new AdResponseParcel(0);
            }
            if (zziuVar.zzbyi != -2) {
                return new AdResponseParcel(zziuVar.zzbyi);
            }
            if (zzdkVar2.zzkj() != null) {
                zzdkVar2.zza(zzdkVar2.zzkj(), "rur");
            }
            AdResponseParcel zza2 = TextUtils.isEmpty(zziuVar.zzcgc) ? null : zziq.zza(context2, adRequestInfoParcel2, zziuVar.zzcgc);
            if (zza2 == null && !TextUtils.isEmpty(zziuVar.zzae)) {
                zza2 = zza$6bacb101(adRequestInfoParcel2, context2, adRequestInfoParcel2.zzaow.zzcs, zziuVar.zzae, zziuVar, zzdkVar2, zzioVar);
            }
            if (zza2 == null) {
                zza2 = new AdResponseParcel(0);
            }
            zzdkVar2.zza(zzkg, "tts");
            zza2.zzccl = zzdkVar2.zzki();
            return zza2;
        } catch (Exception e6) {
            return new AdResponseParcel(0);
        } finally {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzip.3
                final /* synthetic */ Context zzala;
                final /* synthetic */ AdRequestInfoParcel zzcec;
                final /* synthetic */ zzir zzcee;

                AnonymousClass3(Context context2, zzir zzirVar2, AdRequestInfoParcel adRequestInfoParcel2) {
                    r2 = context2;
                    r3 = zzirVar2;
                    r4 = adRequestInfoParcel2;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    zzix zzixVar = zzio.this.zzcdr;
                    zzir zzirVar2 = r3;
                    VersionInfoParcel versionInfoParcel = r4.zzaow;
                    zzixVar.zza$26a23421(zzirVar2);
                }
            });
        }
    }

    private static Location zzb(zzky<Location> zzkyVar) {
        try {
            return zzkyVar.get(((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbcp)).longValue(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            zzkd.zzd("Exception caught while getting location", e);
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:89:0x022f, code lost:            com.google.android.gms.internal.zzkd.zzcx(new java.lang.StringBuilder(46).append("Received error HTTP response code: ").append(r4).toString());     */
    /* JADX WARN: Code restructure failed: missing block: B:91:?, code lost:            return new com.google.android.gms.ads.internal.request.AdResponseParcel(0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.google.android.gms.ads.internal.request.AdResponseParcel zza$6bacb101(com.google.android.gms.ads.internal.request.AdRequestInfoParcel r44, android.content.Context r45, java.lang.String r46, java.lang.String r47, com.google.android.gms.internal.zziu r48, com.google.android.gms.internal.zzdk r49, com.google.android.gms.internal.zzio r50) {
        /*
            Method dump skipped, instructions count: 628
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzip.zza$6bacb101(com.google.android.gms.ads.internal.request.AdRequestInfoParcel, android.content.Context, java.lang.String, java.lang.String, com.google.android.gms.internal.zziu, com.google.android.gms.internal.zzdk, com.google.android.gms.internal.zzio):com.google.android.gms.ads.internal.request.AdResponseParcel");
    }

    private zzip(Context context, zzcv zzcvVar, zzio zzioVar) {
        this.mContext = context;
        this.zzcdy = zzioVar;
        this.zzcdz = zzcvVar;
        this.zzcea = new zzfs(context.getApplicationContext() != null ? context.getApplicationContext() : context, new VersionInfoParcel(9452208, 9452208, true), zzcvVar.zzaxn, new zzkl<zzfp>() { // from class: com.google.android.gms.internal.zzip.4
            AnonymousClass4() {
            }

            @Override // com.google.android.gms.internal.zzkl
            public final /* synthetic */ void zzd(zzfp zzfpVar) {
                zzfpVar.zza("/log", zzeo.zzbhv);
            }
        }, new zzfs.zzb());
    }
}
