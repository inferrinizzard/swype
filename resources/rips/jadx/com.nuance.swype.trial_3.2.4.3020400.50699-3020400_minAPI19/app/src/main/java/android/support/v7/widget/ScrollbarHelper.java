package android.support.v7.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/* loaded from: classes.dex */
final class ScrollbarHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollOffset(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled, boolean reverseLayout) {
        int itemsBefore;
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        int minPosition = Math.min(RecyclerView.LayoutManager.getPosition(startChild), RecyclerView.LayoutManager.getPosition(endChild));
        int maxPosition = Math.max(RecyclerView.LayoutManager.getPosition(startChild), RecyclerView.LayoutManager.getPosition(endChild));
        if (reverseLayout) {
            itemsBefore = Math.max(0, (state.getItemCount() - maxPosition) - 1);
        } else {
            itemsBefore = Math.max(0, minPosition);
        }
        if (smoothScrollbarEnabled) {
            int laidOutArea = Math.abs(orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild));
            int itemRange = Math.abs(RecyclerView.LayoutManager.getPosition(startChild) - RecyclerView.LayoutManager.getPosition(endChild)) + 1;
            float avgSizePerRow = laidOutArea / itemRange;
            return Math.round((itemsBefore * avgSizePerRow) + (orientation.getStartAfterPadding() - orientation.getDecoratedStart(startChild)));
        }
        return itemsBefore;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollExtent(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (!smoothScrollbarEnabled) {
            return Math.abs(RecyclerView.LayoutManager.getPosition(startChild) - RecyclerView.LayoutManager.getPosition(endChild)) + 1;
        }
        int extend = orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild);
        return Math.min(orientation.getTotalSpace(), extend);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollRange(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (!smoothScrollbarEnabled) {
            return state.getItemCount();
        }
        int laidOutArea = orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild);
        int laidOutRange = Math.abs(RecyclerView.LayoutManager.getPosition(startChild) - RecyclerView.LayoutManager.getPosition(endChild)) + 1;
        return (int) ((laidOutArea / laidOutRange) * state.getItemCount());
    }
}
