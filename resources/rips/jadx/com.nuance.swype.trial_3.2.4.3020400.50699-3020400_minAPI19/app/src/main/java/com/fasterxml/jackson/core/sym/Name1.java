package com.fasterxml.jackson.core.sym;

/* loaded from: classes.dex */
public final class Name1 extends Name {
    static final Name1 sEmptyName = new Name1("", 0, 0);
    final int mQuad;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Name1(String name, int hash, int quad) {
        super(name, hash);
        this.mQuad = quad;
    }

    public static Name1 getEmptyName() {
        return sEmptyName;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public final boolean equals(int quad) {
        return quad == this.mQuad;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public final boolean equals(int quad1, int quad2) {
        return quad1 == this.mQuad && quad2 == 0;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public final boolean equals(int[] quads, int qlen) {
        return qlen == 1 && quads[0] == this.mQuad;
    }
}
