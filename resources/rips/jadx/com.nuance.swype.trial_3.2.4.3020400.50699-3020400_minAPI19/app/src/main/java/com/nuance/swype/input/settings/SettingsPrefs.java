package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.sns.OAuthPreference;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.EmojiSkinToneListAdapter;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SystemState;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.preference.CustomCheckBoxPreference;
import com.nuance.swype.preference.DialogPrefs;
import com.nuance.swype.preference.EmojiSkinTonePreference;
import com.nuance.swype.preference.ViewClickPreference;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACDevice;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class SettingsPrefs extends DialogPrefs implements Preference.OnPreferenceChangeListener {
    private static final String AD_SETTINGS_CATEGORY_KEY = "ad_settings";
    private static final String BASIC_SETTINGS_CATEGORY_KEY = "basic_category";
    public static final String CANDIDATES_SIZE = "Candidates_Size";
    private static final String CANDIDATES_SIZES_SETTINGS_KEY = "candidates_size_setting_preference";
    public static final String CANDIDATES_SUMMARY = "Candidates_summary";
    private static final int CANDIDATE_SIZE_BAR_MAX = 10;
    private static final int CANDIDATE_SIZE_BAR_OFFSET = 100;
    protected static final String DATA_MANAGEMENT_CAT = "pref_data_management";
    protected static final int DIALOG_LONG_PRESS_DURATION = 10;
    protected static final int DIALOG_RECOGNITION_SPEED = 11;
    protected static final int DIALOG_VIBRATION_DURATION = 9;
    private static final String EMOJI_CATEGORY_KEY = "emoji_category";
    protected static final String ENABLE_DATA_CONNECTION_KEY = "pref_connect_enable_data_connection";
    private static final int KEYBOARD_HEIGHT_BAR_MAX = 4;
    private static final int KEYBOARD_HEIGHT_BAR_OFFSET = 8;
    private static final String KEYBOARD_HEIGHT_SETTINGS_KEY = "keyboard_height_settings";
    private static final String KEYBOARD_SETTINGS_CATEGORY_KEY = "keyboard_category";
    private static final int LONG_PRESS_DURATION_STEPSIZE = 10;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_END_OF_SPEECH = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_VOICE_KEY = 0;
    private static final String PREDICTION_SETTINGS_CATEGORY_KEY = "prediction_category";
    private static final float QVGA_DEVICE = 0.8f;
    private static final int RECOGNITION_SPEED_STEPSIZE = 50;
    private static final String STATE_EMOJI_LDB = "state_emoji_ldb";
    private static final String STATE_NULL_LDB = "state_null_ldb";
    private static final String STATE_NUMBER_ROW_KB = "state_number_row_kb";
    private static final float UNIT_CONVERSION_CONSTANT = 1000.0f;
    private static final int VIBRATION_DURATION_STEPSIZE = 5;
    private static final String VOICE_HANDWRITING_CATEGORY_KEY = "voice_handwriting_category";
    private final Activity activity;
    private Preference candidateSizePrefs;
    private float changedProgress;
    private Connect connect;
    private Preference emojiSkinTonePreference;
    private String fontSize;
    private boolean isPermissionDialogRationale;
    private Preference keyboardHeightPrefs;
    private boolean mStateEmojiLdb;
    private boolean mStateNullLdb;
    private boolean mStateNumberRowKb;
    private int mValue;
    private float mValueKeyboardHeightLandscape;
    private float mValueKeyboardHeightPortrait;
    private int scalingFactor;
    private PreferenceScreen screen;
    private boolean settingsChanged;
    private Dialog skinToneDialog;
    private static final LogManager.Log log = LogManager.getLog("SettingsPrefs");
    public static final int PREFERENCES_XML = R.xml.settingspreferences;

    protected abstract PreferenceScreen addPreferences();

    protected abstract Preference createInputLanguagePref(Bundle bundle);

    abstract void doShowDialog(int i, Bundle bundle);

    abstract void requestPermissions(String[] strArr, int i);

    protected abstract void showCandidateSizeDialog(Bundle bundle);

    protected abstract void showEmojiSkinToneDialog(Bundle bundle);

    protected abstract void showKeyboardHeightDialog(Bundle bundle);

    @SuppressLint({"NewApi"})
    public SettingsPrefs(Activity activity) {
        this.isPermissionDialogRationale = false;
        this.activity = activity;
        this.connect = Connect.from(activity);
        UsageData.recordScreenVisited(UsageData.Screen.PREFERENCES);
        if (Build.VERSION.SDK_INT >= 23) {
            this.isPermissionDialogRationale = activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mStateNullLdb = savedInstanceState.getBoolean(STATE_NULL_LDB);
            this.mStateNumberRowKb = savedInstanceState.getBoolean(STATE_NUMBER_ROW_KB);
            this.mStateEmojiLdb = savedInstanceState.getBoolean(STATE_EMOJI_LDB);
        } else {
            this.mStateNullLdb = false;
            this.mStateNumberRowKb = true;
            this.mStateEmojiLdb = true;
        }
    }

    public void onResume() {
        rebuildSettings();
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(STATE_NULL_LDB, this.mStateNullLdb);
        bundle.putBoolean(STATE_NUMBER_ROW_KB, this.mStateNumberRowKb);
        bundle.putBoolean(STATE_EMOJI_LDB, this.mStateEmojiLdb);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext() {
        return this.activity;
    }

    private void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        this.screen = addPreferences();
        getContext();
        removePreference(this.screen, AD_SETTINGS_CATEGORY_KEY);
        buildPreferenceScreen();
        buildLanguagesScreen();
        setupPreferenceHandler(this.screen);
    }

    private void buildAdSettings() {
        UserPreferences userPrefs = UserPreferences.from(getContext());
        EditTextPreference firstTime = (EditTextPreference) this.screen.findPreference(UserPreferences.PREF_AD_FIRST_TIME);
        firstTime.setSummary(String.valueOf(userPrefs.getAdStartSession()));
        firstTime.setText(String.valueOf(userPrefs.getAdStartSession()));
        EditTextPreference stepSize = (EditTextPreference) this.screen.findPreference(UserPreferences.PREF_AD_STEP_SIZE);
        stepSize.setSummary(String.valueOf(userPrefs.getAdSessionStepSize()));
        stepSize.setText(String.valueOf(userPrefs.getAdSessionStepSize()));
        EditTextPreference maxPerDay = (EditTextPreference) this.screen.findPreference(UserPreferences.PREF_AD_MAX_PER_DAY);
        maxPerDay.setSummary(String.valueOf(userPrefs.getAdMaxPerDay()));
        maxPerDay.setText(String.valueOf(userPrefs.getAdMaxPerDay()));
    }

    private void buildPreferenceScreen() {
        BuildInfo info = BuildInfo.from(getContext());
        InputMethods inputMethods = InputMethods.from(getContext());
        inputMethods.syncWithCurrentUserConfiguration();
        InputMethods.Language language = inputMethods.getCurrentInputLanguage();
        if (language != null && (info.getHandwritingType() != BuildInfo.HandwritingType.FULL_SUPPORT || language.isCJK())) {
            removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.PREF_ENABLE_HANDWRITING);
        }
        if (language.isCJK() && info.getHandwritingType() != BuildInfo.HandwritingType.NONE) {
            enableCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.HWR_SETTINGS, true);
            PreferenceCategory voiceCategory = (PreferenceCategory) this.screen.findPreference(VOICE_HANDWRITING_CATEGORY_KEY);
            if (voiceCategory != null && this.screen.findPreference(UserPreferences.HWR_SETTINGS) == null) {
                Preference hwrPref = new Preference(getContext());
                Bundle bundle = InputPrefs.createArgs(language.getHandwritingMode());
                hwrPref.setKey(UserPreferences.HWR_SETTINGS);
                hwrPref.setTitle(getContext().getResources().getString(R.string.handwriting));
                hwrPref.setIntent(new Intent(getContext(), (Class<?>) InputPrefsFragmentActivity.class).putExtras(bundle));
                voiceCategory.addPreference(hwrPref);
            }
        }
        if (IMEApplication.from(getContext()).getSpeechProvider() != 0) {
            removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.END_OF_SPEECH_DETECTION);
        }
        Resources res = getContext().getResources();
        if (!res.getBoolean(R.bool.dictation_enabled)) {
            removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.SHOW_VOICE_KEY);
        }
        UserPreferences userPrefs = UserPreferences.from(getContext());
        if (!userPrefs.isShowCharsOnKeypressEnabled()) {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_SHOW_CHARS_ON_KEYPRESS);
        }
        boolean hideAutoCorrectPref = this.mStateNullLdb;
        boolean showEmojiSuggestionsPref = this.mStateEmojiLdb;
        boolean showNumberRowPref = this.mStateNumberRowKb;
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.getCurrentInputView() != null) {
            InputView iv = ime.getCurrentInputView();
            InputMethods.Language currentLanguage = iv.getCurrentInputLanguage();
            if (currentLanguage.isCJK()) {
                hideAutoCorrectPref = false;
                this.mStateNullLdb = false;
            } else {
                iv.getXT9CoreInput().setLanguage(currentLanguage.getCoreLanguageId());
                hideAutoCorrectPref = iv.getXT9CoreInput().isNullLdb();
                this.mStateNullLdb = hideAutoCorrectPref;
            }
            showEmojiSuggestionsPref = iv.getXT9CoreInput().isLanguageHaveEmojiPrediction();
            this.mStateEmojiLdb = showEmojiSuggestionsPref;
            showNumberRowPref = iv.getKeyboard().hasNumRow();
            this.mStateNumberRowKb = showNumberRowPref;
        }
        if (!userPrefs.isShowAutoCorrectionPrefEnabled() || hideAutoCorrectPref) {
            removeCategorySubPreference(this.screen, PREDICTION_SETTINGS_CATEGORY_KEY, "auto_correction");
        }
        if (!showEmojiSuggestionsPref || (ime != null && ime.isAccessibilitySupportEnabled())) {
            removeCategorySubPreference(this.screen, EMOJI_CATEGORY_KEY, UserPreferences.PREF_EMOJI_SUGGESTIONS);
        }
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            removeCategorySubPreference(this.screen, EMOJI_CATEGORY_KEY, UserPreferences.PREF_EMOJI_CHOICE_LIST);
        }
        if (!showNumberRowPref) {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_NUMBER_ROW);
        }
        SystemState sysState = IMEApplication.from(getContext()).getSystemState();
        int enabled = sysState.isKeyboardVibrateEnabled();
        enableCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATE_ON, enabled == -1 ? sysState.isHapticFeedbackEnabled() : true);
        if (enabled != -1) {
            sysState.setKeyboardVibrate(enabled != 0);
            CheckBoxPreference keyboardVibrationPref = (CheckBoxPreference) this.screen.findPreference(UserPreferences.PREF_VIBRATE_ON);
            if (keyboardVibrationPref != null) {
                keyboardVibrationPref.setChecked(enabled != 0);
            }
        }
        enableCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATION_DURATION, enabled == -1 ? sysState.isHapticFeedbackEnabled() : true);
        int enabled2 = sysState.isKeyboardSoundEnabled();
        enableCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_SOUND_ON, (enabled2 == -1 && sysState.isSilentMode()) ? false : true);
        if (enabled2 != -1) {
            sysState.setKeyboardSound(enabled2 != 0);
            CheckBoxPreference keyboardSoundPref = (CheckBoxPreference) this.screen.findPreference(UserPreferences.PREF_SOUND_ON);
            if (keyboardSoundPref != null) {
                keyboardSoundPref.setChecked(enabled2 != 0);
            }
        }
        Preference vibrationPref = this.screen.findPreference(UserPreferences.PREF_VIBRATION_DURATION);
        if (vibrationPref != null) {
            vibrationPref.setSummary(res.getString(R.string.millisecond, Integer.valueOf(userPrefs.getVibrationDuration())));
            if (vibrationPref instanceof ViewClickPreference) {
                ((ViewClickPreference) vibrationPref).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.1
                    @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                    public void onViewClick(Preference pref) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.doShowDialog(9, args);
                    }
                });
            } else {
                vibrationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.2
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.doShowDialog(9, args);
                        return true;
                    }
                });
            }
        }
        this.emojiSkinTonePreference = this.screen.findPreference(UserPreferences.PREF_EMOJI_SKIN_TONE);
        if (this.emojiSkinTonePreference != null) {
            if (Build.VERSION.SDK_INT <= 23 || (ime != null && ime.isAccessibilitySupportEnabled())) {
                removeCategorySubPreference(this.screen, EMOJI_CATEGORY_KEY, UserPreferences.PREF_EMOJI_SKIN_TONE);
            } else if (this.emojiSkinTonePreference instanceof EmojiSkinTonePreference) {
                EmojiSkinTonePreference emojiSkinTonePreference = (EmojiSkinTonePreference) this.emojiSkinTonePreference;
                emojiSkinTonePreference.mViewClickPreferenceListener = new EmojiSkinTonePreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.3
                    @Override // com.nuance.swype.preference.EmojiSkinTonePreference.ViewClickPreferenceListener
                    public void onViewClick(Preference pref) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.showEmojiSkinToneDialog(args);
                    }
                };
                if (emojiSkinTonePreference.mCurrentView != null) {
                    emojiSkinTonePreference.mCurrentView.setOnClickListener(emojiSkinTonePreference);
                }
            } else {
                this.emojiSkinTonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.4
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.showEmojiSkinToneDialog(args);
                        return true;
                    }
                });
            }
        }
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_LONG_PRESS_DELAY);
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_SHOW_CHARS_ON_KEYPRESS);
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_SHOW_COMPLETE_TRACE);
            removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.PREF_ENABLE_HANDWRITING);
            removeCategorySubPreference(this.screen, PREDICTION_SETTINGS_CATEGORY_KEY, "auto_correction");
            PreferenceCategory voiceHandwritingCategory = (PreferenceCategory) this.screen.findPreference(VOICE_HANDWRITING_CATEGORY_KEY);
            if (voiceHandwritingCategory != null) {
                voiceHandwritingCategory.setTitle(R.string.accessibility_setting_voice);
            }
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_SOUND_ON);
            if (!sysState.hasHapticHardwareSupport()) {
                removePreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY);
            }
            removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.END_OF_SPEECH_DETECTION);
        }
        Preference longPressDelayPref = this.screen.findPreference(UserPreferences.PREF_LONG_PRESS_DELAY);
        if (longPressDelayPref != null) {
            longPressDelayPref.setSummary(res.getString(R.string.millisecond, Integer.valueOf(userPrefs.getLongPressDelay())));
            if (longPressDelayPref instanceof ViewClickPreference) {
                ((ViewClickPreference) longPressDelayPref).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.5
                    @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                    public void onViewClick(Preference pref) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.doShowDialog(10, args);
                    }
                });
            } else {
                longPressDelayPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.6
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Bundle args = new Bundle();
                        SettingsPrefs.this.doShowDialog(10, args);
                        return true;
                    }
                });
            }
            this.keyboardHeightPrefs = this.screen.findPreference(KEYBOARD_HEIGHT_SETTINGS_KEY);
            keyboardHeightSummaryUpdate();
            if (this.keyboardHeightPrefs != null) {
                if (!UserPreferences.from(getContext()).isShowKeyboardHeightPrefEnabled()) {
                    removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, KEYBOARD_HEIGHT_SETTINGS_KEY);
                } else if (this.keyboardHeightPrefs instanceof ViewClickPreference) {
                    ((ViewClickPreference) this.keyboardHeightPrefs).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.7
                        @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                        public void onViewClick(Preference pref) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.showKeyboardHeightDialog(args);
                        }
                    });
                } else {
                    this.keyboardHeightPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.8
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.showKeyboardHeightDialog(args);
                            return true;
                        }
                    });
                }
            }
            this.candidateSizePrefs = this.screen.findPreference(CANDIDATES_SIZES_SETTINGS_KEY);
            wordChoiceListFontSummary(this.candidateSizePrefs);
            if (this.candidateSizePrefs != null) {
                if (!UserPreferences.from(getContext()).isShowWordChoiceSizePrefEnabled()) {
                    removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, CANDIDATES_SIZES_SETTINGS_KEY);
                } else if (this.candidateSizePrefs instanceof ViewClickPreference) {
                    ((ViewClickPreference) this.candidateSizePrefs).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.9
                        @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                        public void onViewClick(Preference pref) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.showCandidateSizeDialog(args);
                        }
                    });
                } else {
                    this.candidateSizePrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.10
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.showCandidateSizeDialog(args);
                            return true;
                        }
                    });
                }
            }
        }
        if (!sysState.hasHapticHardwareSupport()) {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATE_ON);
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATION_DURATION);
        }
        if (!AppPreferences.from(getContext()).isSetVibrationDurationAllowed()) {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATION_DURATION);
        } else {
            removeCategorySubPreference(this.screen, KEYBOARD_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_VIBRATE_ON);
        }
        CheckBoxPreference voiceKeyPref = (CheckBoxPreference) this.screen.findPreference(UserPreferences.SHOW_VOICE_KEY);
        if (voiceKeyPref != null) {
            if (IMEApplication.from(getContext()).isTrialExpired()) {
                removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.SHOW_VOICE_KEY);
                removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.END_OF_SPEECH_DETECTION);
            } else {
                enableCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.END_OF_SPEECH_DETECTION, voiceKeyPref.isChecked());
            }
        }
        if (!language.isKoreanLanguage()) {
            removeCategorySubPreference(this.screen, PREDICTION_SETTINGS_CATEGORY_KEY, UserPreferences.PREF_ENABLE_KOREAN_CONSONANT_INPUT);
        }
        if (!this.connect.isLicensed() || (!this.connect.isStarted() && !this.activity.getResources().getBoolean(R.bool.enable_china_connect_special))) {
            removeCategorySubPreference(this.screen, BASIC_SETTINGS_CATEGORY_KEY, ENABLE_DATA_CONNECTION_KEY);
            return;
        }
        CustomCheckBoxPreference pref = (CustomCheckBoxPreference) this.screen.findPreference(ENABLE_DATA_CONNECTION_KEY);
        if (pref != null) {
            boolean enableConnection = UserPreferences.from(this.activity).connectUseCellularData();
            pref.setChecked(enableConnection);
        }
    }

    private void buildLanguagesScreen() {
        InputMethods inputMethods = InputMethods.from(getContext());
        inputMethods.syncWithCurrentUserConfiguration();
        InputMethods.Language language = inputMethods.getCurrentInputLanguage();
        if (language != null) {
            if (language.isCJK()) {
                removeCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA);
                return;
            }
            Preference recognitionSpeedPref = this.screen.findPreference(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA);
            recognitionSpeedSummary();
            if (recognitionSpeedPref != null) {
                if (recognitionSpeedPref instanceof ViewClickPreference) {
                    ((ViewClickPreference) recognitionSpeedPref).setListener(new ViewClickPreference.ViewClickPreferenceListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.11
                        @Override // com.nuance.swype.preference.ViewClickPreference.ViewClickPreferenceListener
                        public void onViewClick(Preference pref) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.doShowDialog(11, args);
                        }
                    });
                } else {
                    recognitionSpeedPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.12
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            Bundle args = new Bundle();
                            SettingsPrefs.this.doShowDialog(11, args);
                            return true;
                        }
                    });
                }
            }
        }
    }

    private static void removeCategorySubPreference(PreferenceScreen screen, String categoryName, String prefName) {
        PreferenceCategory category = (PreferenceCategory) screen.findPreference(categoryName);
        if (category != null) {
            removePreference(category, prefName);
            if (category.getPreferenceCount() == 0) {
                screen.removePreference(category);
            }
        }
    }

    private static void removePreference(PreferenceGroup group, String prefName) {
        Preference preference = group.findPreference(prefName);
        if (preference != null) {
            group.removePreference(preference);
        }
    }

    private static void enableCategorySubPreference(PreferenceScreen screen, String categoryName, String prefName, boolean enable) {
        PreferenceCategory category = (PreferenceCategory) screen.findPreference(categoryName);
        if (category != null) {
            enablePreference(category, prefName, enable);
        }
    }

    private static void enablePreference(PreferenceGroup group, String prefName, boolean enable) {
        Preference preference = group.findPreference(prefName);
        if (preference != null) {
            preference.setEnabled(enable);
        }
    }

    @Override // android.preference.Preference.OnPreferenceChangeListener
    @SuppressLint({"NewApi"})
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        IME currentIME;
        if (preference instanceof CheckBoxPreference) {
            boolean checked = ((Boolean) newValue).booleanValue();
            boolean providerIsDragon = IMEApplication.from(getContext()).getSpeechProvider() == 0;
            SystemState systemState = IMEApplication.from(getContext()).getSystemState();
            UserPreferences userPrefs = UserPreferences.from(this.activity);
            if (UserPreferences.SHOW_VOICE_KEY.equals(preference.getKey())) {
                if (checked && providerIsDragon && ContextCompat.checkSelfPermission(this.activity.getApplicationContext(), "android.permission.RECORD_AUDIO") != 0) {
                    requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 0);
                    this.isPermissionDialogRationale = this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
                    return false;
                }
                enableCategorySubPreference(this.screen, VOICE_HANDWRITING_CATEGORY_KEY, UserPreferences.END_OF_SPEECH_DETECTION, checked && providerIsDragon && ContextCompat.checkSelfPermission(this.activity.getApplicationContext(), "android.permission.RECORD_AUDIO") == 0);
            } else if (preference.getKey().equalsIgnoreCase(UserPreferences.PREF_VIBRATE_ON) && systemState.isKeyboardVibrateEnabled() != -1) {
                if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(getContext())) {
                    Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    getContext().startActivity(intent);
                    return false;
                }
                boolean currentValue = Boolean.parseBoolean(newValue.toString());
                systemState.setKeyboardVibrate(currentValue);
            } else if (preference.getKey().equalsIgnoreCase(UserPreferences.PREF_SOUND_ON) && systemState.isKeyboardSoundEnabled() != -1) {
                if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(getContext())) {
                    Intent intent2 = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
                    Uri uri2 = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent2.setData(uri2);
                    getContext().startActivity(intent2);
                    return false;
                }
                boolean currentValue2 = Boolean.parseBoolean(newValue.toString());
                systemState.setKeyboardSound(currentValue2);
            } else if (preference.getKey().equalsIgnoreCase(UserPreferences.PREF_NUMBER_ROW)) {
                this.settingsChanged = true;
            } else if (preference.getKey().equalsIgnoreCase(UserPreferences.PREF_HIDE_SECONDARIES)) {
                log.d("show secondary characters:", Boolean.valueOf(checked));
                this.settingsChanged = true;
            } else {
                if (this.activity.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                    return true;
                }
                if (preference.getKey().equals(ENABLE_DATA_CONNECTION_KEY)) {
                    log.d(" ENABLE_DATA_CONNECTION_KEY ", Boolean.valueOf(checked));
                    userPrefs.setConnectUseCellularData(checked);
                    Connect.from(this.activity).enableCellularData(checked);
                    this.settingsChanged = true;
                }
            }
        }
        StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(getContext());
        if (sessionScribe != null && (preference instanceof CheckBoxPreference)) {
            boolean value = ((Boolean) newValue).booleanValue();
            sessionScribe.recordSettingsChange(preference.getKey(), Boolean.valueOf(value), Boolean.valueOf(!value));
        }
        IMEApplication imeApp = IMEApplication.from(getContext());
        if (imeApp != null && (currentIME = imeApp.getIME()) != null) {
            currentIME.clearSavedKeyboardState();
        }
        if (preference.getKey().equals(UserPreferences.PREF_AD_FIRST_TIME)) {
            if (newValue.equals("") || newValue.equals("0")) {
                return false;
            }
            preference.setSummary(newValue.toString());
            IMEApplication.from(getContext()).getAdSessionTracker().updateStart(Integer.valueOf(newValue.toString()).intValue());
        } else if (preference.getKey().equals(UserPreferences.PREF_AD_STEP_SIZE)) {
            if (newValue.equals("") || newValue.equals("0")) {
                return false;
            }
            preference.setSummary(newValue.toString());
            IMEApplication.from(getContext()).getAdSessionTracker().updateStepSize(Integer.valueOf(newValue.toString()).intValue());
        } else if (preference.getKey().equals(UserPreferences.PREF_AD_MAX_PER_DAY)) {
            if (newValue.equals("") || newValue.equals("0")) {
                return false;
            }
            preference.setSummary(newValue.toString());
            IMEApplication.from(getContext()).getAdSessionTracker().updateMaxNumberOfTimes(Integer.valueOf(newValue.toString()).intValue());
        }
        return true;
    }

    private void setupPreferenceHandler(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup group = (PreferenceGroup) preference;
            int count = group.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                setupPreferenceHandler(group.getPreference(i));
            }
            return;
        }
        preference.setOnPreferenceChangeListener(this);
    }

    public static void resetSettings(Context context) {
        IMEApplication app = IMEApplication.from(context);
        app.getUserPreferences().reset();
        MyWordsPrefs.resetToDefault(context);
        Connect.Accounts accounts = Connect.from(context).getAccounts();
        String deviceId = Connect.from(context).getDeviceId();
        if (accounts != null && accounts.getDevices() != null) {
            for (ACDevice device : accounts.getDevices()) {
                if (device.getIdentifier().equals(deviceId)) {
                    accounts.unlinkDevice(device);
                    log.d("resetSettings...unlink device...", deviceId);
                }
            }
        }
        OAuthPreference.reset(context);
        app.resetScrapperStatus();
    }

    @SuppressLint({"InflateParams"})
    public Dialog createVibrationDurationDialog() {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.vibration_duration_dialog, (ViewGroup) null);
        final Resources res = getContext().getResources();
        final int min = res.getInteger(R.integer.vibrate_duration_min_ms);
        int max = res.getInteger(R.integer.vibrate_duration_max_ms) - min;
        final int defaultValue = res.getInteger(R.integer.vibrate_duration_ms);
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        this.mValue = getAutoDelay(sp, UserPreferences.PREF_VIBRATION_DURATION, defaultValue);
        final TextView valueDuration = (TextView) view.findViewById(R.id.valueVibration);
        valueDuration.setText(String.format(res.getString(R.string.millisecond), String.valueOf(this.mValue)));
        ((TextView) view.findViewById(R.id.valueTextMin)).setText(String.format("%d", Integer.valueOf(min)));
        TextView valueMax = (TextView) view.findViewById(R.id.valueTextMax);
        int value = max + min;
        valueMax.setText(String.format(res.getString(R.string.millisecond), String.valueOf(value)));
        SeekBar longPressSeekBar = (SeekBar) view.findViewById(R.id.duration_seekbar);
        if (display.density < 0.8f) {
            longPressSeekBar.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        longPressSeekBar.setMax(max / 5);
        longPressSeekBar.setProgress((this.mValue - min) / 5);
        longPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.13
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.mValue = (progress * 5) + min;
                Resources res2 = SettingsPrefs.this.getContext().getResources();
                valueDuration.setText(String.format(res2.getString(R.string.millisecond), String.valueOf(SettingsPrefs.this.mValue)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setTitle(R.string.pref_vibration_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SettingsPrefs.this.saveAutoDelay(UserPreferences.PREF_VIBRATION_DURATION, SettingsPrefs.this.mValue);
                SettingsPrefs.this.screen.findPreference(UserPreferences.PREF_VIBRATION_DURATION).setSummary(res.getString(R.string.millisecond, Integer.valueOf(SettingsPrefs.this.mValue)));
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SettingsPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(UserPreferences.PREF_VIBRATION_DURATION, Integer.valueOf(SettingsPrefs.this.mValue), Integer.valueOf(defaultValue));
                }
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    @SuppressLint({"InflateParams"})
    public Dialog createLongPressDurationDialog() {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.longpress_delay_dialog, (ViewGroup) null);
        final Resources res = getContext().getResources();
        final int min = res.getInteger(R.integer.long_press_timeout_min_ms);
        int max = res.getInteger(R.integer.long_press_timeout_max_ms) - min;
        int defaultValue = res.getInteger(R.integer.long_press_timeout_ms);
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        this.mValue = getAutoDelay(sp, UserPreferences.PREF_LONG_PRESS_DELAY, defaultValue);
        final int oldValue = this.mValue;
        final TextView valueLongPress = (TextView) view.findViewById(R.id.valueLongPress);
        valueLongPress.setText(String.format(res.getString(R.string.millisecond), formatNumberByLocale(this.mValue)));
        SeekBar longPressSeekBar = (SeekBar) view.findViewById(R.id.longpress_seekbar);
        if (display.density < 0.8f) {
            longPressSeekBar.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        longPressSeekBar.setMax(max / 10);
        longPressSeekBar.setProgress((this.mValue - min) / 10);
        longPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.15
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.mValue = (progress * 10) + min;
                Resources res2 = SettingsPrefs.this.getContext().getResources();
                valueLongPress.setText(String.format(res2.getString(R.string.millisecond), SettingsPrefs.this.formatNumberByLocale(SettingsPrefs.this.mValue)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setTitle(R.string.pref_long_press_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SettingsPrefs.this.saveAutoDelay(UserPreferences.PREF_LONG_PRESS_DELAY, SettingsPrefs.this.mValue);
                SettingsPrefs.this.screen.findPreference(UserPreferences.PREF_LONG_PRESS_DELAY).setSummary(res.getString(R.string.millisecond, Integer.valueOf(SettingsPrefs.this.mValue)));
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SettingsPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(UserPreferences.PREF_LONG_PRESS_DELAY, Integer.valueOf(SettingsPrefs.this.mValue), Integer.valueOf(oldValue));
                }
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatNumberByLocale(double number) {
        return NumberFormat.getInstance(new Locale(getContext().getResources().getConfiguration().locale.getLanguage())).format(number);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getAutoDelay(UserPreferences sp, String key, int defValue) {
        try {
            int delay = sp.getInt(key, defValue);
            return delay;
        } catch (ClassCastException e) {
            sp.remove(key);
            sp.setInt(key, defValue);
            return defValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveAutoDelay(String key, int delay) {
        UserPreferences.from(getContext()).setInt(key, delay);
    }

    @SuppressLint({"InflateParams"})
    public Dialog createRecognitionSpeedDialog() {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.longpress_delay_dialog, (ViewGroup) null);
        final Resources res = getContext().getResources();
        final int min = res.getInteger(R.integer.handwriting_auto_recognize_alpha_min_ms);
        int max = res.getInteger(R.integer.handwriting_auto_recognize_alpha_max_ms) - min;
        final int defaultValue = res.getInteger(R.integer.handwriting_auto_recognize_alpha_default_ms);
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        this.mValue = getAutoDelay(sp, UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, defaultValue);
        final TextView valueLongPress = (TextView) view.findViewById(R.id.valueLongPress);
        valueLongPress.setText(String.format(res.getString(R.string.second), Float.valueOf(this.mValue / UNIT_CONVERSION_CONSTANT)));
        SeekBar recognitionDelaySeekBar = (SeekBar) view.findViewById(R.id.longpress_seekbar);
        if (display.density < 0.8f) {
            recognitionDelaySeekBar.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        recognitionDelaySeekBar.setMax(max / 50);
        recognitionDelaySeekBar.setProgress((this.mValue - min) / 50);
        recognitionDelaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.17
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.mValue = (progress * 50) + min;
                Resources res2 = SettingsPrefs.this.getContext().getResources();
                valueLongPress.setText(res2.getString(R.string.second, Float.valueOf(SettingsPrefs.this.mValue / SettingsPrefs.UNIT_CONVERSION_CONSTANT)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setTitle(R.string.handwriting_recognize_speed_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.18
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SettingsPrefs.this.saveAutoDelay(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, SettingsPrefs.this.mValue);
                SettingsPrefs.this.screen.findPreference(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA).setSummary(res.getString(R.string.second, Float.valueOf(SettingsPrefs.this.mValue / SettingsPrefs.UNIT_CONVERSION_CONSTANT)));
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SettingsPrefs.this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, Integer.valueOf(SettingsPrefs.this.mValue), Integer.valueOf(defaultValue));
                }
                UserPreferences sp2 = IMEApplication.from(SettingsPrefs.this.getContext()).getUserPreferences();
                SettingsPrefs.this.mValue = SettingsPrefs.getAutoDelay(sp2, UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, defaultValue);
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createKeyboardHeightDialog(Bundle args) {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        ViewGroup scrollView = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.keyboard_height_dialog, (ViewGroup) null);
        final TextView valueTextPortrait = (TextView) scrollView.findViewById(R.id.valueTextPortrait);
        UserPreferences userPrefs = IMEApplication.from(getContext()).getUserPreferences();
        this.mValueKeyboardHeightPortrait = userPrefs.getKeyboardScalePortrait();
        valueTextPortrait.setText(String.format(getContext().getResources().getString(R.string.percent), Integer.valueOf((int) (this.mValueKeyboardHeightPortrait * 100.0f))));
        SeekBar seekBarPortrait = (SeekBar) scrollView.findViewById(R.id.portrait_seekbar);
        if (display.density < 0.8f) {
            seekBarPortrait.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        seekBarPortrait.setMax(4);
        seekBarPortrait.incrementProgressBy(1);
        seekBarPortrait.setProgress(((int) (this.mValueKeyboardHeightPortrait * 10.0f)) - 8);
        seekBarPortrait.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.19
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.mValueKeyboardHeightPortrait = (progress + 8) / 10.0f;
                valueTextPortrait.setText(String.format(SettingsPrefs.this.getContext().getResources().getString(R.string.percent), Integer.valueOf((progress + 8) * 10)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        final TextView valueTextLandscape = (TextView) scrollView.findViewById(R.id.valueTextLandscape);
        this.mValueKeyboardHeightLandscape = userPrefs.getKeyboardScaleLandscape();
        valueTextLandscape.setText(String.format(getContext().getResources().getString(R.string.percent), Integer.valueOf((int) (this.mValueKeyboardHeightLandscape * 100.0f))));
        SeekBar seekBarLandscape = (SeekBar) scrollView.findViewById(R.id.landscape_seekbar);
        if (display.density < 0.8f) {
            seekBarLandscape.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        seekBarLandscape.setMax(4);
        seekBarLandscape.incrementProgressBy(1);
        seekBarLandscape.setProgress(((int) (this.mValueKeyboardHeightLandscape * 10.0f)) - 8);
        seekBarLandscape.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.20
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.mValueKeyboardHeightLandscape = (progress + 8) / 10.0f;
                valueTextLandscape.setText(String.format(SettingsPrefs.this.getContext().getResources().getString(R.string.percent), Integer.valueOf((progress + 8) * 10)));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(R.string.pref_kb_height_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.21
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SettingsPrefs.this.getContext());
                UserPreferences userPrefs2 = IMEApplication.from(SettingsPrefs.this.getContext()).getUserPreferences();
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange("Keyboard Height", "Portrait:" + (SettingsPrefs.this.mValueKeyboardHeightPortrait / 10.0f) + " Landscape:" + (SettingsPrefs.this.mValueKeyboardHeightLandscape / 10.0f), "Portrait:" + userPrefs2.getKeyboardScalePortrait() + " Landscape:" + userPrefs2.getKeyboardScaleLandscape());
                }
                IME ime = IMEApplication.from(SettingsPrefs.this.getContext()).getIME();
                if (ime != null) {
                    ime.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
                }
                userPrefs2.setKeyboardScalePortrait(SettingsPrefs.this.mValueKeyboardHeightPortrait);
                userPrefs2.setKeyboardScaleLandscape(SettingsPrefs.this.mValueKeyboardHeightLandscape);
                SettingsPrefs.this.keyboardHeightSummaryUpdate();
            }
        });
        builder.setView(scrollView);
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createCandidateSizeDialog(Bundle args) {
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        LayoutInflater systemInflater = (LayoutInflater) this.activity.getSystemService("layout_inflater");
        ViewGroup scrollView = (ViewGroup) systemInflater.inflate(R.layout.candidate_size_dialog, (ViewGroup) null);
        View candidateView = IMEApplication.from(getContext()).getThemedLayoutInflater(systemInflater).inflate(R.layout.candidate_size_setting_view, (ViewGroup) null);
        candidateView.setBackgroundDrawable(IMEApplication.from(getContext()).getThemedDrawable(R.attr.keyboardSuggestStrip));
        ((ViewGroup) scrollView.findViewById(R.id.candidate_size_group)).addView(candidateView, 0, new LinearLayout.LayoutParams(-1, (int) getContext().getResources().getDimension(R.dimen.candidates_list_height)));
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        final String displayText = getContext().getString(R.string.swype);
        final CandidateSizeSettingView cs = (CandidateSizeSettingView) candidateView;
        this.changedProgress = getCandidatesSize(sp, "Candidates_Size", 1.0f);
        cs.setTextSize(this.changedProgress);
        cs.setDisplayText(displayText);
        SeekBar sizeSeekBar = (SeekBar) scrollView.findViewById(R.id.size_seekbar);
        if (display.density < 0.8f) {
            sizeSeekBar.setPadding((int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) getContext().getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        sizeSeekBar.setMax(10);
        sizeSeekBar.incrementProgressBy(1);
        sizeSeekBar.setProgress((int) (((getCandidatesSize(sp, "Candidates_Size", 1.0f) * 100.0f) - 100.0f) / 3.0f));
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.22
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsPrefs.this.changedProgress = 1.0f + (progress * 0.03f);
                SettingsPrefs.this.scalingFactor = progress;
                cs.setTextSize(SettingsPrefs.this.changedProgress);
                cs.setDisplayText(displayText);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity).setIcon(R.drawable.swype_logo).setTitle(R.string.pref_word_choice_title).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.23
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SettingsPrefs.this.getContext());
                if (sessionScribe != null) {
                    float defaultValue = IMEApplication.from(SettingsPrefs.this.getContext()).getUserPreferences().getFloat("Candidates_Size", 1.0f);
                    sessionScribe.recordSettingsChange("Word Choice Size", Float.valueOf(SettingsPrefs.this.changedProgress), Float.valueOf(defaultValue));
                }
                SettingsPrefs.this.saveCandidatesSize("Candidates_Size", SettingsPrefs.this.changedProgress);
                SettingsPrefs.this.setCandidateFontSize(SettingsPrefs.CANDIDATES_SUMMARY, SettingsPrefs.this.scalingFactor);
                SettingsPrefs.this.wordChoiceListFontSummary(SettingsPrefs.this.candidateSizePrefs);
            }
        });
        builder.setView(scrollView);
        return builder.create();
    }

    public static float getCandidatesSize(UserPreferences sp, String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public static int getCandidatesSummarySize(UserPreferences sp, String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveCandidatesSize(String key, float size) {
        IMEApplication.from(getContext()).getUserPreferences().setFloat(key, size);
    }

    public void setCandidateFontSize(String key, int scalingFactor) {
        IMEApplication.from(getContext()).getUserPreferences().setInt(key, scalingFactor);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        CheckBoxPreference voiceKeyPref;
        if (requestCode == 0 && grantResults.length != 0) {
            boolean isGrantedAllPermissions = true;
            int length = grantResults.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (grantResults[i] == 0) {
                    i++;
                } else {
                    isGrantedAllPermissions = false;
                    break;
                }
            }
            if (isGrantedAllPermissions && (voiceKeyPref = (CheckBoxPreference) this.screen.findPreference(UserPreferences.SHOW_VOICE_KEY)) != null) {
                boolean oldValue = voiceKeyPref.isChecked();
                voiceKeyPref.setChecked(!oldValue);
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(voiceKeyPref.getKey(), Boolean.valueOf(oldValue ? false : true), Boolean.valueOf(oldValue));
                }
            }
        }
        recordUserActionForPermissionDialog(requestCode, permissions, grantResults);
    }

    @SuppressLint({"NewApi"})
    private void recordUserActionForPermissionDialog(int requestCode, String[] permissions, int[] grantResults) {
        UsageData.PermissionUserAction userAction;
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isPermissionDialogRationale = this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
            if (requestCode == 0 && grantResults.length != 0 && permissions[0].equals("android.permission.RECORD_AUDIO")) {
                if (grantResults[0] == 0) {
                    userAction = UsageData.PermissionUserAction.ALLOWED;
                } else {
                    if (isPermissionDialogRationale) {
                        userAction = UsageData.PermissionUserAction.DENIED;
                    } else if (this.isPermissionDialogRationale) {
                        userAction = UsageData.PermissionUserAction.NEVER_SHOW_AGAIN;
                    } else {
                        userAction = UsageData.PermissionUserAction.BLOCKED;
                    }
                    this.isPermissionDialogRationale = isPermissionDialogRationale;
                }
                UsageData.recordPermissionRequest(UsageData.Permission.RECORD_AUDIO_SHOW_VOICE_KEY, userAction);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStart() {
        this.settingsChanged = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStop() {
        if (this.settingsChanged) {
            log.d("Settings for My Words have been changed, tag them before leaving");
            UserPreferences userPrefs = UserPreferences.from(getContext());
            UsageData.recordSettingsSummary(userPrefs.getShowNumberRow(), (userPrefs.getKeyboardHideSecondaries() || userPrefs.getShowNumberRow()) ? false : true, userPrefs.connectUseCellularData());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createSkinToneDialog(Bundle args) {
        ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) this.activity.getSystemService("layout_inflater")).inflate(R.layout.skin_tone_dialog, (ViewGroup) null);
        final List<Emoji> emojis = EmojiLoader.getEmojiList();
        EmojiSkinToneListAdapter emojiSkinToneListAdapter = new EmojiSkinToneListAdapter(emojis, this.activity, this);
        ListView listView = (ListView) viewGroup.findViewById(R.id.skin_listView);
        listView.setAdapter((ListAdapter) emojiSkinToneListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.nuance.swype.input.settings.SettingsPrefs.24
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Emoji.SkinToneEnum skinToneEnum = Emoji.SkinToneEnum.getSkinToneFromCode(((Emoji) emojis.get(position)).getDefaultSkinToneColor());
                UserPreferences.from(SettingsPrefs.this.getContext()).setDefaultEmojiSkin(skinToneEnum);
                SettingsPrefs.this.setUserSelectEmojiSkinTone();
                SettingsPrefs.this.skinToneDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.swype_logo);
        builder.setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setTitle(this.activity.getString(R.string.dialog_title_default_skintone));
        builder.setView(viewGroup);
        this.skinToneDialog = builder.create();
        return this.skinToneDialog;
    }

    public static String getEmojiSkinToneCode(Context context) {
        List<Emoji> emojis = EmojiLoader.getEmojiList();
        Emoji.SkinToneEnum skinToneEnum = UserPreferences.from(context).getDefaultEmojiSkin();
        for (int i = 0; i < emojis.size(); i++) {
            log.d("getEmojiSkinToneCode()", " called >>>>> emojiSkinToneCode =" + skinToneEnum.getSkinValue());
            if (skinToneEnum.getSkinValue() == emojis.get(i).getDefaultSkinToneColor()) {
                return emojis.get(i).getEmojiDisplayCode();
            }
        }
        return null;
    }

    public void setUserSelectEmojiSkinTone() {
        if (this.emojiSkinTonePreference != null) {
            ((EmojiSkinTonePreference) this.emojiSkinTonePreference).setEmojiText(getEmojiSkinToneCode(getContext()));
        }
        if (this.skinToneDialog != null && this.skinToneDialog.isShowing()) {
            this.skinToneDialog.dismiss();
        }
    }

    public void keyboardHeightSummaryUpdate() {
        UserPreferences userPrefs = UserPreferences.from(getContext());
        this.mValueKeyboardHeightPortrait = userPrefs.getKeyboardScalePortrait();
        this.mValueKeyboardHeightLandscape = userPrefs.getKeyboardScaleLandscape();
        Resources res = getContext().getResources();
        String portrait = String.format(res.getString(R.string.accessibility_notification_on_keyboard_portrait_short), new Object[0]);
        String lanscape = String.format(res.getString(R.string.accessibility_notification_on_keyboard_landscape_short), new Object[0]);
        StringBuilder sb = new StringBuilder();
        sb.append(portrait).append(XMLResultsHandler.SEP_SPACE).append((int) (this.mValueKeyboardHeightPortrait * 100.0f)).append("% & ").append(lanscape).append(XMLResultsHandler.SEP_SPACE).append((int) (this.mValueKeyboardHeightLandscape * 100.0f)).append("%");
        this.keyboardHeightPrefs.setSummary(sb.toString());
    }

    public void recognitionSpeedSummary() {
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        Resources res = getContext().getResources();
        int defaultValue = res.getInteger(R.integer.handwriting_auto_recognize_alpha_default_ms);
        this.mValue = getAutoDelay(sp, UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, defaultValue);
        this.screen.findPreference(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA).setSummary(res.getString(R.string.second, Float.valueOf(this.mValue / UNIT_CONVERSION_CONSTANT)));
    }

    public void wordChoiceListFontSummary(Preference candidateSizePrefs) {
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        Resources res = getContext().getResources();
        this.scalingFactor = getCandidatesSummarySize(sp, CANDIDATES_SUMMARY, 0);
        if (this.scalingFactor >= 0 && this.scalingFactor <= 4) {
            this.fontSize = res.getString(R.string.settings_word_font_small);
        } else if (this.scalingFactor == 5) {
            this.fontSize = res.getString(R.string.settings_word_font_normal);
        } else {
            this.fontSize = res.getString(R.string.settings_word_font_large);
        }
        candidateSizePrefs.setSummary(this.fontSize);
    }
}
