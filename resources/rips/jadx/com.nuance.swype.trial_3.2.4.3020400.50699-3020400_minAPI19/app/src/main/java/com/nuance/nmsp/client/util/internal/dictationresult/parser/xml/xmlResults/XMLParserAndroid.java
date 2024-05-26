package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM;

/* loaded from: classes.dex */
public class XMLParserAndroid extends XMLParserAndroidOEM {
    public XMLParserAndroid(byte[] bArr) {
        super(bArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM
    public XMLParserAndroidOEM.ResultsHandler getResultHandler() {
        return new XMLResultsHandler();
    }
}
