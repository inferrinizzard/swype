package com.bumptech.glide.util;

/* loaded from: classes.dex */
public final class MultiClassKey {
    private Class<?> first;
    private Class<?> second;

    public MultiClassKey() {
    }

    public MultiClassKey(Class<?> first, Class<?> second) {
        set(first, second);
    }

    public final void set(Class<?> first, Class<?> second) {
        this.first = first;
        this.second = second;
    }

    public final String toString() {
        return "MultiClassKey{first=" + this.first + ", second=" + this.second + '}';
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiClassKey that = (MultiClassKey) o;
        return this.first.equals(that.first) && this.second.equals(that.second);
    }

    public final int hashCode() {
        int result = this.first.hashCode();
        return (result * 31) + this.second.hashCode();
    }
}
