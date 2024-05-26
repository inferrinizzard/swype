package android.support.v4.widget;

import android.content.Context;
import android.os.Build;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.Scroller;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public final class ScrollerCompat {
    ScrollerCompatImpl mImpl;
    Object mScroller;

    /* loaded from: classes.dex */
    interface ScrollerCompatImpl {
        void abortAnimation(Object obj);

        boolean computeScrollOffset(Object obj);

        Object createScroller(Context context, Interpolator interpolator);

        void fling$26e48b1(Object obj, int i, int i2, int i3, int i4, int i5);

        void fling$f2fc891(Object obj, int i, int i2, int i3, int i4);

        float getCurrVelocity(Object obj);

        int getCurrX(Object obj);

        int getCurrY(Object obj);

        int getFinalX(Object obj);

        int getFinalY(Object obj);

        boolean isFinished(Object obj);

        boolean springBack$2bf03f8b(Object obj, int i, int i2, int i3);

        void startScroll(Object obj, int i, int i2, int i3, int i4, int i5);

        void startScroll$364c3051(Object obj, int i, int i2, int i3);
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplBase implements ScrollerCompatImpl {
        ScrollerCompatImplBase() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final Object createScroller(Context context, Interpolator interpolator) {
            return interpolator != null ? new Scroller(context, interpolator) : new Scroller(context);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean isFinished(Object scroller) {
            return ((Scroller) scroller).isFinished();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getCurrX(Object scroller) {
            return ((Scroller) scroller).getCurrX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getCurrY(Object scroller) {
            return ((Scroller) scroller).getCurrY();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final float getCurrVelocity(Object scroller) {
            return 0.0f;
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean computeScrollOffset(Object scroller) {
            return ((Scroller) scroller).computeScrollOffset();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void startScroll$364c3051(Object scroller, int startX, int startY, int dy) {
            ((Scroller) scroller).startScroll(startX, startY, 0, dy);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void startScroll(Object scroller, int startX, int startY, int dx, int dy, int duration) {
            ((Scroller) scroller).startScroll(startX, startY, dx, dy, duration);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void fling$f2fc891(Object scroller, int velX, int velY, int minX, int maxX) {
            ((Scroller) scroller).fling(0, 0, velX, velY, minX, maxX, Integers.STATUS_SUCCESS, Integer.MAX_VALUE);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void fling$26e48b1(Object scroller, int startX, int startY, int velY, int maxY, int overY) {
            ((Scroller) scroller).fling(startX, startY, 0, velY, 0, 0, 0, maxY);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void abortAnimation(Object scroller) {
            ((Scroller) scroller).abortAnimation();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getFinalX(Object scroller) {
            return ((Scroller) scroller).getFinalX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getFinalY(Object scroller) {
            return ((Scroller) scroller).getFinalY();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean springBack$2bf03f8b(Object scroller, int startX, int startY, int maxY) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplGingerbread implements ScrollerCompatImpl {
        ScrollerCompatImplGingerbread() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public float getCurrVelocity(Object scroller) {
            return 0.0f;
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void startScroll(Object scroller, int startX, int startY, int dx, int dy, int duration) {
            ((OverScroller) scroller).startScroll(startX, startY, dx, dy, duration);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void fling$f2fc891(Object scroller, int velX, int velY, int minX, int maxX) {
            ((OverScroller) scroller).fling(0, 0, velX, velY, minX, maxX, Integers.STATUS_SUCCESS, Integer.MAX_VALUE);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void fling$26e48b1(Object scroller, int startX, int startY, int velY, int maxY, int overY) {
            ((OverScroller) scroller).fling(startX, startY, 0, velY, 0, 0, 0, maxY, 0, overY);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean springBack$2bf03f8b(Object scroller, int startX, int startY, int maxY) {
            return ((OverScroller) scroller).springBack(startX, startY, 0, 0, 0, maxY);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final Object createScroller(Context context, Interpolator interpolator) {
            return interpolator != null ? new OverScroller(context, interpolator) : new OverScroller(context);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean isFinished(Object scroller) {
            return ((OverScroller) scroller).isFinished();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getCurrX(Object scroller) {
            return ((OverScroller) scroller).getCurrX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getCurrY(Object scroller) {
            return ((OverScroller) scroller).getCurrY();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final boolean computeScrollOffset(Object scroller) {
            return ((OverScroller) scroller).computeScrollOffset();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void startScroll$364c3051(Object scroller, int startX, int startY, int dy) {
            ((OverScroller) scroller).startScroll(startX, startY, 0, dy);
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final void abortAnimation(Object scroller) {
            ((OverScroller) scroller).abortAnimation();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getFinalX(Object scroller) {
            return ((OverScroller) scroller).getFinalX();
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final int getFinalY(Object scroller) {
            return ((OverScroller) scroller).getFinalY();
        }
    }

    /* loaded from: classes.dex */
    static class ScrollerCompatImplIcs extends ScrollerCompatImplGingerbread {
        ScrollerCompatImplIcs() {
        }

        @Override // android.support.v4.widget.ScrollerCompat.ScrollerCompatImplGingerbread, android.support.v4.widget.ScrollerCompat.ScrollerCompatImpl
        public final float getCurrVelocity(Object scroller) {
            return ((OverScroller) scroller).getCurrVelocity();
        }
    }

    public static ScrollerCompat create(Context context, Interpolator interpolator) {
        return new ScrollerCompat(Build.VERSION.SDK_INT, context, interpolator);
    }

    private ScrollerCompat(int apiVersion, Context context, Interpolator interpolator) {
        if (apiVersion >= 14) {
            this.mImpl = new ScrollerCompatImplIcs();
        } else if (apiVersion >= 9) {
            this.mImpl = new ScrollerCompatImplGingerbread();
        } else {
            this.mImpl = new ScrollerCompatImplBase();
        }
        this.mScroller = this.mImpl.createScroller(context, interpolator);
    }

    public final boolean isFinished() {
        return this.mImpl.isFinished(this.mScroller);
    }

    public final int getCurrX() {
        return this.mImpl.getCurrX(this.mScroller);
    }

    public final int getCurrY() {
        return this.mImpl.getCurrY(this.mScroller);
    }

    public final int getFinalX() {
        return this.mImpl.getFinalX(this.mScroller);
    }

    public final int getFinalY() {
        return this.mImpl.getFinalY(this.mScroller);
    }

    public final float getCurrVelocity() {
        return this.mImpl.getCurrVelocity(this.mScroller);
    }

    public final boolean computeScrollOffset() {
        return this.mImpl.computeScrollOffset(this.mScroller);
    }

    public final void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mImpl.startScroll(this.mScroller, startX, startY, dx, dy, duration);
    }

    public final void fling$69c647f5(int velocityX, int velocityY, int minX, int maxX) {
        this.mImpl.fling$f2fc891(this.mScroller, velocityX, velocityY, minX, maxX);
    }

    public final boolean springBack$6046c8d9(int startX, int startY, int maxY) {
        return this.mImpl.springBack$2bf03f8b(this.mScroller, startX, startY, maxY);
    }

    public final void abortAnimation() {
        this.mImpl.abortAnimation(this.mScroller);
    }
}
