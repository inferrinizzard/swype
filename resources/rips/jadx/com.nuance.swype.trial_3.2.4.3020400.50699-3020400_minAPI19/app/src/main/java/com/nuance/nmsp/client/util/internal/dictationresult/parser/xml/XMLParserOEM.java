package com.nuance.nmsp.client.util.internal.dictationresult.parser.xml;

import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import java.io.IOException;

/* loaded from: classes.dex */
public interface XMLParserOEM {
    DictationResult getDictationResult();

    String getErrorMsg();

    void parse() throws IOException;

    boolean resultIsValid();
}
