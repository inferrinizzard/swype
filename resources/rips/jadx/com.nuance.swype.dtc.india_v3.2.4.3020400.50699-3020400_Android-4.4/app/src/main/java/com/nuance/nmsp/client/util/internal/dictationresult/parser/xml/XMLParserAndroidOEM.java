package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml;

import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/* loaded from: classes.dex */
public abstract class XMLParserAndroidOEM implements XMLParserOEM {
    private byte[] a;
    private DictationResult b;
    private String c = "";

    /* loaded from: classes.dex */
    public interface ResultsHandler extends ContentHandler {
        DictationResult getDictationResult();
    }

    public XMLParserAndroidOEM(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        this.a = bArr;
    }

    private static boolean a(byte[] bArr) {
        try {
            try {
                new InputStreamReader(new ByteArrayInputStream(bArr), "UTF8").close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (Exception e2) {
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return false;
        } catch (Throwable th) {
            InputStreamReader inputStreamReader2 = null;
            try {
                inputStreamReader2.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserOEM
    public final DictationResult getDictationResult() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserOEM
    public final String getErrorMsg() {
        return this.c;
    }

    public abstract ResultsHandler getResultHandler();

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserOEM
    public final void parse() throws IOException {
        try {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            InputSource inputSource = new InputSource(new ByteArrayInputStream(this.a));
            if (!a(this.a)) {
                inputSource.setEncoding("cp1252");
            }
            XMLReader createXMLReader = XMLReaderFactory.createXMLReader();
            ResultsHandler resultHandler = getResultHandler();
            createXMLReader.setContentHandler(resultHandler);
            createXMLReader.parse(inputSource);
            this.b = resultHandler.getDictationResult();
        } catch (SAXException e) {
            this.c = e.getMessage();
        }
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserOEM
    public final boolean resultIsValid() {
        return this.b != null;
    }
}
