package com.google.android.gms.internal;

import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.ads.internal.request.AdRequestInfoParcel;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.ads.internal.request.AutoClickProtectionConfigurationParcel;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzju {
    public final int errorCode;
    public final int orientation;
    public final List<String> zzbnm;
    public final List<String> zzbnn;
    public final List<String> zzbnp;
    public final long zzbns;
    public final zzfz zzbon;
    public final zzgk zzboo;
    public final String zzbop;
    public final zzgc zzboq;
    public final zzlh zzbtm;
    public final AdRequestParcel zzcar;
    public final String zzcau;
    public final long zzcbx;
    public final boolean zzcby;
    public final long zzcbz;
    public final List<String> zzcca;
    public final String zzccd;
    public final RewardItemParcel zzccn;
    public final List<String> zzccp;
    public final boolean zzccq;
    public final AutoClickProtectionConfigurationParcel zzccr;
    public final JSONObject zzcie;
    public boolean zzcif;
    public final zzga zzcig;
    public final String zzcih;
    public final AdSizeParcel zzcii;
    public final List<String> zzcij;
    public final long zzcik;
    public final long zzcil;
    public final zzh.zza zzcim;
    public boolean zzcin;
    public boolean zzcio;

    @zzin
    /* loaded from: classes.dex */
    public static final class zza {
        public final int errorCode;
        public final AdSizeParcel zzapa;
        public final JSONObject zzcie;
        public final zzga zzcig;
        public final long zzcik;
        public final long zzcil;
        public final AdRequestInfoParcel zzcip;
        public final AdResponseParcel zzciq;

        public zza(AdRequestInfoParcel adRequestInfoParcel, AdResponseParcel adResponseParcel, zzga zzgaVar, AdSizeParcel adSizeParcel, int i, long j, long j2, JSONObject jSONObject) {
            this.zzcip = adRequestInfoParcel;
            this.zzciq = adResponseParcel;
            this.zzcig = zzgaVar;
            this.zzapa = adSizeParcel;
            this.errorCode = i;
            this.zzcik = j;
            this.zzcil = j2;
            this.zzcie = jSONObject;
        }
    }

    public zzju(AdRequestParcel adRequestParcel, zzlh zzlhVar, List<String> list, int i, List<String> list2, List<String> list3, int i2, long j, String str, boolean z, zzfz zzfzVar, zzgk zzgkVar, String str2, zzga zzgaVar, zzgc zzgcVar, long j2, AdSizeParcel adSizeParcel, long j3, long j4, long j5, String str3, JSONObject jSONObject, zzh.zza zzaVar, RewardItemParcel rewardItemParcel, List<String> list4, List<String> list5, boolean z2, AutoClickProtectionConfigurationParcel autoClickProtectionConfigurationParcel, String str4, List<String> list6) {
        this.zzcin = false;
        this.zzcio = false;
        this.zzcar = adRequestParcel;
        this.zzbtm = zzlhVar;
        this.zzbnm = zzl(list);
        this.errorCode = i;
        this.zzbnn = zzl(list2);
        this.zzcca = zzl(list3);
        this.orientation = i2;
        this.zzbns = j;
        this.zzcau = str;
        this.zzcby = z;
        this.zzbon = zzfzVar;
        this.zzboo = zzgkVar;
        this.zzbop = str2;
        this.zzcig = zzgaVar;
        this.zzboq = zzgcVar;
        this.zzcbz = j2;
        this.zzcii = adSizeParcel;
        this.zzcbx = j3;
        this.zzcik = j4;
        this.zzcil = j5;
        this.zzccd = str3;
        this.zzcie = jSONObject;
        this.zzcim = zzaVar;
        this.zzccn = rewardItemParcel;
        this.zzcij = zzl(list4);
        this.zzccp = zzl(list5);
        this.zzccq = z2;
        this.zzccr = autoClickProtectionConfigurationParcel;
        this.zzcih = str4;
        this.zzbnp = zzl(list6);
    }

    public zzju(zza zzaVar) {
        this(zzaVar.zzcip.zzcar, null, zzaVar.zzciq.zzbnm, zzaVar.errorCode, zzaVar.zzciq.zzbnn, zzaVar.zzciq.zzcca, zzaVar.zzciq.orientation, zzaVar.zzciq.zzbns, zzaVar.zzcip.zzcau, zzaVar.zzciq.zzcby, null, null, null, zzaVar.zzcig, null, zzaVar.zzciq.zzcbz, zzaVar.zzapa, zzaVar.zzciq.zzcbx, zzaVar.zzcik, zzaVar.zzcil, zzaVar.zzciq.zzccd, zzaVar.zzcie, null, zzaVar.zzciq.zzccn, zzaVar.zzciq.zzcco, zzaVar.zzciq.zzcco, zzaVar.zzciq.zzccq, zzaVar.zzciq.zzccr, null, zzaVar.zzciq.zzbnp);
    }

    private static <T> List<T> zzl(List<T> list) {
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    public final boolean zzho() {
        if (this.zzbtm == null || this.zzbtm.zzuj() == null) {
            return false;
        }
        return this.zzbtm.zzuj().zzho();
    }
}
