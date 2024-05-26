package com.bumptech.glide.load.resource.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifFrameLoader;

/* loaded from: classes.dex */
public class GifDrawable extends GlideDrawable implements GifFrameLoader.FrameCallback {
    private boolean applyGravity;
    public final GifDecoder decoder;
    private final Rect destRect;
    final GifFrameLoader frameLoader;
    boolean isRecycled;
    private boolean isRunning;
    private boolean isStarted;
    private boolean isVisible;
    private int loopCount;
    private int maxLoopCount;
    private final Paint paint;
    public final GifState state;

    public GifDrawable(Context context, GifDecoder.BitmapProvider bitmapProvider, BitmapPool bitmapPool, Transformation<Bitmap> frameTransformation, int targetFrameWidth, int targetFrameHeight, GifHeader gifHeader, byte[] data, Bitmap firstFrame) {
        this(new GifState(gifHeader, data, context, frameTransformation, targetFrameWidth, targetFrameHeight, bitmapProvider, bitmapPool, firstFrame));
    }

    public GifDrawable(GifDrawable other, Bitmap firstFrame, Transformation<Bitmap> frameTransformation) {
        this(new GifState(other.state.gifHeader, other.state.data, other.state.context, frameTransformation, other.state.targetWidth, other.state.targetHeight, other.state.bitmapProvider, other.state.bitmapPool, firstFrame));
    }

    GifDrawable(GifState state) {
        this.destRect = new Rect();
        this.isVisible = true;
        this.maxLoopCount = -1;
        if (state == null) {
            throw new NullPointerException("GifState must not be null");
        }
        this.state = state;
        this.decoder = new GifDecoder(state.bitmapProvider);
        this.paint = new Paint();
        this.decoder.setData(state.gifHeader, state.data);
        this.frameLoader = new GifFrameLoader(state.context, this, this.decoder, state.targetWidth, state.targetHeight);
        GifFrameLoader gifFrameLoader = this.frameLoader;
        Transformation<Bitmap> transformation = state.frameTransformation;
        if (transformation != null) {
            gifFrameLoader.requestBuilder = gifFrameLoader.requestBuilder.transform(transformation);
            return;
        }
        throw new NullPointerException("Transformation must not be null");
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        this.isStarted = true;
        this.loopCount = 0;
        if (this.isVisible) {
            startRunning();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.isStarted = false;
        stopRunning();
        if (Build.VERSION.SDK_INT < 11) {
            reset();
        }
    }

    private void reset() {
        this.frameLoader.clear();
        invalidateSelf();
    }

    private void startRunning() {
        if (this.decoder.header.frameCount != 1) {
            if (!this.isRunning) {
                this.isRunning = true;
                GifFrameLoader gifFrameLoader = this.frameLoader;
                if (!gifFrameLoader.isRunning) {
                    gifFrameLoader.isRunning = true;
                    gifFrameLoader.isCleared = false;
                    gifFrameLoader.loadNextFrame();
                }
            } else {
                return;
            }
        }
        invalidateSelf();
    }

    private void stopRunning() {
        this.isRunning = false;
        this.frameLoader.isRunning = false;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.isVisible = visible;
        if (!visible) {
            stopRunning();
        } else if (this.isStarted) {
            startRunning();
        }
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.state.firstFrame.getWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.state.firstFrame.getHeight();
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyGravity = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (!this.isRecycled) {
            if (this.applyGravity) {
                Gravity.apply(119, getIntrinsicWidth(), getIntrinsicHeight(), getBounds(), this.destRect);
                this.applyGravity = false;
            }
            GifFrameLoader gifFrameLoader = this.frameLoader;
            Bitmap currentFrame = gifFrameLoader.current != null ? gifFrameLoader.current.resource : null;
            Bitmap toDraw = currentFrame != null ? currentFrame : this.state.firstFrame;
            canvas.drawBitmap(toDraw, (Rect) null, this.destRect, this.paint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // com.bumptech.glide.load.resource.gif.GifFrameLoader.FrameCallback
    @TargetApi(11)
    public final void onFrameReady(int frameIndex) {
        if (Build.VERSION.SDK_INT >= 11 && getCallback() == null) {
            stop();
            reset();
            return;
        }
        invalidateSelf();
        if (frameIndex == this.decoder.header.frameCount - 1) {
            this.loopCount++;
        }
        if (this.maxLoopCount != -1 && this.loopCount >= this.maxLoopCount) {
            stop();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.state;
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final boolean isAnimated() {
        return true;
    }

    @Override // com.bumptech.glide.load.resource.drawable.GlideDrawable
    public final void setLoopCount(int loopCount) {
        int intrinsicCount;
        if (loopCount <= 0 && loopCount != -1 && loopCount != 0) {
            throw new IllegalArgumentException("Loop count must be greater than 0, or equal to GlideDrawable.LOOP_FOREVER, or equal to GlideDrawable.LOOP_INTRINSIC");
        }
        if (loopCount == 0) {
            GifDecoder gifDecoder = this.decoder;
            if (gifDecoder.header.loopCount == -1) {
                intrinsicCount = 1;
            } else if (gifDecoder.header.loopCount == 0) {
                intrinsicCount = 0;
            } else {
                intrinsicCount = gifDecoder.header.loopCount + 1;
            }
            if (intrinsicCount == 0) {
                intrinsicCount = -1;
            }
            this.maxLoopCount = intrinsicCount;
            return;
        }
        this.maxLoopCount = loopCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GifState extends Drawable.ConstantState {
        BitmapPool bitmapPool;
        GifDecoder.BitmapProvider bitmapProvider;
        Context context;
        byte[] data;
        public Bitmap firstFrame;
        Transformation<Bitmap> frameTransformation;
        GifHeader gifHeader;
        int targetHeight;
        int targetWidth;

        public GifState(GifHeader header, byte[] data, Context context, Transformation<Bitmap> frameTransformation, int targetWidth, int targetHeight, GifDecoder.BitmapProvider provider, BitmapPool bitmapPool, Bitmap firstFrame) {
            if (firstFrame == null) {
                throw new NullPointerException("The first frame of the GIF must not be null");
            }
            this.gifHeader = header;
            this.data = data;
            this.bitmapPool = bitmapPool;
            this.firstFrame = firstFrame;
            this.context = context.getApplicationContext();
            this.frameTransformation = frameTransformation;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            this.bitmapProvider = provider;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return newDrawable();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new GifDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }
    }
}
