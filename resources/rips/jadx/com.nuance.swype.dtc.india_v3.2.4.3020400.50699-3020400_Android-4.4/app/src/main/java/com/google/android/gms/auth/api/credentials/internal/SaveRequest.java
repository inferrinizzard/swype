package com.google.android.gms.auth.api.credentials.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

/* loaded from: classes.dex */
public final class SaveRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<SaveRequest> CREATOR = new zzl();
    final Credential di;
    final int mVersionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SaveRequest(int i, Credential credential) {
        this.mVersionCode = i;
        this.di = credential;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }
}
