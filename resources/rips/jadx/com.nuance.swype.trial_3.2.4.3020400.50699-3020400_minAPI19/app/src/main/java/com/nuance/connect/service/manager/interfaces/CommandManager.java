package com.nuance.connect.service.manager.interfaces;

import android.os.Bundle;
import android.util.Pair;
import com.nuance.connect.comm.ResponseCallback;
import java.util.List;

/* loaded from: classes.dex */
public interface CommandManager extends ResponseCallback {
    void alarmNotification(String str, Bundle bundle);

    String getCommandFamily();

    List<Pair<String, String>> getRealTimeSubscriptions();

    int getVersion();
}
