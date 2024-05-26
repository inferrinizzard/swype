package android.support.v7.view.menu;

/* loaded from: classes.dex */
public class BaseWrapper<T> {
    public final T mWrappedObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseWrapper(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Wrapped Object can not be null.");
        }
        this.mWrappedObject = object;
    }
}
