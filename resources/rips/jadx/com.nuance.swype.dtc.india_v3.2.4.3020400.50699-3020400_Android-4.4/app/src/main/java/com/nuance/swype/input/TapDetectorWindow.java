package com.nuance.swype.input;

import android.view.MotionEvent;
import android.view.Window;
import com.nuance.android.util.WindowCallbackWrapper;
import com.nuance.swype.input.AbstractTapDetector;
import com.nuance.swype.input.hardkey.HardKeyboardManager;

/* loaded from: classes.dex */
public class TapDetectorWindow extends AbstractTapDetector {
    private TapWindowCallback callback;
    private Window mContextWindow;

    /* loaded from: classes.dex */
    private static class TapWindowCallback extends WindowCallbackWrapper {
        protected boolean touchedOutside;

        private TapWindowCallback() {
        }

        public void update(Window.Callback target) {
            setTarget(target);
        }

        @Override // com.nuance.android.util.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getActionMasked() == 4) {
                this.touchedOutside = true;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    public TapDetectorWindow(AbstractTapDetector.TapHandler[] tapHandlers, Window window) {
        super(tapHandlers);
        window.addFlags(HardKeyboardManager.META_META_RIGHT_ON);
        if (this.callback == null) {
            this.callback = new TapWindowCallback();
        }
        Window.Callback target = window.getCallback();
        if (target != this.callback && !this.callback.wraps(target)) {
            this.callback.update(target);
            window.setCallback(this.callback);
        }
        this.mContextWindow = window;
    }

    @Override // com.nuance.swype.input.AbstractTapDetector
    public void onShowInputRequested(int flags, boolean configChange) {
        SpeechWrapper spw;
        if (this.callback.touchedOutside) {
            boolean userRequested = (flags & 1) != 0;
            if (!configChange && userRequested) {
                if (this.mContextWindow != null && (spw = IMEApplication.from(this.mContextWindow.getContext()).getSpeechWrapper()) != null && spw.isSpeechViewShowing()) {
                    spw.cancelSpeech();
                }
                onTap();
            }
            this.callback.touchedOutside = false;
        }
    }
}
