package android.support.v4.content;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ModernAsyncTask;
import android.support.v4.os.OperationCanceledException;
import android.support.v4.util.TimeUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public abstract class AsyncTaskLoader<D> extends Loader<D> {

    /* JADX WARN: Incorrect inner types in field signature: Landroid/support/v4/content/AsyncTaskLoader<TD;>.android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$LoadTask; */
    volatile LoadTask mCancellingTask;
    private final Executor mExecutor;
    Handler mHandler;
    long mLastLoadCompleteTime;

    /* JADX WARN: Incorrect inner types in field signature: Landroid/support/v4/content/AsyncTaskLoader<TD;>.android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$LoadTask; */
    volatile LoadTask mTask;
    long mUpdateThrottle;

    public abstract D loadInBackground();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class LoadTask extends ModernAsyncTask<Void, Void, D> implements Runnable {
        private final CountDownLatch mDone = new CountDownLatch(1);
        boolean waiting;

        LoadTask() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // android.support.v4.content.ModernAsyncTask
        /* renamed from: doInBackground$532ebdd5, reason: merged with bridge method [inline-methods] */
        public D doInBackground$42af7916() {
            try {
                return (D) AsyncTaskLoader.this.loadInBackground();
            } catch (OperationCanceledException e) {
                if (!this.mFuture.isCancelled()) {
                    throw e;
                }
                return null;
            }
        }

        @Override // android.support.v4.content.ModernAsyncTask
        protected final void onPostExecute(D data) {
            try {
                AsyncTaskLoader asyncTaskLoader = AsyncTaskLoader.this;
                if (asyncTaskLoader.mTask != this) {
                    asyncTaskLoader.dispatchOnCancelled$7168cdc6(this);
                } else if (!asyncTaskLoader.mAbandoned) {
                    asyncTaskLoader.mProcessingChange = false;
                    asyncTaskLoader.mLastLoadCompleteTime = SystemClock.uptimeMillis();
                    asyncTaskLoader.mTask = null;
                    if (asyncTaskLoader.mListener != null) {
                        asyncTaskLoader.mListener.onLoadComplete(asyncTaskLoader, data);
                    }
                }
            } finally {
                this.mDone.countDown();
            }
        }

        @Override // android.support.v4.content.ModernAsyncTask
        protected final void onCancelled$5d527811() {
            try {
                AsyncTaskLoader.this.dispatchOnCancelled$7168cdc6(this);
            } finally {
                this.mDone.countDown();
            }
        }

        @Override // java.lang.Runnable
        public final void run() {
            this.waiting = false;
            AsyncTaskLoader.this.executePendingTask();
        }
    }

    public AsyncTaskLoader(Context context) {
        this(context, ModernAsyncTask.THREAD_POOL_EXECUTOR);
    }

    private AsyncTaskLoader(Context context, Executor executor) {
        super(context);
        this.mLastLoadCompleteTime = -10000L;
        this.mExecutor = executor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.content.Loader
    public final void onForceLoad() {
        super.onForceLoad();
        cancelLoad();
        this.mTask = new LoadTask();
        executePendingTask();
    }

    @Override // android.support.v4.content.Loader
    protected final boolean onCancelLoad() {
        boolean cancelled = false;
        if (this.mTask != null) {
            if (this.mCancellingTask != null) {
                if (this.mTask.waiting) {
                    this.mTask.waiting = false;
                    this.mHandler.removeCallbacks(this.mTask);
                }
                this.mTask = null;
            } else if (this.mTask.waiting) {
                this.mTask.waiting = false;
                this.mHandler.removeCallbacks(this.mTask);
                this.mTask = null;
            } else {
                cancelled = this.mTask.mFuture.cancel(false);
                if (cancelled) {
                    this.mCancellingTask = this.mTask;
                }
                this.mTask = null;
            }
        }
        return cancelled;
    }

    final void executePendingTask() {
        if (this.mCancellingTask == null && this.mTask != null) {
            if (this.mTask.waiting) {
                this.mTask.waiting = false;
                this.mHandler.removeCallbacks(this.mTask);
            }
            if (this.mUpdateThrottle > 0 && SystemClock.uptimeMillis() < this.mLastLoadCompleteTime + this.mUpdateThrottle) {
                this.mTask.waiting = true;
                this.mHandler.postAtTime(this.mTask, this.mLastLoadCompleteTime + this.mUpdateThrottle);
                return;
            }
            LoadTask loadTask = this.mTask;
            Executor executor = this.mExecutor;
            if (loadTask.mStatus != ModernAsyncTask.Status.PENDING) {
                switch (loadTask.mStatus) {
                    case RUNNING:
                        throw new IllegalStateException("Cannot execute task: the task is already running.");
                    case FINISHED:
                        throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
                }
            }
            loadTask.mStatus = ModernAsyncTask.Status.RUNNING;
            loadTask.mWorker.mParams = null;
            executor.execute(loadTask.mFuture);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: android.support.v4.content.AsyncTaskLoader$LoadTask != android.support.v4.content.AsyncTaskLoader<D>$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$android.support.v4.content.AsyncTaskLoader$LoadTask */
    /* JADX WARN: Incorrect inner types in method signature: (Landroid/support/v4/content/AsyncTaskLoader<TD;>.android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$android/support/v4/content/AsyncTaskLoader$LoadTask;)V */
    final void dispatchOnCancelled$7168cdc6(LoadTask loadTask) {
        if (this.mCancellingTask != loadTask) {
            return;
        }
        if (this.mProcessingChange) {
            if (this.mStarted) {
                forceLoad();
            } else {
                this.mContentChanged = true;
            }
        }
        this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
        this.mCancellingTask = null;
        if (this.mOnLoadCanceledListener != null) {
            this.mOnLoadCanceledListener.onLoadCanceled$5dda1f52();
        }
        executePendingTask();
    }

    @Override // android.support.v4.content.Loader
    public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        if (this.mTask != null) {
            writer.print(prefix);
            writer.print("mTask=");
            writer.print(this.mTask);
            writer.print(" waiting=");
            writer.println(this.mTask.waiting);
        }
        if (this.mCancellingTask != null) {
            writer.print(prefix);
            writer.print("mCancellingTask=");
            writer.print(this.mCancellingTask);
            writer.print(" waiting=");
            writer.println(this.mCancellingTask.waiting);
        }
        if (this.mUpdateThrottle != 0) {
            writer.print(prefix);
            writer.print("mUpdateThrottle=");
            TimeUtils.formatDuration(this.mUpdateThrottle, writer);
            writer.print(" mLastLoadCompleteTime=");
            TimeUtils.formatDuration(this.mLastLoadCompleteTime, SystemClock.uptimeMillis(), writer);
            writer.println();
        }
    }
}
