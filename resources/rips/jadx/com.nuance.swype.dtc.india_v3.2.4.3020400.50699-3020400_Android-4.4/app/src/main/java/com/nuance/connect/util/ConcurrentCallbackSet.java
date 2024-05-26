package com.nuance.connect.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: classes.dex */
public final class ConcurrentCallbackSet<E> {
    private final HashSet<E> backing = new HashSet<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();

    public final boolean add(E e) {
        this.writeLock.lock();
        try {
            return this.backing.add(e);
        } finally {
            this.writeLock.unlock();
        }
    }

    public final void addAll(Collection<? extends E> collection) {
        this.writeLock.lock();
        try {
            this.backing.addAll(collection);
        } finally {
            this.writeLock.unlock();
        }
    }

    public final void clear() {
        this.writeLock.lock();
        try {
            this.backing.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    public final boolean isEmpty() {
        this.readLock.lock();
        try {
            return this.backing.isEmpty();
        } finally {
            this.readLock.unlock();
        }
    }

    public final Iterator<E> iterator() {
        this.readLock.lock();
        try {
            return new HashSet(this.backing).iterator();
        } finally {
            this.readLock.unlock();
        }
    }

    public final boolean remove(E e) {
        this.writeLock.lock();
        try {
            return this.backing.remove(e);
        } finally {
            this.writeLock.unlock();
        }
    }

    public final boolean removeAll(Collection<? extends E> collection) {
        this.writeLock.lock();
        try {
            return this.backing.removeAll(collection);
        } finally {
            this.writeLock.unlock();
        }
    }

    public final Object[] toArray() {
        this.readLock.lock();
        try {
            return this.backing.toArray();
        } finally {
            this.readLock.unlock();
        }
    }

    public final <T> T[] toArray(T[] tArr) {
        this.readLock.lock();
        try {
            return (T[]) this.backing.toArray(tArr);
        } finally {
            this.readLock.unlock();
        }
    }

    public final Set<E> toSet() {
        return new HashSet(this.backing);
    }
}
