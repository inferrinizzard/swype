package com.nuance.connect.internal.common;

import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.service.manager.AccountManager;

/* loaded from: classes.dex */
public enum ManagerService {
    CONFIG("swypeconfig", ConnectFeature.CONFIG, new ManagerService[0]),
    LOCATION_BASED_SERVICE("lbs", null, new ManagerService[]{CONFIG}),
    DEVICE("device", null, new ManagerService[]{CONFIG, LOCATION_BASED_SERVICE}),
    SESSION("session", null, new ManagerService[]{DEVICE}),
    DOCUMENTS("docs", ConnectFeature.DOCUMENTS, new ManagerService[]{SESSION}),
    LANGUAGE(Strings.MESSAGE_BUNDLE_LANGUAGE, ConnectFeature.LANGUAGE, new ManagerService[]{SESSION, DOCUMENTS}),
    ACCOUNT(AccountManager.COMMAND_ACCOUNT, ConnectFeature.ACCOUNT, new ManagerService[]{SESSION, DOCUMENTS}),
    SYNC("dlm", ConnectFeature.SYNC, new ManagerService[]{SESSION, ACCOUNT}),
    CATEGORY("cdb", ConnectFeature.CATEGORY, new ManagerService[]{SESSION, DOCUMENTS, LANGUAGE}),
    REPORTING("report", ConnectFeature.REPORTING, new ManagerService[]{SESSION}),
    UPDATE("upgrade", ConnectFeature.UPDATE, new ManagerService[]{SESSION, CATEGORY});

    ManagerService[] managerDependencies;
    String managerName;
    ConnectFeature service;

    ManagerService(String str, ConnectFeature connectFeature, ManagerService[] managerServiceArr) {
        this.managerName = str;
        this.managerDependencies = managerServiceArr;
        this.service = connectFeature;
    }

    public static String managerForService(String str) {
        if (str != null) {
            for (ManagerService managerService : values()) {
                if (managerService.service != null && str.equals(managerService.service.name())) {
                    return managerService.managerName;
                }
            }
        }
        return null;
    }

    public final String getName() {
        return this.managerName;
    }
}
