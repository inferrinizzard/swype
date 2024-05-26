package com.nuance.swype.input.korean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.TwoFingerGestureDetector;
import com.nuance.swype.input.settings.InputPrefs;

/* loaded from: classes.dex */
public class KoreanHandWritingView extends HandWritingView {
    private static final int HIDE_IME = -2;
    private static final int HWR_DISPACH_EVENT_INIT = -1;
    private static final int HWR_DISPACH_EVENT_NORMAL = 1;
    private static final int HWR_DISPACH_EVENT_SWITCH = 2;
    private static final int PEN_WIDTH = 6;
    private static final long TAP_MINIMUM_TIME = 500;
    private static final int TAP_OFFSET = 550;
    private static final int WRITING_MODE_TEXT = 0;
    private static int mSize = 0;
    private final Paint mDemoPaint;
    private long mFirstDownTime;
    private boolean mIsFullScreen;
    private boolean mNewSession;
    private int mOffsetSquare;
    private int mOriginalX;
    private int mOriginalY;
    private Paint mPaint;
    private int mPenColor;
    private int mPointInInvalidArea;
    private final Stroke mStroke;
    private TwoFingerGestureDetector mTwoFingerGestureDetector;
    private Paint m_pntText;
    private int writingPadWidth;

    public KoreanHandWritingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mTwoFingerGestureDetector = new TwoFingerGestureDetector();
    }

    public void setMultitouchListener(TwoFingerGestureDetector.OnScrollListener aListener) {
        this.mTwoFingerGestureDetector.setScrollListener(aListener);
    }

    @SuppressLint({"PrivateResource"})
    public KoreanHandWritingView(Context context, AttributeSet attrs, int def) {
        super(context, attrs, def);
        this.mIsFullScreen = false;
        this.mPaint = null;
        this.m_pntText = null;
        this.mPointInInvalidArea = -1;
        this.mTwoFingerGestureDetector = null;
        this.mFirstDownTime = 0L;
        this.mOffsetSquare = 0;
        this.mOriginalX = 0;
        this.mOriginalY = 0;
        this.mNewSession = true;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        IMEApplication app = IMEApplication.from(context);
        this.mPenColor = app.getThemedColor(R.attr.traceColor);
        this.mDemoPaint = new Paint();
        this.mDemoPaint.setStrokeWidth(0.0f);
        this.mDemoPaint.setTextSize(6.0f * dm.density);
        this.mDemoPaint.setAntiAlias(true);
        this.mDemoPaint.setTypeface(Typeface.SANS_SERIF);
        this.mPaint = new Paint(1);
        this.mPaint.setColor(this.mPenColor);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(mSize);
        this.m_pntText = new Paint();
        this.m_pntText.setAntiAlias(true);
        this.m_pntText.setColor(app.getThemedColor(R.attr.handwritingModeDisplayColor));
        this.m_pntText.setStrokeWidth(0.0f);
        this.m_pntText.setTextSize((int) (24.0f * dm.density));
        this.m_pntText.setTypeface(Typeface.SANS_SERIF);
        this.mStroke = Stroke.create(6);
    }

    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    @SuppressLint({"PrivateResource"})
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        KoreanHandWritingInputView v = (KoreanHandWritingInputView) this.mOnWritingActionListener;
        int mode = v.getHandWritingMode();
        for (Path path : this.mStroke.getPaths()) {
            canvas.drawPath(path, this.mPaint);
        }
        Resources res = getResources();
        CharSequence label = mode == 0 ? res.getText(R.string.korean) : res.getText(R.string.label_symbol_key);
        int textWidth = (int) this.m_pntText.measureText(label.toString());
        int textSize = (int) (this.m_pntText.getTextSize() + this.m_pntText.ascent() + this.m_pntText.descent());
        int x = (getWritingPadWidth() - textWidth) / 2;
        int y = getHeight() - textSize;
        if (v.mKeyboardHeight > 0 && this.mIsFullScreen && UserPreferences.from(getContext()).getKeyboardDockingMode() != KeyboardEx.KeyboardDockMode.MOVABLE_MINI) {
            canvas.drawText(label.toString(), x, y - v.mKeyboardHeight, this.m_pntText);
        } else {
            canvas.drawText(label.toString(), x, y, this.m_pntText);
        }
    }

    public void setWidth(int width) {
        this.writingPadWidth = width;
    }

    private int getWritingPadWidth() {
        return this.writingPadWidth > 0 ? this.writingPadWidth : getWidth();
    }

    public void setPointStatus(int status) {
        this.mPointInInvalidArea = status;
    }

    private boolean isFullScreen() {
        return this.mIsFullScreen;
    }

    public void clearArcs() {
        invalidate();
    }

    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            this.mPointInInvalidArea = -1;
        }
        this.mIsFullScreen = fullScreen;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if (this.mTwoFingerGestureDetector.onTouchEvent(me)) {
            this.mFaddingStrokeQueue.startFading();
            this.mStroke.clear();
            invalidate();
        } else {
            if (isFullScreen()) {
                if (this.mPointInInvalidArea == 2) {
                    this.mInSelectionAreaListener.transferKeyEvent(me);
                    if (me.getAction() == 1 || me.getAction() == 3) {
                        this.mPointInInvalidArea = -1;
                        this.mInSelectionAreaListener.resetArea(-1);
                    }
                } else if (this.mPointInInvalidArea == 1) {
                    if (!this.mInSelectionAreaListener.isSpeechPopupShowing()) {
                        if (me.getAction() == 1 || me.getAction() == 3) {
                            this.mPointInInvalidArea = -1;
                            if (me.getEventTime() - this.mFirstDownTime < TAP_MINIMUM_TIME && getMaxOffsetSquare() < TAP_OFFSET) {
                                this.mInSelectionAreaListener.resetArea(-2);
                                this.mFaddingStrokeQueue.clearAll();
                                this.mNewSession = true;
                                invalidate();
                            }
                        }
                    }
                }
            }
            this.mStroke.handleMotionEvent(me);
            switch (me.getAction()) {
                case 0:
                    int x = (int) me.getX();
                    int y = (int) me.getY();
                    if (isFullScreen()) {
                        if (this.mInSelectionAreaListener.pointInSelectionArea((int) me.getX(), (int) me.getY())) {
                            if (!this.mNewSession) {
                                this.mPointInInvalidArea = 1;
                            } else {
                                this.mPointInInvalidArea = 2;
                                this.mInSelectionAreaListener.transferKeyEvent(me);
                                break;
                            }
                        } else {
                            this.mPointInInvalidArea = 1;
                            if (this.mNewSession) {
                                this.mFirstDownTime = me.getEventTime();
                                this.mOriginalX = x;
                                this.mOriginalY = y;
                                this.mOffsetSquare = 0;
                            }
                        }
                    }
                    if (this.mNewSession) {
                        this.mFaddingStrokeQueue.clearAll();
                        this.mNewSession = false;
                    }
                    this.mOnWritingActionListener.penDown(this);
                    this.mFaddingStrokeQueue.pauseFading();
                    invalidate();
                    break;
                case 1:
                    for (Path path : this.mStroke.getPaths()) {
                        this.mFaddingStrokeQueue.add(path);
                    }
                    this.mFaddingStrokeQueue.startActionFading();
                    this.mFaddingStrokeQueue.startFading();
                    this.mOnWritingActionListener.penUp(this.mStroke.getArcs(), this);
                    this.mStroke.clear();
                    break;
                case 2:
                    int x2 = (int) me.getX();
                    int y2 = (int) me.getY();
                    if (isFullScreen()) {
                        getMaxOffsetSquare(x2, y2, me);
                    }
                    invalidate();
                    break;
                default:
                    invalidate();
                    break;
            }
        }
        return true;
    }

    private int getMaxOffsetSquare() {
        return this.mOffsetSquare;
    }

    private void getMaxOffsetSquare(int x, int y, MotionEvent me) {
        if (me.getEventTime() - this.mFirstDownTime < TAP_MINIMUM_TIME) {
            int xOffsetSquare = (this.mOriginalX - x) * (this.mOriginalX - x);
            int yOffsetSquare = (this.mOriginalY - y) * (this.mOriginalY - y);
            int offsetSquare = xOffsetSquare + yOffsetSquare;
            if (offsetSquare > this.mOffsetSquare) {
                this.mOffsetSquare = offsetSquare;
            }
        }
    }

    public void setNewSession(boolean newSession) {
        this.mNewSession = newSession;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        UserPreferences userPrefs = UserPreferences.from(getContext());
        this.mPenColor = InputPrefs.getPenColor(userPrefs, UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.mPaint.setColor(this.mPenColor);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mSize = InputPrefs.getPenSize(userPrefs, UserPreferences.HWR_PEN_SIZE, (int) (6.0f * dm.density));
        this.mPaint.setStrokeWidth(mSize);
        super.onWindowVisibilityChanged(visibility);
    }
}
