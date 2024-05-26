package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.executor.Prioritized;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.LogTime;

/* loaded from: classes.dex */
public final class EngineRunnable implements Prioritized, Runnable {
    public final DecodeJob<?, ?, ?> decodeJob;
    public volatile boolean isCancelled;
    private final EngineRunnableManager manager;
    private final Priority priority;
    private Stage stage = Stage.CACHE;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface EngineRunnableManager extends ResourceCallback {
        void submitForSource(EngineRunnable engineRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Stage {
        CACHE,
        SOURCE
    }

    public EngineRunnable(EngineRunnableManager manager, DecodeJob<?, ?, ?> decodeJob, Priority priority) {
        this.manager = manager;
        this.decodeJob = decodeJob;
        this.priority = priority;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (!this.isCancelled) {
            Exception exception = null;
            Resource<?> resource = null;
            try {
                if (isDecodingFromCache()) {
                    resource = decodeFromCache();
                } else {
                    DecodeJob<?, ?, ?> decodeJob = this.decodeJob;
                    resource = decodeJob.transformEncodeAndTranscode(decodeJob.decodeSource());
                }
            } catch (Exception e) {
                if (Log.isLoggable("EngineRunnable", 2)) {
                    Log.v("EngineRunnable", "Exception decoding", e);
                }
                exception = e;
            } catch (OutOfMemoryError e2) {
                if (Log.isLoggable("EngineRunnable", 2)) {
                    Log.v("EngineRunnable", "Out Of Memory Error decoding", e2);
                }
                exception = new ErrorWrappingGlideException(e2);
            }
            if (this.isCancelled) {
                if (resource != null) {
                    resource.recycle();
                }
            } else {
                if (resource == null) {
                    if (isDecodingFromCache()) {
                        this.stage = Stage.SOURCE;
                        this.manager.submitForSource(this);
                        return;
                    } else {
                        this.manager.onException(exception);
                        return;
                    }
                }
                this.manager.onResourceReady(resource);
            }
        }
    }

    private boolean isDecodingFromCache() {
        return this.stage == Stage.CACHE;
    }

    private Resource<?> decodeFromCache() throws Exception {
        Resource<?> result = null;
        try {
            DecodeJob<?, ?, ?> decodeJob = this.decodeJob;
            if (decodeJob.diskCacheStrategy.cacheResult) {
                long logTime = LogTime.getLogTime();
                Resource<?> loadFromCache = decodeJob.loadFromCache(decodeJob.resultKey);
                if (Log.isLoggable("DecodeJob", 2)) {
                    decodeJob.logWithTimeAndKey("Decoded transformed from cache", logTime);
                }
                long logTime2 = LogTime.getLogTime();
                Resource<?> transcode = decodeJob.transcode(loadFromCache);
                if (Log.isLoggable("DecodeJob", 2)) {
                    decodeJob.logWithTimeAndKey("Transcoded transformed from cache", logTime2);
                }
                result = transcode;
            } else {
                result = null;
            }
        } catch (Exception e) {
            if (Log.isLoggable("EngineRunnable", 3)) {
                new StringBuilder("Exception decoding result from cache: ").append(e);
            }
        }
        if (result == null) {
            DecodeJob<?, ?, ?> decodeJob2 = this.decodeJob;
            if (!decodeJob2.diskCacheStrategy.cacheSource) {
                return null;
            }
            long logTime3 = LogTime.getLogTime();
            Resource<?> loadFromCache2 = decodeJob2.loadFromCache(decodeJob2.resultKey.getOriginalKey());
            if (Log.isLoggable("DecodeJob", 2)) {
                decodeJob2.logWithTimeAndKey("Decoded source from cache", logTime3);
            }
            return decodeJob2.transformEncodeAndTranscode(loadFromCache2);
        }
        return result;
    }

    @Override // com.bumptech.glide.load.engine.executor.Prioritized
    public final int getPriority() {
        return this.priority.ordinal();
    }
}
