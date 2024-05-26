package com.nuance.nmdp.speechkit.oem;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class OemList {
    private final ArrayList<Object> _list;

    /* loaded from: classes.dex */
    public abstract class OemListIterator {
        private final Iterator<Object> _i;

        public OemListIterator(OemList list) {
            this._i = list._list.iterator();
        }

        public boolean hasMore() {
            return this._i.hasNext();
        }

        public Object next() {
            return this._i.next();
        }
    }

    public OemList(int initialCapacity) {
        this._list = new ArrayList<>(initialCapacity);
    }

    public void add(Object item) {
        this._list.add(item);
    }

    public void remove(Object item) {
        this._list.remove(item);
    }

    public Object removeAt(int index) {
        return this._list.remove(index);
    }

    public Object get(int index) {
        return this._list.get(index);
    }

    public void clear() {
        this._list.clear();
    }

    public int size() {
        return this._list.size();
    }

    public boolean contains(Object o) {
        return this._list.contains(o);
    }

    public void trim() {
        this._list.trimToSize();
    }
}
