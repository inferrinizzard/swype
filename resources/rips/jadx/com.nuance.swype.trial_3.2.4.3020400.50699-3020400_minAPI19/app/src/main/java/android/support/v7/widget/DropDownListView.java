package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.appcompat.R;
import android.view.MotionEvent;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DropDownListView extends ListViewCompat {
    private ViewPropertyAnimatorCompat mClickAnimation;
    private boolean mDrawsInPressedState;
    private boolean mHijackFocus;
    private boolean mListSelectionHidden;
    private ListViewAutoScrollHelper mScrollHelper;

    public DropDownListView(Context context, boolean hijackFocus) {
        super(context, null, R.attr.dropDownListViewStyle);
        this.mHijackFocus = hijackFocus;
        setCacheColorHint(0);
    }

    public boolean onForwardedEvent(MotionEvent event, int activePointerId) {
        View childAt;
        boolean handledEvent = true;
        boolean clearPressedItem = false;
        int actionMasked = MotionEventCompat.getActionMasked(event);
        switch (actionMasked) {
            case 1:
                handledEvent = false;
            case 2:
                int activeIndex = event.findPointerIndex(activePointerId);
                if (activeIndex < 0) {
                    handledEvent = false;
                    break;
                } else {
                    int x = (int) event.getX(activeIndex);
                    int y = (int) event.getY(activeIndex);
                    int position = pointToPosition(x, y);
                    if (position == -1) {
                        clearPressedItem = true;
                        break;
                    } else {
                        View child = getChildAt(position - getFirstVisiblePosition());
                        float f = x;
                        float f2 = y;
                        this.mDrawsInPressedState = true;
                        if (Build.VERSION.SDK_INT >= 21) {
                            drawableHotspotChanged(f, f2);
                        }
                        if (!isPressed()) {
                            setPressed(true);
                        }
                        layoutChildren();
                        if (this.mMotionPosition != -1 && (childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition())) != null && childAt != child && childAt.isPressed()) {
                            childAt.setPressed(false);
                        }
                        this.mMotionPosition = position;
                        float left = f - child.getLeft();
                        float top = f2 - child.getTop();
                        if (Build.VERSION.SDK_INT >= 21) {
                            child.drawableHotspotChanged(left, top);
                        }
                        if (!child.isPressed()) {
                            child.setPressed(true);
                        }
                        Drawable selector = getSelector();
                        boolean z = (selector == null || position == -1) ? false : true;
                        if (z) {
                            selector.setVisible(false, false);
                        }
                        Rect rect = this.mSelectorRect;
                        rect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                        rect.left -= this.mSelectionLeftPadding;
                        rect.top -= this.mSelectionTopPadding;
                        rect.right += this.mSelectionRightPadding;
                        rect.bottom += this.mSelectionBottomPadding;
                        try {
                            boolean z2 = this.mIsChildViewEnabled.getBoolean(this);
                            if (child.isEnabled() != z2) {
                                this.mIsChildViewEnabled.set(this, Boolean.valueOf(!z2));
                                if (position != -1) {
                                    refreshDrawableState();
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if (z) {
                            Rect rect2 = this.mSelectorRect;
                            float exactCenterX = rect2.exactCenterX();
                            float exactCenterY = rect2.exactCenterY();
                            selector.setVisible(getVisibility() == 0, false);
                            DrawableCompat.setHotspot(selector, exactCenterX, exactCenterY);
                        }
                        Drawable selector2 = getSelector();
                        if (selector2 != null && position != -1) {
                            DrawableCompat.setHotspot(selector2, f, f2);
                        }
                        setSelectorEnabled(false);
                        refreshDrawableState();
                        handledEvent = true;
                        if (actionMasked == 1) {
                            performItemClick(child, position, getItemIdAtPosition(position));
                            break;
                        }
                    }
                }
                break;
            case 3:
                handledEvent = false;
                break;
        }
        if (!handledEvent || clearPressedItem) {
            this.mDrawsInPressedState = false;
            setPressed(false);
            drawableStateChanged();
            View childAt2 = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
            if (childAt2 != null) {
                childAt2.setPressed(false);
            }
            if (this.mClickAnimation != null) {
                this.mClickAnimation.cancel();
                this.mClickAnimation = null;
            }
        }
        if (handledEvent) {
            if (this.mScrollHelper == null) {
                this.mScrollHelper = new ListViewAutoScrollHelper(this);
            }
            this.mScrollHelper.setEnabled(true);
            this.mScrollHelper.onTouch(this, event);
        } else if (this.mScrollHelper != null) {
            this.mScrollHelper.setEnabled(false);
        }
        return handledEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListSelectionHidden(boolean hideListSelection) {
        this.mListSelectionHidden = hideListSelection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.ListViewCompat
    public final boolean touchModeDrawsInPressedStateCompat() {
        return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
    }

    @Override // android.view.View
    public boolean isInTouchMode() {
        return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return this.mHijackFocus || super.hasWindowFocus();
    }

    @Override // android.view.View
    public boolean isFocused() {
        return this.mHijackFocus || super.isFocused();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean hasFocus() {
        return this.mHijackFocus || super.hasFocus();
    }
}
