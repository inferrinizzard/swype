package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzfs;
import com.google.android.gms.internal.zzla;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzcj extends zzcd {
    private zzfs.zzc zzarq;
    private boolean zzarr;

    static /* synthetic */ boolean zza$711cf703(zzcj zzcjVar) {
        zzcjVar.zzarr = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.internal.zzcd
    public final void destroy() {
        synchronized (this.zzail) {
            super.destroy();
            this.zzarq.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzcj.6
                @Override // com.google.android.gms.internal.zzla.zzc
                public final /* synthetic */ void zzd(zzft zzftVar) {
                    zzcj.this.zzd(zzftVar);
                }
            }, new zzla.zzb());
            this.zzarq.release();
        }
    }

    @Override // com.google.android.gms.internal.zzcd
    protected final void zzb(final JSONObject jSONObject) {
        this.zzarq.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzcj.5
            @Override // com.google.android.gms.internal.zzla.zzc
            public final /* synthetic */ void zzd(zzft zzftVar) {
                zzftVar.zza("AFMA_updateActiveView", jSONObject);
            }
        }, new zzla.zzb());
    }

    @Override // com.google.android.gms.internal.zzcd
    protected final boolean zzhe() {
        return this.zzarr;
    }

    public zzcj(Context context, AdSizeParcel adSizeParcel, zzju zzjuVar, VersionInfoParcel versionInfoParcel, zzck zzckVar, zzfs zzfsVar) {
        super(context, adSizeParcel, zzjuVar, versionInfoParcel, zzckVar);
        this.zzarq = zzfsVar.zzc(null);
        try {
            final JSONObject zzd = zzd(zzckVar.zzhj().zzhh());
            this.zzarq.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzcj.1
                @Override // com.google.android.gms.internal.zzla.zzc
                public final /* synthetic */ void zzd(zzft zzftVar) {
                    zzcj.this.zza(zzd);
                }
            }, new zzla.zza() { // from class: com.google.android.gms.internal.zzcj.2
                @Override // com.google.android.gms.internal.zzla.zza
                public final void run() {
                }
            });
        } catch (RuntimeException e) {
            zzkd.zzb("Failure while processing active view data.", e);
        } catch (JSONException e2) {
        }
        this.zzarq.zza(new zzla.zzc<zzft>() { // from class: com.google.android.gms.internal.zzcj.3
            @Override // com.google.android.gms.internal.zzla.zzc
            public final /* synthetic */ void zzd(zzft zzftVar) {
                zzcj.zza$711cf703(zzcj.this);
                zzcj.this.zzc(zzftVar);
                zzcj.this.zzgw();
                zzcj.this.zzk(3);
            }
        }, new zzla.zza() { // from class: com.google.android.gms.internal.zzcj.4
            @Override // com.google.android.gms.internal.zzla.zza
            public final void run() {
                zzcj.this.destroy();
            }
        });
        String valueOf = String.valueOf(this.zzaqk.zzari);
        zzkd.zzcv(valueOf.length() != 0 ? "Tracking ad unit: ".concat(valueOf) : new String("Tracking ad unit: "));
    }
}
