package com.nuance.nmsp.client.util.internal.dictationresult;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.dictationresult.Alternative;
import com.nuance.nmsp.client.util.dictationresult.Token;
import java.util.Vector;

/* loaded from: classes.dex */
public class AlternativeImpl implements Alternative {
    private static final LogFactory.Log a = LogFactory.getLog(AlternativeImpl.class);
    private Vector b;
    private Vector c;

    public AlternativeImpl() {
        if (a.isDebugEnabled()) {
            a.debug("AlternativeImpl()");
        }
        this.b = new Vector();
    }

    public AlternativeImpl(Vector vector) {
        if (a.isDebugEnabled()) {
            a.debug("AlternativeImpl(Vector)");
        }
        this.b = vector;
    }

    private TokenImpl a(int i) {
        return (TokenImpl) this.b.elementAt(i);
    }

    public void addToken(TokenImpl tokenImpl) {
        if (a.isDebugEnabled()) {
            a.debug("Adding token " + tokenImpl + " to alternatives");
        }
        this.b.addElement(tokenImpl);
    }

    public Vector getParentTokens() {
        return this.c;
    }

    public Vector getTokens() {
        return this.b;
    }

    public void setParentTokens(Vector vector) {
        this.c = vector;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternative
    public int size() {
        int i = 0;
        int size = this.b.size();
        if (size != 0) {
            for (int i2 = 0; i2 < size; i2++) {
                if (!a(i2).isWhiteSpace()) {
                    i++;
                }
            }
        }
        return i;
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternative
    public String toString() {
        if (this.b.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < this.b.size(); i++) {
            stringBuffer.append(this.b.elementAt(i));
        }
        return stringBuffer.toString();
    }

    @Override // com.nuance.nmsp.client.util.dictationresult.Alternative
    public Token tokenAt(int i) {
        int i2 = 0;
        int i3 = -1;
        int i4 = -1;
        while (i3 != i) {
            int i5 = i4 + 1;
            if (i5 > this.b.size()) {
                return null;
            }
            if (a(i5).isWhiteSpace()) {
                i4 = i5;
            } else {
                i3++;
                i4 = i5;
            }
        }
        TokenImpl a2 = a(i4);
        a2.setLeadingSpaces((i4 + 1 >= this.b.size() || !a(i4 + 1).isWhiteSpace()) ? 0 : a(i4 + 1).toString().length());
        if (i4 != 0 && a(i4 - 1).isWhiteSpace()) {
            i2 = a(i4 - 1).toString().length();
        }
        a2.setTrailingSpaces(i2);
        return a2;
    }

    public String wordAt(int i) {
        if (a.isDebugEnabled()) {
            a.debug("Fetching alternative at " + i);
        }
        TokenImpl tokenImpl = (TokenImpl) this.b.elementAt(i);
        if (a.isDebugEnabled()) {
            a.debug("Found " + tokenImpl);
        }
        return tokenImpl.toString();
    }
}
