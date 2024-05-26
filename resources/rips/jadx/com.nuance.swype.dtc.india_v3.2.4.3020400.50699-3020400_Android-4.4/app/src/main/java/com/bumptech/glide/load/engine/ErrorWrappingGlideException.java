package com.bumptech.glide.load.engine;

/* loaded from: classes.dex */
public final class ErrorWrappingGlideException extends Exception {
    public ErrorWrappingGlideException(Error error) {
        super(error);
    }

    @Override // java.lang.Throwable
    public final /* bridge */ /* synthetic */ Throwable getCause() {
        return (Error) super.getCause();
    }
}
