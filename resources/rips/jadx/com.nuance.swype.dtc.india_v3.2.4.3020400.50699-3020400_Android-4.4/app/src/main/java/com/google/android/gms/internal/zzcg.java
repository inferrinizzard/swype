package com.google.android.gms.internal;

import android.content.Context;
import android.view.View;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzcd;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@zzin
/* loaded from: classes.dex */
public final class zzcg implements zzch {
    private final VersionInfoParcel zzalo;
    private final Context zzaql;
    private final zzfs zzaro;
    public final Object zzail = new Object();
    public final WeakHashMap<zzju, zzcd> zzarm = new WeakHashMap<>();
    private final ArrayList<zzcd> zzarn = new ArrayList<>();

    public zzcg(Context context, VersionInfoParcel versionInfoParcel, zzfs zzfsVar) {
        this.zzaql = context.getApplicationContext();
        this.zzalo = versionInfoParcel;
        this.zzaro = zzfsVar;
    }

    private boolean zzh(zzju zzjuVar) {
        boolean z;
        synchronized (this.zzail) {
            zzcd zzcdVar = this.zzarm.get(zzjuVar);
            z = zzcdVar != null && zzcdVar.zzha();
        }
        return z;
    }

    public final zzcd zza(AdSizeParcel adSizeParcel, zzju zzjuVar) {
        return zza(adSizeParcel, zzjuVar, zzjuVar.zzbtm.getView());
    }

    public final zzcd zza(AdSizeParcel adSizeParcel, zzju zzjuVar, View view) {
        return zza(adSizeParcel, zzjuVar, new zzcd.zzd(view, zzjuVar), null);
    }

    public final zzcd zza(AdSizeParcel adSizeParcel, zzju zzjuVar, zzck zzckVar, zzft zzftVar) {
        zzcd zzciVar;
        synchronized (this.zzail) {
            if (zzh(zzjuVar)) {
                zzciVar = this.zzarm.get(zzjuVar);
            } else {
                zzciVar = zzftVar != null ? new zzci(this.zzaql, adSizeParcel, zzjuVar, this.zzalo, zzckVar, zzftVar) : new zzcj(this.zzaql, adSizeParcel, zzjuVar, this.zzalo, zzckVar, this.zzaro);
                zzciVar.zza(this);
                this.zzarm.put(zzjuVar, zzciVar);
                this.zzarn.add(zzciVar);
            }
        }
        return zzciVar;
    }

    @Override // com.google.android.gms.internal.zzch
    public final void zza(zzcd zzcdVar) {
        synchronized (this.zzail) {
            if (!zzcdVar.zzha()) {
                this.zzarn.remove(zzcdVar);
                Iterator<Map.Entry<zzju, zzcd>> it = this.zzarm.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue() == zzcdVar) {
                        it.remove();
                    }
                }
            }
        }
    }

    public final void zzi(zzju zzjuVar) {
        synchronized (this.zzail) {
            zzcd zzcdVar = this.zzarm.get(zzjuVar);
            if (zzcdVar != null) {
                zzcdVar.zzgy();
            }
        }
    }

    public final void zzk(zzju zzjuVar) {
        synchronized (this.zzail) {
            zzcd zzcdVar = this.zzarm.get(zzjuVar);
            if (zzcdVar != null) {
                zzcdVar.pause();
            }
        }
    }

    public final void zzl(zzju zzjuVar) {
        synchronized (this.zzail) {
            zzcd zzcdVar = this.zzarm.get(zzjuVar);
            if (zzcdVar != null) {
                zzcdVar.resume();
            }
        }
    }
}
