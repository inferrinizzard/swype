package com.flurry.sdk;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class jw<K, V> {
    public final Map<K, List<V>> a = new HashMap();
    private int b;

    public final void a() {
        this.a.clear();
    }

    public final List<V> a(K k) {
        if (k == null) {
            return Collections.emptyList();
        }
        List<V> a = a((jw<K, V>) k, false);
        if (a == null) {
            return Collections.emptyList();
        }
        return a;
    }

    public final void a(K k, V v) {
        if (k != null) {
            a((jw<K, V>) k, true).add(v);
        }
    }

    public final boolean b(K k, V v) {
        List<V> a;
        boolean z = false;
        if (k != null && (a = a((jw<K, V>) k, false)) != null) {
            z = a.remove(v);
            if (a.size() == 0) {
                this.a.remove(k);
            }
        }
        return z;
    }

    public final boolean b(K k) {
        return this.a.remove(k) != null;
    }

    public final Collection<Map.Entry<K, V>> b() {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<K, List<V>> entry : this.a.entrySet()) {
            Iterator<V> it = entry.getValue().iterator();
            while (it.hasNext()) {
                arrayList.add(new AbstractMap.SimpleImmutableEntry(entry.getKey(), it.next()));
            }
        }
        return arrayList;
    }

    public final List<V> a(K k, boolean z) {
        List<V> list = this.a.get(k);
        if (z && list == null) {
            if (this.b > 0) {
                list = new ArrayList<>(this.b);
            } else {
                list = new ArrayList<>();
            }
            this.a.put(k, list);
        }
        return list;
    }
}
