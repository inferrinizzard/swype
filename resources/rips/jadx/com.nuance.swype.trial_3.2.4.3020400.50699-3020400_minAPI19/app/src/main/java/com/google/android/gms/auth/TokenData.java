package com.google.android.gms.auth;

import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzab;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class TokenData extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zze CREATOR = new zze();
    final String co;
    final Long cp;
    final boolean cq;
    final boolean cr;
    final List<String> cs;
    final int mVersionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TokenData(int i, String str, Long l, boolean z, boolean z2, List<String> list) {
        this.mVersionCode = i;
        this.co = zzab.zzhr(str);
        this.cp = l;
        this.cq = z;
        this.cr = z2;
        this.cs = list;
    }

    public static TokenData zzd(Bundle bundle, String str) {
        bundle.setClassLoader(TokenData.class.getClassLoader());
        Bundle bundle2 = bundle.getBundle(str);
        if (bundle2 == null) {
            return null;
        }
        bundle2.setClassLoader(TokenData.class.getClassLoader());
        return (TokenData) bundle2.getParcelable("TokenData");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TokenData)) {
            return false;
        }
        TokenData tokenData = (TokenData) obj;
        return TextUtils.equals(this.co, tokenData.co) && zzaa.equal(this.cp, tokenData.cp) && this.cq == tokenData.cq && this.cr == tokenData.cr && zzaa.equal(this.cs, tokenData.cs);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zze.zza$6d52043c(this, parcel);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.co, this.cp, Boolean.valueOf(this.cq), Boolean.valueOf(this.cr), this.cs});
    }
}
