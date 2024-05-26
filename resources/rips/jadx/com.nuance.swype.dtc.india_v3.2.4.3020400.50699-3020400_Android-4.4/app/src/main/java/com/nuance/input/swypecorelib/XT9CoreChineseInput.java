package com.nuance.input.swypecorelib;

import android.graphics.Point;
import android.text.SpannableStringBuilder;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract;
import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class XT9CoreChineseInput extends XT9CoreInput {
    public static final int CHINESE_CORRECTION_LEVEL = 102;
    public static final int CHINESE_CORRECTION_LEVEL_DEFAULT = 2;
    public static final int CHINESE_CORRECTION_LEVEL_OFF = 0;
    public static final int CHINESE_INPUT_MODE_BPMF = 1;
    public static final int CHINESE_INPUT_MODE_CANGJIE = 4;
    public static final int CHINESE_INPUT_MODE_DOUBLEPINYIN = 3;
    public static final int CHINESE_INPUT_MODE_PINYIN = 0;
    public static final int CHINESE_INPUT_MODE_QUICK_CANGJIE = 5;
    public static final int CHINESE_INPUT_MODE_STROKE = 2;
    public static final int CHINESE_MOHU_PAIR_AN_ANG = 64;
    public static final int CHINESE_MOHU_PAIR_C_CH = 2;
    public static final int CHINESE_MOHU_PAIR_EN_ENG = 128;
    public static final int CHINESE_MOHU_PAIR_F_H = 32;
    public static final int CHINESE_MOHU_PAIR_IN_ING = 256;
    public static final int CHINESE_MOHU_PAIR_N_L = 8;
    public static final int CHINESE_MOHU_PAIR_R_L = 16;
    public static final int CHINESE_MOHU_PAIR_S_SH = 4;
    public static final int CHINESE_MOHU_PAIR_Z_ZH = 1;
    public static final int CHINESE_MOHU_PINYIN = 101;
    public static final int CHINESE_NAME_INPUT = 100;
    public static final int CHINESE_NAME_INPUT_OFF = 0;
    public static final int CHINESE_NAME_INPUT_ON = 1;
    public static final int CHINESE_PHONETIC_TONE1 = 177;
    public static final int CHINESE_PHONETIC_TONE2 = 178;
    public static final int CHINESE_PHONETIC_TONE3 = 179;
    public static final int CHINESE_PHONETIC_TONE4 = 180;
    public static final int CHINESE_PHONETIC_TONE5 = 181;
    private static final char COMPONENT_MARKER = 40959;
    public static final int ET9CPPhraseSource_Audb = 5;
    public static final int ET9CPPhraseSource_DLMDeletable = 14;
    public static final int ET9CPPhraseSource_DLMResettable = 13;
    public static final int ET9CPPhraseSource_Ldb = 1;
    public static final int ET9CPPhraseSource_Msdb = 7;
    public static final int ET9CPPhraseSource_Rdb = 4;
    public static final int ET9CPPhraseSource_Udb = 6;
    public static final int ET9CPPhraseSource_UdbPrediction = 9;
    public static final int ET9CPPhraseSource_Unknown = 0;
    public static final int INPUT_MODE_BPMF = 1;
    public static final int INPUT_MODE_CANGJIE = 4;
    public static final int INPUT_MODE_DOUBLEPINYIN = 3;
    public static final int INPUT_MODE_PINYIN = 0;
    public static final int INPUT_MODE_QUICK_CANGJIE = 5;
    public static final int INPUT_MODE_STROKE = 2;
    private static final char INTERNAL_SEGMENT_DELIMITER = '_';
    public static final int MAXACTIVEKEYCOUNT = 64;
    public static final int MAXONEPAGESIZE = 30;
    public static final int MAXSPELLLEN = 256;
    public static final int MAXWORDLEN = 112;
    public static final int MAXWORDLIST = 256;
    private static final String TAG = "XT9CoreChineseInput";
    private static final char mBopomofoSyllableDelimiter = '_';
    private static final char mChineseDelimiter = '\'';
    private static final char mChineseSegmentDelimiter = ' ';
    private StringBuilder mActiveSpell;
    private StringBuilder mChineseWordInline;
    private int mDelimiterCounts;
    private StringBuilder[] mGetStringBufferArray;
    private StringBuilder mPinyinInline;
    private List<CharSequence> mPopupList;
    private List<AtomicInteger> mPopupWdSourceList;
    private List<CharSequence> mPrefixList;
    private List<CharSequence> mPrefixPool;
    private char[] mScratchBuffer;
    private int[] mScratchInt;
    private int[] mScratchIntWordSource;
    private char[] mScratchSpellBuffer;
    private int[] mScratchSpellInt;
    private List<CharSequence> mSuffixList;
    private List<CharSequence> mSuffixPool;
    String mTextContext;
    private List<AtomicInteger> mWdSourceList;
    private List<CharSequence> mWordList;
    private static final char[] mStrokes = "一丨丿丶﹁？".toCharArray();
    private static final char[] mCompSimp = "十厂~~├‖冂囗~亻~八人乂几匕~勹九亠冫~讠冖阝了刀乃又厶╪土士工艹大扌~口山巾千彳犭夂夕饣丬广亡门~氵忄ツ宀尸弓子女马纟巛丰王~~廿~木不歹车牙日中贝气毛~~片父今~分月文方火户礻~古石龙业目田皿矢钅禾丘白疒立穴衤加矛耒耳亚西此虍虫缶舌竹臼自舟合~~米羽走赤~束酉足身豸角其雨非齿隹鱼革骨鬼敖莫鹿麻敝黑辟力干彐彑幺夫天元比止水爿~民癶吉臣至~血~糸車甫豆豕貝言君~門金食香韋馬髟般~執麥魚鼠鼻齒龍".toCharArray();
    private static final char[] mCompTrad = "~十~~厂匕├冂~~亻~乂人八乃勹九~~几~冫亠~冖了力刀又厶~干~土士工扌卄大兀尢弋~~口山巾千~~彳~犭夕夂广亡忄~氵宀~尸己巳弓子女彑阝幺巛丰王~开夫天元~~廿木支不歹比牙旡戈艹止~少日中水手气毛壬告~片夭斤今父爫分公月戶丹氏勿~文方火心~爿予~未正甘世古石北占业~目田由央~生矢失禾丘~白瓜皮~夗礻疒立玄半穴~衤民弗疋丞奴加召台癶矛舌次吉巩耳共臣西朿西而夸列至~此虍光虫曲~同耒缶先竹臼自~血后舟杀合兆旬多亦~交~~亥羊~并~米~艸如羽糸走赤~克求孛車甫更束~吾豆酉辰夾豕巠~~貝足每身~余希釆谷豸卵角言亨辛~~弟沙君甬玨武青者坴幸亞其昔直~林來~丽奇豖雨妻非叔卓果門~委隹卑舍金肴食采周京享炎~壴甚革~~是冒禺曷咼品耑香秋盾俞風扁音酋首兹軍韋~冓馬鬲~晉髟鬥~骨豈烏鬼般奚芻高齊兼~~冥埶敖執~堇~專區票戚麥莫鹵婁從魚麻鹿章敝尉將巢喜壹厤厥黹單黑番~~~曾矞鄉鼓~敬~鼠僉會亶雍辟與厭爾~鼻賣齒畾~龍龠雚豐~".toCharArray();
    private static final char[] mCangjie = "日月金木水火土竹戈十大中一弓人心手口尸廿山女田難卜重".toCharArray();
    private static final char[] mTones = "¯ˊˇˋ˙".toCharArray();
    private static char[] mPhraseBuffer = new char[112];
    private static char[] mSpellBuffer = new char[112];
    private static int[] mPhraseLength = new int[1];
    private static int[] mSpellLength = new int[1];

    private static native boolean addExplicitKey(long j, int i, int i2);

    private static native boolean addToneForZhuyin(long j, int i);

    private static native void backupWordSymbolInfo(long j);

    private static native boolean breakContext(long j);

    private static native boolean clearAllKeys(long j);

    private static native int clearCommonChar(long j);

    private static native boolean clearKey(long j);

    private static native boolean clearKeyByIndex(long j, int i, int i2);

    private static native long create_context(String str);

    private static native boolean cycleTone(long j);

    private static native boolean deleteOneKeyAndRefresh(long j);

    private static native void destroy_context(long j);

    private static native boolean dlmAdd(long j, char[] cArr, char[] cArr2);

    private static native int dlmCount(long j);

    private static native boolean dlmDelete(long j, char[] cArr);

    private static native int dlmExport(long j, String str);

    private static native boolean dlmGetNext(long j, int i, char[] cArr, int[] iArr, int i2, char[] cArr2, int[] iArr2, int i3);

    private static native void dlmReset(long j);

    private static native void finish(long j);

    private static native int getActivePrefixIndex(long j);

    private static native int getActiveSuffixIndex(long j);

    private static native int getCharSpell(long j, int i, int i2, int i3, char[] cArr);

    private static native int getExactWord(long j, char[] cArr);

    private static native int getInputMode(long j);

    private static native int getKeyCount(long j);

    private static native boolean getPrefix(long j, int i, char[] cArr, int[] iArr, int i2);

    private static native int getPrefixCount(long j);

    private static native boolean getSelection(long j, char[] cArr, int[] iArr, int i);

    private static native boolean getSpell(long j, int i, char[] cArr, int[] iArr, int i2);

    private static native boolean getSuffix(long j, int i, char[] cArr, int[] iArr, int i2);

    private static native int getSuffixCount(long j);

    private static native boolean getTailDoublePinyinUnicode(long j, int[] iArr);

    private static native boolean getWord(long j, int i, char[] cArr, int[] iArr, int i2, char[] cArr2, int[] iArr2, int[] iArr3);

    private static native boolean hasActiveInput(long j);

    private static native boolean hasBilingualPrefix(long j);

    private static native int initialize(long j);

    private static native boolean isFullSentenceActive(long j);

    private static native boolean isHasTraceInfo(long j);

    private static native boolean multiTapBuildWordCandidateList(long j);

    private static native void persistUserDatabase(long j);

    private static native int predictionCloudCommitPhrase(long j, String str, String str2, String str3, int[] iArr);

    private static native int predictionCloudGetInputData(long j, byte[] bArr, short[] sArr);

    private static native int predictionCloudGetSettings(long j, int[] iArr);

    private static native boolean processKeyBySymbol(long j, int i);

    private static native boolean resetUserDictionary(long j);

    private static native void restoreWordSymbolInfo(long j);

    private static native boolean selectWord(long j, int i, char[] cArr, int[] iArr, int i2);

    private static native boolean setActivePrefixIndex(long j, int i);

    private static native boolean setAttribute(long j, int i, int i2);

    private static native boolean setBilingual(long j, boolean z);

    private static native int setCategoryDb(long j, int i, int i2, int i3);

    private static native boolean setContext(long j, char[] cArr, int i);

    private static native int setFullSentence(long j);

    private static native boolean setInputMode(long j, int i);

    private static native int setMSDB(long j, int i, boolean z);

    private static native void setMultiTapHasInvalidKey(long j, boolean z);

    private static native void setShiftState(long j, int i);

    private static native boolean start(long j);

    private static native boolean tryBuildingWordCandidateList(long j, boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public XT9CoreChineseInput(SessionDataCollectorAbstract sessionDataCollector) {
        super(sessionDataCollector);
        this.mWordList = new ArrayList();
        this.mWdSourceList = new ArrayList();
        this.mPrefixList = new ArrayList();
        this.mPrefixPool = new ArrayList();
        this.mSuffixList = new ArrayList();
        this.mSuffixPool = new ArrayList();
        this.mScratchBuffer = new char[112];
        this.mScratchInt = new int[1];
        this.mScratchSpellBuffer = new char[256];
        this.mScratchSpellInt = new int[1];
        this.mScratchIntWordSource = new int[1];
        this.mDelimiterCounts = 0;
        this.mActiveSpell = new StringBuilder(112);
        this.mPinyinInline = new StringBuilder(112);
        this.mChineseWordInline = new StringBuilder(112);
        this.mGetStringBufferArray = new StringBuilder[256];
        this.mPopupList = new ArrayList();
        this.mPopupWdSourceList = new ArrayList();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected long create_native_context(String databaseConfigFile) {
        return create_context(databaseConfigFile);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected XT9Status initialize_native_core(long inputContext) {
        return XT9Status.from(initialize(inputContext));
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected void destroy_native_context(long inputContext) {
        destroy_context(inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void startSession() {
        this.mTextContext = null;
        for (int i = 0; i < this.mGetStringBufferArray.length; i++) {
            this.mGetStringBufferArray[i] = new StringBuilder("");
        }
        start(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void finishSession() {
        this.mWordList.clear();
        this.mWdSourceList.clear();
        this.mPrefixList.clear();
        this.mSuffixList.clear();
        this.mPrefixPool.clear();
        this.mSuffixPool.clear();
        finish(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void persistUserDatabase() {
        persistUserDatabase(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, boolean value) {
        setAttribute(this.inputContext, id, value ? 1 : 0);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, int value) {
        setAttribute(this.inputContext, id, value);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setShiftState(Shift.ShiftState shift) {
        setShiftState(this.inputContext, shift.getIndex());
    }

    public boolean processUpperLetterPress(int key) {
        return processKeyBySymbol(this.inputContext, key);
    }

    public boolean addExplicitKey(int key, Shift.ShiftState shiftState) {
        return addExplicitKey(this.inputContext, key, shiftState.getIndex());
    }

    public XT9Status setMSDB(int langId, boolean isEnabled) {
        return XT9Status.from(setMSDB(this.inputContext, langId, isEnabled));
    }

    public boolean setInputMode(int mode) {
        return setInputMode(this.inputContext, mode);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean processKey(Point point, int key, Shift.ShiftState shiftState, long eventTime) {
        int mode = getInputMode(this.inputContext);
        return ((3 == mode || mode == 0 || 1 == mode) && point != null) ? super.processTap(point, shiftState, eventTime) : super.processKey(cangjieToInternal(key), shiftState, eventTime);
    }

    public void addExplicitSymbol(int symbol, Shift.ShiftState shiftState) {
        int convertSymbol;
        int mode = getInputMode(this.inputContext);
        if (3 == mode || mode == 0 || 1 == mode) {
            convertSymbol = symbol;
        } else {
            convertSymbol = cangjieToInternal(symbol);
        }
        addExplicitKey(convertSymbol, shiftState);
    }

    public boolean isHasTraceInfo() {
        return isHasTraceInfo(this.inputContext);
    }

    public void backupWordSymbolInfo() {
        backupWordSymbolInfo(this.inputContext);
    }

    public void restoreWordSymbolInfo() {
        restoreWordSymbolInfo(this.inputContext);
    }

    public boolean deleteOneKeyAndRefresh() {
        return deleteOneKeyAndRefresh(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearAllKeys() {
        return clearAllKeys(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKey() {
        return clearKey(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKeyByIndex(int index, int count) {
        return clearKeyByIndex(this.inputContext, index, count);
    }

    public boolean cycleTone() {
        return cycleTone(this.inputContext);
    }

    public boolean addToneForZhuyin(int tone) {
        return addToneForZhuyin(this.inputContext, tone);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getKeyCount() {
        return getKeyCount(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean hasActiveInput() {
        return hasActiveInput(this.inputContext);
    }

    public boolean getInlineString(SpannableStringBuilder inlineString) {
        inlineString.clear();
        boolean success = getSelection(this.inputContext, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            for (int i = 0; i < this.mScratchInt[0]; i++) {
                inlineString.append(this.mScratchBuffer[i]);
            }
            this.mChineseWordInline.setLength(0);
            this.mChineseWordInline.append((CharSequence) inlineString);
            getSpellInternal(0, this.mActiveSpell);
            if (this.mActiveSpell.length() > 0) {
                char comp = componentToExternal(this.mActiveSpell.charAt(0));
                if (comp != 0) {
                    this.mActiveSpell.setCharAt(0, comp);
                }
                inlineString.append((CharSequence) this.mActiveSpell);
            }
        }
        return success;
    }

    public void getExactTypeAsInline(StringBuilder inlineWord) {
        inlineWord.setLength(0);
        char[] wordArray = new char[112];
        if (getExactWord(this.inputContext, wordArray) == 0) {
            String exactWord = new String(wordArray).trim();
            int mode = getInputMode(this.inputContext);
            if (4 == mode || 5 == mode) {
                StringBuilder cangjieInline = new StringBuilder(exactWord.toUpperCase());
                normalizeCangjie(cangjieInline);
                inlineWord.append((CharSequence) cangjieInline);
            } else {
                inlineWord.append(exactWord);
            }
            this.mChineseWordInline.setLength(0);
            this.mChineseWordInline.append((CharSequence) inlineWord);
        }
    }

    public StringBuilder getChineseWordsInline() {
        return this.mChineseWordInline;
    }

    public boolean getInlineSelection(StringBuilder inlineSelection) {
        inlineSelection.setLength(0);
        boolean success = getSelection(this.inputContext, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            for (int i = 0; i < this.mScratchInt[0]; i++) {
                inlineSelection.append(this.mScratchBuffer[i]);
            }
        }
        return success;
    }

    public boolean selectWord(int index, StringBuilder insertText) {
        insertText.setLength(0);
        boolean success = selectWord(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            if (isChineseTraditional()) {
                ChineseConversionUtil.appendWithTransform(insertText, this.mScratchBuffer, 0, this.mScratchInt[0], isChineseTraditional());
            } else {
                insertText.append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            }
        }
        return success;
    }

    public boolean setContext(CharSequence newContext) {
        if (newContext == null) {
            return false;
        }
        if (this.mTextContext != null && this.mTextContext.equals(newContext)) {
            return false;
        }
        this.mTextContext = newContext.toString();
        return setContext(this.inputContext, this.mTextContext.toCharArray(), this.mTextContext.length());
    }

    public boolean setContext(CharSequence newContext, boolean update) {
        if (newContext == null) {
            return false;
        }
        if (update) {
            boolean result = setContext(newContext);
            return result;
        }
        this.mTextContext = newContext.toString();
        return false;
    }

    public boolean breakContext() {
        return breakContext(this.inputContext);
    }

    public boolean resetUserDictionary() {
        return resetUserDictionary(this.inputContext);
    }

    public boolean getWord(int wordIndex, List<CharSequence> wordList, AtomicInteger wdSource) {
        boolean success = getWord(this.inputContext, wordIndex, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length, this.mScratchSpellBuffer, this.mScratchSpellInt, this.mScratchIntWordSource);
        if (success && wordIndex >= 0 && wordIndex < 256) {
            this.mGetStringBufferArray[wordIndex].setLength(0);
            if (isChineseTraditional()) {
                ChineseConversionUtil.appendWithTransform(this.mGetStringBufferArray[wordIndex], this.mScratchBuffer, 0, this.mScratchInt[0], isChineseTraditional());
            } else {
                this.mGetStringBufferArray[wordIndex].append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            }
            if (this.mScratchInt[0] > 0) {
                char comp = componentToExternal(this.mScratchBuffer[0]);
                if (comp != 0) {
                    this.mGetStringBufferArray[wordIndex].setCharAt(0, comp);
                    this.mGetStringBufferArray[wordIndex].insert(0, (char) 40959);
                }
                wordList.add(wordList.size(), this.mGetStringBufferArray[wordIndex]);
            }
            wdSource.set(this.mScratchIntWordSource[0]);
        }
        return success;
    }

    public boolean getWord(int index, StringBuilder word, AtomicInteger wordSource) {
        word.setLength(0);
        wordSource.set(0);
        boolean success = getWord(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length, this.mScratchSpellBuffer, this.mScratchSpellInt, this.mScratchIntWordSource);
        if (success) {
            if (isChineseTraditional()) {
                ChineseConversionUtil.appendWithTransform(word, this.mScratchBuffer, 0, this.mScratchInt[0], isChineseTraditional());
            } else {
                word.append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            }
            normalizeComponents(word);
            wordSource.set(this.mScratchIntWordSource[0]);
        }
        return success;
    }

    public boolean getWordSpell(int index, StringBuilder wordSpell) {
        wordSpell.setLength(0);
        boolean success = getWord(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length, this.mScratchSpellBuffer, this.mScratchSpellInt, this.mScratchIntWordSource);
        wordSpell.insert(0, this.mScratchSpellBuffer, 0, this.mScratchSpellInt[0]);
        return success;
    }

    public List<CharSequence> getPrefixes(AtomicInteger defaultPrefixIndex) {
        defaultPrefixIndex.set(getActivePrefixIndex());
        recyclePrefixPool();
        getPrefixesInternal();
        return this.mPrefixList;
    }

    public List<CharSequence> getSuffixes(AtomicInteger defaultSuffixIndex) {
        defaultSuffixIndex.set(getActiveSuffixIndex());
        recycleSuffixPool();
        getSuffixesInternal();
        return this.mSuffixList;
    }

    public int getPrefixesCount() {
        return getPrefixCount(this.inputContext);
    }

    public int getSuffixesCount() {
        return getSuffixCount(this.inputContext);
    }

    private List<CharSequence> getPrefixesInternal() {
        int i = 0;
        while (true) {
            if (i < 256) {
                int prefixPoolSize = this.mPrefixPool.size();
                StringBuilder prefix = prefixPoolSize > 0 ? new StringBuilder(this.mPrefixPool.remove(prefixPoolSize - 1)) : new StringBuilder(112);
                if (getPrefix(i, prefix)) {
                    this.mPrefixList.add(prefix);
                    i++;
                } else {
                    this.mPrefixPool.add(prefix);
                    break;
                }
            } else {
                break;
            }
        }
        return this.mPrefixList;
    }

    private List<CharSequence> getSuffixesInternal() {
        int i = 0;
        while (true) {
            if (i < 256) {
                int suffixPoolSize = this.mSuffixPool.size();
                StringBuilder suffix = suffixPoolSize > 0 ? (StringBuilder) this.mSuffixPool.remove(suffixPoolSize - 1) : new StringBuilder(112);
                if (getSuffix(i, suffix)) {
                    this.mSuffixList.add(suffix);
                    i++;
                } else {
                    this.mSuffixPool.add(suffix);
                    break;
                }
            } else {
                break;
            }
        }
        return this.mSuffixList;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, int[] defaultWordIndex, int countToGet) {
        defaultWordIndex[0] = 0;
        this.mWordList.clear();
        this.mWdSourceList.clear();
        getMoreWords(false, countToGet);
        getInlineString(defaultWord);
        return this.mWordList;
    }

    public List<AtomicInteger> getWordsSource() {
        return this.mWdSourceList;
    }

    public List<CharSequence> getMoreWords(boolean isExactKeyboardInputShowable, int countToGet) {
        int getMax = Math.min(this.mWordList.size() + countToGet, 256);
        if (isExactKeyboardInputShowable) {
            getMax = Math.min(this.mWordList.size() + countToGet, R.styleable.ThemeTemplate_chineseFunctionBarEmoji);
        }
        if (this.mWdSourceList.size() != this.mWordList.size()) {
            return null;
        }
        for (int i = this.mWordList.size(); i < getMax; i++) {
            int wordIndex = i;
            if (isExactKeyboardInputShowable) {
                wordIndex = i - 1;
            }
            AtomicInteger wdSource = new AtomicInteger();
            if (getWord(wordIndex, this.mWordList, wdSource)) {
                this.mWdSourceList.add(i, wdSource);
            }
        }
        return this.mWordList;
    }

    private void recyclePrefixPool() {
        int prefixPoolSize = this.mPrefixPool.size();
        for (int prefixListSize = this.mPrefixList.size(); prefixPoolSize < 256 && prefixListSize > 0; prefixListSize--) {
            CharSequence collect = this.mPrefixList.remove(prefixListSize - 1);
            if (collect != null) {
                this.mPrefixPool.add(collect);
                prefixPoolSize++;
            }
        }
        this.mPrefixList.clear();
    }

    private void recycleSuffixPool() {
        int suffixPoolSize = this.mSuffixPool.size();
        for (int suffixListSize = this.mSuffixList.size(); suffixPoolSize < 256 && suffixListSize > 0; suffixListSize--) {
            CharSequence collect = this.mSuffixList.remove(suffixListSize - 1);
            if (collect != null) {
                this.mSuffixPool.add(collect);
                suffixPoolSize++;
            }
        }
        this.mSuffixList.clear();
    }

    private boolean getSpellInternal(int index, StringBuilder spell) {
        if (index > 0) {
            return false;
        }
        spell.setLength(0);
        this.mDelimiterCounts = 0;
        boolean success = getSpell(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            for (int i = 0; i < this.mScratchInt[0]; i++) {
                if (this.mScratchBuffer[i] == '_') {
                    this.mDelimiterCounts++;
                    this.mScratchBuffer[i] = mChineseSegmentDelimiter;
                } else if (this.mScratchBuffer[i] >= 177 && this.mScratchBuffer[i] <= 181) {
                    this.mScratchBuffer[i] = mTones[this.mScratchBuffer[i] - 177];
                }
            }
            spell.append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            int mode = getInputMode(this.inputContext);
            if (1 == mode) {
                normalizeBPMF(spell);
                return success;
            }
            if (2 == mode) {
                normalizeStroke(spell);
                return success;
            }
            if (4 == mode || 5 == mode) {
                normalizeCangjie(spell);
                return success;
            }
            if (mode == 0) {
                normalizePinyin(spell);
                return success;
            }
            return success;
        }
        return success;
    }

    public boolean getTailDoublePinyinUnicode(AtomicInteger unicode) {
        unicode.set(0);
        boolean ret = getTailDoublePinyinUnicode(this.inputContext, this.mScratchInt);
        if (ret) {
            unicode.set(this.mScratchInt[0]);
        }
        return ret;
    }

    public boolean isHasSegmentDelimiter() {
        boolean hasDelimiter = false;
        if (getSpell(this.inputContext, 0, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length)) {
            for (int i = 0; i < this.mScratchInt[0]; i++) {
                if (this.mScratchBuffer[i] == '_') {
                    hasDelimiter = true;
                }
            }
        }
        return hasDelimiter;
    }

    private boolean getPrefix(int index, StringBuilder prefix) {
        prefix.setLength(0);
        boolean success = getPrefix(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            prefix.append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            if (getInputMode(this.inputContext) == 1) {
                normalizeBPMF(prefix);
            }
        }
        return success;
    }

    private boolean getSuffix(int index, StringBuilder suffix) {
        suffix.setLength(0);
        boolean success = getSuffix(this.inputContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            suffix.append(this.mScratchBuffer, 0, this.mScratchInt[0]);
            if (getInputMode(this.inputContext) == 1) {
                normalizeBPMF(suffix);
            }
        }
        return success;
    }

    public int getActivePrefixIndex() {
        return getActivePrefixIndex(this.inputContext);
    }

    public int getActiveSuffixIndex() {
        return getActiveSuffixIndex(this.inputContext);
    }

    public boolean setActivePrefixIndex(int prefixIndex) {
        return setActivePrefixIndex(this.inputContext, prefixIndex);
    }

    public int clearCommonChar() {
        return clearCommonChar(this.inputContext);
    }

    public int setFullSentence() {
        return setFullSentence(this.inputContext);
    }

    public boolean isFullSentenceActive() {
        return isFullSentenceActive(this.inputContext);
    }

    public int getCharSpell(int baseChar, int altNum, int bGetTone, StringBuffer spell) {
        char[] spellArray = new char[20];
        int status = getCharSpell(this.inputContext, baseChar, altNum, bGetTone, spellArray);
        if (status == 0) {
            spell.append(spellArray);
        }
        return status;
    }

    private int cangjieToInternal(int c) {
        for (int i = 0; i < mCangjie.length; i++) {
            if (mCangjie[i] == c) {
                return i + 65;
            }
        }
        return c;
    }

    private char componentToExternal(char c) {
        if (61112 <= c && c <= 61311) {
            new StringBuilder("componentToExternal...convert Simplified Chinese Stroke: ").append(Integer.toHexString(c)).append(" to: ").append(mCompSimp[c - 61112]);
            return mCompSimp[c - 61112];
        }
        if (61320 <= c && c <= 61728) {
            new StringBuilder("componentToExternal...convert Traditional Chinese Stroke: ").append(Integer.toHexString(c)).append(" to: ").append(mCompTrad[c - 61320]);
            return mCompTrad[c - 61320];
        }
        return (char) 0;
    }

    private void normalizeComponents(StringBuilder s) {
        char comp = componentToExternal(s.charAt(0));
        if (comp != 0) {
            s.setCharAt(0, comp);
            s.insert(0, (char) 40959);
        }
    }

    private void normalizeCangjie(StringBuilder s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ('A' <= c && c <= 'Z') {
                s.setCharAt(i, mCangjie[c - 'A']);
            }
        }
    }

    private void normalizePinyin(StringBuilder s) {
        if (s.length() != 0) {
            this.mPinyinInline.setLength(0);
            for (int i = 0; i < s.length() - 1; i++) {
                char previousChar = s.charAt(i);
                char nextChar = s.charAt(i + 1);
                if ('A' <= previousChar && previousChar <= 'Z' && (('A' <= nextChar && nextChar <= 'Z') || nextChar == ' ')) {
                    this.mPinyinInline.append((char) (previousChar + mChineseSegmentDelimiter));
                    this.mPinyinInline.append(mChineseDelimiter);
                }
                if ('A' <= previousChar && previousChar <= 'Z' && 'a' <= nextChar && nextChar <= 'z') {
                    this.mPinyinInline.append((char) (previousChar + mChineseSegmentDelimiter));
                } else if ('a' <= previousChar && previousChar <= 'z' && (('A' <= nextChar && nextChar <= 'Z') || nextChar == ' ')) {
                    this.mPinyinInline.append(previousChar);
                    this.mPinyinInline.append(mChineseDelimiter);
                } else if ('a' <= previousChar && previousChar <= 'z' && nextChar == '\'') {
                    this.mPinyinInline.append(previousChar);
                } else if ('A' <= previousChar && previousChar <= 'Z' && nextChar == '\'') {
                    this.mPinyinInline.append((char) (previousChar + mChineseSegmentDelimiter));
                } else if ((('A' <= nextChar && nextChar <= 'Z') || nextChar == ' ') && previousChar == '\'') {
                    this.mPinyinInline.append(previousChar);
                } else if ('a' <= previousChar && previousChar <= 'z' && 'a' <= nextChar && nextChar <= 'z') {
                    this.mPinyinInline.append(previousChar);
                } else if ('0' <= previousChar && previousChar <= '9') {
                    this.mPinyinInline.append(previousChar);
                    if (nextChar == ' ') {
                        this.mPinyinInline.append(mChineseDelimiter);
                    }
                }
            }
            char lastChar = s.charAt(s.length() - 1);
            if ('A' <= lastChar && lastChar <= 'Z') {
                this.mPinyinInline.append((char) (lastChar + mChineseSegmentDelimiter));
            } else {
                this.mPinyinInline.append(lastChar);
            }
            s.setLength(0);
            s.append((CharSequence) this.mPinyinInline);
        }
    }

    private char tolowerBPMF(char c) {
        if (c < 61957 || c > 61994) {
            return c;
        }
        char cRet = (char) ((c - 61957) + 12549);
        return cRet;
    }

    private void normalizeBPMF(StringBuilder s) {
        int len = s.length();
        int i = 0;
        while (i < len) {
            char c = s.charAt(i);
            char cLower = tolowerBPMF(c);
            if (c != cLower) {
                s.setCharAt(i, cLower);
                if (i > 0 && s.charAt(i - 1) != ' ' && s.charAt(i - 1) != '\'') {
                    s.insert(i, '_');
                    i++;
                    len++;
                }
            }
            i++;
        }
    }

    private void normalizeStroke(StringBuilder s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c > 0 && c <= 6) {
                s.setCharAt(i, mStrokes[c - 1]);
            }
        }
    }

    public List<CharSequence> getPopupList() {
        addAllContextWord();
        return this.mPopupList;
    }

    public List<AtomicInteger> getPopupWordSourceList() {
        return this.mPopupWdSourceList;
    }

    public void addAllContextWord() {
        this.mPopupList.clear();
        this.mPopupWdSourceList.clear();
        for (int i = 0; i < 256; i++) {
            AtomicInteger wdSource = new AtomicInteger();
            if (getWord(i, this.mPopupList, wdSource)) {
                this.mPopupWdSourceList.add(i, wdSource);
            }
        }
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getInputCoreCategory() {
        return 2;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public Candidates getCandidates() {
        return getCandidates(Candidates.Source.TRACE, 256);
    }

    public int setCategoryDb(int languageId, int categoryId, boolean isEnable) {
        return setCategoryDb(this.inputContext, languageId, categoryId, isEnable ? 1 : 0);
    }

    public int getExactWord(StringBuffer exactWord) {
        int mode = getInputMode(this.inputContext);
        exactWord.setLength(0);
        if (mode != 0 && 1 != mode) {
            return -1;
        }
        char[] wordArray = new char[112];
        int status = getExactWord(this.inputContext, wordArray);
        if (status == 0) {
            String word = new String(wordArray);
            exactWord.append(word.trim());
            return status;
        }
        return status;
    }

    public char chineseDelimiter() {
        return mChineseDelimiter;
    }

    public int getDelimiterSize() {
        return this.mDelimiterCounts;
    }

    public void setBilingual(boolean enabled) {
        setBilingual(this.inputContext, enabled);
    }

    public boolean hasBilingualPrefix() {
        return hasBilingualPrefix(this.inputContext);
    }

    public int predictionCloudGetCharacterset() {
        int[] settings = new int[8];
        if (predictionCloudGetSettings(this.inputContext, settings) == XT9Status.ET9STATUS_NONE.ordinal()) {
            return settings[3];
        }
        return 0;
    }

    public byte[] predictionCloudGetData() {
        byte[] dataBuffer;
        int status;
        short[] actualBufferLength = new short[1];
        do {
            actualBufferLength[0] = (short) (actualBufferLength[0] + 128);
            dataBuffer = new byte[actualBufferLength[0] + 1];
            status = predictionCloudGetInputData(this.inputContext, dataBuffer, actualBufferLength);
        } while (status == 30);
        if (status == XT9Status.ET9STATUS_NONE.ordinal()) {
            return Arrays.copyOf(dataBuffer, (int) actualBufferLength[0]);
        }
        return null;
    }

    public int predictionCloudCommitPhrase(String phrase, String spell, String fullspell, int[] attributes) {
        return predictionCloudCommitPhrase(this.inputContext, phrase, spell, fullspell, attributes);
    }

    public char[] getBPMFTones() {
        return mTones;
    }

    public boolean isChineseTraditional() {
        return this.mLanguageID == 224 || this.mLanguageID == 226;
    }

    public boolean getExactInputText(StringBuilder spell) {
        if (getInputMode(this.inputContext) != 0) {
            return false;
        }
        spell.setLength(0);
        boolean success = getSpell(this.inputContext, 0, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length);
        if (success) {
            for (int i = 0; i < this.mScratchInt[0]; i++) {
                if (this.mScratchBuffer[i] != '_') {
                    spell.append(this.mScratchBuffer[i]);
                }
            }
        }
        return success;
    }

    public boolean tryBuildingWordCandidateList(boolean clearKeyInvalidInput) {
        return tryBuildingWordCandidateList(this.inputContext, clearKeyInvalidInput);
    }

    public boolean multiTapBuildWordCandidateList() {
        return multiTapBuildWordCandidateList(this.inputContext);
    }

    public void setMultiTapHasInvalidKey(boolean hasInvalidKey) {
        setMultiTapHasInvalidKey(this.inputContext, hasInvalidKey);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void onDlmEvent(byte[] event, int len, boolean highPriority) throws Exception {
        super.onDlmEvent(event, len, highPriority);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected boolean exportDlmAsEvents() {
        return false;
    }

    public boolean dlmAdd(String phrase, String spell) {
        return dlmAdd(this.inputContext, phrase.toCharArray(), spell.toCharArray());
    }

    public boolean dlmDelete(String phrase) {
        return dlmDelete(this.inputContext, phrase.toCharArray());
    }

    public boolean dlmGetNext(int index, StringBuilder phrase, StringBuilder spell) {
        phrase.setLength(0);
        spell.setLength(0);
        if (!dlmGetNext(this.inputContext, index, mPhraseBuffer, mPhraseLength, 112, mSpellBuffer, mSpellLength, 112)) {
            return false;
        }
        phrase.append(mPhraseBuffer, 0, mPhraseLength[0]);
        spell.append(mSpellBuffer, 0, mSpellLength[0]);
        return true;
    }

    public int dlmCount() {
        return dlmCount(this.inputContext);
    }

    public void dlmReset() {
        dlmReset(this.inputContext);
    }

    public long dlmExport(String filePath) {
        return dlmExport(this.inputContext, filePath);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean isLanguageHaveEmojiPrediction() {
        return true;
    }
}
