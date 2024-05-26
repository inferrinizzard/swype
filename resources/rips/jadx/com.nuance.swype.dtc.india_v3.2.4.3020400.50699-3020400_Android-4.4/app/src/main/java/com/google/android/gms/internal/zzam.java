package com.google.android.gms.internal;

import android.os.Looper;
import com.google.android.gms.clearcut.zzb;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzad;
import com.google.android.gms.playlog.internal.PlayLoggerContext;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: classes.dex */
public final class zzam {
    protected static volatile com.google.android.gms.clearcut.zzb zzaez = null;
    private static volatile Random zzafb = null;
    private static final Object zzafc = new Object();
    private zzax zzaey;
    protected boolean zzafa;

    public static int zzat() {
        try {
            return ThreadLocalRandom.current().nextInt();
        } catch (NoClassDefFoundError e) {
            return zzau().nextInt();
        } catch (RuntimeException e2) {
            return zzau().nextInt();
        }
    }

    private static Random zzau() {
        if (zzafb == null) {
            synchronized (zzafc) {
                if (zzafb == null) {
                    zzafb = new Random();
                }
            }
        }
        return zzafb;
    }

    public zzam(zzax zzaxVar) {
        this.zzafa = false;
        this.zzaey = zzaxVar;
        zzdc.initialize(zzaxVar.getContext());
        this.zzafa = ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbi)).booleanValue();
        if (this.zzafa && zzaez == null) {
            synchronized (zzafc) {
                if (zzaez == null) {
                    zzaez = new com.google.android.gms.clearcut.zzb(zzaxVar.getContext(), "ADSHIELD");
                }
            }
        }
    }

    public final void zza(int i, int i2, long j) throws IOException {
        zzb.InterfaceC0045zzb interfaceC0045zzb;
        com.google.android.gms.clearcut.zzc zzcVar;
        try {
            if (this.zzafa && zzaez != null && this.zzaey.zzcj()) {
                zzad.zza zzaVar = new zzad.zza();
                zzaVar.zzck = this.zzaey.getContext().getPackageName();
                zzaVar.zzcl = Long.valueOf(j);
                zzb.zza zzaVar2 = new zzb.zza(zzaez, zzapv.zzf(zzaVar), (char) 0);
                zzaVar2.qr.zzahl = i2;
                zzaVar2.qr.bkd = i;
                GoogleApiClient googleApiClient = this.zzaey.zzagp;
                if (zzaVar2.qs) {
                    throw new IllegalStateException("do not reuse LogEventBuilder");
                }
                zzaVar2.qs = true;
                PlayLoggerContext playLoggerContext = zzaVar2.zzana().qu;
                interfaceC0045zzb = com.google.android.gms.clearcut.zzb.this.qk;
                if (interfaceC0045zzb.zzg(playLoggerContext.arv, playLoggerContext.arr)) {
                    zzcVar = com.google.android.gms.clearcut.zzb.this.qh;
                    zzcVar.zza(googleApiClient, zzaVar2.zzana());
                } else {
                    Status status = Status.sq;
                    com.google.android.gms.common.internal.zzab.zzb(status, "Result must not be null");
                    new zzqu(Looper.getMainLooper()).zzc((zzqu) status);
                }
            }
        } catch (Exception e) {
        }
    }
}
