package com.google.android.gms.internal;

import android.content.Context;
import android.util.DisplayMetrics;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;

@zzin
/* loaded from: classes.dex */
public final class zzia extends zzhy {
    private zzhz zzbyg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzia(Context context, zzju.zza zzaVar, zzlh zzlhVar, zzic.zza zzaVar2) {
        super(context, zzaVar, zzlhVar, zzaVar2);
    }

    @Override // com.google.android.gms.internal.zzhy
    protected final int zzpx() {
        if (!this.zzbyg.zzqb()) {
            return !this.zzbyg.zzqc() ? 2 : -2;
        }
        zzkd.zzcv("Ad-Network indicated no fill with passback URL.");
        return 3;
    }

    @Override // com.google.android.gms.internal.zzhy
    protected final void zzpw() {
        int i;
        int i2;
        AdSizeParcel zzdn = this.zzbgf.zzdn();
        if (zzdn.zzaus) {
            DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
            i = displayMetrics.widthPixels;
            i2 = displayMetrics.heightPixels;
        } else {
            i = zzdn.widthPixels;
            i2 = zzdn.heightPixels;
        }
        this.zzbyg = new zzhz(this, this.zzbgf, i, i2, (byte) 0);
        this.zzbgf.zzuj().zzbya = this;
        this.zzbyg.zza(this.zzbxs);
    }
}
