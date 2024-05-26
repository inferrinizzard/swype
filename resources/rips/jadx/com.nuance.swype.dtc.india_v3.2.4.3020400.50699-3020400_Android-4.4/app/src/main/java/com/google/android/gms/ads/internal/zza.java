package com.google.android.gms.ads.internal;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.ThinAdSizeParcel;
import com.google.android.gms.ads.internal.client.VideoOptionsParcel;
import com.google.android.gms.ads.internal.client.zzu;
import com.google.android.gms.ads.internal.client.zzw;
import com.google.android.gms.ads.internal.client.zzy;
import com.google.android.gms.ads.internal.request.zza;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import com.google.android.gms.ads.internal.zzv;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzcd;
import com.google.android.gms.internal.zzcg;
import com.google.android.gms.internal.zzcl;
import com.google.android.gms.internal.zzco;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdi;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzel;
import com.google.android.gms.internal.zzho;
import com.google.android.gms.internal.zzhs;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzjd;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzjv;
import com.google.android.gms.internal.zzjx;
import com.google.android.gms.internal.zzjy;
import com.google.android.gms.internal.zzjz;
import com.google.android.gms.internal.zzka;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@zzin
/* loaded from: classes.dex */
public abstract class zza extends zzu.zza implements com.google.android.gms.ads.internal.client.zza, com.google.android.gms.ads.internal.overlay.zzp, zza.InterfaceC0027zza, zzel, zzic.zza, zzjz {
    protected zzdk zzajn;
    protected zzdi zzajo;
    protected zzdi zzajp;
    protected boolean zzajq = false;
    protected final zzr zzajr = new zzr(this);
    public final zzv zzajs;
    protected transient AdRequestParcel zzajt;
    protected final zzcg zzaju;
    protected final zzd zzajv;

    private static long zzs(String str) {
        int indexOf = str.indexOf("ufe");
        int indexOf2 = str.indexOf(44, indexOf);
        if (indexOf2 == -1) {
            indexOf2 = str.length();
        }
        try {
            return Long.parseLong(str.substring(indexOf + 4, indexOf2));
        } catch (IndexOutOfBoundsException e) {
            zzkd.zzcx("Invalid index for Url fetch time in CSI latency info.");
            return -1L;
        } catch (NumberFormatException e2) {
            zzkd.zzcx("Cannot find valid format of Url fetch time in CSI latency info.");
            return -1L;
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isLoading() {
        return this.zzajq;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean isReady() {
        zzab.zzhi("isLoaded must be called on the main UI thread.");
        return this.zzajs.zzaoy == null && this.zzajs.zzaoz == null && this.zzajs.zzapb != null;
    }

    @Override // com.google.android.gms.internal.zzel
    public void onAppEvent(String str, String str2) {
        if (this.zzajs.zzapg != null) {
            try {
                this.zzajs.zzapg.onAppEvent(str, str2);
            } catch (RemoteException e) {
                zzkd.zzd("Could not call the AppEventListener.", e);
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void pause() {
        zzab.zzhi("pause must be called on the main UI thread.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void resume() {
        zzab.zzhi("resume must be called on the main UI thread.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setManualImpressionsEnabled(boolean z) {
        throw new UnsupportedOperationException("Attempt to call setManualImpressionsEnabled for an unsupported ad type.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void setUserId(String str) {
        zzkd.zzcx("RewardedVideoAd.setUserId() is deprecated. Please do not call this method.");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void stopLoading() {
        zzab.zzhi("stopLoading must be called on the main UI thread.");
        this.zzajq = false;
        this.zzajs.zzi(true);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(AdSizeParcel adSizeParcel) {
        zzab.zzhi("setAdSize must be called on the main UI thread.");
        this.zzajs.zzapa = adSizeParcel;
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzbtm != null && this.zzajs.zzapw == 0) {
            this.zzajs.zzapb.zzbtm.zza(adSizeParcel);
        }
        if (this.zzajs.zzaox == null) {
            return;
        }
        if (this.zzajs.zzaox.getChildCount() > 1) {
            this.zzajs.zzaox.removeView(this.zzajs.zzaox.getNextView());
        }
        this.zzajs.zzaox.setMinimumWidth(adSizeParcel.widthPixels);
        this.zzajs.zzaox.setMinimumHeight(adSizeParcel.heightPixels);
        this.zzajs.zzaox.requestLayout();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(VideoOptionsParcel videoOptionsParcel) {
        zzab.zzhi("setVideoOptions must be called on the main UI thread.");
        this.zzajs.zzapp = videoOptionsParcel;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.client.zzp zzpVar) {
        zzab.zzhi("setAdListener must be called on the main UI thread.");
        this.zzajs.zzape = zzpVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.client.zzq zzqVar) {
        zzab.zzhi("setAdListener must be called on the main UI thread.");
        this.zzajs.zzapf = zzqVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzw zzwVar) {
        zzab.zzhi("setAppEventListener must be called on the main UI thread.");
        this.zzajs.zzapg = zzwVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzy zzyVar) {
        zzab.zzhi("setCorrelationIdProvider must be called on the main UI thread");
        this.zzajs.zzaph = zzyVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(com.google.android.gms.ads.internal.reward.client.zzd zzdVar) {
        zzab.zzhi("setRewardedVideoAdListener can only be called from the UI thread.");
        this.zzajs.zzapr = zzdVar;
    }

    public final void zza(RewardItemParcel rewardItemParcel) {
        if (this.zzajs.zzapr == null) {
            return;
        }
        String str = "";
        int i = 0;
        if (rewardItemParcel != null) {
            try {
                str = rewardItemParcel.type;
                i = rewardItemParcel.zzcid;
            } catch (RemoteException e) {
                zzkd.zzd("Could not call RewardedVideoAdListener.onRewarded().", e);
                return;
            }
        }
        this.zzajs.zzapr.zza(new zzjd(str, i));
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzdo zzdoVar) {
        throw new IllegalStateException("setOnCustomRenderedAdLoadedListener is not supported for current ad type");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzho zzhoVar) {
        throw new IllegalStateException("setInAppPurchaseListener is not supported for current ad type");
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zza(zzhs zzhsVar, String str) {
        throw new IllegalStateException("setPlayStorePurchaseParams is not supported for current ad type");
    }

    public abstract void zza(zzju.zza zzaVar, zzdk zzdkVar);

    @Override // com.google.android.gms.internal.zzjz
    public void zza(HashSet<zzjv> hashSet) {
        this.zzajs.zza(hashSet);
    }

    protected abstract boolean zza(AdRequestParcel adRequestParcel, zzdk zzdkVar);

    boolean zza(zzju zzjuVar) {
        return false;
    }

    public abstract boolean zza(zzju zzjuVar, zzju zzjuVar2);

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zzb(View view) {
        zzv.zza zzaVar = this.zzajs.zzaox;
        if (zzaVar != null) {
            zzaVar.addView(view, zzu.zzfs().zztm());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean zzc(AdRequestParcel adRequestParcel) {
        if (this.zzajs.zzaox == null) {
            return false;
        }
        Object parent = this.zzajs.zzaox.getParent();
        if (!(parent instanceof View)) {
            return false;
        }
        View view = (View) parent;
        zzu.zzfq();
        return zzkh.zza(view, view.getContext());
    }

    public void zzd(AdRequestParcel adRequestParcel) {
        if (zzc(adRequestParcel)) {
            zzb(adRequestParcel);
        } else {
            zzkd.zzcw("Ad is not visible. Not refreshing ad.");
            this.zzajr.zzg(adRequestParcel);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.dynamic.zzd zzdm() {
        zzab.zzhi("getAdFrame must be called on the main UI thread.");
        return com.google.android.gms.dynamic.zze.zzac(this.zzajs.zzaox);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public AdSizeParcel zzdn() {
        zzab.zzhi("getAdSize must be called on the main UI thread.");
        if (this.zzajs.zzapa == null) {
            return null;
        }
        return new ThinAdSizeParcel(this.zzajs.zzapa);
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzp
    public void zzdo() {
        zzds();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void zzdp() {
        zzab.zzhi("recordManualImpression must be called on the main UI thread.");
        if (this.zzajs.zzapb == null) {
            zzkd.zzcx("Ad state was null when trying to ping manual tracking URLs.");
            return;
        }
        zzkd.zzcv("Pinging manual tracking URLs.");
        if (this.zzajs.zzapb.zzcca == null || this.zzajs.zzapb.zzcio) {
            return;
        }
        zzu.zzfq();
        zzkh.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb.zzcca);
        this.zzajs.zzapb.zzcio = true;
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.ads.internal.client.zzab zzdq() {
        return null;
    }

    public void zzdr() {
        zzkd.zzcw("Ad closing.");
        if (this.zzajs.zzapf != null) {
            try {
                this.zzajs.zzapf.onAdClosed();
            } catch (RemoteException e) {
                zzkd.zzd("Could not call AdListener.onAdClosed().", e);
            }
        }
        if (this.zzajs.zzapr != null) {
            try {
                this.zzajs.zzapr.onRewardedVideoAdClosed();
            } catch (RemoteException e2) {
                zzkd.zzd("Could not call RewardedVideoAdListener.onRewardedVideoAdClosed().", e2);
            }
        }
    }

    public final void zzds() {
        zzkd.zzcw("Ad leaving application.");
        if (this.zzajs.zzapf != null) {
            try {
                this.zzajs.zzapf.onAdLeftApplication();
            } catch (RemoteException e) {
                zzkd.zzd("Could not call AdListener.onAdLeftApplication().", e);
            }
        }
        if (this.zzajs.zzapr != null) {
            try {
                this.zzajs.zzapr.onRewardedVideoAdLeftApplication();
            } catch (RemoteException e2) {
                zzkd.zzd("Could not call  RewardedVideoAdListener.onRewardedVideoAdLeftApplication().", e2);
            }
        }
    }

    public final void zzdt() {
        zzkd.zzcw("Ad opening.");
        if (this.zzajs.zzapf != null) {
            try {
                this.zzajs.zzapf.onAdOpened();
            } catch (RemoteException e) {
                zzkd.zzd("Could not call AdListener.onAdOpened().", e);
            }
        }
        if (this.zzajs.zzapr != null) {
            try {
                this.zzajs.zzapr.onRewardedVideoAdOpened();
            } catch (RemoteException e2) {
                zzkd.zzd("Could not call RewardedVideoAdListener.onRewardedVideoAdOpened().", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void zzdu() {
        zzkd.zzcw("Ad finished loading.");
        this.zzajq = false;
        if (this.zzajs.zzapf != null) {
            try {
                this.zzajs.zzapf.onAdLoaded();
            } catch (RemoteException e) {
                zzkd.zzd("Could not call AdListener.onAdLoaded().", e);
            }
        }
        if (this.zzajs.zzapr != null) {
            try {
                this.zzajs.zzapr.onRewardedVideoAdLoaded();
            } catch (RemoteException e2) {
                zzkd.zzd("Could not call RewardedVideoAdListener.onRewardedVideoAdLoaded().", e2);
            }
        }
    }

    public final void zzdv() {
        if (this.zzajs.zzapr == null) {
            return;
        }
        try {
            this.zzajs.zzapr.onRewardedVideoStarted();
        } catch (RemoteException e) {
            zzkd.zzd("Could not call RewardedVideoAdListener.onVideoStarted().", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zzh(int i) {
        zzkd.zzcx(new StringBuilder(30).append("Failed to load ad: ").append(i).toString());
        this.zzajq = false;
        if (this.zzajs.zzapf != null) {
            try {
                this.zzajs.zzapf.onAdFailedToLoad(i);
            } catch (RemoteException e) {
                zzkd.zzd("Could not call AdListener.onAdFailedToLoad().", e);
            }
        }
        if (this.zzajs.zzapr != null) {
            try {
                this.zzajs.zzapr.onRewardedVideoAdFailedToLoad(i);
            } catch (RemoteException e2) {
                zzkd.zzd("Could not call RewardedVideoAdListener.onRewardedVideoAdFailedToLoad().", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zza(zzv zzvVar, zzd zzdVar) {
        this.zzajs = zzvVar;
        this.zzajv = zzdVar;
        zzkh zzfq = zzu.zzfq();
        Context context = this.zzajs.zzagf;
        if (!zzfq.zzcle) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            context.getApplicationContext().registerReceiver(new zzkh.zza(zzfq, (byte) 0), intentFilter);
            zzfq.zzcle = true;
        }
        zzu.zzft().zzb(this.zzajs.zzagf, this.zzajs.zzaow);
        this.zzaju = zzu.zzft().zzaju;
        if (((Boolean) zzu.zzfz().zzd(zzdc.zzbcj)).booleanValue()) {
            final Timer timer = new Timer();
            final CountDownLatch countDownLatch = new CountDownLatch(((Integer) zzu.zzfz().zzd(zzdc.zzbcl)).intValue());
            timer.schedule(new TimerTask() { // from class: com.google.android.gms.ads.internal.zza.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public final void run() {
                    if (((Integer) zzu.zzfz().zzd(zzdc.zzbcl)).intValue() != countDownLatch.getCount()) {
                        zzkd.zzcv("Stopping method tracing");
                        Debug.stopMethodTracing();
                        if (countDownLatch.getCount() == 0) {
                            timer.cancel();
                            return;
                        }
                    }
                    String concat = String.valueOf(zza.this.zzajs.zzagf.getPackageName()).concat("_adsTrace_");
                    try {
                        zzkd.zzcv("Starting method tracing");
                        countDownLatch.countDown();
                        Debug.startMethodTracing(new StringBuilder(String.valueOf(concat).length() + 20).append(concat).append(zzu.zzfu().currentTimeMillis()).toString(), ((Integer) zzu.zzfz().zzd(zzdc.zzbcm)).intValue());
                    } catch (Exception e) {
                        zzkd.zzd("Exception occurred while starting method tracing.", e);
                    }
                }
            }, 0L, ((Long) zzu.zzfz().zzd(zzdc.zzbck)).longValue());
        }
    }

    public void zzdl() {
        this.zzajn = new zzdk(((Boolean) zzu.zzfz().zzd(zzdc.zzaze)).booleanValue(), "load_ad", this.zzajs.zzapa.zzaur);
        this.zzajo = new zzdi(-1L, null, null);
        this.zzajp = new zzdi(-1L, null, null);
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public void destroy() {
        zzab.zzhi("destroy must be called on the main UI thread.");
        this.zzajr.cancel();
        zzcg zzcgVar = this.zzaju;
        zzju zzjuVar = this.zzajs.zzapb;
        synchronized (zzcgVar.zzail) {
            zzcd zzcdVar = zzcgVar.zzarm.get(zzjuVar);
            if (zzcdVar != null) {
                zzcdVar.stop();
            }
        }
        this.zzajs.destroy();
    }

    @Override // com.google.android.gms.ads.internal.client.zzu
    public boolean zzb(AdRequestParcel adRequestParcel) {
        zzab.zzhi("loadAd must be called on the main UI thread.");
        if (com.google.android.gms.common.util.zzi.zzcl(this.zzajs.zzagf) && adRequestParcel.zzatu != null) {
            adRequestParcel = new com.google.android.gms.ads.internal.client.zzf(adRequestParcel).zza(null).zzig();
        }
        if (this.zzajs.zzaoy != null || this.zzajs.zzaoz != null) {
            if (this.zzajt != null) {
                zzkd.zzcx("Aborting last ad request since another ad request is already in progress. The current request object will still be cached for future refreshes.");
            } else {
                zzkd.zzcx("Loading already in progress, saving this object for future refreshes.");
            }
            this.zzajt = adRequestParcel;
            return false;
        }
        zzkd.zzcw("Starting ad request.");
        zzdl();
        this.zzajo = this.zzajn.zzkg();
        if (!adRequestParcel.zzatp) {
            String valueOf = String.valueOf(com.google.android.gms.ads.internal.client.zzm.zziw().zzaq(this.zzajs.zzagf));
            zzkd.zzcw(new StringBuilder(String.valueOf(valueOf).length() + 71).append("Use AdRequest.Builder.addTestDevice(\"").append(valueOf).append("\") to get test ads on this device.").toString());
        }
        this.zzajq = zza(adRequestParcel, this.zzajn);
        return this.zzajq;
    }

    @Override // com.google.android.gms.ads.internal.request.zza.InterfaceC0027zza
    public void zza(zzju.zza zzaVar) {
        if (zzaVar.zzciq.zzccc != -1 && !TextUtils.isEmpty(zzaVar.zzciq.zzccl)) {
            long zzs = zzs(zzaVar.zzciq.zzccl);
            if (zzs != -1) {
                this.zzajn.zza(this.zzajn.zzc(zzs + zzaVar.zzciq.zzccc), "stc");
            }
        }
        zzdk zzdkVar = this.zzajn;
        String str = zzaVar.zzciq.zzccl;
        if (zzdkVar.zzbdo) {
            synchronized (zzdkVar.zzail) {
                zzdkVar.zzbeh = str;
            }
        }
        this.zzajn.zza(this.zzajo, "arf");
        this.zzajp = this.zzajn.zzkg();
        this.zzajn.zzh("gqi", zzaVar.zzciq.zzccm);
        this.zzajs.zzaoy = null;
        this.zzajs.zzapc = zzaVar;
        zza(zzaVar, this.zzajn);
    }

    @Override // com.google.android.gms.internal.zzic.zza
    public void zzb(zzju zzjuVar) {
        this.zzajn.zza(this.zzajp, "awr");
        this.zzajs.zzaoz = null;
        if (zzjuVar.errorCode != -2 && zzjuVar.errorCode != 3) {
            zzjx zzft = zzu.zzft();
            HashSet<zzjv> zzgl = this.zzajs.zzgl();
            synchronized (zzft.zzail) {
                zzft.zzcjp.addAll(zzgl);
            }
        }
        if (zzjuVar.errorCode == -1) {
            this.zzajq = false;
            return;
        }
        if (zza(zzjuVar)) {
            zzkd.zzcv("Ad refresh scheduled.");
        }
        if (zzjuVar.errorCode != -2) {
            zzh(zzjuVar.errorCode);
            return;
        }
        if (this.zzajs.zzapu == null) {
            this.zzajs.zzapu = new zzka(this.zzajs.zzaou);
        }
        this.zzaju.zzi(this.zzajs.zzapb);
        if (zza(this.zzajs.zzapb, zzjuVar)) {
            this.zzajs.zzapb = zzjuVar;
            this.zzajs.zzgu();
            this.zzajn.zzh("is_mraid", this.zzajs.zzapb.zzho() ? "1" : "0");
            this.zzajn.zzh("is_mediation", this.zzajs.zzapb.zzcby ? "1" : "0");
            if (this.zzajs.zzapb.zzbtm != null && this.zzajs.zzapb.zzbtm.zzuj() != null) {
                this.zzajn.zzh("is_delay_pl", this.zzajs.zzapb.zzbtm.zzuj().zzuy() ? "1" : "0");
            }
            this.zzajn.zza(this.zzajo, "ttc");
            if (zzu.zzft().zzsl() != null) {
                zzu.zzft().zzsl().zza(this.zzajn);
            }
            if (this.zzajs.zzgp()) {
                zzdu();
            }
        }
        if (zzjuVar.zzbnp != null) {
            zzu.zzfq().zza(this.zzajs.zzagf, zzjuVar.zzbnp);
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zza
    public void onAdClicked() {
        if (this.zzajs.zzapb == null) {
            zzkd.zzcx("Ad state was null when trying to ping click URLs.");
            return;
        }
        zzkd.zzcv("Pinging click URLs.");
        zzjv zzjvVar = this.zzajs.zzapd;
        synchronized (zzjvVar.zzail) {
            if (zzjvVar.zzciz != -1) {
                zzjv.zza zzaVar = new zzjv.zza();
                zzaVar.zzcja = SystemClock.elapsedRealtime();
                zzjvVar.zzcir.add(zzaVar);
                zzjvVar.zzcix++;
                zzjy zzsk = zzjvVar.zzaob.zzsk();
                synchronized (zzsk.zzail) {
                    zzsk.zzckf++;
                }
                zzjvVar.zzaob.zza(zzjvVar);
            }
        }
        if (this.zzajs.zzapb.zzbnm != null) {
            zzu.zzfq();
            zzkh.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb.zzbnm);
        }
        if (this.zzajs.zzape != null) {
            try {
                this.zzajs.zzape.onAdClicked();
            } catch (RemoteException e) {
                zzkd.zzd("Could not notify onAdClicked event.", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle zza(zzco zzcoVar) {
        String str;
        String zzsp;
        if (zzcoVar == null) {
            return null;
        }
        if (zzcoVar.zzasj) {
            synchronized (zzcoVar.zzail) {
                zzcoVar.zzasj = false;
                zzcoVar.zzail.notifyAll();
                zzkd.zzcv("ContentFetchThread: wakeup");
            }
        }
        zzcl zzhy = zzcoVar.zzasl.zzhy();
        if (zzhy != null) {
            zzsp = zzhy.zzasf;
            str = zzhy.zzasg;
            String valueOf = String.valueOf(zzhy.toString());
            zzkd.zzcv(valueOf.length() != 0 ? "In AdManager: loadAd, ".concat(valueOf) : new String("In AdManager: loadAd, "));
            if (zzsp != null) {
                zzu.zzft().zzcm(zzsp);
            }
        } else {
            str = null;
            zzsp = zzu.zzft().zzsp();
        }
        if (zzsp == null) {
            return null;
        }
        Bundle bundle = new Bundle(1);
        bundle.putString("fingerprint", zzsp);
        if (zzsp.equals(str)) {
            return bundle;
        }
        bundle.putString("v_fp", str);
        return bundle;
    }
}
