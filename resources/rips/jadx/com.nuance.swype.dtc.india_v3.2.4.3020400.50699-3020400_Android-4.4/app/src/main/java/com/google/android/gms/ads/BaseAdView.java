package com.google.android.gms.ads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.internal.client.zzae;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class BaseAdView extends ViewGroup {
    protected final zzae zzaih;

    public BaseAdView(Context context, int i) {
        super(context);
        this.zzaih = new zzae(this, zzg(i));
    }

    public BaseAdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.zzaih = new zzae(this, attributeSet, false, zzg(i));
    }

    public BaseAdView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.zzaih = new zzae(this, attributeSet, false, zzg(i2));
    }

    private static boolean zzg(int i) {
        return i == 2;
    }

    public void destroy() {
        this.zzaih.destroy();
    }

    public AdListener getAdListener() {
        return this.zzaih.getAdListener();
    }

    public AdSize getAdSize() {
        return this.zzaih.getAdSize();
    }

    public String getAdUnitId() {
        return this.zzaih.getAdUnitId();
    }

    public InAppPurchaseListener getInAppPurchaseListener() {
        return this.zzaih.getInAppPurchaseListener();
    }

    public String getMediationAdapterClassName() {
        return this.zzaih.getMediationAdapterClassName();
    }

    public boolean isLoading() {
        return this.zzaih.isLoading();
    }

    public void loadAd(AdRequest adRequest) {
        this.zzaih.zza(adRequest.zzdc());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View childAt = getChildAt(0);
        if (childAt == null || childAt.getVisibility() == 8) {
            return;
        }
        int measuredWidth = childAt.getMeasuredWidth();
        int measuredHeight = childAt.getMeasuredHeight();
        int i5 = ((i3 - i) - measuredWidth) / 2;
        int i6 = ((i4 - i2) - measuredHeight) / 2;
        childAt.layout(i5, i6, measuredWidth + i5, measuredHeight + i6);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4 = 0;
        View childAt = getChildAt(0);
        if (childAt == null || childAt.getVisibility() == 8) {
            AdSize adSize = getAdSize();
            if (adSize != null) {
                Context context = getContext();
                i3 = adSize.getWidthInPixels(context);
                i4 = adSize.getHeightInPixels(context);
            } else {
                i3 = 0;
            }
        } else {
            measureChild(childAt, i, i2);
            i3 = childAt.getMeasuredWidth();
            i4 = childAt.getMeasuredHeight();
        }
        setMeasuredDimension(View.resolveSize(Math.max(i3, getSuggestedMinimumWidth()), i), View.resolveSize(Math.max(i4, getSuggestedMinimumHeight()), i2));
    }

    public void pause() {
        this.zzaih.pause();
    }

    public void resume() {
        this.zzaih.resume();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setAdListener(AdListener adListener) {
        this.zzaih.setAdListener(adListener);
        if (adListener != 0 && (adListener instanceof com.google.android.gms.ads.internal.client.zza)) {
            this.zzaih.zza((com.google.android.gms.ads.internal.client.zza) adListener);
        } else if (adListener == 0) {
            this.zzaih.zza((com.google.android.gms.ads.internal.client.zza) null);
        }
    }

    public void setAdSize(AdSize adSize) {
        this.zzaih.setAdSizes(adSize);
    }

    public void setAdUnitId(String str) {
        this.zzaih.setAdUnitId(str);
    }

    public void setInAppPurchaseListener(InAppPurchaseListener inAppPurchaseListener) {
        this.zzaih.setInAppPurchaseListener(inAppPurchaseListener);
    }

    public void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String str) {
        this.zzaih.setPlayStorePurchaseParams(playStorePurchaseListener, str);
    }
}
