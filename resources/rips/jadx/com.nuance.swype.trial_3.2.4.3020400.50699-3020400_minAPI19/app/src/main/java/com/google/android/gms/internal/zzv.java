package com.google.android.gms.internal;

import android.os.SystemClock;
import com.google.android.gms.internal.zzb;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class zzv implements com.google.android.gms.internal.zzb {
    private final Map<String, zza> zzbw;
    private long zzbx;
    private final File zzby;
    private final int zzbz;

    /* loaded from: classes.dex */
    private static class zzb extends FilterInputStream {
        private int zzcc;

        private zzb(InputStream inputStream) {
            super(inputStream);
            this.zzcc = 0;
        }

        /* synthetic */ zzb(InputStream inputStream, byte b) {
            this(inputStream);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final int read() throws IOException {
            int read = super.read();
            if (read != -1) {
                this.zzcc++;
            }
            return read;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) throws IOException {
            int read = super.read(bArr, i, i2);
            if (read != -1) {
                this.zzcc += read;
            }
            return read;
        }
    }

    private zzv(File file) {
        this.zzbw = new LinkedHashMap(16, 0.75f, true);
        this.zzbx = 0L;
        this.zzby = file;
        this.zzbz = 5242880;
    }

    public zzv(File file, byte b) {
        this(file);
    }

    private static int zza(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        if (read == -1) {
            throw new EOFException();
        }
        return read;
    }

    static void zza(OutputStream outputStream, int i) throws IOException {
        outputStream.write((i >> 0) & 255);
        outputStream.write((i >> 8) & 255);
        outputStream.write((i >> 16) & 255);
        outputStream.write((i >> 24) & 255);
    }

    static void zza(OutputStream outputStream, long j) throws IOException {
        outputStream.write((byte) (j >>> 0));
        outputStream.write((byte) (j >>> 8));
        outputStream.write((byte) (j >>> 16));
        outputStream.write((byte) (j >>> 24));
        outputStream.write((byte) (j >>> 32));
        outputStream.write((byte) (j >>> 40));
        outputStream.write((byte) (j >>> 48));
        outputStream.write((byte) (j >>> 56));
    }

    static void zza(OutputStream outputStream, String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-8");
        zza(outputStream, bytes.length);
        outputStream.write(bytes, 0, bytes.length);
    }

    private void zza(String str, zza zzaVar) {
        if (this.zzbw.containsKey(str)) {
            this.zzbx = (zzaVar.zzca - this.zzbw.get(str).zzca) + this.zzbx;
        } else {
            this.zzbx += zzaVar.zzca;
        }
        this.zzbw.put(str, zzaVar);
    }

    private static byte[] zza(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 != i) {
            throw new IOException(new StringBuilder(50).append("Expected ").append(i).append(" bytes, read ").append(i2).append(" bytes").toString());
        }
        return bArr;
    }

    static int zzb(InputStream inputStream) throws IOException {
        return (zza(inputStream) << 0) | 0 | (zza(inputStream) << 8) | (zza(inputStream) << 16) | (zza(inputStream) << 24);
    }

    static long zzc(InputStream inputStream) throws IOException {
        return 0 | ((zza(inputStream) & 255) << 0) | ((zza(inputStream) & 255) << 8) | ((zza(inputStream) & 255) << 16) | ((zza(inputStream) & 255) << 24) | ((zza(inputStream) & 255) << 32) | ((zza(inputStream) & 255) << 40) | ((zza(inputStream) & 255) << 48) | ((zza(inputStream) & 255) << 56);
    }

    static String zzd(InputStream inputStream) throws IOException {
        return new String(zza(inputStream, (int) zzc(inputStream)), "UTF-8");
    }

    private static String zze(String str) {
        int length = str.length() / 2;
        String valueOf = String.valueOf(String.valueOf(str.substring(0, length).hashCode()));
        String valueOf2 = String.valueOf(String.valueOf(str.substring(length).hashCode()));
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    static Map<String, String> zze(InputStream inputStream) throws IOException {
        int zzb2 = zzb(inputStream);
        Map<String, String> emptyMap = zzb2 == 0 ? Collections.emptyMap() : new HashMap<>(zzb2);
        for (int i = 0; i < zzb2; i++) {
            emptyMap.put(zzd(inputStream).intern(), zzd(inputStream).intern());
        }
        return emptyMap;
    }

    private File zzf(String str) {
        return new File(this.zzby, zze(str));
    }

    @Override // com.google.android.gms.internal.zzb
    public final synchronized void initialize() {
        BufferedInputStream bufferedInputStream;
        if (this.zzby.exists()) {
            File[] listFiles = this.zzby.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    BufferedInputStream bufferedInputStream2 = null;
                    try {
                        bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    } catch (IOException e) {
                        bufferedInputStream = null;
                    } catch (Throwable th) {
                        th = th;
                    }
                    try {
                        try {
                            zza zzf = zza.zzf(bufferedInputStream);
                            zzf.zzca = file.length();
                            zza(zzf.zzcb, zzf);
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e2) {
                            }
                        } catch (Throwable th2) {
                            bufferedInputStream2 = bufferedInputStream;
                            th = th2;
                            if (bufferedInputStream2 != null) {
                                try {
                                    bufferedInputStream2.close();
                                } catch (IOException e3) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e4) {
                        if (file != null) {
                            file.delete();
                        }
                        if (bufferedInputStream != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e5) {
                            }
                        }
                    }
                }
            }
        } else if (!this.zzby.mkdirs()) {
            zzs.zzc("Unable to create cache dir %s", this.zzby.getAbsolutePath());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class zza {
        public String zza;
        public long zzb;
        public long zzc;
        public long zzca;
        public String zzcb;
        public long zzd;
        public long zze;
        public Map<String, String> zzf;

        private zza() {
        }

        public zza(String str, zzb.zza zzaVar) {
            this.zzcb = str;
            this.zzca = zzaVar.data.length;
            this.zza = zzaVar.zza;
            this.zzb = zzaVar.zzb;
            this.zzc = zzaVar.zzc;
            this.zzd = zzaVar.zzd;
            this.zze = zzaVar.zze;
            this.zzf = zzaVar.zzf;
        }

        public static zza zzf(InputStream inputStream) throws IOException {
            zza zzaVar = new zza();
            if (zzv.zzb(inputStream) != 538247942) {
                throw new IOException();
            }
            zzaVar.zzcb = zzv.zzd(inputStream);
            zzaVar.zza = zzv.zzd(inputStream);
            if (zzaVar.zza.equals("")) {
                zzaVar.zza = null;
            }
            zzaVar.zzb = zzv.zzc(inputStream);
            zzaVar.zzc = zzv.zzc(inputStream);
            zzaVar.zzd = zzv.zzc(inputStream);
            zzaVar.zze = zzv.zzc(inputStream);
            zzaVar.zzf = zzv.zze(inputStream);
            return zzaVar;
        }

        public final boolean zza(OutputStream outputStream) {
            try {
                zzv.zza(outputStream, 538247942);
                zzv.zza(outputStream, this.zzcb);
                zzv.zza(outputStream, this.zza == null ? "" : this.zza);
                zzv.zza(outputStream, this.zzb);
                zzv.zza(outputStream, this.zzc);
                zzv.zza(outputStream, this.zzd);
                zzv.zza(outputStream, this.zze);
                Map<String, String> map = this.zzf;
                if (map != null) {
                    zzv.zza(outputStream, map.size());
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        zzv.zza(outputStream, entry.getKey());
                        zzv.zza(outputStream, entry.getValue());
                    }
                } else {
                    zzv.zza(outputStream, 0);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                zzs.zzb("%s", e.toString());
                return false;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0081 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // com.google.android.gms.internal.zzb
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized com.google.android.gms.internal.zzb.zza zza(java.lang.String r11) {
        /*
            r10 = this;
            r1 = 0
            monitor-enter(r10)
            java.util.Map<java.lang.String, com.google.android.gms.internal.zzv$zza> r0 = r10.zzbw     // Catch: java.lang.Throwable -> L85
            java.lang.Object r0 = r0.get(r11)     // Catch: java.lang.Throwable -> L85
            com.google.android.gms.internal.zzv$zza r0 = (com.google.android.gms.internal.zzv.zza) r0     // Catch: java.lang.Throwable -> L85
            if (r0 != 0) goto Lf
            r0 = r1
        Ld:
            monitor-exit(r10)
            return r0
        Lf:
            java.io.File r4 = r10.zzf(r11)     // Catch: java.lang.Throwable -> L85
            com.google.android.gms.internal.zzv$zzb r3 = new com.google.android.gms.internal.zzv$zzb     // Catch: java.io.IOException -> L57 java.lang.Throwable -> L7d
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.io.IOException -> L57 java.lang.Throwable -> L7d
            r2.<init>(r4)     // Catch: java.io.IOException -> L57 java.lang.Throwable -> L7d
            r5 = 0
            r3.<init>(r2, r5)     // Catch: java.io.IOException -> L57 java.lang.Throwable -> L7d
            com.google.android.gms.internal.zzv.zza.zzf(r3)     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r4.length()     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            int r2 = com.google.android.gms.internal.zzv.zzb.zza(r3)     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r8 = (long) r2     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r6 - r8
            int r2 = (int) r6     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            byte[] r5 = zza(r3, r2)     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            com.google.android.gms.internal.zzb$zza r2 = new com.google.android.gms.internal.zzb$zza     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.<init>()     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.data = r5     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            java.lang.String r5 = r0.zza     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zza = r5     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r0.zzb     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zzb = r6     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r0.zzc     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zzc = r6     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r0.zzd     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zzd = r6     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            long r6 = r0.zze     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zze = r6     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            java.util.Map<java.lang.String, java.lang.String> r0 = r0.zzf     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r2.zzf = r0     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L90
            r3.close()     // Catch: java.io.IOException -> L54 java.lang.Throwable -> L85
            r0 = r2
            goto Ld
        L54:
            r0 = move-exception
            r0 = r1
            goto Ld
        L57:
            r0 = move-exception
            r2 = r1
        L59:
            java.lang.String r3 = "%s: %s"
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L8d
            r6 = 0
            java.lang.String r4 = r4.getAbsolutePath()     // Catch: java.lang.Throwable -> L8d
            r5[r6] = r4     // Catch: java.lang.Throwable -> L8d
            r4 = 1
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L8d
            r5[r4] = r0     // Catch: java.lang.Throwable -> L8d
            com.google.android.gms.internal.zzs.zzb(r3, r5)     // Catch: java.lang.Throwable -> L8d
            r10.remove(r11)     // Catch: java.lang.Throwable -> L8d
            if (r2 == 0) goto L78
            r2.close()     // Catch: java.io.IOException -> L7a java.lang.Throwable -> L85
        L78:
            r0 = r1
            goto Ld
        L7a:
            r0 = move-exception
            r0 = r1
            goto Ld
        L7d:
            r0 = move-exception
            r3 = r1
        L7f:
            if (r3 == 0) goto L84
            r3.close()     // Catch: java.lang.Throwable -> L85 java.io.IOException -> L88
        L84:
            throw r0     // Catch: java.lang.Throwable -> L85
        L85:
            r0 = move-exception
            monitor-exit(r10)
            throw r0
        L88:
            r0 = move-exception
            r0 = r1
            goto Ld
        L8b:
            r0 = move-exception
            goto L7f
        L8d:
            r0 = move-exception
            r3 = r2
            goto L7f
        L90:
            r0 = move-exception
            r2 = r3
            goto L59
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzv.zza(java.lang.String):com.google.android.gms.internal.zzb$zza");
    }

    @Override // com.google.android.gms.internal.zzb
    public final synchronized void zza(String str, zzb.zza zzaVar) {
        FileOutputStream fileOutputStream;
        zza zzaVar2;
        int i;
        int i2 = 0;
        synchronized (this) {
            int length = zzaVar.data.length;
            if (this.zzbx + length >= this.zzbz) {
                if (zzs.DEBUG) {
                    zzs.zza("Pruning old cache entries.", new Object[0]);
                }
                long j = this.zzbx;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                Iterator<Map.Entry<String, zza>> it = this.zzbw.entrySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        i = i2;
                        break;
                    }
                    zza value = it.next().getValue();
                    if (zzf(value.zzcb).delete()) {
                        this.zzbx -= value.zzca;
                    } else {
                        zzs.zzb("Could not delete cache entry for key=%s, filename=%s", value.zzcb, zze(value.zzcb));
                    }
                    it.remove();
                    i = i2 + 1;
                    if (((float) (this.zzbx + length)) < this.zzbz * 0.9f) {
                        break;
                    } else {
                        i2 = i;
                    }
                }
                if (zzs.DEBUG) {
                    zzs.zza("pruned %d files, %d bytes, %d ms", Integer.valueOf(i), Long.valueOf(this.zzbx - j), Long.valueOf(SystemClock.elapsedRealtime() - elapsedRealtime));
                }
            }
            File zzf = zzf(str);
            try {
                fileOutputStream = new FileOutputStream(zzf);
                zzaVar2 = new zza(str, zzaVar);
            } catch (IOException e) {
                if (!zzf.delete()) {
                    zzs.zzb("Could not clean up file %s", zzf.getAbsolutePath());
                }
            }
            if (!zzaVar2.zza(fileOutputStream)) {
                fileOutputStream.close();
                zzs.zzb("Failed to write header for %s", zzf.getAbsolutePath());
                throw new IOException();
            }
            fileOutputStream.write(zzaVar.data);
            fileOutputStream.close();
            zza(str, zzaVar2);
        }
    }

    private synchronized void remove(String str) {
        boolean delete = zzf(str).delete();
        zza zzaVar = this.zzbw.get(str);
        if (zzaVar != null) {
            this.zzbx -= zzaVar.zzca;
            this.zzbw.remove(str);
        }
        if (!delete) {
            zzs.zzb("Could not delete cache entry for key=%s, filename=%s", str, zze(str));
        }
    }
}
