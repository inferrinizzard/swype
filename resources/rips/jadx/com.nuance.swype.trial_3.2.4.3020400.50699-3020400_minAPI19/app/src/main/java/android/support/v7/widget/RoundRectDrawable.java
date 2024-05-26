package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
final class RoundRectDrawable extends Drawable {
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    float mPadding;
    float mRadius;
    private ColorStateList mTint;
    private PorterDuffColorFilter mTintFilter;
    boolean mInsetForPadding = false;
    boolean mInsetForRadius = true;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    final Paint mPaint = new Paint(5);

    public RoundRectDrawable(int backgroundColor, float radius) {
        this.mRadius = radius;
        this.mPaint.setColor(backgroundColor);
        this.mBoundsF = new RectF();
        this.mBoundsI = new Rect();
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        boolean clearColorFilter;
        Paint paint = this.mPaint;
        if (this.mTintFilter != null && paint.getColorFilter() == null) {
            paint.setColorFilter(this.mTintFilter);
            clearColorFilter = true;
        } else {
            clearColorFilter = false;
        }
        canvas.drawRoundRect(this.mBoundsF, this.mRadius, this.mRadius, paint);
        if (clearColorFilter) {
            paint.setColorFilter(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = getBounds();
        }
        this.mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        this.mBoundsI.set(bounds);
        if (this.mInsetForPadding) {
            float vInset = RoundRectDrawableWithShadow.calculateVerticalPadding(this.mPadding, this.mRadius, this.mInsetForRadius);
            float hInset = RoundRectDrawableWithShadow.calculateHorizontalPadding(this.mPadding, this.mRadius, this.mInsetForRadius);
            this.mBoundsI.inset((int) Math.ceil(hInset), (int) Math.ceil(vInset));
            this.mBoundsF.set(this.mBoundsI);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected final void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable
    public final void getOutline(Outline outline) {
        outline.setRoundRect(this.mBoundsI, this.mRadius);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList tint) {
        this.mTint = tint;
        this.mTintFilter = createTintFilter(this.mTint, this.mTintMode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode tintMode) {
        this.mTintMode = tintMode;
        this.mTintFilter = createTintFilter(this.mTint, this.mTintMode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected final boolean onStateChange(int[] stateSet) {
        if (this.mTint == null || this.mTintMode == null) {
            return false;
        }
        this.mTintFilter = createTintFilter(this.mTint, this.mTintMode);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        return (this.mTint != null && this.mTint.isStateful()) || super.isStateful();
    }

    private PorterDuffColorFilter createTintFilter(ColorStateList tint, PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        int color = tint.getColorForState(getState(), 0);
        return new PorterDuffColorFilter(color, tintMode);
    }
}
