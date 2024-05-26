package io.fabric.sdk.android.services.concurrency;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import java.util.Collection;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public abstract class PriorityAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements Dependency<Task>, PriorityProvider, Task {
    private final PriorityTask priorityTask = new PriorityTask();

    @Override // java.lang.Comparable
    public int compareTo(Object another) {
        return Priority.compareTo(this, another);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProxyExecutor<Result> implements Executor {
        private final Executor executor;
        private final PriorityAsyncTask task;

        public ProxyExecutor(Executor ex, PriorityAsyncTask task) {
            this.executor = ex;
            this.task = task;
        }

        @Override // java.util.concurrent.Executor
        public final void execute(Runnable command) {
            this.executor.execute(new PriorityFutureTask<Result>(command) { // from class: io.fabric.sdk.android.services.concurrency.PriorityAsyncTask.ProxyExecutor.1
                /* JADX WARN: Incorrect return type in method signature: <T::Lio/fabric/sdk/android/services/concurrency/Dependency<Lio/fabric/sdk/android/services/concurrency/Task;>;:Lio/fabric/sdk/android/services/concurrency/PriorityProvider;:Lio/fabric/sdk/android/services/concurrency/Task;>()TT; */
                @Override // io.fabric.sdk.android.services.concurrency.PriorityFutureTask
                public final Dependency getDelegate() {
                    return ProxyExecutor.this.task;
                }
            });
        }
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final void addDependency(Task task) {
        if (this.status != AsyncTask.Status.PENDING) {
            throw new IllegalStateException("Must not add Dependency after task is running");
        }
        this.priorityTask.addDependency((PriorityTask) task);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final Collection<Task> getDependencies() {
        return this.priorityTask.getDependencies();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final boolean areDependenciesMet() {
        return this.priorityTask.areDependenciesMet();
    }

    public Priority getPriority() {
        return this.priorityTask.getPriority();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final void setFinished(boolean finished) {
        this.priorityTask.setFinished(finished);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final boolean isFinished() {
        return this.priorityTask.isFinished();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final void setError(Throwable throwable) {
        this.priorityTask.setError(throwable);
    }
}
