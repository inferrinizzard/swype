package com.nuance.swype.input.udb;

import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.SpeechWrapper;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class UserDictionaryIterator {
    protected final SpeechWrapper speechWrapper;

    public abstract void delete(Set<String> set);

    public abstract void eraseAll();

    public abstract boolean find(String str);

    public abstract boolean getNext(StringBuilder sb);

    public static UserDictionaryIterator createAlphaIterator(XT9CoreAlphaInput xt9coreAlpha, InputMethods.Language language, SpeechWrapper speechWrapper) {
        return new AlphaUserDictionaryIterator(xt9coreAlpha, language, speechWrapper);
    }

    public static UserDictionaryIterator createChineseIterator(XT9CoreChineseInput xt9coreChinese, InputMethods.Language language, SpeechWrapper speechWrapper) {
        return new ChineseUserDictionaryIterator(xt9coreChinese, language, speechWrapper);
    }

    private UserDictionaryIterator(SpeechWrapper speechWraper) {
        this.speechWrapper = speechWraper;
    }

    /* loaded from: classes.dex */
    public static class AlphaUserDictionaryIterator extends UserDictionaryIterator {
        private final XT9CoreAlphaInput alphaInput;

        private AlphaUserDictionaryIterator(XT9CoreAlphaInput alphaInput, InputMethods.Language language, SpeechWrapper speechWrapper) {
            super(speechWrapper);
            this.alphaInput = alphaInput;
            language.setLanguage(alphaInput);
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public boolean getNext(StringBuilder word) {
            return this.alphaInput.dlmGetNext(word);
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public void delete(Set<String> words) {
            for (String word : words) {
                this.alphaInput.dlmDelete(word);
            }
            if (this.speechWrapper != null) {
                this.speechWrapper.removeCustomWords(this, words);
            }
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public void eraseAll() {
            this.alphaInput.resetUserDatabases();
            if (this.speechWrapper != null) {
                this.speechWrapper.clearAllCustomWords(this);
            }
            XT9CoreJapaneseInput.resetUserDictionary();
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public boolean find(String word) {
            return this.alphaInput.dlmFind(word);
        }
    }

    /* loaded from: classes.dex */
    public static class ChineseUserDictionaryIterator extends UserDictionaryIterator {
        private final XT9CoreChineseInput chineseInput;
        private int index;

        private ChineseUserDictionaryIterator(XT9CoreChineseInput chineseInput, InputMethods.Language language, SpeechWrapper speechWrapper) {
            super(speechWrapper);
            this.chineseInput = chineseInput;
            language.setLanguage(chineseInput);
            this.index = 0;
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public boolean getNext(StringBuilder word) {
            StringBuilder spell = new StringBuilder();
            StringBuilder cnWords = new StringBuilder();
            int count = this.chineseInput.dlmCount();
            if (this.index >= count) {
                this.index = 0;
                return false;
            }
            if (this.chineseInput.dlmGetNext(this.index, cnWords, spell)) {
                word.setLength(0);
                word.append(cnWords.toString());
                this.index++;
                return true;
            }
            this.index = 0;
            return false;
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public void delete(Set<String> words) {
            for (String word : words) {
                this.chineseInput.dlmDelete(word);
            }
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public void eraseAll() {
            this.chineseInput.resetUserDictionary();
            if (this.speechWrapper != null) {
                this.speechWrapper.clearAllCustomWords(this);
            }
        }

        @Override // com.nuance.swype.input.udb.UserDictionaryIterator
        public boolean find(String word) {
            return false;
        }
    }
}
