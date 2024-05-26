package jp.co.omronsoft.openwnn.JAJP;

import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import jp.co.omronsoft.openwnn.CandidateFilter;
import jp.co.omronsoft.openwnn.WnnClause;
import jp.co.omronsoft.openwnn.WnnDictionary;
import jp.co.omronsoft.openwnn.WnnPOS;
import jp.co.omronsoft.openwnn.WnnSentence;
import jp.co.omronsoft.openwnn.WnnWord;

/* loaded from: classes.dex */
public class OpenWnnClauseConverterJAJP {
    private static final int CLAUSE_COST = -1000;
    private static final int FREQ_LEARN = 600;
    private static final int FREQ_USER = 500;
    public static final int MAX_INPUT_LENGTH = 50;
    private byte[][] mConnectMatrix;
    private WnnDictionary mDictionary;
    private WnnPOS mPosDefault;
    private WnnPOS mPosEndOfClause1;
    private WnnPOS mPosEndOfClause2;
    private WnnPOS mPosEndOfClause3;
    private CandidateFilter mFilter = null;
    private HashMap<String, ArrayList<WnnWord>> mIndepWordBag = new HashMap<>();
    private HashMap<String, ArrayList<WnnWord>> mAllIndepWordBag = new HashMap<>();
    private HashMap<String, ArrayList<WnnWord>> mFzkPatterns = new HashMap<>();
    private LinkedList<WnnClause> mConvertResult = new LinkedList<>();
    private WnnSentence[] mSentenceBuffer = new WnnSentence[50];

    public void setDictionary(WnnDictionary dict) {
        this.mConnectMatrix = dict.getConnectMatrix();
        this.mDictionary = dict;
        dict.clearDictionary();
        dict.clearApproxPattern();
        this.mIndepWordBag.clear();
        this.mAllIndepWordBag.clear();
        this.mFzkPatterns.clear();
        this.mPosDefault = dict.getPOS(6);
        this.mPosEndOfClause1 = dict.getPOS(0);
        this.mPosEndOfClause2 = dict.getPOS(1);
        this.mPosEndOfClause3 = dict.getPOS(2);
    }

    public void setFilter(CandidateFilter filter) {
        this.mFilter = filter;
    }

    public Iterator<WnnClause> convert(String input) {
        if (this.mConnectMatrix == null || this.mDictionary == null || input.length() > 50) {
            return null;
        }
        this.mConvertResult.clear();
        if (singleClauseConvert(this.mConvertResult, input, this.mPosEndOfClause2, true)) {
            return this.mConvertResult.iterator();
        }
        return null;
    }

    public WnnSentence consecutiveClauseConvert(String input) {
        WnnClause bestClause;
        WnnSentence ws;
        LinkedList<WnnClause> clauses = new LinkedList<>();
        for (int i = 0; i < input.length(); i++) {
            this.mSentenceBuffer[i] = null;
        }
        WnnSentence[] sentence = this.mSentenceBuffer;
        for (int start = 0; start < input.length(); start++) {
            if (start == 0 || sentence[start - 1] != null) {
                int end = input.length();
                if (end > start + 20) {
                    end = start + 20;
                }
                while (end > start) {
                    int idx = end - 1;
                    if (sentence[idx] != null) {
                        if (start != 0) {
                            if (sentence[idx].frequency > sentence[start - 1].frequency + CLAUSE_COST + 600) {
                                break;
                            }
                        } else if (sentence[idx].frequency > -400) {
                            break;
                        }
                    }
                    String key = input.substring(start, end);
                    clauses.clear();
                    if (end == input.length()) {
                        singleClauseConvert(clauses, key, this.mPosEndOfClause1, false);
                    } else {
                        singleClauseConvert(clauses, key, this.mPosEndOfClause3, false);
                    }
                    if (clauses.isEmpty()) {
                        bestClause = defaultClause(key);
                    } else {
                        bestClause = clauses.get(0);
                    }
                    if (start == 0) {
                        ws = new WnnSentence(key, bestClause);
                    } else {
                        ws = new WnnSentence(sentence[start - 1], bestClause);
                    }
                    ws.frequency += CLAUSE_COST;
                    if (sentence[idx] == null || sentence[idx].frequency < ws.frequency) {
                        sentence[idx] = ws;
                    }
                    end--;
                }
            }
        }
        if (sentence[input.length() - 1] != null) {
            return sentence[input.length() - 1];
        }
        return null;
    }

    private boolean singleClauseConvert(LinkedList<WnnClause> clauseList, String input, WnnPOS terminal, boolean all) {
        boolean ret = false;
        ArrayList<WnnWord> stems = getIndependentWords(input, all);
        if (stems != null && !stems.isEmpty()) {
            Iterator<WnnWord> stemsi = stems.iterator();
            while (stemsi.hasNext()) {
                if (addClause(clauseList, input, stemsi.next(), null, terminal, all)) {
                    ret = true;
                }
            }
        }
        int max = -2000;
        for (int split = 1; split < input.length(); split++) {
            ArrayList<WnnWord> fzks = getAncillaryPattern(input.substring(split));
            if (fzks != null && !fzks.isEmpty()) {
                String str = input.substring(0, split);
                ArrayList<WnnWord> stems2 = getIndependentWords(str, all);
                if (stems2 == null || stems2.isEmpty()) {
                    if (this.mDictionary.searchWord(1, 0, str) <= 0) {
                        break;
                    }
                } else {
                    Iterator<WnnWord> stemsi2 = stems2.iterator();
                    while (stemsi2.hasNext()) {
                        WnnWord stem = stemsi2.next();
                        if (all || stem.frequency > max) {
                            Iterator<WnnWord> fzksi = fzks.iterator();
                            while (fzksi.hasNext()) {
                                WnnWord fzk = fzksi.next();
                                if (addClause(clauseList, input, stem, fzk, terminal, all)) {
                                    ret = true;
                                    max = stem.frequency;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean addClause(LinkedList<WnnClause> clauseList, String input, WnnWord stem, WnnWord fzk, WnnPOS terminal, boolean all) {
        WnnClause clause = null;
        if (fzk == null) {
            if (connectible(stem.partOfSpeech.right, terminal.left)) {
                clause = new WnnClause(input, stem);
            }
        } else if (connectible(stem.partOfSpeech.right, fzk.partOfSpeech.left) && connectible(fzk.partOfSpeech.right, terminal.left)) {
            clause = new WnnClause(input, stem, fzk);
        }
        if (clause == null) {
            return false;
        }
        if (this.mFilter != null && !this.mFilter.isAllowed(clause)) {
            return false;
        }
        if (clauseList.isEmpty()) {
            clauseList.add(0, clause);
            return true;
        }
        if (!all) {
            if (clauseList.get(0).frequency >= clause.frequency) {
                return false;
            }
            clauseList.set(0, clause);
            return true;
        }
        Iterator<WnnClause> clauseListi = clauseList.iterator();
        int index = 0;
        while (clauseListi.hasNext() && clauseListi.next().frequency >= clause.frequency) {
            index++;
        }
        clauseList.add(index, clause);
        return true;
    }

    private boolean connectible(int right, int left) {
        return this.mConnectMatrix[left][right] != 0;
    }

    private ArrayList<WnnWord> getAncillaryPattern(String input) {
        if (input.length() == 0) {
            return null;
        }
        HashMap<String, ArrayList<WnnWord>> fzkPat = this.mFzkPatterns;
        ArrayList<WnnWord> fzks = fzkPat.get(input);
        if (fzks == null) {
            WnnDictionary dict = this.mDictionary;
            dict.clearDictionary();
            dict.clearApproxPattern();
            dict.setDictionary(6, R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight, 500);
            for (int start = input.length() - 1; start >= 0; start--) {
                String key = input.substring(start);
                fzks = fzkPat.get(key);
                if (fzks == null) {
                    fzks = new ArrayList<>();
                    this.mFzkPatterns.put(key, fzks);
                    dict.searchWord(0, 0, key);
                    while (true) {
                        WnnWord word = dict.getNextWord();
                        if (word == null) {
                            break;
                        }
                        fzks.add(word);
                    }
                    for (int end = input.length() - 1; end > start; end--) {
                        ArrayList<WnnWord> followFzks = fzkPat.get(input.substring(end));
                        if (followFzks != null && !followFzks.isEmpty()) {
                            dict.searchWord(0, 0, input.substring(start, end));
                            while (true) {
                                WnnWord word2 = dict.getNextWord();
                                if (word2 != null) {
                                    Iterator<WnnWord> followFzksi = followFzks.iterator();
                                    while (followFzksi.hasNext()) {
                                        WnnWord follow = followFzksi.next();
                                        if (connectible(word2.partOfSpeech.right, follow.partOfSpeech.left)) {
                                            fzks.add(new WnnWord(key, key, new WnnPOS(word2.partOfSpeech.left, follow.partOfSpeech.right)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return fzks;
        }
        return fzks;
    }

    private ArrayList<WnnWord> getIndependentWords(String input, boolean all) {
        if (input.length() == 0) {
            return null;
        }
        ArrayList<WnnWord> words = all ? this.mAllIndepWordBag.get(input) : this.mIndepWordBag.get(input);
        if (words == null) {
            WnnDictionary dict = this.mDictionary;
            dict.clearDictionary();
            dict.clearApproxPattern();
            dict.setDictionary(4, 0, 10);
            dict.setDictionary(5, R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight, 500);
            dict.setDictionary(-1, 500, 500);
            dict.setDictionary(-2, 600, 600);
            ArrayList<WnnWord> words2 = new ArrayList<>();
            if (all) {
                this.mAllIndepWordBag.put(input, words2);
                dict.searchWord(0, 0, input);
                while (true) {
                    WnnWord word = dict.getNextWord();
                    if (word == null) {
                        break;
                    }
                    if (input.equals(word.stroke)) {
                        words2.add(word);
                    }
                }
            } else {
                this.mIndepWordBag.put(input, words2);
                dict.searchWord(0, 0, input);
                while (true) {
                    WnnWord word2 = dict.getNextWord();
                    if (word2 == null) {
                        break;
                    }
                    if (input.equals(word2.stroke)) {
                        Iterator<WnnWord> list = words2.iterator();
                        boolean found = false;
                        while (true) {
                            if (!list.hasNext()) {
                                break;
                            }
                            if (list.next().partOfSpeech.right == word2.partOfSpeech.right) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            words2.add(word2);
                        }
                        if (word2.frequency < 400) {
                            break;
                        }
                    }
                }
            }
            addAutoGeneratedCandidates(input, words2, all);
            return words2;
        }
        return words;
    }

    private void addAutoGeneratedCandidates(String input, ArrayList<WnnWord> wordList, boolean all) {
        wordList.add(new WnnWord(input, input, this.mPosDefault, input.length() * (-1001)));
    }

    private WnnClause defaultClause(String input) {
        return new WnnClause(input, input, this.mPosDefault, input.length() * (-1001));
    }
}
