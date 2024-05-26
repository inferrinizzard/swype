package com.nuance.swype.input.emoji;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.emoji.EmojiPageView;
import com.nuance.swype.input.emoji.finger.FingerInfo;
import com.nuance.swype.input.emoji.finger.FingerStateListener;
import com.nuance.swype.input.emoji.finger.Fingerable;
import com.nuance.swype.input.emoji.util.AnimationState;
import com.nuance.swype.input.emoji.util.PopupManager;
import com.nuance.swype.input.emoji.util.PopupView;
import com.nuance.swype.input.emoji.util.PressStateAnimator;
import com.nuance.swype.input.emoji.util.Util;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.DrawDebug;
import com.nuance.swype.util.LogManager;
import java.util.Arrays;
import java.util.HashMap;

/* loaded from: classes.dex */
public class EmojiGridCell implements FingerStateListener, Fingerable, PressStateAnimator.Listener {
    private static final boolean enablePreScale = true;
    private final AnimationState animationState;
    private Rect bounds;
    private final int cellIndex;
    Emoji emoji;
    private EmojiCacheManager<String, Integer> emojiCacheManager;
    private boolean enablePopupView;
    private final EmojiPageView parent;
    private EmojiPopupView popupView;
    private boolean preScalePopupView;
    private PressStateAnimator pressAnimator;
    private FingerInfo.PressState pressState;
    private String text;
    private final int xCell;
    private int xOriginOffset;
    private final int xPos;
    private final int yCell;
    private final int yPos;
    protected static final LogManager.Log log = LogManager.getLog("EmojiGridCell");
    private static final LogManager.Trace trace = LogManager.getTrace();
    private static final HashMap<String, Rect> boundsCache = new HashMap<>();

    public EmojiGridCell(Context context, EmojiPageView parent, EmojiCategory cat, Emoji emoji, int cellIndex, int xCell, int yCell) {
        String code;
        this.enablePopupView = Build.VERSION.SDK_INT >= 11;
        this.preScalePopupView = false;
        this.pressState = FingerInfo.PressState.UNPRESSED;
        EmojiGridParams grid = parent.getParams();
        int width = grid.widthData.cellSize;
        int height = grid.heightData.cellSize;
        this.parent = parent;
        this.cellIndex = cellIndex;
        this.xCell = xCell;
        this.yCell = yCell;
        int advanceWidth = grid.widthData.margin + width;
        this.xPos = (xCell * advanceWidth) + grid.widthData.margin;
        int advanceHeight = grid.heightData.margin + height;
        this.yPos = (yCell * advanceHeight) + grid.heightData.margin;
        this.emoji = emoji;
        this.text = emoji.getEmojiDisplayCode();
        this.emojiCacheManager = EmojiCacheManager.from(context);
        if (emoji.isSkinToneSupport() && !cat.isRecentCategory() && (code = getDefaultSkinToneCode(parent, emoji)) != null) {
            this.text = code;
        }
        this.enablePopupView = UserPreferences.from(parent.getContext()).getShowPressDownPreview();
        this.animationState = new AnimationState(width / 2, height / 2);
        int dy = this.enablePopupView ? (int) ((-height) * 2.0f) : 0;
        PressStateAnimator.Builder builder = new PressStateAnimator.Builder(this, this.animationState);
        this.pressAnimator = builder.setEnableShortPressPopup(this.enablePopupView).setSize(width, height).startAt(this.xPos, this.yPos).movePopupBy(0, dy).create();
        measureText();
    }

    private String getDefaultSkinToneCode(EmojiPageView parent, Emoji emoji) {
        this.emoji = EmojiLoader.getDefaultSkinToneCode(parent.getContext(), emoji, this.emojiCacheManager);
        if (this.emoji == null) {
            return null;
        }
        return this.emoji.getEmojiDisplayCode();
    }

    public int getColumn() {
        return this.xCell;
    }

    public int getRow() {
        return this.yCell;
    }

    public int getLeft() {
        return this.xPos;
    }

    public int getTop() {
        return this.yPos;
    }

    public int getRight() {
        return this.xPos + getWidth();
    }

    public int getBottom() {
        return this.yPos + getHeight();
    }

    public float getCenterX() {
        return this.xPos + (getWidth() / 2.0f);
    }

    public float getCenterY() {
        return this.yPos + (getHeight() / 2.0f);
    }

    public int getWidth() {
        return this.parent.getParams().widthData.cellSize;
    }

    public int getHeight() {
        return this.parent.getParams().heightData.cellSize;
    }

    public String getText() {
        return this.text;
    }

    public Emoji getEmoji() {
        return this.emoji;
    }

    private Rect getCache(String text) {
        if (boundsCache != null) {
            return boundsCache.get(text);
        }
        return null;
    }

    private void putCache(String text, Rect rect) {
        if (boundsCache != null) {
            boundsCache.put(text, rect);
        }
    }

    private void measureText() {
        if (this.bounds == null) {
            EmojiGridParams params = this.parent.getParams();
            if (this.bounds == null) {
                this.bounds = new Rect();
                params.paint.getTextBounds(this.text, 0, this.text.length(), this.bounds);
                putCache(this.text, this.bounds);
            }
            this.xOriginOffset = params.getXOriginOffset(this.bounds, 1);
        }
    }

    public void getTransformedBounds(int[] bounds) {
        this.animationState.map(0, 0, getWidth(), getHeight(), bounds);
        Util.adjustRect(bounds, this.xPos, this.yPos);
    }

    @Override // com.nuance.swype.input.emoji.util.PressStateAnimator.Listener
    public void onAnimUpdate(PressStateAnimator animator) {
        if (this.popupView != null) {
            this.popupView.transform(animator);
        } else {
            this.parent.invalidate(this);
        }
    }

    @Override // com.nuance.swype.input.emoji.util.PressStateAnimator.Listener
    public void onAnimPressState(PressStateAnimator animator, FingerInfo.PressState pressState) {
        log.d("onAnimPressState(): called  : pressState: ", pressState.toString());
        switch (pressState) {
            case UNPRESSED:
                setPreScaleMode(false);
                return;
            case PRESSED:
                this.parent.notifyFeedback(this, EmojiPageView.EmojiFeedback.FEEDBACK_PRESS);
                return;
            case SHORT:
                setPreScaleMode(true);
                this.parent.notifyFeedback(this, EmojiPageView.EmojiFeedback.FEEDBACK_POPUP);
                return;
            default:
                return;
        }
    }

    private int[] confineAnimationEndPosition() {
        this.pressAnimator.adjustEndDelta(0, 0);
        int[] bounds = this.pressAnimator.getEndBounds();
        Util.adjustRect(bounds, this.xPos, this.yPos);
        return confineAnimationEndPosition(bounds);
    }

    private int[] confineAnimationEndPosition(View view) {
        this.pressAnimator.adjustEndDelta(0, 0);
        int leftPad = view.getPaddingLeft();
        int topPad = view.getPaddingTop();
        int rightPad = view.getPaddingRight();
        int botPad = view.getPaddingBottom();
        int[] bounds = this.pressAnimator.getEndBounds(leftPad, topPad, rightPad, botPad);
        Util.adjustRect(bounds, this.xPos - leftPad, this.yPos - topPad);
        return confineAnimationEndPosition(bounds);
    }

    private int[] getScaledBounds(float scale) {
        float[] cellRect = Util.allocRectF(getLeft(), getTop(), getRight(), getBottom());
        Matrix mat = new Matrix();
        mat.setScale(scale, scale, getCenterX(), getCenterY());
        mat.mapPoints(cellRect);
        return Util.allocRect(cellRect);
    }

    private int[] getPopupViewContentArea() {
        return this.preScalePopupView ? getScaledBounds(this.pressAnimator.getScaleEnd()) : Util.allocRect(getLeft(), getTop(), getRight(), getBottom());
    }

    private int[] confineAnimationEndPosition(int[] bounds) {
        int[] pos = CoordUtils.newInstance(bounds[0], bounds[1]);
        this.parent.getPopupManager().confineContentToOverlay(pos, bounds[2] - bounds[0], bounds[3] - bounds[1], 2);
        int dx = pos[0] - bounds[0];
        int dy = pos[1] - bounds[1];
        this.pressAnimator.adjustEndDelta(dx, dy);
        return bounds;
    }

    private void setPreScaleMode(boolean preScale) {
        if (this.enablePopupView) {
            log.d("setPreScaleMode(): pre-scale active: ", Boolean.valueOf(this.preScalePopupView));
            if (preScale) {
                if (!this.preScalePopupView) {
                    log.d("setPreScaleMode(): pre-scale ON");
                    this.preScalePopupView = true;
                    showPopupView();
                    return;
                }
                return;
            }
            if (this.preScalePopupView) {
                log.d("setPreScaleMode(): pre-scale OFF");
                this.preScalePopupView = false;
                showPopupView();
            }
        }
    }

    @Override // com.nuance.swype.input.emoji.util.PressStateAnimator.Listener
    public void onAnimStateChanged(PressStateAnimator animator, boolean active) {
        if (active) {
            if (this.enablePopupView) {
                startPopupMode();
                return;
            } else {
                confineAnimationEndPosition();
                return;
            }
        }
        stopPopupMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TextBitmap {
        private final int baselineOffset;
        private final Bitmap bitmap;
        private final Paint bitmapPaint = new Paint(1);
        private final int xOriginOffset;

        public TextBitmap(Rect bounds, String text, Paint textPaint) {
            EmojiGridCell.log.d("TextBitmap(): called  : bounds.top: ", bounds.top + ",bounds.left= " + bounds.left);
            this.baselineOffset = -bounds.top;
            this.xOriginOffset = -bounds.left;
            EmojiGridCell.log.d("TextBitmap(): called  : baselineOffset: ", this.baselineOffset + ",xOriginOffset= " + this.xOriginOffset);
            this.bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
            new Canvas(this.bitmap).drawText(text, this.xOriginOffset, this.baselineOffset, textPaint);
        }

        public void draw(Canvas canvas, int x, int y) {
            EmojiGridCell.log.d("TextBitmap(): called  : x: ", x + ",y= " + y);
            canvas.drawBitmap(this.bitmap, x - this.xOriginOffset, y - this.baselineOffset, this.bitmapPaint);
        }

        public void recycle() {
            this.bitmap.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EmojiPopupView extends PopupView {
        private TextBitmap textBitmap;

        @SuppressLint({"PrivateResource"})
        public EmojiPopupView(Context context, PopupManager popupManager) {
            super(context, popupManager, PopupView.Mode.MODE_DISABLE_HW_ACCEL);
            Drawable bg = IMEApplication.from(context).getThemedDrawable(R.attr.emojiPopupBackground);
            setBackgroundDrawable(bg);
        }

        @Override // com.nuance.swype.input.emoji.util.PopupView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return EmojiGridCell.this.pressState != FingerInfo.PressState.UNPRESSED;
        }

        private Path createTextPath(float scale) {
            Paint paint = EmojiGridCell.this.parent.getParams().paint;
            float oldSize = paint.getTextSize();
            float scaled = oldSize * scale;
            paint.setTextSize(scaled);
            Path path = new Path();
            paint.getTextPath(EmojiGridCell.this.text, 0, EmojiGridCell.this.text.length(), 0.0f, 0.0f, path);
            path.close();
            paint.setTextSize(oldSize);
            return path;
        }

        private boolean canDrawTextAtEndScale() {
            return !createTextPath(EmojiGridCell.this.pressAnimator.getScaleEnd()).isEmpty();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void preparePreScaleState() {
            if (EmojiGridCell.this.preScalePopupView) {
                if (!canDrawTextAtEndScale() && this.textBitmap == null) {
                    this.textBitmap = new TextBitmap(EmojiGridCell.this.bounds, EmojiGridCell.this.text, EmojiGridCell.this.parent.getParams().paint);
                    return;
                }
                return;
            }
            if (this.textBitmap != null) {
                this.textBitmap.recycle();
                this.textBitmap = null;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.save(1);
            int x = getPaddingLeft();
            int y = getPaddingTop();
            canvas.translate(x, y);
            if (EmojiGridCell.this.preScalePopupView) {
                float scale = EmojiGridCell.this.pressAnimator.getScaleEnd();
                canvas.scale(scale, scale);
            }
            EmojiGridCell.this.drawContent(canvas, this.textBitmap, EmojiGridCell.this.parent.getDrawDebug());
            canvas.restore();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.swype.input.emoji.util.PopupView
        public void onRemoved() {
            super.onRemoved();
            EmojiGridCell.this.stopPopupMode();
        }

        @TargetApi(11)
        public void transform(PressStateAnimator animator) {
            AnimationState animationState = animator.getAnimState();
            double popupProg = animator.getPopupProg();
            float viewScale = EmojiGridCell.this.preScalePopupView ? animator.getInvertedScale() : animationState.getScale();
            setScaleX(viewScale);
            setScaleY(viewScale);
            setRotation(animationState.getRotate());
            int[] pos = EmojiGridCell.this.parent.getPopupManager().getOffsetFromAnchorPos(EmojiGridCell.this.popupView, EmojiGridCell.this.xPos, EmojiGridCell.this.yPos);
            float tx = animationState.getTransX() + pos[0];
            float ty = animationState.getTransY() + pos[1];
            setTranslationX(tx);
            setTranslationY(ty);
            Drawable bg = getBackground();
            if (bg != null) {
                int alpha = (int) (0.0d + (255.0d * popupProg));
                bg.setAlpha(Math.min(255, Math.max(alpha, 0)));
            }
        }
    }

    private void showPopupView() {
        log.d("showPopupView(): called  : xPos: ", this.xPos + ",yPos= " + this.yPos);
        if (this.popupView == null) {
            this.popupView = new EmojiPopupView(this.parent.getContext(), this.parent.getPopupManager());
            confineAnimationEndPosition(this.popupView);
        }
        int[] bounds = getPopupViewContentArea();
        log.d("showPopupView(): called  : bounds: ", Arrays.toString(bounds));
        boolean added = this.popupView.show(bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
        this.popupView.setAnchor(this.xPos, this.yPos);
        this.popupView.transform(this.pressAnimator);
        this.popupView.preparePreScaleState();
        if (added) {
            this.parent.invalidate(this);
        }
    }

    private void showSkinTonePopupView() {
        log.d("showPopupView(): called  : xPos: ", this.xPos + ",yPos= " + this.yPos);
        if (this.pressState == FingerInfo.PressState.SHORT) {
            this.parent.showSkinTonePopup(this);
        }
    }

    public int getCellIndex() {
        return this.cellIndex;
    }

    private void startPopupMode() {
        if (this.popupView == null && this.enablePopupView) {
            this.preScalePopupView = false;
            showPopupView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopPopupMode() {
        if (this.popupView != null) {
            this.popupView.hide();
            this.popupView = null;
            this.parent.invalidate(this);
        }
    }

    public void draw(Canvas canvas, DrawDebug drawDebug) {
        if (this.popupView == null) {
            canvas.save(1);
            canvas.translate(this.xPos, this.yPos);
            canvas.concat(this.animationState.getMatrix());
            drawContent(canvas, null, drawDebug);
            canvas.restore();
            if (this.emoji.isSkinToneSupport()) {
                drawSkinToneIndicator(canvas);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawContent(Canvas canvas, TextBitmap textBitmap, DrawDebug drawDebug) {
        EmojiGridParams params = this.parent.getParams();
        if (textBitmap != null) {
            textBitmap.draw(canvas, this.xOriginOffset, params.baselineOffset);
        } else {
            canvas.drawText(this.text, this.xOriginOffset, params.baselineOffset, params.paint);
        }
        if (drawDebug != null) {
            drawDebug.drawOriginLines$38ef7fb0(canvas, getWidth(), getHeight(), this.xOriginOffset, params.baselineOffset);
            drawDebug.drawBorderOutline$1be95c50(canvas, getWidth(), getHeight());
        }
    }

    private void drawSkinToneIndicator(Canvas canvas) {
        EmojiGridParams params = this.parent.getParams();
        int y = -this.bounds.top;
        int width = params.widthData.cellSize / 2;
        int height = params.heightData.padding / 2;
        canvas.save(1);
        canvas.translate(getLeft(), getTop());
        Bitmap bitmap = BitmapFactory.decodeResource(this.parent.getContext().getResources(), R.drawable.emoji_icon_skins);
        canvas.drawBitmap(bitmap, this.xOriginOffset + width, (params.baselineOffset - y) - height, params.paint);
        canvas.restore();
    }

    private void setPressState(FingerInfo.PressState state) {
        log.d("setPressState(): ", this, "; state: ", state);
        this.pressState = state;
        if (this.pressState == FingerInfo.PressState.LONG) {
            this.parent.drawEmojiSelector(this);
        }
        if (this.emoji.isSkinToneSupport() && this.emoji.getEmojiSkinToneList().size() > 0) {
            showSkinTonePopupView();
        }
    }

    public String toString() {
        return "Emoji cell " + this.cellIndex;
    }

    @Override // com.nuance.swype.input.emoji.finger.Fingerable
    public boolean isDoubleTapSupported() {
        return false;
    }

    @Override // com.nuance.swype.input.emoji.finger.Fingerable
    public boolean isPressHoldSupported() {
        return true;
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
    public void onFingerPress(Fingerable object, FingerInfo info) {
        log.d("onFingerPress(): called >>>>>> ", this, "; state: ", this.pressState);
        FingerInfo.PressState pressState = info.getPressState();
        setPressState(pressState);
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
    public void onFingerMove(Fingerable object, FingerInfo info) {
        log.d("onFingerMove(): called >>>>>> x:: ", Float.valueOf(info.getPosX()), "; y: ", info.getPosY() + "offsetx::" + info.getOffsetX() + " offsety" + info.getOffsetY() + " , velocity x :: " + info.getVelX() + " , velocity y:: " + info.getVelX());
        this.parent.touchMoveHandle(object, info);
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
    public void onFingerRelease(Fingerable object, FingerInfo info, boolean isEscape) {
        log.d("onFingerRelease(): called", this, "; isEscape: ", Boolean.valueOf(isEscape));
        this.parent.setEmojiPressed(false);
        setPressState(FingerInfo.PressState.UNPRESSED);
        this.parent.setCurrentSkinTone(this);
        this.parent.invalidate();
    }

    @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
    public void onFingerCancel(Fingerable object, FingerInfo info) {
        log.d("onFingerCancel(): called 123>>>>>> ", this, "; state: ", this.pressState);
        setPressState(FingerInfo.PressState.UNPRESSED);
    }
}
