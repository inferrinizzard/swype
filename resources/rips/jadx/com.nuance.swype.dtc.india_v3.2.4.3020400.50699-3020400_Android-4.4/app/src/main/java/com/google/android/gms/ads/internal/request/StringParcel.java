package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class StringParcel extends AbstractSafeParcelable {
    public static final Parcelable.Creator<StringParcel> CREATOR = new zzo();
    final int mVersionCode;
    String zzbem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StringParcel(int i, String str) {
        this.mVersionCode = i;
        this.zzbem = str;
    }

    public StringParcel(String str) {
        this.mVersionCode = 1;
        this.zzbem = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzo.zza$7244a929(this, parcel);
    }

    public String zzre() {
        return this.zzbem;
    }
}
