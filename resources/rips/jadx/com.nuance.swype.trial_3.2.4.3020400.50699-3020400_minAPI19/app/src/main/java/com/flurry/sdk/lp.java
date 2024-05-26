package com.flurry.sdk;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public final class lp {
    @SuppressLint({"NewApi"})
    private static Point b() {
        Display defaultDisplay = ((WindowManager) jr.a().a.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            defaultDisplay.getRealSize(point);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                Method method = Display.class.getMethod("getRawHeight", new Class[0]);
                point.x = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
                point.y = ((Integer) method.invoke(defaultDisplay, new Object[0])).intValue();
            } catch (Throwable th) {
                defaultDisplay.getSize(point);
            }
        } else if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(point);
        } else {
            point.x = defaultDisplay.getWidth();
            point.y = defaultDisplay.getHeight();
        }
        return point;
    }

    public static int e() {
        Point b = b();
        if (b.x == b.y) {
            return 3;
        }
        if (b.x < b.y) {
            return 1;
        }
        return 2;
    }
}
