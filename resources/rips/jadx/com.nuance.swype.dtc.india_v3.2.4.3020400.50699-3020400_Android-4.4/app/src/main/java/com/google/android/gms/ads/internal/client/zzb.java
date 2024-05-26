package com.google.android.gms.ads.internal.client;

import com.google.android.gms.ads.internal.client.zzp;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class zzb extends zzp.zza {
    private final zza zzatk;

    public zzb(zza zzaVar) {
        this.zzatk = zzaVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzp
    public final void onAdClicked() {
        this.zzatk.onAdClicked();
    }
}
