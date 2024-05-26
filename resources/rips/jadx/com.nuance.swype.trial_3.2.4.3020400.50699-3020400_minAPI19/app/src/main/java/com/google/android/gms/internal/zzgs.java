package com.google.android.gms.internal;

import android.os.Bundle;
import android.view.View;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.mediation.NativeAppInstallAdMapper;
import com.google.android.gms.internal.zzgn;
import java.util.ArrayList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzgs extends zzgn.zza {
    private final NativeAppInstallAdMapper zzbpm;

    public zzgs(NativeAppInstallAdMapper nativeAppInstallAdMapper) {
        this.zzbpm = nativeAppInstallAdMapper;
    }

    @Override // com.google.android.gms.internal.zzgn
    public final String getBody() {
        return this.zzbpm.getBody();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final String getCallToAction() {
        return this.zzbpm.getCallToAction();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final Bundle getExtras() {
        return this.zzbpm.getExtras();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final String getHeadline() {
        return this.zzbpm.getHeadline();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final List getImages() {
        List<NativeAd.Image> images = this.zzbpm.getImages();
        if (images == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (NativeAd.Image image : images) {
            arrayList.add(new com.google.android.gms.ads.internal.formats.zzc(image.getDrawable(), image.getUri(), image.getScale()));
        }
        return arrayList;
    }

    @Override // com.google.android.gms.internal.zzgn
    public final boolean getOverrideClickHandling() {
        return this.zzbpm.getOverrideClickHandling();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final boolean getOverrideImpressionRecording() {
        return this.zzbpm.getOverrideImpressionRecording();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final String getPrice() {
        return this.zzbpm.getPrice();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final double getStarRating() {
        return this.zzbpm.getStarRating();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final String getStore() {
        return this.zzbpm.getStore();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final void recordImpression() {
        this.zzbpm.recordImpression();
    }

    @Override // com.google.android.gms.internal.zzgn
    public final void zzk(com.google.android.gms.dynamic.zzd zzdVar) {
        this.zzbpm.handleClick((View) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzgn
    public final zzdr zzku() {
        NativeAd.Image icon = this.zzbpm.getIcon();
        if (icon != null) {
            return new com.google.android.gms.ads.internal.formats.zzc(icon.getDrawable(), icon.getUri(), icon.getScale());
        }
        return null;
    }

    @Override // com.google.android.gms.internal.zzgn
    public final void zzl(com.google.android.gms.dynamic.zzd zzdVar) {
        this.zzbpm.trackView((View) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzgn
    public final void zzm(com.google.android.gms.dynamic.zzd zzdVar) {
        this.zzbpm.untrackView((View) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }
}
