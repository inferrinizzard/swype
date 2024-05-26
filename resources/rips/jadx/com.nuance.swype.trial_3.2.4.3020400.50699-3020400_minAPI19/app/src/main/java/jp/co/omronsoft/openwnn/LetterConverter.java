package jp.co.omronsoft.openwnn;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
public interface LetterConverter {
    boolean convert(ComposingText composingText);

    void setPreferences(SharedPreferences sharedPreferences);
}
