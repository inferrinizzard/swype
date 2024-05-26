package com.google.android.gms.ads.internal.reward.client;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzi implements RewardedVideoAd {
    private final Context mContext;
    private final Object zzail = new Object();
    private final zzb zzchl;
    private RewardedVideoAdListener zzfh;

    public zzi(Context context, zzb zzbVar) {
        this.zzchl = zzbVar;
        this.mContext = context;
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void destroy() {
        destroy(null);
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void destroy(Context context) {
        synchronized (this.zzail) {
            if (this.zzchl == null) {
                return;
            }
            try {
                this.zzchl.zzh(com.google.android.gms.dynamic.zze.zzac(context));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward destroy to RewardedVideoAd", e);
            }
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public RewardedVideoAdListener getRewardedVideoAdListener() {
        RewardedVideoAdListener rewardedVideoAdListener;
        synchronized (this.zzail) {
            rewardedVideoAdListener = this.zzfh;
        }
        return rewardedVideoAdListener;
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public String getUserId() {
        com.google.android.gms.ads.internal.util.client.zzb.zzcx("RewardedVideoAd.getUserId() is deprecated. Please do not call this method.");
        return null;
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public boolean isLoaded() {
        boolean z = false;
        synchronized (this.zzail) {
            if (this.zzchl != null) {
                try {
                    z = this.zzchl.isLoaded();
                } catch (RemoteException e) {
                    com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward isLoaded to RewardedVideoAd", e);
                }
            }
        }
        return z;
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void loadAd(String str, AdRequest adRequest) {
        synchronized (this.zzail) {
            if (this.zzchl == null) {
                return;
            }
            try {
                this.zzchl.zza(com.google.android.gms.ads.internal.client.zzh.zzih().zza(this.mContext, adRequest.zzdc(), str));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward loadAd to RewardedVideoAd", e);
            }
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void pause() {
        pause(null);
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void pause(Context context) {
        synchronized (this.zzail) {
            if (this.zzchl == null) {
                return;
            }
            try {
                this.zzchl.zzf(com.google.android.gms.dynamic.zze.zzac(context));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward pause to RewardedVideoAd", e);
            }
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void resume() {
        resume(null);
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void resume(Context context) {
        synchronized (this.zzail) {
            if (this.zzchl == null) {
                return;
            }
            try {
                this.zzchl.zzg(com.google.android.gms.dynamic.zze.zzac(context));
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward resume to RewardedVideoAd", e);
            }
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void setRewardedVideoAdListener(RewardedVideoAdListener rewardedVideoAdListener) {
        synchronized (this.zzail) {
            this.zzfh = rewardedVideoAdListener;
            if (this.zzchl != null) {
                try {
                    this.zzchl.zza(new zzg(rewardedVideoAdListener));
                } catch (RemoteException e) {
                    com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward setRewardedVideoAdListener to RewardedVideoAd", e);
                }
            }
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void setUserId(String str) {
        com.google.android.gms.ads.internal.util.client.zzb.zzcx("RewardedVideoAd.setUserId() is deprecated. Please do not call this method.");
    }

    @Override // com.google.android.gms.ads.reward.RewardedVideoAd
    public void show() {
        synchronized (this.zzail) {
            if (this.zzchl == null) {
                return;
            }
            try {
                this.zzchl.show();
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward show to RewardedVideoAd", e);
            }
        }
    }
}
