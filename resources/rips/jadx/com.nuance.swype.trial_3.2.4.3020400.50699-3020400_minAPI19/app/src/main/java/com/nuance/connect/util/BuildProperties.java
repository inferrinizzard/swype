package com.nuance.connect.util;

import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.internal.Property;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.DeviceManager;
import com.nuance.connect.service.manager.LanguageManager;
import com.nuance.connect.util.BuildProps;
import com.nuance.connect.util.Logger;
import com.nuance.id.NuanceId;
import com.nuance.id.NuanceIdImpl;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class BuildProperties implements BuildProps {
    private final String[] DEVICE_PROPERTIES;
    private WeakReference<ConnectClient> context;
    private NuanceId nuanceId;
    private boolean tosAccepted;
    private static final String[] REQUIRED_DEVICE_PROPERTIES = {BuildProps.BuildProperty.LANGUAGES.getKey(), BuildProps.BuildProperty.SUPPORTED_LANGUAGES.getKey(), BuildProps.BuildProperty.MANUFACTURER.getKey(), BuildProps.BuildProperty.MODEL.getKey(), BuildProps.BuildProperty.SCREENRESOLUTION.getKey(), BuildProps.BuildProperty.IMEI_HASH.getKey(), BuildProps.BuildProperty.SERIAL_HASH.getKey(), BuildProps.BuildProperty.ANDROIDID_HASH.getKey(), BuildProps.BuildProperty.MAC_HASH.getKey()};
    private static final String[] IGNORE_CHANGE_TO_NULL = {BuildProps.BuildProperty.IMEI_HASH.getKey(), BuildProps.BuildProperty.SERIAL_HASH.getKey(), BuildProps.BuildProperty.ANDROIDID_HASH.getKey(), BuildProps.BuildProperty.MAC_HASH.getKey()};
    private static final String[] IGNORE_CHANGES_PROPERTIES = {BuildProps.BuildProperty.SCREENRESOLUTION.getKey(), BuildProps.BuildProperty.DEVICE_ID.getKey(), BuildProps.BuildProperty.SWYPER_ID.getKey()};
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final ArrayList<String> filterPreTos = new ArrayList<>();
    private final ArrayList<String> filterBlock = new ArrayList<>();
    private final ArrayList<String> criticalProperties = new ArrayList<>();
    private final Map<String, String> customProperties = new HashMap();
    private Property.ValueListener<Boolean> boolListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.util.BuildProperties.1
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            if (property.getKey().equals(ConnectConfiguration.ConfigProperty.TOS_ACCEPTED.name())) {
                BuildProperties.this.tosAccepted = property.getValue().booleanValue();
            }
        }
    };
    private ArrayList<String> USER_PROPERTIES = new ArrayList<>();

    public BuildProperties(ConnectClient connectClient) {
        this.context = new WeakReference<>(connectClient);
        connectClient.addListener(ConnectConfiguration.ConfigProperty.TOS_ACCEPTED, this.boolListener);
        String string = connectClient.getString(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_PRE_TOS);
        if (string != null) {
            String[] split = string.split(",");
            for (String str : split) {
                this.filterPreTos.add(str.trim());
            }
            this.log.d("Pre-Filter: ", this.filterPreTos);
        }
        String string2 = connectClient.getString(ConnectConfiguration.ConfigProperty.BUILD_PROPERTIES_FILTER_BLOCK);
        if (string2 != null) {
            String[] split2 = string2.split(",");
            for (String str2 : split2) {
                this.filterBlock.add(str2.trim());
            }
            this.log.d("Block Filter: ", this.filterBlock);
        }
        Iterator<BuildProps.BuildProperty> it = BuildProps.BuildProperty.getCriticalProperties().iterator();
        while (it.hasNext()) {
            this.criticalProperties.add(it.next().getKey());
        }
        BuildProps.BuildProperty[] values = BuildProps.BuildProperty.values();
        this.DEVICE_PROPERTIES = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            this.DEVICE_PROPERTIES[i] = values[i].getKey();
        }
        this.USER_PROPERTIES.add(BuildProps.BuildProperty.IMEI_HASH.getKey());
        this.USER_PROPERTIES.add(BuildProps.BuildProperty.SERIAL_HASH.getKey());
        this.USER_PROPERTIES.add(BuildProps.BuildProperty.ANDROIDID_HASH.getKey());
        this.USER_PROPERTIES.add(BuildProps.BuildProperty.MAC_HASH.getKey());
    }

    private boolean checkUserProperties() {
        ConnectClient connectClient = this.context.get();
        if (connectClient != null) {
            return connectClient.getBoolean(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES).booleanValue();
        }
        return true;
    }

    protected static String getCompatibilityDeviceProperty(String str) {
        try {
            Field declaredField = Build.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(Build.class).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String readString(String str, String str2) {
        ConnectClient connectClient = this.context.get();
        return connectClient != null ? connectClient.getDataStore().readString(str, str2) : str2;
    }

    @Override // com.nuance.connect.util.BuildProps
    public String[] compareDeviceProperties(HashMap<String, String> hashMap, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        boolean checkUserProperties = checkUserProperties();
        if (strArr == null || strArr.length == 0) {
            strArr = getAllDeviceProperties();
        }
        HashSet hashSet = new HashSet(Arrays.asList(IGNORE_CHANGES_PROPERTIES));
        HashSet hashSet2 = new HashSet(Arrays.asList(IGNORE_CHANGE_TO_NULL));
        HashMap<String, String> deviceProperties = getDeviceProperties();
        if (hashMap != null) {
            for (String str : strArr) {
                if ((checkUserProperties || !this.USER_PROPERTIES.contains(str)) && ((!hashSet2.contains(str) || (deviceProperties.get(str) != null && deviceProperties.get(str).length() != 0)) && !hashSet.contains(str))) {
                    if (deviceProperties.get(str) == null) {
                        if (hashMap.get(str) != null) {
                            arrayList.add(str);
                        }
                    } else if (!hashMap.containsKey(str) || !deviceProperties.get(str).equals(hashMap.get(str))) {
                        arrayList.add(str);
                    }
                }
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // com.nuance.connect.util.BuildProps
    public String get(BuildProps.BuildProperty buildProperty) {
        if (buildProperty != null) {
            return get(buildProperty.getKey());
        }
        return null;
    }

    @Override // com.nuance.connect.util.BuildProps
    public String get(String str) {
        this.nuanceId = new NuanceId(this.context.get());
        return getDeviceProperty(str);
    }

    String[] getAllDeviceProperties() {
        String[] strArr = new String[this.DEVICE_PROPERTIES.length + this.customProperties.size()];
        for (int i = 0; i < this.DEVICE_PROPERTIES.length; i++) {
            strArr[i] = this.DEVICE_PROPERTIES[i];
        }
        int length = this.DEVICE_PROPERTIES.length;
        Iterator<String> it = this.customProperties.keySet().iterator();
        while (true) {
            int i2 = length;
            if (!it.hasNext()) {
                return strArr;
            }
            strArr[i2] = it.next();
            length = i2 + 1;
        }
    }

    @Override // com.nuance.connect.util.BuildProps
    public Set<String> getCriticalProperties() {
        return new HashSet(this.criticalProperties);
    }

    @Override // com.nuance.connect.util.BuildProps
    public HashMap<String, String> getDeviceProperties() {
        HashMap<String, String> hashMap = new HashMap<>();
        boolean checkUserProperties = checkUserProperties();
        this.nuanceId = new NuanceId(this.context.get());
        for (String str : this.DEVICE_PROPERTIES) {
            if (checkUserProperties || !this.USER_PROPERTIES.contains(str)) {
                hashMap.put(str, getDeviceProperty(str));
            }
        }
        for (Map.Entry<String, String> entry : this.customProperties.entrySet()) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getDeviceProperty(String str) {
        return getFilteredDeviceProperty(str);
    }

    @Override // com.nuance.connect.util.BuildProps
    public String[] hasRequiredDeviceProperties(HashMap<String, String> hashMap) {
        ArrayList arrayList = new ArrayList();
        boolean checkUserProperties = checkUserProperties();
        for (String str : REQUIRED_DEVICE_PROPERTIES) {
            if ((checkUserProperties || !this.USER_PROPERTIES.contains(str)) && !hashMap.containsKey(str)) {
                arrayList.add(str);
            }
        }
        if (!hashMap.containsKey(BuildProps.BuildProperty.SWIB.getKey()) && !hashMap.containsKey(BuildProps.BuildProperty.OEM_ID.getKey())) {
            arrayList.add(BuildProps.BuildProperty.SWIB.getKey());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // com.nuance.connect.util.BuildProps
    public void setCustomProperty(String str, String str2, boolean z) {
        if (BuildProps.BuildProperty.isKnownProperty(str)) {
            this.log.w("setCustomProperty called with known property name: ", str, "; ignoring...");
            return;
        }
        this.customProperties.put(str, str2);
        if (!z || this.criticalProperties.contains(str)) {
            return;
        }
        this.criticalProperties.add(str);
    }

    protected String getFilteredDeviceProperty(String str) {
        int i;
        if (this.tosAccepted || !this.filterPreTos.contains(str)) {
            if (this.filterBlock.contains(str)) {
                if (!this.criticalProperties.contains(str)) {
                    this.log.d("Filtering: ", str, "; property is in block list.");
                    return null;
                }
                this.log.d("Request to filter: ", str, " denied; property is in critical list.");
            }
        } else {
            if (!this.criticalProperties.contains(str)) {
                this.log.d("Filtering: ", str, "; TOS not accepted");
                return null;
            }
            this.log.d("Request to filter: ", str, " denied; property is in critical list.");
        }
        BuildProps.BuildProperty from = BuildProps.BuildProperty.from(str);
        switch (from) {
            case ID:
            case PRODUCT:
            case DEVICE:
            case BOARD:
            case MANUFACTURER:
            case BRAND:
            case MODEL:
            case BOOTLOADER:
            case CPU_ABI:
            case CPU_ABI2:
            case DISPLAY:
            case HARDWARE:
            case RADIO:
            case TAGS:
            case TYPE:
                return getCompatibilityDeviceProperty(from.getKey());
            case SWYPE_VERSION:
                return "";
            case OS_VERSION:
                return Build.VERSION.RELEASE;
            case SCREENRESOLUTION:
                ConnectClient connectClient = this.context.get();
                if (connectClient != null) {
                    Display defaultDisplay = ((WindowManager) connectClient.getApplicationContext().getSystemService("window")).getDefaultDisplay();
                    int width = defaultDisplay.getWidth();
                    int height = defaultDisplay.getHeight();
                    return Math.max(width, height) + "x" + Math.min(width, height);
                }
                break;
            case LANGUAGES:
                return readString(LanguageManager.LANGUAGE_PREINSTALL_LIST_PREF, "");
            case SUPPORTED_LANGUAGES:
                return readString(LanguageManager.LANGUAGE_SUPPORTED_LIST_PREF, "");
            case SWIB:
                ConnectClient connectClient2 = this.context.get();
                if (connectClient2 != null) {
                    return connectClient2.getString(ConnectConfiguration.ConfigProperty.SWIB);
                }
                break;
            case OEM_ID:
                ConnectClient connectClient3 = this.context.get();
                if (connectClient3 != null) {
                    return connectClient3.getString(ConnectConfiguration.ConfigProperty.OEM_ID);
                }
                break;
            case SDK_VERSION:
                ConnectClient connectClient4 = this.context.get();
                if (connectClient4 != null) {
                    return connectClient4.getString(ConnectConfiguration.ConfigProperty.SDK_VERSION);
                }
                break;
            case APPLICATION_ID:
                ConnectClient connectClient5 = this.context.get();
                if (connectClient5 != null) {
                    return connectClient5.getString(ConnectConfiguration.ConfigProperty.APPLICATION_ID);
                }
                break;
            case CUSTOMER_STRING:
                ConnectClient connectClient6 = this.context.get();
                if (connectClient6 != null) {
                    return connectClient6.getString(ConnectConfiguration.ConfigProperty.CUSTOMER_STRING);
                }
                break;
            case DEVICE_ID:
                return readString(DeviceManager.DEVICE_PREFERENCE, null);
            case TIMEZONE:
                return Calendar.getInstance().getTimeZone().getID();
            case SWYPER_ID:
                return readString(DeviceManager.SWYPER_ID_PREFERENCE, null);
            case LANGUAGES_DL:
                return readString(LanguageManager.USER_LANGUAGES_PREF, null);
            case ALM_DL:
                return null;
            case SWYPE_PRIVACY_ENABLED:
                return null;
            case SWYPE_BUILD_TYPE:
                ConnectClient connectClient7 = this.context.get();
                if (connectClient7 != null) {
                    return connectClient7.getString(ConnectConfiguration.ConfigProperty.BUILD_TYPE);
                }
                break;
            case SCREENLAYOUT_SIZE:
                ConnectClient connectClient8 = this.context.get();
                return connectClient8 != null ? String.valueOf(connectClient8.getApplicationContext().getResources().getConfiguration().screenLayout & 15) : "0";
            case SCREEN_DENSITY:
                ConnectClient connectClient9 = this.context.get();
                if (connectClient9 != null) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((WindowManager) connectClient9.getApplicationContext().getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
                    i = displayMetrics.densityDpi;
                } else {
                    i = 160;
                }
                return String.valueOf(i);
            case IMEI_HASH:
                NuanceId nuanceId = this.nuanceId;
                nuanceId.getId();
                return nuanceId.id.substring(0, nuanceId.id.length() / 4);
            case SERIAL_HASH:
                NuanceId nuanceId2 = this.nuanceId;
                nuanceId2.getId();
                int length = nuanceId2.id.length() / 4;
                return nuanceId2.id.substring(length, length * 2);
            case ANDROIDID_HASH:
                NuanceId nuanceId3 = this.nuanceId;
                nuanceId3.getId();
                return nuanceId3.id.substring((nuanceId3.id.length() / 4) * 3, nuanceId3.id.length());
            case MAC_HASH:
                NuanceId nuanceId4 = this.nuanceId;
                nuanceId4.getId();
                int length2 = nuanceId4.id.length() / 4;
                return nuanceId4.id.substring(length2 * 2, length2 * 3);
            case NAD_ID:
                return NuanceIdImpl.sha1hash(this.nuanceId.getAndroidId());
            case LOCALE:
                return Locale.getDefault().toString();
            case CORE_VERSION_ALPHA:
                ConnectClient connectClient10 = this.context.get();
                if (connectClient10 != null) {
                    return connectClient10.getString(ConnectConfiguration.ConfigProperty.CORE_VERSION_ALPHA);
                }
                break;
            case CORE_VERSION_CHINESE:
                ConnectClient connectClient11 = this.context.get();
                if (connectClient11 != null) {
                    return connectClient11.getString(ConnectConfiguration.ConfigProperty.CORE_VERSION_CHINESE);
                }
                break;
            case CORE_VERSION_JAPANESE:
                ConnectClient connectClient12 = this.context.get();
                if (connectClient12 != null) {
                    return connectClient12.getString(ConnectConfiguration.ConfigProperty.CORE_VERSION_JAPANESE);
                }
                break;
            case CORE_VERSION_KOREAN:
                ConnectClient connectClient13 = this.context.get();
                if (connectClient13 != null) {
                    return connectClient13.getString(ConnectConfiguration.ConfigProperty.CORE_VERSION_KOREAN);
                }
                break;
            case DOCUMENT_REVISIONS:
                ConnectClient connectClient14 = this.context.get();
                if (connectClient14 != null) {
                    return connectClient14.getString(ConnectConfiguration.ConfigProperty.DOCUMENT_REVISIONS);
                }
                break;
            case CARRIER_NAME:
                ConnectClient connectClient15 = this.context.get();
                if (connectClient15 != null) {
                    return String.valueOf(((TelephonyManager) connectClient15.getSystemService("phone")).getNetworkOperatorName());
                }
                break;
            case SIM_OPERATOR_NAME:
                ConnectClient connectClient16 = this.context.get();
                if (connectClient16 != null) {
                    TelephonyManager telephonyManager = (TelephonyManager) connectClient16.getSystemService("phone");
                    if (telephonyManager.getSimState() == 5) {
                        return String.valueOf(telephonyManager.getSimOperatorName());
                    }
                }
                break;
            case PHONE_SIM_TYPE:
                ConnectClient connectClient17 = this.context.get();
                if (connectClient17 != null) {
                    return String.valueOf(((TelephonyManager) connectClient17.getSystemService("phone")).getPhoneType());
                }
                break;
            case NETWORK_ISO:
                ConnectClient connectClient18 = this.context.get();
                if (connectClient18 != null) {
                    return String.valueOf(((TelephonyManager) connectClient18.getSystemService("phone")).getNetworkCountryIso());
                }
                break;
            case SIM_ISO:
                ConnectClient connectClient19 = this.context.get();
                if (connectClient19 != null) {
                    return String.valueOf(((TelephonyManager) connectClient19.getSystemService("phone")).getSimCountryIso());
                }
                break;
            case NETWORK_OP:
                ConnectClient connectClient20 = this.context.get();
                if (connectClient20 != null) {
                    return String.valueOf(((TelephonyManager) connectClient20.getSystemService("phone")).getNetworkOperator());
                }
                break;
            case SIM_OP:
                ConnectClient connectClient21 = this.context.get();
                if (connectClient21 != null) {
                    TelephonyManager telephonyManager2 = (TelephonyManager) connectClient21.getSystemService("phone");
                    if (telephonyManager2.getSimState() == 5) {
                        return String.valueOf(telephonyManager2.getSimOperator());
                    }
                }
                break;
            case PACKAGE_NAME:
                ConnectClient connectClient22 = this.context.get();
                if (connectClient22 != null) {
                    return connectClient22.getPackageName();
                }
                break;
            case GIP_COUNTRY:
                ConnectClient connectClient23 = this.context.get();
                if (connectClient23 != null) {
                    return connectClient23.getString(ConnectConfiguration.ConfigProperty.LOCATION_GEO_IP_COUNTRY);
                }
                break;
            case SKUS_PURCHASED:
                ConnectClient connectClient24 = this.context.get();
                if (connectClient24 != null) {
                    return connectClient24.getString(ConnectConfiguration.ConfigProperty.CATALOG_SKU_LIST);
                }
                break;
            case POLL_FREQUENCY:
                ConnectClient connectClient25 = this.context.get();
                if (connectClient25 != null) {
                    return String.valueOf(connectClient25.getInteger(ConnectConfiguration.ConfigProperty.POLLING_FREQUENCY));
                }
                break;
            case FEATURE_USED_LANGUAGE:
                ConnectClient connectClient26 = this.context.get();
                if (connectClient26 != null) {
                    return connectClient26.getUsedLastForFeature(FeaturesLastUsed.Feature.LANGUAGE);
                }
                break;
            case FEATURE_USED_UDA:
                ConnectClient connectClient27 = this.context.get();
                if (connectClient27 != null) {
                    return connectClient27.getUsedLastForFeature(FeaturesLastUsed.Feature.LLUDA);
                }
                break;
            case FEATURE_USED_HOTWORDS:
                ConnectClient connectClient28 = this.context.get();
                if (connectClient28 != null) {
                    return connectClient28.getUsedLastForFeature(FeaturesLastUsed.Feature.HOTWORDS);
                }
                break;
            case FEATURE_USED_REPORTING:
                ConnectClient connectClient29 = this.context.get();
                if (connectClient29 != null) {
                    return connectClient29.getUsedLastForFeature(FeaturesLastUsed.Feature.REPORTING);
                }
                break;
            case FEATURE_USED_CHINESEDICT:
                ConnectClient connectClient30 = this.context.get();
                if (connectClient30 != null) {
                    return connectClient30.getUsedLastForFeature(FeaturesLastUsed.Feature.CHINESEDICTIONARIES);
                }
                break;
            case FEATURE_USED_CCPS:
                ConnectClient connectClient31 = this.context.get();
                if (connectClient31 != null) {
                    return connectClient31.getUsedLastForFeature(FeaturesLastUsed.Feature.CCPS);
                }
                break;
            case FEATURE_USED_DLT:
                ConnectClient connectClient32 = this.context.get();
                if (connectClient32 != null) {
                    return connectClient32.getUsedLastForFeature(FeaturesLastUsed.Feature.DLT);
                }
                break;
            case FEATURE_USED_BACKUP_SYNC:
                ConnectClient connectClient33 = this.context.get();
                if (connectClient33 != null) {
                    return connectClient33.getUsedLastForFeature(FeaturesLastUsed.Feature.BACKUP_SYNC);
                }
                break;
            case FEATURE_USED_SCANNER_CALLLOG:
                ConnectClient connectClient34 = this.context.get();
                if (connectClient34 != null) {
                    return connectClient34.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_CALLLOG);
                }
                break;
            case FEATURE_USED_SCANNER_CONTACTS:
                ConnectClient connectClient35 = this.context.get();
                if (connectClient35 != null) {
                    return connectClient35.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_CONTACTS);
                }
                break;
            case FEATURE_USED_SCANNER_FACEBOOK:
                ConnectClient connectClient36 = this.context.get();
                if (connectClient36 != null) {
                    return connectClient36.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_FACEBOOK);
                }
                break;
            case FEATURE_USED_SCANNER_GMAIL:
                ConnectClient connectClient37 = this.context.get();
                if (connectClient37 != null) {
                    return connectClient37.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_GMAIL);
                }
                break;
            case FEATURE_USED_SCANNER_SMS:
                ConnectClient connectClient38 = this.context.get();
                if (connectClient38 != null) {
                    return connectClient38.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_SMS);
                }
                break;
            case FEATURE_USED_SCANNER_TWITTER:
                ConnectClient connectClient39 = this.context.get();
                if (connectClient39 != null) {
                    return connectClient39.getUsedLastForFeature(FeaturesLastUsed.Feature.SCANNER_TWITTER);
                }
                break;
            default:
                return null;
        }
        this.log.d("unable to retrieve property: " + str);
        return null;
    }
}
