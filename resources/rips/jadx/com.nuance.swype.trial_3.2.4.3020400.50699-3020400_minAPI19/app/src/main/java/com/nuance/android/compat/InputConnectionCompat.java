package com.nuance.android.compat;

import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.nuance.swype.util.InputConnectionUtils;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class InputConnectionCompat {
    private static final Method InputConnection_getSelectedText = CompatUtil.getMethod((Class<?>) InputConnection.class, "getSelectedText", (Class<?>[]) new Class[]{Integer.TYPE});
    private static final Method InputConnection_setComposingRegion = CompatUtil.getMethod((Class<?>) InputConnection.class, "setComposingRegion", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE});

    private InputConnectionCompat() {
    }

    private static CharSequence invokeGetSelectedText(InputConnection ic, int flags) {
        return (CharSequence) CompatUtil.invoke(InputConnection_getSelectedText, ic, Integer.valueOf(flags));
    }

    public static CharSequence getSelectedText(InputConnection ic, int flags) {
        if (InputConnection_getSelectedText != null) {
            return invokeGetSelectedText(ic, flags);
        }
        return null;
    }

    public static String getSelectedTextString(InputConnection ic) {
        int[] selection;
        if (InputConnection_getSelectedText != null) {
            CharSequence result = invokeGetSelectedText(ic, 0);
            if (result != null) {
                return result.toString();
            }
            return null;
        }
        ExtractedText eText = ic.getExtractedText(new ExtractedTextRequest(), 0);
        if (eText == null || eText.text == null || (selection = InputConnectionUtils.getSelection(eText)) == null) {
            return null;
        }
        return eText.text.subSequence(selection[0], selection[1]).toString();
    }

    private static boolean invokeSetComposingRegion(InputConnection ic, int start, int end) {
        return ((Boolean) CompatUtil.invoke(InputConnection_setComposingRegion, ic, Integer.valueOf(start), Integer.valueOf(end))).booleanValue();
    }

    public static boolean setComposingRegion(InputConnection ic, int start, int end) {
        return InputConnection_setComposingRegion != null && invokeSetComposingRegion(ic, start, end);
    }
}
