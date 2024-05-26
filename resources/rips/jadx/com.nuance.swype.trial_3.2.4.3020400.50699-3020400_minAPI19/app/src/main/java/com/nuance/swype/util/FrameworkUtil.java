package com.nuance.swype.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/* loaded from: classes.dex */
public final class FrameworkUtil {
    public static void disownParent(View view) {
        ViewParent parent;
        if (view != null && (parent = view.getParent()) != null) {
            ((ViewGroup) parent).removeView(view);
        }
    }
}
