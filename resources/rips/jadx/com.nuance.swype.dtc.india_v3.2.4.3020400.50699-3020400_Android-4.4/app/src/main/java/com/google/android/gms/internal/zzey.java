package com.google.android.gms.internal;

import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzey implements zzep {
    private final zza zzbiy;

    /* loaded from: classes.dex */
    public interface zza {
        void zzb(RewardItemParcel rewardItemParcel);

        void zzev();
    }

    public zzey(zza zzaVar) {
        this.zzbiy = zzaVar;
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        RewardItemParcel rewardItemParcel;
        int parseInt;
        String str;
        String str2 = map.get(NativeProtocol.WEB_DIALOG_ACTION);
        if (!"grant".equals(str2)) {
            if ("video_start".equals(str2)) {
                this.zzbiy.zzev();
                return;
            }
            return;
        }
        try {
            parseInt = Integer.parseInt(map.get("amount"));
            str = map.get("type");
        } catch (NumberFormatException e) {
            zzkd.zzd("Unable to parse reward amount.", e);
        }
        if (!TextUtils.isEmpty(str)) {
            rewardItemParcel = new RewardItemParcel(str, parseInt);
            this.zzbiy.zzb(rewardItemParcel);
        }
        rewardItemParcel = null;
        this.zzbiy.zzb(rewardItemParcel);
    }
}
