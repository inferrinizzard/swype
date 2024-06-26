package com.google.android.gms.ads.formats;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public final class NativeContentAdView extends NativeAdView {
    public NativeContentAdView(Context context) {
        super(context);
    }

    public NativeContentAdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NativeContentAdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public NativeContentAdView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public final View getAdvertiserView() {
        return super.zzq("1004");
    }

    public final View getBodyView() {
        return super.zzq("1002");
    }

    public final View getCallToActionView() {
        return super.zzq("1003");
    }

    public final View getHeadlineView() {
        return super.zzq("1001");
    }

    public final View getImageView() {
        return super.zzq("1005");
    }

    public final View getLogoView() {
        return super.zzq("1006");
    }

    public final void setAdvertiserView(View view) {
        super.zza("1004", view);
    }

    public final void setBodyView(View view) {
        super.zza("1002", view);
    }

    public final void setCallToActionView(View view) {
        super.zza("1003", view);
    }

    public final void setHeadlineView(View view) {
        super.zza("1001", view);
    }

    public final void setImageView(View view) {
        super.zza("1005", view);
    }

    public final void setLogoView(View view) {
        super.zza("1006", view);
    }
}
