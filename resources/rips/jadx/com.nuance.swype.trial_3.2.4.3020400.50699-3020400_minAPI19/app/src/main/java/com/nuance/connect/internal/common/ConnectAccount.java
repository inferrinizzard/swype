package com.nuance.connect.internal.common;

import android.util.SparseArray;
import com.nuance.connect.util.TimeConversion;
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
    private static final String VERSION = "VERSION";
    private static final long serialVersionUID = 1015062423313707143L;
    private String accountId;
    private AccountState accountState;
    private CreationMethod creationMethod;
    private long creationTime;
    private int deviceCount;
    private String identifier;
    private String identifierForDisplay;
    private int type;
    private boolean existsOnServer = false;
    private final Map<String, AccountDevice> devices = new HashMap();

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
            PHONE(1),
            TABLET(2),
            TV(3),
            PHABLET(4);

            private static SparseArray<DeviceType> typeMap = new SparseArray<>();
            private int serverType;

            static {
                for (DeviceType deviceType : values()) {
                    typeMap.append(deviceType.getServerType(), deviceType);
                }
            }

            DeviceType(int i) {
                this.serverType = i;
            }

            public static DeviceType from(int i) {
                return typeMap.get(i, PHONE);
            }

            public final int getServerType() {
                return this.serverType;
            }
        }

        public AccountDevice(String str, String str2, long j, boolean z, DeviceType deviceType) {
            this.deviceId = str;
            this.lastCheckin = j;
            this.isThisDevice = z;
            this.name = str2;
            this.type = deviceType;
        }

        public String getDeviceId() {
            return this.deviceId;
        }

        public DeviceStatus getDeviceStatus() {
            return this.status;
        }

        public boolean getIsThisDevice() {
            return this.isThisDevice;
        }

        public long getLastCheckin() {
            return this.lastCheckin;
        }

        public int getLastCheckinDaysAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_DAY);
        }

        public long getLastCheckinDiff() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(this.lastCheckin);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(new Date());
            return calendar2.getTimeInMillis() - calendar.getTimeInMillis();
        }

        public int getLastCheckinHoursAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_HOUR);
        }

        public int getLastCheckinMinutesAgo() {
            return (int) (getLastCheckinDiff() / TimeConversion.MILLIS_IN_MINUTE);
        }

        public String getName() {
            return this.name;
        }

        public DeviceType getType() {
            return this.type;
        }

        public boolean isDeleted() {
            return DeviceStatus.PENDING_DELETE.equals(this.status);
        }

        public void markAsDeleted() {
            this.status = DeviceStatus.PENDING_DELETE;
        }

        public void setIsThisDevice(boolean z) {
            this.isThisDevice = z;
        }

        public void setLastCheckin(long j) {
            this.lastCheckin = j;
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setType(DeviceType deviceType) {
            this.type = deviceType;
        }

        public String toString() {
            return "AccountDevice [ id: " + this.deviceId + "\nlastCheckin timestamp: " + this.lastCheckin + "\nlastCheckin diff: " + getLastCheckinDiff() + "\nlastCheckin days: " + getLastCheckinDaysAgo() + "\nlastCheckin hours: " + getLastCheckinHoursAgo() + "\nlastCheckin minutes: " + getLastCheckinMinutesAgo() + "\ntype: " + getType() + "\n]";
        }
    }

    /* loaded from: classes.dex */
    public enum AccountState {
        UNREGISTERED,
        REGISTERED,
        PENDING_VERIFICATION,
        VERIFIED,
        PENDING_DELETE
    }

    /* loaded from: classes.dex */
    public enum CreationMethod {
        REGISTERED_ANDROID_ACCOUNT,
        USER_CREATED
    }

    public ConnectAccount(String str, String str2, int i, CreationMethod creationMethod) {
        this.accountState = AccountState.UNREGISTERED;
        this.accountId = str;
        setIdentifier(str2);
        this.identifierForDisplay = str2;
        this.type = i;
        this.deviceCount = 0;
        this.creationMethod = creationMethod;
        this.accountState = AccountState.UNREGISTERED;
    }

    public ConnectAccount(JSONObject jSONObject) {
        this.accountState = AccountState.UNREGISTERED;
        try {
            try {
                this.accountId = jSONObject.getString(ACCOUNT_ID_KEY);
            } catch (Exception e) {
            }
            setIdentifier(jSONObject.getString(DISPLAY_IDENTIFIER_KEY));
            this.identifierForDisplay = jSONObject.getString(DISPLAY_IDENTIFIER_KEY);
            this.type = jSONObject.getInt(TYPE_KEY);
            this.creationMethod = CreationMethod.valueOf(jSONObject.getString(CREATION_METHOD_KEY));
            this.accountState = AccountState.valueOf(jSONObject.getString(ACCOUNT_STATE_KEY));
            try {
                JSONArray jSONArray = jSONObject.getJSONArray(ACCOUNT_DEVICES_KEY);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                    try {
                        addDevice(new AccountDevice(jSONObject2.getString("DEVICE_ID"), jSONObject2.getString(DEVICE_NAME_KEY), jSONObject2.getLong(DEVICE_LAST_CHECKIN_KEY), jSONObject2.getBoolean(IS_THIS_DEVICE_KEY), AccountDevice.DeviceType.valueOf(jSONObject2.getString(DEVICE_TYPE_KEY))));
                    } catch (JSONException e2) {
                    }
                }
            } catch (JSONException e3) {
            }
        } catch (JSONException e4) {
            throw new IllegalArgumentException("Unable to parse JSON object");
        }
    }

    private void setIdentifier(String str) {
        if (str != null) {
            String[] split = str.split("@");
            if (split.length == 2) {
                this.identifier = split[0] + "@" + split[1].toLowerCase(Locale.ENGLISH);
            }
        }
    }

    public void addDevice(AccountDevice accountDevice) {
        if (!this.devices.containsKey(accountDevice.getDeviceId())) {
            this.devices.put(accountDevice.getDeviceId(), accountDevice);
            return;
        }
        AccountDevice accountDevice2 = this.devices.get(accountDevice.getDeviceId());
        accountDevice2.setName(accountDevice.getName());
        accountDevice2.setLastCheckin(accountDevice.getLastCheckin());
    }

    public void addDevices(List<AccountDevice> list) {
        HashSet hashSet = new HashSet();
        for (AccountDevice accountDevice : list) {
            addDevice(accountDevice);
            hashSet.add(accountDevice.getDeviceId());
        }
        this.devices.keySet().retainAll(hashSet);
    }

    public String getAccountId() {
        return this.accountId;
    }

    public AccountState getAccountState() {
        return this.accountState;
    }

    public CreationMethod getCreationMethod() {
        return this.creationMethod;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public int getDeviceCount() {
        return this.deviceCount;
    }

    public Map<String, AccountDevice> getDevices() {
        return this.devices;
    }

    public AccountDevice[] getDevicesArray() {
        AccountDevice[] accountDeviceArr = new AccountDevice[this.devices.size()];
        Iterator<Map.Entry<String, AccountDevice>> it = this.devices.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            accountDeviceArr[i] = it.next().getValue();
            i++;
        }
        return accountDeviceArr;
    }

    public boolean getExistsOnServer() {
        return this.existsOnServer;
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

    public boolean isDeleted() {
        return this.accountState.equals(AccountState.PENDING_DELETE);
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

    public void setAccountId(String str) {
        this.accountId = str;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public void setCreationTimeSeconds(int i) {
        this.creationTime = i * 1000;
    }

    public void setDeviceCount(int i) {
        this.deviceCount = i;
    }

    public void setExistsOnServer(boolean z) {
        this.existsOnServer = z;
    }

    public String toJsonString() {
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("IDENTIFIER", getIdentifier());
            jSONObject.put(DISPLAY_IDENTIFIER_KEY, getIdentifierForDisplay());
            jSONObject.put(ACCOUNT_ID_KEY, getAccountId());
            jSONObject.put(TYPE_KEY, getType());
            jSONObject.put(EXISTS_ON_SERVER_KEY, getExistsOnServer());
            jSONObject.put(CREATION_METHOD_KEY, getCreationMethod().name());
            jSONObject.put(ACCOUNT_STATE_KEY, getAccountState().name());
            Iterator<Map.Entry<String, AccountDevice>> it = getDevices().entrySet().iterator();
            while (it.hasNext()) {
                AccountDevice value = it.next().getValue();
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("DEVICE_ID", value.getDeviceId());
                jSONObject2.put(DEVICE_NAME_KEY, value.getName());
                jSONObject2.put(DEVICE_TYPE_KEY, value.getType().name());
                jSONObject2.put(DEVICE_LAST_CHECKIN_KEY, value.getLastCheckin());
                jSONObject2.put(IS_THIS_DEVICE_KEY, value.getIsThisDevice());
                jSONObject2.put("STATUS", value.getDeviceStatus().name());
                jSONArray.put(jSONObject2);
            }
            jSONObject.put(ACCOUNT_DEVICES_KEY, jSONArray);
            jSONObject.put(VERSION, serialVersionUID);
            return jSONObject.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public String toString() {
        return "Account [ id: " + this.accountId + "\nidentifier: " + this.identifier + "\ntype: " + this.type + "\nexistsOnServer: " + this.existsOnServer + "\ncreationMethod: " + this.creationMethod + "\naccountState: " + this.accountState + "\ndeviceCount: " + this.deviceCount + "\n]";
    }

    public void verify() {
        this.accountState = AccountState.VERIFIED;
    }
}
