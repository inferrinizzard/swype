package android.support.v4.content.res;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/* loaded from: classes.dex */
final class ConfigurationHelperDonut {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getScreenHeightDp(Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (metrics.heightPixels / metrics.density);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getScreenWidthDp(Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (metrics.widthPixels / metrics.density);
    }
}
