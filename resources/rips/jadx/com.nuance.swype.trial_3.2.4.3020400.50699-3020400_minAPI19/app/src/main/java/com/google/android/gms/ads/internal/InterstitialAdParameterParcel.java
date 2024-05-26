package com.google.android.gms.ads.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class InterstitialAdParameterParcel extends AbstractSafeParcelable {
    public static final zzm CREATOR = new zzm();
    public final int versionCode;
    public final boolean zzame;
    public final boolean zzamf;
    public final String zzamg;
    public final boolean zzamh;
    public final float zzami;
    public final int zzamj;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InterstitialAdParameterParcel(int i, boolean z, boolean z2, String str, boolean z3, float f, int i2) {
        this.versionCode = i;
        this.zzame = z;
        this.zzamf = z2;
        this.zzamg = str;
        this.zzamh = z3;
        this.zzami = f;
        this.zzamj = i2;
    }

    public InterstitialAdParameterParcel(boolean z, boolean z2, boolean z3, float f, int i) {
        this(3, z, z2, null, z3, f, i);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzm.zza$2350bab8(this, parcel);
    }
}
