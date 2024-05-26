package com.google.android.gms.common;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.nuance.connect.common.Strings;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.sqlite.ChinesePredictionDataSource;
import com.nuance.nmsp.client.sdk.components.resource.internal.nmas.PDXTransactionImpl;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class ConnectionResult extends AbstractSafeParcelable {
    public final PendingIntent mPendingIntent;
    final int mVersionCode;
    public final int ok;
    final String rc;
    public static final ConnectionResult rb = new ConnectionResult(0);
    public static final Parcelable.Creator<ConnectionResult> CREATOR = new zzb();

    public ConnectionResult(int i) {
        this(i, null, (byte) 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionResult(int i, int i2, PendingIntent pendingIntent, String str) {
        this.mVersionCode = i;
        this.ok = i2;
        this.mPendingIntent = pendingIntent;
        this.rc = str;
    }

    public ConnectionResult(int i, PendingIntent pendingIntent) {
        this(i, pendingIntent, (byte) 0);
    }

    private ConnectionResult(int i, PendingIntent pendingIntent, byte b) {
        this(1, i, pendingIntent, null);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConnectionResult)) {
            return false;
        }
        ConnectionResult connectionResult = (ConnectionResult) obj;
        return this.ok == connectionResult.ok && zzaa.equal(this.mPendingIntent, connectionResult.mPendingIntent) && zzaa.equal(this.rc, connectionResult.rc);
    }

    public final boolean hasResolution() {
        return (this.ok == 0 || this.mPendingIntent == null) ? false : true;
    }

    public final boolean isSuccess() {
        return this.ok == 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.ok), this.mPendingIntent, this.rc});
    }

    public final String toString() {
        String str;
        zzaa.zza zzx = zzaa.zzx(this);
        int i = this.ok;
        switch (i) {
            case -1:
                str = ChinesePredictionDataSource.UNKNOWN;
                break;
            case 0:
                str = Strings.STATUS_SUCCESS;
                break;
            case 1:
                str = "SERVICE_MISSING";
                break;
            case 2:
                str = "SERVICE_VERSION_UPDATE_REQUIRED";
                break;
            case 3:
                str = "SERVICE_DISABLED";
                break;
            case 4:
                str = "SIGN_IN_REQUIRED";
                break;
            case 5:
                str = "INVALID_ACCOUNT";
                break;
            case 6:
                str = "RESOLUTION_REQUIRED";
                break;
            case 7:
                str = "NETWORK_ERROR";
                break;
            case 8:
                str = PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR;
                break;
            case 9:
                str = "SERVICE_INVALID";
                break;
            case 10:
                str = "DEVELOPER_ERROR";
                break;
            case 11:
                str = "LICENSE_CHECK_FAILED";
                break;
            case 13:
                str = "CANCELED";
                break;
            case 14:
                str = "TIMEOUT";
                break;
            case 15:
                str = "INTERRUPTED";
                break;
            case 16:
                str = "API_UNAVAILABLE";
                break;
            case 17:
                str = "SIGN_IN_FAILED";
                break;
            case 18:
                str = "SERVICE_UPDATING";
                break;
            case 19:
                str = "SERVICE_MISSING_PERMISSION";
                break;
            case 20:
                str = "RESTRICTED_PROFILE";
                break;
            case 21:
                str = "API_VERSION_UPDATE_REQUIRED";
                break;
            case 42:
                str = "UPDATE_ANDROID_WEAR";
                break;
            case 99:
                str = "UNFINISHED";
                break;
            case ConnectClient.DEFAULT_MESSAGE_DELAY /* 1500 */:
                str = "DRIVE_EXTERNAL_STORAGE_REQUIRED";
                break;
            default:
                str = new StringBuilder(31).append("UNKNOWN_ERROR_CODE(").append(i).append(")").toString();
                break;
        }
        return zzx.zzg("statusCode", str).zzg("resolution", this.mPendingIntent).zzg("message", this.rc).toString();
    }
}
