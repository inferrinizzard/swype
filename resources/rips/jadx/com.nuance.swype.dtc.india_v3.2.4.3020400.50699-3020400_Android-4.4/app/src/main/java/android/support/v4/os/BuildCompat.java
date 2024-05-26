package android.support.v4.os;

import android.os.Build;

/* loaded from: classes.dex */
public final class BuildCompat {
    public static boolean isAtLeastN() {
        return Build.VERSION.SDK_INT >= 24;
    }
}
