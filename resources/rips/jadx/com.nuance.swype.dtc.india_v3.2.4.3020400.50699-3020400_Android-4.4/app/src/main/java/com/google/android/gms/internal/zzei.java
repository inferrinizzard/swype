package com.google.android.gms.internal;

import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.internal.zzed;

@zzin
/* loaded from: classes.dex */
public final class zzei extends zzed.zza {
    private final NativeCustomTemplateAd.OnCustomClickListener zzbhk;

    public zzei(NativeCustomTemplateAd.OnCustomClickListener onCustomClickListener) {
        this.zzbhk = onCustomClickListener;
    }

    @Override // com.google.android.gms.internal.zzed
    public final void zza(zzdz zzdzVar, String str) {
        this.zzbhk.onCustomClick(new zzea(zzdzVar), str);
    }
}
