package com.google.android.gms.ads.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.webkit.CookieManager;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.purchase.GInAppPurchaseManagerInfoParcel;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.CapabilityParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzer;
import com.google.android.gms.internal.zzgb;
import com.google.android.gms.internal.zzgf;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzhl;
import com.google.android.gms.internal.zzho;
import com.google.android.gms.internal.zzhs;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzjv;
import com.google.android.gms.internal.zzjw;
import com.google.android.gms.internal.zzjx;
import com.google.android.gms.internal.zzjy;
import com.google.android.gms.internal.zzka;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzki;
import com.google.android.gms.internal.zzlh;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@zzin
/* loaded from: classes.dex */
public abstract class zzb extends zza implements com.google.android.gms.ads.internal.overlay.zzg, com.google.android.gms.ads.internal.purchase.zzj, zzs, zzer, zzgb {
    private final Messenger mMessenger;
    public final zzgj zzajz;
    protected transient boolean zzaka;

    public zzb(Context context, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, zzd zzdVar) {
        this(new zzv(context, adSizeParcel, str, versionInfoParcel), zzgjVar, zzdVar);
    }

    private zzb(zzv zzvVar, zzgj zzgjVar, zzd zzdVar) {
        super(zzvVar, zzdVar);
        this.zzajz = zzgjVar;
        this.mMessenger = new Messenger(new zzhl(this.zzajs.zzagf));
        this.zzaka = false;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public String getMediationAdapterClassName() {
        if (this.zzajs.zzapb == null) {
            return null;
        }
        return this.zzajs.zzapb.zzbop;
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zza
    public void onAdClicked() {
        if (this.zzajs.zzapb == null) {
            zzkd.zzcx("Ad state was null when trying to ping click URLs.");
            return;
        }
        if (this.zzajs.zzapb.zzcig != null && this.zzajs.zzapb.zzcig.zzbnm != null) {
            zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb, this.zzajs.zzaou, false, this.zzajs.zzapb.zzcig.zzbnm);
        }
        if (this.zzajs.zzapb.zzbon != null && this.zzajs.zzapb.zzbon.zzbmz != null) {
            zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb, this.zzajs.zzaou, false, this.zzajs.zzapb.zzbon.zzbmz);
        }
        super.onAdClicked();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzg
    public void onPause() {
        this.zzaju.zzk(this.zzajs.zzapb);
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzg
    public void onResume() {
        this.zzaju.zzl(this.zzajs.zzapb);
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void pause() {
        zzab.zzhi("pause must be called on the main UI thread.");
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzbtm != null && this.zzajs.zzgp()) {
            zzu.zzfs();
            zzki.zzi(this.zzajs.zzapb.zzbtm);
        }
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzboo != null) {
            try {
                this.zzajs.zzapb.zzboo.pause();
            } catch (RemoteException e) {
                zzkd.zzcx("Could not pause mediation adapter.");
            }
        }
        this.zzaju.zzk(this.zzajs.zzapb);
        this.zzajr.pause();
    }

    public void recordImpression() {
        zza(this.zzajs.zzapb, false);
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void resume() {
        zzab.zzhi("resume must be called on the main UI thread.");
        zzlh zzlhVar = null;
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzbtm != null) {
            zzlhVar = this.zzajs.zzapb.zzbtm;
        }
        if (zzlhVar != null && this.zzajs.zzgp()) {
            zzu.zzfs();
            zzki.zzj(this.zzajs.zzapb.zzbtm);
        }
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzboo != null) {
            try {
                this.zzajs.zzapb.zzboo.resume();
            } catch (RemoteException e) {
                zzkd.zzcx("Could not resume mediation adapter.");
            }
        }
        if (zzlhVar == null || !zzlhVar.zzup()) {
            this.zzajr.resume();
        }
        this.zzaju.zzl(this.zzajs.zzapb);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void showInterstitial() {
        throw new IllegalStateException("showInterstitial is not supported for current ad type");
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void zza(zzho zzhoVar) {
        zzab.zzhi("setInAppPurchaseListener must be called on the main UI thread.");
        this.zzajs.zzapi = zzhoVar;
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void zza(zzhs zzhsVar, String str) {
        zzab.zzhi("setPlayStorePurchaseParams must be called on the main UI thread.");
        this.zzajs.zzapt = new com.google.android.gms.ads.internal.purchase.zzk(str);
        this.zzajs.zzapj = zzhsVar;
        if (zzu.zzft().zzsm() || zzhsVar == null) {
            return;
        }
        new com.google.android.gms.ads.internal.purchase.zzc(this.zzajs.zzagf, this.zzajs.zzapj, this.zzajs.zzapt).zzpy();
    }

    @Override // com.google.android.gms.internal.zzer
    public void zza(String str, ArrayList<String> arrayList) {
        com.google.android.gms.ads.internal.purchase.zzd zzdVar = new com.google.android.gms.ads.internal.purchase.zzd(str, arrayList, this.zzajs.zzagf, this.zzajs.zzaow.zzcs);
        if (this.zzajs.zzapi != null) {
            try {
                this.zzajs.zzapi.zza(zzdVar);
                return;
            } catch (RemoteException e) {
                zzkd.zzcx("Could not start In-App purchase.");
                return;
            }
        }
        zzkd.zzcx("InAppPurchaseListener is not set. Try to launch default purchase flow.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zzar(this.zzajs.zzagf)) {
            zzkd.zzcx("Google Play Service unavailable, cannot launch default purchase flow.");
            return;
        }
        if (this.zzajs.zzapj == null) {
            zzkd.zzcx("PlayStorePurchaseListener is not set.");
            return;
        }
        if (this.zzajs.zzapt == null) {
            zzkd.zzcx("PlayStorePurchaseVerifier is not initialized.");
            return;
        }
        if (this.zzajs.zzapx) {
            zzkd.zzcx("An in-app purchase request is already in progress, abort");
            return;
        }
        this.zzajs.zzapx = true;
        try {
            if (this.zzajs.zzapj.isValidPurchase(str)) {
                zzu.zzga().zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcnm, new GInAppPurchaseManagerInfoParcel(this.zzajs.zzagf, this.zzajs.zzapt, zzdVar, this));
            } else {
                this.zzajs.zzapx = false;
            }
        } catch (RemoteException e2) {
            zzkd.zzcx("Could not start In-App purchase.");
            this.zzajs.zzapx = false;
        }
    }

    @Override // com.google.android.gms.ads.internal.purchase.zzj
    public void zza(String str, boolean z, int i, final Intent intent, com.google.android.gms.ads.internal.purchase.zzf zzfVar) {
        try {
            if (this.zzajs.zzapj != null) {
                this.zzajs.zzapj.zza(new com.google.android.gms.ads.internal.purchase.zzg(this.zzajs.zzagf, str, z, i, intent, zzfVar));
            }
        } catch (RemoteException e) {
            zzkd.zzcx("Fail to invoke PlayStorePurchaseListener.");
        }
        zzkh.zzclc.postDelayed(new Runnable() { // from class: com.google.android.gms.ads.internal.zzb.1
            @Override // java.lang.Runnable
            public final void run() {
                int zzd = zzu.zzga().zzd(intent);
                zzu.zzga();
                if (zzd == 0 && zzb.this.zzajs.zzapb != null && zzb.this.zzajs.zzapb.zzbtm != null && zzb.this.zzajs.zzapb.zzbtm.zzuh() != null) {
                    zzb.this.zzajs.zzapb.zzbtm.zzuh().close();
                }
                zzb.this.zzajs.zzapx = false;
            }
        }, 500L);
    }

    public boolean zza(AdRequestParcel adRequestParcel, zzju zzjuVar, boolean z) {
        if (!z && this.zzajs.zzgp()) {
            if (zzjuVar.zzbns > 0) {
                this.zzajr.zza(adRequestParcel, zzjuVar.zzbns);
            } else if (zzjuVar.zzcig != null && zzjuVar.zzcig.zzbns > 0) {
                this.zzajr.zza(adRequestParcel, zzjuVar.zzcig.zzbns);
            } else if (!zzjuVar.zzcby && zzjuVar.errorCode == 2) {
                this.zzajr.zzg(adRequestParcel);
            }
        }
        return this.zzajr.zzfc();
    }

    @Override // com.google.android.gms.ads.internal.zza
    final boolean zza(zzju zzjuVar) {
        AdRequestParcel adRequestParcel;
        boolean z = false;
        if (this.zzajt != null) {
            adRequestParcel = this.zzajt;
            this.zzajt = null;
        } else {
            adRequestParcel = zzjuVar.zzcar;
            if (adRequestParcel.extras != null) {
                z = adRequestParcel.extras.getBoolean("_noRefresh", false);
            }
        }
        return zza(adRequestParcel, zzjuVar, z);
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.internal.zzic.zza
    public void zzb(zzju zzjuVar) {
        super.zzb(zzjuVar);
        if (zzjuVar.zzbon != null) {
            zzkd.zzcv("Pinging network fill URLs.");
            zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, zzjuVar, this.zzajs.zzaou, false, zzjuVar.zzbon.zzbnb);
            if (zzjuVar.zzcig.zzbnp != null && zzjuVar.zzcig.zzbnp.size() > 0) {
                zzkd.zzcv("Pinging urls remotely");
                zzu.zzfq().zza(this.zzajs.zzagf, zzjuVar.zzcig.zzbnp);
            }
        }
        if (zzjuVar.errorCode != 3 || zzjuVar.zzcig == null || zzjuVar.zzcig.zzbno == null) {
            return;
        }
        zzkd.zzcv("Pinging no fill URLs.");
        zzu.zzgf();
        zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, zzjuVar, this.zzajs.zzaou, false, zzjuVar.zzcig.zzbno);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zza
    public final boolean zzc(AdRequestParcel adRequestParcel) {
        return super.zzc(adRequestParcel) && !this.zzaka;
    }

    protected boolean zzdw() {
        zzu.zzfq();
        if (zzkh.zza(this.zzajs.zzagf.getPackageManager(), this.zzajs.zzagf.getPackageName(), "android.permission.INTERNET")) {
            zzu.zzfq();
            if (zzkh.zzac(this.zzajs.zzagf)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzg
    public void zzdy() {
        this.zzaka = true;
        zzdt();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzdz() {
        onAdClicked();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzea() {
        zzdx();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzeb() {
        zzdo();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzec() {
        zzdy();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzed() {
        if (this.zzajs.zzapb != null) {
            String str = this.zzajs.zzapb.zzbop;
            zzkd.zzcx(new StringBuilder(String.valueOf(str).length() + 74).append("Mediation adapter ").append(str).append(" refreshed, but mediation adapters should never refresh.").toString());
        }
        zza(this.zzajs.zzapb, true);
        zzdu();
    }

    @Override // com.google.android.gms.internal.zzgb
    public void zzee() {
        recordImpression();
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public void zzef() {
        zzu.zzfq();
        zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.ads.internal.zzb.2
            @Override // java.lang.Runnable
            public final void run() {
                zzb.this.zzajr.pause();
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.zzs
    public void zzeg() {
        zzu.zzfq();
        zzkh.runOnUiThread(new Runnable() { // from class: com.google.android.gms.ads.internal.zzb.3
            @Override // java.lang.Runnable
            public final void run() {
                zzb.this.zzajr.resume();
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.zza
    public boolean zza(AdRequestParcel adRequestParcel, zzdk zzdkVar) {
        if (!zzdw()) {
            return false;
        }
        Bundle zza = zza(zzu.zzft().zzaa(this.zzajs.zzagf));
        this.zzajr.cancel();
        this.zzajs.zzapw = 0;
        zzjw zzjwVar = null;
        if (((Boolean) zzu.zzfz().zzd(zzdc.zzbct)).booleanValue()) {
            zzjwVar = zzu.zzft().zzst();
            zzu.zzgi().zza(this.zzajs.zzagf, this.zzajs.zzaow, false, zzjwVar, zzjwVar.zzcjf, this.zzajs.zzaou);
        }
        AdRequestInfoParcel.zza zza2 = zza(adRequestParcel, zza, zzjwVar);
        zzdkVar.zzh("seq_num", zza2.zzcau);
        zzdkVar.zzh("request_id", zza2.zzcbg);
        zzdkVar.zzh("session_id", zza2.zzcav);
        if (zza2.zzcas != null) {
            zzdkVar.zzh("app_version", String.valueOf(zza2.zzcas.versionCode));
        }
        this.zzajs.zzaoy = zzu.zzfm().zza(this.zzajs.zzagf, zza2, this.zzajs.zzaov, this);
        return true;
    }

    @Override // com.google.android.gms.ads.internal.zza
    public boolean zza(zzju zzjuVar, zzju zzjuVar2) {
        int i;
        int i2 = 0;
        if (zzjuVar != null && zzjuVar.zzboq != null) {
            zzjuVar.zzboq.zza((zzgb) null);
        }
        if (zzjuVar2.zzboq != null) {
            zzjuVar2.zzboq.zza(this);
        }
        if (zzjuVar2.zzcig != null) {
            i = zzjuVar2.zzcig.zzbny;
            i2 = zzjuVar2.zzcig.zzbnz;
        } else {
            i = 0;
        }
        zzka zzkaVar = this.zzajs.zzapu;
        synchronized (zzkaVar.zzail) {
            zzkaVar.zzckh = i;
            zzkaVar.zzcki = i2;
            zzjx zzjxVar = zzkaVar.zzaob;
            String str = zzkaVar.zzcit;
            synchronized (zzjxVar.zzail) {
                zzjxVar.zzcjq.put(str, zzkaVar);
            }
        }
        return true;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzg
    public void zzdx() {
        this.zzaju.zzi(this.zzajs.zzapb);
        this.zzaka = false;
        zzdr();
        zzjv zzjvVar = this.zzajs.zzapd;
        synchronized (zzjvVar.zzail) {
            if (zzjvVar.zzciz != -1 && !zzjvVar.zzcir.isEmpty()) {
                zzjv.zza last = zzjvVar.zzcir.getLast();
                if (last.zzcjb == -1) {
                    last.zzcjb = SystemClock.elapsedRealtime();
                    zzjvVar.zzaob.zza(zzjvVar);
                }
            }
        }
    }

    private AdRequestInfoParcel.zza zza(AdRequestParcel adRequestParcel, Bundle bundle, zzjw zzjwVar) {
        PackageInfo packageInfo;
        ApplicationInfo applicationInfo = this.zzajs.zzagf.getApplicationInfo();
        try {
            packageInfo = this.zzajs.zzagf.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        DisplayMetrics displayMetrics = this.zzajs.zzagf.getResources().getDisplayMetrics();
        Bundle bundle2 = null;
        if (this.zzajs.zzaox != null && this.zzajs.zzaox.getParent() != null) {
            int[] iArr = new int[2];
            this.zzajs.zzaox.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            int width = this.zzajs.zzaox.getWidth();
            int height = this.zzajs.zzaox.getHeight();
            int i3 = 0;
            if (this.zzajs.zzaox.isShown() && i + width > 0 && i2 + height > 0 && i <= displayMetrics.widthPixels && i2 <= displayMetrics.heightPixels) {
                i3 = 1;
            }
            bundle2 = new Bundle(5);
            bundle2.putInt("x", i);
            bundle2.putInt("y", i2);
            bundle2.putInt("width", width);
            bundle2.putInt("height", height);
            bundle2.putInt("visible", i3);
        }
        String zzsj = zzu.zzft().zzsj();
        this.zzajs.zzapd = new zzjv(zzsj, this.zzajs.zzaou);
        zzjv zzjvVar = this.zzajs.zzapd;
        synchronized (zzjvVar.zzail) {
            zzjvVar.zzciy = SystemClock.elapsedRealtime();
            zzjy zzsk = zzjvVar.zzaob.zzsk();
            long j = zzjvVar.zzciy;
            synchronized (zzsk.zzail) {
                if (zzsk.zzckd == -1) {
                    zzsk.zzckd = j;
                    zzsk.zzckc = zzsk.zzckd;
                } else {
                    zzsk.zzckc = j;
                }
                if (adRequestParcel.extras == null || adRequestParcel.extras.getInt("gw", 2) != 1) {
                    zzsk.zzcke++;
                }
            }
        }
        zzu.zzfq();
        String zza = zzkh.zza(this.zzajs.zzagf, this.zzajs.zzaox, this.zzajs.zzapa);
        long j2 = 0;
        if (this.zzajs.zzaph != null) {
            try {
                j2 = this.zzajs.zzaph.getValue();
            } catch (RemoteException e2) {
                zzkd.zzcx("Cannot get correlation id, default to 0.");
            }
        }
        String uuid = UUID.randomUUID().toString();
        Bundle zza2 = zzu.zzft().zza(this.zzajs.zzagf, this, zzsj);
        ArrayList arrayList = new ArrayList();
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= this.zzajs.zzapn.size()) {
                break;
            }
            arrayList.add(this.zzajs.zzapn.keyAt(i5));
            i4 = i5 + 1;
        }
        boolean z = this.zzajs.zzapi != null;
        boolean z2 = this.zzajs.zzapj != null && zzu.zzft().zzsv();
        boolean zzr = this.zzajv.zzakl.zzr(this.zzajs.zzagf);
        String str = "";
        if (((Boolean) zzu.zzfz().zzd(zzdc.zzbdn)).booleanValue()) {
            zzkd.zzcv("Getting webview cookie from CookieManager.");
            CookieManager zzao = zzu.zzfs().zzao(this.zzajs.zzagf);
            if (zzao != null) {
                str = zzao.getCookie("googleads.g.doubleclick.net");
            }
        }
        String str2 = zzjwVar != null ? zzjwVar.zzcjg : null;
        AdSizeParcel adSizeParcel = this.zzajs.zzapa;
        String str3 = this.zzajs.zzaou;
        String str4 = zzu.zzft().zzcjm;
        VersionInfoParcel versionInfoParcel = this.zzajs.zzaow;
        List<String> list = this.zzajs.zzaps;
        boolean zzsn = zzu.zzft().zzsn();
        Messenger messenger = this.mMessenger;
        int i6 = displayMetrics.widthPixels;
        int i7 = displayMetrics.heightPixels;
        float f = displayMetrics.density;
        List<String> zzjx = zzdc.zzjx();
        String str5 = this.zzajs.zzaot;
        NativeAdOptionsParcel nativeAdOptionsParcel = this.zzajs.zzapo;
        CapabilityParcel capabilityParcel = new CapabilityParcel(z, z2, zzr);
        String zzgt = this.zzajs.zzgt();
        zzu.zzfq();
        float zzey = zzkh.zzey();
        zzu.zzfq();
        boolean zzfa = zzkh.zzfa();
        zzu.zzfq();
        int zzam = zzkh.zzam(this.zzajs.zzagf);
        zzu.zzfq();
        int zzn = zzkh.zzn(this.zzajs.zzaox);
        boolean z3 = this.zzajs.zzagf instanceof Activity;
        boolean zzsr = zzu.zzft().zzsr();
        boolean z4 = zzu.zzft().zzcjz;
        int size = zzu.zzgj().zzbje.size();
        zzu.zzfq();
        return new AdRequestInfoParcel.zza(bundle2, adRequestParcel, adSizeParcel, str3, applicationInfo, packageInfo, zzsj, str4, versionInfoParcel, zza2, list, arrayList, bundle, zzsn, messenger, i6, i7, f, zza, j2, uuid, zzjx, str5, nativeAdOptionsParcel, capabilityParcel, zzgt, zzey, zzfa, zzam, zzn, z3, zzsr, str, str2, z4, size, zzkh.zzti());
    }

    public void zza(zzju zzjuVar, boolean z) {
        if (zzjuVar == null) {
            zzkd.zzcx("Ad state was null when trying to ping impression URLs.");
            return;
        }
        if (zzjuVar == null) {
            zzkd.zzcx("Ad state was null when trying to ping impression URLs.");
        } else {
            zzkd.zzcv("Pinging Impression URLs.");
            zzjv zzjvVar = this.zzajs.zzapd;
            synchronized (zzjvVar.zzail) {
                if (zzjvVar.zzciz != -1 && zzjvVar.zzciv == -1) {
                    zzjvVar.zzciv = SystemClock.elapsedRealtime();
                    zzjvVar.zzaob.zza(zzjvVar);
                }
                zzjy zzsk = zzjvVar.zzaob.zzsk();
                synchronized (zzsk.zzail) {
                    zzsk.zzckg++;
                }
            }
            if (zzjuVar.zzbnn != null && !zzjuVar.zzcin) {
                zzu.zzfq();
                zzkh.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, zzjuVar.zzbnn);
                zzjuVar.zzcin = true;
            }
        }
        if (zzjuVar.zzcig != null && zzjuVar.zzcig.zzbnn != null) {
            zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, zzjuVar, this.zzajs.zzaou, z, zzjuVar.zzcig.zzbnn);
        }
        if (zzjuVar.zzbon == null || zzjuVar.zzbon.zzbna == null) {
            return;
        }
        zzu.zzgf();
        zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, zzjuVar, this.zzajs.zzaou, z, zzjuVar.zzbon.zzbna);
    }
}
