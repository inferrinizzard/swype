package io.fabric.sdk.android.services.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class AsyncTask<Params, Progress, Result> {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE;
    public static final Executor SERIAL_EXECUTOR;
    public static final Executor THREAD_POOL_EXECUTOR;
    private static volatile Executor defaultExecutor;
    private static final InternalHandler handler;
    private static final BlockingQueue<Runnable> poolWorkQueue;
    private static final ThreadFactory threadFactory;
    public volatile Status status = Status.PENDING;
    public final AtomicBoolean cancelled = new AtomicBoolean();
    private final AtomicBoolean taskInvoked = new AtomicBoolean();
    public final WorkerRunnable<Params, Result> worker = new WorkerRunnable<Params, Result>() { // from class: io.fabric.sdk.android.services.concurrency.AsyncTask.2
        @Override // java.util.concurrent.Callable
        public final Result call() throws Exception {
            AsyncTask.this.taskInvoked.set(true);
            Process.setThreadPriority(10);
            return (Result) AsyncTask.this.postResult(AsyncTask.this.doInBackground$42af7916());
        }
    };
    public final FutureTask<Result> future = new FutureTask<Result>(this.worker) { // from class: io.fabric.sdk.android.services.concurrency.AsyncTask.3
        @Override // java.util.concurrent.FutureTask
        protected final void done() {
            try {
                AsyncTask.access$400(AsyncTask.this, get());
            } catch (InterruptedException e) {
                Log.w("AsyncTask", e);
            } catch (CancellationException e2) {
                AsyncTask.access$400(AsyncTask.this, null);
            } catch (ExecutionException e3) {
                throw new RuntimeException("An error occured while executing doInBackground()", e3.getCause());
            }
        }
    };

    /* loaded from: classes.dex */
    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    public abstract Result doInBackground$42af7916();

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = availableProcessors + 1;
        MAXIMUM_POOL_SIZE = (CPU_COUNT * 2) + 1;
        threadFactory = new ThreadFactory() { // from class: io.fabric.sdk.android.services.concurrency.AsyncTask.1
            private final AtomicInteger count = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable r) {
                return new Thread(r, "AsyncTask #" + this.count.getAndIncrement());
            }
        };
        poolWorkQueue = new LinkedBlockingQueue(128);
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1L, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
        SERIAL_EXECUTOR = new SerialExecutor((byte) 0);
        handler = new InternalHandler();
        defaultExecutor = SERIAL_EXECUTOR;
    }

    /* loaded from: classes.dex */
    private static class SerialExecutor implements Executor {
        Runnable active;
        final LinkedList<Runnable> tasks;

        private SerialExecutor() {
            this.tasks = new LinkedList<>();
        }

        /* synthetic */ SerialExecutor(byte b) {
            this();
        }

        @Override // java.util.concurrent.Executor
        public final synchronized void execute(final Runnable r) {
            this.tasks.offer(new Runnable() { // from class: io.fabric.sdk.android.services.concurrency.AsyncTask.SerialExecutor.1
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }
                }
            });
            if (this.active == null) {
                scheduleNext();
            }
        }

        protected final synchronized void scheduleNext() {
            Runnable poll = this.tasks.poll();
            this.active = poll;
            if (poll != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(this.active);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Result postResult(Result result) {
        handler.obtainMessage(1, new AsyncTaskResult(this, result)).sendToTarget();
        return result;
    }

    public void onPreExecute() {
    }

    public void onPostExecute$5d527811() {
    }

    protected static void onProgressUpdate$1b4f7664() {
    }

    public void onCancelled$5d527811() {
    }

    public final boolean cancel$138603() {
        this.cancelled.set(true);
        return this.future.cancel(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.fabric.sdk.android.services.concurrency.AsyncTask$4, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass4 {
        public static final /* synthetic */ int[] $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status = new int[Status.values().length];

        static {
            try {
                $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[Status.RUNNING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[Status.FINISHED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public final void handleMessage(Message msg) {
            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case 1:
                    AsyncTask.access$500$1d60ec96(result.task);
                    return;
                case 2:
                    AsyncTask.onProgressUpdate$1b4f7664();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        public Params[] params;

        private WorkerRunnable() {
        }

        /* synthetic */ WorkerRunnable(byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AsyncTaskResult<Data> {
        final Data[] data;
        final AsyncTask task;

        AsyncTaskResult(AsyncTask task, Data... data) {
            this.task = task;
            this.data = data;
        }
    }

    static /* synthetic */ void access$400(AsyncTask x0, Object x1) {
        if (x0.taskInvoked.get()) {
            return;
        }
        x0.postResult(x1);
    }

    static /* synthetic */ void access$500$1d60ec96(AsyncTask x0) {
        if (x0.cancelled.get()) {
            x0.onCancelled$5d527811();
        } else {
            x0.onPostExecute$5d527811();
        }
        x0.status = Status.FINISHED;
    }
}
