package com.google.android.gms.internal;

import com.google.android.gms.internal.zzau;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class zzbo {
    protected static final String TAG = zzbo.class.getSimpleName();
    private final String className;
    private final zzax zzaey;
    private final String zzahf;
    private List<Class> zzahi;
    private final int zzahg = 2;
    private volatile Method zzahh = null;
    private CountDownLatch zzahj = new CountDownLatch(1);

    public final Method zzcz() {
        if (this.zzahh != null) {
            return this.zzahh;
        }
        try {
            if (this.zzahj.await(2L, TimeUnit.SECONDS)) {
                return this.zzahh;
            }
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public zzbo(zzax zzaxVar, String str, String str2, List<Class> list) {
        this.zzaey = zzaxVar;
        this.className = str;
        this.zzahf = str2;
        this.zzahi = new ArrayList(list);
        this.zzaey.zzagg.submit(new Runnable() { // from class: com.google.android.gms.internal.zzbo.1
            @Override // java.lang.Runnable
            public final void run() {
                zzbo.zza(zzbo.this);
            }
        });
    }

    private String zzd(byte[] bArr, String str) throws zzau.zza, UnsupportedEncodingException {
        return new String(this.zzaey.zzagi.zzc(bArr, str), "UTF-8");
    }

    static /* synthetic */ void zza(zzbo zzboVar) {
        try {
            Class loadClass = zzboVar.zzaey.zzagh.loadClass(zzboVar.zzd(zzboVar.zzaey.zzagj, zzboVar.className));
            if (loadClass == null) {
                return;
            }
            zzboVar.zzahh = loadClass.getMethod(zzboVar.zzd(zzboVar.zzaey.zzagj, zzboVar.zzahf), (Class[]) zzboVar.zzahi.toArray(new Class[zzboVar.zzahi.size()]));
            if (zzboVar.zzahh == null) {
            }
        } catch (zzau.zza e) {
        } catch (UnsupportedEncodingException e2) {
        } catch (ClassNotFoundException e3) {
        } catch (NoSuchMethodException e4) {
        } catch (NullPointerException e5) {
        } finally {
            zzboVar.zzahj.countDown();
        }
    }
}
