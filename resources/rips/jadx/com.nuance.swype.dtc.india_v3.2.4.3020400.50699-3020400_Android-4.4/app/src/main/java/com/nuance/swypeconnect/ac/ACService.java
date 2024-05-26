package com.nuance.swypeconnect.ac;

/* loaded from: classes.dex */
public abstract class ACService {
    boolean isShutdown;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dependenciesMet() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract String getName();

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean requireInitialization() {
        return false;
    }

    public abstract boolean requiresDocument(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void shutdown();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void start();
}
