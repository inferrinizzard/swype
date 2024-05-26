package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.PdxParamUtils;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import java.util.LinkedList;

/* loaded from: classes.dex */
final class GenericRecognitionParser implements IPdxParser<GenericRecognition> {
    private LinkedList<GenericRecognition> _recognitions = new LinkedList<>();
    private boolean _expectMore = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GenericRecognitionImpl implements GenericRecognition {
        private final PdxValue.Dictionary _appserverDict;
        private final boolean _final;
        private final PdxValue.Dictionary _fullDict;

        GenericRecognitionImpl(PdxValue.Dictionary dict) {
            this._fullDict = dict;
            boolean isFinal = true;
            PdxValue f = dict == null ? null : dict.get("final_response");
            if (f != null && f.getType() == 1 && ((PdxValue.Integer) f).get() == 0) {
                isFinal = false;
            }
            PdxValue appserverResults = dict.get("appserver_results");
            if (appserverResults != null && appserverResults.getType() == 2) {
                this._appserverDict = (PdxValue.Dictionary) appserverResults;
            } else {
                this._appserverDict = null;
            }
            this._final = isFinal;
        }

        @Override // com.nuance.nmdp.speechkit.GenericRecognition
        public PdxValue.Dictionary getFullResult() {
            return this._fullDict;
        }

        @Override // com.nuance.nmdp.speechkit.GenericRecognition
        public PdxValue.Dictionary getAppserverResult() {
            return this._appserverDict;
        }

        @Override // com.nuance.nmdp.speechkit.GenericRecognition
        public boolean isFinalResult() {
            return this._final;
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
    public final boolean parse(QueryResult result) {
        try {
            PdxValue.Dictionary pdxDict = PdxParamUtils.createFromPdx(result);
            GenericRecognitionImpl parsed = new GenericRecognitionImpl(pdxDict);
            this._expectMore = !parsed.isFinalResult();
            this._recognitions.add(parsed);
            return true;
        } catch (Throwable th) {
            Logger.error(this, "Error parsing result");
            return false;
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
    public final GenericRecognition getParsed() {
        if (this._recognitions.isEmpty()) {
            return null;
        }
        return this._recognitions.remove();
    }

    @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
    public final boolean expectMore() {
        return this._expectMore;
    }
}
