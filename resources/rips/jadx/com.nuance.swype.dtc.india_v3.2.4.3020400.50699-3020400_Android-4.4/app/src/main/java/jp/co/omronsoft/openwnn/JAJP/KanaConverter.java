package jp.co.omronsoft.openwnn.JAJP;

import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.internal.common.Document;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jp.co.omronsoft.openwnn.WnnDictionary;
import jp.co.omronsoft.openwnn.WnnPOS;
import jp.co.omronsoft.openwnn.WnnWord;

/* loaded from: classes.dex */
public class KanaConverter {
    private WnnPOS mPosDefault;
    private WnnPOS mPosNumber;
    private WnnPOS mPosSymbol;
    private static final HashMap<String, String> mHanSuujiMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.1
        private static final long serialVersionUID = 1;

        {
            put("あ", "1");
            put("い", MessageAPI.LANGUAGE);
            put("う", MessageAPI.CLASS);
            put("え", "1111");
            put("お", "11111");
            put("ぁ", "111111");
            put("ぃ", "1111111");
            put("ぅ", "11111111");
            put("ぇ", "111111111");
            put("ぉ", "1111111111");
            put("か", MessageAPI.DELAYED_FROM);
            put("き", MessageAPI.VALUE);
            put("く", "222");
            put("け", "2222");
            put("こ", "22222");
            put("さ", MessageAPI.SESSION_ID);
            put("し", MessageAPI.REPORTING_FREQUENCY);
            put("す", "333");
            put("せ", "3333");
            put("そ", "33333");
            put("た", MessageAPI.TRANSACTION_ID);
            put("ち", MessageAPI.ALM_LIST);
            put("つ", "444");
            put("て", "4444");
            put("と", "44444");
            put("っ", "444444");
            put("な", MessageAPI.DEVICE_ID);
            put("に", MessageAPI.USERNAME);
            put("ぬ", "555");
            put("ね", "5555");
            put("の", "55555");
            put("は", "6");
            put("ひ", MessageAPI.NEW_PASSWORD);
            put("ふ", "666");
            put("へ", "6666");
            put("ほ", "66666");
            put("ま", MessageAPI.MESSAGE);
            put("み", MessageAPI.CATEGORY_LIST);
            put("む", "777");
            put("め", "7777");
            put("も", "77777");
            put("や", "8");
            put("ゆ", MessageAPI.CHANGE_LOG);
            put("よ", "888");
            put("ゃ", "8888");
            put("ゅ", "88888");
            put("ょ", "888888");
            put("ら", MessageAPI.PROPERTIES_TO_VALIDATE);
            put("り", MessageAPI.INPUT_TYPE);
            put("る", "999");
            put("れ", "9999");
            put("ろ", "99999");
            put("わ", "0");
            put("を", "00");
            put("ん", "000");
            put("ゎ", "0000");
            put("ー", "00000");
        }
    };
    private static final HashMap<String, String> mZenSuujiMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.2
        private static final long serialVersionUID = 1;

        {
            put("あ", "１");
            put("い", "１１");
            put("う", "１１１");
            put("え", "１１１１");
            put("お", "１１１１１");
            put("ぁ", "１１１１１１");
            put("ぃ", "１１１１１１１");
            put("ぅ", "１１１１１１１１");
            put("ぇ", "１１１１１１１１１");
            put("ぉ", "１１１１１１１１１１");
            put("か", "２");
            put("き", "２２");
            put("く", "２２２");
            put("け", "２２２２");
            put("こ", "２２２２２");
            put("さ", "３");
            put("し", "３３");
            put("す", "３３３");
            put("せ", "３３３３");
            put("そ", "３３３３３");
            put("た", "４");
            put("ち", "４４");
            put("つ", "４４４");
            put("て", "４４４４");
            put("と", "４４４４４");
            put("っ", "４４４４４４");
            put("な", "５");
            put("に", "５５");
            put("ぬ", "５５５");
            put("ね", "５５５５");
            put("の", "５５５５５");
            put("は", "６");
            put("ひ", "６６");
            put("ふ", "６６６");
            put("へ", "６６６６");
            put("ほ", "６６６６６");
            put("ま", "７");
            put("み", "７７");
            put("む", "７７７");
            put("め", "７７７７");
            put("も", "７７７７７");
            put("や", "８");
            put("ゆ", "８８");
            put("よ", "８８８");
            put("ゃ", "８８８８");
            put("ゅ", "８８８８８");
            put("ょ", "８８８８８８");
            put("ら", "９");
            put("り", "９９");
            put("る", "９９９");
            put("れ", "９９９９");
            put("ろ", "９９９９９");
            put("わ", "０");
            put("を", "００");
            put("ん", "０００");
            put("ゎ", "００００");
            put("ー", "０００００");
        }
    };
    private static final HashMap<String, String> mHanKataMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.3
        private static final long serialVersionUID = 1;

        {
            put("あ", "ｱ");
            put("い", "ｲ");
            put("う", "ｳ");
            put("え", "ｴ");
            put("お", "ｵ");
            put("ぁ", "ｧ");
            put("ぃ", "ｨ");
            put("ぅ", "ｩ");
            put("ぇ", "ｪ");
            put("ぉ", "ｫ");
            put("ヴぁ", "ｳﾞｧ");
            put("ヴぃ", "ｳﾞｨ");
            put("ヴ", "ｳﾞ");
            put("ヴぇ", "ｳﾞｪ");
            put("ヴぉ", "ｳﾞｫ");
            put("か", "ｶ");
            put("き", "ｷ");
            put("く", "ｸ");
            put("け", "ｹ");
            put("こ", "ｺ");
            put("が", "ｶﾞ");
            put("ぎ", "ｷﾞ");
            put("ぐ", "ｸﾞ");
            put("げ", "ｹﾞ");
            put("ご", "ｺﾞ");
            put("さ", "ｻ");
            put("し", "ｼ");
            put("す", "ｽ");
            put("せ", "ｾ");
            put("そ", "ｿ");
            put("ざ", "ｻﾞ");
            put("じ", "ｼﾞ");
            put("ず", "ｽﾞ");
            put("ぜ", "ｾﾞ");
            put("ぞ", "ｿﾞ");
            put("た", "ﾀ");
            put("ち", "ﾁ");
            put("つ", "ﾂ");
            put("て", "ﾃ");
            put("と", "ﾄ");
            put("っ", "ｯ");
            put("だ", "ﾀﾞ");
            put("ぢ", "ﾁﾞ");
            put("づ", "ﾂﾞ");
            put("で", "ﾃﾞ");
            put("ど", "ﾄﾞ");
            put("な", "ﾅ");
            put("に", "ﾆ");
            put("ぬ", "ﾇ");
            put("ね", "ﾈ");
            put("の", "ﾉ");
            put("は", "ﾊ");
            put("ひ", "ﾋ");
            put("ふ", "ﾌ");
            put("へ", "ﾍ");
            put("ほ", "ﾎ");
            put("ば", "ﾊﾞ");
            put("び", "ﾋﾞ");
            put("ぶ", "ﾌﾞ");
            put("べ", "ﾍﾞ");
            put("ぼ", "ﾎﾞ");
            put("ぱ", "ﾊﾟ");
            put("ぴ", "ﾋﾟ");
            put("ぷ", "ﾌﾟ");
            put("ぺ", "ﾍﾟ");
            put("ぽ", "ﾎﾟ");
            put("ま", "ﾏ");
            put("み", "ﾐ");
            put("む", "ﾑ");
            put("め", "ﾒ");
            put("も", "ﾓ");
            put("や", "ﾔ");
            put("ゆ", "ﾕ");
            put("よ", "ﾖ");
            put("ゃ", "ｬ");
            put("ゅ", "ｭ");
            put("ょ", "ｮ");
            put("ら", "ﾗ");
            put("り", "ﾘ");
            put("る", "ﾙ");
            put("れ", "ﾚ");
            put("ろ", "ﾛ");
            put("わ", "ﾜ");
            put("を", "ｦ");
            put("ん", "ﾝ");
            put("ゎ", "ﾜ");
            put("ー", "ｰ");
        }
    };
    private static final HashMap<String, String> mZenKataMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.4
        private static final long serialVersionUID = 1;

        {
            put("あ", "ア");
            put("い", "イ");
            put("う", "ウ");
            put("え", "エ");
            put("お", "オ");
            put("ぁ", "ァ");
            put("ぃ", "ィ");
            put("ぅ", "ゥ");
            put("ぇ", "ェ");
            put("ぉ", "ォ");
            put("ヴぁ", "ヴァ");
            put("ヴぃ", "ヴィ");
            put("ヴ", "ヴ");
            put("ヴぇ", "ヴェ");
            put("ヴぉ", "ヴォ");
            put("か", "カ");
            put("き", "キ");
            put("く", "ク");
            put("け", "ケ");
            put("こ", "コ");
            put("が", "ガ");
            put("ぎ", "ギ");
            put("ぐ", "グ");
            put("げ", "ゲ");
            put("ご", "ゴ");
            put("さ", "サ");
            put("し", "シ");
            put("す", "ス");
            put("せ", "セ");
            put("そ", "ソ");
            put("ざ", "ザ");
            put("じ", "ジ");
            put("ず", "ズ");
            put("ぜ", "ゼ");
            put("ぞ", "ゾ");
            put("た", "タ");
            put("ち", "チ");
            put("つ", "ツ");
            put("て", "テ");
            put("と", "ト");
            put("っ", "ッ");
            put("だ", "ダ");
            put("ぢ", "ヂ");
            put("づ", "ヅ");
            put("で", "デ");
            put("ど", "ド");
            put("な", "ナ");
            put("に", "ニ");
            put("ぬ", "ヌ");
            put("ね", "ネ");
            put("の", "ノ");
            put("は", "ハ");
            put("ひ", "ヒ");
            put("ふ", "フ");
            put("へ", "ヘ");
            put("ほ", "ホ");
            put("ば", "バ");
            put("び", "ビ");
            put("ぶ", "ブ");
            put("べ", "ベ");
            put("ぼ", "ボ");
            put("ぱ", "パ");
            put("ぴ", "ピ");
            put("ぷ", "プ");
            put("ぺ", "ペ");
            put("ぽ", "ポ");
            put("ま", "マ");
            put("み", "ミ");
            put("む", "ム");
            put("め", "メ");
            put("も", "モ");
            put("や", "ヤ");
            put("ゆ", "ユ");
            put("よ", "ヨ");
            put("ゃ", "ャ");
            put("ゅ", "ュ");
            put("ょ", "ョ");
            put("ら", "ラ");
            put("り", "リ");
            put("る", "ル");
            put("れ", "レ");
            put("ろ", "ロ");
            put("わ", "ワ");
            put("を", "ヲ");
            put("ん", "ン");
            put("ゎ", "ヮ");
            put("ー", "ー");
        }
    };
    private static final HashMap<String, String> mHanEijiMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.5
        private static final long serialVersionUID = 1;

        {
            put("あ", ".");
            put("い", "@");
            put("う", XMLResultsHandler.SEP_HYPHEN);
            put("え", Document.ID_SEPARATOR);
            put("お", "/");
            put("ぁ", ":");
            put("ぃ", "~");
            put("か", "A");
            put("き", "B");
            put("く", "C");
            put("さ", "D");
            put("し", "E");
            put("す", "F");
            put("た", "G");
            put("ち", "H");
            put("つ", "I");
            put("な", "J");
            put("に", "K");
            put("ぬ", "L");
            put("は", "M");
            put("ひ", "N");
            put("ふ", "O");
            put("ま", "P");
            put("み", "Q");
            put("む", "R");
            put("め", "S");
            put("や", "T");
            put("ゆ", "U");
            put("よ", "V");
            put("ら", "W");
            put("り", "X");
            put("る", "Y");
            put("れ", "Z");
            put("わ", XMLResultsHandler.SEP_HYPHEN);
        }
    };
    private static final HashMap<String, String> mZenEijiMap = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.6
        private static final long serialVersionUID = 1;

        {
            put("あ", "．");
            put("い", "＠");
            put("う", "ー");
            put("え", "＿");
            put("お", "／");
            put("ぁ", "：");
            put("ぃ", "〜");
            put("か", "Ａ");
            put("き", "Ｂ");
            put("く", "Ｃ");
            put("さ", "Ｄ");
            put("し", "Ｅ");
            put("す", "Ｆ");
            put("た", "Ｇ");
            put("ち", "Ｈ");
            put("つ", "Ｉ");
            put("な", "Ｊ");
            put("に", "Ｋ");
            put("ぬ", "Ｌ");
            put("は", "Ｍ");
            put("ひ", "Ｎ");
            put("ふ", "Ｏ");
            put("ま", "Ｐ");
            put("み", "Ｑ");
            put("む", "Ｒ");
            put("め", "Ｓ");
            put("や", "Ｔ");
            put("ゆ", "Ｕ");
            put("よ", "Ｖ");
            put("ら", "Ｗ");
            put("り", "Ｘ");
            put("る", "Ｙ");
            put("れ", "Ｚ");
            put("わ", "ー");
        }
    };
    private static final HashMap<String, String> mZenEijiMapQwety = new HashMap<String, String>() { // from class: jp.co.omronsoft.openwnn.JAJP.KanaConverter.7
        private static final long serialVersionUID = 1;

        {
            put("a", "ａ");
            put("b", "ｂ");
            put("c", "ｃ");
            put("d", "ｄ");
            put("e", "ｅ");
            put("f", "ｆ");
            put("g", "ｇ");
            put("h", "ｈ");
            put("i", "ｉ");
            put("j", "ｊ");
            put("k", "ｋ");
            put("l", "ｌ");
            put("m", "ｍ");
            put("n", "ｎ");
            put("o", "ｏ");
            put("p", "ｐ");
            put("q", "ｑ");
            put("r", "ｒ");
            put("s", "ｓ");
            put("t", "ｔ");
            put("u", "ｕ");
            put("v", "ｖ");
            put("w", "ｗ");
            put("x", "ｘ");
            put("y", "ｙ");
            put("z", "ｚ");
            put("A", "Ａ");
            put("B", "Ｂ");
            put("C", "Ｃ");
            put("D", "Ｄ");
            put("E", "Ｅ");
            put("F", "Ｆ");
            put("G", "Ｇ");
            put("H", "Ｈ");
            put("I", "Ｉ");
            put("J", "Ｊ");
            put("K", "Ｋ");
            put("L", "Ｌ");
            put("M", "Ｍ");
            put("N", "Ｎ");
            put("O", "Ｏ");
            put("P", "Ｐ");
            put("Q", "Ｑ");
            put("R", "Ｒ");
            put("S", "Ｓ");
            put("T", "Ｔ");
            put("U", "Ｕ");
            put("V", "Ｖ");
            put("W", "Ｗ");
            put("X", "Ｘ");
            put("Y", "Ｙ");
            put("Z", "Ｚ");
        }
    };
    private static final DecimalFormat mFormat = new DecimalFormat("###,###");
    private List<WnnWord> mAddCandidateList = new ArrayList();
    private StringBuffer mStringBuff = new StringBuffer();

    public void setDictionary(WnnDictionary dict) {
        this.mPosDefault = dict.getPOS(6);
        this.mPosNumber = dict.getPOS(5);
        this.mPosSymbol = dict.getPOS(9);
    }

    public List<WnnWord> createPseudoCandidateList(String inputHiragana, String inputRomaji, int keyBoardMode) {
        List<WnnWord> list = this.mAddCandidateList;
        list.clear();
        if (inputHiragana.length() != 0) {
            list.add(new WnnWord(inputHiragana, inputHiragana));
            if (createCandidateString(inputHiragana, mZenKataMap, this.mStringBuff)) {
                list.add(new WnnWord(this.mStringBuff.toString(), inputHiragana, this.mPosDefault));
            }
            if (createCandidateString(inputHiragana, mHanKataMap, this.mStringBuff)) {
                list.add(new WnnWord(this.mStringBuff.toString(), inputHiragana, this.mPosDefault));
            }
            if (keyBoardMode == 2) {
                createPseudoCandidateListForQwerty(inputHiragana, inputRomaji);
            } else {
                if (createCandidateString(inputHiragana, mHanSuujiMap, this.mStringBuff)) {
                    String convHanSuuji = this.mStringBuff.toString();
                    String convNumComma = convertNumber(convHanSuuji);
                    list.add(new WnnWord(convHanSuuji, inputHiragana, this.mPosNumber));
                    if (convNumComma != null) {
                        list.add(new WnnWord(convNumComma, inputHiragana, this.mPosNumber));
                    }
                }
                if (createCandidateString(inputHiragana, mZenSuujiMap, this.mStringBuff)) {
                    list.add(new WnnWord(this.mStringBuff.toString(), inputHiragana, this.mPosNumber));
                }
                if (createCandidateString(inputHiragana, mHanEijiMap, this.mStringBuff)) {
                    String convHanEiji = this.mStringBuff.toString();
                    String convHanEijiLower = convHanEiji.toLowerCase(Locale.getDefault());
                    list.add(new WnnWord(convHanEijiLower, inputHiragana, this.mPosSymbol));
                    list.add(new WnnWord(convertCaps(convHanEijiLower), inputHiragana, this.mPosSymbol));
                    list.add(new WnnWord(convHanEiji, inputHiragana, this.mPosSymbol));
                }
                if (createCandidateString(inputHiragana, mZenEijiMap, this.mStringBuff)) {
                    String convZenEiji = this.mStringBuff.toString();
                    String convZenEijiLower = convZenEiji.toLowerCase(Locale.JAPAN);
                    list.add(new WnnWord(convZenEijiLower, inputHiragana, this.mPosSymbol));
                    list.add(new WnnWord(convertCaps(convZenEijiLower), inputHiragana, this.mPosSymbol));
                    list.add(new WnnWord(convZenEiji, inputHiragana, this.mPosSymbol));
                }
            }
        }
        return list;
    }

    private void createPseudoCandidateListForQwerty(String inputHiragana, String inputRomaji) {
        List<WnnWord> list = this.mAddCandidateList;
        String convHanEijiLower = inputRomaji.toLowerCase(Locale.getDefault());
        list.add(new WnnWord(inputRomaji, inputHiragana, this.mPosDefault));
        list.add(new WnnWord(convHanEijiLower, inputHiragana, this.mPosSymbol));
        list.add(new WnnWord(convertCaps(convHanEijiLower), inputHiragana, this.mPosSymbol));
        list.add(new WnnWord(inputRomaji.toUpperCase(Locale.getDefault()), inputHiragana, this.mPosSymbol));
        if (createCandidateString(inputRomaji, mZenEijiMapQwety, this.mStringBuff)) {
            String convZenEiji = this.mStringBuff.toString();
            String convZenEijiLower = convZenEiji.toLowerCase(Locale.JAPAN);
            list.add(new WnnWord(convZenEiji, inputHiragana, this.mPosSymbol));
            list.add(new WnnWord(convZenEijiLower, inputHiragana, this.mPosSymbol));
            list.add(new WnnWord(convertCaps(convZenEijiLower), inputHiragana, this.mPosSymbol));
            list.add(new WnnWord(convZenEiji.toUpperCase(Locale.JAPAN), inputHiragana, this.mPosSymbol));
        }
    }

    private boolean createCandidateString(String input, HashMap<String, String> map, StringBuffer outBuf) {
        if (outBuf.length() > 0) {
            outBuf.delete(0, outBuf.length());
        }
        for (int index = 0; index < input.length(); index++) {
            String convChar = map.get(input.substring(index, index + 1));
            if (convChar == null) {
                return false;
            }
            outBuf.append(convChar);
        }
        return true;
    }

    private String convertCaps(String moji) {
        if (moji == null || moji.length() <= 0) {
            return "";
        }
        String tmp = moji.substring(0, 1).toUpperCase(Locale.JAPAN) + moji.substring(1).toLowerCase(Locale.JAPAN);
        return tmp;
    }

    private String convertNumber(String numComma) {
        try {
            return mFormat.format(Double.parseDouble(numComma));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
