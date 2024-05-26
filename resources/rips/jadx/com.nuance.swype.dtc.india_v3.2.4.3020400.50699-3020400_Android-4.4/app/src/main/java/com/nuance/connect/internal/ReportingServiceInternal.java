package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.nuance.connect.api.ReportingService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.host.service.BuildSettings;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.sqlite.ReportingDataSource;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ReportingServiceInternal extends AbstractService implements ReportingService {
    private static final int MAX_POINT_SIZE = 1024;
    private static final String REPORTING_AGGREGATE_COUNT = "REPORTING_AGGREGATE_COUNT";
    private static final String REPORTING_INDIVIDUAL_COUNT = "REPORTING_INDIVIDUAL_COUNT";
    private static final String REPORTING_MAXIMUM_SIZE = "REPORTING_MAXIMUM_SIZE";
    private final ConnectServiceManagerInternal connectService;
    private final ReportingDataSource database;
    private boolean reportingEnabled;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ReportingService.class.getSimpleName());
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_SET_ALLOWED_REPORTING_METRICS, InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_SUCCESS, InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_FAILURE};
    private final ConcurrentCallbackSet<ReportingService.Callback> callbacks = new ConcurrentCallbackSet<>();
    private final Set<String> allowedUserPoints = new HashSet();
    private final HashSet<String> allowedPoints = new HashSet<>();
    private Set<String> allowedServerPoints = null;
    private final HashMap<String, Integer> customDatapointSizes = new HashMap<>();
    private volatile String activeLanguages = "";
    private final ReportingDataSource.Callback databaseCallback = new ReportingDataSource.Callback() { // from class: com.nuance.connect.internal.ReportingServiceInternal.1
        void notifyCallbacks(boolean z, Bundle bundle) {
            if (z) {
                ReportingServiceInternal.this.notifyCallbacksOfLoggingSuccess(bundle);
            } else {
                ReportingServiceInternal.this.notifyCallbacksOfLoggingFailure(4, "Writing to the staging database failed", bundle);
            }
        }

        @Override // com.nuance.connect.sqlite.ReportingDataSource.Callback
        public void onAggregate(boolean z, String str, String str2, double d, double d2, String str3, long j) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, str);
            bundle.putString(Strings.STAT_NAME, str2);
            bundle.putDouble(Strings.STAT_POINT_VALUE, d);
            bundle.putDouble(Strings.STAT_POINT_INTERVAL, d2);
            bundle.putString(Strings.STAT_EXTRA, str3);
            bundle.putBoolean(Strings.STAT_TYPE, true);
            bundle.putLong(Strings.STAT_TIMESTAMP, j);
            notifyCallbacks(z, bundle);
        }

        @Override // com.nuance.connect.sqlite.ReportingDataSource.Callback
        public void onIndividual(boolean z, String str, String str2, String str3, String str4, long j) {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.STAT_ID, str);
            bundle.putString(Strings.STAT_NAME, str2);
            bundle.putString(Strings.STAT_VALUE, str3);
            bundle.putString(Strings.STAT_EXTRA, str4);
            bundle.putBoolean(Strings.STAT_TYPE, false);
            bundle.putLong(Strings.STAT_TIMESTAMP, j);
            notifyCallbacks(z, bundle);
        }
    };
    private final ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.ReportingServiceInternal.2
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.REPORTING_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[ReportingServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < ReportingServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = ReportingServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            int i = 0;
            switch (AnonymousClass3.$SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.fromInt(message.what).ordinal()]) {
                case 1:
                    ReportingServiceInternal.log.d("MESSAGE_HOST_SET_ALLOWED_REPORTING_METRICS");
                    synchronized (ReportingServiceInternal.this.allowedPoints) {
                        ReportingServiceInternal.this.allowedServerPoints = (HashSet) message.getData().getSerializable(Strings.DEFAULT_KEY);
                    }
                    ReportingServiceInternal.this.recalculateAllowedDataPoints();
                    return;
                case 2:
                    ReportingServiceInternal.log.d("MESSAGE_HOST_REPORTING_TRANSMISSION_SUCCESS");
                    int i2 = message.getData().getInt(Strings.DEFAULT_KEY);
                    ReportingService.Callback[] loopableCallbackSet = ReportingServiceInternal.this.getLoopableCallbackSet();
                    int length = loopableCallbackSet.length;
                    while (i < length) {
                        loopableCallbackSet[i].onTransmission(i2);
                        i++;
                    }
                    return;
                case 3:
                    ReportingServiceInternal.log.d("MESSAGE_HOST_REPORTING_TRANSMISSION_FAILURE");
                    int i3 = message.getData().getInt(Strings.DEFAULT_KEY);
                    ReportingService.Callback[] loopableCallbackSet2 = ReportingServiceInternal.this.getLoopableCallbackSet();
                    int length2 = loopableCallbackSet2.length;
                    while (i < length2) {
                        loopableCallbackSet2[i].onTransmissionFailure(i3, "");
                        i++;
                    }
                    return;
                default:
                    ReportingServiceInternal.log.e("ReportingServiceInternal - caught unexpected message: ", InternalMessages.fromInt(message.what).name());
                    return;
            }
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* renamed from: com.nuance.connect.internal.ReportingServiceInternal$3, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];

        static {
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_SET_ALLOWED_REPORTING_METRICS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_SUCCESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_REPORTING_TRANSMISSION_FAILURE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReportingServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
        int readInt = connectServiceManagerInternal.getDataManager().getDataStore().readInt(REPORTING_AGGREGATE_COUNT, 50000);
        int readInt2 = connectServiceManagerInternal.getDataManager().getDataStore().readInt(REPORTING_INDIVIDUAL_COUNT, 50000);
        long readLong = connectServiceManagerInternal.getDataManager().getDataStore().readLong(REPORTING_MAXIMUM_SIZE, -1L);
        Iterator<String> it = StringUtils.stringToList(((BuildSettings) connectServiceManagerInternal.getHostService(HostInterface.HostService.HOST_BUILD_SETTINGS)).getCustomDatapointSizes(), ",").iterator();
        while (it.hasNext()) {
            String[] split = it.next().split(":");
            if (split.length == 2) {
                this.customDatapointSizes.put(split[0], Integer.valueOf(split[1]));
            }
        }
        this.database = new ReportingDataSource(this.connectService.getContext(), readInt2, readInt, readLong);
        this.database.registerCallback(this.databaseCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ReportingService.Callback[] getLoopableCallbackSet() {
        return (ReportingService.Callback[]) this.callbacks.toArray(new ReportingService.Callback[0]);
    }

    private int getMaxPointSize(String str) {
        if (this.customDatapointSizes.containsKey(str)) {
            return Math.max(this.customDatapointSizes.get(str).intValue(), 1024);
        }
        return 1024;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacksOfLoggingFailure(int i, String str, Bundle bundle) {
        for (ReportingService.Callback callback : getLoopableCallbackSet()) {
            callback.onLoggingFailure(i, str, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacksOfLoggingSuccess(Bundle bundle) {
        for (ReportingService.Callback callback : getLoopableCallbackSet()) {
            callback.onLoggingSuccess(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recalculateAllowedDataPoints() {
        boolean z;
        HashSet hashSet = new HashSet();
        synchronized (this.allowedUserPoints) {
            hashSet.addAll(this.allowedUserPoints);
        }
        synchronized (this.allowedPoints) {
            HashSet hashSet2 = new HashSet(this.allowedPoints);
            this.allowedPoints.clear();
            this.allowedPoints.addAll(hashSet);
            if (this.allowedServerPoints != null) {
                this.allowedPoints.retainAll(this.allowedServerPoints);
            }
            z = !hashSet2.equals(this.allowedPoints);
        }
        if (z) {
            Set<String> allowedPoints = allowedPoints();
            for (ReportingService.Callback callback : getLoopableCallbackSet()) {
                callback.allowedPoints(allowedPoints);
            }
        }
    }

    @Override // com.nuance.connect.api.ReportingService
    public Set<String> allowedPoints() {
        Set<String> set;
        synchronized (this.allowedPoints) {
            set = (Set) this.allowedPoints.clone();
        }
        return set;
    }

    @Override // com.nuance.connect.api.ReportingService
    public void clearData() {
        this.database.clearAggregate(System.currentTimeMillis());
        this.database.clearIndividual(System.currentTimeMillis());
        for (ReportingService.Callback callback : getLoopableCallbackSet()) {
            callback.onClear(1);
        }
    }

    @Override // com.nuance.connect.api.ReportingService
    public void disableReporting() {
        this.reportingEnabled = false;
    }

    @Override // com.nuance.connect.api.ReportingService
    public void enableReporting() {
        this.reportingEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.REPORTING.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.ReportingService
    public int getMaxIndividualEntries() {
        return this.database.getMaxIndividualEntries();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.REPORTING.name();
    }

    @Override // com.nuance.connect.api.ReportingService
    public boolean isEnabled() {
        return this.reportingEnabled;
    }

    @Override // com.nuance.connect.api.ReportingService
    public boolean isPointAllowed(String str) {
        boolean contains;
        synchronized (this.allowedPoints) {
            contains = this.allowedPoints.contains(str);
        }
        return contains;
    }

    @Override // com.nuance.connect.api.ReportingService
    public void log(Bundle bundle) {
        if (!this.reportingEnabled) {
            log.d("Logging is not enabled");
            return;
        }
        if (bundle == null) {
            log.d("point not valid: null");
            notifyCallbacksOfLoggingFailure(3, "point invalid: null", null);
            return;
        }
        if (!isPointAllowed(bundle.getString(Strings.STAT_ID))) {
            log.d("point not allowed: ", bundle);
            notifyCallbacksOfLoggingFailure(2, "point not allowed: " + bundle, bundle);
            return;
        }
        if (bundle.getBoolean(Strings.STAT_TYPE)) {
            String string = bundle.getString(Strings.STAT_EXTRA);
            int length = String.valueOf(string).length();
            if (length > getMaxPointSize(bundle.getString(Strings.STAT_ID))) {
                notifyCallbacksOfLoggingFailure(6, "point too large. size: " + length + " point: " + bundle, bundle);
                return;
            }
            String string2 = bundle.getString(Strings.STAT_ID);
            if (string2 != null && !string2.isEmpty()) {
                this.database.createAggregatePoint(string2, bundle.getString(Strings.STAT_NAME) + "|" + this.activeLanguages, bundle.getDouble(Strings.STAT_POINT_VALUE), bundle.getDouble(Strings.STAT_POINT_INTERVAL), string, bundle.getLong(Strings.STAT_TIMESTAMP));
                return;
            } else {
                log.d("point not valid: empty aggregate values");
                notifyCallbacksOfLoggingFailure(3, "point invalid: null or empty values", bundle);
                return;
            }
        }
        String string3 = bundle.getString(Strings.STAT_EXTRA);
        String string4 = bundle.getString(Strings.STAT_VALUE);
        int length2 = String.valueOf(string4).length() + String.valueOf(string3).length();
        if (length2 > getMaxPointSize(bundle.getString(Strings.STAT_ID))) {
            notifyCallbacksOfLoggingFailure(6, "point too large. size: " + length2 + " point: " + bundle.getString(Strings.STAT_ID), bundle);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bundle.getString(Strings.STAT_NAME));
        if (bundle.getBoolean(Strings.STAT_RECORD_LANGUAGE, false)) {
            sb.append("|");
            sb.append(this.activeLanguages);
        }
        String string5 = bundle.getString(Strings.STAT_ID);
        String sb2 = sb.toString();
        if (string5 != null && !string5.isEmpty() && sb2 != null && !sb2.isEmpty() && string4 != null && !string4.isEmpty()) {
            this.database.createIndividualPoint(string5, sb2, string4, string3, bundle.getLong(Strings.STAT_TIMESTAMP));
        } else {
            log.d("point not valid: empty datapoint values");
            notifyCallbacksOfLoggingFailure(3, "point invalid: null or empty values", bundle);
        }
    }

    @Override // com.nuance.connect.api.ReportingService
    public void registerCallback(ReportingService.Callback callback) {
        if (callback == null) {
            log.e("registerCallback called with null");
        } else {
            this.callbacks.add(callback);
        }
    }

    @Override // com.nuance.connect.api.ReportingService
    public void registerDataPoints(Set<String> set) {
        synchronized (this.allowedUserPoints) {
            this.allowedUserPoints.clear();
            if (set != null) {
                this.allowedUserPoints.addAll(set);
            }
        }
        recalculateAllowedDataPoints();
    }

    @Override // com.nuance.connect.api.ReportingService
    public void sendData() {
        log.d("sendData");
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_SEND_REPORTING_NOW);
    }

    @Override // com.nuance.connect.api.ReportingService
    public void setActiveLanguages(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        if (iArr == null) {
            iArr = new int[0];
        }
        for (int i : iArr) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(i);
        }
        this.activeLanguages = sb.toString();
    }

    @Override // com.nuance.connect.api.ReportingService
    public void setMaxIndividualEntries(int i) {
        this.connectService.getDataManager().getDataStore().saveInt(REPORTING_INDIVIDUAL_COUNT, i);
        this.database.setMaxIndividualEntries(i);
    }

    @Override // com.nuance.connect.api.ReportingService
    public void unregisterCallback(ReportingService.Callback callback) {
        if (callback == null) {
            log.e("unregisterCallback called with null");
        } else {
            this.callbacks.remove(callback);
        }
    }

    @Override // com.nuance.connect.api.ReportingService
    public void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
