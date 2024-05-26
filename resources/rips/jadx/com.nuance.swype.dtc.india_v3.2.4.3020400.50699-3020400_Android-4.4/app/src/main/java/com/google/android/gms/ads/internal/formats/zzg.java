package com.google.android.gms.ads.internal.formats;

import android.content.Context;
import android.os.RemoteException;
import android.view.View;
import com.google.android.gms.ads.internal.zzq;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzas;
import com.google.android.gms.internal.zzgn;
import com.google.android.gms.internal.zzgo;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzlh;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzg extends zzi {
    private Object zzail;
    private zzgn zzbfv;
    private zzgo zzbfw;
    private final zzq zzbfx;
    private zzh zzbfy;
    private boolean zzbfz;

    private zzg(Context context, zzq zzqVar, zzas zzasVar) {
        super(context, zzqVar, null, zzasVar, null, null, null, null);
        this.zzbfz = false;
        this.zzail = new Object();
        this.zzbfx = zzqVar;
    }

    public zzg(Context context, zzq zzqVar, zzas zzasVar, zzgn zzgnVar) {
        this(context, zzqVar, zzasVar);
        this.zzbfv = zzgnVar;
    }

    public zzg(Context context, zzq zzqVar, zzas zzasVar, zzgo zzgoVar) {
        this(context, zzqVar, zzasVar);
        this.zzbfw = zzgoVar;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi
    public zzb zza(View.OnClickListener onClickListener) {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi
    public void zza(View view, Map<String, WeakReference<View>> map, View.OnTouchListener onTouchListener, View.OnClickListener onClickListener) {
        synchronized (this.zzail) {
            this.zzbfz = true;
            try {
                if (this.zzbfv != null) {
                    this.zzbfv.zzl(com.google.android.gms.dynamic.zze.zzac(view));
                } else if (this.zzbfw != null) {
                    this.zzbfw.zzl(com.google.android.gms.dynamic.zze.zzac(view));
                }
            } catch (RemoteException e) {
                zzkd.zzd("Failed to call prepareAd", e);
            }
            this.zzbfz = false;
        }
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi, com.google.android.gms.ads.internal.formats.zzh
    public void zza(View view, Map<String, WeakReference<View>> map, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3) {
        zzab.zzhi("performClick must be called on the main UI thread.");
        synchronized (this.zzail) {
            if (this.zzbfy != null) {
                this.zzbfy.zza(view, map, jSONObject, jSONObject2, jSONObject3);
                this.zzbfx.onAdClicked();
            } else {
                try {
                    if (this.zzbfv != null && !this.zzbfv.getOverrideClickHandling()) {
                        this.zzbfv.zzk(com.google.android.gms.dynamic.zze.zzac(view));
                        this.zzbfx.onAdClicked();
                    }
                    if (this.zzbfw != null && !this.zzbfw.getOverrideClickHandling()) {
                        this.zzbfw.zzk(com.google.android.gms.dynamic.zze.zzac(view));
                        this.zzbfx.onAdClicked();
                    }
                } catch (RemoteException e) {
                    zzkd.zzd("Failed to call performClick", e);
                }
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi, com.google.android.gms.ads.internal.formats.zzh
    public void zzb(View view, Map<String, WeakReference<View>> map) {
        synchronized (this.zzail) {
            try {
                if (this.zzbfv != null) {
                    this.zzbfv.zzm(com.google.android.gms.dynamic.zze.zzac(view));
                } else if (this.zzbfw != null) {
                    this.zzbfw.zzm(com.google.android.gms.dynamic.zze.zzac(view));
                }
            } catch (RemoteException e) {
                zzkd.zzd("Failed to call untrackView", e);
            }
        }
    }

    public void zzc(zzh zzhVar) {
        synchronized (this.zzail) {
            this.zzbfy = zzhVar;
        }
    }

    public boolean zzkz() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzbfz;
        }
        return z;
    }

    public zzh zzla() {
        zzh zzhVar;
        synchronized (this.zzail) {
            zzhVar = this.zzbfy;
        }
        return zzhVar;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi
    public zzlh zzlb() {
        return null;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzi, com.google.android.gms.ads.internal.formats.zzh
    public void recordImpression() {
        zzab.zzhi("recordImpression must be called on the main UI thread.");
        synchronized (this.zzail) {
            this.zzbge = true;
            if (this.zzbfy != null) {
                this.zzbfy.recordImpression();
                this.zzbfx.recordImpression();
            } else {
                try {
                    if (this.zzbfv != null && !this.zzbfv.getOverrideImpressionRecording()) {
                        this.zzbfv.recordImpression();
                        this.zzbfx.recordImpression();
                    } else if (this.zzbfw != null && !this.zzbfw.getOverrideImpressionRecording()) {
                        this.zzbfw.recordImpression();
                        this.zzbfx.recordImpression();
                    }
                } catch (RemoteException e) {
                    zzkd.zzd("Failed to call recordImpression", e);
                }
            }
        }
    }
}
