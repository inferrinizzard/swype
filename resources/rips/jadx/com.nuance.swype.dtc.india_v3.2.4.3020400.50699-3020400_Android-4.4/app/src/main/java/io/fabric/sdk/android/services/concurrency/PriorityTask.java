package io.fabric.sdk.android.services.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class PriorityTask implements Dependency<Task>, PriorityProvider, Task {
    private final List<Task> dependencies = new ArrayList();
    private final AtomicBoolean hasRun = new AtomicBoolean(false);
    private final AtomicReference<Throwable> throwable = new AtomicReference<>(null);

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final synchronized Collection<Task> getDependencies() {
        return Collections.unmodifiableCollection(this.dependencies);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final synchronized void addDependency(Task task) {
        this.dependencies.add(task);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Dependency
    public final boolean areDependenciesMet() {
        Iterator i$ = getDependencies().iterator();
        while (i$.hasNext()) {
            if (!i$.next().isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final synchronized void setFinished(boolean finished) {
        this.hasRun.set(finished);
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final boolean isFinished() {
        return this.hasRun.get();
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override // io.fabric.sdk.android.services.concurrency.Task
    public final void setError(Throwable throwable) {
        this.throwable.set(throwable);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object other) {
        return Priority.compareTo(this, other);
    }

    public static boolean isProperDelegate(Object object) {
        try {
            Dependency<Task> dep = (Dependency) object;
            Task task = (Task) object;
            PriorityProvider provider = (PriorityProvider) object;
            return (dep == null || task == null || provider == null) ? false : true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
