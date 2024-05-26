package jp.co.omronsoft.openwnn;

/* loaded from: classes.dex */
public interface WnnDictionary {
    public static final int APPROX_PATTERN_EN_QWERTY_NEAR = 2;
    public static final int APPROX_PATTERN_EN_QWERTY_NEAR_UPPER = 3;
    public static final int APPROX_PATTERN_EN_TOLOWER = 1;
    public static final int APPROX_PATTERN_EN_TOUPPER = 0;
    public static final int APPROX_PATTERN_JAJP_12KEY_NORMAL = 4;
    public static final int INDEX_LEARN_DICTIONARY = -2;
    public static final int INDEX_USER_DICTIONARY = -1;
    public static final int ORDER_BY_FREQUENCY = 0;
    public static final int ORDER_BY_KEY = 1;
    public static final int POS_TYPE_BUNTOU = 3;
    public static final int POS_TYPE_CHIMEI = 8;
    public static final int POS_TYPE_JINMEI = 7;
    public static final int POS_TYPE_KIGOU = 9;
    public static final int POS_TYPE_MEISI = 6;
    public static final int POS_TYPE_SUUJI = 5;
    public static final int POS_TYPE_TANKANJI = 4;
    public static final int POS_TYPE_V1 = 0;
    public static final int POS_TYPE_V2 = 1;
    public static final int POS_TYPE_V3 = 2;
    public static final int SEARCH_EXACT = 0;
    public static final int SEARCH_LINK = 2;
    public static final int SEARCH_PREFIX = 1;

    int addWordToUserDictionary(WnnWord wnnWord);

    int addWordToUserDictionary(WnnWord[] wnnWordArr);

    void clearApproxPattern();

    int clearDictionary();

    int clearLearnDictionary();

    int clearUserDictionary();

    byte[][] getConnectMatrix();

    WnnWord getNextWord();

    WnnWord getNextWord(int i);

    WnnPOS getPOS(int i);

    WnnWord[] getUserDictionaryWords();

    boolean isActive();

    int learnWord(WnnWord wnnWord);

    int learnWord(WnnWord wnnWord, WnnWord wnnWord2);

    int removeWordFromUserDictionary(WnnWord wnnWord);

    int removeWordFromUserDictionary(WnnWord[] wnnWordArr);

    int searchWord(int i, int i2, String str);

    int searchWord(int i, int i2, String str, WnnWord wnnWord);

    int setApproxPattern(int i);

    int setApproxPattern(String str, String str2);

    int setDictionary(int i, int i2, int i3);

    boolean setInUseState(boolean z);
}
