package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.clearcut.zzb;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class zzpg implements zzb.InterfaceC0045zzb {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    static Boolean qU = null;
    final zza qV;

    public zzpg() {
        this(new zza(null));
    }

    public zzpg(Context context) {
        this(new zza(context));
    }

    private zzpg(zza zzaVar) {
        this.qV = (zza) com.google.android.gms.common.internal.zzab.zzy(zzaVar);
    }

    private static zzb zzgt(String str) {
        int i = 0;
        if (str == null) {
            return null;
        }
        String str2 = "";
        int indexOf = str.indexOf(44);
        if (indexOf >= 0) {
            str2 = str.substring(0, indexOf);
            i = indexOf + 1;
        }
        int indexOf2 = str.indexOf(47, i);
        if (indexOf2 <= 0) {
            String valueOf = String.valueOf(str);
            Log.e("LogSamplerImpl", valueOf.length() != 0 ? "Failed to parse the rule: ".concat(valueOf) : new String("Failed to parse the rule: "));
            return null;
        }
        try {
            long parseLong = Long.parseLong(str.substring(i, indexOf2));
            long parseLong2 = Long.parseLong(str.substring(indexOf2 + 1));
            if (parseLong >= 0 && parseLong2 >= 0) {
                return new zzb(str2, parseLong, parseLong2);
            }
            Log.e("LogSamplerImpl", new StringBuilder(72).append("negative values not supported: ").append(parseLong).append("/").append(parseLong2).toString());
            return null;
        } catch (NumberFormatException e) {
            String valueOf2 = String.valueOf(str);
            Log.e("LogSamplerImpl", valueOf2.length() != 0 ? "parseLong() failed while parsing: ".concat(valueOf2) : new String("parseLong() failed while parsing: "), e);
            return null;
        }
    }

    /* loaded from: classes.dex */
    static class zza {
        final ContentResolver mContentResolver;

        zza(Context context) {
            if (context != null) {
                if (zzpg.qU == null) {
                    zzpg.qU = Boolean.valueOf(context.checkCallingOrSelfPermission("com.google.android.providers.gsf.permission.READ_GSERVICES") == 0);
                }
                if (zzpg.qU.booleanValue()) {
                    this.mContentResolver = context.getContentResolver();
                    zzaeo.zzb(this.mContentResolver, "gms:playlog:service:sampling_");
                    return;
                }
            }
            this.mContentResolver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class zzb {
        public final String qW;
        public final long qX;
        public final long qY;

        public zzb(String str, long j, long j2) {
            this.qW = str;
            this.qX = j;
            this.qY = j2;
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof zzb)) {
                return false;
            }
            zzb zzbVar = (zzb) obj;
            return com.google.android.gms.common.internal.zzaa.equal(this.qW, zzbVar.qW) && com.google.android.gms.common.internal.zzaa.equal(Long.valueOf(this.qX), Long.valueOf(zzbVar.qX)) && com.google.android.gms.common.internal.zzaa.equal(Long.valueOf(this.qY), Long.valueOf(zzbVar.qY));
        }

        public final int hashCode() {
            return Arrays.hashCode(new Object[]{this.qW, Long.valueOf(this.qX), Long.valueOf(this.qY)});
        }
    }

    private static long zzd(String str, long j) {
        if (str == null || str.isEmpty()) {
            return zzpd.zzm(ByteBuffer.allocate(8).putLong(j).array());
        }
        byte[] bytes = str.getBytes(UTF_8);
        ByteBuffer allocate = ByteBuffer.allocate(bytes.length + 8);
        allocate.put(bytes);
        allocate.putLong(j);
        return zzpd.zzm(allocate.array());
    }

    @Override // com.google.android.gms.clearcut.zzb.InterfaceC0045zzb
    public final boolean zzg(String str, int i) {
        String zza$3a3f5217;
        if (str == null || str.isEmpty()) {
            str = i >= 0 ? String.valueOf(i) : null;
        }
        if (str == null) {
            return true;
        }
        zza zzaVar = this.qV;
        long long$5e91314b = zzaVar.mContentResolver == null ? 0L : zzaeo.getLong$5e91314b(zzaVar.mContentResolver, "android_id");
        zza zzaVar2 = this.qV;
        if (zzaVar2.mContentResolver == null) {
            zza$3a3f5217 = null;
        } else {
            ContentResolver contentResolver = zzaVar2.mContentResolver;
            String valueOf = String.valueOf("gms:playlog:service:sampling_");
            String valueOf2 = String.valueOf(str);
            zza$3a3f5217 = zzaeo.zza$3a3f5217(contentResolver, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        }
        zzb zzgt = zzgt(zza$3a3f5217);
        if (zzgt == null) {
            return true;
        }
        long zzd = zzd(zzgt.qW, long$5e91314b);
        long j = zzgt.qX;
        long j2 = zzgt.qY;
        if (j < 0 || j2 < 0) {
            throw new IllegalArgumentException(new StringBuilder(72).append("negative values not supported: ").append(j).append("/").append(j2).toString());
        }
        if (j2 > 0) {
            if ((zzd >= 0 ? zzd % j2 : (((zzd & Long.MAX_VALUE) % j2) + ((Long.MAX_VALUE % j2) + 1)) % j2) < j) {
                return true;
            }
        }
        return false;
    }
}
