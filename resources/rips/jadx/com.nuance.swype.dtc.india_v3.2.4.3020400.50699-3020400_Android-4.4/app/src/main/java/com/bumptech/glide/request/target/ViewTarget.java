package com.bumptech.glide.request.target;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.bumptech.glide.request.Request;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ViewTarget<T extends View, Z> extends BaseTarget<Z> {
    private static boolean isTagUsedAtLeastOnce = false;
    private static Integer tagId = null;
    private final SizeDeterminer sizeDeterminer;
    protected final T view;

    public ViewTarget(T view) {
        if (view == null) {
            throw new NullPointerException("View must not be null!");
        }
        this.view = view;
        this.sizeDeterminer = new SizeDeterminer(view);
    }

    public final T getView() {
        return this.view;
    }

    @Override // com.bumptech.glide.request.target.Target
    public final void getSize(SizeReadyCallback cb) {
        SizeDeterminer sizeDeterminer = this.sizeDeterminer;
        int viewWidthOrParam = sizeDeterminer.getViewWidthOrParam();
        int viewHeightOrParam = sizeDeterminer.getViewHeightOrParam();
        if (SizeDeterminer.isSizeValid(viewWidthOrParam) && SizeDeterminer.isSizeValid(viewHeightOrParam)) {
            cb.onSizeReady(viewWidthOrParam, viewHeightOrParam);
            return;
        }
        if (!sizeDeterminer.cbs.contains(cb)) {
            sizeDeterminer.cbs.add(cb);
        }
        if (sizeDeterminer.layoutListener == null) {
            ViewTreeObserver viewTreeObserver = sizeDeterminer.view.getViewTreeObserver();
            sizeDeterminer.layoutListener = new SizeDeterminer.SizeDeterminerLayoutListener(sizeDeterminer);
            viewTreeObserver.addOnPreDrawListener(sizeDeterminer.layoutListener);
        }
    }

    public String toString() {
        return "Target for: " + this.view;
    }

    /* loaded from: classes.dex */
    private static class SizeDeterminer {
        final List<SizeReadyCallback> cbs = new ArrayList();
        private Point displayDimens;
        SizeDeterminerLayoutListener layoutListener;
        final View view;

        public SizeDeterminer(View view) {
            this.view = view;
        }

        final int getViewHeightOrParam() {
            ViewGroup.LayoutParams layoutParams = this.view.getLayoutParams();
            if (isSizeValid(this.view.getHeight())) {
                return this.view.getHeight();
            }
            if (layoutParams != null) {
                return getSizeForParam(layoutParams.height, true);
            }
            return 0;
        }

        final int getViewWidthOrParam() {
            ViewGroup.LayoutParams layoutParams = this.view.getLayoutParams();
            if (isSizeValid(this.view.getWidth())) {
                return this.view.getWidth();
            }
            if (layoutParams != null) {
                return getSizeForParam(layoutParams.width, false);
            }
            return 0;
        }

        private int getSizeForParam(int param, boolean isHeight) {
            if (param != -2) {
                return param;
            }
            if (this.displayDimens == null) {
                Display defaultDisplay = ((WindowManager) this.view.getContext().getSystemService("window")).getDefaultDisplay();
                if (Build.VERSION.SDK_INT >= 13) {
                    this.displayDimens = new Point();
                    defaultDisplay.getSize(this.displayDimens);
                } else {
                    this.displayDimens = new Point(defaultDisplay.getWidth(), defaultDisplay.getHeight());
                }
            }
            Point displayDimens = this.displayDimens;
            return isHeight ? displayDimens.y : displayDimens.x;
        }

        static boolean isSizeValid(int size) {
            return size > 0 || size == -2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class SizeDeterminerLayoutListener implements ViewTreeObserver.OnPreDrawListener {
            private final WeakReference<SizeDeterminer> sizeDeterminerRef;

            public SizeDeterminerLayoutListener(SizeDeterminer sizeDeterminer) {
                this.sizeDeterminerRef = new WeakReference<>(sizeDeterminer);
            }

            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                if (Log.isLoggable("ViewTarget", 2)) {
                    Log.v("ViewTarget", "OnGlobalLayoutListener called listener=" + this);
                }
                SizeDeterminer sizeDeterminer = this.sizeDeterminerRef.get();
                if (sizeDeterminer != null) {
                    SizeDeterminer.access$000(sizeDeterminer);
                    return true;
                }
                return true;
            }
        }

        static /* synthetic */ void access$000(SizeDeterminer x0) {
            if (!x0.cbs.isEmpty()) {
                int viewWidthOrParam = x0.getViewWidthOrParam();
                int viewHeightOrParam = x0.getViewHeightOrParam();
                if (!isSizeValid(viewWidthOrParam) || !isSizeValid(viewHeightOrParam)) {
                    return;
                }
                Iterator<SizeReadyCallback> it = x0.cbs.iterator();
                while (it.hasNext()) {
                    it.next().onSizeReady(viewWidthOrParam, viewHeightOrParam);
                }
                x0.cbs.clear();
                ViewTreeObserver viewTreeObserver = x0.view.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(x0.layoutListener);
                }
                x0.layoutListener = null;
            }
        }
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public final void setRequest(Request request) {
        if (tagId == null) {
            isTagUsedAtLeastOnce = true;
            this.view.setTag(request);
        } else {
            this.view.setTag(tagId.intValue(), request);
        }
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public final Request getRequest() {
        Object tag;
        if (tagId == null) {
            tag = this.view.getTag();
        } else {
            tag = this.view.getTag(tagId.intValue());
        }
        if (tag == null) {
            return null;
        }
        if (tag instanceof Request) {
            Request request = (Request) tag;
            return request;
        }
        throw new IllegalArgumentException("You must not call setTag() on a view Glide is targeting");
    }
}
