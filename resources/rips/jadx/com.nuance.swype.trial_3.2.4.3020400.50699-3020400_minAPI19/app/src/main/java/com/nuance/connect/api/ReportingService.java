package com.nuance.connect.api;

import android.os.Bundle;
import java.util.Set;

/* loaded from: classes.dex */
public interface ReportingService {

    /* loaded from: classes.dex */
    public interface Callback {
        void allowedPoints(Set<String> set);

        void onClear(int i);

        void onLoggingFailure(int i, String str, Bundle bundle);

        void onLoggingSuccess(Bundle bundle);

        void onTransmission(int i);

        void onTransmissionFailure(int i, String str);
    }

    Set<String> allowedPoints();

    void clearData();

    void disableReporting();

    void enableReporting();

    int getMaxIndividualEntries();

    boolean isEnabled();

    boolean isPointAllowed(String str);

    void log(Bundle bundle);

    void registerCallback(Callback callback);

    void registerDataPoints(Set<String> set);

    void sendData();

    void setActiveLanguages(int[] iArr);

    void setMaxIndividualEntries(int i);

    void unregisterCallback(Callback callback);

    void unregisterCallbacks();
}
