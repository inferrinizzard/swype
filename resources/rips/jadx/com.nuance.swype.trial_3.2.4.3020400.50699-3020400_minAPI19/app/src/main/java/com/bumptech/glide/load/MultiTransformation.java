package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class MultiTransformation<T> implements Transformation<T> {
    private String id;
    private final Collection<? extends Transformation<T>> transformations;

    @SafeVarargs
    public MultiTransformation(Transformation<T>... transformations) {
        if (transformations.length <= 0) {
            throw new IllegalArgumentException("MultiTransformation must contain at least one Transformation");
        }
        this.transformations = Arrays.asList(transformations);
    }

    @Override // com.bumptech.glide.load.Transformation
    public final Resource<T> transform(Resource<T> resource, int outWidth, int outHeight) {
        Resource<T> previous = resource;
        Iterator i$ = this.transformations.iterator();
        while (i$.hasNext()) {
            Resource<T> transformed = i$.next().transform(previous, outWidth, outHeight);
            if (previous != null && !previous.equals(resource) && !previous.equals(transformed)) {
                previous.recycle();
            }
            previous = transformed;
        }
        return previous;
    }

    @Override // com.bumptech.glide.load.Transformation
    public final String getId() {
        if (this.id == null) {
            StringBuilder sb = new StringBuilder();
            for (Transformation<T> transformation : this.transformations) {
                sb.append(transformation.getId());
            }
            this.id = sb.toString();
        }
        return this.id;
    }
}
