package com.nuance.swype.input.emoji.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Pair;
import com.nuance.android.compat.CompatUtil;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class Util {
    private static final Method Character_getName = CompatUtil.getMethod((Class<?>) Character.class, "getName", (Class<?>[]) new Class[]{Integer.TYPE});

    private Util() {
    }

    public static String posToString(int x, int y) {
        return "(" + x + "," + y + ")";
    }

    public static String posToString(int[] pos) {
        return posToString(pos, 0);
    }

    public static String posToString(int[] pos, int offset) {
        return posToString(pos[offset], pos[offset + 1]);
    }

    public static String rectToString(int[] rect) {
        return posToString(rect[0], 0) + XMLResultsHandler.SEP_HYPHEN + posToString(rect, 2);
    }

    public static String rectToString(int left, int top, int right, int bottom) {
        return posToString(left, top) + XMLResultsHandler.SEP_HYPHEN + posToString(right, bottom);
    }

    public static <F, S> Pair<F, S> newPair(F f, S s) {
        return new Pair<>(f, s);
    }

    public static int roundUpInt(float val) {
        return (int) Math.ceil(val);
    }

    public static int roundDownInt(float val) {
        return (int) Math.floor(val);
    }

    public static int roundInt(float val) {
        return Math.round(val);
    }

    public static void copyRect(int[] dest, int[] src) {
        dest[0] = src[0];
        dest[1] = src[1];
        dest[2] = src[2];
        dest[3] = src[3];
    }

    public static int[] allocRect(int left, int top, int right, int bottom) {
        return new int[]{left, top, right, bottom};
    }

    public static int[] allocRect(float[] points) {
        int[] out = {roundDownInt(points[0]), roundDownInt(points[1]), roundUpInt(points[2]), roundUpInt(points[3])};
        return out;
    }

    public static float[] allocRectF(int left, int top, int right, int bottom) {
        return new float[]{left, top, right, bottom};
    }

    public static void adjustRect(int[] dest, int left, int top, int right, int bottom) {
        dest[0] = dest[0] + left;
        dest[1] = dest[1] + top;
        dest[2] = dest[2] + right;
        dest[3] = dest[3] + bottom;
    }

    public static void adjustRect(float[] dest, float left, float top, float right, float bottom) {
        dest[0] = dest[0] + left;
        dest[1] = dest[1] + top;
        dest[2] = dest[2] + right;
        dest[3] = dest[3] + bottom;
    }

    public static void adjustRect(int[] dest, int dx, int dy) {
        dest[0] = dest[0] + dx;
        dest[1] = dest[1] + dy;
        dest[2] = dest[2] + dx;
        dest[3] = dest[3] + dy;
    }

    private static String getCharacterName(int codePoint) {
        if (Character_getName != null) {
            return (String) CompatUtil.invoke(Character_getName, null, Integer.valueOf(codePoint));
        }
        return null;
    }

    public static String getCharDesc(String codePointText) {
        int codePoint = codePointText.codePointAt(0);
        String hex = String.format("%04x", Integer.valueOf(codePoint));
        return getCharName(codePointText) + " [U+" + hex + "]";
    }

    public static String getCharName(String codePointText) {
        if (codePointText == null) {
            return "null";
        }
        String name = getCharacterName(codePointText.codePointAt(0));
        if (name == null) {
            return "???";
        }
        return name;
    }

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round((metrics.densityDpi / 160.0f) * dp);
    }
}
