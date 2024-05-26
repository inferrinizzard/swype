package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zzsc;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class zzsb {
    private static zzsc KG;
    private static final zzb.zza KH = new zzb.zza() { // from class: com.google.android.gms.internal.zzsb.1
        @Override // com.google.android.gms.internal.zzsb.zzb.zza
        public final int zzd(Context context, String str, boolean z) {
            return zzsb.zzd(context, str, z);
        }

        @Override // com.google.android.gms.internal.zzsb.zzb.zza
        public final int zzt(Context context, String str) {
            return zzsb.zzt(context, str);
        }
    };
    public static final zzb KI = new zzb() { // from class: com.google.android.gms.internal.zzsb.2
        @Override // com.google.android.gms.internal.zzsb.zzb
        public final zzb.C0109zzb zza(Context context, String str, zzb.zza zzaVar) {
            zzb.C0109zzb c0109zzb = new zzb.C0109zzb();
            c0109zzb.KQ = zzaVar.zzd(context, str, true);
            if (c0109zzb.KQ != 0) {
                c0109zzb.KR = 1;
            } else {
                c0109zzb.KP = zzaVar.zzt(context, str);
                if (c0109zzb.KP != 0) {
                    c0109zzb.KR = -1;
                }
            }
            return c0109zzb;
        }
    };
    public static final zzb KJ = new zzb() { // from class: com.google.android.gms.internal.zzsb.3
        @Override // com.google.android.gms.internal.zzsb.zzb
        public final zzb.C0109zzb zza(Context context, String str, zzb.zza zzaVar) {
            zzb.C0109zzb c0109zzb = new zzb.C0109zzb();
            c0109zzb.KP = zzaVar.zzt(context, str);
            if (c0109zzb.KP != 0) {
                c0109zzb.KR = -1;
            } else {
                c0109zzb.KQ = zzaVar.zzd(context, str, true);
                if (c0109zzb.KQ != 0) {
                    c0109zzb.KR = 1;
                }
            }
            return c0109zzb;
        }
    };
    public static final zzb KK = new zzb() { // from class: com.google.android.gms.internal.zzsb.4
        @Override // com.google.android.gms.internal.zzsb.zzb
        public final zzb.C0109zzb zza(Context context, String str, zzb.zza zzaVar) {
            zzb.C0109zzb c0109zzb = new zzb.C0109zzb();
            c0109zzb.KP = zzaVar.zzt(context, str);
            c0109zzb.KQ = zzaVar.zzd(context, str, true);
            if (c0109zzb.KP == 0 && c0109zzb.KQ == 0) {
                c0109zzb.KR = 0;
            } else if (c0109zzb.KP >= c0109zzb.KQ) {
                c0109zzb.KR = -1;
            } else {
                c0109zzb.KR = 1;
            }
            return c0109zzb;
        }
    };
    public static final zzb KL = new zzb() { // from class: com.google.android.gms.internal.zzsb.5
        @Override // com.google.android.gms.internal.zzsb.zzb
        public final zzb.C0109zzb zza(Context context, String str, zzb.zza zzaVar) {
            zzb.C0109zzb c0109zzb = new zzb.C0109zzb();
            c0109zzb.KP = zzaVar.zzt(context, str);
            c0109zzb.KQ = zzaVar.zzd(context, str, true);
            if (c0109zzb.KP == 0 && c0109zzb.KQ == 0) {
                c0109zzb.KR = 0;
            } else if (c0109zzb.KQ >= c0109zzb.KP) {
                c0109zzb.KR = 1;
            } else {
                c0109zzb.KR = -1;
            }
            return c0109zzb;
        }
    };
    public static final zzb KM = new zzb() { // from class: com.google.android.gms.internal.zzsb.6
        @Override // com.google.android.gms.internal.zzsb.zzb
        public final zzb.C0109zzb zza(Context context, String str, zzb.zza zzaVar) {
            zzb.C0109zzb c0109zzb = new zzb.C0109zzb();
            c0109zzb.KP = zzaVar.zzt(context, str);
            if (c0109zzb.KP != 0) {
                c0109zzb.KQ = zzaVar.zzd(context, str, false);
            } else {
                c0109zzb.KQ = zzaVar.zzd(context, str, true);
            }
            if (c0109zzb.KP == 0 && c0109zzb.KQ == 0) {
                c0109zzb.KR = 0;
            } else if (c0109zzb.KQ >= c0109zzb.KP) {
                c0109zzb.KR = 1;
            } else {
                c0109zzb.KR = -1;
            }
            return c0109zzb;
        }
    };
    final Context KN;

    /* loaded from: classes.dex */
    public static class zza extends Exception {
        private zza(String str) {
            super(str);
        }

        /* synthetic */ zza(String str, byte b) {
            this(str);
        }

        private zza(String str, Throwable th) {
            super(str, th);
        }

        /* synthetic */ zza(String str, Throwable th, byte b) {
            this(str, th);
        }
    }

    /* loaded from: classes.dex */
    public interface zzb {

        /* loaded from: classes.dex */
        public interface zza {
            int zzd(Context context, String str, boolean z);

            int zzt(Context context, String str);
        }

        /* renamed from: com.google.android.gms.internal.zzsb$zzb$zzb, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        public static class C0109zzb {
            public int KP = 0;
            public int KQ = 0;
            public int KR = 0;
        }

        C0109zzb zza(Context context, String str, zza zzaVar);
    }

    private zzsb(Context context) {
        this.KN = (Context) com.google.android.gms.common.internal.zzab.zzy(context);
    }

    public static zzsb zza(Context context, zzb zzbVar, String str) throws zza {
        byte b = 0;
        zzb.C0109zzb zza2 = zzbVar.zza(context, str, KH);
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 68 + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza2.KP).append(" and remote module ").append(str).append(":").append(zza2.KQ).toString());
        if (zza2.KR == 0 || ((zza2.KR == -1 && zza2.KP == 0) || (zza2.KR == 1 && zza2.KQ == 0))) {
            throw new zza(new StringBuilder(91).append("No acceptable module found. Local version is ").append(zza2.KP).append(" and remote version is ").append(zza2.KQ).append(".").toString(), b);
        }
        if (zza2.KR == -1) {
            return zzv(context, str);
        }
        if (zza2.KR != 1) {
            throw new zza(new StringBuilder(47).append("VersionPolicy returned invalid code:").append(zza2.KR).toString(), b);
        }
        try {
            return zza(context, str, zza2.KQ);
        } catch (zza e) {
            String valueOf = String.valueOf(e.getMessage());
            Log.w("DynamiteModule", valueOf.length() != 0 ? "Failed to load remote module: ".concat(valueOf) : new String("Failed to load remote module: "));
            if (zza2.KP != 0) {
                final int i = zza2.KP;
                if (zzbVar.zza(context, str, new zzb.zza() { // from class: com.google.android.gms.internal.zzsb.7
                    @Override // com.google.android.gms.internal.zzsb.zzb.zza
                    public final int zzd(Context context2, String str2, boolean z) {
                        return 0;
                    }

                    @Override // com.google.android.gms.internal.zzsb.zzb.zza
                    public final int zzt(Context context2, String str2) {
                        return i;
                    }
                }).KR == -1) {
                    return zzv(context, str);
                }
            }
            throw new zza("Remote load failed. No local fallback found.", e, b);
        }
    }

    private static zzsb zza(Context context, String str, int i) throws zza {
        byte b = 0;
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zzsc zzcs = zzcs(context);
        if (zzcs == null) {
            throw new zza("Failed to create IDynamiteLoader.", b);
        }
        try {
            com.google.android.gms.dynamic.zzd zza2 = zzcs.zza(com.google.android.gms.dynamic.zze.zzac(context), str, i);
            if (com.google.android.gms.dynamic.zze.zzad(zza2) == null) {
                throw new zza("Failed to load remote module.", b);
            }
            return new zzsb((Context) com.google.android.gms.dynamic.zze.zzad(zza2));
        } catch (RemoteException e) {
            throw new zza("Failed to load remote module.", e, b);
        }
    }

    private static zzsc zzcs(Context context) {
        synchronized (zzsb.class) {
            if (KG != null) {
                return KG;
            }
            if (com.google.android.gms.common.zzc.zzang().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            }
            try {
                zzsc zzfd = zzsc.zza.zzfd((IBinder) context.createPackageContext("com.google.android.gms", 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance());
                if (zzfd != null) {
                    KG = zzfd;
                    return zzfd;
                }
            } catch (Exception e) {
                String valueOf = String.valueOf(e.getMessage());
                Log.e("DynamiteModule", valueOf.length() != 0 ? "Failed to load IDynamiteLoader from GmsCore: ".concat(valueOf) : new String("Failed to load IDynamiteLoader from GmsCore: "));
            }
            return null;
        }
    }

    public static int zzd(Context context, String str, boolean z) {
        zzsc zzcs = zzcs(context);
        if (zzcs == null) {
            return 0;
        }
        try {
            return zzcs.zza(com.google.android.gms.dynamic.zze.zzac(context), str, z);
        } catch (RemoteException e) {
            String valueOf = String.valueOf(e.getMessage());
            Log.w("DynamiteModule", valueOf.length() != 0 ? "Failed to retrieve remote module version: ".concat(valueOf) : new String("Failed to retrieve remote module version: "));
            return 0;
        }
    }

    public static int zzt(Context context, String str) {
        int i;
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            String valueOf = String.valueOf("com.google.android.gms.dynamite.descriptors.");
            String valueOf2 = String.valueOf("ModuleDescriptor");
            Class<?> loadClass = classLoader.loadClass(new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(str).length() + String.valueOf(valueOf2).length()).append(valueOf).append(str).append(".").append(valueOf2).toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (declaredField.get(null).equals(str)) {
                i = declaredField2.getInt(null);
            } else {
                String valueOf3 = String.valueOf(declaredField.get(null));
                Log.e("DynamiteModule", new StringBuilder(String.valueOf(valueOf3).length() + 51 + String.valueOf(str).length()).append("Module descriptor id '").append(valueOf3).append("' didn't match expected id '").append(str).append("'").toString());
                i = 0;
            }
            return i;
        } catch (ClassNotFoundException e) {
            Log.w("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 45).append("Local module descriptor class for ").append(str).append(" not found.").toString());
            return 0;
        } catch (Exception e2) {
            String valueOf4 = String.valueOf(e2.getMessage());
            Log.e("DynamiteModule", valueOf4.length() != 0 ? "Failed to load module descriptor class: ".concat(valueOf4) : new String("Failed to load module descriptor class: "));
            return 0;
        }
    }

    private static zzsb zzv(Context context, String str) {
        String valueOf = String.valueOf(str);
        Log.i("DynamiteModule", valueOf.length() != 0 ? "Selected local version of ".concat(valueOf) : new String("Selected local version of "));
        return new zzsb(context.getApplicationContext());
    }
}
