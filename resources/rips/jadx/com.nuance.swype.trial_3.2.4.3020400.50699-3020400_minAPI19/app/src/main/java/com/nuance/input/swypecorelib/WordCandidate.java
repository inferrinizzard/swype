package com.nuance.input.swypecorelib;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class WordCandidate {
    private static final int ASDB = 4;
    private static final int AUTOAPPEND = 6;
    private static final int CONSTRUCTED = 8;
    private static final int DRAWABLE = -1;
    private static final int GDB = 10;
    private static final int LDB = 2;
    private static final int MDB = 3;
    private static final int NEW_WORD = 9;
    private static final int STEM = 5;
    private static final int TERMPUNCT = 7;
    private static final int UDB = 1;
    private static final int UNKNOWN = 0;
    static final int WORD_IS_AUTO_ACCECPT = 2048;
    static final int WORD_IS_DEFAULT_MASK = 64;
    static final int WORD_IS_REMOVALBE = 256;
    static final int WORD_IS_SMART_SELECTION = 1024;
    static final int WORD_IS_SPELL_CORRECTED_MASK = 32;
    static final int WORD_IS_TERMINAL_MASK = 128;
    static final int WORD_SHOULD_REMOVE_SPACE_BEFORE = 512;
    static final int WORD_SOURCE_MASK = 15;
    private String emoji_unicode;
    private int height;
    private int mContextKillLength;
    String mContextPredict;
    private int mId;
    String mWord;
    private int mWordAttribute;
    private int mWordCompletionLength;
    private int width;
    private int xPos;
    private int yPos;

    /* loaded from: classes.dex */
    public enum Source {
        WORD_SOURCE_UNKNOWN(0),
        WORD_SOURCE_USER_ADDED(1),
        WORD_SOURCE_LDB(2),
        WORD_SOURCE_MDB(3),
        WORD_SOURCE_ASDB(4),
        WORD_SOURCE_STEM(5),
        WORD_SOURCE_AUTOAPPEND(6),
        WORD_SOURCE_TERMPUNCT(7),
        WORD_SOURCE_CONSTRUCTED(8),
        WORD_SOURCE_NEW_WORD(9),
        WORD_SOURCE_GESTURE(10),
        WORD_SOURCE_DRAWABLE(-1);

        private final int value;

        Source(int value) {
            this.value = value;
        }

        public final int value() {
            return this.value;
        }
    }

    public WordCandidate(String word, String contextPredict, int wordCompletionLength, int wordAttribute, int id, int contextKillLength) {
        this.mWord = word;
        this.mContextPredict = contextPredict;
        this.mWordCompletionLength = wordCompletionLength;
        this.mWordAttribute = wordAttribute;
        this.mId = id;
        this.mContextKillLength = contextKillLength;
    }

    public WordCandidate(String word, int wordCompletionLength, int wordAttribute, int id) {
        this.mWord = word;
        this.mWordCompletionLength = wordCompletionLength;
        this.mWordAttribute = wordAttribute;
        this.mId = id;
        this.mContextKillLength = 0;
    }

    public WordCandidate(String word) {
        this.mWord = word;
        this.mWordCompletionLength = 0;
        this.mWordAttribute = 0;
        this.mId = -1;
        this.mContextKillLength = 0;
    }

    public WordCandidate(String word, int id) {
        this.mWord = word;
        this.mWordCompletionLength = 0;
        this.mWordAttribute = 0;
        this.mId = id;
        this.mContextKillLength = 0;
    }

    public static List<WordCandidate> createCandidates(String[] suggestions, int[] packedFields) {
        List<WordCandidate> candidateList = new ArrayList<>(suggestions.length);
        int length = suggestions.length;
        int i = 0;
        int fieldIndex = 0;
        while (i < length) {
            String word = suggestions[i];
            int fieldIndex2 = fieldIndex + 1;
            int completionLength = packedFields[fieldIndex];
            int fieldIndex3 = fieldIndex2 + 1;
            int attr = packedFields[fieldIndex2];
            int id = packedFields[fieldIndex3];
            WordCandidate candidate = new WordCandidate(word, completionLength, attr, id);
            candidateList.add(candidate);
            i++;
            fieldIndex = fieldIndex3 + 1;
        }
        return candidateList;
    }

    public String toString() {
        return this.mContextPredict == null ? this.mWord.toString() : this.mContextPredict.toString() + ' ' + this.mWord.toString();
    }

    public int length() {
        return this.mContextPredict == null ? this.mWord.length() : this.mContextPredict.length() + 1 + this.mWord.length();
    }

    public String word() {
        return this.mWord;
    }

    public String contextPredict() {
        return this.mContextPredict;
    }

    public int contextPredictLength() {
        if (this.mContextPredict == null) {
            return 0;
        }
        return this.mContextPredict.length();
    }

    public int completionLength() {
        return this.mWordCompletionLength;
    }

    public int attribute() {
        return this.mWordAttribute;
    }

    public int id() {
        return this.mId;
    }

    public int contextKillLength() {
        return this.mContextKillLength;
    }

    public Source source() {
        switch (this.mWordAttribute & 15) {
            case 1:
                return Source.WORD_SOURCE_USER_ADDED;
            case 2:
                return Source.WORD_SOURCE_LDB;
            case 3:
                return Source.WORD_SOURCE_MDB;
            case 4:
                return Source.WORD_SOURCE_ASDB;
            case 5:
                return Source.WORD_SOURCE_STEM;
            case 6:
                return Source.WORD_SOURCE_AUTOAPPEND;
            case 7:
                return Source.WORD_SOURCE_TERMPUNCT;
            case 8:
                return Source.WORD_SOURCE_CONSTRUCTED;
            case 9:
                return Source.WORD_SOURCE_NEW_WORD;
            case 10:
                return Source.WORD_SOURCE_GESTURE;
            default:
                return Source.WORD_SOURCE_UNKNOWN;
        }
    }

    public boolean isExactADictionaryWord() {
        Source src = source();
        return (src == Source.WORD_SOURCE_NEW_WORD || src == Source.WORD_SOURCE_UNKNOWN) ? false : true;
    }

    public boolean isSpellCorrected() {
        return (this.mWordAttribute & 32) != 0;
    }

    public boolean isAutoAccept() {
        return (this.mWordAttribute & 2048) != 0;
    }

    public boolean isDefault() {
        return (this.mWordAttribute & 64) != 0;
    }

    public boolean isSmartSuggestion() {
        return (this.mWordAttribute & 1024) != 0;
    }

    public void setSmartSuggestion(boolean val) {
        if (val) {
            this.mWordAttribute |= 1024;
        } else {
            this.mWordAttribute &= -1025;
        }
    }

    public boolean isTerminal() {
        return (this.mWordAttribute & 128) != 0;
    }

    public boolean isCompletion() {
        return this.mWordCompletionLength > 0;
    }

    public boolean isExact() {
        return this.mId == 0;
    }

    public boolean isRemovable() {
        return ((this.mWordAttribute & 256) == 0 || source() == Source.WORD_SOURCE_NEW_WORD || source() == Source.WORD_SOURCE_CONSTRUCTED) ? false : true;
    }

    public boolean shouldRemoveSpaceBefore() {
        return (this.mWordAttribute & 512) != 0;
    }

    public int hashCode() {
        return this.mWord.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WordCandidate)) {
            return false;
        }
        WordCandidate c = (WordCandidate) o;
        return c.mWord != null && this.mWord.toString().equals(c.mWord.toString());
    }

    public boolean containsDoubleLetters() {
        if (this.mWord == null || this.mWord.length() < 2) {
            return false;
        }
        for (int i = 0; i < this.mWord.length() - 1; i++) {
            if (this.mWord.charAt(i) == this.mWord.charAt(i + 1)) {
                return true;
            }
        }
        return false;
    }

    public void setWord(String mWord) {
        this.mWord = mWord;
    }

    public void setLeft(int xPos) {
        this.xPos = xPos;
    }

    public int getLeft() {
        return this.xPos;
    }

    public void setTop(int yPos) {
        this.yPos = yPos;
    }

    public int getTop() {
        return this.yPos;
    }

    public int getRight() {
        return this.xPos + getWidth();
    }

    public int getBottom() {
        return this.yPos + getHeight();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public void setEmojiUnicode(String emoji_unicode) {
        this.emoji_unicode = emoji_unicode;
    }

    public String getEmojiUnicode() {
        return this.emoji_unicode;
    }
}
