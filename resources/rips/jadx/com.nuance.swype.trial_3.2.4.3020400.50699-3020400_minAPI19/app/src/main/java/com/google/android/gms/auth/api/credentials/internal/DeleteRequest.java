package com.google.android.gms.auth.api.credentials.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

/* loaded from: classes.dex */
public final class DeleteRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<DeleteRequest> CREATOR = new zzh();
    final Credential di;
    final int mVersionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeleteRequest(int i, Credential credential) {
        this.mVersionCode = i;
        this.di = credential;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }
}
