package com.crashlytics.android;

/* loaded from: classes.dex */
public final class CrashlyticsMissingDependencyException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CrashlyticsMissingDependencyException(String message) {
        super("\n" + message + "\n");
    }
}
