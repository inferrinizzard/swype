package com.bumptech.glide.request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DecodeJob;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.EngineJob;
import com.bumptech.glide.load.engine.EngineKey;
import com.bumptech.glide.load.engine.EngineResource;
import com.bumptech.glide.load.engine.EngineRunnable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.provider.LoadProvider;
import com.bumptech.glide.request.animation.GlideAnimationFactory;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.Future;

/* loaded from: classes.dex */
public final class GenericRequest<A, T, Z, R> implements Request, ResourceCallback, SizeReadyCallback {
    private static final Queue<GenericRequest<?, ?, ?, ?>> REQUEST_POOL = Util.createQueue(0);
    private GlideAnimationFactory<R> animationFactory;
    private Context context;
    private DiskCacheStrategy diskCacheStrategy;
    private Engine engine;
    private Drawable errorDrawable;
    private int errorResourceId;
    private Drawable fallbackDrawable;
    private int fallbackResourceId;
    private boolean isMemoryCacheable;
    private LoadProvider<A, T, Z, R> loadProvider;
    private Engine.LoadStatus loadStatus;
    private boolean loadedFromMemoryCache;
    private A model;
    private int overrideHeight;
    private int overrideWidth;
    private Drawable placeholderDrawable;
    private int placeholderResourceId;
    private Priority priority;
    private RequestCoordinator requestCoordinator;
    private RequestListener<? super A, R> requestListener;
    private Resource<?> resource;
    private Key signature;
    private float sizeMultiplier;
    private long startTime;
    private Status status;
    private final String tag = String.valueOf(hashCode());
    private Target<R> target;
    private Class<R> transcodeClass;
    private Transformation<Z> transformation;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED
    }

    public static <A, T, Z, R> GenericRequest<A, T, Z, R> obtain(LoadProvider<A, T, Z, R> loadProvider, A model, Key signature, Context context, Priority priority, Target<R> target, float sizeMultiplier, Drawable placeholderDrawable, int placeholderResourceId, Drawable errorDrawable, int errorResourceId, Drawable fallbackDrawable, int fallbackResourceId, RequestListener<? super A, R> requestListener, RequestCoordinator requestCoordinator, Engine engine, Transformation<Z> transformation, Class<R> transcodeClass, boolean isMemoryCacheable, GlideAnimationFactory<R> animationFactory, int overrideWidth, int overrideHeight, DiskCacheStrategy diskCacheStrategy) {
        GenericRequest<A, T, Z, R> request = (GenericRequest) REQUEST_POOL.poll();
        if (request == null) {
            request = new GenericRequest<>();
        }
        ((GenericRequest) request).loadProvider = loadProvider;
        ((GenericRequest) request).model = model;
        ((GenericRequest) request).signature = signature;
        ((GenericRequest) request).fallbackDrawable = fallbackDrawable;
        ((GenericRequest) request).fallbackResourceId = fallbackResourceId;
        ((GenericRequest) request).context = context.getApplicationContext();
        ((GenericRequest) request).priority = priority;
        ((GenericRequest) request).target = target;
        ((GenericRequest) request).sizeMultiplier = sizeMultiplier;
        ((GenericRequest) request).placeholderDrawable = placeholderDrawable;
        ((GenericRequest) request).placeholderResourceId = placeholderResourceId;
        ((GenericRequest) request).errorDrawable = errorDrawable;
        ((GenericRequest) request).errorResourceId = errorResourceId;
        ((GenericRequest) request).requestListener = requestListener;
        ((GenericRequest) request).requestCoordinator = requestCoordinator;
        ((GenericRequest) request).engine = engine;
        ((GenericRequest) request).transformation = transformation;
        ((GenericRequest) request).transcodeClass = transcodeClass;
        ((GenericRequest) request).isMemoryCacheable = isMemoryCacheable;
        ((GenericRequest) request).animationFactory = animationFactory;
        ((GenericRequest) request).overrideWidth = overrideWidth;
        ((GenericRequest) request).overrideHeight = overrideHeight;
        ((GenericRequest) request).diskCacheStrategy = diskCacheStrategy;
        ((GenericRequest) request).status = Status.PENDING;
        if (model != null) {
            check("ModelLoader", loadProvider.getModelLoader(), "try .using(ModelLoader)");
            check("Transcoder", loadProvider.getTranscoder(), "try .as*(Class).transcode(ResourceTranscoder)");
            check("Transformation", transformation, "try .transform(UnitTransformation.get())");
            if (diskCacheStrategy.cacheSource) {
                check("SourceEncoder", loadProvider.getSourceEncoder(), "try .sourceEncoder(Encoder) or .diskCacheStrategy(NONE/RESULT)");
            } else {
                check("SourceDecoder", loadProvider.getSourceDecoder(), "try .decoder/.imageDecoder/.videoDecoder(ResourceDecoder) or .diskCacheStrategy(ALL/SOURCE)");
            }
            if (diskCacheStrategy.cacheSource || diskCacheStrategy.cacheResult) {
                check("CacheDecoder", loadProvider.getCacheDecoder(), "try .cacheDecoder(ResouceDecoder) or .diskCacheStrategy(NONE)");
            }
            if (diskCacheStrategy.cacheResult) {
                check("Encoder", loadProvider.getEncoder(), "try .encode(ResourceEncoder) or .diskCacheStrategy(NONE/SOURCE)");
            }
        }
        return request;
    }

    private GenericRequest() {
    }

    @Override // com.bumptech.glide.request.Request
    public final void recycle() {
        this.loadProvider = null;
        this.model = null;
        this.context = null;
        this.target = null;
        this.placeholderDrawable = null;
        this.errorDrawable = null;
        this.fallbackDrawable = null;
        this.requestListener = null;
        this.requestCoordinator = null;
        this.transformation = null;
        this.animationFactory = null;
        this.loadedFromMemoryCache = false;
        this.loadStatus = null;
        REQUEST_POOL.offer(this);
    }

    private static void check(String name, Object object, String suggestion) {
        if (object == null) {
            throw new NullPointerException(name + " must not be null, " + suggestion);
        }
    }

    @Override // com.bumptech.glide.request.Request
    public final void begin() {
        this.startTime = LogTime.getLogTime();
        if (this.model == null) {
            onException(null);
            return;
        }
        this.status = Status.WAITING_FOR_SIZE;
        if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
            onSizeReady(this.overrideWidth, this.overrideHeight);
        } else {
            this.target.getSize(this);
        }
        if (!isComplete()) {
            if (!(this.status == Status.FAILED) && canNotifyStatusChanged()) {
                this.target.onLoadStarted(getPlaceholderDrawable());
            }
        }
        if (Log.isLoggable("GenericRequest", 2)) {
            logV("finished run method in " + LogTime.getElapsedMillis(this.startTime));
        }
    }

    @Override // com.bumptech.glide.request.Request
    public final void clear() {
        Util.assertMainThread();
        if (this.status == Status.CLEARED) {
            return;
        }
        this.status = Status.CANCELLED;
        if (this.loadStatus != null) {
            Engine.LoadStatus loadStatus = this.loadStatus;
            EngineJob engineJob = loadStatus.engineJob;
            ResourceCallback resourceCallback = loadStatus.cb;
            Util.assertMainThread();
            if (!engineJob.hasResource && !engineJob.hasException) {
                engineJob.cbs.remove(resourceCallback);
                if (engineJob.cbs.isEmpty() && !engineJob.hasException && !engineJob.hasResource && !engineJob.isCancelled) {
                    EngineRunnable engineRunnable = engineJob.engineRunnable;
                    engineRunnable.isCancelled = true;
                    DecodeJob<?, ?, ?> decodeJob = engineRunnable.decodeJob;
                    decodeJob.isCancelled = true;
                    decodeJob.fetcher.cancel();
                    Future<?> future = engineJob.future;
                    if (future != null) {
                        future.cancel(true);
                    }
                    engineJob.isCancelled = true;
                    engineJob.listener.onEngineJobCancelled(engineJob, engineJob.key);
                }
            } else {
                if (engineJob.ignoredCallbacks == null) {
                    engineJob.ignoredCallbacks = new HashSet();
                }
                engineJob.ignoredCallbacks.add(resourceCallback);
            }
            this.loadStatus = null;
        }
        if (this.resource != null) {
            releaseResource(this.resource);
        }
        if (canNotifyStatusChanged()) {
            this.target.onLoadCleared(getPlaceholderDrawable());
        }
        this.status = Status.CLEARED;
    }

    @Override // com.bumptech.glide.request.Request
    public final void pause() {
        clear();
        this.status = Status.PAUSED;
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isRunning() {
        return this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE;
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isComplete() {
        return this.status == Status.COMPLETE;
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isResourceSet() {
        return isComplete();
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isCancelled() {
        return this.status == Status.CANCELLED || this.status == Status.CLEARED;
    }

    private Drawable getPlaceholderDrawable() {
        if (this.placeholderDrawable == null && this.placeholderResourceId > 0) {
            this.placeholderDrawable = this.context.getResources().getDrawable(this.placeholderResourceId);
        }
        return this.placeholderDrawable;
    }

    @Override // com.bumptech.glide.request.target.SizeReadyCallback
    public final void onSizeReady(int width, int height) {
        EngineResource engineResource;
        EngineResource<?> engineResource2;
        Engine.LoadStatus loadStatus;
        if (Log.isLoggable("GenericRequest", 2)) {
            logV("Got onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
        }
        if (this.status == Status.WAITING_FOR_SIZE) {
            this.status = Status.RUNNING;
            int width2 = Math.round(this.sizeMultiplier * width);
            int height2 = Math.round(this.sizeMultiplier * height);
            DataFetcher<T> dataFetcher = this.loadProvider.getModelLoader().getResourceFetcher(this.model, width2, height2);
            if (dataFetcher == null) {
                onException(new Exception("Failed to load model: '" + this.model + "'"));
                return;
            }
            ResourceTranscoder<Z, R> transcoder = this.loadProvider.getTranscoder();
            if (Log.isLoggable("GenericRequest", 2)) {
                logV("finished setup for calling load in " + LogTime.getElapsedMillis(this.startTime));
            }
            this.loadedFromMemoryCache = true;
            Engine engine = this.engine;
            Key key = this.signature;
            LoadProvider<A, T, Z, R> loadProvider = this.loadProvider;
            Transformation<Z> transformation = this.transformation;
            Priority priority = this.priority;
            boolean z = this.isMemoryCacheable;
            DiskCacheStrategy diskCacheStrategy = this.diskCacheStrategy;
            Util.assertMainThread();
            long logTime = LogTime.getLogTime();
            EngineKey engineKey = new EngineKey(dataFetcher.getId(), key, width2, height2, loadProvider.getCacheDecoder(), loadProvider.getSourceDecoder(), transformation, loadProvider.getEncoder(), transcoder, loadProvider.getSourceEncoder());
            if (!z) {
                engineResource = null;
            } else {
                Resource<?> remove = engine.cache.remove(engineKey);
                if (remove == null) {
                    engineResource = null;
                } else if (remove instanceof EngineResource) {
                    engineResource = (EngineResource) remove;
                } else {
                    engineResource = new EngineResource(remove, true);
                }
                if (engineResource != null) {
                    engineResource.acquire();
                    engine.activeResources.put(engineKey, new Engine.ResourceWeakReference(engineKey, engineResource, engine.getReferenceQueue()));
                }
            }
            if (engineResource != null) {
                onResourceReady(engineResource);
                if (Log.isLoggable("Engine", 2)) {
                    Engine.logWithTimeAndKey("Loaded resource from cache", logTime, engineKey);
                }
                loadStatus = null;
            } else {
                if (!z) {
                    engineResource2 = null;
                } else {
                    WeakReference<EngineResource<?>> weakReference = engine.activeResources.get(engineKey);
                    if (weakReference == null) {
                        engineResource2 = null;
                    } else {
                        engineResource2 = weakReference.get();
                        if (engineResource2 != null) {
                            engineResource2.acquire();
                        } else {
                            engine.activeResources.remove(engineKey);
                        }
                    }
                }
                if (engineResource2 != null) {
                    onResourceReady(engineResource2);
                    if (Log.isLoggable("Engine", 2)) {
                        Engine.logWithTimeAndKey("Loaded resource from active resources", logTime, engineKey);
                    }
                    loadStatus = null;
                } else {
                    EngineJob engineJob = engine.jobs.get(engineKey);
                    if (engineJob != null) {
                        engineJob.addCallback(this);
                        if (Log.isLoggable("Engine", 2)) {
                            Engine.logWithTimeAndKey("Added to existing load", logTime, engineKey);
                        }
                        loadStatus = new Engine.LoadStatus(this, engineJob);
                    } else {
                        Engine.EngineJobFactory engineJobFactory = engine.engineJobFactory;
                        EngineJob engineJob2 = new EngineJob(engineKey, engineJobFactory.diskCacheService, engineJobFactory.sourceService, z, engineJobFactory.listener);
                        EngineRunnable engineRunnable = new EngineRunnable(engineJob2, new DecodeJob(engineKey, width2, height2, dataFetcher, loadProvider, transformation, transcoder, engine.diskCacheProvider, diskCacheStrategy, priority), priority);
                        engine.jobs.put(engineKey, engineJob2);
                        engineJob2.addCallback(this);
                        engineJob2.engineRunnable = engineRunnable;
                        engineJob2.future = engineJob2.diskCacheService.submit(engineRunnable);
                        if (Log.isLoggable("Engine", 2)) {
                            Engine.logWithTimeAndKey("Started new load", logTime, engineKey);
                        }
                        loadStatus = new Engine.LoadStatus(this, engineJob2);
                    }
                }
            }
            this.loadStatus = loadStatus;
            this.loadedFromMemoryCache = this.resource != null;
            if (Log.isLoggable("GenericRequest", 2)) {
                logV("finished onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private boolean canNotifyStatusChanged() {
        return this.requestCoordinator == null || this.requestCoordinator.canNotifyStatusChanged(this);
    }

    private boolean isFirstReadyResource() {
        return this.requestCoordinator == null || !this.requestCoordinator.isAnyResourceSet();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.request.ResourceCallback
    public final void onResourceReady(Resource<?> resource) {
        if (resource == null) {
            onException(new Exception("Expected to receive a Resource<R> with an object of " + this.transcodeClass + " inside, but instead got null."));
            return;
        }
        Object obj = resource.get();
        if (obj == null || !this.transcodeClass.isAssignableFrom(obj.getClass())) {
            releaseResource(resource);
            onException(new Exception("Expected to receive an object of " + this.transcodeClass + " but instead got " + (obj != null ? obj.getClass() : "") + "{" + obj + "} inside Resource{" + resource + "}." + (obj != null ? "" : " To indicate failure return a null Resource object, rather than a Resource object containing null data.")));
            return;
        }
        if (!(this.requestCoordinator == null || this.requestCoordinator.canSetImage(this))) {
            releaseResource(resource);
            this.status = Status.COMPLETE;
            return;
        }
        boolean isFirstReadyResource = isFirstReadyResource();
        this.status = Status.COMPLETE;
        this.resource = resource;
        if (this.requestListener == null || !this.requestListener.onResourceReady$25390db0()) {
            this.target.onResourceReady(obj, this.animationFactory.build(this.loadedFromMemoryCache, isFirstReadyResource));
        }
        if (this.requestCoordinator != null) {
            this.requestCoordinator.onRequestSuccess(this);
        }
        if (!Log.isLoggable("GenericRequest", 2)) {
            return;
        }
        logV("Resource ready in " + LogTime.getElapsedMillis(this.startTime) + " size: " + (resource.getSize() * 9.5367431640625E-7d) + " fromCache: " + this.loadedFromMemoryCache);
    }

    @Override // com.bumptech.glide.request.ResourceCallback
    public final void onException(Exception e) {
        Drawable drawable;
        Log.isLoggable("GenericRequest", 3);
        this.status = Status.FAILED;
        if (this.requestListener != null) {
            RequestListener<? super A, R> requestListener = this.requestListener;
            isFirstReadyResource();
            if (requestListener.onException$6f56094()) {
                return;
            }
        }
        if (!canNotifyStatusChanged()) {
            return;
        }
        if (this.model != null) {
            drawable = null;
        } else {
            if (this.fallbackDrawable == null && this.fallbackResourceId > 0) {
                this.fallbackDrawable = this.context.getResources().getDrawable(this.fallbackResourceId);
            }
            drawable = this.fallbackDrawable;
        }
        if (drawable == null) {
            if (this.errorDrawable == null && this.errorResourceId > 0) {
                this.errorDrawable = this.context.getResources().getDrawable(this.errorResourceId);
            }
            drawable = this.errorDrawable;
        }
        if (drawable == null) {
            drawable = getPlaceholderDrawable();
        }
        this.target.onLoadFailed$71731cd5(drawable);
    }

    private void logV(String message) {
        Log.v("GenericRequest", message + " this: " + this.tag);
    }

    private void releaseResource(Resource resource) {
        Util.assertMainThread();
        if (resource instanceof EngineResource) {
            ((EngineResource) resource).release();
            this.resource = null;
            return;
        }
        throw new IllegalArgumentException("Cannot release anything but an EngineResource");
    }
}
