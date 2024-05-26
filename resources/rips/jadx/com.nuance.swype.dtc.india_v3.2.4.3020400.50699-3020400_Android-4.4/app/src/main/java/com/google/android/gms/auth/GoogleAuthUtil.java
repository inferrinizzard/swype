package com.google.android.gms.auth;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.auth.zzd;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzbq;
import com.google.android.gms.internal.zzng;
import java.io.IOException;

/* loaded from: classes.dex */
public final class GoogleAuthUtil extends zzd {
    public static final String KEY_CALLER_UID = zzd.KEY_CALLER_UID;
    public static final String KEY_ANDROID_PACKAGE_NAME = zzd.KEY_ANDROID_PACKAGE_NAME;

    @Deprecated
    public static String getToken(Context context, String str, String str2) throws IOException, UserRecoverableAuthException, GoogleAuthException {
        Account account = new Account(str, "com.google");
        Bundle bundle = new Bundle();
        zzab.zzhj("Calling this from your main thread can lead to deadlock");
        zzab.zzh(str2, "Scope cannot be empty or null.");
        zzab.zzb(account, "Account cannot be null.");
        try {
            com.google.android.gms.common.zze.zzbb(context.getApplicationContext());
            Bundle bundle2 = new Bundle(bundle);
            String str3 = context.getApplicationInfo().packageName;
            bundle2.putString("clientPackageName", str3);
            if (TextUtils.isEmpty(bundle2.getString(zzd.KEY_ANDROID_PACKAGE_NAME))) {
                bundle2.putString(zzd.KEY_ANDROID_PACKAGE_NAME, str3);
            }
            bundle2.putLong("service_connection_start_time_millis", SystemClock.elapsedRealtime());
            return ((TokenData) zzd.zza(context, zzd.cf, new zzd.zza<TokenData>() { // from class: com.google.android.gms.auth.zzd.1
                final /* synthetic */ Account ch;
                final /* synthetic */ String ci;
                final /* synthetic */ Bundle val$options;

                public AnonymousClass1(Account account2, String str22, Bundle bundle22) {
                    r1 = account2;
                    r2 = str22;
                    r3 = bundle22;
                }

                @Override // com.google.android.gms.auth.zzd.zza
                public final /* synthetic */ TokenData zzbs(IBinder iBinder) throws RemoteException, IOException, GoogleAuthException {
                    Bundle bundle3 = (Bundle) zzd.zzo(zzbq.zza.zza(iBinder).zza(r1, r2, r3));
                    TokenData zzd = TokenData.zzd(bundle3, "tokenDetails");
                    if (zzd != null) {
                        return zzd;
                    }
                    String string = bundle3.getString("Error");
                    Intent intent = (Intent) bundle3.getParcelable("userRecoveryIntent");
                    zzng zzfx = zzng.zzfx(string);
                    if (zzng.zza(zzfx)) {
                        String valueOf = String.valueOf(zzfx);
                        Log.w("GoogleAuthUtil", new StringBuilder(String.valueOf(valueOf).length() + 31).append("isUserRecoverableError status: ").append(valueOf).toString());
                        throw new UserRecoverableAuthException(string, intent);
                    }
                    if (zzng.zzb(zzfx)) {
                        throw new IOException(string);
                    }
                    throw new GoogleAuthException(string);
                }
            })).co;
        } catch (GooglePlayServicesNotAvailableException e) {
            throw new GoogleAuthException(e.getMessage());
        } catch (GooglePlayServicesRepairableException e2) {
            throw new GooglePlayServicesAvailabilityException(e2.cn, e2.getMessage(), new Intent(e2.mIntent));
        }
    }
}
