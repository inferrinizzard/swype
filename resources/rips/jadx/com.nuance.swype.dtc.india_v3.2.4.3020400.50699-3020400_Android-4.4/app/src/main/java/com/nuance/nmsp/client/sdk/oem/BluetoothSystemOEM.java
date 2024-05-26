package com.nuance.nmsp.client.sdk.oem;

import android.content.Context;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.BluetoothSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth;
import java.util.Vector;

/* loaded from: classes.dex */
public class BluetoothSystemOEM implements BluetoothSystem {
    private LogFactory.Log a = LogFactory.getLog(getClass());
    private Context b;
    private boolean c;

    public BluetoothSystemOEM(Vector vector) {
        this.b = null;
        this.c = false;
        if (vector != null) {
            for (int i = 0; i < vector.size(); i++) {
                Parameter parameter = (Parameter) vector.get(i);
                String name = parameter.getName();
                if (parameter.getType() == Parameter.Type.SDK) {
                    if (name.equals(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT)) {
                        this.b = (Context) parameter.getValueRaw();
                        if (this.a.isDebugEnabled()) {
                            this.a.debug("NMSP_DEFINES_ANDROID_CONTEXT is passed in as" + this.b);
                        }
                    } else if (name.equals(NMSPDefines.NMSP_DEFINES_DISABLE_BLUETOOTH) && new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                        if (this.a.isDebugEnabled()) {
                            this.a.debug("Disable_Bluetooth is true.");
                        }
                        this.c = true;
                    }
                }
            }
        }
        if (this.b != null) {
            this.c = this.b.checkCallingOrSelfPermission("android.permission.BLUETOOTH") != 0;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.BluetoothSystem
    public boolean isBluetoothHeadsetConnected() {
        if (this.c) {
            return false;
        }
        if (this.b == null) {
            if (!this.a.isErrorEnabled()) {
                return false;
            }
            this.a.error("ANDROID_CONTEXT parameter is not passed in!!!");
            return false;
        }
        Bluetooth createInstance = Bluetooth.createInstance(this.b);
        boolean isHeadsetConnected = createInstance.isHeadsetConnected();
        createInstance.close();
        return isHeadsetConnected;
    }
}
