package com.localytics.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import com.localytics.android.Localytics;
import com.nuance.connect.util.TimeConversion;
import com.nuance.swype.input.IME;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class BaseHandler extends Handler {
    private static final int MESSAGE_GET_VALUE = 2;
    static final int MESSAGE_INIT = 1;
    static final int MESSAGE_UPLOAD = 3;
    static final int MESSAGE_UPLOAD_CALLBACK = 4;
    private static final String UPLOAD_WAKE_LOCK = "UPLOAD_WAKE_LOCK";
    protected boolean doesRetry;
    LocalyticsDao mLocalyticsDao;
    BaseProvider mProvider;
    private PowerManager.WakeLock mWakeLock;
    private int maxRowToUpload;
    private int numberOfRetries;
    protected boolean shouldProcessPendingUploadMessage;
    String siloName;
    protected BaseUploadThread uploadThread;

    protected abstract void _deleteUploadedData(int i);

    protected abstract TreeMap<Integer, Object> _getDataToUpload();

    protected abstract int _getMaxRowToUpload();

    protected abstract BaseUploadThread _getUploadThread(TreeMap<Integer, Object> treeMap, String str);

    abstract void _init();

    protected abstract void _onUploadCompleted(boolean z, String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseHandler(LocalyticsDao localyticsDao, Looper looper) {
        super(looper);
        this.maxRowToUpload = 0;
        this.numberOfRetries = 0;
        this.uploadThread = null;
        this.shouldProcessPendingUploadMessage = false;
        this.doesRetry = true;
        this.mLocalyticsDao = localyticsDao;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        final String customerId;
        try {
            super.handleMessage(msg);
            Localytics.Log.v(String.format("%s handler received %s", this.siloName, msg));
            switch (msg.what) {
                case 1:
                    _init();
                    return;
                case 2:
                    final FutureTask fTask = (FutureTask) msg.obj;
                    _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.BaseHandler.1
                        @Override // java.lang.Runnable
                        public void run() {
                            fTask.run();
                        }
                    });
                    return;
                case 3:
                    Localytics.Log.d(String.format("%s handler received MESSAGE_UPLOAD", this.siloName));
                    Object[] params = (Object[]) msg.obj;
                    final Boolean adjustMaxRowToUpload = (Boolean) params[0];
                    Future<String> customerIdFuture = (Future) params[1];
                    if (customerIdFuture == null) {
                        customerId = (String) params[2];
                    } else {
                        String customerId2 = customerIdFuture.get();
                        customerId = customerId2;
                    }
                    Boolean isPendingUpload = (Boolean) params[3];
                    if (this.shouldProcessPendingUploadMessage || !isPendingUpload.booleanValue()) {
                        _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.BaseHandler.2
                            @Override // java.lang.Runnable
                            public void run() {
                                BaseHandler.this._upload(adjustMaxRowToUpload.booleanValue(), customerId);
                            }
                        });
                        return;
                    }
                    return;
                case 4:
                    Localytics.Log.d(String.format("%s handler received MESSAGE_UPLOAD_CALLBACK", this.siloName));
                    Object[] params2 = (Object[]) msg.obj;
                    final int rowsToDelete = ((Integer) params2[0]).intValue();
                    final String responseString = (String) params2[1];
                    final boolean successful = ((Boolean) params2[2]).booleanValue();
                    _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.BaseHandler.3
                        @Override // java.lang.Runnable
                        public void run() {
                            BaseHandler.this._uploadCallback(rowsToDelete, responseString, successful);
                        }
                    });
                    return;
                default:
                    handleMessageExtended(msg);
                    return;
            }
        } catch (Exception e) {
            Localytics.Log.e(String.format("%s handler can't handle message %s", this.siloName, String.valueOf(msg.what)), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleMessageExtended(Message msg) throws Exception {
        throw new Exception("Fell through switch statement");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _runBatchTransactionOnProvider(Runnable runnable) {
        if (this.mProvider != null) {
            this.mProvider.runBatchTransaction(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> FutureTask<T> getFuture(Callable<T> callable) {
        FutureTask<T> task = new FutureTask<>(callable);
        queueMessage(obtainMessage(2, task));
        return task;
    }

    <T> T getFutureTaskValue(FutureTask<T> task, T defaultValue) {
        try {
            T ret = task.get();
            return ret;
        } catch (Exception e) {
            return defaultValue;
        } catch (Throwable th) {
            return defaultValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getInt(Callable<Integer> callable) {
        return ((Integer) getType(callable, 0)).intValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getBool(Callable<Boolean> callable) {
        return ((Boolean) getType(callable, false)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getString(Callable<String> callable) {
        return (String) getType(callable, null);
    }

    <K, V> Map<K, V> getMap(Callable<Map<K, V>> callable) {
        return (Map) getType(callable, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> List<T> getList(Callable<List<T>> callable) {
        return (List) getType(callable, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> T getType(Callable<T> callable, T t) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new RuntimeException("Cannot be called on the main thread.");
        }
        return (T) getFutureTaskValue(getFuture(callable), t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean queueMessage(Message msg) {
        if (getLooper().getThread() != Thread.currentThread()) {
            return sendMessage(msg);
        }
        handleMessage(msg);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean queueMessageDelayed(Message msg, long delay) {
        return delay == 0 ? queueMessage(msg) : sendMessageDelayed(msg, delay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void upload() {
        upload(true, 0L, this.mLocalyticsDao.getCustomerIdFuture(), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void upload(Future<String> customerIdFuture) {
        upload(true, 0L, customerIdFuture, null);
    }

    void upload(boolean adjustMaxRowToUpload, long backoff, Future<String> customerIdFuture, String customerId) {
        if (backoff == 0) {
            queueMessage(obtainMessage(3, new Object[]{Boolean.valueOf(adjustMaxRowToUpload), customerIdFuture, customerId, false}));
        } else {
            queueMessageDelayed(obtainMessage(3, new Object[]{Boolean.valueOf(adjustMaxRowToUpload), customerIdFuture, customerId, true}), backoff);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _upload(boolean adjustMaxRow, String customerId) {
        int lastRowToUpload = _getMaxRowToUpload();
        if (adjustMaxRow && this.maxRowToUpload != 0 && !this.shouldProcessPendingUploadMessage) {
            this.maxRowToUpload = lastRowToUpload;
            Localytics.Log.d(String.format("Already uploading %s", this.siloName.toLowerCase()));
            return;
        }
        try {
            TreeMap<Integer, Object> data = _getDataToUpload();
            if (data.size() == 0) {
                this.maxRowToUpload = 0;
                return;
            }
            this.shouldProcessPendingUploadMessage = false;
            if (adjustMaxRow) {
                this.maxRowToUpload = lastRowToUpload;
            }
            enterWakeLock();
            BaseUploadThread _getUploadThread = _getUploadThread(data, customerId);
            this.uploadThread = _getUploadThread;
            if (_getUploadThread != null) {
                this.uploadThread.start();
            }
        } catch (Exception e) {
            Localytics.Log.w("Error occurred during upload", e);
            this.maxRowToUpload = 0;
        }
    }

    protected void _uploadCallback(int rowsToDelete, String responseString, boolean successful) {
        String customerId = this.uploadThread.customerID;
        this.uploadThread = null;
        if (rowsToDelete > 0) {
            _deleteUploadedData(rowsToDelete);
            this.numberOfRetries = 0;
        } else {
            this.numberOfRetries++;
        }
        if (!this.doesRetry || rowsToDelete == this.maxRowToUpload || this.numberOfRetries > 3) {
            if (rowsToDelete == this.maxRowToUpload) {
                _onUploadCompleted(successful, responseString);
            }
            this.numberOfRetries = 0;
            this.maxRowToUpload = 0;
            exitWakeLock();
            return;
        }
        this.shouldProcessPendingUploadMessage = true;
        upload(false, this.numberOfRetries > 0 ? IME.RETRY_DELAY_IN_MILLIS : 0L, null, customerId);
    }

    protected void enterWakeLock() {
        Context appContext = this.mLocalyticsDao.getAppContext();
        if (appContext.getPackageManager().checkPermission("android.permission.WAKE_LOCK", appContext.getPackageName()) == 0) {
            if (this.mWakeLock == null) {
                PowerManager pm = (PowerManager) appContext.getSystemService("power");
                this.mWakeLock = pm.newWakeLock(1, UPLOAD_WAKE_LOCK);
                this.mWakeLock.setReferenceCounted(false);
                if (this.mWakeLock.isHeld()) {
                    Localytics.Log.w("Wake lock will be acquired but is held when shouldn't be.");
                }
                this.mWakeLock.acquire(TimeConversion.MILLIS_IN_MINUTE);
                if (this.mWakeLock.isHeld()) {
                    Localytics.Log.v("Wake lock acquired.");
                    return;
                } else {
                    Localytics.Log.w("Localytics library failed to get wake lock");
                    return;
                }
            }
            return;
        }
        Localytics.Log.v("android.permission.WAKE_LOCK is missing from the Manifest file.");
    }

    protected void exitWakeLock() {
        Context appContext = this.mLocalyticsDao.getAppContext();
        if (appContext.getPackageManager().checkPermission("android.permission.WAKE_LOCK", appContext.getPackageName()) == 0) {
            if (this.mWakeLock != null) {
                if (!this.mWakeLock.isHeld()) {
                    Localytics.Log.w("Wake lock will be released but not held when should be.");
                }
                this.mWakeLock.release();
                if (this.mWakeLock.isHeld()) {
                    Localytics.Log.w("Wake lock was not released when it should have been.");
                } else {
                    Localytics.Log.v("Wake lock released.");
                }
                this.mWakeLock = null;
                return;
            }
            return;
        }
        Localytics.Log.v("android.permission.WAKE_LOCK is missing from the Manifest file.");
    }
}
