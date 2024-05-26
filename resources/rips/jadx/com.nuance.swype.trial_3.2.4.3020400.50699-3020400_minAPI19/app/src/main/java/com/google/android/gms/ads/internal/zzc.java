package com.google.android.gms.ads.internal;

import android.content.Context;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzcd;
import com.google.android.gms.internal.zzcg;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdk;
import com.google.android.gms.internal.zzdl;
import com.google.android.gms.internal.zzdo;
import com.google.android.gms.internal.zzep;
import com.google.android.gms.internal.zzft;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzhg;
import com.google.android.gms.internal.zzic;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzjo;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzlh;
import com.google.android.gms.internal.zzlj;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public abstract class zzc extends zzb implements zzh, zzhg {
    public zzc(Context context, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, zzd zzdVar) {
        super(context, adSizeParcel, str, zzgjVar, versionInfoParcel, zzdVar);
    }

    @Override // com.google.android.gms.internal.zzhg
    public void zza(int i, int i2, int i3, int i4) {
        zzdt();
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void zza(zzdo zzdoVar) {
        zzab.zzhi("setOnCustomRenderedAdLoadedListener must be called on the main UI thread.");
        this.zzajs.zzapq = zzdoVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zza(zzft zzftVar) {
        zzftVar.zza("/trackActiveViewUnit", new zzep() { // from class: com.google.android.gms.ads.internal.zzc.1
            @Override // com.google.android.gms.internal.zzep
            public final void zza(zzlh zzlhVar, Map<String, String> map) {
                if (zzc.this.zzajs.zzapb == null) {
                    zzkd.zzcx("Request to enable ActiveView before adState is available.");
                    return;
                }
                zzcg zzcgVar = zzc.this.zzaju;
                AdSizeParcel adSizeParcel = zzc.this.zzajs.zzapa;
                zzju zzjuVar = zzc.this.zzajs.zzapb;
                zzcgVar.zza(adSizeParcel, zzjuVar, new zzcd.zzd(zzlhVar.getView(), zzjuVar), zzlhVar);
            }
        });
    }

    @Override // com.google.android.gms.ads.internal.zzh
    public void zzc(View view) {
        this.zzajs.zzapv = view;
        zzb(new zzju(this.zzajs.zzapc));
    }

    @Override // com.google.android.gms.ads.internal.zzh
    public void zzeh() {
        onAdClicked();
    }

    @Override // com.google.android.gms.ads.internal.zzh
    public void zzei() {
        recordImpression();
        zzdp();
    }

    @Override // com.google.android.gms.internal.zzhg
    public void zzej() {
        zzdr();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public zzlh zza(zzju.zza zzaVar, zze zzeVar, zzjo zzjoVar) {
        zzlh zzlhVar = null;
        View nextView = this.zzajs.zzaox.getNextView();
        if (nextView instanceof zzlh) {
            zzlhVar = (zzlh) nextView;
            if (((Boolean) zzu.zzfz().zzd(zzdc.zzazz)).booleanValue()) {
                zzkd.zzcv("Reusing webview...");
                zzlhVar.zza(this.zzajs.zzagf, this.zzajs.zzapa, this.zzajn);
            } else {
                zzlhVar.destroy();
                zzlhVar = null;
            }
        }
        if (zzlhVar == null) {
            if (nextView != 0) {
                this.zzajs.zzaox.removeView(nextView);
            }
            zzu.zzfr();
            zzlhVar = zzlj.zza(this.zzajs.zzagf, this.zzajs.zzapa, false, false, this.zzajs.zzaov, this.zzajs.zzaow, this.zzajn, this, this.zzajv);
            if (this.zzajs.zzapa.zzaut == null) {
                zzb(zzlhVar.getView());
            }
        }
        zzlh zzlhVar2 = zzlhVar;
        zzlhVar2.zzuj().zza(this, this, this, this, false, this, null, zzeVar, this, zzjoVar);
        zza(zzlhVar2);
        zzlhVar2.zzcz(zzaVar.zzcip.zzcbg);
        return zzlhVar2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zza
    public void zza(final zzju.zza zzaVar, final zzdk zzdkVar) {
        if (zzaVar.errorCode != -2) {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzc.2
                @Override // java.lang.Runnable
                public final void run() {
                    zzc.this.zzb(new zzju(zzaVar));
                }
            });
            return;
        }
        if (zzaVar.zzapa != null) {
            this.zzajs.zzapa = zzaVar.zzapa;
        }
        if (!zzaVar.zzciq.zzcby || zzaVar.zzciq.zzauw) {
            ((Boolean) zzu.zzfz().zzd(zzdc.zzbdf)).booleanValue();
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.zzc.3
                final /* synthetic */ zzjo zzakf = null;

                @Override // java.lang.Runnable
                public final void run() {
                    if (zzaVar.zzciq.zzcch && zzc.this.zzajs.zzapq != null) {
                        String str = null;
                        if (zzaVar.zzciq.zzbto != null) {
                            zzu.zzfq();
                            str = zzkh.zzco(zzaVar.zzciq.zzbto);
                        }
                        zzdl zzdlVar = new zzdl(zzc.this, str, zzaVar.zzciq.body);
                        zzc.this.zzajs.zzapw = 1;
                        try {
                            zzc.this.zzajq = false;
                            zzc.this.zzajs.zzapq.zza(zzdlVar);
                            return;
                        } catch (RemoteException e) {
                            zzkd.zzd("Could not call the onCustomRenderedAdLoadedListener.", e);
                            zzc.this.zzajq = true;
                        }
                    }
                    final zze zzeVar = new zze(zzc.this.zzajs.zzagf, zzaVar);
                    zzlh zza = zzc.this.zza(zzaVar, zzeVar, this.zzakf);
                    zza.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.gms.ads.internal.zzc.3.1
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            zzeVar.recordClick();
                            return false;
                        }
                    });
                    zza.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.gms.ads.internal.zzc.3.2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            zzeVar.recordClick();
                        }
                    });
                    zzc.this.zzajs.zzapw = 0;
                    zzv zzvVar = zzc.this.zzajs;
                    zzu.zzfp();
                    zzvVar.zzaoz = zzic.zza(zzc.this.zzajs.zzagf, zzc.this, zzaVar, zzc.this.zzajs.zzaov, zza, zzc.this.zzajz, zzc.this, zzdkVar);
                }
            });
        } else {
            this.zzajs.zzapw = 0;
            zzv zzvVar = this.zzajs;
            zzu.zzfp();
            zzvVar.zzaoz = zzic.zza(this.zzajs.zzagf, this, zzaVar, this.zzajs.zzaov, null, this.zzajz, this, zzdkVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza
    public boolean zza(zzju zzjuVar, zzju zzjuVar2) {
        if (this.zzajs.zzgp() && this.zzajs.zzaox != null) {
            this.zzajs.zzaox.zzgv().zzcll = zzjuVar2.zzccd;
        }
        return super.zza(zzjuVar, zzjuVar2);
    }
}
