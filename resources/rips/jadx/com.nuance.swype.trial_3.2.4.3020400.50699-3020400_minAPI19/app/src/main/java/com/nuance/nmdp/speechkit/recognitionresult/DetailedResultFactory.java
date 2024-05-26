package com.nuance.nmdp.speechkit.recognitionresult;

import com.nuance.nmsp.client.util.dictationresult.DictationResult;
import com.nuance.nmsp.client.util.dictationresult.DictationResultFactory;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class DetailedResultFactory {
    public static List<DetailedResult> createDetailedResults(byte[] buffer) throws IllegalArgumentException {
        if (buffer == null || buffer.length < 4) {
            throw new IllegalArgumentException("Cannot parse results: The buffer length is too small to be containing any results.");
        }
        DictationResult dictRes = DictationResultFactory.createDictationResult(buffer);
        List<DetailedResult> detailedResults = new ArrayList<>();
        for (int i = 0; i < dictRes.size(); i++) {
            detailedResults.add(new DetailedResultImpl(dictRes.sentenceAt(i)));
        }
        return detailedResults;
    }
}
