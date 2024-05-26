package com.nuance.connect.service.manager.interfaces;

import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.util.VersionUtils;

/* loaded from: classes.dex */
public interface Manager {
    void deregister();

    void destroy();

    String[] getDependencies();

    int getDependentCount();

    GenericProperty.BooleanProperty getIdleProperty();

    String getManagerName();

    int getManagerPollInterval();

    AbstractCommandManager.ManagerState getManagerStartState();

    void incrementDependentCount();

    void init();

    void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z);

    void postInit();

    void postStart();

    void rebind();

    void restart();

    void start();
}
