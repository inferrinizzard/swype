package com.nuance.connect.common;

/* loaded from: classes.dex */
public enum ConnectFeature {
    CONFIG(new ConnectFeature[0]),
    DOCUMENTS(new ConnectFeature[]{CONFIG}),
    LANGUAGE(new ConnectFeature[]{CONFIG, DOCUMENTS}),
    ACCOUNT(new ConnectFeature[]{CONFIG, DOCUMENTS}),
    DLM(new ConnectFeature[]{CONFIG}),
    SYNC(new ConnectFeature[]{CONFIG, DLM, ACCOUNT, DOCUMENTS}),
    CATEGORY(new ConnectFeature[]{CONFIG, DLM, DOCUMENTS}),
    LIVING_LANGUAGE(new ConnectFeature[]{CATEGORY, DOCUMENTS}),
    REPORTING(new ConnectFeature[]{CONFIG}),
    UPDATE(new ConnectFeature[]{CONFIG, DOCUMENTS}),
    ADDON_DICTIONARIES(new ConnectFeature[]{CATEGORY, DOCUMENTS}),
    CHINESE_PREDICTION(new ConnectFeature[]{DOCUMENTS}),
    CATALOG(new ConnectFeature[]{CATEGORY, DOCUMENTS});

    private ConnectFeature[] dependencies;

    ConnectFeature(ConnectFeature[] connectFeatureArr) {
        this.dependencies = connectFeatureArr;
    }
}
