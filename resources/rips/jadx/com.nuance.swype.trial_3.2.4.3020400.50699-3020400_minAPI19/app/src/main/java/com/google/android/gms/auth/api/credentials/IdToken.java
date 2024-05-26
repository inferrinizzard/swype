package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public final class IdToken extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<IdToken> CREATOR = new zze();
    final String cM;
    final String cY;
    final int mVersionCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IdToken(int i, String str, String str2) {
        com.google.android.gms.auth.api.credentials.internal.zzb.zzfm(str);
        zzab.zzb(!TextUtils.isEmpty(str2), "id token string cannot be null or empty");
        this.mVersionCode = i;
        this.cM = str;
        this.cY = str2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zze.zza$209b9bb7(this, parcel);
    }
}
