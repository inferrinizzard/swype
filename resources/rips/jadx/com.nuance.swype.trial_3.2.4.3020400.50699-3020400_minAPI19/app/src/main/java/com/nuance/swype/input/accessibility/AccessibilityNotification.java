package com.nuance.swype.input.accessibility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.nuance.android.compat.AccessibilityEventCompat;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;

/* loaded from: classes.dex */
public class AccessibilityNotification {
    private static AccessibilityNotification instance;
    private static Toast mToast;
    private static Handler mToast_DelayHandler;
    private static Runnable mToast_DelayRunnable;
    private static int mToast_DelayTime;

    /* loaded from: classes.dex */
    public enum Notification_Type {
        ON_KEYBOARD_CLOSE(R.string.accessibility_notification_on_keyboard_close_short),
        ON_KEYBOARD_PORTRAIT(R.string.accessibility_notification_on_keyboard_portrait_short),
        ON_KEYBOARD_LANDSCAPE(R.string.accessibility_notification_on_keyboard_landscape_short),
        ON_KEYBOARD_LAYER_TEXT(R.string.accessibility_notification_on_main_keyboard_A),
        ON_KEYBOARD_LAYER_SYMBOLS(R.string.accessibility_notification_on_symbol_keyboard),
        ON_KEYBOARD_LAYER_EDIT(R.string.edit_keyboard),
        ON_KEYBOARD_LAYER_NUM(R.string.number_keyboard),
        ON_KEYBOARD_LAYER_PHONE(R.string.accessibility_notification_on_phone_keyboard),
        ON_KEYBOARD_SYMBOL1(R.string.accessibility_notification_on_symbol_keyboard_shift_off),
        ON_KEYBOARD_SYMBOL2(R.string.accessibility_notification_on_symbol_keyboard_shift_on),
        ON_KEYBOARD_TEXT_LOWER_CASE(R.string.accessibility_notification_on_main_keyboard_shift_off),
        ON_KEYBOARD_TEXT_CAP(R.string.accessibility_notification_on_main_keyboard_shift_on_A),
        ON_KEYBOARD_TEXT_ALL_CAP(R.string.accessibility_notification_on_main_keyboard_shift_lock_A);

        private final int resId;

        Notification_Type(int resId) {
            this.resId = resId;
        }

        public final int getResourceId() {
            return this.resId;
        }
    }

    @SuppressLint({"NewApi"})
    public void speak(Context context) {
        AccessibilityEvent event = AccessibilityEventCompat.obtainTextEvent(context);
        InputView currentView = IMEApplication.from(context).getIME().getCurrentInputView();
        if (currentView != null) {
            currentView.onInitializeAccessibilityEvent(event);
            IMEApplication.from(context).getAppPreferences().getAccessibilityInfo().requestSendAccessibilityEvent(event);
        }
    }

    public void speak(Context context, CharSequence label) {
        if (label != null) {
            AccessibilityInfo accessibilityInfo = IMEApplication.from(context).getAppPreferences().getAccessibilityInfo();
            AccessibilityEvent event = AccessibilityEventCompat.initTextEvent(context, label);
            accessibilityInfo.requestSendAccessibilityEvent(event);
        }
    }

    public static AccessibilityNotification getInstance() {
        if (instance == null) {
            instance = new AccessibilityNotification();
        }
        return instance;
    }

    public void notifyKeyboardOpen(Context context, int orientation, KeyboardEx.KeyboardLayerType keyboardLayer, Shift.ShiftState shiftState, String currentLanguage) {
        if (!isInVoiceUI(context)) {
            StringBuilder notice = new StringBuilder();
            if (currentLanguage != null) {
                notice.append(currentLanguage);
                notice.append(XMLResultsHandler.SEP_SPACE);
            }
            if (orientation == 1) {
                notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_PORTRAIT.getResourceId()));
            } else {
                notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LANDSCAPE.getResourceId()));
            }
            switch (keyboardLayer) {
                case KEYBOARD_TEXT:
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LAYER_TEXT.getResourceId()));
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    switch (shiftState) {
                        case OFF:
                            notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_TEXT_LOWER_CASE.getResourceId()));
                            break;
                        case ON:
                            notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_TEXT_CAP.getResourceId()));
                            break;
                        case LOCKED:
                            notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_TEXT_ALL_CAP.getResourceId()));
                            break;
                    }
                case KEYBOARD_SYMBOLS:
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LAYER_SYMBOLS.getResourceId()));
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    if (shiftState == Shift.ShiftState.OFF) {
                        notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_SYMBOL1.getResourceId()));
                        break;
                    } else {
                        notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_SYMBOL2.getResourceId()));
                        break;
                    }
                case KEYBOARD_EDIT:
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LAYER_EDIT.getResourceId()));
                    break;
                case KEYBOARD_NUM:
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LAYER_NUM.getResourceId()));
                    break;
                case KEYBOARD_PHONE:
                    notice.append(XMLResultsHandler.SEP_SPACE);
                    notice.append(context.getResources().getString(Notification_Type.ON_KEYBOARD_LAYER_PHONE.getResourceId()));
                    break;
            }
            announceNotification(context, notice.toString(), true);
        }
    }

    public void notifyKeyboardClose(Context context) {
        String notice = context.getResources().getString(Notification_Type.ON_KEYBOARD_CLOSE.getResourceId());
        announceNotification(context, notice, true);
    }

    public void speak(Context context, Notification_Type notificationId) {
        String notice = context.getResources().getString(notificationId.getResourceId());
        announceNotification(context, notice, false);
    }

    public void speakKeyboardLayer(Context context, KeyboardEx.KeyboardLayerType keyboardLayer) {
        switch (keyboardLayer) {
            case KEYBOARD_TEXT:
                speak(context, Notification_Type.ON_KEYBOARD_LAYER_TEXT);
                return;
            case KEYBOARD_SYMBOLS:
                speak(context, Notification_Type.ON_KEYBOARD_LAYER_SYMBOLS);
                return;
            case KEYBOARD_EDIT:
                speak(context, Notification_Type.ON_KEYBOARD_LAYER_EDIT);
                return;
            case KEYBOARD_NUM:
                speak(context, Notification_Type.ON_KEYBOARD_LAYER_NUM);
                return;
            default:
                return;
        }
    }

    private boolean isInVoiceUI(Context context) {
        IMEApplication app;
        if (context == null || (app = IMEApplication.from(context)) == null) {
            return true;
        }
        SpeechWrapper sw = app.getSpeechWrapper();
        return sw != null && sw.isResumable();
    }

    public void speakShiftState(Context context, Shift.ShiftState state, KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
            switch (state) {
                case OFF:
                    speak(context, Notification_Type.ON_KEYBOARD_TEXT_LOWER_CASE);
                    return;
                case ON:
                    speak(context, Notification_Type.ON_KEYBOARD_TEXT_CAP);
                    return;
                case LOCKED:
                    speak(context, Notification_Type.ON_KEYBOARD_TEXT_ALL_CAP);
                    return;
                default:
                    return;
            }
        }
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
            switch (state) {
                case OFF:
                    speak(context, Notification_Type.ON_KEYBOARD_SYMBOL1);
                    return;
                case ON:
                case LOCKED:
                    speak(context, Notification_Type.ON_KEYBOARD_SYMBOL2);
                    return;
                default:
                    return;
            }
        }
    }

    public void announceNotification(final Context context, final String notice, boolean instant) {
        cleanToastDelayHandler();
        if (instant) {
            speakByToast(context, notice);
            return;
        }
        mToast_DelayHandler = new Handler();
        mToast_DelayRunnable = new Runnable() { // from class: com.nuance.swype.input.accessibility.AccessibilityNotification.1
            @Override // java.lang.Runnable
            public void run() {
                AccessibilityNotification.speakByToast(context, notice.toString());
            }
        };
        mToast_DelayHandler.postDelayed(mToast_DelayRunnable, mToast_DelayTime);
    }

    @SuppressLint({"InflateParams"})
    public static void speakByToast(Context context, CharSequence text) {
        if (context != null) {
            if (mToast == null) {
                mToast = new Toast(context);
                mToast_DelayTime = context.getResources().getInteger(R.integer.accessibility_notification_toast_delay_time);
            }
            View v = LayoutInflater.from(context).inflate(R.layout.toast, (ViewGroup) null);
            TextView tv = (TextView) v.findViewById(R.id.message);
            int trans = context.getResources().getColor(android.R.color.transparent);
            v.setBackgroundColor(trans);
            tv.setTextColor(trans);
            tv.setText(text);
            mToast.setView(v);
            mToast.setDuration(0);
            mToast.show();
        }
    }

    private static void cleanToastDelayHandler() {
        if (mToast_DelayHandler != null && mToast_DelayRunnable != null) {
            mToast_DelayHandler.removeCallbacks(mToast_DelayRunnable);
            mToast_DelayHandler = null;
        }
    }
}
