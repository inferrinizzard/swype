package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class Status extends AbstractSafeParcelable implements Result, ReflectedParcelable {
    final PendingIntent mPendingIntent;
    final int mVersionCode;
    final int ok;
    final String rc;
    public static final Status sq = new Status(0);
    public static final Status sr = new Status(14);
    public static final Status ss = new Status(8);
    public static final Status st = new Status(15);
    public static final Status su = new Status(16);
    public static final Status sv = new Status(17);
    public static final Status sw = new Status(18);
    public static final Parcelable.Creator<Status> CREATOR = new zzf();

    public Status(int i) {
        this(i, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this.mVersionCode = i;
        this.ok = i2;
        this.rc = str;
        this.mPendingIntent = pendingIntent;
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.mVersionCode == status.mVersionCode && this.ok == status.ok && zzaa.equal(this.rc, status.rc) && zzaa.equal(this.mPendingIntent, status.mPendingIntent);
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this;
    }

    public final int getStatusCode() {
        return this.ok;
    }

    public final String getStatusMessage() {
        return this.rc;
    }

    public final boolean isSuccess() {
        return this.ok <= 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.mVersionCode), Integer.valueOf(this.ok), this.rc, this.mPendingIntent});
    }

    public final String toString() {
        return zzaa.zzx(this).zzg("statusCode", this.rc != null ? this.rc : CommonStatusCodes.getStatusCodeString(this.ok)).zzg("resolution", this.mPendingIntent).toString();
    }
}
