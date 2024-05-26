package com.google.android.gms.ads.internal.reward.client;

import android.os.RemoteException;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zze implements RewardItem {
    private final zza zzchk;

    public zze(zza zzaVar) {
        this.zzchk = zzaVar;
    }

    @Override // com.google.android.gms.ads.reward.RewardItem
    public int getAmount() {
        if (this.zzchk == null) {
            return 0;
        }
        try {
            return this.zzchk.getAmount();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getAmount to RewardItem", e);
            return 0;
        }
    }

    @Override // com.google.android.gms.ads.reward.RewardItem
    public String getType() {
        if (this.zzchk == null) {
            return null;
        }
        try {
            return this.zzchk.getType();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getType to RewardItem", e);
            return null;
        }
    }
}
