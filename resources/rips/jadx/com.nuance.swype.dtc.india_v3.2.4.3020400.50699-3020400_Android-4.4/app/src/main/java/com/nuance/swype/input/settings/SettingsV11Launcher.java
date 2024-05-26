package com.nuance.swype.input.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;

/* loaded from: classes.dex */
public class SettingsV11Launcher implements ShowSettings {
    private static final String HELP_URL = "help_url";

    protected Class<? extends FragmentActivity> getSettingsClass() {
        return SettingsV11.class;
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showMain(Context context) {
        Intent intent = new Intent(context, (Class<?>) SettingsV11.class).setFlags(268500992);
        context.startActivity(intent.addFlags(32768));
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showTutorial(Context context) {
        Intent intent = new Intent(context, (Class<?>) TutorialFragmentActivity.class);
        intent.setFlags(872415232);
        context.startActivity(intent);
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showGestures(Context context) {
        Intent intent = createFragmentIntent(context, GesturesFragment.class);
        Bundle b = new Bundle();
        b.putString("help_url", context.getString(R.string.fullhelp_gestures_file));
        intent.putExtra(":android:show_fragment_args", b);
        intent.addFlags(32768);
        context.startActivity(intent);
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showLanguages(Context context) {
        context.startActivity(createFragmentIntent(context, LanguageOptionsFragment.class).addFlags(32768));
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showLanguageDownloads(Context context) {
        Intent intent = new Intent(context, (Class<?>) LanguageUpdateFragmentActivity.class);
        intent.setFlags(872415232);
        context.startActivity(intent);
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showMyWordsPrefs(Context context) {
        context.startActivity(createFragmentIntent(context, MyWordsPrefsFragment.class).addFlags(32768));
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showUpdates(Context context) {
        context.startActivity(createFragmentIntent(context, UpdatesFragment.class).addFlags(32768));
    }

    public void showAccount(Context context) {
        context.startActivity(createFragmentIntent(context, AccountPrefsFragment.class).addFlags(32768));
    }

    private Intent createSettingsIntent(Context context) {
        return new Intent(context, getSettingsClass()).setFlags(268500992);
    }

    private Intent createFragmentIntent(Context context, Class<?> fragmentClass) {
        return createSettingsIntent(context).addFlags(67174400).putExtra(":android:show_fragment", fragmentClass.getName()).putExtra(":android:no_headers", fragmentClass.getName());
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showFunctionBar(Context context) {
        context.startActivity(createFragmentIntent(context, FunctionBarFragment.class).addFlags(32768));
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showThemes(Context context) {
        if (ThemeManager.isDownloadableThemesEnabled()) {
            context.startActivity(createSettingsIntent(context).addFlags(32768));
        } else {
            context.startActivity(createFragmentIntent(context, ThemesFragment.class).addFlags(32768));
        }
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showChineseSettings(Context context) {
        context.startActivity(createFragmentIntent(context, ChinesePrefsFragment.class).addFlags(32768));
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public boolean isRunning() {
        return SettingsV11.isRunning();
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showAddonDictionaries(Context context, boolean shouldClearPreviousTask) {
        Intent i = new Intent(context, (Class<?>) AddonDictionariesPrefsFragmentActivity.class);
        i.setFlags(872415232);
        Intent start = ConnectLegal.getLegalActivitiesStartIntentForIntent(i, context, true, false, null);
        start.addFlags(268435456);
        context.startActivity(start);
    }

    @Override // com.nuance.swype.input.settings.ShowSettings
    public void showSettingsPrefs(Context context) {
        context.startActivity(createFragmentIntent(context, SettingsPrefsFragment.class).addFlags(32768));
    }
}
