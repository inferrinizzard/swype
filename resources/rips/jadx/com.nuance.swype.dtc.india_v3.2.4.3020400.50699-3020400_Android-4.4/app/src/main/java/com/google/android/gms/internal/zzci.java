package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzci extends zzcd {
    private final zzft zzarp;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzcd
    public final void destroy() {
        synchronized (this.zzail) {
            super.destroy();
            zzd(this.zzarp);
        }
    }

    @Override // com.google.android.gms.internal.zzcd
    protected final void zzb(JSONObject jSONObject) {
        this.zzarp.zza("AFMA_updateActiveView", jSONObject);
    }

    @Override // com.google.android.gms.internal.zzcd
    public final void zzgy() {
        destroy();
    }

    @Override // com.google.android.gms.internal.zzcd
    protected final boolean zzhe() {
        return true;
    }

    public zzci(Context context, AdSizeParcel adSizeParcel, zzju zzjuVar, VersionInfoParcel versionInfoParcel, zzck zzckVar, zzft zzftVar) {
        super(context, adSizeParcel, zzjuVar, versionInfoParcel, zzckVar);
        this.zzarp = zzftVar;
        zzc(this.zzarp);
        zzgw();
        zzk(3);
        String valueOf = String.valueOf(this.zzaqk.zzari);
        zzkd.zzcv(valueOf.length() != 0 ? "Tracking ad unit: ".concat(valueOf) : new String("Tracking ad unit: "));
    }
}
