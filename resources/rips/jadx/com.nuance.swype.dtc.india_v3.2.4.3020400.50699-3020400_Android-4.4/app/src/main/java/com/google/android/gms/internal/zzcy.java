package com.google.android.gms.internal;

import android.content.SharedPreferences;

@zzin
/* loaded from: classes.dex */
public abstract class zzcy<T> {
    private final int zzaxo;
    final String zzaxp;
    public final T zzaxq;

    /* synthetic */ zzcy(int i, String str, Object obj, byte b) {
        this(i, str, obj);
    }

    public static zzcy<Integer> zza(int i, String str, int i2) {
        return new zzcy<Integer>(i, str, Integer.valueOf(i2)) { // from class: com.google.android.gms.internal.zzcy.2
            {
                byte b = 0;
            }

            @Override // com.google.android.gms.internal.zzcy
            public final /* synthetic */ Integer zza(SharedPreferences sharedPreferences) {
                return Integer.valueOf(sharedPreferences.getInt(this.zzaxp, ((Integer) this.zzaxq).intValue()));
            }
        };
    }

    public static zzcy<Long> zza(int i, String str, long j) {
        return new zzcy<Long>(i, str, Long.valueOf(j)) { // from class: com.google.android.gms.internal.zzcy.3
            {
                byte b = 0;
            }

            @Override // com.google.android.gms.internal.zzcy
            public final /* synthetic */ Long zza(SharedPreferences sharedPreferences) {
                return Long.valueOf(sharedPreferences.getLong(this.zzaxp, ((Long) this.zzaxq).longValue()));
            }
        };
    }

    public static zzcy<Boolean> zza(int i, String str, Boolean bool) {
        return new zzcy<Boolean>(i, str, bool) { // from class: com.google.android.gms.internal.zzcy.1
            {
                byte b = 0;
            }

            @Override // com.google.android.gms.internal.zzcy
            public final /* synthetic */ Boolean zza(SharedPreferences sharedPreferences) {
                return Boolean.valueOf(sharedPreferences.getBoolean(this.zzaxp, ((Boolean) this.zzaxq).booleanValue()));
            }
        };
    }

    public static zzcy<String> zza(int i, String str, String str2) {
        return new zzcy<String>(i, str, str2) { // from class: com.google.android.gms.internal.zzcy.4
            {
                byte b = 0;
            }

            @Override // com.google.android.gms.internal.zzcy
            public final /* synthetic */ String zza(SharedPreferences sharedPreferences) {
                return sharedPreferences.getString(this.zzaxp, (String) this.zzaxq);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract T zza(SharedPreferences sharedPreferences);

    private zzcy(int i, String str, T t) {
        this.zzaxo = i;
        this.zzaxp = str;
        this.zzaxq = t;
        com.google.android.gms.ads.internal.zzu.zzfy().zzaxr.add(this);
    }

    public static zzcy<String> zza(int i, String str) {
        zzcy<String> zza = zza(i, str, (String) null);
        com.google.android.gms.ads.internal.zzu.zzfy().zzaxs.add(zza);
        return zza;
    }

    public static zzcy<String> zzb(int i, String str) {
        zzcy<String> zza = zza(i, str, (String) null);
        com.google.android.gms.ads.internal.zzu.zzfy().zzaxt.add(zza);
        return zza;
    }
}
