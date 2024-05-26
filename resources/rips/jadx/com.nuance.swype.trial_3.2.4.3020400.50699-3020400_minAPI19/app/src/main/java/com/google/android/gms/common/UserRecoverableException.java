package com.google.android.gms.common;

import android.content.Intent;

/* loaded from: classes.dex */
public class UserRecoverableException extends Exception {
    public final Intent mIntent;

    public UserRecoverableException(String str, Intent intent) {
        super(str);
        this.mIntent = intent;
    }
}
