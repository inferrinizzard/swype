package com.nuance.swype.view;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

@TargetApi(21)
/* loaded from: classes.dex */
public class TintDrawableV21 extends TintDrawable {
    public TintDrawableV21(Drawable drawable) {
        super(drawable);
    }

    @Override // com.nuance.swype.view.TintDrawable
    protected boolean applyTint() {
        setTintList(this.colors);
        setTintMode(this.mode);
        invalidateSelf();
        return true;
    }

    @Override // com.nuance.swype.view.TintDrawable, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean drawableChanged = this.state.drawable.setState(state);
        onBoundsChange(getBounds());
        return drawableChanged;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList tint) {
        this.state.drawable.setTintList(tint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode tintMode) {
        this.state.drawable.setTintMode(tintMode);
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.state.drawable.getColorFilter();
    }
}
