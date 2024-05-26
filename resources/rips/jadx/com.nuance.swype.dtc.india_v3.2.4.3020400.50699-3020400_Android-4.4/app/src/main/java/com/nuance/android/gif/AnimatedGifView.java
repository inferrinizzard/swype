package com.nuance.android.gif;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class AnimatedGifView extends View {
    protected static final LogManager.Log log = LogManager.getLog("AnimatedGifView");
    private AnimationCallback animationCallback;
    private AnimationRunnable animationRunnable;
    Runnable animationStopped;
    int[] appStarupAttrs;
    private String assetFilePath;
    private Bitmap bitmapFrame;
    private final int bottomContainerHeight;
    public int decodeStatus;
    private int frameCount;
    private final float frameDelayScale;
    Runnable frameDidAnimateCallback;
    private int frameIndex;
    Runnable frameWillAnimateCallback;
    private int height;
    public int imageType;
    private final int maxHeight;
    private final int maxWidth;
    private int resId;
    private Bitmap staticBitmap;
    private int width;

    /* loaded from: classes.dex */
    public interface AnimationCallback {
    }

    public AnimatedGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.imageType = 0;
        this.decodeStatus = 0;
        this.appStarupAttrs = new int[]{R.attr.startupDialogWidth, R.attr.startupDialogHeight};
        this.frameWillAnimateCallback = new Runnable() { // from class: com.nuance.android.gif.AnimatedGifView.1
            @Override // java.lang.Runnable
            public final void run() {
                AnimationCallback unused = AnimatedGifView.this.animationCallback;
                int unused2 = AnimatedGifView.this.frameIndex;
                int unused3 = AnimatedGifView.this.frameCount;
                AnimatedGifView.this.invalidate();
            }
        };
        this.frameDidAnimateCallback = new Runnable() { // from class: com.nuance.android.gif.AnimatedGifView.2
            @Override // java.lang.Runnable
            public final void run() {
                AnimationCallback unused = AnimatedGifView.this.animationCallback;
                int unused2 = AnimatedGifView.this.frameIndex;
                int unused3 = AnimatedGifView.this.frameCount;
            }
        };
        this.animationStopped = new Runnable() { // from class: com.nuance.android.gif.AnimatedGifView.3
            @Override // java.lang.Runnable
            public final void run() {
                AnimationCallback unused = AnimatedGifView.this.animationCallback;
            }
        };
        this.frameDelayScale = 0.5f;
        this.bottomContainerHeight = getResources().getDimensionPixelSize(R.dimen.startup_theme_animation_buttom_container_height);
        if (!IMEApplication.from(getContext()).isScreenLayoutTablet()) {
            Rect displayRect = new Rect();
            IMEApplication.from(getContext()).getDisplayRectSize(displayRect);
            this.maxWidth = displayRect.right - displayRect.left;
            this.maxHeight = (displayRect.bottom - displayRect.top) - this.bottomContainerHeight;
            return;
        }
        TypedArray a = context.obtainStyledAttributes(R.style.AppStartupTheme_FloatingActivity, this.appStarupAttrs);
        this.maxWidth = (int) a.getDimension(0, 0.0f);
        this.maxHeight = ((int) a.getDimension(1, 0.0f)) - this.bottomContainerHeight;
        a.recycle();
    }

    public AnimatedGifView(Context context) {
        this(context, null);
    }

    private InputStream getInputStream(String assetFilePath) {
        try {
            InputStream is = getContext().getAssets().open(assetFilePath);
            return is;
        } catch (IOException e) {
            return null;
        }
    }

    private InputStream getInputStream() {
        if (this.assetFilePath != null) {
            return getInputStream(this.assetFilePath);
        }
        if (this.resId > 0) {
            return getContext().getResources().openRawResource(this.resId);
        }
        return null;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(this.width, Integers.STATUS_SUCCESS), View.MeasureSpec.makeMeasureSpec(this.height, Integers.STATUS_SUCCESS));
    }

    public void setGif(String assetFilePath) {
        InputStream is = getInputStream(assetFilePath);
        if (is != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setGif(assetFilePath, bitmap);
        }
    }

    public void setGif(String assetFilePath, Bitmap bitmap) {
        this.resId = 0;
        this.assetFilePath = assetFilePath;
        this.imageType = 0;
        this.decodeStatus = 0;
        setStaticBitmap(bitmap);
    }

    public void setGif(int resId) {
        Bitmap bitmap = BitmapFactory.decodeStream(getResources().openRawResource(resId));
        setGif(resId, bitmap);
    }

    public void setGif(int resId, Bitmap bitmap) {
        this.assetFilePath = null;
        this.resId = resId;
        this.imageType = 0;
        this.decodeStatus = 0;
        setStaticBitmap(bitmap);
    }

    private void setStaticBitmap(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        float ratio = Math.min(this.maxHeight / this.height, this.maxWidth / this.width);
        this.height = (int) (this.height * ratio);
        this.width = (int) (this.width * ratio);
        this.staticBitmap = Bitmap.createScaledBitmap(bitmap, this.width, this.height, false);
        bitmap.recycle();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.decodeStatus == 2) {
            if (this.imageType == 1) {
                if (this.staticBitmap != null) {
                    canvas.drawBitmap(this.staticBitmap, 0.0f, 0.0f, (Paint) null);
                    return;
                }
                return;
            } else {
                if (this.imageType == 2) {
                    boolean isDrawn = false;
                    synchronized (this.animationRunnable.drawingLock) {
                        if (this.bitmapFrame != null && !this.bitmapFrame.isRecycled()) {
                            canvas.drawBitmap(this.bitmapFrame, 0.0f, 0.0f, (Paint) null);
                            isDrawn = true;
                        }
                    }
                    if (isDrawn && this.animationCallback != null) {
                        post(this.frameDidAnimateCallback);
                        return;
                    }
                    return;
                }
                return;
            }
        }
        if (this.staticBitmap != null) {
            canvas.drawBitmap(this.staticBitmap, 0.0f, 0.0f, (Paint) null);
        }
    }

    public void setAnimationCallback(AnimationCallback callback) {
        this.animationCallback = callback;
    }

    /* loaded from: classes.dex */
    private class AnimationRunnable implements Runnable {
        private GifDecoder decoder;
        private final Object drawingLock;
        private final Object frameDelayPause;
        private final InputStream gifInputStream;
        private final Object pauseLock;
        private boolean paused;
        private boolean stopped;
        final /* synthetic */ AnimatedGifView this$0;

        private void waitOnPause() {
            synchronized (this.pauseLock) {
                while (this.paused) {
                    try {
                        this.pauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        @Override // java.lang.Runnable
        public final void run() {
            int delay;
            synchronized (this.drawingLock) {
                this.decoder = new GifDecoder();
                try {
                    this.decoder.read(this.gifInputStream, this.gifInputStream.available());
                    if (this.decoder.width == 0 || this.decoder.height == 0) {
                        this.this$0.imageType = 1;
                    } else {
                        this.this$0.imageType = 2;
                    }
                    this.this$0.decodeStatus = 2;
                    this.this$0.frameCount = this.decoder.getFrameCount();
                    AnimatedGifView.log.d("frames: ", Integer.valueOf(this.decoder.getFrameCount()));
                    AnimatedGifView.log.d("runLoop: ", Integer.valueOf(this.decoder.getLoopCount()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            waitOnPause();
            if (this.this$0.animationCallback != null) {
                this.this$0.post(new Runnable() { // from class: com.nuance.android.gif.AnimatedGifView.AnimationRunnable.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnimationCallback unused = AnimationRunnable.this.this$0.animationCallback;
                    }
                });
            }
            waitOnPause();
            for (int i = 0; !this.stopped && i < this.this$0.frameCount; i++) {
                this.this$0.frameIndex = i;
                synchronized (this.drawingLock) {
                    this.decoder.advance();
                }
                long frameLoadingStartTimer = System.currentTimeMillis();
                synchronized (this.drawingLock) {
                    if (this.this$0.bitmapFrame != null) {
                        this.this$0.bitmapFrame.recycle();
                    }
                    this.this$0.bitmapFrame = Bitmap.createScaledBitmap(this.decoder.getNextFrame(), this.this$0.width, this.this$0.height, false);
                }
                long frameLoadingStopTimer = System.currentTimeMillis() - frameLoadingStartTimer;
                this.this$0.post(this.this$0.frameWillAnimateCallback);
                try {
                    synchronized (this.frameDelayPause) {
                        if (!this.stopped && (delay = Math.max(0, this.decoder.getDelay(i)) - ((int) frameLoadingStopTimer)) > 0 && i > 0) {
                            this.frameDelayPause.wait(delay);
                        }
                    }
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                waitOnPause();
            }
            if (this.this$0.animationCallback != null) {
                this.this$0.post(this.this$0.animationStopped);
            }
        }
    }
}
