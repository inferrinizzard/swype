package com.nuance.swype.input;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AbstractTapDetector {
    private static final int MAX_DOUBLE_TAP_TIME = 667;
    private static final int MSG_CURSOR_UPDATE_PENDING = 3;
    private static final int MSG_DOUBLE_TAP = 2;
    private static final int MSG_SINGLE_TAP = 1;
    private static final int TAP_DELAY = 100;
    private static final int WAIT_TIME_AFTER_DOUBLE_TAP = 100;
    private static final int WAIT_TIME_AFTER_SINGLE_TAP = 50;
    private final Handler handler;
    private long lastTimestamp;
    private long nextAllowedTap;

    /* loaded from: classes.dex */
    public interface TapHandler {
        boolean onDoubleTap();

        boolean onSingleTap(boolean z, boolean z2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTapDetector(TapHandler[] tapHandlers) {
        this.handler = new TapMessageHandler(tapHandlers);
    }

    public void onExtractedTextClicked() {
        onTap();
    }

    public void onShowInputRequested(int flags, boolean configChange) {
    }

    public void onViewClicked(boolean focusChanged) {
    }

    public void onStartInput() {
    }

    public void onInitializeInterface() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean onTap() {
        long currentTimestamp = SystemClock.uptimeMillis();
        boolean send = currentTimestamp > this.nextAllowedTap;
        if (send) {
            if (currentTimestamp - this.lastTimestamp < 667) {
                this.handler.sendEmptyMessageDelayed(2, 100L);
                setNextAllowedTapTime(currentTimestamp + 100);
            } else {
                this.handler.sendEmptyMessageDelayed(1, 100L);
                this.handler.removeMessages(3);
                this.handler.sendEmptyMessageDelayed(3, 100L);
                setNextAllowedTapTime(50 + currentTimestamp);
            }
        }
        this.lastTimestamp = currentTimestamp;
        return send;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNextAllowedTapTime(long nextAllowed) {
        this.nextAllowedTap = Math.max(this.nextAllowedTap, nextAllowed);
    }

    /* loaded from: classes.dex */
    private static class TapMessageHandler extends Handler {
        private final List<WeakReference<TapHandler>> tapHandlersRef = new ArrayList();

        public TapMessageHandler(TapHandler[] tapHandlers) {
            for (TapHandler handler : tapHandlers) {
                this.tapHandlersRef.add(new WeakReference<>(handler));
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    boolean reSyncCache = hasMessages(3);
                    if (reSyncCache) {
                        removeMessages(3);
                    }
                    Iterator<WeakReference<TapHandler>> it = this.tapHandlersRef.iterator();
                    while (it.hasNext()) {
                        TapHandler handler = it.next().get();
                        if (handler != null && handler.onSingleTap(reSyncCache, false)) {
                            return;
                        }
                    }
                    return;
                case 2:
                    Iterator<WeakReference<TapHandler>> it2 = this.tapHandlersRef.iterator();
                    while (it2.hasNext()) {
                        TapHandler handler2 = it2.next().get();
                        if (handler2 != null && handler2.onDoubleTap()) {
                            return;
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public boolean tapDetected() {
        this.handler.removeMessages(3);
        return this.handler.hasMessages(1) || this.handler.hasMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean singleTapDetected() {
        return this.handler.hasMessages(1);
    }
}
