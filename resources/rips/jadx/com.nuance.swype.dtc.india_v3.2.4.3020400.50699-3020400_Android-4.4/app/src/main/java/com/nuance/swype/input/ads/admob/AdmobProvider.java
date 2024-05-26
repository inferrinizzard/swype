package com.nuance.swype.input.ads.admob;

import android.content.Context;
import android.provider.Settings;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ads.AdProvider;
import com.nuance.swype.util.AdsUtil;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public class AdmobProvider extends AdProvider {
    private AdView mAdView;
    private final boolean mShowTestAd;

    public AdmobProvider(Context context) {
        super(context);
        this.mShowTestAd = context.getResources().getBoolean(R.bool.show_test_ad);
    }

    @Override // com.nuance.swype.input.ads.AdProvider
    public void setup(ViewGroup billboardView) {
        log.d("Setting up Admob provider");
        this.mAdView = (AdView) billboardView.findViewById(R.id.keyboard_ad_view);
        this.mAdView.setAdListener(new AdStatusListener());
    }

    @Override // com.nuance.swype.input.ads.AdProvider
    public void loadAd() {
        super.loadAd();
        log.d("Loading a Admob ad");
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequestBuilder.tagForChildDirectedTreatment(AdsUtil.sTagForChildDirectedTreatment);
        log.d("Setting child directed? ", Boolean.valueOf(AdsUtil.sTagForChildDirectedTreatment));
        if (this.mShowTestAd) {
            try {
                String deviceIdHash = getHashedAndroidId();
                log.d("Adding device ", deviceIdHash, " as a test devices");
                adRequestBuilder.addTestDevice(deviceIdHash);
                adRequestBuilder.addTestDevice(deviceIdHash.toLowerCase());
                adRequestBuilder.addTestDevice(deviceIdHash.toUpperCase());
            } catch (NoSuchAlgorithmException e) {
                log.d("Hashing algo not found. Cannot add this device as test device for ads");
            }
        } else {
            log.d("Build does not support showing test ads");
        }
        this.mAdView.loadAd(adRequestBuilder.build());
    }

    private String getHashedAndroidId() throws NoSuchAlgorithmException {
        String androidId = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
        byte[] hashedAndroidId = MessageDigest.getInstance("MD5").digest(androidId.getBytes());
        String result = new BigInteger(1, hashedAndroidId).toString(16);
        return String.format("%32s", result).replace(' ', '0');
    }

    /* loaded from: classes.dex */
    private class AdStatusListener extends AdListener {
        private AdStatusListener() {
        }

        @Override // com.google.android.gms.ads.AdListener
        public void onAdFailedToLoad(int errorCode) {
            AdmobProvider.log.d("Ad load failed. Error code: ", Integer.valueOf(errorCode));
            super.onAdFailedToLoad(errorCode);
            AdmobProvider.this.notifyAdLoadStatusChanged(AdmobProvider.this.getProviderIndependentErrorCode(errorCode));
        }

        @Override // com.google.android.gms.ads.AdListener
        public void onAdLoaded() {
            AdmobProvider.log.d("Ad loaded successfully!");
            super.onAdLoaded();
            IMEApplication.from(AdmobProvider.this.mContext).getAdSessionTracker().wasShown();
            AdmobProvider.this.notifyAdLoadStatusChanged(AdProvider.AD_LOAD_STATUS.SUCCESS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AdProvider.AD_LOAD_STATUS getProviderIndependentErrorCode(int errorCode) {
        switch (errorCode) {
            case 0:
                return AdProvider.AD_LOAD_STATUS.FAILED_INTERNAL_ERROR;
            case 1:
                return AdProvider.AD_LOAD_STATUS.FAILED_INVALID_REQUEST;
            case 2:
                return AdProvider.AD_LOAD_STATUS.FAILED_NETWORK_ERROR;
            case 3:
                return AdProvider.AD_LOAD_STATUS.FAILED_NO_FILL;
            default:
                return AdProvider.AD_LOAD_STATUS.FAILED_UNKNOWN;
        }
    }

    @Override // com.nuance.swype.input.ads.AdProvider
    public void showAdView() {
        log.d("showing admob view");
        this.mAdView.setVisibility(0);
        this.mAdView.resume();
    }

    @Override // com.nuance.swype.input.ads.AdProvider
    public void hideAdView() {
        log.d("hiding admob view");
        this.mAdView.pause();
        this.mAdView.setVisibility(8);
    }
}
