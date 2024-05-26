package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzju;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Future;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzjl extends zzkc implements zzjk {
    private final Context mContext;
    private final zzju.zza zzbxr;
    final zzjf zzchz;
    private final ArrayList<Future> zzchw = new ArrayList<>();
    private final ArrayList<String> zzchx = new ArrayList<>();
    private final HashSet<String> zzchy = new HashSet<>();
    private final Object zzail = new Object();

    public zzjl(Context context, zzju.zza zzaVar, zzjf zzjfVar) {
        this.mContext = context;
        this.zzbxr = zzaVar;
        this.zzchz = zzjfVar;
    }

    private zzju zza(int i, String str, zzfz zzfzVar) {
        return new zzju(this.zzbxr.zzcip.zzcar, null, this.zzbxr.zzciq.zzbnm, i, this.zzbxr.zzciq.zzbnn, this.zzbxr.zzciq.zzcca, this.zzbxr.zzciq.orientation, this.zzbxr.zzciq.zzbns, this.zzbxr.zzcip.zzcau, this.zzbxr.zzciq.zzcby, zzfzVar, null, str, this.zzbxr.zzcig, null, this.zzbxr.zzciq.zzcbz, this.zzbxr.zzapa, this.zzbxr.zzciq.zzcbx, this.zzbxr.zzcik, this.zzbxr.zzciq.zzccc, this.zzbxr.zzciq.zzccd, this.zzbxr.zzcie, null, this.zzbxr.zzciq.zzccn, this.zzbxr.zzciq.zzcco, this.zzbxr.zzciq.zzccp, this.zzbxr.zzciq.zzccq, this.zzbxr.zzciq.zzccr, null, this.zzbxr.zzciq.zzbnp);
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void onStop() {
    }

    @Override // com.google.android.gms.internal.zzjk
    public final void zza$505cff1c(int i) {
    }

    @Override // com.google.android.gms.internal.zzjk
    public final void zzcg(String str) {
        synchronized (this.zzail) {
            this.zzchy.add(str);
        }
    }

    @Override // com.google.android.gms.internal.zzkc
    public final void zzew() {
        String str;
        for (zzfz zzfzVar : this.zzbxr.zzcig.zzbnk) {
            String str2 = zzfzVar.zzbnc;
            for (String str3 : zzfzVar.zzbmw) {
                if ("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter".equals(str3)) {
                    try {
                        str = new JSONObject(str2).getString("class_name");
                    } catch (JSONException e) {
                        zzkd.zzb("Unable to determine custom event class name, skipping...", e);
                    }
                } else {
                    str = str3;
                }
                String str4 = zzfzVar.zzbmu;
                synchronized (this.zzail) {
                    zzjm zzcf = this.zzchz.zzcf(str);
                    if (zzcf != null && zzcf.zzcib != null && zzcf.zzbog != null) {
                        this.zzchw.add((Future) new zzjg(this.mContext, str, str2, str4, this.zzbxr, zzcf, this).zzpy());
                        this.zzchx.add(str);
                    }
                }
            }
        }
        for (int i = 0; i < this.zzchw.size(); i++) {
            try {
                this.zzchw.get(i).get();
                synchronized (this.zzail) {
                    if (this.zzchy.contains(this.zzchx.get(i))) {
                        final zzju zza = zza(-2, this.zzchx.get(i), this.zzbxr.zzcig.zzbnk.get(i));
                        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzjl.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                zzjl.this.zzchz.zzb(zza);
                            }
                        });
                        return;
                    }
                }
            } catch (InterruptedException e2) {
            } catch (Exception e3) {
            }
        }
        final zzju zza2 = zza(3, null, null);
        com.google.android.gms.ads.internal.util.client.zza.zzcnb.post(new Runnable() { // from class: com.google.android.gms.internal.zzjl.2
            @Override // java.lang.Runnable
            public final void run() {
                zzjl.this.zzchz.zzb(zza2);
            }
        });
    }
}
