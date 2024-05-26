package android.support.v7.widget;

import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.swype.input.hardkey.HardKeyboardManager;

/* loaded from: classes.dex */
public final class RecyclerViewAccessibilityDelegate extends AccessibilityDelegateCompat {
    final AccessibilityDelegateCompat mItemDelegate = new AccessibilityDelegateCompat() { // from class: android.support.v7.widget.RecyclerViewAccessibilityDelegate.1
        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            if (!RecyclerViewAccessibilityDelegate.this.mRecyclerView.hasPendingAdapterUpdates() && RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null) {
                RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfoForItem(host, info);
            }
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public final boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (super.performAccessibilityAction(host, action, args)) {
                return true;
            }
            if (RecyclerViewAccessibilityDelegate.this.mRecyclerView.hasPendingAdapterUpdates() || RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() == null) {
                return false;
            }
            RecyclerView.LayoutManager layoutManager = RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager();
            RecyclerView.Recycler recycler = layoutManager.mRecyclerView.mRecycler;
            RecyclerView.State state = layoutManager.mRecyclerView.mState;
            return false;
        }
    };
    final RecyclerView mRecyclerView;

    public RecyclerViewAccessibilityDelegate(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override // android.support.v4.view.AccessibilityDelegateCompat
    public final boolean performAccessibilityAction(View host, int action, Bundle args) {
        int paddingTop;
        int i;
        int paddingLeft;
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        if (this.mRecyclerView.hasPendingAdapterUpdates() || this.mRecyclerView.getLayoutManager() == null) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        RecyclerView.Recycler recycler = layoutManager.mRecyclerView.mRecycler;
        RecyclerView.State state = layoutManager.mRecyclerView.mState;
        if (layoutManager.mRecyclerView == null) {
            return false;
        }
        switch (action) {
            case HardKeyboardManager.META_CTRL_ON /* 4096 */:
                paddingTop = ViewCompat.canScrollVertically(layoutManager.mRecyclerView, 1) ? (layoutManager.mHeight - layoutManager.getPaddingTop()) - layoutManager.getPaddingBottom() : 0;
                if (ViewCompat.canScrollHorizontally(layoutManager.mRecyclerView, 1)) {
                    i = paddingTop;
                    paddingLeft = (layoutManager.mWidth - layoutManager.getPaddingLeft()) - layoutManager.getPaddingRight();
                    break;
                }
                i = paddingTop;
                paddingLeft = 0;
                break;
            case 8192:
                paddingTop = ViewCompat.canScrollVertically(layoutManager.mRecyclerView, -1) ? -((layoutManager.mHeight - layoutManager.getPaddingTop()) - layoutManager.getPaddingBottom()) : 0;
                if (ViewCompat.canScrollHorizontally(layoutManager.mRecyclerView, -1)) {
                    i = paddingTop;
                    paddingLeft = -((layoutManager.mWidth - layoutManager.getPaddingLeft()) - layoutManager.getPaddingRight());
                    break;
                }
                i = paddingTop;
                paddingLeft = 0;
                break;
            default:
                paddingLeft = 0;
                i = 0;
                break;
        }
        if (i == 0 && paddingLeft == 0) {
            return false;
        }
        layoutManager.mRecyclerView.scrollBy(paddingLeft, i);
        return true;
    }

    @Override // android.support.v4.view.AccessibilityDelegateCompat
    public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        info.setClassName(RecyclerView.class.getName());
        if (!this.mRecyclerView.hasPendingAdapterUpdates() && this.mRecyclerView.getLayoutManager() != null) {
            RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
            RecyclerView.Recycler recycler = layoutManager.mRecyclerView.mRecycler;
            RecyclerView.State state = layoutManager.mRecyclerView.mState;
            if (ViewCompat.canScrollVertically(layoutManager.mRecyclerView, -1) || ViewCompat.canScrollHorizontally(layoutManager.mRecyclerView, -1)) {
                info.addAction(8192);
                info.setScrollable(true);
            }
            if (ViewCompat.canScrollVertically(layoutManager.mRecyclerView, 1) || ViewCompat.canScrollHorizontally(layoutManager.mRecyclerView, 1)) {
                info.addAction(HardKeyboardManager.META_CTRL_ON);
                info.setScrollable(true);
            }
            AccessibilityNodeInfoCompat.IMPL.setCollectionInfo(info.mInfo, new AccessibilityNodeInfoCompat.CollectionInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionInfo$50cb9c52(layoutManager.getRowCountForAccessibility(recycler, state), layoutManager.getColumnCountForAccessibility(recycler, state))).mInfo);
        }
    }

    @Override // android.support.v4.view.AccessibilityDelegateCompat
    public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        event.setClassName(RecyclerView.class.getName());
        if ((host instanceof RecyclerView) && !this.mRecyclerView.hasPendingAdapterUpdates()) {
            RecyclerView rv = (RecyclerView) host;
            if (rv.getLayoutManager() != null) {
                rv.getLayoutManager().onInitializeAccessibilityEvent(event);
            }
        }
    }
}
