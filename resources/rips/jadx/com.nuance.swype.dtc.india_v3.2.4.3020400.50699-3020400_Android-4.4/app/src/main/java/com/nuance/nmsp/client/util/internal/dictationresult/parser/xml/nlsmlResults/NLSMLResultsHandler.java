package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.nlsmlResults;

import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: classes.dex */
public class NLSMLResultsHandler extends DefaultHandler implements XMLParserAndroidOEM.ResultsHandler {
    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM.ResultsHandler
    public DictationResult getDictationResult() {
        return null;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
        throw new SAXException("Not implemented");
    }
}
