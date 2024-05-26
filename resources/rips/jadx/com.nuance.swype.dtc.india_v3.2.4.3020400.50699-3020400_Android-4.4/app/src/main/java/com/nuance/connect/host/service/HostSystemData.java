package com.nuance.connect.host.service;

import android.accounts.Account;

/* loaded from: classes.dex */
public interface HostSystemData {
    public static final int TYPE_EMAIL = 1;

    Account[] getAccountsForBackupSync();

    String getCurrentApplicationName();

    int getCurrentFieldInfo();

    String getCurrentLocale();
}
