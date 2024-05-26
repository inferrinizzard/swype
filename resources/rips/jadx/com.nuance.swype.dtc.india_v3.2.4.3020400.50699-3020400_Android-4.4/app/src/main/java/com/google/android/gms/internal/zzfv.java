package com.google.android.gms.internal;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzfv implements zzfu {
    private final zzft zzbms;
    private final HashSet<AbstractMap.SimpleEntry<String, zzep>> zzbmt = new HashSet<>();

    public zzfv(zzft zzftVar) {
        this.zzbms = zzftVar;
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(String str, zzep zzepVar) {
        this.zzbms.zza(str, zzepVar);
        this.zzbmt.add(new AbstractMap.SimpleEntry<>(str, zzepVar));
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(String str, JSONObject jSONObject) {
        this.zzbms.zza(str, jSONObject);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, zzep zzepVar) {
        this.zzbms.zzb(str, zzepVar);
        this.zzbmt.remove(new AbstractMap.SimpleEntry(str, zzepVar));
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, JSONObject jSONObject) {
        this.zzbms.zzb(str, jSONObject);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzj(String str, String str2) {
        this.zzbms.zzj(str, str2);
    }

    @Override // com.google.android.gms.internal.zzfu
    public final void zzmf() {
        Iterator<AbstractMap.SimpleEntry<String, zzep>> it = this.zzbmt.iterator();
        while (it.hasNext()) {
            AbstractMap.SimpleEntry<String, zzep> next = it.next();
            String valueOf = String.valueOf(next.getValue().toString());
            zzkd.v(valueOf.length() != 0 ? "Unregistering eventhandler: ".concat(valueOf) : new String("Unregistering eventhandler: "));
            this.zzbms.zzb(next.getKey(), next.getValue());
        }
        this.zzbmt.clear();
    }
}
