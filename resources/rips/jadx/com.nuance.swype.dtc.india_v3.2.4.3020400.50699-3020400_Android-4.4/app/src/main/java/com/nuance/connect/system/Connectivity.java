package com.nuance.connect.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.IntentStrings;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;

/* loaded from: classes.dex */
public class Connectivity {
    private static final String CONECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    protected NetworkState.NetworkConfiguration backgroundConfiguration;
    protected int connectionChangeStableCellularRequirement;
    protected int connectionChangeStableWifiRequirement;
    protected final Context context;
    protected NetworkState.NetworkConfiguration foregroundConfiguration;
    protected NetworkState.NetworkConfiguration lastBackgroundConfiguration;
    protected NetworkState.NetworkConfiguration lastForegroundConfiguration;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    protected boolean requireStableCellularTime = false;
    protected boolean requireStableWifiTime = false;
    private NetworkState state = new NetworkState();
    protected boolean changedForegroundState = false;
    protected boolean changedBackgroundState = false;
    private ConcurrentCallbackSet<NetworkListener> networkListeners = new ConcurrentCallbackSet<>();
    private final BroadcastReceiver mConnectivityCheckReceiver = new BroadcastReceiver() { // from class: com.nuance.connect.system.Connectivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Connectivity.this.checkAvailableNetworkConnections();
        }
    };

    public Connectivity(Context context) {
        this.context = context;
        IntentFilter intentFilter = new IntentFilter(CONECTIVITY_CHANGE_ACTION);
        intentFilter.addAction(IntentStrings.REFRESH_CONNECTION_DATA_INTENT);
        context.registerReceiver(this.mConnectivityCheckReceiver, intentFilter);
    }

    private int getStableConnectionRequirement(int i) {
        int i2 = this.connectionChangeStableCellularRequirement;
        switch (i) {
            case 1:
            case 6:
            case 7:
            case 9:
                return this.connectionChangeStableWifiRequirement;
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            default:
                return i2;
        }
    }

    private boolean requireStableConnection(int i) {
        boolean z = this.requireStableCellularTime;
        switch (i) {
            case 1:
            case 6:
            case 7:
            case 9:
                return this.requireStableWifiTime;
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            default:
                return z;
        }
    }

    private void sendStatus() {
        if ((this.state.hasConnectivity(this.backgroundConfiguration) && this.changedBackgroundState) || (this.state.hasConnectivity(this.foregroundConfiguration) && this.changedForegroundState)) {
            this.log.d("Connectivity.sendStatus() -- onNetworkAvailable()");
            for (NetworkListener networkListener : (NetworkListener[]) this.networkListeners.toArray(new NetworkListener[0])) {
                networkListener.onNetworkAvailable();
            }
        } else if (!this.state.hasConnectivity(this.backgroundConfiguration) && !this.state.hasConnectivity(this.foregroundConfiguration) && (this.changedBackgroundState || this.changedForegroundState)) {
            this.log.d("Connectivity.sendStatus() -- onNetworkDisconnect()");
            for (NetworkListener networkListener2 : (NetworkListener[]) this.networkListeners.toArray(new NetworkListener[0])) {
                networkListener2.onNetworkDisconnect();
            }
        }
        this.changedForegroundState = false;
        this.changedBackgroundState = false;
    }

    private void sendStatusListener(NetworkListener networkListener) {
        if (this.state.hasConnectivity(this.backgroundConfiguration) || this.state.hasConnectivity(this.foregroundConfiguration)) {
            this.log.d("Connectivity.sendStatusListener() -- onNetworkAvailable()");
            networkListener.onNetworkAvailable();
        } else {
            if (this.state.hasConnectivity(this.backgroundConfiguration) || this.state.hasConnectivity(this.foregroundConfiguration)) {
                return;
            }
            this.log.d("Connectivity.sendStatusListener() -- onNetworkDisconnect()");
            networkListener.onNetworkDisconnect();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0027  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.nuance.connect.system.NetworkState setNetworkState(android.net.NetworkInfo r7, com.nuance.connect.system.NetworkState r8) {
        /*
            r6 = this;
            r2 = 0
            if (r7 == 0) goto L2d
            boolean r0 = r7.isConnected()
            if (r0 == 0) goto L2d
            int r0 = r7.getType()
            int r3 = r6.getStableConnectionRequirement(r0)
            long r0 = java.lang.System.currentTimeMillis()
            if (r8 != 0) goto L19
            long r4 = (long) r3
            long r0 = r0 - r4
        L19:
            boolean r3 = r7.isConnected()
            if (r3 == 0) goto L2d
            com.nuance.connect.system.NetworkState r2 = new com.nuance.connect.system.NetworkState
            r2.<init>(r7, r0)
            r0 = r2
        L25:
            if (r0 != 0) goto L2c
            com.nuance.connect.system.NetworkState r0 = new com.nuance.connect.system.NetworkState
            r0.<init>()
        L2c:
            return r0
        L2d:
            r0 = r2
            goto L25
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.system.Connectivity.setNetworkState(android.net.NetworkInfo, com.nuance.connect.system.NetworkState):com.nuance.connect.system.NetworkState");
    }

    public void checkAvailableNetworkConnections() {
        if (this.backgroundConfiguration == null || this.foregroundConfiguration == null) {
            return;
        }
        this.log.d("checkAvailableNetworkConnections");
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.context.getSystemService("connectivity")).getActiveNetworkInfo();
        NetworkState networkState = this.state;
        this.state = setNetworkState(activeNetworkInfo, networkState);
        if (networkState == null || !networkState.isSame(this.state)) {
            this.changedBackgroundState = true;
            this.changedForegroundState = true;
        } else {
            if (this.state.hasConnectivity(this.foregroundConfiguration) != networkState.hasConnectivity(this.lastForegroundConfiguration)) {
                this.changedForegroundState = true;
            }
            if (this.state.hasConnectivity(this.backgroundConfiguration) != networkState.hasConnectivity(this.lastBackgroundConfiguration)) {
                this.changedBackgroundState = true;
            }
        }
        this.log.d("Connectivity FOREGROUND hasConnectivity: ", Boolean.valueOf(this.state.hasConnectivity(this.foregroundConfiguration)));
        this.log.d("Connectivity BACKGROUND hasConnectivity: ", Boolean.valueOf(this.state.hasConnectivity(this.backgroundConfiguration)));
        sendStatus();
    }

    public void destroy() {
        this.networkListeners.clear();
        this.context.unregisterReceiver(this.mConnectivityCheckReceiver);
    }

    public NetworkState.NetworkConfiguration getBackgroundConfiguration() {
        return this.backgroundConfiguration;
    }

    public NetworkState.NetworkConfiguration getForegroundConfiguration() {
        return this.foregroundConfiguration;
    }

    public NetworkState getState() {
        return this.state;
    }

    public void registerNetworkListener(NetworkListener networkListener) {
        this.networkListeners.add(networkListener);
        sendStatusListener(networkListener);
    }

    public void setBackgroundConfiguration(String str) {
        this.lastBackgroundConfiguration = this.backgroundConfiguration;
        this.backgroundConfiguration = NetworkState.NetworkConfiguration.fromString(str);
        checkAvailableNetworkConnections();
    }

    public void setForegroundConfiguration(String str) {
        this.lastForegroundConfiguration = this.foregroundConfiguration;
        this.foregroundConfiguration = NetworkState.NetworkConfiguration.fromString(str);
        checkAvailableNetworkConnections();
    }

    public void setStableCellularTime(int i) {
        if (i >= 0) {
            this.requireStableCellularTime = true;
            this.connectionChangeStableCellularRequirement = i;
            if (this.foregroundConfiguration != null) {
                this.foregroundConfiguration.setConnectionStableRequirement(i);
            }
            if (this.backgroundConfiguration != null) {
                this.backgroundConfiguration.setConnectionStableRequirement(i);
            }
        } else {
            this.requireStableCellularTime = false;
            this.connectionChangeStableCellularRequirement = Integers.STATUS_SUCCESS;
        }
        checkAvailableNetworkConnections();
    }

    public void setStableWifiTime(int i) {
        if (i >= 0) {
            this.requireStableWifiTime = true;
            this.connectionChangeStableWifiRequirement = i;
            if (this.foregroundConfiguration != null) {
                this.foregroundConfiguration.setConnectionStableRequirement(i);
            }
            if (this.backgroundConfiguration != null) {
                this.backgroundConfiguration.setConnectionStableRequirement(i);
            }
        } else {
            this.requireStableWifiTime = false;
            this.connectionChangeStableWifiRequirement = Integers.STATUS_SUCCESS;
        }
        checkAvailableNetworkConnections();
    }

    public void unregisterNetworkListener(NetworkListener networkListener) {
        this.networkListeners.remove(networkListener);
    }
}
