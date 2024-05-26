package com.nuance.swype.input;

import android.content.Context;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class AppPreferences extends SwypePreferences {
    private static final String ACCOUNT_NOTIFICATION_PENDING = "account_notification_pending";
    private static final String ACCOUNT_NOTIFICATION_SHOW_AT = "account_notification_at";
    public static final String ALL_CATEGORYDBS = "all_categorydbs";
    public static final String APP_PREFS_FILE = "app_prefs";
    public static final String AVAILABLE_CATEGORYDBS = "available_categorydbs";
    public static final String AVAILABLE_LANGUAGES = "available_languages";
    private static final String BILINGUAL_TIP_SHOWN = "bilingual_tip";
    public static final String BUILTIN_LANGUAGES = "builtin_languages";
    public static final String CHINESE_CLOUD_ALL = "chinese_cloud_all";
    public static final String CHINESE_CLOUD_DISABLED = "chinese_cloud_diabled";
    private static final String CHINESE_CLOUD_NETWORK_SETTING = "chinese_cloud_network_setting";
    public static final String CHINESE_CLOUD_WIFI_ONLY = "chinese_cloud_wifi_only";
    public static final String CJK_FULL_SCREEN_ENABLED = "fullscreen.enabled.";
    public static final String CONNECT_ENABLED = "enable_connect";
    public static final String CONNECT_PERMITTED = "connect_permitted";
    private static final String CONNECT_UPDATE_AVAILABLE = "connect_update_available";
    private static final String CONNECT_UPDATE_FILE_PATH = "connect_update_file_path";
    private static final String CONNECT_UPDATE_NOTIFICATION_SENT = "connect_update_notification_sent";
    private static final String CUSTOM_WORDS_SYNCHRONIZATION_FORCE_RESYNC_KEY = "custom_words_synchronization_force_resync_key";
    private static final String CUSTOM_WORDS_SYNCHRONIZATION_SERVER_WORDS_COUNT_KEY = "custom_words_synchronization_server_words_count_key";
    public static final String DEFAULT_LANGUAGE = "default_language";
    public static final String DICTATION_LANGUAGE = "dictation_language";
    public static final String ENABLED_CATEGORYDBS = "enabled_categorydbs";
    private static final String ENABLE_CUSTOM_WORDS_SYNCHRONIZATION_KEY = "enable_custom_words_synchronization_key";
    public static final String LANGUAGE_MODEL_KEY = "language_model";
    public static final boolean LANGUAGE_MODEL_SETTING_DEFAULT = true;
    private static final String LAST_SMS_CALLLOG_SCRAPED_TIME_IN_MILLI_SECOND = "last_sms_calllog_scraped_time_millis";
    private static final String NEW_THEMES_ANIMATION_ALREADY_SHOW = "NEW_THEMES_ANIMATION_ARLEADY_SHOW";
    private static final String PASSWORD_TIP_SHOWN = "PASSWORD_TIP_SHOWN";
    public static final String PREF_CHINESE_SYMBOL_RECENT_LIST = "pref_chinese_symbol_recent_list";
    private static final String PREF_CONTACTS_PERIMSSION_PRE_NAG = "pref_contacts_permission_pre_nag";
    private static final String PREF_CURRENT_BUILD_SWIB = "pref_current_build_swib";
    private static final String PREF_EMOJI_RECENT_LIST = "pref_emoji_recent_list";
    private static final String PREF_EMOJI_STATE_LAST_CATEGORY_ITEM = "pref_emoji_state_last_category_item";
    private static final String PREF_EMOJI_STATE_LAST_CATEGORY_NAME = "pref_emoji_state_last_category_name";
    private static final String PREF_HANDWRITING_INPUT_AREA = "handwriting_input_area";
    public static final String PREF_HINDI_INPUT_MODE = "pref_hindi_input_mode";
    public static final String PREF_ON_INSTALL_FIRST_MESSAGE = "pref_on_install_first_message";
    private static final String PREF_SHOW_TIPS = "show_tips";
    private static final String PREF_THEMES_AVAILABLE_IN_STORE = "pref_themes_available_in_store";
    private static final String PREF_USER_VISITED_STORE = "pref_user_visited_store";
    private static final String RECENT_EMOJIS_ETHNIC_SUPPORTED_LIST = "pref_emoji_ethnic_supported_list";
    private static final String RECENT_LANGUAGE_TIP_SHOWN = "recent_language_tip";
    private static final String SHOW_EDIT_GESTURE_TIP = "SHOW_EDIT_GESTURE_TIP";
    private static final String SHOW_HOW_TO_USE_HWR = "show_how_to_use_hwr";
    private static final String STARTUP_SEQUENCE_ACCOUNT_EMAIL = "STARTUP_SEQUENCE_ACOUNT_EMAIL";
    private static final String STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_DISPLAY_NAME = "STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_DISPLAY_NAME";
    private static final String STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID = "STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID";
    public static final String SUPPORTED_LANGUAGES = "supported_languages";
    private static final String THIRD_PARTY_LICENSE_INVALID_MESSAGE_TIMES = "third_party_license_invalid_message_times";
    public static final String TOTAL_SWYPE_DISTANCE = "total_swype_distance";
    private static final String UPGRADE_CONNECT = "upgrade_connect";
    private static final String UPGRADE_SWIB = "upgrade_swib";
    private AccessibilityInfo accessibilityInfo;
    private boolean upgrading;

    public static AppPreferences from(Context context) {
        return IMEApplication.from(context).getAppPreferences();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AppPreferences(Context context, BuildInfo buildInfo) {
        super(context, context.getSharedPreferences(APP_PREFS_FILE, 0));
        this.accessibilityInfo = new AccessibilityInfo(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void moveFromPrevious(UserPreferences userPrefs) {
        if (this.upgrading || (!contains(UPGRADE_SWIB) && userPrefs.contains(UPGRADE_SWIB))) {
            Map<String, String> stringPrefs = new HashMap<>();
            stringPrefs.put(UPGRADE_SWIB, "");
            stringPrefs.put(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_DISPLAY_NAME, "");
            stringPrefs.put(STARTUP_SEQUENCE_ACCOUNT_EMAIL, "");
            stringPrefs.put(DEFAULT_LANGUAGE, "");
            stringPrefs.put(BUILTIN_LANGUAGES, "");
            stringPrefs.put(AVAILABLE_LANGUAGES, "");
            stringPrefs.put(SUPPORTED_LANGUAGES, "");
            for (String key : stringPrefs.keySet()) {
                if (!contains(key)) {
                    setString(key, userPrefs.getUpgradedString(key, stringPrefs.get(key), "%x"));
                }
                userPrefs.remove(key);
            }
            Map<String, Boolean> booleanPrefs = new HashMap<>();
            booleanPrefs.put(PASSWORD_TIP_SHOWN, false);
            booleanPrefs.put(SHOW_EDIT_GESTURE_TIP, false);
            for (String key2 : booleanPrefs.keySet()) {
                if (!contains(key2)) {
                    setBoolean(key2, userPrefs.getBoolean(key2, booleanPrefs.get(key2).booleanValue()));
                }
                userPrefs.remove(key2);
            }
            if (!contains(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID)) {
                setString(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID, Integer.toHexString(userPrefs.getInt(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID, 0)));
            }
            userPrefs.remove(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID);
            if (!contains(InputMethods.SETTING_CURRENT_LANGUAGE)) {
                setString(InputMethods.SETTING_CURRENT_LANGUAGE, Integer.toHexString(userPrefs.getInt(InputMethods.SETTING_CURRENT_LANGUAGE, 0)));
            }
            userPrefs.remove(InputMethods.SETTING_CURRENT_LANGUAGE);
        }
    }

    public final boolean getMultitapMode(boolean defaultValue) {
        return getBoolean(InputMethods.MULTITAP_INPUT_MODE, defaultValue);
    }

    public final void setMultitapMode(boolean on) {
        setBoolean(InputMethods.MULTITAP_INPUT_MODE, on);
    }

    public final boolean isChinesePersonalDictionaryEnabled() {
        return getDefaultBoolean(R.bool.chinese_personal_dictionary_default);
    }

    public final boolean isKoreanPersonalDictionaryEnabled() {
        return getDefaultBoolean(R.bool.korean_personal_dictionary_default);
    }

    public final boolean isJapanesePersonalDictionaryEnabled() {
        return getDefaultBoolean(R.bool.japanese_personal_dictionary_default);
    }

    public final boolean isShowAskBeforeAddEnabled() {
        return getDefaultBoolean(R.bool.show_ask_before_add);
    }

    public final boolean getCustomWordsSynchronizationAvailability() {
        return getBoolean(ENABLE_CUSTOM_WORDS_SYNCHRONIZATION_KEY, true);
    }

    public final void enableCustomWordsSynchronization() {
        setBoolean(ENABLE_CUSTOM_WORDS_SYNCHRONIZATION_KEY, true);
    }

    public final void disableCustomWordsSynchronization() {
        setBoolean(ENABLE_CUSTOM_WORDS_SYNCHRONIZATION_KEY, false);
    }

    public final boolean getCustomWordsSynchronizationForceResync() {
        return getBoolean(CUSTOM_WORDS_SYNCHRONIZATION_FORCE_RESYNC_KEY, false);
    }

    public final void setCustomWordsSynchronizationForceResync(boolean forceResync) {
        setBoolean(CUSTOM_WORDS_SYNCHRONIZATION_FORCE_RESYNC_KEY, forceResync);
    }

    public final int getCustomWordsSynchronizationServerWordsCount() {
        return getInt(CUSTOM_WORDS_SYNCHRONIZATION_SERVER_WORDS_COUNT_KEY, -1);
    }

    public final void setCustomWordsSynchronizationServerWordsCount(int count) {
        setInt(CUSTOM_WORDS_SYNCHRONIZATION_SERVER_WORDS_COUNT_KEY, count);
    }

    public final boolean showHowToUseHandWritingTip() {
        return getBoolean(SHOW_HOW_TO_USE_HWR, true);
    }

    public final void setShowHowToUseHandWritingTip(boolean show) {
        setBoolean(SHOW_HOW_TO_USE_HWR, show);
    }

    public final boolean showSwypeWelcomeStartupScreens() {
        return getDefaultBoolean(R.bool.show_startup_tip);
    }

    public final boolean showStartupRegistration() {
        return getDefaultBoolean(R.bool.show_registration_startup);
    }

    public final boolean isBackupAndSyncSupported() {
        return getDefaultBoolean(R.bool.enable_backup_and_sync);
    }

    public final boolean isSetVibrationDurationAllowed() {
        return getDefaultBoolean(R.bool.allow_set_vibration_duration);
    }

    public final void setStartupSequenceAccountEmail(String value) {
        setString(STARTUP_SEQUENCE_ACCOUNT_EMAIL, value);
    }

    public final String getStartupSequenceAccountEmail() {
        return getString(STARTUP_SEQUENCE_ACCOUNT_EMAIL, "");
    }

    public final void setStartupSequenceDownloadLanguageDisplayName(String value) {
        setString(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_DISPLAY_NAME, value);
    }

    public final String getStartupSequenceDownloadLanguageDisplayName() {
        return getString(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_DISPLAY_NAME, "");
    }

    public final void setStartupSequenceDownloadLanguageID(String value) {
        setString(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID, value);
    }

    public final String getStartupSequenceDownloadLanguageID() {
        return getString(STARTUP_SEQUENCE_DOWNLOAD_LANGUAGE_ID, "");
    }

    public final boolean isPasswordTipAlreadyShown() {
        return getBoolean(PASSWORD_TIP_SHOWN, false);
    }

    public final void setPassWordTipShown() {
        setBoolean(PASSWORD_TIP_SHOWN, true);
    }

    public final boolean showToolTip() {
        return getBoolean(PREF_SHOW_TIPS, getDefaultBoolean(R.bool.show_tips_default));
    }

    public final boolean isShowEditGestureTip() {
        return getBoolean(SHOW_EDIT_GESTURE_TIP, true);
    }

    public final void enableShowEditGestureTip(boolean value) {
        setBoolean(SHOW_EDIT_GESTURE_TIP, value);
    }

    public final boolean isPersonalizationEnable() {
        return getDefaultBoolean(R.bool.enable_sns_persionalization);
    }

    public final boolean isUpgrade() {
        String oldSwib = getString(UPGRADE_SWIB, "");
        if (oldSwib.length() == 0) {
            return this.upgrading;
        }
        return !oldSwib.equals(BuildInfo.from(getContext()).getSwib()) || this.upgrading;
    }

    public final void ackUpgrade() {
        setString(UPGRADE_SWIB, BuildInfo.from(getContext()).getSwib());
        this.upgrading = false;
    }

    public final long getLastSmsCalllogScrapedTimeInMilliSecond(String langName) {
        return getLong(LAST_SMS_CALLLOG_SCRAPED_TIME_IN_MILLI_SECOND + langName, 0L);
    }

    public final void setLastSmsCalllogScrapedTimeMilliSecond(String langName, long time) {
        setLong(LAST_SMS_CALLLOG_SCRAPED_TIME_IN_MILLI_SECOND + langName, time);
    }

    public final AccessibilityInfo getAccessibilityInfo() {
        return this.accessibilityInfo;
    }

    public final int getHandwritingInputArea(int defaultValue) {
        return getInt(PREF_HANDWRITING_INPUT_AREA, defaultValue);
    }

    public final void setHandwritingInputArea(int val) {
        setInt(PREF_HANDWRITING_INPUT_AREA, val);
    }

    public final boolean getDefaultFullscreenHandwriting() {
        return getDefaultBoolean(R.bool.fullscreen_cjk_hwr_default);
    }

    public final boolean isBilingualTipAlreadyShown() {
        return getBoolean(BILINGUAL_TIP_SHOWN, false);
    }

    public final void setBilingualTipShown() {
        setBoolean(BILINGUAL_TIP_SHOWN, true);
    }

    public final boolean isRecentLanguageTipAlreadyShown() {
        return getBoolean(RECENT_LANGUAGE_TIP_SHOWN, false);
    }

    public final void setRecentLanguageTipShown() {
        setBoolean(RECENT_LANGUAGE_TIP_SHOWN, true);
    }

    public final void setshowToolTip(boolean val) {
        setBoolean(PREF_SHOW_TIPS, val);
    }

    public final void resetAccessibilityInfo(Context context) {
        this.accessibilityInfo = new AccessibilityInfo(context);
    }

    public final int getThirdPartyLicenseMessageTimes() {
        return getInt(THIRD_PARTY_LICENSE_INVALID_MESSAGE_TIMES, 0);
    }

    public final void setThirdPartyLicenseMessageTimes(int value) {
        setInt(THIRD_PARTY_LICENSE_INVALID_MESSAGE_TIMES, value);
    }

    public final boolean getAccountNotificationPending() {
        return getBoolean(ACCOUNT_NOTIFICATION_PENDING, true);
    }

    public final void setAccountNotificationPending(boolean value) {
        setBoolean(ACCOUNT_NOTIFICATION_PENDING, value);
    }

    public final long getAccountNotificationShowAt() {
        return getLong(ACCOUNT_NOTIFICATION_SHOW_AT, Long.MIN_VALUE);
    }

    public final void setAccountNotificationShowAt(long value) {
        setLong(ACCOUNT_NOTIFICATION_SHOW_AT, value);
    }

    public final void setUpgradeConnect(boolean upgrade) {
        setBoolean(UPGRADE_CONNECT, upgrade);
    }

    public final boolean getUpgradeConnect() {
        return getBoolean(UPGRADE_CONNECT, false);
    }

    public final void setUpdateFilePath(String path) {
        setString(CONNECT_UPDATE_FILE_PATH, path);
    }

    public final String getUpdateFilePath() {
        return getString(CONNECT_UPDATE_FILE_PATH, null);
    }

    public final void setUpdateAvailable(boolean available) {
        setBoolean(CONNECT_UPDATE_AVAILABLE, available);
    }

    public final boolean getUpdateAvailable() {
        return getBoolean(CONNECT_UPDATE_AVAILABLE, false);
    }

    public final void setUpdateNotificationSent(boolean available) {
        setBoolean(CONNECT_UPDATE_NOTIFICATION_SENT, available);
    }

    public final boolean getUpdateNotificationSent() {
        return getBoolean(CONNECT_UPDATE_NOTIFICATION_SENT, false);
    }

    public final void setChineseCloudNetworkOption(String opt) {
        setString(CHINESE_CLOUD_NETWORK_SETTING, opt);
    }

    public final String getChineseCloudNetworkOption() {
        return getString(CHINESE_CLOUD_NETWORK_SETTING, "");
    }

    public final void setEmojiRecentList(String recentList) {
        setString(PREF_EMOJI_RECENT_LIST, recentList);
    }

    public final String getEmojiRecentList() {
        return getString(PREF_EMOJI_RECENT_LIST, "");
    }

    public final void setChineseSymbolRecentList(String recentList) {
        setString(PREF_CHINESE_SYMBOL_RECENT_LIST, recentList);
    }

    public final String getChineseSymbolRecentList() {
        return getString(PREF_CHINESE_SYMBOL_RECENT_LIST, "");
    }

    public final void setLastUsedEmojiCategoryName(String categoryName) {
        setString(PREF_EMOJI_STATE_LAST_CATEGORY_NAME, categoryName);
    }

    public final void setLastUsedEmojiCategoryItem(int pageNumber) {
        setInt(PREF_EMOJI_STATE_LAST_CATEGORY_ITEM, pageNumber);
    }

    public final String getLastUsedEmojiCategoryName() {
        return getString(PREF_EMOJI_STATE_LAST_CATEGORY_NAME, "");
    }

    public final int getLastUsedEmojiCategoryItem() {
        return getInt(PREF_EMOJI_STATE_LAST_CATEGORY_ITEM, 0);
    }

    public final void setCurrentSWIB(String swib) {
        setString(PREF_CURRENT_BUILD_SWIB, swib);
    }

    public final String getCurrentSWIB() {
        return getString(PREF_CURRENT_BUILD_SWIB, "");
    }

    public final boolean isNewThemesAnimationAlreadyShown() {
        return getBoolean(NEW_THEMES_ANIMATION_ALREADY_SHOW, false);
    }

    public final void setNewThemesAnimationAlreadyShow() {
        setBoolean(NEW_THEMES_ANIMATION_ALREADY_SHOW, true);
    }

    public final boolean isContactsPermissionPreNagShown() {
        return getBoolean(PREF_CONTACTS_PERIMSSION_PRE_NAG, false);
    }

    public final void setContactsPermissionPreNagShown() {
        setBoolean(PREF_CONTACTS_PERIMSSION_PRE_NAG, true);
    }

    public final void setOnInstallFirstMessage(boolean value) {
        setBoolean(PREF_ON_INSTALL_FIRST_MESSAGE, value);
    }

    public final boolean getOnInstallFirstMessage() {
        return getBoolean(PREF_ON_INSTALL_FIRST_MESSAGE, true);
    }

    public final boolean isNewThemeAvailableInStore() {
        return getBoolean(PREF_THEMES_AVAILABLE_IN_STORE, false);
    }

    public final void setNewThemeAvailableInStore(boolean value) {
        setBoolean(PREF_THEMES_AVAILABLE_IN_STORE, value);
    }

    public final boolean isUserVisitedStore() {
        return getBoolean(PREF_USER_VISITED_STORE, false);
    }

    public final void setUserVisitedStore(boolean value) {
        setBoolean(PREF_USER_VISITED_STORE, value);
    }

    public final boolean isLoadExternalLDBEnabled() {
        return getDefaultBoolean(R.bool.enable_external_ldb_path);
    }

    public final void setEmojiRecentCategoryList(String recentList) {
        setString(RECENT_EMOJIS_ETHNIC_SUPPORTED_LIST, recentList);
    }

    public final String getEmojiCategoryaRecentList() {
        return getString(RECENT_EMOJIS_ETHNIC_SUPPORTED_LIST, "");
    }
}
