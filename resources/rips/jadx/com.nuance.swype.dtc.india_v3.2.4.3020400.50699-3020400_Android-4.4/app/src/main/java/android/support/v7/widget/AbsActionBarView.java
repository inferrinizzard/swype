package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.appcompat.R;
import android.support.v7.view.ActionBarPolicy;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
abstract class AbsActionBarView extends ViewGroup {
    protected ActionMenuPresenter mActionMenuPresenter;
    protected int mContentHeight;
    private boolean mEatingHover;
    private boolean mEatingTouch;
    protected ActionMenuView mMenuView;
    protected final Context mPopupContext;
    protected final VisibilityAnimListener mVisAnimListener;
    protected ViewPropertyAnimatorCompat mVisibilityAnim;

    AbsActionBarView(Context context) {
        this(context, null);
    }

    AbsActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mVisAnimListener = new VisibilityAnimListener();
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarPopupTheme, tv, true) && tv.resourceId != 0) {
            this.mPopupContext = new ContextThemeWrapper(context, tv.resourceId);
        } else {
            this.mPopupContext = context;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(newConfig);
        }
        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        setContentHeight(a.getLayoutDimension(R.styleable.ActionBar_height, 0));
        a.recycle();
        if (this.mActionMenuPresenter != null) {
            ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
            if (!actionMenuPresenter.mMaxItemsSet) {
                actionMenuPresenter.mMaxItems = ActionBarPolicy.get(actionMenuPresenter.mContext).getMaxActionButtons();
            }
            if (actionMenuPresenter.mMenu == null) {
                return;
            }
            actionMenuPresenter.mMenu.onItemsChanged(true);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == 0) {
            this.mEatingTouch = false;
        }
        if (!this.mEatingTouch) {
            boolean handled = super.onTouchEvent(ev);
            if (action == 0 && !handled) {
                this.mEatingTouch = true;
            }
        }
        if (action == 1 || action == 3) {
            this.mEatingTouch = false;
        }
        return true;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == 9) {
            this.mEatingHover = false;
        }
        if (!this.mEatingHover) {
            boolean handled = super.onHoverEvent(ev);
            if (action == 9 && !handled) {
                this.mEatingHover = true;
            }
        }
        if (action == 10 || action == 3) {
            this.mEatingHover = false;
        }
        return true;
    }

    public void setContentHeight(int height) {
        this.mContentHeight = height;
        requestLayout();
    }

    public int getContentHeight() {
        return this.mContentHeight;
    }

    public int getAnimatedVisibility() {
        return this.mVisibilityAnim != null ? this.mVisAnimListener.mFinalVisibility : getVisibility();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (visibility != getVisibility()) {
            if (this.mVisibilityAnim != null) {
                this.mVisibilityAnim.cancel();
            }
            super.setVisibility(visibility);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int measureChildView$1bb94239(View child, int availableWidth, int childSpecHeight) {
        child.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integers.STATUS_SUCCESS), childSpecHeight);
        return Math.max(0, (availableWidth - child.getMeasuredWidth()) + 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int next(int x, int val, boolean isRtl) {
        return isRtl ? x - val : x + val;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int positionChild(View child, int x, int y, int contentHeight, boolean reverse) {
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        int childTop = y + ((contentHeight - childHeight) / 2);
        if (reverse) {
            child.layout(x - childWidth, childTop, x, childTop + childHeight);
        } else {
            child.layout(x, childTop, x + childWidth, childTop + childHeight);
        }
        return reverse ? -childWidth : childWidth;
    }

    /* loaded from: classes.dex */
    protected class VisibilityAnimListener implements ViewPropertyAnimatorListener {
        private boolean mCanceled = false;
        int mFinalVisibility;

        protected VisibilityAnimListener() {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public final void onAnimationStart(View view) {
            AbsActionBarView.super.setVisibility(0);
            this.mCanceled = false;
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public final void onAnimationEnd(View view) {
            if (!this.mCanceled) {
                AbsActionBarView.this.mVisibilityAnim = null;
                AbsActionBarView.super.setVisibility(this.mFinalVisibility);
            }
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public final void onAnimationCancel(View view) {
            this.mCanceled = true;
        }
    }
}
