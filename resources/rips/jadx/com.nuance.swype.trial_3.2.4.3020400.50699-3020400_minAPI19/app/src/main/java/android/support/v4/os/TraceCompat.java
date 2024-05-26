package android.support.v4.os;

import android.os.Build;
import android.os.Trace;

/* loaded from: classes.dex */
public final class TraceCompat {
    public static void beginSection(String sectionName) {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        Trace.beginSection(sectionName);
    }

    public static void endSection() {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        Trace.endSection();
    }
}
