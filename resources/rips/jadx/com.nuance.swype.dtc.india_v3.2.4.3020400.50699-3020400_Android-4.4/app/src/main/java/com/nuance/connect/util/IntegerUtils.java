package com.nuance.connect.util;

/* loaded from: classes.dex */
public class IntegerUtils {
    public static boolean arrayCompare(int[] iArr, int[] iArr2) {
        if (iArr == iArr2) {
            return true;
        }
        if (iArr == null || iArr2 == null) {
            return false;
        }
        int length = iArr.length;
        if (iArr2.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (iArr[i] != iArr2[i]) {
                return false;
            }
        }
        return true;
    }
}
