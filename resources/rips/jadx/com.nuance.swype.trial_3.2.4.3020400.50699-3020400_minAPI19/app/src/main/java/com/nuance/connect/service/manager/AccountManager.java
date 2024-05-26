package com.nuance.connect.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.nuance.connect.comm.AbstractTransaction;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.ConnectAccount;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.AccountListener;
import com.nuance.connect.util.BuildProps;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AccountManager extends AbstractCommandManager {
    public static final String ACCOUNT_FIRST_START;
    public static final String ACCOUNT_PREF;
    public static final String COMMAND_ACCOUNT = "account";
    public static final String COMMAND_ACCOUNT_CHECK = "accountCheck";
    public static final String COMMAND_DELETE_ACCOUNT = "deleteAccount";
    public static final String COMMAND_DEVICE_LIST = "deviceList";
    public static final String COMMAND_FAMILY;
    public static final String COMMAND_NAME_DEVICE = "nameDevice";
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_REVERIFY = "reverify";
    public static final String COMMAND_UNLINK_DEVICE = "unlinkDevice";
    public static final String COMMAND_VERIFY = "verify";
    public static final int COMMAND_VERSION = 7;
    public static final String DEVICE_TYPE_STRING_TRANSLATED;
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    private AccountListener accountListener;
    private volatile ConnectAccount activeAccount;
    private volatile boolean isPrefsLoaded;
    private Logger.Log log;

    /* loaded from: classes.dex */
    private class RegisterAccountTransaction extends AbstractTransaction {
        private ConnectAccount account;

        private RegisterAccountTransaction(ConnectAccount connectAccount) {
            String str;
            int i;
            this.account = connectAccount;
            String identifier = connectAccount.getIdentifier();
            int type = connectAccount.getType();
            int serverType = ConnectAccount.AccountDevice.DeviceType.PHONE.getServerType();
            ConnectAccount.AccountDevice[] accountDeviceArr = (ConnectAccount.AccountDevice[]) connectAccount.getDevices().values().toArray(new ConnectAccount.AccountDevice[0]);
            if (accountDeviceArr.length > 0) {
                ConnectAccount.AccountDevice accountDevice = accountDeviceArr[0];
                i = accountDevice.getType().getServerType();
                str = accountDevice.getName();
            } else {
                str = "Android device";
                i = serverType;
            }
            sendRegister(type, identifier, i, str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onResponseRegister(Response response) {
            AccountManager.this.log.v("onResponseRegister()");
            if (response.status != 1) {
                if (response.status == 15) {
                    rollback();
                    AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, Strings.STATUS_FAILURE);
                    return;
                }
                return;
            }
            AccountManager.this.activeAccount = this.account;
            AccountManager.this.activeAccount.setAccountState(ConnectAccount.AccountState.REGISTERED);
            AccountManager.this.savePreferences();
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DEFAULT_KEY, Strings.STATUS_SUCCESS);
            bundle.putSerializable(Strings.BUNDLE_KEY, AccountManager.this.activeAccount);
            AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, bundle);
            this.currentCommand = null;
            AccountManager.this.finishTransaction(getName());
        }

        private void sendRegister(int i, String str, int i2, String str2) {
            AccountManager.this.log.v("sendRegister()");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.IDENTIFIER, str);
            hashMap.put(MessageAPI.TYPE, Integer.valueOf(i));
            hashMap.put(MessageAPI.LANGUAGE, AccountManager.this.client.getCurrentLocale().toString());
            hashMap.put(MessageAPI.DEVICE_FORM_FACTOR, Integer.valueOf(i2));
            hashMap.put(MessageAPI.NAME, str2);
            this.currentCommand = AccountManager.this.createCommand("register", Command.REQUEST_TYPE.USER, hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.AccountManager.RegisterAccountTransaction.1
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    super.onFailure(command);
                    AccountManager.this.log.e("onFailure() ", command.command);
                    RegisterAccountTransaction.this.rollback();
                    AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, Strings.STATUS_FAILURE);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onIOExceptionResponse(Command command) {
                    super.onIOExceptionResponse(command);
                    AccountManager.this.log.e("onIOExceptionResponse() ", command.command);
                    RegisterAccountTransaction.this.rollback();
                    AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, Strings.STATUS_FAILURE);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    AccountManager.this.log.d("sendRegister.onResponse()");
                    RegisterAccountTransaction.this.onResponseRegister(response);
                }

                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public boolean onRetry(Command command, int i3, int i4, String str3) {
                    AccountManager.this.log.e("onDelay() delay: ", Integer.valueOf(i3));
                    if (i3 <= 0) {
                        return true;
                    }
                    Logger.getLog(Logger.LoggerType.OEM).d("Account creation failed, server delayed the request.");
                    RegisterAccountTransaction.this.rollback();
                    Bundle bundle = new Bundle();
                    bundle.putString(Strings.DEFAULT_KEY, Strings.STATUS_FAILURE);
                    bundle.putInt(Strings.MESSAGE_BUNDLE_DELAY, i3);
                    bundle.putInt("status", i4);
                    bundle.putString("message", str3);
                    AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_CREATED, bundle);
                    return false;
                }
            });
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return RegisterAccountTransaction.class.getName();
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            AccountManager.this.log.v("getNextCommand=", Boolean.valueOf(this.currentCommand != null), " name=", getName());
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.USER;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.currentCommand = null;
            AccountManager.this.finishTransaction(getName());
        }
    }

    /* loaded from: classes.dex */
    public class VerifyAccountTransaction extends AbstractTransaction {
        ConnectAccount account;
        private volatile boolean canceled;
        private String code;
        private volatile boolean success;

        VerifyAccountTransaction(ConnectAccount connectAccount, String str) {
            this.code = str;
            this.account = connectAccount;
            sendVerify();
        }

        private void fail(boolean z) {
            AccountManager.this.log.d("fail(", Boolean.valueOf(z), ")");
            rollback();
            if (z) {
                AccountManager.this.cleanHouse();
            }
        }

        private void finishAccountActivation() {
            AccountManager.this.log.d("finishAccountActivation()");
            AccountManager.this.activeAccount = this.account;
            AccountManager.this.requestRecalculatePollInterval();
            AccountManager.this.savePreferences();
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DEFAULT_KEY, Strings.STATUS_SUCCESS);
            bundle.putSerializable(Strings.BUNDLE_KEY, AccountManager.this.activeAccount);
            AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, bundle);
            AccountManager.this.client.linkedAccount();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onResponseVerify(Response response) {
            AccountManager.this.log.v("onResponseVerify()");
            if (response.status != 1) {
                if (response.status == 24) {
                    AccountManager.this.log.d("Account link failed due to INVALID_ACCOUNT status from server");
                    fail(true);
                    return;
                } else {
                    AccountManager.this.log.d("Account link failed due to unknown status: ", Integer.valueOf(response.status));
                    fail(false);
                    return;
                }
            }
            try {
                JSONObject jSONObject = (JSONObject) response.parameters.get(MessageAPI.ACCOUNT);
                String string = jSONObject.getString(MessageAPI.ACCOUNT_ID);
                int i = jSONObject.getInt(MessageAPI.DEVICE_COUNT);
                int i2 = jSONObject.getInt(MessageAPI.CREATION_TIMESTAMP);
                this.account.setAccountId(string);
                this.account.setDeviceCount(i);
                this.account.setCreationTimeSeconds(i2);
                AccountManager.this.client.setProperty(ConnectConfiguration.ConfigProperty.ACCOUNT_ID, string);
                this.account.verify();
                AccountManager.this.setDevices((JSONArray) response.parameters.get(MessageAPI.DEVICE_LIST), this.account);
                this.success = true;
                this.currentCommand = null;
            } catch (JSONException e) {
                AccountManager.this.log.e((Object) "Error processing JSON response", (Throwable) e);
                fail(false);
            }
        }

        private void sendVerify() {
            AccountManager.this.log.v("sendVerify()");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.IDENTIFIER, this.account.getIdentifier());
            hashMap.put(MessageAPI.TYPE, Integer.valueOf(this.account.getType()));
            hashMap.put(MessageAPI.CODE, this.code);
            this.currentCommand = AccountManager.this.createCommand(AccountManager.COMMAND_VERIFY, Command.REQUEST_TYPE.USER, hashMap, new AbstractTransaction.AbstractResponseCallback() { // from class: com.nuance.connect.service.manager.AccountManager.VerifyAccountTransaction.1
                @Override // com.nuance.connect.comm.AbstractTransaction.AbstractResponseCallback, com.nuance.connect.comm.ResponseCallback
                public void onFailure(Command command) {
                    super.onFailure(command);
                    AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, Strings.STATUS_FAILURE);
                }

                @Override // com.nuance.connect.comm.ResponseCallback
                public void onResponse(Response response) {
                    VerifyAccountTransaction.this.onResponseVerify(response);
                }
            });
        }

        @Override // com.nuance.connect.comm.Transaction
        public void cancel() {
            this.canceled = true;
            if (this.currentCommand != null) {
                this.currentCommand.canceled = true;
            }
        }

        @Override // com.nuance.connect.comm.Transaction
        public String createDownloadFile(Context context) {
            return null;
        }

        @Override // com.nuance.connect.comm.Transaction
        public String getName() {
            return VerifyAccountTransaction.class.getName();
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command getNextCommand() {
            AccountManager.this.log.d("getNextCommand=", Boolean.valueOf(this.currentCommand != null), " name=", getName());
            return this.currentCommand;
        }

        @Override // com.nuance.connect.comm.Transaction
        public int getPriority() {
            return 10;
        }

        @Override // com.nuance.connect.comm.Transaction
        public Command.REQUEST_TYPE getRequestType() {
            return Command.REQUEST_TYPE.USER;
        }

        @Override // com.nuance.connect.comm.AbstractTransaction, com.nuance.connect.comm.Transaction
        public void onEndProcessing() {
            AccountManager.this.finishTransaction(getName());
            if (!this.success) {
                AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, Strings.STATUS_FAILURE);
            } else {
                AccountManager.this.sendPoll();
                finishAccountActivation();
            }
        }

        @Override // com.nuance.connect.comm.AbstractTransaction
        public void rollback() {
            this.currentCommand = null;
        }
    }

    static {
        String name = ManagerService.ACCOUNT.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        ACCOUNT_PREF = MANAGER_NAME + "_ACTIVE_ACCOUNT";
        ACCOUNT_FIRST_START = MANAGER_NAME + "_FIRST_START";
        DEVICE_TYPE_STRING_TRANSLATED = MANAGER_NAME + "_DEVICE_TYPE_STRING";
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_CLIENT_ACCOUNT_REGISTER, InternalMessages.MESSAGE_CLIENT_ACCOUNT_VERIFY, InternalMessages.MESSAGE_CLIENT_ACCOUNT_REVERIFY, InternalMessages.MESSAGE_CLIENT_ACCOUNT_DELETE, InternalMessages.MESSAGE_CLIENT_ACCOUNT_UNLINK_DEVICE, InternalMessages.MESSAGE_CLIENT_ACCOUNT_REFRESH_DEVICES};
    }

    public AccountManager(ConnectClient connectClient) {
        super(connectClient);
        this.isPrefsLoaded = false;
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.accountListener = new AccountListener() { // from class: com.nuance.connect.service.manager.AccountManager.1
            @Override // com.nuance.connect.service.manager.interfaces.AccountListener
            public void onInvalidated() {
                AccountManager.this.log.d("onInvalidated");
                AccountManager.this.cleanHouse();
                AccountManager.this.savePreferences();
                AccountManager.this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_INVALIDATED);
            }

            @Override // com.nuance.connect.service.manager.interfaces.AccountListener
            public void onLinked() {
            }
        };
        this.log.v("AccountManager()");
        this.version = 7;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand(COMMAND_ACCOUNT, new int[]{1, 0});
        this.validCommands.addCommand("register", new int[]{1, 15});
        this.validCommands.addCommand(COMMAND_ACCOUNT_CHECK, new int[]{1});
        this.validCommands.addCommand(COMMAND_VERIFY, new int[]{1, 15});
        this.validCommands.addCommand(COMMAND_REVERIFY, new int[]{1, 15});
        this.validCommands.addCommand(COMMAND_UNLINK_DEVICE, new int[]{1, 0});
        this.validCommands.addCommand(COMMAND_NAME_DEVICE, new int[]{1, 0});
        this.validCommands.addCommand(COMMAND_DEVICE_LIST, new int[]{1});
        this.validCommands.addCommand(COMMAND_DELETE_ACCOUNT, new int[]{1, 0});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanHouse() {
        this.activeAccount = null;
        requestRecalculatePollInterval();
    }

    private void deleteActiveAccount(boolean z, boolean z2) {
        if (z) {
            cleanHouse();
        } else if (this.activeAccount != null) {
            this.activeAccount.setAccountState(ConnectAccount.AccountState.PENDING_DELETE);
        }
        savePreferences();
        if (z2) {
            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_DELETED);
        }
    }

    private void deleteDevice(String str, boolean z) {
        if (str.equals(this.client.getDeviceId())) {
            deleteActiveAccount(z, z);
        } else if (this.activeAccount == null || this.activeAccount.getDeviceCount() == 0) {
            this.log.e("Attempting to Delete a device on an inactive account, ignoring.");
        } else if (this.activeAccount.getDevices().containsKey(str)) {
            if (z) {
                this.activeAccount.getDevices().remove(str);
                sendDeviceUpdate();
            } else {
                this.activeAccount.getDevices().get(str).markAsDeleted();
            }
        }
        savePreferences();
    }

    private ConnectAccount findActiveAccount() {
        return this.activeAccount;
    }

    private ConnectAccount getRegisteringAccount() {
        return this.activeAccount;
    }

    private void loadPreferences() {
        if (this.isPrefsLoaded) {
            return;
        }
        if (this.client.getDataStore().exists(ACCOUNT_PREF)) {
            try {
                this.activeAccount = new ConnectAccount(new JSONObject(this.client.getDataStore().readString(ACCOUNT_PREF, null)));
                this.log.d("Active Account loaded from JSON successfully");
                this.log.d("ID: ", this.activeAccount.getAccountId(), ", state: ", this.activeAccount.getAccountState(), ", deviceCt: ", Integer.valueOf(this.activeAccount.getDeviceCount()));
            } catch (Exception e) {
                this.log.d((Object) "Failed to load stored account", (Throwable) e);
            }
        }
        this.isPrefsLoaded = true;
    }

    private void onResponseAccount(Response response) {
        this.log.v("onResponseAccount()");
        if (response.status == 1) {
            String normalizeEmail = StringUtils.normalizeEmail((String) response.parameters.get(MessageAPI.IDENTIFIER));
            int intValue = ((Integer) response.parameters.get(MessageAPI.DEVICE_COUNT)).intValue();
            if (this.activeAccount != null && this.activeAccount.getIdentifier().equals(normalizeEmail)) {
                this.activeAccount.setDeviceCount(intValue);
                setDevices((JSONArray) response.parameters.get(MessageAPI.DEVICE_LIST), this.activeAccount);
                requestRecalculatePollInterval();
            }
        } else if (response.status == 0 && this.activeAccount != null && this.activeAccount.getAccountState().equals(ConnectAccount.AccountState.VERIFIED)) {
            cleanHouse();
        }
        sendStatusUpdate();
        savePreferences();
    }

    private void onResponseAccountCheck(Response response) {
        this.log.v("onResponseAccountCheck()");
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.ACCOUNT_LIST);
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    if (jSONObject.has(MessageAPI.IDENTIFIER)) {
                        arrayList.add(jSONObject.getString(MessageAPI.IDENTIFIER));
                    }
                } catch (JSONException e) {
                    this.log.v("JSONException ", e.getMessage());
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (((String) it.next()).equals(this.activeAccount.getAccountId())) {
                this.activeAccount.setExistsOnServer(true);
            }
        }
        savePreferences();
        sendStatusUpdate();
    }

    private void onResponseDeleteAccount(Response response) {
        this.log.v("onResponseDeleteAccount()");
        if (response.status == 1) {
            deleteActiveAccount(true, true);
            sendPoll();
        }
    }

    private void onResponseDeviceList(Response response) {
        this.log.v("onResponseDeviceList() ", Integer.valueOf(response.status));
        if (response.status == 1) {
            setDevices((JSONArray) response.parameters.get(MessageAPI.DEVICE_LIST), this.activeAccount);
            requestRecalculatePollInterval();
            sendDeviceUpdate();
            savePreferences();
        }
    }

    private void onResponseReverify(Response response) {
        this.log.v("onResponseReverify()");
        if (response.status == 1) {
            Logger.getLog(Logger.LoggerType.OEM).d("Verification code resent.");
        }
    }

    private void onResponseUnlinkDevice(Response response) {
        this.log.v("onResponseUnlinkDevice()");
        if (response.status == 1) {
            String str = (String) response.initialCommand.parameters.get(MessageAPI.DEVICE_ID);
            deleteDevice(str, true);
            if (str.equals(this.client.getDeviceId())) {
                Logger.getLog(Logger.LoggerType.OEM).d("Unlinking current device.  The account is no longer valid.");
                this.client.invalidAccount();
            }
            requestRecalculatePollInterval();
            sendPoll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestRecalculatePollInterval() {
        this.client.postMessage(InternalMessages.MESSAGE_RECALCULATE_POLLING);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePreferences() {
        if (this.activeAccount != null) {
            this.client.getDataStore().saveString(ACCOUNT_PREF, this.activeAccount.toJsonString());
        } else {
            this.client.getDataStore().delete(ACCOUNT_PREF);
        }
    }

    private void sendAccount() {
        this.log.v("sendAccount()");
        sendCommand(createCommand(COMMAND_ACCOUNT, Command.REQUEST_TYPE.BACKGROUND));
    }

    private void sendAccountCheck() {
        this.log.v("sendAccountCheck()");
        JSONArray jSONArray = new JSONArray();
        if (this.activeAccount != null) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(MessageAPI.IDENTIFIER, this.activeAccount.getIdentifier());
                jSONObject.put(MessageAPI.TYPE, this.activeAccount.getType());
                jSONArray.put(jSONObject);
            } catch (JSONException e) {
            }
        }
        if (jSONArray.length() > 0) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.ACCOUNT_LIST, jSONArray);
            this.client.sendCommand(createCommand(COMMAND_ACCOUNT_CHECK, Command.REQUEST_TYPE.USER, hashMap));
        }
    }

    private void sendDeleteAccount(boolean z) {
        this.log.v("sendDeleteAccount(", Boolean.valueOf(z), ")");
        Command createCommand = createCommand(COMMAND_DELETE_ACCOUNT, Command.REQUEST_TYPE.USER);
        if (z) {
            createCommand.parameters.put(MessageAPI.DELETE_DATA, 1);
        }
        this.client.sendCommand(createCommand);
        deleteActiveAccount(false, false);
    }

    private void sendDeviceList() {
        this.log.v("sendDeviceList(): ", this.activeAccount);
        if (this.activeAccount != null) {
            Command createCommand = createCommand(COMMAND_DEVICE_LIST, Command.REQUEST_TYPE.BACKGROUND);
            createCommand.allowDuplicateOfCommand = false;
            this.client.sendCommand(createCommand);
        }
    }

    private void sendDeviceUpdate() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Strings.DEFAULT_KEY, this.activeAccount);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_DEVICES_UPDATED, bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPoll() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Strings.DEFAULT_KEY, true);
        this.client.postMessage(InternalMessages.MESSAGE_COMMAND_SESSION_POLL, bundle);
    }

    private void sendReverify(String str, int i) {
        this.log.v("sendReverify()");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MessageAPI.IDENTIFIER, str);
        hashMap.put(MessageAPI.TYPE, Integer.valueOf(i));
        hashMap.put(MessageAPI.LANGUAGE, this.client.getCurrentLocale().toString());
        this.client.sendCommand(createCommand(COMMAND_REVERIFY, Command.REQUEST_TYPE.USER, hashMap));
    }

    private void sendStatusUpdate() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Strings.DEFAULT_KEY, this.activeAccount);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_LIST_AVAILABLE, bundle);
    }

    private void sendUnlinkDevice(String str) {
        this.log.v("sendUnlinkDevice(): ", this.activeAccount);
        if (this.activeAccount != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MessageAPI.DEVICE_ID, str);
            this.client.sendCommand(createCommand(COMMAND_UNLINK_DEVICE, Command.REQUEST_TYPE.USER, hashMap));
            deleteDevice(str, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDevices(JSONArray jSONArray, ConnectAccount connectAccount) {
        int i = 0;
        if (jSONArray == null) {
            this.log.e("no devices for account!");
            return;
        }
        this.log.v("count ", Integer.valueOf(jSONArray.length()));
        ArrayList arrayList = new ArrayList();
        String deviceId = this.client.getDeviceId();
        if (deviceId == null) {
            deviceId = "";
        }
        while (true) {
            int i2 = i;
            if (i2 >= jSONArray.length()) {
                break;
            }
            try {
                JSONObject jSONObject = jSONArray.getJSONObject(i2);
                String string = jSONObject.getString(MessageAPI.NAME);
                String string2 = jSONObject.getString(MessageAPI.DEVICE_ID);
                ConnectAccount.AccountDevice.DeviceType from = ConnectAccount.AccountDevice.DeviceType.from(jSONObject.has(MessageAPI.DEVICE_FORM_FACTOR) ? jSONObject.getInt(MessageAPI.DEVICE_FORM_FACTOR) : ConnectAccount.AccountDevice.DeviceType.PHONE.getServerType());
                if (string.endsWith(from.toString())) {
                    string = string.substring(0, string.lastIndexOf(from.toString()));
                }
                ConnectAccount.AccountDevice accountDevice = new ConnectAccount.AccountDevice(string2, string, TimeUnit.SECONDS.toMillis(jSONObject.getLong(MessageAPI.LAST_CHECKIN)), deviceId.equals(string2), from);
                arrayList.add(accountDevice);
                this.log.v(accountDevice.toString());
            } catch (NullPointerException e) {
                this.log.e((Object) "Error processing response", (Throwable) e);
            } catch (JSONException e2) {
                this.log.e((Object) "Error processing JSON response", (Throwable) e2);
            }
            i = i2 + 1;
        }
        if (arrayList.size() > 0) {
            connectAccount.getDevices().clear();
            connectAccount.addDevices(arrayList);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void deregister() {
        super.deregister();
        this.activeAccount = null;
        savePreferences();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void destroy() {
        super.destroy();
    }

    public String getAccountId() {
        if (this.activeAccount == null) {
            loadPreferences();
        }
        if (this.activeAccount != null) {
            return this.activeAccount.getAccountId();
        }
        return null;
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.ACCOUNT.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public int getManagerPollInterval() {
        return (this.activeAccount == null || this.activeAccount.getDeviceCount() <= 1) ? this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue() : this.client.getInteger(ConnectConfiguration.ConfigProperty.POLL_INTERVAL_SYNC).intValue();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        loadPreferences();
        this.client.addListener(this.accountListener);
    }

    public boolean isAccountLinked() {
        if (this.activeAccount == null) {
            loadPreferences();
        }
        return this.activeAccount != null && ConnectAccount.AccountState.VERIFIED.equals(this.activeAccount.getAccountState());
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_ACCOUNT_REGISTER:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_REGISTER)");
                ConnectAccount connectAccount = (ConnectAccount) message.getData().getSerializable(Strings.DEFAULT_KEY);
                if (connectAccount != null) {
                    this.client.sendTransaction(new RegisterAccountTransaction(connectAccount));
                }
                return true;
            case MESSAGE_CLIENT_ACCOUNT_VERIFY:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_VERIFY)");
                Bundle data = message.getData();
                if (data != null) {
                    String string = data.getString(Strings.DEFAULT_KEY);
                    ConnectAccount registeringAccount = getRegisteringAccount();
                    if (registeringAccount != null) {
                        this.log.d("sendVerify(", registeringAccount.getIdentifier(), ", ", Integer.valueOf(registeringAccount.getType()), ", ", string, ")");
                        VerifyAccountTransaction verifyAccountTransaction = new VerifyAccountTransaction(registeringAccount, string);
                        if (this.activeTransactions.containsKey(verifyAccountTransaction.getName())) {
                            this.log.d("Another verification request is being processed; ignoring.");
                            this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, Strings.STATUS_CANCELED);
                        } else {
                            this.client.sendTransaction(verifyAccountTransaction);
                        }
                    } else {
                        this.log.d("No account found to verify.");
                        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_ACCOUNT_VERIFY, Strings.STATUS_FAILURE);
                    }
                }
                return true;
            case MESSAGE_CLIENT_ACCOUNT_REVERIFY:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_REVERIFY)");
                if (this.activeAccount != null) {
                    sendReverify(this.activeAccount.getIdentifier(), this.activeAccount.getType());
                }
                return true;
            case MESSAGE_CLIENT_ACCOUNT_DELETE:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_DELETE)");
                sendDeleteAccount(message.getData().getBoolean(Strings.DEFAULT_KEY));
                return true;
            case MESSAGE_CLIENT_ACCOUNT_UNLINK_DEVICE:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_UNLINK_DEVICE)");
                Bundle data2 = message.getData();
                if (data2 != null) {
                    sendUnlinkDevice(data2.getString(Strings.DEFAULT_KEY));
                }
                return true;
            case MESSAGE_CLIENT_ACCOUNT_REFRESH_DEVICES:
                this.log.v("onHandleMessage(MESSAGE_CLIENT_ACCOUNT_REFRESH_DEVICES)");
                sendDeviceList();
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        if (this.validCommands.isResponseFor(COMMAND_ACCOUNT, response)) {
            onResponseAccount(response);
            return;
        }
        if (this.validCommands.isResponseFor(COMMAND_ACCOUNT_CHECK, response)) {
            onResponseAccountCheck(response);
            return;
        }
        if (this.validCommands.isResponseFor(COMMAND_REVERIFY, response)) {
            onResponseReverify(response);
            return;
        }
        if (this.validCommands.isResponseFor(COMMAND_UNLINK_DEVICE, response)) {
            onResponseUnlinkDevice(response);
        } else if (this.validCommands.isResponseFor(COMMAND_DEVICE_LIST, response)) {
            onResponseDeviceList(response);
        } else if (this.validCommands.isResponseFor(COMMAND_DELETE_ACCOUNT, response)) {
            onResponseDeleteAccount(response);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (Strings.BUILD_TYPE_DEV.equals(this.client.getBuildProps().get(BuildProps.BuildProperty.SWYPE_BUILD_TYPE))) {
            String string = this.client.getString(ConnectConfiguration.ConfigProperty.ACCOUNT_ACCOUNT_EMAIL);
            if (string.length() > 0) {
                this.log.d("AccountManager: Auto-registering an account for TESTING PURPOSES: " + string);
            }
        }
        managerStartComplete();
    }
}
