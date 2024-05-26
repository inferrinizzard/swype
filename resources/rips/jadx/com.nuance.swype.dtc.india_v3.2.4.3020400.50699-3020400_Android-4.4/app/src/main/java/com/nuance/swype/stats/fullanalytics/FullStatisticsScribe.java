package com.nuance.swype.stats.fullanalytics;

import android.content.Context;
import com.nuance.swype.stats.AbstractScribe;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swypeconnect.ac.ACReportingLogHelper;
import com.nuance.swypeconnect.ac.ACReportingService;

/* loaded from: classes.dex */
public class FullStatisticsScribe extends AbstractScribe implements StatisticsManager.SessionStatsScribe {
    public FullStatisticsScribe() {
    }

    public FullStatisticsScribe(Context context) {
        super(context);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordLanguageChange(String name, String newLanguage) {
        if (allowedProcess(ACReportingService.ACDataPoints.LANGUAGE_CHANGE)) {
            sendStat(ACReportingService.ACDataPoints.LANGUAGE_CHANGE, name, newLanguage);
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordSettingsChange(String setting, Object newValue, Object oldValue) {
        if (allowedProcess(ACReportingService.ACDataPoints.SETTINGS_CHANGE)) {
            sendStat(ACReportingService.ACDataPoints.SETTINGS_CHANGE, setting, "after:" + newValue.toString(), "before:" + oldValue.toString());
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordKeyboardSizeChange(String newSize) {
        if (allowedProcess(ACReportingService.ACDataPoints.KEYBOARD_RESIZE)) {
            sendStat(ACReportingService.ACDataPoints.KEYBOARD_RESIZE, "KEYBOARD_SIZE", newSize);
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void trackDistanceSwyped(long distance) {
        ACReportingLogHelper helper = getHelper();
        if (helper != null && allowedProcess(ACReportingService.ACDataPoints.TOTAL_SWYPE_DISTANCE)) {
            log.v("recordTotalSwypeDistance distance=" + distance);
            helper.trackDistanceSwype(distance);
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark() {
        ACReportingService.ACReportingIntervalTracker t = getTracker();
        if (t != null) {
            t.mark();
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark(int state) {
        ACReportingService.ACReportingIntervalTracker t = getTracker();
        if (t != null) {
            t.mark(state);
        }
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark(int state, int count) {
        ACReportingService.ACReportingIntervalTracker t = getTracker();
        if (t != null) {
            t.mark(state, count);
        }
    }
}
