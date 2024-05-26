package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzla;
import java.util.concurrent.TimeUnit;

@zzin
/* loaded from: classes.dex */
public final class zzih {
    final Context mContext;
    final com.google.android.gms.ads.internal.zzq zzbfx;
    final zzas zzbgd;
    final zzju.zza zzbxr;
    zzfq zzbyw;
    zzfs.zze zzbyx;
    zzfp zzbyy;
    boolean zzbyz;
    static final long zzbyt = TimeUnit.SECONDS.toMillis(60);
    static final Object zzamr = new Object();
    static boolean zzbyu = false;
    static zzfs zzbyv = null;

    /* loaded from: classes.dex */
    public static abstract class zza {
        public abstract void zze(zzft zzftVar);

        public void zzqq() {
        }
    }

    public zzih(Context context, zzju.zza zzaVar, com.google.android.gms.ads.internal.zzq zzqVar, zzas zzasVar) {
        this.zzbyz = false;
        this.mContext = context;
        this.zzbxr = zzaVar;
        this.zzbfx = zzqVar;
        this.zzbgd = zzasVar;
        this.zzbyz = ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbcf)).booleanValue();
    }

    public final void zza(final zza zzaVar) {
        if (this.zzbyz) {
            zzfs.zze zzeVar = this.zzbyx;
            if (zzeVar == null) {
                zzkd.zzcx("SharedJavascriptEngine not initialized");
                return;
            } else {
                zzeVar.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzih.1
                    @Override // com.google.android.gms.internal.zzla.zzc
                    public final /* synthetic */ void zzd(zzft zzftVar) {
                        zzaVar.zze(zzftVar);
                    }
                }, new zzla.zza() { // from class: com.google.android.gms.internal.zzih.2
                    @Override // com.google.android.gms.internal.zzla.zza
                    public final void run() {
                        zzaVar.zzqq();
                    }
                });
                return;
            }
        }
        zzfp zzfpVar = this.zzbyy;
        if (zzfpVar == null) {
            zzkd.zzcx("JavascriptEngine not initialized");
        } else {
            zzaVar.zze(zzfpVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String zzd(zzju.zza zzaVar) {
        String str = (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbac);
        String valueOf = String.valueOf(zzaVar.zzciq.zzbto.indexOf("https") == 0 ? "https:" : "http:");
        String valueOf2 = String.valueOf(str);
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }
}
