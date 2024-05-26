package com.bumptech.glide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorFileLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorResourceLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorStringLoader;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorUriLoader;
import com.bumptech.glide.load.model.stream.HttpUrlGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamByteArrayLoader;
import com.bumptech.glide.load.model.stream.StreamFileLoader;
import com.bumptech.glide.load.model.stream.StreamResourceLoader;
import com.bumptech.glide.load.model.stream.StreamStringLoader;
import com.bumptech.glide.load.model.stream.StreamUriLoader;
import com.bumptech.glide.load.model.stream.StreamUrlLoader;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDataLoadProvider;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.bitmap.ImageVideoDataLoadProvider;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDataLoadProvider;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.file.StreamFileDataLoadProvider;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableLoadProvider;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperTransformation;
import com.bumptech.glide.load.resource.gifbitmap.ImageVideoGifDrawableLoadProvider;
import com.bumptech.glide.load.resource.transcode.GifBitmapWrapperDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.GlideBitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.bumptech.glide.manager.RequestManagerFragment;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.provider.DataLoadProvider;
import com.bumptech.glide.provider.DataLoadProviderRegistry;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class Glide {
    private static volatile Glide glide;
    private static boolean modulesEnabled = true;
    private final CenterCrop bitmapCenterCrop;
    private final FitCenter bitmapFitCenter;
    public final BitmapPool bitmapPool;
    private final BitmapPreFiller bitmapPreFiller;
    private final DecodeFormat decodeFormat;
    final GifBitmapWrapperTransformation drawableCenterCrop;
    final GifBitmapWrapperTransformation drawableFitCenter;
    final Engine engine;
    private final GenericLoaderFactory loaderFactory;
    public final MemoryCache memoryCache;
    private final ImageViewTargetFactory imageViewTargetFactory = new ImageViewTargetFactory();
    final TranscoderRegistry transcoderRegistry = new TranscoderRegistry();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final DataLoadProviderRegistry dataLoadProviderRegistry = new DataLoadProviderRegistry();

    public static Glide get(Context context) {
        List<GlideModule> modules;
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    Context applicationContext = context.getApplicationContext();
                    GlideBuilder builder = new GlideBuilder(applicationContext);
                    if (modulesEnabled) {
                        modules = new ManifestParser(applicationContext).parse();
                    } else {
                        modules = Collections.emptyList();
                    }
                    Iterator i$ = modules.iterator();
                    while (i$.hasNext()) {
                        i$.next().applyOptions(applicationContext, builder);
                    }
                    if (builder.sourceService == null) {
                        builder.sourceService = new FifoPriorityThreadPoolExecutor(Math.max(1, Runtime.getRuntime().availableProcessors()));
                    }
                    if (builder.diskCacheService == null) {
                        builder.diskCacheService = new FifoPriorityThreadPoolExecutor(1);
                    }
                    MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(builder.context);
                    if (builder.bitmapPool == null) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            builder.bitmapPool = new LruBitmapPool(memorySizeCalculator.bitmapPoolSize);
                        } else {
                            builder.bitmapPool = new BitmapPoolAdapter();
                        }
                    }
                    if (builder.memoryCache == null) {
                        builder.memoryCache = new LruResourceCache(memorySizeCalculator.memoryCacheSize);
                    }
                    if (builder.diskCacheFactory == null) {
                        builder.diskCacheFactory = new InternalCacheDiskCacheFactory(builder.context);
                    }
                    if (builder.engine == null) {
                        builder.engine = new Engine(builder.memoryCache, builder.diskCacheFactory, builder.diskCacheService, builder.sourceService);
                    }
                    if (builder.decodeFormat == null) {
                        builder.decodeFormat = DecodeFormat.DEFAULT;
                    }
                    glide = new Glide(builder.engine, builder.memoryCache, builder.bitmapPool, builder.context, builder.decodeFormat);
                    Iterator i$2 = modules.iterator();
                    while (i$2.hasNext()) {
                        i$2.next().registerComponents(applicationContext, glide);
                    }
                }
            }
        }
        return glide;
    }

    private Glide(Engine engine, MemoryCache memoryCache, BitmapPool bitmapPool, Context context, DecodeFormat decodeFormat) {
        this.engine = engine;
        this.bitmapPool = bitmapPool;
        this.memoryCache = memoryCache;
        this.decodeFormat = decodeFormat;
        this.loaderFactory = new GenericLoaderFactory(context);
        this.bitmapPreFiller = new BitmapPreFiller(memoryCache, bitmapPool, decodeFormat);
        StreamBitmapDataLoadProvider streamBitmapLoadProvider = new StreamBitmapDataLoadProvider(bitmapPool, decodeFormat);
        this.dataLoadProviderRegistry.register(InputStream.class, Bitmap.class, streamBitmapLoadProvider);
        FileDescriptorBitmapDataLoadProvider fileDescriptorLoadProvider = new FileDescriptorBitmapDataLoadProvider(bitmapPool, decodeFormat);
        this.dataLoadProviderRegistry.register(ParcelFileDescriptor.class, Bitmap.class, fileDescriptorLoadProvider);
        ImageVideoDataLoadProvider imageVideoDataLoadProvider = new ImageVideoDataLoadProvider(streamBitmapLoadProvider, fileDescriptorLoadProvider);
        this.dataLoadProviderRegistry.register(ImageVideoWrapper.class, Bitmap.class, imageVideoDataLoadProvider);
        GifDrawableLoadProvider gifDrawableLoadProvider = new GifDrawableLoadProvider(context, bitmapPool);
        this.dataLoadProviderRegistry.register(InputStream.class, GifDrawable.class, gifDrawableLoadProvider);
        this.dataLoadProviderRegistry.register(ImageVideoWrapper.class, GifBitmapWrapper.class, new ImageVideoGifDrawableLoadProvider(imageVideoDataLoadProvider, gifDrawableLoadProvider, bitmapPool));
        this.dataLoadProviderRegistry.register(InputStream.class, File.class, new StreamFileDataLoadProvider());
        register(File.class, ParcelFileDescriptor.class, new FileDescriptorFileLoader.Factory());
        register(File.class, InputStream.class, new StreamFileLoader.Factory());
        register(Integer.TYPE, ParcelFileDescriptor.class, new FileDescriptorResourceLoader.Factory());
        register(Integer.TYPE, InputStream.class, new StreamResourceLoader.Factory());
        register(Integer.class, ParcelFileDescriptor.class, new FileDescriptorResourceLoader.Factory());
        register(Integer.class, InputStream.class, new StreamResourceLoader.Factory());
        register(String.class, ParcelFileDescriptor.class, new FileDescriptorStringLoader.Factory());
        register(String.class, InputStream.class, new StreamStringLoader.Factory());
        register(Uri.class, ParcelFileDescriptor.class, new FileDescriptorUriLoader.Factory());
        register(Uri.class, InputStream.class, new StreamUriLoader.Factory());
        register(URL.class, InputStream.class, new StreamUrlLoader.Factory());
        register(GlideUrl.class, InputStream.class, new HttpUrlGlideUrlLoader.Factory());
        register(byte[].class, InputStream.class, new StreamByteArrayLoader.Factory());
        this.transcoderRegistry.register(Bitmap.class, GlideBitmapDrawable.class, new GlideBitmapDrawableTranscoder(context.getResources(), bitmapPool));
        this.transcoderRegistry.register(GifBitmapWrapper.class, GlideDrawable.class, new GifBitmapWrapperDrawableTranscoder(new GlideBitmapDrawableTranscoder(context.getResources(), bitmapPool)));
        this.bitmapCenterCrop = new CenterCrop(bitmapPool);
        this.drawableCenterCrop = new GifBitmapWrapperTransformation(bitmapPool, this.bitmapCenterCrop);
        this.bitmapFitCenter = new FitCenter(bitmapPool);
        this.drawableFitCenter = new GifBitmapWrapperTransformation(bitmapPool, this.bitmapFitCenter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <T, Z> DataLoadProvider<T, Z> buildDataProvider(Class<T> dataClass, Class<Z> decodedClass) {
        return this.dataLoadProviderRegistry.get(dataClass, decodedClass);
    }

    public final void clearMemory() {
        Util.assertMainThread();
        this.memoryCache.clearMemory();
        this.bitmapPool.clearMemory();
    }

    public final void setMemoryCategory(MemoryCategory memoryCategory) {
        Util.assertMainThread();
        this.memoryCache.setSizeMultiplier(memoryCategory.multiplier);
        this.bitmapPool.setSizeMultiplier(memoryCategory.multiplier);
    }

    public static void clear(Target<?> target) {
        Util.assertMainThread();
        Request request = target.getRequest();
        if (request != null) {
            request.clear();
            target.setRequest(null);
        }
    }

    public static void clear(View view) {
        clear(new ClearTarget(view));
    }

    public final <T, Y> void register(Class<T> modelClass, Class<Y> resourceClass, ModelLoaderFactory<T, Y> factory) {
        this.loaderFactory.register(modelClass, resourceClass, factory);
    }

    public static <T, Y> ModelLoader<T, Y> buildModelLoader(Class<T> modelClass, Class<Y> resourceClass, Context context) {
        if (modelClass == null) {
            Log.isLoggable("Glide", 3);
            return null;
        }
        return get(context).loaderFactory.buildModelLoader(modelClass, resourceClass);
    }

    public static <T> ModelLoader<T, InputStream> buildStreamModelLoader(Class<T> modelClass, Context context) {
        return buildModelLoader(modelClass, InputStream.class, context);
    }

    public static <T> ModelLoader<T, ParcelFileDescriptor> buildFileDescriptorModelLoader(Class<T> modelClass, Context context) {
        return buildModelLoader(modelClass, ParcelFileDescriptor.class, context);
    }

    public static RequestManager with(Context context) {
        RequestManager requestManager;
        RequestManagerRetriever requestManagerRetriever = RequestManagerRetriever.get();
        Context context2 = context;
        while (context2 != null) {
            if (Util.isOnMainThread() && !(context2 instanceof Application)) {
                if (context2 instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) context2;
                    if (Util.isOnBackgroundThread()) {
                        context2 = fragmentActivity.getApplicationContext();
                    } else {
                        RequestManagerRetriever.assertNotDestroyed(fragmentActivity);
                        SupportRequestManagerFragment supportRequestManagerFragment = requestManagerRetriever.getSupportRequestManagerFragment(fragmentActivity.getSupportFragmentManager());
                        requestManager = supportRequestManagerFragment.requestManager;
                        if (requestManager == null) {
                            RequestManager requestManager2 = new RequestManager(fragmentActivity, supportRequestManagerFragment.lifecycle, supportRequestManagerFragment.requestManagerTreeNode);
                            supportRequestManagerFragment.requestManager = requestManager2;
                            return requestManager2;
                        }
                        return requestManager;
                    }
                } else if (context2 instanceof Activity) {
                    Activity activity = (Activity) context2;
                    if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < 11) {
                        context2 = activity.getApplicationContext();
                    } else {
                        RequestManagerRetriever.assertNotDestroyed(activity);
                        RequestManagerFragment requestManagerFragment = requestManagerRetriever.getRequestManagerFragment(activity.getFragmentManager());
                        requestManager = requestManagerFragment.requestManager;
                        if (requestManager == null) {
                            RequestManager requestManager3 = new RequestManager(activity, requestManagerFragment.lifecycle, requestManagerFragment.requestManagerTreeNode);
                            requestManagerFragment.requestManager = requestManager3;
                            return requestManager3;
                        }
                        return requestManager;
                    }
                } else if (context2 instanceof ContextWrapper) {
                    context2 = ((ContextWrapper) context2).getBaseContext();
                }
            }
            return requestManagerRetriever.getApplicationManager(context2);
        }
        throw new IllegalArgumentException("You cannot start a load on a null Context");
    }

    /* loaded from: classes.dex */
    private static class ClearTarget extends ViewTarget<View, Object> {
        public ClearTarget(View view) {
            super(view);
        }

        @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
        public final void onLoadStarted(Drawable placeholder) {
        }

        @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
        public final void onLoadFailed$71731cd5(Drawable errorDrawable) {
        }

        @Override // com.bumptech.glide.request.target.Target
        public final void onResourceReady(Object resource, GlideAnimation<? super Object> glideAnimation) {
        }

        @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
        public final void onLoadCleared(Drawable placeholder) {
        }
    }
}
