package com.nuance.connect.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.util.HandlerThread;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.WeakReferenceHandler;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public abstract class SQLDataSource implements Handler.Callback {
    private static final long CLOSE_DELAY = 15000;
    private static final int MESSAGE_CLOSE = 1000;
    private static final int MESSAGE_DEBUG_LOG = 1001;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, SQLDataSource.class.getSimpleName());
    private final Context context;
    private volatile SQLiteDatabase database;
    private volatile long lastOpenedTime = 0;
    private volatile HandlerThread handlerThread = null;
    private final AtomicInteger openReferences = new AtomicInteger(0);
    private final ReentrantLock openCloseLock = new ReentrantLock();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AsyncHandlerThread extends HandlerThread {
        private final Handler resultHandler;

        AsyncHandlerThread(Handler.Callback callback) {
            Looper myLooper = Looper.myLooper();
            this.resultHandler = WeakReferenceHandler.create(myLooper == null ? Looper.getMainLooper() : myLooper, callback);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.connect.util.HandlerThread
        public Message handleMessage(Message message) {
            this.resultHandler.dispatchMessage(message);
            return null;
        }
    }

    public SQLDataSource(Context context) {
        this.context = context;
    }

    private void disableCloseTimer() {
        this.lastOpenedTime = System.currentTimeMillis();
        this.openCloseLock.lock();
        try {
            if (this.handlerThread != null) {
                this.handlerThread.removeMessages(MESSAGE_CLOSE);
            }
        } finally {
            this.openCloseLock.unlock();
        }
    }

    private void restartCloseTimer() {
        this.lastOpenedTime = System.currentTimeMillis();
        this.openCloseLock.lock();
        try {
            if (this.handlerThread != null && this.openReferences.get() <= 0) {
                this.handlerThread.removeMessages(MESSAGE_CLOSE);
                this.handlerThread.processDelayed(MESSAGE_CLOSE, CLOSE_DELAY);
            }
        } finally {
            this.openCloseLock.unlock();
        }
    }

    private void setDatabase(SQLiteDatabase sQLiteDatabase) {
        this.openCloseLock.lock();
        try {
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null && sQLiteDatabase2.isOpen()) {
                log.e("Still an open database");
                sQLiteDatabase2.close();
            }
            this.database = sQLiteDatabase;
        } finally {
            this.openCloseLock.unlock();
        }
    }

    public void beginTransaction() {
        this.openReferences.incrementAndGet();
        this.openCloseLock.lock();
        try {
            SQLiteDatabase database = getDatabase();
            disableCloseTimer();
            if (database != null) {
                database.beginTransaction();
            }
        } finally {
            this.openCloseLock.unlock();
        }
    }

    public void decrementOpenRefCount(int i) {
        int decrementAndGet = this.openReferences.decrementAndGet();
        if (decrementAndGet <= 0) {
            restartCloseTimer();
            if (decrementAndGet < 0) {
                log.e("Error in reference state: count=" + decrementAndGet);
                this.openReferences.set(0);
            }
        }
    }

    public void endTransaction() {
        this.openReferences.decrementAndGet();
        this.openCloseLock.lock();
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                log.e("database is null for endTransaction!");
                return;
            }
            sQLiteDatabase.endTransaction();
            if (sQLiteDatabase.inTransaction()) {
                return;
            }
            restartCloseTimer();
        } finally {
            this.openCloseLock.unlock();
        }
    }

    public SQLiteDatabase getDatabase() {
        this.openCloseLock.lock();
        try {
            if (openSafe()) {
                return this.database;
            }
            log.e("Could not open the database");
            throw new IllegalStateException("Could not open the database");
        } finally {
            this.openCloseLock.unlock();
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MESSAGE_CLOSE /* 1000 */:
                log.d("Database close requested. Thread=", Long.valueOf(Thread.currentThread().getId()), ", handler=", this.handlerThread, ", openReferences=", Integer.valueOf(this.openReferences.get()));
                if (this.openCloseLock.tryLock()) {
                    try {
                        if (this.lastOpenedTime <= System.currentTimeMillis() - CLOSE_DELAY && this.openReferences.get() <= 0) {
                            log.d("Database idle, closing now. ");
                            HandlerThread handlerThread = this.handlerThread;
                            this.handlerThread = null;
                            SQLiteDatabase sQLiteDatabase = this.database;
                            if (sQLiteDatabase != null) {
                                sQLiteDatabase.close();
                            }
                            if (handlerThread != null) {
                                handlerThread.quit();
                            }
                        }
                    } finally {
                        this.openCloseLock.unlock();
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public int incrementOpenRefCount() {
        int incrementAndGet = this.openReferences.incrementAndGet();
        disableCloseTimer();
        return incrementAndGet;
    }

    public boolean isOpen() {
        boolean z;
        this.openCloseLock.lock();
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase != null) {
                if (sQLiteDatabase.isOpen()) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            this.openCloseLock.unlock();
        }
    }

    public abstract SQLiteDatabase open() throws SQLException;

    public boolean openSafe() {
        boolean z = false;
        this.openCloseLock.lock();
        try {
            if (this.handlerThread != null) {
                this.handlerThread.removeMessages(MESSAGE_CLOSE);
            } else {
                this.handlerThread = new AsyncHandlerThread(this);
                this.handlerThread.start();
            }
            restartCloseTimer();
            try {
                if (!isOpen()) {
                    setDatabase(open());
                }
                z = isOpen();
            } catch (SQLException e) {
                log.e("Error opening db: ", e.getMessage());
            } catch (Exception e2) {
                log.e("Error opening db: ", e2.getMessage());
            }
            return z;
        } finally {
            this.openCloseLock.unlock();
        }
    }

    public void setTransactionSuccessful() {
        this.openCloseLock.lock();
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                log.e("database is null for endTransaction!");
            } else {
                sQLiteDatabase.setTransactionSuccessful();
            }
        } finally {
            this.openCloseLock.unlock();
        }
    }
}
