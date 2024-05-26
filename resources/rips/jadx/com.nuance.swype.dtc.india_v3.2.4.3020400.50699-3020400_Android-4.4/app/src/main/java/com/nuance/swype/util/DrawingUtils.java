package com.nuance.swype.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.UserPreferences;
import java.lang.Character;

/* loaded from: classes.dex */
public final class DrawingUtils {
    private static final char[] SPECIAL_CHARACTERS = {12289};
    private static final char[] SPECIAL_CHARACTERS_ADJUSTMENTS = {3};

    public static int hAdjustCharAlignment(String text, Rect textBounds, int gravity, Paint paint) {
        char c = 0;
        int gravity2 = gravity & 7;
        if (text.length() == 1) {
            char charAt = text.charAt(0);
            Character.UnicodeBlock of = Character.UnicodeBlock.of(charAt);
            if (of == Character.UnicodeBlock.DEVANAGARI || of == Character.UnicodeBlock.GUJARATI || of == Character.UnicodeBlock.MALAYALAM || of == Character.UnicodeBlock.MYANMAR || of == Character.UnicodeBlock.TELUGU || of == Character.UnicodeBlock.KANNADA || of == Character.UnicodeBlock.KHMER || of == Character.UnicodeBlock.TAMIL || of == Character.UnicodeBlock.BENGALI || of == Character.UnicodeBlock.SINHALA) {
                c = 4;
            } else if (Character.getType(charAt) == 6) {
                c = 1;
            } else {
                int i = 0;
                while (true) {
                    if (i >= SPECIAL_CHARACTERS.length) {
                        break;
                    }
                    if (SPECIAL_CHARACTERS[i] != charAt) {
                        i++;
                    } else {
                        c = SPECIAL_CHARACTERS_ADJUSTMENTS[i];
                        break;
                    }
                }
            }
        }
        switch (c) {
            case 1:
                switch (gravity2) {
                    case 3:
                        int adjustment = -textBounds.left;
                        return adjustment;
                    case 4:
                    default:
                        int adjustment2 = (-(textBounds.right + textBounds.left)) / 2;
                        return adjustment2;
                    case 5:
                        int adjustment3 = -textBounds.right;
                        return adjustment3;
                }
            case 2:
                int adjustment4 = (-textBounds.width()) / 2;
                return adjustment4;
            case 3:
                int adjustment5 = textBounds.width() / 2;
                return adjustment5;
            case 4:
                switch (AnonymousClass1.$SwitchMap$android$graphics$Paint$Align[paint.getTextAlign().ordinal()]) {
                    case 1:
                        paint.setTextAlign(Paint.Align.LEFT);
                        int adjustment6 = (-(textBounds.right + textBounds.left)) / 2;
                        return adjustment6;
                    case 2:
                        paint.setTextAlign(Paint.Align.LEFT);
                        int adjustment7 = -textBounds.right;
                        return adjustment7;
                    case 3:
                        int adjustment8 = -textBounds.left;
                        return adjustment8;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nuance.swype.util.DrawingUtils$1, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Paint$Align = new int[Paint.Align.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Paint$Align[Paint.Align.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Paint$Align[Paint.Align.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Paint$Align[Paint.Align.LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static int getExcessAboveAscent(Rect textBounds, Paint.FontMetrics fm) {
        if (fm.ascent <= textBounds.top) {
            return 0;
        }
        return (int) Math.ceil(fm.ascent - textBounds.top);
    }

    public static int getExcessBelowDescent(Rect textBounds, Paint.FontMetrics fm) {
        if (fm.descent >= textBounds.bottom) {
            return 0;
        }
        return (int) Math.ceil(textBounds.bottom - fm.descent);
    }

    public static float getKeyboardScale(Context context) {
        UserPreferences userPrefs = IMEApplication.from(context).getUserPreferences();
        if (context.getResources().getConfiguration().orientation == 1) {
            float keyboardScale = userPrefs.getKeyboardScalePortrait();
            return keyboardScale;
        }
        float keyboardScale2 = userPrefs.getKeyboardScaleLandscape();
        return keyboardScale2;
    }
}
