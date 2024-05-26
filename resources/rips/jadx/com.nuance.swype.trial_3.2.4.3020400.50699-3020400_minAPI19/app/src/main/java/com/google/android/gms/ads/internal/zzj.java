package com.google.android.gms.ads.internal;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.zzr;
import com.google.android.gms.ads.internal.client.zzy;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzeb;
import com.google.android.gms.internal.zzec;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkh;
import com.nuance.connect.comm.MessageAPI;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zzj extends zzr.zza {
    final Context mContext;
    final zzd zzajv;
    final zzgj zzajz;
    private final com.google.android.gms.ads.internal.client.zzq zzalf;
    private final zzeb zzalg;
    private final zzec zzalh;
    private final SimpleArrayMap<String, zzee> zzali;
    private final SimpleArrayMap<String, zzed> zzalj;
    private final NativeAdOptionsParcel zzalk;
    private final zzy zzalm;
    final String zzaln;
    final VersionInfoParcel zzalo;
    private WeakReference<zzq> zzalp;
    private final Object zzail = new Object();
    private final List<String> zzall = zzeq();

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzj(Context context, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, com.google.android.gms.ads.internal.client.zzq zzqVar, zzeb zzebVar, zzec zzecVar, SimpleArrayMap<String, zzee> simpleArrayMap, SimpleArrayMap<String, zzed> simpleArrayMap2, NativeAdOptionsParcel nativeAdOptionsParcel, zzy zzyVar, zzd zzdVar) {
        this.mContext = context;
        this.zzaln = str;
        this.zzajz = zzgjVar;
        this.zzalo = versionInfoParcel;
        this.zzalf = zzqVar;
        this.zzalh = zzecVar;
        this.zzalg = zzebVar;
        this.zzali = simpleArrayMap;
        this.zzalj = simpleArrayMap2;
        this.zzalk = nativeAdOptionsParcel;
        this.zzalm = zzyVar;
        this.zzajv = zzdVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> zzeq() {
        ArrayList arrayList = new ArrayList();
        if (this.zzalh != null) {
            arrayList.add("1");
        }
        if (this.zzalg != null) {
            arrayList.add(MessageAPI.DELAYED_FROM);
        }
        if (this.zzali.size() > 0) {
            arrayList.add(MessageAPI.SESSION_ID);
        }
        return arrayList;
    }

    @Override // com.google.android.gms.ads.internal.client.zzr
    public String getMediationAdapterClassName() {
        synchronized (this.zzail) {
            if (this.zzalp == null) {
                return null;
            }
            zzq zzqVar = this.zzalp.get();
            return zzqVar != null ? zzqVar.getMediationAdapterClassName() : null;
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzr
    public boolean isLoading() {
        synchronized (this.zzail) {
            if (this.zzalp == null) {
                return false;
            }
            zzq zzqVar = this.zzalp.get();
            return zzqVar != null ? zzqVar.isLoading() : false;
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzr
    public void zzf(final AdRequestParcel adRequestParcel) {
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzj.1
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (zzj.this.zzail) {
                    zzj zzjVar = zzj.this;
                    zzq zzqVar = new zzq(zzjVar.mContext, zzjVar.zzajv, AdSizeParcel.zzk(zzjVar.mContext), zzjVar.zzaln, zzjVar.zzajz, zzjVar.zzalo);
                    zzj.this.zzalp = new WeakReference(zzqVar);
                    zzqVar.zzb(zzj.this.zzalg);
                    zzqVar.zzb(zzj.this.zzalh);
                    zzqVar.zza(zzj.this.zzali);
                    zzqVar.zza(zzj.this.zzalf);
                    zzqVar.zzb(zzj.this.zzalj);
                    zzqVar.zzb(zzj.this.zzeq());
                    zzqVar.zzb(zzj.this.zzalk);
                    zzqVar.zza(zzj.this.zzalm);
                    zzqVar.zzb(adRequestParcel);
                }
            }
        });
    }
}
