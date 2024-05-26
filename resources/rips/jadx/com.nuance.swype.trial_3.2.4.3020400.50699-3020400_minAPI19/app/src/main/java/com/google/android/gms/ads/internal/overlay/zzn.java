package com.google.android.gms.ads.internal.overlay;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.google.android.gms.internal.zzdi;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzlh;

@zzin
/* loaded from: classes.dex */
public class zzn extends zzj {
    @Override // com.google.android.gms.ads.internal.overlay.zzj
    public zzi zza(Context context, zzlh zzlhVar, int i, boolean z, zzdk zzdkVar, zzdi zzdiVar) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (com.google.android.gms.common.util.zzs.zzhb(14) && (applicationInfo == null || applicationInfo.targetSdkVersion >= 11)) {
            return new zzc(context, z, zzlhVar.zzdn().zzaus, new zzx(context, zzlhVar.zzum(), zzlhVar.getRequestId(), zzdkVar, zzdiVar));
        }
        return null;
    }
}
