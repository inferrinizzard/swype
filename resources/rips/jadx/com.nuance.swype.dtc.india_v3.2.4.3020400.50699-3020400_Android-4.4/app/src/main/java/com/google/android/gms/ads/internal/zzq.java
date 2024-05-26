package com.google.android.gms.ads.internal;

import android.content.Context;
import android.os.RemoteException;
import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzcd;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzeb;
import com.google.android.gms.internal.zzec;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzgn;
import com.google.android.gms.internal.zzgo;
import com.google.android.gms.internal.zzho;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zzq extends zzb {
    public zzq(Context context, zzd zzdVar, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel) {
        super(context, adSizeParcel, str, zzgjVar, versionInfoParcel, zzdVar);
    }

    private void zza(final com.google.android.gms.ads.internal.formats.zzd zzdVar) {
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzq.2
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    if (zzq.this.zzajs.zzapk != null) {
                        zzq.this.zzajs.zzapk.zza(zzdVar);
                    }
                } catch (RemoteException e) {
                    zzkd.zzd("Could not call OnAppInstallAdLoadedListener.onAppInstallAdLoaded().", e);
                }
            }
        });
    }

    private void zza(final com.google.android.gms.ads.internal.formats.zze zzeVar) {
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzq.3
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    if (zzq.this.zzajs.zzapl != null) {
                        zzq.this.zzajs.zzapl.zza(zzeVar);
                    }
                } catch (RemoteException e) {
                    zzkd.zzd("Could not call OnContentAdLoadedListener.onContentAdLoaded().", e);
                }
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void pause() {
        throw new IllegalStateException("Native Ad DOES NOT support pause().");
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void resume() {
        throw new IllegalStateException("Native Ad DOES NOT support resume().");
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.client.zzu
    public void showInterstitial() {
        throw new IllegalStateException("Interstitial is NOT supported by NativeAdManager.");
    }

    public void zza(SimpleArrayMap<String, zzee> simpleArrayMap) {
        zzab.zzhi("setOnCustomTemplateAdLoadedListeners must be called on the main UI thread.");
        this.zzajs.zzapn = simpleArrayMap;
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void zza(zzdo zzdoVar) {
        throw new IllegalStateException("CustomRendering is NOT supported by NativeAdManager.");
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void zza(zzho zzhoVar) {
        throw new IllegalStateException("In App Purchase is NOT supported by NativeAdManager.");
    }

    @Override // com.google.android.gms.ads.internal.zza
    public void zza(final zzju.zza zzaVar, zzdk zzdkVar) {
        if (zzaVar.zzapa != null) {
            this.zzajs.zzapa = zzaVar.zzapa;
        }
        if (zzaVar.errorCode != -2) {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzq.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzq.this.zzb(new zzju(zzaVar));
                }
            });
            return;
        }
        this.zzajs.zzapw = 0;
        zzv zzvVar = this.zzajs;
        zzu.zzfp();
        zzvVar.zzaoz = zzic.zza(this.zzajs.zzagf, this, zzaVar, this.zzajs.zzaov, null, this.zzajz, this, zzdkVar);
        String valueOf = String.valueOf(this.zzajs.zzaoz.getClass().getName());
        zzkd.zzcv(valueOf.length() != 0 ? "AdRenderer: ".concat(valueOf) : new String("AdRenderer: "));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzb
    public final boolean zza(AdRequestParcel adRequestParcel, zzju zzjuVar, boolean z) {
        return this.zzajr.zzfc();
    }

    public void zzb(SimpleArrayMap<String, zzed> simpleArrayMap) {
        zzab.zzhi("setOnCustomClickListener must be called on the main UI thread.");
        this.zzajs.zzapm = simpleArrayMap;
    }

    public void zzb(NativeAdOptionsParcel nativeAdOptionsParcel) {
        zzab.zzhi("setNativeAdOptions must be called on the main UI thread.");
        this.zzajs.zzapo = nativeAdOptionsParcel;
    }

    public void zzb(zzeb zzebVar) {
        zzab.zzhi("setOnAppInstallAdLoadedListener must be called on the main UI thread.");
        this.zzajs.zzapk = zzebVar;
    }

    public void zzb(zzec zzecVar) {
        zzab.zzhi("setOnContentAdLoadedListener must be called on the main UI thread.");
        this.zzajs.zzapl = zzecVar;
    }

    public void zzb(List<String> list) {
        zzab.zzhi("setNativeTemplates must be called on the main UI thread.");
        this.zzajs.zzaps = list;
    }

    public SimpleArrayMap<String, zzee> zzfb() {
        zzab.zzhi("getOnCustomTemplateAdLoadedListeners must be called on the main UI thread.");
        return this.zzajs.zzapn;
    }

    public zzed zzv(String str) {
        zzab.zzhi("getOnCustomClickListener must be called on the main UI thread.");
        return this.zzajs.zzapm.get(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza
    public boolean zza(zzju zzjuVar, final zzju zzjuVar2) {
        zzb((List<String>) null);
        if (!this.zzajs.zzgp()) {
            throw new IllegalStateException("Native ad DOES NOT have custom rendering mode.");
        }
        if (zzjuVar2.zzcby) {
            try {
                zzgn zzmo = zzjuVar2.zzboo != null ? zzjuVar2.zzboo.zzmo() : null;
                zzgo zzmp = zzjuVar2.zzboo != null ? zzjuVar2.zzboo.zzmp() : null;
                if (zzmo != null && this.zzajs.zzapk != null) {
                    com.google.android.gms.ads.internal.formats.zzd zzdVar = new com.google.android.gms.ads.internal.formats.zzd(zzmo.getHeadline(), zzmo.getImages(), zzmo.getBody(), zzmo.zzku() != null ? zzmo.zzku() : null, zzmo.getCallToAction(), zzmo.getStarRating(), zzmo.getStore(), zzmo.getPrice(), null, zzmo.getExtras());
                    zzdVar.zzb(new com.google.android.gms.ads.internal.formats.zzg(this.zzajs.zzagf, this, this.zzajs.zzaov, zzmo));
                    zza(zzdVar);
                } else {
                    if (zzmp == null || this.zzajs.zzapl == null) {
                        zzkd.zzcx("No matching mapper/listener for retrieved native ad template.");
                        zzh(0);
                        return false;
                    }
                    com.google.android.gms.ads.internal.formats.zze zzeVar = new com.google.android.gms.ads.internal.formats.zze(zzmp.getHeadline(), zzmp.getImages(), zzmp.getBody(), zzmp.zzky() != null ? zzmp.zzky() : null, zzmp.getCallToAction(), zzmp.getAdvertiser(), null, zzmp.getExtras());
                    zzeVar.zzb(new com.google.android.gms.ads.internal.formats.zzg(this.zzajs.zzagf, this, this.zzajs.zzaov, zzmp));
                    zza(zzeVar);
                }
            } catch (RemoteException e) {
                zzkd.zzd("Failed to get native ad mapper", e);
            }
        } else {
            zzh.zza zzaVar = zzjuVar2.zzcim;
            if ((zzaVar instanceof com.google.android.gms.ads.internal.formats.zze) && this.zzajs.zzapl != null) {
                zza((com.google.android.gms.ads.internal.formats.zze) zzjuVar2.zzcim);
            } else if ((zzaVar instanceof com.google.android.gms.ads.internal.formats.zzd) && this.zzajs.zzapk != null) {
                zza((com.google.android.gms.ads.internal.formats.zzd) zzjuVar2.zzcim);
            } else {
                if (!(zzaVar instanceof com.google.android.gms.ads.internal.formats.zzf) || this.zzajs.zzapn == null || this.zzajs.zzapn.get(((com.google.android.gms.ads.internal.formats.zzf) zzaVar).getCustomTemplateId()) == null) {
                    zzkd.zzcx("No matching listener for retrieved native ad template.");
                    zzh(0);
                    return false;
                }
                final String customTemplateId = ((com.google.android.gms.ads.internal.formats.zzf) zzaVar).getCustomTemplateId();
                zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzq.4
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            zzq.this.zzajs.zzapn.get(customTemplateId).zza((com.google.android.gms.ads.internal.formats.zzf) zzjuVar2.zzcim);
                        } catch (RemoteException e2) {
                            zzkd.zzd("Could not call onCustomTemplateAdLoadedListener.onCustomTemplateAdLoaded().", e2);
                        }
                    }
                });
            }
        }
        return super.zza(zzjuVar, zzjuVar2);
    }

    public void zza(com.google.android.gms.ads.internal.formats.zzh zzhVar) {
        if (this.zzajs.zzapb.zzcie != null) {
            zzu.zzft().zzaju.zza(this.zzajs.zzapa, this.zzajs.zzapb, new zzcd.zza(zzhVar), null);
        }
    }
}
