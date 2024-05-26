package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.internal.dictationresult.DictationResultImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.SentenceImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.TokenImpl;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: classes.dex */
public class XMLResultsHandler extends DefaultHandler implements XMLParserAndroidOEM.ResultsHandler {
    public static final String ATTR_CONFIDENCE = "confidence";
    public static final String ATTR_GRAMMAR = "grammar";
    public static final String ATTR_MODE = "mode";
    public static final String ATTR_TIMING = "timing";
    public static final String ATTR_WORDS_CONFIDENCE = "word_confidence";
    public static final String ELEM_AUDIO_STATS = "NSS_Audio_Statistics";
    public static final String ELEM_INPUT = "input";
    public static final String ELEM_INTERPRETATION = "interpretation";
    public static final String ELEM_RESULT = "result";
    public static final String SEP_COMMA = ",";
    public static final String SEP_HYPHEN = "-";
    public static final String SEP_SPACE = " ";
    private static final LogFactory.Log a = LogFactory.getLog(XMLResultsHandler.class);
    private Stack b = new Stack();
    private StringBuffer c = null;
    private StringBuffer d = null;
    private String e = null;
    private String f = null;
    private double g = 0.0d;
    private Vector h = new Vector();
    private Hashtable i = new Hashtable();

    private void a() throws SAXException {
        if (this.b.isEmpty() || this.b.peek() != ELEM_INPUT) {
            throw new SAXException("End Element> The top of the stack does not contain the token interpretation");
        }
        if (this.e == null) {
            throw new SAXException("Did not get any timings from input");
        }
        if (this.c == null) {
            throw new SAXException("Did not get any character from input");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(this.c.toString(), SEP_SPACE);
        StringTokenizer stringTokenizer2 = new StringTokenizer(this.e, ",");
        StringTokenizer stringTokenizer3 = this.f != null ? new StringTokenizer(this.f, ",") : null;
        this.c = null;
        this.e = null;
        this.f = null;
        if (stringTokenizer.countTokens() != stringTokenizer2.countTokens()) {
            throw new SAXException("timing(" + stringTokenizer2.countTokens() + ") and inputs(" + stringTokenizer.countTokens() + ") information does not have the same number of items");
        }
        SentenceImpl sentenceImpl = new SentenceImpl();
        sentenceImpl.setConfidenceScore(this.g);
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            String nextToken2 = stringTokenizer2.nextToken();
            double d = 0.0d;
            if (stringTokenizer3 != null) {
                d = Double.parseDouble(stringTokenizer3.nextToken());
            }
            sentenceImpl.appendTokenToSentence(new TokenImpl(nextToken, Long.parseLong(nextToken2.substring(0, nextToken2.indexOf(SEP_HYPHEN))), Long.parseLong(nextToken2.substring(nextToken2.indexOf(SEP_HYPHEN) + 1)), d, true));
        }
        a(sentenceImpl);
    }

    private void a(SentenceImpl sentenceImpl) {
        if (!this.h.isEmpty()) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.h.size()) {
                    break;
                }
                if (((SentenceImpl) this.h.elementAt(i2)).getConfidenceScore() < sentenceImpl.getConfidenceScore()) {
                    this.h.insertElementAt(sentenceImpl, i2);
                    return;
                }
                i = i2 + 1;
            }
        }
        this.h.addElement(sentenceImpl);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) throws SAXException {
        if (a.isTraceEnabled()) {
            a.trace("Received characters: " + new String(cArr, i, i2));
        }
        if (!this.b.isEmpty() && this.b.peek() == ELEM_INPUT) {
            if (this.c == null) {
                this.c = new StringBuffer(i2);
            }
            this.c.append(cArr, i, i2);
        } else {
            if (this.b.isEmpty() || this.b.peek() != ELEM_AUDIO_STATS) {
                return;
            }
            if (this.d == null) {
                this.d = new StringBuffer(i2);
            }
            this.d.append(cArr, i, i2);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) throws SAXException {
        if (a.isTraceEnabled()) {
            a.trace("Received endElement " + str2);
        }
        if (str2.equals(ELEM_RESULT)) {
            if (this.b.isEmpty() || this.b.peek() != ELEM_RESULT) {
                throw new SAXException("End Element> The top of the stack does not contain the token result");
            }
            this.b.pop();
            return;
        }
        if (str2.equals(ELEM_INTERPRETATION)) {
            if (this.b.isEmpty() || this.b.peek() != ELEM_INTERPRETATION) {
                throw new SAXException("End Element> The top of the stack does not contain the token interpretation");
            }
            this.b.pop();
            return;
        }
        if (str2.equals(ELEM_INPUT)) {
            a();
            this.b.pop();
            return;
        }
        if (str2.equals(ELEM_AUDIO_STATS)) {
            if (this.b.isEmpty() || this.b.peek() != ELEM_AUDIO_STATS) {
                throw new SAXException("End Element> The top of the stack does not contain the token NSS_Audio_Statistics");
            }
            this.b.pop();
            return;
        }
        if (this.b.isEmpty() || this.b.peek() != ELEM_AUDIO_STATS) {
            return;
        }
        String trim = this.d == null ? "" : this.d.toString().trim();
        if (str2.equals("InputAudioLength")) {
            this.i.put("IAL", trim);
        }
        this.i.put(str2, trim);
        this.d = null;
    }

    @Override // com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.XMLParserAndroidOEM.ResultsHandler
    public DictationResult getDictationResult() {
        return new DictationResultImpl(this.h, this.i);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
        if (a.isTraceEnabled()) {
            a.trace("Received startElement " + str2);
        }
        if (str2.equals(ELEM_RESULT)) {
            if (this.b.size() != 0) {
                throw new SAXException("StartElement> Found result but it is not the first token.");
            }
            this.b.push(ELEM_RESULT);
            return;
        }
        if (!str2.equals(ELEM_INTERPRETATION)) {
            if (str2.equals(ELEM_INPUT)) {
                if (this.b.isEmpty() || this.b.peek() != ELEM_INTERPRETATION) {
                    throw new SAXException("StartElement> The input token was found without a interpretation being opened before.");
                }
                this.b.push(ELEM_INPUT);
                return;
            }
            if (str2.equals(ELEM_AUDIO_STATS)) {
                if (this.b.isEmpty() || this.b.peek() != ELEM_RESULT) {
                    throw new SAXException("StartElement> The NSS_Audio_Statistics token was found without a result being opened before.");
                }
                this.b.push(ELEM_AUDIO_STATS);
                return;
            }
            return;
        }
        if (this.b.isEmpty() || this.b.peek() != ELEM_RESULT) {
            throw new SAXException("StartElement> The interpretation token was found without a result being opened before.");
        }
        this.b.push(ELEM_INTERPRETATION);
        String value = attributes.getValue(ATTR_TIMING);
        String value2 = attributes.getValue(ATTR_CONFIDENCE);
        String value3 = attributes.getValue(ATTR_WORDS_CONFIDENCE);
        if (value == null) {
            throw new SAXException("StartElement> There are no timings associated with this interpretation.");
        }
        if (value2 == null) {
            throw new SAXException("StartElement> There is no confidence associated with this interpretation.");
        }
        this.e = value;
        this.f = value3;
        try {
            this.g = Double.parseDouble(value2);
        } catch (NumberFormatException e) {
            throw new SAXException("StartElement> Could not parse the confidence: " + value2, e);
        }
    }
}
