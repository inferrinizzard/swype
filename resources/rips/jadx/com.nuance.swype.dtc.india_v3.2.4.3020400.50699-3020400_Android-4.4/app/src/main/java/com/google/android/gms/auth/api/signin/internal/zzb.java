package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.internal.zzqt;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class zzb extends AsyncTaskLoader<Void> implements zzqt {
    private Semaphore dW;
    private Set<GoogleApiClient> dX;

    public zzb(Context context, Set<GoogleApiClient> set) {
        super(context);
        this.dW = new Semaphore(0);
        this.dX = set;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // android.support.v4.content.AsyncTaskLoader
    /* renamed from: zzafx, reason: merged with bridge method [inline-methods] */
    public Void loadInBackground() {
        int i = 0;
        Iterator<GoogleApiClient> it = this.dX.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                try {
                    this.dW.tryAcquire(i2, 5L, TimeUnit.SECONDS);
                    return null;
                } catch (InterruptedException e) {
                    Log.i("GACSignInLoader", "Unexpected InterruptedException", e);
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            i = it.next().zza(this) ? i2 + 1 : i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.content.Loader
    public final void onStartLoading() {
        this.dW.drainPermits();
        forceLoad();
    }

    @Override // com.google.android.gms.internal.zzqt
    public final void zzafy() {
        this.dW.release();
    }
}
