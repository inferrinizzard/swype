package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public final class Scope extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<Scope> CREATOR = new zze();
    final int mVersionCode;
    public final String sp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Scope(int i, String str) {
        zzab.zzh(str, "scopeUri must not be null or empty");
        this.mVersionCode = i;
        this.sp = str;
    }

    public Scope(String str) {
        this(1, str);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Scope) {
            return this.sp.equals(((Scope) obj).sp);
        }
        return false;
    }

    public final int hashCode() {
        return this.sp.hashCode();
    }

    public final String toString() {
        return this.sp;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zze.zza$514aa83(this, parcel);
    }
}
