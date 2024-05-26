package com.nuance.swype.stats.basicanalytics;

import android.content.Context;
import com.nuance.swype.stats.AbstractScribe;
import com.nuance.swype.stats.StatisticsManager;

/* loaded from: classes.dex */
public class BasicStatisticsScribe extends AbstractScribe implements StatisticsManager.SessionStatsScribe {
    public BasicStatisticsScribe() {
    }

    public BasicStatisticsScribe(Context ctx) {
        super(ctx);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordLanguageChange(String name, String newLanguage) {
        log.v("recordLanguageChange newLanguage=" + newLanguage);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordSettingsChange(String setting, Object newValue, Object oldValue) {
        log.v("recordSettingsChange setting=" + setting + " newValue=" + newValue + " oldValue=" + oldValue);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void recordKeyboardSizeChange(String newSize) {
        log.v("recordKeyboardSizeChange newSize=" + newSize);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void trackDistanceSwyped(long length) {
        log.v("trackDistanceSwyped length=" + length);
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark() {
        log.v("mark()");
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark(int state) {
        log.v("mark(" + state + ")");
    }

    @Override // com.nuance.swype.stats.StatisticsManager.SessionStatsScribe
    public void mark(int state, int count) {
        log.v("mark(" + state + ", " + count + ")");
    }
}
