package com.nuance.swype.input;

import com.nuance.input.swypecorelib.Candidates;
import com.nuance.swype.util.LogManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class OOVWordHistory {
    LogManager.Log log = LogManager.getLog(OOVWordHistory.class.getSimpleName());
    private final Set<String> oovWords = new HashSet();

    public void clear() {
        this.oovWords.clear();
    }

    public void add(String word) {
    }

    public void overrideDefault(Candidates candidates) {
        if (candidates.source().equals(Candidates.Source.TAP)) {
            String cand = candidates.getExactCandidateString().toString().toLowerCase();
            if (this.oovWords.contains(cand)) {
                this.log.d("overrideDefault(): setting default to exact to ", cand);
                candidates.setDefaultIndex(candidates.getExactCandidateIndex());
            }
        }
    }
}
