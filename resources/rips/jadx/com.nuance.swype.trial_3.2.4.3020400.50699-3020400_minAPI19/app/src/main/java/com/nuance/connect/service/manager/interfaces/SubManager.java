package com.nuance.connect.service.manager.interfaces;

import android.os.Bundle;
import com.nuance.connect.comm.Transaction;
import com.nuance.connect.util.VersionUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

/* loaded from: classes.dex */
public interface SubManager {
    void alarmNotification(String str, Bundle bundle);

    int categoriesManagedCount();

    Transaction createSubscribeTransaction(String str);

    int getManagerPollInterval();

    String getName();

    List<Integer> getTypesSupported();

    void init();

    boolean isEnabled();

    void languageUpdated(int[] iArr, Set<Integer> set);

    void localeUpdated(Locale locale);

    void onDataUpdated();

    void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2);

    Map<String, String> parseJsonListResponse(JSONObject jSONObject);

    void setEnabled(boolean z);

    void start();

    boolean unsubscribe(String str);
}
