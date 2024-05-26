package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.TwoFingerGestureDetector;
import com.nuance.swype.input.settings.InputPrefs;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class JapaneseHandWritingView extends HandWritingView {
    private float mDensity;
    private Path mFaddingPath;
    private boolean mNewSession;
    private final Path mPath;
    private final List<Point> mPoints;
    private TwoFingerGestureDetector mTwoFingerGestureDetector;
    private String mWritingMode;
    private Paint m_pntMidLine;
    private Paint m_pntText;
    private Paint m_pntWrite;

    public JapaneseHandWritingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mTwoFingerGestureDetector = new TwoFingerGestureDetector();
    }

    public void setMultitouchListener(TwoFingerGestureDetector.OnScrollListener aListener) {
        this.mTwoFingerGestureDetector.setScrollListener(aListener);
    }

    @SuppressLint({"PrivateResource"})
    public JapaneseHandWritingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPoints = new ArrayList();
        this.mPath = new Path();
        this.m_pntWrite = null;
        this.m_pntText = null;
        this.m_pntMidLine = null;
        this.mTwoFingerGestureDetector = null;
        this.mNewSession = true;
        this.mFaddingPath = new Path();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int hmdColor = IMEApplication.from(context).getThemedColor(R.attr.handwritingModeDisplayColor);
        this.mDensity = dm.density;
        this.m_pntText = new Paint();
        this.m_pntText.setStrokeWidth(0.0f);
        this.m_pntText.setTextSize(12.0f * this.mDensity);
        this.m_pntText.setAntiAlias(true);
        Typeface italic = Typeface.create(Typeface.SERIF, 2);
        this.m_pntText.setTypeface(italic);
        this.m_pntText.setColor(hmdColor);
        this.m_pntWrite = new Paint(1);
        this.m_pntWrite.setStyle(Paint.Style.STROKE);
        this.m_pntWrite.setStrokeJoin(Paint.Join.ROUND);
        this.m_pntWrite.setStrokeCap(Paint.Cap.ROUND);
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.m_pntWrite.setColor(penColor);
        int penSize = InputPrefs.getPenSize(UserPreferences.from(context), UserPreferences.HWR_PEN_SIZE, (int) (6.0f * dm.density));
        this.m_pntWrite.setStrokeWidth(penSize);
        this.m_pntMidLine = new Paint();
        this.m_pntMidLine.setStyle(Paint.Style.STROKE);
        this.m_pntMidLine.setColor(-7829368);
    }

    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        onBufferDraw(c);
        c.drawPath(this.mFaddingPath, this.m_pntWrite);
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if (this.mTwoFingerGestureDetector.onTouchEvent(me)) {
            clearArcs();
            this.mPoints.clear();
            invalidate();
            return true;
        }
        switch (me.getAction()) {
            case 0:
                this.mFaddingStrokeQueue.pauseFading();
                int x = (int) me.getX();
                int y = (int) me.getY();
                if (this.mNewSession) {
                    this.mNewSession = false;
                }
                this.mOnWritingActionListener.penDown(this);
                this.mFaddingPath.moveTo(x, y);
                this.mPoints.add(new Point(x, y));
                invalidate();
                return true;
            case 1:
                this.mFaddingStrokeQueue.add(this.mFaddingPath);
                this.mFaddingPath.reset();
                this.mPoints.add(new Point((int) me.getX(), (int) me.getY()));
                this.mPoints.add(new Point(0, 0));
                this.mOnWritingActionListener.penUp(this.mPoints, this);
                this.mPoints.clear();
                this.mFaddingStrokeQueue.startActionFading();
                this.mFaddingStrokeQueue.startFading();
                invalidate();
                return true;
            case 2:
                int points = me.getHistorySize();
                if (points != 0) {
                    for (int i = 0; i < points; i++) {
                        int x2 = (int) me.getHistoricalX(i);
                        int y2 = (int) me.getHistoricalY(i);
                        this.mFaddingPath.lineTo(x2, y2);
                        this.mPoints.add(new Point(x2, y2));
                    }
                } else {
                    int x3 = (int) me.getX();
                    int y3 = (int) me.getY();
                    this.mFaddingPath.lineTo(x3, y3);
                    this.mPoints.add(new Point(x3, y3));
                }
                invalidate();
                return true;
            default:
                return false;
        }
    }

    public void clearArcs() {
        this.mPoints.clear();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.m_pntWrite.setColor(penColor);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int penSize = InputPrefs.getPenSize(UserPreferences.from(getContext()), UserPreferences.HWR_PEN_SIZE, (int) (6.0f * dm.density));
        this.m_pntWrite.setStrokeWidth(penSize);
        super.onWindowVisibilityChanged(visibility);
    }

    public void setNewSession(boolean newSession) {
        this.mNewSession = newSession;
    }

    private void onBufferDraw(Canvas c) {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0 && this.mWritingMode != null) {
            float x = getPaddingLeft() + (16.0f * this.mDensity);
            float y = getPaddingTop() + (12.0f * this.mDensity);
            c.translate(0.0f, y);
            c.drawText(this.mWritingMode, x, y, this.m_pntText);
            c.translate(0.0f, -y);
        }
    }

    private void reset() {
        if (this.mPoints != null) {
            this.mPoints.clear();
        }
        clearArcs();
    }

    public void setWritingMode(String writingMode) {
        this.mWritingMode = writingMode;
    }

    @Override // com.nuance.swype.input.HandWritingView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
