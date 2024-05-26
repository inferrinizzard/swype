package com.nuance.swype.input;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/* loaded from: classes.dex */
public class PlatformUtil {
    private final ConnectivityManager mConnectivityManager;

    public PlatformUtil(Context context) {
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public boolean checkForDataConnection() {
        NetworkInfo[] netInfos = this.mConnectivityManager.getAllNetworkInfo();
        if (netInfos == null) {
            return false;
        }
        for (NetworkInfo networkInfo : netInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public boolean isWifiEnabled() {
        NetworkInfo netInfo = this.mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.getType() == 1;
    }

    public boolean isMobileEnabled() {
        NetworkInfo netInfo = this.mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.getType() == 0;
    }
}
