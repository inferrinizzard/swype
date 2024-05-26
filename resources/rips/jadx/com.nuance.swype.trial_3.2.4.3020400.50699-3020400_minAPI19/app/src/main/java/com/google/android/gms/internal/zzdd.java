package com.google.android.gms.internal;

import android.content.Context;
import android.os.Build;
import com.facebook.internal.ServerProtocol;
import com.nuance.connect.comm.MessageAPI;
import java.util.LinkedHashMap;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzdd {
    Context mContext;
    String zzarj;
    boolean zzbdo = ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzaze)).booleanValue();
    String zzbdp = (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazf);
    Map<String, String> zzbdq = new LinkedHashMap();

    public zzdd(Context context, String str) {
        this.mContext = null;
        this.zzarj = null;
        this.mContext = context;
        this.zzarj = str;
        this.zzbdq.put("s", "gmob_sdk");
        this.zzbdq.put("v", MessageAPI.SESSION_ID);
        this.zzbdq.put("os", Build.VERSION.RELEASE);
        this.zzbdq.put(ServerProtocol.DIALOG_PARAM_SDK_VERSION, Build.VERSION.SDK);
        Map<String, String> map = this.zzbdq;
        com.google.android.gms.ads.internal.zzu.zzfq();
        map.put("device", zzkh.zztg());
        this.zzbdq.put("app", context.getApplicationContext() != null ? context.getApplicationContext().getPackageName() : context.getPackageName());
        Map<String, String> map2 = this.zzbdq;
        com.google.android.gms.ads.internal.zzu.zzfq();
        map2.put("is_lite_sdk", zzkh.zzan(context) ? "1" : "0");
        zziv zzy = com.google.android.gms.ads.internal.zzu.zzfw().zzy(this.mContext);
        this.zzbdq.put("network_coarse", Integer.toString(zzy.zzcgp));
        this.zzbdq.put("network_fine", Integer.toString(zzy.zzcgq));
    }
}
