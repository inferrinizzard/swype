package com.google.android.gms.ads.internal;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.client.zzs;
import com.google.android.gms.ads.internal.client.zzy;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzeb;
import com.google.android.gms.internal.zzec;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzk extends zzs.zza {
    private final Context mContext;
    private final zzd zzajv;
    private final zzgj zzajz;
    private com.google.android.gms.ads.internal.client.zzq zzalf;
    private NativeAdOptionsParcel zzalk;
    private zzy zzalm;
    private final String zzaln;
    private final VersionInfoParcel zzalo;
    private zzeb zzals;
    private zzec zzalt;
    private SimpleArrayMap<String, zzee> zzalv = new SimpleArrayMap<>();
    private SimpleArrayMap<String, zzed> zzalu = new SimpleArrayMap<>();

    public zzk(Context context, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, zzd zzdVar) {
        this.mContext = context;
        this.zzaln = str;
        this.zzajz = zzgjVar;
        this.zzalo = versionInfoParcel;
        this.zzajv = zzdVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(NativeAdOptionsParcel nativeAdOptionsParcel) {
        this.zzalk = nativeAdOptionsParcel;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(zzeb zzebVar) {
        this.zzals = zzebVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(zzec zzecVar) {
        this.zzalt = zzecVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zza(String str, zzee zzeeVar, zzed zzedVar) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Custom template ID for native custom template ad is empty. Please provide a valid template id.");
        }
        this.zzalv.put(str, zzeeVar);
        this.zzalu.put(str, zzedVar);
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zzb(com.google.android.gms.ads.internal.client.zzq zzqVar) {
        this.zzalf = zzqVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public void zzb(zzy zzyVar) {
        this.zzalm = zzyVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzs
    public com.google.android.gms.ads.internal.client.zzr zzes() {
        return new zzj(this.mContext, this.zzaln, this.zzajz, this.zzalo, this.zzalf, this.zzals, this.zzalt, this.zzalv, this.zzalu, this.zzalk, this.zzalm, this.zzajv);
    }
}
