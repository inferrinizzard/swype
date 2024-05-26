package com.nuance.connect.host.service;

/* loaded from: classes.dex */
public interface HostInterface {

    /* loaded from: classes.dex */
    public enum HostService {
        HOST_BUILD_SETTINGS,
        HOST_SETTINGS_MANAGER,
        HOST_SYSTEM_DATA
    }

    Object getHostService(HostService hostService);
}
