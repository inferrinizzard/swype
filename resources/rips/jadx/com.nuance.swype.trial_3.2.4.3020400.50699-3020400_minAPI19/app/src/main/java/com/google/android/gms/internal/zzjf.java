package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.ads.mediation.AbstractAdViewAdapter;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.reward.client.RewardedVideoAdRequestParcel;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.internal.zzju;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzjf extends com.google.android.gms.ads.internal.zzb implements zzji {
    private static final zzgi zzchg = new zzgi();
    final Map<String, zzjm> zzchh;
    boolean zzchi;

    public zzjf(Context context, com.google.android.gms.ads.internal.zzd zzdVar, AdSizeParcel adSizeParcel, zzgj zzgjVar, VersionInfoParcel versionInfoParcel) {
        super(context, adSizeParcel, null, zzgjVar, versionInfoParcel, zzdVar);
        this.zzchh = new HashMap();
    }

    public final boolean isLoaded() {
        com.google.android.gms.common.internal.zzab.zzhi("isLoaded must be called on the main UI thread.");
        return this.zzajs.zzaoy == null && this.zzajs.zzaoz == null && this.zzajs.zzapb != null && !this.zzchi;
    }

    @Override // com.google.android.gms.internal.zzji
    public final void onRewardedVideoAdClosed() {
        zzdr();
    }

    @Override // com.google.android.gms.internal.zzji
    public final void onRewardedVideoAdLeftApplication() {
        zzds();
    }

    @Override // com.google.android.gms.internal.zzji
    public final void onRewardedVideoAdOpened() {
        zza(this.zzajs.zzapb, false);
        zzdt();
    }

    @Override // com.google.android.gms.internal.zzji
    public final void onRewardedVideoStarted() {
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzbon != null) {
            com.google.android.gms.ads.internal.zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb, this.zzajs.zzaou, false, this.zzajs.zzapb.zzbon.zzbnd);
        }
        zzdv();
    }

    public final void zza(RewardedVideoAdRequestParcel rewardedVideoAdRequestParcel) {
        com.google.android.gms.common.internal.zzab.zzhi("loadAd must be called on the main UI thread.");
        if (TextUtils.isEmpty(rewardedVideoAdRequestParcel.zzaou)) {
            zzkd.zzcx("Invalid ad unit id. Aborting.");
            return;
        }
        this.zzchi = false;
        this.zzajs.zzaou = rewardedVideoAdRequestParcel.zzaou;
        super.zzb(rewardedVideoAdRequestParcel.zzcar);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.ads.internal.zzb
    public final boolean zza(AdRequestParcel adRequestParcel, zzju zzjuVar, boolean z) {
        return false;
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza
    public final boolean zza(zzju zzjuVar, zzju zzjuVar2) {
        return true;
    }

    @Override // com.google.android.gms.internal.zzji
    public final void zzc(RewardItemParcel rewardItemParcel) {
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzbon != null) {
            com.google.android.gms.ads.internal.zzu.zzgf();
            zzgf.zza(this.zzajs.zzagf, this.zzajs.zzaow.zzcs, this.zzajs.zzapb, this.zzajs.zzaou, false, this.zzajs.zzapb.zzbon.zzbne);
        }
        if (this.zzajs.zzapb != null && this.zzajs.zzapb.zzcig != null && !TextUtils.isEmpty(this.zzajs.zzapb.zzcig.zzbnt)) {
            rewardItemParcel = new RewardItemParcel(this.zzajs.zzapb.zzcig.zzbnt, this.zzajs.zzapb.zzcig.zzbnu);
        }
        zza(rewardItemParcel);
    }

    public final zzjm zzcf(String str) {
        Exception exc;
        zzjm zzjmVar;
        zzjm zzjmVar2 = this.zzchh.get(str);
        if (zzjmVar2 != null) {
            return zzjmVar2;
        }
        try {
            zzjmVar = new zzjm(("com.google.ads.mediation.admob.AdMobAdapter".equals(str) ? zzchg : this.zzajz).zzbm(str), this);
            try {
                this.zzchh.put(str, zzjmVar);
                return zzjmVar;
            } catch (Exception e) {
                exc = e;
                String valueOf = String.valueOf(str);
                zzkd.zzd(valueOf.length() != 0 ? "Fail to instantiate adapter ".concat(valueOf) : new String("Fail to instantiate adapter "), exc);
                return zzjmVar;
            }
        } catch (Exception e2) {
            exc = e2;
            zzjmVar = zzjmVar2;
        }
    }

    @Override // com.google.android.gms.internal.zzji
    public final void zzrr() {
        onAdClicked();
    }

    @Override // com.google.android.gms.ads.internal.zza
    public final void zza(final zzju.zza zzaVar, zzdk zzdkVar) {
        if (zzaVar.errorCode != -2) {
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzjf.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzjf.this.zzb(new zzju(zzaVar));
                }
            });
            return;
        }
        this.zzajs.zzapc = zzaVar;
        if (zzaVar.zzcig == null) {
            this.zzajs.zzapc = zze(zzaVar);
        }
        this.zzajs.zzapw = 0;
        com.google.android.gms.ads.internal.zzv zzvVar = this.zzajs;
        com.google.android.gms.ads.internal.zzu.zzfp();
        zzjl zzjlVar = new zzjl(this.zzajs.zzagf, this.zzajs.zzapc, this);
        String valueOf = String.valueOf(zzjlVar.getClass().getName());
        zzkd.zzcv(valueOf.length() != 0 ? "AdRenderer: ".concat(valueOf) : new String("AdRenderer: "));
        zzjlVar.zzpy();
        zzvVar.zzaoz = zzjlVar;
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public final void pause() {
        com.google.android.gms.common.internal.zzab.zzhi("pause must be called on the main UI thread.");
        for (String str : this.zzchh.keySet()) {
            try {
                zzjm zzjmVar = this.zzchh.get(str);
                if (zzjmVar != null && zzjmVar.zzbog != null) {
                    zzjmVar.zzbog.pause();
                }
            } catch (RemoteException e) {
                String valueOf = String.valueOf(str);
                zzkd.zzcx(valueOf.length() != 0 ? "Fail to pause adapter: ".concat(valueOf) : new String("Fail to pause adapter: "));
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.zzb, com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public final void resume() {
        com.google.android.gms.common.internal.zzab.zzhi("resume must be called on the main UI thread.");
        for (String str : this.zzchh.keySet()) {
            try {
                zzjm zzjmVar = this.zzchh.get(str);
                if (zzjmVar != null && zzjmVar.zzbog != null) {
                    zzjmVar.zzbog.resume();
                }
            } catch (RemoteException e) {
                String valueOf = String.valueOf(str);
                zzkd.zzcx(valueOf.length() != 0 ? "Fail to resume adapter: ".concat(valueOf) : new String("Fail to resume adapter: "));
            }
        }
    }

    @Override // com.google.android.gms.ads.internal.zza, com.google.android.gms.ads.internal.client.zzu
    public final void destroy() {
        com.google.android.gms.common.internal.zzab.zzhi("destroy must be called on the main UI thread.");
        for (String str : this.zzchh.keySet()) {
            try {
                zzjm zzjmVar = this.zzchh.get(str);
                if (zzjmVar != null && zzjmVar.zzbog != null) {
                    zzjmVar.zzbog.destroy();
                }
            } catch (RemoteException e) {
                String valueOf = String.valueOf(str);
                zzkd.zzcx(valueOf.length() != 0 ? "Fail to destroy adapter: ".concat(valueOf) : new String("Fail to destroy adapter: "));
            }
        }
    }

    private static zzju.zza zze(zzju.zza zzaVar) {
        zzkd.v("Creating mediation ad response for non-mediated rewarded ad.");
        try {
            String jSONObject = zziq.zzc(zzaVar.zzciq).toString();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put(AbstractAdViewAdapter.AD_UNIT_ID_PARAMETER, zzaVar.zzcip.zzaou);
            return new zzju.zza(zzaVar.zzcip, zzaVar.zzciq, new zzga(Arrays.asList(new zzfz(jSONObject, Arrays.asList("com.google.ads.mediation.admob.AdMobAdapter"), Collections.emptyList(), Collections.emptyList(), jSONObject2.toString(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList())), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), ""), zzaVar.zzapa, zzaVar.errorCode, zzaVar.zzcik, zzaVar.zzcil, zzaVar.zzcie);
        } catch (JSONException e) {
            zzkd.zzb("Unable to generate ad state for non-mediated rewarded video.", e);
            return new zzju.zza(zzaVar.zzcip, zzaVar.zzciq, null, zzaVar.zzapa, 0, zzaVar.zzcik, zzaVar.zzcil, zzaVar.zzcie);
        }
    }
}
