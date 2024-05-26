package com.google.android.gms.internal;

import java.util.concurrent.Future;

@zzin
/* loaded from: classes.dex */
public abstract class zzkc implements zzkj<Future> {
    volatile Thread zzckk;
    private boolean zzckl;
    private final Runnable zzw;

    public zzkc() {
        this.zzw = new Runnable() { // from class: com.google.android.gms.internal.zzkc.1
            @Override // java.lang.Runnable
            public final void run() {
                zzkc.this.zzckk = Thread.currentThread();
                zzkc.this.zzew();
            }
        };
        this.zzckl = false;
    }

    public zzkc(boolean z) {
        this.zzw = new Runnable() { // from class: com.google.android.gms.internal.zzkc.1
            @Override // java.lang.Runnable
            public final void run() {
                zzkc.this.zzckk = Thread.currentThread();
                zzkc.this.zzew();
            }
        };
        this.zzckl = z;
    }

    @Override // com.google.android.gms.internal.zzkj
    public final void cancel() {
        onStop();
        if (this.zzckk != null) {
            this.zzckk.interrupt();
        }
    }

    public abstract void onStop();

    public abstract void zzew();

    @Override // com.google.android.gms.internal.zzkj
    /* renamed from: zzsz, reason: merged with bridge method [inline-methods] */
    public final Future zzpy() {
        return this.zzckl ? zzkg.zza(1, this.zzw) : zzkg.zza(this.zzw);
    }
}
