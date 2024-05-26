package com.fasterxml.jackson.core.sym;

import com.fasterxml.jackson.core.util.InternCache;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class CharsToNameCanonicalizer {
    static final CharsToNameCanonicalizer sBootstrapSymbolTable = new CharsToNameCanonicalizer();
    protected Bucket[] _buckets;
    protected final boolean _canonicalize;
    protected boolean _dirty;
    public final int _hashSeed;
    protected int _indexMask;
    protected final boolean _intern;
    protected int _longestCollisionList;
    protected CharsToNameCanonicalizer _parent;
    protected int _size;
    protected int _sizeThreshold;
    protected String[] _symbols;

    public static CharsToNameCanonicalizer createRoot() {
        long now = System.currentTimeMillis();
        int seed = (((int) now) + ((int) (now >>> 32))) | 1;
        CharsToNameCanonicalizer charsToNameCanonicalizer = sBootstrapSymbolTable;
        return new CharsToNameCanonicalizer(null, true, true, charsToNameCanonicalizer._symbols, charsToNameCanonicalizer._buckets, charsToNameCanonicalizer._size, seed, charsToNameCanonicalizer._longestCollisionList);
    }

    private CharsToNameCanonicalizer() {
        this._canonicalize = true;
        this._intern = true;
        this._dirty = true;
        this._hashSeed = 0;
        this._longestCollisionList = 0;
        initTables$13462e();
    }

    private void initTables$13462e() {
        this._symbols = new String[64];
        this._buckets = new Bucket[32];
        this._indexMask = 63;
        this._size = 0;
        this._longestCollisionList = 0;
        this._sizeThreshold = 48;
    }

    private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, boolean canonicalize, boolean intern, String[] symbols, Bucket[] buckets, int size, int hashSeed, int longestColl) {
        this._parent = parent;
        this._canonicalize = canonicalize;
        this._intern = intern;
        this._symbols = symbols;
        this._buckets = buckets;
        this._size = size;
        this._hashSeed = hashSeed;
        int arrayLen = symbols.length;
        this._sizeThreshold = arrayLen - (arrayLen >> 2);
        this._indexMask = arrayLen - 1;
        this._longestCollisionList = longestColl;
        this._dirty = false;
    }

    public final CharsToNameCanonicalizer makeChild(boolean canonicalize, boolean intern) {
        String[] symbols;
        Bucket[] buckets;
        int size;
        int hashSeed;
        int longestCollisionList;
        synchronized (this) {
            symbols = this._symbols;
            buckets = this._buckets;
            size = this._size;
            hashSeed = this._hashSeed;
            longestCollisionList = this._longestCollisionList;
        }
        return new CharsToNameCanonicalizer(this, canonicalize, intern, symbols, buckets, size, hashSeed, longestCollisionList);
    }

    public final String findSymbol(char[] buffer, int start, int len, int h) {
        String sym;
        if (len <= 0) {
            return "";
        }
        if (!this._canonicalize) {
            return new String(buffer, start, len);
        }
        int index = _hashToIndex(h);
        String sym2 = this._symbols[index];
        if (sym2 != null) {
            if (sym2.length() == len) {
                int i = 0;
                while (sym2.charAt(i) == buffer[start + i] && (i = i + 1) < len) {
                }
                if (i == len) {
                    return sym2;
                }
            }
            Bucket b = this._buckets[index >> 1];
            if (b != null && (sym = b.find(buffer, start, len)) != null) {
                return sym;
            }
        }
        if (this._dirty) {
            if (this._size >= this._sizeThreshold) {
                rehash();
                index = _hashToIndex(calcHash$1ceb5030(buffer, len));
            }
        } else {
            String[] strArr = this._symbols;
            int length = strArr.length;
            this._symbols = new String[length];
            System.arraycopy(strArr, 0, this._symbols, 0, length);
            Bucket[] bucketArr = this._buckets;
            int length2 = bucketArr.length;
            this._buckets = new Bucket[length2];
            System.arraycopy(bucketArr, 0, this._buckets, 0, length2);
            this._dirty = true;
        }
        String newSymbol = new String(buffer, start, len);
        if (this._intern) {
            newSymbol = InternCache.instance.intern(newSymbol);
        }
        this._size++;
        if (this._symbols[index] == null) {
            this._symbols[index] = newSymbol;
        } else {
            int bix = index >> 1;
            Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
            this._buckets[bix] = newB;
            this._longestCollisionList = Math.max(newB._length, this._longestCollisionList);
            if (this._longestCollisionList > 255) {
                throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, 255 -- suspect a DoS attack based on hash collisions");
            }
        }
        return newSymbol;
    }

    private int _hashToIndex(int rawHash) {
        return ((rawHash >>> 15) + rawHash) & this._indexMask;
    }

    private int calcHash$1ceb5030(char[] buffer, int len) {
        int hash = this._hashSeed;
        for (int i = 0; i < len; i++) {
            hash = (hash * 33) + buffer[i];
        }
        if (hash == 0) {
            return 1;
        }
        return hash;
    }

    private int calcHash(String key) {
        int len = key.length();
        int hash = this._hashSeed;
        for (int i = 0; i < len; i++) {
            hash = (hash * 33) + key.charAt(i);
        }
        if (hash == 0) {
            return 1;
        }
        return hash;
    }

    private void rehash() {
        int size = this._symbols.length;
        int newSize = size + size;
        if (newSize > 65536) {
            this._size = 0;
            Arrays.fill(this._symbols, (Object) null);
            Arrays.fill(this._buckets, (Object) null);
            this._dirty = true;
            return;
        }
        String[] oldSyms = this._symbols;
        Bucket[] oldBuckets = this._buckets;
        this._symbols = new String[newSize];
        this._buckets = new Bucket[newSize >> 1];
        this._indexMask = newSize - 1;
        this._sizeThreshold = newSize - (newSize >> 2);
        int count = 0;
        int maxColl = 0;
        for (int i = 0; i < size; i++) {
            String symbol = oldSyms[i];
            if (symbol != null) {
                count++;
                int index = _hashToIndex(calcHash(symbol));
                if (this._symbols[index] == null) {
                    this._symbols[index] = symbol;
                } else {
                    int bix = index >> 1;
                    Bucket newB = new Bucket(symbol, this._buckets[bix]);
                    this._buckets[bix] = newB;
                    maxColl = Math.max(maxColl, newB._length);
                }
            }
        }
        int size2 = size >> 1;
        for (int i2 = 0; i2 < size2; i2++) {
            for (Bucket b = oldBuckets[i2]; b != null; b = b._next) {
                count++;
                String symbol2 = b._symbol;
                int index2 = _hashToIndex(calcHash(symbol2));
                if (this._symbols[index2] == null) {
                    this._symbols[index2] = symbol2;
                } else {
                    int bix2 = index2 >> 1;
                    Bucket newB2 = new Bucket(symbol2, this._buckets[bix2]);
                    this._buckets[bix2] = newB2;
                    maxColl = Math.max(maxColl, newB2._length);
                }
            }
        }
        this._longestCollisionList = maxColl;
        if (count != this._size) {
            throw new Error("Internal error on SymbolTable.rehash(): had " + this._size + " entries; now have " + count + ".");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Bucket {
        final int _length;
        final Bucket _next;
        final String _symbol;

        public Bucket(String symbol, Bucket next) {
            this._symbol = symbol;
            this._next = next;
            this._length = next == null ? 1 : next._length + 1;
        }

        public final String find(char[] buf, int start, int len) {
            String sym = this._symbol;
            Bucket b = this._next;
            while (true) {
                if (sym.length() == len) {
                    int i = 0;
                    while (sym.charAt(i) == buf[start + i] && (i = i + 1) < len) {
                    }
                    if (i == len) {
                        return sym;
                    }
                }
                if (b == null) {
                    return null;
                }
                sym = b._symbol;
                b = b._next;
            }
        }
    }

    public final void release() {
        if (this._dirty && this._parent != null) {
            CharsToNameCanonicalizer charsToNameCanonicalizer = this._parent;
            if (this._size > 12000 || this._longestCollisionList > 63) {
                synchronized (charsToNameCanonicalizer) {
                    charsToNameCanonicalizer.initTables$13462e();
                    charsToNameCanonicalizer._dirty = false;
                }
            } else if (this._size > charsToNameCanonicalizer._size) {
                synchronized (charsToNameCanonicalizer) {
                    charsToNameCanonicalizer._symbols = this._symbols;
                    charsToNameCanonicalizer._buckets = this._buckets;
                    charsToNameCanonicalizer._size = this._size;
                    charsToNameCanonicalizer._sizeThreshold = this._sizeThreshold;
                    charsToNameCanonicalizer._indexMask = this._indexMask;
                    charsToNameCanonicalizer._longestCollisionList = this._longestCollisionList;
                    charsToNameCanonicalizer._dirty = false;
                }
            }
            this._dirty = false;
        }
    }
}
