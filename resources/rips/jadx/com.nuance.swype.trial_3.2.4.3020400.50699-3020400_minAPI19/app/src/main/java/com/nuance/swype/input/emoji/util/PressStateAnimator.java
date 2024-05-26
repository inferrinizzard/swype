package com.nuance.swype.input.emoji.util;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.nuance.swype.input.emoji.finger.FingerInfo;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class PressStateAnimator implements SpringListener {
    private static final double BASE_RADIAN_COS_MOVEMENT = 4.71238898038469d;
    private static final double COS_MOVEMENT_RANGE = 3.141592653589793d;
    private static final float ROTATION_RANGE = 360.0f;
    private static final float rotationCount = 1.0f;
    private int activeSpringCount;
    private final AnimationState animationState;
    private int dx;
    private int dy;
    private final float horMovementRange;
    private final Listener listener;
    private final Params params;
    private Spring popupSpring;
    private Spring pressSpring;
    protected static final LogManager.Log log = LogManager.getLog("PressStateAnimator");
    private static final BaseSpringSystem springSystem = SpringSystem.create();

    /* loaded from: classes.dex */
    public interface Listener {
        void onAnimPressState(PressStateAnimator pressStateAnimator, FingerInfo.PressState pressState);

        void onAnimStateChanged(PressStateAnimator pressStateAnimator, boolean z);

        void onAnimUpdate(PressStateAnimator pressStateAnimator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Params {
        private static final SpringConfig defaultSpringConfig = SpringConfig.fromOrigamiTensionAndFriction(80.0d, 5.0d);
        private static final SpringConfig defaultpopupSpringConfig = SpringConfig.fromOrigamiTensionAndFriction(50.0d, 5.75d);
        public int[] begin;
        public boolean enableShortPressPopup;
        public int[] end;
        public int height;
        public float horMovementFactor;
        public float popupScaleAdd;
        public SpringConfig popupSpringConfig;
        public float pressScaleAdd;
        public SpringConfig pressSpringConfig;
        public int width;

        private Params() {
            this.pressScaleAdd = PressStateAnimator.rotationCount;
            this.popupScaleAdd = PressStateAnimator.rotationCount;
            this.horMovementFactor = 0.5f;
            this.pressSpringConfig = defaultSpringConfig;
            this.popupSpringConfig = defaultpopupSpringConfig;
            this.enableShortPressPopup = true;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private final AnimationState animationState;
        private final Listener listener;
        private final Params params = new Params();

        public Builder(Listener listener, AnimationState animationState) {
            this.listener = listener;
            this.animationState = animationState;
        }

        public Builder startAt(int xBegin, int yBegin) {
            this.params.begin = CoordUtils.newInstance(xBegin, yBegin);
            return this;
        }

        public Builder movePopupBy(int dx, int dy) {
            this.params.end = CoordUtils.newInstance(this.params.begin[0] + dx, this.params.begin[1] + dy);
            return this;
        }

        public Builder movePopupTo(int xEnd, int yEnd) {
            this.params.end = CoordUtils.newInstance(xEnd, yEnd);
            return this;
        }

        public Builder setSize(int width, int height) {
            this.params.width = width;
            this.params.height = height;
            return this;
        }

        public Builder setScaleValues(float pressScaleAdd, float popupScaleAdd) {
            this.params.pressScaleAdd = pressScaleAdd;
            this.params.popupScaleAdd = popupScaleAdd;
            return this;
        }

        public Builder setHorMovementFactor(float factor) {
            this.params.horMovementFactor = factor;
            return this;
        }

        public Builder setPressSpringConfig(SpringConfig springConfig) {
            this.params.pressSpringConfig = springConfig;
            return this;
        }

        public Builder setPopupSpringConfig(SpringConfig springConfig) {
            this.params.popupSpringConfig = springConfig;
            return this;
        }

        public Builder setEnableShortPressPopup(boolean enablePopup) {
            this.params.enableShortPressPopup = enablePopup;
            return this;
        }

        public PressStateAnimator create() {
            return new PressStateAnimator(this.listener, this.animationState, this.params);
        }
    }

    private PressStateAnimator(Listener listener, AnimationState animationState, Params params) {
        this.activeSpringCount = 0;
        this.listener = listener;
        this.animationState = animationState;
        this.params = params;
        this.horMovementRange = params.width * params.horMovementFactor;
        this.dx = params.end[0] - params.begin[0];
        this.dy = params.end[1] - params.begin[1];
    }

    public float getDeltaX() {
        return this.dx;
    }

    public float getDeltaY() {
        return this.dy;
    }

    public final void adjustEndDelta(int dx, int dy) {
        int deltaX = this.params.end[0] - this.params.begin[0];
        int deltaY = this.params.end[1] - this.params.begin[1];
        this.dx = deltaX + dx;
        this.dy = deltaY + dy;
    }

    private void initPressSpring() {
        if (this.pressSpring == null) {
            this.pressSpring = springSystem.createSpring().setSpringConfig(this.params.pressSpringConfig).addListener(this);
        }
    }

    private void initPopoupSpring() {
        if (this.popupSpring == null && this.params.enableShortPressPopup) {
            this.popupSpring = springSystem.createSpring().setSpringConfig(this.params.popupSpringConfig).addListener(this);
        }
    }

    private void initSprings() {
        initPressSpring();
        initPopoupSpring();
    }

    private void clearSprings() {
        if (this.pressSpring != null) {
            this.pressSpring.destroy();
            this.pressSpring = null;
        }
        if (this.popupSpring != null) {
            this.popupSpring.destroy();
            this.popupSpring = null;
        }
    }

    private void updateMatrix(double pressProg, double popupProg, boolean isPressed) {
        float scale = rotationCount + ((float) SpringUtil.mapValueFromRangeToRange(pressProg, 0.0d, 1.0d, 0.0d, this.params.pressScaleAdd)) + ((float) SpringUtil.mapValueFromRangeToRange(popupProg, 0.0d, 1.0d, 0.0d, this.params.popupScaleAdd));
        float rotate = ((float) popupProg) * ROTATION_RANGE;
        float xAdjust = (float) (Math.cos((COS_MOVEMENT_RANGE * popupProg) + BASE_RADIAN_COS_MOVEMENT) * this.horMovementRange);
        if (isPressed) {
            xAdjust = -xAdjust;
        }
        float transX = ((float) SpringUtil.mapValueFromRangeToRange(popupProg, 0.0d, 1.0d, 0.0d, this.dx)) + xAdjust;
        float transY = (float) SpringUtil.mapValueFromRangeToRange(popupProg, 0.0d, 1.0d, 0.0d, this.dy);
        this.animationState.transform(rotate, scale, transX, transY);
    }

    public float getInvertedScale() {
        return this.animationState.getScale() / getScaleEnd();
    }

    public float getInvertedScaleStart() {
        return rotationCount / getScaleEnd();
    }

    public float getScaleEnd() {
        float max = rotationCount + this.params.pressScaleAdd;
        if (this.params.enableShortPressPopup) {
            return max + this.params.popupScaleAdd;
        }
        return max;
    }

    public int[] getCurBounds(int leftPad, int topPad, int rightPad, int botPad) {
        int horPad = leftPad + rightPad;
        int verPad = topPad + botPad;
        return this.animationState.map(-leftPad, -topPad, this.params.width + horPad, this.params.height + verPad, null);
    }

    public int[] getEndBounds(int leftPad, int topPad, int rightPad, int botPad) {
        updateMatrix(1.0d, 1.0d, true);
        int[] out = getCurBounds(leftPad, topPad, rightPad, botPad);
        this.animationState.reset();
        return out;
    }

    public int[] getEndBounds() {
        updateMatrix(1.0d, 1.0d, true);
        int[] out = this.animationState.map(0, 0, this.params.width, this.params.height, null);
        this.animationState.reset();
        return out;
    }

    public int[] getStartBounds(int leftPad, int topPad, int rightPad, int botPad) {
        updateMatrix(0.0d, 0.0d, true);
        int[] out = getCurBounds(leftPad, topPad, rightPad, botPad);
        this.animationState.reset();
        return out;
    }

    public int[] getStartBounds() {
        updateMatrix(0.0d, 0.0d, true);
        int[] out = this.animationState.map(0, 0, this.params.width, this.params.height, null);
        this.animationState.reset();
        return out;
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringUpdate(Spring spring) {
        double popupProg = this.popupSpring != null ? this.popupSpring.getCurrentValue() : 0.0d;
        if (this.pressSpring != null) {
            boolean isPressed = this.pressSpring.getEndValue() != 0.0d;
            updateMatrix(this.pressSpring.getCurrentValue(), popupProg, isPressed);
            this.listener.onAnimUpdate(this);
        }
    }

    public AnimationState getAnimState() {
        return this.animationState;
    }

    public boolean isPopupActive() {
        return (this.popupSpring == null || (this.popupSpring.isAtRest() && this.popupSpring.getCurrentValue() == 0.0d)) ? false : true;
    }

    public double getPopupProg() {
        if (this.popupSpring != null) {
            return this.popupSpring.getCurrentValue();
        }
        return 0.0d;
    }

    public double getPressProg() {
        if (this.pressSpring != null) {
            return this.pressSpring.getCurrentValue();
        }
        return 0.0d;
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringAtRest(Spring spring) {
        if (spring.getEndValue() == 0.0d) {
            int i = this.activeSpringCount - 1;
            this.activeSpringCount = i;
            if (i == 0) {
                clearSprings();
                this.listener.onAnimStateChanged(this, false);
            }
        }
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringActivate(Spring spring) {
        if (spring.getEndValue() != 0.0d) {
            this.activeSpringCount++;
            if (this.activeSpringCount == 1) {
                this.listener.onAnimStateChanged(this, true);
            }
        }
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringEndStateChange(Spring spring) {
        if (this.activeSpringCount == 0 && spring.isAtRest()) {
            clearSprings();
        }
    }

    public void setPressState(FingerInfo.PressState state) {
        switch (state) {
            case UNPRESSED:
                this.listener.onAnimPressState(this, state);
                if (this.pressSpring != null) {
                    this.pressSpring.setEndValue(0.0d);
                }
                if (this.popupSpring != null) {
                    this.popupSpring.setEndValue(0.0d);
                    return;
                }
                return;
            case PRESSED:
                initSprings();
                this.listener.onAnimPressState(this, state);
                if (this.pressSpring != null) {
                    this.pressSpring.setEndValue(1.0d);
                    return;
                }
                return;
            case SHORT:
                if (this.params.enableShortPressPopup) {
                    this.listener.onAnimPressState(this, state);
                    if (this.popupSpring != null) {
                        this.popupSpring.setEndValue(1.0d);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }
}
