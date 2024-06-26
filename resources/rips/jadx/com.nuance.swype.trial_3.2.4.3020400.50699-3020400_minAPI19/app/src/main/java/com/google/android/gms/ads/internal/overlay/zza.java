package com.google.android.gms.ads.internal.overlay;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;

@zzin
/* loaded from: classes.dex */
public class zza {
    public boolean zza(Context context, Intent intent, zzp zzpVar) {
        try {
            String valueOf = String.valueOf(intent.toURI());
            zzkd.v(valueOf.length() != 0 ? "Launching an intent: ".concat(valueOf) : new String("Launching an intent: "));
            com.google.android.gms.ads.internal.zzu.zzfq();
            zzkh.zzb(context, intent);
            if (zzpVar != null) {
                zzpVar.zzdo();
            }
            return true;
        } catch (ActivityNotFoundException e) {
            zzkd.zzcx(e.getMessage());
            return false;
        }
    }

    public boolean zza(Context context, AdLauncherIntentInfoParcel adLauncherIntentInfoParcel, zzp zzpVar) {
        int i;
        if (adLauncherIntentInfoParcel == null) {
            zzkd.zzcx("No intent data for launcher overlay.");
            return false;
        }
        if (adLauncherIntentInfoParcel.intent != null) {
            return zza(context, adLauncherIntentInfoParcel.intent, zzpVar);
        }
        Intent intent = new Intent();
        if (TextUtils.isEmpty(adLauncherIntentInfoParcel.url)) {
            zzkd.zzcx("Open GMSG did not contain a URL.");
            return false;
        }
        if (TextUtils.isEmpty(adLauncherIntentInfoParcel.mimeType)) {
            intent.setData(Uri.parse(adLauncherIntentInfoParcel.url));
        } else {
            intent.setDataAndType(Uri.parse(adLauncherIntentInfoParcel.url), adLauncherIntentInfoParcel.mimeType);
        }
        intent.setAction("android.intent.action.VIEW");
        if (!TextUtils.isEmpty(adLauncherIntentInfoParcel.packageName)) {
            intent.setPackage(adLauncherIntentInfoParcel.packageName);
        }
        if (!TextUtils.isEmpty(adLauncherIntentInfoParcel.zzbro)) {
            String[] split = adLauncherIntentInfoParcel.zzbro.split("/", 2);
            if (split.length < 2) {
                String valueOf = String.valueOf(adLauncherIntentInfoParcel.zzbro);
                zzkd.zzcx(valueOf.length() != 0 ? "Could not parse component name from open GMSG: ".concat(valueOf) : new String("Could not parse component name from open GMSG: "));
                return false;
            }
            intent.setClassName(split[0], split[1]);
        }
        String str = adLauncherIntentInfoParcel.zzbrp;
        if (!TextUtils.isEmpty(str)) {
            try {
                i = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                zzkd.zzcx("Could not parse intent flags.");
                i = 0;
            }
            intent.addFlags(i);
        }
        return zza(context, intent, zzpVar);
    }
}
