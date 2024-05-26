package com.nuance.connect.comm;

import com.nuance.connect.comm.CommandQueue;
import com.nuance.connect.util.Logger;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class HttpConnector extends Connector {
    private static final int DEFAULT_READ_TIMEOUT = 120;
    private static final int HTTP_INVALID_RANGE = 416;
    private int delayTimeoutSeconds;
    private final Logger.Log log;
    private String serverURL;
    private SSLSocketFactory socketFactory;

    /* loaded from: classes.dex */
    private static class ByteCountingOutputStream extends FilterOutputStream {
        private long size;

        public ByteCountingOutputStream(OutputStream outputStream) {
            super(outputStream);
            this.size = 0L;
        }

        public long size() {
            return this.size;
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(int i) throws IOException {
            super.write(i);
            this.size++;
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            super.write(bArr, i, i2);
            this.size += i2;
        }
    }

    public HttpConnector(MessageSendingBus messageSendingBus, CommandQueue.ConnectionStatus connectionStatus, ConnectorCallback connectorCallback, AnalyticsDataUsageScribe analyticsDataUsageScribe, String str, String str2) {
        super(messageSendingBus, connectionStatus, connectorCallback, analyticsDataUsageScribe, str2);
        this.log = Logger.getThreadLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.delayTimeoutSeconds = 120;
        updateMinimumSSLProtocol(str);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:73:0x0273. Please report as an issue. */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:0x0646 A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:102:0x064b A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TRY_LEAVE, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0658  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0946  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0653 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0679 A[Catch: all -> 0x078e, TRY_ENTER, TryCatch #27 {all -> 0x078e, blocks: (B:84:0x04ff, B:87:0x051a, B:89:0x0527, B:120:0x0679, B:122:0x0686, B:130:0x06cf, B:132:0x06dc, B:145:0x08cf, B:146:0x08d8, B:147:0x08b2, B:149:0x08ba, B:151:0x08c0, B:153:0x072e, B:158:0x073e, B:161:0x0747, B:168:0x0783, B:166:0x0797, B:164:0x07a3, B:175:0x0768, B:190:0x07ae, B:192:0x07df, B:193:0x07f2), top: B:83:0x04ff, inners: #33, #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:132:0x06dc A[Catch: all -> 0x078e, TRY_LEAVE, TryCatch #27 {all -> 0x078e, blocks: (B:84:0x04ff, B:87:0x051a, B:89:0x0527, B:120:0x0679, B:122:0x0686, B:130:0x06cf, B:132:0x06dc, B:145:0x08cf, B:146:0x08d8, B:147:0x08b2, B:149:0x08ba, B:151:0x08c0, B:153:0x072e, B:158:0x073e, B:161:0x0747, B:168:0x0783, B:166:0x0797, B:164:0x07a3, B:175:0x0768, B:190:0x07ae, B:192:0x07df, B:193:0x07f2), top: B:83:0x04ff, inners: #33, #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0729  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x054d A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TRY_ENTER, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:186:0x0552 A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:188:0x0557 A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x051a A[Catch: all -> 0x078e, TryCatch #27 {all -> 0x078e, blocks: (B:84:0x04ff, B:87:0x051a, B:89:0x0527, B:120:0x0679, B:122:0x0686, B:130:0x06cf, B:132:0x06dc, B:145:0x08cf, B:146:0x08d8, B:147:0x08b2, B:149:0x08ba, B:151:0x08c0, B:153:0x072e, B:158:0x073e, B:161:0x0747, B:168:0x0783, B:166:0x0797, B:164:0x07a3, B:175:0x0768, B:190:0x07ae, B:192:0x07df, B:193:0x07f2), top: B:83:0x04ff, inners: #33, #38 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0641 A[Catch: all -> 0x005e, SSLException -> 0x055b, IOException -> 0x08d9, TransactionException -> 0x08f3, ConnectorException -> 0x090d, Exception -> 0x090f, TRY_ENTER, TryCatch #36 {IOException -> 0x08d9, Exception -> 0x090f, blocks: (B:98:0x0641, B:100:0x0646, B:102:0x064b, B:184:0x054d, B:186:0x0552, B:188:0x0557, B:189:0x055a), top: B:82:0x04ff, outer: #38 }] */
    /* JADX WARN: Type inference failed for: r30v0, types: [com.nuance.connect.comm.HttpConnector] */
    /* JADX WARN: Type inference failed for: r7v28 */
    /* JADX WARN: Type inference failed for: r7v29, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r7v31 */
    /* JADX WARN: Type inference failed for: r7v32 */
    /* JADX WARN: Type inference failed for: r7v33 */
    /* JADX WARN: Type inference failed for: r7v34 */
    /* JADX WARN: Type inference failed for: r7v35 */
    /* JADX WARN: Type inference failed for: r7v38, types: [com.nuance.connect.util.Logger$Log] */
    /* JADX WARN: Type inference failed for: r7v42, types: [com.nuance.connect.util.Logger$Log] */
    /* JADX WARN: Type inference failed for: r7v51, types: [com.nuance.connect.util.Logger$Log] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.BufferedInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r9v20 */
    /* JADX WARN: Type inference failed for: r9v29 */
    /* JADX WARN: Type inference failed for: r9v30 */
    /* JADX WARN: Type inference failed for: r9v34, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r9v38 */
    /* JADX WARN: Type inference failed for: r9v46 */
    /* JADX WARN: Type inference failed for: r9v47 */
    /* JADX WARN: Type inference failed for: r9v48 */
    /* JADX WARN: Type inference failed for: r9v6, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v7 */
    @Override // com.nuance.connect.comm.Connector
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean processCommand(com.nuance.connect.comm.Command r31, com.nuance.connect.comm.Transaction r32, com.nuance.connect.comm.CommandQueue.NetworkExpirer r33) throws com.nuance.connect.comm.ConnectorException {
        /*
            Method dump skipped, instructions count: 2572
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.comm.HttpConnector.processCommand(com.nuance.connect.comm.Command, com.nuance.connect.comm.Transaction, com.nuance.connect.comm.CommandQueue$NetworkExpirer):boolean");
    }

    public void setServerURL(String str) {
        this.serverURL = str;
    }

    public synchronized void updateMinimumSSLProtocol(String str) {
        this.log.d("updateMinimumSSLProtocol: ", str);
        this.socketFactory = CustomProtocolSocketFactory.createSocketFactory(str);
    }
}
