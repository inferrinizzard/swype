package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.TwoFingerGestureDetector;
import com.nuance.swype.input.settings.InputPrefs;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class ChineseHandWritingView extends HandWritingView {
    private static final int HIDE_IME = -2;
    private static final int HWR_DISPACH_EVENT_INIT = -1;
    private static final int HWR_DISPACH_EVENT_NORMAL = 1;
    private static final int HWR_DISPACH_EVENT_SWITCH = 2;
    private static final long TAP_MINIMUM_TIME = 500;
    private static final int TAP_OFFSET = 550;
    protected static final boolean mcrEnabled = true;
    private int digitSymbolLabelWidth;
    private boolean isLandscape;
    private float labelSize;
    private RectF mDirtyRect;
    private long mFirstDownTime;
    private boolean mIntegratedEnabled;
    private boolean mIsFullScreen;
    private float mLastTouchX;
    private float mLastTouchY;
    private final Path mLinePath;
    private boolean mNewSession;
    private int mOffsetSquare;
    private int mOriginalX;
    private int mOriginalY;
    private final Path mPath;
    private int mPointInInvalidArea;
    private final List<Point> mPoints;
    private Stroke mStroke;
    private TwoFingerGestureDetector mTwoFingerGestureDetector;
    private Paint m_pntSepLine;
    private Paint m_pntText;
    private Paint m_pntWrite;
    private int writingPadWidthOnResize;

    public ChineseHandWritingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mTwoFingerGestureDetector = new TwoFingerGestureDetector();
    }

    public void setMultitouchListener(TwoFingerGestureDetector.OnScrollListener aListener) {
        this.mTwoFingerGestureDetector.setScrollListener(aListener);
    }

    @SuppressLint({"PrivateResource"})
    public ChineseHandWritingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPoints = new LinkedList();
        this.mPath = new Path();
        this.mLinePath = new Path();
        this.m_pntWrite = null;
        this.m_pntText = null;
        this.m_pntSepLine = null;
        this.mIntegratedEnabled = false;
        this.mNewSession = true;
        this.mIsFullScreen = false;
        this.mTwoFingerGestureDetector = null;
        this.mDirtyRect = new RectF();
        this.mPointInInvalidArea = -1;
        this.mFirstDownTime = 0L;
        this.mOffsetSquare = 0;
        this.mOriginalX = 0;
        this.mOriginalY = 0;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int hmdColor = IMEApplication.from(context).getThemedColor(R.attr.handwritingModeDisplayColor);
        this.m_pntSepLine = new Paint();
        this.m_pntSepLine.setStyle(Paint.Style.STROKE);
        this.m_pntSepLine.setStrokeJoin(Paint.Join.ROUND);
        this.m_pntSepLine.setStrokeCap(Paint.Cap.ROUND);
        this.m_pntSepLine.setStrokeWidth(dm.density * 2.0f);
        this.m_pntSepLine.setColor(hmdColor);
        this.m_pntSepLine.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 10.0f));
        this.m_pntText = new Paint();
        this.m_pntText.setStrokeWidth(0.0f);
        if (this.labelSize > 0.0f) {
            this.m_pntText.setTextSize(this.labelSize);
        } else {
            this.m_pntText.setTextSize(getResources().getDimensionPixelSize(R.dimen.hand_writing_mode_text_size));
        }
        this.digitSymbolLabelWidth = (int) this.m_pntText.measureText(context.getResources().getText(R.string.handwriting_123_mode_simp).toString());
        this.m_pntText.setAntiAlias(true);
        Typeface italic = Typeface.create(Typeface.SERIF, 2);
        this.m_pntText.setTypeface(italic);
        this.m_pntText.setColor(hmdColor);
        this.m_pntWrite = new Paint(1);
        this.m_pntWrite.setStyle(Paint.Style.STROKE);
        this.m_pntWrite.setStrokeJoin(Paint.Join.ROUND);
        this.m_pntWrite.setStrokeCap(Paint.Cap.ROUND);
        this.labelSize = context.getResources().getDisplayMetrics().density * 20.0f;
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.m_pntWrite.setColor(penColor);
        boolean isTablet = IMEApplication.from(getContext()).isScreenLayoutTablet();
        int penSize = InputPrefs.getPenSize(UserPreferences.from(getContext()), UserPreferences.HWR_PEN_SIZE, (int) ((isTablet ? 7 : 5) * dm.density));
        this.m_pntWrite.setStrokeWidth(penSize);
        this.mStroke = Stroke.create(penSize);
    }

    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        Path[] paths = this.mStroke.getPaths();
        for (Path path : paths) {
            c.drawPath(path, this.m_pntWrite);
        }
        onBufferDraw(c);
    }

    public void setPointStatus(int status) {
        this.mPointInInvalidArea = status;
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if (this.mStroke != null) {
            this.mStroke.handleMotionEvent(me);
        }
        if (this.mTwoFingerGestureDetector.onTouchEvent(me)) {
            if (this.mStroke != null) {
                this.mStroke.clear();
            }
            reset();
            this.mNewSession = true;
            invalidate();
            return true;
        }
        if (isFullScreen()) {
            if (this.mPointInInvalidArea == 2) {
                this.mInSelectionAreaListener.transferKeyEvent(me);
                if (me.getAction() != 1 && me.getAction() != 3) {
                    return true;
                }
                this.mPointInInvalidArea = -1;
                this.mInSelectionAreaListener.resetArea(-1);
                if (this.mStroke != null) {
                    this.mStroke.clear();
                }
                reset();
                return true;
            }
            if (this.mPointInInvalidArea == 1) {
                if (this.mInSelectionAreaListener.isSpeechPopupShowing()) {
                    return true;
                }
                if (me.getAction() == 1 || me.getAction() == 3) {
                    this.mPointInInvalidArea = -1;
                    if (me.getEventTime() - this.mFirstDownTime < TAP_MINIMUM_TIME && getMaxOffsetSquare() < TAP_OFFSET) {
                        this.mInSelectionAreaListener.resetArea(-2);
                        if (this.mStroke != null) {
                            this.mStroke.clear();
                        }
                        reset();
                        this.mNewSession = true;
                        invalidate();
                        return true;
                    }
                }
            }
        }
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
                            return true;
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
                    reset();
                    this.mNewSession = false;
                }
                this.mOnWritingActionListener.penDown(this);
                this.mPath.moveTo(x, y);
                addArcPoint(x, y);
                this.mLastTouchX = x;
                this.mLastTouchY = y;
                this.mFaddingStrokeQueue.pauseFading();
                invalidate();
                return true;
            case 1:
                int x2 = (int) me.getX();
                int y2 = (int) me.getY();
                resetDirtyRect(x2, y2);
                if (this.mPoints.size() <= 1) {
                    this.mPath.lineTo(x2 + 1, y2 + 1);
                    addArcPoint(x2 + 1, y2 + 1);
                }
                this.mPoints.add(new Point(0, 0));
                Path[] paths = this.mStroke.getPaths();
                for (Path path : paths) {
                    this.mFaddingStrokeQueue.add(path);
                }
                this.mOnWritingActionListener.penUp(this.mPoints, this);
                this.mLastTouchX = x2;
                this.mLastTouchY = y2;
                this.mPoints.clear();
                this.mStroke.clear();
                invalidate();
                return true;
            case 2:
                int x3 = (int) me.getX();
                int y3 = (int) me.getY();
                resetDirtyRect(x3, y3);
                int points = me.getHistorySize();
                if (points != 0) {
                    for (int i = 0; i < points; i++) {
                        x3 = (int) me.getHistoricalX(i);
                        y3 = (int) me.getHistoricalY(i);
                        this.mPath.lineTo(x3, y3);
                        addArcPoint(x3, y3);
                        if (isFullScreen()) {
                            getMaxOffsetSquare(x3, y3, me);
                        }
                    }
                } else {
                    x3 = (int) me.getX();
                    y3 = (int) me.getY();
                    this.mPath.lineTo(x3, y3);
                    addArcPoint(x3, y3);
                    if (isFullScreen()) {
                        getMaxOffsetSquare(x3, y3, me);
                    }
                }
                this.mLastTouchX = x3;
                this.mLastTouchY = y3;
                int pen_width = (int) this.m_pntWrite.getStrokeWidth();
                invalidate((int) (this.mDirtyRect.left - pen_width), (int) (this.mDirtyRect.top - pen_width), (int) (this.mDirtyRect.right + pen_width), (int) (this.mDirtyRect.bottom + pen_width));
                invalidate();
                return true;
            default:
                invalidate();
                return false;
        }
    }

    public int getMaxOffsetSquare() {
        return this.mOffsetSquare;
    }

    public void getMaxOffsetSquare(int x, int y, MotionEvent me) {
        if (me.getEventTime() - this.mFirstDownTime < TAP_MINIMUM_TIME) {
            int xOffsetSquare = (this.mOriginalX - x) * (this.mOriginalX - x);
            int yOffsetSquare = (this.mOriginalY - y) * (this.mOriginalY - y);
            int offsetSquare = xOffsetSquare + yOffsetSquare;
            if (offsetSquare > this.mOffsetSquare) {
                this.mOffsetSquare = offsetSquare;
            }
        }
    }

    public void clearArcs() {
        this.mPath.reset();
        if (!this.mPoints.isEmpty()) {
            Iterator<Point> it = this.mPoints.iterator();
            if (it.hasNext()) {
                Point pt = it.next();
                this.mPath.moveTo(pt.x, pt.y);
            }
            while (it.hasNext()) {
                Point pt2 = it.next();
                this.mPath.lineTo(pt2.x, pt2.y);
            }
        }
        invalidate();
    }

    public void clearLinePath() {
        this.mLinePath.reset();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.m_pntWrite.setColor(penColor);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        boolean isTablet = IMEApplication.from(getContext()).isScreenLayoutTablet();
        int penSize = InputPrefs.getPenSize(UserPreferences.from(getContext()), UserPreferences.HWR_PEN_SIZE, (int) ((isTablet ? 7 : 5) * dm.density));
        this.m_pntWrite.setStrokeWidth(penSize);
        super.onWindowVisibilityChanged(visibility);
    }

    public void setIntegratedEnabled(boolean integrated) {
        this.mIntegratedEnabled = integrated;
        this.isLandscape = getContext().getResources().getConfiguration().orientation == 2;
    }

    public void setNewSession(boolean newSession) {
        this.mNewSession = newSession;
    }

    private void onBufferDraw(Canvas c) {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0) {
            if (isFullScreen()) {
                ChineseFSHandWritingInputView v = (ChineseFSHandWritingInputView) this.mOnWritingActionListener;
                if (!this.mIntegratedEnabled) {
                    c.drawLine(width / 2.0f, height / 5.0f, width / 2.0f, height, this.m_pntText);
                    if (!this.isLandscape) {
                        int textSize = (int) (this.m_pntText.getTextSize() + this.m_pntText.ascent() + this.m_pntText.descent());
                        if (v.mKeyboardHeight > 0) {
                            c.drawText(v.mStringHandWritingABCMode, 5.0f, (height - v.mKeyboardHeight) - (textSize * 5), this.m_pntText);
                        } else {
                            c.drawText(v.mStringHandWritingABCMode, 5.0f, (height * 5.0f) / 6.0f, this.m_pntText);
                        }
                        if (v.mKeyboardHeight > 0) {
                            c.drawText(v.mStringHandWriting123Mode, (width - this.digitSymbolLabelWidth) - 5, (height - v.mKeyboardHeight) - (textSize * 5), this.m_pntText);
                            return;
                        } else {
                            c.drawText(v.mStringHandWriting123Mode, (width - this.digitSymbolLabelWidth) - 5, (height * 5.0f) / 6.0f, this.m_pntText);
                            return;
                        }
                    }
                    int textSize2 = (int) (this.m_pntText.getTextSize() + this.m_pntText.ascent() + this.m_pntText.descent());
                    if (v.mKeyboardHeight > 0) {
                        c.drawText(v.mStringHandWritingABCMode, 5.0f, (height - v.mKeyboardHeight) - (textSize2 * 5), this.m_pntText);
                    } else {
                        c.drawText(v.mStringHandWritingABCMode, 5.0f, height - textSize2, this.m_pntText);
                    }
                    if (v.mKeyboardHeight > 0) {
                        c.drawText(v.mStringHandWriting123Mode, (width - this.digitSymbolLabelWidth) - 5, (height - v.mKeyboardHeight) - (textSize2 * 5), this.m_pntText);
                        return;
                    } else {
                        c.drawText(v.mStringHandWriting123Mode, (width - this.digitSymbolLabelWidth) - 5, height - textSize2, this.m_pntText);
                        return;
                    }
                }
                return;
            }
            ChineseHandWritingInputView v2 = (ChineseHandWritingInputView) this.mOnWritingActionListener;
            if (this.writingPadWidthOnResize != width) {
                this.mLinePath.reset();
            }
            this.writingPadWidthOnResize = width;
            if (!this.mIntegratedEnabled) {
                c.drawLine(width / 2.0f, height / 3.0f, width / 2.0f, height, this.m_pntText);
                if (!this.isLandscape) {
                    c.drawText(v2.mStringHandWritingABCMode, 20.0f, height - 20, this.m_pntText);
                    c.drawText(v2.mStringHandWriting123Mode, ((width - this.digitSymbolLabelWidth) - v2.getCharacterListWidth()) - 20, height - 20, this.m_pntText);
                } else {
                    int textSize3 = (int) (this.m_pntText.getTextSize() + this.m_pntText.ascent() + this.m_pntText.descent());
                    c.drawText(v2.mStringHandWritingABCMode, 20.0f, height - textSize3, this.m_pntText);
                    c.drawText(v2.mStringHandWriting123Mode, ((width - this.digitSymbolLabelWidth) - v2.getCharacterListWidth()) - 20, height - textSize3, this.m_pntText);
                }
            }
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            this.mPointInInvalidArea = -1;
        }
        this.mIsFullScreen = fullScreen;
    }

    public boolean isFullScreen() {
        return this.mIsFullScreen;
    }

    private void reset() {
        if (this.mPoints != null) {
            this.mPoints.clear();
        }
        clearArcs();
    }

    private void resetDirtyRect(float eventX, float eventY) {
        this.mDirtyRect.left = Math.min(this.mLastTouchX, eventX);
        this.mDirtyRect.right = Math.max(this.mLastTouchX, eventX);
        this.mDirtyRect.top = Math.min(this.mLastTouchY, eventY);
        this.mDirtyRect.bottom = Math.max(this.mLastTouchY, eventY);
    }

    private void addArcPoint(int x, int y) {
        int size = this.mPoints.size();
        if (size <= 0 || this.mPoints.get(size - 1).x != x || this.mPoints.get(size - 1).y != y) {
            this.mPoints.add(new Point(x, y));
        }
    }
}
