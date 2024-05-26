package com.nuance.swype.input.settings;

import android.content.Context;

/* loaded from: classes.dex */
public interface ShowSettings {
    boolean isRunning();

    void showAddonDictionaries(Context context, boolean z);

    void showChineseSettings(Context context);

    void showFunctionBar(Context context);

    void showGestures(Context context);

    void showLanguageDownloads(Context context);

    void showLanguages(Context context);

    void showMain(Context context);

    void showMyWordsPrefs(Context context);

    void showSettingsPrefs(Context context);

    void showThemes(Context context);

    void showTutorial(Context context);

    void showUpdates(Context context);
}
