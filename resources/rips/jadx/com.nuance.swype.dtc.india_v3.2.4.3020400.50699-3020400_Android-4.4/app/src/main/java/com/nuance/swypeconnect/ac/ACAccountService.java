package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.AccountService;
import com.nuance.connect.internal.common.ConnectAccount;
import com.nuance.connect.service.manager.AccountManager;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACDevice;

/* loaded from: classes.dex */
public final class ACAccountService extends ACService {
    public static final int ACCOUNT_TYPE_EMAIL = 1;
    public static final int ACCOUNT_TYPE_GMAIL_AUTH = 2;
    public static final int CONNECTION_STATUS_REFRESH_DELAYED = 12;
    public static final int CONNECTION_STATUS_STALLED = 2;
    public static final int STATUS_DELETED_ACCOUNT = 256;
    public static final int STATUS_DEVICE_UNLINKED = 1024;
    public static final int STATUS_INVALID_ACCOUNT = 512;
    public static final int STATUS_INVALID_VERIFICATION_CODE = 768;
    public static final int STATUS_REQUEST_IGNORED = 1280;
    private AccountService accountService;
    private CallbackWrapper serviceCallback;
    private final ConcurrentCallbackSet<ACAccountCallback> callbacks = new ConcurrentCallbackSet<>();
    private Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private Logger.Log customerLog = Logger.getLog(Logger.LoggerType.CUSTOMER);

    /* loaded from: classes.dex */
    public interface ACAccountCallback {
        void created();

        void devicesUpdated(ACDevice[] aCDeviceArr);

        void linked();

        void onError(int i, String str);

        void status(int i, String str);

        void verifyFailed();
    }

    /* loaded from: classes.dex */
    public static final class ACAccountException extends ACException {
        public static final int REASON_ACCOUNT_EXISTS = 204;
        public static final int REASON_ACCOUNT_LINKED = 205;
        public static final int REASON_INVALID_ACCOUNT = 201;
        public static final int REASON_INVALID_ACCOUNT_ID = 200;
        public static final int REASON_INVALID_ACCOUNT_TYPE = 202;
        public static final int REASON_INVALID_DEVICE = 203;
        public static final int REASON_INVALID_DEVICE_NAME = 206;
        public static final int REASON_INVALID_DEVICE_TYPE = 207;
        public static final int REASON_INVALID_VERIFICATION_CODE = 208;
        private static final long serialVersionUID = 7258446979818295259L;

        ACAccountException(int i) {
            super(i);
        }

        ACAccountException(int i, String str) {
            super(i, str);
        }

        ACAccountException(int i, Throwable th) {
            super(i, th);
        }

        ACAccountException(Throwable th) {
            super(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class CallbackWrapper implements AccountService.AccountCallback {
        ACAccountCallback[] callbacksArray;

        private CallbackWrapper(ACAccountCallback[] aCAccountCallbackArr) {
            this.callbacksArray = new ACAccountCallback[0];
            updateCallbacks(aCAccountCallbackArr);
        }

        @Override // com.nuance.connect.api.AccountService.AccountCallback
        public final void created() {
            Logger.getLog(Logger.LoggerType.CUSTOMER).d("Account successfully created.");
            for (ACAccountCallback aCAccountCallback : this.callbacksArray) {
                aCAccountCallback.created();
            }
        }

        @Override // com.nuance.connect.api.AccountService.AccountCallback
        public final void devicesUpdated(ConnectAccount.AccountDevice[] accountDeviceArr) {
            ACDevice[] aCDeviceArr = new ACDevice[accountDeviceArr.length];
            for (int i = 0; i < accountDeviceArr.length; i++) {
                aCDeviceArr[i] = new ACDevice(accountDeviceArr[i]);
            }
            for (ACAccountCallback aCAccountCallback : this.callbacksArray) {
                aCAccountCallback.devicesUpdated(aCDeviceArr);
            }
        }

        @Override // com.nuance.connect.api.AccountService.AccountCallback
        public final void linked() {
            Logger.getLog(Logger.LoggerType.CUSTOMER).d("Account successfully linked.");
            for (ACAccountCallback aCAccountCallback : this.callbacksArray) {
                aCAccountCallback.linked();
            }
        }

        @Override // com.nuance.connect.api.AccountService.AccountCallback
        public final void onError(int i, String str) {
            for (ACAccountCallback aCAccountCallback : this.callbacksArray) {
                aCAccountCallback.onError(i, str);
            }
        }

        @Override // com.nuance.connect.api.AccountService.AccountCallback
        public final void status(int i, String str) {
            int i2 = 0;
            if (i == 768) {
                ACAccountCallback[] aCAccountCallbackArr = this.callbacksArray;
                int length = aCAccountCallbackArr.length;
                while (i2 < length) {
                    aCAccountCallbackArr[i2].verifyFailed();
                    i2++;
                }
                return;
            }
            ACAccountCallback[] aCAccountCallbackArr2 = this.callbacksArray;
            int length2 = aCAccountCallbackArr2.length;
            while (i2 < length2) {
                aCAccountCallbackArr2[i2].status(i, str);
                i2++;
            }
        }

        final void updateCallbacks(ACAccountCallback[] aCAccountCallbackArr) {
            if (aCAccountCallbackArr != null) {
                this.callbacksArray = aCAccountCallbackArr;
            } else {
                this.callbacksArray = new ACAccountCallback[0];
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public final void createAccount(int i, String str, ACDevice.ACDeviceType aCDeviceType, String str2) throws ACAccountException {
        this.customerLog.d("Creating Account");
        this.oemLog.d("Account identifier: ", str, " device name: ", str2);
        if (this.accountService.getAccount() != null && this.accountService.isAccountRegisteredAndVerified()) {
            this.oemLog.d("Trying to create an account when one already exists.");
            throw new ACAccountException(204, "Account creation exception.  Account has already been created.");
        }
        if (aCDeviceType == null) {
            this.oemLog.d("Must supply a valid Device Type");
            throw new ACAccountException(207, "Account creation exception.  Unspecified device type");
        }
        if (str2 == null || str2.isEmpty()) {
            this.oemLog.d("Must supply a valid Device Name");
            throw new ACAccountException(206, "Account creation exception.  Unspecified device name.");
        }
        if (i == 1) {
            if (!this.accountService.isValidEmail(str)) {
                this.oemLog.d("Account creation exception.  Invalid email format");
                throw new ACAccountException(200, "The email address supplied is invalid");
            }
            ConnectAccount.AccountDevice.DeviceType.valueOf(aCDeviceType.name());
            this.accountService.registerAccount(i, str, ConnectAccount.AccountDevice.DeviceType.valueOf(aCDeviceType.name()), str2);
            return;
        }
        if (i != 2) {
            this.oemLog.d("unsupported type exception");
            throw new ACAccountException(202, "Account creation exception.  Unsupported account type");
        }
        if (!this.accountService.isValidEmail(str)) {
            this.oemLog.d("Account creation exception.  Invalid email format");
            throw new ACAccountException(200, "The gmail address supplied is invalid");
        }
        ConnectAccount.AccountDevice.DeviceType.valueOf(aCDeviceType.name());
        this.accountService.registerAccount(i, str, ConnectAccount.AccountDevice.DeviceType.valueOf(aCDeviceType.name()), str2);
    }

    public final void deleteAccount(boolean z) throws ACAccountException {
        this.customerLog.d("Deleting account.");
        if (this.accountService.isAccountActive()) {
            this.accountService.deleteAccount(z);
        } else {
            this.oemLog.d("Account Exception deleting account.  No account found.");
            throw new ACAccountException(201, "Account not found.");
        }
    }

    public final ACAccount getAccount() {
        ConnectAccount account = this.accountService.getAccount();
        if (account != null) {
            return new ACAccount(account);
        }
        return null;
    }

    public final ACDevice[] getDevices() {
        ConnectAccount account = this.accountService.getAccount();
        if (account == null || !this.accountService.isAccountRegisteredAndVerified()) {
            return null;
        }
        return new ACAccount(account).getDevices();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.ACCOUNT_SERVICE;
    }

    public final void refreshDeviceList() {
        this.accountService.refreshDevices();
    }

    public final void registerCallback(ACAccountCallback aCAccountCallback) {
        if (aCAccountCallback != null) {
            synchronized (this.callbacks) {
                this.callbacks.add(aCAccountCallback);
                if (this.serviceCallback == null) {
                    this.serviceCallback = new CallbackWrapper((ACAccountCallback[]) this.callbacks.toArray(new ACAccountCallback[0]));
                    this.accountService.registerCallback(this.serviceCallback);
                } else {
                    this.serviceCallback.updateCallbacks((ACAccountCallback[]) this.callbacks.toArray(new ACAccountCallback[0]));
                }
            }
        }
    }

    public final void removeDeviceFromAccount(ACDevice aCDevice) throws ACAccountException {
        this.customerLog.d("Removing device.");
        ConnectAccount account = this.accountService.getAccount();
        if (account == null) {
            this.oemLog.d("Account Exception removing device.  No account found.");
            throw new ACAccountException(201, "Account not found.");
        }
        if (account.getDevices().containsKey(aCDevice.getIdentifier())) {
            this.accountService.unlinkDevice(aCDevice.getIdentifier());
        } else {
            this.oemLog.d("Account Exception removing device.  No matching device found.");
            throw new ACAccountException(203, "Device not found, perhaps it was already deleted.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 1;
    }

    public final void reverify() throws ACAccountException {
        this.oemLog.d(AccountManager.COMMAND_REVERIFY);
        ConnectAccount account = this.accountService.getAccount();
        if (account == null || account.getAccountState().equals(ConnectAccount.AccountState.UNREGISTERED) || account.getAccountState().equals(ConnectAccount.AccountState.PENDING_DELETE)) {
            this.oemLog.d("Account Exception reverifying.  No account found.");
            throw new ACAccountException(201, "Account not found.");
        }
        if (account.getAccountState().equals(ConnectAccount.AccountState.VERIFIED)) {
            this.oemLog.d("Account Exception reverifying.  Account is already linked.");
            throw new ACAccountException(205, "Reverify is not allowed on an account that has been linked.");
        }
        this.accountService.sendReverify();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
    }

    public final void unregisterCallback(ACAccountCallback aCAccountCallback) {
        synchronized (this.callbacks) {
            this.callbacks.remove(aCAccountCallback);
            if (this.serviceCallback != null && this.callbacks.isEmpty()) {
                this.accountService.unregisterCallback(this.serviceCallback);
                this.serviceCallback = null;
            }
            if (this.serviceCallback != null) {
                this.serviceCallback.updateCallbacks((ACAccountCallback[]) this.callbacks.toArray(new ACAccountCallback[0]));
            }
        }
    }

    public final void unregisterCallbacks() {
        synchronized (this.callbacks) {
            this.accountService.unregisterCallbacks();
            this.callbacks.clear();
            this.serviceCallback = null;
        }
    }

    public final void verifyAccount(String str) throws ACAccountException {
        ConnectAccount account = this.accountService.getAccount();
        if (account == null) {
            this.oemLog.d("No account found to verify.");
            throw new ACAccountException(201, "Account verification exception.  Account not found.");
        }
        if (!account.getAccountState().equals(ConnectAccount.AccountState.REGISTERED)) {
            this.oemLog.d("Account verification exception.  Account is already linked.");
            throw new ACAccountException(205, "The account is already verified and the device is linked.");
        }
        if (str == null) {
            this.oemLog.d("The verification string is null");
            throw new ACAccountException(208, "The verification code supplied is not valid.");
        }
        this.accountService.validateAccount(str);
    }
}
