package jp.co.omronsoft.openwnn;

/* loaded from: classes.dex */
public class OpenWnnDictionaryImplJni {
    public static final int APPROX_PATTERN_EN_QWERTY_NEAR = 2;
    public static final int APPROX_PATTERN_EN_QWERTY_NEAR_UPPER = 3;
    public static final int APPROX_PATTERN_EN_TOLOWER = 1;
    public static final int APPROX_PATTERN_EN_TOUPPER = 0;
    public static final int APPROX_PATTERN_JAJP_12KEY_NORMAL = 4;
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

    public static final native void clearApproxPatterns(long j);

    public static final native int clearDictionaryParameters(long j);

    public static final native void clearResult(long j);

    public static final native String[] createBindArray(long j, String str, int i, int i2);

    public static final native String createQueryStringBase(long j, int i, int i2, String str);

    public static final native long createWnnWork(String str);

    public static final native int freeWnnWork(long j);

    public static final native String[] getApproxPattern(long j, String str);

    public static final native String getCandidate(long j);

    public static final native byte[] getConnectArray(long j, int i);

    public static final native int getFrequency(long j);

    public static final native int getLeftPartOfSpeech(long j);

    public static final native int getLeftPartOfSpeechSpecifiedType(long j, int i);

    public static final native int getNextWord(long j, int i);

    public static final native int getNumberOfLeftPOS(long j);

    public static final native int getNumberOfRightPOS(long j);

    public static final native int getRightPartOfSpeech(long j);

    public static final native int getRightPartOfSpeechSpecifiedType(long j, int i);

    public static final native String getStroke(long j);

    public static final native int searchWord(long j, int i, int i2, String str);

    public static final native int selectWord(long j);

    public static final native int setApproxPattern(long j, int i);

    public static final native int setApproxPattern(long j, String str, String str2);

    public static final native int setCandidate(long j, String str);

    public static final native int setDictionaryParameter(long j, int i, int i2, int i3);

    public static final native int setLeftPartOfSpeech(long j, int i);

    public static final native int setRightPartOfSpeech(long j, int i);

    public static final native int setStroke(long j, String str);
}
