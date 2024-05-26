package com.google.android.gms.ads.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.internal.client.zzae;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class SearchAdView extends ViewGroup {
    private final zzae zzaih;

    public SearchAdView(Context context) {
        super(context);
        this.zzaih = new zzae(this);
    }

    public SearchAdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.zzaih = new zzae(this, attributeSet, false);
    }

    public SearchAdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.zzaih = new zzae(this, attributeSet, false);
    }

    public final void destroy() {
        this.zzaih.destroy();
    }

    public final AdListener getAdListener() {
        return this.zzaih.getAdListener();
    }

    public final AdSize getAdSize() {
        return this.zzaih.getAdSize();
    }

    public final String getAdUnitId() {
        return this.zzaih.getAdUnitId();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected final void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
    protected final void onMeasure(int i, int i2) {
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

    public final void pause() {
        this.zzaih.pause();
    }

    public final void resume() {
        this.zzaih.resume();
    }

    public final void setAdListener(AdListener adListener) {
        this.zzaih.setAdListener(adListener);
    }

    public final void setAdSize(AdSize adSize) {
        this.zzaih.setAdSizes(adSize);
    }

    public final void setAdUnitId(String str) {
        this.zzaih.setAdUnitId(str);
    }

    public final void loadAd(SearchAdRequest searchAdRequest) {
        this.zzaih.zza(searchAdRequest.zzaic);
    }

    public final void loadAd(DynamicHeightSearchAdRequest dynamicHeightSearchAdRequest) {
        if (!AdSize.SEARCH.equals(getAdSize())) {
            throw new IllegalStateException("You must use AdSize.SEARCH for a DynamicHeightSearchAdRequest");
        }
        this.zzaih.zza(dynamicHeightSearchAdRequest.zzcqy.zzaic);
    }
}
