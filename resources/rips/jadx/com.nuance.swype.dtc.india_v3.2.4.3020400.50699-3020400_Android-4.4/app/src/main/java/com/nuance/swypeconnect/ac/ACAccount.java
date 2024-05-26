package com.nuance.swypeconnect.ac;

import com.nuance.connect.internal.common.ConnectAccount;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/* loaded from: classes.dex */
public final class ACAccount {
    private ConnectAccount account;
    private ACDevice[] devices;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACAccount(ConnectAccount connectAccount) {
        this.account = connectAccount;
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ConnectAccount.AccountDevice> entry : connectAccount.getDevices().entrySet()) {
            if (!entry.getValue().isDeleted()) {
                arrayList.add(new ACDevice(entry.getValue()));
            }
        }
        this.devices = (ACDevice[]) arrayList.toArray(new ACDevice[0]);
    }

    public final Date getCreationDate() {
        return new Date(this.account.getCreationTime());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ACDevice[] getDevices() {
        return (ACDevice[]) this.devices.clone();
    }

    public final String getIdentifier() {
        return this.account.getIdentifier();
    }

    public final int getType() {
        return this.account.getType();
    }

    public final boolean isLinked() {
        return ConnectAccount.AccountState.VERIFIED == this.account.getAccountState();
    }
}
