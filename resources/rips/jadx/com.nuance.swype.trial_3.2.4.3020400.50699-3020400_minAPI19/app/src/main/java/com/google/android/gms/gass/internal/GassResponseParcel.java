package com.google.android.gms.gass.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.internal.zzae;
import com.google.android.gms.internal.zzapu;
import com.google.android.gms.internal.zzapv;

/* loaded from: classes.dex */
public final class GassResponseParcel extends AbstractSafeParcelable {
    public static final Parcelable.Creator<GassResponseParcel> CREATOR = new zzd();
    zzae.zza YX = null;
    byte[] YY;
    public final int versionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GassResponseParcel(int i, byte[] bArr) {
        this.versionCode = i;
        this.YY = bArr;
        zzaww();
    }

    private void zzaww() {
        if (this.YX != null || this.YY == null) {
            if (this.YX == null || this.YY != null) {
                if (this.YX != null && this.YY != null) {
                    throw new IllegalStateException("Invalid internal representation - full");
                }
                if (this.YX != null || this.YY != null) {
                    throw new IllegalStateException("Impossible");
                }
                throw new IllegalStateException("Invalid internal representation - empty");
            }
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzd.zza$765441b7(this, parcel);
    }

    public final zzae.zza zzbld() {
        if (!(this.YX != null)) {
            try {
                byte[] bArr = this.YY;
                this.YX = (zzae.zza) zzapv.zzb$16844d7a(new zzae.zza(), bArr, bArr.length);
                this.YY = null;
            } catch (zzapu e) {
                throw new IllegalStateException(e);
            }
        }
        zzaww();
        return this.YX;
    }
}
