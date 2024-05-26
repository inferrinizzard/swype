package android.support.v4.content;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
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
abstract class ModernAsyncTask<Params, Progress, Result> {
    public static final Executor THREAD_POOL_EXECUTOR;
    private static volatile Executor sDefaultExecutor;
    private static InternalHandler sHandler;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() { // from class: android.support.v4.content.ModernAsyncTask.1
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable r) {
            return new Thread(r, "ModernAsyncTask #" + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(10);
    volatile Status mStatus = Status.PENDING;
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    final WorkerRunnable<Params, Result> mWorker = new WorkerRunnable<Params, Result>() { // from class: android.support.v4.content.ModernAsyncTask.2
        @Override // java.util.concurrent.Callable
        public final Result call() throws Exception {
            ModernAsyncTask.this.mTaskInvoked.set(true);
            Process.setThreadPriority(10);
            return (Result) ModernAsyncTask.this.postResult(ModernAsyncTask.this.doInBackground$42af7916());
        }
    };
    final FutureTask<Result> mFuture = new FutureTask<Result>(this.mWorker) { // from class: android.support.v4.content.ModernAsyncTask.3
        @Override // java.util.concurrent.FutureTask
        protected final void done() {
            try {
                Result result = get();
                ModernAsyncTask.access$300(ModernAsyncTask.this, result);
            } catch (InterruptedException e) {
                Log.w("AsyncTask", e);
            } catch (CancellationException e2) {
                ModernAsyncTask.access$300(ModernAsyncTask.this, null);
            } catch (ExecutionException e3) {
                throw new RuntimeException("An error occurred while executing doInBackground()", e3.getCause());
            } catch (Throwable t) {
                throw new RuntimeException("An error occurred while executing doInBackground()", t);
            }
        }
    };

    /* loaded from: classes.dex */
    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    protected abstract Result doInBackground$42af7916();

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 128, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
        sDefaultExecutor = threadPoolExecutor;
    }

    private static Handler getHandler() {
        InternalHandler internalHandler;
        synchronized (ModernAsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            internalHandler = sHandler;
        }
        return internalHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Result postResult(Result result) {
        getHandler().obtainMessage(1, new AsyncTaskResult(this, result)).sendToTarget();
        return result;
    }

    protected void onPostExecute(Result result) {
    }

    protected static void onProgressUpdate$1b4f7664() {
    }

    protected void onCancelled$5d527811() {
    }

    /* renamed from: android.support.v4.content.ModernAsyncTask$4, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$android$support$v4$content$ModernAsyncTask$Status = new int[Status.values().length];

        static {
            try {
                $SwitchMap$android$support$v4$content$ModernAsyncTask$Status[Status.RUNNING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$support$v4$content$ModernAsyncTask$Status[Status.FINISHED.ordinal()] = 2;
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
                    ModernAsyncTask.access$400(result.mTask, result.mData[0]);
                    return;
                case 2:
                    ModernAsyncTask.onProgressUpdate$1b4f7664();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;

        private WorkerRunnable() {
        }

        /* synthetic */ WorkerRunnable(byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AsyncTaskResult<Data> {
        final Data[] mData;
        final ModernAsyncTask mTask;

        AsyncTaskResult(ModernAsyncTask task, Data... data) {
            this.mTask = task;
            this.mData = data;
        }
    }

    static /* synthetic */ void access$300(ModernAsyncTask x0, Object x1) {
        if (x0.mTaskInvoked.get()) {
            return;
        }
        x0.postResult(x1);
    }

    static /* synthetic */ void access$400(ModernAsyncTask x0, Object x1) {
        if (x0.mFuture.isCancelled()) {
            x0.onCancelled$5d527811();
        } else {
            x0.onPostExecute(x1);
        }
        x0.mStatus = Status.FINISHED;
    }
}
