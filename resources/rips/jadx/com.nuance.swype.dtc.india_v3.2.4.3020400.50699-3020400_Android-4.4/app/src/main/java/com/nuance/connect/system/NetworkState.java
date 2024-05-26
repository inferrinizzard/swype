package com.nuance.connect.system;

import android.net.NetworkInfo;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public class NetworkState {
    public static final int TYPE_BLUETOOTH = 7;
    public static final int TYPE_DISCONNECTED = -1;
    public static final int TYPE_DUMMY = 8;
    public static final int TYPE_ETHERNET = 9;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_MOBILE_DUN = 4;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_MOBILE_MMS = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_WIMAX = 6;
    private final int connectionType;
    private final String connectionTypeName;
    private final boolean hasConnectivity;
    private final boolean isFailover;
    private final boolean isRoaming;
    private final long lastConnectionChange;

    /* loaded from: classes.dex */
    public static class NetworkConfiguration {
        private static final String delimiter = "-";
        private boolean wifiEnabled = false;
        private boolean cellularEnabled = false;
        private boolean roamingEnabled = false;
        private int connectionStableRequirement = Integers.STATUS_SUCCESS;

        public NetworkConfiguration() {
        }

        public NetworkConfiguration(boolean z, boolean z2, boolean z3) {
            setNetworkState(z, z2, z3);
        }

        public static NetworkConfiguration fromString(String str) {
            String[] strArr;
            try {
                strArr = str.split("-");
            } catch (NullPointerException e) {
                strArr = null;
            }
            if (strArr == null || strArr.length != 3) {
                return null;
            }
            return new NetworkConfiguration(Boolean.parseBoolean(strArr[0]), Boolean.parseBoolean(strArr[1]), Boolean.parseBoolean(strArr[2]));
        }

        public int getConnectionStableRequirement() {
            return this.connectionStableRequirement;
        }

        public boolean isCellularEnabled() {
            return this.cellularEnabled;
        }

        public boolean isRoamingEnabled() {
            return this.roamingEnabled;
        }

        public boolean isWifiEnabled() {
            return this.wifiEnabled;
        }

        public boolean requiresStableConnection() {
            return this.connectionStableRequirement > 0;
        }

        public void setConnectionStableRequirement(int i) {
            this.connectionStableRequirement = i;
        }

        public void setNetworkState(boolean z, boolean z2, boolean z3) {
            this.wifiEnabled = z;
            this.cellularEnabled = z2;
            this.roamingEnabled = z3;
        }

        public String toReadableString() {
            return "wifi(" + this.wifiEnabled + ")-cellular(" + this.cellularEnabled + ")-roaming(" + this.roamingEnabled + ")";
        }

        public String toString() {
            return this.wifiEnabled + "-" + this.cellularEnabled + "-" + this.roamingEnabled;
        }
    }

    public NetworkState() {
        this.hasConnectivity = false;
        this.isFailover = false;
        this.connectionType = -1;
        this.connectionTypeName = null;
        this.isRoaming = false;
        this.lastConnectionChange = System.currentTimeMillis();
    }

    public NetworkState(NetworkInfo networkInfo, long j) {
        this.hasConnectivity = networkInfo != null ? networkInfo.isConnected() : false;
        this.isFailover = networkInfo != null ? networkInfo.isFailover() : false;
        this.connectionType = networkInfo != null ? networkInfo.getType() : -1;
        this.connectionTypeName = networkInfo != null ? networkInfo.getTypeName() : null;
        this.isRoaming = networkInfo != null ? networkInfo.isRoaming() : false;
        this.lastConnectionChange = j;
    }

    private static boolean isValidConnection(int i, boolean z, NetworkConfiguration networkConfiguration) {
        if (networkConfiguration == null) {
            return false;
        }
        switch (i) {
            case -1:
            case 8:
            default:
                return false;
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                if (!networkConfiguration.isCellularEnabled() || z) {
                    return networkConfiguration.isCellularEnabled() && networkConfiguration.isRoamingEnabled() && z;
                }
                return true;
            case 1:
            case 7:
            case 9:
                return networkConfiguration.isWifiEnabled();
        }
    }

    public static boolean isValidConnection(NetworkInfo networkInfo, NetworkConfiguration networkConfiguration) {
        if (networkInfo == null || networkConfiguration == null) {
            return false;
        }
        return isValidConnection(networkInfo.getType(), networkInfo.isRoaming(), networkConfiguration);
    }

    public int getConnectionType() {
        return this.connectionType;
    }

    public String getConnectionTypeName() {
        return this.connectionTypeName;
    }

    public long getLastConnectionChange() {
        return this.lastConnectionChange;
    }

    public boolean hasConnectivity(NetworkConfiguration networkConfiguration) {
        return this.hasConnectivity && isValidConnection(this.connectionType, this.isRoaming, networkConfiguration);
    }

    public boolean isFailover() {
        return this.isFailover;
    }

    public boolean isRoaming() {
        return this.isRoaming;
    }

    public boolean isSame(Object obj) {
        if (!(obj instanceof NetworkState)) {
            return false;
        }
        NetworkState networkState = (NetworkState) obj;
        if (this.hasConnectivity == networkState.hasConnectivity && this.isFailover == networkState.isFailover && this.connectionType == networkState.connectionType) {
            return ((this.connectionTypeName == null && networkState.connectionTypeName == null) || (this.connectionTypeName != null && this.connectionTypeName.equals(networkState.connectionTypeName))) && this.isRoaming == networkState.isRoaming;
        }
        return false;
    }

    public boolean isStableConnection(NetworkConfiguration networkConfiguration) {
        boolean hasConnectivity = hasConnectivity(networkConfiguration);
        if (networkConfiguration.getConnectionStableRequirement() <= 0 || System.currentTimeMillis() - this.lastConnectionChange >= networkConfiguration.getConnectionStableRequirement()) {
            return hasConnectivity;
        }
        return false;
    }

    public boolean isWifi() {
        return this.hasConnectivity && !this.isRoaming && this.connectionType == 1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hasConnectivity: ").append(this.hasConnectivity).append("\nisFailover: ").append(this.isFailover).append("\nconnectionType: ").append(this.connectionType).append("\nconnectionTypeName: ").append(this.connectionTypeName).append("\nisRoaming: ").append(this.isRoaming).append("\nlastConnectionChange: ").append(this.lastConnectionChange).append("\n");
        return sb.toString();
    }
}
