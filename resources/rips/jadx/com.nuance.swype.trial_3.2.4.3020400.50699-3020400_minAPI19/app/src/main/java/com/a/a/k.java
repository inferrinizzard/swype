package com.a.a;

import java.io.IOException;

/* loaded from: classes.dex */
final class k {
    private final n a;
    private final f b;
    private c c;
    volatile n d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void b() {
        if (this.d != null) {
            return;
        }
        synchronized (this) {
            if (this.d != null) {
                return;
            }
            try {
                if (this.c != null) {
                    this.d = this.a.getParserForType().parseFrom(this.c, this.b);
                }
            } catch (IOException e) {
            }
        }
    }

    public final boolean equals(Object obj) {
        b();
        return this.d.equals(obj);
    }

    public final int hashCode() {
        b();
        return this.d.hashCode();
    }

    public final String toString() {
        b();
        return this.d.toString();
    }
}
