package com.nuance.connect.api;

import com.nuance.connect.internal.common.ConnectAccount;

/* loaded from: classes.dex */
public interface AccountService {
    public static final int CONNECTION_STATUS_REFRESH_DELAYED = 12;
    public static final int CONNECTION_STATUS_STALLED = 2;
    public static final int STATUS_DELETED_ACCOUNT = 256;
    public static final int STATUS_DEVICE_UNLINKED = 1024;
    public static final int STATUS_INVALID_ACCOUNT = 512;
    public static final int STATUS_INVALID_VERIFICATION_CODE = 768;
    public static final int STATUS_REQUEST_IGNORED = 1280;

    /* loaded from: classes.dex */
    public interface AccountCallback {
        void created();

        void devicesUpdated(ConnectAccount.AccountDevice[] accountDeviceArr);

        void linked();

        void onError(int i, String str);

        void status(int i, String str);
    }

    void deleteAccount(boolean z);

    ConnectAccount getAccount();

    String getDeviceNameFromId(String str);

    boolean isAccountActive();

    boolean isAccountKnownAccount(int i, String str);

    boolean isAccountRegistered();

    boolean isAccountRegisteredAndVerified();

    boolean isValidEmail(String str);

    void refreshDevices();

    void registerAccount(int i, String str, ConnectAccount.AccountDevice.DeviceType deviceType, String str2);

    void registerCallback(AccountCallback accountCallback);

    void sendReverify();

    void unlinkDevice(String str);

    void unregisterCallback(AccountCallback accountCallback);

    void unregisterCallbacks();

    void validateAccount(String str);
}
