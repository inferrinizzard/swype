package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class zza<E> extends AbstractSet<E> {
    private final ArrayMap<E, E> AV;

    public zza() {
        this.AV = new ArrayMap<>();
    }

    private zza(int i) {
        this.AV = new ArrayMap<>(i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public zza(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean add(E e) {
        if (this.AV.containsKey(e)) {
            return false;
        }
        this.AV.put(e, e);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean addAll(Collection<? extends E> collection) {
        return collection instanceof zza ? zza((zza) collection) : super.addAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final void clear() {
        this.AV.clear();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean contains(Object obj) {
        return this.AV.containsKey(obj);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public final Iterator<E> iterator() {
        return this.AV.keySet().iterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean remove(Object obj) {
        if (!this.AV.containsKey(obj)) {
            return false;
        }
        this.AV.remove(obj);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final int size() {
        return this.AV.size();
    }

    private boolean zza(zza<? extends E> zzaVar) {
        int size = size();
        ArrayMap<E, E> arrayMap = this.AV;
        ArrayMap<? extends E, ? extends E> arrayMap2 = zzaVar.AV;
        int i = arrayMap2.mSize;
        arrayMap.ensureCapacity(arrayMap.mSize + i);
        if (arrayMap.mSize == 0) {
            if (i > 0) {
                System.arraycopy(arrayMap2.mHashes, 0, arrayMap.mHashes, 0, i);
                System.arraycopy(arrayMap2.mArray, 0, arrayMap.mArray, 0, i << 1);
                arrayMap.mSize = i;
            }
        } else {
            for (int i2 = 0; i2 < i; i2++) {
                arrayMap.put(arrayMap2.keyAt(i2), arrayMap2.valueAt(i2));
            }
        }
        return size() > size;
    }
}
