package com.nuance.swype.usagedata;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.ServerProtocol;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.connect.util.TimeConversion;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.ads.AdProvider;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class UsageData {
    private static Screen activeScreen;
    private static long browseStartTimeThemes;
    private static Context mContext;
    private static boolean mIsAllowed;
    private static final LogManager.Log log = LogManager.getLog("UsageData");
    private static Analytics mAnalyticsImpl = new AnalyticsNull();
    private static Map<String, Permission> sPermissionMap = new HashMap();

    /* loaded from: classes.dex */
    public interface AnalyticsSessionManagementStrategy {
        boolean canClose();

        boolean canOpen();

        boolean isOpen();

        void markSessionClosed();

        void markSessionOpened();
    }

    /* loaded from: classes.dex */
    public enum Screen {
        BUNDLE_PREVIEW("Store Bundle Preview"),
        BUNDLE_UPSELL("Store Bundle Upsell"),
        CATEGORY_PAGE_PREVIEW("Store Category Page Preview"),
        CHINESE_PREFERENCES("Swype Chinese"),
        FUNCTION_BAR("Function Bar"),
        GET_THEMES("Store Get Themes"),
        GET_THEMES_PREVIEW("Store Get Themes Preview"),
        HELP("Help"),
        LANGUAGES("Languages"),
        MY_THEMES("My Themes"),
        MY_THEMES_PREVIEW("My Themes Preview"),
        MY_WORDS("My Words"),
        PREFERENCES("Preferences"),
        SETTINGS_DRAWER("Settings Drawer"),
        SWYPE_KEY("Swype Key"),
        SYSTEM_NOTIFICATION("System Notification"),
        THEMES_OPTIONS("Themes Options"),
        GESTURES("Gestures"),
        DOWNLOAD_LANGUAGE("Download Languages");

        private final String screen;

        Screen(String s) {
            this.screen = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.screen;
        }
    }

    /* loaded from: classes.dex */
    public enum Event {
        BUNDLE_PREVIEW("Store Bundle Preview"),
        SCANNING_SETTINGS("Settings Scanning"),
        STORE_THEME_CHANGED("Store Theme Changed"),
        THEME_PREVIEW_TRIAL_CONVERSION("Trial Conversion");

        private final String event;

        Event(String s) {
            this.event = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.event;
        }
    }

    /* loaded from: classes.dex */
    public enum DLMRequestType {
        DLM_CONTENT_RESET("ET9AW_DLM_Request_ContentReset"),
        DLM_WORD_DISCARDED("ET9AW_DLM_Request_WordDiscarded");

        private final String requestType;

        DLMRequestType(String s) {
            this.requestType = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.requestType;
        }
    }

    /* loaded from: classes.dex */
    public enum DownloadLocation {
        MY_THEMES("My Themes"),
        GET_THEMES("Get Themes"),
        BUNDLE("Bundle Page");

        private final String result;

        DownloadLocation(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum DownloadResult {
        AC_EXCEPTION("ACException"),
        CANCELED("Canceled"),
        COMPLETED(AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_COMPLETED),
        NETWORK("Network Failure"),
        NO_SPACE("No Space");

        private final String result;

        DownloadResult(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum PaymentProvider {
        GOOGLE_PLAY;

        private final String result;

        PaymentProvider() {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum Permission {
        READ_CALL_LOG_CALL_LOGS("READ_CALL_LOG (Call Logs)"),
        READ_CONTACTS_CALL_LOGS("READ_CONTACTS (Call Logs)"),
        READ_SMS_CALL_LOGS("READ_SMS (Call Logs)"),
        RECORD_AUDIO_KB_VOICE_KEY("RECORD_AUDIO (Keyboard Voice Key)"),
        RECORD_AUDIO_SHOW_VOICE_KEY("RECORD_AUDIO (Show Voice Key)");

        private final String action;

        Permission(String s) {
            this.action = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.action;
        }
    }

    /* loaded from: classes.dex */
    public enum PermissionAttribute {
        USER_ACTION;

        private final String mAttrName;

        PermissionAttribute() {
            this.mAttrName = attrName;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.mAttrName;
        }
    }

    /* loaded from: classes.dex */
    public enum PermissionUserAction {
        ALLOWED("Allowed"),
        DENIED("Denied"),
        NEVER_SHOW_AGAIN("Never Show Again"),
        BLOCKED("Blocked (NSA Previously Selected");

        private final String result;

        PermissionUserAction(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum EmojiSelectedAttribute {
        SOURCE;

        private final String mAttrName;

        EmojiSelectedAttribute() {
            this.mAttrName = attrName;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.mAttrName;
        }
    }

    /* loaded from: classes.dex */
    public enum EmojiSelectedSource {
        EMOJEENIE("emojeenie"),
        SHORTCUT("shortcut"),
        NWP("nwp"),
        PICKER("picker"),
        RECENT("recent");

        private final String mSource;

        EmojiSelectedSource(String source) {
            this.mSource = source;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.mSource;
        }
    }

    /* loaded from: classes.dex */
    public enum PurchaseType {
        BUNDLE("Bundle"),
        THEME("Theme");

        private final String result;

        PurchaseType(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum ThemeUpsellUserAction {
        CANCEL(SessionEventImpl.NMSP_CALLLOG_META_CANCEL),
        GO_TO_BUNDLE("Go To Bundle"),
        BUY_BUNDLE("Buy Bundle"),
        BUY_THEME("Buy Theme");

        private final String result;

        ThemeUpsellUserAction(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    /* loaded from: classes.dex */
    public enum BackupAndSyncStatus {
        UNREGISTERED("Not Registered"),
        REGISTERED("Registered"),
        ON("On"),
        OFF("Off");

        private final String status;

        BackupAndSyncStatus(String s) {
            this.status = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.status;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum DictionaryBehavior {
        EXPLICIT("Explicit"),
        IMPLICIT("Implicit");

        private final String behavior;

        DictionaryBehavior(String s) {
            this.behavior = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.behavior;
        }
    }

    /* loaded from: classes.dex */
    public enum EventTag {
        BUNDLE_UPSELL("Store Bundle Upsell"),
        CATEGORY_PAGE_PREVIEW("Store Category Page Preview (View All)"),
        DOWNLOAD("Store Download"),
        STORE_GET_THEMES_VISIT("Store Get Themes Page Visit"),
        STORE_MY_THEMES_VISIT("Store My Themes Page Visit"),
        STORE_THEMES_PREVIEW("Store Themes Preview"),
        STORE_TRANSACTION_COMPLETED("Store Transaction Completed"),
        STORE_TRANSACTION_FAILED("Store Transaction Failed"),
        VOICE_USAGE_DRAGON("Voice Usage (Dragon)"),
        DLM_WIPE("DLM Wipe"),
        BLUETOOTH_KB_USAGE("Bluetooth Keyboard Usage"),
        SETTINGS_SUMMARY("Settings Summary"),
        MY_WORDS("My Words"),
        KEYBOARD_OPEN_X10("Keyboard Open (x10)"),
        AD_LOADED("Ad Loaded"),
        AD_FAILURE("Ad Failure"),
        AD_BILLBOARD_CLOSE("Ad Billboard Close"),
        USED_SMS_SCANNER("Used SMS Scanner"),
        USED_GMAIL_SCANNER("Used Gmail Scanner"),
        USED_CALL_LOG_SCANNER("Used Call Log Scanner"),
        EMOJI_SELECTED("Emoji Selected"),
        LANGUAGE_DOWNLOAD_REQUEST("Language Download Request");

        private final String event;

        EventTag(String s) {
            this.event = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.event;
        }
    }

    /* loaded from: classes.dex */
    public enum EventAttribute {
        BROWSE_TIME("Browse Time Per Page"),
        CATEGORY("Category"),
        ERROR_CODE("Error Code"),
        ERROR_DESCRIPTION("Error Description"),
        EVENT_ERR("Event Error"),
        LANGUAGE("Language"),
        LOCATION("Location"),
        NAME(SessionEventImpl.NMSP_CALLLOG_META_NAME),
        PAYMENT_PROVIDER("Payment Provider"),
        POSITION("Position"),
        PRICE("Price"),
        RESULT("Result"),
        TAB_PREVIEWED_FROM("Tab Previewed From"),
        THEME_NAME("Theme Name"),
        TYPE("Type"),
        USER_ACTION("User Action"),
        DLM_REQUEST_TYPE("DLM Request Type"),
        BACKUP_SYNC_STATUS("Backup Sync Status"),
        DLM_DISCARD_REASON("DLM Discard Reason"),
        DLM_DISCARD_WORD("DLM Discard Word"),
        LOAD_TIME("Load Time"),
        NUMBER_ROW("Number Row"),
        SECONDARY_CHARACTERS("Secondary Characters"),
        CELLULAR_DATA("Cellular Data"),
        LIVING_LANGUAGE("Living Language"),
        DICTIONARY_BEHAVIOR("Dictionary Behavior"),
        UDB_WORD_COUNT("UDB Word Count"),
        DOWNLOAD_LANGUAGE_NAME("Language");

        private final String attribute;

        EventAttribute(String s) {
            this.attribute = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.attribute;
        }
    }

    /* loaded from: classes.dex */
    public enum AdResult {
        SUCCESS("Success"),
        FAILED_NO_FILL("No Fill"),
        FAILED_INVALID_REQUEST("Invalid Request"),
        FAILED_INTERNAL_ERROR("Internal Error"),
        FAILED_NETWORK_ERROR("Network Error"),
        FAILED_UNKNOWN("Unknown Error"),
        FAILED_UNMAPPED("Unmapped Error");

        private final String result;

        AdResult(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    public static void init(Context context) {
        log.d("init: " + context);
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null!");
        }
        mContext = context;
        sPermissionMap.put("android.permission.READ_CONTACTS", Permission.READ_CONTACTS_CALL_LOGS);
        sPermissionMap.put("android.permission.READ_SMS", Permission.READ_SMS_CALL_LOGS);
        sPermissionMap.put(PermissionUtils.READ_CALL_LOG, Permission.READ_CALL_LOG_CALL_LOGS);
        init();
    }

    private static void init() {
        Analytics analyticsNull;
        log.d("init: mContext: " + mContext);
        boolean isAllowed = UserPreferences.from(mContext).isDataUsageOptAccepted();
        log.d("isContributionAllowed: " + isAllowed);
        if (mIsAllowed != isAllowed) {
            mIsAllowed = isAllowed;
            if (!isAllowed) {
                mAnalyticsImpl = new AnalyticsNull();
                return;
            }
            switch (BuildInfo.from(mContext).getUsageDataProvider()) {
                case -1:
                    log.d("init: creating AnalyticsNull");
                    analyticsNull = new AnalyticsNull();
                    break;
                case 0:
                    log.d("init: creating AnalyticsFlurry");
                    log.d("init: context: " + mContext + ", key: " + BuildInfo.from(mContext).getUsageDataFlurryKey());
                    analyticsNull = new AnalyticsFlurry(mContext, BuildInfo.from(mContext).getUsageDataFlurryKey());
                    break;
                case 1:
                    log.d("init: creating AnalyticsFirebase");
                    analyticsNull = new AnalyticsFirebase();
                    break;
                case 2:
                    if (AnalyticsLocalytics.isLocalyticsEnabledForThisDevice(mContext)) {
                        log.d("init: creating AnalyticsLocalytics");
                        analyticsNull = new AnalyticsLocalytics(mContext, new TimeBasedAnalyticsSessionManager((byte) 0));
                        break;
                    } else {
                        analyticsNull = new AnalyticsNull();
                        break;
                    }
                default:
                    analyticsNull = new AnalyticsNull();
                    break;
            }
            mAnalyticsImpl = analyticsNull;
        }
    }

    public static void startSession(Context context) {
        log.d("startSession: " + context);
        init();
        mAnalyticsImpl.startSession(context);
    }

    public static void endSession(Context context) {
        log.d("endSession: " + context);
        mAnalyticsImpl.endSession(context);
    }

    public static void recordMyWordsSettings(BackupAndSyncStatus status, boolean livingLanguageEnabled, boolean askBeforeAdd) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EventAttribute.BACKUP_SYNC_STATUS.toString(), status.toString());
        attributes.put(EventAttribute.LIVING_LANGUAGE.toString(), isEnabled(livingLanguageEnabled));
        attributes.put(EventAttribute.DICTIONARY_BEHAVIOR.toString(), askBeforeAdd ? DictionaryBehavior.EXPLICIT.toString() : DictionaryBehavior.IMPLICIT.toString());
        mAnalyticsImpl.tagEvent(EventTag.MY_WORDS.toString(), attributes);
    }

    public static void recordEvent(Event event, Map<String, String> attributes) {
        recordEvent(event.toString(), attributes);
    }

    public static void recordEvent(String event, Map<String, String> attributes) {
        mAnalyticsImpl.tagEvent(event, attributes);
    }

    public static void recordScreenVisited(Screen newScreen) {
        log.d("recordScreenVisited: " + newScreen);
        if (newScreen == null) {
            log.d("recordScreenVisited: newScreen null! ignoring");
        } else {
            recordThemesUsageData(newScreen);
            mAnalyticsImpl.tagScreen(newScreen.toString());
        }
    }

    public static void recordCategoryBrowse(String categoryName, long browseTime) {
        Map<String, String> attributes = new HashMap<>();
        if (categoryName != null) {
            attributes.put(EventAttribute.NAME.toString(), categoryName.toString());
        } else {
            eventError("NAME not provided!", attributes);
        }
        attributes.put(EventAttribute.BROWSE_TIME.toString(), getBrowseTime(browseTime));
        mAnalyticsImpl.tagEvent(EventTag.CATEGORY_PAGE_PREVIEW.toString(), attributes);
    }

    public static void recordDownloadEvent(String themeName, DownloadLocation location, DownloadResult result) {
        Map<String, String> attributes = new HashMap<>();
        if (themeName != null) {
            attributes.put(EventAttribute.NAME.toString(), themeName.toString());
        } else {
            eventError("NAME not provided!", attributes);
        }
        if (location != null) {
            attributes.put(EventAttribute.LOCATION.toString(), location.toString());
        } else {
            eventError("LOCATION not provided!", attributes);
        }
        if (result != null) {
            attributes.put(EventAttribute.RESULT.toString(), result.toString());
        } else {
            eventError("RESULT not provided!", attributes);
        }
        mAnalyticsImpl.tagEvent(EventTag.DOWNLOAD.toString(), attributes);
    }

    public static void recordPermissionRequest(Permission permission, PermissionUserAction userAction) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(PermissionAttribute.USER_ACTION.toString(), userAction.toString());
        mAnalyticsImpl.tagEvent(permission.toString(), attributes);
    }

    public static void recordPermissionGrantResult(String permission, int grantResult, Activity activity) {
        PermissionUserAction userAction;
        log.d("recordPermissionGrantResult: " + permission + ", result: " + grantResult);
        if (grantResult == 0) {
            userAction = PermissionUserAction.ALLOWED;
        } else {
            userAction = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) ? false : true ? PermissionUserAction.NEVER_SHOW_AGAIN : PermissionUserAction.DENIED;
        }
        recordPermissionRequest(sPermissionMap.get(permission), userAction);
    }

    public static void recordStoreTransactionComplete(String themeSku, String price, PurchaseType type, PaymentProvider pp) {
        Map<String, String> attributes = new HashMap<>();
        if (themeSku != null) {
            attributes.put(EventAttribute.NAME.toString(), themeSku);
        } else {
            eventError("NAME not provided!", attributes);
        }
        if (price != null) {
            attributes.put(EventAttribute.PRICE.toString(), price);
        } else {
            eventError("PRICE not provided!", attributes);
        }
        if (type != null) {
            attributes.put(EventAttribute.TYPE.toString(), type.toString());
        } else {
            eventError("TYPE not provided!", attributes);
        }
        if (pp != null) {
            attributes.put(EventAttribute.PAYMENT_PROVIDER.toString(), pp.toString());
        } else {
            eventError("PAYMENT_PROVIDER not provided!", attributes);
        }
        mAnalyticsImpl.tagEvent(EventTag.STORE_TRANSACTION_COMPLETED.toString(), attributes);
    }

    public static void recordStoreTransactionFailed(String themeSku, PaymentProvider pp, String errorDesc, int errorCode) {
        Map<String, String> attributes = new HashMap<>();
        if (themeSku != null) {
            attributes.put(EventAttribute.NAME.toString(), themeSku);
        } else {
            eventError("NAME not provided!", attributes);
        }
        if (pp != null) {
            attributes.put(EventAttribute.PAYMENT_PROVIDER.toString(), pp.toString());
        } else {
            eventError("PAYMENT_PROVIDER not provided!", attributes);
        }
        if (errorDesc != null) {
            attributes.put(EventAttribute.ERROR_DESCRIPTION.toString(), errorDesc.toString());
        } else {
            eventError("ERROR_DESCRIPTION not provided!", attributes);
        }
        attributes.put(EventAttribute.ERROR_CODE.toString(), Integer.toString(errorCode));
        mAnalyticsImpl.tagEvent(EventTag.STORE_TRANSACTION_FAILED.toString(), attributes);
    }

    public static void recordVoiceUsageEvent(String language) {
        Map<String, String> attributes = new HashMap<>();
        if (language != null) {
            attributes.put(EventAttribute.LANGUAGE.toString(), language);
        } else {
            eventError("LANGUAGE not provided!", attributes);
        }
        mAnalyticsImpl.tagEvent(EventTag.VOICE_USAGE_DRAGON.toString(), attributes);
    }

    public static void recordKeyboardOpen(Context context) {
        int keyboardOpens = UserPreferences.from(context).getInt("usagedata_keyboard_opens", 0) + 1;
        log.d("recordKeyboardOpen: keyboardOpens:" + keyboardOpens);
        if (keyboardOpens >= 10) {
            keyboardOpens = 0;
            mAnalyticsImpl.tagEvent(EventTag.KEYBOARD_OPEN_X10.toString(), null);
        }
        UserPreferences.from(context).setInt("usagedata_keyboard_opens", keyboardOpens);
    }

    public static void recordAdResult$7e29e9d3(AdProvider.AD_LOAD_STATUS adLoadStatus, long loadTimeMs) {
        AdResult adResult;
        EventTag eventTag;
        String format;
        Map<String, String> attributes = new HashMap<>();
        switch (adLoadStatus) {
            case SUCCESS:
                adResult = AdResult.SUCCESS;
                break;
            case FAILED_NO_FILL:
                adResult = AdResult.FAILED_NO_FILL;
                break;
            case FAILED_INVALID_REQUEST:
                adResult = AdResult.FAILED_INVALID_REQUEST;
                break;
            case FAILED_INTERNAL_ERROR:
                adResult = AdResult.FAILED_INTERNAL_ERROR;
                break;
            case FAILED_NETWORK_ERROR:
                adResult = AdResult.FAILED_NETWORK_ERROR;
                break;
            case FAILED_UNKNOWN:
                adResult = AdResult.FAILED_UNKNOWN;
                break;
            default:
                log.e("Unknown AD_LOAD_STATUS: " + adLoadStatus);
                adResult = AdResult.FAILED_UNMAPPED;
                break;
        }
        if (adResult == AdResult.SUCCESS) {
            eventTag = EventTag.AD_LOADED;
            String eventAttribute = EventAttribute.LOAD_TIME.toString();
            double d = loadTimeMs / 500.0d;
            if (d >= 20.0d) {
                format = "10s+";
            } else {
                double floor = Math.floor(d);
                double ceil = Math.ceil(d);
                if (floor == ceil) {
                    ceil += 1.0d;
                }
                format = String.format("%1.1f-%1.1fs", Double.valueOf(floor / 2.0d), Double.valueOf(ceil / 2.0d));
            }
            attributes.put(eventAttribute, format);
        } else {
            eventTag = EventTag.AD_FAILURE;
            attributes.put(EventAttribute.TYPE.toString(), adResult.toString());
        }
        mAnalyticsImpl.tagEvent(eventTag.toString(), attributes);
    }

    public static void exitedSettings() {
        recordThemesUsageData(null);
    }

    public static Screen getActiveScreen() {
        return activeScreen;
    }

    public static void recordThemeUpsell(ThemeUpsellUserAction userAction, String themeName, String from, String category) {
        Map<String, String> attributes = new HashMap<>();
        if (userAction != null) {
            attributes.put(EventAttribute.USER_ACTION.toString(), userAction.toString());
        } else {
            eventError("USER_ACTION not provided!", attributes);
        }
        if (themeName != null) {
            attributes.put(EventAttribute.THEME_NAME.toString(), themeName);
        } else {
            eventError("THEME_NAME not provided!", attributes);
        }
        if (from != null) {
            attributes.put(EventAttribute.TAB_PREVIEWED_FROM.toString(), from);
        } else {
            eventError("TAB_PREVIEWED_FROM not provided!", attributes);
        }
        if (category != null) {
            attributes.put(EventAttribute.CATEGORY.toString(), category);
        } else {
            eventError("CATEGORY not provided!", attributes);
        }
        mAnalyticsImpl.tagEvent(EventTag.BUNDLE_UPSELL.toString(), attributes);
    }

    public static void recordSettingsSummary(boolean numberRowEnabled, boolean showSecondaries, boolean celluarDataEnabled) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EventAttribute.NUMBER_ROW.toString(), isEnabled(numberRowEnabled));
        attributes.put(EventAttribute.SECONDARY_CHARACTERS.toString(), isEnabled(showSecondaries));
        attributes.put(EventAttribute.CELLULAR_DATA.toString(), isEnabled(celluarDataEnabled));
        mAnalyticsImpl.tagEvent(EventTag.SETTINGS_SUMMARY.toString(), attributes);
    }

    public static void recordDLMWipe$627721fe(DLMRequestType requestType, boolean isSyncOn, String language) {
        Map<String, String> attributes = new HashMap<>();
        if (requestType != null) {
            attributes.put(EventAttribute.DLM_REQUEST_TYPE.toString(), requestType.toString());
        } else {
            eventError("DLM_REQUEST_TYPE not provided!", attributes);
        }
        attributes.put(EventAttribute.BACKUP_SYNC_STATUS.toString(), isSyncOn ? ServerProtocol.DIALOG_RETURN_SCOPES_TRUE : ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        if (language != null) {
            attributes.put(EventAttribute.LANGUAGE.toString(), language);
        } else {
            eventError("LANGUAGE not provided!", attributes);
        }
        mAnalyticsImpl.tagEvent(EventTag.DLM_WIPE.toString(), attributes);
    }

    public static void recordBTKbUsage() {
        log.d("BTlocalytics recordBTKbUsage:");
        log.d("BT keyboard is in use, tag the usage");
        mAnalyticsImpl.tagEvent(EventTag.BLUETOOTH_KB_USAGE.toString(), null);
    }

    private static void recordThemesUsageData(Screen newScreen) {
        EventTag tagEvent;
        boolean isMyThemesExitEvent = false;
        boolean isGetThemesExitEvent = false;
        String s = "activeScreen=" + activeScreen + ", ";
        log.d("recordThemesUsageData: " + (s + "newScreen=" + newScreen + ", "));
        if (activeScreen != newScreen) {
            if (Screen.MY_THEMES == activeScreen) {
                isMyThemesExitEvent = true;
            } else if (Screen.GET_THEMES == activeScreen) {
                isGetThemesExitEvent = true;
            } else {
                log.d("recordThemesUsageData: oldScreen=" + activeScreen + ", not committing usage data");
            }
        } else {
            log.d("recordThemesUsageData: oldScreen==newScreen, not committing usage data");
        }
        if (isMyThemesExitEvent || isGetThemesExitEvent) {
            Long browseTimeSec = null;
            Map<String, String> attributes = new HashMap<>();
            if (browseStartTimeThemes > 0) {
                browseTimeSec = Long.valueOf(System.currentTimeMillis() - browseStartTimeThemes);
                browseStartTimeThemes = System.currentTimeMillis();
            } else {
                eventError("recordThemesUsageData: invalid browseStartTimeThemes: " + browseStartTimeThemes, attributes);
            }
            if (isMyThemesExitEvent) {
                tagEvent = EventTag.STORE_MY_THEMES_VISIT;
            } else {
                tagEvent = EventTag.STORE_GET_THEMES_VISIT;
            }
            if (tagEvent != null) {
                attributes.put(EventAttribute.BROWSE_TIME.toString(), getBrowseTime(browseTimeSec.longValue()));
                mAnalyticsImpl.tagEvent(tagEvent.toString(), attributes);
            }
        }
        activeScreen = newScreen;
        if (Screen.MY_THEMES == newScreen) {
            browseStartTimeThemes = System.currentTimeMillis();
        } else if (Screen.GET_THEMES == newScreen) {
            browseStartTimeThemes = System.currentTimeMillis();
        }
    }

    public static void setCustomDimension(CustomDimension.Dimension dim, String value) {
        if (dim == null) {
            log.d("setCustomDimensions: dim is null, ignoring");
        } else if (value == null) {
            log.d("setCustomDimensions: value is null, ignoring");
        } else {
            mAnalyticsImpl.setCustomDimension(dim, value);
        }
    }

    private static void eventError(String err, Map<String, String> attributes) {
        int i = 0;
        while (attributes.get(EventAttribute.EVENT_ERR.toString() + i) != null && i < 10) {
            i++;
        }
        String str = EventAttribute.EVENT_ERR.toString() + i;
        if (err == null) {
            err = "error string not provided!";
        }
        attributes.put(str, err);
    }

    private static String getBrowseTime(long t) {
        long time = t / 1000;
        if (time > 0 && time <= 5) {
            return "0-5s";
        }
        if (time > 5 && time <= 30) {
            return "5-30s";
        }
        if (time > 30 && time <= 60) {
            return "30-1m";
        }
        if (time > 60 && time <= 120) {
            return "1m-2m";
        }
        if (time > 120 && time <= 300) {
            return "2m-5m";
        }
        if (time > 300) {
            return "5m +";
        }
        return "0-5s";
    }

    private static String isEnabled(boolean enabled) {
        return enabled ? "on" : "off";
    }

    /* loaded from: classes.dex */
    public static class TimeBasedAnalyticsSessionManager implements AnalyticsSessionManagementStrategy {
        private long mLastOpenedTime;
        private boolean mOpened;
        private final long mSessionLengthInMs;

        /* synthetic */ TimeBasedAnalyticsSessionManager(byte b) {
            this();
        }

        private TimeBasedAnalyticsSessionManager() {
            this.mSessionLengthInMs = TimeConversion.MILLIS_IN_HOUR;
        }

        @Override // com.nuance.swype.usagedata.UsageData.AnalyticsSessionManagementStrategy
        public final boolean canOpen() {
            return hasTimeElapsed();
        }

        private boolean hasTimeElapsed() {
            return System.currentTimeMillis() - this.mLastOpenedTime > this.mSessionLengthInMs;
        }

        @Override // com.nuance.swype.usagedata.UsageData.AnalyticsSessionManagementStrategy
        public final boolean isOpen() {
            return this.mOpened;
        }

        @Override // com.nuance.swype.usagedata.UsageData.AnalyticsSessionManagementStrategy
        public final void markSessionOpened() {
            this.mLastOpenedTime = System.currentTimeMillis();
            this.mOpened = true;
        }

        @Override // com.nuance.swype.usagedata.UsageData.AnalyticsSessionManagementStrategy
        public final void markSessionClosed() {
            this.mLastOpenedTime = 0L;
            this.mOpened = false;
        }

        @Override // com.nuance.swype.usagedata.UsageData.AnalyticsSessionManagementStrategy
        public final boolean canClose() {
            return this.mOpened && hasTimeElapsed();
        }
    }

    public static void recordUsingSmsScanner() {
        log.d("recordUsingSmsScanner: Recording sms scanner");
        mAnalyticsImpl.tagEvent(EventTag.USED_SMS_SCANNER.toString(), null);
    }

    public static void recordUsingCallLogScanner() {
        log.d("recordUsingCallLogScanner: Recording Call log scanner");
        mAnalyticsImpl.tagEvent(EventTag.USED_CALL_LOG_SCANNER.toString(), null);
    }

    public static void recordUsingGmailScanner() {
        log.d("recordUsingGmailScanner: Recording Gmail scanner");
        mAnalyticsImpl.tagEvent(EventTag.USED_GMAIL_SCANNER.toString(), null);
    }

    public static void recordEmojiSelected(EmojiSelectedSource source) {
        log.d("recordEmojiSelected: Recording Emoji from ", source);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmojiSelectedAttribute.SOURCE.toString(), source.toString());
        mAnalyticsImpl.tagEvent(EventTag.EMOJI_SELECTED.toString(), attributes);
    }

    public static void recordLanguageDownloadRequest(String englishName) {
        log.d("recordLanguageDownloadRequest: Recording download request for: ", englishName);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EventAttribute.DOWNLOAD_LANGUAGE_NAME.toString(), englishName);
        mAnalyticsImpl.tagEvent(EventTag.LANGUAGE_DOWNLOAD_REQUEST.toString(), attributes);
    }
}
