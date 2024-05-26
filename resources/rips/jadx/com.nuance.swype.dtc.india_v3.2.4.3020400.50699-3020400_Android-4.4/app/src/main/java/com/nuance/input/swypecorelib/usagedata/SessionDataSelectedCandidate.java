package com.nuance.input.swypecorelib.usagedata;

import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;

/* loaded from: classes.dex */
public class SessionDataSelectedCandidate {
    public boolean autoAccepted;
    public Candidates.Source candidatesSource;
    public WordCandidate defaultCandidate;
    public WordCandidate exactCandidate;
    public final WordCandidate selectedCandidate;

    public SessionDataSelectedCandidate(WordCandidate selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public WordCandidate getSelectedCandidate() {
        return this.selectedCandidate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WordCandidate getDefaultCandidate() {
        return this.defaultCandidate;
    }

    public WordCandidate getExactCandidate() {
        return this.exactCandidate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAutoAccepted() {
        return this.autoAccepted;
    }

    public Candidates.Source getCandidatesSource() {
        return this.candidatesSource;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        buffer.append(getSelectedCandidate().id()).append(',');
        buffer.append(isAutoAccepted()).append(',');
        buffer.append(getCandidatesSource()).append(',');
        buffer.append(getSelectedCandidate().toString()).append(',');
        buffer.append(getDefaultCandidate().toString()).append(',');
        buffer.append(getExactCandidate().toString());
        buffer.append(']');
        return buffer.toString();
    }
}
