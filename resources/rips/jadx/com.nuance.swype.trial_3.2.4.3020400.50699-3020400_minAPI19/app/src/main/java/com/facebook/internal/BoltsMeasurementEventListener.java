package com.facebook.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.facebook.appevents.AppEventsLogger;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;

/* loaded from: classes.dex */
public class BoltsMeasurementEventListener extends BroadcastReceiver {
    private static final String BOLTS_MEASUREMENT_EVENT_PREFIX = "bf_";
    private static final String MEASUREMENT_EVENT_ARGS_KEY = "event_args";
    private static final String MEASUREMENT_EVENT_NAME_KEY = "event_name";
    private static final String MEASUREMENT_EVENT_NOTIFICATION_NAME = "com.parse.bolts.measurement_event";
    private static BoltsMeasurementEventListener _instance;
    private Context applicationContext;

    private BoltsMeasurementEventListener(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    private void open() {
        LocalBroadcastManager.getInstance(this.applicationContext).registerReceiver(this, new IntentFilter("com.parse.bolts.measurement_event"));
    }

    private void close() {
        LocalBroadcastManager.getInstance(this.applicationContext).unregisterReceiver(this);
    }

    public static BoltsMeasurementEventListener getInstance(Context context) {
        if (_instance != null) {
            return _instance;
        }
        BoltsMeasurementEventListener boltsMeasurementEventListener = new BoltsMeasurementEventListener(context);
        _instance = boltsMeasurementEventListener;
        boltsMeasurementEventListener.open();
        return _instance;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(context);
        String eventName = BOLTS_MEASUREMENT_EVENT_PREFIX + intent.getStringExtra("event_name");
        Bundle eventArgs = intent.getBundleExtra("event_args");
        Bundle logData = new Bundle();
        for (String key : eventArgs.keySet()) {
            String safeKey = key.replaceAll("[^0-9a-zA-Z _-]", XMLResultsHandler.SEP_HYPHEN).replaceAll("^[ -]*", "").replaceAll("[ -]*$", "");
            logData.putString(safeKey, (String) eventArgs.get(key));
        }
        appEventsLogger.logEvent(eventName, logData);
    }
}
