package com.google.android.gms.clearcut;

import android.os.Parcel;
import com.google.android.gms.clearcut.zzb;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzapz;
import com.google.android.gms.playlog.internal.PlayLoggerContext;
import java.util.Arrays;

/* loaded from: classes.dex */
public class LogEventParcelable extends AbstractSafeParcelable {
    public static final zzd CREATOR = new zzd();
    public boolean qA;
    public final zzapz.zzd qB;
    public final zzb.zzc qC;
    public final zzb.zzc qD;
    public PlayLoggerContext qu;
    public byte[] qv;
    public int[] qw;
    public String[] qx;
    public int[] qy;
    public byte[][] qz;
    public final int versionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LogEventParcelable(int i, PlayLoggerContext playLoggerContext, byte[] bArr, int[] iArr, String[] strArr, int[] iArr2, byte[][] bArr2, boolean z) {
        this.versionCode = i;
        this.qu = playLoggerContext;
        this.qv = bArr;
        this.qw = iArr;
        this.qx = strArr;
        this.qB = null;
        this.qC = null;
        this.qD = null;
        this.qy = iArr2;
        this.qz = bArr2;
        this.qA = z;
    }

    public LogEventParcelable(PlayLoggerContext playLoggerContext, zzapz.zzd zzdVar, zzb.zzc zzcVar, int[] iArr, String[] strArr, int[] iArr2, byte[][] bArr, boolean z) {
        this.versionCode = 1;
        this.qu = playLoggerContext;
        this.qB = zzdVar;
        this.qC = zzcVar;
        this.qD = null;
        this.qw = iArr;
        this.qx = strArr;
        this.qy = iArr2;
        this.qz = bArr;
        this.qA = z;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LogEventParcelable)) {
            return false;
        }
        LogEventParcelable logEventParcelable = (LogEventParcelable) obj;
        return this.versionCode == logEventParcelable.versionCode && zzaa.equal(this.qu, logEventParcelable.qu) && Arrays.equals(this.qv, logEventParcelable.qv) && Arrays.equals(this.qw, logEventParcelable.qw) && Arrays.equals(this.qx, logEventParcelable.qx) && zzaa.equal(this.qB, logEventParcelable.qB) && zzaa.equal(this.qC, logEventParcelable.qC) && zzaa.equal(this.qD, logEventParcelable.qD) && Arrays.equals(this.qy, logEventParcelable.qy) && Arrays.deepEquals(this.qz, logEventParcelable.qz) && this.qA == logEventParcelable.qA;
    }

    public String toString() {
        return "LogEventParcelable[" + this.versionCode + ", " + this.qu + ", LogEventBytes: " + (this.qv == null ? null : new String(this.qv)) + ", TestCodes: " + Arrays.toString(this.qw) + ", MendelPackages: " + Arrays.toString(this.qx) + ", LogEvent: " + this.qB + ", ExtensionProducer: " + this.qC + ", VeProducer: " + this.qD + ", ExperimentIDs: " + Arrays.toString(this.qy) + ", ExperimentTokens: " + Arrays.toString(this.qz) + ", AddPhenotypeExperimentTokens: " + this.qA + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.versionCode), this.qu, this.qv, this.qw, this.qx, this.qB, this.qC, this.qD, this.qy, this.qz, Boolean.valueOf(this.qA)});
    }
}
