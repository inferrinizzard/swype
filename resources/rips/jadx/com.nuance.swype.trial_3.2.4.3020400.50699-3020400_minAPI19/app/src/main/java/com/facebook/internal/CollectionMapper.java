package com.facebook.internal;

import com.facebook.FacebookException;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class CollectionMapper {

    /* loaded from: classes.dex */
    public interface Collection<T> {
        Object get(T t);

        Iterator<T> keyIterator();

        void set(T t, Object obj, OnErrorListener onErrorListener);
    }

    /* loaded from: classes.dex */
    public interface OnErrorListener {
        void onError(FacebookException facebookException);
    }

    /* loaded from: classes.dex */
    public interface OnMapValueCompleteListener extends OnErrorListener {
        void onComplete(Object obj);
    }

    /* loaded from: classes.dex */
    public interface OnMapperCompleteListener extends OnErrorListener {
        void onComplete();
    }

    /* loaded from: classes.dex */
    public interface ValueMapper {
        void mapValue(Object obj, OnMapValueCompleteListener onMapValueCompleteListener);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> void iterate(final Collection<T> collection, ValueMapper valueMapper, final OnMapperCompleteListener onMapperCompleteListener) {
        final Mutable mutable = new Mutable(false);
        final Mutable mutable2 = new Mutable(1);
        final OnMapperCompleteListener onMapperCompleteListener2 = new OnMapperCompleteListener() { // from class: com.facebook.internal.CollectionMapper.1
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r0v9, types: [T, java.lang.Integer] */
            @Override // com.facebook.internal.CollectionMapper.OnMapperCompleteListener
            public final void onComplete() {
                if (!((Boolean) Mutable.this.value).booleanValue()) {
                    Mutable mutable3 = mutable2;
                    ?? valueOf = Integer.valueOf(((Integer) mutable2.value).intValue() - 1);
                    mutable3.value = valueOf;
                    if (valueOf.intValue() == 0) {
                        onMapperCompleteListener.onComplete();
                    }
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r1v1, types: [T, java.lang.Boolean] */
            @Override // com.facebook.internal.CollectionMapper.OnErrorListener
            public final void onError(FacebookException exception) {
                if (!((Boolean) Mutable.this.value).booleanValue()) {
                    Mutable.this.value = true;
                    onMapperCompleteListener.onError(exception);
                }
            }
        };
        Iterator keyIterator = collection.keyIterator();
        LinkedList linkedList = new LinkedList();
        while (keyIterator.hasNext()) {
            linkedList.add(keyIterator.next());
        }
        for (final Object obj : linkedList) {
            Object obj2 = collection.get(obj);
            OnMapValueCompleteListener onMapValueCompleteListener = new OnMapValueCompleteListener() { // from class: com.facebook.internal.CollectionMapper.2
                @Override // com.facebook.internal.CollectionMapper.OnMapValueCompleteListener
                public final void onComplete(Object mappedValue) {
                    Collection.this.set(obj, mappedValue, onMapperCompleteListener2);
                    onMapperCompleteListener2.onComplete();
                }

                @Override // com.facebook.internal.CollectionMapper.OnErrorListener
                public final void onError(FacebookException exception) {
                    onMapperCompleteListener2.onError(exception);
                }
            };
            mutable2.value = (T) Integer.valueOf(((Integer) mutable2.value).intValue() + 1);
            valueMapper.mapValue(obj2, onMapValueCompleteListener);
        }
        onMapperCompleteListener2.onComplete();
    }

    private CollectionMapper() {
    }
}
