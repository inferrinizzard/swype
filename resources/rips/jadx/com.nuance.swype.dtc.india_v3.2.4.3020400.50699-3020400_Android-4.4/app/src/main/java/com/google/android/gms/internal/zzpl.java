package com.google.android.gms.internal;

import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

/* loaded from: classes.dex */
public final class zzpl extends zzpo<com.google.android.gms.common.api.zzb> {
    private int sH;
    private boolean sI;

    public final void zza(zzpj<?> zzpjVar, ConnectionResult connectionResult) {
        Object obj = null;
        synchronized (obj) {
            SimpleArrayMap simpleArrayMap = null;
            try {
                simpleArrayMap.put(zzpjVar, connectionResult);
                this.sH--;
                if (!connectionResult.isSuccess()) {
                    this.sI = true;
                }
                if (this.sH == 0) {
                    Status status = this.sI ? new Status(13) : Status.sq;
                    SimpleArrayMap simpleArrayMap2 = null;
                    zzc((zzpl) (simpleArrayMap2.size() == 1 ? new com.google.android.gms.common.api.zza(status) : new com.google.android.gms.common.api.zzb(status)));
                }
            } finally {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Incorrect condition in loop: B:6:0x0010 */
    @Override // com.google.android.gms.internal.zzpo
    /* renamed from: zzy, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.common.api.zzb zzc(com.google.android.gms.common.api.Status r4) {
        /*
            r3 = this;
            r0 = 0
            monitor-enter(r0)
            com.google.android.gms.common.ConnectionResult r2 = new com.google.android.gms.common.ConnectionResult     // Catch: java.lang.Throwable -> L36
            r0 = 8
            r2.<init>(r0)     // Catch: java.lang.Throwable -> L36
            r0 = 0
            r1 = r0
        Lb:
            r0 = 0
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L36
            if (r1 >= r0) goto L20
            r0 = 0
            java.lang.Object r0 = r0.keyAt(r1)     // Catch: java.lang.Throwable -> L36
            com.google.android.gms.internal.zzpj r0 = (com.google.android.gms.internal.zzpj) r0     // Catch: java.lang.Throwable -> L36
            r3.zza(r0, r2)     // Catch: java.lang.Throwable -> L36
            int r0 = r1 + 1
            r1 = r0
            goto Lb
        L20:
            r0 = 0
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L36
            r1 = 1
            if (r0 != r1) goto L30
            com.google.android.gms.common.api.zza r0 = new com.google.android.gms.common.api.zza     // Catch: java.lang.Throwable -> L36
            r0.<init>(r4)     // Catch: java.lang.Throwable -> L36
        L2d:
            r1 = 0
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L36
            return r0
        L30:
            com.google.android.gms.common.api.zzb r0 = new com.google.android.gms.common.api.zzb     // Catch: java.lang.Throwable -> L36
            r0.<init>(r4)     // Catch: java.lang.Throwable -> L36
            goto L2d
        L36:
            r0 = move-exception
            r1 = 0
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L36
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzpl.zzc(com.google.android.gms.common.api.Status):com.google.android.gms.common.api.zzb");
    }
}
