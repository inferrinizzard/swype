package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import java.util.ArrayList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzgh implements zzfy {
    private final Context mContext;
    private final zzdk zzajn;
    private final zzgj zzajz;
    private final boolean zzarl;
    private final boolean zzawn;
    private final zzga zzboe;
    private final AdRequestInfoParcel zzbot;
    private final long zzbou;
    private final long zzbov;
    private zzgd zzbpd;
    private final Object zzail = new Object();
    private boolean zzbox = false;
    private List<zzge> zzboz = new ArrayList();

    public zzgh(Context context, AdRequestInfoParcel adRequestInfoParcel, zzgj zzgjVar, zzga zzgaVar, boolean z, boolean z2, long j, long j2, zzdk zzdkVar) {
        this.mContext = context;
        this.zzbot = adRequestInfoParcel;
        this.zzajz = zzgjVar;
        this.zzboe = zzgaVar;
        this.zzarl = z;
        this.zzawn = z2;
        this.zzbou = j;
        this.zzbov = j2;
        this.zzajn = zzdkVar;
    }

    @Override // com.google.android.gms.internal.zzfy
    public final void cancel() {
        synchronized (this.zzail) {
            this.zzbox = true;
            if (this.zzbpd != null) {
                this.zzbpd.cancel();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzfy
    public final zzge zzd(List<zzfz> list) {
        zzkd.zzcv("Starting mediation.");
        ArrayList arrayList = new ArrayList();
        zzdi zzkg = this.zzajn.zzkg();
        for (zzfz zzfzVar : list) {
            String valueOf = String.valueOf(zzfzVar.zzbmv);
            zzkd.zzcw(valueOf.length() != 0 ? "Trying mediation network: ".concat(valueOf) : new String("Trying mediation network: "));
            for (String str : zzfzVar.zzbmw) {
                zzdi zzkg2 = this.zzajn.zzkg();
                synchronized (this.zzail) {
                    if (this.zzbox) {
                        return new zzge(-1);
                    }
                    this.zzbpd = new zzgd(this.mContext, str, this.zzajz, this.zzboe, zzfzVar, this.zzbot.zzcar, this.zzbot.zzapa, this.zzbot.zzaow, this.zzarl, this.zzawn, this.zzbot.zzapo, this.zzbot.zzaps);
                    final zzge zza = this.zzbpd.zza(this.zzbou, this.zzbov);
                    this.zzboz.add(zza);
                    if (zza.zzbom == 0) {
                        zzkd.zzcv("Adapter succeeded.");
                        this.zzajn.zzh("mediation_network_succeed", str);
                        if (!arrayList.isEmpty()) {
                            this.zzajn.zzh("mediation_networks_fail", TextUtils.join(",", arrayList));
                        }
                        this.zzajn.zza(zzkg2, "mls");
                        this.zzajn.zza(zzkg, "ttm");
                        return zza;
                    }
                    arrayList.add(str);
                    this.zzajn.zza(zzkg2, "mlf");
                    if (zza.zzboo != null) {
                        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzgh.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                try {
                                    zza.zzboo.destroy();
                                } catch (RemoteException e) {
                                    zzkd.zzd("Could not destroy mediation adapter.", e);
                                }
                            }
                        });
                    }
                }
            }
        }
        if (!arrayList.isEmpty()) {
            this.zzajn.zzh("mediation_networks_fail", TextUtils.join(",", arrayList));
        }
        return new zzge(1);
    }

    @Override // com.google.android.gms.internal.zzfy
    public final List<zzge> zzmg() {
        return this.zzboz;
    }
}
