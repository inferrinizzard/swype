package com.google.android.gms.internal;

import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public abstract class zzbp implements Callable {
    protected final String TAG = getClass().getSimpleName();
    protected final String className;
    protected final zzax zzaey;
    protected final zzae.zza zzaha;
    protected final String zzahf;
    protected Method zzahh;
    protected final int zzahl;
    protected final int zzahm;

    public zzbp(zzax zzaxVar, String str, String str2, zzae.zza zzaVar, int i, int i2) {
        this.zzaey = zzaxVar;
        this.className = str;
        this.zzahf = str2;
        this.zzaha = zzaVar;
        this.zzahl = i;
        this.zzahm = i2;
    }

    protected abstract void zzcu() throws IllegalAccessException, InvocationTargetException;

    /* JADX INFO: Access modifiers changed from: private */
    @Override // java.util.concurrent.Callable
    /* renamed from: zzcx, reason: merged with bridge method [inline-methods] */
    public Void call() throws Exception {
        try {
            long nanoTime = System.nanoTime();
            this.zzahh = this.zzaey.zzc(this.className, this.zzahf);
            if (this.zzahh != null) {
                zzcu();
                zzam zzamVar = this.zzaey.zzago;
                if (zzamVar != null && this.zzahl != Integer.MIN_VALUE) {
                    zzamVar.zza(this.zzahm, this.zzahl, (System.nanoTime() - nanoTime) / 1000);
                }
            }
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e2) {
        }
        return null;
    }
}
