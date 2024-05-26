package com.nuance.swypeconnect.ac;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.api.ReportingService;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.common.Strings;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACLanguage;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public final class ACReportingService extends ACService {
    private static final boolean BUNDLE_AGGREGATE_TYPE = true;
    private static final boolean BUNDLE_INDIVIDUAL_TYPE = false;
    public static final int MAXIMUM_DATABASE_ENTRIES = 100000;
    private static final int MAX_TRANSMISSION_RETRIES = 3;
    public static final int MINIMUM_DATABASE_ENTRIES = 10000;
    public static final int REASON_DATA_POINT_IDENTIFIER_NOT_FOUND = 3000;
    public static final int REPORTING_CLEAR_USER_INITIATED = 1;
    public static final int REPORTING_LOG_FAILURE_DATABASE = 4;
    public static final int REPORTING_LOG_FAILURE_POINT_INVALID = 3;
    public static final int REPORTING_LOG_FAILURE_POINT_NOT_ALLOWED = 2;
    public static final int REPORTING_LOG_FAILURE_POINT_TOO_LARGE = 6;
    public static final int REPORTING_TRANSMISSION_FAILURE = 5;
    private final ConnectServiceManager connect;
    private final ACReportingLogHelper logHelper;
    private final ReportingService reportingService;
    private final ACReportingIntervalTracker tracker;
    private final ACReportingWriter writer;
    public static final int INPUT_TYPE_TAPPED = InputType.TAPPED.ordinal();
    public static final int INPUT_TYPE_SWYPED = InputType.SWYPED.ordinal();
    public static final int INPUT_TYPE_HANDWRITTEN = InputType.HANDWRITTEN.ordinal();
    public static final int INPUT_TYPE_SPOKEN = InputType.SPOKEN.ordinal();
    public static final int STRATEGY_RETRANSMIT = TransmitStrategy.RETRANSMIT.ordinal();
    public static final int STRATEGY_PERSIST = TransmitStrategy.PERSIST.ordinal();
    public static final int STRATEGY_PURGE = TransmitStrategy.PURGE.ordinal();
    private static final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final ACLanguage.Listener languageListener = new ACLanguage.Listener() { // from class: com.nuance.swypeconnect.ac.ACReportingService.1
        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onFinishInput(String str, ACLanguage.InputSessionState inputSessionState) {
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onLanguageChange(int[] iArr, int i) {
            ACReportingService.this.reportingService.setActiveLanguages(iArr);
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onLocale(Locale locale) {
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onStartInput(EditorInfo editorInfo) {
        }
    };
    private final ConcurrentCallbackSet<ACReportingCallback> callbacks = new ConcurrentCallbackSet<>();
    private TransmitStrategy currentStrategy = TransmitStrategy.PURGE;
    private final ReportingService.Callback serviceCallback = new ReportingService.Callback() { // from class: com.nuance.swypeconnect.ac.ACReportingService.2
        private int transmissionAttempt = 0;

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void allowedPoints(Set<String> set) {
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.allowedPoints(set);
            }
        }

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void onClear(int i) {
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.onClear(i);
            }
        }

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void onLoggingFailure(int i, String str, Bundle bundle) {
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.onLoggingFailure(i, str, ACReportingEntry.from(bundle));
            }
        }

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void onLoggingSuccess(Bundle bundle) {
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.onLoggingSuccess(ACReportingEntry.from(bundle));
            }
        }

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void onTransmission(int i) {
            ACReportingService.this.connect.updateFeatureLastUsed(FeaturesLastUsed.Feature.REPORTING, System.currentTimeMillis());
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.onTransmission(i);
            }
            this.transmissionAttempt = 0;
        }

        @Override // com.nuance.connect.api.ReportingService.Callback
        public void onTransmissionFailure(int i, String str) {
            for (ACReportingCallback aCReportingCallback : (ACReportingCallback[]) ACReportingService.this.callbacks.toArray(new ACReportingCallback[0])) {
                aCReportingCallback.onTransmissionFailure(i, str);
            }
            switch (AnonymousClass3.$SwitchMap$com$nuance$swypeconnect$ac$ACReportingService$TransmitStrategy[ACReportingService.this.currentStrategy.ordinal()]) {
                case 1:
                    int i2 = this.transmissionAttempt;
                    this.transmissionAttempt = i2 + 1;
                    if (i2 < 3) {
                        ACReportingService.this.sendData();
                        return;
                    } else {
                        ACReportingService.this.clearData();
                        this.transmissionAttempt = 0;
                        return;
                    }
                case 2:
                    return;
                default:
                    ACReportingService.this.clearData();
                    return;
            }
        }
    };

    /* renamed from: com.nuance.swypeconnect.ac.ACReportingService$3, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$swypeconnect$ac$ACReportingService$TransmitStrategy = new int[TransmitStrategy.values().length];

        static {
            try {
                $SwitchMap$com$nuance$swypeconnect$ac$ACReportingService$TransmitStrategy[TransmitStrategy.RETRANSMIT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$swypeconnect$ac$ACReportingService$TransmitStrategy[TransmitStrategy.PERSIST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$swypeconnect$ac$ACReportingService$TransmitStrategy[TransmitStrategy.PURGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public enum ACDataPoints {
        LANGUAGE_CHANGE("S001", ACReportingEntry.Type.DATAPOINT),
        SETTINGS_CHANGE("S002", ACReportingEntry.Type.DATAPOINT),
        LOCALE_CHANGE("S003", ACReportingEntry.Type.DATAPOINT),
        KEYBOARD_RESIZE("S004", ACReportingEntry.Type.DATAPOINT),
        WPM_SWYPED("S005", ACReportingEntry.Type.AGGREGATE),
        WPM_TAPPED("S006", ACReportingEntry.Type.AGGREGATE),
        WPM_SPOKEN("S007", ACReportingEntry.Type.AGGREGATE),
        WPM_HANDWRITTEN("S008", ACReportingEntry.Type.AGGREGATE),
        WORD_SWYPED_COUNT("S009", ACReportingEntry.Type.AGGREGATE),
        WORD_TAPPED_COUNT("S010", ACReportingEntry.Type.AGGREGATE),
        WORD_SPOKEN_COUNT("S011", ACReportingEntry.Type.AGGREGATE),
        WORD_HANDWRITTEN_COUNT("S012", ACReportingEntry.Type.AGGREGATE),
        KEYCODE_TAPPED("S013", ACReportingEntry.Type.DATAPOINT),
        TRACE_PATH("S014", ACReportingEntry.Type.DATAPOINT),
        ACTIVE_WORD("S015", ACReportingEntry.Type.DATAPOINT),
        DELETED_WORD("S016", ACReportingEntry.Type.DATAPOINT),
        KEYCODE_LONG("S017", ACReportingEntry.Type.DATAPOINT),
        COMPLETED_TEXT("S018", ACReportingEntry.Type.DATAPOINT),
        KEYBOARD_PAGE_CHANGE("S019", ACReportingEntry.Type.DATAPOINT),
        RECAPTURE("S020", ACReportingEntry.Type.DATAPOINT),
        UDB_ADD("S021", ACReportingEntry.Type.DATAPOINT),
        UDB_DELETE("S022", ACReportingEntry.Type.DATAPOINT),
        SELECTION_LIST_OPTIONS("S023", ACReportingEntry.Type.DATAPOINT),
        SELECTION_WORD("S024", ACReportingEntry.Type.DATAPOINT),
        SELECTION_LIST_CONTEXT("S025", ACReportingEntry.Type.DATAPOINT),
        SHIFT_STATE("S026", ACReportingEntry.Type.DATAPOINT),
        ALTERNATIVE_TEXT("S027", ACReportingEntry.Type.DATAPOINT),
        INLINE_TEXT("S028", ACReportingEntry.Type.DATAPOINT),
        SHIFT_MARGIN("S029", ACReportingEntry.Type.DATAPOINT),
        TEXT_BUFFER("S030", ACReportingEntry.Type.DATAPOINT),
        KEYBOARD_XML("S031", ACReportingEntry.Type.DATAPOINT),
        APPLICATION_LOGGING("S032", ACReportingEntry.Type.DATAPOINT),
        SEARCH_LOGGING("S033", ACReportingEntry.Type.DATAPOINT),
        COMMITTED_SENTENCE("S034", ACReportingEntry.Type.DATAPOINT),
        SET_CONTEXT("S035", ACReportingEntry.Type.DATAPOINT),
        TOTAL_SWYPE_DISTANCE("S036", ACReportingEntry.Type.AGGREGATE),
        GESTURE_TYPE("S037", ACReportingEntry.Type.DATAPOINT),
        INITIAL_LOCALE_SETTING("S038", ACReportingEntry.Type.DATAPOINT),
        SEVENGRAM_LOGGING("S039", ACReportingEntry.Type.DATAPOINT),
        PARTIAL_SEARCH("S040", ACReportingEntry.Type.DATAPOINT),
        SELECTION_LIST_DISPLAY("S041", ACReportingEntry.Type.DATAPOINT),
        WCL_USE_TRACKING("S048", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_1("S049", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_2("S050", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_3("S051", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_4("S052", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_5("S053", ACReportingEntry.Type.DATAPOINT),
        FUTURE_DATAPOINT_6("S054", ACReportingEntry.Type.DATAPOINT),
        FUTURE_AGGREGATE_1("S055", ACReportingEntry.Type.AGGREGATE),
        FUTURE_AGGREGATE_2("S056", ACReportingEntry.Type.AGGREGATE),
        FUTURE_AGGREGATE_3("S057", ACReportingEntry.Type.AGGREGATE),
        FUTURE_AGGREGATE_4("S058", ACReportingEntry.Type.AGGREGATE),
        FUTURE_AGGREGATE_5("S059", ACReportingEntry.Type.AGGREGATE);

        private static ACDataPoints[] values;
        private final String id;
        private final ACReportingEntry.Type type;

        ACDataPoints(String str, ACReportingEntry.Type type) {
            this.id = str;
            this.type = type;
        }

        public static ACDataPoints from(String str) {
            if (values == null) {
                values = values();
            }
            if (str == null) {
                return null;
            }
            for (ACDataPoints aCDataPoints : values) {
                if (aCDataPoints.id.equals(str)) {
                    return aCDataPoints;
                }
            }
            return null;
        }

        public static Set<String> getIdentifiers() {
            HashSet hashSet = new HashSet();
            Iterator it = Arrays.asList(values()).iterator();
            while (it.hasNext()) {
                hashSet.add(((ACDataPoints) it.next()).toString());
            }
            return hashSet;
        }

        public final ACReportingEntry.Type getType() {
            return this.type;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.id;
        }
    }

    /* loaded from: classes.dex */
    public static class ACReportingAggregate extends ACReportingEntry {
        private double interval;
        private double value;

        public ACReportingAggregate() {
            this(new Bundle());
        }

        ACReportingAggregate(Bundle bundle) {
            super(bundle);
            this.value = bundle.getDouble(Strings.STAT_POINT_VALUE);
            this.interval = bundle.getDouble(Strings.STAT_POINT_INTERVAL);
        }

        public double getInterval() {
            return this.interval;
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        public ACReportingEntry.Type getType() {
            return ACReportingEntry.Type.AGGREGATE;
        }

        public double getValue() {
            return this.value;
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        public int hashCode() {
            int hashCode = super.hashCode();
            long doubleToLongBits = Double.doubleToLongBits(this.value);
            long doubleToLongBits2 = Double.doubleToLongBits(this.interval);
            return (((hashCode * 31) + ((int) (doubleToLongBits ^ (doubleToLongBits >>> 32)))) * 31) + ((int) ((doubleToLongBits2 >>> 32) ^ doubleToLongBits2));
        }

        public void setInterval(double d) {
            this.interval = d;
        }

        public void setValue(double d) {
            this.value = d;
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, getIdentifier());
            bundle.putString(Strings.STAT_NAME, getName());
            bundle.putDouble(Strings.STAT_POINT_VALUE, this.value);
            bundle.putDouble(Strings.STAT_POINT_INTERVAL, this.interval);
            bundle.putString(Strings.STAT_EXTRA, getExtra());
            bundle.putBoolean(Strings.STAT_TYPE, true);
            bundle.putLong(Strings.STAT_TIMESTAMP, getTimestamp().getTime());
            return bundle;
        }
    }

    /* loaded from: classes.dex */
    public interface ACReportingCallback {
        void allowedPoints(Set<String> set);

        void onClear(int i);

        void onLoggingFailure(int i, String str, ACReportingEntry aCReportingEntry);

        void onLoggingSuccess(ACReportingEntry aCReportingEntry);

        void onTransmission(int i);

        int onTransmissionFailure(int i, String str);
    }

    /* loaded from: classes.dex */
    public static class ACReportingDatapoint extends ACReportingEntry {
        private String value;

        public ACReportingDatapoint() {
            this(new Bundle());
        }

        ACReportingDatapoint(Bundle bundle) {
            super(bundle);
            this.value = bundle.getString(Strings.STAT_VALUE);
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        public ACReportingEntry.Type getType() {
            return ACReportingEntry.Type.DATAPOINT;
        }

        public String getValue() {
            return this.value;
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        public int hashCode() {
            return (this.value == null ? 0 : this.value.hashCode()) + (super.hashCode() * 31);
        }

        public void setValue(String str) {
            this.value = str;
        }

        @Override // com.nuance.swypeconnect.ac.ACReportingService.ACReportingEntry
        Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, getIdentifier());
            bundle.putString(Strings.STAT_NAME, getName());
            bundle.putString(Strings.STAT_VALUE, getValue());
            bundle.putString(Strings.STAT_EXTRA, getExtra());
            bundle.putBoolean(Strings.STAT_TYPE, false);
            bundle.putLong(Strings.STAT_TIMESTAMP, getTimestamp().getTime());
            return bundle;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ACReportingEntry {
        private String extra;
        private String id;
        private String name;
        private long timestamp;

        /* loaded from: classes.dex */
        public enum Type {
            AGGREGATE,
            DATAPOINT
        }

        private ACReportingEntry(Bundle bundle) {
            this.id = bundle.getString(Strings.STAT_ID);
            this.name = bundle.getString(Strings.STAT_NAME);
            if (bundle.containsKey(Strings.STAT_EXTRA)) {
                this.extra = bundle.getString(Strings.STAT_EXTRA);
            } else {
                this.extra = null;
            }
            if (bundle.containsKey(Strings.STAT_TIMESTAMP)) {
                this.timestamp = bundle.getLong(Strings.STAT_TIMESTAMP);
            } else {
                this.timestamp = System.currentTimeMillis();
            }
        }

        static ACReportingEntry from(Bundle bundle) {
            if (bundle.containsKey(Strings.STAT_TYPE)) {
                return bundle.getBoolean(Strings.STAT_TYPE) ? new ACReportingAggregate(bundle) : new ACReportingDatapoint(bundle);
            }
            ACReportingService.oemLog.e("Could not create reporting entry from bundle: " + bundle);
            return null;
        }

        public boolean equals(Object obj) {
            ACReportingService.oemLog.d("equals this=", this, ", o=", obj);
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ACReportingEntry)) {
                return false;
            }
            Bundle bundle = toBundle();
            Bundle bundle2 = ((ACReportingEntry) obj).toBundle();
            if (bundle.size() != bundle2.size()) {
                ACReportingService.oemLog.d("different key quantities");
                return false;
            }
            for (String str : bundle.keySet()) {
                Object obj2 = bundle.get(str);
                Object obj3 = bundle2.get(str);
                if (obj2 == null) {
                    if (obj3 != null || !bundle2.containsKey(str)) {
                        return false;
                    }
                } else if (!obj2.equals(obj3)) {
                    return false;
                }
            }
            return true;
        }

        public String getExtra() {
            return this.extra;
        }

        public String getIdentifier() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Date getTimestamp() {
            return new Date(this.timestamp);
        }

        public abstract Type getType();

        public int hashCode() {
            return (((((this.name == null ? 0 : this.name.hashCode()) + (((this.id == null ? 0 : this.id.hashCode()) + 527) * 31)) * 31) + (this.extra != null ? this.extra.hashCode() : 0)) * 31) + ((int) (this.timestamp ^ (this.timestamp >>> 32)));
        }

        public void setExtra(String str) {
            this.extra = str;
        }

        public void setIdentifier(String str) {
            this.id = str;
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setTimestamp(Date date) {
            if (date != null) {
                this.timestamp = date.getTime();
            } else {
                this.timestamp = 0L;
            }
        }

        abstract Bundle toBundle();
    }

    /* loaded from: classes.dex */
    public static final class ACReportingIntervalTracker {
        private final ACReportingLogHelper helper;

        ACReportingIntervalTracker(ACReportingLogHelper aCReportingLogHelper) {
            this.helper = aCReportingLogHelper;
        }

        public final void mark() {
            this.helper.mark();
        }

        public final void mark(int i) {
            this.helper.mark(i);
        }

        public final void mark(int i, int i2) {
            this.helper.mark(i, i2);
        }
    }

    /* loaded from: classes.dex */
    public class ACReportingWriter {
        private ACReportingWriter() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isEntryAllowed(ACDataPoints aCDataPoints) {
            return ACReportingService.this.isEntryAllowed(aCDataPoints.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isReportingEnabled() {
            return ACReportingService.this.reportingService.isEnabled();
        }

        public void logAggregate(String str, String str2, double d, double d2, Date date, String str3) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, str);
            bundle.putString(Strings.STAT_NAME, str2);
            bundle.putDouble(Strings.STAT_POINT_VALUE, d);
            bundle.putDouble(Strings.STAT_POINT_INTERVAL, d2);
            bundle.putString(Strings.STAT_EXTRA, str3);
            bundle.putBoolean(Strings.STAT_TYPE, true);
            bundle.putLong(Strings.STAT_TIMESTAMP, date == null ? System.currentTimeMillis() : date.getTime());
            ACReportingService.this.reportingService.log(bundle);
        }

        public void logPoint(String str, String str2, double d, Date date, String str3) {
            logPoint(str, str2, String.valueOf(d), date, str3);
        }

        public void logPoint(String str, String str2, int i, Date date, String str3) {
            logPoint(str, str2, String.valueOf(i), date, str3);
        }

        public void logPoint(String str, String str2, long j, Date date, String str3) {
            logPoint(str, str2, String.valueOf(j), date, str3);
        }

        public void logPoint(String str, String str2, String str3, Date date, String str4) {
            logPoint(str, str2, str3, date, str4, false);
        }

        public void logPoint(String str, String str2, String str3, Date date, String str4, boolean z) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, str);
            bundle.putString(Strings.STAT_NAME, str2);
            bundle.putString(Strings.STAT_VALUE, str3);
            bundle.putString(Strings.STAT_EXTRA, str4);
            bundle.putBoolean(Strings.STAT_TYPE, false);
            bundle.putLong(Strings.STAT_TIMESTAMP, date == null ? System.currentTimeMillis() : date.getTime());
            bundle.putBoolean(Strings.STAT_RECORD_LANGUAGE, z);
            ACReportingService.this.reportingService.log(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum InputType {
        TAPPED,
        SWYPED,
        HANDWRITTEN,
        SPOKEN,
        UNKNOWN;

        public static InputType from(int i) {
            return (i >= UNKNOWN.ordinal() || i < TAPPED.ordinal()) ? UNKNOWN : values()[i];
        }
    }

    /* loaded from: classes.dex */
    enum TransmitStrategy {
        RETRANSMIT,
        PERSIST,
        PURGE;

        public static TransmitStrategy from(int i) {
            return (i >= PURGE.ordinal() || i < RETRANSMIT.ordinal()) ? PURGE : values()[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACReportingService(ACManager aCManager, ReportingService reportingService, String str, PersistentDataStore persistentDataStore) {
        ACReportingLogHelper aCReportingLogHelper;
        this.connect = aCManager.getConnect();
        this.reportingService = reportingService;
        this.reportingService.registerDataPoints(ACDataPoints.getIdentifiers());
        this.writer = new ACReportingWriter();
        try {
            aCReportingLogHelper = (ACReportingLogHelper) Class.forName(str).newInstance();
        } catch (ClassNotFoundException e) {
            aCReportingLogHelper = null;
        } catch (IllegalAccessException e2) {
            aCReportingLogHelper = null;
        } catch (IllegalArgumentException e3) {
            aCReportingLogHelper = null;
        } catch (InstantiationException e4) {
            aCReportingLogHelper = null;
        }
        aCReportingLogHelper = aCReportingLogHelper == null ? new ACReportingLogHelper() : aCReportingLogHelper;
        aCReportingLogHelper.init(this.writer, aCManager.getLanguageSettings());
        this.logHelper = aCReportingLogHelper;
        this.tracker = new ACReportingIntervalTracker(this.logHelper);
        if (this.reportingService.isEnabled()) {
            this.logHelper.enable();
        }
        ACLanguage languageSettings = aCManager.getLanguageSettings();
        if (languageSettings != null) {
            languageSettings.registerListener(this.languageListener);
            this.reportingService.setActiveLanguages(languageSettings.getActiveLanguages());
        }
        this.reportingService.registerCallback(this.serviceCallback);
    }

    public final void clearData() {
        this.reportingService.clearData();
    }

    public final void disableReporting() {
        this.logHelper.disable();
        this.reportingService.disableReporting();
    }

    public final void enableReporting() {
        this.reportingService.enableReporting();
        this.logHelper.enable();
    }

    public final ACReportingLogHelper getHelper() {
        return this.logHelper;
    }

    public final ACReportingIntervalTracker getIntervalTracker() {
        return this.tracker;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.REPORTING_SERVICE;
    }

    public final ACReportingWriter getWriter() {
        return this.writer;
    }

    public final boolean isEnabled() {
        return this.reportingService.isEnabled();
    }

    public final boolean isEntryAllowed(ACReportingEntry aCReportingEntry) {
        return isEntryAllowed(aCReportingEntry.getIdentifier());
    }

    public final boolean isEntryAllowed(String str) {
        return this.reportingService.isPointAllowed(str);
    }

    public final void log(ACReportingEntry aCReportingEntry) {
        this.reportingService.log(aCReportingEntry.toBundle());
    }

    public final void registerCallback(ACReportingCallback aCReportingCallback) {
        if (aCReportingCallback != null) {
            this.callbacks.add(aCReportingCallback);
        }
    }

    public final void registerDataPoints(Set<String> set) throws ACException {
        if (set != null) {
            for (String str : set) {
                if (ACDataPoints.from(str) == null) {
                    throw new ACException(REASON_DATA_POINT_IDENTIFIER_NOT_FOUND, "Data point is unknown: " + str);
                }
            }
        }
        this.reportingService.registerDataPoints(set);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 1 || i == 4;
    }

    public final void sendData() {
        this.reportingService.sendData();
    }

    public final void setMaxEntries(int i) throws ACException {
        if (i < 10000 || i > 100000) {
            throw new ACException(131, "Max entries was set outside of accepted parameters.  Entries: " + i);
        }
        this.reportingService.setMaxIndividualEntries(i);
    }

    public final void setTransmissionFailureStrategy(int i) {
        this.currentStrategy = TransmitStrategy.from(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
    }

    public final void unregisterCallback(ACReportingCallback aCReportingCallback) {
        this.callbacks.remove(aCReportingCallback);
    }

    public final void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
