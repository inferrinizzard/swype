package io.fabric.sdk.android;

/* loaded from: classes.dex */
public interface InitializationCallback<T> {
    public static final InitializationCallback EMPTY = new Empty(0);

    void failure(Exception exc);

    void success$5d527811();

    /* loaded from: classes.dex */
    public static class Empty implements InitializationCallback<Object> {
        /* synthetic */ Empty(byte b) {
            this();
        }

        private Empty() {
        }

        @Override // io.fabric.sdk.android.InitializationCallback
        public final void success$5d527811() {
        }

        @Override // io.fabric.sdk.android.InitializationCallback
        public final void failure(Exception exception) {
        }
    }
}
