package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AlphaHandWritingView extends View {
    private static final int FADING_DELAY = 60;
    private static final int MSG_FADING = 1;
    private static final int PEN_WIDTH = 6;
    private final Handler.Callback handlerCallback;
    private final float mDensity;
    private boolean mFadingStarted;
    private final FadingStrokeQueue mFadingStrokeQueue;
    private final Handler mHandler;
    private OnWritingAction mOnWritingActionListener;
    private final Paint mPaint;
    private final Stroke mStroke;
    private String mWritingMode;
    private final Paint mWritingModePaint;

    /* loaded from: classes.dex */
    public interface OnWritingAction {
        void penDown(View view);

        void penUp(List<Point> list, View view);

        void penUp(Stroke.Arc[] arcArr, View view);
    }

    public AlphaHandWritingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaHandWritingView(Context context, AttributeSet attrs, int def) {
        super(context, attrs, def);
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaHandWritingView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        AlphaHandWritingView.this.fading();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mHandler = WeakReferenceHandler.create(this.handlerCallback);
        this.mDensity = getResources().getDisplayMetrics().density;
        IMEApplication app = IMEApplication.from(context);
        int penColorStroke = app.getThemedColor(R.attr.traceColor);
        this.mWritingModePaint = new Paint(1);
        this.mWritingModePaint.setStrokeWidth(0.0f);
        this.mWritingModePaint.setColor(app.getThemedColor(R.attr.handwritingModeDisplayColor));
        this.mWritingModePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.hand_writing_mode_text_size));
        Typeface italic = Typeface.create(Typeface.SERIF, 2);
        this.mWritingModePaint.setTypeface(italic);
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(6.0f * this.mDensity);
        this.mPaint.setColor(penColorStroke);
        this.mFadingStrokeQueue = new FadingStrokeQueue(context, penColorStroke);
        this.mStroke = Stroke.create(6);
    }

    public void setOnWritingActionListener(OnWritingAction listener) {
        this.mOnWritingActionListener = listener;
    }

    public OnWritingAction getOnWritingActionListener() {
        return this.mOnWritingActionListener;
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mWritingMode != null) {
            float x = getPaddingLeft() + (16.0f * this.mDensity);
            float y = getPaddingTop() + (12.0f * this.mDensity);
            canvas.translate(0.0f, y);
            canvas.drawText(this.mWritingMode, x, y, this.mWritingModePaint);
            canvas.translate(0.0f, -y);
        }
        this.mFadingStrokeQueue.draw(canvas);
        if (this.mFadingStarted) {
            this.mFadingStrokeQueue.reduceAlpha();
        }
        Path[] paths = this.mStroke.getPaths();
        for (Path path : paths) {
            canvas.drawPath(path, this.mPaint);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fading() {
        if (this.mFadingStrokeQueue.mFadingQueueSize > 0) {
            this.mFadingStrokeQueue.reduceAlpha();
            invalidate();
            this.mHandler.sendEmptyMessageDelayed(1, 60L);
        }
    }

    public void startFading() {
        this.mHandler.removeMessages(1);
        if (this.mFadingStrokeQueue.mFadingQueueSize > 0) {
            this.mFadingStarted = true;
            this.mHandler.sendEmptyMessageDelayed(1, 60L);
        } else {
            this.mFadingStarted = false;
        }
    }

    private void stopFading() {
        this.mHandler.removeMessages(1);
        this.mFadingStrokeQueue.clear();
        this.mFadingStarted = false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0009. Please report as an issue. */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        this.mStroke.handleMotionEvent(me);
        switch (me.getAction()) {
            case 0:
                this.mOnWritingActionListener.penDown(this);
                invalidate();
                return true;
            case 1:
                Path[] paths = this.mStroke.getPaths();
                for (Path path : paths) {
                    this.mFadingStrokeQueue.add(path);
                }
                this.mOnWritingActionListener.penUp(this.mStroke.getArcs(), this);
                this.mStroke.clear();
                return true;
            default:
                invalidate();
                return true;
        }
    }

    public void clearAll() {
        stopFading();
        invalidate();
    }

    public void setWritingMode(String mode) {
        this.mWritingMode = mode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FadingStroke {
        public final int mColor;
        public int mCurrentAlpha;
        public final Paint mPaint;
        public final Path mPath;

        public FadingStroke(Context context, int color) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            this.mColor = color;
            this.mCurrentAlpha = Color.alpha(this.mColor);
            this.mPath = new Path();
            this.mPaint = new Paint(1);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeJoin(Paint.Join.ROUND);
            this.mPaint.setStrokeCap(Paint.Cap.ROUND);
            this.mPaint.setStrokeWidth(6.0f * dm.density);
            this.mPaint.setColor(this.mColor);
        }

        public void resetAlpha() {
            this.mPath.rewind();
            this.mCurrentAlpha = Color.alpha(this.mColor);
            this.mPaint.setColor(this.mColor);
        }

        public void decreaseAlpha() {
            if (this.mCurrentAlpha > 0) {
                this.mCurrentAlpha--;
            } else {
                this.mCurrentAlpha = 0;
            }
            this.mPaint.setARGB(this.mCurrentAlpha, Color.red(this.mColor), Color.green(this.mColor), Color.blue(this.mColor));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FadingStrokeQueue {
        private static final int MAX_QUEUE_CAPACITY = 64;
        int mFadingQueueSize;
        final FadingStroke[] mFadingStrokes = new FadingStroke[64];
        final List<FadingStroke> mFadingStrokesCache = new ArrayList(64);

        public FadingStrokeQueue(Context context, int penColor) {
            for (int i = 0; i < 64; i++) {
                this.mFadingStrokesCache.add(new FadingStroke(context, penColor));
            }
            this.mFadingQueueSize = 0;
        }

        public void add(Path path) {
            if (this.mFadingQueueSize >= 64) {
                removeEnd();
            }
            if (this.mFadingStrokesCache.size() > 0) {
                FadingStroke stroke = this.mFadingStrokesCache.remove(0);
                stroke.resetAlpha();
                stroke.mPath.reset();
                stroke.mPath.addPath(path);
                this.mFadingStrokes[this.mFadingQueueSize] = stroke;
                this.mFadingQueueSize++;
                return;
            }
            LogManager.getLog().e("FadingQueue is empty!");
        }

        void draw(Canvas canvas) {
            if (this.mFadingQueueSize > 0) {
                for (int i = 0; i < this.mFadingQueueSize; i++) {
                    canvas.drawPath(this.mFadingStrokes[i].mPath, this.mFadingStrokes[i].mPaint);
                }
            }
        }

        public void reduceAlpha() {
            if (this.mFadingQueueSize > 0) {
                for (int i = 0; i < this.mFadingQueueSize; i++) {
                    this.mFadingStrokes[i].decreaseAlpha();
                }
                if (this.mFadingStrokes[0].mCurrentAlpha == 0) {
                    removeEnd();
                }
            }
        }

        public void clear() {
            for (int i = 0; i < this.mFadingQueueSize; i++) {
                this.mFadingStrokesCache.add(this.mFadingStrokes[i]);
            }
            this.mFadingQueueSize = 0;
        }

        private void removeEnd() {
            if (this.mFadingQueueSize > 0) {
                FadingStroke recycleStroke = this.mFadingStrokes[0];
                for (int start = 0; start < this.mFadingQueueSize - 1; start++) {
                    this.mFadingStrokes[start] = this.mFadingStrokes[start + 1];
                }
                this.mFadingStrokesCache.add(recycleStroke);
                this.mFadingQueueSize--;
            }
        }
    }
}
