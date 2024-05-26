package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Patterns;
import com.nuance.connect.api.AccountService;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.ConnectAccount;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.TimeConversion;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AccountServiceInternal extends AbstractService implements AccountService {
    private static final String ACCOUNT_PREF = "account_ACTIVE_ACCOUNT";
    private static final int CALLBACK_CREATED = 0;
    private static final int CALLBACK_ERROR = 3;
    private static final int CALLBACK_LINKED = 1;
    private static final int CALLBACK_STATUS = 4;
    private static final int CALLBACK_UPDATED = 2;
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_ACCOUNT_LIST_AVAILABLE, InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, InternalMessages.MESSAGE_HOST_ACCOUNT_INVALIDATED, InternalMessages.MESSAGE_HOST_ACCOUNT_DEVICES_UPDATED, InternalMessages.MESSAGE_HOST_ACCOUNT_DELETED};
    private ConnectAccount activeAccount;
    private ConnectServiceManagerInternal connectService;
    private final MessageThrottleProtectionUtil throttleProtection;
    private final ConcurrentCallbackSet<AccountService.AccountCallback> callbacks = new ConcurrentCallbackSet<>();
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.AccountServiceInternal.1
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.ACCOUNT_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[AccountServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < AccountServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = AccountServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            AccountServiceInternal.this.handleMessage(handler, message);
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };
    private final Handler messageHandler = new Handler(Looper.getMainLooper());
    private Logger.Log devLog = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());

    /* loaded from: classes.dex */
    public static class MessageThrottleProtectionUtil {
        private static final long SEND_DELAY = 60000;
        private static final Logger.Log devLog = Logger.getLog(Logger.LoggerType.DEVELOPER, AccountServiceInternal.class.getClass().getSimpleName());
        private WeakReference<ClientBinder> binder;
        private WeakReference<Handler> handler;
        private final Map<InternalMessages, DelayMessageRunnable> messageRunnable = new HashMap();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class DelayMessageRunnable implements Runnable {
            private final InternalMessages msg;
            private boolean scheduled;
            private int fib = 1;
            private long nextSend = System.currentTimeMillis();

            DelayMessageRunnable(InternalMessages internalMessages) {
                this.msg = internalMessages;
            }

            @Override // java.lang.Runnable
            public synchronized void run() {
                this.nextSend = System.currentTimeMillis() + (MessageThrottleProtectionUtil.fib(this.fib) * 60000);
                this.scheduled = false;
                this.fib = 1;
                ClientBinder clientBinder = (ClientBinder) MessageThrottleProtectionUtil.this.binder.get();
                if (clientBinder != null) {
                    clientBinder.sendConnectMessage(this.msg);
                }
            }

            public synchronized boolean trySend() {
                boolean z;
                if (System.currentTimeMillis() >= this.nextSend) {
                    run();
                    z = true;
                } else if (this.scheduled) {
                    this.fib++;
                    z = false;
                } else {
                    Handler handler = (Handler) MessageThrottleProtectionUtil.this.handler.get();
                    if (handler != null) {
                        handler.postDelayed(this, this.nextSend - System.currentTimeMillis());
                    }
                    this.scheduled = true;
                    z = false;
                }
                MessageThrottleProtectionUtil.devLog.d("Throttle message: ", this.msg, " fib: ", Integer.valueOf(this.fib), " nextSend: ", TimeConversion.prettyDateFormat(this.nextSend), " sent: ", Boolean.valueOf(z));
                return z;
            }
        }

        MessageThrottleProtectionUtil(Handler handler, ClientBinder clientBinder) {
            this.binder = new WeakReference<>(clientBinder);
            this.handler = new WeakReference<>(handler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int fib(int i) {
            if (i < 0) {
                return 0;
            }
            int[] iArr = {0, 1, 1};
            if (i <= 2) {
                return iArr[i];
            }
            for (int i2 = 2; i2 < i; i2++) {
                iArr[0] = iArr[1];
                iArr[1] = iArr[2];
                iArr[2] = iArr[0] + iArr[1];
            }
            return iArr[2];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public long getSendTime(InternalMessages internalMessages) {
            long j;
            DelayMessageRunnable delayMessageRunnable = this.messageRunnable.get(internalMessages);
            if (delayMessageRunnable == null) {
                return Long.MIN_VALUE;
            }
            synchronized (delayMessageRunnable) {
                j = delayMessageRunnable.nextSend;
            }
            return j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean send(InternalMessages internalMessages) {
            if (this.handler.get() == null || this.binder.get() == null) {
                devLog.e("Scheduling failure, required variable is null! Handler: ", this.handler.get(), "; Binder: ", this.binder.get());
            }
            DelayMessageRunnable delayMessageRunnable = this.messageRunnable.get(internalMessages);
            if (delayMessageRunnable == null) {
                delayMessageRunnable = new DelayMessageRunnable(internalMessages);
                this.messageRunnable.put(internalMessages, delayMessageRunnable);
            }
            return delayMessageRunnable.trySend();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccountServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
        this.throttleProtection = new MessageThrottleProtectionUtil(this.messageHandler, connectServiceManagerInternal.getBinder());
        PersistentDataStore dataStore = connectServiceManagerInternal.getDataManager().getDataStore();
        if (dataStore == null || !dataStore.exists(ACCOUNT_PREF)) {
            return;
        }
        try {
            this.activeAccount = new ConnectAccount(new JSONObject(dataStore.readString(ACCOUNT_PREF, null)));
            this.connectService.getUserSettings().setDlmSyncAccountEnabled(isAccountRegisteredAndVerified());
            this.devLog.d("Active Account loaded from JSON successfully");
        } catch (Exception e) {
            this.devLog.d("Failed to load active account");
        }
    }

    private void doCallback(int i, int i2, String str) {
        int i3 = 0;
        AccountService.AccountCallback[] accountCallbackArr = (AccountService.AccountCallback[]) this.callbacks.toArray(new AccountService.AccountCallback[0]);
        switch (i) {
            case 0:
                int length = accountCallbackArr.length;
                while (i3 < length) {
                    accountCallbackArr[i3].created();
                    i3++;
                }
                return;
            case 1:
                int length2 = accountCallbackArr.length;
                while (i3 < length2) {
                    accountCallbackArr[i3].linked();
                    i3++;
                }
                return;
            case 2:
                if (this.activeAccount == null) {
                    return;
                }
                int length3 = accountCallbackArr.length;
                while (i3 < length3) {
                    accountCallbackArr[i3].devicesUpdated(this.activeAccount.getDevicesArray());
                    i3++;
                }
                return;
            case 3:
                int length4 = accountCallbackArr.length;
                while (i3 < length4) {
                    accountCallbackArr[i3].onError(i2, str);
                    i3++;
                }
                return;
            default:
                int length5 = accountCallbackArr.length;
                while (i3 < length5) {
                    accountCallbackArr[i3].status(i2, str);
                    i3++;
                }
                return;
        }
    }

    private ConnectAccount findActiveAccount(HashMap<String, ConnectAccount> hashMap) {
        if (hashMap != null) {
            Iterator<Map.Entry<String, ConnectAccount>> it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                ConnectAccount value = it.next().getValue();
                if (value.getAccountState().equals(ConnectAccount.AccountState.REGISTERED) || value.getAccountState().equals(ConnectAccount.AccountState.VERIFIED)) {
                    return value;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Handler handler, Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_HOST_ACCOUNT_LIST_AVAILABLE:
                this.devLog.d("MESSAGE_HOST_ACCOUNT_LIST_AVAILABLE");
                ConnectAccount connectAccount = (ConnectAccount) message.getData().getSerializable(Strings.DEFAULT_KEY);
                if (connectAccount == null || connectAccount.isDeleted()) {
                    this.oemLog.d("Account has been deleted.");
                    doCallback(3, 512, "Account has been deleted");
                } else {
                    this.oemLog.d("Account updated; device count: ", Integer.valueOf(connectAccount.getDevicesArray().length));
                    doCallback(2, 0, null);
                }
                this.activeAccount = connectAccount;
                return;
            case MESSAGE_HOST_ACCOUNT_VERIFY:
                String string = message.getData().getString(Strings.DEFAULT_KEY);
                this.devLog.d("MESSAGE_HOST_ACCOUNT_VERIFY status:", string);
                if (Strings.STATUS_SUCCESS.equals(string)) {
                    this.oemLog.v("Account successfully linked.");
                    this.activeAccount = (ConnectAccount) message.getData().getSerializable(Strings.BUNDLE_KEY);
                    this.connectService.getUserSettings().setDlmSyncAccountEnabled(true);
                    doCallback(1, 0, null);
                    doCallback(2, 0, null);
                    return;
                }
                if (Strings.STATUS_FAILURE.equals(string)) {
                    this.oemLog.v("Account linking failed. Invalid verification code.");
                    doCallback(4, 768, "The verification code supplied is invalid.");
                    return;
                } else if (Strings.STATUS_CANCELED.equals(string)) {
                    this.oemLog.v("Account linking canceled.");
                    doCallback(4, 1280, "Verification request cancelled, a duplicate verification request is already being processed.");
                    return;
                } else {
                    this.oemLog.v("Account linking failed.  Server indicates the account is invalid.");
                    doCallback(3, 512, "Account has been invalidated");
                    return;
                }
            case MESSAGE_HOST_ACCOUNT_CREATED:
                String string2 = message.getData().getString(Strings.DEFAULT_KEY);
                this.devLog.d("MESSAGE_HOST_ACCOUNT_CREATED status:", string2);
                if (string2.equals(Strings.STATUS_SUCCESS)) {
                    this.oemLog.v("Account created successfully");
                    this.activeAccount = (ConnectAccount) message.getData().getSerializable(Strings.BUNDLE_KEY);
                    doCallback(0, 0, null);
                    return;
                } else {
                    this.oemLog.v("Failed to create account");
                    if (message.getData().containsKey(Strings.MESSAGE_BUNDLE_DELAY)) {
                        doCallback(4, 2, String.valueOf(message.getData().getInt(Strings.MESSAGE_BUNDLE_DELAY)));
                        return;
                    } else {
                        doCallback(3, 512, "Invalid argument supplied during account creation");
                        return;
                    }
                }
            case MESSAGE_HOST_ACCOUNT_INVALIDATED:
                this.activeAccount = null;
                this.oemLog.v("Account linking failed.  Server indicates the account is invalid.");
                this.connectService.getUserSettings().setDlmSyncAccountEnabled(false);
                doCallback(3, 512, "Account has been invalidated.");
                return;
            case MESSAGE_HOST_ACCOUNT_DELETED:
                this.activeAccount = null;
                this.oemLog.v("Account has been deleted.");
                this.connectService.getUserSettings().setDlmSyncAccountEnabled(false);
                doCallback(4, 256, "Account has been deleted.");
                return;
            case MESSAGE_HOST_ACCOUNT_DEVICES_UPDATED:
                this.devLog.d("MESSAGE_HOST_ACCOUNT_DEVICES_UPDATED");
                this.activeAccount = (ConnectAccount) message.getData().getSerializable(Strings.DEFAULT_KEY);
                this.oemLog.d("Account device updated; device count: ", this.activeAccount == null ? "null account" : Integer.valueOf(this.activeAccount.getDevicesArray().length));
                doCallback(2, 0, null);
                return;
            default:
                return;
        }
    }

    private void setActiveAccount(ConnectAccount connectAccount) {
        this.activeAccount = connectAccount;
    }

    @Override // com.nuance.connect.api.AccountService
    public void deleteAccount(boolean z) {
        if (this.activeAccount != null) {
            this.activeAccount.setAccountState(ConnectAccount.AccountState.PENDING_DELETE);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(Strings.DEFAULT_KEY, z);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_ACCOUNT_DELETE, bundle);
        this.connectService.getUserSettings().setDlmSyncAccountEnabled(false);
    }

    @Override // com.nuance.connect.api.AccountService
    public ConnectAccount getAccount() {
        if (this.activeAccount == null || !isAccountActive()) {
            return null;
        }
        return this.activeAccount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.ACCOUNT.values();
    }

    @Override // com.nuance.connect.api.AccountService
    public String getDeviceNameFromId(String str) {
        ConnectAccount.AccountDevice accountDevice;
        return (this.activeAccount == null || this.activeAccount.isDeleted() || (accountDevice = this.activeAccount.getDevices().get(str)) == null) ? "" : accountDevice.getName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.ACCOUNT.name();
    }

    @Override // com.nuance.connect.api.AccountService
    public boolean isAccountActive() {
        return (this.activeAccount == null || this.activeAccount.getAccountState().equals(ConnectAccount.AccountState.UNREGISTERED) || this.activeAccount.getAccountState().equals(ConnectAccount.AccountState.PENDING_DELETE)) ? false : true;
    }

    @Override // com.nuance.connect.api.AccountService
    public boolean isAccountKnownAccount(int i, String str) {
        return false;
    }

    @Override // com.nuance.connect.api.AccountService
    public boolean isAccountRegistered() {
        return this.activeAccount != null && this.activeAccount.getAccountState().equals(ConnectAccount.AccountState.REGISTERED);
    }

    @Override // com.nuance.connect.api.AccountService
    public boolean isAccountRegisteredAndVerified() {
        return this.activeAccount != null && this.activeAccount.getAccountState().equals(ConnectAccount.AccountState.VERIFIED);
    }

    @Override // com.nuance.connect.api.AccountService
    public boolean isValidEmail(String str) {
        return str != null && Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    @Override // com.nuance.connect.api.AccountService
    public void refreshDevices() {
        if (this.throttleProtection.send(InternalMessages.MESSAGE_CLIENT_ACCOUNT_REFRESH_DEVICES)) {
            return;
        }
        doCallback(4, 12, "getDevices delayed until: " + this.throttleProtection.getSendTime(InternalMessages.MESSAGE_CLIENT_ACCOUNT_REFRESH_DEVICES));
    }

    @Override // com.nuance.connect.api.AccountService
    public void registerAccount(int i, String str, ConnectAccount.AccountDevice.DeviceType deviceType, String str2) {
        this.devLog.d("registerAccount()");
        String deviceId = ((ConfigService) this.connectService.getFeatureService(ConnectFeature.CONFIG)).getDeviceId();
        Bundle bundle = new Bundle();
        ConnectAccount connectAccount = new ConnectAccount(null, str, i, ConnectAccount.CreationMethod.USER_CREATED);
        connectAccount.addDevice(new ConnectAccount.AccountDevice(deviceId, str2, System.currentTimeMillis(), true, deviceType));
        bundle.putSerializable(Strings.DEFAULT_KEY, connectAccount);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_ACCOUNT_REGISTER, bundle);
        setActiveAccount(connectAccount);
    }

    @Override // com.nuance.connect.api.AccountService
    public void registerCallback(AccountService.AccountCallback accountCallback) {
        this.callbacks.add(accountCallback);
    }

    @Override // com.nuance.connect.api.AccountService
    public void sendReverify() {
        if (this.throttleProtection.send(InternalMessages.MESSAGE_CLIENT_ACCOUNT_REVERIFY)) {
            return;
        }
        long sendTime = this.throttleProtection.getSendTime(InternalMessages.MESSAGE_CLIENT_ACCOUNT_REVERIFY);
        this.oemLog.d("reverify delayed until: ", Long.valueOf(sendTime));
        doCallback(4, 12, "reverify delayed until: " + sendTime);
    }

    @Override // com.nuance.connect.api.AccountService
    public void unlinkDevice(String str) {
        ConnectAccount.AccountDevice accountDevice;
        Bundle bundle = new Bundle();
        bundle.putString(Strings.DEFAULT_KEY, str);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_ACCOUNT_UNLINK_DEVICE, bundle);
        if (this.activeAccount == null || this.activeAccount.getDevices() == null || (accountDevice = this.activeAccount.getDevices().get(str)) == null) {
            return;
        }
        accountDevice.markAsDeleted();
        if (accountDevice.getIsThisDevice()) {
            this.activeAccount.setAccountState(ConnectAccount.AccountState.PENDING_DELETE);
            doCallback(3, 512, "The current account has be invalidated because the device has been unlinked.");
            this.connectService.getUserSettings().setDlmSyncAccountEnabled(false);
        }
    }

    @Override // com.nuance.connect.api.AccountService
    public void unregisterCallback(AccountService.AccountCallback accountCallback) {
        this.callbacks.remove(accountCallback);
    }

    @Override // com.nuance.connect.api.AccountService
    public void unregisterCallbacks() {
        this.callbacks.clear();
    }

    @Override // com.nuance.connect.api.AccountService
    public void validateAccount(String str) {
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_ACCOUNT_VERIFY, str);
    }
}
