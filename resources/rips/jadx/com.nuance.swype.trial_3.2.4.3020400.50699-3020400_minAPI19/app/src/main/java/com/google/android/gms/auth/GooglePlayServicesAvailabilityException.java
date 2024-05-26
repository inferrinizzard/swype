package com.google.android.gms.auth;

import android.content.Intent;

/* loaded from: classes.dex */
public final class GooglePlayServicesAvailabilityException extends UserRecoverableAuthException {
    private final int cn;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GooglePlayServicesAvailabilityException(int i, String str, Intent intent) {
        super(str, intent);
        this.cn = i;
    }
}
