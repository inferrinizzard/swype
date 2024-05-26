package com.google.android.gms.ads.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkh;

@zzin
/* loaded from: classes.dex */
public class zze {
    public void zza(Context context, AdOverlayInfoParcel adOverlayInfoParcel) {
        zza(context, adOverlayInfoParcel, true);
    }

    public void zza(Context context, AdOverlayInfoParcel adOverlayInfoParcel, boolean z) {
        if (adOverlayInfoParcel.zzbts == 4 && adOverlayInfoParcel.zzbtl == null) {
            if (adOverlayInfoParcel.zzbtk != null) {
                adOverlayInfoParcel.zzbtk.onAdClicked();
            }
            com.google.android.gms.ads.internal.zzu.zzfn().zza(context, adOverlayInfoParcel.zzbtj, adOverlayInfoParcel.zzbtr);
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(context, AdActivity.CLASS_NAME);
        intent.putExtra("com.google.android.gms.ads.internal.overlay.useClientJar", adOverlayInfoParcel.zzaow.zzcnm);
        intent.putExtra("shouldCallOnOverlayOpened", z);
        AdOverlayInfoParcel.zza(intent, adOverlayInfoParcel);
        if (!com.google.android.gms.common.util.zzs.zzhb(21)) {
            intent.addFlags(524288);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        com.google.android.gms.ads.internal.zzu.zzfq();
        zzkh.zzb(context, intent);
    }
}
