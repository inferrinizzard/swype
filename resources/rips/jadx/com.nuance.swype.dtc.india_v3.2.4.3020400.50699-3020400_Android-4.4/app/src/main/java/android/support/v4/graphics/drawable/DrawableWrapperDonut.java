package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class DrawableWrapperDonut extends Drawable implements Drawable.Callback, DrawableWrapper, TintAwareDrawable {
    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
    private boolean mColorFilterSet;
    private int mCurrentColor;
    private PorterDuff.Mode mCurrentMode;
    Drawable mDrawable;
    private boolean mMutated;
    DrawableWrapperState mState;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperDonut(DrawableWrapperState state, Resources res) {
        this.mState = state;
        if (this.mState == null || this.mState.mDrawableState == null) {
            return;
        }
        setWrappedDrawable(newDrawableFromState(this.mState.mDrawableState, res));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperDonut(Drawable dr) {
        this.mState = mutateConstantState();
        setWrappedDrawable(dr);
    }

    protected Drawable newDrawableFromState(Drawable.ConstantState state, Resources res) {
        return state.newDrawable();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mDrawable.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        if (this.mDrawable != null) {
            this.mDrawable.setBounds(bounds);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setChangingConfigurations(int configs) {
        this.mDrawable.setChangingConfigurations(configs);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return (this.mState != null ? this.mState.getChangingConfigurations() : 0) | super.getChangingConfigurations() | this.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        this.mDrawable.setDither(dither);
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean filter) {
        this.mDrawable.setFilterBitmap(filter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mDrawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mDrawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList tintList = (!isCompatTintEnabled() || this.mState == null) ? null : this.mState.mTint;
        return (tintList != null && tintList.isStateful()) || this.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setState(int[] stateSet) {
        boolean handled = this.mDrawable.setState(stateSet);
        return updateTint(stateSet) || handled;
    }

    @Override // android.graphics.drawable.Drawable
    public int[] getState() {
        return this.mDrawable.getState();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable getCurrent() {
        return this.mDrawable.getCurrent();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart) || this.mDrawable.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public Region getTransparentRegion() {
        return this.mDrawable.getTransparentRegion();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return this.mDrawable.getMinimumWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.mDrawable.getMinimumHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        return this.mDrawable.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mState != null) {
            if (this.mState.mDrawableState != null) {
                this.mState.mChangingConfigurations = getChangingConfigurations();
                return this.mState;
            }
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState = mutateConstantState();
            if (this.mDrawable != null) {
                this.mDrawable.mutate();
            }
            if (this.mState != null) {
                this.mState.mDrawableState = this.mDrawable != null ? this.mDrawable.getConstantState() : null;
            }
            this.mMutated = true;
        }
        return this;
    }

    DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateDonut(this.mState);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        return this.mDrawable.setLevel(level);
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public void setTint(int tint) {
        setTintList(ColorStateList.valueOf(tint));
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public void setTintList(ColorStateList tint) {
        this.mState.mTint = tint;
        updateTint(getState());
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public void setTintMode(PorterDuff.Mode tintMode) {
        this.mState.mTintMode = tintMode;
        updateTint(getState());
    }

    private boolean updateTint(int[] state) {
        if (!isCompatTintEnabled()) {
            return false;
        }
        ColorStateList tintList = this.mState.mTint;
        PorterDuff.Mode tintMode = this.mState.mTintMode;
        if (tintList != null && tintMode != null) {
            int color = tintList.getColorForState(state, tintList.getDefaultColor());
            if (this.mColorFilterSet && color == this.mCurrentColor && tintMode == this.mCurrentMode) {
                return false;
            }
            setColorFilter(color, tintMode);
            this.mCurrentColor = color;
            this.mCurrentMode = tintMode;
            this.mColorFilterSet = true;
            return true;
        }
        this.mColorFilterSet = false;
        clearColorFilter();
        return false;
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapper
    public final Drawable getWrappedDrawable() {
        return this.mDrawable;
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapper
    public final void setWrappedDrawable(Drawable dr) {
        if (this.mDrawable != null) {
            this.mDrawable.setCallback(null);
        }
        this.mDrawable = dr;
        if (dr != null) {
            dr.setCallback(this);
            dr.setVisible(isVisible(), true);
            dr.setState(getState());
            dr.setLevel(getLevel());
            dr.setBounds(getBounds());
            if (this.mState != null) {
                this.mState.mDrawableState = dr.getConstantState();
            }
        }
        invalidateSelf();
    }

    protected boolean isCompatTintEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static abstract class DrawableWrapperState extends Drawable.ConstantState {
        int mChangingConfigurations;
        Drawable.ConstantState mDrawableState;
        ColorStateList mTint;
        PorterDuff.Mode mTintMode;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public abstract Drawable newDrawable(Resources resources);

        /* JADX INFO: Access modifiers changed from: package-private */
        public DrawableWrapperState(DrawableWrapperState orig) {
            this.mTint = null;
            this.mTintMode = DrawableWrapperDonut.DEFAULT_TINT_MODE;
            if (orig != null) {
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mDrawableState = orig.mDrawableState;
                this.mTint = orig.mTint;
                this.mTintMode = orig.mTintMode;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return newDrawable(null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return (this.mDrawableState != null ? this.mDrawableState.getChangingConfigurations() : 0) | this.mChangingConfigurations;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DrawableWrapperStateDonut extends DrawableWrapperState {
        DrawableWrapperStateDonut(DrawableWrapperState orig) {
            super(orig);
        }

        @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new DrawableWrapperDonut(this, res);
        }
    }
}
