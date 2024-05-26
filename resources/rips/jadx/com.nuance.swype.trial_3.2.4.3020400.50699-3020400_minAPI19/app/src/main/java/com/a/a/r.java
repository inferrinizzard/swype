package com.a.a;

import com.a.a.g;
import java.lang.Comparable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class r<K extends Comparable<K>, V> extends AbstractMap<K, V> {
    private final int a;
    private List<r<K, V>.b> b;
    private Map<K, V> c;
    boolean d;

    /* JADX WARN: Incorrect inner types in field signature: Lcom/a/a/r<TK;TV;>.com/a/a/r$com/a/a/r$com/a/a/r$com/a/a/r$d; */
    private volatile d e;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class a {
        private static final Iterator<Object> a = new t();
        private static final Iterable<Object> b = new u();

        static <T> Iterable<T> a() {
            return (Iterable<T>) b;
        }
    }

    private r(int i) {
        this.a = i;
        this.b = Collections.emptyList();
        this.c = Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public /* synthetic */ r(int i, byte b2) {
        this(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <FieldDescriptorType extends g.a<FieldDescriptorType>> r<FieldDescriptorType, Object> a(int i) {
        return new s(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public V c(int i) {
        e();
        V v = (V) this.b.remove(i).getValue();
        if (!this.c.isEmpty()) {
            Iterator<Map.Entry<K, V>> it = f().entrySet().iterator();
            this.b.add(new b(this, it.next()));
            it.remove();
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e() {
        if (this.d) {
            throw new UnsupportedOperationException();
        }
    }

    private SortedMap<K, V> f() {
        e();
        if (this.c.isEmpty() && !(this.c instanceof TreeMap)) {
            this.c = new TreeMap();
        }
        return (SortedMap) this.c;
    }

    public void a() {
        if (this.d) {
            return;
        }
        this.c = this.c.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.c);
        this.d = true;
    }

    public final Map.Entry<K, V> b(int i) {
        return this.b.get(i);
    }

    public final int c() {
        return this.b.size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        e();
        if (!this.b.isEmpty()) {
            this.b.clear();
        }
        if (this.c.isEmpty()) {
            return;
        }
        this.c.clear();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        Comparable comparable = (Comparable) obj;
        return a((r<K, V>) comparable) >= 0 || this.c.containsKey(comparable);
    }

    public final Iterable<Map.Entry<K, V>> d() {
        return this.c.isEmpty() ? a.a() : this.c.entrySet();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.e == null) {
            this.e = new d(this, (byte) 0);
        }
        return this.e;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        Comparable comparable = (Comparable) obj;
        int a2 = a((r<K, V>) comparable);
        return a2 >= 0 ? (V) this.b.get(a2).getValue() : this.c.get(comparable);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public /* synthetic */ Object put(Object obj, Object obj2) {
        return a((r<K, V>) obj, (Comparable) obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        e();
        Comparable comparable = (Comparable) obj;
        int a2 = a((r<K, V>) comparable);
        if (a2 >= 0) {
            return (V) c(a2);
        }
        if (this.c.isEmpty()) {
            return null;
        }
        return this.c.remove(comparable);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.b.size() + this.c.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class b implements Comparable<r<K, V>.b>, Map.Entry<K, V> {
        final K b;
        private V c;

        b(K k, V v) {
            this.b = k;
            this.c = v;
        }

        b(r rVar, Map.Entry<K, V> entry) {
            this(entry.getKey(), entry.getValue());
        }

        private static boolean a(Object obj, Object obj2) {
            return obj == null ? obj2 == null : obj.equals(obj2);
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            return a(this.b, entry.getKey()) && a(this.c, entry.getValue());
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            return this.c;
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return (this.b == null ? 0 : this.b.hashCode()) ^ (this.c != null ? this.c.hashCode() : 0);
        }

        @Override // java.util.Map.Entry
        public final V setValue(V v) {
            r.this.e();
            V v2 = this.c;
            this.c = v;
            return v2;
        }

        public final String toString() {
            return this.b + "=" + this.c;
        }

        @Override // java.util.Map.Entry
        public final /* synthetic */ Object getKey() {
            return this.b;
        }

        @Override // java.lang.Comparable
        public final /* synthetic */ int compareTo(Object obj) {
            return this.b.compareTo(((b) obj).b);
        }
    }

    /* loaded from: classes.dex */
    private class c implements Iterator<Map.Entry<K, V>> {
        private int b;
        private boolean c;
        private Iterator<Map.Entry<K, V>> d;

        private c() {
            this.b = -1;
        }

        /* synthetic */ c(r rVar, byte b) {
            this();
        }

        private Iterator<Map.Entry<K, V>> b() {
            if (this.d == null) {
                this.d = r.this.c.entrySet().iterator();
            }
            return this.d;
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.b + 1 < r.this.b.size() || b().hasNext();
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (!this.c) {
                throw new IllegalStateException("remove() was called before next()");
            }
            this.c = false;
            r.this.e();
            if (this.b >= r.this.b.size()) {
                b().remove();
                return;
            }
            r rVar = r.this;
            int i = this.b;
            this.b = i - 1;
            rVar.c(i);
        }

        @Override // java.util.Iterator
        public final /* synthetic */ Object next() {
            this.c = true;
            int i = this.b + 1;
            this.b = i;
            return i < r.this.b.size() ? (Map.Entry) r.this.b.get(this.b) : b().next();
        }
    }

    /* loaded from: classes.dex */
    private class d extends AbstractSet<Map.Entry<K, V>> {
        private d() {
        }

        /* synthetic */ d(r rVar, byte b) {
            this();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final void clear() {
            r.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final boolean contains(Object obj) {
            Map.Entry entry = (Map.Entry) obj;
            Object obj2 = r.this.get(entry.getKey());
            Object value = entry.getValue();
            return obj2 == value || (obj2 != null && obj2.equals(value));
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new c(r.this, (byte) 0);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final boolean remove(Object obj) {
            Map.Entry entry = (Map.Entry) obj;
            if (!contains(entry)) {
                return false;
            }
            r.this.remove(entry.getKey());
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final int size() {
            return r.this.size();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final /* synthetic */ boolean add(Object obj) {
            Map.Entry entry = (Map.Entry) obj;
            if (contains(entry)) {
                return false;
            }
            r.this.a((r) entry.getKey(), (Comparable) entry.getValue());
            return true;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final V a(K k, V v) {
        e();
        int a2 = a((r<K, V>) k);
        if (a2 >= 0) {
            return (V) this.b.get(a2).setValue(v);
        }
        e();
        if (this.b.isEmpty() && !(this.b instanceof ArrayList)) {
            this.b = new ArrayList(this.a);
        }
        int i = -(a2 + 1);
        if (i >= this.a) {
            return f().put(k, v);
        }
        if (this.b.size() == this.a) {
            b remove = this.b.remove(this.a - 1);
            f().put(remove.b, remove.getValue());
        }
        this.b.add(i, new b(k, v));
        return null;
    }

    private int a(K k) {
        int size = this.b.size() - 1;
        if (size >= 0) {
            int compareTo = k.compareTo(this.b.get(size).b);
            if (compareTo > 0) {
                return -(size + 2);
            }
            if (compareTo == 0) {
                return size;
            }
        }
        int i = 0;
        int i2 = size;
        while (i <= i2) {
            int i3 = (i + i2) / 2;
            int compareTo2 = k.compareTo(this.b.get(i3).b);
            if (compareTo2 < 0) {
                i2 = i3 - 1;
            } else {
                if (compareTo2 <= 0) {
                    return i3;
                }
                i = i3 + 1;
            }
        }
        return -(i + 1);
    }
}
