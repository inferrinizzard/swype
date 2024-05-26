package com.nuance.swype.input.keyboard;

import android.content.Context;
import android.os.Message;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.swype.input.UserPreferences;

/* loaded from: classes.dex */
public class TimerHandler extends WeakReferenceHandlerWrapper<Callbacks> {
    private static final int MSG_LONG_PRESS_KEY = 1;
    private static final int MSG_MULTITAP_TIMER_TIMEOUT = 4;
    private static final int MSG_QUICK_KEY_PRESS_TIMER_TIMEOUT = 3;
    private static final int MSG_REPEAT_KEY = 2;
    private static final int MSG_TOUCH_TIMER_TIMEOUT = 5;

    /* loaded from: classes.dex */
    public interface Callbacks {
        void longPressKey(int i, int i2, KeyType keyType);

        void multiTapTimerTimeOut();

        void quickPressKeyTimeOut(int i, int i2, KeyType keyType);

        void repeatedKey(int i, int i2, KeyType keyType, RepeatKeyRate repeatKeyRate);

        void touchTimerTimeOut(int i);
    }

    public TimerHandler(Callbacks ownerInstance) {
        super(ownerInstance);
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Callbacks callbacks = getOwnerInstance();
        if (callbacks != null) {
            switch (msg.what) {
                case 1:
                    KeyTimerMessageParam param = (KeyTimerMessageParam) msg.obj;
                    callbacks.longPressKey(param.pointerId, param.keyIndex, param.keyType);
                    return;
                case 2:
                    KeyTimerMessageParam param2 = (KeyTimerMessageParam) msg.obj;
                    callbacks.repeatedKey(param2.pointerId, param2.keyIndex, param2.keyType, param2.repeatKeyRate);
                    return;
                case 3:
                    KeyTimerMessageParam param3 = (KeyTimerMessageParam) msg.obj;
                    callbacks.quickPressKeyTimeOut(param3.pointerId, param3.keyIndex, param3.keyType);
                    return;
                case 4:
                    callbacks.multiTapTimerTimeOut();
                    return;
                case 5:
                    callbacks.touchTimerTimeOut(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    public void startLongPressKeyTimer(int pointerId, int keyIndex, KeyType keyType, int delay) {
        cancelLongPressKeyTimer();
        sendMessageDelayed(obtainMessage(1, obtainKeyTimerMessageParam(pointerId, keyIndex, keyType)), delay);
    }

    public void cancelLongPressKeyTimer() {
        removeMessages(1);
    }

    public void startRepeatKeyTimer(int pointerId, int keyIndex, KeyType keyType, RepeatKeyRate repeatKeyRate, int delay) {
        cancelRepeatKeyTimer();
        sendMessageDelayed(obtainMessage(2, obtainKeyTimerMessageParam(pointerId, keyIndex, keyType, repeatKeyRate)), delay);
    }

    public void cancelRepeatKeyTimer() {
        removeMessages(2);
    }

    public void startQuickKeyPressTimerTimeOut(int pointerId, int keyIndex, KeyType keyType, int timeout) {
        cancelQuickKeyPressTimerTimeOut();
        sendMessageDelayed(obtainMessage(3, obtainKeyTimerMessageParam(pointerId, keyIndex, keyType)), timeout);
    }

    public void cancelQuickKeyPressTimerTimeOut() {
        removeMessages(3);
    }

    public boolean isQuickKeyPressTimerTimeOutActive() {
        return hasMessages(3);
    }

    public void startMultiTapTimerTimeOut(int timeout) {
        cancelMultiTapTimerTimeOut();
        sendMessageDelayed(obtainMessage(4), timeout);
    }

    public boolean isMultiTapTimerTimeOutActive() {
        return hasMessages(4);
    }

    public void cancelMultiTapTimerTimeOut() {
        removeMessages(4);
    }

    public void startTouchTimerTimeOut(int timeout) {
        cancelTouchTimerTimeOut();
        sendMessageDelayed(obtainMessage(5, Integer.valueOf(timeout)), timeout);
    }

    public void cancelTouchTimerTimeOut() {
        removeMessages(5);
    }

    private KeyTimerMessageParam obtainKeyTimerMessageParam(int pointerId, int keyIndex, KeyType keyType) {
        return obtainKeyTimerMessageParam(pointerId, keyIndex, keyType, null);
    }

    private KeyTimerMessageParam obtainKeyTimerMessageParam(int pointerId, int keyIndex, KeyType keyType, RepeatKeyRate repeatKeyRate) {
        return new KeyTimerMessageParam(pointerId, keyIndex, keyType, repeatKeyRate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class KeyTimerMessageParam {
        int keyIndex;
        KeyType keyType;
        int pointerId;
        RepeatKeyRate repeatKeyRate;

        private KeyTimerMessageParam(int pointerId, int keyIndex, KeyType keyType, RepeatKeyRate repeatKeyRate) {
            this.pointerId = pointerId;
            this.keyIndex = keyIndex;
            this.keyType = keyType;
            this.repeatKeyRate = repeatKeyRate;
        }
    }

    /* loaded from: classes.dex */
    public static final class RepeatKeyRate {
        int repeatChangeRate;
        int repeatCount = 1;
        final int repeatPressTimeout;

        public RepeatKeyRate(Context context) {
            this.repeatPressTimeout = UserPreferences.from(context).getLongPressDelay();
            this.repeatChangeRate = (this.repeatPressTimeout / 2) + 300;
        }

        public final void updateRepeatCount() {
            this.repeatCount++;
            if (this.repeatChangeRate > 100) {
                this.repeatChangeRate = (int) (this.repeatChangeRate / 1.2d);
            }
        }

        public final int getRepeatDelay() {
            return this.repeatChangeRate;
        }

        public final int getRepeatCount() {
            return this.repeatCount;
        }
    }
}
