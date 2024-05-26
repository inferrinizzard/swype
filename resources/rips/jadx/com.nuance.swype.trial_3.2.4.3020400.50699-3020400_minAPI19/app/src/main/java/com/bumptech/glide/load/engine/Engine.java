package com.bumptech.glide.load.engine;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DecodeJob;
import com.bumptech.glide.load.engine.EngineResource;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public final class Engine implements EngineJobListener, EngineResource.ResourceListener, MemoryCache.ResourceRemovedListener {
    public final Map<Key, WeakReference<EngineResource<?>>> activeResources;
    public final MemoryCache cache;
    public final LazyDiskCacheProvider diskCacheProvider;
    public final EngineJobFactory engineJobFactory;
    public final Map<Key, EngineJob> jobs;
    private final EngineKeyFactory keyFactory;
    private final ResourceRecycler resourceRecycler;
    private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;

    /* loaded from: classes.dex */
    public static class LoadStatus {
        public final ResourceCallback cb;
        public final EngineJob engineJob;

        public LoadStatus(ResourceCallback cb, EngineJob engineJob) {
            this.cb = cb;
            this.engineJob = engineJob;
        }
    }

    public Engine(MemoryCache memoryCache, DiskCache.Factory diskCacheFactory, ExecutorService diskCacheService, ExecutorService sourceService) {
        this(memoryCache, diskCacheFactory, diskCacheService, sourceService, (byte) 0);
    }

    /* JADX WARN: Can't parse signature for local variable: Ljava/util/Map<Lcom/bumptech/glide/load/Key;Lcom/bumptech/glide/load/engine/EngineJob;>;
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:588)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoAttachVisitor.checkSignature(DebugInfoAttachVisitor.java:160)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoAttachVisitor.getVarType(DebugInfoAttachVisitor.java:147)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoAttachVisitor.attachDebugInfo(DebugInfoAttachVisitor.java:94)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoAttachVisitor.processDebugInfo(DebugInfoAttachVisitor.java:51)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoAttachVisitor.visit(DebugInfoAttachVisitor.java:41)
     */
    private Engine(MemoryCache cache, DiskCache.Factory diskCacheFactory, ExecutorService diskCacheService, ExecutorService sourceService, byte jobs) {
        this.cache = cache;
        this.diskCacheProvider = new LazyDiskCacheProvider(diskCacheFactory);
        Map<Key, WeakReference<EngineResource<?>>> activeResources = new HashMap<>();
        this.activeResources = activeResources;
        EngineKeyFactory keyFactory = new EngineKeyFactory();
        this.keyFactory = keyFactory;
        Map<Key, EngineJob> jobs2 = new HashMap<>();
        this.jobs = jobs2;
        EngineJobFactory engineJobFactory = new EngineJobFactory(diskCacheService, sourceService, this);
        this.engineJobFactory = engineJobFactory;
        ResourceRecycler resourceRecycler = new ResourceRecycler();
        this.resourceRecycler = resourceRecycler;
        cache.setResourceRemovedListener(this);
    }

    public static void logWithTimeAndKey(String log, long startTime, Key key) {
        Log.v("Engine", log + " in " + LogTime.getElapsedMillis(startTime) + "ms, key: " + key);
    }

    @Override // com.bumptech.glide.load.engine.EngineJobListener
    public final void onEngineJobComplete(Key key, EngineResource<?> resource) {
        Util.assertMainThread();
        if (resource != null) {
            resource.key = key;
            resource.listener = this;
            if (resource.isCacheable) {
                this.activeResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
            }
        }
        this.jobs.remove(key);
    }

    @Override // com.bumptech.glide.load.engine.EngineJobListener
    public final void onEngineJobCancelled(EngineJob engineJob, Key key) {
        Util.assertMainThread();
        EngineJob current = this.jobs.get(key);
        if (engineJob.equals(current)) {
            this.jobs.remove(key);
        }
    }

    @Override // com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener
    public final void onResourceRemoved(Resource<?> resource) {
        Util.assertMainThread();
        this.resourceRecycler.recycle(resource);
    }

    @Override // com.bumptech.glide.load.engine.EngineResource.ResourceListener
    public final void onResourceReleased(Key cacheKey, EngineResource resource) {
        Util.assertMainThread();
        this.activeResources.remove(cacheKey);
        if (resource.isCacheable) {
            this.cache.put(cacheKey, resource);
        } else {
            this.resourceRecycler.recycle(resource);
        }
    }

    public final ReferenceQueue<EngineResource<?>> getReferenceQueue() {
        if (this.resourceReferenceQueue == null) {
            this.resourceReferenceQueue = new ReferenceQueue<>();
            Looper.myQueue().addIdleHandler(new RefQueueIdleHandler(this.activeResources, this.resourceReferenceQueue));
        }
        return this.resourceReferenceQueue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LazyDiskCacheProvider implements DecodeJob.DiskCacheProvider {
        private volatile DiskCache diskCache;
        private final DiskCache.Factory factory;

        public LazyDiskCacheProvider(DiskCache.Factory factory) {
            this.factory = factory;
        }

        @Override // com.bumptech.glide.load.engine.DecodeJob.DiskCacheProvider
        public final DiskCache getDiskCache() {
            if (this.diskCache == null) {
                synchronized (this) {
                    if (this.diskCache == null) {
                        this.diskCache = this.factory.build();
                    }
                    if (this.diskCache == null) {
                        this.diskCache = new DiskCacheAdapter();
                    }
                }
            }
            return this.diskCache;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ResourceWeakReference extends WeakReference<EngineResource<?>> {
        private final Key key;

        public ResourceWeakReference(Key key, EngineResource<?> r, ReferenceQueue<? super EngineResource<?>> q) {
            super(r, q);
            this.key = key;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RefQueueIdleHandler implements MessageQueue.IdleHandler {
        private final Map<Key, WeakReference<EngineResource<?>>> activeResources;
        private final ReferenceQueue<EngineResource<?>> queue;

        public RefQueueIdleHandler(Map<Key, WeakReference<EngineResource<?>>> activeResources, ReferenceQueue<EngineResource<?>> queue) {
            this.activeResources = activeResources;
            this.queue = queue;
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ResourceWeakReference ref = (ResourceWeakReference) this.queue.poll();
            if (ref != null) {
                this.activeResources.remove(ref.key);
                return true;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class EngineJobFactory {
        public final ExecutorService diskCacheService;
        public final EngineJobListener listener;
        public final ExecutorService sourceService;

        public EngineJobFactory(ExecutorService diskCacheService, ExecutorService sourceService, EngineJobListener listener) {
            this.diskCacheService = diskCacheService;
            this.sourceService = sourceService;
            this.listener = listener;
        }
    }
}
