package com.nuance.swype.widget.directional;

import com.nuance.swype.input.R;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: classes.dex */
public final class DirectionalUtil {
    private static final String[] rtlLanguages = {"ar", "fa", "he", "iw", "ur"};

    /* loaded from: classes.dex */
    public interface OnCreateDrawableStateCallback {
        int[] baseCreateDrawableState(int i);
    }

    public static boolean isLanguageRtl(String langString) {
        return Arrays.binarySearch(rtlLanguages, langString) >= 0;
    }

    public static boolean isCurrentlyRtl() {
        return isLanguageRtl(Locale.getDefault().getLanguage());
    }

    public static int[] onCreateDrawableState(OnCreateDrawableStateCallback callback, int extraSpace) {
        return onCreateDrawableState(callback, extraSpace, isCurrentlyRtl());
    }

    public static int[] onCreateDrawableState(OnCreateDrawableStateCallback callback, int extraSpace, boolean isRtl) {
        if (isRtl) {
            int extraSpace2 = extraSpace + 1;
            int[] state = callback.baseCreateDrawableState(extraSpace2);
            state[state.length - extraSpace2] = R.attr.state_mirrored;
            return state;
        }
        return callback.baseCreateDrawableState(extraSpace);
    }
}
