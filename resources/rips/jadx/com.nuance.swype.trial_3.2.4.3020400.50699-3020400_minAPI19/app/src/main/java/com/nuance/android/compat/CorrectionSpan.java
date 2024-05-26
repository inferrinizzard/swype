package com.nuance.android.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import com.nuance.swype.util.LogManager;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: classes.dex */
public class CorrectionSpan extends CharacterStyle implements ParcelableSpan {
    public static final String ACTION_SUGGESTION_PICKED = "android.text.style.SUGGESTION_PICKED";
    public static final Parcelable.Creator<CorrectionSpan> CREATOR;
    public static final int FLAG_AUTO_CORRECTION = 4;
    public static final int FLAG_EASY_CORRECT = 1;
    public static final int FLAG_MISSPELLED = 2;
    public static final int SUGGESTIONS_MAX_SIZE = 5;
    public static final String SUGGESTION_SPAN_PICKED_AFTER = "after";
    public static final String SUGGESTION_SPAN_PICKED_BEFORE = "before";
    public static final String SUGGESTION_SPAN_PICKED_HASHCODE = "hashcode";
    private static final Method TextPaint_setUnderlineText;
    private static final boolean isSdk18;
    private static final LogManager.Log log = LogManager.getLog("SmartCorrectionSpan");
    private int mAutoCorrectionUnderlineColor;
    private float mAutoCorrectionUnderlineThickness;
    private int mEasyCorrectUnderlineColor;
    private float mEasyCorrectUnderlineThickness;
    private int mFlags;
    private final int mHashCode;
    private final String mLocaleString;
    private int mMisspelledUnderlineColor;
    private float mMisspelledUnderlineThickness;
    private final String mNotificationTargetClassName;
    private final String mNotificationTargetPackageName;
    private final String[] mSuggestions;
    private int underlineColor;
    private int underlineThickness;

    static {
        isSdk18 = Build.VERSION.SDK_INT >= 18;
        CREATOR = new Parcelable.Creator<CorrectionSpan>() { // from class: com.nuance.android.compat.CorrectionSpan.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public final CorrectionSpan createFromParcel(Parcel source) {
                return new CorrectionSpan(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public final CorrectionSpan[] newArray(int size) {
                return new CorrectionSpan[size];
            }
        };
        TextPaint_setUnderlineText = CompatUtil.getMethod((Class<?>) TextPaint.class, "setUnderlineText", (Class<?>[]) new Class[]{Integer.TYPE, Float.TYPE});
    }

    @TargetApi(15)
    public static CorrectionSpan createCorrectionSpan(Context context, int underlineColor, int underlineThickness) {
        String[] suggestions = new String[0];
        return new CorrectionSpan(context, underlineColor, underlineThickness, 4, suggestions, null, null);
    }

    private CorrectionSpan(Context context, int underlineColor, int underlineThickness, int flags, String[] suggestions, Class<?> notificationTargetClass, Locale locale) {
        this.underlineColor = underlineColor;
        this.underlineThickness = underlineThickness;
        this.mSuggestions = (String[]) Arrays.copyOf(suggestions, Math.min(5, suggestions.length));
        this.mFlags = flags;
        if (locale != null) {
            this.mLocaleString = locale.toString();
        } else if (context != null) {
            this.mLocaleString = context.getResources().getConfiguration().locale.toString();
        } else {
            log.e("No locale or context specified in SuggestionSpan constructor");
            this.mLocaleString = "";
        }
        if (notificationTargetClass != null) {
            this.mNotificationTargetClassName = notificationTargetClass.getCanonicalName();
        } else {
            this.mNotificationTargetClassName = "";
        }
        if (context != null) {
            this.mNotificationTargetPackageName = context.getPackageName();
        } else {
            this.mNotificationTargetPackageName = "";
        }
        this.mHashCode = hashCodeInternal(this.mSuggestions, this.mLocaleString, this.mNotificationTargetClassName);
        initStyle(context);
    }

    private void initStyle(Context context) {
        log.d("initStyle()");
        if (context == null) {
            this.mMisspelledUnderlineThickness = 0.0f;
            this.mEasyCorrectUnderlineThickness = 0.0f;
            this.mAutoCorrectionUnderlineThickness = 0.0f;
            this.mMisspelledUnderlineColor = -16777216;
            this.mEasyCorrectUnderlineColor = -16777216;
            this.mAutoCorrectionUnderlineColor = this.underlineColor;
            return;
        }
        this.mMisspelledUnderlineThickness = this.underlineThickness;
        this.mEasyCorrectUnderlineThickness = this.underlineThickness;
        this.mAutoCorrectionUnderlineThickness = this.underlineThickness;
        this.mMisspelledUnderlineColor = -65536;
        this.mEasyCorrectUnderlineColor = -16776961;
        this.mAutoCorrectionUnderlineColor = this.underlineColor;
    }

    public CorrectionSpan(Parcel src) {
        log.d("CorrectionSpan(Parcel src)");
        this.mSuggestions = new String[5];
        src.readStringArray(this.mSuggestions);
        this.mFlags = src.readInt();
        this.mLocaleString = src.readString();
        this.mNotificationTargetClassName = src.readString();
        this.mNotificationTargetPackageName = isSdk18 ? src.readString() : "";
        this.mHashCode = src.readInt();
        this.mEasyCorrectUnderlineColor = src.readInt();
        this.mEasyCorrectUnderlineThickness = src.readFloat();
        this.mMisspelledUnderlineColor = src.readInt();
        this.mMisspelledUnderlineThickness = src.readFloat();
        this.mAutoCorrectionUnderlineColor = src.readInt();
        this.mAutoCorrectionUnderlineThickness = src.readFloat();
    }

    public String[] getSuggestions() {
        log.d("getSuggestions()");
        int len = this.mSuggestions.length;
        String[] array = new String[len];
        for (int i = 0; i != len; i++) {
            array[i] = this.mSuggestions[i];
        }
        return array;
    }

    public String getLocale() {
        log.d("getLocale()");
        return this.mLocaleString;
    }

    public String getNotificationTargetClassName() {
        log.d("getNotificationTargetClassName()");
        return this.mNotificationTargetClassName;
    }

    public int getFlags() {
        log.d("getFlags()");
        return this.mFlags;
    }

    public void setFlags(int flags) {
        log.d("setFlags()");
        this.mFlags = flags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        log.d("describeContents()");
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        log.d("writeToParcel()");
        dest.writeStringArray(this.mSuggestions);
        dest.writeInt(this.mFlags);
        dest.writeString(this.mLocaleString);
        dest.writeString(this.mNotificationTargetClassName);
        if (isSdk18) {
            dest.writeString(this.mNotificationTargetPackageName);
        }
        dest.writeInt(this.mHashCode);
        dest.writeInt(this.mEasyCorrectUnderlineColor);
        dest.writeFloat(this.mEasyCorrectUnderlineThickness);
        dest.writeInt(this.mMisspelledUnderlineColor);
        dest.writeFloat(this.mMisspelledUnderlineThickness);
        dest.writeInt(this.mAutoCorrectionUnderlineColor);
        dest.writeFloat(this.mAutoCorrectionUnderlineThickness);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        log.d("getSpanTypeId()");
        return 19;
    }

    public boolean equals(Object o) {
        log.d("equals()");
        return (o instanceof CorrectionSpan) && o.hashCode() == this.mHashCode;
    }

    public int hashCode() {
        log.d("hashCode()");
        return this.mHashCode;
    }

    private static int hashCodeInternal(String[] suggestions, String locale, String notificationTargetClassName) {
        return Arrays.hashCode(new Object[]{Long.valueOf(SystemClock.uptimeMillis()), suggestions, locale, notificationTargetClassName});
    }

    private static void invokeSetUnderlineText(TextPaint tp, int color, float thickness) {
        if (TextPaint_setUnderlineText != null) {
            CompatUtil.invoke(TextPaint_setUnderlineText, tp, Integer.valueOf(color), Float.valueOf(thickness));
        }
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        boolean misspelled = (this.mFlags & 2) != 0;
        boolean easy = (this.mFlags & 1) != 0;
        boolean autoCorrection = (this.mFlags & 4) != 0;
        log.d("updateDrawState()");
        if (easy) {
            if (!misspelled) {
                invokeSetUnderlineText(tp, this.mEasyCorrectUnderlineColor, this.mEasyCorrectUnderlineThickness);
                return;
            } else {
                invokeSetUnderlineText(tp, this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
                return;
            }
        }
        if (autoCorrection) {
            invokeSetUnderlineText(tp, this.mAutoCorrectionUnderlineColor, this.mAutoCorrectionUnderlineThickness);
        }
    }

    public int getUnderlineColor() {
        log.d("getUnderlineColor()");
        boolean misspelled = (this.mFlags & 2) != 0;
        boolean easy = (this.mFlags & 1) != 0;
        boolean autoCorrection = (this.mFlags & 4) != 0;
        if (easy) {
            if (!misspelled) {
                return this.mEasyCorrectUnderlineColor;
            }
            return this.mMisspelledUnderlineColor;
        }
        if (autoCorrection) {
            return this.mAutoCorrectionUnderlineColor;
        }
        return 0;
    }
}
