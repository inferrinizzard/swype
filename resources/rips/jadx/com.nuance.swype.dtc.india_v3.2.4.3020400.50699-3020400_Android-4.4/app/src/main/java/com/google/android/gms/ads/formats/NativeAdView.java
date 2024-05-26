package com.google.android.gms.ads.formats;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzdt;

/* loaded from: classes.dex */
public abstract class NativeAdView extends FrameLayout {
    private final FrameLayout zzaiz;
    private final zzdt zzaja;

    public NativeAdView(Context context) {
        super(context);
        this.zzaiz = zzf(context);
        this.zzaja = zzdh();
    }

    public NativeAdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzaiz = zzf(context);
        this.zzaja = zzdh();
    }

    public NativeAdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzaiz = zzf(context);
        this.zzaja = zzdh();
    }

    public NativeAdView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.zzaiz = zzf(context);
        this.zzaja = zzdh();
    }

    private zzdt zzdh() {
        zzab.zzb(this.zzaiz, "createDelegate must be called after mOverlayFrame has been created");
        return zzm.zzix().zza(this.zzaiz.getContext(), this, this.zzaiz);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        super.bringChildToFront(this.zzaiz);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void bringChildToFront(View view) {
        super.bringChildToFront(view);
        if (this.zzaiz != view) {
            super.bringChildToFront(this.zzaiz);
        }
    }

    public void destroy() {
        try {
            this.zzaja.destroy();
        } catch (RemoteException e) {
            zzb.zzb("Unable to destroy native ad view", e);
        }
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        super.removeAllViews();
        super.addView(this.zzaiz);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        if (this.zzaiz == view) {
            return;
        }
        super.removeView(view);
    }

    public void setNativeAd(NativeAd nativeAd) {
        try {
            this.zzaja.zze((zzd) nativeAd.zzdg());
        } catch (RemoteException e) {
            zzb.zzb("Unable to call setNativeAd on delegate", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void zza(String str, View view) {
        try {
            this.zzaja.zzc(str, zze.zzac(view));
        } catch (RemoteException e) {
            zzb.zzb("Unable to call setAssetView on delegate", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final View zzq(String str) {
        try {
            zzd zzap = this.zzaja.zzap(str);
            if (zzap != null) {
                return (View) zze.zzad(zzap);
            }
        } catch (RemoteException e) {
            zzb.zzb("Unable to call getAssetView on delegate", e);
        }
        return null;
    }

    private FrameLayout zzf(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(frameLayout);
        return frameLayout;
    }
}
