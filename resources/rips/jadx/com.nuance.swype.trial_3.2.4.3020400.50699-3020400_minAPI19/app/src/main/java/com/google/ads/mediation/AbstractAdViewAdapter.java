package com.google.ads.mediation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.MediationNativeAdapter;
import com.google.android.gms.ads.mediation.MediationNativeListener;
import com.google.android.gms.ads.mediation.NativeAppInstallAdMapper;
import com.google.android.gms.ads.mediation.NativeContentAdMapper;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdListener;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzlt;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public abstract class AbstractAdViewAdapter implements com.google.android.gms.ads.mediation.MediationBannerAdapter, MediationNativeAdapter, MediationRewardedVideoAdAdapter, zzlt {
    public static final String AD_UNIT_ID_PARAMETER = "pubid";
    protected AdView zzfb;
    protected InterstitialAd zzfc;
    private AdLoader zzfd;
    private Context zzfe;
    private InterstitialAd zzff;
    private MediationRewardedVideoAdListener zzfg;
    final RewardedVideoAdListener zzfh = new RewardedVideoAdListener() { // from class: com.google.ads.mediation.AbstractAdViewAdapter.1
        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewarded(RewardItem rewardItem) {
            AbstractAdViewAdapter.this.zzfg.onRewarded(AbstractAdViewAdapter.this, rewardItem);
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoAdClosed() {
            AbstractAdViewAdapter.this.zzfg.onAdClosed(AbstractAdViewAdapter.this);
            AbstractAdViewAdapter.this.zzff = null;
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoAdFailedToLoad(int i) {
            AbstractAdViewAdapter.this.zzfg.onAdFailedToLoad(AbstractAdViewAdapter.this, i);
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoAdLeftApplication() {
            AbstractAdViewAdapter.this.zzfg.onAdLeftApplication(AbstractAdViewAdapter.this);
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoAdLoaded() {
            AbstractAdViewAdapter.this.zzfg.onAdLoaded(AbstractAdViewAdapter.this);
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoAdOpened() {
            AbstractAdViewAdapter.this.zzfg.onAdOpened(AbstractAdViewAdapter.this);
        }

        @Override // com.google.android.gms.ads.reward.RewardedVideoAdListener
        public final void onRewardedVideoStarted() {
            AbstractAdViewAdapter.this.zzfg.onVideoStarted(AbstractAdViewAdapter.this);
        }
    };

    /* loaded from: classes.dex */
    static class zza extends NativeAppInstallAdMapper {
        private final NativeAppInstallAd zzfj;

        public zza(NativeAppInstallAd nativeAppInstallAd) {
            this.zzfj = nativeAppInstallAd;
            setHeadline(nativeAppInstallAd.getHeadline().toString());
            setImages(nativeAppInstallAd.getImages());
            setBody(nativeAppInstallAd.getBody().toString());
            setIcon(nativeAppInstallAd.getIcon());
            setCallToAction(nativeAppInstallAd.getCallToAction().toString());
            if (nativeAppInstallAd.getStarRating() != null) {
                setStarRating(nativeAppInstallAd.getStarRating().doubleValue());
            }
            if (nativeAppInstallAd.getStore() != null) {
                setStore(nativeAppInstallAd.getStore().toString());
            }
            if (nativeAppInstallAd.getPrice() != null) {
                setPrice(nativeAppInstallAd.getPrice().toString());
            }
            setOverrideImpressionRecording(true);
            setOverrideClickHandling(true);
        }

        @Override // com.google.android.gms.ads.mediation.NativeAdMapper
        public final void trackView(View view) {
            if (view instanceof NativeAdView) {
                ((NativeAdView) view).setNativeAd(this.zzfj);
            }
        }
    }

    /* loaded from: classes.dex */
    static class zzb extends NativeContentAdMapper {
        private final NativeContentAd zzfk;

        public zzb(NativeContentAd nativeContentAd) {
            this.zzfk = nativeContentAd;
            setHeadline(nativeContentAd.getHeadline().toString());
            setImages(nativeContentAd.getImages());
            setBody(nativeContentAd.getBody().toString());
            if (nativeContentAd.getLogo() != null) {
                setLogo(nativeContentAd.getLogo());
            }
            setCallToAction(nativeContentAd.getCallToAction().toString());
            setAdvertiser(nativeContentAd.getAdvertiser().toString());
            setOverrideImpressionRecording(true);
            setOverrideClickHandling(true);
        }

        @Override // com.google.android.gms.ads.mediation.NativeAdMapper
        public final void trackView(View view) {
            if (view instanceof NativeAdView) {
                ((NativeAdView) view).setNativeAd(this.zzfk);
            }
        }
    }

    /* loaded from: classes.dex */
    static final class zzc extends AdListener implements com.google.android.gms.ads.internal.client.zza {
        final AbstractAdViewAdapter zzfl;
        final com.google.android.gms.ads.mediation.MediationBannerListener zzfm;

        public zzc(AbstractAdViewAdapter abstractAdViewAdapter, com.google.android.gms.ads.mediation.MediationBannerListener mediationBannerListener) {
            this.zzfl = abstractAdViewAdapter;
            this.zzfm = mediationBannerListener;
        }

        @Override // com.google.android.gms.ads.internal.client.zza
        public final void onAdClicked() {
            this.zzfm.onAdClicked(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdClosed() {
            this.zzfm.onAdClosed(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdFailedToLoad(int i) {
            this.zzfm.onAdFailedToLoad(this.zzfl, i);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLeftApplication() {
            this.zzfm.onAdLeftApplication(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLoaded() {
            this.zzfm.onAdLoaded(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdOpened() {
            this.zzfm.onAdOpened(this.zzfl);
        }
    }

    /* loaded from: classes.dex */
    static final class zzd extends AdListener implements com.google.android.gms.ads.internal.client.zza {
        final AbstractAdViewAdapter zzfl;
        final com.google.android.gms.ads.mediation.MediationInterstitialListener zzfn;

        public zzd(AbstractAdViewAdapter abstractAdViewAdapter, com.google.android.gms.ads.mediation.MediationInterstitialListener mediationInterstitialListener) {
            this.zzfl = abstractAdViewAdapter;
            this.zzfn = mediationInterstitialListener;
        }

        @Override // com.google.android.gms.ads.internal.client.zza
        public final void onAdClicked() {
            this.zzfn.onAdClicked(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdClosed() {
            this.zzfn.onAdClosed(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdFailedToLoad(int i) {
            this.zzfn.onAdFailedToLoad(this.zzfl, i);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLeftApplication() {
            this.zzfn.onAdLeftApplication(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLoaded() {
            this.zzfn.onAdLoaded(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdOpened() {
            this.zzfn.onAdOpened(this.zzfl);
        }
    }

    /* loaded from: classes.dex */
    static final class zze extends AdListener implements NativeAppInstallAd.OnAppInstallAdLoadedListener, NativeContentAd.OnContentAdLoadedListener, com.google.android.gms.ads.internal.client.zza {
        final AbstractAdViewAdapter zzfl;
        final MediationNativeListener zzfo;

        public zze(AbstractAdViewAdapter abstractAdViewAdapter, MediationNativeListener mediationNativeListener) {
            this.zzfl = abstractAdViewAdapter;
            this.zzfo = mediationNativeListener;
        }

        @Override // com.google.android.gms.ads.internal.client.zza
        public final void onAdClicked() {
            this.zzfo.onAdClicked(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdClosed() {
            this.zzfo.onAdClosed(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdFailedToLoad(int i) {
            this.zzfo.onAdFailedToLoad(this.zzfl, i);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLeftApplication() {
            this.zzfo.onAdLeftApplication(this.zzfl);
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdLoaded() {
        }

        @Override // com.google.android.gms.ads.AdListener
        public final void onAdOpened() {
            this.zzfo.onAdOpened(this.zzfl);
        }

        @Override // com.google.android.gms.ads.formats.NativeAppInstallAd.OnAppInstallAdLoadedListener
        public final void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
            this.zzfo.onAdLoaded(this.zzfl, new zza(nativeAppInstallAd));
        }

        @Override // com.google.android.gms.ads.formats.NativeContentAd.OnContentAdLoadedListener
        public final void onContentAdLoaded(NativeContentAd nativeContentAd) {
            this.zzfo.onAdLoaded(this.zzfl, new zzb(nativeContentAd));
        }
    }

    public String getAdUnitId(Bundle bundle) {
        return bundle.getString(AD_UNIT_ID_PARAMETER);
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public View getBannerView() {
        return this.zzfb;
    }

    @Override // com.google.android.gms.internal.zzlt
    public Bundle getInterstitialAdapterInfo() {
        return new MediationAdapter.zza().zzbb(1).zzvp();
    }

    @Override // com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
    public void initialize(Context context, com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest, String str, MediationRewardedVideoAdListener mediationRewardedVideoAdListener, Bundle bundle, Bundle bundle2) {
        this.zzfe = context.getApplicationContext();
        this.zzfg = mediationRewardedVideoAdListener;
        this.zzfg.onInitializationSucceeded(this);
    }

    @Override // com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
    public boolean isInitialized() {
        return this.zzfg != null;
    }

    @Override // com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
    public void loadAd(com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest, Bundle bundle, Bundle bundle2) {
        if (this.zzfe == null || this.zzfg == null) {
            com.google.android.gms.ads.internal.util.client.zzb.e("AdMobAdapter.loadAd called before initialize.");
            return;
        }
        this.zzff = new InterstitialAd(this.zzfe);
        this.zzff.zzd(true);
        this.zzff.setAdUnitId(getAdUnitId(bundle));
        this.zzff.setRewardedVideoAdListener(this.zzfh);
        this.zzff.loadAd(zza(this.zzfe, mediationAdRequest, bundle2, bundle));
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onDestroy() {
        if (this.zzfb != null) {
            this.zzfb.destroy();
            this.zzfb = null;
        }
        if (this.zzfc != null) {
            this.zzfc = null;
        }
        if (this.zzfd != null) {
            this.zzfd = null;
        }
        if (this.zzff != null) {
            this.zzff = null;
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onPause() {
        if (this.zzfb != null) {
            this.zzfb.pause();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onResume() {
        if (this.zzfb != null) {
            this.zzfb.resume();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public void requestBannerAd(Context context, com.google.android.gms.ads.mediation.MediationBannerListener mediationBannerListener, Bundle bundle, AdSize adSize, com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest, Bundle bundle2) {
        this.zzfb = new AdView(context);
        this.zzfb.setAdSize(new AdSize(adSize.getWidth(), adSize.getHeight()));
        this.zzfb.setAdUnitId(getAdUnitId(bundle));
        this.zzfb.setAdListener(new zzc(this, mediationBannerListener));
        this.zzfb.loadAd(zza(context, mediationAdRequest, bundle2, bundle));
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public void requestInterstitialAd(Context context, com.google.android.gms.ads.mediation.MediationInterstitialListener mediationInterstitialListener, Bundle bundle, com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest, Bundle bundle2) {
        this.zzfc = new InterstitialAd(context);
        this.zzfc.setAdUnitId(getAdUnitId(bundle));
        this.zzfc.setAdListener(new zzd(this, mediationInterstitialListener));
        this.zzfc.loadAd(zza(context, mediationAdRequest, bundle2, bundle));
    }

    @Override // com.google.android.gms.ads.mediation.MediationNativeAdapter
    public void requestNativeAd(Context context, MediationNativeListener mediationNativeListener, Bundle bundle, NativeMediationAdRequest nativeMediationAdRequest, Bundle bundle2) {
        zze zzeVar = new zze(this, mediationNativeListener);
        AdLoader.Builder withAdListener = zza(context, bundle.getString(AD_UNIT_ID_PARAMETER)).withAdListener(zzeVar);
        NativeAdOptions nativeAdOptions = nativeMediationAdRequest.getNativeAdOptions();
        if (nativeAdOptions != null) {
            withAdListener.withNativeAdOptions(nativeAdOptions);
        }
        if (nativeMediationAdRequest.isAppInstallAdRequested()) {
            withAdListener.forAppInstallAd(zzeVar);
        }
        if (nativeMediationAdRequest.isContentAdRequested()) {
            withAdListener.forContentAd(zzeVar);
        }
        this.zzfd = withAdListener.build();
        this.zzfd.loadAd(zza(context, nativeMediationAdRequest, bundle2, bundle));
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public void showInterstitial() {
        this.zzfc.show();
    }

    @Override // com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
    public void showVideo() {
        this.zzff.show();
    }

    public abstract Bundle zza(Bundle bundle, Bundle bundle2);

    AdLoader.Builder zza(Context context, String str) {
        return new AdLoader.Builder(context, str);
    }

    AdRequest zza(Context context, com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest, Bundle bundle, Bundle bundle2) {
        AdRequest.Builder builder = new AdRequest.Builder();
        Date birthday = mediationAdRequest.getBirthday();
        if (birthday != null) {
            builder.setBirthday(birthday);
        }
        int gender = mediationAdRequest.getGender();
        if (gender != 0) {
            builder.setGender(gender);
        }
        Set<String> keywords = mediationAdRequest.getKeywords();
        if (keywords != null) {
            Iterator<String> it = keywords.iterator();
            while (it.hasNext()) {
                builder.addKeyword(it.next());
            }
        }
        Location location = mediationAdRequest.getLocation();
        if (location != null) {
            builder.setLocation(location);
        }
        if (mediationAdRequest.isTesting()) {
            builder.addTestDevice(zzm.zziw().zzaq(context));
        }
        if (mediationAdRequest.taggedForChildDirectedTreatment() != -1) {
            builder.tagForChildDirectedTreatment(mediationAdRequest.taggedForChildDirectedTreatment() == 1);
        }
        builder.setIsDesignedForFamilies(mediationAdRequest.isDesignedForFamilies());
        builder.addNetworkExtrasBundle(AdMobAdapter.class, zza(bundle, bundle2));
        return builder.build();
    }
}
