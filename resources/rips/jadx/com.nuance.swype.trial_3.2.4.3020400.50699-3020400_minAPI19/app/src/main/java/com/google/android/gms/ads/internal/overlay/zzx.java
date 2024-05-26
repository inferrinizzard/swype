package com.google.android.gms.ads.internal.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdg;
import com.google.android.gms.internal.zzdi;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkm;
import java.util.concurrent.TimeUnit;

@zzin
/* loaded from: classes.dex */
public class zzx {
    private final Context mContext;
    private final VersionInfoParcel zzamw;
    private final String zzbvq;
    private final zzdi zzbvr;
    private final zzdk zzbvs;
    private final long[] zzbvu;
    private final String[] zzbvv;
    private zzdi zzbvw;
    private zzdi zzbvx;
    private zzdi zzbvy;
    private zzdi zzbvz;
    private boolean zzbwa;
    private zzi zzbwb;
    private boolean zzbwc;
    private boolean zzbwd;
    private final zzkm zzbvt = new zzkm(new zzkm.zzb().zza("min_1", Double.MIN_VALUE, 1.0d).zza("1_5", 1.0d, 5.0d).zza("5_10", 5.0d, 10.0d).zza("10_20", 10.0d, 20.0d).zza("20_30", 20.0d, 30.0d).zza("30_max", 30.0d, Double.MAX_VALUE), (byte) 0);
    private long zzbwe = -1;

    public void zza(zzi zziVar) {
        zzdg.zza(this.zzbvs, this.zzbvr, "vpc");
        this.zzbvw = zzdg.zzb(this.zzbvs);
        if (this.zzbvs != null) {
            this.zzbvs.zzh("vpn", zziVar.zzni());
        }
        this.zzbwb = zziVar;
    }

    public void zzoj() {
        if (this.zzbvw == null || this.zzbvx != null) {
            return;
        }
        zzdg.zza(this.zzbvs, this.zzbvw, "vfr");
        this.zzbvx = zzdg.zzb(this.zzbvs);
    }

    public void zzpi() {
        this.zzbwa = true;
        if (this.zzbvx == null || this.zzbvy != null) {
            return;
        }
        zzdg.zza(this.zzbvs, this.zzbvx, "vfp");
        this.zzbvy = zzdg.zzb(this.zzbvs);
    }

    public void zzpj() {
        this.zzbwa = false;
    }

    public zzx(Context context, VersionInfoParcel versionInfoParcel, String str, zzdk zzdkVar, zzdi zzdiVar) {
        this.mContext = context;
        this.zzamw = versionInfoParcel;
        this.zzbvq = str;
        this.zzbvs = zzdkVar;
        this.zzbvr = zzdiVar;
        String str2 = (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzayt);
        if (str2 == null) {
            this.zzbvv = new String[0];
            this.zzbvu = new long[0];
            return;
        }
        String[] split = TextUtils.split(str2, ",");
        this.zzbvv = new String[split.length];
        this.zzbvu = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                this.zzbvu[i] = Long.parseLong(split[i]);
            } catch (NumberFormatException e) {
                zzkd.zzd("Unable to parse frame hash target time number.", e);
                this.zzbvu[i] = -1;
            }
        }
    }

    public void onStop() {
        if (!((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzays)).booleanValue() || this.zzbwc) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "native-player-metrics");
        bundle.putString(ShareConstants.WEB_DIALOG_RESULT_PARAM_REQUEST_ID, this.zzbvq);
        bundle.putString("player", this.zzbwb.zzni());
        for (zzkm.zza zzaVar : this.zzbvt.getBuckets()) {
            String valueOf = String.valueOf("fps_c_");
            String valueOf2 = String.valueOf(zzaVar.name);
            bundle.putString(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), Integer.toString(zzaVar.count));
            String valueOf3 = String.valueOf("fps_p_");
            String valueOf4 = String.valueOf(zzaVar.name);
            bundle.putString(valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3), Double.toString(zzaVar.zzcly));
        }
        for (int i = 0; i < this.zzbvu.length; i++) {
            String str = this.zzbvv[i];
            if (str != null) {
                String valueOf5 = String.valueOf("fh_");
                String valueOf6 = String.valueOf(Long.valueOf(this.zzbvu[i]));
                bundle.putString(new StringBuilder(String.valueOf(valueOf5).length() + 0 + String.valueOf(valueOf6).length()).append(valueOf5).append(valueOf6).toString(), str);
            }
        }
        com.google.android.gms.ads.internal.zzu.zzfq().zza(this.mContext, this.zzamw.zzcs, "gmob-apps", bundle, true);
        this.zzbwc = true;
    }

    public void zzb(zzi zziVar) {
        long j;
        if (this.zzbvy != null && this.zzbvz == null) {
            zzdg.zza(this.zzbvs, this.zzbvy, "vff");
            zzdg.zza(this.zzbvs, this.zzbvr, "vtt");
            this.zzbvz = zzdg.zzb(this.zzbvs);
        }
        long nanoTime = com.google.android.gms.ads.internal.zzu.zzfu().nanoTime();
        if (this.zzbwa && this.zzbwd && this.zzbwe != -1) {
            double nanos = TimeUnit.SECONDS.toNanos(1L) / (nanoTime - this.zzbwe);
            zzkm zzkmVar = this.zzbvt;
            zzkmVar.zzclv++;
            for (int i = 0; i < zzkmVar.zzclt.length; i++) {
                if (zzkmVar.zzclt[i] <= nanos && nanos < zzkmVar.zzcls[i]) {
                    int[] iArr = zzkmVar.zzclu;
                    iArr[i] = iArr[i] + 1;
                }
                if (nanos < zzkmVar.zzclt[i]) {
                    break;
                }
            }
        }
        this.zzbwd = this.zzbwa;
        this.zzbwe = nanoTime;
        long longValue = ((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzayu)).longValue();
        long currentPosition = zziVar.getCurrentPosition();
        for (int i2 = 0; i2 < this.zzbvv.length; i2++) {
            if (this.zzbvv[i2] == null && longValue > Math.abs(currentPosition - this.zzbvu[i2])) {
                String[] strArr = this.zzbvv;
                Bitmap bitmap = zziVar.getBitmap(8, 8);
                long j2 = 0;
                long j3 = 63;
                int i3 = 0;
                while (i3 < 8) {
                    int i4 = 0;
                    long j4 = j2;
                    while (true) {
                        j = j3;
                        if (i4 < 8) {
                            int pixel = bitmap.getPixel(i4, i3);
                            j4 |= (Color.green(pixel) + (Color.blue(pixel) + Color.red(pixel)) > 128 ? 1L : 0L) << ((int) j);
                            i4++;
                            j3 = j - 1;
                        }
                    }
                    i3++;
                    j3 = j;
                    j2 = j4;
                }
                strArr[i2] = String.format("%016X", Long.valueOf(j2));
                return;
            }
        }
    }
}
