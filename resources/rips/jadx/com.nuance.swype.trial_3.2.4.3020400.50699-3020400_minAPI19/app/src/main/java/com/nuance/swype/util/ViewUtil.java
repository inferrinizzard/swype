package com.nuance.swype.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public final class ViewUtil {
    public static void resetView(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(view);
            }
            forceLayout(view);
        }
    }

    public static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                forceLayout(group.getChildAt(idx));
            }
        }
    }

    public static int getDefaultSizePreferMin(int minSize, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
                int result = Math.min(minSize, specSize);
                return result;
            case 1073741824:
                return specSize;
            default:
                return minSize;
        }
    }

    public static int getHorPadding(View view) {
        return view.getPaddingLeft() + view.getPaddingRight();
    }

    public static int getVerPadding(View view) {
        return view.getPaddingTop() + view.getPaddingBottom();
    }
}
