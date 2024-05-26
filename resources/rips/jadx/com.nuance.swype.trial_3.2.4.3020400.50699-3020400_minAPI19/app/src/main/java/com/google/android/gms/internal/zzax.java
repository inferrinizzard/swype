package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gass.internal.zza;
import com.google.android.gms.internal.zzae;
import com.google.android.gms.internal.zzau;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.IME;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class zzax {
    private static final String TAG = zzax.class.getSimpleName();
    protected static final Object zzagr = new Object();
    private static com.google.android.gms.common.zzc zzagt = null;
    protected Context zzagf;
    ExecutorService zzagg;
    DexClassLoader zzagh;
    zzau zzagi;
    byte[] zzagj;
    zzam zzago;
    private volatile AdvertisingIdClient zzagk = null;
    private volatile boolean zzafn = false;
    private Future zzagl = null;
    volatile zzae.zza zzagm = null;
    Future zzagn = null;
    GoogleApiClient zzagp = null;
    protected boolean zzagq = false;
    protected boolean zzags = false;
    protected boolean zzagu = false;
    private Map<Pair<String, String>, zzbo> zzagv = new HashMap();

    private zzax(Context context) {
        this.zzagf = context;
    }

    public static zzax zza(Context context, String str, String str2, boolean z) {
        zzax zzaxVar = new zzax(context);
        try {
            zzaxVar.zzc(str, str2, z);
            return zzaxVar;
        } catch (zzaw e) {
            return null;
        }
    }

    private static void zza(File file) {
        if (file.exists()) {
            file.delete();
        } else {
            String.format("File %s not found. No need for deletion", file.getAbsolutePath());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x009a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void zza(java.io.File r10, java.lang.String r11) {
        /*
            r9 = this;
            r6 = 2
            r5 = 1
            r4 = 0
            java.io.File r2 = new java.io.File
            java.lang.String r0 = "%s/%s.tmp"
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r4] = r10
            r1[r5] = r11
            java.lang.String r0 = java.lang.String.format(r0, r1)
            r2.<init>(r0)
            boolean r0 = r2.exists()
            if (r0 == 0) goto L1c
        L1b:
            return
        L1c:
            java.io.File r3 = new java.io.File
            java.lang.String r0 = "%s/%s.dex"
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r4] = r10
            r1[r5] = r11
            java.lang.String r0 = java.lang.String.format(r0, r1)
            r3.<init>(r0)
            boolean r0 = r3.exists()
            if (r0 == 0) goto L1b
            r1 = 0
            long r4 = r3.length()
            r6 = 0
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 <= 0) goto L1b
            int r0 = (int) r4
            byte[] r4 = new byte[r0]
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch: com.google.android.gms.internal.zzau.zza -> L96 java.lang.Throwable -> La2 java.security.NoSuchAlgorithmException -> Lbb java.io.IOException -> Lc0
            r0.<init>(r3)     // Catch: com.google.android.gms.internal.zzau.zza -> L96 java.lang.Throwable -> La2 java.security.NoSuchAlgorithmException -> Lbb java.io.IOException -> Lc0
            int r1 = r0.read(r4)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            if (r1 > 0) goto L54
            r0.close()     // Catch: java.io.IOException -> Lac
        L50:
            zza(r3)
            goto L1b
        L54:
            com.google.android.gms.internal.zzae$zzd r1 = new com.google.android.gms.internal.zzae$zzd     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r1.<init>()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            java.lang.String r5 = android.os.Build.VERSION.SDK     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r5 = r5.getBytes()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r1.zzev = r5     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r5 = r11.getBytes()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r1.zzeu = r5     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            com.google.android.gms.internal.zzau r5 = r9.zzagi     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r6 = r9.zzagj     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            java.lang.String r4 = r5.zzd(r6, r4)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r4 = r4.getBytes()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r1.data = r4     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r4 = com.google.android.gms.internal.zzak.zzg(r4)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r1.zzet = r4     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r2.createNewFile()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r4.<init>(r2)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            byte[] r1 = com.google.android.gms.internal.zzapv.zzf(r1)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r2 = 0
            int r5 = r1.length     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r4.write(r1, r2, r5)     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r4.close()     // Catch: java.lang.Throwable -> Lb4 com.google.android.gms.internal.zzau.zza -> Lb9 java.security.NoSuchAlgorithmException -> Lbe java.io.IOException -> Lc3
            r0.close()     // Catch: java.io.IOException -> Lae
        L92:
            zza(r3)
            goto L1b
        L96:
            r0 = move-exception
            r0 = r1
        L98:
            if (r0 == 0) goto L9d
            r0.close()     // Catch: java.io.IOException -> Lb0
        L9d:
            zza(r3)
            goto L1b
        La2:
            r0 = move-exception
        La3:
            if (r1 == 0) goto La8
            r1.close()     // Catch: java.io.IOException -> Lb2
        La8:
            zza(r3)
            throw r0
        Lac:
            r0 = move-exception
            goto L50
        Lae:
            r0 = move-exception
            goto L92
        Lb0:
            r0 = move-exception
            goto L9d
        Lb2:
            r1 = move-exception
            goto La8
        Lb4:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
            goto La3
        Lb9:
            r1 = move-exception
            goto L98
        Lbb:
            r0 = move-exception
            r0 = r1
            goto L98
        Lbe:
            r1 = move-exception
            goto L98
        Lc0:
            r0 = move-exception
            r0 = r1
            goto L98
        Lc3:
            r1 = move-exception
            goto L98
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzax.zza(java.io.File, java.lang.String):void");
    }

    private static void zzn(String str) {
        zza(new File(str));
    }

    public final Context getContext() {
        return this.zzagf;
    }

    public final boolean zza(String str, String str2, List<Class> list) {
        if (this.zzagv.containsKey(new Pair(str, str2))) {
            return false;
        }
        this.zzagv.put(new Pair<>(str, str2), new zzbo(this, str, str2, list));
        return true;
    }

    public final Method zzc(String str, String str2) {
        zzbo zzboVar = this.zzagv.get(new Pair(str, str2));
        if (zzboVar == null) {
            return null;
        }
        return zzboVar.zzcz();
    }

    public final boolean zzci() {
        return this.zzagq;
    }

    public final boolean zzcj() {
        return this.zzagu;
    }

    public final AdvertisingIdClient zzcr() {
        if (!this.zzafn) {
            return null;
        }
        if (this.zzagk != null) {
            return this.zzagk;
        }
        if (this.zzagl != null) {
            try {
                this.zzagl.get(IME.NEXT_SCAN_IN_MILLIS, TimeUnit.MILLISECONDS);
                this.zzagl = null;
            } catch (InterruptedException e) {
            } catch (ExecutionException e2) {
            } catch (TimeoutException e3) {
                this.zzagl.cancel(true);
            }
        }
        return this.zzagk;
    }

    public final void zzcs() {
        synchronized (zzagr) {
            if (this.zzagu) {
                return;
            }
            if (!this.zzags || this.zzagp == null) {
                this.zzagu = false;
            } else {
                this.zzagp.connect();
                this.zzagu = true;
            }
        }
    }

    public final void zzct() {
        synchronized (zzagr) {
            if (this.zzagu && this.zzagp != null) {
                this.zzagp.disconnect();
                this.zzagu = false;
            }
        }
    }

    private boolean zzc(String str, String str2, boolean z) throws zzaw {
        this.zzagg = Executors.newCachedThreadPool();
        this.zzafn = z;
        if (z) {
            this.zzagl = this.zzagg.submit(new Runnable() { // from class: com.google.android.gms.internal.zzax.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzax.zzb(zzax.this);
                }
            });
        }
        zzagt = com.google.android.gms.common.zzc.zzang();
        this.zzagq = zzagt.zzbn(this.zzagf) > 0;
        this.zzags = zzagt.isGooglePlayServicesAvailable(this.zzagf) == 0;
        if (this.zzagf.getApplicationContext() != null) {
            this.zzagp = new GoogleApiClient.Builder(this.zzagf).addApi(com.google.android.gms.clearcut.zzb.API).build();
        }
        zzdc.initialize(this.zzagf);
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbu)).booleanValue()) {
            this.zzagn = this.zzagg.submit(new Runnable() { // from class: com.google.android.gms.internal.zzax.2
                @Override // java.lang.Runnable
                public final void run() {
                    zzax.zzc(zzax.this);
                }
            });
        }
        this.zzagi = new zzau();
        try {
            this.zzagj = this.zzagi.zzl(str);
            zzm(str2);
            this.zzago = new zzam(this);
            return true;
        } catch (zzau.zza e) {
            throw new zzaw(e);
        }
    }

    private boolean zzm(String str) throws zzaw {
        try {
            File cacheDir = this.zzagf.getCacheDir();
            if (cacheDir == null && (cacheDir = this.zzagf.getDir("dex", 0)) == null) {
                throw new zzaw();
            }
            File file = cacheDir;
            File file2 = new File(String.format("%s/%s.jar", file, "1460683162801"));
            if (!file2.exists()) {
                byte[] zzc = this.zzagi.zzc(this.zzagj, str);
                file2.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(zzc, 0, zzc.length);
                fileOutputStream.close();
            }
            zzb(file, "1460683162801");
            try {
                this.zzagh = new DexClassLoader(file2.getAbsolutePath(), file.getAbsolutePath(), null, this.zzagf.getClassLoader());
                zza(file2);
                zza(file, "1460683162801");
                zzn(String.format("%s/%s.dex", file, "1460683162801"));
                return true;
            } catch (Throwable th) {
                zza(file2);
                zza(file, "1460683162801");
                zzn(String.format("%s/%s.dex", file, "1460683162801"));
                throw th;
            }
        } catch (zzau.zza e) {
            throw new zzaw(e);
        } catch (FileNotFoundException e2) {
            throw new zzaw(e2);
        } catch (IOException e3) {
            throw new zzaw(e3);
        } catch (NullPointerException e4) {
            throw new zzaw(e4);
        }
    }

    private boolean zzb(File file, String str) {
        boolean z;
        File file2 = new File(String.format("%s/%s.tmp", file, str));
        if (!file2.exists()) {
            return false;
        }
        File file3 = new File(String.format("%s/%s.dex", file, str));
        if (file3.exists()) {
            return false;
        }
        try {
            long length = file2.length();
            if (length <= 0) {
                zza(file2);
                z = false;
            } else {
                byte[] bArr = new byte[(int) length];
                if (new FileInputStream(file2).read(bArr) <= 0) {
                    zza(file2);
                    z = false;
                } else {
                    zzae.zzd zzdVar = (zzae.zzd) zzapv.zzb$16844d7a(new zzae.zzd(), bArr, bArr.length);
                    if (str.equals(new String(zzdVar.zzeu)) && Arrays.equals(zzdVar.zzet, zzak.zzg(zzdVar.data)) && Arrays.equals(zzdVar.zzev, Build.VERSION.SDK.getBytes())) {
                        byte[] zzc = this.zzagi.zzc(this.zzagj, new String(zzdVar.data));
                        file3.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file3);
                        fileOutputStream.write(zzc, 0, zzc.length);
                        fileOutputStream.close();
                        z = true;
                    } else {
                        zza(file2);
                        z = false;
                    }
                }
            }
            return z;
        } catch (zzau.zza | IOException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    public final int zzat() {
        return this.zzago != null ? zzam.zzat() : Integers.STATUS_SUCCESS;
    }

    static /* synthetic */ void zzb(zzax zzaxVar) {
        try {
            if (zzaxVar.zzagk == null) {
                AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(zzaxVar.zzagf);
                advertisingIdClient.start();
                zzaxVar.zzagk = advertisingIdClient;
            }
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e) {
            zzaxVar.zzagk = null;
        }
    }

    static /* synthetic */ void zzc(zzax zzaxVar) {
        if (zzaxVar.zzags) {
            try {
                zzaxVar.zzagm = new zza.C0056zza(zzaxVar.zzagf, zzaxVar.zzagf.getPackageName(), Integer.toString(zzaxVar.zzagf.getPackageManager().getPackageInfo(zzaxVar.zzagf.getPackageName(), 0).versionCode)).zzsi$1d3d48d2();
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
    }
}
