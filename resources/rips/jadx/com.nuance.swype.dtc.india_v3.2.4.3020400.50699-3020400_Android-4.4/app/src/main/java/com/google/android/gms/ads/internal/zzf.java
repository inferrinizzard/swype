package com.google.android.gms.ads.internal;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzjo;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzlh;

@zzin
/* loaded from: classes.dex */
public class zzf extends zzc implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {
    private boolean zzakp;

    /* loaded from: classes.dex */
    public class zza {
        public zza() {
        }

        public void onClick() {
            zzf.this.onAdClicked();
        }
    }

    public zzf(Context context, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, VersionInfoParcel versionInfoParcel, zzd zzdVar) {
        super(context, adSizeParcel, str, zzgjVar, versionInfoParcel, zzdVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean zzb(zzju zzjuVar, zzju zzjuVar2) {
        if (zzjuVar2.zzcby) {
            View zzf = zzn.zzf(zzjuVar2);
            if (zzf == null) {
                zzkd.zzcx("Could not get mediation view");
                return false;
            }
            View nextView = this.zzajs.zzaox.getNextView();
            if (nextView != 0) {
                if (nextView instanceof zzlh) {
                    ((zzlh) nextView).destroy();
                }
                this.zzajs.zzaox.removeView(nextView);
            }
            if (!zzn.zzg(zzjuVar2)) {
                try {
                    zzb(zzf);
                } catch (Throwable th) {
                    zzkd.zzd("Could not add mediation view to view hierarchy.", th);
                    return false;
                }
            }
        } else if (zzjuVar2.zzcii != null && zzjuVar2.zzbtm != null) {
            zzjuVar2.zzbtm.zza(zzjuVar2.zzcii);
            this.zzajs.zzaox.removeAllViews();
            this.zzajs.zzaox.setMinimumWidth(zzjuVar2.zzcii.widthPixels);
            this.zzajs.zzaox.setMinimumHeight(zzjuVar2.zzcii.heightPixels);
            zzb(zzjuVar2.zzbtm.getView());
        }
        if (this.zzajs.zzaox.getChildCount() > 1) {
            this.zzajs.zzaox.showNext();
        }
        if (zzjuVar != null) {
            View nextView2 = this.zzajs.zzaox.getNextView();
            if (nextView2 instanceof zzlh) {
                ((zzlh) nextView2).zza(this.zzajs.zzagf, this.zzajs.zzapa, this.zzajn);
            } else if (nextView2 != 0) {
                this.zzajs.zzaox.removeView(nextView2);
            }
            this.zzajs.zzgo();
        }
        this.zzajs.zzaox.setVisibility(0);
        return true;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        zze(this.zzajs.zzapb);
    }

    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
    public void onScrollChanged() {
        zze(this.zzajs.zzapb);
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public void setManualImpressionsEnabled(boolean z) {
        zzab.zzhi("setManualImpressionsEnabled must be called from the main thread.");
        this.zzakp = z;
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.client.zzu
    public void showInterstitial() {
        throw new IllegalStateException("Interstitial is NOT supported by BannerAdManager.");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzb
    public final void zza(zzju zzjuVar, boolean z) {
        super.zza(zzjuVar, z);
        if (zzn.zzg(zzjuVar)) {
            zzn.zza(zzjuVar, new zza());
        }
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public com.google.android.gms.ads.internal.client.zzab zzdq() {
        zzab.zzhi("getVideoController must be called from the main thread.");
        if (this.zzajs.zzapb == null || this.zzajs.zzapb.zzbtm == null) {
            return null;
        }
        return this.zzajs.zzapb.zzbtm.zzut();
    }

    @Override // com.google.android.gms.ads.internal.zzb
    protected final boolean zzdw() {
        boolean z = true;
        zzu.zzfq();
        if (!zzkh.zza(this.zzajs.zzagf.getPackageManager(), this.zzajs.zzagf.getPackageName(), "android.permission.INTERNET")) {
            com.google.android.gms.ads.internal.client.zzm.zziw().zza(this.zzajs.zzaox, this.zzajs.zzapa, "Missing internet permission in AndroidManifest.xml.", "Missing internet permission in AndroidManifest.xml. You must have the following declaration: <uses-permission android:name=\"android.permission.INTERNET\" />");
            z = false;
        }
        zzu.zzfq();
        if (!zzkh.zzac(this.zzajs.zzagf)) {
            com.google.android.gms.ads.internal.client.zzm.zziw().zza(this.zzajs.zzaox, this.zzajs.zzapa, "Missing AdActivity with android:configChanges in AndroidManifest.xml.", "Missing AdActivity with android:configChanges in AndroidManifest.xml. You must have the following declaration within the <application> element: <activity android:name=\"com.google.android.gms.ads.AdActivity\" android:configChanges=\"keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize\" />");
            z = false;
        }
        if (!z && this.zzajs.zzaox != null) {
            this.zzajs.zzaox.setVisibility(0);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzc
    public final zzlh zza(zzju.zza zzaVar, zze zzeVar, zzjo zzjoVar) {
        AdSize zzij;
        AdSizeParcel adSizeParcel;
        if (this.zzajs.zzapa.zzaut == null && this.zzajs.zzapa.zzauv) {
            zzv zzvVar = this.zzajs;
            if (zzaVar.zzciq.zzauv) {
                adSizeParcel = this.zzajs.zzapa;
            } else {
                String str = zzaVar.zzciq.zzccb;
                if (str != null) {
                    String[] split = str.split("[xX]");
                    split[0] = split[0].trim();
                    split[1] = split[1].trim();
                    zzij = new AdSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                } else {
                    zzij = this.zzajs.zzapa.zzij();
                }
                adSizeParcel = new AdSizeParcel(this.zzajs.zzagf, zzij);
            }
            zzvVar.zzapa = adSizeParcel;
        }
        return super.zza(zzaVar, zzeVar, zzjoVar);
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public boolean zzb(AdRequestParcel adRequestParcel) {
        if (adRequestParcel.zzatr != this.zzakp) {
            adRequestParcel = new AdRequestParcel(adRequestParcel.versionCode, adRequestParcel.zzatm, adRequestParcel.extras, adRequestParcel.zzatn, adRequestParcel.zzato, adRequestParcel.zzatp, adRequestParcel.zzatq, adRequestParcel.zzatr || this.zzakp, adRequestParcel.zzats, adRequestParcel.zzatt, adRequestParcel.zzatu, adRequestParcel.zzatv, adRequestParcel.zzatw, adRequestParcel.zzatx, adRequestParcel.zzaty, adRequestParcel.zzatz, adRequestParcel.zzaua, adRequestParcel.zzaub);
        }
        return super.zzb(adRequestParcel);
    }

    /* JADX WARN: Code restructure failed: missing block: B:65:0x00da, code lost:            if (((java.lang.Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(com.google.android.gms.internal.zzdc.zzbce)).booleanValue() != false) goto L52;     */
    @Override // com.google.android.gms.ads.internal.zzc, com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean zza(com.google.android.gms.internal.zzju r5, final com.google.android.gms.internal.zzju r6) {
        /*
            Method dump skipped, instructions count: 273
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.internal.zzf.zza(com.google.android.gms.internal.zzju, com.google.android.gms.internal.zzju):boolean");
    }

    final void zze(zzju zzjuVar) {
        if (zzjuVar == null || zzjuVar.zzcif || this.zzajs.zzaox == null) {
            return;
        }
        zzu.zzfq();
        if (zzkh.zza(this.zzajs.zzaox, this.zzajs.zzagf) && this.zzajs.zzaox.getGlobalVisibleRect(new Rect(), null)) {
            if (zzjuVar != null && zzjuVar.zzbtm != null && zzjuVar.zzbtm.zzuj() != null) {
                zzjuVar.zzbtm.zzuj().zzcos = null;
            }
            zza(zzjuVar, false);
            zzjuVar.zzcif = true;
        }
    }
}
