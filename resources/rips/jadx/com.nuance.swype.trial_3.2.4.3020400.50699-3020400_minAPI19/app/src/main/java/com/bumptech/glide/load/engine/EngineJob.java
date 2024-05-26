package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.EngineRunnable;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/* loaded from: classes.dex */
public final class EngineJob implements EngineRunnable.EngineRunnableManager {
    private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback(0));
    public final List<ResourceCallback> cbs;
    public final ExecutorService diskCacheService;
    private EngineResource<?> engineResource;
    private final EngineResourceFactory engineResourceFactory;
    public EngineRunnable engineRunnable;
    private Exception exception;
    public volatile Future<?> future;
    public boolean hasException;
    public boolean hasResource;
    public Set<ResourceCallback> ignoredCallbacks;
    private final boolean isCacheable;
    public boolean isCancelled;
    public final Key key;
    public final EngineJobListener listener;
    private Resource<?> resource;
    private final ExecutorService sourceService;

    public EngineJob(Key key, ExecutorService diskCacheService, ExecutorService sourceService, boolean isCacheable, EngineJobListener listener) {
        this(key, diskCacheService, sourceService, isCacheable, listener, DEFAULT_FACTORY);
    }

    private EngineJob(Key key, ExecutorService diskCacheService, ExecutorService sourceService, boolean isCacheable, EngineJobListener listener, EngineResourceFactory engineResourceFactory) {
        this.cbs = new ArrayList();
        this.key = key;
        this.diskCacheService = diskCacheService;
        this.sourceService = sourceService;
        this.isCacheable = isCacheable;
        this.listener = listener;
        this.engineResourceFactory = engineResourceFactory;
    }

    @Override // com.bumptech.glide.load.engine.EngineRunnable.EngineRunnableManager
    public final void submitForSource(EngineRunnable runnable) {
        this.future = this.sourceService.submit(runnable);
    }

    public final void addCallback(ResourceCallback cb) {
        Util.assertMainThread();
        if (this.hasResource) {
            cb.onResourceReady(this.engineResource);
        } else if (this.hasException) {
            cb.onException(this.exception);
        } else {
            this.cbs.add(cb);
        }
    }

    private boolean isInIgnoredCallbacks(ResourceCallback cb) {
        return this.ignoredCallbacks != null && this.ignoredCallbacks.contains(cb);
    }

    @Override // com.bumptech.glide.request.ResourceCallback
    public final void onResourceReady(Resource<?> resource) {
        this.resource = resource;
        MAIN_THREAD_HANDLER.obtainMessage(1, this).sendToTarget();
    }

    @Override // com.bumptech.glide.request.ResourceCallback
    public final void onException(Exception e) {
        this.exception = e;
        MAIN_THREAD_HANDLER.obtainMessage(2, this).sendToTarget();
    }

    /* loaded from: classes.dex */
    static class EngineResourceFactory {
        EngineResourceFactory() {
        }
    }

    /* loaded from: classes.dex */
    private static class MainThreadCallback implements Handler.Callback {
        private MainThreadCallback() {
        }

        /* synthetic */ MainThreadCallback(byte b) {
            this();
        }

        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message message) {
            if (1 == message.what || 2 == message.what) {
                EngineJob job = (EngineJob) message.obj;
                if (1 == message.what) {
                    EngineJob.access$100(job);
                    return true;
                }
                EngineJob.access$200(job);
                return true;
            }
            return false;
        }
    }

    static /* synthetic */ void access$100(EngineJob x0) {
        if (x0.isCancelled) {
            x0.resource.recycle();
            return;
        }
        if (x0.cbs.isEmpty()) {
            throw new IllegalStateException("Received a resource without any callbacks to notify");
        }
        x0.engineResource = new EngineResource<>(x0.resource, x0.isCacheable);
        x0.hasResource = true;
        x0.engineResource.acquire();
        x0.listener.onEngineJobComplete(x0.key, x0.engineResource);
        for (ResourceCallback resourceCallback : x0.cbs) {
            if (!x0.isInIgnoredCallbacks(resourceCallback)) {
                x0.engineResource.acquire();
                resourceCallback.onResourceReady(x0.engineResource);
            }
        }
        x0.engineResource.release();
    }

    static /* synthetic */ void access$200(EngineJob x0) {
        if (x0.isCancelled) {
            return;
        }
        if (x0.cbs.isEmpty()) {
            throw new IllegalStateException("Received an exception without any callbacks to notify");
        }
        x0.hasException = true;
        x0.listener.onEngineJobComplete(x0.key, null);
        for (ResourceCallback resourceCallback : x0.cbs) {
            if (!x0.isInIgnoredCallbacks(resourceCallback)) {
                resourceCallback.onException(x0.exception);
            }
        }
    }
}
