package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzty;
import com.google.android.gms.internal.zzua;
import java.util.concurrent.Callable;

@DynamiteApi
/* loaded from: classes.dex */
public class FlagProviderImpl extends zzty.zza {
    private boolean zzamt = false;
    private SharedPreferences zzaxu;

    @Override // com.google.android.gms.internal.zzty
    public void init(zzd zzdVar) {
        Context context = (Context) zze.zzad(zzdVar);
        if (this.zzamt) {
            return;
        }
        try {
            this.zzaxu = zzb.zzn(context.createPackageContext("com.google.android.gms", 0));
            this.zzamt = true;
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    @Override // com.google.android.gms.internal.zzty
    public boolean getBooleanFlagValue(String str, boolean z, int i) {
        return !this.zzamt ? z : ((Boolean) zzua.zzb(new Callable<Boolean>() { // from class: com.google.android.gms.flags.impl.zza.zza.1
            final /* synthetic */ SharedPreferences OW;
            final /* synthetic */ String OX;
            final /* synthetic */ Boolean OY;

            public AnonymousClass1(SharedPreferences sharedPreferences, String str2, Boolean bool) {
                r1 = sharedPreferences;
                r2 = str2;
                r3 = bool;
            }

            @Override // java.util.concurrent.Callable
            public final /* synthetic */ Boolean call() throws Exception {
                return Boolean.valueOf(r1.getBoolean(r2, r3.booleanValue()));
            }
        })).booleanValue();
    }

    @Override // com.google.android.gms.internal.zzty
    public int getIntFlagValue(String str, int i, int i2) {
        return !this.zzamt ? i : ((Integer) zzua.zzb(new Callable<Integer>() { // from class: com.google.android.gms.flags.impl.zza.zzb.1
            final /* synthetic */ SharedPreferences OW;
            final /* synthetic */ String OX;
            final /* synthetic */ Integer OZ;

            public AnonymousClass1(SharedPreferences sharedPreferences, String str2, Integer num) {
                r1 = sharedPreferences;
                r2 = str2;
                r3 = num;
            }

            @Override // java.util.concurrent.Callable
            public final /* synthetic */ Integer call() throws Exception {
                return Integer.valueOf(r1.getInt(r2, r3.intValue()));
            }
        })).intValue();
    }

    @Override // com.google.android.gms.internal.zzty
    public long getLongFlagValue(String str, long j, int i) {
        return !this.zzamt ? j : ((Long) zzua.zzb(new Callable<Long>() { // from class: com.google.android.gms.flags.impl.zza.zzc.1
            final /* synthetic */ SharedPreferences OW;
            final /* synthetic */ String OX;
            final /* synthetic */ Long Pa;

            public AnonymousClass1(SharedPreferences sharedPreferences, String str2, Long l) {
                r1 = sharedPreferences;
                r2 = str2;
                r3 = l;
            }

            @Override // java.util.concurrent.Callable
            public final /* synthetic */ Long call() throws Exception {
                return Long.valueOf(r1.getLong(r2, r3.longValue()));
            }
        })).longValue();
    }

    @Override // com.google.android.gms.internal.zzty
    public String getStringFlagValue(String str, String str2, int i) {
        return !this.zzamt ? str2 : (String) zzua.zzb(new Callable<String>() { // from class: com.google.android.gms.flags.impl.zza.zzd.1
            final /* synthetic */ SharedPreferences OW;
            final /* synthetic */ String OX;
            final /* synthetic */ String Pb;

            public AnonymousClass1(SharedPreferences sharedPreferences, String str3, String str22) {
                r1 = sharedPreferences;
                r2 = str3;
                r3 = str22;
            }

            @Override // java.util.concurrent.Callable
            public final /* synthetic */ String call() throws Exception {
                return r1.getString(r2, r3);
            }
        });
    }
}
