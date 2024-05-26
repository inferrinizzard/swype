package com.google.android.gms.internal;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.google.android.gms.internal.zzae;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public abstract class zzao implements zzan {
    protected MotionEvent zzafd;
    protected LinkedList<MotionEvent> zzafe = new LinkedList<>();
    protected long zzaff = 0;
    protected long zzafg = 0;
    protected long zzafh = 0;
    protected long zzafi = 0;
    protected long zzafj = 0;
    private boolean zzafk = false;
    protected DisplayMetrics zzafl;

    /* JADX INFO: Access modifiers changed from: protected */
    public zzao(Context context) {
        zzak.zzar();
        try {
            this.zzafl = context.getResources().getDisplayMetrics();
        } catch (UnsupportedOperationException e) {
            this.zzafl = new DisplayMetrics();
            this.zzafl.density = 1.0f;
        }
    }

    @Override // com.google.android.gms.internal.zzan
    public final void zza(int i, int i2, int i3) {
        if (this.zzafd != null) {
            this.zzafd.recycle();
        }
        this.zzafd = MotionEvent.obtain(0L, i3, 1, i * this.zzafl.density, i2 * this.zzafl.density, 0.0f, 0.0f, 0, 0.0f, 0.0f, 0, 0);
    }

    @Override // com.google.android.gms.internal.zzan
    public final String zzb(Context context) {
        return zza(context, (String) null, false);
    }

    @Override // com.google.android.gms.internal.zzan
    public final String zzb(Context context, String str) {
        return zza(context, str, true);
    }

    protected abstract zzae.zza zzc(Context context);

    protected abstract zzae.zza zzd(Context context);

    @Override // com.google.android.gms.internal.zzan
    public final void zza(MotionEvent motionEvent) {
        if (this.zzafk) {
            this.zzafi = 0L;
            this.zzafh = 0L;
            this.zzafg = 0L;
            this.zzaff = 0L;
            this.zzafj = 0L;
            Iterator<MotionEvent> it = this.zzafe.iterator();
            while (it.hasNext()) {
                it.next().recycle();
            }
            this.zzafe.clear();
            this.zzafd = null;
            this.zzafk = false;
        }
        switch (motionEvent.getAction()) {
            case 0:
                this.zzaff++;
                return;
            case 1:
                this.zzafd = MotionEvent.obtain(motionEvent);
                this.zzafe.add(this.zzafd);
                if (this.zzafe.size() > 6) {
                    this.zzafe.remove().recycle();
                }
                this.zzafh++;
                if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbt)).booleanValue()) {
                    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
                    int i = 0;
                    for (int length = stackTrace.length - 1; length >= 0; length--) {
                        i++;
                        if (!stackTrace[length].toString().startsWith("com.google.android.ads.") && !stackTrace[length].toString().startsWith("com.google.android.gms.")) {
                        }
                        this.zzafj = i;
                        return;
                    }
                    this.zzafj = i;
                    return;
                }
                return;
            case 2:
                this.zzafg += motionEvent.getHistorySize() + 1;
                return;
            case 3:
                this.zzafi++;
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String zza(Context context, String str, boolean z) {
        zzae.zza zzc;
        boolean z2;
        try {
            if (z) {
                zzae.zza zzd = zzd(context);
                this.zzafk = true;
                zzc = zzd;
            } else {
                zzc = zzc(context);
            }
            if (zzc == null || zzc.aM() == 0) {
                return Integer.toString(5);
            }
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbl)).booleanValue()) {
                z2 = ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbu)).booleanValue() && z;
            } else {
                z2 = true;
            }
            return zzak.zza(zzc, str, !z2);
        } catch (UnsupportedEncodingException e) {
            return Integer.toString(7);
        } catch (NoSuchAlgorithmException e2) {
            return Integer.toString(7);
        } catch (Throwable th) {
            return Integer.toString(3);
        }
    }
}
