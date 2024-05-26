package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

/* loaded from: classes.dex */
public class SignInResponse extends AbstractSafeParcelable {
    public static final Parcelable.Creator<SignInResponse> CREATOR = new zzi();
    public final ResolveAccountResponse aue;
    final int mVersionCode;
    public final ConnectionResult rF;

    public SignInResponse() {
        this(new ConnectionResult(8, null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SignInResponse(int i, ConnectionResult connectionResult, ResolveAccountResponse resolveAccountResponse) {
        this.mVersionCode = i;
        this.rF = connectionResult;
        this.aue = resolveAccountResponse;
    }

    private SignInResponse(ConnectionResult connectionResult) {
        this(1, connectionResult, null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
