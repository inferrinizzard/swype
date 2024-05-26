package com.google.android.gms.internal;

import java.util.Collections;
import java.util.Map;

/* loaded from: classes.dex */
public interface zzb {

    /* loaded from: classes.dex */
    public static class zza {
        public byte[] data;
        public String zza;
        public long zzb;
        public long zzc;
        public long zzd;
        public long zze;
        public Map<String, String> zzf = Collections.emptyMap();
    }

    void initialize();

    zza zza(String str);

    void zza(String str, zza zzaVar);
}
