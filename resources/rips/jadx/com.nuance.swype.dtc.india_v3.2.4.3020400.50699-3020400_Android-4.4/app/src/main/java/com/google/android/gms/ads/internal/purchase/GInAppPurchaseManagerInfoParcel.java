package com.google.android.gms.ads.internal.purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.internal.zzhn;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class GInAppPurchaseManagerInfoParcel extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zza CREATOR = new zza();
    public final int versionCode;
    public final zzk zzapt;
    public final zzhn zzbwm;
    public final Context zzbwn;
    public final zzj zzbwo;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GInAppPurchaseManagerInfoParcel(int i, IBinder iBinder, IBinder iBinder2, IBinder iBinder3, IBinder iBinder4) {
        this.versionCode = i;
        this.zzapt = (zzk) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder));
        this.zzbwm = (zzhn) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder2));
        this.zzbwn = (Context) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder3));
        this.zzbwo = (zzj) com.google.android.gms.dynamic.zze.zzad(zzd.zza.zzfc(iBinder4));
    }

    public GInAppPurchaseManagerInfoParcel(Context context, zzk zzkVar, zzhn zzhnVar, zzj zzjVar) {
        this.versionCode = 2;
        this.zzbwn = context;
        this.zzapt = zzkVar;
        this.zzbwm = zzhnVar;
        this.zzbwo = zzjVar;
    }

    public static void zza(Intent intent, GInAppPurchaseManagerInfoParcel gInAppPurchaseManagerInfoParcel) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo", gInAppPurchaseManagerInfoParcel);
        intent.putExtra("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo", bundle);
    }

    public static GInAppPurchaseManagerInfoParcel zzc(Intent intent) {
        try {
            Bundle bundleExtra = intent.getBundleExtra("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo");
            bundleExtra.setClassLoader(GInAppPurchaseManagerInfoParcel.class.getClassLoader());
            return (GInAppPurchaseManagerInfoParcel) bundleExtra.getParcelable("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo");
        } catch (Exception e) {
            return null;
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zza.zza$629388ef(this, parcel);
    }
}
