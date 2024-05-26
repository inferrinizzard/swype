package android.support.v4.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.widget.EdgeEffect;

/* loaded from: classes.dex */
public final class EdgeEffectCompat {
    private static final EdgeEffectImpl IMPL;
    private Object mEdgeEffect;

    /* loaded from: classes.dex */
    interface EdgeEffectImpl {
        boolean draw(Object obj, Canvas canvas);

        void finish(Object obj);

        boolean isFinished(Object obj);

        Object newEdgeEffect(Context context);

        boolean onAbsorb(Object obj, int i);

        boolean onPull(Object obj, float f);

        boolean onPull(Object obj, float f, float f2);

        boolean onRelease(Object obj);

        void setSize(Object obj, int i, int i2);
    }

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new EdgeEffectLollipopImpl();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new EdgeEffectIcsImpl();
        } else {
            IMPL = new BaseEdgeEffectImpl();
        }
    }

    /* loaded from: classes.dex */
    static class BaseEdgeEffectImpl implements EdgeEffectImpl {
        BaseEdgeEffectImpl() {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final Object newEdgeEffect(Context context) {
            return null;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final void setSize(Object edgeEffect, int width, int height) {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean isFinished(Object edgeEffect) {
            return true;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final void finish(Object edgeEffect) {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onPull(Object edgeEffect, float deltaDistance) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onRelease(Object edgeEffect) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onAbsorb(Object edgeEffect, int velocity) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean draw(Object edgeEffect, Canvas canvas) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onPull(Object edgeEffect, float deltaDistance, float displacement) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class EdgeEffectIcsImpl implements EdgeEffectImpl {
        EdgeEffectIcsImpl() {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onPull(Object edgeEffect, float deltaDistance) {
            return EdgeEffectCompatIcs.onPull(edgeEffect, deltaDistance);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onPull(Object edgeEffect, float deltaDistance, float displacement) {
            return EdgeEffectCompatIcs.onPull(edgeEffect, deltaDistance);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final Object newEdgeEffect(Context context) {
            return new EdgeEffect(context);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final void setSize(Object edgeEffect, int width, int height) {
            ((EdgeEffect) edgeEffect).setSize(width, height);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean isFinished(Object edgeEffect) {
            return ((EdgeEffect) edgeEffect).isFinished();
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final void finish(Object edgeEffect) {
            ((EdgeEffect) edgeEffect).finish();
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onRelease(Object edgeEffect) {
            EdgeEffect edgeEffect2 = (EdgeEffect) edgeEffect;
            edgeEffect2.onRelease();
            return edgeEffect2.isFinished();
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onAbsorb(Object edgeEffect, int velocity) {
            ((EdgeEffect) edgeEffect).onAbsorb(velocity);
            return true;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean draw(Object edgeEffect, Canvas canvas) {
            return ((EdgeEffect) edgeEffect).draw(canvas);
        }
    }

    /* loaded from: classes.dex */
    static class EdgeEffectLollipopImpl extends EdgeEffectIcsImpl {
        EdgeEffectLollipopImpl() {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectIcsImpl, android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public final boolean onPull(Object edgeEffect, float deltaDistance, float displacement) {
            ((EdgeEffect) edgeEffect).onPull(deltaDistance, displacement);
            return true;
        }
    }

    public EdgeEffectCompat(Context context) {
        this.mEdgeEffect = IMPL.newEdgeEffect(context);
    }

    public final void setSize(int width, int height) {
        IMPL.setSize(this.mEdgeEffect, width, height);
    }

    public final boolean isFinished() {
        return IMPL.isFinished(this.mEdgeEffect);
    }

    public final void finish() {
        IMPL.finish(this.mEdgeEffect);
    }

    @Deprecated
    public final boolean onPull(float deltaDistance) {
        return IMPL.onPull(this.mEdgeEffect, deltaDistance);
    }

    public final boolean onPull(float deltaDistance, float displacement) {
        return IMPL.onPull(this.mEdgeEffect, deltaDistance, displacement);
    }

    public final boolean onRelease() {
        return IMPL.onRelease(this.mEdgeEffect);
    }

    public final boolean onAbsorb(int velocity) {
        return IMPL.onAbsorb(this.mEdgeEffect, velocity);
    }

    public final boolean draw(Canvas canvas) {
        return IMPL.draw(this.mEdgeEffect, canvas);
    }
}
