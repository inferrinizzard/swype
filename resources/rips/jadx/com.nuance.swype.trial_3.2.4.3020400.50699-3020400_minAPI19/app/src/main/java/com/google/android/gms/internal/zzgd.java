package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.ads.mediation.AdUrlAdapter;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.internal.zzge;
import com.google.android.gms.internal.zzgm;
import com.nuance.swype.input.IME;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzgd implements zzge.zza {
    final Context mContext;
    private final zzgj zzajz;
    final NativeAdOptionsParcel zzalk;
    final List<String> zzall;
    final VersionInfoParcel zzalo;
    AdRequestParcel zzanc;
    final AdSizeParcel zzani;
    final boolean zzarl;
    final boolean zzawn;
    final String zzboc;
    private final long zzbod;
    private final zzga zzboe;
    final zzfz zzbof;
    zzgk zzbog;
    private zzgm zzboi;
    final Object zzail = new Object();
    int zzboh = -2;

    public zzgd(Context context, String str, zzgj zzgjVar, zzga zzgaVar, zzfz zzfzVar, AdRequestParcel adRequestParcel, AdSizeParcel adSizeParcel, VersionInfoParcel versionInfoParcel, boolean z, boolean z2, NativeAdOptionsParcel nativeAdOptionsParcel, List<String> list) {
        this.mContext = context;
        this.zzajz = zzgjVar;
        this.zzbof = zzfzVar;
        if ("com.google.ads.mediation.customevent.CustomEventAdapter".equals(str)) {
            this.zzboc = zzmh();
        } else {
            this.zzboc = str;
        }
        this.zzboe = zzgaVar;
        this.zzbod = zzgaVar.zzbnl != -1 ? zzgaVar.zzbnl : IME.RETRY_DELAY_IN_MILLIS;
        this.zzanc = adRequestParcel;
        this.zzani = adSizeParcel;
        this.zzalo = versionInfoParcel;
        this.zzarl = z;
        this.zzawn = z2;
        this.zzalk = nativeAdOptionsParcel;
        this.zzall = list;
    }

    private static zzgk zza(MediationAdapter mediationAdapter) {
        return new zzgq(mediationAdapter);
    }

    private String zzmh() {
        try {
            if (!TextUtils.isEmpty(this.zzbof.zzbmy)) {
                return this.zzajz.zzbn(this.zzbof.zzbmy) ? "com.google.android.gms.ads.mediation.customevent.CustomEventAdapter" : "com.google.ads.mediation.customevent.CustomEventAdapter";
            }
        } catch (RemoteException e) {
            zzkd.zzcx("Fail to determine the custom event's version, assuming the old one.");
        }
        return "com.google.ads.mediation.customevent.CustomEventAdapter";
    }

    private int zzml() {
        if (this.zzbof.zzbnc == null) {
            return 0;
        }
        try {
            JSONObject jSONObject = new JSONObject(this.zzbof.zzbnc);
            if ("com.google.ads.mediation.admob.AdMobAdapter".equals(this.zzboc)) {
                return jSONObject.optInt("cpm_cents", 0);
            }
            int optInt = zzz(2) ? jSONObject.optInt("cpm_floor_cents", 0) : 0;
            return optInt == 0 ? jSONObject.optInt("penalized_average_cpm_cents", 0) : optInt;
        } catch (JSONException e) {
            zzkd.zzcx("Could not convert to json. Returning 0");
            return 0;
        }
    }

    public final void cancel() {
        synchronized (this.zzail) {
            try {
                if (this.zzbog != null) {
                    this.zzbog.destroy();
                }
            } catch (RemoteException e) {
                zzkd.zzd("Could not destroy mediation adapter.", e);
            }
            this.zzboh = -1;
            this.zzail.notify();
        }
    }

    @Override // com.google.android.gms.internal.zzge.zza
    public final void zza$37cb6271(zzgm zzgmVar) {
        synchronized (this.zzail) {
            this.zzboh = 0;
            this.zzboi = zzgmVar;
            this.zzail.notify();
        }
    }

    final String zzbj(String str) {
        if (str == null || !zzmk() || zzz(2)) {
            return str;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            jSONObject.remove("cpm_floor_cents");
            return jSONObject.toString();
        } catch (JSONException e) {
            zzkd.zzcx("Could not remove field. Returning the original value");
            return str;
        }
    }

    final boolean zzmk() {
        return this.zzboe.zzbnv != -1;
    }

    @Override // com.google.android.gms.internal.zzge.zza
    public final void zzy(int i) {
        synchronized (this.zzail) {
            this.zzboh = i;
            this.zzail.notify();
        }
    }

    final boolean zzz(int i) {
        try {
            Bundle zzmr = this.zzarl ? this.zzbog.zzmr() : this.zzani.zzaus ? this.zzbog.getInterstitialAdapterInfo() : this.zzbog.zzmq();
            return zzmr != null && (zzmr.getInt("capabilities", 0) & i) == i;
        } catch (RemoteException e) {
            zzkd.zzcx("Could not get adapter info. Returning false");
            return false;
        }
    }

    public final zzge zza(long j, long j2) {
        zzge zzgeVar;
        synchronized (this.zzail) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            final zzgc zzgcVar = new zzgc();
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzgd.1
                @Override // java.lang.Runnable
                public final void run() {
                    synchronized (zzgd.this.zzail) {
                        if (zzgd.this.zzboh != -2) {
                            return;
                        }
                        zzgd.this.zzbog = zzgd.this.zzmj();
                        if (zzgd.this.zzbog == null) {
                            zzgd.this.zzy(4);
                            return;
                        }
                        if (zzgd.this.zzmk() && !zzgd.this.zzz(1)) {
                            String str = zzgd.this.zzboc;
                            zzkd.zzcx(new StringBuilder(String.valueOf(str).length() + 56).append("Ignoring adapter ").append(str).append(" as delayed impression is not supported").toString());
                            zzgd.this.zzy(2);
                            return;
                        }
                        zzgc zzgcVar2 = zzgcVar;
                        zzgd zzgdVar = zzgd.this;
                        synchronized (zzgcVar2.zzail) {
                            zzgcVar2.zzboa = zzgdVar;
                        }
                        zzgd zzgdVar2 = zzgd.this;
                        zzgc zzgcVar3 = zzgcVar;
                        if ("com.google.ads.mediation.AdUrlAdapter".equals(zzgdVar2.zzboc)) {
                            if (zzgdVar2.zzanc.zzatw == null) {
                                zzgdVar2.zzanc = new com.google.android.gms.ads.internal.client.zzf(zzgdVar2.zzanc).zzc(new Bundle()).zzig();
                            }
                            Bundle bundle = zzgdVar2.zzanc.zzatw.getBundle(zzgdVar2.zzboc);
                            if (bundle == null) {
                                bundle = new Bundle();
                            }
                            bundle.putString("sdk_less_network_id", zzgdVar2.zzbof.zzbmv);
                            zzgdVar2.zzanc.zzatw.putBundle(zzgdVar2.zzboc, bundle);
                        }
                        String zzbj = zzgdVar2.zzbj(zzgdVar2.zzbof.zzbnc);
                        try {
                            if (zzgdVar2.zzalo.zzcnl < 4100000) {
                                if (zzgdVar2.zzani.zzaus) {
                                    zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzanc, zzbj, zzgcVar3);
                                } else {
                                    zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzani, zzgdVar2.zzanc, zzbj, zzgcVar3);
                                }
                            } else if (zzgdVar2.zzarl) {
                                zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzanc, zzbj, zzgdVar2.zzbof.zzbmu, zzgcVar3, zzgdVar2.zzalk, zzgdVar2.zzall);
                            } else if (zzgdVar2.zzani.zzaus) {
                                zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzanc, zzbj, zzgdVar2.zzbof.zzbmu, zzgcVar3);
                            } else if (!zzgdVar2.zzawn) {
                                zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzani, zzgdVar2.zzanc, zzbj, zzgdVar2.zzbof.zzbmu, zzgcVar3);
                            } else if (zzgdVar2.zzbof.zzbnf != null) {
                                zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzanc, zzbj, zzgdVar2.zzbof.zzbmu, zzgcVar3, new NativeAdOptionsParcel(zzgd.zzbk(zzgdVar2.zzbof.zzbnj)), zzgdVar2.zzbof.zzbni);
                            } else {
                                zzgdVar2.zzbog.zza(com.google.android.gms.dynamic.zze.zzac(zzgdVar2.mContext), zzgdVar2.zzani, zzgdVar2.zzanc, zzbj, zzgdVar2.zzbof.zzbmu, zzgcVar3);
                            }
                        } catch (RemoteException e) {
                            zzkd.zzd("Could not request ad from mediation adapter.", e);
                            zzgdVar2.zzy(5);
                        }
                    }
                }
            });
            long j3 = this.zzbod;
            while (this.zzboh == -2) {
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                long j4 = j3 - (elapsedRealtime2 - elapsedRealtime);
                long j5 = j2 - (elapsedRealtime2 - j);
                if (j4 <= 0 || j5 <= 0) {
                    zzkd.zzcw("Timed out waiting for adapter.");
                    this.zzboh = 3;
                } else {
                    try {
                        this.zzail.wait(Math.min(j4, j5));
                    } catch (InterruptedException e) {
                        this.zzboh = -1;
                    }
                }
            }
            zzgeVar = new zzge(this.zzbof, this.zzbog, this.zzboc, zzgcVar, this.zzboh, zzmi(), com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime() - elapsedRealtime);
        }
        return zzgeVar;
    }

    private zzgm zzmi() {
        if (this.zzboh != 0 || !zzmk()) {
            return null;
        }
        try {
            if (zzz(4) && this.zzboi != null && this.zzboi.zzmm() != 0) {
                return this.zzboi;
            }
        } catch (RemoteException e) {
            zzkd.zzcx("Could not get cpm value from MediationResponseMetadata");
        }
        final int zzml = zzml();
        return new zzgm.zza() { // from class: com.google.android.gms.internal.zzgd.2
            @Override // com.google.android.gms.internal.zzgm
            public final int zzmm() throws RemoteException {
                return zzml;
            }
        };
    }

    final zzgk zzmj() {
        String valueOf = String.valueOf(this.zzboc);
        zzkd.zzcw(valueOf.length() != 0 ? "Instantiating mediation adapter: ".concat(valueOf) : new String("Instantiating mediation adapter: "));
        if (!this.zzarl) {
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbe)).booleanValue() && "com.google.ads.mediation.admob.AdMobAdapter".equals(this.zzboc)) {
                return zza(new AdMobAdapter());
            }
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbf)).booleanValue() && "com.google.ads.mediation.AdUrlAdapter".equals(this.zzboc)) {
                return zza(new AdUrlAdapter());
            }
            if ("com.google.ads.mediation.admob.AdMobCustomTabsAdapter".equals(this.zzboc)) {
                return new zzgq(new zzgy());
            }
        }
        try {
            return this.zzajz.zzbm(this.zzboc);
        } catch (RemoteException e) {
            String valueOf2 = String.valueOf(this.zzboc);
            zzkd.zza(valueOf2.length() != 0 ? "Could not instantiate mediation adapter: ".concat(valueOf2) : new String("Could not instantiate mediation adapter: "), e);
            return null;
        }
    }

    static NativeAdOptions zzbk(String str) {
        int i = 0;
        NativeAdOptions.Builder builder = new NativeAdOptions.Builder();
        if (str == null) {
            return builder.build();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            builder.setRequestMultipleImages(jSONObject.optBoolean("multiple_images", false));
            builder.setReturnUrlsForImageAssets(jSONObject.optBoolean("only_urls", false));
            String optString = jSONObject.optString("native_image_orientation", "any");
            if ("landscape".equals(optString)) {
                i = 2;
            } else if ("portrait".equals(optString)) {
                i = 1;
            }
            builder.setImageOrientation(i);
        } catch (JSONException e) {
            zzkd.zzd("Exception occurred when creating native ad options", e);
        }
        return builder.build();
    }
}
