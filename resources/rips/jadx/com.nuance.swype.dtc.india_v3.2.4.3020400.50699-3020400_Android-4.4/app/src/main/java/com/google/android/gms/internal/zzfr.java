package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzfp;
import com.google.android.gms.internal.zzli;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzfr implements zzfp {
    final zzlh zzbgf;

    public zzfr(Context context, VersionInfoParcel versionInfoParcel, zzas zzasVar) {
        zzlh zza;
        com.google.android.gms.ads.internal.zzu.zzfr();
        zza = zzlj.zza(context, new AdSizeParcel(), false, false, zzasVar, versionInfoParcel, null, null, null);
        this.zzbgf = zza;
        this.zzbgf.getWebView().setWillNotDraw(true);
    }

    private static void runOnUiThread(Runnable runnable) {
        if (com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
            runnable.run();
        } else {
            zzkh.zzclc.post(runnable);
        }
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void destroy() {
        this.zzbgf.destroy();
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(String str, zzep zzepVar) {
        this.zzbgf.zzuj().zza(str, zzepVar);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zza(final String str, final JSONObject jSONObject) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfr.1
            @Override // java.lang.Runnable
            public final void run() {
                zzfr.this.zzbgf.zza(str, jSONObject);
            }
        });
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void zza$279dadbc$3b0b03b9(com.google.android.gms.ads.internal.client.zza zzaVar, com.google.android.gms.ads.internal.overlay.zzg zzgVar, zzel zzelVar, com.google.android.gms.ads.internal.overlay.zzp zzpVar) {
        this.zzbgf.zzuj().zza(zzaVar, zzgVar, zzelVar, zzpVar, false, null, null, new com.google.android.gms.ads.internal.zze(this.zzbgf.getContext(), false), null, null);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, zzep zzepVar) {
        this.zzbgf.zzuj().zzb(str, zzepVar);
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzb(String str, JSONObject jSONObject) {
        this.zzbgf.zzb(str, jSONObject);
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void zzbg(String str) {
        final String format = String.format("<!DOCTYPE html><html><head><script src=\"%s\"></script></head><body></body></html>", str);
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfr.3
            @Override // java.lang.Runnable
            public final void run() {
                zzfr.this.zzbgf.loadData(format, "text/html", "UTF-8");
            }
        });
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void zzbh(final String str) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfr.5
            @Override // java.lang.Runnable
            public final void run() {
                zzfr.this.zzbgf.loadUrl(str);
            }
        });
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void zzbi(final String str) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfr.4
            @Override // java.lang.Runnable
            public final void run() {
                zzfr.this.zzbgf.loadData(str, "text/html", "UTF-8");
            }
        });
    }

    @Override // com.google.android.gms.internal.zzft
    public final void zzj(final String str, final String str2) {
        runOnUiThread(new Runnable() { // from class: com.google.android.gms.internal.zzfr.2
            @Override // java.lang.Runnable
            public final void run() {
                zzfr.this.zzbgf.zzj(str, str2);
            }
        });
    }

    @Override // com.google.android.gms.internal.zzfp
    public final zzfu zzly() {
        return new zzfv(this);
    }

    @Override // com.google.android.gms.internal.zzfp
    public final void zza(final zzfp.zza zzaVar) {
        this.zzbgf.zzuj().zzbya = new zzli.zza() { // from class: com.google.android.gms.internal.zzfr.6
            @Override // com.google.android.gms.internal.zzli.zza
            public final void zza(zzlh zzlhVar, boolean z) {
                zzaVar.zzlz();
            }
        };
    }
}
