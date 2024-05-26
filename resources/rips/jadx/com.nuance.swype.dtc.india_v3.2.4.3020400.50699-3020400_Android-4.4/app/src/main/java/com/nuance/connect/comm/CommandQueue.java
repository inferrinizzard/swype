package com.nuance.connect.comm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.comm.Command;
import com.nuance.connect.common.Strings;
import com.nuance.connect.system.Connectivity;
import com.nuance.connect.system.NetworkListener;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.Logger;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class CommandQueue {
    private static final int BACKDOWN_START = 0;
    private static final int DEFAULT_DOWNLOAD_TIMEOUT_SECONDS = 120;
    public static final int MAX_DELAY_SECONDS = (int) TimeUnit.HOURS.toSeconds(12);
    public static final int MESSAGE_CHECK_QUEUE = 3;
    private static final int MESSAGE_LAST = 3;
    public static final int MESSAGE_QUEUE_PAUSE = 1;
    public static final int MESSAGE_QUEUE_RESUME = 2;
    private static final int THREAD_POOL_MAX_THREADS = 6;
    private static final int THREAD_POOL_THREADS = 3;
    private static final int THREAD_POOL_TIMEOUT_SECONDS = 60;
    public static final int USER_TRANSACTION_DELAY_THRESHOLD = 30;
    private volatile boolean active;
    private final AnalyticsDataUsageScribeImpl analyticsScribe;
    private int backdownDelay;
    private final MessageSendingBus client;
    private final CommandQueueErrorCallback commandQueueErrorCallback;
    private final Connectivity connectivity;
    private volatile boolean hasConnectivity;
    private final HttpConnector httpConnector;
    private String minimumSSLProtocol;
    private HttpPersistentConnector persistentConnector;
    private final String requestKey;
    ResponseListener responseListener;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final AtomicInteger backdownCounter = new AtomicInteger(0);
    private int defaultDelaySeconds = 300;
    private int delayTimeoutSeconds = 120;
    private final AtomicBoolean enforceServerDelay = new AtomicBoolean(false);
    private int concurrentThreadCount = 3;
    private final List<TransactionRunnable> activeTransactions = new ArrayList();
    private volatile boolean goodConnection = false;
    private final NetworkListener networkListener = new NetworkListener() { // from class: com.nuance.connect.comm.CommandQueue.1
        @Override // com.nuance.connect.system.NetworkListener
        public void onNetworkAvailable() {
            CommandQueue.this.log.d("CommandQueue.onNetworkAvailable()");
            CommandQueue.this.hasConnectivity = true;
            CommandQueue.this.analyticsScribe.flush();
            if (!CommandQueue.this.enforceServerDelay.get() && CommandQueue.this.executor.isPaused) {
                CommandQueue.this.log.d("Resuming Transaction Queue");
                CommandQueue.this.resetTimers();
            }
            CommandQueue.this.processQueue(null);
        }

        @Override // com.nuance.connect.system.NetworkListener
        public void onNetworkDisconnect() {
            CommandQueue.this.log.d("CommandQueue.onNetworkDisconnect()");
            CommandQueue.this.hasConnectivity = false;
            CommandQueue.this.analyticsScribe.flush();
            CommandQueue.this.executor.pause();
        }

        @Override // com.nuance.connect.system.NetworkListener
        public void onNetworkUnavailable() {
            CommandQueue.this.log.d("CommandQueue.onNetworkUnavailable()");
            CommandQueue.this.hasConnectivity = false;
            CommandQueue.this.analyticsScribe.flush();
            CommandQueue.this.executor.pause();
        }
    };
    private final RejectedExecutionHandler rejectedHandler = new RejectedExecutionHandler() { // from class: com.nuance.connect.comm.CommandQueue.2
        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            CommandQueue.this.log.d("rejectedExecution");
            if (threadPoolExecutor.isShutdown() || !(runnable instanceof TransactionRunnable)) {
                return;
            }
            boolean z = false;
            synchronized (CommandQueue.this.activeTransactions) {
                if (CommandQueue.this.activeTransactions.contains(runnable)) {
                    CommandQueue.this.log.d("rejectedExecution found old command.  Resubmitting...");
                    z = true;
                }
            }
            if (z) {
                ((TransactionRunnable) runnable).setState(TransactionRunnable.CurrentState.PENDING);
                CommandQueue.this.processQueue((TransactionRunnable) runnable);
            }
        }
    };
    private final ConnectionStatus connection = new ConnectionStatus() { // from class: com.nuance.connect.comm.CommandQueue.3
        @Override // com.nuance.connect.comm.CommandQueue.ConnectionStatus
        public boolean isConnected() {
            return CommandQueue.this.isOnline();
        }
    };
    private final ConnectorCallback callback = new ConnectorCallback() { // from class: com.nuance.connect.comm.CommandQueue.4
        @Override // com.nuance.connect.comm.ConnectorCallback
        public int getDefaultDelay() {
            return CommandQueue.this.defaultDelaySeconds;
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public String getDeviceId() {
            return CommandQueue.this.client.getDeviceId();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public String getSessionId() {
            return CommandQueue.this.client.getSessionId();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onAccountInvalidated() {
            CommandQueue.this.commandQueueErrorCallback.onAccountInvalidated();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onDeviceInvalidated() {
            HashSet hashSet = new HashSet();
            synchronized (CommandQueue.this.activeTransactions) {
                Iterator it = CommandQueue.this.activeTransactions.iterator();
                while (it.hasNext()) {
                    hashSet.add(it.next());
                    it.remove();
                }
            }
            Iterator it2 = hashSet.iterator();
            while (it2.hasNext()) {
                TransactionRunnable transactionRunnable = (TransactionRunnable) it2.next();
                transactionRunnable.getTransaction().onTransactionRejected(3);
                transactionRunnable.getTransaction().onEndProcessing();
            }
            CommandQueue.this.commandQueueErrorCallback.onDeviceInvalidated();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onDownloadStatus(Command command, int i, int i2) {
            if (!CommandQueue.this.active || command.responseCallback == null) {
                return;
            }
            command.responseCallback.onDownloadStatusResponse(command, i, i2);
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onFailure(Command command, int i, String str) {
            CommandQueue.this.log.d("onFailure cmd=", command.commandFamily, "/", command.command);
            if (CommandQueue.this.active && command.responseCallback != null) {
                command.responseCallback.onFailure(command);
            }
            CommandQueue.this.commandQueueErrorCallback.onConnectionStatus(i, str);
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onResponse(Response response) {
            CommandQueue.this.log.d("onResponse cmd=", response.initialCommand.commandFamily, "/", response.initialCommand.command);
            if (CommandQueue.this.active) {
                ResponseCallback responseCallback = response.initialCommand.responseCallback;
                if (responseCallback != null) {
                    if (response.fileLocation != null) {
                        response.file = new File(response.fileLocation);
                        responseCallback.onFileResponse(response);
                    } else {
                        responseCallback.onResponse(response);
                    }
                }
                if (CommandQueue.this.responseListener != null) {
                    CommandQueue.this.responseListener.onResponse(response);
                }
            }
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onSessionInvalidated() {
            CommandQueue.this.commandQueueErrorCallback.onSessionInvalidated();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onSuccess(Command command) {
            CommandQueue.this.log.d("onSuccess cmd=", command.commandFamily, "/", command.command, " (", command.thirdPartyURL, ")");
            if (command.thirdPartyURL != null) {
                return;
            }
            if (!CommandQueue.this.goodConnection) {
                CommandQueue.this.commandQueueErrorCallback.onConnectionStatus(1, "");
                CommandQueue.this.goodConnection = true;
            }
            CommandQueue.this.resetTimers();
        }

        @Override // com.nuance.connect.comm.ConnectorCallback
        public void onUnlicensed(int i) {
            CommandQueue.this.commandQueueErrorCallback.onUnlicensed(i);
            HashSet hashSet = new HashSet();
            synchronized (CommandQueue.this.activeTransactions) {
                Iterator it = CommandQueue.this.activeTransactions.iterator();
                while (it.hasNext()) {
                    hashSet.add(it.next());
                    it.remove();
                }
            }
            Iterator it2 = hashSet.iterator();
            while (it2.hasNext()) {
                TransactionRunnable transactionRunnable = (TransactionRunnable) it2.next();
                transactionRunnable.getTransaction().onTransactionRejected(3);
                transactionRunnable.getTransaction().onEndProcessing();
            }
        }
    };
    private final MessageHandler connectionHandler = new MessageHandler(this);
    private final ConnectionPool executor = new ConnectionPool(3, 6, 60, TimeUnit.SECONDS, new PriorityBlockingQueue(11, new PriorityTaskComparator()), this.rejectedHandler);

    /* loaded from: classes.dex */
    public interface CommandQueueErrorCallback {
        void onAccountInvalidated();

        void onConnectionStatus(int i, String str);

        void onDeviceInvalidated();

        void onDeviceNotAvailable();

        void onQueuePaused(int i, int i2, String str, boolean z);

        void onSessionInvalidated();

        void onSessionNotAvailable();

        void onUnlicensed(int i);
    }

    /* loaded from: classes.dex */
    public static class CommandQueueErrorCallbackDefault implements CommandQueueErrorCallback {
        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onAccountInvalidated() {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onConnectionStatus(int i, String str) {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onDeviceInvalidated() {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onDeviceNotAvailable() {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onQueuePaused(int i, int i2, String str, boolean z) {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onSessionInvalidated() {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onSessionNotAvailable() {
        }

        @Override // com.nuance.connect.comm.CommandQueue.CommandQueueErrorCallback
        public void onUnlicensed(int i) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectionPool extends ThreadPoolExecutor {
        private volatile boolean isPaused;
        private final ReentrantLock pauseLock;
        private final Condition unpaused;

        public ConnectionPool(int i, int i2, long j, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectedExecutionHandler rejectedExecutionHandler) {
            super(i, i2, j, timeUnit, blockingQueue, rejectedExecutionHandler);
            this.pauseLock = new ReentrantLock();
            this.unpaused = this.pauseLock.newCondition();
        }

        @Override // java.util.concurrent.ThreadPoolExecutor
        protected void beforeExecute(Thread thread, Runnable runnable) {
            super.beforeExecute(thread, runnable);
            this.pauseLock.lock();
            while (this.isPaused) {
                try {
                    this.unpaused.await();
                } catch (InterruptedException e) {
                    thread.interrupt();
                    return;
                } finally {
                    this.pauseLock.unlock();
                }
            }
        }

        public void pause() {
            if (!this.isPaused) {
                CommandQueue.this.log.d("PAUSING");
            }
            this.pauseLock.lock();
            try {
                this.isPaused = true;
            } finally {
                this.pauseLock.unlock();
            }
        }

        public void resume() {
            if (this.isPaused) {
                CommandQueue.this.log.d("RESUMING");
            }
            this.pauseLock.lock();
            try {
                this.isPaused = false;
                this.unpaused.signalAll();
            } finally {
                this.pauseLock.unlock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ConnectionStatus {
        boolean isConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MessageHandler extends Handler {
        private final WeakReference<CommandQueue> mgrRef;

        public MessageHandler(CommandQueue commandQueue) {
            super(Looper.getMainLooper());
            this.mgrRef = new WeakReference<>(commandQueue);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            CommandQueue commandQueue = this.mgrRef.get();
            if (commandQueue != null) {
                commandQueue.handleMessage(this, message);
            }
        }

        public void sendMessageDelayedSeconds(Message message, int i) {
            sendMessageDelayed(message, i * 1000);
        }

        public void stop() {
            this.mgrRef.clear();
            for (int i = 0; i <= 3; i++) {
                removeMessages(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class NetworkExpirer implements Runnable {
        private final int delayTimeoutMillis;
        private Runnable dispose;
        private boolean expired;

        public NetworkExpirer() {
            this.delayTimeoutMillis = CommandQueue.this.delayTimeoutSeconds * 1000;
        }

        public NetworkExpirer(int i) {
            this.delayTimeoutMillis = i * 1000;
        }

        private void sendTimeoutMessage() {
            CommandQueue.this.connectionHandler.removeCallbacks(this);
            if (this.delayTimeoutMillis > 0) {
                CommandQueue.this.connectionHandler.postDelayed(this, this.delayTimeoutMillis);
            }
        }

        public void complete() {
            CommandQueue.this.connectionHandler.removeCallbacks(this);
        }

        public int getInterval() {
            return HardKeyboardManager.META_CTRL_ON;
        }

        public boolean isExpired() {
            return this.expired;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.expired = true;
            if (this.dispose != null) {
                this.dispose.run();
            }
        }

        public void setExpirer(Runnable runnable) {
            this.dispose = runnable;
        }

        public void start() {
            sendTimeoutMessage();
        }

        public void tick() {
            sendTimeoutMessage();
        }
    }

    /* loaded from: classes.dex */
    private static class PriorityTaskComparator implements Serializable, Comparator<Runnable> {
        private static final long serialVersionUID = -4723176824518225341L;

        private PriorityTaskComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Runnable runnable, Runnable runnable2) {
            return (runnable2 instanceof TransactionRunnable ? ((TransactionRunnable) runnable2).getPriority() : 0) - (runnable instanceof TransactionRunnable ? ((TransactionRunnable) runnable).getPriority() : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TransactionRunnable implements Runnable {
        private final CommandQueue commandQueue;
        private volatile CurrentState state = CurrentState.PENDING;
        private final Transaction transaction;

        /* loaded from: classes.dex */
        public enum CurrentState {
            PENDING,
            QUEUED,
            PROCESSING,
            PROCESSED
        }

        public TransactionRunnable(Transaction transaction, CommandQueue commandQueue) {
            this.transaction = transaction;
            this.commandQueue = commandQueue;
        }

        public int getPriority() {
            return this.transaction.getPriority();
        }

        public CurrentState getState() {
            return this.state;
        }

        public Transaction getTransaction() {
            return this.transaction;
        }

        public boolean isSame(Transaction transaction) {
            return this.transaction.isSame(transaction);
        }

        @Override // java.lang.Runnable
        public void run() {
            setState(CurrentState.PROCESSING);
            this.commandQueue.processTransaction(this);
        }

        public void setState(CurrentState currentState) {
            this.state = currentState;
        }
    }

    public CommandQueue(MessageSendingBus messageSendingBus, CommandQueueErrorCallback commandQueueErrorCallback, Connectivity connectivity, String str, String str2, int i) {
        this.client = messageSendingBus;
        this.commandQueueErrorCallback = commandQueueErrorCallback;
        this.connectivity = connectivity;
        this.minimumSSLProtocol = str;
        this.requestKey = str2;
        this.analyticsScribe = new AnalyticsDataUsageScribeImpl(this.client);
        this.httpConnector = new HttpConnector(this.client, this.connection, this.callback, this.analyticsScribe, str, str2);
        if (i > 0) {
            pauseQueue(i, 3, "Pausing queue due to existing backdown.");
        }
    }

    private void clearOfflineQueue() {
        this.log.d("clearOfflineQueue() size: ", Integer.valueOf(this.activeTransactions.size()));
        ArrayList arrayList = new ArrayList();
        synchronized (this.activeTransactions) {
            Iterator<TransactionRunnable> it = this.activeTransactions.iterator();
            while (it.hasNext()) {
                TransactionRunnable next = it.next();
                if (!next.getTransaction().onTransactionOfflineQueued() && (TransactionRunnable.CurrentState.PENDING == next.getState() || TransactionRunnable.CurrentState.QUEUED == next.getState())) {
                    arrayList.add(next);
                    it.remove();
                }
            }
        }
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            Transaction transaction = ((TransactionRunnable) it2.next()).getTransaction();
            try {
                transaction.onTransactionRejected(2);
                transaction.onEndProcessing();
            } catch (RuntimeException e) {
                this.log.w(e.getMessage());
            }
            this.log.d("Transaction ", transaction.getName(), " rejected.  Device offline or server error.");
        }
    }

    private static int fib(int i) {
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

    private int getBackdownDelay() {
        return this.backdownDelay;
    }

    private void incrementBackdownDelay() {
        int fib = fib(this.backdownCounter.get() + 1) * this.defaultDelaySeconds;
        if (fib <= MAX_DELAY_SECONDS) {
            this.backdownDelay = fib;
            this.backdownCounter.getAndIncrement();
        }
    }

    private boolean isCommandInQueue(String str) {
        synchronized (this.activeTransactions) {
            Iterator<TransactionRunnable> it = this.activeTransactions.iterator();
            while (it.hasNext()) {
                if (it.next().getTransaction().getName().equals(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processQueue(TransactionRunnable transactionRunnable) {
        this.log.d("executor active threads=", Integer.valueOf(this.executor.getActiveCount()), "; completed tasks=", Long.valueOf(this.executor.getCompletedTaskCount()), "; isActive: ", Boolean.valueOf(!this.executor.isPaused));
        if (!isOnline()) {
            this.log.d("not online... exiting");
            clearOfflineQueue();
            return;
        }
        if (this.executor.isPaused) {
            this.log.d("execution delayed... exiting");
            return;
        }
        ArrayList arrayList = new ArrayList();
        if (transactionRunnable == null) {
            synchronized (this.activeTransactions) {
                for (TransactionRunnable transactionRunnable2 : this.activeTransactions) {
                    Transaction transaction = transactionRunnable2.getTransaction();
                    this.log.d("processQueue() transaction: ", transaction.getName(), " keep if offline: ", Boolean.valueOf(transaction.onTransactionOfflineQueued()));
                    if (transactionRunnable2.getState().equals(TransactionRunnable.CurrentState.PENDING)) {
                        arrayList.add(transactionRunnable2);
                    }
                }
            }
        } else {
            arrayList.add(transactionRunnable);
        }
        Iterator it = arrayList.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            TransactionRunnable transactionRunnable3 = (TransactionRunnable) it.next();
            Transaction transaction2 = transactionRunnable3.getTransaction();
            if (transaction2.requiresDeviceId() && this.client.getDeviceId() == null) {
                if (!isCommandInQueue(this.client.getDeviceRegisterCommand()) && !z2) {
                    this.log.d("processQueue()  deviceId is null, transaction ", transaction2.getName(), " requires it, no queued register device command found -- sending register request");
                    this.commandQueueErrorCallback.onDeviceNotAvailable();
                    z2 = true;
                }
                z2 = z2;
            } else if (transaction2.requiresSessionId() && this.client.getSessionId() == null) {
                if (!z && this.client.getDeviceId() != null && !isCommandInQueue(this.client.getSessionCreateCommand())) {
                    this.log.d("processQueue()  sessionId is null, no queued create session command found -- sending session create request");
                    this.commandQueueErrorCallback.onSessionNotAvailable();
                    z = true;
                }
                z = z;
            } else {
                Command.REQUEST_TYPE requestType = transaction2.getRequestType();
                NetworkState.NetworkConfiguration foregroundConfiguration = this.connectivity.getForegroundConfiguration();
                if (requestType == Command.REQUEST_TYPE.BACKGROUND) {
                    foregroundConfiguration = this.connectivity.getBackgroundConfiguration();
                } else if (requestType == Command.REQUEST_TYPE.CRITICAL && !this.connectivity.getState().hasConnectivity(foregroundConfiguration)) {
                    foregroundConfiguration = this.connectivity.getBackgroundConfiguration();
                }
                boolean z3 = this.connectivity.getState().hasConnectivity(foregroundConfiguration) ? false : true;
                boolean z4 = (z3 || !transaction2.wifiOnly() || this.connectivity.getState().isWifi() || !foregroundConfiguration.isWifiEnabled()) ? z3 : true;
                this.log.d("processQueue idx: ", "", " transaction: ", transaction2.getName(), " sessionId=", this.client.getSessionId(), " type=", requestType, " delay=", Boolean.valueOf(z4), " executor.isPaused=", Boolean.valueOf(this.executor.isPaused));
                if (z4 && transaction2.getRequestType() == Command.REQUEST_TYPE.USER) {
                    synchronized (this.activeTransactions) {
                        this.activeTransactions.remove(transactionRunnable3);
                    }
                    transaction2.onTransactionRejected(2);
                    transaction2.onEndProcessing();
                } else if (!z4) {
                    transactionRunnable3.setState(TransactionRunnable.CurrentState.QUEUED);
                    this.executor.execute(transactionRunnable3);
                    it.remove();
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00f2, code lost:            if (r11.active != false) goto L51;     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00fe, code lost:            r12.setState(com.nuance.connect.comm.CommandQueue.TransactionRunnable.CurrentState.PENDING);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void processTransaction(com.nuance.connect.comm.CommandQueue.TransactionRunnable r12) {
        /*
            Method dump skipped, instructions count: 262
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.comm.CommandQueue.processTransaction(com.nuance.connect.comm.CommandQueue$TransactionRunnable):void");
    }

    private boolean removeTransaction(Transaction transaction) {
        Transaction transaction2 = null;
        try {
            synchronized (this.activeTransactions) {
                for (TransactionRunnable transactionRunnable : this.activeTransactions) {
                    if (transactionRunnable.getTransaction().equals(transaction)) {
                        this.activeTransactions.remove(transactionRunnable);
                        Transaction transaction3 = transactionRunnable.getTransaction();
                        if (transaction3 != null) {
                            try {
                                transaction3.onEndProcessing();
                            } catch (RuntimeException e) {
                                this.log.w(e.getMessage());
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    transaction2.onEndProcessing();
                } catch (RuntimeException e2) {
                    this.log.w(e2.getMessage());
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTimers() {
        if (this.connectionHandler == null || !this.executor.isPaused) {
            return;
        }
        this.connectionHandler.removeMessages(2);
        this.backdownCounter.set(0);
        this.backdownDelay = 0;
        this.executor.resume();
    }

    public void alarmNotification(String str, Bundle bundle) {
    }

    public void createPersistantConnection(PersistantConnectionConfig persistantConnectionConfig, boolean z) {
        this.log.d("createPersistantConnection() immediately=", Boolean.valueOf(z));
        if (this.persistentConnector == null) {
            this.persistentConnector = new HttpPersistentConnector(this.client, this.connection, this.callback, this.analyticsScribe, this.minimumSSLProtocol, this.requestKey);
        }
        this.persistentConnector.setPersistentConfig(persistantConnectionConfig, z);
    }

    protected void handleMessage(MessageHandler messageHandler, Message message) {
        int i;
        boolean z = true;
        switch (message.what) {
            case 1:
                this.goodConnection = false;
                this.executor.pause();
                int i2 = message.getData().getInt(Strings.MESSAGE_BUNDLE_DELAY);
                if (i2 == 0) {
                    i = getBackdownDelay();
                } else {
                    this.enforceServerDelay.set(true);
                    i = i2;
                }
                Message obtainMessage = messageHandler.obtainMessage(2);
                obtainMessage.arg1 = i2;
                messageHandler.sendMessageDelayedSeconds(obtainMessage, i);
                this.log.d("MESSAGE_QUEUE_PAUSE for " + i + " seconds");
                if (i <= 30) {
                    z = false;
                    break;
                } else {
                    clearOfflineQueue();
                    z = false;
                    break;
                }
            case 2:
                this.log.d("MESSAGE_QUEUE_RESUME Resuming Transaction Queue");
                if (message.arg1 == 0) {
                    incrementBackdownDelay();
                } else {
                    this.log.d("Server timeout has expired");
                    this.enforceServerDelay.set(false);
                }
                this.executor.resume();
                break;
        }
        if (z) {
            processQueue(null);
        }
    }

    public boolean hasPendingTransactions() {
        return !this.activeTransactions.isEmpty();
    }

    public boolean isIdle() {
        boolean z = this.executor.getActiveCount() == 0;
        this.log.d("isIdle=", Boolean.valueOf(z));
        return z;
    }

    public boolean isOnline() {
        return this.hasConnectivity;
    }

    public void pauseQueue(int i, int i2, String str) {
        Message obtainMessage = this.connectionHandler.obtainMessage(1);
        Bundle bundle = new Bundle();
        bundle.putInt(Strings.MESSAGE_BUNDLE_DELAY, i < 0 ? 0 : i);
        bundle.putInt("status", i2);
        bundle.putString("message", str);
        obtainMessage.setData(bundle);
        this.connectionHandler.sendMessage(obtainMessage);
        this.commandQueueErrorCallback.onQueuePaused(i, i2, str, i > 0);
    }

    public void registerResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void retryConnection() {
        this.connectionHandler.sendEmptyMessage(2);
    }

    public void sendCommand(Command command) {
        sendTransaction(new SingleCommandTransaction(command));
    }

    public void sendTransaction(Transaction transaction) {
        this.log.d("sendTransaction - starting");
        if (this.executor.isShutdown()) {
            transaction.onTransactionRejected(1);
            transaction.onEndProcessing();
            this.log.e("sendTransaction after shutdown!");
            this.log.e("sendTransaction name: " + transaction.getName());
            return;
        }
        if (transaction.getRequestType() == Command.REQUEST_TYPE.USER) {
            this.connectivity.checkAvailableNetworkConnections();
        }
        synchronized (this.activeTransactions) {
            if (!transaction.allowDuplicates()) {
                Iterator<TransactionRunnable> it = this.activeTransactions.iterator();
                while (it.hasNext()) {
                    if (it.next().isSame(transaction)) {
                        this.log.d("Ignoring duplicate of transaction: ", transaction.getName());
                        break;
                    }
                }
            }
            if (isOnline() && this.executor.isPaused && transaction.getRequestType() == Command.REQUEST_TYPE.USER) {
                this.log.d("Resuming Transaction Queue for USER transaction.");
                resetTimers();
            }
            if ((!isOnline() || this.executor.isPaused) && !transaction.onTransactionOfflineQueued()) {
                this.log.d("sendTransaction - not online so we're rejecting it");
                transaction.onTransactionRejected(2);
                transaction.onEndProcessing();
            } else {
                TransactionRunnable transactionRunnable = new TransactionRunnable(transaction, this);
                synchronized (this.activeTransactions) {
                    this.activeTransactions.add(transactionRunnable);
                }
                processQueue(transactionRunnable);
            }
        }
    }

    public void setConnectionLimit(int i) {
        this.log.d("CommandQueue.setConnectionLimit() limit=", Integer.valueOf(i));
        this.concurrentThreadCount = Math.max(1, i);
        this.executor.setCorePoolSize(this.concurrentThreadCount);
        this.executor.setMaximumPoolSize(this.concurrentThreadCount);
    }

    public void setDefaultDelaySeconds(int i) {
        this.log.d("CommandQueue.setDefaultDelaySeconds() seconds=", Integer.valueOf(i));
        this.defaultDelaySeconds = i;
    }

    public void setHTTPAnalyticsTime(int i) {
        this.log.d("CommandQueue.setHTTPAnalyticsTime() value=", Integer.valueOf(i));
        this.analyticsScribe.setTrackingInterval(i);
    }

    public void setNetworkTimeoutSeconds(int i) {
        this.log.d("CommandQueue.setNetworkTimeoutSeconds() seconds=", Integer.valueOf(i));
        this.delayTimeoutSeconds = i;
    }

    public void setServerURL(String str) {
        this.log.d("CommandQueue.setServerURL() key=", str);
        this.httpConnector.setServerURL(str);
    }

    public void start() {
        this.log.d("CommandQueue.start");
        this.active = true;
        this.connectivity.registerNetworkListener(this.networkListener);
    }

    public void stop() {
        this.active = false;
        if (this.connectionHandler != null) {
            this.connectionHandler.stop();
        }
        if (this.networkListener != null) {
            this.connectivity.unregisterNetworkListener(this.networkListener);
        }
        this.executor.shutdown();
    }

    public void updateMinimumSSLProtocol(String str) {
        this.minimumSSLProtocol = str;
        this.httpConnector.updateMinimumSSLProtocol(str);
        if (this.persistentConnector != null) {
            this.persistentConnector.updateMinimumSSLProtocol(str);
        }
    }
}
