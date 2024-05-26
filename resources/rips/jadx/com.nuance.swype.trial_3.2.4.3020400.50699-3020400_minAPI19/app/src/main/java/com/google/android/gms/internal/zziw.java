package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zziv;
import java.util.WeakHashMap;

@zzin
/* loaded from: classes.dex */
public final class zziw {
    private WeakHashMap<Context, zza> zzcha = new WeakHashMap<>();

    /* loaded from: classes.dex */
    private class zza {
        public final long zzchb = com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis();
        public final zziv zzchc;

        public zza(zziv zzivVar) {
            this.zzchc = zzivVar;
        }
    }

    public final zziv zzy(Context context) {
        zziv zzrn;
        zza zzaVar = this.zzcha.get(context);
        if (zzaVar != null) {
            if (!(zzaVar.zzchb + ((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbat)).longValue() < com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis())) {
                if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbas)).booleanValue()) {
                    zzrn = new zziv.zza(context, zzaVar.zzchc).zzrn();
                    this.zzcha.put(context, new zza(zzrn));
                    return zzrn;
                }
            }
        }
        zzrn = new zziv.zza(context).zzrn();
        this.zzcha.put(context, new zza(zzrn));
        return zzrn;
    }
}
