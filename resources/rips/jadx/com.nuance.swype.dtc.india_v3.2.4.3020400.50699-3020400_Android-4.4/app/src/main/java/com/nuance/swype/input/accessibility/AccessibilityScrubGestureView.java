package com.nuance.swype.input.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.IMEHandlerManager;
import com.nuance.swype.input.KeyboardViewEx;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AccessibilityScrubGestureView extends View {
    List<IGestureViewListener> gestureViewListeners;
    private boolean isForVoiceUI;
    private IME mIme;
    private KeyboardViewEx mKeyboardView;
    private PopupWindow mScrubGestureUpperScreenPopup;

    public AccessibilityScrubGestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccessibilityScrubGestureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        IMEApplication app = IMEApplication.from(context);
        if (app != null) {
            this.mIme = app.getIME();
        }
    }

    public void setKeyboardView(KeyboardViewEx view) {
        this.mKeyboardView = view;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        if (this.isForVoiceUI) {
            notifyListenerOfHoverEvent(event);
            return true;
        }
        switch (event.getAction()) {
            case 7:
                return this.mKeyboardView.handleScrubGestureFrameHoverEventMove(event);
            case 8:
            case 9:
            default:
                return false;
            case 10:
                int hover_exit_adjustment_width_offset = this.mKeyboardView.getKeyboardAccessibilityState() != null ? this.mKeyboardView.getKeyboardAccessibilityState().getHoverExitWidthOffset() : 0;
                DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
                boolean isTouchUp = event.getY() > 0.0f && event.getY() < ((float) getHeight()) && event.getX() > ((float) hover_exit_adjustment_width_offset) && event.getX() < ((float) (display.widthPixels - hover_exit_adjustment_width_offset));
                boolean isExitFromBottom = event.getY() >= ((float) getHeight());
                this.mKeyboardView.handleScrubGestureFrameHoverEventExit(event, isTouchUp, isExitFromBottom);
                return true;
        }
    }

    private void setUpperScreenScrubGestureFrame() {
        if (this.mScrubGestureUpperScreenPopup == null) {
            this.mScrubGestureUpperScreenPopup = new PopupWindow(this);
        }
    }

    public void hideUpperScreenScrubGestureFrame(boolean aFlag) {
        if (this.mScrubGestureUpperScreenPopup != null) {
            this.mScrubGestureUpperScreenPopup.dismiss();
            if (aFlag) {
                this.mScrubGestureUpperScreenPopup = null;
            }
        }
    }

    private void showUpperScreenScrubGestureFrame(int x, int y, int w, int h) {
        if (this.mScrubGestureUpperScreenPopup != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mKeyboardView.measure(w, h);
            this.mScrubGestureUpperScreenPopup.update(x, y, w, h);
            this.mScrubGestureUpperScreenPopup.showAtLocation(this.mKeyboardView, 0, x, y);
        }
    }

    public void addUpperScreenScrubGestureFrame() {
        if (this.mScrubGestureUpperScreenPopup == null || !this.mScrubGestureUpperScreenPopup.isShowing()) {
            setUpperScreenScrubGestureFrame();
            int status_bar_height = IMEHandlerManager.getStatusBarHeight(this.mIme);
            DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
            int w = display.widthPixels;
            int upperScreenScrubGestureYOffset = (display.heightPixels - status_bar_height) - this.mKeyboardView.getKeyboard().getHeight();
            showUpperScreenScrubGestureFrame(0, -upperScreenScrubGestureYOffset, w, upperScreenScrubGestureYOffset);
        }
    }

    public void addGestureViewListener(IGestureViewListener listener) {
        if (this.gestureViewListeners == null) {
            this.gestureViewListeners = new ArrayList();
        }
        if (listener != null) {
            this.gestureViewListeners.add(listener);
        }
    }

    public void removeGestureViewListener(IGestureViewListener listener) {
        if (this.gestureViewListeners != null) {
            this.gestureViewListeners.remove(listener);
        }
    }

    public void removeAllGestureViewListener() {
        if (this.gestureViewListeners != null) {
            this.gestureViewListeners.clear();
            this.gestureViewListeners = null;
        }
    }

    public void notifyListenerOfHoverEvent(MotionEvent event) {
        if (this.gestureViewListeners != null) {
            Iterator<IGestureViewListener> it = this.gestureViewListeners.iterator();
            while (it.hasNext()) {
                it.next().onGestureViewHoverEvent(event);
            }
        }
    }

    public void useForVoiceUI(boolean value) {
        this.isForVoiceUI = value;
    }
}
