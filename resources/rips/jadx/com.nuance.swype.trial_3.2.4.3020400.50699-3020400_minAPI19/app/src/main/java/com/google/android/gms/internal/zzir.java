package com.google.android.gms.internal;

import com.google.android.gms.internal.zzfs;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzir {
    String zzbvq;
    String zzcem;
    zzfs.zzc zzceo;
    final Object zzail = new Object();
    zzkv<zziu> zzcen = new zzkv<>();
    public final zzep zzcep = new zzep() { // from class: com.google.android.gms.internal.zzir.1
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            synchronized (zzir.this.zzail) {
                if (zzir.this.zzcen.isDone()) {
                    return;
                }
                if (zzir.this.zzbvq.equals(map.get("request_id"))) {
                    zziu zziuVar = new zziu(1, map);
                    String valueOf = String.valueOf(zziuVar.zzcfz);
                    String valueOf2 = String.valueOf(zziuVar.zzcfu);
                    zzkd.zzcx(new StringBuilder(String.valueOf(valueOf).length() + 24 + String.valueOf(valueOf2).length()).append("Invalid ").append(valueOf).append(" request error: ").append(valueOf2).toString());
                    zzir.this.zzcen.zzh(zziuVar);
                }
            }
        }
    };
    public final zzep zzceq = new zzep() { // from class: com.google.android.gms.internal.zzir.2
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            synchronized (zzir.this.zzail) {
                if (zzir.this.zzcen.isDone()) {
                    return;
                }
                zziu zziuVar = new zziu(-2, map);
                if (zzir.this.zzbvq.equals(zziuVar.zzbvq)) {
                    String str = zziuVar.zzae;
                    if (str == null) {
                        zzkd.zzcx("URL missing in loadAdUrl GMSG.");
                        return;
                    }
                    if (str.contains("%40mediation_adapters%40")) {
                        String replaceAll = str.replaceAll("%40mediation_adapters%40", zzkb.zza(zzlhVar.getContext(), map.get("check_adapters"), zzir.this.zzcem));
                        zziuVar.zzae = replaceAll;
                        String valueOf = String.valueOf(replaceAll);
                        zzkd.v(valueOf.length() != 0 ? "Ad request URL modified to ".concat(valueOf) : new String("Ad request URL modified to "));
                    }
                    zzir.this.zzcen.zzh(zziuVar);
                }
            }
        }
    };
    public final zzep zzcer = new zzep() { // from class: com.google.android.gms.internal.zzir.3
        @Override // com.google.android.gms.internal.zzep
        public final void zza(zzlh zzlhVar, Map<String, String> map) {
            synchronized (zzir.this.zzail) {
                if (zzir.this.zzcen.isDone()) {
                    return;
                }
                zziu zziuVar = new zziu(-2, map);
                if (zzir.this.zzbvq.equals(zziuVar.zzbvq)) {
                    zzir.this.zzcen.zzh(zziuVar);
                }
            }
        }
    };

    public zzir(String str, String str2) {
        this.zzcem = str2;
        this.zzbvq = str;
    }
}
