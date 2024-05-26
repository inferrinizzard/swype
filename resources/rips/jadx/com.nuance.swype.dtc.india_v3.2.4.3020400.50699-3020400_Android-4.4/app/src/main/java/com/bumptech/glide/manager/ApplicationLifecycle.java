package com.bumptech.glide.manager;

/* loaded from: classes.dex */
final class ApplicationLifecycle implements Lifecycle {
    @Override // com.bumptech.glide.manager.Lifecycle
    public final void addListener(LifecycleListener listener) {
        listener.onStart();
    }
}
