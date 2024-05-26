package com.google.android.gms.internal;

import android.os.Bundle;

@zzin
/* loaded from: classes.dex */
public final class zzka {
    public final Object zzail;
    public final zzjx zzaob;
    public final String zzcit;
    public int zzckh;
    public int zzcki;

    private zzka(zzjx zzjxVar, String str) {
        this.zzail = new Object();
        this.zzaob = zzjxVar;
        this.zzcit = str;
    }

    public zzka(String str) {
        this(com.google.android.gms.ads.internal.zzu.zzft(), str);
    }

    public final Bundle toBundle() {
        Bundle bundle;
        synchronized (this.zzail) {
            bundle = new Bundle();
            bundle.putInt("pmnli", this.zzckh);
            bundle.putInt("pmnll", this.zzcki);
        }
        return bundle;
    }
}
