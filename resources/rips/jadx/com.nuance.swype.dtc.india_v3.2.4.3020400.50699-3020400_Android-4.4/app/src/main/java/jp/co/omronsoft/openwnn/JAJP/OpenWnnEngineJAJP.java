package jp.co.omronsoft.openwnn.JAJP;

import android.content.SharedPreferences;
import com.nuance.swype.input.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import jp.co.omronsoft.openwnn.CandidateFilter;
import jp.co.omronsoft.openwnn.ComposingText;
import jp.co.omronsoft.openwnn.OpenWnnDictionaryImpl;
import jp.co.omronsoft.openwnn.StrSegmentClause;
import jp.co.omronsoft.openwnn.WnnClause;
import jp.co.omronsoft.openwnn.WnnDictionary;
import jp.co.omronsoft.openwnn.WnnEngine;
import jp.co.omronsoft.openwnn.WnnSentence;
import jp.co.omronsoft.openwnn.WnnWord;

/* loaded from: classes.dex */
public class OpenWnnEngineJAJP implements WnnEngine {
    public static final int DIC_LANG_EN = 1;
    public static final int DIC_LANG_EN_EMAIL_ADDRESS = 5;
    public static final int DIC_LANG_INIT = 0;
    public static final int DIC_LANG_JP = 0;
    public static final int DIC_LANG_JP_EISUKANA = 4;
    public static final int DIC_LANG_JP_PERSON_NAME = 2;
    public static final int DIC_LANG_JP_POSTAL_ADDRESS = 6;
    public static final int DIC_USERDIC = 3;
    public static final int FREQ_LEARN = 600;
    public static final int FREQ_USER = 500;
    public static final int KEYBOARD_KEYPAD12 = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_UNDEF = 0;
    public static final int MAX_OUTPUT_LENGTH = 50;
    private HashMap<String, WnnWord> mCandTable;
    private OpenWnnClauseConverterJAJP mClauseConverter;
    private ArrayList<WnnWord> mConvResult;
    private WnnSentence mConvertSentence;
    private WnnDictionary mDictionaryJP;
    private boolean mExactMatchMode;
    private int mGetCandidateFrom;
    private String mInputHiragana;
    private String mInputRomaji;
    private KanaConverter mKanaConverter;
    private int mOutputNum;
    private WnnWord mPreviousWord;
    private boolean mSingleClauseMode;
    private int mDictType = 0;
    private int mKeyboardType = 0;
    private CandidateFilter mFilter = null;

    public OpenWnnEngineJAJP(String libpath, String syspath, String dicpath) {
        this.mDictionaryJP = new OpenWnnDictionaryImpl(libpath, dicpath);
        if (!this.mDictionaryJP.isActive()) {
            this.mDictionaryJP = new OpenWnnDictionaryImpl(syspath, dicpath);
        }
        this.mDictionaryJP.clearDictionary();
        this.mDictionaryJP.clearApproxPattern();
        this.mDictionaryJP.setInUseState(false);
        this.mConvResult = new ArrayList<>();
        this.mCandTable = new HashMap<>();
        this.mClauseConverter = new OpenWnnClauseConverterJAJP();
        this.mKanaConverter = new KanaConverter();
    }

    private void setDictionaryForPrediction(int strlen) {
        WnnDictionary dict = this.mDictionaryJP;
        dict.clearDictionary();
        if (this.mDictType != 4) {
            dict.clearApproxPattern();
            if (strlen == 0) {
                dict.setDictionary(2, R.styleable.ThemeTemplate_chineseCommaKeypad, R.styleable.ThemeTemplate_chineseCommaKeypad);
                dict.setDictionary(3, 100, R.styleable.ThemeTemplate_chineseComma);
                dict.setDictionary(-2, FREQ_LEARN, FREQ_LEARN);
                return;
            }
            dict.setDictionary(0, 100, R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight);
            if (strlen > 1) {
                dict.setDictionary(1, 100, R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight);
            }
            dict.setDictionary(2, R.styleable.ThemeTemplate_chineseCommaKeypad, R.styleable.ThemeTemplate_chineseCommaKeypad);
            dict.setDictionary(3, 100, R.styleable.ThemeTemplate_chineseComma);
            dict.setDictionary(-1, 500, 500);
            dict.setDictionary(-2, FREQ_LEARN, FREQ_LEARN);
            if (this.mKeyboardType != 2) {
                dict.setApproxPattern(4);
            }
        }
    }

    private WnnWord getCandidate(int index) {
        if (this.mGetCandidateFrom == 0) {
            if (this.mDictType == 4) {
                this.mGetCandidateFrom = 2;
            } else if (this.mSingleClauseMode) {
                this.mGetCandidateFrom = 1;
            } else {
                while (true) {
                    if (index < this.mConvResult.size()) {
                        break;
                    }
                    WnnWord word = this.mDictionaryJP.getNextWord();
                    if (word == null) {
                        this.mGetCandidateFrom = 1;
                        break;
                    }
                    if (!this.mExactMatchMode || this.mInputHiragana.equals(word.stroke)) {
                        addCandidate(word);
                    }
                }
            }
        }
        if (this.mGetCandidateFrom == 1) {
            Iterator<?> convResult = this.mClauseConverter.convert(this.mInputHiragana);
            if (convResult != null) {
                while (convResult.hasNext()) {
                    addCandidate(convResult.next());
                }
            }
            this.mGetCandidateFrom = 2;
        }
        if (this.mGetCandidateFrom == 2) {
            Iterator<WnnWord> it = this.mKanaConverter.createPseudoCandidateList(this.mInputHiragana, this.mInputRomaji, this.mKeyboardType).iterator();
            while (it.hasNext()) {
                addCandidate(it.next());
            }
            this.mGetCandidateFrom = 3;
        }
        if (index >= this.mConvResult.size()) {
            return null;
        }
        return this.mConvResult.get(index);
    }

    private boolean addCandidate(WnnWord word) {
        if (word.candidate == null || this.mCandTable.containsKey(word.candidate) || word.candidate.length() > 50) {
            return false;
        }
        if (this.mFilter != null && !this.mFilter.isAllowed(word)) {
            return false;
        }
        this.mCandTable.put(word.candidate, word);
        this.mConvResult.add(word);
        return true;
    }

    private void clearCandidates() {
        this.mConvResult.clear();
        this.mCandTable.clear();
        this.mOutputNum = 0;
        this.mInputHiragana = null;
        this.mInputRomaji = null;
        this.mGetCandidateFrom = 0;
        this.mSingleClauseMode = false;
    }

    public boolean setDictionary(int type) {
        this.mDictType = type;
        return true;
    }

    private int setSearchKey(ComposingText text, int maxLen) {
        String input = text.toString(1);
        if (maxLen >= 0 && maxLen <= input.length()) {
            input = input.substring(0, maxLen);
            this.mExactMatchMode = true;
        } else {
            this.mExactMatchMode = false;
        }
        if (input.length() == 0) {
            this.mInputHiragana = "";
            this.mInputRomaji = "";
            return 0;
        }
        this.mInputHiragana = input;
        this.mInputRomaji = text.toString(0);
        return input.length();
    }

    public void clearPreviousWord() {
        this.mPreviousWord = null;
    }

    public void setPreviousWord(WnnWord word) {
        this.mPreviousWord = word;
    }

    public void setKeyboardType(int keyboardType) {
        this.mKeyboardType = keyboardType;
    }

    public void setFilter(CandidateFilter filter) {
        this.mFilter = filter;
        this.mClauseConverter.setFilter(filter);
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public void init() {
        clearPreviousWord();
        this.mClauseConverter.setDictionary(this.mDictionaryJP);
        this.mKanaConverter.setDictionary(this.mDictionaryJP);
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public void close() {
    }

    public WnnWord doExactSearch(String hirakana, String word) {
        WnnWord wnnword = null;
        clearCandidates();
        if (hirakana != null && word != null) {
            this.mInputHiragana = hirakana;
            setDictionaryForPrediction(1);
            if (this.mDictionaryJP.setInUseState(true)) {
                this.mDictionaryJP.searchWord(0, 0, hirakana);
                int count = -1;
                do {
                    count++;
                    wnnword = getCandidate(count);
                    if (wnnword == null) {
                        break;
                    }
                } while (!wnnword.candidate.equals(word));
                this.mDictionaryJP.setInUseState(false);
            }
        }
        return wnnword;
    }

    public ArrayList<String> doPredictionSearch(WnnWord word) {
        clearCandidates();
        if (word == null) {
            return null;
        }
        this.mInputHiragana = "";
        setDictionaryForPrediction(0);
        if (!this.mDictionaryJP.setInUseState(true)) {
            return null;
        }
        this.mDictionaryJP.searchWord(2, 0, "", word);
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        while (true) {
            WnnWord wnnword = getCandidate(count);
            if (wnnword == null) {
                break;
            }
            list.add(wnnword.candidate);
            count++;
        }
        this.mDictionaryJP.setInUseState(false);
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int convert(ComposingText text) {
        String input;
        WnnSentence sentence;
        clearCandidates();
        if (text == null) {
            return 0;
        }
        this.mDictionaryJP.setInUseState(true);
        int cursor = text.getCursor(1);
        WnnClause head = null;
        if (cursor > 0) {
            String input2 = text.toString(1, 0, cursor - 1);
            Iterator<WnnClause> headCandidates = this.mClauseConverter.convert(input2);
            if (headCandidates == null || !headCandidates.hasNext()) {
                return 0;
            }
            head = new WnnClause(input2, headCandidates.next());
            input = text.toString(1, cursor, text.size(1) - 1);
        } else {
            input = text.toString(1);
        }
        if (input.length() == 0) {
            sentence = null;
        } else {
            WnnSentence sentence2 = this.mClauseConverter.consecutiveClauseConvert(input);
            sentence = sentence2;
        }
        WnnSentence sentence3 = head != null ? new WnnSentence(head, sentence) : sentence;
        if (sentence3 == null) {
            return 0;
        }
        StrSegmentClause[] ss = new StrSegmentClause[sentence3.elements.size()];
        int pos = 0;
        int idx = 0;
        Iterator<WnnClause> it = sentence3.elements.iterator();
        while (it.hasNext()) {
            WnnClause clause = it.next();
            int len = clause.stroke.length();
            ss[idx] = new StrSegmentClause(clause, pos, (pos + len) - 1);
            pos += len;
            idx++;
        }
        text.setCursor(2, text.size(2));
        text.replaceStrSegment(2, ss, text.getCursor(2));
        this.mConvertSentence = sentence3;
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int searchWords(String key) {
        clearCandidates();
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int searchWords(WnnWord word) {
        clearCandidates();
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public WnnWord getNextCandidate() {
        if (this.mInputHiragana == null) {
            return null;
        }
        WnnWord word = getCandidate(this.mOutputNum);
        if (word != null) {
            this.mOutputNum++;
            return word;
        }
        return word;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public boolean learn(WnnWord word) {
        int ret = -1;
        if (word.partOfSpeech.right == 0) {
            word.partOfSpeech = this.mDictionaryJP.getPOS(6);
        }
        WnnDictionary dict = this.mDictionaryJP;
        if (word instanceof WnnSentence) {
            Iterator<WnnClause> clauses = ((WnnSentence) word).elements.iterator();
            while (clauses.hasNext()) {
                WnnWord wd = clauses.next();
                if (this.mPreviousWord != null) {
                    ret = dict.learnWord(wd, this.mPreviousWord);
                } else {
                    ret = dict.learnWord(wd);
                }
                this.mPreviousWord = wd;
                if (ret != 0) {
                    break;
                }
            }
        } else {
            if (this.mPreviousWord != null) {
                ret = dict.learnWord(word, this.mPreviousWord);
            } else {
                ret = dict.learnWord(word);
            }
            this.mPreviousWord = word;
            this.mClauseConverter.setDictionary(dict);
        }
        return ret == 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int addWord(WnnWord word) {
        this.mDictionaryJP.setInUseState(true);
        this.mDictionaryJP.addWordToUserDictionary(word);
        this.mDictionaryJP.setInUseState(false);
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public boolean deleteWord(WnnWord word) {
        this.mDictionaryJP.setInUseState(true);
        this.mDictionaryJP.removeWordFromUserDictionary(word);
        this.mDictionaryJP.setInUseState(false);
        return false;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public void setPreferences(SharedPreferences pref) {
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public void breakSequence() {
        clearPreviousWord();
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int makeCandidateListOf(int clausePosition) {
        clearCandidates();
        if (this.mConvertSentence == null || this.mConvertSentence.elements.size() <= clausePosition) {
            return 0;
        }
        this.mSingleClauseMode = true;
        WnnClause clause = this.mConvertSentence.elements.get(clausePosition);
        this.mInputHiragana = clause.stroke;
        this.mInputRomaji = clause.candidate;
        return 1;
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public boolean initializeDictionary(int dictionary) {
        switch (dictionary) {
            case 1:
                this.mDictionaryJP.setInUseState(true);
                this.mDictionaryJP.clearLearnDictionary();
                this.mDictionaryJP.setInUseState(false);
                return true;
            case 2:
                this.mDictionaryJP.setInUseState(true);
                this.mDictionaryJP.clearUserDictionary();
                this.mDictionaryJP.setInUseState(false);
                return true;
            default:
                return false;
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public boolean initializeDictionary(int dictionary, int type) {
        return initializeDictionary(dictionary);
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public WnnWord[] getUserDictionaryWords() {
        this.mDictionaryJP.setInUseState(true);
        WnnWord[] result = this.mDictionaryJP.getUserDictionaryWords();
        this.mDictionaryJP.setInUseState(false);
        Arrays.sort(result, new WnnWordComparator());
        return result;
    }

    /* loaded from: classes.dex */
    private static class WnnWordComparator implements Serializable, Comparator<Object> {
        private static final long serialVersionUID = 1;

        private WnnWordComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Object object1, Object object2) {
            WnnWord wnnWord1 = (WnnWord) object1;
            WnnWord wnnWord2 = (WnnWord) object2;
            return wnnWord1.stroke.compareTo(wnnWord2.stroke);
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnEngine
    public int predict(ComposingText text, int minLen, int maxLen) {
        return 0;
    }
}
