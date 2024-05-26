package com.bumptech.glide.manager;

import com.bumptech.glide.util.Util;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ActivityFragmentLifecycle implements Lifecycle {
    private boolean isDestroyed;
    private boolean isStarted;
    private final Set<LifecycleListener> lifecycleListeners = Collections.newSetFromMap(new WeakHashMap());

    @Override // com.bumptech.glide.manager.Lifecycle
    public final void addListener(LifecycleListener listener) {
        this.lifecycleListeners.add(listener);
        if (this.isDestroyed) {
            listener.onDestroy();
        } else if (this.isStarted) {
            listener.onStart();
        } else {
            listener.onStop();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onStart() {
        this.isStarted = true;
        Iterator i$ = Util.getSnapshot(this.lifecycleListeners).iterator();
        while (i$.hasNext()) {
            ((LifecycleListener) i$.next()).onStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onStop() {
        this.isStarted = false;
        Iterator i$ = Util.getSnapshot(this.lifecycleListeners).iterator();
        while (i$.hasNext()) {
            ((LifecycleListener) i$.next()).onStop();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onDestroy() {
        this.isDestroyed = true;
        Iterator i$ = Util.getSnapshot(this.lifecycleListeners).iterator();
        while (i$.hasNext()) {
            ((LifecycleListener) i$.next()).onDestroy();
        }
    }
}
