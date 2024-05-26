package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class GenericResultImpl implements GenericResult {
    private String _status;

    @Override // com.nuance.nmdp.speechkit.GenericResult
    public final String getQueryResult() {
        return this._status;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<GenericResult> getParser() {
        return new IPdxParser<GenericResult>() { // from class: com.nuance.nmdp.speechkit.GenericResultImpl.1
            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean parse(QueryResult result) {
                GenericResultImpl.this._status = result.getUTF8String("status");
                return GenericResultImpl.this._status != null;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean expectMore() {
                return false;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public GenericResult getParsed() {
                return GenericResultImpl.this;
            }
        };
    }
}
