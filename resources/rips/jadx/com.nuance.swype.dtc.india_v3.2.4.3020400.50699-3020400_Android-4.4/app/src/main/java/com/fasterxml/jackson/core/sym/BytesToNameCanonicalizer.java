package com.fasterxml.jackson.core.sym;

import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class BytesToNameCanonicalizer {
    protected int _collCount;
    protected int _collEnd;
    protected Bucket[] _collList;
    private boolean _collListShared;
    protected int _count;
    private final int _hashSeed;
    protected final boolean _intern;
    protected int _longestCollisionList;
    protected int[] _mainHash;
    protected int _mainHashMask;
    private boolean _mainHashShared;
    protected Name[] _mainNames;
    private boolean _mainNamesShared;
    private transient boolean _needRehash;
    protected final BytesToNameCanonicalizer _parent;
    protected final AtomicReference<TableInfo> _tableInfo;

    public BytesToNameCanonicalizer(int seed) {
        this._parent = null;
        this._hashSeed = seed;
        this._intern = true;
        this._tableInfo = new AtomicReference<>(initTableInfo$267dee87());
    }

    private BytesToNameCanonicalizer(BytesToNameCanonicalizer parent, boolean intern, int seed, TableInfo state) {
        this._parent = parent;
        this._hashSeed = seed;
        this._intern = intern;
        this._tableInfo = null;
        this._count = state.count;
        this._mainHashMask = state.mainHashMask;
        this._mainHash = state.mainHash;
        this._mainNames = state.mainNames;
        this._collList = state.collList;
        this._collCount = state.collCount;
        this._collEnd = state.collEnd;
        this._longestCollisionList = state.longestCollisionList;
        this._needRehash = false;
        this._mainHashShared = true;
        this._mainNamesShared = true;
        this._collListShared = true;
    }

    private static TableInfo initTableInfo$267dee87() {
        return new TableInfo(63, new int[64], new Name[64]);
    }

    public final BytesToNameCanonicalizer makeChild$ed8baa8(boolean intern) {
        return new BytesToNameCanonicalizer(this, intern, this._hashSeed, this._tableInfo.get());
    }

    public final void release() {
        if (this._parent != null) {
            if (!this._mainHashShared) {
                BytesToNameCanonicalizer bytesToNameCanonicalizer = this._parent;
                TableInfo tableInfo = new TableInfo(this);
                int i = tableInfo.count;
                TableInfo tableInfo2 = bytesToNameCanonicalizer._tableInfo.get();
                if (i > tableInfo2.count) {
                    if (i > 6000 || tableInfo.longestCollisionList > 63) {
                        tableInfo = initTableInfo$267dee87();
                    }
                    bytesToNameCanonicalizer._tableInfo.compareAndSet(tableInfo2, tableInfo);
                }
                this._mainHashShared = true;
                this._mainNamesShared = true;
                this._collListShared = true;
            }
        }
    }

    public final Name findName(int firstQuad) {
        int hash = calcHash(firstQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & 255;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, 0);
            }
        }
        return null;
    }

    public final Name findName(int firstQuad, int secondQuad) {
        int hash = secondQuad == 0 ? calcHash(firstQuad) : calcHash(firstQuad, secondQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad, secondQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & 255;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, secondQuad);
            }
        }
        return null;
    }

    public final Name findName(int[] quads, int qlen) {
        if (qlen < 3) {
            return findName(quads[0], qlen >= 2 ? quads[1] : 0);
        }
        int hash = calcHash(quads, qlen);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null || name.equals(quads, qlen)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & 255;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, quads, qlen);
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00dd  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0117  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0288  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.fasterxml.jackson.core.sym.Name addName(java.lang.String r19, int[] r20, int r21) {
        /*
            Method dump skipped, instructions count: 858
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer.addName(java.lang.String, int[], int):com.fasterxml.jackson.core.sym.Name");
    }

    private int calcHash(int firstQuad) {
        int i = this._hashSeed ^ firstQuad;
        int i2 = i + (i >>> 15);
        return i2 ^ (i2 >>> 9);
    }

    private int calcHash(int firstQuad, int secondQuad) {
        int i = (((firstQuad >>> 15) ^ firstQuad) + (secondQuad * 33)) ^ this._hashSeed;
        return i + (i >>> 7);
    }

    private int calcHash(int[] quads, int qlen) {
        if (qlen < 3) {
            throw new IllegalArgumentException();
        }
        int i = quads[0] ^ this._hashSeed;
        int i2 = (((i + (i >>> 9)) * 33) + quads[1]) * 65599;
        int i3 = (i2 + (i2 >>> 15)) ^ quads[2];
        int hash = i3 + (i3 >>> 17);
        for (int i4 = 3; i4 < qlen; i4++) {
            int i5 = (hash * 31) ^ quads[i4];
            int i6 = i5 + (i5 >>> 3);
            hash = i6 ^ (i6 << 7);
        }
        int i7 = (hash >>> 15) + hash;
        return i7 ^ (i7 << 9);
    }

    private int findBestBucket() {
        Bucket[] buckets = this._collList;
        int bestCount = Integer.MAX_VALUE;
        int bestIx = -1;
        int len = this._collEnd;
        for (int i = 0; i < len; i++) {
            int count = buckets[i]._length;
            if (count < bestCount) {
                if (count != 1) {
                    bestCount = count;
                    bestIx = i;
                } else {
                    return i;
                }
            }
        }
        int i2 = bestIx;
        return i2;
    }

    private void expandCollision() {
        Bucket[] old = this._collList;
        int len = old.length;
        this._collList = new Bucket[len + len];
        System.arraycopy(old, 0, this._collList, 0, len);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class TableInfo {
        public final int collCount;
        public final int collEnd;
        public final Bucket[] collList;
        public final int count;
        public final int longestCollisionList;
        public final int[] mainHash;
        public final int mainHashMask;
        public final Name[] mainNames;

        public TableInfo(int mainHashMask, int[] mainHash, Name[] mainNames) {
            this.count = 0;
            this.mainHashMask = 63;
            this.mainHash = mainHash;
            this.mainNames = mainNames;
            this.collList = null;
            this.collCount = 0;
            this.collEnd = 0;
            this.longestCollisionList = 0;
        }

        public TableInfo(BytesToNameCanonicalizer src) {
            this.count = src._count;
            this.mainHashMask = src._mainHashMask;
            this.mainHash = src._mainHash;
            this.mainNames = src._mainNames;
            this.collList = src._collList;
            this.collCount = src._collCount;
            this.collEnd = src._collEnd;
            this.longestCollisionList = src._longestCollisionList;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Bucket {
        final int _length;
        protected final Name _name;
        protected final Bucket _next;

        Bucket(Name name, Bucket next) {
            this._name = name;
            this._next = next;
            this._length = next == null ? 1 : next._length + 1;
        }

        public final Name find(int hash, int firstQuad, int secondQuad) {
            if (this._name.hashCode() == hash && this._name.equals(firstQuad, secondQuad)) {
                return this._name;
            }
            for (Bucket curr = this._next; curr != null; curr = curr._next) {
                Name currName = curr._name;
                if (currName.hashCode() == hash && currName.equals(firstQuad, secondQuad)) {
                    return currName;
                }
            }
            return null;
        }

        public final Name find(int hash, int[] quads, int qlen) {
            if (this._name.hashCode() == hash && this._name.equals(quads, qlen)) {
                return this._name;
            }
            for (Bucket curr = this._next; curr != null; curr = curr._next) {
                Name currName = curr._name;
                if (currName.hashCode() == hash && currName.equals(quads, qlen)) {
                    return currName;
                }
            }
            return null;
        }
    }
}
