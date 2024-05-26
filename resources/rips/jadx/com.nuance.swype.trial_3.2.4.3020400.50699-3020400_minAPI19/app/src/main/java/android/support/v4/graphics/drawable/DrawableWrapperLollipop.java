package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableWrapperDonut;

/* loaded from: classes.dex */
final class DrawableWrapperLollipop extends DrawableWrapperKitKat {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperLollipop(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperLollipop(DrawableWrapperDonut.DrawableWrapperState state, Resources resources) {
        super(state, resources);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspot(float x, float y) {
        this.mDrawable.setHotspot(x, y);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mDrawable.setHotspotBounds(left, top, right, bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public final void getOutline(Outline outline) {
        this.mDrawable.getOutline(outline);
    }

    @Override // android.graphics.drawable.Drawable
    public final Rect getDirtyBounds() {
        return this.mDrawable.getDirtyBounds();
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut, android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTintList(ColorStateList tint) {
        if (isCompatTintEnabled()) {
            super.setTintList(tint);
        } else {
            this.mDrawable.setTintList(tint);
        }
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut, android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTint(int tintColor) {
        if (isCompatTintEnabled()) {
            super.setTint(tintColor);
        } else {
            this.mDrawable.setTint(tintColor);
        }
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut, android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTintMode(PorterDuff.Mode tintMode) {
        if (isCompatTintEnabled()) {
            super.setTintMode(tintMode);
        } else {
            this.mDrawable.setTintMode(tintMode);
        }
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut, android.graphics.drawable.Drawable
    public final boolean setState(int[] stateSet) {
        if (!super.setState(stateSet)) {
            return false;
        }
        invalidateSelf();
        return true;
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut
    protected final boolean isCompatTintEnabled() {
        if (Build.VERSION.SDK_INT != 21) {
            return false;
        }
        Drawable drawable = this.mDrawable;
        return (drawable instanceof GradientDrawable) || (drawable instanceof DrawableContainer) || (drawable instanceof InsetDrawable);
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperKitKat, android.support.v4.graphics.drawable.DrawableWrapperHoneycomb, android.support.v4.graphics.drawable.DrawableWrapperDonut
    final DrawableWrapperDonut.DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateLollipop(this.mState);
    }

    /* loaded from: classes.dex */
    private static class DrawableWrapperStateLollipop extends DrawableWrapperDonut.DrawableWrapperState {
        DrawableWrapperStateLollipop(DrawableWrapperDonut.DrawableWrapperState orig) {
            super(orig);
        }

        @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new DrawableWrapperLollipop(this, res);
        }
    }
}
