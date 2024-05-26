package com.google.android.gms.auth.api.credentials;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzab;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class Credential extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<Credential> CREATOR = new zza();
    final Uri cJ;
    final List<IdToken> cK;
    final String cL;
    final String cM;
    final String cN;
    final String cO;
    final String mName;
    final int mVersionCode;
    public final String zzbgg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Credential(int i, String str, String str2, Uri uri, List<IdToken> list, String str3, String str4, String str5, String str6) {
        this.mVersionCode = i;
        String trim = ((String) zzab.zzb(str, "credential identifier cannot be null")).trim();
        zzab.zzh(trim, "credential identifier cannot be empty");
        this.zzbgg = trim;
        if (str2 != null && TextUtils.isEmpty(str2.trim())) {
            str2 = null;
        }
        this.mName = str2;
        this.cJ = uri;
        this.cK = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.cL = str3;
        if (str3 != null && str3.isEmpty()) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        if (!TextUtils.isEmpty(str4)) {
            com.google.android.gms.auth.api.credentials.internal.zzb.zzfm(str4);
        }
        this.cM = str4;
        this.cN = str5;
        this.cO = str6;
        if (!TextUtils.isEmpty(this.cL) && !TextUtils.isEmpty(this.cM)) {
            throw new IllegalStateException("password and accountType cannot both be set");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Credential)) {
            return false;
        }
        Credential credential = (Credential) obj;
        return TextUtils.equals(this.zzbgg, credential.zzbgg) && TextUtils.equals(this.mName, credential.mName) && zzaa.equal(this.cJ, credential.cJ) && TextUtils.equals(this.cL, credential.cL) && TextUtils.equals(this.cM, credential.cM) && TextUtils.equals(this.cN, credential.cN);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzbgg, this.mName, this.cJ, this.cL, this.cM, this.cN});
    }
}
