package com.nuance.android.compat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.hardkey.HardKeyboardManager;

/* loaded from: classes.dex */
public class AccessibilityEventCompat {
    private AccessibilityEventCompat() {
    }

    @SuppressLint({"InlinedApi"})
    public static AccessibilityEvent obtainTextEvent(Context context) {
        notifyHoverEnter(context);
        return AccessibilityEvent.obtain(HardKeyboardManager.META_CTRL_RIGHT_ON);
    }

    @SuppressLint({"InlinedApi"})
    @TargetApi(14)
    public static AccessibilityEvent initTextEvent(Context context, CharSequence label) {
        AccessibilityEvent event = AccessibilityEvent.obtain(HardKeyboardManager.META_CTRL_RIGHT_ON);
        IMEApplication.from(context).getIME().getCurrentInputView().onInitializeAccessibilityEvent(event);
        event.getText().clear();
        event.getText().add(label);
        return event;
    }

    @TargetApi(14)
    private static void notifyHoverEnter(Context context) {
        InputView view = IMEApplication.from(context).getIME().getCurrentInputView();
        if (view != null) {
            AccessibilityInfo accessibilityInfo = IMEApplication.from(context).getAppPreferences().getAccessibilityInfo();
            AccessibilityEvent event = AccessibilityEvent.obtain(128);
            view.onInitializeAccessibilityEvent(event);
            accessibilityInfo.requestSendAccessibilityEvent(event);
        }
    }
}
