package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.recognitionresult.DetailedResult;
import com.nuance.nmdp.speechkit.recognitionresult.DetailedResultFactory;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RecognitionImpl implements Recognition {
    private List<DetailedResult> _detailedResults;
    private boolean _isFinalResponse;
    private String[] _results = null;
    private int[] _scores = null;
    private String _suggestion = null;

    /* loaded from: classes.dex */
    class ResultImpl implements Recognition.Result {
        private final int _score;
        private final String _text;

        ResultImpl(String text, int score) {
            this._text = text;
            this._score = score;
        }

        @Override // com.nuance.nmdp.speechkit.Recognition.Result
        public int getScore() {
            return this._score;
        }

        @Override // com.nuance.nmdp.speechkit.Recognition.Result
        public String getText() {
            return this._text;
        }
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public final String getSuggestion() {
        return this._suggestion;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public final int getResultCount() {
        return this._scores.length;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public final Recognition.Result getResult(int index) {
        if (index < 0 || index >= this._scores.length) {
            throw new IndexOutOfBoundsException("index must be >= 0 and < getResultCount().");
        }
        return new ResultImpl(this._results[index], this._scores[index]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<Recognition> getParser() {
        return new IPdxParser<Recognition>() { // from class: com.nuance.nmdp.speechkit.RecognitionImpl.1
            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean parse(QueryResult result) {
                boolean transcription_found = true;
                if (result.containsKey("transcription")) {
                    byte[] data = result.getByteString("transcription");
                    RecognitionImpl.this._detailedResults = DetailedResultFactory.createDetailedResults(data);
                    String[] results = new String[RecognitionImpl.this._detailedResults.size()];
                    int[] scores = new int[RecognitionImpl.this._detailedResults.size()];
                    for (int i = 0; i < RecognitionImpl.this._detailedResults.size(); i++) {
                        results[i] = ((DetailedResult) RecognitionImpl.this._detailedResults.get(i)).toString();
                        scores[i] = (int) ((DetailedResult) RecognitionImpl.this._detailedResults.get(i)).getConfidenceScore();
                    }
                    RecognitionImpl.this._results = results;
                    RecognitionImpl.this._scores = scores;
                } else {
                    transcription_found = false;
                    RecognitionImpl.this._results = new String[0];
                    RecognitionImpl.this._scores = new int[0];
                    RecognitionImpl.this._suggestion = null;
                    if (RecognitionImpl.this._detailedResults != null) {
                        RecognitionImpl.this._detailedResults.clear();
                    }
                }
                if (result.containsKey("prompt")) {
                    String prompt = result.getUTF8String("prompt");
                    RecognitionImpl.this._suggestion = prompt;
                }
                RecognitionImpl.this._isFinalResponse = result.isFinalResponse();
                if (!RecognitionImpl.this._isFinalResponse && !transcription_found) {
                    RecognitionImpl.this._results = new String[0];
                    RecognitionImpl.this._scores = new int[0];
                    RecognitionImpl.this._suggestion = null;
                    if (RecognitionImpl.this._detailedResults != null) {
                        RecognitionImpl.this._detailedResults.clear();
                    }
                    Logger.warn(this, "Unable to extract transcriptions from result");
                    return true;
                }
                return true;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public Recognition getParsed() {
                return RecognitionImpl.this;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean expectMore() {
                return !RecognitionImpl.this._isFinalResponse;
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public final List<DetailedResult> getDetailedResults() {
        return this._detailedResults;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public final boolean isFinalResponse() {
        return this._isFinalResponse;
    }
}
