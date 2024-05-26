package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.internal.zzin;
import java.util.Collections;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class AdResponseParcel extends AbstractSafeParcelable {
    public static final zzh CREATOR = new zzh();
    public String body;
    public final int errorCode;
    public final int orientation;
    public final int versionCode;
    public final boolean zzauu;
    public final boolean zzauv;
    public final boolean zzauw;
    public final List<String> zzbnm;
    public final List<String> zzbnn;
    public final List<String> zzbnp;
    public final boolean zzbnq;
    public final long zzbns;
    private AdRequestInfoParcel zzbot;
    public final String zzbto;
    public final boolean zzcaz;
    public final boolean zzcbq;
    public String zzcbr;
    public final long zzcbx;
    public final boolean zzcby;
    public final long zzcbz;
    public final List<String> zzcca;
    public final String zzccb;
    public final long zzccc;
    public final String zzccd;
    public final boolean zzcce;
    public final String zzccf;
    public final String zzccg;
    public final boolean zzcch;
    public final boolean zzcci;
    public final boolean zzccj;
    public LargeParcelTeleporter zzcck;
    public String zzccl;
    public final String zzccm;
    public final RewardItemParcel zzccn;
    public final List<String> zzcco;
    public final List<String> zzccp;
    public final boolean zzccq;
    public final AutoClickProtectionConfigurationParcel zzccr;
    public final String zzccs;
    public final String zzcct;

    public AdResponseParcel(int i) {
        this(18, null, null, null, i, null, -1L, false, -1L, null, -1L, -1, null, -1L, null, false, null, null, false, false, false, true, false, null, null, null, false, false, null, null, null, false, null, false, null, null, null, false, null);
    }

    public AdResponseParcel(int i, long j) {
        this(18, null, null, null, i, null, -1L, false, -1L, null, j, -1, null, -1L, null, false, null, null, false, false, false, true, false, null, null, null, false, false, null, null, null, false, null, false, null, null, null, false, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdResponseParcel(int i, String str, String str2, List<String> list, int i2, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i3, String str3, long j4, String str4, boolean z2, String str5, String str6, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, LargeParcelTeleporter largeParcelTeleporter, String str7, String str8, boolean z8, boolean z9, RewardItemParcel rewardItemParcel, List<String> list4, List<String> list5, boolean z10, AutoClickProtectionConfigurationParcel autoClickProtectionConfigurationParcel, boolean z11, String str9, List<String> list6, String str10, boolean z12, String str11) {
        StringParcel stringParcel;
        this.versionCode = i;
        this.zzbto = str;
        this.body = str2;
        this.zzbnm = list != null ? Collections.unmodifiableList(list) : null;
        this.errorCode = i2;
        this.zzbnn = list2 != null ? Collections.unmodifiableList(list2) : null;
        this.zzcbx = j;
        this.zzcby = z;
        this.zzcbz = j2;
        this.zzcca = list3 != null ? Collections.unmodifiableList(list3) : null;
        this.zzbns = j3;
        this.orientation = i3;
        this.zzccb = str3;
        this.zzccc = j4;
        this.zzccd = str4;
        this.zzcce = z2;
        this.zzccf = str5;
        this.zzccg = str6;
        this.zzcch = z3;
        this.zzauu = z4;
        this.zzcaz = z5;
        this.zzcci = z6;
        this.zzccj = z7;
        this.zzcck = largeParcelTeleporter;
        this.zzccl = str7;
        this.zzccm = str8;
        if (this.body == null && this.zzcck != null && (stringParcel = (StringParcel) this.zzcck.zza(StringParcel.CREATOR)) != null && !TextUtils.isEmpty(stringParcel.zzre())) {
            this.body = stringParcel.zzre();
        }
        this.zzauv = z8;
        this.zzauw = z9;
        this.zzccn = rewardItemParcel;
        this.zzcco = list4;
        this.zzccp = list5;
        this.zzccq = z10;
        this.zzccr = autoClickProtectionConfigurationParcel;
        this.zzcbq = z11;
        this.zzcbr = str9;
        this.zzbnp = list6;
        this.zzccs = str10;
        this.zzbnq = z12;
        this.zzcct = str11;
    }

    public AdResponseParcel(AdRequestInfoParcel adRequestInfoParcel, String str, String str2, List<String> list, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i, String str3, long j4, String str4, String str5, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, String str6, boolean z7, boolean z8, RewardItemParcel rewardItemParcel, List<String> list4, List<String> list5, boolean z9, AutoClickProtectionConfigurationParcel autoClickProtectionConfigurationParcel, boolean z10, String str7, List<String> list6, String str8, boolean z11, String str9) {
        this(18, str, str2, list, -2, list2, j, z, j2, list3, j3, i, str3, j4, str4, false, null, str5, z2, z3, z4, z5, z6, null, null, str6, z7, z8, rewardItemParcel, list4, list5, z9, autoClickProtectionConfigurationParcel, z10, str7, list6, str8, z11, str9);
        this.zzbot = adRequestInfoParcel;
    }

    public AdResponseParcel(AdRequestInfoParcel adRequestInfoParcel, String str, String str2, List<String> list, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i, String str3, long j4, String str4, boolean z2, String str5, String str6, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, String str7, boolean z8, boolean z9, RewardItemParcel rewardItemParcel, List<String> list4, List<String> list5, boolean z10, AutoClickProtectionConfigurationParcel autoClickProtectionConfigurationParcel, boolean z11, String str8, List<String> list6, String str9, boolean z12, String str10) {
        this(18, str, str2, list, -2, list2, j, z, j2, list3, j3, i, str3, j4, str4, z2, str5, str6, z3, z4, z5, z6, z7, null, null, str7, z8, z9, rewardItemParcel, list4, list5, z10, autoClickProtectionConfigurationParcel, z11, str8, list6, str9, z12, str10);
        this.zzbot = adRequestInfoParcel;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        if (this.zzbot != null && this.zzbot.versionCode >= 9 && !TextUtils.isEmpty(this.body)) {
            this.zzcck = new LargeParcelTeleporter(new StringParcel(this.body));
            this.body = null;
        }
        zzh.zza(this, parcel, i);
    }
}
