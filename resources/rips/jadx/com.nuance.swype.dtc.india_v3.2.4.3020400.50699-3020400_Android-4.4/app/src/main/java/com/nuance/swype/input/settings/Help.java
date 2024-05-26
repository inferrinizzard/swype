package com.nuance.swype.input.settings;

import android.app.Activity;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;

/* loaded from: classes.dex */
public abstract class Help {
    public static final int HELP_XML = R.xml.help;
    private static final String TIPS_KEY = "show_tips";
    private static final String VERSION_KEY = "version";
    private final Activity activity;
    private PreferenceScreen screen;

    protected abstract PreferenceScreen addPreferences();

    public Help(Activity activity) {
        this.activity = activity;
        UsageData.recordScreenVisited(UsageData.Screen.HELP);
    }

    public void onResume() {
        rebuildSettings();
    }

    private Context getContext() {
        return this.activity;
    }

    protected final void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        this.screen = addPreferences();
        BuildInfo info = BuildInfo.from(getContext());
        Preference versionPreference = this.screen.findPreference("version");
        if (versionPreference != null) {
            versionPreference.setSummary(info.getBuildVersion());
        }
        CheckBoxPreference showTips = (CheckBoxPreference) this.screen.findPreference(TIPS_KEY);
        final AppPreferences appPrefs = IMEApplication.from(this.activity).getAppPreferences();
        if (showTips != null) {
            showTips.setChecked(appPrefs.showToolTip());
            showTips.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.Help.1
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    boolean checked = ((CheckBoxPreference) preference).isChecked();
                    appPrefs.setshowToolTip(checked);
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(Help.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange("Show tips", Boolean.valueOf(checked), Boolean.valueOf(!checked));
                    }
                    return true;
                }
            });
        }
    }
}
