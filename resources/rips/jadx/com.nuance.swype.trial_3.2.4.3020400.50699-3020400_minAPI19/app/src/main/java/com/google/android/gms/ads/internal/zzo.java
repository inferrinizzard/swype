package com.google.android.gms.ads.internal;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.client.zzz;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;

@zzin
/* loaded from: classes.dex */
public class zzo extends zzz.zza {
    private static final Object zzamr = new Object();
    private static zzo zzams;
    private final Context mContext;
    private boolean zzamu;
    private VersionInfoParcel zzamw;
    private final Object zzail = new Object();
    private float zzamv = -1.0f;
    private boolean zzamt = false;

    private zzo(Context context, VersionInfoParcel versionInfoParcel) {
        this.mContext = context;
        this.zzamw = versionInfoParcel;
    }

    public static zzo zza(Context context, VersionInfoParcel versionInfoParcel) {
        zzo zzoVar;
        synchronized (zzamr) {
            if (zzams == null) {
                zzams = new zzo(context.getApplicationContext(), versionInfoParcel);
            }
            zzoVar = zzams;
        }
        return zzoVar;
    }

    public static zzo zzex() {
        zzo zzoVar;
        synchronized (zzamr) {
            zzoVar = zzams;
        }
        return zzoVar;
    }

    @Override // com.google.android.gms.ads.internal.client.zzz
    public void initialize() {
        synchronized (zzamr) {
            if (this.zzamt) {
                zzkd.zzcx("Mobile ads is initialized already.");
            } else {
                this.zzamt = true;
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzz
    public void setAppMuted(boolean z) {
        synchronized (this.zzail) {
            this.zzamu = z;
        }
    }

    @Override // com.google.android.gms.ads.internal.client.zzz
    public void setAppVolume(float f) {
        synchronized (this.zzail) {
            this.zzamv = f;
        }
    }

    public float zzey() {
        float f;
        synchronized (this.zzail) {
            f = this.zzamv;
        }
        return f;
    }

    public boolean zzez() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzamv >= 0.0f;
        }
        return z;
    }

    public boolean zzfa() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzamu;
        }
        return z;
    }

    @Override // com.google.android.gms.ads.internal.client.zzz
    public void zzu(String str) {
        zzdc.initialize(this.mContext);
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (((Boolean) zzu.zzfz().zzd(zzdc.zzbct)).booleanValue()) {
            zzu.zzgi().zza(this.mContext, this.zzamw, true, null, str, null);
        }
    }
}
