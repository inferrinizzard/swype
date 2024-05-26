package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.nlsmlResults;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM;

/* loaded from: classes.dex */
public class NLSMLParserAndroid extends XMLParserAndroidOEM {
    public NLSMLParserAndroid(byte[] bArr) {
        super(bArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM
    public XMLParserAndroidOEM.ResultsHandler getResultHandler() {
        return new NLSMLResultsHandler();
    }
}
