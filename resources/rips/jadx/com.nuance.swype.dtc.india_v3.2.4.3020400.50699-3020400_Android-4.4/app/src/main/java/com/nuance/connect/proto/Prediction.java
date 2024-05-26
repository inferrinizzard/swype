package com.nuance.connect.proto;

import com.a.a.b;
import com.a.a.c;
import com.a.a.d;
import com.a.a.e;
import com.a.a.f;
import com.a.a.h;
import com.a.a.j;
import com.a.a.o;
import com.a.a.p;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class Prediction {

    /* loaded from: classes.dex */
    public interface LoggingRequestV1OrBuilder extends o {
        String getDeviceID();

        c getDeviceIDBytes();

        LoggingTransactionV1 getTransactions(int i);

        int getTransactionsCount();

        List<LoggingTransactionV1> getTransactionsList();

        boolean hasDeviceID();
    }

    /* loaded from: classes.dex */
    public interface LoggingTransactionV1OrBuilder extends o {
        String getApplicationName();

        c getApplicationNameBytes();

        String getCcpsVersion();

        c getCcpsVersionBytes();

        int getCloudTime();

        String getCountryCode();

        c getCountryCodeBytes();

        PredictionResultV1 getPredictionResult();

        int getResultCode();

        int getTotalTime();

        String getTransactionID();

        c getTransactionIDBytes();

        boolean hasApplicationName();

        boolean hasCcpsVersion();

        boolean hasCloudTime();

        boolean hasCountryCode();

        boolean hasPredictionResult();

        boolean hasResultCode();

        boolean hasTotalTime();

        boolean hasTransactionID();
    }

    /* loaded from: classes.dex */
    public interface PredictionRequestV1OrBuilder extends o {
        String getDeviceID();

        c getDeviceIDBytes();

        int getNumPredictionsRequired();

        c getPredictionData();

        long getRequestTimestamp();

        String getTransactionID();

        c getTransactionIDBytes();

        String getXt9Version();

        c getXt9VersionBytes();

        boolean hasDeviceID();

        boolean hasNumPredictionsRequired();

        boolean hasPredictionData();

        boolean hasRequestTimestamp();

        boolean hasTransactionID();

        boolean hasXt9Version();
    }

    /* loaded from: classes.dex */
    public interface PredictionResponseV1OrBuilder extends o {
        int getCoreTime();

        PredictionResultV1 getPredictionResult(int i);

        int getPredictionResultCount();

        List<PredictionResultV1> getPredictionResultList();

        int getTimeSpent();

        String getTransactionID();

        c getTransactionIDBytes();

        boolean hasCoreTime();

        boolean hasTimeSpent();

        boolean hasTransactionID();
    }

    /* loaded from: classes.dex */
    public interface PredictionResultV1OrBuilder extends o {
        int getAttribute(int i);

        int getAttributeCount();

        List<Integer> getAttributeList();

        int getFullSpell(int i);

        int getFullSpellCount();

        List<Integer> getFullSpellList();

        int getPhrase(int i);

        int getPhraseCount();

        List<Integer> getPhraseList();

        int getSpell(int i);

        int getSpellCount();

        List<Integer> getSpellList();
    }

    private Prediction() {
    }

    public static void registerAllExtensions(f fVar) {
    }

    /* loaded from: classes.dex */
    public static final class LoggingRequestV1 extends h implements LoggingRequestV1OrBuilder {
        public static final int DEVICEID_FIELD_NUMBER = 1;
        public static p<LoggingRequestV1> PARSER = new b<LoggingRequestV1>() { // from class: com.nuance.connect.proto.Prediction.LoggingRequestV1.1
            @Override // com.a.a.p
            public final LoggingRequestV1 parsePartialFrom(d dVar, f fVar) throws j {
                return new LoggingRequestV1(dVar, fVar);
            }
        };
        public static final int TRANSACTIONS_FIELD_NUMBER = 2;
        private static final LoggingRequestV1 defaultInstance;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private Object deviceID_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private List<LoggingTransactionV1> transactions_;

        static {
            LoggingRequestV1 loggingRequestV1 = new LoggingRequestV1(true);
            defaultInstance = loggingRequestV1;
            loggingRequestV1.initFields();
        }

        private LoggingRequestV1(h.a aVar) {
            super(aVar);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private LoggingRequestV1(boolean z) {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        public static LoggingRequestV1 getDefaultInstance() {
            return defaultInstance;
        }

        private void initFields() {
            this.deviceID_ = "";
            this.transactions_ = Collections.emptyList();
        }

        public static Builder newBuilder() {
            return Builder.access$3800();
        }

        public static Builder newBuilder(LoggingRequestV1 loggingRequestV1) {
            return newBuilder().mergeFrom(loggingRequestV1);
        }

        public static LoggingRequestV1 parseDelimitedFrom(InputStream inputStream) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream);
        }

        public static LoggingRequestV1 parseDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream, fVar);
        }

        public static LoggingRequestV1 parseFrom(c cVar) throws j {
            return PARSER.parseFrom(cVar);
        }

        public static LoggingRequestV1 parseFrom(c cVar, f fVar) throws j {
            return PARSER.parseFrom(cVar, fVar);
        }

        public static LoggingRequestV1 parseFrom(d dVar) throws IOException {
            return PARSER.parseFrom(dVar);
        }

        public static LoggingRequestV1 parseFrom(d dVar, f fVar) throws IOException {
            return PARSER.parseFrom(dVar, fVar);
        }

        public static LoggingRequestV1 parseFrom(InputStream inputStream) throws IOException {
            return PARSER.parseFrom(inputStream);
        }

        public static LoggingRequestV1 parseFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseFrom(inputStream, fVar);
        }

        public static LoggingRequestV1 parseFrom(byte[] bArr) throws j {
            return PARSER.parseFrom(bArr);
        }

        public static LoggingRequestV1 parseFrom(byte[] bArr, f fVar) throws j {
            return PARSER.parseFrom(bArr, fVar);
        }

        public final LoggingRequestV1 getDefaultInstanceForType() {
            return defaultInstance;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final String getDeviceID() {
            Object obj = this.deviceID_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.deviceID_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final c getDeviceIDBytes() {
            Object obj = this.deviceID_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.deviceID_ = a;
            return a;
        }

        @Override // com.a.a.h, com.a.a.n
        public final p<LoggingRequestV1> getParserForType() {
            return PARSER;
        }

        @Override // com.a.a.n
        public final int getSerializedSize() {
            int i = 0;
            int i2 = this.memoizedSerializedSize;
            if (i2 == -1) {
                int b = (this.bitField0_ & 1) == 1 ? e.b(1, getDeviceIDBytes()) + 0 : 0;
                while (true) {
                    i2 = b;
                    if (i >= this.transactions_.size()) {
                        break;
                    }
                    b = e.b$3eface7e(this.transactions_.get(i)) + i2;
                    i++;
                }
                this.memoizedSerializedSize = i2;
            }
            return i2;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final LoggingTransactionV1 getTransactions(int i) {
            return this.transactions_.get(i);
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final int getTransactionsCount() {
            return this.transactions_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final List<LoggingTransactionV1> getTransactionsList() {
            return this.transactions_;
        }

        public final LoggingTransactionV1OrBuilder getTransactionsOrBuilder(int i) {
            return this.transactions_.get(i);
        }

        public final List<? extends LoggingTransactionV1OrBuilder> getTransactionsOrBuilderList() {
            return this.transactions_;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
        public final boolean hasDeviceID() {
            return (this.bitField0_ & 1) == 1;
        }

        @Override // com.a.a.o
        public final boolean isInitialized() {
            byte b = this.memoizedIsInitialized;
            if (b != -1) {
                return b == 1;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.a.a.n
        public final Builder newBuilderForType() {
            return newBuilder();
        }

        @Override // com.a.a.n
        public final Builder toBuilder() {
            return newBuilder(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.a.a.h
        public final Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        @Override // com.a.a.n
        public final void writeTo(e eVar) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                eVar.a(1, getDeviceIDBytes());
            }
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.transactions_.size()) {
                    return;
                }
                eVar.a$3eface71(this.transactions_.get(i2));
                i = i2 + 1;
            }
        }

        /* loaded from: classes.dex */
        public static final class Builder extends h.a<LoggingRequestV1, Builder> implements LoggingRequestV1OrBuilder {
            private int bitField0_;
            private Object deviceID_ = "";
            private List<LoggingTransactionV1> transactions_ = Collections.emptyList();

            private Builder() {
                maybeForceBuilderInitialization();
            }

            static /* synthetic */ Builder access$3800() {
                return create();
            }

            private static Builder create() {
                return new Builder();
            }

            private void ensureTransactionsIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.transactions_ = new ArrayList(this.transactions_);
                    this.bitField0_ |= 2;
                }
            }

            private void maybeForceBuilderInitialization() {
            }

            public final Builder addAllTransactions(Iterable<? extends LoggingTransactionV1> iterable) {
                ensureTransactionsIsMutable();
                h.a.addAll(iterable, this.transactions_);
                return this;
            }

            public final Builder addTransactions(int i, LoggingTransactionV1.Builder builder) {
                ensureTransactionsIsMutable();
                this.transactions_.add(i, builder.build());
                return this;
            }

            public final Builder addTransactions(int i, LoggingTransactionV1 loggingTransactionV1) {
                if (loggingTransactionV1 == null) {
                    throw new NullPointerException();
                }
                ensureTransactionsIsMutable();
                this.transactions_.add(i, loggingTransactionV1);
                return this;
            }

            public final Builder addTransactions(LoggingTransactionV1.Builder builder) {
                ensureTransactionsIsMutable();
                this.transactions_.add(builder.build());
                return this;
            }

            public final Builder addTransactions(LoggingTransactionV1 loggingTransactionV1) {
                if (loggingTransactionV1 == null) {
                    throw new NullPointerException();
                }
                ensureTransactionsIsMutable();
                this.transactions_.add(loggingTransactionV1);
                return this;
            }

            @Override // com.a.a.n.a
            public final LoggingRequestV1 build() {
                LoggingRequestV1 buildPartial = buildPartial();
                if (buildPartial.isInitialized()) {
                    return buildPartial;
                }
                throw newUninitializedMessageException(buildPartial);
            }

            public final LoggingRequestV1 buildPartial() {
                LoggingRequestV1 loggingRequestV1 = new LoggingRequestV1(this);
                int i = (this.bitField0_ & 1) != 1 ? 0 : 1;
                loggingRequestV1.deviceID_ = this.deviceID_;
                if ((this.bitField0_ & 2) == 2) {
                    this.transactions_ = Collections.unmodifiableList(this.transactions_);
                    this.bitField0_ &= -3;
                }
                loggingRequestV1.transactions_ = this.transactions_;
                loggingRequestV1.bitField0_ = i;
                return loggingRequestV1;
            }

            @Override // com.a.a.h.a
            /* renamed from: clear */
            public final Builder mo26clear() {
                super.mo26clear();
                this.deviceID_ = "";
                this.bitField0_ &= -2;
                this.transactions_ = Collections.emptyList();
                this.bitField0_ &= -3;
                return this;
            }

            public final Builder clearDeviceID() {
                this.bitField0_ &= -2;
                this.deviceID_ = LoggingRequestV1.getDefaultInstance().getDeviceID();
                return this;
            }

            public final Builder clearTransactions() {
                this.transactions_ = Collections.emptyList();
                this.bitField0_ &= -3;
                return this;
            }

            @Override // com.a.a.h.a, com.a.a.a.AbstractC0002a
            /* renamed from: clone */
            public final Builder mo3clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override // com.a.a.h.a
            /* renamed from: getDefaultInstanceForType */
            public final LoggingRequestV1 mo27getDefaultInstanceForType() {
                return LoggingRequestV1.getDefaultInstance();
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final String getDeviceID() {
                Object obj = this.deviceID_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.deviceID_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final c getDeviceIDBytes() {
                Object obj = this.deviceID_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.deviceID_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final LoggingTransactionV1 getTransactions(int i) {
                return this.transactions_.get(i);
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final int getTransactionsCount() {
                return this.transactions_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final List<LoggingTransactionV1> getTransactionsList() {
                return Collections.unmodifiableList(this.transactions_);
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingRequestV1OrBuilder
            public final boolean hasDeviceID() {
                return (this.bitField0_ & 1) == 1;
            }

            @Override // com.a.a.o
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.a.a.h.a
            public final Builder mergeFrom(LoggingRequestV1 loggingRequestV1) {
                if (loggingRequestV1 != LoggingRequestV1.getDefaultInstance()) {
                    if (loggingRequestV1.hasDeviceID()) {
                        this.bitField0_ |= 1;
                        this.deviceID_ = loggingRequestV1.deviceID_;
                    }
                    if (!loggingRequestV1.transactions_.isEmpty()) {
                        if (this.transactions_.isEmpty()) {
                            this.transactions_ = loggingRequestV1.transactions_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureTransactionsIsMutable();
                            this.transactions_.addAll(loggingRequestV1.transactions_);
                        }
                    }
                }
                return this;
            }

            public final Builder removeTransactions(int i) {
                ensureTransactionsIsMutable();
                this.transactions_.remove(i);
                return this;
            }

            public final Builder setDeviceID(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.deviceID_ = str;
                return this;
            }

            public final Builder setDeviceIDBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.deviceID_ = cVar;
                return this;
            }

            public final Builder setTransactions(int i, LoggingTransactionV1.Builder builder) {
                ensureTransactionsIsMutable();
                this.transactions_.set(i, builder.build());
                return this;
            }

            public final Builder setTransactions(int i, LoggingTransactionV1 loggingTransactionV1) {
                if (loggingTransactionV1 == null) {
                    throw new NullPointerException();
                }
                ensureTransactionsIsMutable();
                this.transactions_.set(i, loggingTransactionV1);
                return this;
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x001c  */
            /* JADX WARN: Removed duplicated region for block: B:14:? A[SYNTHETIC] */
            @Override // com.a.a.a.AbstractC0002a, com.a.a.n.a
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final com.nuance.connect.proto.Prediction.LoggingRequestV1.Builder mergeFrom(com.a.a.d r5, com.a.a.f r6) throws java.io.IOException {
                /*
                    r4 = this;
                    r2 = 0
                    com.a.a.p<com.nuance.connect.proto.Prediction$LoggingRequestV1> r0 = com.nuance.connect.proto.Prediction.LoggingRequestV1.PARSER     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    java.lang.Object r0 = r0.parsePartialFrom(r5, r6)     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$LoggingRequestV1 r0 = (com.nuance.connect.proto.Prediction.LoggingRequestV1) r0     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    if (r0 == 0) goto Le
                    r4.mergeFrom(r0)
                Le:
                    return r4
                Lf:
                    r0 = move-exception
                    r1 = r0
                    com.a.a.n r0 = r1.a     // Catch: java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$LoggingRequestV1 r0 = (com.nuance.connect.proto.Prediction.LoggingRequestV1) r0     // Catch: java.lang.Throwable -> L20
                    throw r1     // Catch: java.lang.Throwable -> L16
                L16:
                    r1 = move-exception
                    r3 = r1
                    r1 = r0
                    r0 = r3
                L1a:
                    if (r1 == 0) goto L1f
                    r4.mergeFrom(r1)
                L1f:
                    throw r0
                L20:
                    r0 = move-exception
                    r1 = r2
                    goto L1a
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.LoggingRequestV1.Builder.mergeFrom(com.a.a.d, com.a.a.f):com.nuance.connect.proto.Prediction$LoggingRequestV1$Builder");
            }
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0015. Please report as an issue. */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:30:0x003b  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private LoggingRequestV1(com.a.a.d r8, com.a.a.f r9) throws com.a.a.j {
            /*
                r7 = this;
                r2 = 1
                r0 = 0
                r1 = -1
                r5 = 2
                r7.<init>()
                r7.memoizedIsInitialized = r1
                r7.memoizedSerializedSize = r1
                r7.initFields()
                r1 = r0
            Lf:
                if (r1 != 0) goto L70
                int r3 = r8.a()     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                switch(r3) {
                    case 0: goto L20;
                    case 10: goto L22;
                    case 18: goto L47;
                    default: goto L18;
                }     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
            L18:
                boolean r3 = r7.parseUnknownField(r8, r9, r3)     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                if (r3 != 0) goto Lf
                r1 = r2
                goto Lf
            L20:
                r1 = r2
                goto Lf
            L22:
                int r3 = r7.bitField0_     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r3 = r3 | 1
                r7.bitField0_ = r3     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                com.a.a.c r3 = r8.l()     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r7.deviceID_ = r3     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                goto Lf
            L2f:
                r1 = move-exception
                r6 = r1
                r1 = r0
                r0 = r6
                r0.a = r7     // Catch: java.lang.Throwable -> L36
                throw r0     // Catch: java.lang.Throwable -> L36
            L36:
                r0 = move-exception
            L37:
                r1 = r1 & 2
                if (r1 != r5) goto L43
                java.util.List<com.nuance.connect.proto.Prediction$LoggingTransactionV1> r1 = r7.transactions_
                java.util.List r1 = java.util.Collections.unmodifiableList(r1)
                r7.transactions_ = r1
            L43:
                r7.makeExtensionsImmutable()
                throw r0
            L47:
                r3 = r0 & 2
                if (r3 == r5) goto L54
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r3.<init>()     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r7.transactions_ = r3     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r0 = r0 | 2
            L54:
                java.util.List<com.nuance.connect.proto.Prediction$LoggingTransactionV1> r3 = r7.transactions_     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                com.a.a.p<com.nuance.connect.proto.Prediction$LoggingTransactionV1> r4 = com.nuance.connect.proto.Prediction.LoggingTransactionV1.PARSER     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                com.a.a.n r4 = r8.a(r4, r9)     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                r3.add(r4)     // Catch: com.a.a.j -> L2f java.io.IOException -> L60 java.lang.Throwable -> L80
                goto Lf
            L60:
                r1 = move-exception
                r6 = r1
                r1 = r0
                r0 = r6
                com.a.a.j r2 = new com.a.a.j     // Catch: java.lang.Throwable -> L36
                java.lang.String r0 = r0.getMessage()     // Catch: java.lang.Throwable -> L36
                r2.<init>(r0)     // Catch: java.lang.Throwable -> L36
                r2.a = r7     // Catch: java.lang.Throwable -> L36
                throw r2     // Catch: java.lang.Throwable -> L36
            L70:
                r0 = r0 & 2
                if (r0 != r5) goto L7c
                java.util.List<com.nuance.connect.proto.Prediction$LoggingTransactionV1> r0 = r7.transactions_
                java.util.List r0 = java.util.Collections.unmodifiableList(r0)
                r7.transactions_ = r0
            L7c:
                r7.makeExtensionsImmutable()
                return
            L80:
                r1 = move-exception
                r6 = r1
                r1 = r0
                r0 = r6
                goto L37
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.LoggingRequestV1.<init>(com.a.a.d, com.a.a.f):void");
        }
    }

    /* loaded from: classes.dex */
    public static final class LoggingTransactionV1 extends h implements LoggingTransactionV1OrBuilder {
        public static final int APPLICATIONNAME_FIELD_NUMBER = 7;
        public static final int CCPSVERSION_FIELD_NUMBER = 6;
        public static final int CLOUDTIME_FIELD_NUMBER = 4;
        public static final int COUNTRYCODE_FIELD_NUMBER = 8;
        public static p<LoggingTransactionV1> PARSER = new b<LoggingTransactionV1>() { // from class: com.nuance.connect.proto.Prediction.LoggingTransactionV1.1
            @Override // com.a.a.p
            public final LoggingTransactionV1 parsePartialFrom(d dVar, f fVar) throws j {
                return new LoggingTransactionV1(dVar, fVar);
            }
        };
        public static final int PREDICTIONRESULT_FIELD_NUMBER = 2;
        public static final int RESULTCODE_FIELD_NUMBER = 3;
        public static final int TOTALTIME_FIELD_NUMBER = 5;
        public static final int TRANSACTIONID_FIELD_NUMBER = 1;
        private static final LoggingTransactionV1 defaultInstance;
        private static final long serialVersionUID = 0;
        private Object applicationName_;
        private int bitField0_;
        private Object ccpsVersion_;
        private int cloudTime_;
        private Object countryCode_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private PredictionResultV1 predictionResult_;
        private int resultCode_;
        private int totalTime_;
        private Object transactionID_;

        static {
            LoggingTransactionV1 loggingTransactionV1 = new LoggingTransactionV1(true);
            defaultInstance = loggingTransactionV1;
            loggingTransactionV1.initFields();
        }

        private LoggingTransactionV1(h.a aVar) {
            super(aVar);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private LoggingTransactionV1(boolean z) {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        public static LoggingTransactionV1 getDefaultInstance() {
            return defaultInstance;
        }

        private void initFields() {
            this.transactionID_ = "";
            this.predictionResult_ = PredictionResultV1.getDefaultInstance();
            this.resultCode_ = 0;
            this.cloudTime_ = 0;
            this.totalTime_ = 0;
            this.ccpsVersion_ = "";
            this.applicationName_ = "";
            this.countryCode_ = "";
        }

        public static Builder newBuilder() {
            return Builder.access$2600();
        }

        public static Builder newBuilder(LoggingTransactionV1 loggingTransactionV1) {
            return newBuilder().mergeFrom(loggingTransactionV1);
        }

        public static LoggingTransactionV1 parseDelimitedFrom(InputStream inputStream) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream);
        }

        public static LoggingTransactionV1 parseDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream, fVar);
        }

        public static LoggingTransactionV1 parseFrom(c cVar) throws j {
            return PARSER.parseFrom(cVar);
        }

        public static LoggingTransactionV1 parseFrom(c cVar, f fVar) throws j {
            return PARSER.parseFrom(cVar, fVar);
        }

        public static LoggingTransactionV1 parseFrom(d dVar) throws IOException {
            return PARSER.parseFrom(dVar);
        }

        public static LoggingTransactionV1 parseFrom(d dVar, f fVar) throws IOException {
            return PARSER.parseFrom(dVar, fVar);
        }

        public static LoggingTransactionV1 parseFrom(InputStream inputStream) throws IOException {
            return PARSER.parseFrom(inputStream);
        }

        public static LoggingTransactionV1 parseFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseFrom(inputStream, fVar);
        }

        public static LoggingTransactionV1 parseFrom(byte[] bArr) throws j {
            return PARSER.parseFrom(bArr);
        }

        public static LoggingTransactionV1 parseFrom(byte[] bArr, f fVar) throws j {
            return PARSER.parseFrom(bArr, fVar);
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final String getApplicationName() {
            Object obj = this.applicationName_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.applicationName_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final c getApplicationNameBytes() {
            Object obj = this.applicationName_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.applicationName_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final String getCcpsVersion() {
            Object obj = this.ccpsVersion_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.ccpsVersion_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final c getCcpsVersionBytes() {
            Object obj = this.ccpsVersion_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.ccpsVersion_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final int getCloudTime() {
            return this.cloudTime_;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final String getCountryCode() {
            Object obj = this.countryCode_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.countryCode_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final c getCountryCodeBytes() {
            Object obj = this.countryCode_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.countryCode_ = a;
            return a;
        }

        public final LoggingTransactionV1 getDefaultInstanceForType() {
            return defaultInstance;
        }

        @Override // com.a.a.h, com.a.a.n
        public final p<LoggingTransactionV1> getParserForType() {
            return PARSER;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final PredictionResultV1 getPredictionResult() {
            return this.predictionResult_;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final int getResultCode() {
            return this.resultCode_;
        }

        @Override // com.a.a.n
        public final int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i == -1) {
                i = (this.bitField0_ & 1) == 1 ? e.b(1, getTransactionIDBytes()) + 0 : 0;
                if ((this.bitField0_ & 2) == 2) {
                    i += e.b$3eface7e(this.predictionResult_);
                }
                if ((this.bitField0_ & 4) == 4) {
                    i += e.b(3, this.resultCode_);
                }
                if ((this.bitField0_ & 8) == 8) {
                    i += e.b(4, this.cloudTime_);
                }
                if ((this.bitField0_ & 16) == 16) {
                    i += e.b(5, this.totalTime_);
                }
                if ((this.bitField0_ & 32) == 32) {
                    i += e.b(6, getCcpsVersionBytes());
                }
                if ((this.bitField0_ & 64) == 64) {
                    i += e.b(7, getApplicationNameBytes());
                }
                if ((this.bitField0_ & 128) == 128) {
                    i += e.b(8, getCountryCodeBytes());
                }
                this.memoizedSerializedSize = i;
            }
            return i;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final int getTotalTime() {
            return this.totalTime_;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final String getTransactionID() {
            Object obj = this.transactionID_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.transactionID_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final c getTransactionIDBytes() {
            Object obj = this.transactionID_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.transactionID_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasApplicationName() {
            return (this.bitField0_ & 64) == 64;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasCcpsVersion() {
            return (this.bitField0_ & 32) == 32;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasCloudTime() {
            return (this.bitField0_ & 8) == 8;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasCountryCode() {
            return (this.bitField0_ & 128) == 128;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasPredictionResult() {
            return (this.bitField0_ & 2) == 2;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasResultCode() {
            return (this.bitField0_ & 4) == 4;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasTotalTime() {
            return (this.bitField0_ & 16) == 16;
        }

        @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
        public final boolean hasTransactionID() {
            return (this.bitField0_ & 1) == 1;
        }

        @Override // com.a.a.o
        public final boolean isInitialized() {
            byte b = this.memoizedIsInitialized;
            if (b != -1) {
                return b == 1;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.a.a.n
        public final Builder newBuilderForType() {
            return newBuilder();
        }

        @Override // com.a.a.n
        public final Builder toBuilder() {
            return newBuilder(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.a.a.h
        public final Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        @Override // com.a.a.n
        public final void writeTo(e eVar) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                eVar.a(1, getTransactionIDBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                eVar.a$3eface71(this.predictionResult_);
            }
            if ((this.bitField0_ & 4) == 4) {
                eVar.a(3, this.resultCode_);
            }
            if ((this.bitField0_ & 8) == 8) {
                eVar.a(4, this.cloudTime_);
            }
            if ((this.bitField0_ & 16) == 16) {
                eVar.a(5, this.totalTime_);
            }
            if ((this.bitField0_ & 32) == 32) {
                eVar.a(6, getCcpsVersionBytes());
            }
            if ((this.bitField0_ & 64) == 64) {
                eVar.a(7, getApplicationNameBytes());
            }
            if ((this.bitField0_ & 128) == 128) {
                eVar.a(8, getCountryCodeBytes());
            }
        }

        /* loaded from: classes.dex */
        public static final class Builder extends h.a<LoggingTransactionV1, Builder> implements LoggingTransactionV1OrBuilder {
            private int bitField0_;
            private int cloudTime_;
            private int resultCode_;
            private int totalTime_;
            private Object transactionID_ = "";
            private PredictionResultV1 predictionResult_ = PredictionResultV1.getDefaultInstance();
            private Object ccpsVersion_ = "";
            private Object applicationName_ = "";
            private Object countryCode_ = "";

            private Builder() {
                maybeForceBuilderInitialization();
            }

            static /* synthetic */ Builder access$2600() {
                return create();
            }

            private static Builder create() {
                return new Builder();
            }

            private void maybeForceBuilderInitialization() {
            }

            @Override // com.a.a.n.a
            public final LoggingTransactionV1 build() {
                LoggingTransactionV1 buildPartial = buildPartial();
                if (buildPartial.isInitialized()) {
                    return buildPartial;
                }
                throw newUninitializedMessageException(buildPartial);
            }

            public final LoggingTransactionV1 buildPartial() {
                LoggingTransactionV1 loggingTransactionV1 = new LoggingTransactionV1(this);
                int i = this.bitField0_;
                int i2 = (i & 1) != 1 ? 0 : 1;
                loggingTransactionV1.transactionID_ = this.transactionID_;
                if ((i & 2) == 2) {
                    i2 |= 2;
                }
                loggingTransactionV1.predictionResult_ = this.predictionResult_;
                if ((i & 4) == 4) {
                    i2 |= 4;
                }
                loggingTransactionV1.resultCode_ = this.resultCode_;
                if ((i & 8) == 8) {
                    i2 |= 8;
                }
                loggingTransactionV1.cloudTime_ = this.cloudTime_;
                if ((i & 16) == 16) {
                    i2 |= 16;
                }
                loggingTransactionV1.totalTime_ = this.totalTime_;
                if ((i & 32) == 32) {
                    i2 |= 32;
                }
                loggingTransactionV1.ccpsVersion_ = this.ccpsVersion_;
                if ((i & 64) == 64) {
                    i2 |= 64;
                }
                loggingTransactionV1.applicationName_ = this.applicationName_;
                if ((i & 128) == 128) {
                    i2 |= 128;
                }
                loggingTransactionV1.countryCode_ = this.countryCode_;
                loggingTransactionV1.bitField0_ = i2;
                return loggingTransactionV1;
            }

            @Override // com.a.a.h.a
            /* renamed from: clear */
            public final Builder mo26clear() {
                super.mo26clear();
                this.transactionID_ = "";
                this.bitField0_ &= -2;
                this.predictionResult_ = PredictionResultV1.getDefaultInstance();
                this.bitField0_ &= -3;
                this.resultCode_ = 0;
                this.bitField0_ &= -5;
                this.cloudTime_ = 0;
                this.bitField0_ &= -9;
                this.totalTime_ = 0;
                this.bitField0_ &= -17;
                this.ccpsVersion_ = "";
                this.bitField0_ &= -33;
                this.applicationName_ = "";
                this.bitField0_ &= -65;
                this.countryCode_ = "";
                this.bitField0_ &= -129;
                return this;
            }

            public final Builder clearApplicationName() {
                this.bitField0_ &= -65;
                this.applicationName_ = LoggingTransactionV1.getDefaultInstance().getApplicationName();
                return this;
            }

            public final Builder clearCcpsVersion() {
                this.bitField0_ &= -33;
                this.ccpsVersion_ = LoggingTransactionV1.getDefaultInstance().getCcpsVersion();
                return this;
            }

            public final Builder clearCloudTime() {
                this.bitField0_ &= -9;
                this.cloudTime_ = 0;
                return this;
            }

            public final Builder clearCountryCode() {
                this.bitField0_ &= -129;
                this.countryCode_ = LoggingTransactionV1.getDefaultInstance().getCountryCode();
                return this;
            }

            public final Builder clearPredictionResult() {
                this.predictionResult_ = PredictionResultV1.getDefaultInstance();
                this.bitField0_ &= -3;
                return this;
            }

            public final Builder clearResultCode() {
                this.bitField0_ &= -5;
                this.resultCode_ = 0;
                return this;
            }

            public final Builder clearTotalTime() {
                this.bitField0_ &= -17;
                this.totalTime_ = 0;
                return this;
            }

            public final Builder clearTransactionID() {
                this.bitField0_ &= -2;
                this.transactionID_ = LoggingTransactionV1.getDefaultInstance().getTransactionID();
                return this;
            }

            @Override // com.a.a.h.a, com.a.a.a.AbstractC0002a
            /* renamed from: clone */
            public final Builder mo3clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final String getApplicationName() {
                Object obj = this.applicationName_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.applicationName_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final c getApplicationNameBytes() {
                Object obj = this.applicationName_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.applicationName_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final String getCcpsVersion() {
                Object obj = this.ccpsVersion_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.ccpsVersion_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final c getCcpsVersionBytes() {
                Object obj = this.ccpsVersion_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.ccpsVersion_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final int getCloudTime() {
                return this.cloudTime_;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final String getCountryCode() {
                Object obj = this.countryCode_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.countryCode_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final c getCountryCodeBytes() {
                Object obj = this.countryCode_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.countryCode_ = a;
                return a;
            }

            @Override // com.a.a.h.a
            /* renamed from: getDefaultInstanceForType */
            public final LoggingTransactionV1 mo27getDefaultInstanceForType() {
                return LoggingTransactionV1.getDefaultInstance();
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final PredictionResultV1 getPredictionResult() {
                return this.predictionResult_;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final int getResultCode() {
                return this.resultCode_;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final int getTotalTime() {
                return this.totalTime_;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final String getTransactionID() {
                Object obj = this.transactionID_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.transactionID_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final c getTransactionIDBytes() {
                Object obj = this.transactionID_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.transactionID_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasApplicationName() {
                return (this.bitField0_ & 64) == 64;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasCcpsVersion() {
                return (this.bitField0_ & 32) == 32;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasCloudTime() {
                return (this.bitField0_ & 8) == 8;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasCountryCode() {
                return (this.bitField0_ & 128) == 128;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasPredictionResult() {
                return (this.bitField0_ & 2) == 2;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasResultCode() {
                return (this.bitField0_ & 4) == 4;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasTotalTime() {
                return (this.bitField0_ & 16) == 16;
            }

            @Override // com.nuance.connect.proto.Prediction.LoggingTransactionV1OrBuilder
            public final boolean hasTransactionID() {
                return (this.bitField0_ & 1) == 1;
            }

            @Override // com.a.a.o
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.a.a.h.a
            public final Builder mergeFrom(LoggingTransactionV1 loggingTransactionV1) {
                if (loggingTransactionV1 != LoggingTransactionV1.getDefaultInstance()) {
                    if (loggingTransactionV1.hasTransactionID()) {
                        this.bitField0_ |= 1;
                        this.transactionID_ = loggingTransactionV1.transactionID_;
                    }
                    if (loggingTransactionV1.hasPredictionResult()) {
                        mergePredictionResult(loggingTransactionV1.getPredictionResult());
                    }
                    if (loggingTransactionV1.hasResultCode()) {
                        setResultCode(loggingTransactionV1.getResultCode());
                    }
                    if (loggingTransactionV1.hasCloudTime()) {
                        setCloudTime(loggingTransactionV1.getCloudTime());
                    }
                    if (loggingTransactionV1.hasTotalTime()) {
                        setTotalTime(loggingTransactionV1.getTotalTime());
                    }
                    if (loggingTransactionV1.hasCcpsVersion()) {
                        this.bitField0_ |= 32;
                        this.ccpsVersion_ = loggingTransactionV1.ccpsVersion_;
                    }
                    if (loggingTransactionV1.hasApplicationName()) {
                        this.bitField0_ |= 64;
                        this.applicationName_ = loggingTransactionV1.applicationName_;
                    }
                    if (loggingTransactionV1.hasCountryCode()) {
                        this.bitField0_ |= 128;
                        this.countryCode_ = loggingTransactionV1.countryCode_;
                    }
                }
                return this;
            }

            public final Builder mergePredictionResult(PredictionResultV1 predictionResultV1) {
                if ((this.bitField0_ & 2) != 2 || this.predictionResult_ == PredictionResultV1.getDefaultInstance()) {
                    this.predictionResult_ = predictionResultV1;
                } else {
                    this.predictionResult_ = PredictionResultV1.newBuilder(this.predictionResult_).mergeFrom(predictionResultV1).buildPartial();
                }
                this.bitField0_ |= 2;
                return this;
            }

            public final Builder setApplicationName(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 64;
                this.applicationName_ = str;
                return this;
            }

            public final Builder setApplicationNameBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 64;
                this.applicationName_ = cVar;
                return this;
            }

            public final Builder setCcpsVersion(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 32;
                this.ccpsVersion_ = str;
                return this;
            }

            public final Builder setCcpsVersionBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 32;
                this.ccpsVersion_ = cVar;
                return this;
            }

            public final Builder setCloudTime(int i) {
                this.bitField0_ |= 8;
                this.cloudTime_ = i;
                return this;
            }

            public final Builder setCountryCode(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 128;
                this.countryCode_ = str;
                return this;
            }

            public final Builder setCountryCodeBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 128;
                this.countryCode_ = cVar;
                return this;
            }

            public final Builder setPredictionResult(PredictionResultV1.Builder builder) {
                this.predictionResult_ = builder.build();
                this.bitField0_ |= 2;
                return this;
            }

            public final Builder setPredictionResult(PredictionResultV1 predictionResultV1) {
                if (predictionResultV1 == null) {
                    throw new NullPointerException();
                }
                this.predictionResult_ = predictionResultV1;
                this.bitField0_ |= 2;
                return this;
            }

            public final Builder setResultCode(int i) {
                this.bitField0_ |= 4;
                this.resultCode_ = i;
                return this;
            }

            public final Builder setTotalTime(int i) {
                this.bitField0_ |= 16;
                this.totalTime_ = i;
                return this;
            }

            public final Builder setTransactionID(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.transactionID_ = str;
                return this;
            }

            public final Builder setTransactionIDBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.transactionID_ = cVar;
                return this;
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x001c  */
            /* JADX WARN: Removed duplicated region for block: B:14:? A[SYNTHETIC] */
            @Override // com.a.a.a.AbstractC0002a, com.a.a.n.a
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final com.nuance.connect.proto.Prediction.LoggingTransactionV1.Builder mergeFrom(com.a.a.d r5, com.a.a.f r6) throws java.io.IOException {
                /*
                    r4 = this;
                    r2 = 0
                    com.a.a.p<com.nuance.connect.proto.Prediction$LoggingTransactionV1> r0 = com.nuance.connect.proto.Prediction.LoggingTransactionV1.PARSER     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    java.lang.Object r0 = r0.parsePartialFrom(r5, r6)     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$LoggingTransactionV1 r0 = (com.nuance.connect.proto.Prediction.LoggingTransactionV1) r0     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    if (r0 == 0) goto Le
                    r4.mergeFrom(r0)
                Le:
                    return r4
                Lf:
                    r0 = move-exception
                    r1 = r0
                    com.a.a.n r0 = r1.a     // Catch: java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$LoggingTransactionV1 r0 = (com.nuance.connect.proto.Prediction.LoggingTransactionV1) r0     // Catch: java.lang.Throwable -> L20
                    throw r1     // Catch: java.lang.Throwable -> L16
                L16:
                    r1 = move-exception
                    r3 = r1
                    r1 = r0
                    r0 = r3
                L1a:
                    if (r1 == 0) goto L1f
                    r4.mergeFrom(r1)
                L1f:
                    throw r0
                L20:
                    r0 = move-exception
                    r1 = r2
                    goto L1a
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.LoggingTransactionV1.Builder.mergeFrom(com.a.a.d, com.a.a.f):com.nuance.connect.proto.Prediction$LoggingTransactionV1$Builder");
            }
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0014. Please report as an issue. */
        private LoggingTransactionV1(d dVar, f fVar) throws j {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            initFields();
            boolean z = false;
            while (!z) {
                try {
                    try {
                        int a = dVar.a();
                        switch (a) {
                            case 0:
                                z = true;
                            case 10:
                                this.bitField0_ |= 1;
                                this.transactionID_ = dVar.l();
                            case 18:
                                PredictionResultV1.Builder builder = (this.bitField0_ & 2) == 2 ? this.predictionResult_.toBuilder() : null;
                                this.predictionResult_ = (PredictionResultV1) dVar.a(PredictionResultV1.PARSER, fVar);
                                if (builder != null) {
                                    builder.mergeFrom(this.predictionResult_);
                                    this.predictionResult_ = builder.buildPartial();
                                }
                                this.bitField0_ |= 2;
                            case 24:
                                this.bitField0_ |= 4;
                                this.resultCode_ = dVar.s();
                            case 32:
                                this.bitField0_ |= 8;
                                this.cloudTime_ = dVar.s();
                            case 40:
                                this.bitField0_ |= 16;
                                this.totalTime_ = dVar.s();
                            case 50:
                                this.bitField0_ |= 32;
                                this.ccpsVersion_ = dVar.l();
                            case 58:
                                this.bitField0_ |= 64;
                                this.applicationName_ = dVar.l();
                            case 66:
                                this.bitField0_ |= 128;
                                this.countryCode_ = dVar.l();
                            default:
                                if (!parseUnknownField(dVar, fVar, a)) {
                                    z = true;
                                }
                        }
                    } catch (j e) {
                        e.a = this;
                        throw e;
                    } catch (IOException e2) {
                        j jVar = new j(e2.getMessage());
                        jVar.a = this;
                        throw jVar;
                    }
                } finally {
                    makeExtensionsImmutable();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class PredictionRequestV1 extends h implements PredictionRequestV1OrBuilder {
        public static final int DEVICEID_FIELD_NUMBER = 1;
        public static final int NUMPREDICTIONSREQUIRED_FIELD_NUMBER = 4;
        public static p<PredictionRequestV1> PARSER = new b<PredictionRequestV1>() { // from class: com.nuance.connect.proto.Prediction.PredictionRequestV1.1
            @Override // com.a.a.p
            public final PredictionRequestV1 parsePartialFrom(d dVar, f fVar) throws j {
                return new PredictionRequestV1(dVar, fVar);
            }
        };
        public static final int PREDICTIONDATA_FIELD_NUMBER = 5;
        public static final int REQUESTTIMESTAMP_FIELD_NUMBER = 3;
        public static final int TRANSACTIONID_FIELD_NUMBER = 2;
        public static final int XT9VERSION_FIELD_NUMBER = 6;
        private static final PredictionRequestV1 defaultInstance;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private Object deviceID_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private int numPredictionsRequired_;
        private c predictionData_;
        private long requestTimestamp_;
        private Object transactionID_;
        private Object xt9Version_;

        static {
            PredictionRequestV1 predictionRequestV1 = new PredictionRequestV1(true);
            defaultInstance = predictionRequestV1;
            predictionRequestV1.initFields();
        }

        private PredictionRequestV1(h.a aVar) {
            super(aVar);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private PredictionRequestV1(boolean z) {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        public static PredictionRequestV1 getDefaultInstance() {
            return defaultInstance;
        }

        private void initFields() {
            this.deviceID_ = "";
            this.transactionID_ = "";
            this.requestTimestamp_ = 0L;
            this.numPredictionsRequired_ = 0;
            this.predictionData_ = c.a;
            this.xt9Version_ = "";
        }

        public static Builder newBuilder() {
            return Builder.access$100();
        }

        public static Builder newBuilder(PredictionRequestV1 predictionRequestV1) {
            return newBuilder().mergeFrom(predictionRequestV1);
        }

        public static PredictionRequestV1 parseDelimitedFrom(InputStream inputStream) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream);
        }

        public static PredictionRequestV1 parseDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream, fVar);
        }

        public static PredictionRequestV1 parseFrom(c cVar) throws j {
            return PARSER.parseFrom(cVar);
        }

        public static PredictionRequestV1 parseFrom(c cVar, f fVar) throws j {
            return PARSER.parseFrom(cVar, fVar);
        }

        public static PredictionRequestV1 parseFrom(d dVar) throws IOException {
            return PARSER.parseFrom(dVar);
        }

        public static PredictionRequestV1 parseFrom(d dVar, f fVar) throws IOException {
            return PARSER.parseFrom(dVar, fVar);
        }

        public static PredictionRequestV1 parseFrom(InputStream inputStream) throws IOException {
            return PARSER.parseFrom(inputStream);
        }

        public static PredictionRequestV1 parseFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseFrom(inputStream, fVar);
        }

        public static PredictionRequestV1 parseFrom(byte[] bArr) throws j {
            return PARSER.parseFrom(bArr);
        }

        public static PredictionRequestV1 parseFrom(byte[] bArr, f fVar) throws j {
            return PARSER.parseFrom(bArr, fVar);
        }

        public final PredictionRequestV1 getDefaultInstanceForType() {
            return defaultInstance;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final String getDeviceID() {
            Object obj = this.deviceID_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.deviceID_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final c getDeviceIDBytes() {
            Object obj = this.deviceID_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.deviceID_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final int getNumPredictionsRequired() {
            return this.numPredictionsRequired_;
        }

        @Override // com.a.a.h, com.a.a.n
        public final p<PredictionRequestV1> getParserForType() {
            return PARSER;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final c getPredictionData() {
            return this.predictionData_;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final long getRequestTimestamp() {
            return this.requestTimestamp_;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final String getTransactionID() {
            Object obj = this.transactionID_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.transactionID_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final c getTransactionIDBytes() {
            Object obj = this.transactionID_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.transactionID_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final String getXt9Version() {
            Object obj = this.xt9Version_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.xt9Version_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final c getXt9VersionBytes() {
            Object obj = this.xt9Version_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.xt9Version_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasDeviceID() {
            return (this.bitField0_ & 1) == 1;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasNumPredictionsRequired() {
            return (this.bitField0_ & 8) == 8;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasPredictionData() {
            return (this.bitField0_ & 16) == 16;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasRequestTimestamp() {
            return (this.bitField0_ & 4) == 4;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasTransactionID() {
            return (this.bitField0_ & 2) == 2;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
        public final boolean hasXt9Version() {
            return (this.bitField0_ & 32) == 32;
        }

        @Override // com.a.a.o
        public final boolean isInitialized() {
            byte b = this.memoizedIsInitialized;
            if (b != -1) {
                return b == 1;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.a.a.n
        public final Builder newBuilderForType() {
            return newBuilder();
        }

        @Override // com.a.a.n
        public final Builder toBuilder() {
            return newBuilder(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.a.a.h
        public final Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        /* loaded from: classes.dex */
        public static final class Builder extends h.a<PredictionRequestV1, Builder> implements PredictionRequestV1OrBuilder {
            private int bitField0_;
            private int numPredictionsRequired_;
            private long requestTimestamp_;
            private Object deviceID_ = "";
            private Object transactionID_ = "";
            private c predictionData_ = c.a;
            private Object xt9Version_ = "";

            private Builder() {
                maybeForceBuilderInitialization();
            }

            static /* synthetic */ Builder access$100() {
                return create();
            }

            private static Builder create() {
                return new Builder();
            }

            private void maybeForceBuilderInitialization() {
            }

            @Override // com.a.a.n.a
            public final PredictionRequestV1 build() {
                PredictionRequestV1 buildPartial = buildPartial();
                if (buildPartial.isInitialized()) {
                    return buildPartial;
                }
                throw newUninitializedMessageException(buildPartial);
            }

            public final PredictionRequestV1 buildPartial() {
                PredictionRequestV1 predictionRequestV1 = new PredictionRequestV1(this);
                int i = this.bitField0_;
                int i2 = (i & 1) != 1 ? 0 : 1;
                predictionRequestV1.deviceID_ = this.deviceID_;
                if ((i & 2) == 2) {
                    i2 |= 2;
                }
                predictionRequestV1.transactionID_ = this.transactionID_;
                if ((i & 4) == 4) {
                    i2 |= 4;
                }
                predictionRequestV1.requestTimestamp_ = this.requestTimestamp_;
                if ((i & 8) == 8) {
                    i2 |= 8;
                }
                predictionRequestV1.numPredictionsRequired_ = this.numPredictionsRequired_;
                if ((i & 16) == 16) {
                    i2 |= 16;
                }
                predictionRequestV1.predictionData_ = this.predictionData_;
                if ((i & 32) == 32) {
                    i2 |= 32;
                }
                predictionRequestV1.xt9Version_ = this.xt9Version_;
                predictionRequestV1.bitField0_ = i2;
                return predictionRequestV1;
            }

            @Override // com.a.a.h.a
            /* renamed from: clear */
            public final Builder mo26clear() {
                super.mo26clear();
                this.deviceID_ = "";
                this.bitField0_ &= -2;
                this.transactionID_ = "";
                this.bitField0_ &= -3;
                this.requestTimestamp_ = 0L;
                this.bitField0_ &= -5;
                this.numPredictionsRequired_ = 0;
                this.bitField0_ &= -9;
                this.predictionData_ = c.a;
                this.bitField0_ &= -17;
                this.xt9Version_ = "";
                this.bitField0_ &= -33;
                return this;
            }

            public final Builder clearDeviceID() {
                this.bitField0_ &= -2;
                this.deviceID_ = PredictionRequestV1.getDefaultInstance().getDeviceID();
                return this;
            }

            public final Builder clearNumPredictionsRequired() {
                this.bitField0_ &= -9;
                this.numPredictionsRequired_ = 0;
                return this;
            }

            public final Builder clearPredictionData() {
                this.bitField0_ &= -17;
                this.predictionData_ = PredictionRequestV1.getDefaultInstance().getPredictionData();
                return this;
            }

            public final Builder clearRequestTimestamp() {
                this.bitField0_ &= -5;
                this.requestTimestamp_ = 0L;
                return this;
            }

            public final Builder clearTransactionID() {
                this.bitField0_ &= -3;
                this.transactionID_ = PredictionRequestV1.getDefaultInstance().getTransactionID();
                return this;
            }

            public final Builder clearXt9Version() {
                this.bitField0_ &= -33;
                this.xt9Version_ = PredictionRequestV1.getDefaultInstance().getXt9Version();
                return this;
            }

            @Override // com.a.a.h.a, com.a.a.a.AbstractC0002a
            /* renamed from: clone */
            public final Builder mo3clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override // com.a.a.h.a
            /* renamed from: getDefaultInstanceForType */
            public final PredictionRequestV1 mo27getDefaultInstanceForType() {
                return PredictionRequestV1.getDefaultInstance();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final String getDeviceID() {
                Object obj = this.deviceID_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.deviceID_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final c getDeviceIDBytes() {
                Object obj = this.deviceID_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.deviceID_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final int getNumPredictionsRequired() {
                return this.numPredictionsRequired_;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final c getPredictionData() {
                return this.predictionData_;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final long getRequestTimestamp() {
                return this.requestTimestamp_;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final String getTransactionID() {
                Object obj = this.transactionID_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.transactionID_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final c getTransactionIDBytes() {
                Object obj = this.transactionID_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.transactionID_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final String getXt9Version() {
                Object obj = this.xt9Version_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.xt9Version_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final c getXt9VersionBytes() {
                Object obj = this.xt9Version_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.xt9Version_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasDeviceID() {
                return (this.bitField0_ & 1) == 1;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasNumPredictionsRequired() {
                return (this.bitField0_ & 8) == 8;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasPredictionData() {
                return (this.bitField0_ & 16) == 16;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasRequestTimestamp() {
                return (this.bitField0_ & 4) == 4;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasTransactionID() {
                return (this.bitField0_ & 2) == 2;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionRequestV1OrBuilder
            public final boolean hasXt9Version() {
                return (this.bitField0_ & 32) == 32;
            }

            @Override // com.a.a.o
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.a.a.h.a
            public final Builder mergeFrom(PredictionRequestV1 predictionRequestV1) {
                if (predictionRequestV1 != PredictionRequestV1.getDefaultInstance()) {
                    if (predictionRequestV1.hasDeviceID()) {
                        this.bitField0_ |= 1;
                        this.deviceID_ = predictionRequestV1.deviceID_;
                    }
                    if (predictionRequestV1.hasTransactionID()) {
                        this.bitField0_ |= 2;
                        this.transactionID_ = predictionRequestV1.transactionID_;
                    }
                    if (predictionRequestV1.hasRequestTimestamp()) {
                        setRequestTimestamp(predictionRequestV1.getRequestTimestamp());
                    }
                    if (predictionRequestV1.hasNumPredictionsRequired()) {
                        setNumPredictionsRequired(predictionRequestV1.getNumPredictionsRequired());
                    }
                    if (predictionRequestV1.hasPredictionData()) {
                        setPredictionData(predictionRequestV1.getPredictionData());
                    }
                    if (predictionRequestV1.hasXt9Version()) {
                        this.bitField0_ |= 32;
                        this.xt9Version_ = predictionRequestV1.xt9Version_;
                    }
                }
                return this;
            }

            public final Builder setDeviceID(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.deviceID_ = str;
                return this;
            }

            public final Builder setDeviceIDBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.deviceID_ = cVar;
                return this;
            }

            public final Builder setNumPredictionsRequired(int i) {
                this.bitField0_ |= 8;
                this.numPredictionsRequired_ = i;
                return this;
            }

            public final Builder setPredictionData(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 16;
                this.predictionData_ = cVar;
                return this;
            }

            public final Builder setRequestTimestamp(long j) {
                this.bitField0_ |= 4;
                this.requestTimestamp_ = j;
                return this;
            }

            public final Builder setTransactionID(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.transactionID_ = str;
                return this;
            }

            public final Builder setTransactionIDBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.transactionID_ = cVar;
                return this;
            }

            public final Builder setXt9Version(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 32;
                this.xt9Version_ = str;
                return this;
            }

            public final Builder setXt9VersionBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 32;
                this.xt9Version_ = cVar;
                return this;
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x001c  */
            /* JADX WARN: Removed duplicated region for block: B:14:? A[SYNTHETIC] */
            @Override // com.a.a.a.AbstractC0002a, com.a.a.n.a
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final com.nuance.connect.proto.Prediction.PredictionRequestV1.Builder mergeFrom(com.a.a.d r5, com.a.a.f r6) throws java.io.IOException {
                /*
                    r4 = this;
                    r2 = 0
                    com.a.a.p<com.nuance.connect.proto.Prediction$PredictionRequestV1> r0 = com.nuance.connect.proto.Prediction.PredictionRequestV1.PARSER     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    java.lang.Object r0 = r0.parsePartialFrom(r5, r6)     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionRequestV1 r0 = (com.nuance.connect.proto.Prediction.PredictionRequestV1) r0     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    if (r0 == 0) goto Le
                    r4.mergeFrom(r0)
                Le:
                    return r4
                Lf:
                    r0 = move-exception
                    r1 = r0
                    com.a.a.n r0 = r1.a     // Catch: java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionRequestV1 r0 = (com.nuance.connect.proto.Prediction.PredictionRequestV1) r0     // Catch: java.lang.Throwable -> L20
                    throw r1     // Catch: java.lang.Throwable -> L16
                L16:
                    r1 = move-exception
                    r3 = r1
                    r1 = r0
                    r0 = r3
                L1a:
                    if (r1 == 0) goto L1f
                    r4.mergeFrom(r1)
                L1f:
                    throw r0
                L20:
                    r0 = move-exception
                    r1 = r2
                    goto L1a
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.PredictionRequestV1.Builder.mergeFrom(com.a.a.d, com.a.a.f):com.nuance.connect.proto.Prediction$PredictionRequestV1$Builder");
            }
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0013. Please report as an issue. */
        private PredictionRequestV1(d dVar, f fVar) throws j {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            initFields();
            boolean z = false;
            while (!z) {
                try {
                    try {
                        int a = dVar.a();
                        switch (a) {
                            case 0:
                                z = true;
                            case 10:
                                this.bitField0_ |= 1;
                                this.deviceID_ = dVar.l();
                            case 18:
                                this.bitField0_ |= 2;
                                this.transactionID_ = dVar.l();
                            case 24:
                                this.bitField0_ |= 4;
                                this.requestTimestamp_ = dVar.t();
                            case 32:
                                this.bitField0_ |= 8;
                                this.numPredictionsRequired_ = dVar.s();
                            case 42:
                                this.bitField0_ |= 16;
                                this.predictionData_ = dVar.l();
                            case 50:
                                this.bitField0_ |= 32;
                                this.xt9Version_ = dVar.l();
                            default:
                                if (!parseUnknownField(dVar, fVar, a)) {
                                    z = true;
                                }
                        }
                    } catch (j e) {
                        e.a = this;
                        throw e;
                    } catch (IOException e2) {
                        j jVar = new j(e2.getMessage());
                        jVar.a = this;
                        throw jVar;
                    }
                } finally {
                    makeExtensionsImmutable();
                }
            }
        }

        @Override // com.a.a.n
        public final void writeTo(e eVar) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                eVar.a(1, getDeviceIDBytes());
            }
            if ((this.bitField0_ & 2) == 2) {
                eVar.a(2, getTransactionIDBytes());
            }
            if ((this.bitField0_ & 4) == 4) {
                long j = this.requestTimestamp_;
                eVar.c(3, 0);
                while (((-128) & j) != 0) {
                    eVar.d((((int) j) & 127) | 128);
                    j >>>= 7;
                }
                eVar.d((int) j);
            }
            if ((this.bitField0_ & 8) == 8) {
                eVar.a(4, this.numPredictionsRequired_);
            }
            if ((this.bitField0_ & 16) == 16) {
                eVar.a(5, this.predictionData_);
            }
            if ((this.bitField0_ & 32) == 32) {
                eVar.a(6, getXt9VersionBytes());
            }
        }

        @Override // com.a.a.n
        public final int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i == -1) {
                i = (this.bitField0_ & 1) == 1 ? e.b(1, getDeviceIDBytes()) + 0 : 0;
                if ((this.bitField0_ & 2) == 2) {
                    i += e.b(2, getTransactionIDBytes());
                }
                if ((this.bitField0_ & 4) == 4) {
                    long j = this.requestTimestamp_;
                    i += (((-128) & j) != 0 ? ((-16384) & j) == 0 ? 2 : ((-2097152) & j) == 0 ? 3 : ((-268435456) & j) == 0 ? 4 : ((-34359738368L) & j) == 0 ? 5 : ((-4398046511104L) & j) == 0 ? 6 : ((-562949953421312L) & j) == 0 ? 7 : ((-72057594037927936L) & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10 : 1) + e.e(3);
                }
                if ((this.bitField0_ & 8) == 8) {
                    i += e.b(4, this.numPredictionsRequired_);
                }
                if ((this.bitField0_ & 16) == 16) {
                    i += e.b(5, this.predictionData_);
                }
                if ((this.bitField0_ & 32) == 32) {
                    i += e.b(6, getXt9VersionBytes());
                }
                this.memoizedSerializedSize = i;
            }
            return i;
        }
    }

    /* loaded from: classes.dex */
    public static final class PredictionResponseV1 extends h implements PredictionResponseV1OrBuilder {
        public static final int CORETIME_FIELD_NUMBER = 4;
        public static p<PredictionResponseV1> PARSER = new b<PredictionResponseV1>() { // from class: com.nuance.connect.proto.Prediction.PredictionResponseV1.1
            @Override // com.a.a.p
            public final PredictionResponseV1 parsePartialFrom(d dVar, f fVar) throws j {
                return new PredictionResponseV1(dVar, fVar);
            }
        };
        public static final int PREDICTIONRESULT_FIELD_NUMBER = 2;
        public static final int TIMESPENT_FIELD_NUMBER = 3;
        public static final int TRANSACTIONID_FIELD_NUMBER = 1;
        private static final PredictionResponseV1 defaultInstance;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private int coreTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private List<PredictionResultV1> predictionResult_;
        private int timeSpent_;
        private Object transactionID_;

        static {
            PredictionResponseV1 predictionResponseV1 = new PredictionResponseV1(true);
            defaultInstance = predictionResponseV1;
            predictionResponseV1.initFields();
        }

        private PredictionResponseV1(h.a aVar) {
            super(aVar);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private PredictionResponseV1(boolean z) {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        public static PredictionResponseV1 getDefaultInstance() {
            return defaultInstance;
        }

        private void initFields() {
            this.transactionID_ = "";
            this.predictionResult_ = Collections.emptyList();
            this.timeSpent_ = 0;
            this.coreTime_ = 0;
        }

        public static Builder newBuilder() {
            return Builder.access$1100();
        }

        public static Builder newBuilder(PredictionResponseV1 predictionResponseV1) {
            return newBuilder().mergeFrom(predictionResponseV1);
        }

        public static PredictionResponseV1 parseDelimitedFrom(InputStream inputStream) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream);
        }

        public static PredictionResponseV1 parseDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream, fVar);
        }

        public static PredictionResponseV1 parseFrom(c cVar) throws j {
            return PARSER.parseFrom(cVar);
        }

        public static PredictionResponseV1 parseFrom(c cVar, f fVar) throws j {
            return PARSER.parseFrom(cVar, fVar);
        }

        public static PredictionResponseV1 parseFrom(d dVar) throws IOException {
            return PARSER.parseFrom(dVar);
        }

        public static PredictionResponseV1 parseFrom(d dVar, f fVar) throws IOException {
            return PARSER.parseFrom(dVar, fVar);
        }

        public static PredictionResponseV1 parseFrom(InputStream inputStream) throws IOException {
            return PARSER.parseFrom(inputStream);
        }

        public static PredictionResponseV1 parseFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseFrom(inputStream, fVar);
        }

        public static PredictionResponseV1 parseFrom(byte[] bArr) throws j {
            return PARSER.parseFrom(bArr);
        }

        public static PredictionResponseV1 parseFrom(byte[] bArr, f fVar) throws j {
            return PARSER.parseFrom(bArr, fVar);
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final int getCoreTime() {
            return this.coreTime_;
        }

        public final PredictionResponseV1 getDefaultInstanceForType() {
            return defaultInstance;
        }

        @Override // com.a.a.h, com.a.a.n
        public final p<PredictionResponseV1> getParserForType() {
            return PARSER;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final PredictionResultV1 getPredictionResult(int i) {
            return this.predictionResult_.get(i);
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final int getPredictionResultCount() {
            return this.predictionResult_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final List<PredictionResultV1> getPredictionResultList() {
            return this.predictionResult_;
        }

        public final PredictionResultV1OrBuilder getPredictionResultOrBuilder(int i) {
            return this.predictionResult_.get(i);
        }

        public final List<? extends PredictionResultV1OrBuilder> getPredictionResultOrBuilderList() {
            return this.predictionResult_;
        }

        @Override // com.a.a.n
        public final int getSerializedSize() {
            int i = 0;
            int i2 = this.memoizedSerializedSize;
            if (i2 == -1) {
                int b = (this.bitField0_ & 1) == 1 ? e.b(1, getTransactionIDBytes()) + 0 : 0;
                while (true) {
                    i2 = b;
                    if (i >= this.predictionResult_.size()) {
                        break;
                    }
                    b = e.b$3eface7e(this.predictionResult_.get(i)) + i2;
                    i++;
                }
                if ((this.bitField0_ & 2) == 2) {
                    i2 += e.b(3, this.timeSpent_);
                }
                if ((this.bitField0_ & 4) == 4) {
                    i2 += e.b(4, this.coreTime_);
                }
                this.memoizedSerializedSize = i2;
            }
            return i2;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final int getTimeSpent() {
            return this.timeSpent_;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final String getTransactionID() {
            Object obj = this.transactionID_;
            if (obj instanceof String) {
                return (String) obj;
            }
            c cVar = (c) obj;
            String e = cVar.e();
            if (cVar.f()) {
                this.transactionID_ = e;
            }
            return e;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final c getTransactionIDBytes() {
            Object obj = this.transactionID_;
            if (!(obj instanceof String)) {
                return (c) obj;
            }
            c a = c.a((String) obj);
            this.transactionID_ = a;
            return a;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final boolean hasCoreTime() {
            return (this.bitField0_ & 4) == 4;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final boolean hasTimeSpent() {
            return (this.bitField0_ & 2) == 2;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
        public final boolean hasTransactionID() {
            return (this.bitField0_ & 1) == 1;
        }

        @Override // com.a.a.o
        public final boolean isInitialized() {
            byte b = this.memoizedIsInitialized;
            if (b != -1) {
                return b == 1;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.a.a.n
        public final Builder newBuilderForType() {
            return newBuilder();
        }

        @Override // com.a.a.n
        public final Builder toBuilder() {
            return newBuilder(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.a.a.h
        public final Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        @Override // com.a.a.n
        public final void writeTo(e eVar) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) == 1) {
                eVar.a(1, getTransactionIDBytes());
            }
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.predictionResult_.size()) {
                    break;
                }
                eVar.a$3eface71(this.predictionResult_.get(i2));
                i = i2 + 1;
            }
            if ((this.bitField0_ & 2) == 2) {
                eVar.a(3, this.timeSpent_);
            }
            if ((this.bitField0_ & 4) == 4) {
                eVar.a(4, this.coreTime_);
            }
        }

        /* loaded from: classes.dex */
        public static final class Builder extends h.a<PredictionResponseV1, Builder> implements PredictionResponseV1OrBuilder {
            private int bitField0_;
            private int coreTime_;
            private int timeSpent_;
            private Object transactionID_ = "";
            private List<PredictionResultV1> predictionResult_ = Collections.emptyList();

            private Builder() {
                maybeForceBuilderInitialization();
            }

            static /* synthetic */ Builder access$1100() {
                return create();
            }

            private static Builder create() {
                return new Builder();
            }

            private void ensurePredictionResultIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.predictionResult_ = new ArrayList(this.predictionResult_);
                    this.bitField0_ |= 2;
                }
            }

            private void maybeForceBuilderInitialization() {
            }

            public final Builder addAllPredictionResult(Iterable<? extends PredictionResultV1> iterable) {
                ensurePredictionResultIsMutable();
                h.a.addAll(iterable, this.predictionResult_);
                return this;
            }

            public final Builder addPredictionResult(int i, PredictionResultV1.Builder builder) {
                ensurePredictionResultIsMutable();
                this.predictionResult_.add(i, builder.build());
                return this;
            }

            public final Builder addPredictionResult(int i, PredictionResultV1 predictionResultV1) {
                if (predictionResultV1 == null) {
                    throw new NullPointerException();
                }
                ensurePredictionResultIsMutable();
                this.predictionResult_.add(i, predictionResultV1);
                return this;
            }

            public final Builder addPredictionResult(PredictionResultV1.Builder builder) {
                ensurePredictionResultIsMutable();
                this.predictionResult_.add(builder.build());
                return this;
            }

            public final Builder addPredictionResult(PredictionResultV1 predictionResultV1) {
                if (predictionResultV1 == null) {
                    throw new NullPointerException();
                }
                ensurePredictionResultIsMutable();
                this.predictionResult_.add(predictionResultV1);
                return this;
            }

            @Override // com.a.a.n.a
            public final PredictionResponseV1 build() {
                PredictionResponseV1 buildPartial = buildPartial();
                if (buildPartial.isInitialized()) {
                    return buildPartial;
                }
                throw newUninitializedMessageException(buildPartial);
            }

            public final PredictionResponseV1 buildPartial() {
                PredictionResponseV1 predictionResponseV1 = new PredictionResponseV1(this);
                int i = this.bitField0_;
                int i2 = (i & 1) != 1 ? 0 : 1;
                predictionResponseV1.transactionID_ = this.transactionID_;
                if ((this.bitField0_ & 2) == 2) {
                    this.predictionResult_ = Collections.unmodifiableList(this.predictionResult_);
                    this.bitField0_ &= -3;
                }
                predictionResponseV1.predictionResult_ = this.predictionResult_;
                if ((i & 4) == 4) {
                    i2 |= 2;
                }
                predictionResponseV1.timeSpent_ = this.timeSpent_;
                if ((i & 8) == 8) {
                    i2 |= 4;
                }
                predictionResponseV1.coreTime_ = this.coreTime_;
                predictionResponseV1.bitField0_ = i2;
                return predictionResponseV1;
            }

            @Override // com.a.a.h.a
            /* renamed from: clear */
            public final Builder mo26clear() {
                super.mo26clear();
                this.transactionID_ = "";
                this.bitField0_ &= -2;
                this.predictionResult_ = Collections.emptyList();
                this.bitField0_ &= -3;
                this.timeSpent_ = 0;
                this.bitField0_ &= -5;
                this.coreTime_ = 0;
                this.bitField0_ &= -9;
                return this;
            }

            public final Builder clearCoreTime() {
                this.bitField0_ &= -9;
                this.coreTime_ = 0;
                return this;
            }

            public final Builder clearPredictionResult() {
                this.predictionResult_ = Collections.emptyList();
                this.bitField0_ &= -3;
                return this;
            }

            public final Builder clearTimeSpent() {
                this.bitField0_ &= -5;
                this.timeSpent_ = 0;
                return this;
            }

            public final Builder clearTransactionID() {
                this.bitField0_ &= -2;
                this.transactionID_ = PredictionResponseV1.getDefaultInstance().getTransactionID();
                return this;
            }

            @Override // com.a.a.h.a, com.a.a.a.AbstractC0002a
            /* renamed from: clone */
            public final Builder mo3clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final int getCoreTime() {
                return this.coreTime_;
            }

            @Override // com.a.a.h.a
            /* renamed from: getDefaultInstanceForType */
            public final PredictionResponseV1 mo27getDefaultInstanceForType() {
                return PredictionResponseV1.getDefaultInstance();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final PredictionResultV1 getPredictionResult(int i) {
                return this.predictionResult_.get(i);
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final int getPredictionResultCount() {
                return this.predictionResult_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final List<PredictionResultV1> getPredictionResultList() {
                return Collections.unmodifiableList(this.predictionResult_);
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final int getTimeSpent() {
                return this.timeSpent_;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final String getTransactionID() {
                Object obj = this.transactionID_;
                if (obj instanceof String) {
                    return (String) obj;
                }
                String e = ((c) obj).e();
                this.transactionID_ = e;
                return e;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final c getTransactionIDBytes() {
                Object obj = this.transactionID_;
                if (!(obj instanceof String)) {
                    return (c) obj;
                }
                c a = c.a((String) obj);
                this.transactionID_ = a;
                return a;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final boolean hasCoreTime() {
                return (this.bitField0_ & 8) == 8;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final boolean hasTimeSpent() {
                return (this.bitField0_ & 4) == 4;
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResponseV1OrBuilder
            public final boolean hasTransactionID() {
                return (this.bitField0_ & 1) == 1;
            }

            @Override // com.a.a.o
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.a.a.h.a
            public final Builder mergeFrom(PredictionResponseV1 predictionResponseV1) {
                if (predictionResponseV1 != PredictionResponseV1.getDefaultInstance()) {
                    if (predictionResponseV1.hasTransactionID()) {
                        this.bitField0_ |= 1;
                        this.transactionID_ = predictionResponseV1.transactionID_;
                    }
                    if (!predictionResponseV1.predictionResult_.isEmpty()) {
                        if (this.predictionResult_.isEmpty()) {
                            this.predictionResult_ = predictionResponseV1.predictionResult_;
                            this.bitField0_ &= -3;
                        } else {
                            ensurePredictionResultIsMutable();
                            this.predictionResult_.addAll(predictionResponseV1.predictionResult_);
                        }
                    }
                    if (predictionResponseV1.hasTimeSpent()) {
                        setTimeSpent(predictionResponseV1.getTimeSpent());
                    }
                    if (predictionResponseV1.hasCoreTime()) {
                        setCoreTime(predictionResponseV1.getCoreTime());
                    }
                }
                return this;
            }

            public final Builder removePredictionResult(int i) {
                ensurePredictionResultIsMutable();
                this.predictionResult_.remove(i);
                return this;
            }

            public final Builder setCoreTime(int i) {
                this.bitField0_ |= 8;
                this.coreTime_ = i;
                return this;
            }

            public final Builder setPredictionResult(int i, PredictionResultV1.Builder builder) {
                ensurePredictionResultIsMutable();
                this.predictionResult_.set(i, builder.build());
                return this;
            }

            public final Builder setPredictionResult(int i, PredictionResultV1 predictionResultV1) {
                if (predictionResultV1 == null) {
                    throw new NullPointerException();
                }
                ensurePredictionResultIsMutable();
                this.predictionResult_.set(i, predictionResultV1);
                return this;
            }

            public final Builder setTimeSpent(int i) {
                this.bitField0_ |= 4;
                this.timeSpent_ = i;
                return this;
            }

            public final Builder setTransactionID(String str) {
                if (str == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.transactionID_ = str;
                return this;
            }

            public final Builder setTransactionIDBytes(c cVar) {
                if (cVar == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.transactionID_ = cVar;
                return this;
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x001c  */
            /* JADX WARN: Removed duplicated region for block: B:14:? A[SYNTHETIC] */
            @Override // com.a.a.a.AbstractC0002a, com.a.a.n.a
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final com.nuance.connect.proto.Prediction.PredictionResponseV1.Builder mergeFrom(com.a.a.d r5, com.a.a.f r6) throws java.io.IOException {
                /*
                    r4 = this;
                    r2 = 0
                    com.a.a.p<com.nuance.connect.proto.Prediction$PredictionResponseV1> r0 = com.nuance.connect.proto.Prediction.PredictionResponseV1.PARSER     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    java.lang.Object r0 = r0.parsePartialFrom(r5, r6)     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionResponseV1 r0 = (com.nuance.connect.proto.Prediction.PredictionResponseV1) r0     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    if (r0 == 0) goto Le
                    r4.mergeFrom(r0)
                Le:
                    return r4
                Lf:
                    r0 = move-exception
                    r1 = r0
                    com.a.a.n r0 = r1.a     // Catch: java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionResponseV1 r0 = (com.nuance.connect.proto.Prediction.PredictionResponseV1) r0     // Catch: java.lang.Throwable -> L20
                    throw r1     // Catch: java.lang.Throwable -> L16
                L16:
                    r1 = move-exception
                    r3 = r1
                    r1 = r0
                    r0 = r3
                L1a:
                    if (r1 == 0) goto L1f
                    r4.mergeFrom(r1)
                L1f:
                    throw r0
                L20:
                    r0 = move-exception
                    r1 = r2
                    goto L1a
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.PredictionResponseV1.Builder.mergeFrom(com.a.a.d, com.a.a.f):com.nuance.connect.proto.Prediction$PredictionResponseV1$Builder");
            }
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0015. Please report as an issue. */
        /* JADX WARN: Multi-variable type inference failed */
        private PredictionResponseV1(d dVar, f fVar) throws j {
            boolean z = false;
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            initFields();
            int i = 0;
            while (!z) {
                try {
                    try {
                        int a = dVar.a();
                        switch (a) {
                            case 0:
                                z = true;
                            case 10:
                                this.bitField0_ |= 1;
                                this.transactionID_ = dVar.l();
                            case 18:
                                if ((i & 2) != 2) {
                                    this.predictionResult_ = new ArrayList();
                                    i |= 2;
                                }
                                this.predictionResult_.add(dVar.a(PredictionResultV1.PARSER, fVar));
                            case 24:
                                this.bitField0_ |= 2;
                                this.timeSpent_ = dVar.m();
                            case 32:
                                this.bitField0_ |= 4;
                                this.coreTime_ = dVar.m();
                            default:
                                if (!parseUnknownField(dVar, fVar, a)) {
                                    z = true;
                                }
                        }
                    } catch (j e) {
                        e.a = this;
                        throw e;
                    } catch (IOException e2) {
                        j jVar = new j(e2.getMessage());
                        jVar.a = this;
                        throw jVar;
                    }
                } finally {
                    if ((i & 2) == 2) {
                        this.predictionResult_ = Collections.unmodifiableList(this.predictionResult_);
                    }
                    makeExtensionsImmutable();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class PredictionResultV1 extends h implements PredictionResultV1OrBuilder {
        public static final int ATTRIBUTE_FIELD_NUMBER = 4;
        public static final int FULLSPELL_FIELD_NUMBER = 3;
        public static p<PredictionResultV1> PARSER = new b<PredictionResultV1>() { // from class: com.nuance.connect.proto.Prediction.PredictionResultV1.1
            @Override // com.a.a.p
            public final PredictionResultV1 parsePartialFrom(d dVar, f fVar) throws j {
                return new PredictionResultV1(dVar, fVar);
            }
        };
        public static final int PHRASE_FIELD_NUMBER = 1;
        public static final int SPELL_FIELD_NUMBER = 2;
        private static final PredictionResultV1 defaultInstance;
        private static final long serialVersionUID = 0;
        private List<Integer> attribute_;
        private List<Integer> fullSpell_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private List<Integer> phrase_;
        private List<Integer> spell_;

        static {
            PredictionResultV1 predictionResultV1 = new PredictionResultV1(true);
            defaultInstance = predictionResultV1;
            predictionResultV1.initFields();
        }

        private PredictionResultV1(h.a aVar) {
            super(aVar);
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        private PredictionResultV1(boolean z) {
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
        }

        public static PredictionResultV1 getDefaultInstance() {
            return defaultInstance;
        }

        private void initFields() {
            this.phrase_ = Collections.emptyList();
            this.spell_ = Collections.emptyList();
            this.fullSpell_ = Collections.emptyList();
            this.attribute_ = Collections.emptyList();
        }

        public static Builder newBuilder() {
            return Builder.access$1900();
        }

        public static Builder newBuilder(PredictionResultV1 predictionResultV1) {
            return newBuilder().mergeFrom(predictionResultV1);
        }

        public static PredictionResultV1 parseDelimitedFrom(InputStream inputStream) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream);
        }

        public static PredictionResultV1 parseDelimitedFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseDelimitedFrom(inputStream, fVar);
        }

        public static PredictionResultV1 parseFrom(c cVar) throws j {
            return PARSER.parseFrom(cVar);
        }

        public static PredictionResultV1 parseFrom(c cVar, f fVar) throws j {
            return PARSER.parseFrom(cVar, fVar);
        }

        public static PredictionResultV1 parseFrom(d dVar) throws IOException {
            return PARSER.parseFrom(dVar);
        }

        public static PredictionResultV1 parseFrom(d dVar, f fVar) throws IOException {
            return PARSER.parseFrom(dVar, fVar);
        }

        public static PredictionResultV1 parseFrom(InputStream inputStream) throws IOException {
            return PARSER.parseFrom(inputStream);
        }

        public static PredictionResultV1 parseFrom(InputStream inputStream, f fVar) throws IOException {
            return PARSER.parseFrom(inputStream, fVar);
        }

        public static PredictionResultV1 parseFrom(byte[] bArr) throws j {
            return PARSER.parseFrom(bArr);
        }

        public static PredictionResultV1 parseFrom(byte[] bArr, f fVar) throws j {
            return PARSER.parseFrom(bArr, fVar);
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getAttribute(int i) {
            return this.attribute_.get(i).intValue();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getAttributeCount() {
            return this.attribute_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final List<Integer> getAttributeList() {
            return this.attribute_;
        }

        public final PredictionResultV1 getDefaultInstanceForType() {
            return defaultInstance;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getFullSpell(int i) {
            return this.fullSpell_.get(i).intValue();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getFullSpellCount() {
            return this.fullSpell_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final List<Integer> getFullSpellList() {
            return this.fullSpell_;
        }

        @Override // com.a.a.h, com.a.a.n
        public final p<PredictionResultV1> getParserForType() {
            return PARSER;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getPhrase(int i) {
            return this.phrase_.get(i).intValue();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getPhraseCount() {
            return this.phrase_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final List<Integer> getPhraseList() {
            return this.phrase_;
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getSpell(int i) {
            return this.spell_.get(i).intValue();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final int getSpellCount() {
            return this.spell_.size();
        }

        @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
        public final List<Integer> getSpellList() {
            return this.spell_;
        }

        @Override // com.a.a.o
        public final boolean isInitialized() {
            byte b = this.memoizedIsInitialized;
            if (b != -1) {
                return b == 1;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.a.a.n
        public final Builder newBuilderForType() {
            return newBuilder();
        }

        @Override // com.a.a.n
        public final Builder toBuilder() {
            return newBuilder(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.a.a.h
        public final Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }

        @Override // com.a.a.n
        public final void writeTo(e eVar) throws IOException {
            getSerializedSize();
            for (int i = 0; i < this.phrase_.size(); i++) {
                eVar.a(1, this.phrase_.get(i).intValue());
            }
            for (int i2 = 0; i2 < this.spell_.size(); i2++) {
                eVar.a(2, this.spell_.get(i2).intValue());
            }
            for (int i3 = 0; i3 < this.fullSpell_.size(); i3++) {
                eVar.a(3, this.fullSpell_.get(i3).intValue());
            }
            for (int i4 = 0; i4 < this.attribute_.size(); i4++) {
                eVar.a(4, this.attribute_.get(i4).intValue());
            }
        }

        /* loaded from: classes.dex */
        public static final class Builder extends h.a<PredictionResultV1, Builder> implements PredictionResultV1OrBuilder {
            private int bitField0_;
            private List<Integer> phrase_ = Collections.emptyList();
            private List<Integer> spell_ = Collections.emptyList();
            private List<Integer> fullSpell_ = Collections.emptyList();
            private List<Integer> attribute_ = Collections.emptyList();

            private Builder() {
                maybeForceBuilderInitialization();
            }

            static /* synthetic */ Builder access$1900() {
                return create();
            }

            private static Builder create() {
                return new Builder();
            }

            private void ensureAttributeIsMutable() {
                if ((this.bitField0_ & 8) != 8) {
                    this.attribute_ = new ArrayList(this.attribute_);
                    this.bitField0_ |= 8;
                }
            }

            private void ensureFullSpellIsMutable() {
                if ((this.bitField0_ & 4) != 4) {
                    this.fullSpell_ = new ArrayList(this.fullSpell_);
                    this.bitField0_ |= 4;
                }
            }

            private void ensurePhraseIsMutable() {
                if ((this.bitField0_ & 1) != 1) {
                    this.phrase_ = new ArrayList(this.phrase_);
                    this.bitField0_ |= 1;
                }
            }

            private void ensureSpellIsMutable() {
                if ((this.bitField0_ & 2) != 2) {
                    this.spell_ = new ArrayList(this.spell_);
                    this.bitField0_ |= 2;
                }
            }

            private void maybeForceBuilderInitialization() {
            }

            public final Builder addAllAttribute(Iterable<? extends Integer> iterable) {
                ensureAttributeIsMutable();
                h.a.addAll(iterable, this.attribute_);
                return this;
            }

            public final Builder addAllFullSpell(Iterable<? extends Integer> iterable) {
                ensureFullSpellIsMutable();
                h.a.addAll(iterable, this.fullSpell_);
                return this;
            }

            public final Builder addAllPhrase(Iterable<? extends Integer> iterable) {
                ensurePhraseIsMutable();
                h.a.addAll(iterable, this.phrase_);
                return this;
            }

            public final Builder addAllSpell(Iterable<? extends Integer> iterable) {
                ensureSpellIsMutable();
                h.a.addAll(iterable, this.spell_);
                return this;
            }

            public final Builder addAttribute(int i) {
                ensureAttributeIsMutable();
                this.attribute_.add(Integer.valueOf(i));
                return this;
            }

            public final Builder addFullSpell(int i) {
                ensureFullSpellIsMutable();
                this.fullSpell_.add(Integer.valueOf(i));
                return this;
            }

            public final Builder addPhrase(int i) {
                ensurePhraseIsMutable();
                this.phrase_.add(Integer.valueOf(i));
                return this;
            }

            public final Builder addSpell(int i) {
                ensureSpellIsMutable();
                this.spell_.add(Integer.valueOf(i));
                return this;
            }

            @Override // com.a.a.n.a
            public final PredictionResultV1 build() {
                PredictionResultV1 buildPartial = buildPartial();
                if (buildPartial.isInitialized()) {
                    return buildPartial;
                }
                throw newUninitializedMessageException(buildPartial);
            }

            public final PredictionResultV1 buildPartial() {
                PredictionResultV1 predictionResultV1 = new PredictionResultV1(this);
                if ((this.bitField0_ & 1) == 1) {
                    this.phrase_ = Collections.unmodifiableList(this.phrase_);
                    this.bitField0_ &= -2;
                }
                predictionResultV1.phrase_ = this.phrase_;
                if ((this.bitField0_ & 2) == 2) {
                    this.spell_ = Collections.unmodifiableList(this.spell_);
                    this.bitField0_ &= -3;
                }
                predictionResultV1.spell_ = this.spell_;
                if ((this.bitField0_ & 4) == 4) {
                    this.fullSpell_ = Collections.unmodifiableList(this.fullSpell_);
                    this.bitField0_ &= -5;
                }
                predictionResultV1.fullSpell_ = this.fullSpell_;
                if ((this.bitField0_ & 8) == 8) {
                    this.attribute_ = Collections.unmodifiableList(this.attribute_);
                    this.bitField0_ &= -9;
                }
                predictionResultV1.attribute_ = this.attribute_;
                return predictionResultV1;
            }

            @Override // com.a.a.h.a
            /* renamed from: clear */
            public final Builder mo26clear() {
                super.mo26clear();
                this.phrase_ = Collections.emptyList();
                this.bitField0_ &= -2;
                this.spell_ = Collections.emptyList();
                this.bitField0_ &= -3;
                this.fullSpell_ = Collections.emptyList();
                this.bitField0_ &= -5;
                this.attribute_ = Collections.emptyList();
                this.bitField0_ &= -9;
                return this;
            }

            public final Builder clearAttribute() {
                this.attribute_ = Collections.emptyList();
                this.bitField0_ &= -9;
                return this;
            }

            public final Builder clearFullSpell() {
                this.fullSpell_ = Collections.emptyList();
                this.bitField0_ &= -5;
                return this;
            }

            public final Builder clearPhrase() {
                this.phrase_ = Collections.emptyList();
                this.bitField0_ &= -2;
                return this;
            }

            public final Builder clearSpell() {
                this.spell_ = Collections.emptyList();
                this.bitField0_ &= -3;
                return this;
            }

            @Override // com.a.a.h.a, com.a.a.a.AbstractC0002a
            /* renamed from: clone */
            public final Builder mo3clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getAttribute(int i) {
                return this.attribute_.get(i).intValue();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getAttributeCount() {
                return this.attribute_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final List<Integer> getAttributeList() {
                return Collections.unmodifiableList(this.attribute_);
            }

            @Override // com.a.a.h.a
            /* renamed from: getDefaultInstanceForType */
            public final PredictionResultV1 mo27getDefaultInstanceForType() {
                return PredictionResultV1.getDefaultInstance();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getFullSpell(int i) {
                return this.fullSpell_.get(i).intValue();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getFullSpellCount() {
                return this.fullSpell_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final List<Integer> getFullSpellList() {
                return Collections.unmodifiableList(this.fullSpell_);
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getPhrase(int i) {
                return this.phrase_.get(i).intValue();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getPhraseCount() {
                return this.phrase_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final List<Integer> getPhraseList() {
                return Collections.unmodifiableList(this.phrase_);
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getSpell(int i) {
                return this.spell_.get(i).intValue();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final int getSpellCount() {
                return this.spell_.size();
            }

            @Override // com.nuance.connect.proto.Prediction.PredictionResultV1OrBuilder
            public final List<Integer> getSpellList() {
                return Collections.unmodifiableList(this.spell_);
            }

            @Override // com.a.a.o
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.a.a.h.a
            public final Builder mergeFrom(PredictionResultV1 predictionResultV1) {
                if (predictionResultV1 != PredictionResultV1.getDefaultInstance()) {
                    if (!predictionResultV1.phrase_.isEmpty()) {
                        if (this.phrase_.isEmpty()) {
                            this.phrase_ = predictionResultV1.phrase_;
                            this.bitField0_ &= -2;
                        } else {
                            ensurePhraseIsMutable();
                            this.phrase_.addAll(predictionResultV1.phrase_);
                        }
                    }
                    if (!predictionResultV1.spell_.isEmpty()) {
                        if (this.spell_.isEmpty()) {
                            this.spell_ = predictionResultV1.spell_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureSpellIsMutable();
                            this.spell_.addAll(predictionResultV1.spell_);
                        }
                    }
                    if (!predictionResultV1.fullSpell_.isEmpty()) {
                        if (this.fullSpell_.isEmpty()) {
                            this.fullSpell_ = predictionResultV1.fullSpell_;
                            this.bitField0_ &= -5;
                        } else {
                            ensureFullSpellIsMutable();
                            this.fullSpell_.addAll(predictionResultV1.fullSpell_);
                        }
                    }
                    if (!predictionResultV1.attribute_.isEmpty()) {
                        if (this.attribute_.isEmpty()) {
                            this.attribute_ = predictionResultV1.attribute_;
                            this.bitField0_ &= -9;
                        } else {
                            ensureAttributeIsMutable();
                            this.attribute_.addAll(predictionResultV1.attribute_);
                        }
                    }
                }
                return this;
            }

            public final Builder setAttribute(int i, int i2) {
                ensureAttributeIsMutable();
                this.attribute_.set(i, Integer.valueOf(i2));
                return this;
            }

            public final Builder setFullSpell(int i, int i2) {
                ensureFullSpellIsMutable();
                this.fullSpell_.set(i, Integer.valueOf(i2));
                return this;
            }

            public final Builder setPhrase(int i, int i2) {
                ensurePhraseIsMutable();
                this.phrase_.set(i, Integer.valueOf(i2));
                return this;
            }

            public final Builder setSpell(int i, int i2) {
                ensureSpellIsMutable();
                this.spell_.set(i, Integer.valueOf(i2));
                return this;
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x001c  */
            /* JADX WARN: Removed duplicated region for block: B:14:? A[SYNTHETIC] */
            @Override // com.a.a.a.AbstractC0002a, com.a.a.n.a
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final com.nuance.connect.proto.Prediction.PredictionResultV1.Builder mergeFrom(com.a.a.d r5, com.a.a.f r6) throws java.io.IOException {
                /*
                    r4 = this;
                    r2 = 0
                    com.a.a.p<com.nuance.connect.proto.Prediction$PredictionResultV1> r0 = com.nuance.connect.proto.Prediction.PredictionResultV1.PARSER     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    java.lang.Object r0 = r0.parsePartialFrom(r5, r6)     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionResultV1 r0 = (com.nuance.connect.proto.Prediction.PredictionResultV1) r0     // Catch: com.a.a.j -> Lf java.lang.Throwable -> L20
                    if (r0 == 0) goto Le
                    r4.mergeFrom(r0)
                Le:
                    return r4
                Lf:
                    r0 = move-exception
                    r1 = r0
                    com.a.a.n r0 = r1.a     // Catch: java.lang.Throwable -> L20
                    com.nuance.connect.proto.Prediction$PredictionResultV1 r0 = (com.nuance.connect.proto.Prediction.PredictionResultV1) r0     // Catch: java.lang.Throwable -> L20
                    throw r1     // Catch: java.lang.Throwable -> L16
                L16:
                    r1 = move-exception
                    r3 = r1
                    r1 = r0
                    r0 = r3
                L1a:
                    if (r1 == 0) goto L1f
                    r4.mergeFrom(r1)
                L1f:
                    throw r0
                L20:
                    r0 = move-exception
                    r1 = r2
                    goto L1a
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.proto.Prediction.PredictionResultV1.Builder.mergeFrom(com.a.a.d, com.a.a.f):com.nuance.connect.proto.Prediction$PredictionResultV1$Builder");
            }
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:5:0x001a. Please report as an issue. */
        private PredictionResultV1(d dVar, f fVar) throws j {
            int i;
            Throwable th;
            IOException e;
            j e2;
            int i2;
            int i3;
            int i4;
            int i5;
            int i6;
            int i7;
            this.memoizedIsInitialized = (byte) -1;
            this.memoizedSerializedSize = -1;
            initFields();
            boolean z = false;
            int i8 = 0;
            while (!z) {
                try {
                    int a = dVar.a();
                    switch (a) {
                        case 0:
                            z = true;
                        case 8:
                            if ((i8 & 1) != 1) {
                                this.phrase_ = new ArrayList();
                                i = i8 | 1;
                            } else {
                                i = i8;
                            }
                            try {
                                try {
                                    this.phrase_.add(Integer.valueOf(dVar.m()));
                                    i8 = i;
                                } catch (j e3) {
                                    e2 = e3;
                                    e2.a = this;
                                    throw e2;
                                } catch (IOException e4) {
                                    e = e4;
                                    j jVar = new j(e.getMessage());
                                    jVar.a = this;
                                    throw jVar;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                if ((i & 1) == 1) {
                                    this.phrase_ = Collections.unmodifiableList(this.phrase_);
                                }
                                if ((i & 2) == 2) {
                                    this.spell_ = Collections.unmodifiableList(this.spell_);
                                }
                                if ((i & 4) == 4) {
                                    this.fullSpell_ = Collections.unmodifiableList(this.fullSpell_);
                                }
                                if ((i & 8) == 8) {
                                    this.attribute_ = Collections.unmodifiableList(this.attribute_);
                                }
                                makeExtensionsImmutable();
                                throw th;
                            }
                        case 10:
                            int d = dVar.d(dVar.s());
                            if ((i8 & 1) == 1 || dVar.w() <= 0) {
                                i7 = i8;
                            } else {
                                this.phrase_ = new ArrayList();
                                i7 = i8 | 1;
                            }
                            while (dVar.w() > 0) {
                                this.phrase_.add(Integer.valueOf(dVar.m()));
                            }
                            dVar.e(d);
                            i8 = i7;
                            break;
                        case 16:
                            if ((i8 & 2) != 2) {
                                this.spell_ = new ArrayList();
                                i6 = i8 | 2;
                            } else {
                                i6 = i8;
                            }
                            this.spell_.add(Integer.valueOf(dVar.m()));
                            i8 = i6;
                        case 18:
                            int d2 = dVar.d(dVar.s());
                            if ((i8 & 2) == 2 || dVar.w() <= 0) {
                                i5 = i8;
                            } else {
                                this.spell_ = new ArrayList();
                                i5 = i8 | 2;
                            }
                            while (dVar.w() > 0) {
                                this.spell_.add(Integer.valueOf(dVar.m()));
                            }
                            dVar.e(d2);
                            i8 = i5;
                            break;
                        case 24:
                            if ((i8 & 4) != 4) {
                                this.fullSpell_ = new ArrayList();
                                i4 = i8 | 4;
                            } else {
                                i4 = i8;
                            }
                            this.fullSpell_.add(Integer.valueOf(dVar.m()));
                            i8 = i4;
                        case 26:
                            int d3 = dVar.d(dVar.s());
                            if ((i8 & 4) == 4 || dVar.w() <= 0) {
                                i3 = i8;
                            } else {
                                this.fullSpell_ = new ArrayList();
                                i3 = i8 | 4;
                            }
                            while (dVar.w() > 0) {
                                this.fullSpell_.add(Integer.valueOf(dVar.m()));
                            }
                            dVar.e(d3);
                            i8 = i3;
                            break;
                        case 32:
                            if ((i8 & 8) != 8) {
                                this.attribute_ = new ArrayList();
                                i2 = i8 | 8;
                            } else {
                                i2 = i8;
                            }
                            this.attribute_.add(Integer.valueOf(dVar.m()));
                            i8 = i2;
                        case 34:
                            int d4 = dVar.d(dVar.s());
                            if ((i8 & 8) != 8 && dVar.w() > 0) {
                                this.attribute_ = new ArrayList();
                                i8 |= 8;
                            }
                            while (dVar.w() > 0) {
                                this.attribute_.add(Integer.valueOf(dVar.m()));
                            }
                            dVar.e(d4);
                            break;
                        default:
                            if (!parseUnknownField(dVar, fVar, a)) {
                                z = true;
                            }
                    }
                } catch (j e5) {
                    e2 = e5;
                } catch (IOException e6) {
                    e = e6;
                } catch (Throwable th3) {
                    i = i8;
                    th = th3;
                }
            }
            if ((i8 & 1) == 1) {
                this.phrase_ = Collections.unmodifiableList(this.phrase_);
            }
            if ((i8 & 2) == 2) {
                this.spell_ = Collections.unmodifiableList(this.spell_);
            }
            if ((i8 & 4) == 4) {
                this.fullSpell_ = Collections.unmodifiableList(this.fullSpell_);
            }
            if ((i8 & 8) == 8) {
                this.attribute_ = Collections.unmodifiableList(this.attribute_);
            }
            makeExtensionsImmutable();
        }

        @Override // com.a.a.n
        public final int getSerializedSize() {
            int i = 0;
            int i2 = this.memoizedSerializedSize;
            if (i2 != -1) {
                return i2;
            }
            int i3 = 0;
            for (int i4 = 0; i4 < this.phrase_.size(); i4++) {
                i3 += e.g(this.phrase_.get(i4).intValue());
            }
            int size = i3 + 0 + (getPhraseList().size() * 1);
            int i5 = 0;
            for (int i6 = 0; i6 < this.spell_.size(); i6++) {
                i5 += e.g(this.spell_.get(i6).intValue());
            }
            int size2 = size + i5 + (getSpellList().size() * 1);
            int i7 = 0;
            for (int i8 = 0; i8 < this.fullSpell_.size(); i8++) {
                i7 += e.g(this.fullSpell_.get(i8).intValue());
            }
            int size3 = size2 + i7 + (getFullSpellList().size() * 1);
            int i9 = 0;
            while (i < this.attribute_.size()) {
                int g = e.g(this.attribute_.get(i).intValue()) + i9;
                i++;
                i9 = g;
            }
            int size4 = size3 + i9 + (getAttributeList().size() * 1);
            this.memoizedSerializedSize = size4;
            return size4;
        }
    }
}
