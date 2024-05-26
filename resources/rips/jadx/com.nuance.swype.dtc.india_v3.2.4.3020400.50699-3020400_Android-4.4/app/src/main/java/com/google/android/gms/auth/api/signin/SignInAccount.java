package com.google.android.gms.auth.api.signin;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public class SignInAccount extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<SignInAccount> CREATOR = new zzc();

    @Deprecated
    String dB;
    public GoogleSignInAccount dV;
    final int versionCode;

    @Deprecated
    String zzcvj;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SignInAccount(int i, String str, GoogleSignInAccount googleSignInAccount, String str2) {
        this.versionCode = i;
        this.dV = googleSignInAccount;
        this.dB = zzab.zzh(str, "8.3 and 8.4 SDKs require non-null email");
        this.zzcvj = zzab.zzh(str2, "8.3 and 8.4 SDKs require non-null userId");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }
}
