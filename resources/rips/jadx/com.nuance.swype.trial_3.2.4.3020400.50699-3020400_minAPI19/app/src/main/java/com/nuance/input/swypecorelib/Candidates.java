package com.nuance.input.swypecorelib;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Candidates {
    public static final int ATTR_FILL_SCREEN = 1;
    public static final int ATTR_JOIN_CANDIDATES = 2;
    private int attrs;
    public int mDefaultIndex;
    public int mExactIndex;
    public int mSmartSelectionIndex;
    private Source mSource;
    private List<WordCandidate> mWordCandidates;

    /* loaded from: classes.dex */
    public enum Source {
        TRACE,
        TAP,
        RECAPTURE,
        RECAPTURE_BY_TEXT_SELECTION,
        NEXT_WORD_PREDICTION,
        HWR,
        SPEECH_ALTERNATES,
        COMPLETIONS,
        INVALID,
        CAPS_EDIT,
        TOOL_TIP,
        UDB_EDIT,
        EMOJEENIE
    }

    public Candidates(Source source) {
        this.mSource = source;
        this.mDefaultIndex = 0;
        this.mSmartSelectionIndex = -1;
        this.mExactIndex = 0;
        this.mWordCandidates = new ArrayList();
    }

    public Candidates(List<WordCandidate> candidates, Source source, int defaultIndex, int exactIndex) {
        this.mSource = source;
        this.mDefaultIndex = defaultIndex;
        this.mSmartSelectionIndex = -1;
        this.mExactIndex = exactIndex;
        this.mWordCandidates = new ArrayList(candidates);
    }

    public Candidates(List<WordCandidate> candidates, Source source) {
        this.mWordCandidates = candidates == null ? new ArrayList<>() : candidates;
        this.mSource = source;
        this.mDefaultIndex = -1;
        this.mSmartSelectionIndex = -1;
        this.mExactIndex = -1;
        for (int i = 0; i < this.mWordCandidates.size(); i++) {
            WordCandidate word = this.mWordCandidates.get(i);
            if (word.isDefault()) {
                this.mDefaultIndex = i;
            }
            if (word.isExact()) {
                this.mExactIndex = i;
            }
            if (word.isSmartSuggestion()) {
                this.mSmartSelectionIndex = i;
            }
            if (this.mDefaultIndex != -1 && this.mExactIndex != -1 && this.mSmartSelectionIndex != -1) {
                break;
            }
        }
        if (this.mExactIndex == -1) {
            this.mExactIndex = 0;
        }
        if (this.mDefaultIndex == -1) {
            this.mDefaultIndex = 0;
        }
        if (this.mDefaultIndex > 0 && source == Source.TAP && isAllUpperCaseFirstWord()) {
            this.mDefaultIndex = 0;
        }
    }

    public Candidates(List<CharSequence> candidates, Source source, int defaultIndex) {
        this.mSource = source;
        this.mDefaultIndex = defaultIndex;
        this.mSmartSelectionIndex = defaultIndex;
        this.mExactIndex = defaultIndex;
        this.mWordCandidates = new ArrayList(candidates.size());
        for (int i = 0; i < candidates.size(); i++) {
            this.mWordCandidates.add(new WordCandidate(candidates.get(i).toString(), 0, 0, i));
        }
    }

    public Candidates(Candidates candidates) {
        this.mExactIndex = candidates.mExactIndex;
        this.mDefaultIndex = candidates.mDefaultIndex;
        this.mSmartSelectionIndex = candidates.mSmartSelectionIndex;
        this.mSource = candidates.mSource;
        this.mWordCandidates = new ArrayList(candidates.count());
        this.mWordCandidates.addAll(candidates.mWordCandidates);
        this.attrs = candidates.attrs;
    }

    public boolean isExactADictionaryWord() {
        return this.mWordCandidates.get(this.mExactIndex).isExactADictionaryWord();
    }

    public void setDefaultIndex(int index) {
        this.mDefaultIndex = index;
    }

    public void setExactIndex(int index) {
        this.mExactIndex = index;
    }

    public static boolean match(Candidates cand, Source... states) {
        return cand != null && cand.match(states);
    }

    public boolean match(Source... types) {
        for (Source type : types) {
            if (source().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public Source source() {
        return this.mSource;
    }

    public void setSource(Source src) {
        this.mSource = src;
    }

    public String toString() {
        return "mInlineIndex = " + this.mExactIndex + " mDefaultIndex = " + this.mDefaultIndex + " : " + this.mWordCandidates;
    }

    public List<WordCandidate> getCandidates() {
        return this.mWordCandidates;
    }

    public int getExactCandidateIndex() {
        return this.mExactIndex;
    }

    public int getDefaultCandidateIndex() {
        return this.mDefaultIndex;
    }

    public int getSmartSelectionIndex() {
        return hasSmartSelectionIndex() ? this.mSmartSelectionIndex : getDefaultCandidateIndex();
    }

    public boolean hasSmartSelectionIndex() {
        return this.mSmartSelectionIndex >= 0;
    }

    public int count() {
        return this.mWordCandidates.size();
    }

    public CharSequence getExactCandidateString() {
        return this.mWordCandidates.size() <= 0 ? "" : this.mWordCandidates.get(this.mExactIndex).mWord;
    }

    public WordCandidate getExactCandidate() {
        if (this.mWordCandidates.size() <= 0) {
            return null;
        }
        return this.mWordCandidates.get(this.mExactIndex);
    }

    public CharSequence getDefaultCandidateString() {
        return this.mWordCandidates.size() <= 0 ? "" : this.mWordCandidates.get(this.mDefaultIndex).mWord;
    }

    public WordCandidate getDefaultCandidate() {
        if (this.mWordCandidates.size() <= 0) {
            return null;
        }
        return this.mWordCandidates.get(this.mDefaultIndex);
    }

    public WordCandidate get(int index) {
        if (this.mWordCandidates.size() <= 0) {
            return null;
        }
        return this.mWordCandidates.get(index);
    }

    public void clear() {
        this.mWordCandidates.clear();
    }

    public void remove(int location) {
        this.mWordCandidates.remove(location);
    }

    public boolean add(int location, WordCandidate candidate) {
        if (this.mWordCandidates.contains(candidate)) {
            return false;
        }
        this.mWordCandidates.add(location, candidate);
        return true;
    }

    public boolean add(WordCandidate candidate) {
        if (this.mWordCandidates.contains(candidate)) {
            return false;
        }
        this.mWordCandidates.add(candidate);
        return true;
    }

    public void addAll(int location, List<WordCandidate> candidates) {
        this.mWordCandidates.addAll(location, candidates);
    }

    public void addAll(List<WordCandidate> candidates) {
        this.mWordCandidates.addAll(candidates);
    }

    public List<WordCandidate> sublist(int start, int end) {
        if (this.mWordCandidates.size() <= 0) {
            return null;
        }
        return this.mWordCandidates.subList(start, end);
    }

    public CharSequence getCandidateString(int index) {
        return this.mWordCandidates.size() <= 0 ? "" : this.mWordCandidates.get(index).mWord;
    }

    public static Candidates createCandidates(List<CharSequence> suggestions, int defaultWordIndex, Source source) {
        return new Candidates(suggestions, source, 0);
    }

    public boolean hasAttribute(int attribute) {
        return (this.attrs & attribute) != 0;
    }

    public void addAttribute(int attribute) {
        this.attrs |= attribute;
    }

    public void removeAttribute(int attribute) {
        this.attrs &= attribute ^ (-1);
    }

    public boolean isReadOnly() {
        return this.mSource == Source.INVALID || this.mSource == Source.TOOL_TIP || this.mSource == Source.UDB_EDIT;
    }

    public boolean isUDBEditing() {
        return this.mSource == Source.UDB_EDIT;
    }

    public static List<WordCandidate> createCandidates(String[] suggestions, int[] packedFields) {
        List<WordCandidate> candidateList = new ArrayList<>(suggestions.length);
        int fieldIndex = 0;
        for (String word : suggestions) {
            String contextPredict = null;
            String[] wordPieces = word.split("[\u001f]");
            if (wordPieces.length > 1) {
                contextPredict = wordPieces[1];
            }
            int fieldIndex2 = fieldIndex + 1;
            int completionLength = packedFields[fieldIndex];
            int fieldIndex3 = fieldIndex2 + 1;
            int attr = packedFields[fieldIndex2];
            int fieldIndex4 = fieldIndex3 + 1;
            int id = packedFields[fieldIndex3];
            fieldIndex = fieldIndex4 + 1;
            int contextKillLen = packedFields[fieldIndex4];
            WordCandidate candidate = new WordCandidate(wordPieces[0], contextPredict, completionLength, attr, id, contextKillLen);
            candidateList.add(candidate);
        }
        return candidateList;
    }

    private boolean isAllUpperCaseFirstWord() {
        if (this.mWordCandidates == null || this.mWordCandidates.size() <= 0) {
            return false;
        }
        String word = this.mWordCandidates.get(0).word();
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isUpperCase(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
