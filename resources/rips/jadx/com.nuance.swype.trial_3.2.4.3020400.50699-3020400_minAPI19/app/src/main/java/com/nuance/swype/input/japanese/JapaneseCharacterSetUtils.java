package com.nuance.swype.input.japanese;

/* loaded from: classes.dex */
public class JapaneseCharacterSetUtils {
    private static final int HIRACYCLECOUNT = 57;
    private static final int HIRAMODECOUNT = 4;
    private static final int KANACOUNT = 48;
    private static final int RowsCount = 10;
    private static final int[] Rows = {5, 10, 15, 20, 25, 30, 35, 38, 43, 49};
    private static final int[] RowsCycle = {10, 15, 20, 26, 31, 36, 41, 47, 52, 57};
    private static final char[] HiraToDigit = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private static final char[] HiraToDigitFull = {65297, 65298, 65299, 65300, 65301, 65302, 65303, 65304, 65305, 65296};
    private static final char[] HiraganaSmall = {12353, 12355, 12357, 12359, 12361, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12387, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12419, 12421, 12423, 0, 0, 0, 0, 0, 12430, 0, 0, 0, 0};
    private static final char[] HiraganaBig = {12354, 12356, 12358, 12360, 12362, 12363, 12365, 12367, 12369, 12371, 12373, 12375, 12377, 12379, 12381, 12383, 12385, 12388, 12390, 12392, 12394, 12395, 12396, 12397, 12398, 12399, 12402, 12405, 12408, 12411, 12414, 12415, 12416, 12417, 12418, 12420, 12422, 12424, 12425, 12426, 12427, 12428, 12429, 12431, 12432, 12433, 12434, 12435};
    private static final char[] HiraganaCycle = {12354, 12356, 12358, 12360, 12362, 12353, 12355, 12357, 12359, 12361, 12363, 12365, 12367, 12369, 12371, 12373, 12375, 12377, 12379, 12381, 12383, 12385, 12388, 12390, 12392, 12387, 12394, 12395, 12396, 12397, 12398, 12399, 12402, 12405, 12408, 12411, 12414, 12415, 12416, 12417, 12418, 12420, 12422, 12424, 12419, 12421, 12423, 12425, 12426, 12427, 12428, 12429, 12431, 12434, 12435, 12540, 12430};
    private static final char[] HiraganaVo = {0, 0, 12532, 0, 0, 12364, 12366, 12368, 12370, 12372, 12374, 12376, 12378, 12380, 12382, 12384, 12386, 12389, 12391, 12393, 0, 0, 0, 0, 0, 12400, 12403, 12406, 12409, 12412, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final char[] HiraganaSemiVo = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12401, 12404, 12407, 12410, 12413, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final char[] KatakanaSmall = {12449, 12451, 12453, 12455, 12457, 12533, 0, 0, 12534, 0, 0, 0, 0, 0, 0, 0, 0, 12483, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12515, 12517, 12519, 0, 0, 0, 0, 0, 12526, 0, 0, 0, 0};
    private static final char[] KatakanaBig = {12450, 12452, 12454, 12456, 12458, 12459, 12461, 12463, 12465, 12467, 12469, 12471, 12473, 12475, 12477, 12479, 12481, 12484, 12486, 12488, 12490, 12491, 12492, 12493, 12494, 12495, 12498, 12501, 12504, 12507, 12510, 12511, 12512, 12513, 12514, 12516, 12518, 12520, 12521, 12522, 12523, 12524, 12525, 12527, 12528, 12529, 12530, 12531};
    private static final char[] KatakanaVo = {0, 0, 12532, 0, 0, 12460, 12462, 12464, 12466, 12468, 12470, 12472, 12474, 12476, 12478, 12480, 12482, 12485, 12487, 12489, 0, 0, 0, 0, 0, 12496, 12499, 12502, 12505, 12508, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final char[] KatakanaSemiVo = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12497, 12500, 12503, 12506, 12509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final char[][] HiraModes = {HiraganaBig, HiraganaSmall, HiraganaVo, HiraganaSemiVo};

    public static boolean isKatakana(char charin) {
        for (int i = 0; i < 48; i++) {
            if (charin == KatakanaSmall[i] || charin == KatakanaBig[i] || charin == KatakanaVo[i] || charin == KatakanaSemiVo[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHiragana(char charin) {
        for (int i = 0; i < 48; i++) {
            if (charin == HiraganaSmall[i] || charin == HiraganaBig[i] || charin == HiraganaVo[i] || charin == HiraganaSemiVo[i]) {
                return true;
            }
        }
        return false;
    }

    public static char convertHira(char charin) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 48; j++) {
                if (HiraModes[i][j] == charin) {
                    int k = i + 1;
                    if (k == 4) {
                        k = 0;
                    }
                    while (k != i) {
                        if (HiraModes[k][j] != 0) {
                            return HiraModes[k][j];
                        }
                        k++;
                        if (k == 4) {
                            k = 0;
                        }
                    }
                    return (char) 0;
                }
            }
        }
        return (char) 0;
    }

    public static int isHiraIndex(char charin) {
        if (charin == 12540) {
            return 304;
        }
        for (int i = 0; i < 48; i++) {
            if (charin == HiraganaSmall[i]) {
                return i + 512;
            }
            if (charin == HiraganaBig[i]) {
                return i + 256;
            }
            if (charin == HiraganaVo[i]) {
                return i + 768;
            }
            if (charin == HiraganaSemiVo[i]) {
                return i + 1024;
            }
        }
        return 0;
    }

    public static int isHiraCycle(char charin) {
        for (int i = 0; i < 57; i++) {
            if (charin == HiraganaCycle[i]) {
                return i + 256;
            }
        }
        return 0;
    }

    public static char hiraCycle(char charin) {
        int k = isHiraCycle(charin);
        if (k <= 0) {
            return (char) 0;
        }
        int j = k % 256;
        for (int r = 0; r < 10; r++) {
            if (j < RowsCycle[r]) {
                int l = j - 1;
                if (r > 0) {
                    if (l < RowsCycle[r - 1]) {
                        l = RowsCycle[r] - 1;
                    }
                } else if (l < 0) {
                    l = RowsCycle[r] - 1;
                }
                while (HiraganaCycle[j] == 0 && l != j) {
                    l--;
                    if (r > 0) {
                        if (l < Rows[r - 1]) {
                            l = Rows[r] - 1;
                        }
                    } else if (l < 0) {
                        l = Rows[r] - 1;
                    }
                }
                if (l != j) {
                    return HiraganaCycle[l];
                }
                return (char) 0;
            }
        }
        return (char) 0;
    }

    public static char hira2Digital(char charin) {
        int k = isHiraIndex(charin);
        if (k <= 0) {
            return (char) 0;
        }
        int i = (k / 256) - 1;
        int j = k % 256;
        int r = 0;
        while (r < 9) {
            if (j < Rows[r]) {
                return HiraToDigit[r];
            }
            r++;
        }
        if (i == 0 && j < Rows[r]) {
            return HiraToDigit[9];
        }
        if (i != 1 || j >= Rows[r]) {
            return (char) 0;
        }
        return HiraToDigit[9];
    }

    public static char hira2DigitalFull(char charin) {
        int k = isHiraIndex(charin);
        if (k <= 0) {
            return (char) 0;
        }
        int i = (k / 256) - 1;
        int j = k % 256;
        int r = 0;
        while (r < 9) {
            if (j < Rows[r]) {
                return HiraToDigitFull[r];
            }
            r++;
        }
        if (i == 0 && j < Rows[r]) {
            return HiraToDigitFull[9];
        }
        if (i != 1 || j >= Rows[r]) {
            return (char) 0;
        }
        return HiraToDigitFull[9];
    }

    public static boolean convertHira2Digital(char[] charsin, int slen, char[] charsout, int[] dlen) {
        dlen[0] = 0;
        for (int ilen = 0; ilen < slen; ilen++) {
            char charout = hira2Digital(charsin[ilen]);
            if (charout != 0 && dlen[0] < 64) {
                charsout[dlen[0]] = charout;
                dlen[0] = dlen[0] + 1;
            } else {
                dlen[0] = 0;
                charsout[0] = 0;
                return false;
            }
        }
        return true;
    }

    public static boolean convertHira2DigitalFull(char[] charsin, int slen, char[] charsout, int[] dlen) {
        dlen[0] = 0;
        for (int ilen = 0; ilen < slen; ilen++) {
            char charout = hira2DigitalFull(charsin[ilen]);
            if (charout != 0 && dlen[0] < 64) {
                charsout[dlen[0]] = charout;
                dlen[0] = dlen[0] + 1;
            } else {
                dlen[0] = 0;
                charsout[0] = 0;
                return false;
            }
        }
        return true;
    }

    public static boolean isBigHiragana(char charin) {
        if (charin == 12540) {
            return false;
        }
        for (int i = 0; i < 48 && charin != HiraganaSmall[i]; i++) {
            if (charin == HiraganaBig[i]) {
                return true;
            }
            if (charin == HiraganaVo[i] || charin == HiraganaSemiVo[i]) {
                return false;
            }
        }
        return false;
    }
}
