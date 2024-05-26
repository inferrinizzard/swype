package io.fabric.sdk.android.services.concurrency;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/* loaded from: classes.dex */
public class PriorityFutureTask<V> extends FutureTask<V> implements Dependency<Task>, PriorityProvider, Task {
    final Object delegate;

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final /* bridge */ /* synthetic */ void addDependency(Task task) {
        ((Dependency) ((PriorityProvider) getDelegate())).addDependency(task);
    }

    public PriorityFutureTask(Callable<V> callable) {
        super(callable);
        this.delegate = checkAndInitDelegate(callable);
    }

    public PriorityFutureTask(Runnable runnable, V result) {
        super(runnable, result);
        this.delegate = checkAndInitDelegate(runnable);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object another) {
        return ((PriorityProvider) getDelegate()).compareTo(another);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final Collection<Task> getDependencies() {
        return ((Dependency) ((PriorityProvider) getDelegate())).getDependencies();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final boolean areDependenciesMet() {
        return ((Dependency) ((PriorityProvider) getDelegate())).areDependenciesMet();
    }

    @Override // io.fabric.sdk.android.services.concurrency.PriorityProvider
    public final Priority getPriority() {
        return ((PriorityProvider) getDelegate()).getPriority();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final void setFinished(boolean finished) {
        ((Task) ((PriorityProvider) getDelegate())).setFinished(finished);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final boolean isFinished() {
        return ((Task) ((PriorityProvider) getDelegate())).isFinished();
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final void setError(Throwable throwable) {
        ((Task) ((PriorityProvider) getDelegate())).setError(throwable);
    }

    /* JADX WARN: Incorrect return type in method signature: <T::Lio/fabric/sdk/android/services/concurrency/Dependency<Lio/fabric/sdk/android/services/concurrency/Task;>;:Lio/fabric/sdk/android/services/concurrency/PriorityProvider;:Lio/fabric/sdk/android/services/concurrency/Task;>()TT; */
    public Dependency getDelegate() {
        return (Dependency) this.delegate;
    }

    /* JADX WARN: Incorrect return type in method signature: <T::Lio/fabric/sdk/android/services/concurrency/Dependency<Lio/fabric/sdk/android/services/concurrency/Task;>;:Lio/fabric/sdk/android/services/concurrency/PriorityProvider;:Lio/fabric/sdk/android/services/concurrency/Task;>(Ljava/lang/Object;)TT; */
    private static Dependency checkAndInitDelegate(Object object) {
        return PriorityTask.isProperDelegate(object) ? (Dependency) object : new PriorityTask();
    }
}
