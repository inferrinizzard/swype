package com.nuance.swype.stats;

import android.content.Context;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.Observer;
import com.nuance.swypeconnect.ac.ACReportingService;
import com.nuance.swypeconnect.ac.oem_62314.ACReportingLogHelperNuance;
import java.util.Date;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public abstract class AbstractScribe {
    private static final String NAME_SEPARATOR = "|";
    private static final String NUMBER_REPLACE = "#";
    private boolean allowedToSend;
    protected Context context;
    private String currentApplication;
    private int currentFieldInfo;
    private InputMethods.Language currentLanguage;
    private ScribeFilter filter;
    private final Observer statsContextObserver;
    private static final Pattern NUMBER_REMOVE_PATTERN = Pattern.compile("[0-9]");
    public static final LogManager.Log log = LogManager.getLog("Stats");

    public AbstractScribe() {
        this.allowedToSend = false;
        this.statsContextObserver = new Observer() { // from class: com.nuance.swype.stats.AbstractScribe.1
            @Override // com.nuance.swype.util.Observer
            public void update() {
                IMEApplication app = IMEApplication.from(AbstractScribe.this.context);
                if (app != null) {
                    AbstractScribe.this.currentApplication = app.getCurrentApplicationName();
                    AbstractScribe.this.currentFieldInfo = app.getCurrentFieldInfo();
                    AbstractScribe.this.currentLanguage = app.getCurrentLanguage();
                    AbstractScribe.this.filter = app.getScribeFilter();
                }
            }
        };
    }

    public AbstractScribe(Context ctx) {
        this.allowedToSend = false;
        this.statsContextObserver = new Observer() { // from class: com.nuance.swype.stats.AbstractScribe.1
            @Override // com.nuance.swype.util.Observer
            public void update() {
                IMEApplication app = IMEApplication.from(AbstractScribe.this.context);
                if (app != null) {
                    AbstractScribe.this.currentApplication = app.getCurrentApplicationName();
                    AbstractScribe.this.currentFieldInfo = app.getCurrentFieldInfo();
                    AbstractScribe.this.currentLanguage = app.getCurrentLanguage();
                    AbstractScribe.this.filter = app.getScribeFilter();
                }
            }
        };
        if (ctx != null) {
            this.context = ctx;
            this.allowedToSend = true;
            IMEApplication app = IMEApplication.from(ctx);
            app.registerContextObserver(this.statsContextObserver);
            this.currentApplication = app.getCurrentApplicationName();
            this.currentFieldInfo = app.getCurrentFieldInfo();
            this.currentLanguage = app.getCurrentLanguage();
            this.filter = app.getScribeFilter();
        }
    }

    public boolean allowedProcess(ACReportingService.ACDataPoints point) {
        return allowedProcess(point.toString());
    }

    private ACReportingService getReporting() {
        return Connect.from(this.context).getReportingService();
    }

    public boolean allowedProcess(String statistic) {
        if (!this.allowedToSend) {
            return false;
        }
        if (this.filter != null) {
            return this.filter.isDataPointAllowed(statistic);
        }
        return getReporting() != null && getReporting().isEntryAllowed(ACReportingService.ACDataPoints.SELECTION_LIST_CONTEXT.toString());
    }

    public void sendStat(ACReportingService.ACDataPoints point, String value) {
        sendStat(point, getDefaultName(), value);
    }

    public void sendStat(ACReportingService.ACDataPoints point, String name, String value) {
        sendStat(point, name, value, null);
    }

    public void sendStat(ACReportingService.ACDataPoints point, String name, String value, String extra) {
        if (getReporting() != null && point != null) {
            log.d("sendStat...ACDataPoints: ", point.toString());
            getReporting().getWriter().logPoint(point.toString(), name, value, (Date) null, extra);
        }
    }

    public void sendStatFiltered(ACReportingService.ACDataPoints point, String value) {
        sendStat(point, filterString(value));
    }

    public ACReportingLogHelperNuance getHelper() {
        if (getReporting() == null || !(getReporting().getHelper() instanceof ACReportingLogHelperNuance)) {
            return null;
        }
        return (ACReportingLogHelperNuance) getReporting().getHelper();
    }

    public ACReportingService.ACReportingIntervalTracker getTracker() {
        if (getReporting() != null) {
            return getReporting().getIntervalTracker();
        }
        return null;
    }

    public static String filterString(String content) {
        if (content == null) {
            return null;
        }
        return NUMBER_REMOVE_PATTERN.matcher(content).replaceAll(NUMBER_REPLACE);
    }

    protected void sendStatToConnectFiltered(ACReportingService.ACDataPoints point, String name, String value, String extra) {
        if (getReporting() != null && point != null) {
            getReporting().getWriter().logPoint(point.toString(), getDefaultName(), filterString(value), (Date) null, extra);
        }
    }

    protected String getDefaultName() {
        return this.currentApplication + NAME_SEPARATOR + this.currentLanguage;
    }

    protected String getDefaultExtra() {
        return String.valueOf(this.currentFieldInfo);
    }
}
