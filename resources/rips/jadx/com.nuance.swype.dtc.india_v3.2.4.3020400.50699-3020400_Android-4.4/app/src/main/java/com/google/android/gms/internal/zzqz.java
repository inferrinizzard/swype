package com.google.android.gms.internal;

import android.os.Binder;

/* loaded from: classes.dex */
public abstract class zzqz<T> {
    private T vQ = null;
    protected final String zzaxp;
    protected final T zzaxq;
    private static final Object zzamr = new Object();
    private static zza vN = null;
    private static int vO = 0;
    private static String vP = "com.google.android.providers.gsf.permission.READ_GSERVICES";

    /* loaded from: classes.dex */
    private interface zza {
        Long getLong$4885d6e9();

        String getString$7157d249();

        Integer zzb$1b7f1b3f();
    }

    protected zzqz(String str, T t) {
        this.zzaxp = str;
        this.zzaxq = t;
    }

    public static zzqz<Integer> zza(String str, Integer num) {
        return new zzqz<Integer>(str, num) { // from class: com.google.android.gms.internal.zzqz.3
            @Override // com.google.android.gms.internal.zzqz
            protected final /* synthetic */ Integer zzgy$9543ced() {
                zza zzaVar = null;
                return zzaVar.zzb$1b7f1b3f();
            }
        };
    }

    public static zzqz<Long> zza(String str, Long l) {
        return new zzqz<Long>(str, l) { // from class: com.google.android.gms.internal.zzqz.2
            @Override // com.google.android.gms.internal.zzqz
            protected final /* synthetic */ Long zzgy$9543ced() {
                zza zzaVar = null;
                return zzaVar.getLong$4885d6e9();
            }
        };
    }

    public static zzqz<String> zzab(String str, String str2) {
        return new zzqz<String>(str, str2) { // from class: com.google.android.gms.internal.zzqz.5
            @Override // com.google.android.gms.internal.zzqz
            protected final /* synthetic */ String zzgy$9543ced() {
                zza zzaVar = null;
                return zzaVar.getString$7157d249();
            }
        };
    }

    public final T get() {
        try {
            return zzgy$9543ced();
        } catch (SecurityException e) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return zzgy$9543ced();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    protected abstract T zzgy$9543ced();
}
