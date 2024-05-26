package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.ads.AdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.NetworkExtras;

@zzin
/* loaded from: classes.dex */
public final class zzgw<NETWORK_EXTRAS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> implements MediationBannerListener, MediationInterstitialListener {
    private final zzgl zzbpk;

    public zzgw(zzgl zzglVar) {
        this.zzbpk = zzglVar;
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onClick(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onClick.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onClick must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.1
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdClicked();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClicked.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdClicked();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClicked.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onDismissScreen(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onDismissScreen.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onDismissScreen must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.4
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdClosed();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClosed.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdClosed();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClosed.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public final void onDismissScreen(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onDismissScreen.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onDismissScreen must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.9
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdClosed();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClosed.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdClosed();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdClosed.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onFailedToReceiveAd(MediationBannerAdapter<?, ?> mediationBannerAdapter, final AdRequest.ErrorCode errorCode) {
        String valueOf = String.valueOf(errorCode);
        com.google.android.gms.ads.internal.util.client.zzb.zzcv(new StringBuilder(String.valueOf(valueOf).length() + 47).append("Adapter called onFailedToReceiveAd with error. ").append(valueOf).toString());
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onFailedToReceiveAd must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.5
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdFailedToLoad(zzgx.zza(errorCode));
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdFailedToLoad.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdFailedToLoad(zzgx.zza(errorCode));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public final void onFailedToReceiveAd(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter, final AdRequest.ErrorCode errorCode) {
        String valueOf = String.valueOf(errorCode);
        com.google.android.gms.ads.internal.util.client.zzb.zzcv(new StringBuilder(String.valueOf(valueOf).length() + 47).append("Adapter called onFailedToReceiveAd with error ").append(valueOf).append(".").toString());
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onFailedToReceiveAd must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.10
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdFailedToLoad(zzgx.zza(errorCode));
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdFailedToLoad.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdFailedToLoad(zzgx.zza(errorCode));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onLeaveApplication(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onLeaveApplication.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onLeaveApplication must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.6
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdLeftApplication();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLeftApplication.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdLeftApplication();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLeftApplication.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public final void onLeaveApplication(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onLeaveApplication.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onLeaveApplication must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.11
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdLeftApplication();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLeftApplication.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdLeftApplication();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLeftApplication.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onPresentScreen(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onPresentScreen.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onPresentScreen must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.7
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdOpened();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdOpened.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdOpened();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdOpened.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public final void onPresentScreen(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onPresentScreen.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onPresentScreen must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.2
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdOpened();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdOpened.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdOpened();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdOpened.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public final void onReceivedAd(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onReceivedAd.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onReceivedAd must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.8
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdLoaded();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLoaded.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdLoaded();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLoaded.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public final void onReceivedAd(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcv("Adapter called onReceivedAd.");
        if (!com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx("onReceivedAd must be called on the main UI thread.");
            com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzgw.3
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        zzgw.this.zzbpk.onAdLoaded();
                    } catch (RemoteException e) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLoaded.", e);
                    }
                }
            });
        } else {
            try {
                this.zzbpk.onAdLoaded();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not call onAdLoaded.", e);
            }
        }
    }
}
