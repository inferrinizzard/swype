package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzab;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AccountChangeEvent extends AbstractSafeParcelable {
    public static final Parcelable.Creator<AccountChangeEvent> CREATOR = new zza();
    final long ca;
    final String cb;
    final int cc;
    final int cd;
    final String ce;
    final int mVersion;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccountChangeEvent(int i, long j, String str, int i2, int i3, String str2) {
        this.mVersion = i;
        this.ca = j;
        this.cb = (String) zzab.zzy(str);
        this.cc = i2;
        this.cd = i3;
        this.ce = str2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AccountChangeEvent)) {
            return false;
        }
        AccountChangeEvent accountChangeEvent = (AccountChangeEvent) obj;
        return this.mVersion == accountChangeEvent.mVersion && this.ca == accountChangeEvent.ca && zzaa.equal(this.cb, accountChangeEvent.cb) && this.cc == accountChangeEvent.cc && this.cd == accountChangeEvent.cd && zzaa.equal(this.ce, accountChangeEvent.ce);
    }

    public String toString() {
        String str = ChinesePredictionDataSource.UNKNOWN;
        switch (this.cc) {
            case 1:
                str = "ADDED";
                break;
            case 2:
                str = "REMOVED";
                break;
            case 3:
                str = "RENAMED_FROM";
                break;
            case 4:
                str = "RENAMED_TO";
                break;
        }
        String str2 = this.cb;
        String str3 = this.ce;
        return new StringBuilder(String.valueOf(str2).length() + 91 + String.valueOf(str).length() + String.valueOf(str3).length()).append("AccountChangeEvent {accountName = ").append(str2).append(", changeType = ").append(str).append(", changeData = ").append(str3).append(", eventIndex = ").append(this.cd).append("}").toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zza.zza$119e69c0(this, parcel);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.mVersion), Long.valueOf(this.ca), this.cb, Integer.valueOf(this.cc), Integer.valueOf(this.cd), this.ce});
    }
}
