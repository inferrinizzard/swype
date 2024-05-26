package com.nuance.swype.util;

import android.graphics.Rect;
import android.view.View;

/* loaded from: classes.dex */
public final class GeomUtil {
    public static Rect getRect(View view) {
        return new Rect(view.getLeft(), view.getTop(), view.getLeft() + view.getWidth(), view.getTop() + view.getHeight());
    }

    public static boolean confine(Rect rc, Rect area, int padding) {
        if (padding != 0) {
            area = shrink(new Rect(area), padding, padding, padding, padding);
        }
        return confine(rc, area);
    }

    public static void moveRectTo(Rect rc, int x, int y) {
        int width = rc.width();
        int height = rc.height();
        rc.left = x;
        rc.top = y;
        rc.right = rc.left + width;
        rc.bottom = rc.top + height;
    }

    public static void moveRectBy(Rect rc, int dx, int dy) {
        int width = rc.width();
        int height = rc.height();
        rc.left += dx;
        rc.top += dy;
        rc.right = rc.left + width;
        rc.bottom = rc.top + height;
    }

    private static Rect shrink(Rect rc, int leftPad, int topPad, int rightPad, int botPad) {
        rc.left += leftPad;
        rc.top += topPad;
        rc.right -= rightPad;
        rc.bottom -= botPad;
        return rc;
    }

    public static Rect shrink(Rect rc, Rect insets) {
        return shrink(rc, insets.left, insets.top, insets.right, insets.bottom);
    }

    public static Rect expand(Rect rc, Rect insets) {
        return shrink(rc, -insets.left, -insets.top, -insets.right, -insets.bottom);
    }

    private static int getSnapDist(int v1, int v2, int threshold) {
        int d1 = Math.abs(v1);
        int d2 = Math.abs(v2);
        if (d1 < d2) {
            if (d1 < threshold) {
                return v1;
            }
            return 0;
        }
        if (d2 < threshold) {
            return v2;
        }
        return 0;
    }

    public static int getSnapDistHor(Rect child, Rect parent, int threshold) {
        return getSnapDist(parent.left - child.left, parent.right - child.right, threshold);
    }

    public static int getSnapDistVer(Rect child, Rect parent, int threshold) {
        return getSnapDist(parent.top - child.top, parent.bottom - child.bottom, threshold);
    }

    public static void setSize(Rect rc, int width, int height) {
        rc.right = rc.left + width;
        rc.bottom = rc.top + height;
    }

    public static int getOffsetX(int areaWidth, int contentWidth, int gravity) {
        switch (gravity & 7) {
            case 3:
                return 0;
            case 4:
            default:
                int offset = (areaWidth - contentWidth) / 2;
                return offset;
            case 5:
                int offset2 = areaWidth - contentWidth;
                return offset2;
        }
    }

    public static int getOffsetY(int areaHeight, int contentHeight, int gravity) {
        switch (gravity & 7) {
            case 3:
                return 0;
            case 4:
            default:
                int offset = (areaHeight - contentHeight) / 2;
                return offset;
            case 5:
                int offset2 = areaHeight - contentHeight;
                return offset2;
        }
    }

    public static boolean confine(Rect child, Rect parent) {
        int dx = Math.max(parent.left - child.left, 0);
        if (dx == 0) {
            dx = Math.min(parent.right - child.right, 0);
        }
        int dy = Math.max(parent.top - child.top, 0);
        if (dy == 0) {
            dy = Math.min(parent.bottom - child.bottom, 0);
        }
        if (dx == 0 && dy == 0) {
            return false;
        }
        moveRectBy(child, dx, dy);
        return true;
    }

    public static void moveRectTo(Rect rc, int[] pt) {
        moveRectTo(rc, pt[0], pt[1]);
    }
}
