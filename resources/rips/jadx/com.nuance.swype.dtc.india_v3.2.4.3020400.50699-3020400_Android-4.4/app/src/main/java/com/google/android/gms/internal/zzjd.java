package com.google.android.gms.internal;

import com.google.android.gms.ads.internal.reward.client.zza;

@zzin
/* loaded from: classes.dex */
public final class zzjd extends zza.AbstractBinderC0031zza {
    private final String zzcfz;
    private final int zzche;

    public zzjd(String str, int i) {
        this.zzcfz = str;
        this.zzche = i;
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zza
    public final int getAmount() {
        return this.zzche;
    }

    @Override // com.google.android.gms.ads.internal.reward.client.zza
    public final String getType() {
        return this.zzcfz;
    }
}
