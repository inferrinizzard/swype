package com.localytics.android;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;
import bolts.MeasurementEvent;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.CreativeManager;
import com.localytics.android.InAppCampaign;
import com.localytics.android.Localytics;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class InAppManager extends BaseMarketingManager {
    private static final List<Integer> AUGMENTED_BLACKOUT_WEEKDAYS_RULE;
    private static final Map<String, Integer> DEFAULT_FREQUENCY_CAPPING_RULE;
    private static final int GLOBAL_FREQUENCY_CAPPING_RULE_CAMPAIGN_ID = -1;
    protected CreativeManager mCreativeManager;
    private FragmentManager mFragmentManager;
    private boolean mIsMidDisplayingInApp;
    private MarketingHandler mMarketingHandler;
    private InAppMessagesAdapter mTestCampaignsListAdapter;
    private static final String[] GLOBAL_FREQUENCY_CAPPING_RULE_REQUIRED_KEYS = {"display_frequencies"};
    private static final String[] INDIVIDUAL_FREQUENCY_CAPPING_RULE_REQUIRED_KEYS = {"max_display_count", "ignore_global"};
    private static final String[] PROJECTION_IN_APP_RULE_RECORD = {"_id", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION};
    private static final SimpleDateFormat TIME_SDF = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<Map<String, String>> AUGMENTED_BLACKOUT_TIMES_RULE = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum FrequencyCappingFilter {
        FREQUENCY,
        MAX_COUNT,
        BLACKOUT
    }

    static {
        HashMap<String, String> rule = new HashMap<>();
        rule.put("start", "00:00");
        rule.put("end", "23:59");
        AUGMENTED_BLACKOUT_TIMES_RULE.add(rule);
        AUGMENTED_BLACKOUT_WEEKDAYS_RULE = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
        HashMap hashMap = new HashMap();
        DEFAULT_FREQUENCY_CAPPING_RULE = hashMap;
        hashMap.put("max_display_count", 1);
        DEFAULT_FREQUENCY_CAPPING_RULE.put("ignore_global", 1);
        TIME_SDF.setLenient(false);
        DATE_SDF.setLenient(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InAppManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler) {
        this(localyticsDao, marketingHandler, new CreativeManager(localyticsDao, marketingHandler));
    }

    InAppManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler, CreativeManager creativeManager) {
        super(localyticsDao);
        this.mIsMidDisplayingInApp = false;
        this.mTestCampaignsListAdapter = null;
        this.mMarketingHandler = marketingHandler;
        this.mCreativeManager = creativeManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(11)
    public void dismissCurrentInAppMessage() {
        if (this.mFragmentManager != null) {
            try {
                Fragment currentFragment = this.mFragmentManager.findFragmentByTag("marketing_dialog");
                if (currentFragment instanceof InAppDialogFragment) {
                    ((InAppDialogFragment) currentFragment).dismissCampaign();
                }
            } catch (Exception e) {
                Localytics.Log.e("Localytics library threw an uncaught exception", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(11)
    public boolean _manifestProcessingAllowed() {
        return (this.mFragmentManager == null || this.mFragmentManager.findFragmentByTag("marketing_dialog") == null) && !this.mCreativeManager.hasPendingDownloads();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _processMarketingObject(Map<String, Object> marketingMap, Map<String, Object> config) throws JSONException {
        List<MarketingMessage> inAppMessages = new ArrayList<>();
        Object inapps = marketingMap.get("amp");
        if (inapps != null) {
            for (Map<String, Object> obj : JsonHelper.toList((JSONArray) JsonHelper.toJSON(inapps))) {
                inAppMessages.add(new MarketingMessage(obj));
            }
        }
        _removeDeactivatedInAppMessages(inAppMessages);
        _validateAndStoreFrequencyCappingRule(marketingMap, -1);
        List<MarketingMessage> creativesToDownload = new ArrayList<>();
        SparseArray<String> creativeUrls = _getInAppCreativeUrls();
        for (MarketingMessage inApp : inAppMessages) {
            String oldRemoteFileUrl = creativeUrls.get(((Integer) inApp.get("campaign_id")).intValue());
            String newRemoteFileUrl = getInAppRemoteFileURL(inApp);
            boolean isNewUrl = (TextUtils.isEmpty(newRemoteFileUrl) || newRemoteFileUrl.equals(oldRemoteFileUrl)) ? false : true;
            int ruleId = _saveInAppMessage(inApp, config, isNewUrl);
            if (ruleId > 0 && _validateAndStoreFrequencyCappingRule(inApp, (Integer) inApp.get("campaign_id")) && isNewUrl && JsonHelper.getSafeIntegerFromMap(inApp, "control_group") == 0) {
                MarketingMessage copy = new MarketingMessage(inApp);
                copy.put("_id", Integer.valueOf(ruleId));
                updateMessageWithSpecialKeys(copy, this.mLocalyticsDao, false);
                creativesToDownload.add(copy);
            }
        }
        if (creativesToDownload.size() > 0 && !Constants.isTestModeEnabled()) {
            this.mCreativeManager.downloadCreatives(creativesToDownload, new CreativeManager.LastDownloadedCallback() { // from class: com.localytics.android.InAppManager.1
                @Override // com.localytics.android.CreativeManager.LastDownloadedCallback
                public void onLastDownloaded() {
                    InAppManager.this.mMarketingHandler._processPendingManifest();
                }
            });
        }
        this.mProvider.vacuumIfNecessary();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _handleTestCampaigns() {
        if (this.mTestCampaignsListAdapter == null) {
            return false;
        }
        final InAppMessagesAdapter adapter = this.mTestCampaignsListAdapter;
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.InAppManager.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    adapter.updateDataSet();
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Localytics.Log.e("Exception while handling test mode in-apps", e);
                }
            }
        });
        this.mTestCampaignsListAdapter = null;
        return true;
    }

    int _saveInAppMessage(MarketingMessage inAppMessage, Map<String, Object> config, boolean shouldDeleteCreative) {
        if (!_validateInAppMessage(inAppMessage)) {
            return 0;
        }
        int campaignId = JsonHelper.getSafeIntegerFromMap(inAppMessage, "campaign_id");
        int ruleId = 0;
        int localVersion = 0;
        Cursor cursorRule = null;
        try {
            cursorRule = this.mProvider.query("marketing_rules", PROJECTION_IN_APP_RULE_RECORD, String.format("%s = ?", "campaign_id"), new String[]{Integer.toString(campaignId)}, null);
            if (cursorRule.moveToFirst()) {
                ruleId = cursorRule.getInt(cursorRule.getColumnIndexOrThrow("_id"));
                localVersion = cursorRule.getInt(cursorRule.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION));
            }
            if (ruleId > 0) {
                Localytics.Log.w(String.format("Existing in-app rule already exists for this campaign\n\t campaignID = %d\n\t ruleID = %d", Integer.valueOf(campaignId), Integer.valueOf(ruleId)));
                int remoteVersion = JsonHelper.getSafeIntegerFromMap(inAppMessage, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION);
                if (localVersion >= remoteVersion) {
                    Localytics.Log.w(String.format("No update needed. Campaign version has not been updated\n\t version: %d", Integer.valueOf(localVersion)));
                    return 0;
                }
                _deleteInAppRuleEventsAndConditions(ruleId);
                if (shouldDeleteCreative) {
                    _deleteInAppCreative(ruleId);
                }
            } else {
                Localytics.Log.w("In-app campaign not found. Creating a new one.");
            }
            ContentValues values = _parseInAppMessage(ruleId, inAppMessage, config);
            int ruleId2 = (int) this.mProvider.replace("marketing_rules", values);
            if (ruleId2 > 0) {
                _saveInAppConditions(ruleId2, JsonHelper.getSafeListFromMap(inAppMessage, "conditions"));
                List<String> eventNames = null;
                try {
                    eventNames = JsonHelper.toList((JSONArray) JsonHelper.toJSON(inAppMessage.get("display_events")));
                } catch (JSONException e) {
                }
                if (eventNames != null) {
                    _bindRuleToEvents(ruleId2, eventNames);
                    return ruleId2;
                }
                return ruleId2;
            }
            return ruleId2;
        } finally {
            if (cursorRule != null) {
                cursorRule.close();
            }
        }
    }

    private void _destroyLocalInAppMessage(MarketingMessage inAppMessage) {
        try {
            int campaignId = ((Integer) inAppMessage.get("campaign_id")).intValue();
            int ruleId = _getRuleIdFromCampaignId(campaignId);
            _deleteInAppRuleEventsAndConditions(ruleId);
            this.mProvider.remove("marketing_rules", String.format("%s = ?", "_id"), new String[]{Integer.toString(ruleId)});
            _deleteInAppCreative(ruleId);
        } catch (Exception e) {
            Localytics.Log.e("Localytics library threw an uncaught exception", e);
        }
    }

    private void _deleteInAppRuleEventsAndConditions(int ruleId) {
        long[] arr$ = _getConditionIdFromRuleId(ruleId);
        for (long conditionId : arr$) {
            this.mProvider.remove("marketing_condition_values", String.format("%s = ?", "condition_id_ref"), new String[]{Long.toString(conditionId)});
        }
        this.mProvider.remove("marketing_conditions", String.format("%s = ?", "rule_id_ref"), new String[]{Integer.toString(ruleId)});
        this.mProvider.remove("marketing_ruleevent", String.format("%s = ?", "rule_id_ref"), new String[]{Integer.toString(ruleId)});
    }

    private void _deleteInAppCreative(int ruleId) {
        String basePath = CreativeManager.getInAppUnzipFileDirPath(ruleId, this.mLocalyticsDao);
        if (basePath != null) {
            Utils.deleteFile(new File(basePath));
            Utils.deleteFile(new File(basePath + ".zip"));
        }
    }

    protected boolean _validateInAppMessage(MarketingMessage inAppMessage) {
        int campaignId = JsonHelper.getSafeIntegerFromMap(inAppMessage, "campaign_id");
        String ruleName = JsonHelper.getSafeStringFromMap(inAppMessage, "rule_name");
        List<Object> eventNames = JsonHelper.getSafeListFromMap(inAppMessage, "display_events");
        int expiration = JsonHelper.getSafeIntegerFromMap(inAppMessage, "expiration");
        String location = JsonHelper.getSafeStringFromMap(inAppMessage, "location");
        long now = this.mLocalyticsDao.getCurrentTimeMillis() / 1000;
        return (campaignId == 0 || TextUtils.isEmpty(ruleName) || eventNames == null || TextUtils.isEmpty(location) || (((long) expiration) <= now && !Constants.isTestModeEnabled())) ? false : true;
    }

    private ContentValues _parseInAppMessage(int ruleId, MarketingMessage inAppMessage, Map<String, Object> config) {
        int schemaVersion;
        ContentValues values = new ContentValues();
        values.put("_id", ruleId > 0 ? Integer.valueOf(ruleId) : null);
        values.put("campaign_id", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, "campaign_id")));
        values.put("expiration", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, "expiration")));
        values.put("display_seconds", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, "display_seconds")));
        values.put("display_session", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, "display_session")));
        values.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
        values.put("phone_location", JsonHelper.getSafeStringFromMap(inAppMessage, "phone_location"));
        Map<String, Object> phoneSize = JsonHelper.getSafeMapFromMap(inAppMessage, "phone_size");
        values.put("phone_size_width", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(phoneSize, "width")));
        values.put("phone_size_height", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(phoneSize, "height")));
        Map<String, Object> tabletSize = JsonHelper.getSafeMapFromMap(inAppMessage, "tablet_size");
        values.put("tablet_location", JsonHelper.getSafeStringFromMap(inAppMessage, "tablet_location"));
        values.put("tablet_size_width", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(tabletSize, "width")));
        values.put("tablet_size_height", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(tabletSize, "height")));
        values.put("time_to_display", (Integer) 1);
        values.put("internet_required", Integer.valueOf(JsonHelper.getSafeBooleanFromMap(inAppMessage, "internet_required") ? 1 : 0));
        values.put("ab_test", JsonHelper.getSafeStringFromMap(inAppMessage, "ab"));
        String ruleName = JsonHelper.getSafeStringFromMap(inAppMessage, "rule_name");
        values.put("rule_name_non_unique", ruleName);
        values.put("rule_name", UUID.randomUUID().toString());
        values.put("location", JsonHelper.getSafeStringFromMap(inAppMessage, "location"));
        values.put("devices", JsonHelper.getSafeStringFromMap(inAppMessage, "devices"));
        values.put("control_group", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(inAppMessage, "control_group")));
        if (config != null && (schemaVersion = JsonHelper.getSafeIntegerFromMap(config, "schema_version")) > 0) {
            values.put("schema_version", Integer.valueOf(schemaVersion));
        }
        return values;
    }

    private void _saveInAppConditions(long ruleId, List<Object> conditions) {
        if (conditions != null) {
            long[] arr$ = _getConditionIdFromRuleId(ruleId);
            for (long conditionId : arr$) {
                this.mProvider.remove("marketing_condition_values", String.format("%s = ?", "condition_id_ref"), new String[]{Long.toString(conditionId)});
            }
            this.mProvider.remove("marketing_conditions", String.format("%s = ?", "rule_id_ref"), new String[]{Long.toString(ruleId)});
            Iterator i$ = conditions.iterator();
            while (i$.hasNext()) {
                List<String> condition = (List) i$.next();
                ContentValues values = new ContentValues();
                values.put("attribute_name", condition.get(0));
                values.put("operator", condition.get(1));
                values.put("rule_id_ref", Long.valueOf(ruleId));
                long conditionId2 = this.mProvider.insert("marketing_conditions", values);
                for (int i = 2; i < condition.size(); i++) {
                    ContentValues values2 = new ContentValues();
                    values2.put("value", JsonHelper.getSafeStringFromValue(condition.get(i)));
                    values2.put("condition_id_ref", Long.valueOf(conditionId2));
                    this.mProvider.insert("marketing_condition_values", values2);
                }
            }
        }
    }

    private void _bindRuleToEvents(long ruleId, List<String> eventNames) {
        this.mProvider.remove("marketing_ruleevent", String.format("%s = ?", "rule_id_ref"), new String[]{Long.toString(ruleId)});
        for (String eventName : eventNames) {
            ContentValues values = new ContentValues();
            values.put(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, eventName);
            values.put("rule_id_ref", Long.valueOf(ruleId));
            this.mProvider.insert("marketing_ruleevent", values);
        }
    }

    boolean _validateFrequencyCappingRule(Map<String, Object> rule, boolean isGlobal) {
        String[] arr$ = isGlobal ? GLOBAL_FREQUENCY_CAPPING_RULE_REQUIRED_KEYS : INDIVIDUAL_FREQUENCY_CAPPING_RULE_REQUIRED_KEYS;
        for (String key : arr$) {
            if (!rule.containsKey(key)) {
                return false;
            }
        }
        List<Map<String, Integer>> frequencyRules = (List) rule.get("display_frequencies");
        if (!_checkFrequencyCappingRuleArray(frequencyRules, new String[]{"days", "count"}) || !_additionalFCDisplayFrequencyRulesValidation(frequencyRules)) {
            return false;
        }
        List<Map> blackoutRulesList = (List) rule.get("blackout_rules");
        if (blackoutRulesList != null) {
            if (blackoutRulesList.size() <= 0) {
                return false;
            }
            for (Map m : blackoutRulesList) {
                if (!m.containsKey("times") && !m.containsKey("dates") && !m.containsKey("weekdays")) {
                    return false;
                }
                List<Map<String, String>> blackoutTimesRules = (List) m.get("times");
                if (!_checkFrequencyCappingRuleArray(blackoutTimesRules, new String[]{"start", "end"}) || !_additionalFCDatesAndTimesRulesValidation(blackoutTimesRules, TIME_SDF)) {
                    return false;
                }
                List<Map<String, String>> blackoutDatesRules = (List) m.get("dates");
                if (!_checkFrequencyCappingRuleArray(blackoutDatesRules, new String[]{"start", "end"}) || !_additionalFCDatesAndTimesRulesValidation(blackoutDatesRules, DATE_SDF)) {
                    return false;
                }
                List<Integer> blackoutWeekdaysRules = (List) m.get("weekdays");
                if ((blackoutWeekdaysRules != null && (blackoutWeekdaysRules.size() <= 0 || blackoutWeekdaysRules.size() > 7)) || !_additionalFCWeekdaysRulesValidation(blackoutWeekdaysRules)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _checkFrequencyCappingRuleArray(List<?> rulesList, String[] subKeys) {
        if (rulesList != null) {
            if (rulesList.size() <= 0) {
                return false;
            }
            if (subKeys.length > 0) {
                for (String subKey : subKeys) {
                    Iterator i$ = rulesList.iterator();
                    while (i$.hasNext()) {
                        if (!((Map) i$.next()).containsKey(subKey)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    boolean _additionalFCDisplayFrequencyRulesValidation(List<Map<String, Integer>> displayFrequencyRules) {
        if (displayFrequencyRules != null) {
            for (Map<String, Integer> displayFrequencyRule : displayFrequencyRules) {
                if (displayFrequencyRule.get("days").intValue() <= 0 || displayFrequencyRule.get("count").intValue() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _additionalFCDatesAndTimesRulesValidation(List<Map<String, String>> rules, SimpleDateFormat formatter) {
        if (rules != null) {
            for (Map<String, String> rule : rules) {
                try {
                    Date startTime = formatter.parse(rule.get("start"));
                    Date endTime = formatter.parse(rule.get("end"));
                    if (startTime.after(endTime)) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _additionalFCWeekdaysRulesValidation(List<Integer> weekdaysRules) {
        if (weekdaysRules != null) {
            if (weekdaysRules.size() > 7) {
                return false;
            }
            for (Integer weekday : weekdaysRules) {
                if (weekday.intValue() < 0 || weekday.intValue() > 6) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _validateAndStoreFrequencyCappingRule(Map<String, Object> objectWithRule, Integer campaignId) {
        Map<String, Object> rule = (Map) objectWithRule.get("frequency_capping");
        if (rule != null) {
            if (_validateFrequencyCappingRule(rule, campaignId.intValue() == -1)) {
                boolean success = _saveFrequencyCappingRule(rule, campaignId);
                return success;
            }
        }
        if (campaignId.intValue() == -1) {
            return false;
        }
        boolean success2 = _saveFrequencyCappingRule(new HashMap(DEFAULT_FREQUENCY_CAPPING_RULE), campaignId);
        return success2;
    }

    boolean _saveFrequencyCappingRule(Map<String, Object> rule, Integer campaignId) {
        this.mProvider.mDb.beginTransaction();
        int frequencyId = (int) _saveFrequencyCappingRuleBase(rule, campaignId);
        List<Map<String, Integer>> frequencyRules = (List) rule.get("display_frequencies");
        boolean success = frequencyId > 0 && _saveFrequencyCappingRuleDisplayFrequency(frequencyRules, Integer.valueOf(frequencyId));
        List<Map<String, Object>> blackoutRules = (List) rule.get("blackout_rules");
        boolean success2 = success && _saveFrequencyCappingRuleBlackout(blackoutRules, Integer.valueOf(frequencyId));
        if (success2) {
            this.mProvider.mDb.setTransactionSuccessful();
        }
        this.mProvider.mDb.endTransaction();
        return success2;
    }

    boolean _deleteFrequencyCappingRule(Integer campaignId) {
        try {
            this.mProvider.remove("frequency_capping_rules", String.format("%s = ?", "campaign_id"), new String[]{String.valueOf(campaignId)});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    long _saveFrequencyCappingRuleBase(Map<String, ?> rule, Integer campaignId) {
        ContentValues values = new ContentValues();
        values.put("campaign_id", campaignId);
        if (campaignId.intValue() != -1) {
            values.put("max_display_count", (Integer) rule.get("max_display_count"));
            values.put("ignore_global", (Integer) rule.get("ignore_global"));
        } else {
            values.putNull("max_display_count");
            values.putNull("ignore_global");
        }
        return this.mProvider.mDb.replace("frequency_capping_rules", null, values);
    }

    boolean _saveFrequencyCappingRuleDisplayFrequency(List<Map<String, Integer>> rules, Integer frequencyId) {
        if (rules != null) {
            for (Map<String, Integer> displayFrequencyRule : rules) {
                ContentValues values = new ContentValues();
                values.put("frequency_id", frequencyId);
                values.put("count", displayFrequencyRule.get("count"));
                values.put("days", displayFrequencyRule.get("days"));
                if (this.mProvider.insert("frequency_capping_display_frequencies", values) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _saveFrequencyCappingRuleBlackout(List<Map<String, Object>> rules, Integer frequencyId) {
        if (rules != null) {
            int groupId = 0;
            for (Map<String, Object> blackoutRule : rules) {
                Map<String, Object> augmentedBlackoutRule = _augmentBlackoutRule(blackoutRule);
                List<Map<String, String>> blackoutDatesRules = (List) augmentedBlackoutRule.get("dates");
                if (!_saveFrequencyCappingRuleBlackoutDates(blackoutDatesRules, frequencyId, Integer.valueOf(groupId))) {
                    return false;
                }
                List<Integer> blackoutWeekdaysRules = (List) augmentedBlackoutRule.get("weekdays");
                if (!_saveFrequencyCappingRuleBlackoutWeekdays(blackoutWeekdaysRules, frequencyId, Integer.valueOf(groupId))) {
                    return false;
                }
                List<Map<String, String>> blackoutTimesRules = (List) augmentedBlackoutRule.get("times");
                if (!_saveFrequencyCappingRuleBlackoutTimes(blackoutTimesRules, frequencyId, Integer.valueOf(groupId))) {
                    return false;
                }
                groupId++;
            }
        }
        return true;
    }

    Map<String, Object> _augmentBlackoutRule(Map<String, Object> blackoutRule) {
        Map<String, Object> augmentedRule = new HashMap<>(blackoutRule);
        boolean hasDates = blackoutRule.containsKey("dates");
        boolean hasWeekdays = blackoutRule.containsKey("weekdays");
        boolean hasTimes = blackoutRule.containsKey("times");
        if ((hasDates || hasWeekdays) && !hasTimes) {
            augmentedRule.put("times", new ArrayList(AUGMENTED_BLACKOUT_TIMES_RULE));
        } else if (hasTimes && !hasDates && !hasWeekdays) {
            augmentedRule.put("weekdays", new ArrayList(AUGMENTED_BLACKOUT_WEEKDAYS_RULE));
        }
        return augmentedRule;
    }

    boolean _saveFrequencyCappingRuleBlackoutDates(List<Map<String, String>> rules, Integer frequencyId, Integer groupId) {
        if (rules != null) {
            for (Map<String, String> blackoutRule : rules) {
                try {
                    this.mProvider.mDb.execSQL(String.format("INSERT INTO %s VALUES (?, ?, datetime(?,'start of day'), datetime(?,'start of day','1 day','-1 second'));", "frequency_capping_blackout_dates"), new Object[]{frequencyId, groupId, blackoutRule.get("start"), blackoutRule.get("end")});
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _saveFrequencyCappingRuleBlackoutWeekdays(List<Integer> rules, Integer frequencyId, Integer groupId) {
        if (rules != null) {
            for (Integer weekday : rules) {
                ContentValues values = new ContentValues();
                values.put("frequency_id", frequencyId);
                values.put("rule_group_id", groupId);
                values.put("day", weekday);
                if (this.mProvider.insert("frequency_capping_blackout_weekdays", values) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean _saveFrequencyCappingRuleBlackoutTimes(List<Map<String, String>> rules, Integer frequencyId, Integer groupId) {
        if (rules != null) {
            for (Map<String, String> blackoutRule : rules) {
                Integer start = Integer.valueOf(_timeStringToSeconds(blackoutRule.get("start")));
                Integer end = Integer.valueOf(_timeStringToSeconds(blackoutRule.get("end")));
                if (start.intValue() == -1 || end.intValue() == -1) {
                    return false;
                }
                ContentValues values = new ContentValues();
                values.put("frequency_id", frequencyId);
                values.put("rule_group_id", groupId);
                values.put("start", start);
                values.put("end", Integer.valueOf(end.intValue() + 59));
                if (this.mProvider.insert("frequency_capping_blackout_times", values) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int _timeStringToSeconds(String time) {
        try {
            String[] components = time.split(":");
            return (Integer.valueOf(components[0]).intValue() * ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL) + (Integer.valueOf(components[1]).intValue() * 60);
        } catch (Exception e) {
            return -1;
        }
    }

    void _removeDeactivatedInAppMessages(List<MarketingMessage> inAppMessages) {
        List<Integer> campaignIds = new ArrayList<>();
        Iterator i$ = inAppMessages.iterator();
        while (i$.hasNext()) {
            campaignIds.add(Integer.valueOf(JsonHelper.getSafeIntegerFromMap(i$.next(), "campaign_id")));
        }
        _deleteFrequencyCappingRule(-1);
        Vector<MarketingMessage> inAppMessageMaps = new Vector<>();
        Cursor rulesCursor = null;
        try {
            rulesCursor = this.mProvider.query("marketing_rules", null, null, null, null);
            for (int i = 0; i < rulesCursor.getCount(); i++) {
                rulesCursor.moveToPosition(i);
                int campaignId = rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("campaign_id"));
                if (!campaignIds.contains(Integer.valueOf(campaignId))) {
                    MarketingMessage inAppMessageMap = new MarketingMessage();
                    inAppMessageMap.put("_id", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("_id"))));
                    inAppMessageMap.put("campaign_id", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("campaign_id"))));
                    inAppMessageMap.put("expiration", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("expiration"))));
                    inAppMessageMap.put("display_seconds", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("display_seconds"))));
                    inAppMessageMap.put("display_session", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("display_session"))));
                    inAppMessageMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, rulesCursor.getString(rulesCursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
                    inAppMessageMap.put("phone_location", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("phone_location")));
                    inAppMessageMap.put("phone_size_width", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("phone_size_width"))));
                    inAppMessageMap.put("phone_size_height", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("phone_size_height"))));
                    inAppMessageMap.put("tablet_location", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("tablet_location")));
                    inAppMessageMap.put("tablet_size_width", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("tablet_size_width"))));
                    inAppMessageMap.put("tablet_size_height", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("tablet_size_height"))));
                    inAppMessageMap.put("time_to_display", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("time_to_display"))));
                    inAppMessageMap.put("internet_required", Integer.valueOf(rulesCursor.getInt(rulesCursor.getColumnIndexOrThrow("internet_required"))));
                    inAppMessageMap.put("ab_test", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("ab_test")));
                    inAppMessageMap.put("rule_name_non_unique", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("rule_name_non_unique")));
                    inAppMessageMap.put("location", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("location")));
                    inAppMessageMap.put("devices", rulesCursor.getString(rulesCursor.getColumnIndexOrThrow("devices")));
                    inAppMessageMaps.add(inAppMessageMap);
                }
            }
            Iterator i$2 = inAppMessageMaps.iterator();
            while (i$2.hasNext()) {
                MarketingMessage inApp = i$2.next();
                _destroyLocalInAppMessage(inApp);
                _deleteFrequencyCappingRule((Integer) inApp.get("campaign_id"));
            }
        } finally {
            if (rulesCursor != null) {
                rulesCursor.close();
            }
        }
    }

    SparseArray<String> _getInAppCreativeUrls() {
        SparseArray<String> creativeUrlMap = new SparseArray<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("marketing_rules", new String[]{"campaign_id", "phone_location", "tablet_location", "devices"}, null, null, null);
            while (cursor.moveToNext()) {
                MarketingMessage marketingMessage = new MarketingMessage();
                int campaignId = cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"));
                marketingMessage.put("phone_location", cursor.getString(cursor.getColumnIndexOrThrow("phone_location")));
                marketingMessage.put("tablet_location", cursor.getString(cursor.getColumnIndexOrThrow("tablet_location")));
                marketingMessage.put("devices", cursor.getString(cursor.getColumnIndexOrThrow("devices")));
                String creativeUrl = getInAppRemoteFileURL(marketingMessage);
                creativeUrlMap.put(campaignId, creativeUrl);
            }
            return creativeUrlMap;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _inAppMessageTrigger(final String event, final Map<String, String> attributes) {
        List<MarketingMessage> messages = _getQualifiedInAppMessageForTrigger(event);
        if (messages != null) {
            List<MarketingMessage> messages2 = _campaignsSatisfyingConditions(messages, attributes);
            List<MarketingMessage> readyToDisplay = _messagesReadyToDisplay(messages2);
            if (readyToDisplay.size() > 0) {
                _triggerMessage(readyToDisplay.get(0), attributes);
                return;
            }
            if (messages2.size() > 0) {
                List<MarketingMessage> withoutCreatives = new LinkedList<>(messages2);
                withoutCreatives.removeAll(readyToDisplay);
                if (withoutCreatives.size() > 0) {
                    this.mCreativeManager.priorityDownloadCreatives(withoutCreatives, new CreativeManager.FirstDownloadedCallback() { // from class: com.localytics.android.InAppManager.3
                        @Override // com.localytics.android.CreativeManager.FirstDownloadedCallback
                        public void onFirstDownloaded() {
                            InAppManager.this._inAppMessageTrigger(event, attributes);
                        }
                    });
                }
            }
        }
    }

    protected List<MarketingMessage> _messagesReadyToDisplay(List<MarketingMessage> messages) {
        Collection<? extends MarketingMessage> controlGroupMessages = _controlGroupMessages(messages);
        List<MarketingMessage> nonControlGroupMessages = new LinkedList<>(messages);
        nonControlGroupMessages.removeAll(controlGroupMessages);
        List<MarketingMessage> withCreatives = this.mCreativeManager.inAppCampaignsWithDownloadedCreatives(nonControlGroupMessages);
        List<MarketingMessage> readyToDisplay = new LinkedList<>(withCreatives);
        readyToDisplay.addAll(controlGroupMessages);
        return readyToDisplay;
    }

    private List<MarketingMessage> _controlGroupMessages(List<MarketingMessage> messages) {
        List<MarketingMessage> controlGroupMessages = new LinkedList<>();
        for (MarketingMessage msg : messages) {
            if (JsonHelper.getSafeIntegerFromMap(msg, "control_group") > 0) {
                controlGroupMessages.add(msg);
            }
        }
        return controlGroupMessages;
    }

    protected void _triggerMessage(MarketingMessage message, Map<String, String> attributes) {
        int campaignId = JsonHelper.getSafeIntegerFromMap(message, "campaign_id");
        if (JsonHelper.getSafeIntegerFromMap(message, "control_group") == 1) {
            if (_setInAppMessageAsDisplayed(campaignId)) {
                _tagAmpActionEventForControlGroup(message);
                return;
            }
            return;
        }
        _tryDisplayingInAppCampaign(message, attributes);
    }

    private void _tagAmpActionEventForControlGroup(MarketingMessage marketingMessage) {
        TreeMap<String, String> impressionAttributes = new TreeMap<>();
        String campaignId = marketingMessage.get("campaign_id").toString();
        String ruleName = marketingMessage.get("rule_name_non_unique").toString();
        String schemaVersion = marketingMessage.get("schema_version").toString();
        String ab = (String) marketingMessage.get("ab_test");
        if (!TextUtils.isEmpty(ab)) {
            impressionAttributes.put("ampAB", ab);
        }
        impressionAttributes.put("ampAction", "control");
        impressionAttributes.put("type", "Control");
        impressionAttributes.put("ampCampaignId", campaignId);
        impressionAttributes.put("ampCampaign", ruleName);
        impressionAttributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
        impressionAttributes.put("Schema Version - Server", schemaVersion);
        this.mLocalyticsDao.tagEvent("ampView", impressionAttributes);
    }

    List<MarketingMessage> _getQualifiedInAppMessageForTrigger(String event) {
        String eventName;
        if (Constants.isTestModeEnabled()) {
            return null;
        }
        if (event.equals("open")) {
            if (Constants.isTestModeEnabled()) {
                return null;
            }
            eventName = "AMP Loaded";
        } else {
            eventName = event;
        }
        List<MarketingMessage> inAppMessages = _getInAppMessageMaps(eventName);
        if (inAppMessages.size() == 0) {
            Context appContext = this.mLocalyticsDao.getAppContext();
            if (eventName.startsWith(appContext.getPackageName())) {
                String eventString = eventName.substring(appContext.getPackageName().length() + 1, eventName.length());
                inAppMessages = _getInAppMessageMaps(eventString);
            }
        }
        return _filterInAppMessagesDisqualifiedByFrequencyCappingRules(inAppMessages);
    }

    private List<MarketingMessage> _getInAppMessageMaps(String eventName) {
        LinkedList<MarketingMessage> inAppMessageMaps = new LinkedList<>();
        Cursor cursor = null;
        try {
            String now = Long.toString(this.mLocalyticsDao.getCurrentTimeMillis() / 1000);
            String sql = String.format("SELECT * FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = ?) AND %s > ?;", "marketing_rules", "_id", "rule_id_ref", "marketing_ruleevent", MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, "expiration");
            cursor = this.mProvider.mDb.rawQuery(sql, new String[]{eventName, now});
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                MarketingMessage inAppMessageMap = new MarketingMessage();
                inAppMessageMap.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                inAppMessageMap.put("campaign_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                inAppMessageMap.put("expiration", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("expiration"))));
                inAppMessageMap.put("display_seconds", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_seconds"))));
                inAppMessageMap.put("display_session", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_session"))));
                inAppMessageMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, cursor.getString(cursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
                inAppMessageMap.put("phone_location", cursor.getString(cursor.getColumnIndexOrThrow("phone_location")));
                inAppMessageMap.put("phone_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_width"))));
                inAppMessageMap.put("phone_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_height"))));
                inAppMessageMap.put("tablet_location", cursor.getString(cursor.getColumnIndexOrThrow("tablet_location")));
                inAppMessageMap.put("tablet_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_width"))));
                inAppMessageMap.put("tablet_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_height"))));
                inAppMessageMap.put("time_to_display", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("time_to_display"))));
                inAppMessageMap.put("internet_required", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("internet_required"))));
                inAppMessageMap.put("ab_test", cursor.getString(cursor.getColumnIndexOrThrow("ab_test")));
                inAppMessageMap.put("rule_name_non_unique", cursor.getString(cursor.getColumnIndexOrThrow("rule_name_non_unique")));
                inAppMessageMap.put("location", cursor.getString(cursor.getColumnIndexOrThrow("location")));
                inAppMessageMap.put("devices", cursor.getString(cursor.getColumnIndexOrThrow("devices")));
                inAppMessageMap.put("control_group", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("control_group"))));
                inAppMessageMap.put("schema_version", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("schema_version"))));
                updateMessageWithSpecialKeys(inAppMessageMap, this.mLocalyticsDao, true);
                inAppMessageMaps.add(inAppMessageMap);
            }
            return inAppMessageMaps;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getInAppRemoteFileURL(MarketingMessage marketingMessage) {
        String devices = JsonHelper.getSafeStringFromMap(marketingMessage, "devices");
        return (devices == null || !devices.equals("tablet")) ? JsonHelper.getSafeStringFromMap(marketingMessage, "phone_location") : JsonHelper.getSafeStringFromMap(marketingMessage, "tablet_location");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateMessageWithSpecialKeys(MarketingMessage message, LocalyticsDao localyticsDao, boolean includeDimensions) {
        int displayWidth;
        int displayHeight;
        int ruleId = ((Integer) message.get("_id")).intValue();
        String localHtmlURL = "file://" + CreativeManager.getInAppLocalHtmlLocation(ruleId, localyticsDao);
        String remoteFileLocation = getInAppRemoteFileURL(message);
        String localFileLocation = CreativeManager.getInAppLocalFileURL(ruleId, remoteFileLocation.endsWith(".zip"), localyticsDao);
        message.put("creative_url", remoteFileLocation);
        message.put("html_url", localHtmlURL);
        message.put("base_path", CreativeManager.getInAppUnzipFileDirPath(ruleId, localyticsDao));
        message.put("zip_name", String.format("amp_rule_%d.zip", Integer.valueOf(ruleId)));
        message.put("local_file_location", localFileLocation);
        message.put("download_url", remoteFileLocation);
        if (includeDimensions) {
            String devices = (String) message.get("devices");
            if (devices.equals("tablet")) {
                displayWidth = ((Integer) message.get("tablet_size_width")).intValue();
                displayHeight = ((Integer) message.get("tablet_size_height")).intValue();
            } else {
                devices.equals("both");
                displayWidth = ((Integer) message.get("phone_size_width")).intValue();
                displayHeight = ((Integer) message.get("phone_size_height")).intValue();
            }
            message.put("display_width", Float.valueOf(displayWidth));
            message.put("display_height", Float.valueOf(displayHeight));
        }
    }

    List<MarketingMessage> _filterInAppMessagesDisqualifiedByFrequencyCappingRules(List<MarketingMessage> candidates) {
        Set<Integer> eligibleCampaignIds = new HashSet<>();
        Iterator i$ = candidates.iterator();
        while (i$.hasNext()) {
            eligibleCampaignIds.add((Integer) i$.next().get("campaign_id"));
        }
        if (eligibleCampaignIds.size() > 0) {
            eligibleCampaignIds.removeAll(_getDisqualifiedCampaigns(FrequencyCappingFilter.MAX_COUNT, eligibleCampaignIds));
        }
        if (eligibleCampaignIds.size() > 0) {
            eligibleCampaignIds.removeAll(_getDisqualifiedCampaigns(FrequencyCappingFilter.FREQUENCY, eligibleCampaignIds));
        }
        if (eligibleCampaignIds.size() > 0) {
            eligibleCampaignIds.removeAll(_getDisqualifiedCampaigns(FrequencyCappingFilter.BLACKOUT, eligibleCampaignIds));
        }
        if (eligibleCampaignIds.size() > 0) {
            eligibleCampaignIds.removeAll(_getGloballyDisqualifiedCampaigns(FrequencyCappingFilter.FREQUENCY, eligibleCampaignIds));
        }
        if (eligibleCampaignIds.size() > 0) {
            eligibleCampaignIds.removeAll(_getGloballyDisqualifiedCampaigns(FrequencyCappingFilter.BLACKOUT, eligibleCampaignIds));
        }
        List<MarketingMessage> stillEligibleCandidates = new LinkedList<>();
        if (eligibleCampaignIds.size() > 0) {
            for (MarketingMessage message : candidates) {
                int campaignId = ((Integer) message.get("campaign_id")).intValue();
                if (eligibleCampaignIds.contains(Integer.valueOf(campaignId))) {
                    stillEligibleCandidates.add(message);
                }
            }
        }
        return stillEligibleCandidates;
    }

    Set<Integer> _getDisqualifiedCampaigns(FrequencyCappingFilter filter, Set<Integer> campaignIds) {
        switch (filter) {
            case MAX_COUNT:
                return _getCampaignIdsFromFrequencyCappingQuery(String.format("SELECT fc.%s FROM %s AS fc JOIN %s AS cd ON fc.%s=cd.%s WHERE cd.%s in (%s) GROUP BY fc.%s HAVING count(*) >= fc.%s;", "campaign_id", "frequency_capping_rules", "campaigns_displayed", "campaign_id", "campaign_id", "campaign_id", TextUtils.join(",", campaignIds), "campaign_id", "max_display_count"));
            case FREQUENCY:
                return _getCampaignIdsFromFrequencyCappingQuery(String.format("SELECT DISTINCT fc.%s FROM %s AS fc JOIN %s AS df ON fc.%s=df.%s JOIN %s AS cd ON fc.%s=cd.%s WHERE (cd.%s BETWEEN datetime('%s','-'||df.%s||' days') AND datetime('%s')) AND fc.%s in (%s) GROUP BY df.%s HAVING count(cd.%s) >= df.%s;", "campaign_id", "frequency_capping_rules", "frequency_capping_display_frequencies", "_id", "frequency_id", "campaigns_displayed", "campaign_id", "campaign_id", "date", this.mLocalyticsDao.getTimeStringForSQLite(), "days", this.mLocalyticsDao.getTimeStringForSQLite(), "campaign_id", TextUtils.join(",", campaignIds), "_id", "date", "count"));
            case BLACKOUT:
                Calendar calendar = this.mLocalyticsDao.getCalendar();
                int currentWeekday = calendar.get(7) - 1;
                int currentTime = (((calendar.get(11) * 60) + calendar.get(12)) * 60) + calendar.get(13);
                Set<Integer> campaigns = _getCampaignIdsFromFrequencyCappingQuery(String.format("SELECT fc.%s FROM %s AS fc, %s AS d, %s AS t WHERE ((fc.%s=t.%s) AND (fc.%s=d.%s)) AND (d.%s=t.%s) AND (datetime('%s','localtime') BETWEEN d.%s and d.%s) AND (? BETWEEN t.%s AND t.%s) AND fc.%s IN (%s);", "campaign_id", "frequency_capping_rules", "frequency_capping_blackout_dates", "frequency_capping_blackout_times", "_id", "frequency_id", "_id", "frequency_id", "rule_group_id", "rule_group_id", this.mLocalyticsDao.getTimeStringForSQLite(), "start", "end", "start", "end", "campaign_id", TextUtils.join(",", campaignIds)), new String[]{String.valueOf(currentTime)});
                campaigns.addAll(_getCampaignIdsFromFrequencyCappingQuery(String.format("SELECT fc.%s FROM %s AS fc, %s AS w, %s AS t WHERE ((fc.%s=t.%s) AND (fc.%s=w.%s)) AND (w.%s=t.%s) AND (w.%s=?)  AND (? BETWEEN t.%s AND t.%s) AND fc.%s IN (%s);", "campaign_id", "frequency_capping_rules", "frequency_capping_blackout_weekdays", "frequency_capping_blackout_times", "_id", "frequency_id", "_id", "frequency_id", "rule_group_id", "rule_group_id", "day", "start", "end", "campaign_id", TextUtils.join(",", campaignIds)), new String[]{String.valueOf(currentWeekday), String.valueOf(currentTime)}));
                return campaigns;
            default:
                return new HashSet();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:24:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    java.util.Set<java.lang.Integer> _getGloballyDisqualifiedCampaigns(com.localytics.android.InAppManager.FrequencyCappingFilter r14, java.util.Set<java.lang.Integer> r15) {
        /*
            Method dump skipped, instructions count: 528
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.InAppManager._getGloballyDisqualifiedCampaigns(com.localytics.android.InAppManager$FrequencyCappingFilter, java.util.Set):java.util.Set");
    }

    Set<Integer> _getIgnoresGlobalCampaigns(boolean ignoreGlobal, Set<Integer> campaignIds) {
        Object[] objArr = new Object[6];
        objArr[0] = "campaign_id";
        objArr[1] = "frequency_capping_rules";
        objArr[2] = "ignore_global";
        objArr[3] = Integer.valueOf(ignoreGlobal ? 1 : 0);
        objArr[4] = "campaign_id";
        objArr[5] = TextUtils.join(",", campaignIds);
        return _getCampaignIdsFromFrequencyCappingQuery(String.format("SELECT %s FROM %s WHERE %s = %s AND %s in (%s);", objArr));
    }

    private Set<Integer> _getCampaignIdsFromFrequencyCappingQuery(String query) {
        return _getCampaignIdsFromFrequencyCappingQuery(query, null);
    }

    private Set<Integer> _getCampaignIdsFromFrequencyCappingQuery(String query, String[] inputs) {
        Set<Integer> campaignIds = new HashSet<>();
        if (!TextUtils.isEmpty(query)) {
            Cursor cursor = null;
            try {
                cursor = this.mProvider.mDb.rawQuery(query, inputs);
                while (cursor.moveToNext()) {
                    campaignIds.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return campaignIds;
    }

    void _tryDisplayingInAppCampaign(MarketingMessage inAppMessage, Map<String, String> attributes) {
        _tryDisplayingInAppCampaign(inAppMessage, null, attributes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.localytics.android.InAppManager$4, reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass4 implements Runnable {
        final /* synthetic */ Map val$attributes;
        final /* synthetic */ String val$event;
        final /* synthetic */ MarketingMessage val$inAppMessage;

        AnonymousClass4(MarketingMessage marketingMessage, String str, Map map) {
            this.val$inAppMessage = marketingMessage;
            this.val$event = str;
            this.val$attributes = map;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (InAppManager.this.mFragmentManager != null && InAppManager.this.mFragmentManager.findFragmentByTag("marketing_dialog") == null && !InAppManager.this.mIsMidDisplayingInApp) {
                    InAppManager.this.mIsMidDisplayingInApp = true;
                    int campaignId = ((Integer) this.val$inAppMessage.get("campaign_id")).intValue();
                    new AnonymousClass1(campaignId).execute(new Void[0]);
                }
            } catch (Exception e) {
                Localytics.Log.e("Localytics library threw an uncaught exception", e);
            }
        }

        /* renamed from: com.localytics.android.InAppManager$4$1, reason: invalid class name */
        /* loaded from: classes.dex */
        class AnonymousClass1 extends AsyncTask<Void, Void, Boolean> {
            final /* synthetic */ int val$campaignId;

            AnonymousClass1(int i) {
                this.val$campaignId = i;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... params) {
                try {
                    return Boolean.valueOf(InAppManager.this.mMarketingHandler.setInAppAsDisplayed(this.val$campaignId));
                } catch (Exception e) {
                    Localytics.Log.e("Exception while trying to display in-app", e);
                    return false;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean readyToDisplay) {
                try {
                    if (readyToDisplay.booleanValue()) {
                        if (InAppManager.this.mFragmentManager == null) {
                            InAppManager.this.mMarketingHandler.setInAppMessageAsNotDisplayed(this.val$campaignId);
                        } else {
                            if (!Constants.isTestModeEnabled()) {
                                InAppManager.this.mMarketingHandler.mListeners.getProxy().localyticsWillDisplayInAppMessage();
                            }
                            InAppDialogFragment.newInstance(InAppManager.this.getInAppCampaignFromMarketingMessage(AnonymousClass4.this.val$inAppMessage, AnonymousClass4.this.val$event, AnonymousClass4.this.val$attributes)).setMessagingListener(InAppManager.this.mMarketingHandler.mListeners.getProxy()).setDialogCallback(new InAppDialogCallback() { // from class: com.localytics.android.InAppManager.4.1.1
                                @Override // com.localytics.android.InAppDialogCallback
                                public void doneDisplayingCampaign() {
                                    InAppManager.this.mMarketingHandler.post(new Runnable() { // from class: com.localytics.android.InAppManager.4.1.1.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            InAppManager.this.mMarketingHandler._processPendingManifest();
                                        }
                                    });
                                }
                            }).show(InAppManager.this.mFragmentManager, "marketing_dialog");
                            InAppManager.this.mFragmentManager.executePendingTransactions();
                        }
                    }
                } catch (Exception e) {
                    Localytics.Log.e("Exception while trying to display in-app", e);
                    InAppManager.this.mMarketingHandler.setInAppMessageAsNotDisplayed(this.val$campaignId);
                }
                InAppManager.this.mIsMidDisplayingInApp = false;
            }
        }
    }

    @TargetApi(11)
    void _tryDisplayingInAppCampaign(MarketingMessage inAppMessage, String event, Map<String, String> attributes) {
        new Handler(Looper.getMainLooper()).post(new AnonymousClass4(inAppMessage, event, attributes));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _setInAppMessageAsDisplayed(int campaignId) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("frequency_capping_rules", new String[]{"ignore_global"}, String.format("%s = ?", "campaign_id"), new String[]{String.valueOf(campaignId)}, null);
            if (cursor.moveToFirst()) {
                int ignoresGlobal = cursor.getInt(cursor.getColumnIndex("ignore_global"));
                this.mProvider.mDb.execSQL(String.format("INSERT INTO %s VALUES (?, datetime('%s'), ?);", "campaigns_displayed", this.mLocalyticsDao.getTimeStringForSQLite()), new Integer[]{Integer.valueOf(campaignId), Integer.valueOf(ignoresGlobal)});
                return true;
            }
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _setInAppMessageAsNotDisplayed(int campaignId) {
        String campaignIdString = String.valueOf(campaignId);
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("campaigns_displayed", new String[]{"date"}, String.format("%s = ?", "campaign_id"), new String[]{campaignIdString}, String.format("%s DESC", "date"));
            if (cursor.moveToFirst()) {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                boolean z = this.mProvider.remove("campaigns_displayed", String.format("%s = ? AND %s = ?", "campaign_id", "date"), new String[]{campaignIdString, date}) > 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    List<MarketingMessage> _campaignsSatisfyingConditions(List<MarketingMessage> inAppMessages, Map<String, String> attributes) {
        List<MarketingMessage> candidates = new LinkedList<>();
        for (MarketingMessage message : inAppMessages) {
            if (!(((Integer) message.get("internet_required")).intValue() == 1) || _isConnectingToInternet()) {
                if (_isInAppMessageSatisfiedConditions(message, attributes)) {
                    candidates.add(message);
                }
            }
        }
        return candidates;
    }

    private boolean _isConnectingToInternet() {
        NetworkInfo[] info;
        ConnectivityManager connectivity = (ConnectivityManager) this.mLocalyticsDao.getAppContext().getSystemService("connectivity");
        if (connectivity != null && (info = connectivity.getAllNetworkInfo()) != null) {
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean _isInAppMessageSatisfiedConditions(MarketingMessage inAppMessage, Map<String, String> attributes) {
        int campaignId = ((Integer) inAppMessage.get("campaign_id")).intValue();
        int ruleId = _getRuleIdFromCampaignId(campaignId);
        Vector<MarketingCondition> inAppConditions = _getInAppConditions(ruleId);
        if (inAppConditions == null) {
            return true;
        }
        Iterator i$ = inAppConditions.iterator();
        while (i$.hasNext()) {
            if (!i$.next().isSatisfiedByAttributes(attributes)) {
                return false;
            }
        }
        return true;
    }

    private Vector<MarketingCondition> _getInAppConditions(int ruleId) {
        Vector<MarketingCondition> inAppConditions;
        Vector<MarketingCondition> inAppConditions2 = null;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("marketing_conditions", null, String.format("%s = ?", "rule_id_ref"), new String[]{Integer.toString(ruleId)}, null);
            while (true) {
                try {
                    inAppConditions = inAppConditions2;
                    if (!cursor.moveToNext()) {
                        break;
                    }
                    int conditionId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("attribute_name"));
                    String operator = cursor.getString(cursor.getColumnIndexOrThrow("operator"));
                    Vector<String> values = _getInAppConditionValues(conditionId);
                    inAppConditions2 = inAppConditions == null ? new Vector<>() : inAppConditions;
                    MarketingCondition condition = new MarketingCondition(name, operator, values);
                    condition.setPackageName(this.mLocalyticsDao.getAppContext().getPackageName());
                    inAppConditions2.add(condition);
                } catch (Throwable th) {
                    th = th;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return inAppConditions;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private int _getRuleIdFromCampaignId(int campaignId) {
        int ruleId = 0;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("marketing_rules", new String[]{"_id"}, String.format("%s = ?", "campaign_id"), new String[]{Integer.toString(campaignId)}, null);
            if (cursor.moveToFirst()) {
                ruleId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
            return ruleId;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long[] _getConditionIdFromRuleId(long ruleId) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("marketing_conditions", new String[]{"_id"}, String.format("%s = ?", "rule_id_ref"), new String[]{Long.toString(ruleId)}, null);
            long[] conditionIds = new long[cursor.getCount()];
            int i = 0;
            while (true) {
                int i2 = i;
                if (!cursor.moveToNext()) {
                    break;
                }
                i = i2 + 1;
                conditionIds[i2] = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
            return conditionIds;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Vector<String> _getInAppConditionValues(int conditionId) {
        Vector<String> values;
        Vector<String> values2 = null;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("marketing_condition_values", new String[]{"value"}, String.format("%s = ?", "condition_id_ref"), new String[]{Integer.toString(conditionId)}, null);
            while (true) {
                try {
                    values = values2;
                    if (!cursor.moveToNext()) {
                        break;
                    }
                    String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                    values2 = values == null ? new Vector<>() : values;
                    values2.add(value);
                } catch (Throwable th) {
                    th = th;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return values;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(11)
    public void showInAppTest() {
        try {
            if (this.mFragmentManager != null && this.mFragmentManager.findFragmentByTag("marketing_test_mode_button") == null && this.mFragmentManager.findFragmentByTag("marketing_test_mode_list") == null) {
                final TestModeButton button = TestModeButton.newInstance();
                final TestModeListView listView = TestModeListView.newInstance();
                final Context appContext = this.mLocalyticsDao.getAppContext();
                final InAppMessagesAdapter adapter = new InAppMessagesAdapter(appContext, this);
                listView.setAdapter(adapter);
                Map<Integer, MarketingCallable> callbacksForButton = new TreeMap<>();
                callbacksForButton.put(8, new MarketingCallable() { // from class: com.localytics.android.InAppManager.5
                    /* JADX INFO: Access modifiers changed from: package-private */
                    @Override // com.localytics.android.MarketingCallable
                    public Object call(Object[] params) {
                        try {
                            adapter.updateDataSet();
                            listView.show(InAppManager.this.mFragmentManager, "marketing_test_mode_list");
                            InAppManager.this.mFragmentManager.executePendingTransactions();
                            return null;
                        } catch (Exception e) {
                            Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_POPUP_CAMPAIGN_LIST exception", e);
                            return null;
                        }
                    }
                });
                button.setCallbacks(callbacksForButton);
                button.show(this.mFragmentManager, "marketing_test_mode_button");
                this.mFragmentManager.executePendingTransactions();
                Map<Integer, MarketingCallable> callbacksForList = new TreeMap<>();
                callbacksForList.put(9, new MarketingCallable() { // from class: com.localytics.android.InAppManager.6
                    /* JADX INFO: Access modifiers changed from: package-private */
                    @Override // com.localytics.android.MarketingCallable
                    public Object call(Object[] params) {
                        try {
                            button.show(InAppManager.this.mFragmentManager, "marketing_test_mode_button");
                            InAppManager.this.mFragmentManager.executePendingTransactions();
                            return null;
                        } catch (Exception e) {
                            Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_CLOSE_CAMPAIGN_LIST exception", e);
                            return null;
                        }
                    }
                });
                callbacksForList.put(11, new MarketingCallable() { // from class: com.localytics.android.InAppManager.7
                    /* JADX INFO: Access modifiers changed from: package-private */
                    @Override // com.localytics.android.MarketingCallable
                    public Object call(Object[] params) {
                        try {
                            InAppManager.this.mLocalyticsDao.tagEvent("Test Mode Update Data");
                            InAppManager.this.mTestCampaignsListAdapter = adapter;
                            InAppManager.this.mMarketingHandler.upload();
                            return null;
                        } catch (Exception e) {
                            Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_REFRESH_CAMPAIGN_LIST exception", e);
                            return null;
                        }
                    }
                });
                callbacksForList.put(12, new MarketingCallable() { // from class: com.localytics.android.InAppManager.8
                    /* JADX INFO: Access modifiers changed from: package-private */
                    /* JADX WARN: Type inference failed for: r0v0, types: [com.localytics.android.InAppManager$8$1] */
                    @Override // com.localytics.android.MarketingCallable
                    public Object call(Object[] params) {
                        new AsyncTask<Void, Void, String>() { // from class: com.localytics.android.InAppManager.8.1
                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public String doInBackground(Void... params2) {
                                try {
                                    return InAppManager.this.mLocalyticsDao.getPushRegistrationId();
                                } catch (Exception e) {
                                    Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_COPY_PUSH_TOKEN exception", e);
                                    return null;
                                }
                            }

                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            @TargetApi(11)
                            public void onPostExecute(String registrationId) {
                                try {
                                    if (!TextUtils.isEmpty(registrationId)) {
                                        if (DatapointHelper.getApiLevel() < 11) {
                                            ((ClipboardManager) appContext.getSystemService("clipboard")).setText(registrationId);
                                        } else {
                                            ((android.content.ClipboardManager) appContext.getSystemService("clipboard")).setText(registrationId);
                                        }
                                        Toast.makeText(appContext, registrationId + " has been copied to the clipboard.", 1).show();
                                        return;
                                    }
                                    Toast.makeText(appContext, "No push token found. Please check your integration.", 1).show();
                                } catch (Exception e) {
                                    Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_COPY_PUSH_TOKEN exception", e);
                                }
                            }
                        }.execute(new Void[0]);
                        return null;
                    }
                });
                callbacksForList.put(13, new MarketingCallable() { // from class: com.localytics.android.InAppManager.9
                    /* JADX INFO: Access modifiers changed from: package-private */
                    /* JADX WARN: Type inference failed for: r0v0, types: [com.localytics.android.InAppManager$9$1] */
                    @Override // com.localytics.android.MarketingCallable
                    @TargetApi(11)
                    public Object call(Object[] params) {
                        new AsyncTask<Void, Void, String>() { // from class: com.localytics.android.InAppManager.9.1
                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public String doInBackground(Void... params2) {
                                try {
                                    return InAppManager.this.mLocalyticsDao.getInstallationId();
                                } catch (Exception e) {
                                    Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_COPY_INSTALL_ID exception", e);
                                    return null;
                                }
                            }

                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            @TargetApi(11)
                            public void onPostExecute(String installId) {
                                try {
                                    if (!TextUtils.isEmpty(installId)) {
                                        if (DatapointHelper.getApiLevel() < 11) {
                                            ((ClipboardManager) appContext.getSystemService("clipboard")).setText(installId);
                                        } else {
                                            ((android.content.ClipboardManager) appContext.getSystemService("clipboard")).setText(installId);
                                        }
                                        Toast.makeText(appContext, installId + " has been copied to the clipboard.", 1).show();
                                        return;
                                    }
                                    Toast.makeText(appContext, "No install id found. Please check your integration.", 1).show();
                                } catch (Exception e) {
                                    Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_COPY_INSTALL_ID exception", e);
                                }
                            }
                        }.execute(new Void[0]);
                        return null;
                    }
                });
                callbacksForList.put(10, new AnonymousClass10(appContext));
                listView.setCallbacks(callbacksForList);
            }
        } catch (Exception e) {
            Localytics.Log.e("Exception while attempting to show in-app test", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.localytics.android.InAppManager$10, reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass10 extends MarketingCallable {
        final /* synthetic */ Context val$appContext;

        AnonymousClass10(Context context) {
            this.val$appContext = context;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.localytics.android.MarketingCallable
        public Object call(Object[] params) {
            try {
                final MarketingMessage marketingMessage = (MarketingMessage) params[0];
                InAppManager.updateMessageWithSpecialKeys(marketingMessage, InAppManager.this.mLocalyticsDao, true);
                int ruleId = ((Integer) marketingMessage.get("_id")).intValue();
                final boolean creativeExist = InAppManager.this.mCreativeManager.inAppCreativeExists(ruleId);
                final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
                if (!creativeExist) {
                    mainThreadHandler.post(new Runnable() { // from class: com.localytics.android.InAppManager.10.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Toast.makeText(AnonymousClass10.this.val$appContext, "Downloading the campaign...\nIt'll be shown in few seconds.", 0).show();
                            } catch (Exception e) {
                                Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_SHOW_CAMPAIGN exception", e);
                            }
                        }
                    });
                }
                InAppManager.this.mMarketingHandler.post(new Runnable() { // from class: com.localytics.android.InAppManager.10.2
                    @Override // java.lang.Runnable
                    public void run() {
                        final Runnable displayRunnable = new Runnable() { // from class: com.localytics.android.InAppManager.10.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    if (InAppManager.this.mFragmentManager != null && InAppManager.this.mFragmentManager.findFragmentByTag("marketing_dialog" + marketingMessage.get("campaign_id")) == null) {
                                        InAppDialogFragment.newInstance(InAppManager.this.getInAppCampaignFromMarketingMessage(marketingMessage, null, null)).setMessagingListener(InAppManager.this.mMarketingHandler.mListeners.getProxy()).show(InAppManager.this.mFragmentManager, "marketing_dialog" + marketingMessage.get("campaign_id"));
                                        InAppManager.this.mFragmentManager.executePendingTransactions();
                                    }
                                } catch (Exception e) {
                                    Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_SHOW_CAMPAIGN exception", e);
                                }
                            }
                        };
                        try {
                            if (creativeExist) {
                                mainThreadHandler.post(displayRunnable);
                            } else {
                                List<MarketingMessage> messages = new LinkedList<>();
                                messages.add(marketingMessage);
                                InAppManager.this.mCreativeManager.priorityDownloadCreatives(messages, new CreativeManager.FirstDownloadedCallback() { // from class: com.localytics.android.InAppManager.10.2.2
                                    @Override // com.localytics.android.CreativeManager.FirstDownloadedCallback
                                    public void onFirstDownloaded() {
                                        mainThreadHandler.post(displayRunnable);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_SHOW_CAMPAIGN exception", e);
                        }
                    }
                });
                return null;
            } catch (Exception e) {
                Localytics.Log.e("MarketingCallable ON_IN_APP_TEST_SHOW_CAMPAIGN exception", e);
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InAppCampaign getInAppCampaignFromMarketingMessage(MarketingMessage message, String event, Map<String, String> eventAttributes) {
        Map<String, String> webViewAttributes = new HashMap<>();
        webViewAttributes.put("html_url", (String) message.get("html_url"));
        webViewAttributes.put("base_path", (String) message.get("base_path"));
        webViewAttributes.put("display_width", message.get("display_width").toString());
        webViewAttributes.put("display_height", message.get("display_height").toString());
        return new InAppCampaign.Builder().setCampaignId(((Integer) message.get("campaign_id")).intValue()).setRuleName((String) message.get("rule_name_non_unique")).setVersion(Long.parseLong((String) message.get(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION))).setSchemaVersion(((Integer) message.get("schema_version")).intValue()).setDisplayLocation((String) message.get("location")).setAbTest((String) message.get("ab_test")).setEventAttributes(eventAttributes).setWebViewAttributes(webViewAttributes).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<MarketingMessage> getInAppMessages() {
        return this.mMarketingHandler.getList(new Callable<List<MarketingMessage>>() { // from class: com.localytics.android.InAppManager.11
            @Override // java.util.concurrent.Callable
            public List<MarketingMessage> call() {
                ArrayList<MarketingMessage> marketingMessages = new ArrayList<>();
                Cursor cursor = null;
                try {
                    cursor = InAppManager.this.mProvider.query("marketing_rules", null, null, null, "campaign_id");
                    while (cursor.moveToNext()) {
                        MarketingMessage marketingMessage = new MarketingMessage();
                        marketingMessage.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                        marketingMessage.put("campaign_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                        marketingMessage.put("expiration", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("expiration"))));
                        marketingMessage.put("display_seconds", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_seconds"))));
                        marketingMessage.put("display_session", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_session"))));
                        marketingMessage.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, cursor.getString(cursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
                        marketingMessage.put("phone_location", cursor.getString(cursor.getColumnIndexOrThrow("phone_location")));
                        marketingMessage.put("phone_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_width"))));
                        marketingMessage.put("phone_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_height"))));
                        marketingMessage.put("tablet_location", cursor.getString(cursor.getColumnIndexOrThrow("tablet_location")));
                        marketingMessage.put("tablet_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_width"))));
                        marketingMessage.put("tablet_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_height"))));
                        marketingMessage.put("time_to_display", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("time_to_display"))));
                        marketingMessage.put("internet_required", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("internet_required"))));
                        marketingMessage.put("ab_test", cursor.getString(cursor.getColumnIndexOrThrow("ab_test")));
                        marketingMessage.put("rule_name_non_unique", cursor.getString(cursor.getColumnIndexOrThrow("rule_name_non_unique")));
                        marketingMessage.put("location", cursor.getString(cursor.getColumnIndexOrThrow("location")));
                        marketingMessage.put("devices", cursor.getString(cursor.getColumnIndexOrThrow("devices")));
                        marketingMessage.put("control_group", cursor.getString(cursor.getColumnIndexOrThrow("control_group")));
                        marketingMessage.put("schema_version", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("schema_version"))));
                        marketingMessages.add(marketingMessage);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    marketingMessages = null;
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
                return marketingMessages;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ManifestHolder {
        String manifest;
        boolean successful;

        public ManifestHolder(boolean successful, String manifest) {
            this.successful = successful;
            this.manifest = manifest;
        }
    }
}
