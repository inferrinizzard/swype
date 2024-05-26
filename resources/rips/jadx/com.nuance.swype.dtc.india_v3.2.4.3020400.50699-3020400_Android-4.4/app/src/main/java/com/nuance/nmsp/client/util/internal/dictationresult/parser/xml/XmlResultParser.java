package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.ResultParser;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.nlsmlResults.NLSMLParserAndroid;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLParserAndroid;
import java.io.IOException;

/* loaded from: classes.dex */
public class XmlResultParser implements ResultParser {
    private static final LogFactory.Log a = LogFactory.getLog(XmlResultParser.class);
    private byte[] b;

    public XmlResultParser(byte[] bArr) {
        this.b = bArr;
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.ResultParser
    public DictationResult parse() {
        a.debug("Unpacking XML dictation results.");
        try {
            XMLParserAndroid xMLParserAndroid = new XMLParserAndroid(this.b);
            xMLParserAndroid.parse();
            if (xMLParserAndroid.resultIsValid()) {
                return xMLParserAndroid.getDictationResult();
            }
            if (a.isDebugEnabled()) {
                a.debug("Could not parse XML dictation results: " + xMLParserAndroid.getErrorMsg() + ". Trying to parse NLSML results.");
            }
            NLSMLParserAndroid nLSMLParserAndroid = new NLSMLParserAndroid(this.b);
            nLSMLParserAndroid.parse();
            if (nLSMLParserAndroid.resultIsValid()) {
                return nLSMLParserAndroid.getDictationResult();
            }
            String str = "Could not parse XML neither NLSML dictation results. Error from XML Parser: " + xMLParserAndroid.getErrorMsg() + ". Error from NLSML Parser: " + nLSMLParserAndroid.getErrorMsg();
            if (a.isErrorEnabled()) {
                a.error(str);
            }
            throw new IllegalArgumentException(str);
        } catch (IOException e) {
            if (a.isErrorEnabled()) {
                a.error("Received IOException while parsing XML/NLSML.", e);
            }
            throw new IllegalArgumentException("Received IOException while parsing XML/NLSML.", e);
        }
    }
}
