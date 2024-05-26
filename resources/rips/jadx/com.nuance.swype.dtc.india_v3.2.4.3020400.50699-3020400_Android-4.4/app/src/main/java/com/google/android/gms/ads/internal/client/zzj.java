package com.google.android.gms.ads.internal.client;

import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.internal.client.zzw;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class zzj extends zzw.zza {
    private final AppEventListener zzaux;

    public zzj(AppEventListener appEventListener) {
        this.zzaux = appEventListener;
    }

    @Override // com.google.android.gms.ads.internal.client.zzw
    public final void onAppEvent(String str, String str2) {
        this.zzaux.onAppEvent(str, str2);
    }
}
