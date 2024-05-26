package bolts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.internal.NativeProtocol;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MeasurementEvent {
    public static final String APP_LINK_NAVIGATE_IN_EVENT_NAME = "al_nav_in";
    public static final String APP_LINK_NAVIGATE_OUT_EVENT_NAME = "al_nav_out";
    public static final String MEASUREMENT_EVENT_ARGS_KEY = "event_args";
    public static final String MEASUREMENT_EVENT_NAME_KEY = "event_name";
    public static final String MEASUREMENT_EVENT_NOTIFICATION_NAME = "com.parse.bolts.measurement_event";
    private Context appContext;
    private Bundle args;
    private String name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void sendBroadcastEvent(Context context, String name, Intent intent, Map<String, String> extraLoggingData) {
        Bundle logData = new Bundle();
        if (intent != null) {
            Bundle applinkData = AppLinks.getAppLinkData(intent);
            if (applinkData != null) {
                logData = getApplinkLogData(context, name, applinkData, intent);
            } else {
                Uri intentUri = intent.getData();
                if (intentUri != null) {
                    logData.putString("intentData", intentUri.toString());
                }
                Bundle intentExtras = intent.getExtras();
                if (intentExtras != null) {
                    for (String key : intentExtras.keySet()) {
                        String logValue = objectToJSONString(intentExtras.get(key));
                        logData.putString(key, logValue);
                    }
                }
            }
        }
        if (extraLoggingData != null) {
            for (String key2 : extraLoggingData.keySet()) {
                logData.putString(key2, extraLoggingData.get(key2));
            }
        }
        new MeasurementEvent(context, name, logData).sendBroadcast();
    }

    private MeasurementEvent(Context context, String eventName, Bundle eventArgs) {
        this.appContext = context.getApplicationContext();
        this.name = eventName;
        this.args = eventArgs;
    }

    private void sendBroadcast() {
        if (this.name == null) {
            getClass().getName();
        }
        try {
            Class<?> clazz = Class.forName("android.support.v4.content.LocalBroadcastManager");
            Method methodGetInstance = clazz.getMethod("getInstance", Context.class);
            Method methodSendBroadcast = clazz.getMethod("sendBroadcast", Intent.class);
            Object localBroadcastManager = methodGetInstance.invoke(null, this.appContext);
            Intent event = new Intent(MEASUREMENT_EVENT_NOTIFICATION_NAME);
            event.putExtra(MEASUREMENT_EVENT_NAME_KEY, this.name);
            event.putExtra(MEASUREMENT_EVENT_ARGS_KEY, this.args);
            methodSendBroadcast.invoke(localBroadcastManager, event);
        } catch (Exception e) {
            getClass().getName();
        }
    }

    private static Bundle getApplinkLogData(Context context, String eventName, Bundle appLinkData, Intent applinkIntent) {
        Bundle logData = new Bundle();
        ComponentName resolvedActivity = applinkIntent.resolveActivity(context.getPackageManager());
        if (resolvedActivity != null) {
            logData.putString("class", resolvedActivity.getShortClassName());
        }
        if (APP_LINK_NAVIGATE_OUT_EVENT_NAME.equals(eventName)) {
            if (resolvedActivity != null) {
                logData.putString("package", resolvedActivity.getPackageName());
            }
            if (applinkIntent.getData() != null) {
                logData.putString("outputURL", applinkIntent.getData().toString());
            }
            if (applinkIntent.getScheme() != null) {
                logData.putString("outputURLScheme", applinkIntent.getScheme());
            }
        } else if (APP_LINK_NAVIGATE_IN_EVENT_NAME.equals(eventName)) {
            if (applinkIntent.getData() != null) {
                logData.putString("inputURL", applinkIntent.getData().toString());
            }
            if (applinkIntent.getScheme() != null) {
                logData.putString("inputURLScheme", applinkIntent.getScheme());
            }
        }
        for (String key : appLinkData.keySet()) {
            Object o = appLinkData.get(key);
            if (o instanceof Bundle) {
                for (String subKey : ((Bundle) o).keySet()) {
                    String logValue = objectToJSONString(((Bundle) o).get(subKey));
                    if (key.equals("referer_app_link")) {
                        if (subKey.equalsIgnoreCase("url")) {
                            logData.putString("refererURL", logValue);
                        } else if (subKey.equalsIgnoreCase(NativeProtocol.BRIDGE_ARG_APP_NAME_STRING)) {
                            logData.putString("refererAppName", logValue);
                        } else if (subKey.equalsIgnoreCase("package")) {
                            logData.putString("sourceApplication", logValue);
                        }
                    }
                    logData.putString(key + "/" + subKey, logValue);
                }
            } else {
                String logValue2 = objectToJSONString(o);
                if (key.equals("target_url")) {
                    Uri targetURI = Uri.parse(logValue2);
                    logData.putString("targetURL", targetURI.toString());
                    logData.putString("targetURLHost", targetURI.getHost());
                } else {
                    logData.putString(key, logValue2);
                }
            }
        }
        return logData;
    }

    private static String objectToJSONString(Object o) {
        String str = null;
        if (o == null) {
            return null;
        }
        if ((o instanceof JSONArray) || (o instanceof JSONObject)) {
            return o.toString();
        }
        try {
            if (o instanceof Collection) {
                str = new JSONArray((Collection) o).toString();
            } else if (o instanceof Map) {
                str = new JSONObject((Map) o).toString();
            } else {
                str = o.toString();
            }
            return str;
        } catch (Exception e) {
            return str;
        }
    }
}
