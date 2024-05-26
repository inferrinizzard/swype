package com.nuance.input.swypecorelib;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Correction extends TextSpan {
    private List<String> suggestions;

    public Correction(int relativeOffset, int len) {
        super(relativeOffset, len);
        this.suggestions = new ArrayList();
    }

    public int getNumSuggestions() {
        return this.suggestions.size();
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    public String getSuggestion(int index) {
        if (index >= this.suggestions.size()) {
            return null;
        }
        return this.suggestions.get(index);
    }

    public String toString() {
        return "offset=" + getOffset() + ", len=" + getLength() + ", numSugg=" + getNumSuggestions();
    }

    @Override // com.nuance.input.swypecorelib.TextSpan
    public boolean doesContain(int pos) {
        return pos >= getOffset() && pos <= getOffset() + getLength();
    }
}
