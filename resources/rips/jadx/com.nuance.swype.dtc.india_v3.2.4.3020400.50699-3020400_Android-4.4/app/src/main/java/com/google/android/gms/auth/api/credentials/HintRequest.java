package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public final class HintRequest extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<HintRequest> CREATOR = new zzd();
    final String[] cS;
    final CredentialPickerConfig cV;
    final boolean cW;
    final boolean cX;
    final int mVersionCode;

    /* loaded from: classes.dex */
    public static final class Builder {
        public String[] cS;
        public CredentialPickerConfig cV = new CredentialPickerConfig.Builder().build();
        public boolean cW;
        public boolean cX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HintRequest(int i, CredentialPickerConfig credentialPickerConfig, boolean z, boolean z2, String[] strArr) {
        this.mVersionCode = i;
        this.cV = (CredentialPickerConfig) zzab.zzy(credentialPickerConfig);
        this.cW = z;
        this.cX = z2;
        this.cS = (String[]) zzab.zzy(strArr);
    }

    public /* synthetic */ HintRequest(Builder builder, byte b) {
        this(builder);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }

    private HintRequest(Builder builder) {
        this(1, builder.cV, builder.cW, builder.cX, builder.cS);
    }
}
