package com.nuance.swype.view;

import android.R;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class OverlayView extends BasicViewLayout {
    protected static final LogManager.Log log = LogManager.getLog("OverlayView");
    private int parentViewGroupId;

    public OverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, R.id.content);
    }

    public OverlayView(Context context, AttributeSet attrs, int parentViewGroupId) {
        super(context, attrs);
        this.parentViewGroupId = parentViewGroupId;
    }

    public final void attach(View view) {
        if (view.getWindowToken() == null) {
            throw new IllegalStateException("Target view has no window");
        }
        ViewParent parent = getParent();
        if (parent == null) {
            ViewGroup findParentGroup = findParentGroup(view);
            if (findParentGroup != null) {
                findParentGroup.addView(this, -1, -1);
                return;
            }
            throw new IllegalStateException("Unable to find parent for overlay");
        }
        if (!isDebugMode() || findParentGroup(view) == parent) {
            return;
        }
        log.w("ensureOverlayAddedToContentFrame(): overlay parented in wrong view");
    }

    public final int[] getOverlayPos(View view) {
        int[] pos = CoordUtils.getWindowPos(view);
        windowToOverlay(pos);
        return pos;
    }

    public final int[] windowToOverlay(int[] coords) {
        int[] pos = CoordUtils.getWindowPos(this);
        CoordUtils.subtract(coords, pos, coords);
        return coords;
    }

    private ViewGroup findParentGroup(View view) {
        View rootView = view.getRootView();
        if (this.parentViewGroupId == 0 && (rootView instanceof FrameLayout)) {
            ViewGroup parentGroup = (FrameLayout) rootView;
            return parentGroup;
        }
        ViewGroup parentGroup2 = (ViewGroup) rootView.findViewById(this.parentViewGroupId != 0 ? this.parentViewGroupId : R.id.content);
        return parentGroup2;
    }

    public final Region getTouchableRegion() {
        int viewCount = getChildCount();
        if (viewCount > 0) {
            Region region = new Region();
            for (int i = 0; i < viewCount; i++) {
                View view = getChildAt(i);
                if (view.isShown()) {
                    Rect rec = GeomUtil.getRect(view);
                    GeomUtil.moveRectTo(rec, getOverlayPos(view));
                    region.op(rec, region, Region.Op.UNION);
                }
            }
            if (!region.isEmpty()) {
                return region;
            }
        }
        return null;
    }

    public final void detach() {
        ViewParent parent;
        if (this == null || (parent = getParent()) == null) {
            return;
        }
        ((ViewGroup) parent).removeView(this);
    }
}
