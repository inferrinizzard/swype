package com.nuance.nmsp.client.sdk.oem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.UUID;

/* loaded from: classes.dex */
public class DeviceInfoOEM implements DeviceInfo {
    private static final LogFactory.Log a = LogFactory.getLog(DeviceInfoOEM.class);
    private static DeviceInfo b;
    private TelephonyManager c;
    private Context d;

    private DeviceInfoOEM() {
        this.d = null;
    }

    public DeviceInfoOEM(Context context) {
        this.d = null;
        this.d = context;
        this.c = (TelephonyManager) context.getSystemService("phone");
    }

    public static synchronized DeviceInfo getInstance() {
        DeviceInfo deviceInfo;
        synchronized (DeviceInfoOEM.class) {
            if (b == null) {
                b = new DeviceInfoOEM();
            }
            deviceInfo = b;
        }
        return deviceInfo;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getDeviceModel() {
        return Build.MODEL;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getDeviceSubModel() {
        return Build.DEVICE;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getOSType() {
        return "Android";
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getOperatorName() {
        String networkOperatorName = this.c.getNetworkOperatorName();
        return networkOperatorName == null ? "" : networkOperatorName;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getPhoneNumber() {
        String line1Number = this.c.getLine1Number();
        return line1Number == null ? "" : line1Number;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.DeviceInfo
    public String getUniqueID() {
        SharedPreferences sharedPreferences = this.d.getSharedPreferences("nuance_sdk_pref", 0);
        String string = sharedPreferences.getString("NUANCE_NMSP_UID", "");
        if (string.length() == 0) {
            string = UUID.randomUUID().toString();
            if (string != null) {
                string = string.replaceAll(XMLResultsHandler.SEP_HYPHEN, "");
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("NUANCE_NMSP_UID", string);
            if (!edit.commit()) {
                a.error("Storing nuance sdk uid has failed");
            }
        }
        return string == null ? "" : string;
    }
}
