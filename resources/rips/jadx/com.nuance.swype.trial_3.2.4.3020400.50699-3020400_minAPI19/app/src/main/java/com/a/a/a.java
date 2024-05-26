package com.a.a;

import com.a.a.c;
import com.a.a.n;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class a implements n {

    /* renamed from: com.a.a.a$a, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public static abstract class AbstractC0002a<BuilderType extends AbstractC0002a> implements n.a {

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.a.a.a$a$a, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        public static final class C0003a extends FilterInputStream {
            private int a;

            /* JADX INFO: Access modifiers changed from: package-private */
            public C0003a(InputStream inputStream, int i) {
                super(inputStream);
                this.a = i;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public final int available() throws IOException {
                return Math.min(super.available(), this.a);
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public final int read() throws IOException {
                if (this.a <= 0) {
                    return -1;
                }
                int read = super.read();
                if (read < 0) {
                    return read;
                }
                this.a--;
                return read;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public final int read(byte[] bArr, int i, int i2) throws IOException {
                if (this.a <= 0) {
                    return -1;
                }
                int read = super.read(bArr, i, Math.min(i2, this.a));
                if (read < 0) {
                    return read;
                }
                this.a -= read;
                return read;
            }

            @Override // java.io.FilterInputStream, java.io.InputStream
            public final long skip(long j) throws IOException {
                long skip = super.skip(Math.min(j, this.a));
                if (skip >= 0) {
                    this.a = (int) (this.a - skip);
                }
                return skip;
            }
        }

        public static <T> void addAll(Iterable<T> iterable, Collection<? super T> collection) {
            if (iterable instanceof l) {
                checkForNullValues(((l) iterable).a());
            } else {
                checkForNullValues(iterable);
            }
            if (iterable instanceof Collection) {
                collection.addAll((Collection) iterable);
                return;
            }
            Iterator<T> it = iterable.iterator();
            while (it.hasNext()) {
                collection.add(it.next());
            }
        }

        private static void checkForNullValues(Iterable<?> iterable) {
            Iterator<?> it = iterable.iterator();
            while (it.hasNext()) {
                if (it.next() == null) {
                    throw new NullPointerException();
                }
            }
        }

        public static v newUninitializedMessageException(n nVar) {
            return new v();
        }

        @Override // 
        /* renamed from: clone, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
        public abstract BuilderType mo3clone();

        public boolean mergeDelimitedFrom(InputStream inputStream) throws IOException {
            return mergeDelimitedFrom(inputStream, f.a());
        }

        public boolean mergeDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            int read = inputStream.read();
            if (read == -1) {
                return false;
            }
            m8mergeFrom((InputStream) new C0003a(inputStream, d.a(read, inputStream)), fVar);
            return true;
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m4mergeFrom(c cVar) throws j {
            try {
                d h = cVar.h();
                m6mergeFrom(h);
                h.a(0);
                return this;
            } catch (j e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e2);
            }
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m5mergeFrom(c cVar, f fVar) throws j {
            try {
                d h = cVar.h();
                mergeFrom(h, fVar);
                h.a(0);
                return this;
            } catch (j e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e2);
            }
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m6mergeFrom(d dVar) throws IOException {
            return mergeFrom(dVar, f.a());
        }

        @Override // com.a.a.n.a
        public abstract BuilderType mergeFrom(d dVar, f fVar) throws IOException;

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m7mergeFrom(InputStream inputStream) throws IOException {
            d a = d.a(inputStream);
            m6mergeFrom(a);
            a.a(0);
            return this;
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m8mergeFrom(InputStream inputStream, f fVar) throws IOException {
            d a = d.a(inputStream);
            mergeFrom(a, fVar);
            a.a(0);
            return this;
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m9mergeFrom(byte[] bArr) throws j {
            return m10mergeFrom(bArr, 0, bArr.length);
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m10mergeFrom(byte[] bArr, int i, int i2) throws j {
            try {
                d a = d.a(bArr, i, i2);
                m6mergeFrom(a);
                a.a(0);
                return this;
            } catch (j e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m11mergeFrom(byte[] bArr, int i, int i2, f fVar) throws j {
            try {
                d a = d.a(bArr, i, i2);
                mergeFrom(a, fVar);
                a.a(0);
                return this;
            } catch (j e) {
                throw e;
            } catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }

        /* renamed from: mergeFrom, reason: merged with bridge method [inline-methods] */
        public BuilderType m12mergeFrom(byte[] bArr, f fVar) throws j {
            return m11mergeFrom(bArr, 0, bArr.length, fVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public v newUninitializedMessageException() {
        return new v();
    }

    @Override // com.a.a.n
    public byte[] toByteArray() {
        try {
            byte[] bArr = new byte[getSerializedSize()];
            e a = e.a(bArr);
            writeTo(a);
            a.c();
            return bArr;
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public void writeDelimitedTo(OutputStream outputStream) throws IOException {
        int serializedSize = getSerializedSize();
        e a = e.a(outputStream, e.a(e.g(serializedSize) + serializedSize));
        a.f(serializedSize);
        writeTo(a);
        a.a();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        e a = e.a(outputStream, e.a(getSerializedSize()));
        writeTo(a);
        a.a();
    }

    public c toByteString() {
        try {
            c.b a = c.a(getSerializedSize());
            writeTo(a.a);
            a.a.c();
            return new m(a.b);
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", e);
        }
    }
}
