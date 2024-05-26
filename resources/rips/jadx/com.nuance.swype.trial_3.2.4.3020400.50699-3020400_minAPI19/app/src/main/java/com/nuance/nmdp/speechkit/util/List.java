package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.oem.OemList;
import java.util.Vector;

/* loaded from: classes.dex */
public final class List extends OemList {

    /* loaded from: classes.dex */
    public final class Iterator extends OemList.OemListIterator {
        public Iterator(List list) {
            super(list);
        }
    }

    public List() {
        super(0);
    }

    private List(int initialCapacity) {
        super(initialCapacity);
    }

    public final Iterator iterator() {
        return new Iterator(this);
    }

    public final List copy() {
        int size = size();
        List copy = new List(size);
        Iterator it = iterator();
        while (it.hasMore()) {
            copy.add(it.next());
        }
        return copy;
    }

    public final Vector toVector() {
        Vector v = new Vector(size());
        Iterator it = iterator();
        while (it.hasMore()) {
            v.addElement(it.next());
        }
        return v;
    }
}
