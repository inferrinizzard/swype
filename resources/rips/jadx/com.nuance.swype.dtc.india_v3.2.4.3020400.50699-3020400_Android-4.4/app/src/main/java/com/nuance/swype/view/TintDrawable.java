package com.nuance.swype.view;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class TintDrawable extends Drawable implements Drawable.Callback {
    protected static final LogManager.Log log = LogManager.getLog("TintDrawable");
    protected ColorStateList colors;
    FilterInfo filterInfo;
    private boolean isMutated;
    protected PorterDuff.Mode mode;
    protected TintState state;

    public TintDrawable(Drawable drawable) {
        this(null, null);
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable must not be null");
        }
        this.state.drawable = drawable;
        this.state.drawable.setCallback(this);
    }

    public void setTintParams(ColorStateList colors, PorterDuff.Mode mode) {
        this.colors = colors;
        this.mode = mode;
        applyTint();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FilterInfo {
        private int color;
        private PorterDuffColorFilter filter;

        private FilterInfo() {
        }

        PorterDuffColorFilter getFilter() {
            return this.filter;
        }

        boolean update(int color, PorterDuff.Mode mode) {
            if (this.filter != null && this.color == color) {
                return false;
            }
            this.filter = new PorterDuffColorFilter(color, mode);
            return true;
        }

        boolean reset() {
            boolean changed = this.filter != null;
            this.filter = null;
            return changed;
        }
    }

    protected boolean applyTint() {
        boolean changed;
        if (this.colors != null) {
            int color = this.colors.getColorForState(getState(), 0);
            changed = this.filterInfo.update(color, this.mode);
        } else {
            changed = this.filterInfo.reset();
        }
        this.state.drawable.mutate().setColorFilter(this.filterInfo.getFilter());
        return changed;
    }

    public Drawable getWrappedDrawable() {
        return this.state.drawable;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.state.drawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.Callback getCallback() {
        return super.getCallback();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.state.drawable != null) {
            this.state.drawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.state.changingConfigurationsMask | this.state.drawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        return this.state.drawable.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.state.drawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.state.drawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    @TargetApi(19)
    public int getAlpha() {
        return this.state.drawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.state.drawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.state.drawable.isStateful() || (this.colors != null && this.colors.isStateful());
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        log.d("onStateChange(): ", state);
        boolean drawableChanged = false;
        if (this.state.drawable.isStateful()) {
            drawableChanged = this.state.drawable.setState(state);
        }
        boolean filterChanged = applyTint();
        onBoundsChange(getBounds());
        return drawableChanged || filterChanged;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        this.state.drawable.setLevel(level);
        onBoundsChange(getBounds());
        invalidateSelf();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        this.state.drawable.setBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.state.drawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.state.drawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!this.state.canConstantState()) {
            return null;
        }
        this.state.changingConfigurationsMask = getChangingConfigurations();
        return this.state;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.isMutated && super.mutate() == this) {
            this.state.drawable.mutate();
            this.isMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TintState extends Drawable.ConstantState {
        int changingConfigurationsMask;
        Drawable drawable;
        private boolean mCanConstantState;
        private boolean mCheckedConstantState;
        boolean useIntrinsicSizeAsMin;

        TintState(TintState orig, TintDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    this.drawable = orig.drawable.getConstantState().newDrawable(res);
                } else {
                    this.drawable = orig.drawable.getConstantState().newDrawable();
                }
                this.drawable.setCallback(owner);
                this.useIntrinsicSizeAsMin = orig.useIntrinsicSizeAsMin;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new TintDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new TintDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.changingConfigurationsMask;
        }

        final boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.drawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }

    private TintDrawable(TintState state, Resources res) {
        this.filterInfo = new FilterInfo();
        this.state = new TintState(state, this, res);
    }
}
