package com.google.android.gms.internal;

import android.location.Location;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.nuance.connect.comm.MessageAPI;
import java.util.Date;
import java.util.List;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public final class zzgu implements NativeMediationAdRequest {
    private final NativeAdOptionsParcel zzalk;
    private final List<String> zzall;
    private final int zzaud;
    private final boolean zzaup;
    private final int zzbph;
    private final Date zzfp;
    private final Set<String> zzfr;
    private final boolean zzfs;
    private final Location zzft;

    public zzgu(Date date, int i, Set<String> set, Location location, boolean z, int i2, NativeAdOptionsParcel nativeAdOptionsParcel, List<String> list, boolean z2) {
        this.zzfp = date;
        this.zzaud = i;
        this.zzfr = set;
        this.zzft = location;
        this.zzfs = z;
        this.zzbph = i2;
        this.zzalk = nativeAdOptionsParcel;
        this.zzall = list;
        this.zzaup = z2;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final Date getBirthday() {
        return this.zzfp;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final int getGender() {
        return this.zzaud;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final Set<String> getKeywords() {
        return this.zzfr;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final Location getLocation() {
        return this.zzft;
    }

    @Override // com.google.android.gms.ads.mediation.NativeMediationAdRequest
    public final NativeAdOptions getNativeAdOptions() {
        if (this.zzalk == null) {
            return null;
        }
        return new NativeAdOptions.Builder().setReturnUrlsForImageAssets(this.zzalk.zzbgp).setImageOrientation(this.zzalk.zzbgq).setRequestMultipleImages(this.zzalk.zzbgr).build();
    }

    @Override // com.google.android.gms.ads.mediation.NativeMediationAdRequest
    public final boolean isAppInstallAdRequested() {
        return this.zzall != null && this.zzall.contains(MessageAPI.DELAYED_FROM);
    }

    @Override // com.google.android.gms.ads.mediation.NativeMediationAdRequest
    public final boolean isContentAdRequested() {
        return this.zzall != null && this.zzall.contains("1");
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final boolean isDesignedForFamilies() {
        return this.zzaup;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final boolean isTesting() {
        return this.zzfs;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdRequest
    public final int taggedForChildDirectedTreatment() {
        return this.zzbph;
    }
}
