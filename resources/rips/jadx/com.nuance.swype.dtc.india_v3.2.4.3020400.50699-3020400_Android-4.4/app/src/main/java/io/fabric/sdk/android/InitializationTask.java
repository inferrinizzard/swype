package io.fabric.sdk.android;

import io.fabric.sdk.android.services.common.TimingMetric;
import io.fabric.sdk.android.services.concurrency.Priority;
import io.fabric.sdk.android.services.concurrency.PriorityAsyncTask;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;

/* loaded from: classes.dex */
public final class InitializationTask<Result> extends PriorityAsyncTask<Void, Void, Result> {
    final Kit<Result> kit;

    public InitializationTask(Kit<Result> kit) {
        this.kit = kit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
    public final void onPreExecute() {
        super.onPreExecute();
        TimingMetric timingMetric = createAndStartTimingMetric("onPreExecute");
        try {
            try {
                boolean result = this.kit.onPreExecute();
                timingMetric.stopMeasuring();
                if (!result) {
                    cancel$138603();
                }
            } catch (UnmetDependencyException e) {
                throw e;
            } catch (Exception ex) {
                Fabric.getLogger().e("Fabric", "Failure onPreExecute()", ex);
                timingMetric.stopMeasuring();
                cancel$138603();
            }
        } catch (Throwable th) {
            timingMetric.stopMeasuring();
            cancel$138603();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
    public final void onPostExecute$5d527811() {
        this.kit.initializationCallback.success$5d527811();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
    public final void onCancelled$5d527811() {
        String message = this.kit.getIdentifier() + " Initialization was cancelled";
        InitializationException exception = new InitializationException(message);
        this.kit.initializationCallback.failure(exception);
    }

    @Override // io.fabric.sdk.android.services.concurrency.PriorityAsyncTask, io.fabric.sdk.android.services.concurrency.PriorityProvider
    public final Priority getPriority() {
        return Priority.HIGH;
    }

    private TimingMetric createAndStartTimingMetric(String event) {
        TimingMetric timingMetric = new TimingMetric(this.kit.getIdentifier() + "." + event, "KitInitialization");
        timingMetric.startMeasuring();
        return timingMetric;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
    public final /* bridge */ /* synthetic */ Object doInBackground$42af7916() {
        TimingMetric createAndStartTimingMetric = createAndStartTimingMetric("doInBackground");
        Result result = null;
        if (!this.cancelled.get()) {
            result = this.kit.doInBackground();
        }
        createAndStartTimingMetric.stopMeasuring();
        return result;
    }
}
