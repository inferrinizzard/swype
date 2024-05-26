package com.nuance.connect.internal;

import com.nuance.connect.common.ConnectFeature;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractService {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ConnectFeature[] getDependencies();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ConnectHandler[] getHandlers();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String getServiceName();
}
