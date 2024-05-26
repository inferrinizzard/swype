package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzli;

@zzin
/* loaded from: classes.dex */
public class zzid extends zzhy implements zzli.zza {
    /* JADX INFO: Access modifiers changed from: package-private */
    public zzid(Context context, zzju.zza zzaVar, zzlh zzlhVar, zzic.zza zzaVar2) {
        super(context, zzaVar, zzlhVar, zzaVar2);
    }

    protected void zzqd() {
    }

    @Override // com.google.android.gms.internal.zzhy
    protected final void zzpw() {
        if (this.zzbxs.errorCode != -2) {
            return;
        }
        this.zzbgf.zzuj().zzbya = this;
        zzqd();
        zzkd.zzcv("Loading HTML in WebView.");
        zzlh zzlhVar = this.zzbgf;
        com.google.android.gms.ads.internal.zzu.zzfq();
        zzlhVar.loadDataWithBaseURL(zzkh.zzco(this.zzbxs.zzbto), this.zzbxs.body, "text/html", "UTF-8", null);
    }
}
