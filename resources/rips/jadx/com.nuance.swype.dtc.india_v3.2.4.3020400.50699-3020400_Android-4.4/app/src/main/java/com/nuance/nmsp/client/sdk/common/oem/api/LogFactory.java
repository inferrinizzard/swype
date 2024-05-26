package com.nuance.nmsp.client.sdk.common.oem.api;

import com.nuance.connect.comm.MessageAPI;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.oem.LogFactoryOEM;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class LogFactory {

    /* loaded from: classes.dex */
    public static abstract class Log {
        private static String[] a = {"0", "1", MessageAPI.DELAYED_FROM, MessageAPI.SESSION_ID, MessageAPI.TRANSACTION_ID, MessageAPI.DEVICE_ID, "6", MessageAPI.MESSAGE, "8", MessageAPI.PROPERTIES_TO_VALIDATE, "A", "B", "C", "D", "E", "F"};

        public static String FormatUuid(byte[] bArr) {
            if (bArr == null) {
                return "";
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < 16; i++) {
                stringBuffer.append(a(bArr[i]));
                if (i == 3 || i == 5 || i == 7 || i == 9) {
                    stringBuffer.append(XMLResultsHandler.SEP_HYPHEN);
                }
            }
            return stringBuffer.toString().toLowerCase();
        }

        private static String a(byte b) {
            return a[(byte) (((byte) (((byte) (b & 240)) >>> 4)) & ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE)] + a[(byte) (b & ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE)];
        }

        public static String[] bufferStr(byte[] bArr) {
            String str;
            int length = bArr.length / 8;
            if (bArr.length % 8 != 0) {
                length++;
            }
            String[] strArr = new String[length];
            try {
                str = new String(bArr, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                str = new String(bArr);
            }
            int i = 0;
            int i2 = 0;
            while (i < length) {
                char[] cArr = new char[41];
                for (int i3 = 0; i3 < 41; i3++) {
                    cArr[i3] = ' ';
                }
                int i4 = i2;
                for (int i5 = 0; i5 < 8 && i4 < bArr.length; i5++) {
                    String a2 = a(bArr[i4]);
                    cArr[i5 * 3] = a2.charAt(0);
                    cArr[(i5 * 3) + 1] = a2.charAt(1);
                    cArr[(i5 << 1) + 26] = str.charAt(i4);
                    i4++;
                }
                strArr[i] = new String(cArr);
                i++;
                i2 = i4;
            }
            return strArr;
        }

        public abstract void debug(Object obj);

        public abstract void debug(Object obj, Throwable th);

        public abstract void error(Object obj);

        public abstract void error(Object obj, Throwable th);

        public abstract void fatal(Object obj);

        public abstract void fatal(Object obj, Throwable th);

        public abstract void info(Object obj);

        public abstract void info(Object obj, Throwable th);

        public abstract boolean isDebugEnabled();

        public abstract boolean isErrorEnabled();

        public abstract boolean isFatalEnabled();

        public abstract boolean isInfoEnabled();

        public abstract boolean isTraceEnabled();

        public abstract boolean isWarnEnabled();

        public abstract void setCurrentSession(String str);

        public abstract void trace(Object obj);

        public abstract void trace(Object obj, Throwable th);

        public void traceBuffer(byte[] bArr) {
            if (isTraceEnabled()) {
                trace("Buffer dump:");
                for (String str : bufferStr(bArr)) {
                    trace(str);
                }
            }
        }

        public abstract void unsetCurrentSession();

        public abstract void warn(Object obj);

        public abstract void warn(Object obj, Throwable th);
    }

    public static Log getLog(Class cls) {
        return LogFactoryOEM.getLog(cls);
    }
}
