package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzpm;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class zzqy {
    static final zzpm.zza<?, ?>[] vF = new zzpm.zza[0];
    private final Map<Api.zzc<?>, Api.zze> ui;
    final Set<zzpm.zza<?, ?>> vG;
    private final zzb vH;
    zzc vI;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface zzb {
        void zzh(zzpm.zza<?, ?> zzaVar);
    }

    /* loaded from: classes.dex */
    interface zzc {
        void zzaqn();
    }

    public zzqy(Api.zzc<?> zzcVar, Api.zze zzeVar) {
        this.vG = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        this.vH = new zzb() { // from class: com.google.android.gms.internal.zzqy.1
            @Override // com.google.android.gms.internal.zzqy.zzb
            public final void zzh(zzpm.zza<?, ?> zzaVar) {
                zzqy.this.vG.remove(zzaVar);
                if (zzqy.this.vI == null || !zzqy.this.vG.isEmpty()) {
                    return;
                }
                zzqy.this.vI.zzaqn();
            }
        };
        this.vI = null;
        this.ui = new ArrayMap();
        this.ui.put(zzcVar, zzeVar);
    }

    public zzqy(Map<Api.zzc<?>, Api.zze> map) {
        this.vG = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
        this.vH = new zzb() { // from class: com.google.android.gms.internal.zzqy.1
            @Override // com.google.android.gms.internal.zzqy.zzb
            public final void zzh(zzpm.zza<?, ?> zzaVar) {
                zzqy.this.vG.remove(zzaVar);
                if (zzqy.this.vI == null || !zzqy.this.vG.isEmpty()) {
                    return;
                }
                zzqy.this.vI.zzaqn();
            }
        };
        this.vI = null;
        this.ui = map;
    }

    public final void release() {
        for (zzpm.zza zzaVar : (zzpm.zza[]) this.vG.toArray(vF)) {
            zzaVar.zza((zzb) null);
            if (zzaVar.zzaov()) {
                this.vG.remove(zzaVar);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <A extends Api.zzb> void zzg(zzpm.zza<? extends Result, A> zzaVar) {
        this.vG.add(zzaVar);
        zzaVar.zza(this.vH);
    }
}
