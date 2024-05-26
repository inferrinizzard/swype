package com.bumptech.glide.load.model;

import android.content.Context;
import com.bumptech.glide.load.data.DataFetcher;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public final class GenericLoaderFactory {
    private static final ModelLoader NULL_MODEL_LOADER = new ModelLoader() { // from class: com.bumptech.glide.load.model.GenericLoaderFactory.1
        @Override // com.bumptech.glide.load.model.ModelLoader
        public final DataFetcher getResourceFetcher(Object model, int width, int height) {
            throw new NoSuchMethodError("This should never be called!");
        }

        public final String toString() {
            return "NULL_MODEL_LOADER";
        }
    };
    private final Context context;
    private final Map<Class, Map<Class, ModelLoaderFactory>> modelClassToResourceFactories = new HashMap();
    private final Map<Class, Map<Class, ModelLoader>> cachedModelLoaders = new HashMap();

    public GenericLoaderFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    public final synchronized <T, Y> ModelLoaderFactory<T, Y> register(Class<T> modelClass, Class<Y> resourceClass, ModelLoaderFactory<T, Y> factory) {
        ModelLoaderFactory previous;
        this.cachedModelLoaders.clear();
        Map<Class, ModelLoaderFactory> resourceToFactories = this.modelClassToResourceFactories.get(modelClass);
        if (resourceToFactories == null) {
            resourceToFactories = new HashMap<>();
            this.modelClassToResourceFactories.put(modelClass, resourceToFactories);
        }
        previous = resourceToFactories.put(resourceClass, factory);
        if (previous != null) {
            Iterator i$ = this.modelClassToResourceFactories.values().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                if (i$.next().containsValue(previous)) {
                    previous = null;
                    break;
                }
            }
        }
        return previous;
    }

    public final synchronized <T, Y> ModelLoader<T, Y> buildModelLoader(Class<T> modelClass, Class<Y> resourceClass) {
        ModelLoader<T, Y> modelLoader;
        ModelLoaderFactory<T, Y> modelLoaderFactory;
        Map<Class, ModelLoaderFactory> map;
        Map<Class, ModelLoader> map2 = this.cachedModelLoaders.get(modelClass);
        ModelLoader<T, Y> result = map2 != null ? map2.get(resourceClass) : null;
        if (result != null) {
            modelLoader = NULL_MODEL_LOADER.equals(result) ? null : result;
        } else {
            Map<Class, ModelLoaderFactory> map3 = this.modelClassToResourceFactories.get(modelClass);
            ModelLoaderFactory<T, Y> factory = map3 != null ? map3.get(resourceClass) : null;
            if (factory == null) {
                Iterator<Class> it = this.modelClassToResourceFactories.keySet().iterator();
                ModelLoaderFactory<T, Y> modelLoaderFactory2 = factory;
                while (true) {
                    if (!it.hasNext()) {
                        factory = modelLoaderFactory2;
                        break;
                    }
                    Class next = it.next();
                    if (next.isAssignableFrom(modelClass) && (map = this.modelClassToResourceFactories.get(next)) != null) {
                        modelLoaderFactory = map.get(resourceClass);
                        if (modelLoaderFactory != null) {
                            factory = modelLoaderFactory;
                            break;
                        }
                    } else {
                        modelLoaderFactory = modelLoaderFactory2;
                    }
                    modelLoaderFactory2 = modelLoaderFactory;
                }
            }
            if (factory != null) {
                result = factory.build(this.context, this);
                cacheModelLoader(modelClass, resourceClass, result);
            } else {
                cacheModelLoader(modelClass, resourceClass, NULL_MODEL_LOADER);
            }
            modelLoader = result;
        }
        return modelLoader;
    }

    private <T, Y> void cacheModelLoader(Class<T> modelClass, Class<Y> resourceClass, ModelLoader<T, Y> modelLoader) {
        Map<Class, ModelLoader> resourceToLoaders = this.cachedModelLoaders.get(modelClass);
        if (resourceToLoaders == null) {
            resourceToLoaders = new HashMap<>();
            this.cachedModelLoaders.put(modelClass, resourceToLoaders);
        }
        resourceToLoaders.put(resourceClass, modelLoader);
    }
}
