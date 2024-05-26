package android.support.v4.content;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.util.TypedValue;

/* loaded from: classes.dex */
public class ContextCompat {
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

    public static final Drawable getDrawable(Context context, int id) {
        int resolvedId;
        int version = Build.VERSION.SDK_INT;
        if (version < 21) {
            if (version >= 16) {
                return context.getResources().getDrawable(id);
            }
            synchronized (sLock) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
        return context.getDrawable(id);
    }

    public static final ColorStateList getColorStateList(Context context, int id) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColorStateList(id) : context.getResources().getColorStateList(id);
    }

    public static final int getColor(Context context, int id) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColor(id) : context.getResources().getColor(id);
    }

    public static int checkSelfPermission(Context context, String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid());
    }
}
