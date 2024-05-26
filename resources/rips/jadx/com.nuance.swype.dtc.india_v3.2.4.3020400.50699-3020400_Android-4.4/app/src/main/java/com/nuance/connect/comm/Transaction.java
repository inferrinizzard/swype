package com.nuance.connect.comm;

import android.content.Context;
import com.nuance.connect.comm.Command;

/* loaded from: classes.dex */
public interface Transaction {
    public static final int REJECTED_INVALID_DEVICE = 3;
    public static final int REJECTED_OFFLINE = 2;
    public static final int REJECTED_SHUTDOWN = 1;

    boolean allowDuplicates();

    void cancel();

    String createDownloadFile(Context context);

    String getDownloadFile();

    String getName();

    Command getNextCommand();

    PersistantConnectionConfig getPersistantConfig();

    int getPriority();

    Command.REQUEST_TYPE getRequestType();

    boolean isSame(Transaction transaction);

    void onEndProcessing();

    boolean onTransactionOfflineQueued();

    void onTransactionRejected(int i);

    boolean requiresDeviceId();

    boolean requiresPersistantConnection();

    boolean requiresSessionId();

    boolean wifiOnly();
}
