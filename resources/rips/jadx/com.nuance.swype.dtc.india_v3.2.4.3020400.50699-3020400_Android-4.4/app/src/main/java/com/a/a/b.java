package com.a.a;

import com.a.a.a;
import com.a.a.n;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public abstract class b<MessageType extends n> implements p<MessageType> {
    private static final f EMPTY_REGISTRY = f.a();

    private v newUninitializedMessageException(MessageType messagetype) {
        return messagetype instanceof a ? ((a) messagetype).newUninitializedMessageException() : new v();
    }

    @Override // com.a.a.p
    public MessageType parseDelimitedFrom(InputStream inputStream) throws j {
        return parseDelimitedFrom(inputStream, EMPTY_REGISTRY);
    }

    @Override // com.a.a.p
    public MessageType parseDelimitedFrom(InputStream inputStream, f fVar) throws j {
        return checkMessageInitialized(m16parsePartialDelimitedFrom(inputStream, fVar));
    }

    @Override // com.a.a.p
    public MessageType parseFrom(c cVar) throws j {
        return parseFrom(cVar, EMPTY_REGISTRY);
    }

    @Override // com.a.a.p
    public MessageType parseFrom(c cVar, f fVar) throws j {
        return checkMessageInitialized(m18parsePartialFrom(cVar, fVar));
    }

    @Override // com.a.a.p
    public MessageType parseFrom(d dVar) throws j {
        return parseFrom(dVar, EMPTY_REGISTRY);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.a.a.p
    public MessageType parseFrom(d dVar, f fVar) throws j {
        return (MessageType) checkMessageInitialized((n) parsePartialFrom(dVar, fVar));
    }

    @Override // com.a.a.p
    public MessageType parseFrom(InputStream inputStream) throws j {
        return parseFrom(inputStream, EMPTY_REGISTRY);
    }

    @Override // com.a.a.p
    public MessageType parseFrom(InputStream inputStream, f fVar) throws j {
        return checkMessageInitialized(m21parsePartialFrom(inputStream, fVar));
    }

    @Override // com.a.a.p
    public MessageType parseFrom(byte[] bArr) throws j {
        return parseFrom(bArr, EMPTY_REGISTRY);
    }

    /* renamed from: parseFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m13parseFrom(byte[] bArr, int i, int i2) throws j {
        return m14parseFrom(bArr, i, i2, EMPTY_REGISTRY);
    }

    /* renamed from: parseFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m14parseFrom(byte[] bArr, int i, int i2, f fVar) throws j {
        return checkMessageInitialized(m24parsePartialFrom(bArr, i, i2, fVar));
    }

    @Override // com.a.a.p
    public MessageType parseFrom(byte[] bArr, f fVar) throws j {
        return m14parseFrom(bArr, 0, bArr.length, fVar);
    }

    /* renamed from: parsePartialDelimitedFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m15parsePartialDelimitedFrom(InputStream inputStream) throws j {
        return m16parsePartialDelimitedFrom(inputStream, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialDelimitedFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m16parsePartialDelimitedFrom(InputStream inputStream, f fVar) throws j {
        try {
            int read = inputStream.read();
            if (read == -1) {
                return null;
            }
            return m21parsePartialFrom((InputStream) new a.AbstractC0002a.C0003a(inputStream, d.a(read, inputStream)), fVar);
        } catch (IOException e) {
            throw new j(e.getMessage());
        }
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m17parsePartialFrom(c cVar) throws j {
        return m18parsePartialFrom(cVar, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m19parsePartialFrom(d dVar) throws j {
        return (MessageType) parsePartialFrom(dVar, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m20parsePartialFrom(InputStream inputStream) throws j {
        return m21parsePartialFrom(inputStream, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m22parsePartialFrom(byte[] bArr) throws j {
        return m24parsePartialFrom(bArr, 0, bArr.length, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m23parsePartialFrom(byte[] bArr, int i, int i2) throws j {
        return m24parsePartialFrom(bArr, i, i2, EMPTY_REGISTRY);
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m25parsePartialFrom(byte[] bArr, f fVar) throws j {
        return m24parsePartialFrom(bArr, 0, bArr.length, fVar);
    }

    private MessageType checkMessageInitialized(MessageType messagetype) throws j {
        if (messagetype == null || messagetype.isInitialized()) {
            return messagetype;
        }
        j jVar = new j(newUninitializedMessageException(messagetype).getMessage());
        jVar.a = messagetype;
        throw jVar;
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m18parsePartialFrom(c cVar, f fVar) throws j {
        try {
            try {
                d h = cVar.h();
                MessageType messagetype = (MessageType) parsePartialFrom(h, fVar);
                try {
                    h.a(0);
                    return messagetype;
                } catch (j e) {
                    e.a = messagetype;
                    throw e;
                }
            } catch (j e2) {
                throw e2;
            }
        } catch (IOException e3) {
            throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e3);
        }
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m24parsePartialFrom(byte[] bArr, int i, int i2, f fVar) throws j {
        try {
            try {
                d a = d.a(bArr, i, i2);
                MessageType messagetype = (MessageType) parsePartialFrom(a, fVar);
                try {
                    a.a(0);
                    return messagetype;
                } catch (j e) {
                    e.a = messagetype;
                    throw e;
                }
            } catch (j e2) {
                throw e2;
            }
        } catch (IOException e3) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e3);
        }
    }

    /* renamed from: parsePartialFrom, reason: merged with bridge method [inline-methods] */
    public MessageType m21parsePartialFrom(InputStream inputStream, f fVar) throws j {
        d a = d.a(inputStream);
        MessageType messagetype = (MessageType) parsePartialFrom(a, fVar);
        try {
            a.a(0);
            return messagetype;
        } catch (j e) {
            e.a = messagetype;
            throw e;
        }
    }
}
