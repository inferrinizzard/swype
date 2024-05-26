package com.nuance.swype.view;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.nuance.swype.input.ShadowEffect;
import com.nuance.swype.input.ShadowProps;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class ShadowDrawable extends Drawable implements Drawable.Callback {
    protected static final LogManager.Log log = LogManager.getLog("ShadowDrawable");
    private boolean isMutated;
    private ShadowDrawableState myState;
    private ShadowEffect shadowEffect;
    private Matrix shadowTransformMatrix;
    private final Rect tempRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ShadowType {
        SHADOW_DRAWABLE,
        SHADOW_DYNAMIC,
        SHADOW_SHADOW_LAYER
    }

    /* synthetic */ ShadowDrawable(ShadowDrawableState x0, Resources x2, byte b) {
        this(x0, x2);
    }

    ShadowDrawable() {
        this.tempRect = new Rect();
        this.myState = new ShadowDrawableState(null, null, null);
    }

    private ShadowDrawable(ShadowDrawableState orig, Resources res) {
        this.tempRect = new Rect();
        this.myState = new ShadowDrawableState(orig, null, res);
    }

    private ShadowDrawable(Resources res, Drawable primaryDrawable, ShadowProps shadowProps) {
        this.tempRect = new Rect();
        this.myState = new ShadowDrawableState(null, this, res);
        this.myState.primaryDrawable = primaryDrawable;
        this.myState.shadowDrawable = null;
        this.myState.shadowProps = shadowProps;
        if (primaryDrawable != null) {
            primaryDrawable.setCallback(this);
        }
        this.myState.shadowType = ShadowType.SHADOW_DYNAMIC;
    }

    private ShadowDrawable(Resources res, Drawable primaryDrawable, ShadowProps shadowProps, byte b) {
        this(res, primaryDrawable, shadowProps);
    }

    public static void addBackgroundShadow(Resources res, View view, ShadowProps props, boolean mirror) {
        boolean z = true;
        if (props != null && props.isEnabled()) {
            Drawable back = view.getBackground();
            if (!(back instanceof ShadowDrawable)) {
                view.setBackgroundDrawable(new ShadowDrawable(res, back, props, (byte) 0));
            }
        }
        int i = mirror ? 1 : 0;
        Drawable background = view.getBackground();
        if (background == null || !(background instanceof ShadowDrawable)) {
            return;
        }
        ShadowDrawable shadowDrawable = (ShadowDrawable) background;
        if (shadowDrawable.myState.mirrorState != i) {
            shadowDrawable.myState.mirrorState = i;
            shadowDrawable.myState.shadowProps.flipHor(i == 1);
            shadowDrawable.onBoundsChange(shadowDrawable.getBounds());
            shadowDrawable.invalidateSelf();
        } else {
            z = false;
        }
        if (!z) {
            return;
        }
        view.setBackgroundDrawable(null);
        view.setBackgroundDrawable(shadowDrawable);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    @TargetApi(11)
    public final void invalidateDrawable(Drawable who) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    @TargetApi(11)
    public final void scheduleDrawable(Drawable who, Runnable what, long when) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    @TargetApi(11)
    public final void unscheduleDrawable(Drawable who, Runnable what) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        boolean isMirrored = this.myState.mirrorState != 0;
        if (isMirrored) {
            int width = getBounds().width();
            canvas.save();
            canvas.translate(width, 0.0f);
            canvas.scale(-1.0f, 1.0f);
        }
        if (this.myState.shadowDrawable != null) {
            if (this.shadowTransformMatrix != null) {
                canvas.save();
                canvas.concat(this.shadowTransformMatrix);
            }
            this.myState.shadowDrawable.draw(canvas);
            if (this.shadowTransformMatrix != null) {
                canvas.restore();
            }
            this.myState.primaryDrawable.draw(canvas);
        } else {
            if (this.shadowEffect == null) {
                this.shadowEffect = new ShadowEffect(this.myState.shadowProps);
            }
            this.shadowEffect.draw(canvas, this.myState.primaryDrawable);
        }
        if (!isMirrored) {
            return;
        }
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        int mask = super.getChangingConfigurations() | this.myState.changingConfigurationsMask | this.myState.primaryDrawable.getChangingConfigurations();
        if (this.myState.shadowDrawable != null) {
            return mask | this.myState.shadowDrawable.getChangingConfigurations();
        }
        return mask;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean visible, boolean restart) {
        this.myState.primaryDrawable.setVisible(visible, restart);
        if (this.myState.shadowDrawable != null) {
            this.myState.shadowDrawable.setVisible(visible, restart);
        }
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int alpha) {
        this.myState.primaryDrawable.setAlpha(alpha);
        if (this.myState.shadowDrawable != null) {
            this.myState.shadowDrawable.setAlpha(alpha);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter cf) {
        this.myState.primaryDrawable.setColorFilter(cf);
        if (this.myState.shadowDrawable != null) {
            this.myState.shadowDrawable.setColorFilter(cf);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        return this.myState.primaryDrawable.isStateful() && this.myState.shadowDrawable != null && this.myState.shadowDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected final boolean onStateChange(int[] state) {
        boolean changed = this.myState.primaryDrawable.setState(state);
        if (this.myState.shadowDrawable != null) {
            changed |= this.myState.shadowDrawable.setState(state);
        }
        onBoundsChange(getBounds());
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean getPadding(Rect padding) {
        this.myState.primaryDrawable.getPadding(padding);
        padding.left = (int) (padding.left + this.myState.shadowProps.getForegroundInsetLeft());
        padding.right = (int) (padding.right + this.myState.shadowProps.getForegroundInsetRight());
        padding.top = (int) (padding.top + this.myState.shadowProps.getForegroundInsetTop());
        padding.bottom = (int) (padding.bottom + this.myState.shadowProps.getForegroundInsetBottom());
        if (this.myState.mirrorState != 0) {
            int oldLeft = padding.left;
            padding.left = padding.right;
            padding.right = oldLeft;
            return true;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x00fb, code lost:            if ((r4.width() == r7.width() && r4.height() == r7.height()) == false) goto L25;     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected final void onBoundsChange(android.graphics.Rect r15) {
        /*
            Method dump skipped, instructions count: 381
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.view.ShadowDrawable.onBoundsChange(android.graphics.Rect):void");
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        float shadowWidth = this.myState.shadowProps.getForegroundInsetLeft() + this.myState.shadowProps.getForegroundInsetRight();
        return this.myState.primaryDrawable.getIntrinsicWidth() + ((int) shadowWidth);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        float shadowHeight = this.myState.shadowProps.getForegroundInsetTop() + this.myState.shadowProps.getForegroundInsetBottom();
        return this.myState.primaryDrawable.getIntrinsicHeight() + ((int) shadowHeight);
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        ShadowDrawableState shadowDrawableState = this.myState;
        if (!shadowDrawableState.isConstantStateChecked) {
            shadowDrawableState.isCanConstantState = (shadowDrawableState.primaryDrawable.getConstantState() == null || shadowDrawableState.shadowDrawable.getConstantState() == null) ? false : true;
            shadowDrawableState.isConstantStateChecked = true;
        }
        if (shadowDrawableState.isCanConstantState) {
            this.myState.changingConfigurationsMask = getChangingConfigurations();
            return this.myState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        if (!this.isMutated && super.mutate() == this) {
            this.myState.primaryDrawable.mutate();
            this.isMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ShadowDrawableState extends Drawable.ConstantState {
        int changingConfigurationsMask;
        boolean isCanConstantState;
        boolean isConstantStateChecked;
        int mirrorState;
        Drawable primaryDrawable;
        Drawable shadowDrawable;
        ShadowProps shadowProps;
        ShadowType shadowType;

        private static Drawable cloneDrawable(Drawable drawable, Resources res, ShadowDrawable owner) {
            Drawable out = drawable.getConstantState().newDrawable(res);
            out.setCallback(owner);
            return out;
        }

        ShadowDrawableState(ShadowDrawableState orig, ShadowDrawable owner, Resources res) {
            this.mirrorState = 0;
            this.shadowType = ShadowType.SHADOW_DYNAMIC;
            if (orig != null) {
                this.primaryDrawable = cloneDrawable(orig.primaryDrawable, res, owner);
                this.shadowDrawable = cloneDrawable(orig.shadowDrawable, res, owner);
                this.mirrorState = orig.mirrorState;
                this.shadowType = orig.shadowType;
                this.isCanConstantState = true;
                this.isConstantStateChecked = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new ShadowDrawable(this, (Resources) null, (byte) 0);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new ShadowDrawable(this, res, (byte) 0);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.changingConfigurationsMask;
        }
    }
}
