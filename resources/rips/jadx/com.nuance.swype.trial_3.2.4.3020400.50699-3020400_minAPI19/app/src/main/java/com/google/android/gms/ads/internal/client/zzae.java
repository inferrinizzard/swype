package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.Correlator;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.OnCustomRenderedAdLoadedListener;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import com.google.android.gms.internal.zzdp;
import com.google.android.gms.internal.zzgi;
import com.google.android.gms.internal.zzht;
import com.google.android.gms.internal.zzhx;
import com.google.android.gms.internal.zzin;
import java.util.concurrent.atomic.AtomicBoolean;

@zzin
/* loaded from: classes.dex */
public class zzae {
    private final zzh zzahz;
    private boolean zzakp;
    private String zzaln;
    private zza zzatk;
    private AdListener zzatl;
    private AppEventListener zzaux;
    private AdSize[] zzauy;
    private final zzgi zzawb;
    private final AtomicBoolean zzawc;
    private final VideoController zzawd;
    final zzo zzawe;
    private Correlator zzawf;
    private zzu zzawg;
    private InAppPurchaseListener zzawh;
    private OnCustomRenderedAdLoadedListener zzawi;
    private PlayStorePurchaseListener zzawj;
    private VideoOptions zzawk;
    private String zzawl;
    private ViewGroup zzawm;
    private boolean zzawn;

    public zzae(ViewGroup viewGroup) {
        this(viewGroup, null, false, zzh.zzih(), false, (byte) 0);
    }

    public zzae(ViewGroup viewGroup, AttributeSet attributeSet, boolean z) {
        this(viewGroup, attributeSet, z, zzh.zzih(), false, (byte) 0);
    }

    private zzae(ViewGroup viewGroup, AttributeSet attributeSet, boolean z, zzh zzhVar, boolean z2, byte b) {
        this(viewGroup, attributeSet, z, zzhVar, z2);
    }

    public zzae(ViewGroup viewGroup, AttributeSet attributeSet, boolean z, boolean z2) {
        this(viewGroup, attributeSet, z, zzh.zzih(), z2, (byte) 0);
    }

    public zzae(ViewGroup viewGroup, boolean z) {
        this(viewGroup, null, false, zzh.zzih(), z, (byte) 0);
    }

    private static AdSizeParcel zza(Context context, AdSize[] adSizeArr, boolean z) {
        AdSizeParcel adSizeParcel = new AdSizeParcel(context, adSizeArr);
        adSizeParcel.zzk(z);
        return adSizeParcel;
    }

    public void destroy() {
        try {
            if (this.zzawg != null) {
                this.zzawg.destroy();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to destroy AdView.", e);
        }
    }

    public AdListener getAdListener() {
        return this.zzatl;
    }

    public AdSize getAdSize() {
        AdSizeParcel zzdn;
        try {
            if (this.zzawg != null && (zzdn = this.zzawg.zzdn()) != null) {
                return zzdn.zzij();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to get the current AdSize.", e);
        }
        if (this.zzauy != null) {
            return this.zzauy[0];
        }
        return null;
    }

    public AdSize[] getAdSizes() {
        return this.zzauy;
    }

    public String getAdUnitId() {
        return this.zzaln;
    }

    public AppEventListener getAppEventListener() {
        return this.zzaux;
    }

    public InAppPurchaseListener getInAppPurchaseListener() {
        return this.zzawh;
    }

    public String getMediationAdapterClassName() {
        try {
            if (this.zzawg != null) {
                return this.zzawg.getMediationAdapterClassName();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to get the mediation adapter class name.", e);
        }
        return null;
    }

    public OnCustomRenderedAdLoadedListener getOnCustomRenderedAdLoadedListener() {
        return this.zzawi;
    }

    public VideoController getVideoController() {
        return this.zzawd;
    }

    public VideoOptions getVideoOptions() {
        return this.zzawk;
    }

    public boolean isLoading() {
        try {
            if (this.zzawg != null) {
                return this.zzawg.isLoading();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to check if ad is loading.", e);
        }
        return false;
    }

    public void pause() {
        try {
            if (this.zzawg != null) {
                this.zzawg.pause();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to call pause.", e);
        }
    }

    public void recordManualImpression() {
        if (this.zzawc.getAndSet(true)) {
            return;
        }
        try {
            if (this.zzawg != null) {
                this.zzawg.zzdp();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to record impression.", e);
        }
    }

    public void resume() {
        try {
            if (this.zzawg != null) {
                this.zzawg.resume();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to call resume.", e);
        }
    }

    public void setAdListener(AdListener adListener) {
        this.zzatl = adListener;
        this.zzawe.zza(adListener);
    }

    public void setAdSizes(AdSize... adSizeArr) {
        if (this.zzauy != null) {
            throw new IllegalStateException("The ad size can only be set once on AdView.");
        }
        zza(adSizeArr);
    }

    public void setAdUnitId(String str) {
        if (this.zzaln != null) {
            throw new IllegalStateException("The ad unit ID can only be set once on AdView.");
        }
        this.zzaln = str;
    }

    public void setAppEventListener(AppEventListener appEventListener) {
        try {
            this.zzaux = appEventListener;
            if (this.zzawg != null) {
                this.zzawg.zza(appEventListener != null ? new zzj(appEventListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the AppEventListener.", e);
        }
    }

    public void setCorrelator(Correlator correlator) {
        this.zzawf = correlator;
        try {
            if (this.zzawg != null) {
                this.zzawg.zza(this.zzawf == null ? null : this.zzawf.zzdd());
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set correlator.", e);
        }
    }

    public void setInAppPurchaseListener(InAppPurchaseListener inAppPurchaseListener) {
        if (this.zzawj != null) {
            throw new IllegalStateException("Play store purchase parameter has already been set.");
        }
        try {
            this.zzawh = inAppPurchaseListener;
            if (this.zzawg != null) {
                this.zzawg.zza(inAppPurchaseListener != null ? new zzht(inAppPurchaseListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the InAppPurchaseListener.", e);
        }
    }

    public void setManualImpressionsEnabled(boolean z) {
        this.zzakp = z;
        try {
            if (this.zzawg != null) {
                this.zzawg.setManualImpressionsEnabled(this.zzakp);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set manual impressions.", e);
        }
    }

    public void setOnCustomRenderedAdLoadedListener(OnCustomRenderedAdLoadedListener onCustomRenderedAdLoadedListener) {
        this.zzawi = onCustomRenderedAdLoadedListener;
        try {
            if (this.zzawg != null) {
                this.zzawg.zza(onCustomRenderedAdLoadedListener != null ? new zzdp(onCustomRenderedAdLoadedListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the onCustomRenderedAdLoadedListener.", e);
        }
    }

    public void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String str) {
        if (this.zzawh != null) {
            throw new IllegalStateException("InAppPurchaseListener has already been set.");
        }
        try {
            this.zzawj = playStorePurchaseListener;
            this.zzawl = str;
            if (this.zzawg != null) {
                this.zzawg.zza(playStorePurchaseListener != null ? new zzhx(playStorePurchaseListener) : null, str);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the play store purchase parameter.", e);
        }
    }

    public void setVideoOptions(VideoOptions videoOptions) {
        this.zzawk = videoOptions;
        try {
            if (this.zzawg != null) {
                this.zzawg.zza(videoOptions == null ? null : new VideoOptionsParcel(videoOptions));
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set video options.", e);
        }
    }

    public void zza(zza zzaVar) {
        try {
            this.zzatk = zzaVar;
            if (this.zzawg != null) {
                this.zzawg.zza(zzaVar != null ? new zzb(zzaVar) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the AdClickListener.", e);
        }
    }

    public void zza(AdSize... adSizeArr) {
        this.zzauy = adSizeArr;
        try {
            if (this.zzawg != null) {
                this.zzawg.zza(zza(this.zzawm.getContext(), this.zzauy, this.zzawn));
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the ad size.", e);
        }
        this.zzawm.requestLayout();
    }

    public boolean zzb(AdSizeParcel adSizeParcel) {
        return "search_v2".equals(adSizeParcel.zzaur);
    }

    public zzab zzjk() {
        if (this.zzawg == null) {
            return null;
        }
        try {
            return this.zzawg.zzdq();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to retrieve VideoController.", e);
            return null;
        }
    }

    private zzae(ViewGroup viewGroup, AttributeSet attributeSet, boolean z, zzh zzhVar, boolean z2) {
        this.zzawb = new zzgi();
        this.zzawd = new VideoController();
        this.zzawe = new zzo() { // from class: com.google.android.gms.ads.internal.client.zzae.1
            @Override // com.google.android.gms.ads.internal.client.zzo, com.google.android.gms.ads.AdListener
            public final void onAdFailedToLoad(int i) {
                zzae.this.zzawd.zza(zzae.this.zzjk());
                super.onAdFailedToLoad(i);
            }

            @Override // com.google.android.gms.ads.internal.client.zzo, com.google.android.gms.ads.AdListener
            public final void onAdLoaded() {
                zzae.this.zzawd.zza(zzae.this.zzjk());
                super.onAdLoaded();
            }
        };
        this.zzawm = viewGroup;
        this.zzahz = zzhVar;
        this.zzawg = null;
        this.zzawc = new AtomicBoolean(false);
        this.zzawn = z2;
        if (attributeSet != null) {
            Context context = viewGroup.getContext();
            try {
                zzk zzkVar = new zzk(context, attributeSet);
                this.zzauy = zzkVar.zzl(z);
                this.zzaln = zzkVar.getAdUnitId();
                if (viewGroup.isInEditMode()) {
                    com.google.android.gms.ads.internal.util.client.zza zziw = zzm.zziw();
                    AdSize adSize = this.zzauy[0];
                    boolean z3 = this.zzawn;
                    AdSizeParcel adSizeParcel = new AdSizeParcel(context, adSize);
                    adSizeParcel.zzk(z3);
                    zziw.zza(viewGroup, adSizeParcel, "Ads by Google");
                }
            } catch (IllegalArgumentException e) {
                zzm.zziw().zza(viewGroup, new AdSizeParcel(context, AdSize.BANNER), e.getMessage(), e.getMessage());
            }
        }
    }

    public void zza(zzad zzadVar) {
        try {
            if (this.zzawg == null) {
                if ((this.zzauy == null || this.zzaln == null) && this.zzawg == null) {
                    throw new IllegalStateException("The ad size and ad unit ID must be set before loadAd is called.");
                }
                Context context = this.zzawm.getContext();
                AdSizeParcel zza = zza(context, this.zzauy, this.zzawn);
                this.zzawg = zzb(zza) ? zzm.zzix().zza(context, zza, this.zzaln) : zzm.zzix().zza(context, zza, this.zzaln, this.zzawb);
                this.zzawg.zza(new zzc(this.zzawe));
                if (this.zzatk != null) {
                    this.zzawg.zza(new zzb(this.zzatk));
                }
                if (this.zzaux != null) {
                    this.zzawg.zza(new zzj(this.zzaux));
                }
                if (this.zzawh != null) {
                    this.zzawg.zza(new zzht(this.zzawh));
                }
                if (this.zzawj != null) {
                    this.zzawg.zza(new zzhx(this.zzawj), this.zzawl);
                }
                if (this.zzawi != null) {
                    this.zzawg.zza(new zzdp(this.zzawi));
                }
                if (this.zzawf != null) {
                    this.zzawg.zza(this.zzawf.zzdd());
                }
                if (this.zzawk != null) {
                    this.zzawg.zza(new VideoOptionsParcel(this.zzawk));
                }
                this.zzawg.setManualImpressionsEnabled(this.zzakp);
                try {
                    com.google.android.gms.dynamic.zzd zzdm = this.zzawg.zzdm();
                    if (zzdm != null) {
                        this.zzawm.addView((View) com.google.android.gms.dynamic.zze.zzad(zzdm));
                    }
                } catch (RemoteException e) {
                    com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to get an ad frame.", e);
                }
            }
            if (this.zzawg.zzb(this.zzahz.zza(this.zzawm.getContext(), zzadVar))) {
                this.zzawb.zzbpg = zzadVar.zzjg();
            }
        } catch (RemoteException e2) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to load ad.", e2);
        }
    }
}
