package com.nuance.nmsp.client.sdk.components.general;

import android.content.Context;
import com.nuance.nmsp.client.sdk.oem.DeviceInfoOEM;

/* loaded from: classes.dex */
public class ResourceUtil {
    public static String getDeviceModel() {
        return DeviceInfoOEM.getInstance().getDeviceModel();
    }

    public static String getDeviceSubModel() {
        return DeviceInfoOEM.getInstance().getDeviceSubModel();
    }

    public static String getOSType() {
        return DeviceInfoOEM.getInstance().getOSType();
    }

    public static String getOSVersion() {
        return DeviceInfoOEM.getInstance().getOSVersion();
    }

    public static String getOperatorName(Context context) {
        return new DeviceInfoOEM(context).getOperatorName();
    }

    public static String getPhoneNumber(Context context) {
        return new DeviceInfoOEM(context).getPhoneNumber();
    }

    public static String getUniqueID(Context context) {
        return new DeviceInfoOEM(context).getUniqueID();
    }
}
