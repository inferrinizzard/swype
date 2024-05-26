package android.support.v4.view;

import android.view.WindowInsets;

/* loaded from: classes.dex */
final class WindowInsetsCompatApi21 extends WindowInsetsCompat {
    final WindowInsets mSource;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowInsetsCompatApi21(WindowInsets source) {
        this.mSource = source;
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final int getSystemWindowInsetLeft() {
        return this.mSource.getSystemWindowInsetLeft();
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final int getSystemWindowInsetTop() {
        return this.mSource.getSystemWindowInsetTop();
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final int getSystemWindowInsetRight() {
        return this.mSource.getSystemWindowInsetRight();
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final int getSystemWindowInsetBottom() {
        return this.mSource.getSystemWindowInsetBottom();
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final boolean isConsumed() {
        return this.mSource.isConsumed();
    }

    @Override // android.support.v4.view.WindowInsetsCompat
    public final WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return new WindowInsetsCompatApi21(this.mSource.replaceSystemWindowInsets(left, top, right, bottom));
    }
}
