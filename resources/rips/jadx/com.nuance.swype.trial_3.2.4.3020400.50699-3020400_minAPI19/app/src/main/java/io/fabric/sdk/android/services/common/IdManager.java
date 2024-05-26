package io.fabric.sdk.android.services.common;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class IdManager {
    public final Context appContext;
    public final String appIdentifier;
    private final String appInstallIdentifier;
    public final boolean collectHardwareIds;
    public final boolean collectUserIds;
    private final ReentrantLock installationIdLock = new ReentrantLock();
    private final InstallerPackageNameProvider installerPackageNameProvider;
    private final Collection<Kit> kits;
    private static final Pattern ID_PATTERN = Pattern.compile("[^\\p{Alnum}]");
    private static final String FORWARD_SLASH_REGEX = Pattern.quote("/");

    /* loaded from: classes.dex */
    public enum DeviceIdentifierType {
        WIFI_MAC_ADDRESS(1),
        BLUETOOTH_MAC_ADDRESS(2),
        FONT_TOKEN(53),
        ANDROID_ID(100),
        ANDROID_DEVICE_ID(101),
        ANDROID_SERIAL(102),
        ANDROID_ADVERTISING_ID(103);

        public final int protobufIndex;

        DeviceIdentifierType(int pbufIndex) {
            this.protobufIndex = pbufIndex;
        }
    }

    public IdManager(Context appContext, String appIdentifier, String appInstallIdentifier, Collection<Kit> kits) {
        if (appContext == null) {
            throw new IllegalArgumentException("appContext must not be null");
        }
        if (appIdentifier == null) {
            throw new IllegalArgumentException("appIdentifier must not be null");
        }
        if (kits == null) {
            throw new IllegalArgumentException("kits must not be null");
        }
        this.appContext = appContext;
        this.appIdentifier = appIdentifier;
        this.appInstallIdentifier = appInstallIdentifier;
        this.kits = kits;
        this.installerPackageNameProvider = new InstallerPackageNameProvider();
        this.collectHardwareIds = CommonUtils.getBooleanResourceValue(appContext, "com.crashlytics.CollectDeviceIdentifiers", true);
        if (!this.collectHardwareIds) {
            Fabric.getLogger();
            new StringBuilder("Device ID collection disabled for ").append(appContext.getPackageName());
        }
        this.collectUserIds = CommonUtils.getBooleanResourceValue(appContext, "com.crashlytics.CollectUserIdentifiers", true);
        if (!this.collectUserIds) {
            Fabric.getLogger();
            new StringBuilder("User information collection disabled for ").append(appContext.getPackageName());
        }
    }

    public final String createIdHeaderValue(String apiKey, String packageName) {
        try {
            String replacementString = "slc";
            StringBuilder replacement = new StringBuilder(replacementString);
            String obsId = packageName.replaceAll("\\.", replacement.reverse().toString());
            Cipher cipher = CommonUtils.createCipher$4ef6f629(CommonUtils.sha1(apiKey + obsId));
            JSONObject ids = new JSONObject();
            try {
                ids.put("APPLICATION_INSTALLATION_UUID".toLowerCase(Locale.US), getAppInstallIdentifier());
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Could not write application id to JSON", e);
            }
            addDeviceIdentifiersTo(ids);
            try {
                ids.put("os_version", getOsVersionString());
            } catch (Exception e2) {
                Fabric.getLogger().e("Fabric", "Could not write OS version to JSON", e2);
            }
            try {
                ids.put("model", getModelName());
            } catch (Exception e3) {
                Fabric.getLogger().e("Fabric", "Could not write model to JSON", e3);
            }
            if (ids.length() <= 0) {
                return "";
            }
            try {
                String toReturn = CommonUtils.hexify(cipher.doFinal(ids.toString().getBytes()));
                return toReturn;
            } catch (GeneralSecurityException e4) {
                Fabric.getLogger().e("Fabric", "Could not encrypt IDs", e4);
                return "";
            }
        } catch (GeneralSecurityException e5) {
            Fabric.getLogger().e("Fabric", "Could not create cipher to encrypt headers.", e5);
            return "";
        }
    }

    private void addDeviceIdentifiersTo(JSONObject ids) {
        for (Map.Entry<DeviceIdentifierType, String> id : getDeviceIdentifiers().entrySet()) {
            try {
                ids.put(id.getKey().name().toLowerCase(Locale.US), id.getValue());
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Could not write value to JSON: " + id.getKey().name(), e);
            }
        }
    }

    private boolean hasPermission(String permission) {
        return this.appContext.checkCallingPermission(permission) == 0;
    }

    private static String formatId(String id) {
        if (id == null) {
            return null;
        }
        return ID_PATTERN.matcher(id).replaceAll("").toLowerCase(Locale.US);
    }

    public final String getAppInstallIdentifier() {
        String appInstallId = this.appInstallIdentifier;
        if (appInstallId == null) {
            SharedPreferences prefs = CommonUtils.getSharedPrefs(this.appContext);
            String appInstallId2 = prefs.getString("crashlytics.installation.id", null);
            if (appInstallId2 == null) {
                return createInstallationUUID(prefs);
            }
            return appInstallId2;
        }
        return appInstallId;
    }

    public static String getOsVersionString() {
        return String.format(Locale.US, "%s/%s", removeForwardSlashesIn(Build.VERSION.RELEASE), removeForwardSlashesIn(Build.VERSION.INCREMENTAL));
    }

    public static String getModelName() {
        return String.format(Locale.US, "%s/%s", removeForwardSlashesIn(Build.MANUFACTURER), removeForwardSlashesIn(Build.MODEL));
    }

    private static String removeForwardSlashesIn(String s) {
        return s.replaceAll(FORWARD_SLASH_REGEX, "");
    }

    public final String createInstallationUUID(SharedPreferences prefs) {
        this.installationIdLock.lock();
        try {
            String uuid = prefs.getString("crashlytics.installation.id", null);
            if (uuid == null) {
                uuid = formatId(UUID.randomUUID().toString());
                prefs.edit().putString("crashlytics.installation.id", uuid).commit();
            }
            return uuid;
        } finally {
            this.installationIdLock.unlock();
        }
    }

    public final Map<DeviceIdentifierType, String> getDeviceIdentifiers() {
        WifiManager wifiManager;
        WifiInfo connectionInfo;
        TelephonyManager telephonyManager;
        String str = null;
        Map<DeviceIdentifierType, String> ids = new HashMap<>();
        for (Object obj : this.kits) {
            if (obj instanceof DeviceIdentifierProvider) {
                for (Map.Entry<DeviceIdentifierType, String> entry : ((DeviceIdentifierProvider) obj).getDeviceIdentifiers().entrySet()) {
                    putNonNullIdInto(ids, entry.getKey(), entry.getValue());
                }
            }
        }
        putNonNullIdInto(ids, DeviceIdentifierType.ANDROID_ID, getAndroidId());
        putNonNullIdInto(ids, DeviceIdentifierType.ANDROID_DEVICE_ID, (this.collectHardwareIds && hasPermission("android.permission.READ_PHONE_STATE") && (telephonyManager = (TelephonyManager) this.appContext.getSystemService("phone")) != null) ? formatId(telephonyManager.getDeviceId()) : null);
        putNonNullIdInto(ids, DeviceIdentifierType.ANDROID_SERIAL, getSerialNumber());
        putNonNullIdInto(ids, DeviceIdentifierType.WIFI_MAC_ADDRESS, (!this.collectHardwareIds || !hasPermission("android.permission.ACCESS_WIFI_STATE") || (wifiManager = (WifiManager) this.appContext.getSystemService("wifi")) == null || (connectionInfo = wifiManager.getConnectionInfo()) == null) ? null : formatId(connectionInfo.getMacAddress()));
        putNonNullIdInto(ids, DeviceIdentifierType.BLUETOOTH_MAC_ADDRESS, getBluetoothMacAddress());
        DeviceIdentifierType deviceIdentifierType = DeviceIdentifierType.ANDROID_ADVERTISING_ID;
        if (this.collectHardwareIds) {
            final AdvertisingInfoProvider advertisingInfoProvider = new AdvertisingInfoProvider(this.appContext);
            final AdvertisingInfo advertisingInfo = new AdvertisingInfo(advertisingInfoProvider.preferenceStore.get().getString("advertising_id", ""), advertisingInfoProvider.preferenceStore.get().getBoolean("limit_ad_tracking_enabled", false));
            if (AdvertisingInfoProvider.isInfoValid(advertisingInfo)) {
                Fabric.getLogger();
                new Thread(new BackgroundPriorityRunnable() { // from class: io.fabric.sdk.android.services.common.AdvertisingInfoProvider.1
                    @Override // io.fabric.sdk.android.services.common.BackgroundPriorityRunnable
                    public final void onRun() {
                        AdvertisingInfo infoToStore = AdvertisingInfoProvider.this.getAdvertisingInfoFromStrategies();
                        if (!advertisingInfo.equals(infoToStore)) {
                            Fabric.getLogger();
                            AdvertisingInfoProvider.this.storeInfoToPreferences(infoToStore);
                        }
                    }
                }).start();
            } else {
                advertisingInfo = advertisingInfoProvider.getAdvertisingInfoFromStrategies();
                advertisingInfoProvider.storeInfoToPreferences(advertisingInfo);
            }
            if (advertisingInfo != null) {
                str = advertisingInfo.advertisingId;
            }
        }
        putNonNullIdInto(ids, deviceIdentifierType, str);
        return Collections.unmodifiableMap(ids);
    }

    public final String getInstallerPackageName() {
        return this.installerPackageNameProvider.getInstallerPackageName(this.appContext);
    }

    private static void putNonNullIdInto(Map<DeviceIdentifierType, String> idMap, DeviceIdentifierType idKey, String idValue) {
        if (idValue != null) {
            idMap.put(idKey, idValue);
        }
    }

    public final String getAndroidId() {
        if (!this.collectHardwareIds) {
            return null;
        }
        String androidId = Settings.Secure.getString(this.appContext.getContentResolver(), "android_id");
        if ("9774d56d682e549c".equals(androidId)) {
            return null;
        }
        String toReturn = formatId(androidId);
        return toReturn;
    }

    public final String getBluetoothMacAddress() {
        if (this.collectHardwareIds && hasPermission("android.permission.BLUETOOTH")) {
            try {
                BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
                if (bt != null) {
                    formatId(bt.getAddress());
                    return null;
                }
                return null;
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Utils#getBluetoothMacAddress failed, returning null. Requires prior call to BluetoothAdatpter.getDefaultAdapter() on thread that has called Looper.prepare()", e);
                return null;
            }
        }
        return null;
    }

    private String getSerialNumber() {
        if (this.collectHardwareIds && Build.VERSION.SDK_INT >= 9) {
            try {
                return formatId((String) Build.class.getField("SERIAL").get(null));
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Could not retrieve android.os.Build.SERIAL value", e);
            }
        }
        return null;
    }
}
