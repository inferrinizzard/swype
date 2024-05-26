package io.fabric.sdk.android;

import android.content.Context;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.PriorityAsyncTask;
import java.io.File;

/* loaded from: classes.dex */
public abstract class Kit<Result> implements Comparable<Kit> {
    public Context context;
    public Fabric fabric;
    public IdManager idManager;
    InitializationCallback<Result> initializationCallback;
    public InitializationTask<Result> initializationTask = new InitializationTask<>(this);

    public abstract Result doInBackground();

    public abstract String getIdentifier();

    public abstract String getVersion();

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(Kit kit) {
        Kit kit2 = kit;
        if (containsAnnotatedDependency(kit2)) {
            return 1;
        }
        if (kit2.containsAnnotatedDependency(this)) {
            return -1;
        }
        if (!hasAnnotatedDependency() || kit2.hasAnnotatedDependency()) {
            return (hasAnnotatedDependency() || !kit2.hasAnnotatedDependency()) ? 0 : -1;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void injectParameters(Context context, Fabric fabric, InitializationCallback<Result> callback, IdManager idManager) {
        this.fabric = fabric;
        this.context = new FabricContext(context, getIdentifier(), getPath());
        this.initializationCallback = callback;
        this.idManager = idManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.lang.Void[], Params[]] */
    public final void initialize() {
        InitializationTask<Result> initializationTask = this.initializationTask;
        ?? r2 = {0};
        PriorityAsyncTask.ProxyExecutor proxyExecutor = new PriorityAsyncTask.ProxyExecutor(this.fabric.executorService, initializationTask);
        if (initializationTask.status != AsyncTask.Status.PENDING) {
            switch (initializationTask.status) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        initializationTask.status = AsyncTask.Status.RUNNING;
        initializationTask.onPreExecute();
        initializationTask.worker.params = r2;
        proxyExecutor.execute(initializationTask.future);
    }

    public boolean onPreExecute() {
        return true;
    }

    public final String getPath() {
        return ".Fabric" + File.separator + getIdentifier();
    }

    private boolean containsAnnotatedDependency(Kit target) {
        DependsOn dependsOn = (DependsOn) getClass().getAnnotation(DependsOn.class);
        if (dependsOn != null) {
            Class[] arr$ = dependsOn.value();
            for (Class cls : arr$) {
                if (cls.equals(target.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAnnotatedDependency() {
        return ((DependsOn) getClass().getAnnotation(DependsOn.class)) != null;
    }
}
