package com.nuance.swype.input.emoji.finger;

import android.content.Context;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.emoji.finger.FingerState;
import com.nuance.swype.input.emoji.finger.Fingerable;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class FingerStateManager implements View.OnTouchListener {
    protected static final LogManager.Log log = LogManager.getLog("FingerStateManager");
    private final FingerState.Params fingerStateParams;
    protected SparseArray<Finger> fingers;
    private FingerStateListener listener;
    private Fingerable.FingerableMapper mapper;

    /* loaded from: classes.dex */
    public static class SimpleMapper implements Fingerable.FingerableMapper {
        private Fingerable globalItem;
        private final boolean isDoubleTapSupported;

        private SimpleMapper(Context context, boolean isDoubleTapSupported) {
            this.globalItem = new Fingerable() { // from class: com.nuance.swype.input.emoji.finger.FingerStateManager.SimpleMapper.1
                @Override // com.nuance.swype.input.emoji.finger.Fingerable
                public boolean isDoubleTapSupported() {
                    return SimpleMapper.this.isDoubleTapSupported;
                }

                @Override // com.nuance.swype.input.emoji.finger.Fingerable
                public boolean isPressHoldSupported() {
                    return true;
                }
            };
            this.isDoubleTapSupported = isDoubleTapSupported;
        }

        @Override // com.nuance.swype.input.emoji.finger.Fingerable.FingerableMapper
        public Fingerable map(float x, float y) {
            return this.globalItem;
        }
    }

    public FingerStateManager(Context context, FingerState.Params params, FingerStateListener listener, Fingerable.FingerableMapper mapper) {
        this.fingers = new SparseArray<>();
        this.mapper = mapper;
        this.listener = listener;
        this.fingerStateParams = params;
    }

    public FingerStateManager(Context context, FingerStateListener listener, Fingerable.FingerableMapper mapper) {
        this(context, createFingerStateParams(context), listener, mapper);
    }

    private static FingerState.Params createFingerStateParams(Context context) {
        FingerState.Params defaults = FingerState.Params.createDefault(context);
        return new FingerState.Params.Builder(defaults).setTrackVelocity(false).build();
    }

    public FingerStateManager(Context context, FingerStateListener listener) {
        this(context, listener, new SimpleMapper(context, false));
    }

    protected Finger getFinger(int pointerId) {
        Finger finger = this.fingers.get(pointerId);
        if (finger == null) {
            Finger finger2 = new Finger(pointerId);
            log.d("onTouch(): mapping pointer id ", Integer.valueOf(pointerId), " to new finger instance ", finger2);
            this.fingers.put(pointerId, finger2);
            return finger2;
        }
        return finger;
    }

    private void notifyFingerOnTouch(int pointerIndex, int action, MotionEvent event) {
        int pointerId = event.getPointerId(pointerIndex);
        if (pointerId >= 0) {
            Finger finger = getFinger(pointerId);
            Fingerable fingerable = 3 != action ? this.mapper.map(event.getX(pointerIndex), event.getY(pointerIndex)) : null;
            finger.onTouch(fingerable, action, event);
        }
    }

    public void cancelAll() {
        int count = this.fingers.size();
        for (int idx = 0; idx < count; idx++) {
            this.fingers.valueAt(idx).cancel();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction() & 255;
        switch (action) {
            case 0:
            case 1:
            case 5:
                notifyFingerOnTouch(event.getActionIndex(), action, event);
                return true;
            case 2:
                onMove(event);
                return true;
            case 3:
                int pointerCount = event.getPointerCount();
                for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
                    notifyFingerOnTouch(pointerIndex, action, event);
                }
                return true;
            case 4:
            default:
                log.d("onTouch(): ignoring action: ", Integer.valueOf(action));
                return true;
        }
    }

    void onMove(MotionEvent ev) {
        int pointerCount = ev.getPointerCount();
        for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
            getFinger(ev.getPointerId(pointerIndex)).updateVelocityTracker(ev);
        }
        int historySize = ev.getHistorySize();
        for (int idxHistory = 0; idxHistory < historySize; idxHistory++) {
            for (int idxPointer = 0; idxPointer < pointerCount; idxPointer++) {
                Finger finger = getFinger(ev.getPointerId(idxPointer));
                float xPos = ev.getHistoricalX(idxPointer, idxHistory);
                float yPos = ev.getHistoricalY(idxPointer, idxHistory);
                Fingerable fingerable = this.mapper.map(xPos, yPos);
                finger.onMove(fingerable, xPos, yPos, true);
            }
        }
        for (int pointerIndex2 = 0; pointerIndex2 < pointerCount; pointerIndex2++) {
            Finger finger2 = getFinger(ev.getPointerId(pointerIndex2));
            float xPos2 = ev.getX(pointerIndex2);
            float yPos2 = ev.getY(pointerIndex2);
            Fingerable fingerable2 = this.mapper.map(xPos2, yPos2);
            finger2.onMove(fingerable2, xPos2, yPos2, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Finger implements FingerStateListener {
        private FingerState fingerState;
        private int pointerId;

        public Finger(int pointerId) {
            this.pointerId = pointerId;
            this.fingerState = new FingerState(this, pointerId, FingerStateManager.this.fingerStateParams);
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerPress(Fingerable object, FingerInfo info) {
            if (object instanceof FingerStateListener) {
                ((FingerStateListener) object).onFingerPress(object, info);
            }
            if (FingerStateManager.this.listener != null) {
                FingerStateManager.this.listener.onFingerPress(object, info);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerMove(Fingerable object, FingerInfo info) {
            if (object instanceof FingerStateListener) {
                ((FingerStateListener) object).onFingerMove(object, info);
            }
            if (FingerStateManager.this.listener != null) {
                FingerStateManager.this.listener.onFingerMove(object, info);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerRelease(Fingerable object, FingerInfo info, boolean isEscape) {
            if (object instanceof FingerStateListener) {
                ((FingerStateListener) object).onFingerRelease(object, info, isEscape);
            }
            if (FingerStateManager.this.listener != null) {
                FingerStateManager.this.listener.onFingerRelease(object, info, isEscape);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerCancel(Fingerable object, FingerInfo info) {
            if (object instanceof FingerStateListener) {
                ((FingerStateListener) object).onFingerCancel(object, info);
            }
            if (FingerStateManager.this.listener != null) {
                FingerStateManager.this.listener.onFingerCancel(object, info);
            }
        }

        public void cancel() {
            this.fingerState.onPointerCancel();
        }

        public void onTouch(Fingerable object, int action, MotionEvent event) {
            switch (action) {
                case 0:
                case 5:
                    this.fingerState.onPointerDown(object, event);
                    return;
                case 1:
                    this.fingerState.onPointerRelease(object, event);
                    return;
                case 2:
                    return;
                case 3:
                    cancel();
                    return;
                case 4:
                default:
                    FingerStateManager.log.d("onTouch(): unrecognized action: " + action);
                    return;
            }
        }

        public void updateVelocityTracker(MotionEvent ev) {
            this.fingerState.onVelocityData(ev);
        }

        public void onMove(Fingerable dummyItem, float x, float y, boolean isHistorical) {
            this.fingerState.onPointerMove(dummyItem, x, y, isHistorical);
        }
    }
}
