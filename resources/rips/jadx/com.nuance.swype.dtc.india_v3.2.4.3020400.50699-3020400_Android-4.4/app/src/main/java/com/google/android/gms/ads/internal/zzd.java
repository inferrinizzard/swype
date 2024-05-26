package com.google.android.gms.ads.internal;

import com.google.android.gms.internal.zzem;
import com.google.android.gms.internal.zzff;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzjn;
import com.google.android.gms.internal.zzjp;

@zzin
/* loaded from: classes.dex */
public class zzd {
    public final zzff zzakj;
    public final com.google.android.gms.ads.internal.overlay.zzj zzakk;
    public final com.google.android.gms.ads.internal.overlay.zzm zzakl;
    public final zzjp zzakm;

    public zzd(zzff zzffVar, com.google.android.gms.ads.internal.overlay.zzj zzjVar, com.google.android.gms.ads.internal.overlay.zzm zzmVar, zzjp zzjpVar) {
        this.zzakj = zzffVar;
        this.zzakk = zzjVar;
        this.zzakl = zzmVar;
        this.zzakm = zzjpVar;
    }

    public static zzd zzek() {
        return new zzd(new zzem(), new com.google.android.gms.ads.internal.overlay.zzn(), new com.google.android.gms.ads.internal.overlay.zzt(), new zzjn());
    }
}
