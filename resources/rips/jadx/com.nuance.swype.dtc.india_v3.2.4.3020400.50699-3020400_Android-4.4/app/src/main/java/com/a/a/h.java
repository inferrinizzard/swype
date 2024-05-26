package com.a.a;

import com.a.a.a;
import com.a.a.g;
import com.a.a.i;
import com.a.a.x;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;

/* loaded from: classes.dex */
public abstract class h extends com.a.a.a implements Serializable {
    private static final long serialVersionUID = 1;

    /* loaded from: classes.dex */
    public static abstract class a<MessageType extends h, BuilderType extends a> extends a.AbstractC0002a<BuilderType> {
        @Override // 
        /* renamed from: clear, reason: merged with bridge method [inline-methods] */
        public BuilderType mo26clear() {
            return this;
        }

        @Override // com.a.a.a.AbstractC0002a
        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public BuilderType mo3clone() {
            throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
        }

        @Override // 
        /* renamed from: getDefaultInstanceForType, reason: merged with bridge method [inline-methods] */
        public abstract MessageType mo27getDefaultInstanceForType();

        public abstract BuilderType mergeFrom(MessageType messagetype);

        protected boolean parseUnknownField(com.a.a.d dVar, f fVar, int i) throws IOException {
            return dVar.b(i);
        }
    }

    /* loaded from: classes.dex */
    static final class d implements Serializable {
        private String a;
        private byte[] b;

        d(n nVar) {
            this.a = nVar.getClass().getName();
            this.b = nVar.toByteArray();
        }
    }

    public h() {
    }

    public h(a aVar) {
    }

    public static <ContainingType extends n, Type> c<ContainingType, Type> newRepeatedGeneratedExtension(ContainingType containingtype, n nVar, i.b<?> bVar, int i, x.a aVar, boolean z) {
        byte b2 = 0;
        return new c<>(containingtype, Collections.emptyList(), nVar, new b(bVar, i, aVar, true, z, b2), b2);
    }

    public static <ContainingType extends n, Type> c<ContainingType, Type> newSingularGeneratedExtension(ContainingType containingtype, Type type, n nVar, i.b<?> bVar, int i, x.a aVar) {
        return new c<>(containingtype, type, nVar, new b(bVar, i, aVar, false, false, (byte) 0), (byte) 0);
    }

    @Override // com.a.a.n
    public p<? extends n> getParserForType() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }

    public void makeExtensionsImmutable() {
    }

    public boolean parseUnknownField(com.a.a.d dVar, f fVar, int i) throws IOException {
        return dVar.b(i);
    }

    public Object writeReplace() throws ObjectStreamException {
        return new d(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class b implements g.a<b> {
        final i.b<?> a;
        final int b;
        final x.a c;
        final boolean d;
        private final boolean e;

        private b(i.b<?> bVar, int i, x.a aVar, boolean z, boolean z2) {
            this.a = bVar;
            this.b = i;
            this.c = aVar;
            this.d = z;
            this.e = z2;
        }

        /* synthetic */ b(i.b bVar, int i, x.a aVar, boolean z, boolean z2, byte b) {
            this(bVar, i, aVar, z, z2);
        }

        @Override // com.a.a.g.a
        public final x.a a() {
            return this.c;
        }

        @Override // com.a.a.g.a
        public final boolean b() {
            return this.d;
        }

        @Override // java.lang.Comparable
        public final /* synthetic */ int compareTo(Object obj) {
            return this.b - ((b) obj).b;
        }
    }

    /* loaded from: classes.dex */
    public static final class c<ContainingType extends n, Type> {
        private final ContainingType a;
        private final Type b;
        final n c;
        final b d;

        /* synthetic */ c(n nVar, Object obj, n nVar2, b bVar, byte b) {
            this(nVar, obj, nVar2, bVar);
        }

        private c(ContainingType containingtype, Type type, n nVar, b bVar) {
            if (containingtype == null) {
                throw new IllegalArgumentException("Null containingTypeDefaultInstance");
            }
            if (bVar.c == x.a.k && nVar == null) {
                throw new IllegalArgumentException("Null messageDefaultInstance");
            }
            this.a = containingtype;
            this.b = type;
            this.c = nVar;
            this.d = bVar;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:10:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0027  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static <MessageType extends com.a.a.n> boolean parseUnknownField(com.a.a.g<com.a.a.h.b> r7, MessageType r8, com.a.a.d r9, com.a.a.f r10, int r11) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 342
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.a.a.h.parseUnknownField(com.a.a.g, com.a.a.n, com.a.a.d, com.a.a.f, int):boolean");
    }
}
