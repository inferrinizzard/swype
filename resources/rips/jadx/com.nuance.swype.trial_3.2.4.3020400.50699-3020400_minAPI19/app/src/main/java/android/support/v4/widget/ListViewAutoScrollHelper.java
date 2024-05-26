package android.support.v4.widget;

import android.os.Build;
import android.view.View;
import android.widget.ListView;

/* loaded from: classes.dex */
public final class ListViewAutoScrollHelper extends AutoScrollHelper {
    private final ListView mTarget;

    public ListViewAutoScrollHelper(ListView target) {
        super(target);
        this.mTarget = target;
    }

    @Override // android.support.v4.widget.AutoScrollHelper
    public final void scrollTargetBy$255f295(int deltaY) {
        View childAt;
        ListView listView = this.mTarget;
        if (Build.VERSION.SDK_INT >= 19) {
            listView.scrollListBy(deltaY);
            return;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        if (firstVisiblePosition == -1 || (childAt = listView.getChildAt(0)) == null) {
            return;
        }
        listView.setSelectionFromTop(firstVisiblePosition, childAt.getTop() - deltaY);
    }

    @Override // android.support.v4.widget.AutoScrollHelper
    public final boolean canTargetScrollVertically(int direction) {
        ListView target = this.mTarget;
        int itemCount = target.getCount();
        if (itemCount == 0) {
            return false;
        }
        int childCount = target.getChildCount();
        int firstPosition = target.getFirstVisiblePosition();
        int lastPosition = firstPosition + childCount;
        if (direction > 0) {
            if (lastPosition >= itemCount && target.getChildAt(childCount - 1).getBottom() <= target.getHeight()) {
                return false;
            }
        } else {
            if (direction >= 0) {
                return false;
            }
            if (firstPosition <= 0 && target.getChildAt(0).getTop() >= 0) {
                return false;
            }
        }
        return true;
    }
}
