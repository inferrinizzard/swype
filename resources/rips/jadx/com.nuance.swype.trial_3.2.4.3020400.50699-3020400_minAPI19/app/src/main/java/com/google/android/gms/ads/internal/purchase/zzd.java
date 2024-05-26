package com.google.android.gms.ads.internal.purchase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.internal.zzhn;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.nuance.connect.common.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public class zzd extends zzhn.zza {
    private Context mContext;
    private String zzarj;
    private String zzbwy;
    private ArrayList<String> zzbwz;

    public zzd(String str, ArrayList<String> arrayList, Context context, String str2) {
        this.zzbwy = str;
        this.zzbwz = arrayList;
        this.zzarj = str2;
        this.mContext = context;
    }

    private void zzps() {
        try {
            this.mContext.getClassLoader().loadClass("com.google.ads.conversiontracking.IAPConversionReporter").getDeclaredMethod("reportWithProductId", Context.class, String.class, String.class, Boolean.TYPE).invoke(null, this.mContext, this.zzbwy, "", true);
        } catch (ClassNotFoundException e) {
            zzkd.zzcx("Google Conversion Tracking SDK 1.2.0 or above is required to report a conversion.");
        } catch (NoSuchMethodException e2) {
            zzkd.zzcx("Google Conversion Tracking SDK 1.2.0 or above is required to report a conversion.");
        } catch (Exception e3) {
            zzkd.zzd("Fail to report a conversion.", e3);
        }
    }

    @Override // com.google.android.gms.internal.zzhn
    public String getProductId() {
        return this.zzbwy;
    }

    @Override // com.google.android.gms.internal.zzhn
    public void recordResolution(int i) {
        if (i == 1) {
            zzps();
        }
        Map<String, String> zzpr = zzpr();
        zzpr.put("status", String.valueOf(i));
        zzpr.put(Strings.MESSAGE_BUNDLE_CATALOG_SKU, this.zzbwy);
        LinkedList linkedList = new LinkedList();
        Iterator<String> it = this.zzbwz.iterator();
        while (it.hasNext()) {
            String next = it.next();
            zzu.zzfq();
            linkedList.add(zzkh.zzb(next, zzpr));
        }
        zzu.zzfq();
        zzkh.zza(this.mContext, this.zzarj, linkedList);
    }

    @Override // com.google.android.gms.internal.zzhn
    public void recordPlayBillingResolution(int i) {
        int i2 = 1;
        if (i == 0) {
            zzps();
        }
        Map<String, String> zzpr = zzpr();
        zzpr.put("google_play_status", String.valueOf(i));
        zzpr.put(Strings.MESSAGE_BUNDLE_CATALOG_SKU, this.zzbwy);
        if (i != 0) {
            i2 = i == 1 ? 2 : i == 4 ? 3 : 0;
        }
        zzpr.put("status", String.valueOf(i2));
        LinkedList linkedList = new LinkedList();
        Iterator<String> it = this.zzbwz.iterator();
        while (it.hasNext()) {
            String next = it.next();
            zzu.zzfq();
            linkedList.add(zzkh.zzb(next, zzpr));
        }
        zzu.zzfq();
        zzkh.zza(this.mContext, this.zzarj, linkedList);
    }

    private Map<String, String> zzpr() {
        String packageName = this.mContext.getPackageName();
        String str = "";
        try {
            str = this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            zzkd.zzd("Error to retrieve app version", e);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() - zzu.zzft().zzsk().zzckd;
        HashMap hashMap = new HashMap();
        hashMap.put("sessionid", zzu.zzft().zzcjm);
        hashMap.put("appid", packageName);
        hashMap.put("osversion", String.valueOf(Build.VERSION.SDK_INT));
        hashMap.put("sdkversion", this.zzarj);
        hashMap.put("appversion", str);
        hashMap.put("timestamp", String.valueOf(elapsedRealtime));
        return hashMap;
    }
}
