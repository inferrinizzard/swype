package com.nuance.sns;

import android.content.Context;
import android.content.SharedPreferences;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class ScraperStatus {
    private static final String CALLLOG_STATUS_PREF = "calllog_status_pref";
    private static final String GMAIL_STATUS_PREF = "gmail_status_pref";
    private static final String SCRAPING_STATUS = "scraping_status";
    public static final String SCRAPING_STATUS_FAILED = "failed";
    public static final String SCRAPING_STATUS_FINISHED = "finished";
    public static final String SCRAPING_STATUS_NONE = "none";
    public static final String SCRAPING_STATUS_WORKING = "working";
    private static final String SMS_STATUS_PREF = "sms_calllog_status_pref";
    private static final String STATUS_VALUE_SEPERATOR = ":";
    private static final String TWITTER_STATUS_PREF = "twitter_status_pref";
    private final String pref;
    private final StatusObserver statusObserver;

    /* loaded from: classes.dex */
    public interface IStatusListener {
        void onStatusChange(ScraperStatus scraperStatus);
    }

    /* loaded from: classes.dex */
    public enum STATUS {
        WORKING,
        FINISHED,
        FAILED,
        NONE
    }

    private ScraperStatus(String pref) {
        this.statusObserver = new StatusObserver();
        this.pref = pref;
    }

    public synchronized String getScrapStatus(Context context) {
        return getSharedPreferences(context).getString(SCRAPING_STATUS, SCRAPING_STATUS_NONE);
    }

    public synchronized void setScrapStatus(Context context, String status) {
        SharedPreferencesEditorCompat.apply(getSharedPreferences(context).edit().putString(SCRAPING_STATUS, status));
        this.statusObserver.notifyStatusChange(this);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(this.pref, 0);
    }

    public void addStatusListener(IStatusListener listener) {
        this.statusObserver.addListener(listener);
    }

    public void removeStatusListener(IStatusListener listener) {
        this.statusObserver.removeStatusListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearStatus(Context context) {
        SharedPreferencesEditorCompat.apply(context.getSharedPreferences(this.pref, 0).edit().clear());
    }

    public static String formatStatusValue(String status, String time) {
        return status + STATUS_VALUE_SEPERATOR + time;
    }

    private static String formatTime(Locale locale, String millis) {
        Date date;
        try {
            date = new Date(Long.decode(millis).longValue());
        } catch (NumberFormatException e) {
            date = new Date();
        }
        return new SimpleDateFormat("EEE, MMM d, yyyy", locale).format(date);
    }

    public static StatusValue parseStatus(Context context, String status) {
        String[] values = status.split(STATUS_VALUE_SEPERATOR);
        if (values.length == 2) {
            Locale locale = context.getResources().getConfiguration().locale;
            if (SCRAPING_STATUS_FINISHED.equals(values[0])) {
                return new StatusValue(STATUS.FINISHED, formatTime(locale, values[1]));
            }
            if (SCRAPING_STATUS_WORKING.equals(values[0])) {
                return new StatusValue(STATUS.WORKING, formatTime(locale, values[1]));
            }
            if (SCRAPING_STATUS_FAILED.equals(values[0])) {
                return new StatusValue(STATUS.FAILED, formatTime(locale, values[1]));
            }
        }
        return new StatusValue(STATUS.NONE, "");
    }

    /* loaded from: classes.dex */
    public static class StatusValue {
        public final STATUS status;
        public final String time;

        StatusValue(STATUS s, String t) {
            this.status = s;
            this.time = t;
        }
    }

    /* loaded from: classes.dex */
    public static class StatusObserver {
        private final List<IStatusListener> subscribers = new CopyOnWriteArrayList();

        /* JADX INFO: Access modifiers changed from: private */
        public void addListener(IStatusListener listener) {
            removeStatusListener(listener);
            this.subscribers.add(listener);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeStatusListener(IStatusListener listener) {
            this.subscribers.remove(listener);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyStatusChange(ScraperStatus scraperStatus) {
            Iterator<IStatusListener> it = this.subscribers.iterator();
            while (it.hasNext()) {
                it.next().onStatusChange(scraperStatus);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ScraperStatusFactory {
        private ScraperStatus calllogScraperStatus;
        private ScraperStatus gmailScraperStatus;
        private ScraperStatus smsScraperStatus;
        private ScraperStatus twitterScraperStatus;

        public synchronized ScraperStatus getTwitterStatusPreference() {
            if (this.twitterScraperStatus == null) {
                this.twitterScraperStatus = new ScraperStatus(ScraperStatus.TWITTER_STATUS_PREF);
            }
            return this.twitterScraperStatus;
        }

        public synchronized ScraperStatus getGmailStatusPreference() {
            if (this.gmailScraperStatus == null) {
                this.gmailScraperStatus = new ScraperStatus(ScraperStatus.GMAIL_STATUS_PREF);
            }
            return this.gmailScraperStatus;
        }

        public synchronized ScraperStatus getSMSStatusPreference() {
            if (this.smsScraperStatus == null) {
                this.smsScraperStatus = new ScraperStatus(ScraperStatus.SMS_STATUS_PREF);
            }
            return this.smsScraperStatus;
        }

        public synchronized ScraperStatus getCalllogScraperStatus() {
            if (this.calllogScraperStatus == null) {
                this.calllogScraperStatus = new ScraperStatus(ScraperStatus.CALLLOG_STATUS_PREF);
            }
            return this.calllogScraperStatus;
        }

        public void reset(Context context) {
            getTwitterStatusPreference().clearStatus(context);
            getSMSStatusPreference().clearStatus(context);
        }
    }
}
