package com.nuance.connect.util;

import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.util.BuildProps;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class PrivateBuildProperties implements BuildProps {
    private final String[] DEVICE_PROPERTIES;
    private WeakReference<ConnectClient> context;
    private final BuildProperties defaultProperties;
    private static final String[] REQUIRED_DEVICE_PROPERTIES = {BuildProps.BuildProperty.OEM_ID.getKey()};
    private static final String[] IGNORE_CHANGES_PROPERTIES = {BuildProps.BuildProperty.DEVICE_ID.getKey(), BuildProps.BuildProperty.SWYPER_ID.getKey()};
    private final Map<String, String> customProperties = new HashMap();
    private final Set<String> criticalProperties = new HashSet();
    private ArrayList<String> USER_PROPERTIES = new ArrayList<>();

    public PrivateBuildProperties(ConnectClient connectClient) {
        this.context = new WeakReference<>(connectClient);
        this.defaultProperties = new BuildProperties(connectClient);
        List<BuildProps.BuildProperty> criticalProperties = BuildProps.BuildProperty.getCriticalProperties();
        this.DEVICE_PROPERTIES = new String[criticalProperties.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= criticalProperties.size()) {
                return;
            }
            this.DEVICE_PROPERTIES[i2] = criticalProperties.get(i2).getKey();
            this.criticalProperties.add(criticalProperties.get(i2).getKey());
            i = i2 + 1;
        }
    }

    private boolean checkUserProperties() {
        ConnectClient connectClient = this.context.get();
        if (connectClient != null) {
            return connectClient.getBoolean(ConnectConfiguration.ConfigProperty.COLLECT_USER_PROPERTIES).booleanValue();
        }
        return true;
    }

    @Override // com.nuance.connect.util.BuildProps
    public String[] compareDeviceProperties(HashMap<String, String> hashMap, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        boolean checkUserProperties = checkUserProperties();
        if (strArr == null || strArr.length == 0) {
            strArr = this.DEVICE_PROPERTIES;
        }
        HashSet hashSet = new HashSet(Arrays.asList(IGNORE_CHANGES_PROPERTIES));
        HashMap<String, String> deviceProperties = getDeviceProperties();
        if (hashMap != null) {
            for (String str : strArr) {
                if ((checkUserProperties || !this.USER_PROPERTIES.contains(str)) && !hashSet.contains(str)) {
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
        return getDeviceProperty(str);
    }

    @Override // com.nuance.connect.util.BuildProps
    public Set<String> getCriticalProperties() {
        return new HashSet(this.criticalProperties);
    }

    @Override // com.nuance.connect.util.BuildProps
    public HashMap<String, String> getDeviceProperties() {
        HashMap<String, String> hashMap = new HashMap<>();
        boolean checkUserProperties = checkUserProperties();
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

    protected String getDeviceProperty(String str) {
        BuildProps.BuildProperty from = BuildProps.BuildProperty.from(str);
        if (from == null) {
            return this.customProperties.get(str);
        }
        switch (from) {
            case LANGUAGES:
            case SUPPORTED_LANGUAGES:
            case OEM_ID:
            case SDK_VERSION:
            case APPLICATION_ID:
            case CUSTOMER_STRING:
            case DEVICE_ID:
            case SWYPER_ID:
            case LOCALE:
            case CORE_VERSION_ALPHA:
            case CORE_VERSION_CHINESE:
            case CORE_VERSION_JAPANESE:
            case CORE_VERSION_KOREAN:
            case DOCUMENT_REVISIONS:
            case ANONYMOUS_BUILD:
                return this.defaultProperties.getDeviceProperty(from.getKey());
            default:
                return null;
        }
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
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // com.nuance.connect.util.BuildProps
    public void setCustomProperty(String str, String str2, boolean z) {
        if (!BuildProps.BuildProperty.isKnownProperty(str)) {
            this.customProperties.put(str, str2);
        }
        this.criticalProperties.add(str);
    }
}
