package com.nuance.swype.util;

import com.nuance.swype.util.LogManager;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class EmojiUtils {
    protected static final LogManager.Log log = LogManager.getLog("EmojiUtils");
    private static final char[] character_emoji = {'#', '*', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 169, 174, 8252, 8265, 8482, 8505, 8596, 8597, 8598, 8599, 8600, 8601, 8617, 8618, 8986, 8987, 9000, 9167, 9193, 9194, 9195, 9196, 9197, 9198, 9199, 9200, 9201, 9202, 9203, 9208, 9209, 9210, 9410, 9642, 9643, 9654, 9664, 9723, 9724, 9725, 9726, 9728, 9729, 9730, 9731, 9732, 9742, 9745, 9748, 9749, 9752, 9757, 9760, 9762, 9763, 9766, 9770, 9774, 9775, 9784, 9785, 9786, 9800, 9801, 9802, 9803, 9804, 9805, 9806, 9807, 9808, 9809, 9810, 9811, 9824, 9827, 9829, 9830, 9832, 9851, 9855, 9874, 9875, 9876, 9878, 9879, 9881, 9883, 9884, 9888, 9889, 9898, 9899, 9904, 9905, 9917, 9918, 9924, 9925, 9928, 9934, 9935, 9937, 9939, 9940, 9961, 9962, 9968, 9969, 9970, 9971, 9972, 9973, 9975, 9976, 9977, 9978, 9981, 9986, 9989, 9992, 9993, 9994, 9995, 9996, 9997, 9999, 10002, 10004, 10006, 10013, 10017, 10024, 10035, 10036, 10052, 10055, 10060, 10062, 10067, 10068, 10069, 10071, 10083, 10084, 10133, 10134, 10135, 10145, 10160, 10175, 10548, 10549, 11013, 11014, 11015, 11035, 11036, 11088, 11093, 12336, 12349, 12951, 12953};

    private static boolean isVariant(char ch) {
        return ch == 9877 || ch == 9792 || ch == 9794;
    }

    private static boolean isSelector(char ch) {
        return ch == 65039 || ch == 65038;
    }

    private static boolean isToneModifier(int codePoint) {
        return codePoint >= 127995 && codePoint <= 127999;
    }

    public static boolean characterIsEmoji(char ch) {
        return Arrays.binarySearch(character_emoji, ch) >= 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0029, code lost:            if (r2 != 0) goto L17;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:            r4 = 11;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x002c, code lost:            if (r4 == 11) goto L138;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x002e, code lost:            r2 = r2 - 1;     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x0160  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0070  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0020 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x00ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int characterBefore(java.lang.CharSequence r11, int r12, int r13) {
        /*
            Method dump skipped, instructions count: 398
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.util.EmojiUtils.characterBefore(java.lang.CharSequence, int, int):int");
    }
}
