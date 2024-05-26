package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.internal.zzib;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;
import com.nuance.connect.internal.common.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@zzin
/* loaded from: classes.dex */
public final class zzif extends zzib {
    private final zzdk zzajn;
    private zzgj zzajz;
    final zzlh zzbgf;
    private zzga zzboe;
    zzfy zzbym;
    protected zzge zzbyn;
    boolean zzbyo;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzif(Context context, zzju.zza zzaVar, zzgj zzgjVar, zzic.zza zzaVar2, zzdk zzdkVar, zzlh zzlhVar) {
        super(context, zzaVar, zzaVar2);
        this.zzajz = zzgjVar;
        this.zzboe = zzaVar.zzcig;
        this.zzajn = zzdkVar;
        this.zzbgf = zzlhVar;
    }

    @Override // com.google.android.gms.internal.zzib, com.google.android.gms.internal.zzkc
    public final void onStop() {
        synchronized (this.zzbxu) {
            super.onStop();
            if (this.zzbym != null) {
                this.zzbym.cancel();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzib
    protected final zzju zzak(int i) {
        AdRequestInfoParcel adRequestInfoParcel = this.zzbxr.zzcip;
        return new zzju(adRequestInfoParcel.zzcar, this.zzbgf, this.zzbxs.zzbnm, i, this.zzbxs.zzbnn, this.zzbxs.zzcca, this.zzbxs.orientation, this.zzbxs.zzbns, adRequestInfoParcel.zzcau, this.zzbxs.zzcby, this.zzbyn != null ? this.zzbyn.zzbon : null, this.zzbyn != null ? this.zzbyn.zzboo : null, this.zzbyn != null ? this.zzbyn.zzbop : AdMobAdapter.class.getName(), this.zzboe, this.zzbyn != null ? this.zzbyn.zzboq : null, this.zzbxs.zzcbz, this.zzbxr.zzapa, this.zzbxs.zzcbx, this.zzbxr.zzcik, this.zzbxs.zzccc, this.zzbxs.zzccd, this.zzbxr.zzcie, null, this.zzbxs.zzccn, this.zzbxs.zzcco, this.zzbxs.zzccp, this.zzboe != null ? this.zzboe.zzbnx : false, this.zzbxs.zzccr, this.zzbym != null ? zzg(this.zzbym.zzmg()) : null, this.zzbxs.zzbnp);
    }

    @Override // com.google.android.gms.internal.zzib
    protected final void zzh(long j) throws zzib.zza {
        Bundle bundle;
        synchronized (this.zzbxu) {
            this.zzbym = this.zzboe.zzbnv != -1 ? new zzgg(this.mContext, this.zzbxr.zzcip, this.zzajz, this.zzboe, this.zzbxs.zzauu, this.zzbxs.zzauw, j, ((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbh)).longValue()) : new zzgh(this.mContext, this.zzbxr.zzcip, this.zzajz, this.zzboe, this.zzbxs.zzauu, this.zzbxs.zzauw, j, ((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbh)).longValue(), this.zzajn);
        }
        ArrayList arrayList = new ArrayList(this.zzboe.zzbnk);
        boolean z = false;
        Bundle bundle2 = this.zzbxr.zzcip.zzcar.zzatw;
        if (bundle2 != null && (bundle = bundle2.getBundle("com.google.ads.mediation.admob.AdMobAdapter")) != null) {
            z = bundle.getBoolean("_skipMediation");
        }
        if (z) {
            ListIterator listIterator = arrayList.listIterator();
            while (listIterator.hasNext()) {
                if (!((zzfz) listIterator.next()).zzbmw.contains("com.google.ads.mediation.admob.AdMobAdapter")) {
                    listIterator.remove();
                }
            }
        }
        this.zzbyn = this.zzbym.zzd(arrayList);
        switch (this.zzbyn.zzbom) {
            case 0:
                if (this.zzbyn.zzbon == null || this.zzbyn.zzbon.zzbnf == null) {
                    return;
                }
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzif.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        synchronized (zzif.this.zzbxu) {
                            zzif.this.zzbyo = com.google.android.gms.ads.internal.zzn.zza(zzif.this.zzbgf, zzif.this.zzbyn, countDownLatch);
                        }
                    }
                });
                try {
                    countDownLatch.await(10L, TimeUnit.SECONDS);
                    synchronized (this.zzbxu) {
                        if (!this.zzbyo) {
                            throw new zzib.zza("View could not be prepared", 0);
                        }
                        if (this.zzbgf.isDestroyed()) {
                            throw new zzib.zza("Assets not loaded, web view is destroyed", 0);
                        }
                    }
                    return;
                } catch (InterruptedException e) {
                    String valueOf = String.valueOf(e);
                    throw new zzib.zza(new StringBuilder(String.valueOf(valueOf).length() + 38).append("Interrupted while waiting for latch : ").append(valueOf).toString(), 0);
                }
            case 1:
                throw new zzib.zza("No fill from any mediation ad networks.", 3);
            default:
                throw new zzib.zza(new StringBuilder(40).append("Unexpected mediation result: ").append(this.zzbyn.zzbom).toString(), 0);
        }
    }

    private static String zzg(List<zzge> list) {
        int i;
        if (list == null) {
            return "".toString();
        }
        String str = "";
        for (zzge zzgeVar : list) {
            if (zzgeVar != null && zzgeVar.zzbon != null && !TextUtils.isEmpty(zzgeVar.zzbon.zzbmx)) {
                String valueOf = String.valueOf(str);
                String str2 = zzgeVar.zzbon.zzbmx;
                switch (zzgeVar.zzbom) {
                    case -1:
                        i = 4;
                        break;
                    case 0:
                        i = 0;
                        break;
                    case 1:
                        i = 1;
                        break;
                    case 2:
                    default:
                        i = 6;
                        break;
                    case 3:
                        i = 2;
                        break;
                    case 4:
                        i = 3;
                        break;
                    case 5:
                        i = 5;
                        break;
                }
                String valueOf2 = String.valueOf(new StringBuilder(String.valueOf(str2).length() + 33).append(str2).append(".").append(i).append(".").append(zzgeVar.zzbos).toString());
                str = new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(valueOf2).length()).append(valueOf).append(valueOf2).append(Document.ID_SEPARATOR).toString();
            }
        }
        return str.substring(0, Math.max(0, str.length() - 1));
    }
}
