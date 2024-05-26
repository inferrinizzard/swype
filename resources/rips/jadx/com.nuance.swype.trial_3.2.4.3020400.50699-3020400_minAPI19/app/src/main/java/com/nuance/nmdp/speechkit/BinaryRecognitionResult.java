package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.recognitionresult.DetailedResult;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.dictationresult.DictationResultFactory;
import java.util.List;

/* loaded from: classes.dex */
public class BinaryRecognitionResult implements Recognition {
    private DictationResult _binResult;
    private boolean _isFinalResponse;
    private String[] _results = null;
    private int[] _scores = null;
    private String _suggestion = null;

    @Override // com.nuance.nmdp.speechkit.Recognition
    public boolean isFinalResponse() {
        return this._isFinalResponse;
    }

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

    public DictationResult results() {
        return this._binResult;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public String getSuggestion() {
        return this._suggestion;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public int getResultCount() {
        return this._scores.length;
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public Recognition.Result getResult(int index) {
        if (index < 0 || index >= this._scores.length) {
            throw new IndexOutOfBoundsException("index must be >= 0 and < getResultCount().");
        }
        return new ResultImpl(this._results[index], this._scores[index]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IPdxParser<Recognition> getParser() {
        return new IPdxParser<Recognition>() { // from class: com.nuance.nmdp.speechkit.BinaryRecognitionResult.1
            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean parse(QueryResult result) {
                if (!result.containsKey("transcription")) {
                    BinaryRecognitionResult.this._binResult = null;
                } else {
                    byte[] data = result.getByteString("transcription");
                    BinaryRecognitionResult.this._binResult = DictationResultFactory.createDictationResult(data);
                }
                BinaryRecognitionResult.this._isFinalResponse = result.isFinalResponse();
                return true;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public Recognition getParsed() {
                return BinaryRecognitionResult.this;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean expectMore() {
                return !BinaryRecognitionResult.this._isFinalResponse;
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.Recognition
    public List<DetailedResult> getDetailedResults() {
        return null;
    }
}
