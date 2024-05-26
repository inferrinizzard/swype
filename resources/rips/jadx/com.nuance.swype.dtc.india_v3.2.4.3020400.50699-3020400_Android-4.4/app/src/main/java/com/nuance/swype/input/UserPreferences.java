package com.nuance.swype.input;

import android.content.Context;
import android.preference.PreferenceManager;
import com.nuance.connect.internal.common.Document;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.emoji.Emoji;
import java.util.Set;

/* loaded from: classes.dex */
public final class UserPreferences extends SwypePreferences {
    public static final String ACTIVATION_POPUP = "activation_popup";
    private static final String ACTIVE_DOWNLOADED_THEME_ID_KEY = "downloaded.theme";
    public static final String ADDONDICTIONARIES_CATEGORY = "addondictionaries";
    protected static final String ADD_ON_DICTIONARIES = "addondictionaries";
    protected static final String AD_TAG_FOR_CHILD_DIRECTED_TREATMENT = "ad_tag_for_child_directed";
    protected static final String ASK_BEFORE_ADD = "ask_before_add";
    public static final String CHECK_PACKAGE_UPDATE = "check_package_update";
    public static final String CHINESESETTINGS_CATEGORY = "chinesesettings";
    public static final String CHINESE_FUZZY_F_H = "fuzzy_pinyin_f_h";
    public static final String CHINESE_FUZZY_N_L = "fuzzy_pinyin_n_l";
    public static final String CHINESE_FUZZY_N_NG = "fuzzy_pinyin_n_ng";
    public static final String CHINESE_FUZZY_R_L = "fuzzy_pinyin_r_l";
    public static final String CHINESE_FUZZY_Z_ZH_C_CH_S_SH = "fuzzy_pinyin_z_zh_c_ch_s_sh";
    protected static final String CHINESE_SETTINGS = "chinesesettings";
    public static final String CONNECT_DLM_SYNC = "pref_backup_enabled";
    public static final String CONNECT_USE_CELLULAR_DATA = "connect_use_cellular_data";
    static final String CURRENT_DICTATION_LANGUAGE = "dictation.language";
    private static final String CURRENT_THEME_ID_KEY = "swype.theme";
    public static final String DATA_USAGE_ACCEPTED_KEY = "data_usage_accepted";
    public static final String DEFAULT_AUTOMATIC_DICTATION_LANGUAGE = "default.automatic";
    public static final String DEPRECATED_CONNECT_LIVING_LANGUAGE = "connect_living_language";
    public static final String DEVICE_TYPE_PROPERTY = "device_type_property";
    protected static final String EDITKEYBOARD = "edit_layer";
    public static final String EDITKEYBOARD_CATEGORY = "edit_layer";
    public static final String EMOJI_ON_FUNCTION_BAR = "function_bar_emoji";
    public static final String END_OF_SPEECH_DETECTION = "end_of_speech_detection";
    protected static final String FUNCTIONBAR_THEMES = "themes";
    public static final String HWR_AUTO_ACCEPT_COLOR = "pen_color";
    public static final String HWR_AUTO_ACCEPT_RECOGNIZE = "hwr_auto_recpgnize_delay";
    public static final String HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA = "hwr_auto_recognize_delay_alpha";
    public static final String HWR_PEN_SIZE = "hwr_pen_size";
    public static final String HWR_SETTINGS = "handwriting_settings";
    protected static final String INPUTMODE = "inputmode";
    public static final String INPUTMODE_CATEGORY = "inputmode";
    private static final String KEYBOARD_DOCKING_MODE = "keyboard.docking.mode";
    private static final String KEYBOARD_FONT_BOLD = "keyboard_font_weight_bold";
    private static final String KEYBOARD_SCALE_LANDSCAPE = "Keyboard_Scale_Landscape";
    private static final String KEYBOARD_SCALE_PORTRAIT = "Keyboard_Scale_Portrait";
    public static final String KOREAN_AUTO_CORRECTION = "korean_auto_correction";
    protected static final String LANGUAGE_OPTION = "language_option";
    public static final String LANGUAGE_OPTION_CATEGORY = "language_option";
    private static final String LAST_CHECKED_SKU_LIST = "last_checked_sku_inventory";
    private static final String LOGIN_GOOGLE_ACCOUNT = "login_google_account";
    private static final String MLS_GOOGLE_PLAY_SERVICE_UPGRADE = "mls_google_play_service_upgrade";
    private static final String MLS_HOT_WORDS_ADDED = "mls_hot_words_added";
    private static final String MLS_HOT_WORDS_ADDED_OVER = "mls_hot_words_added_over";
    public static final String MLS_HOT_WORDS_LEFT_NUM = "mls_hot_words_left_num";
    public static final String NEXT_WORD_PREDICTION = "next_word_prediction";
    protected static final String NUMBERKEYBOARD = "number_keyboard";
    public static final String NUMBERKEYBOARD_CATEGORY = "number_keyboard";
    public static final String PREF_AD_FIRST_TIME = "ad_setting_first_time";
    public static final String PREF_AD_MAX_PER_DAY = "ad_setting_max_ad_in_day";
    public static final String PREF_AD_STEP_SIZE = "ad_setting_step_size";
    public static final String PREF_ATTACH_COMMON_WORDS_LDB = "attach_common_words_ldb";
    public static final String PREF_AUTO_CAP = "auto_cap";
    public static final String PREF_AUTO_CORRECTION = "auto_correction";
    private static final String PREF_AUTO_DEFAULT_LAYER_RETURN = "auto_default_layer_return";
    public static final String PREF_AUTO_PUNCTUATION = "auto_punctuation";
    public static final String PREF_AUTO_SPACE = "auto_space";
    public static final String PREF_BACKSPACE_REVERT = "backspace_revert";
    public static final String PREF_CLOUD_INPUT = "cloud_input";
    private static final String PREF_DRAWER_AUTO_OPENED_ONCE = "drawer_automatically_opened_once";
    public static final String PREF_EMOJI_CHOICE_LIST = "emoji_choice_list";
    public static final String PREF_EMOJI_SKIN_TONE = "pref_emoji_skin_tone";
    public static final String PREF_EMOJI_SUGGESTIONS = "emoji_suggestions";
    public static final String PREF_ENABLE_CHINESE_BILINGUAL = "enable_chinese_bilingual";
    public static final String PREF_ENABLE_DIFF_HWR_TYPE = "enable_diff_hwr_result_type";
    public static final String PREF_ENABLE_HANDWRITING = "enable_handwriting";
    public static final String PREF_ENABLE_HWR_ROTATION = "enable_hwr_rotation";
    public static final String PREF_ENABLE_HWR_SCR_MODE = "enable_hwr_scr_mode";
    public static final String PREF_ENABLE_INSTANT_GESTURE = "enable_instant_gesture";
    public static final String PREF_ENABLE_KOREAN_CONSONANT_INPUT = "enable_korean_consonant_input";
    public static final String PREF_FULL_SENTENCE_ON = "full_sentence_input_on";
    public static final String PREF_HIDE_SECONDARIES = "hide_secondaries";
    public static final String PREF_HWR_ALTERNATIVE_DIRECTION = "enable_hwr_alternative_direction";
    public static final String PREF_LONG_PRESS_DELAY = "long_press_delay";
    public static final String PREF_NETWORK_AGREEMENT = "agree_using_network";
    public static final String PREF_NETWORK_AGREEMENT_NOT_ASK = "agree_using_network_not_ask";
    public static final String PREF_NUMBER_ROW = "number_row";
    public static final String PREF_SHOW_CHARS_ON_KEYPRESS = "show_chars_on_keypress";
    public static final String PREF_SHOW_COMPLETE_TRACE = "show_complete_trace";
    public static final String PREF_SHOW_KEY_ALT_SYMBOLS = "show_key_alt_symbols";
    public static final String PREF_SHOW_NETWORK_DIALOG_FROM_KEYBOARD = "show_network_dialog_from_keyboard";
    public static final String PREF_SOUND_ON = "sound_on";
    public static final String PREF_SWYPING = "enable_swyping";
    public static final String PREF_UNRECOGNIZED_WORD_DECORATION = "unrecognized_word_decoration";
    public static final String PREF_VIBRATE_ON = "vibrate_on";
    public static final String PREF_VIBRATION_DURATION = "vibration_duration";
    protected static final String QUICKTOGGLE = "quicktoggle";
    public static final String QUICKTOGGLE_CATEGORY = "quicktoggle";
    protected static final String SETTINGS = "settings";
    public static final String SETTINGS_CATEGORY = "settings";
    protected static final String SHOWFUNCTIONBAR = "function_bar_display";
    public static final String SHOWN_USAGE_STATISTICS_LOG = "shown_usage_statistics_log";
    public static final String SHOW_FUNCTION_BAR = "function_bar_display";
    public static final String SHOW_VOICE_KEY = "show_voice_key";
    public static final String STATISTICS_COLLECTION = "statistics_collection";
    public static final String THEMES_CATEGORY = "themes";
    public static final String TRACE_AUTO_ACCEPT = "trace_auto_accept";
    public static final String TRACE_FILTER_DEFAULT = "hi";
    public static final String TRACE_FILTER_HIGH = "hi";
    public static final String TRACE_FILTER_KEY = "trace_filter";
    public static final String TRACE_FILTER_LOW = "low";
    public static final String USAGE_COLLECTION = "usage_collection";
    public static final String USAGE_STATISTICS_LOG_ACCEPTED_KEY = "usage_statistics_log_accepted";
    public static final String WORD_COMPLETION_POINT = "word_completion_point";
    private BuildInfo buildInfo;

    public static UserPreferences from(Context context) {
        return IMEApplication.from(context).getUserPreferences();
    }

    public static UserPreferences createUserPreferences(Context context, BuildInfo buildInfo) {
        return new UserPreferences(context, buildInfo);
    }

    public UserPreferences(Context context, BuildInfo buildInfo) {
        super(context, PreferenceManager.getDefaultSharedPreferences(context));
        this.buildInfo = buildInfo;
    }

    public static String makeChineseFullSentencePrefKey(InputMethods.InputMode inputMode) {
        if (inputMode.mParent.isChineseSimplified() || (inputMode.mParent.isChineseTraditional() && (InputMethods.isChineseInputModePinyin(inputMode.mInputMode) || InputMethods.isChineseInputModeZhuyin(inputMode.mInputMode)))) {
            String key = inputMode.mParent.isChineseSimplified() ? "chs" : "cht";
            return key + Document.ID_SEPARATOR + inputMode.mInputMode + "_full_sentence_input_on";
        }
        return null;
    }

    public final boolean getAutoCap() {
        return getBoolean(PREF_AUTO_CAP, getDefaultBoolean(R.bool.auto_cap_default));
    }

    public final boolean getAutoCorrection() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return false;
        }
        if (ime == null || !ime.isHardKeyboardActive()) {
            return getBoolean("auto_correction", getDefaultBoolean(R.bool.auto_correction_default));
        }
        return false;
    }

    public final boolean getAutoPunctuation() {
        return getBoolean(PREF_AUTO_PUNCTUATION, getDefaultBoolean(R.bool.auto_punctuation_default));
    }

    public final boolean getEnableChineseBilingual() {
        return getBoolean(PREF_ENABLE_CHINESE_BILINGUAL, getDefaultBoolean(R.bool.chinese_bilingual_default));
    }

    public final boolean getEnableKoreanConsonantInput() {
        return getBoolean(PREF_ENABLE_KOREAN_CONSONANT_INPUT, getDefaultBoolean(R.bool.enable_consonant_input_default));
    }

    public final boolean getAutoSpace() {
        return getBoolean(PREF_AUTO_SPACE, getDefaultBoolean(R.bool.auto_space_default));
    }

    public final boolean getShowCompleteTrace() {
        return getBoolean(PREF_SHOW_COMPLETE_TRACE, getDefaultBoolean(R.bool.show_complete_trace_default));
    }

    public final boolean getShowNumberRow() {
        return getBoolean(PREF_NUMBER_ROW, getDefaultBoolean(R.bool.number_row_default));
    }

    public final boolean getShowPressDownPreview() {
        return getBoolean(PREF_SHOW_CHARS_ON_KEYPRESS, getDefaultBoolean(R.bool.show_chars_on_keypress_default)) && isShowCharsOnKeypressEnabled();
    }

    public final boolean getMlsHotWordsImported() {
        return getBoolean(MLS_HOT_WORDS_ADDED, false);
    }

    public final void setMlsHotWordsImported() {
        setBoolean(MLS_HOT_WORDS_ADDED, true);
    }

    public final boolean isImportedMlsHotWordsOver() {
        return getBoolean(MLS_HOT_WORDS_ADDED_OVER, false);
    }

    public final boolean getGoogleAccountFailedDialogShown() {
        return getBoolean(LOGIN_GOOGLE_ACCOUNT, false);
    }

    public final void setGoogleAccountFailedDialogShown() {
        setBoolean(LOGIN_GOOGLE_ACCOUNT, true);
    }

    public final void setMlsHotWordsImportedOver(boolean over) {
        setBoolean(MLS_HOT_WORDS_ADDED_OVER, over);
    }

    public final boolean hasCheckedPackageUpdate() {
        return getBoolean(CHECK_PACKAGE_UPDATE, false);
    }

    public final void setCheckPackageUpdate(boolean update) {
        setBoolean(CHECK_PACKAGE_UPDATE, update);
    }

    public final boolean getGoogleServiceUpgradeDialogShown() {
        return getBoolean(MLS_GOOGLE_PLAY_SERVICE_UPGRADE, false);
    }

    public final void setGoogleServiceUpgradeDialogShown() {
        setBoolean(MLS_GOOGLE_PLAY_SERVICE_UPGRADE, true);
    }

    public final boolean getEnableSimplifiedMode() {
        return !getBoolean(PREF_SHOW_KEY_ALT_SYMBOLS, getDefaultBoolean(R.bool.show_key_alt_symbols_default));
    }

    public final boolean isNextWordPredictionEnabled() {
        if (IMEApplication.from(getContext()).getAppSpecificBehavior().shouldSkipWrongStartInputView()) {
            return false;
        }
        return getBoolean("next_word_prediction", getDefaultBoolean(R.bool.next_word_prediction_default));
    }

    public final boolean isEmojiChoiceListEnabled() {
        return getBoolean(PREF_EMOJI_CHOICE_LIST, getDefaultBoolean(R.bool.emoji_choice_list_default));
    }

    public final boolean isBackspaceRevertEnabled() {
        return getBoolean(PREF_BACKSPACE_REVERT, getDefaultBoolean(R.bool.enable_backspace_revert));
    }

    public final boolean isShowAutoCorrectionPrefEnabled() {
        IME ime;
        boolean showAutoCorrection = getDefaultBoolean(R.bool.show_pref_auto_correction);
        if (showAutoCorrection && (ime = IMEApplication.from(getContext()).getIME()) != null && ime.isHardKeyboardActive()) {
            return false;
        }
        return showAutoCorrection;
    }

    public final boolean isShowEnableSwypingEnabled() {
        return getDefaultBoolean(R.bool.show_enable_swyping);
    }

    public final boolean isShowWordChoiceSizePrefEnabled() {
        return getDefaultBoolean(R.bool.word_choice_size_show_prefs);
    }

    public final boolean isShowKeyboardHeightPrefEnabled() {
        return getDefaultBoolean(R.bool.keyboard_height_show_prefs);
    }

    public final boolean isUnrecognizedWordDecorationEnabled() {
        return getDefaultBoolean(R.bool.enable_unrecognized_word_decoration);
    }

    public final boolean isShowCharsOnKeypressEnabled() {
        return getDefaultBoolean(R.bool.show_preview_key);
    }

    public final boolean isKeySoundOn() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime == null || !ime.isAccessibilitySupportEnabled()) {
            return getBoolean(PREF_SOUND_ON, getDefaultBoolean(R.bool.sound_on_keypress_default));
        }
        return false;
    }

    public final boolean isVibrateOn() {
        return getBoolean(PREF_VIBRATE_ON, getDefaultBoolean(R.bool.vibrate_on_keypress_default));
    }

    public final int getVibrationDuration() {
        return getInt(PREF_VIBRATION_DURATION, getContext().getResources().getInteger(R.integer.vibrate_duration_ms));
    }

    public final int getLongPressDelay() {
        int delay = getInt(PREF_LONG_PRESS_DELAY, getContext().getResources().getInteger(R.integer.long_press_timeout_ms));
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return delay * 3;
        }
        return delay;
    }

    public final int getWordCompletionPoint() {
        return getInt(WORD_COMPLETION_POINT, 1);
    }

    public final boolean isTraceAutoAcceptEnabled() {
        return getBoolean("trace_auto_accept", getDefaultBoolean(R.bool.auto_accept_trace_default));
    }

    public final boolean isEndOfSpeechDetectionEnabled() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return true;
        }
        return getBoolean(END_OF_SPEECH_DETECTION, getDefaultBoolean(R.bool.end_of_speech_detection_default));
    }

    public final boolean isShowVoiceKeyEnabled() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime == null || ime.getInputFieldInfo() == null) {
            return true;
        }
        boolean dictationEnabledBuild = getDefaultBoolean(R.bool.dictation_enabled);
        boolean voiceKeyPref = getBoolean(SHOW_VOICE_KEY, getDefaultBoolean(R.bool.show_voice_key_default));
        return dictationEnabledBuild && voiceKeyPref && !ime.getInputFieldInfo().isVoiceDisabled();
    }

    public final int getFuzzyPairs(int defaultValue) {
        int nFuzzy = 0;
        if (getBoolean(CHINESE_FUZZY_Z_ZH_C_CH_S_SH, false)) {
            nFuzzy = 7;
        }
        if (getBoolean(CHINESE_FUZZY_N_NG, false)) {
            nFuzzy |= R.styleable.ThemeTemplate_wclCjkChineseInlineTextColor;
        }
        if (getBoolean(CHINESE_FUZZY_N_L, false)) {
            nFuzzy |= 8;
        }
        if (getBoolean(CHINESE_FUZZY_R_L, false)) {
            nFuzzy |= 16;
        }
        if (getBoolean(CHINESE_FUZZY_F_H, false)) {
            return nFuzzy | 32;
        }
        return nFuzzy;
    }

    public final boolean isStatisticsCollectionEnabled() {
        return getBoolean(STATISTICS_COLLECTION, false);
    }

    public final void setStatisticsCollectionEnabled(boolean value) {
        setBoolean(STATISTICS_COLLECTION, value);
    }

    public final void setDeviceType(boolean set) {
        setBoolean(DEVICE_TYPE_PROPERTY, set);
    }

    public final boolean isDeviceTypeRecorded() {
        return getBoolean(DEVICE_TYPE_PROPERTY, false);
    }

    public final boolean isDataUsageOptAccepted() {
        return getBoolean(DATA_USAGE_ACCEPTED_KEY, false);
    }

    public final void setDataUsageOptStatus(boolean value) {
        setBoolean(DATA_USAGE_ACCEPTED_KEY, value);
    }

    public final boolean connectUseCellularData() {
        return getBoolean(CONNECT_USE_CELLULAR_DATA, getDefaultBoolean(R.bool.connect_use_cellular_data));
    }

    public final void setConnectUseCellularData(boolean value) {
        setBoolean(CONNECT_USE_CELLULAR_DATA, value);
    }

    public final boolean isConnectLivingLanguageAllowed_Deprecated() {
        return getBoolean(DEPRECATED_CONNECT_LIVING_LANGUAGE, false);
    }

    public final void removeConnectLivingLanguageAllowed_Deprecated() {
        remove(DEPRECATED_CONNECT_LIVING_LANGUAGE);
    }

    public final boolean isConnectDLMBackupAllowed() {
        return getBoolean(CONNECT_DLM_SYNC, getDefaultBoolean(R.bool.connect_dlm_sync_default));
    }

    public final void setConnectDLMBackupAllowed(boolean value) {
        setBoolean(CONNECT_DLM_SYNC, value);
    }

    public final boolean isUsageCollectionEnabled() {
        return getBoolean(USAGE_COLLECTION, false);
    }

    public final void setUsageCollectionEnabled(boolean value) {
        setBoolean(USAGE_COLLECTION, value);
    }

    public final boolean isActivationCodePopupShown() {
        return getBoolean(ACTIVATION_POPUP, false);
    }

    public final void setActivationCodePopupShown(boolean value) {
        setBoolean(ACTIVATION_POPUP, value);
    }

    public final boolean getLanguageOption() {
        return getBoolean("language_option", false);
    }

    public final boolean getSettings() {
        return getBoolean("settings", false);
    }

    public final boolean getInputMode() {
        return getBoolean("inputmode", true);
    }

    public final boolean getQuickToggle() {
        return getBoolean("quicktoggle", true);
    }

    public final boolean getEditKeyboard() {
        return getBoolean("edit_layer", false);
    }

    public final boolean getNumberKeyboard() {
        return getBoolean("number_keyboard", false);
    }

    public final boolean getShowFunctionBar() {
        return getBoolean("function_bar_display", true);
    }

    public final boolean getThemes() {
        return getBoolean("themes", false);
    }

    public final boolean getAddOnDictionaries() {
        return getBoolean("addondictionaries", false);
    }

    public final boolean getChineseSettings() {
        return getBoolean("chinesesettings", true);
    }

    public final boolean getEmojiOnFunctionBar() {
        return getBoolean(EMOJI_ON_FUNCTION_BAR, true);
    }

    public final void setEmojiOnFunctionBar(boolean value) {
        setBoolean(EMOJI_ON_FUNCTION_BAR, value);
    }

    public final void setLanguageOption(boolean value) {
        setBoolean("language_option", value);
    }

    public final void setSettings(boolean value) {
        setBoolean("settings", value);
    }

    public final void setInputMode(boolean value) {
        setBoolean("inputmode", value);
    }

    public final void setQuickToggle(boolean value) {
        setBoolean("quicktoggle", value);
    }

    public final void setEditKeyboard(boolean value) {
        setBoolean("edit_layer", value);
    }

    public final void setNumberKeyboard(boolean value) {
        setBoolean("number_keyboard", value);
    }

    public final void setShowFunctionBar(boolean value) {
        setBoolean("function_bar_display", value);
    }

    public final void setThemes(boolean value) {
        setBoolean("themes", value);
    }

    public final void setAddOnDictionaries(boolean value) {
        setBoolean("addondictionaries", value);
    }

    public final void setChineseSettings(boolean value) {
        setBoolean("chinesesettings", value);
    }

    public final String getCurrentThemeId() {
        return getString(CURRENT_THEME_ID_KEY, null);
    }

    public final void setCurrentThemeId(String themeId) {
        setString(CURRENT_THEME_ID_KEY, themeId);
    }

    public final boolean getAskBeforeAdd() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return false;
        }
        if (IMEApplication.from(getContext()).getCurrentLanguage() != null) {
            return getBoolean(ASK_BEFORE_ADD, getDefaultBoolean(R.bool.ask_before_add_default)) && !IMEApplication.from(getContext()).getCurrentLanguage().isNonSpacedLanguage();
        }
        return getBoolean(ASK_BEFORE_ADD, getDefaultBoolean(R.bool.ask_before_add_default));
    }

    public final void setAskBeforeAdd(boolean value) {
        setBoolean(ASK_BEFORE_ADD, value);
    }

    public final boolean getTagForChildDirectedTreatment() {
        return getBoolean(AD_TAG_FOR_CHILD_DIRECTED_TREATMENT, true);
    }

    public final void setTagForChildDirectedTreatment(boolean value) {
        setBoolean(AD_TAG_FOR_CHILD_DIRECTED_TREATMENT, value);
    }

    public final boolean isHandwritingEnabled() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if ((ime != null && ime.isAccessibilitySupportEnabled()) || this.buildInfo.getHandwritingType() == BuildInfo.HandwritingType.NONE) {
            return false;
        }
        if (InputMethods.from(getContext()).getCurrentInputLanguage().isCJK()) {
            return ime == null || !ime.isDeviceExploreByTouchOn();
        }
        if (this.buildInfo.getHandwritingType() != BuildInfo.HandwritingType.CJK_ONLY) {
            return getBoolean(PREF_ENABLE_HANDWRITING, getDefaultBoolean(R.bool.enable_handwriting_default));
        }
        return false;
    }

    public final float getKeyboardScalePortrait() {
        return getFloat(KEYBOARD_SCALE_PORTRAIT, 1.0f);
    }

    public final void setKeyboardScalePortrait(float size) {
        setFloat(KEYBOARD_SCALE_PORTRAIT, size);
    }

    public final float getKeyboardScaleLandscape() {
        return getFloat(KEYBOARD_SCALE_LANDSCAPE, 1.0f);
    }

    public final void setKeyboardScaleLandscape(float size) {
        setFloat(KEYBOARD_SCALE_LANDSCAPE, size);
    }

    public final KeyboardEx.KeyboardDockMode getKeyboardDockingMode() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return KeyboardEx.KeyboardDockMode.DOCK_FULL;
        }
        int orientation = getContext().getResources().getConfiguration().orientation;
        return KeyboardEx.KeyboardDockMode.fromInt(getInt(KEYBOARD_DOCKING_MODE + orientation, KeyboardEx.KeyboardDockMode.DOCK_FULL.ordinal()));
    }

    public final void setKeyboardDockingMode(KeyboardEx.KeyboardDockMode mode) {
        int orientation = getContext().getResources().getConfiguration().orientation;
        setInt(KEYBOARD_DOCKING_MODE + orientation, mode.ordinal());
    }

    public final KeyboardEx.KeyboardDockMode getKeyboardDockingMode(int orientation) {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            return KeyboardEx.KeyboardDockMode.DOCK_FULL;
        }
        return KeyboardEx.KeyboardDockMode.fromInt(getInt(KEYBOARD_DOCKING_MODE + orientation, KeyboardEx.KeyboardDockMode.DOCK_FULL.ordinal()));
    }

    public final void setKeyboardDockingMode(KeyboardEx.KeyboardDockMode mode, int orientation) {
        setInt(KEYBOARD_DOCKING_MODE + orientation, mode.ordinal());
    }

    public final String getCurrentDictationLanguageName() {
        return getString(CURRENT_DICTATION_LANGUAGE, DEFAULT_AUTOMATIC_DICTATION_LANGUAGE);
    }

    public final void setCurrentDictationLanguageName(String langName) {
        setString(CURRENT_DICTATION_LANGUAGE, langName);
    }

    public final boolean isAutoReturnToEditorDefaultLayerEnabled() {
        return getBoolean(PREF_AUTO_DEFAULT_LAYER_RETURN, getDefaultBoolean(R.bool.auto_default_layer_return_default));
    }

    public final boolean isSwypingEnabled() {
        return getBoolean(PREF_SWYPING, getDefaultBoolean(R.bool.enable_swyping_default));
    }

    public final boolean isHardwareAccelerationEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.enable_hardware_acceleration);
    }

    public final boolean isHardwareKeyboardEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.enable_hardkeyboard);
    }

    public final boolean getNetworkAgreement() {
        return getBoolean(PREF_NETWORK_AGREEMENT, false);
    }

    public final void setNetworkAgreement(boolean value) {
        setBoolean(PREF_NETWORK_AGREEMENT, value);
    }

    public final boolean getNetworkAgreementNotAsk() {
        return getBoolean(PREF_NETWORK_AGREEMENT_NOT_ASK, false);
    }

    public final void setNetworkAgreementNotAsk(boolean value) {
        setBoolean(PREF_NETWORK_AGREEMENT_NOT_ASK, value);
    }

    public final boolean shouldShowNetworkAgreementDialog() {
        return (getNetworkAgreement() || getNetworkAgreementNotAsk()) ? false : true;
    }

    public final boolean getShowNetworkDialogFromKeyboard() {
        return getBoolean(PREF_SHOW_NETWORK_DIALOG_FROM_KEYBOARD, true);
    }

    public final void setShowNetworkDialogFromKeyboard(boolean value) {
        setBoolean(PREF_SHOW_NETWORK_DIALOG_FROM_KEYBOARD, value);
    }

    public final boolean getKeyboardFontBold() {
        return getBoolean(KEYBOARD_FONT_BOLD, getDefaultBoolean(R.bool.font_typeface_bold));
    }

    public final void setKeyboardFontBold(boolean bold) {
        setBoolean(KEYBOARD_FONT_BOLD, bold);
    }

    public final boolean isEmojiSuggestionsEnabled() {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime == null || !ime.isAccessibilitySupportEnabled()) {
            return getBoolean(PREF_EMOJI_SUGGESTIONS, getDefaultBoolean(R.bool.emoji_setting_default));
        }
        return false;
    }

    public final boolean isDrawerAutomaticallyOpenedOnce() {
        return getBoolean(PREF_DRAWER_AUTO_OPENED_ONCE, false);
    }

    public final void setDrawerAutomaticallyOpenedOnce(boolean value) {
        setBoolean(PREF_DRAWER_AUTO_OPENED_ONCE, value);
    }

    public final void setActiveDownloadedTheme(String themeSku) {
        setString(ACTIVE_DOWNLOADED_THEME_ID_KEY, themeSku);
    }

    public final String getActiveDownloadedTheme() {
        return getString(ACTIVE_DOWNLOADED_THEME_ID_KEY, "");
    }

    public final Set<String> getLastCheckedSkuList(Set<String> defaultValue) {
        return getStringSet(LAST_CHECKED_SKU_LIST, defaultValue);
    }

    public final void setLastCheckedSkuList(Set<String> skuSet) {
        setStringSet(LAST_CHECKED_SKU_LIST, skuSet);
    }

    public final int getAdStartSession() {
        String value = getString(PREF_AD_FIRST_TIME, "");
        return value.equals("") ? getContext().getResources().getInteger(R.integer.ad_config_show_first_on) : Integer.parseInt(value, 10);
    }

    public final int getAdSessionStepSize() {
        String value = getString(PREF_AD_STEP_SIZE, "");
        return value.equals("") ? getContext().getResources().getInteger(R.integer.ad_config_show_step_size) : Integer.parseInt(value, 10);
    }

    public final int getAdMaxPerDay() {
        String value = getString(PREF_AD_MAX_PER_DAY, "");
        return value.equals("") ? getContext().getResources().getInteger(R.integer.ad_config_max_ads_per_day) : Integer.parseInt(value, 10);
    }

    public final boolean isInstantGestureEnabled() {
        return getBoolean(PREF_ENABLE_INSTANT_GESTURE, getDefaultBoolean(R.bool.enable_instant_gesture_default));
    }

    public final boolean isDiffHwrTypeEnabled() {
        return getBoolean(PREF_ENABLE_DIFF_HWR_TYPE, getDefaultBoolean(R.bool.enable_diff_hwr_result_type_default));
    }

    public final boolean isHwrRotationEnabled() {
        return getBoolean(PREF_ENABLE_HWR_ROTATION, getDefaultBoolean(R.bool.enable_hwr_rotation_default));
    }

    public final void setHwrRotation(boolean value) {
        setBoolean(PREF_ENABLE_HWR_ROTATION, value);
    }

    public final boolean isAttachingCommonWordsLDBAllowed() {
        return getBoolean(PREF_ATTACH_COMMON_WORDS_LDB, getDefaultBoolean(R.bool.attach_common_words_ldb_default));
    }

    public final void setAttachCommonWordsLDB(boolean value) {
        setBoolean(PREF_ATTACH_COMMON_WORDS_LDB, value);
    }

    public final boolean isHwrAlternativeDirectionEnabled() {
        return getBoolean(PREF_HWR_ALTERNATIVE_DIRECTION, getDefaultBoolean(R.bool.enable_hwr_alternative_direction_default));
    }

    public final void setHwrAlternativeDirection(boolean value) {
        setBoolean(PREF_HWR_ALTERNATIVE_DIRECTION, value);
    }

    public final boolean isHwrScrmodeEnabled() {
        return getBoolean(PREF_ENABLE_HWR_SCR_MODE, getDefaultBoolean(R.bool.enable_hwr_scr_mode_default));
    }

    public final boolean isHwrUCRModeEnabled() {
        return getBoolean(PREF_ENABLE_HWR_SCR_MODE, getDefaultBoolean(R.bool.enable_hwr_ucr_mode_default));
    }

    public final boolean getKeyboardHideSecondaries() {
        return !getBoolean(PREF_HIDE_SECONDARIES, getDefaultBoolean(R.bool.font_typeface_bold));
    }

    public final void setKeyboardHideSecondaries(boolean hide) {
        setBoolean(PREF_HIDE_SECONDARIES, hide);
    }

    public final Emoji.SkinToneEnum getDefaultEmojiSkin() {
        return Emoji.SkinToneEnum.getSkinToneFromCode(getInt(PREF_EMOJI_SKIN_TONE, Emoji.SkinToneEnum.NORMAL.getSkinValue()));
    }

    public final void setDefaultEmojiSkin(Emoji.SkinToneEnum skinToneEnum) {
        setInt(PREF_EMOJI_SKIN_TONE, skinToneEnum.getSkinValue());
    }

    public final void setCachedEmojiSkinTone(String emoji_code, int skinToneValue) {
        setInt(emoji_code, skinToneValue);
    }

    public final int getCachedEmojiSkinTone(String emoji_code, int defaultValue) {
        return getInt(emoji_code, defaultValue);
    }
}
