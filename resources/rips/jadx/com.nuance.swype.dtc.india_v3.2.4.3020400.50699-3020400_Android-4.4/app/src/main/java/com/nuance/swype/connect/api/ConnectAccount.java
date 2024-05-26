package com.nuance.swype.connect.api;

import com.nuance.connect.util.TimeConversion;
import com.nuance.swype.util.LogManager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ConnectAccount implements Serializable {
    private static final String ACCOUNT_DEVICES_KEY = "DEVICES";
    private static final String ACCOUNT_ID_KEY = "ACCOUNT_ID";
    private static final String ACCOUNT_STATE_KEY = "ACCOUNT_STATE";
    public static final int ACCOUNT_TYPE_EMAIL = 1;
    private static final String CREATION_METHOD_KEY = "CREATION_METHOD";
    private static final String DEVICE_ID_KEY = "DEVICE_ID";
    private static final String DEVICE_LAST_CHECKIN_KEY = "LAST_CHECKIN";
    private static final String DEVICE_NAME_KEY = "NAME";
    private static final String DEVICE_STATUS_KEY = "STATUS";
    private static final String DEVICE_TYPE_KEY = "DEVICE_TYPE";
    private static final String DISPLAY_IDENTIFIER_KEY = "DISPLAY_IDENTIFIER";
    private static final String EXISTS_ON_SERVER_KEY = "EXISTS_ON_SERVER";
    private static final String IDENTIFIER_KEY = "IDENTIFIER";
    private static final String IS_THIS_DEVICE_KEY = "IS_THIS_DEVICE";
    private static final String TYPE_KEY = "TYPE";
    private static final String VERSION_KEY = "VERSION";
    private static final LogManager.Log log = LogManager.getLog("ConnectAccount");
    private static final long serialVersionUID = 149763289234560956L;
    private String accountId;
    private CreationMethod creationMethod;
    private int deviceCount;
    private String identifier;
    private String identifierForDisplay;
    private int type;
    private boolean existsOnServer = false;
    private AccountState accountState = AccountState.UNREGISTERED;
    private final Map<String, AccountDevice> devices = new HashMap();

    /* loaded from: classes.dex */
    public enum AccountState {
        UNREGISTERED,
        REGISTERED,
        VERIFIED,
        PENDING_DELETE
    }

    /* loaded from: classes.dex */
    public enum CreationMethod {
        REGISTERED_ANDROID_ACCOUNT,
        USER_CREATED
    }

    public ConnectAccount(String accountId, String identifier, int type, CreationMethod creationMethod) {
        this.accountId = accountId;
        setIdentifier(identifier);
        this.identifierForDisplay = identifier;
        this.type = type;
        this.deviceCount = 0;
        this.creationMethod = creationMethod;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getIdentifierForDisplay() {
        return this.identifierForDisplay;
    }

    public int getType() {
        return this.type;
    }

    public boolean getExistsOnServer() {
        return this.existsOnServer;
    }

    public CreationMethod getCreationMethod() {
        return this.creationMethod;
    }

    public AccountState getAccountState() {
        return this.accountState;
    }

    public boolean isDeleted() {
        return this.accountState.equals(AccountState.PENDING_DELETE);
    }

    public int getDeviceCount() {
        return this.deviceCount;
    }

    public Map<String, AccountDevice> getDevices() {
        return this.devices;
    }

    public void reset() {
        this.devices.clear();
        this.accountId = null;
        if (this.accountState != AccountState.PENDING_DELETE) {
            this.accountState = AccountState.UNREGISTERED;
        }
        this.existsOnServer = false;
        this.deviceCount = 0;
    }

    public void verify() {
        this.accountState = AccountState.VERIFIED;
    }

    public void setExistsOnServer(boolean exists) {
        this.existsOnServer = exists;
    }

    public void setAccountState(AccountState state) {
        this.accountState = state;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setDeviceCount(int count) {
        this.deviceCount = count;
    }

    private void setIdentifier(String identifier) {
        if (identifier != null) {
            String[] parts = identifier.split("@");
            if (parts.length == 2) {
                this.identifier = parts[0] + "@" + parts[1].toLowerCase(Locale.ENGLISH);
            }
        }
    }

    public void addDevice(AccountDevice device) {
        if (this.devices.containsKey(device.getDeviceId())) {
            AccountDevice updateDevice = this.devices.get(device.getDeviceId());
            updateDevice.setName(device.getName());
            updateDevice.setLastCheckin(device.getLastCheckin());
            return;
        }
        this.devices.put(device.getDeviceId(), device);
    }

    public void addDevices(List<AccountDevice> devices) {
        HashSet<String> s = new HashSet<>();
        for (AccountDevice device : devices) {
            addDevice(device);
            s.add(device.getDeviceId());
        }
        this.devices.keySet().retainAll(s);
    }

    public String toString() {
        return new StringBuffer("Account [ id: ").append(this.accountId).append("\nidentifier: ").append(this.identifier).append("\ntype: ").append(this.type).append("\nexistsOnServer: ").append(this.existsOnServer).append("\ncreationMethod: ").append(this.creationMethod).append("\naccountState: ").append(this.accountState).append("\ndeviceCount: ").append(this.deviceCount).append("\n]").toString();
    }

    public String toJsonString() {
        JSONArray jsdevices = new JSONArray();
        JSONObject account = new JSONObject();
        try {
            account.put("IDENTIFIER", getIdentifier());
            account.put(DISPLAY_IDENTIFIER_KEY, getIdentifierForDisplay());
            account.put(ACCOUNT_ID_KEY, getAccountId());
            account.put(TYPE_KEY, getType());
            account.put(EXISTS_ON_SERVER_KEY, getExistsOnServer());
            account.put(CREATION_METHOD_KEY, getCreationMethod().name());
            account.put(ACCOUNT_STATE_KEY, getAccountState().name());
            Iterator<Map.Entry<String, AccountDevice>> it = getDevices().entrySet().iterator();
            while (it.hasNext()) {
                AccountDevice device = it.next().getValue();
                JSONObject jsdevice = new JSONObject();
                jsdevice.put("DEVICE_ID", device.getDeviceId());
                jsdevice.put(DEVICE_NAME_KEY, device.getName());
                jsdevice.put(DEVICE_TYPE_KEY, device.getType().name());
                jsdevice.put(DEVICE_LAST_CHECKIN_KEY, device.getLastCheckin());
                jsdevice.put(IS_THIS_DEVICE_KEY, device.getIsThisDevice());
                jsdevice.put("STATUS", device.getDeviceStatus().name());
                jsdevices.put(jsdevice);
            }
            account.put(ACCOUNT_DEVICES_KEY, jsdevices);
            account.put(VERSION_KEY, serialVersionUID);
            String json = account.toString();
            return json;
        } catch (JSONException e) {
            log.d("failed to create JSON blob for account data");
            return "";
        }
    }

    /* loaded from: classes.dex */
    public static class AccountDevice implements Serializable {
        private static final long serialVersionUID = 10486293320907243L;
        private String deviceId;
        private boolean isThisDevice;
        private long lastCheckin;
        private String name;
        private DeviceStatus status = DeviceStatus.ACTIVE;
        private DeviceType type;

        /* loaded from: classes.dex */
        public enum DeviceStatus {
            ACTIVE,
            PENDING_DELETE
        }

        /* loaded from: classes.dex */
        public enum DeviceType {
            PHONE,
            TABLET,
            TV
        }

        public AccountDevice(String deviceId, String name, long lastCheckin, boolean isThisDevice, DeviceType type) {
            this.deviceId = deviceId;
            this.lastCheckin = lastCheckin;
            this.isThisDevice = isThisDevice;
            this.name = name;
            this.type = type;
        }

        public String getDeviceId() {
            return this.deviceId;
        }

        public String getName() {
            return this.name;
        }

        public void setType(DeviceType type) {
            this.type = type;
        }

        public DeviceType getType() {
            return this.type;
        }

        public long getLastCheckin() {
            return this.lastCheckin;
        }

        public void setLastCheckin(long checkin) {
            this.lastCheckin = checkin;
        }

        public void markAsDeleted() {
            this.status = DeviceStatus.PENDING_DELETE;
        }

        public DeviceStatus getDeviceStatus() {
            return this.status;
        }

        public boolean isDeleted() {
            return DeviceStatus.PENDING_DELETE.equals(this.status);
        }

        public long getLastCheckinDiff() {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(TimeUnit.SECONDS.toMillis(this.lastCheckin));
            Calendar cNow = Calendar.getInstance();
            cNow.setTime(new Date());
            return cNow.getTimeInMillis() - c.getTimeInMillis();
        }

        public int getLastCheckinDaysAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_DAY);
        }

        public int getLastCheckinHoursAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_HOUR);
        }

        public int getLastCheckinMinutesAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_MINUTE);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIsThisDevice(boolean isThisDevice) {
            this.isThisDevice = isThisDevice;
        }

        public boolean getIsThisDevice() {
            return this.isThisDevice;
        }

        public String toString() {
            return new StringBuffer("AccountDevice [ id: ").append(this.deviceId).append("\nlastCheckin timestamp: ").append(this.lastCheckin).append("\nlastCheckin diff: ").append(getLastCheckinDiff()).append("\nlastCheckin days: ").append(getLastCheckinDaysAgo()).append("\nlastCheckin hours: ").append(getLastCheckinHoursAgo()).append("\nlastCheckin minutes: ").append(getLastCheckinMinutesAgo()).append("\ntype: ").append(getType()).append("\n]").toString();
        }
    }
}
