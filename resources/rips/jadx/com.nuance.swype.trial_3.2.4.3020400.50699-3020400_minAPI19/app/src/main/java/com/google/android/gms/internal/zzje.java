package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.reward.client.RewardedVideoAdRequestParcel;
import com.google.android.gms.ads.internal.reward.client.zzb;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.util.Iterator;

@zzin
/* loaded from: classes.dex */
public final class zzje extends zzb.zza {
    private final Context mContext;
    private final Object zzail = new Object();
    private final VersionInfoParcel zzalo;
    private final zzjf zzchf;

    public zzje(Context context, com.google.android.gms.ads.internal.zzd zzdVar, zzgj zzgjVar, VersionInfoParcel versionInfoParcel) {
        this.mContext = context;
        this.zzalo = versionInfoParcel;
        this.zzchf = new zzjf(context, zzdVar, AdSizeParcel.zzii(), zzgjVar, versionInfoParcel);
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void destroy() {
        zzh(null);
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final boolean isLoaded() {
        boolean isLoaded;
        synchronized (this.zzail) {
            isLoaded = this.zzchf.isLoaded();
        }
        return isLoaded;
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void pause() {
        zzf(null);
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void resume() {
        zzg(null);
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void setUserId(String str) {
        zzkd.zzcx("RewardedVideoAd.setUserId() is deprecated. Please do not call this method.");
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void zza(RewardedVideoAdRequestParcel rewardedVideoAdRequestParcel) {
        synchronized (this.zzail) {
            this.zzchf.zza(rewardedVideoAdRequestParcel);
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void zza(com.google.android.gms.ads.internal.reward.client.zzd zzdVar) {
        synchronized (this.zzail) {
            this.zzchf.zza(zzdVar);
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void zzf(com.google.android.gms.dynamic.zzd zzdVar) {
        synchronized (this.zzail) {
            this.zzchf.pause();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void zzh(com.google.android.gms.dynamic.zzd zzdVar) {
        synchronized (this.zzail) {
            this.zzchf.destroy();
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void show() {
        synchronized (this.zzail) {
            zzjf zzjfVar = this.zzchf;
            com.google.android.gms.common.internal.zzab.zzhi("showAd must be called on the main UI thread.");
            if (zzjfVar.isLoaded()) {
                zzjfVar.zzchi = true;
                zzjm zzcf = zzjfVar.zzcf(zzjfVar.zzajs.zzapb.zzbop);
                if (zzcf != null && zzcf.zzbog != null) {
                    try {
                        zzcf.zzbog.showVideo();
                    } catch (RemoteException e) {
                        zzkd.zzd("Could not call showVideo.", e);
                    }
                }
            } else {
                zzkd.zzcx("The reward video has not loaded.");
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zzb
    public final void zzg(com.google.android.gms.dynamic.zzd zzdVar) {
        Context context;
        synchronized (this.zzail) {
            if (zzdVar == null) {
                context = null;
            } else {
                try {
                    context = (Context) com.google.android.gms.dynamic.zze.zzad(zzdVar);
                } catch (Exception e) {
                    zzkd.zzd("Unable to extract updated context.", e);
                }
            }
            if (context != null) {
                Iterator<zzjm> it = this.zzchf.zzchh.values().iterator();
                while (it.hasNext()) {
                    try {
                        it.next().zzbog.zzj(com.google.android.gms.dynamic.zze.zzac(context));
                    } catch (RemoteException e2) {
                        zzkd.zzb("Unable to call Adapter.onContextChanged.", e2);
                    }
                }
            }
            this.zzchf.resume();
        }
    }
}
