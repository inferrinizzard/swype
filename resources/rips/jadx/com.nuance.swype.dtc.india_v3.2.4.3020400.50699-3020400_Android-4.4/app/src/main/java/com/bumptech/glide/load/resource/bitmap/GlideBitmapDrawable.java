package com.bumptech.glide.load.resource.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

/* loaded from: classes.dex */
public class GlideBitmapDrawable extends GlideDrawable {
    private boolean applyGravity;
    private final Rect destRect;
    private int height;
    private boolean mutated;
    BitmapState state;
    private int width;

    public GlideBitmapDrawable(Resources res, Bitmap bitmap) {
        this(res, new BitmapState(bitmap));
    }

    GlideBitmapDrawable(Resources res, BitmapState state) {
        int targetDensity;
        this.destRect = new Rect();
        if (state == null) {
            throw new NullPointerException("BitmapState must not be null");
        }
        this.state = state;
        if (res != null) {
            int density = res.getDisplayMetrics().densityDpi;
            targetDensity = density == 0 ? 160 : density;
            state.targetDensity = targetDensity;
        } else {
            targetDensity = state.targetDensity;
        }
        this.width = state.bitmap.getScaledWidth(targetDensity);
        this.height = state.bitmap.getScaledHeight(targetDensity);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.height;
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final boolean isAnimated() {
        return false;
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final void setLoopCount(int loopCount) {
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyGravity = true;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.state;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.applyGravity) {
            Gravity.apply(119, this.width, this.height, getBounds(), this.destRect);
            this.applyGravity = false;
        }
        canvas.drawBitmap(this.state.bitmap, (Rect) null, this.destRect, this.state.paint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.state.paint.getAlpha() != alpha) {
            BitmapState bitmapState = this.state;
            bitmapState.mutatePaint();
            bitmapState.paint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        BitmapState bitmapState = this.state;
        bitmapState.mutatePaint();
        bitmapState.paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        Bitmap bm = this.state.bitmap;
        return (bm == null || bm.hasAlpha() || this.state.paint.getAlpha() < 255) ? -3 : -1;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mutated && super.mutate() == this) {
            this.state = new BitmapState(this.state);
            this.mutated = true;
        }
        return this;
    }

    /* loaded from: classes.dex */
    static class BitmapState extends Drawable.ConstantState {
        private static final Paint DEFAULT_PAINT = new Paint(6);
        final Bitmap bitmap;
        Paint paint;
        int targetDensity;

        public BitmapState(Bitmap bitmap) {
            this.paint = DEFAULT_PAINT;
            this.bitmap = bitmap;
        }

        BitmapState(BitmapState other) {
            this(other.bitmap);
            this.targetDensity = other.targetDensity;
        }

        final void mutatePaint() {
            if (DEFAULT_PAINT == this.paint) {
                this.paint = new Paint(6);
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new GlideBitmapDrawable((Resources) null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new GlideBitmapDrawable(res, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }
    }
}
