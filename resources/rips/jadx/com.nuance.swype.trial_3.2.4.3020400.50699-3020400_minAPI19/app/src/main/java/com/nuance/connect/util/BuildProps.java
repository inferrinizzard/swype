package com.nuance.connect.util;

import com.nuance.connect.common.Strings;
import com.nuance.connect.service.manager.DeviceManager;
import com.nuance.connect.service.manager.LanguageManager;
import com.nuance.swypeconnect.ac.ACManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public interface BuildProps {

    /* loaded from: classes.dex */
    public enum BuildProperty {
        ANDROIDID_HASH("ANDROIDID_HASH", false),
        MAC_HASH("MAC_HASH", false),
        SERIAL_HASH("SERIAL_HASH", false),
        IMEI_HASH("IMEI_HASH", false),
        NAD_ID("NAD_ID", false),
        SWYPE_PRIVACY_ENABLED("SWYPE_PRIVACY_ENABLED", false),
        ALM_DL("ALM_DL", false),
        LANGUAGES_DL(LanguageManager.USER_LANGUAGES_PREF, false),
        SWYPER_ID(DeviceManager.SWYPER_ID_PREFERENCE, true),
        SWIB("SWIB", false),
        TIMEZONE("TIMEZONE", false),
        TYPE("TYPE", false),
        TAGS("TAGS", false),
        RADIO("RADIO", false),
        HARDWARE("HARDWARE", false),
        DISPLAY("DISPLAY", false),
        CPU_ABI2("CPU_ABI2", false),
        CPU_ABI("CPU_ABI", false),
        BOOTLOADER("BOOTLOADER", false),
        LANGUAGES("LANGUAGES", false),
        SUPPORTED_LANGUAGES("SUPPORTED_LANGUAGES", false),
        SCREENRESOLUTION("SCREENRESOLUTION", false),
        SWYPE_VERSION("SWYPE_VERSION", false),
        SWYPE_BUILD_TYPE("SWYPE_BUILD_TYPE", false),
        OS_VERSION("OS_VERSION", false),
        SWYPE_PROJECT_NAME("SWYPE_PROJECT_NAME", false),
        SWYPE_OEM_NAME("SWYPE_OEM_NAME", false),
        MODEL("MODEL", false),
        BRAND("BRAND", false),
        BOARD("BOARD", false),
        DEVICE(ACManager.DEVICE_SERVICE, false),
        PRODUCT("PRODUCT", false),
        DEVICE_ID(DeviceManager.DEVICE_PREFERENCE, true),
        MANUFACTURER("MANUFACTURER", false),
        SCREENLAYOUT_SIZE("SCREENLAYOUT_SIZE", false),
        ID("ID", false),
        SCREEN_DENSITY("SCREEN_DENSITY", false),
        LOCALE(Strings.LOCALE, true),
        OEM_ID("OEM_ID", true),
        SDK_VERSION("SDK_VERSION", true),
        APPLICATION_ID("APPLICATION_ID", true),
        CUSTOMER_STRING("CUSTOMER_STRING", true),
        CORE_VERSION_ALPHA("CORE_VERSION_ALPHA", true),
        CORE_VERSION_CHINESE("CORE_VERSION_CHINESE", true),
        CORE_VERSION_JAPANESE("CORE_VERSION_JAPANESE", true),
        CORE_VERSION_KOREAN("CORE_VERSION_KOREAN", true),
        DOCUMENT_REVISIONS("DOCUMENT_REVISIONS", true),
        PACKAGE_NAME("PACKAGE_NAME", false),
        CARRIER_NAME("CARRIER_NAME", false),
        SIM_OPERATOR_NAME("SIM_OPERATOR_NAME", false),
        ANONYMOUS_BUILD("ANONYMOUS_BUILD", false),
        PHONE_SIM_TYPE("PHONE_SIM_TYPE", false),
        NETWORK_ISO("NETWORK_ISO", false),
        SIM_ISO("SIM_ISO", false),
        NETWORK_OP("NETWORK_OP", false),
        SIM_OP("SIM_OP", false),
        GIP_COUNTRY("GIP_COUNTRY", false),
        SKUS_PURCHASED("SKUS_PURCHASED", true),
        POLL_FREQUENCY("POLL_FREQ", true),
        FEATURE_USED_LANGUAGE("FEATURE_USED_LANGUAGE", false),
        FEATURE_USED_UDA("FEATURE_USED_UDA", false),
        FEATURE_USED_HOTWORDS("FEATURE_USED_HOTWORDS", false),
        FEATURE_USED_REPORTING("FEATURE_USED_REPORTING", false),
        FEATURE_USED_CHINESEDICT("FEATURE_USED_CHINESEDICT", false),
        FEATURE_USED_CCPS("FEATURE_USED_CCPS", false),
        FEATURE_USED_DLT("FEATURE_USED_DLT", false),
        FEATURE_USED_BACKUP_SYNC("FEATURE_USED_BACKUP_SYNC", false),
        FEATURE_USED_SCANNER_CALLLOG("FEATURE_USED_SCANNER_CALLLOG", false),
        FEATURE_USED_SCANNER_CONTACTS("FEATURE_USED_SCANNER_CONTACTS", false),
        FEATURE_USED_SCANNER_FACEBOOK("FEATURE_USED_SCANNER_FACEBOOK", false),
        FEATURE_USED_SCANNER_GMAIL("FEATURE_USED_SCANNER_GMAIL", false),
        FEATURE_USED_SCANNER_SMS("FEATURE_USED_SCANNER_SMS", false),
        FEATURE_USED_SCANNER_TWITTER("FEATURE_USED_SCANNER_TWITTER", false);

        private boolean critical;
        private String key;
        private static final HashMap<String, BuildProperty> propMap = new HashMap<>();
        private static final ArrayList<BuildProperty> criticalList = new ArrayList<>();

        static {
            for (BuildProperty buildProperty : values()) {
                propMap.put(buildProperty.getKey(), buildProperty);
                if (buildProperty.isCritical()) {
                    criticalList.add(buildProperty);
                }
            }
        }

        BuildProperty(String str, boolean z) {
            this.key = str;
            this.critical = z;
        }

        public static BuildProperty from(String str) {
            return propMap.get(str);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static List<BuildProperty> getCriticalProperties() {
            return criticalList;
        }

        public static boolean isKnownProperty(String str) {
            return propMap.containsKey(str);
        }

        public final String getKey() {
            return this.key;
        }

        public final boolean isCritical() {
            return this.critical;
        }
    }

    String[] compareDeviceProperties(HashMap<String, String> hashMap, String[] strArr);

    String get(BuildProperty buildProperty);

    String get(String str);

    Set<String> getCriticalProperties();

    HashMap<String, String> getDeviceProperties();

    String[] hasRequiredDeviceProperties(HashMap<String, String> hashMap);

    void setCustomProperty(String str, String str2, boolean z);
}
