package com.bumptech.glide;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.DefaultConnectivityMonitor;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.manager.NullConnectivityMonitor;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;
import java.io.InputStream;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class RequestManager implements LifecycleListener {
    public final Context context;
    public final Glide glide;
    public final Lifecycle lifecycle;
    public final OptionsApplier optionsApplier;
    public final RequestTracker requestTracker;
    private final RequestManagerTreeNode treeNode;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public RequestManager(android.content.Context r3, com.bumptech.glide.manager.Lifecycle r4, com.bumptech.glide.manager.RequestManagerTreeNode r5) {
        /*
            r2 = this;
            com.bumptech.glide.manager.RequestTracker r0 = new com.bumptech.glide.manager.RequestTracker
            r0.<init>()
            com.bumptech.glide.manager.ConnectivityMonitorFactory r1 = new com.bumptech.glide.manager.ConnectivityMonitorFactory
            r1.<init>()
            r2.<init>(r3, r4, r5, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.RequestManager.<init>(android.content.Context, com.bumptech.glide.manager.Lifecycle, com.bumptech.glide.manager.RequestManagerTreeNode):void");
    }

    private RequestManager(Context context, final Lifecycle lifecycle, RequestManagerTreeNode treeNode, RequestTracker requestTracker) {
        ConnectivityMonitor connectivityMonitor;
        this.context = context.getApplicationContext();
        this.lifecycle = lifecycle;
        this.treeNode = treeNode;
        this.requestTracker = requestTracker;
        this.glide = Glide.get(context);
        this.optionsApplier = new OptionsApplier();
        RequestManagerConnectivityListener requestManagerConnectivityListener = new RequestManagerConnectivityListener(requestTracker);
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0) {
            connectivityMonitor = new DefaultConnectivityMonitor(context, requestManagerConnectivityListener);
        } else {
            connectivityMonitor = new NullConnectivityMonitor();
        }
        if (Util.isOnBackgroundThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.bumptech.glide.RequestManager.1
                @Override // java.lang.Runnable
                public final void run() {
                    lifecycle.addListener(RequestManager.this);
                }
            });
        } else {
            lifecycle.addListener(this);
        }
        lifecycle.addListener(connectivityMonitor);
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public final void onDestroy() {
        RequestTracker requestTracker = this.requestTracker;
        Iterator it = Util.getSnapshot(requestTracker.requests).iterator();
        while (it.hasNext()) {
            ((Request) it.next()).clear();
        }
        requestTracker.pendingRequests.clear();
    }

    public final <T> DrawableTypeRequest<T> loadGeneric(Class<T> modelClass) {
        ModelLoader<T, InputStream> streamModelLoader = Glide.buildStreamModelLoader(modelClass, this.context);
        ModelLoader<T, ParcelFileDescriptor> fileDescriptorModelLoader = Glide.buildFileDescriptorModelLoader(modelClass, this.context);
        if (streamModelLoader == null && fileDescriptorModelLoader == null) {
            throw new IllegalArgumentException("Unknown type " + modelClass + ". You must provide a Model of a type for which there is a registered ModelLoader, if you are using a custom model, you must first call Glide#register with a ModelLoaderFactory for your custom model class");
        }
        return new DrawableTypeRequest<>(modelClass, streamModelLoader, fileDescriptorModelLoader, this.context, this.glide, this.requestTracker, this.lifecycle, this.optionsApplier);
    }

    /* loaded from: classes.dex */
    public final class GenericModelRequest<A, T> {
        public final Class<T> dataClass;
        public final ModelLoader<A, T> modelLoader;

        public GenericModelRequest(ModelLoader<A, T> modelLoader, Class<T> dataClass) {
            this.modelLoader = modelLoader;
            this.dataClass = dataClass;
        }

        /* loaded from: classes.dex */
        public final class GenericTypeRequest {
            public final A model;
            public final Class<A> modelClass;
            public final boolean providedModel = true;

            public GenericTypeRequest(A a) {
                this.model = a;
                this.modelClass = a != null ? (Class<A>) a.getClass() : null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OptionsApplier {
        OptionsApplier() {
        }
    }

    /* loaded from: classes.dex */
    private static class RequestManagerConnectivityListener implements ConnectivityMonitor.ConnectivityListener {
        private final RequestTracker requestTracker;

        public RequestManagerConnectivityListener(RequestTracker requestTracker) {
            this.requestTracker = requestTracker;
        }

        @Override // com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener
        public final void onConnectivityChanged(boolean isConnected) {
            if (isConnected) {
                RequestTracker requestTracker = this.requestTracker;
                for (Request request : Util.getSnapshot(requestTracker.requests)) {
                    if (!request.isComplete() && !request.isCancelled()) {
                        request.pause();
                        if (!requestTracker.isPaused) {
                            request.begin();
                        } else {
                            requestTracker.pendingRequests.add(request);
                        }
                    }
                }
            }
        }
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public final void onStart() {
        Util.assertMainThread();
        RequestTracker requestTracker = this.requestTracker;
        requestTracker.isPaused = false;
        for (Request request : Util.getSnapshot(requestTracker.requests)) {
            if (!request.isComplete() && !request.isCancelled() && !request.isRunning()) {
                request.begin();
            }
        }
        requestTracker.pendingRequests.clear();
    }

    @Override // com.bumptech.glide.manager.LifecycleListener
    public final void onStop() {
        Util.assertMainThread();
        RequestTracker requestTracker = this.requestTracker;
        requestTracker.isPaused = true;
        for (Request request : Util.getSnapshot(requestTracker.requests)) {
            if (request.isRunning()) {
                request.pause();
                requestTracker.pendingRequests.add(request);
            }
        }
    }
}
