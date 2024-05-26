package com.nuance.swype.connect;

import android.content.Context;
import android.util.SparseArray;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACManager;

/* loaded from: classes.dex */
public abstract class ConnectedStatus {
    private static final LogManager.Log log = LogManager.getLog("ConnectedStatus");
    private ACManager acManager;
    private boolean connectedWifi;
    private boolean connectedWireless;
    private final ACManager.ACConnectionCallback connectionCallback;
    private boolean initialized;
    private boolean registered;
    private UserPreferences settings;
    private boolean stalled;

    public ConnectedStatus(Context ctx) {
        this(ctx, Connect.from(ctx).getSDKManager());
    }

    protected ConnectedStatus(Context context, ACManager acManager) {
        this.connectionCallback = new ACManager.ACConnectionCallback() { // from class: com.nuance.swype.connect.ConnectedStatus.1
            @Override // com.nuance.swypeconnect.ac.ACManager.ACConnectionCallback
            public void connected(int type, int connectionType) {
                ConnectedStatus.log.d("ACConnectionCallback.connected(), we're connected connectionType: " + connectionType + "  type:" + type);
                boolean wasConnected = ConnectedStatus.this.connectedWifi || ConnectedStatus.this.connectedWireless;
                ConnectedStatus.log.d("wasConnected ", Boolean.valueOf(wasConnected));
                if (connectionType == 3 || connectionType == 1) {
                    if (!ConnectedStatus.this.isInitialized()) {
                        ConnectedStatus.this.acManager.retryConnection();
                    }
                    switch (type) {
                        case 0:
                            ConnectedStatus.this.connectedWireless = true;
                            ConnectedStatus.this.onForegroundConnection(0);
                            break;
                        case 1:
                            ConnectedStatus.this.connectedWifi = true;
                            ConnectedStatus.this.onForegroundConnection(1);
                            break;
                    }
                    ConnectedStatus.this.stalled = false;
                    if (!wasConnected) {
                        if (ConnectedStatus.this.connectedWifi || ConnectedStatus.this.connectedWireless) {
                            ConnectedStatus.this.onConnectionChanged(true);
                        }
                    }
                }
            }

            @Override // com.nuance.swypeconnect.ac.ACManager.ACConnectionCallback
            public void disconnected(int connectionType) {
                boolean wasConnected = ConnectedStatus.this.connectedWifi || ConnectedStatus.this.connectedWireless;
                switch (connectionType) {
                    case 1:
                        ConnectedStatus.this.connectedWireless = false;
                        ConnectedStatus.this.connectedWifi = false;
                        break;
                    case 3:
                        ConnectedStatus.this.connectedWireless = false;
                        ConnectedStatus.this.connectedWifi = false;
                        break;
                }
                if (wasConnected && !ConnectedStatus.this.connectedWifi && !ConnectedStatus.this.connectedWireless) {
                    ConnectedStatus.this.onConnectionChanged(false);
                }
            }

            @Override // com.nuance.swypeconnect.ac.ACManager.ACConnectionCallback
            public void connectionStatus(int status, String message) {
                if (status == 13) {
                    ConnectedStatus.this.initialized = true;
                    ConnectedStatus.this.onInitialized();
                } else if (status == 2) {
                    ConnectedStatus.this.stalled = true;
                } else if (status == 1) {
                    ConnectedStatus.this.stalled = false;
                }
                ConnectedStatus.this.onConnectionStatus(status, message);
            }
        };
        log.d("ConnectedStatus()");
        this.acManager = acManager;
        this.initialized = acManager != null ? acManager.isConnectInitialized() : false;
        this.settings = IMEApplication.from(context).getUserPreferences();
    }

    public void register() {
        log.d("register");
        if (this.acManager != null) {
            if (!this.registered) {
                this.acManager.registerConnectionCallback(this.connectionCallback);
                this.registered = true;
            }
            this.initialized = this.acManager.isConnectInitialized();
        }
    }

    public void unregister() {
        if (this.acManager != null) {
            this.acManager.unregisterConnectionCallback(this.connectionCallback);
            this.registered = false;
        }
    }

    public void onConnectionStatus(int status, String message) {
        log.d("onConnectionStatus() status:" + StatusStates.forId(status) + " message:" + message);
    }

    public void onInitialized() {
        log.d("Initialized");
    }

    public void onConnectionChanged(boolean isConnected) {
        log.d("onConnectionChanged(", Boolean.valueOf(isConnected), ")");
    }

    public void onForegroundConnection(int type) {
        log.d("onForegroundConnection(", Integer.valueOf(type), ")");
    }

    public boolean isConnected() {
        return this.connectedWifi || this.connectedWireless;
    }

    public boolean isConnectedWifi() {
        return this.connectedWifi;
    }

    public boolean isConnectedCellular() {
        return this.connectedWireless;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isDataConnectionPermitted() {
        return this.settings.connectUseCellularData();
    }

    public boolean isStalled() {
        return this.stalled;
    }

    /* loaded from: classes.dex */
    public enum StatusStates {
        CONNECTION_STATUS_OK(1, "CONNECTION_STATUS_OK"),
        CONNECTION_STATUS_STALLED(2, "CONNECTION_STATUS_STALLED"),
        CONNECTION_STATUS_DELAYED(3, "CONNECTION_STATUS_DELAYED"),
        CONNECTION_STATUS_STALLED_DOWNLOAD(8, "CONNECTION_STATUS_STALLED_DOWNLOAD"),
        CONNECTION_STATUS_STALLED_HTTP_ERROR(7, "CONNECTION_STATUS_STALLED_HTTP_ERROR"),
        CONNECTION_STATUS_STALLED_JSON_PARSE(5, "CONNECTION_STATUS_STALLED_JSON_PARSE"),
        CONNECTION_STATUS_STALLED_PROTOCOL_EXCEPTION(6, "CONNECTION_STATUS_STALLED_PROTOCOL_EXCEPTION"),
        CONNECTION_STATUS_STALLED_SSL(4, "CONNECTION_STATUS_STALLED_SSL"),
        CONNECTION_STATUS_STALLED_UNKNOWN_RESPONSE(9, "CONNECTION_STATUS_STALLED_UNKNOWN_RESPONSE"),
        CONNECTION_STATUS_REFRESH_COMPLETED(10, "CONNECTION_STATUS_REFRESH_COMPLETED"),
        CONNECTION_STATUS_REFRESH_PENDING(11, "CONNECTION_STATUS_REFRESH_PENDING"),
        CONNECTION_STATUS_REFRESH_DELAYED(12, "CONNECTION_STATUS_REFRESH_DELAYED"),
        CONNECTION_STATUS_INITIALIZATION_COMPLETE(13, "CONNECTION_STATUS_INITIALIZATION_COMPLETE"),
        BUILD_STATUS_UNLICENSED(14, "BUILD_STATUS_UNLICENSED"),
        BUILD_STATUS_LICENSED(15, "BUILD_STATUS_LICENSED"),
        CONNECTION_STATUS_UNKNOWN(0, "CONNECTION_STATUS_UNKNOWN");

        private static final SparseArray<StatusStates> map = new SparseArray<>();
        private String description;
        private int id;

        static {
            for (StatusStates status : values()) {
                map.put(status.getId(), status);
            }
        }

        StatusStates(int id, String description) {
            this.id = id;
            this.description = description;
        }

        public final int getId() {
            return this.id;
        }

        public final String getDescription() {
            return this.description;
        }

        public static StatusStates forId(int id) {
            return map.get(id, CONNECTION_STATUS_UNKNOWN);
        }
    }
}
