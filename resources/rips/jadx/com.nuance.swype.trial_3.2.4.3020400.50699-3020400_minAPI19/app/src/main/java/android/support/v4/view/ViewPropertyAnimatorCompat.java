package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class ViewPropertyAnimatorCompat {
    static final ViewPropertyAnimatorCompatImpl IMPL;
    private WeakReference<View> mView;
    private Runnable mStartAction = null;
    private Runnable mEndAction = null;
    private int mOldLayerType = -1;

    /* loaded from: classes.dex */
    interface ViewPropertyAnimatorCompatImpl {
        void alpha(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);

        void cancel(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view);

        void setDuration$65a8a4c6(View view, long j);

        void setListener(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener);

        void start(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view);

        void translationX(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);

        void translationY(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);
    }

    static /* synthetic */ Runnable access$002$1d924e4e(ViewPropertyAnimatorCompat x0) {
        x0.mEndAction = null;
        return null;
    }

    static /* synthetic */ Runnable access$102$1d924e4e(ViewPropertyAnimatorCompat x0) {
        x0.mStartAction = null;
        return null;
    }

    static /* synthetic */ int access$402$3efd0312(ViewPropertyAnimatorCompat x0) {
        x0.mOldLayerType = -1;
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewPropertyAnimatorCompat(View view) {
        this.mView = new WeakReference<>(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class BaseViewPropertyAnimatorCompatImpl implements ViewPropertyAnimatorCompatImpl {
        WeakHashMap<View, Runnable> mStarterMap = null;

        BaseViewPropertyAnimatorCompatImpl() {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void setDuration$65a8a4c6(View view, long value) {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void alpha(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void translationX(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void translationY(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void cancel(ViewPropertyAnimatorCompat vpa, View view) {
            postStartMessage(vpa, view);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            view.setTag(2113929216, listener);
        }

        final void startAnimation(ViewPropertyAnimatorCompat vpa, View view) {
            Object listenerTag = view.getTag(2113929216);
            ViewPropertyAnimatorListener listener = null;
            if (listenerTag instanceof ViewPropertyAnimatorListener) {
                listener = (ViewPropertyAnimatorListener) listenerTag;
            }
            Runnable startAction = vpa.mStartAction;
            Runnable endAction = vpa.mEndAction;
            ViewPropertyAnimatorCompat.access$102$1d924e4e(vpa);
            ViewPropertyAnimatorCompat.access$002$1d924e4e(vpa);
            if (startAction != null) {
                startAction.run();
            }
            if (listener != null) {
                listener.onAnimationStart(view);
                listener.onAnimationEnd(view);
            }
            if (endAction != null) {
                endAction.run();
            }
            if (this.mStarterMap != null) {
                this.mStarterMap.remove(view);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Starter implements Runnable {
            WeakReference<View> mViewRef;
            ViewPropertyAnimatorCompat mVpa;

            /* synthetic */ Starter(BaseViewPropertyAnimatorCompatImpl x0, ViewPropertyAnimatorCompat x1, View x2, byte b) {
                this(x1, x2);
            }

            private Starter(ViewPropertyAnimatorCompat vpa, View view) {
                this.mViewRef = new WeakReference<>(view);
                this.mVpa = vpa;
            }

            @Override // java.lang.Runnable
            public final void run() {
                View view = this.mViewRef.get();
                if (view == null) {
                    return;
                }
                BaseViewPropertyAnimatorCompatImpl.this.startAnimation(this.mVpa, view);
            }
        }

        private void postStartMessage(ViewPropertyAnimatorCompat vpa, View view) {
            Runnable starter = null;
            if (this.mStarterMap != null) {
                Runnable starter2 = this.mStarterMap.get(view);
                starter = starter2;
            }
            if (starter == null) {
                starter = new Starter(this, vpa, view, (byte) 0);
                if (this.mStarterMap == null) {
                    this.mStarterMap = new WeakHashMap<>();
                }
                this.mStarterMap.put(view, starter);
            }
            view.removeCallbacks(starter);
            view.post(starter);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void start(ViewPropertyAnimatorCompat vpa, View view) {
            Runnable runnable;
            if (this.mStarterMap != null && (runnable = this.mStarterMap.get(view)) != null) {
                view.removeCallbacks(runnable);
            }
            startAnimation(vpa, view);
        }
    }

    /* loaded from: classes.dex */
    static class ICSViewPropertyAnimatorCompatImpl extends BaseViewPropertyAnimatorCompatImpl {
        WeakHashMap<View, Integer> mLayerMap = null;

        ICSViewPropertyAnimatorCompatImpl() {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            view.setTag(2113929216, listener);
            view.animate().setListener(new AnimatorListenerAdapter() { // from class: android.support.v4.view.ViewPropertyAnimatorCompatICS.1
                final /* synthetic */ View val$view;

                public AnonymousClass1(View view2) {
                    r2 = view2;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public final void onAnimationCancel(Animator animation) {
                    ViewPropertyAnimatorListener.this.onAnimationCancel(r2);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public final void onAnimationEnd(Animator animation) {
                    ViewPropertyAnimatorListener.this.onAnimationEnd(r2);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public final void onAnimationStart(Animator animation) {
                    ViewPropertyAnimatorListener.this.onAnimationStart(r2);
                }
            });
        }

        /* loaded from: classes.dex */
        static class MyVpaListener implements ViewPropertyAnimatorListener {
            boolean mAnimEndCalled;
            ViewPropertyAnimatorCompat mVpa;

            MyVpaListener(ViewPropertyAnimatorCompat vpa) {
                this.mVpa = vpa;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListener
            public final void onAnimationStart(View view) {
                this.mAnimEndCalled = false;
                if (this.mVpa.mOldLayerType >= 0) {
                    ViewCompat.setLayerType(view, 2, null);
                }
                if (this.mVpa.mStartAction != null) {
                    Runnable startAction = this.mVpa.mStartAction;
                    ViewPropertyAnimatorCompat.access$102$1d924e4e(this.mVpa);
                    startAction.run();
                }
                Object listenerTag = view.getTag(2113929216);
                ViewPropertyAnimatorListener listener = null;
                if (listenerTag instanceof ViewPropertyAnimatorListener) {
                    listener = (ViewPropertyAnimatorListener) listenerTag;
                }
                if (listener != null) {
                    listener.onAnimationStart(view);
                }
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListener
            public final void onAnimationEnd(View view) {
                if (this.mVpa.mOldLayerType >= 0) {
                    ViewCompat.setLayerType(view, this.mVpa.mOldLayerType, null);
                    ViewPropertyAnimatorCompat.access$402$3efd0312(this.mVpa);
                }
                if (Build.VERSION.SDK_INT >= 16 || !this.mAnimEndCalled) {
                    if (this.mVpa.mEndAction != null) {
                        Runnable endAction = this.mVpa.mEndAction;
                        ViewPropertyAnimatorCompat.access$002$1d924e4e(this.mVpa);
                        endAction.run();
                    }
                    Object listenerTag = view.getTag(2113929216);
                    ViewPropertyAnimatorListener listener = null;
                    if (listenerTag instanceof ViewPropertyAnimatorListener) {
                        listener = (ViewPropertyAnimatorListener) listenerTag;
                    }
                    if (listener != null) {
                        listener.onAnimationEnd(view);
                    }
                    this.mAnimEndCalled = true;
                }
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListener
            public final void onAnimationCancel(View view) {
                Object listenerTag = view.getTag(2113929216);
                ViewPropertyAnimatorListener listener = null;
                if (listenerTag instanceof ViewPropertyAnimatorListener) {
                    listener = (ViewPropertyAnimatorListener) listenerTag;
                }
                if (listener != null) {
                    listener.onAnimationCancel(view);
                }
            }
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void setDuration$65a8a4c6(View view, long value) {
            view.animate().setDuration(value);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void alpha(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().alpha(value);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void translationX(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().translationX(value);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void translationY(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().translationY(value);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void cancel(ViewPropertyAnimatorCompat vpa, View view) {
            view.animate().cancel();
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void start(ViewPropertyAnimatorCompat vpa, View view) {
            view.animate().start();
        }
    }

    /* loaded from: classes.dex */
    static class JBViewPropertyAnimatorCompatImpl extends ICSViewPropertyAnimatorCompatImpl {
        JBViewPropertyAnimatorCompatImpl() {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorCompat.ICSViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl, android.support.v4.view.ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
        public final void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            if (listener != null) {
                view.animate().setListener(new AnimatorListenerAdapter() { // from class: android.support.v4.view.ViewPropertyAnimatorCompatJB.1
                    final /* synthetic */ View val$view;

                    public AnonymousClass1(View view2) {
                        r2 = view2;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public final void onAnimationCancel(Animator animation) {
                        ViewPropertyAnimatorListener.this.onAnimationCancel(r2);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public final void onAnimationEnd(Animator animation) {
                        ViewPropertyAnimatorListener.this.onAnimationEnd(r2);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public final void onAnimationStart(Animator animation) {
                        ViewPropertyAnimatorListener.this.onAnimationStart(r2);
                    }
                });
            } else {
                view2.animate().setListener(null);
            }
        }
    }

    /* loaded from: classes.dex */
    static class JBMr2ViewPropertyAnimatorCompatImpl extends JBViewPropertyAnimatorCompatImpl {
        JBMr2ViewPropertyAnimatorCompatImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class KitKatViewPropertyAnimatorCompatImpl extends JBMr2ViewPropertyAnimatorCompatImpl {
        KitKatViewPropertyAnimatorCompatImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class LollipopViewPropertyAnimatorCompatImpl extends KitKatViewPropertyAnimatorCompatImpl {
        LollipopViewPropertyAnimatorCompatImpl() {
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopViewPropertyAnimatorCompatImpl();
            return;
        }
        if (version >= 19) {
            IMPL = new KitKatViewPropertyAnimatorCompatImpl();
            return;
        }
        if (version >= 18) {
            IMPL = new JBMr2ViewPropertyAnimatorCompatImpl();
            return;
        }
        if (version >= 16) {
            IMPL = new JBViewPropertyAnimatorCompatImpl();
        } else if (version >= 14) {
            IMPL = new ICSViewPropertyAnimatorCompatImpl();
        } else {
            IMPL = new BaseViewPropertyAnimatorCompatImpl();
        }
    }

    public final ViewPropertyAnimatorCompat setDuration(long value) {
        View view = this.mView.get();
        if (view != null) {
            IMPL.setDuration$65a8a4c6(view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat alpha(float value) {
        View view = this.mView.get();
        if (view != null) {
            IMPL.alpha(this, view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat translationX(float value) {
        View view = this.mView.get();
        if (view != null) {
            IMPL.translationX(this, view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat translationY(float value) {
        View view = this.mView.get();
        if (view != null) {
            IMPL.translationY(this, view, value);
        }
        return this;
    }

    public final void cancel() {
        View view = this.mView.get();
        if (view != null) {
            IMPL.cancel(this, view);
        }
    }

    public final void start() {
        View view = this.mView.get();
        if (view != null) {
            IMPL.start(this, view);
        }
    }

    public final ViewPropertyAnimatorCompat setListener(ViewPropertyAnimatorListener listener) {
        View view = this.mView.get();
        if (view != null) {
            IMPL.setListener(this, view, listener);
        }
        return this;
    }
}
