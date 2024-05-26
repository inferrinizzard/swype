package com.google.android.gms.ads;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.internal.client.zzad;
import com.google.android.gms.ads.internal.client.zzc;
import com.google.android.gms.ads.internal.client.zzh;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.client.zzr;
import com.google.android.gms.ads.internal.client.zzs;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzeg;
import com.google.android.gms.internal.zzeh;
import com.google.android.gms.internal.zzei;
import com.google.android.gms.internal.zzej;
import com.google.android.gms.internal.zzgi;

/* loaded from: classes.dex */
public class AdLoader {
    private final Context mContext;
    private final zzh zzahz;
    private final zzr zzaia;

    /* loaded from: classes.dex */
    public static class Builder {
        private final Context mContext;
        private final zzs zzaib;

        private Builder(Context context, zzs zzsVar) {
            this.mContext = context;
            this.zzaib = zzsVar;
        }

        public Builder(Context context, String str) {
            this((Context) zzab.zzb(context, "context cannot be null"), zzm.zzix().zzb(context, str, new zzgi()));
        }

        public AdLoader build() {
            try {
                return new AdLoader(this.mContext, this.zzaib.zzes());
            } catch (RemoteException e) {
                zzb.zzb("Failed to build AdLoader.", e);
                return null;
            }
        }

        public Builder forAppInstallAd(NativeAppInstallAd.OnAppInstallAdLoadedListener onAppInstallAdLoadedListener) {
            try {
                this.zzaib.zza(new zzeg(onAppInstallAdLoadedListener));
            } catch (RemoteException e) {
                zzb.zzd("Failed to add app install ad listener", e);
            }
            return this;
        }

        public Builder forContentAd(NativeContentAd.OnContentAdLoadedListener onContentAdLoadedListener) {
            try {
                this.zzaib.zza(new zzeh(onContentAdLoadedListener));
            } catch (RemoteException e) {
                zzb.zzd("Failed to add content ad listener", e);
            }
            return this;
        }

        public Builder forCustomTemplateAd(String str, NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener onCustomTemplateAdLoadedListener, NativeCustomTemplateAd.OnCustomClickListener onCustomClickListener) {
            try {
                this.zzaib.zza(str, new zzej(onCustomTemplateAdLoadedListener), onCustomClickListener == null ? null : new zzei(onCustomClickListener));
            } catch (RemoteException e) {
                zzb.zzd("Failed to add custom template ad listener", e);
            }
            return this;
        }

        public Builder withAdListener(AdListener adListener) {
            try {
                this.zzaib.zzb(new zzc(adListener));
            } catch (RemoteException e) {
                zzb.zzd("Failed to set AdListener.", e);
            }
            return this;
        }

        public Builder withCorrelator(Correlator correlator) {
            zzab.zzy(correlator);
            try {
                this.zzaib.zzb(correlator.zzdd());
            } catch (RemoteException e) {
                zzb.zzd("Failed to set correlator.", e);
            }
            return this;
        }

        public Builder withNativeAdOptions(NativeAdOptions nativeAdOptions) {
            try {
                this.zzaib.zza(new NativeAdOptionsParcel(nativeAdOptions));
            } catch (RemoteException e) {
                zzb.zzd("Failed to specify native ad options", e);
            }
            return this;
        }
    }

    AdLoader(Context context, zzr zzrVar) {
        this(context, zzrVar, zzh.zzih());
    }

    private AdLoader(Context context, zzr zzrVar, zzh zzhVar) {
        this.mContext = context;
        this.zzaia = zzrVar;
        this.zzahz = zzhVar;
    }

    private void zza(zzad zzadVar) {
        try {
            this.zzaia.zzf(this.zzahz.zza(this.mContext, zzadVar));
        } catch (RemoteException e) {
            zzb.zzb("Failed to load ad.", e);
        }
    }

    public String getMediationAdapterClassName() {
        try {
            return this.zzaia.getMediationAdapterClassName();
        } catch (RemoteException e) {
            zzb.zzd("Failed to get the mediation adapter class name.", e);
            return null;
        }
    }

    public boolean isLoading() {
        try {
            return this.zzaia.isLoading();
        } catch (RemoteException e) {
            zzb.zzd("Failed to check if ad is loading.", e);
            return false;
        }
    }

    public void loadAd(AdRequest adRequest) {
        zza(adRequest.zzdc());
    }

    public void loadAd(PublisherAdRequest publisherAdRequest) {
        zza(publisherAdRequest.zzdc());
    }
}
