package android.support.v4.app;

import android.support.v4.content.Loader;

/* loaded from: classes.dex */
public abstract class LoaderManager {

    /* loaded from: classes.dex */
    public interface LoaderCallbacks<D> {
        Loader<D> onCreateLoader$e57f803();

        void onLoadFinished$13079eae();
    }

    public abstract <D> Loader<D> initLoader$71be8de6(LoaderCallbacks<D> loaderCallbacks);

    public boolean hasRunningLoaders() {
        return false;
    }
}
