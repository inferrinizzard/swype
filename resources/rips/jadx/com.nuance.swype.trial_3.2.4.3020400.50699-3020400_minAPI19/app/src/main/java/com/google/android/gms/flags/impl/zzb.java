package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzua;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public final class zzb {
    private static SharedPreferences Pc = null;

    public static SharedPreferences zzn(final Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (Pc == null) {
                Pc = (SharedPreferences) zzua.zzb(new Callable<SharedPreferences>() { // from class: com.google.android.gms.flags.impl.zzb.1
                    @Override // java.util.concurrent.Callable
                    public final /* synthetic */ SharedPreferences call() throws Exception {
                        return context.getSharedPreferences("google_sdk_flags", 1);
                    }
                });
            }
            sharedPreferences = Pc;
        }
        return sharedPreferences;
    }
}
