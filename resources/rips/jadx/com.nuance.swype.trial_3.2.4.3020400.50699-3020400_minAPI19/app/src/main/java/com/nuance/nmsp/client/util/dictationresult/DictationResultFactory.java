package com.nuance.nmsp.client.util.dictationresult;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.ResultParser;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.binary.DnsBinV1ResultParser;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XmlResultParser;
import com.nuance.nmsp.client.util.internal.dictationresult.util.Util;

/* loaded from: classes.dex */
public class DictationResultFactory {
    private static final LogFactory.Log a = LogFactory.getLog(DictationResultFactory.class);

    private DictationResultFactory() {
    }

    public static DictationResult createDictationResult(byte[] bArr) throws IllegalArgumentException {
        ResultParser dnsBinV1ResultParser;
        if (a.isDebugEnabled()) {
            a.debug("DictationResultImpl(buffer [size: " + bArr.length + "] )");
            if (a.isTraceEnabled()) {
                a.traceBuffer(bArr);
            }
        }
        if (bArr == null || bArr.length < 4) {
            if (a.isErrorEnabled()) {
                a.error("Cannot parse dictation results: The buffer length is too small to be containing any results.");
            }
            throw new IllegalArgumentException("Cannot parse dictation results: The buffer length is too small to be containing any results.");
        }
        try {
            if (bArr.length > 20) {
                String constructByteEncodedString = Util.constructByteEncodedString(bArr, 0, 20, "ISO-8859-1");
                if (constructByteEncodedString.startsWith("<?xml") || (constructByteEncodedString.indexOf("<?") >= 0 && constructByteEncodedString.indexOf("xml") >= 0)) {
                    if (a.isDebugEnabled()) {
                        a.debug("Detected xml results. Using xml parser.");
                    }
                    dnsBinV1ResultParser = new XmlResultParser(bArr);
                    return dnsBinV1ResultParser.parse();
                }
            }
            if (a.isDebugEnabled()) {
                a.debug("Detected binary results. Using binary parser.");
            }
            dnsBinV1ResultParser = new DnsBinV1ResultParser(bArr);
            return dnsBinV1ResultParser.parse();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse dictation results: illegal format buffer.");
        }
    }
}
