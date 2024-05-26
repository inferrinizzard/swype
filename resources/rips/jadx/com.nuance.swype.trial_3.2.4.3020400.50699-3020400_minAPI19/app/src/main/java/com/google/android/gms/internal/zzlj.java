package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;

@zzin
/* loaded from: classes.dex */
public final class zzlj {
    public static zzlh zza(Context context, AdSizeParcel adSizeParcel, boolean z, boolean z2, zzas zzasVar, VersionInfoParcel versionInfoParcel, zzdk zzdkVar, com.google.android.gms.ads.internal.zzs zzsVar, com.google.android.gms.ads.internal.zzd zzdVar) {
        zzlk zzlkVar = new zzlk(zzll.zzb$71c2c049(context, adSizeParcel, z, zzasVar, versionInfoParcel, zzdkVar, zzsVar, zzdVar));
        zzlkVar.setWebViewClient(com.google.android.gms.ads.internal.zzu.zzfs().zzb(zzlkVar, z2));
        zzlkVar.setWebChromeClient(com.google.android.gms.ads.internal.zzu.zzfs().zzk(zzlkVar));
        return zzlkVar;
    }
}
