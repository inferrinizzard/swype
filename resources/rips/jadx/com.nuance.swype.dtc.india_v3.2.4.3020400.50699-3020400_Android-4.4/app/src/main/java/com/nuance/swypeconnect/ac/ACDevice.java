package com.nuance.swypeconnect.ac;

import com.nuance.connect.internal.common.ConnectAccount;
import java.util.Date;

/* loaded from: classes.dex */
public final class ACDevice {
    private ConnectAccount.AccountDevice device;

    /* loaded from: classes.dex */
    public enum ACDeviceType {
        PHONE,
        TABLET,
        TV,
        PHABLET
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACDevice(ConnectAccount.AccountDevice accountDevice) {
        this.device = accountDevice;
    }

    public final ACDeviceType getDeviceType() {
        return ACDeviceType.valueOf(this.device.getType().name());
    }

    public final String getIdentifier() {
        return this.device.getDeviceId();
    }

    public final Date getLastCheckin() {
        return new Date(this.device.getLastCheckin());
    }

    public final String getName() {
        return this.device.getName();
    }
}
