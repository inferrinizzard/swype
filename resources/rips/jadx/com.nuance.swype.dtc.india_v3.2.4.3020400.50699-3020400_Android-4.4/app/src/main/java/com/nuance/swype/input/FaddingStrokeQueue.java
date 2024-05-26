package com.nuance.swype.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FaddingStrokeQueue {
    protected static final int FADING_DELAY = 130;
    protected static final int FADING_SPEED = 8;
    protected static final int FADING_SPEED_PEN_DOWN = 12;
    protected static final int MSG_FADING = 1;
    protected static final int SUB_FADING_SPEED = 3;
    private static final LogManager.Log log = LogManager.getLog("FaddingStrokeQueue");
    protected boolean mFaddingStarted;
    protected FaddingStrokeQueueBuffer mFaddingStrokeQueueBuffer;
    View mView;
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.FaddingStrokeQueue.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    FaddingStrokeQueue.this.fading();
                    return true;
                default:
                    return false;
            }
        }
    };
    final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);

    public FaddingStrokeQueue(Context context, int color, View v) {
        this.mFaddingStrokeQueueBuffer = new FaddingStrokeQueueBuffer(context, color);
        this.mView = v;
    }

    public void draw(Canvas c) {
        this.mFaddingStrokeQueueBuffer.draw(c);
        if (this.mFaddingStarted) {
            this.mFaddingStrokeQueueBuffer.reduceAlpha();
        }
    }

    public void clearAll() {
        stopFading();
    }

    public void fading() {
        if (this.mFaddingStrokeQueueBuffer.mFadingQueueSize > 0) {
            this.mFaddingStrokeQueueBuffer.reduceAlpha();
            this.mView.invalidate();
            this.mHandler.sendEmptyMessageDelayed(1, 130L);
        }
    }

    public void startActionFading() {
        this.mFaddingStarted = true;
        if (this.mFaddingStrokeQueueBuffer.mFadingQueueSize > 0) {
            this.mFaddingStrokeQueueBuffer.reduceAlpha();
            this.mView.invalidate();
        }
    }

    public void startActionFadingPenDown() {
        this.mFaddingStarted = true;
        if (this.mFaddingStrokeQueueBuffer.mFadingQueueSize > 0) {
            this.mFaddingStrokeQueueBuffer.reduceAlphaPenDown();
            this.mView.invalidate();
        }
    }

    public void stopActionFading() {
        this.mFaddingStarted = false;
    }

    public void startFading() {
        this.mHandler.removeMessages(1);
        if (this.mFaddingStrokeQueueBuffer.mFadingQueueSize > 0) {
            this.mFaddingStarted = true;
            this.mHandler.sendEmptyMessageDelayed(1, 130L);
        } else {
            this.mFaddingStarted = false;
        }
    }

    public void pauseFading() {
        this.mHandler.removeMessages(1);
        this.mFaddingStarted = false;
    }

    public void stopFading() {
        this.mHandler.removeMessages(1);
        this.mFaddingStrokeQueueBuffer.clear();
        this.mFaddingStarted = false;
        this.mView.invalidate();
    }

    public void add(Path path) {
        this.mFaddingStrokeQueueBuffer.add(path);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class FadingStroke {
        public int mColor;
        public int mCurrentAlpha;
        public Paint mPaint;
        public Path mPath;

        public FadingStroke(Context context, int color) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            this.mColor = color;
            this.mCurrentAlpha = Color.alpha(this.mColor);
            this.mPath = new Path();
            this.mPaint = new Paint(1);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeJoin(Paint.Join.ROUND);
            this.mPaint.setStrokeCap(Paint.Cap.ROUND);
            int i = dm.heightPixels;
            context.getResources().getDimension(R.dimen.max_height_for_fullscreen);
            int penSize = InputPrefs.getPenSize(UserPreferences.from(context), UserPreferences.HWR_PEN_SIZE, (int) (5.0f * dm.density));
            this.mPaint.setStrokeWidth(penSize);
            this.mPaint.setColor(this.mColor);
        }

        public void setStrokeWidth(float width) {
            this.mPath.rewind();
            this.mPaint.setStrokeWidth(width);
        }

        public void resetAlpha() {
            this.mPath.rewind();
            this.mCurrentAlpha = Color.alpha(this.mColor);
            this.mPaint.setColor(this.mColor);
        }

        public void decreaseAlpha(boolean pendown) {
            int speed;
            if (pendown) {
                speed = 12;
            } else {
                speed = 8;
            }
            if (this.mCurrentAlpha > speed && this.mCurrentAlpha > 10) {
                this.mCurrentAlpha -= 8;
            } else if (this.mCurrentAlpha > 3 && this.mCurrentAlpha > 10) {
                this.mCurrentAlpha -= 3;
            } else {
                this.mCurrentAlpha = 5;
            }
            this.mPaint.setARGB(this.mCurrentAlpha, Color.red(this.mColor), Color.green(this.mColor), Color.blue(this.mColor));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class FaddingStrokeQueueBuffer {
        private static final int MAX_QUEUE_CAPACITY = 480;
        private FadingStroke[] mFaddingStrokes = new FadingStroke[MAX_QUEUE_CAPACITY];
        List<FadingStroke> mFaddingStrokesCache = new ArrayList(MAX_QUEUE_CAPACITY);
        public int mFadingQueueSize;

        public FaddingStrokeQueueBuffer(Context context, int penColor) {
            for (int i = 0; i < MAX_QUEUE_CAPACITY; i++) {
                this.mFaddingStrokesCache.add(new FadingStroke(context, penColor));
            }
            this.mFadingQueueSize = 0;
        }

        public void setFadingStrokeWidth(float width) {
            for (FadingStroke fadingStroke : this.mFaddingStrokes) {
                fadingStroke.setStrokeWidth(width);
            }
        }

        public void add(Path path) {
            if (this.mFadingQueueSize >= MAX_QUEUE_CAPACITY) {
                removeEnd();
            }
            if (this.mFaddingStrokesCache.size() > 0) {
                FadingStroke stroke = this.mFaddingStrokesCache.remove(0);
                stroke.resetAlpha();
                stroke.mPath.reset();
                stroke.mPath.addPath(path);
                this.mFaddingStrokes[this.mFadingQueueSize] = stroke;
                this.mFadingQueueSize++;
                return;
            }
            LogManager.getLog().e("FadingQueue is empty!");
        }

        public void draw(Canvas canvas) {
            if (this.mFadingQueueSize > 0) {
                for (int i = 0; i < this.mFadingQueueSize; i++) {
                    canvas.drawPath(this.mFaddingStrokes[i].mPath, this.mFaddingStrokes[i].mPaint);
                }
            }
        }

        public void reduceAlpha() {
            if (this.mFadingQueueSize > 0) {
                for (int i = 0; i < this.mFadingQueueSize; i++) {
                    this.mFaddingStrokes[i].decreaseAlpha(false);
                }
                if (this.mFaddingStrokes[0].mCurrentAlpha == 0) {
                    removeEnd();
                }
            }
        }

        public void reduceAlphaPenDown() {
            if (this.mFadingQueueSize > 0) {
                for (int i = 0; i < this.mFadingQueueSize; i++) {
                    this.mFaddingStrokes[i].decreaseAlpha(true);
                }
                if (this.mFaddingStrokes[0].mCurrentAlpha == 0) {
                    removeEnd();
                }
            }
        }

        public void clear() {
            for (int i = 0; i < this.mFadingQueueSize; i++) {
                this.mFaddingStrokesCache.add(this.mFaddingStrokes[i]);
            }
            this.mFadingQueueSize = 0;
        }

        private void removeEnd() {
            if (this.mFadingQueueSize > 0) {
                FadingStroke recycleStroke = this.mFaddingStrokes[0];
                for (int start = 0; start < this.mFadingQueueSize - 1; start++) {
                    this.mFaddingStrokes[start] = this.mFaddingStrokes[start + 1];
                }
                this.mFaddingStrokesCache.add(recycleStroke);
                this.mFadingQueueSize--;
            }
        }
    }
}
