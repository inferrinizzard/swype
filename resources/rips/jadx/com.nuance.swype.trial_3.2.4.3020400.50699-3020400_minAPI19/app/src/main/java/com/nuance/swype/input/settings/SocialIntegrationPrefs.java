package com.nuance.swype.input.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import com.nuance.sns.ScraperStatus;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScannerException;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.Calendar;

/* loaded from: classes.dex */
public abstract class SocialIntegrationPrefs {
    private static final String APPS_PREFERENCE_KEY = "apps_preference_key";
    private static final String CALLLOG_PREFERENCE_KEY = "calllog_preference_key";
    private static final String CONTACTS_PREFERENCE_KEY = "contacts_preference_key";
    private static final String GMAIL_PREFERENCE_KEY = "gmail_preference_key";
    private static final String SMS_PREFERENCE_KEY = "sms_preference_key";
    private static final String TWITTER_PREFERENCE_KEY = "twitter_preference_key";
    private final Activity activity;
    private ScraperStatus calllogScraperStatus;
    private ScraperStatusMonitor clScraperStatusMonitor;
    private ScraperStatusMonitor gmScraperStatusMonitor;
    private ScraperStatus gmailScraperStatus;
    private PreferenceScreen screen;
    private ScraperStatus smsScraperStatus;
    private ScraperStatusMonitor smsScraperStatusMonitor;
    private ScraperStatusMonitor twScraperStatusMonitor;
    private ScraperStatus twitterScraperStatus;
    public static final int SOCIAL_INTEGRATION_PREFS_XML = R.xml.socialintegrationpreferences;
    private static final LogManager.Log log = LogManager.getLog("SocialIntegration");

    protected abstract PreferenceScreen addPreferences();

    protected abstract void showNoNetworkDialog();

    public SocialIntegrationPrefs(Activity activity) {
        this.activity = activity;
    }

    public void onStart() {
        log.d("onStart()...");
        rebuildSettings();
    }

    public void onStop() {
        log.d("onStop()...");
        if (this.twitterScraperStatus != null) {
            this.twitterScraperStatus.removeStatusListener(this.twScraperStatusMonitor);
        }
        if (this.gmailScraperStatus != null) {
            this.gmailScraperStatus.removeStatusListener(this.gmScraperStatusMonitor);
        }
        if (this.smsScraperStatus != null) {
            this.smsScraperStatus.removeStatusListener(this.smsScraperStatusMonitor);
        }
        if (this.calllogScraperStatus != null) {
            this.calllogScraperStatus.removeStatusListener(this.clScraperStatusMonitor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Preference findPreference(String key) {
        if (this.screen != null) {
            return this.screen.findPreference(key);
        }
        return null;
    }

    private void rebuildSettings() {
        if (this.screen != null) {
            this.screen.removeAll();
        }
        this.screen = addPreferences();
        buildSocialNetworkPersonalization();
        updateSocialNetworkPersonalizationStatus();
    }

    public Dialog createNoNetworkDialog() {
        return new AlertDialog.Builder(this.activity).setTitle(R.string.no_network_available).setMessage(R.string.no_network_try_again_msg).setNegativeButton(R.string.dismiss_button, (DialogInterface.OnClickListener) null).create();
    }

    private void updateSocialNetworkPersonalizationStatus() {
        if (AppPreferences.from(this.activity).isPersonalizationEnable()) {
            this.twScraperStatusMonitor.updateScraperStatus(this.twitterScraperStatus);
            this.gmScraperStatusMonitor.updateScraperStatus(this.gmailScraperStatus);
            String smsStatus = this.smsScraperStatus.getScrapStatus(this.activity);
            log.d("smsStatus:", smsStatus);
            log.d("callLogStatus:", this.calllogScraperStatus.getScrapStatus(this.activity));
            if (smsStatus.equals(ScraperStatus.SCRAPING_STATUS_FINISHED)) {
                try {
                    String[] strs = smsStatus.split(":");
                    if (strs.length >= 2) {
                        Calendar lastRun = IMEApplication.from(this.activity).getConnect().getScannerService().getScanner(ACScannerService.ScannerType.SMS).getLastRun();
                        LogManager.Log log2 = log;
                        Object[] objArr = new Object[4];
                        objArr[0] = "sms lastRun:";
                        objArr[1] = lastRun == null ? "null" : lastRun.toString();
                        objArr[2] = "lastRun miliSec:";
                        objArr[3] = lastRun == null ? "" : Long.valueOf(lastRun.getTimeInMillis());
                        log2.d(objArr);
                        long milSec = Long.parseLong(strs[1]);
                        log.d("smsStatus miliSec:", strs[1]);
                        Calendar smsCal = Calendar.getInstance();
                        smsCal.setTimeInMillis(milSec);
                        if (lastRun == null || lastRun.before(smsCal)) {
                            if (this.calllogScraperStatus.getScrapStatus(this.activity).equals(ScraperStatus.SCRAPING_STATUS_NONE)) {
                                this.calllogScraperStatus.setScrapStatus(this.activity, this.smsScraperStatus.getScrapStatus(this.activity));
                            }
                            this.smsScraperStatus.setScrapStatus(this.activity, ScraperStatus.SCRAPING_STATUS_NONE);
                        }
                    }
                } catch (ACScannerException e) {
                    log.e(e.getMessage());
                } catch (Exception e2) {
                    log.e(e2.getMessage());
                }
            }
            this.smsScraperStatusMonitor.updateScraperStatus(this.smsScraperStatus);
            this.clScraperStatusMonitor.updateScraperStatus(this.calllogScraperStatus);
        }
    }

    private void registerSnsOnClickListener(final String prefKey) {
        Preference pref = findPreference(prefKey);
        if (pref != null) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.SocialIntegrationPrefs.1
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (IMEApplication.from(SocialIntegrationPrefs.this.activity).getPlatformUtil().checkForDataConnection() || SocialIntegrationPrefs.SMS_PREFERENCE_KEY.equals(prefKey) || SocialIntegrationPrefs.CALLLOG_PREFERENCE_KEY.equals(prefKey)) {
                        return false;
                    }
                    SocialIntegrationPrefs.this.showNoNetworkDialog();
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(SocialIntegrationPrefs.this.activity);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange(prefKey, "Sign in:Yes", "Sign in:No");
                    }
                    return true;
                }
            });
        }
    }

    private void buildSocialNetworkPersonalization() {
        registerSnsOnClickListener(TWITTER_PREFERENCE_KEY);
        registerSnsOnClickListener(GMAIL_PREFERENCE_KEY);
        registerSnsOnClickListener(CONTACTS_PREFERENCE_KEY);
        registerSnsOnClickListener(SMS_PREFERENCE_KEY);
        registerSnsOnClickListener(CALLLOG_PREFERENCE_KEY);
        registerSnsOnClickListener(APPS_PREFERENCE_KEY);
        ScraperStatus.ScraperStatusFactory factory = IMEApplication.from(this.activity).getScraperStatusFactory();
        this.twitterScraperStatus = factory.getTwitterStatusPreference();
        this.gmailScraperStatus = factory.getGmailStatusPreference();
        this.smsScraperStatus = factory.getSMSStatusPreference();
        this.calllogScraperStatus = factory.getCalllogScraperStatus();
        this.twScraperStatusMonitor = new ScraperStatusMonitor(R.string.sns_twitter_sign_on, TWITTER_PREFERENCE_KEY);
        this.gmScraperStatusMonitor = new ScraperStatusMonitor(R.string.sns_gmail_sign_on, GMAIL_PREFERENCE_KEY);
        this.smsScraperStatusMonitor = new ScraperStatusMonitor(R.string.sns_sms_sign_on, SMS_PREFERENCE_KEY);
        this.clScraperStatusMonitor = new ScraperStatusMonitor(R.string.sns_calllog_sign_on, CALLLOG_PREFERENCE_KEY);
        this.twitterScraperStatus.addStatusListener(this.twScraperStatusMonitor);
        this.gmailScraperStatus.addStatusListener(this.gmScraperStatusMonitor);
        this.smsScraperStatus.addStatusListener(this.smsScraperStatusMonitor);
        this.calllogScraperStatus.addStatusListener(this.clScraperStatusMonitor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ScraperStatusMonitor implements ScraperStatus.IStatusListener {
        private final int signInStringResId;
        private final String summaryPrefKey;

        public ScraperStatusMonitor(int signInStringResId, String summaryPrefKey) {
            this.summaryPrefKey = summaryPrefKey;
            this.signInStringResId = signInStringResId;
        }

        private ScraperStatus.StatusValue getStatusValue(ScraperStatus scraperStatus) {
            return ScraperStatus.parseStatus(SocialIntegrationPrefs.this.activity, scraperStatus.getScrapStatus(SocialIntegrationPrefs.this.activity));
        }

        @Override // com.nuance.sns.ScraperStatus.IStatusListener
        public void onStatusChange(final ScraperStatus status) {
            SocialIntegrationPrefs.this.activity.runOnUiThread(new Runnable() { // from class: com.nuance.swype.input.settings.SocialIntegrationPrefs.ScraperStatusMonitor.1
                @Override // java.lang.Runnable
                public void run() {
                    ScraperStatusMonitor.this.updateScraperStatus(status);
                }
            });
        }

        public void updateScraperStatus(ScraperStatus status) {
            ScraperStatus.StatusValue statusValue = getStatusValue(status);
            Preference pref = SocialIntegrationPrefs.this.findPreference(this.summaryPrefKey);
            if (pref != null) {
                pref.setSummary(getStatusSummary(statusValue));
            }
        }

        private String getStatusSummary(ScraperStatus.StatusValue status) {
            if (status != null && status.status != null) {
                switch (status.status) {
                    case NONE:
                    case FAILED:
                        return getSignOnStatus();
                    case WORKING:
                        return getWorkingStatus();
                    case FINISHED:
                        return getFinishedStatus(status.time);
                }
            }
            return getSignOnStatus();
        }

        private String getWorkingStatus() {
            return SocialIntegrationPrefs.this.activity.getString(R.string.sns_personalizing);
        }

        private String getFinishedStatus(String time) {
            return String.format(SocialIntegrationPrefs.this.activity.getString(R.string.sns_personalize_finished), time);
        }

        private String getSignOnStatus() {
            return SocialIntegrationPrefs.this.activity.getString(this.signInStringResId);
        }
    }
}
