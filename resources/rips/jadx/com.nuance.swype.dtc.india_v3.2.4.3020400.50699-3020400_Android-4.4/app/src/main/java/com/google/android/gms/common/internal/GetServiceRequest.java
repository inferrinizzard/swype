package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzq;

/* loaded from: classes.dex */
public class GetServiceRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<GetServiceRequest> CREATOR = new zzj();
    final int version;
    Account yA;
    long yB;
    final int yu;
    int yv;
    String yw;
    IBinder yx;
    Scope[] yy;
    Bundle yz;

    public GetServiceRequest(int i) {
        this.version = 3;
        this.yv = com.google.android.gms.common.zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        this.yu = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GetServiceRequest(int i, int i2, int i3, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, long j) {
        this.version = i;
        this.yu = i2;
        this.yv = i3;
        this.yw = str;
        if (i < 2) {
            this.yA = iBinder != null ? zza.zza(zzq.zza.zzdp(iBinder)) : null;
        } else {
            this.yx = iBinder;
            this.yA = account;
        }
        this.yy = scopeArr;
        this.yz = bundle;
        this.yB = j;
    }
}
