package com.google.android.gms.ads.internal;

import android.content.Context;
import android.view.MotionEvent;
import com.google.android.gms.internal.zzan;
import com.google.android.gms.internal.zzar;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkg;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: Access modifiers changed from: package-private */
@zzin
/* loaded from: classes.dex */
public final class zzi implements zzan, Runnable {
    private zzv zzajs;
    private final List<Object[]> zzalc = new Vector();
    private final AtomicReference<zzan> zzald = new AtomicReference<>();
    CountDownLatch zzale = new CountDownLatch(1);

    public zzi(zzv zzvVar) {
        this.zzajs = zzvVar;
        if (com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            zzkg.zza(this);
        } else {
            run();
        }
    }

    private boolean zzeo() {
        try {
            this.zzale.await();
            return true;
        } catch (InterruptedException e) {
            zzkd.zzd("Interrupted during GADSignals creation.", e);
            return false;
        }
    }

    private void zzep() {
        if (this.zzalc.isEmpty()) {
            return;
        }
        for (Object[] objArr : this.zzalc) {
            if (objArr.length == 1) {
                this.zzald.get().zza((MotionEvent) objArr[0]);
            } else if (objArr.length == 3) {
                this.zzald.get().zza(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
            }
        }
        this.zzalc.clear();
    }

    @Override // com.google.android.gms.internal.zzan
    public final void zza(int i, int i2, int i3) {
        zzan zzanVar = this.zzald.get();
        if (zzanVar == null) {
            this.zzalc.add(new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
        } else {
            zzep();
            zzanVar.zza(i, i2, i3);
        }
    }

    @Override // com.google.android.gms.internal.zzan
    public final void zza(MotionEvent motionEvent) {
        zzan zzanVar = this.zzald.get();
        if (zzanVar == null) {
            this.zzalc.add(new Object[]{motionEvent});
        } else {
            zzep();
            zzanVar.zza(motionEvent);
        }
    }

    @Override // com.google.android.gms.internal.zzan
    public final String zzb(Context context) {
        zzan zzanVar;
        if (!zzeo() || (zzanVar = this.zzald.get()) == null) {
            return "";
        }
        zzep();
        return zzanVar.zzb(zzi(context));
    }

    @Override // com.google.android.gms.internal.zzan
    public final String zzb(Context context, String str) {
        zzan zzanVar;
        if (!zzeo() || (zzanVar = this.zzald.get()) == null) {
            return "";
        }
        zzep();
        return zzanVar.zzb(zzi(context), str);
    }

    private static Context zzi(Context context) {
        Context applicationContext;
        return (((Boolean) zzu.zzfz().zzd(zzdc.zzayk)).booleanValue() && (applicationContext = context.getApplicationContext()) != null) ? applicationContext : context;
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            this.zzald.set(zzar.zza(this.zzajs.zzaow.zzcs, zzi(this.zzajs.zzagf), !((Boolean) zzu.zzfz().zzd(zzdc.zzayw)).booleanValue() || this.zzajs.zzaow.zzcnm));
        } finally {
            this.zzale.countDown();
            this.zzajs = null;
        }
    }
}
