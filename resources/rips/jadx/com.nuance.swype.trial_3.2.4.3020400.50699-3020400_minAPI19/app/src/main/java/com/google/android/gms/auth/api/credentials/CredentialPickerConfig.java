package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

/* loaded from: classes.dex */
public final class CredentialPickerConfig extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<CredentialPickerConfig> CREATOR = new zzb();
    final boolean cP;
    final boolean cQ;
    final boolean mShowCancelButton;
    final int mVersionCode;

    /* loaded from: classes.dex */
    public static class Builder {
        public boolean cP = false;
        public boolean mShowCancelButton = true;
        public boolean cQ = false;

        public final CredentialPickerConfig build() {
            return new CredentialPickerConfig(this, (byte) 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CredentialPickerConfig(int i, boolean z, boolean z2, boolean z3) {
        this.mVersionCode = i;
        this.cP = z;
        this.mShowCancelButton = z2;
        this.cQ = z3;
    }

    /* synthetic */ CredentialPickerConfig(Builder builder, byte b) {
        this(builder);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzb.zza$13561712(this, parcel);
    }

    private CredentialPickerConfig(Builder builder) {
        this(1, builder.cP, builder.mShowCancelButton, builder.cQ);
    }
}
