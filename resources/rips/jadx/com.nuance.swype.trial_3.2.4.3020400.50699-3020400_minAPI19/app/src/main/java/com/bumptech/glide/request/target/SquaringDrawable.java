package com.bumptech.glide.request.target;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

/* loaded from: classes.dex */
public final class SquaringDrawable extends GlideDrawable {
    private boolean mutated;
    private State state;
    private GlideDrawable wrapped;

    public SquaringDrawable(GlideDrawable wrapped, int side) {
        this(new State(wrapped.getConstantState(), side), wrapped, null);
    }

    SquaringDrawable(State state, GlideDrawable wrapped, Resources res) {
        this.state = state;
        if (wrapped == null) {
            if (res != null) {
                this.wrapped = (GlideDrawable) state.wrapped.newDrawable(res);
                return;
            } else {
                this.wrapped = (GlideDrawable) state.wrapped.newDrawable();
                return;
            }
        }
        this.wrapped = wrapped;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.wrapped.setBounds(left, top, right, bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setBounds(Rect bounds) {
        super.setBounds(bounds);
        this.wrapped.setBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setChangingConfigurations(int configs) {
        this.wrapped.setChangingConfigurations(configs);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        return this.wrapped.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setDither(boolean dither) {
        this.wrapped.setDither(dither);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setFilterBitmap(boolean filter) {
        this.wrapped.setFilterBitmap(filter);
    }

    @Override // android.graphics.drawable.Drawable
    @TargetApi(11)
    public final Drawable.Callback getCallback() {
        return this.wrapped.getCallback();
    }

    @Override // android.graphics.drawable.Drawable
    @TargetApi(19)
    public final int getAlpha() {
        return this.wrapped.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(int color, PorterDuff.Mode mode) {
        this.wrapped.setColorFilter(color, mode);
    }

    @Override // android.graphics.drawable.Drawable
    public final void clearColorFilter() {
        this.wrapped.clearColorFilter();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable getCurrent() {
        return this.wrapped.getCurrent();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean visible, boolean restart) {
        return this.wrapped.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return this.state.side;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return this.state.side;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumWidth() {
        return this.wrapped.getMinimumWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumHeight() {
        return this.wrapped.getMinimumHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean getPadding(Rect padding) {
        return this.wrapped.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public final void invalidateSelf() {
        super.invalidateSelf();
        this.wrapped.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void unscheduleSelf(Runnable what) {
        super.unscheduleSelf(what);
        this.wrapped.unscheduleSelf(what);
    }

    @Override // android.graphics.drawable.Drawable
    public final void scheduleSelf(Runnable what, long when) {
        super.scheduleSelf(what, when);
        this.wrapped.scheduleSelf(what, when);
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        this.wrapped.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i) {
        this.wrapped.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.wrapped.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return this.wrapped.getOpacity();
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final boolean isAnimated() {
        return this.wrapped.isAnimated();
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final void setLoopCount(int loopCount) {
        this.wrapped.setLoopCount(loopCount);
    }

    @Override // android.graphics.drawable.Animatable
    public final void start() {
        this.wrapped.start();
    }

    @Override // android.graphics.drawable.Animatable
    public final void stop() {
        this.wrapped.stop();
    }

    @Override // android.graphics.drawable.Animatable
    public final boolean isRunning() {
        return this.wrapped.isRunning();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        if (!this.mutated && super.mutate() == this) {
            this.wrapped = (GlideDrawable) this.wrapped.mutate();
            this.state = new State(this.state);
            this.mutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        return this.state;
    }

    /* loaded from: classes.dex */
    static class State extends Drawable.ConstantState {
        private final int side;
        private final Drawable.ConstantState wrapped;

        State(State other) {
            this(other.wrapped, other.side);
        }

        State(Drawable.ConstantState wrapped, int side) {
            this.wrapped = wrapped;
            this.side = side;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return newDrawable(null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new SquaringDrawable(this, null, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }
    }
}
