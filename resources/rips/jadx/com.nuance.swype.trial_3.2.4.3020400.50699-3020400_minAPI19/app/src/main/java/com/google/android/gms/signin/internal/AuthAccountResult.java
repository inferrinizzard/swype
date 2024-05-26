package com.google.android.gms.signin.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

/* loaded from: classes.dex */
public class AuthAccountResult extends AbstractSafeParcelable implements Result {
    public static final Parcelable.Creator<AuthAccountResult> CREATOR = new zza();
    int atW;
    Intent atX;
    final int mVersionCode;

    public AuthAccountResult() {
        this((byte) 0);
    }

    private AuthAccountResult(byte b) {
        this(2, 0, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthAccountResult(int i, int i2, Intent intent) {
        this.mVersionCode = i;
        this.atW = i2;
        this.atX = intent;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.atW == 0 ? Status.sq : Status.su;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
