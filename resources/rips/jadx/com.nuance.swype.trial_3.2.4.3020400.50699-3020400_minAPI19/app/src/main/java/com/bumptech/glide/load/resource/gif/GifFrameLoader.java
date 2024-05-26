package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class GifFrameLoader {
    final FrameCallback callback;
    DelayTarget current;
    private final GifDecoder gifDecoder;
    final Handler handler;
    boolean isCleared;
    boolean isLoadPending;
    boolean isRunning;
    GenericRequestBuilder<GifDecoder, GifDecoder, Bitmap, Bitmap> requestBuilder;

    /* loaded from: classes.dex */
    public interface FrameCallback {
        void onFrameReady(int i);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public GifFrameLoader(android.content.Context r16, com.bumptech.glide.load.resource.gif.GifFrameLoader.FrameCallback r17, com.bumptech.glide.gifdecoder.GifDecoder r18, int r19, int r20) {
        /*
            r15 = this;
            com.bumptech.glide.Glide r2 = com.bumptech.glide.Glide.get(r16)
            com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool r2 = r2.bitmapPool
            com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder r12 = new com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder
            r12.<init>(r2)
            com.bumptech.glide.load.resource.gif.GifFrameModelLoader r2 = new com.bumptech.glide.load.resource.gif.GifFrameModelLoader
            r2.<init>()
            com.bumptech.glide.load.Encoder r13 = com.bumptech.glide.load.resource.NullEncoder.get()
            com.bumptech.glide.RequestManager r3 = com.bumptech.glide.Glide.with(r16)
            java.lang.Class<com.bumptech.glide.gifdecoder.GifDecoder> r4 = com.bumptech.glide.gifdecoder.GifDecoder.class
            com.bumptech.glide.RequestManager$GenericModelRequest r5 = new com.bumptech.glide.RequestManager$GenericModelRequest
            r5.<init>(r2, r4)
            com.bumptech.glide.RequestManager$GenericModelRequest$GenericTypeRequest r14 = new com.bumptech.glide.RequestManager$GenericModelRequest$GenericTypeRequest
            r0 = r18
            r14.<init>(r0)
            java.lang.Class<android.graphics.Bitmap> r8 = android.graphics.Bitmap.class
            com.bumptech.glide.GenericTranscodeRequest r2 = new com.bumptech.glide.GenericTranscodeRequest
            com.bumptech.glide.RequestManager$GenericModelRequest r3 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.RequestManager r3 = com.bumptech.glide.RequestManager.this
            android.content.Context r3 = r3.context
            com.bumptech.glide.RequestManager$GenericModelRequest r4 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.RequestManager r4 = com.bumptech.glide.RequestManager.this
            com.bumptech.glide.Glide r4 = r4.glide
            java.lang.Class<A> r5 = r14.modelClass
            com.bumptech.glide.RequestManager$GenericModelRequest r6 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.load.model.ModelLoader<A, T> r6 = r6.modelLoader
            com.bumptech.glide.RequestManager$GenericModelRequest r7 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            java.lang.Class<T> r7 = r7.dataClass
            com.bumptech.glide.RequestManager$GenericModelRequest r9 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.RequestManager r9 = com.bumptech.glide.RequestManager.this
            com.bumptech.glide.manager.RequestTracker r9 = r9.requestTracker
            com.bumptech.glide.RequestManager$GenericModelRequest r10 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.RequestManager r10 = com.bumptech.glide.RequestManager.this
            com.bumptech.glide.manager.Lifecycle r10 = r10.lifecycle
            com.bumptech.glide.RequestManager$GenericModelRequest r11 = com.bumptech.glide.RequestManager.GenericModelRequest.this
            com.bumptech.glide.RequestManager r11 = com.bumptech.glide.RequestManager.this
            com.bumptech.glide.RequestManager$OptionsApplier r11 = r11.optionsApplier
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)
            com.bumptech.glide.GenericTranscodeRequest r2 = (com.bumptech.glide.GenericTranscodeRequest) r2
            boolean r3 = r14.providedModel
            if (r3 == 0) goto L60
            A r3 = r14.model
            r2.load(r3)
        L60:
            com.bumptech.glide.GenericRequestBuilder r2 = r2.sourceEncoder(r13)
            com.bumptech.glide.GenericRequestBuilder r2 = r2.decoder(r12)
            r3 = 1
            com.bumptech.glide.GenericRequestBuilder r2 = r2.skipMemoryCache(r3)
            com.bumptech.glide.load.engine.DiskCacheStrategy r3 = com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
            com.bumptech.glide.GenericRequestBuilder r2 = r2.diskCacheStrategy(r3)
            r0 = r19
            r1 = r20
            com.bumptech.glide.GenericRequestBuilder r2 = r2.override(r0, r1)
            r0 = r17
            r1 = r18
            r15.<init>(r0, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.gif.GifFrameLoader.<init>(android.content.Context, com.bumptech.glide.load.resource.gif.GifFrameLoader$FrameCallback, com.bumptech.glide.gifdecoder.GifDecoder, int, int):void");
    }

    private GifFrameLoader(FrameCallback callback, GifDecoder gifDecoder, GenericRequestBuilder<GifDecoder, GifDecoder, Bitmap, Bitmap> requestBuilder) {
        this.isRunning = false;
        this.isLoadPending = false;
        Handler handler = new Handler(Looper.getMainLooper(), new FrameLoaderCallback(this, (byte) 0));
        this.callback = callback;
        this.gifDecoder = gifDecoder;
        this.handler = handler;
        this.requestBuilder = requestBuilder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void loadNextFrame() {
        int i;
        if (this.isRunning && !this.isLoadPending) {
            this.isLoadPending = true;
            long uptimeMillis = SystemClock.uptimeMillis();
            GifDecoder gifDecoder = this.gifDecoder;
            if (gifDecoder.header.frameCount <= 0 || gifDecoder.framePointer < 0) {
                i = -1;
            } else {
                i = gifDecoder.getDelay(gifDecoder.framePointer);
            }
            long targetTime = uptimeMillis + i;
            this.gifDecoder.advance();
            DelayTarget next = new DelayTarget(this.handler, this.gifDecoder.framePointer, targetTime);
            this.requestBuilder.signature(new FrameSignature()).into((GenericRequestBuilder<GifDecoder, GifDecoder, Bitmap, Bitmap>) next);
        }
    }

    /* loaded from: classes.dex */
    private class FrameLoaderCallback implements Handler.Callback {
        private FrameLoaderCallback() {
        }

        /* synthetic */ FrameLoaderCallback(GifFrameLoader x0, byte b) {
            this();
        }

        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                DelayTarget target = (DelayTarget) msg.obj;
                GifFrameLoader gifFrameLoader = GifFrameLoader.this;
                if (gifFrameLoader.isCleared) {
                    gifFrameLoader.handler.obtainMessage(2, target).sendToTarget();
                    return true;
                }
                DelayTarget delayTarget = gifFrameLoader.current;
                gifFrameLoader.current = target;
                gifFrameLoader.callback.onFrameReady(target.index);
                if (delayTarget != null) {
                    gifFrameLoader.handler.obtainMessage(2, delayTarget).sendToTarget();
                }
                gifFrameLoader.isLoadPending = false;
                gifFrameLoader.loadNextFrame();
                return true;
            }
            if (msg.what == 2) {
                Glide.clear((DelayTarget) msg.obj);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DelayTarget extends SimpleTarget<Bitmap> {
        private final Handler handler;
        final int index;
        Bitmap resource;
        private final long targetTime;

        @Override // com.bumptech.glide.request.target.Target
        public final /* bridge */ /* synthetic */ void onResourceReady(Object x0, GlideAnimation x1) {
            this.resource = (Bitmap) x0;
            this.handler.sendMessageAtTime(this.handler.obtainMessage(1, this), this.targetTime);
        }

        public DelayTarget(Handler handler, int index, long targetTime) {
            this.handler = handler;
            this.index = index;
            this.targetTime = targetTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FrameSignature implements Key {
        private final UUID uuid;

        public FrameSignature() {
            this(UUID.randomUUID());
        }

        private FrameSignature(UUID uuid) {
            this.uuid = uuid;
        }

        @Override // com.bumptech.glide.load.Key
        public final boolean equals(Object o) {
            if (o instanceof FrameSignature) {
                return ((FrameSignature) o).uuid.equals(this.uuid);
            }
            return false;
        }

        @Override // com.bumptech.glide.load.Key
        public final int hashCode() {
            return this.uuid.hashCode();
        }

        @Override // com.bumptech.glide.load.Key
        public final void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    public final void clear() {
        this.isRunning = false;
        if (this.current != null) {
            Glide.clear(this.current);
            this.current = null;
        }
        this.isCleared = true;
    }
}
