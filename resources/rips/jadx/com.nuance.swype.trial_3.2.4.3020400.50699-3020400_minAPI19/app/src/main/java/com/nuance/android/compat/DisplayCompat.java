package com.nuance.android.compat;

import android.graphics.Rect;
import android.view.Display;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class DisplayCompat {
    private static final Method Display_getRectSize = CompatUtil.getMethod((Class<?>) Display.class, "getRectSize", (Class<?>[]) new Class[]{Rect.class});

    private DisplayCompat() {
    }

    public static void getRectSize(Display display, Rect outSize) {
        if (Display_getRectSize != null) {
            CompatUtil.invoke(Display_getRectSize, display, outSize);
        } else {
            outSize.set(0, 0, display.getWidth(), display.getHeight());
        }
    }
}
