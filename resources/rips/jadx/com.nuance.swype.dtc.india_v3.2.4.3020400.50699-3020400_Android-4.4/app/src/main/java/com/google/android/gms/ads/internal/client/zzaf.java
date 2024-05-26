package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.Correlator;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.OnCustomRenderedAdLoadedListener;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.internal.zzdp;
import com.google.android.gms.internal.zzgi;
import com.google.android.gms.internal.zzht;
import com.google.android.gms.internal.zzhx;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzaf {
    private final Context mContext;
    private final zzh zzahz;
    private String zzaln;
    private zza zzatk;
    private AdListener zzatl;
    private AppEventListener zzaux;
    private final zzgi zzawb;
    private Correlator zzawf;
    private zzu zzawg;
    private InAppPurchaseListener zzawh;
    private OnCustomRenderedAdLoadedListener zzawi;
    private PlayStorePurchaseListener zzawj;
    private String zzawl;
    private PublisherInterstitialAd zzawp;
    private boolean zzawq;
    private RewardedVideoAdListener zzfh;

    public zzaf(Context context) {
        this(context, zzh.zzih(), null);
    }

    public zzaf(Context context, PublisherInterstitialAd publisherInterstitialAd) {
        this(context, zzh.zzih(), publisherInterstitialAd);
    }

    public zzaf(Context context, zzh zzhVar, PublisherInterstitialAd publisherInterstitialAd) {
        this.zzawb = new zzgi();
        this.mContext = context;
        this.zzahz = zzhVar;
        this.zzawp = publisherInterstitialAd;
    }

    private void zzan(String str) {
        if (this.zzawg == null) {
            throw new IllegalStateException(new StringBuilder(String.valueOf(str).length() + 63).append("The ad unit ID must be set on InterstitialAd before ").append(str).append(" is called.").toString());
        }
    }

    public AdListener getAdListener() {
        return this.zzatl;
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

    public boolean isLoaded() {
        try {
            if (this.zzawg == null) {
                return false;
            }
            return this.zzawg.isReady();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to check if ad is ready.", e);
            return false;
        }
    }

    public boolean isLoading() {
        try {
            if (this.zzawg == null) {
                return false;
            }
            return this.zzawg.isLoading();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to check if ad is loading.", e);
            return false;
        }
    }

    public void setAdListener(AdListener adListener) {
        try {
            this.zzatl = adListener;
            if (this.zzawg != null) {
                this.zzawg.zza(adListener != null ? new zzc(adListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the AdListener.", e);
        }
    }

    public void setAdUnitId(String str) {
        if (this.zzaln != null) {
            throw new IllegalStateException("The ad unit ID can only be set once on InterstitialAd.");
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

    public void setOnCustomRenderedAdLoadedListener(OnCustomRenderedAdLoadedListener onCustomRenderedAdLoadedListener) {
        try {
            this.zzawi = onCustomRenderedAdLoadedListener;
            if (this.zzawg != null) {
                this.zzawg.zza(onCustomRenderedAdLoadedListener != null ? new zzdp(onCustomRenderedAdLoadedListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the OnCustomRenderedAdLoadedListener.", e);
        }
    }

    public void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String str) {
        if (this.zzawh != null) {
            throw new IllegalStateException("In app purchase parameter has already been set.");
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

    public void setRewardedVideoAdListener(RewardedVideoAdListener rewardedVideoAdListener) {
        try {
            this.zzfh = rewardedVideoAdListener;
            if (this.zzawg != null) {
                this.zzawg.zza(rewardedVideoAdListener != null ? new com.google.android.gms.ads.internal.reward.client.zzg(rewardedVideoAdListener) : null);
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to set the AdListener.", e);
        }
    }

    public void show() {
        try {
            zzan("show");
            this.zzawg.showInterstitial();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to show interstitial.", e);
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

    public void zzd(boolean z) {
        this.zzawq = z;
    }

    public void zza(zzad zzadVar) {
        try {
            if (this.zzawg == null) {
                if (this.zzaln == null) {
                    zzan("loadAd");
                }
                this.zzawg = zzm.zzix().zzb(this.mContext, this.zzawq ? AdSizeParcel.zzii() : new AdSizeParcel(), this.zzaln, this.zzawb);
                if (this.zzatl != null) {
                    this.zzawg.zza(new zzc(this.zzatl));
                }
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
                if (this.zzfh != null) {
                    this.zzawg.zza(new com.google.android.gms.ads.internal.reward.client.zzg(this.zzfh));
                }
            }
            if (this.zzawg.zzb(this.zzahz.zza(this.mContext, zzadVar))) {
                this.zzawb.zzbpg = zzadVar.zzjg();
            }
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Failed to load ad.", e);
        }
    }
}
