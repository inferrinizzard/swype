package com.google.android.gms.auth;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzm;
import java.io.IOException;

/* loaded from: classes.dex */
public class zzd {
    public static final String KEY_ANDROID_PACKAGE_NAME;
    public static final String KEY_CALLER_UID;
    static final ComponentName cf;
    private static final ComponentName cg;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface zza<T> {
        T zzbs(IBinder iBinder) throws RemoteException, IOException, GoogleAuthException;
    }

    static {
        int i = Build.VERSION.SDK_INT;
        KEY_CALLER_UID = "callerUid";
        int i2 = Build.VERSION.SDK_INT;
        KEY_ANDROID_PACKAGE_NAME = "androidPackageName";
        cf = new ComponentName("com.google.android.gms", "com.google.android.gms.auth.GetToken");
        cg = new ComponentName("com.google.android.gms", "com.google.android.gms.recovery.RecoveryService");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> T zza(Context context, ComponentName componentName, zza<T> zzaVar) throws IOException, GoogleAuthException {
        com.google.android.gms.common.zza zzaVar2 = new com.google.android.gms.common.zza();
        zzm zzce = zzm.zzce(context);
        try {
            if (!zzce.zza(componentName, zzaVar2, "GoogleAuthUtil")) {
                throw new IOException("Could not bind to service.");
            }
            try {
                zzab.zzhj("BlockingServiceConnection.getService() called on main thread");
                if (zzaVar2.qZ) {
                    throw new IllegalStateException("Cannot call get on this connection more than once");
                }
                zzaVar2.qZ = true;
                return zzaVar.zzbs(zzaVar2.ra.take());
            } catch (RemoteException | InterruptedException e) {
                Log.i("GoogleAuthUtil", "Error on service connection.", e);
                throw new IOException("Error on service connection.", e);
            }
        } finally {
            zzce.zzb$9b3168c(componentName, zzaVar2);
        }
    }

    static /* synthetic */ Object zzo(Object obj) throws IOException {
        if (obj != null) {
            return obj;
        }
        Log.w("GoogleAuthUtil", "Binder call returned null.");
        throw new IOException("Service unavailable.");
    }
}
