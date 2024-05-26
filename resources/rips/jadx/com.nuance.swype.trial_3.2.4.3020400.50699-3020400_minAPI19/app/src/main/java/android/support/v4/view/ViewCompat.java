package android.support.v4.view;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class ViewCompat {
    static final ViewCompatImpl IMPL;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ViewCompatImpl {
        ViewPropertyAnimatorCompat animate(View view);

        boolean canScrollHorizontally(View view, int i);

        boolean canScrollVertically(View view, int i);

        WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat);

        float getAlpha(View view);

        ColorStateList getBackgroundTintList(View view);

        PorterDuff.Mode getBackgroundTintMode(View view);

        float getElevation(View view);

        boolean getFitsSystemWindows(View view);

        int getImportantForAccessibility(View view);

        int getLayerType(View view);

        int getLayoutDirection(View view);

        Matrix getMatrix(View view);

        int getMeasuredState(View view);

        int getMeasuredWidthAndState(View view);

        int getMinimumHeight(View view);

        int getMinimumWidth(View view);

        int getOverScrollMode(View view);

        ViewParent getParentForAccessibility(View view);

        float getScaleX(View view);

        float getTranslationX(View view);

        float getTranslationY(View view);

        int getWindowSystemUiVisibility(View view);

        boolean hasAccessibilityDelegate(View view);

        boolean hasOverlappingRendering(View view);

        boolean hasTransientState(View view);

        boolean isAttachedToWindow(View view);

        boolean isLaidOut(View view);

        boolean isNestedScrollingEnabled(View view);

        boolean isOpaque(View view);

        void jumpDrawablesToCurrentState(View view);

        void offsetLeftAndRight(View view, int i);

        void offsetTopAndBottom(View view, int i);

        WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat);

        void postInvalidateOnAnimation(View view);

        void postInvalidateOnAnimation(View view, int i, int i2, int i3, int i4);

        void postOnAnimation(View view, Runnable runnable);

        void postOnAnimationDelayed(View view, Runnable runnable, long j);

        void requestApplyInsets(View view);

        int resolveSizeAndState(int i, int i2, int i3);

        void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat);

        void setActivated(View view, boolean z);

        void setAlpha(View view, float f);

        void setBackgroundTintList(View view, ColorStateList colorStateList);

        void setBackgroundTintMode(View view, PorterDuff.Mode mode);

        void setChildrenDrawingOrderEnabled$4d3af60(ViewGroup viewGroup);

        void setElevation(View view, float f);

        void setImportantForAccessibility(View view, int i);

        void setLayerPaint(View view, Paint paint);

        void setLayerType(View view, int i, Paint paint);

        void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener);

        void setSaveFromParentEnabled$53599cc9(View view);

        void setScaleX(View view, float f);

        void setScaleY(View view, float f);

        void setTranslationX(View view, float f);

        void setTranslationY(View view, float f);

        void stopNestedScroll(View view);
    }

    /* loaded from: classes.dex */
    static class BaseViewCompatImpl implements ViewCompatImpl {
        WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;

        BaseViewCompatImpl() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:9:0x001e A[ORIG_RETURN, RETURN] */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean canScrollHorizontally(android.view.View r6, int r7) {
            /*
                r5 = this;
                r0 = 1
                r1 = 0
                boolean r2 = r6 instanceof android.support.v4.view.ScrollingView
                if (r2 == 0) goto L29
                android.support.v4.view.ScrollingView r6 = (android.support.v4.view.ScrollingView) r6
                int r2 = r6.computeHorizontalScrollOffset()
                int r3 = r6.computeHorizontalScrollRange()
                int r4 = r6.computeHorizontalScrollExtent()
                int r3 = r3 - r4
                if (r3 == 0) goto L27
                if (r7 >= 0) goto L21
                if (r2 <= 0) goto L1f
                r2 = r0
            L1c:
                if (r2 == 0) goto L29
            L1e:
                return r0
            L1f:
                r2 = r1
                goto L1c
            L21:
                int r3 = r3 + (-1)
                if (r2 >= r3) goto L27
                r2 = r0
                goto L1c
            L27:
                r2 = r1
                goto L1c
            L29:
                r0 = r1
                goto L1e
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.ViewCompat.BaseViewCompatImpl.canScrollHorizontally(android.view.View, int):boolean");
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:9:0x001e A[ORIG_RETURN, RETURN] */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean canScrollVertically(android.view.View r6, int r7) {
            /*
                r5 = this;
                r0 = 1
                r1 = 0
                boolean r2 = r6 instanceof android.support.v4.view.ScrollingView
                if (r2 == 0) goto L29
                android.support.v4.view.ScrollingView r6 = (android.support.v4.view.ScrollingView) r6
                int r2 = r6.computeVerticalScrollOffset()
                int r3 = r6.computeVerticalScrollRange()
                int r4 = r6.computeVerticalScrollExtent()
                int r3 = r3 - r4
                if (r3 == 0) goto L27
                if (r7 >= 0) goto L21
                if (r2 <= 0) goto L1f
                r2 = r0
            L1c:
                if (r2 == 0) goto L29
            L1e:
                return r0
            L1f:
                r2 = r1
                goto L1c
            L21:
                int r3 = r3 + (-1)
                if (r2 >= r3) goto L27
                r2 = r0
                goto L1c
            L27:
                r2 = r1
                goto L1c
            L29:
                r0 = r1
                goto L1e
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.ViewCompat.BaseViewCompatImpl.canScrollVertically(android.view.View, int):boolean");
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getOverScrollMode(View v) {
            return 2;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasAccessibilityDelegate(View v) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasTransientState(View view) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view) {
            view.invalidate();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view, int left, int top, int right, int bottom) {
            view.invalidate(left, top, right, bottom);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimation(View view, Runnable action) {
            view.postDelayed(action, getFrameTime());
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postDelayed(action, getFrameTime() + delayMillis);
        }

        long getFrameTime() {
            return 10L;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getImportantForAccessibility(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setImportantForAccessibility(View view, int mode) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getAlpha(View view) {
            return 1.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerType(View view, int layerType, Paint paint) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayerType(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerPaint(View view, Paint p) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayoutDirection(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewParent getParentForAccessibility(View view) {
            return view.getParent();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isOpaque(View view) {
            Drawable bg = view.getBackground();
            return bg != null && bg.getOpacity() == -1;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
            return View.resolveSize(size, measureSpec);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredWidthAndState(View view) {
            return view.getMeasuredWidth();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredState(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasOverlappingRendering(View view) {
            return true;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getScaleX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public Matrix getMatrix(View view) {
            return null;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumWidth(View view) {
            return ViewCompatBase.getMinimumWidth(view);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumHeight(View view) {
            return ViewCompatBase.getMinimumHeight(view);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewPropertyAnimatorCompat animate(View view) {
            return new ViewPropertyAnimatorCompat(view);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationX(View view, float value) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationY(View view, float value) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAlpha(View view, float value) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleX(View view, float value) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleY(View view, float value) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getWindowSystemUiVisibility(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void requestApplyInsets(View view) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setElevation(View view, float elevation) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getElevation(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setChildrenDrawingOrderEnabled$4d3af60(ViewGroup viewGroup) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean getFitsSystemWindows(View view) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void jumpDrawablesToCurrentState(View view) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener listener) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return insets;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public WindowInsetsCompat dispatchApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return insets;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setSaveFromParentEnabled$53599cc9(View v) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setActivated(View view, boolean activated) {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isNestedScrollingEnabled(View view) {
            if (view instanceof NestedScrollingChild) {
                return ((NestedScrollingChild) view).isNestedScrollingEnabled();
            }
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void stopNestedScroll(View view) {
            if (view instanceof NestedScrollingChild) {
                ((NestedScrollingChild) view).stopNestedScroll();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public ColorStateList getBackgroundTintList(View view) {
            if (view instanceof TintableBackgroundView) {
                return ((TintableBackgroundView) view).getSupportBackgroundTintList();
            }
            return null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setBackgroundTintList(View view, ColorStateList tintList) {
            if (!(view instanceof TintableBackgroundView)) {
                return;
            }
            ((TintableBackgroundView) view).setSupportBackgroundTintList(tintList);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setBackgroundTintMode(View view, PorterDuff.Mode mode) {
            if (!(view instanceof TintableBackgroundView)) {
                return;
            }
            ((TintableBackgroundView) view).setSupportBackgroundTintMode(mode);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public PorterDuff.Mode getBackgroundTintMode(View view) {
            if (view instanceof TintableBackgroundView) {
                return ((TintableBackgroundView) view).getSupportBackgroundTintMode();
            }
            return null;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isLaidOut(View view) {
            return view.getWidth() > 0 && view.getHeight() > 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isAttachedToWindow(View view) {
            return view.getWindowToken() != null;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetLeftAndRight(View view, int offset) {
            int left = view.getLeft();
            view.offsetLeftAndRight(offset);
            if (offset != 0) {
                Object parent = view.getParent();
                if (parent instanceof View) {
                    int abs = Math.abs(offset);
                    ((View) parent).invalidate(left - abs, view.getTop(), left + view.getWidth() + abs, view.getBottom());
                } else {
                    view.invalidate();
                }
            }
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetTopAndBottom(View view, int offset) {
            int top = view.getTop();
            view.offsetTopAndBottom(offset);
            if (offset != 0) {
                Object parent = view.getParent();
                if (parent instanceof View) {
                    int abs = Math.abs(offset);
                    ((View) parent).invalidate(view.getLeft(), top - abs, view.getRight(), top + view.getHeight() + abs);
                } else {
                    view.invalidate();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    static class EclairMr1ViewCompatImpl extends BaseViewCompatImpl {
        EclairMr1ViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean isOpaque(View view) {
            return view.isOpaque();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setChildrenDrawingOrderEnabled$4d3af60(ViewGroup viewGroup) {
            if (ViewCompatEclairMr1.sChildrenDrawingOrderMethod == null) {
                try {
                    ViewCompatEclairMr1.sChildrenDrawingOrderMethod = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", Boolean.TYPE);
                } catch (NoSuchMethodException e) {
                    Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", e);
                }
                ViewCompatEclairMr1.sChildrenDrawingOrderMethod.setAccessible(true);
            }
            try {
                ViewCompatEclairMr1.sChildrenDrawingOrderMethod.invoke(viewGroup, true);
            } catch (IllegalAccessException e2) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e2);
            } catch (IllegalArgumentException e3) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e3);
            } catch (InvocationTargetException e4) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e4);
            }
        }
    }

    /* loaded from: classes.dex */
    static class GBViewCompatImpl extends EclairMr1ViewCompatImpl {
        GBViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getOverScrollMode(View v) {
            return v.getOverScrollMode();
        }
    }

    /* loaded from: classes.dex */
    static class HCViewCompatImpl extends GBViewCompatImpl {
        HCViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetLeftAndRight(View view, int offset) {
            ViewCompatHC.offsetLeftAndRight(view, offset);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetTopAndBottom(View view, int offset) {
            ViewCompatHC.offsetTopAndBottom(view, offset);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl
        final long getFrameTime() {
            return ValueAnimator.getFrameDelay();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final float getAlpha(View view) {
            return view.getAlpha();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setLayerType(View view, int layerType, Paint paint) {
            view.setLayerType(layerType, paint);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getLayerType(View view) {
            return view.getLayerType();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerPaint(View view, Paint paint) {
            setLayerType(view, view.getLayerType(), paint);
            view.invalidate();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
            return View.resolveSizeAndState(size, measureSpec, childMeasuredState);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getMeasuredWidthAndState(View view) {
            return view.getMeasuredWidthAndState();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getMeasuredState(View view) {
            return view.getMeasuredState();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final float getTranslationX(View view) {
            return view.getTranslationX();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final float getTranslationY(View view) {
            return view.getTranslationY();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final Matrix getMatrix(View view) {
            return view.getMatrix();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setTranslationX(View view, float value) {
            view.setTranslationX(value);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setTranslationY(View view, float value) {
            view.setTranslationY(value);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setAlpha(View view, float value) {
            view.setAlpha(value);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setScaleX(View view, float value) {
            view.setScaleX(value);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setScaleY(View view, float value) {
            view.setScaleY(value);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final float getScaleX(View view) {
            return view.getScaleX();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void jumpDrawablesToCurrentState(View view) {
            view.jumpDrawablesToCurrentState();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setSaveFromParentEnabled$53599cc9(View view) {
            view.setSaveFromParentEnabled(false);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setActivated(View view, boolean activated) {
            view.setActivated(activated);
        }
    }

    /* loaded from: classes.dex */
    static class ICSViewCompatImpl extends HCViewCompatImpl {
        static boolean accessibilityDelegateCheckFailed = false;
        static Field mAccessibilityDelegateField;

        ICSViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
            v.setAccessibilityDelegate((View.AccessibilityDelegate) (delegate == null ? null : delegate.mBridge));
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean hasAccessibilityDelegate(View v) {
            if (accessibilityDelegateCheckFailed) {
                return false;
            }
            if (mAccessibilityDelegateField == null) {
                try {
                    Field declaredField = View.class.getDeclaredField("mAccessibilityDelegate");
                    mAccessibilityDelegateField = declaredField;
                    declaredField.setAccessible(true);
                } catch (Throwable th) {
                    accessibilityDelegateCheckFailed = true;
                    return false;
                }
            }
            try {
                return mAccessibilityDelegateField.get(v) != null;
            } catch (Throwable th2) {
                accessibilityDelegateCheckFailed = true;
                return false;
            }
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final ViewPropertyAnimatorCompat animate(View view) {
            if (this.mViewPropertyAnimatorCompatMap == null) {
                this.mViewPropertyAnimatorCompatMap = new WeakHashMap<>();
            }
            ViewPropertyAnimatorCompat vpa = this.mViewPropertyAnimatorCompatMap.get(view);
            if (vpa == null) {
                ViewPropertyAnimatorCompat vpa2 = new ViewPropertyAnimatorCompat(view);
                this.mViewPropertyAnimatorCompatMap.put(view, vpa2);
                return vpa2;
            }
            return vpa;
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean canScrollHorizontally(View v, int direction) {
            return v.canScrollHorizontally(direction);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean canScrollVertically(View v, int direction) {
            return v.canScrollVertically(direction);
        }
    }

    /* loaded from: classes.dex */
    static class ICSMr1ViewCompatImpl extends ICSViewCompatImpl {
        ICSMr1ViewCompatImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class JBViewCompatImpl extends ICSMr1ViewCompatImpl {
        JBViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setImportantForAccessibility(View view, int mode) {
            if (mode == 4) {
                mode = 2;
            }
            view.setImportantForAccessibility(mode);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean hasTransientState(View view) {
            return view.hasTransientState();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void postInvalidateOnAnimation(View view) {
            view.postInvalidateOnAnimation();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void postInvalidateOnAnimation(View view, int left, int top, int right, int bottom) {
            view.postInvalidate(left, top, right, bottom);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void postOnAnimation(View view, Runnable action) {
            view.postOnAnimation(action);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postOnAnimationDelayed(action, delayMillis);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getImportantForAccessibility(View view) {
            return view.getImportantForAccessibility();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final ViewParent getParentForAccessibility(View view) {
            return view.getParentForAccessibility();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getMinimumWidth(View view) {
            return view.getMinimumWidth();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getMinimumHeight(View view) {
            return view.getMinimumHeight();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void requestApplyInsets(View view) {
            view.requestFitSystemWindows();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean getFitsSystemWindows(View view) {
            return view.getFitsSystemWindows();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean hasOverlappingRendering(View view) {
            return view.hasOverlappingRendering();
        }
    }

    /* loaded from: classes.dex */
    static class JbMr1ViewCompatImpl extends JBViewCompatImpl {
        JbMr1ViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setLayerPaint(View view, Paint paint) {
            view.setLayerPaint(paint);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getLayoutDirection(View view) {
            return view.getLayoutDirection();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final int getWindowSystemUiVisibility(View view) {
            return view.getWindowSystemUiVisibility();
        }
    }

    /* loaded from: classes.dex */
    static class JbMr2ViewCompatImpl extends JbMr1ViewCompatImpl {
        JbMr2ViewCompatImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class KitKatViewCompatImpl extends JbMr2ViewCompatImpl {
        KitKatViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.JBViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setImportantForAccessibility(View view, int mode) {
            view.setImportantForAccessibility(mode);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean isLaidOut(View view) {
            return view.isLaidOut();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean isAttachedToWindow(View view) {
            return view.isAttachedToWindow();
        }
    }

    /* loaded from: classes.dex */
    static class LollipopViewCompatImpl extends KitKatViewCompatImpl {
        LollipopViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.JBViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void requestApplyInsets(View view) {
            view.requestApplyInsets();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setElevation(View view, float elevation) {
            view.setElevation(elevation);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final float getElevation(View view) {
            return view.getElevation();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setOnApplyWindowInsetsListener(View view, final OnApplyWindowInsetsListener listener) {
            if (listener == null) {
                view.setOnApplyWindowInsetsListener(null);
            } else {
                view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: android.support.v4.view.ViewCompatLollipop.1
                    public AnonymousClass1() {
                    }

                    @Override // android.view.View.OnApplyWindowInsetsListener
                    public final WindowInsets onApplyWindowInsets(View view2, WindowInsets windowInsets) {
                        WindowInsetsCompatApi21 insets = new WindowInsetsCompatApi21(windowInsets);
                        return ((WindowInsetsCompatApi21) OnApplyWindowInsetsListener.this.onApplyWindowInsets(view2, insets)).mSource;
                    }
                });
            }
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final boolean isNestedScrollingEnabled(View view) {
            return view.isNestedScrollingEnabled();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void stopNestedScroll(View view) {
            view.stopNestedScroll();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final ColorStateList getBackgroundTintList(View view) {
            return view.getBackgroundTintList();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setBackgroundTintList(View view, ColorStateList tintList) {
            view.setBackgroundTintList(tintList);
            if (Build.VERSION.SDK_INT != 21) {
                return;
            }
            Drawable background = view.getBackground();
            boolean z = (view.getBackgroundTintList() == null || view.getBackgroundTintMode() == null) ? false : true;
            if (background == null || !z) {
                return;
            }
            if (background.isStateful()) {
                background.setState(view.getDrawableState());
            }
            view.setBackground(background);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void setBackgroundTintMode(View view, PorterDuff.Mode mode) {
            view.setBackgroundTintMode(mode);
            if (Build.VERSION.SDK_INT != 21) {
                return;
            }
            Drawable background = view.getBackground();
            boolean z = (view.getBackgroundTintList() == null || view.getBackgroundTintMode() == null) ? false : true;
            if (background == null || !z) {
                return;
            }
            if (background.isStateful()) {
                background.setState(view.getDrawableState());
            }
            view.setBackground(background);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final PorterDuff.Mode getBackgroundTintMode(View view) {
            return view.getBackgroundTintMode();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            WindowInsets windowInsets;
            WindowInsets onApplyWindowInsets;
            if (!(insets instanceof WindowInsetsCompatApi21) || (onApplyWindowInsets = v.onApplyWindowInsets((windowInsets = ((WindowInsetsCompatApi21) insets).mSource))) == windowInsets) {
                return insets;
            }
            return new WindowInsetsCompatApi21(onApplyWindowInsets);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final WindowInsetsCompat dispatchApplyWindowInsets(View v, WindowInsetsCompat insets) {
            WindowInsets windowInsets;
            WindowInsets dispatchApplyWindowInsets;
            if (!(insets instanceof WindowInsetsCompatApi21) || (dispatchApplyWindowInsets = v.dispatchApplyWindowInsets((windowInsets = ((WindowInsetsCompatApi21) insets).mSource))) == windowInsets) {
                return insets;
            }
            return new WindowInsetsCompatApi21(dispatchApplyWindowInsets);
        }

        @Override // android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetLeftAndRight(View view, int offset) {
            boolean z;
            Rect emptyTempRect = ViewCompatLollipop.getEmptyTempRect();
            Object parent = view.getParent();
            if (parent instanceof View) {
                View view2 = (View) parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                z = !emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            } else {
                z = false;
            }
            ViewCompatHC.offsetLeftAndRight(view, offset);
            if (!z || !emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                return;
            }
            ((View) parent).invalidate(emptyTempRect);
        }

        @Override // android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void offsetTopAndBottom(View view, int offset) {
            boolean z;
            Rect emptyTempRect = ViewCompatLollipop.getEmptyTempRect();
            Object parent = view.getParent();
            if (parent instanceof View) {
                View view2 = (View) parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                z = !emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            } else {
                z = false;
            }
            ViewCompatHC.offsetTopAndBottom(view, offset);
            if (!z || !emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                return;
            }
            ((View) parent).invalidate(emptyTempRect);
        }
    }

    /* loaded from: classes.dex */
    static class MarshmallowViewCompatImpl extends LollipopViewCompatImpl {
        MarshmallowViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.LollipopViewCompatImpl, android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void offsetLeftAndRight(View view, int offset) {
            view.offsetLeftAndRight(offset);
        }

        @Override // android.support.v4.view.ViewCompat.LollipopViewCompatImpl, android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public final void offsetTopAndBottom(View view, int offset) {
            view.offsetTopAndBottom(offset);
        }
    }

    /* loaded from: classes.dex */
    static class Api24ViewCompatImpl extends MarshmallowViewCompatImpl {
        Api24ViewCompatImpl() {
        }
    }

    static {
        int version = Build.VERSION.SDK_INT;
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24ViewCompatImpl();
            return;
        }
        if (version >= 23) {
            IMPL = new MarshmallowViewCompatImpl();
            return;
        }
        if (version >= 21) {
            IMPL = new LollipopViewCompatImpl();
            return;
        }
        if (version >= 19) {
            IMPL = new KitKatViewCompatImpl();
            return;
        }
        if (version >= 18) {
            IMPL = new JbMr2ViewCompatImpl();
            return;
        }
        if (version >= 17) {
            IMPL = new JbMr1ViewCompatImpl();
            return;
        }
        if (version >= 16) {
            IMPL = new JBViewCompatImpl();
            return;
        }
        if (version >= 15) {
            IMPL = new ICSMr1ViewCompatImpl();
            return;
        }
        if (version >= 14) {
            IMPL = new ICSViewCompatImpl();
            return;
        }
        if (version >= 11) {
            IMPL = new HCViewCompatImpl();
            return;
        }
        if (version >= 9) {
            IMPL = new GBViewCompatImpl();
        } else if (version >= 7) {
            IMPL = new EclairMr1ViewCompatImpl();
        } else {
            IMPL = new BaseViewCompatImpl();
        }
    }

    public static boolean canScrollHorizontally(View v, int direction) {
        return IMPL.canScrollHorizontally(v, direction);
    }

    public static boolean canScrollVertically(View v, int direction) {
        return IMPL.canScrollVertically(v, direction);
    }

    public static int getOverScrollMode(View v) {
        return IMPL.getOverScrollMode(v);
    }

    public static void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
        IMPL.setAccessibilityDelegate(v, delegate);
    }

    public static boolean hasAccessibilityDelegate(View v) {
        return IMPL.hasAccessibilityDelegate(v);
    }

    public static boolean hasTransientState(View view) {
        return IMPL.hasTransientState(view);
    }

    public static void postInvalidateOnAnimation(View view) {
        IMPL.postInvalidateOnAnimation(view);
    }

    public static void postInvalidateOnAnimation(View view, int left, int top, int right, int bottom) {
        IMPL.postInvalidateOnAnimation(view, left, top, right, bottom);
    }

    public static void postOnAnimation(View view, Runnable action) {
        IMPL.postOnAnimation(view, action);
    }

    public static void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
        IMPL.postOnAnimationDelayed(view, action, delayMillis);
    }

    public static int getImportantForAccessibility(View view) {
        return IMPL.getImportantForAccessibility(view);
    }

    public static void setImportantForAccessibility(View view, int mode) {
        IMPL.setImportantForAccessibility(view, mode);
    }

    public static float getAlpha(View view) {
        return IMPL.getAlpha(view);
    }

    public static void setLayerType(View view, int layerType, Paint paint) {
        IMPL.setLayerType(view, layerType, paint);
    }

    public static int getLayerType(View view) {
        return IMPL.getLayerType(view);
    }

    public static void setLayerPaint(View view, Paint paint) {
        IMPL.setLayerPaint(view, paint);
    }

    public static int getLayoutDirection(View view) {
        return IMPL.getLayoutDirection(view);
    }

    public static ViewParent getParentForAccessibility(View view) {
        return IMPL.getParentForAccessibility(view);
    }

    public static boolean isOpaque(View view) {
        return IMPL.isOpaque(view);
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        return IMPL.resolveSizeAndState(size, measureSpec, childMeasuredState);
    }

    public static int getMeasuredWidthAndState(View view) {
        return IMPL.getMeasuredWidthAndState(view);
    }

    public static int getMeasuredState(View view) {
        return IMPL.getMeasuredState(view);
    }

    public static float getTranslationX(View view) {
        return IMPL.getTranslationX(view);
    }

    public static float getTranslationY(View view) {
        return IMPL.getTranslationY(view);
    }

    public static Matrix getMatrix(View view) {
        return IMPL.getMatrix(view);
    }

    public static int getMinimumWidth(View view) {
        return IMPL.getMinimumWidth(view);
    }

    public static int getMinimumHeight(View view) {
        return IMPL.getMinimumHeight(view);
    }

    public static ViewPropertyAnimatorCompat animate(View view) {
        return IMPL.animate(view);
    }

    public static void setTranslationX(View view, float value) {
        IMPL.setTranslationX(view, value);
    }

    public static void setTranslationY(View view, float value) {
        IMPL.setTranslationY(view, value);
    }

    public static void setAlpha(View view, float value) {
        IMPL.setAlpha(view, value);
    }

    public static void setScaleX(View view, float value) {
        IMPL.setScaleX(view, value);
    }

    public static void setScaleY(View view, float value) {
        IMPL.setScaleY(view, value);
    }

    public static float getScaleX(View view) {
        return IMPL.getScaleX(view);
    }

    public static void setElevation(View view, float elevation) {
        IMPL.setElevation(view, elevation);
    }

    public static float getElevation(View view) {
        return IMPL.getElevation(view);
    }

    public static int getWindowSystemUiVisibility(View view) {
        return IMPL.getWindowSystemUiVisibility(view);
    }

    public static void requestApplyInsets(View view) {
        IMPL.requestApplyInsets(view);
    }

    public static boolean getFitsSystemWindows(View v) {
        return IMPL.getFitsSystemWindows(v);
    }

    public static void jumpDrawablesToCurrentState(View v) {
        IMPL.jumpDrawablesToCurrentState(v);
    }

    public static void setOnApplyWindowInsetsListener(View v, OnApplyWindowInsetsListener listener) {
        IMPL.setOnApplyWindowInsetsListener(v, listener);
    }

    public static WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
        return IMPL.onApplyWindowInsets(view, insets);
    }

    public static WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat insets) {
        return IMPL.dispatchApplyWindowInsets(view, insets);
    }

    public static void setActivated(View view, boolean activated) {
        IMPL.setActivated(view, activated);
    }

    public static boolean hasOverlappingRendering(View view) {
        return IMPL.hasOverlappingRendering(view);
    }

    public static ColorStateList getBackgroundTintList(View view) {
        return IMPL.getBackgroundTintList(view);
    }

    public static void setBackgroundTintList(View view, ColorStateList tintList) {
        IMPL.setBackgroundTintList(view, tintList);
    }

    public static PorterDuff.Mode getBackgroundTintMode(View view) {
        return IMPL.getBackgroundTintMode(view);
    }

    public static void setBackgroundTintMode(View view, PorterDuff.Mode mode) {
        IMPL.setBackgroundTintMode(view, mode);
    }

    public static boolean isNestedScrollingEnabled(View view) {
        return IMPL.isNestedScrollingEnabled(view);
    }

    public static void stopNestedScroll(View view) {
        IMPL.stopNestedScroll(view);
    }

    public static boolean isLaidOut(View view) {
        return IMPL.isLaidOut(view);
    }

    public static void offsetTopAndBottom(View view, int offset) {
        IMPL.offsetTopAndBottom(view, offset);
    }

    public static void offsetLeftAndRight(View view, int offset) {
        IMPL.offsetLeftAndRight(view, offset);
    }

    public static boolean isAttachedToWindow(View view) {
        return IMPL.isAttachedToWindow(view);
    }
}
