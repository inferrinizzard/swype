package com.nuance.swype.stats;

import android.content.Context;
import com.nuance.swype.input.IMEApplication;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public class StatisticsManager {
    private static final String EMPTY_STATISTICS_SCRIBE = "com.nuance.swype.stats.basicanalytics.BasicStatisticsScribe";
    private static final String FULL_STATISTICS_SCRIBE = "com.nuance.swype.stats.fullanalytics.FullStatisticsScribe";
    protected static final String TAG = "Stats";
    private SessionStatsScribe sessionScribe;

    /* loaded from: classes.dex */
    public interface SessionStatsScribe {
        void mark();

        void mark(int i);

        void mark(int i, int i2);

        void recordKeyboardSizeChange(String str);

        void recordLanguageChange(String str, String str2);

        void recordSettingsChange(String str, Object obj, Object obj2);

        void trackDistanceSwyped(long j);
    }

    public static StatisticsManager from(Context ctx) {
        return ((IMEApplication) ctx.getApplicationContext()).getStatisticsManager();
    }

    public StatisticsManager(Context ctx) {
        if (!loadScribe(FULL_STATISTICS_SCRIBE, ctx)) {
            loadScribe(EMPTY_STATISTICS_SCRIBE, null);
        }
    }

    private boolean loadScribe(String scribeClass, Context ctx) {
        try {
            Class<? extends U> asSubclass = Class.forName(scribeClass).asSubclass(AbstractScribe.class);
            asSubclass.asSubclass(SessionStatsScribe.class);
            this.sessionScribe = (SessionStatsScribe) (ctx == null ? (AbstractScribe) asSubclass.getConstructor(new Class[0]).newInstance(new Object[0]) : (AbstractScribe) asSubclass.getConstructor(Context.class).newInstance(ctx));
            return true;
        } catch (ClassCastException e) {
            return false;
        } catch (ClassNotFoundException e2) {
            return false;
        } catch (IllegalAccessException e3) {
            return false;
        } catch (InstantiationException e4) {
            return false;
        } catch (NoSuchMethodException e5) {
            return false;
        } catch (InvocationTargetException e6) {
            return false;
        }
    }

    public static SessionStatsScribe getSessionStatsScribe(Context ctx) {
        StatisticsManager manager = ((IMEApplication) ctx.getApplicationContext()).getStatisticsManager();
        if (manager == null) {
            return null;
        }
        SessionStatsScribe scribe = manager.getSessionStatsScribe();
        return scribe;
    }

    public SessionStatsScribe getSessionStatsScribe() {
        return this.sessionScribe;
    }
}
