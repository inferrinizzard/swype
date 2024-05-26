package com.nuance.swype.util;

import android.graphics.Point;
import android.view.View;

/* loaded from: classes.dex */
public final class CoordUtils {

    /* loaded from: classes.dex */
    public static class CoordMapper {
        private final int[] loc = new int[2];
        private final View view;

        public CoordMapper(View view) {
            this.view = view;
        }

        public final void map(Point pt) {
            this.view.getLocationInWindow(this.loc);
            pt.x -= this.loc[0];
            pt.y -= this.loc[1];
        }
    }

    public static int[] newInstance(int x, int y) {
        int[] out = new int[2];
        set(out, x, y);
        return out;
    }

    private static void set(int[] coords, int x, int y) {
        coords[0] = x;
        coords[1] = y;
    }

    public static int[] getWindowPos(View view) {
        int[] out = new int[2];
        view.getLocationInWindow(out);
        return out;
    }

    public static void subtract(int[] first, int[] second, int[] result) {
        set(result, first[0] - second[0], first[1] - second[1]);
    }
}
