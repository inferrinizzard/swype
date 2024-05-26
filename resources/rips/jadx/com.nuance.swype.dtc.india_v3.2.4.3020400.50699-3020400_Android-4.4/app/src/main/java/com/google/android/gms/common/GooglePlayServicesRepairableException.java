package com.google.android.gms.common;

import android.content.Intent;

/* loaded from: classes.dex */
public final class GooglePlayServicesRepairableException extends UserRecoverableException {
    public final int cn;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GooglePlayServicesRepairableException(int i, String str, Intent intent) {
        super(str, intent);
        this.cn = i;
    }
}
