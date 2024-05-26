package com.google.android.gms.internal;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public final class zzfg extends zzfd {
    private static final Set<String> zzbjp = Collections.synchronizedSet(new HashSet());
    private static final DecimalFormat zzbjq = new DecimalFormat("#,###");
    private File zzbjr;
    private boolean zzbjs;

    public zzfg(zzlh zzlhVar) {
        super(zzlhVar);
        File cacheDir = this.mContext.getCacheDir();
        if (cacheDir == null) {
            zzkd.zzcx("Context.getCacheDir() returned null");
            return;
        }
        this.zzbjr = new File(cacheDir, "admobVideoStreams");
        if (!this.zzbjr.isDirectory() && !this.zzbjr.mkdirs()) {
            String valueOf = String.valueOf(this.zzbjr.getAbsolutePath());
            zzkd.zzcx(valueOf.length() != 0 ? "Could not create preload cache directory at ".concat(valueOf) : new String("Could not create preload cache directory at "));
            this.zzbjr = null;
        } else {
            if (this.zzbjr.setReadable(true, false) && this.zzbjr.setExecutable(true, false)) {
                return;
            }
            String valueOf2 = String.valueOf(this.zzbjr.getAbsolutePath());
            zzkd.zzcx(valueOf2.length() != 0 ? "Could not set cache file permissions at ".concat(valueOf2) : new String("Could not set cache file permissions at "));
            this.zzbjr = null;
        }
    }

    private File zzb(File file) {
        return new File(this.zzbjr, String.valueOf(file.getName()).concat(".done"));
    }

    @Override // com.google.android.gms.internal.zzfd
    public final void abort() {
        this.zzbjs = true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:180:0x047a, code lost:            r11.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x0482, code lost:            if (com.google.android.gms.internal.zzkd.zzaz(3) == false) goto L159;     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x0484, code lost:            r2 = com.google.android.gms.internal.zzfg.zzbjq.format(r6);        com.google.android.gms.internal.zzkd.zzcv(new java.lang.StringBuilder((java.lang.String.valueOf(r2).length() + 22) + java.lang.String.valueOf(r27).length()).append("Preloaded ").append(r2).append(" bytes from ").append(r27).toString());     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x04c2, code lost:            r12.setReadable(true, false);     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x04cb, code lost:            if (r13.isFile() == false) goto L183;     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x04cd, code lost:            r13.setLastModified(java.lang.System.currentTimeMillis());     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x04e7, code lost:            r13.createNewFile();     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v33 */
    /* JADX WARN: Type inference failed for: r3v34, types: [com.google.android.gms.internal.zzfd] */
    /* JADX WARN: Type inference failed for: r3v45 */
    /* JADX WARN: Type inference failed for: r3v46 */
    /* JADX WARN: Type inference failed for: r3v50 */
    /* JADX WARN: Type inference failed for: r3v58 */
    /* JADX WARN: Type inference failed for: r3v59 */
    /* JADX WARN: Type inference failed for: r4v20, types: [long] */
    /* JADX WARN: Type inference failed for: r4v24, types: [java.lang.String] */
    @Override // com.google.android.gms.internal.zzfd
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean zzaz(java.lang.String r27) {
        /*
            Method dump skipped, instructions count: 1335
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzfg.zzaz(java.lang.String):boolean");
    }
}
