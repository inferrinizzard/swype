package com.nuance.swype.util;

import android.content.Context;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public final class DisplayUtils {
    public static boolean isLowEndDevice(Context context) {
        return context.getResources().getBoolean(R.bool.LOW_END_DEVICE_BUILD);
    }
}
