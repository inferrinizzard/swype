package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.gms.internal.zzhd;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzhe extends zzhf implements zzep {
    private final Context mContext;
    private final WindowManager zzaqm;
    private final zzlh zzbgf;
    private final zzcu zzbrc;
    DisplayMetrics zzbrd;
    private float zzbre;
    int zzbrf;
    int zzbrg;
    private int zzbrh;
    int zzbri;
    int zzbrj;
    int zzbrk;
    int zzbrl;

    public zzhe(zzlh zzlhVar, Context context, zzcu zzcuVar) {
        super(zzlhVar);
        this.zzbrf = -1;
        this.zzbrg = -1;
        this.zzbri = -1;
        this.zzbrj = -1;
        this.zzbrk = -1;
        this.zzbrl = -1;
        this.zzbgf = zzlhVar;
        this.mContext = context;
        this.zzbrc = zzcuVar;
        this.zzaqm = (WindowManager) context.getSystemService("window");
    }

    public final void zze(int i, int i2) {
        int i3;
        if (this.mContext instanceof Activity) {
            com.google.android.gms.ads.internal.zzu.zzfq();
            i3 = zzkh.zzk((Activity) this.mContext)[0];
        } else {
            i3 = 0;
        }
        int i4 = i2 - i3;
        try {
            super.zzbgf.zzb("onDefaultPositionReceived", new JSONObject().put("x", i).put("y", i4).put("width", this.zzbrk).put("height", this.zzbrl));
        } catch (JSONException e) {
            zzkd.zzb("Error occured while dispatching default position.", e);
        }
        zzli zzuj = this.zzbgf.zzuj();
        if (zzuj.zzbiu != null) {
            zzha zzhaVar = zzuj.zzbiu;
            zzhaVar.zzbqh = i;
            zzhaVar.zzbqi = i2;
        }
    }

    @Override // com.google.android.gms.internal.zzep
    public final void zza(zzlh zzlhVar, Map<String, String> map) {
        this.zzbrd = new DisplayMetrics();
        Display defaultDisplay = this.zzaqm.getDefaultDisplay();
        defaultDisplay.getMetrics(this.zzbrd);
        this.zzbre = this.zzbrd.density;
        this.zzbrh = defaultDisplay.getRotation();
        this.zzbrf = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.zzbrd, this.zzbrd.widthPixels);
        this.zzbrg = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.zzbrd, this.zzbrd.heightPixels);
        Activity zzue = this.zzbgf.zzue();
        if (zzue == null || zzue.getWindow() == null) {
            this.zzbri = this.zzbrf;
            this.zzbrj = this.zzbrg;
        } else {
            com.google.android.gms.ads.internal.zzu.zzfq();
            int[] zzh = zzkh.zzh(zzue);
            this.zzbri = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.zzbrd, zzh[0]);
            this.zzbrj = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.zzbrd, zzh[1]);
        }
        if (this.zzbgf.zzdn().zzaus) {
            this.zzbrk = this.zzbrf;
            this.zzbrl = this.zzbrg;
        } else {
            this.zzbgf.measure(0, 0);
            this.zzbrk = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.mContext, this.zzbgf.getMeasuredWidth());
            this.zzbrl = com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.mContext, this.zzbgf.getMeasuredHeight());
        }
        zza(this.zzbrf, this.zzbrg, this.zzbri, this.zzbrj, this.zzbre, this.zzbrh);
        zzhd.zza zzaVar = new zzhd.zza();
        zzcu zzcuVar = this.zzbrc;
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:"));
        zzaVar.zzbqy = zzcuVar.zza(intent);
        zzcu zzcuVar2 = this.zzbrc;
        Intent intent2 = new Intent("android.intent.action.VIEW");
        intent2.setData(Uri.parse("sms:"));
        zzaVar.zzbqx = zzcuVar2.zza(intent2);
        zzaVar.zzbqz = this.zzbrc.zzju();
        zzaVar.zzbra = this.zzbrc.zzjr();
        zzaVar.zzbrb = true;
        this.zzbgf.zzb("onDeviceFeaturesReceived", new zzhd(zzaVar, (byte) 0).toJson());
        int[] iArr = new int[2];
        this.zzbgf.getLocationOnScreen(iArr);
        zze(com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.mContext, iArr[0]), com.google.android.gms.ads.internal.client.zzm.zziw().zzb(this.mContext, iArr[1]));
        if (zzkd.zzaz(2)) {
            zzkd.zzcw("Dispatching Ready Event.");
        }
        try {
            super.zzbgf.zzb("onReadyEventReceived", new JSONObject().put("js", this.zzbgf.zzum().zzcs));
        } catch (JSONException e) {
            zzkd.zzb("Error occured while dispatching ready Event.", e);
        }
    }
}
