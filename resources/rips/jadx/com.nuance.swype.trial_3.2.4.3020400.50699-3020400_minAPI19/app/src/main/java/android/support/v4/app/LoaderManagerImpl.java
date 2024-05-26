package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import com.nuance.swype.input.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: LoaderManager.java */
/* loaded from: classes.dex */
public final class LoaderManagerImpl extends LoaderManager {
    static boolean DEBUG = false;
    boolean mCreatingLoader;
    FragmentHostCallback mHost;
    boolean mRetaining;
    boolean mStarted;
    final String mWho;
    final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat<>();
    final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: LoaderManager.java */
    /* loaded from: classes.dex */
    public final class LoaderInfo implements Loader.OnLoadCanceledListener<Object>, Loader.OnLoadCompleteListener<Object> {
        LoaderManager.LoaderCallbacks<Object> mCallbacks;
        Object mData;
        boolean mDeliveredData;
        boolean mDestroyed;
        boolean mHaveData;
        boolean mListenerRegistered;
        Loader<Object> mLoader;
        LoaderInfo mPendingLoader;
        boolean mReportNextStart;
        boolean mRetaining;
        boolean mRetainingStarted;
        boolean mStarted;
        final int mId = 0;
        final Bundle mArgs = null;

        public LoaderInfo(int id, Bundle args, LoaderManager.LoaderCallbacks<Object> callbacks) {
            this.mCallbacks = callbacks;
        }

        final void start() {
            if (this.mRetaining && this.mRetainingStarted) {
                this.mStarted = true;
                return;
            }
            if (!this.mStarted) {
                this.mStarted = true;
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Starting: " + this);
                }
                if (this.mLoader == null && this.mCallbacks != null) {
                    this.mLoader = this.mCallbacks.onCreateLoader$e57f803();
                }
                if (this.mLoader != null) {
                    if (this.mLoader.getClass().isMemberClass() && !Modifier.isStatic(this.mLoader.getClass().getModifiers())) {
                        throw new IllegalArgumentException("Object returned from onCreateLoader must not be a non-static inner member class: " + this.mLoader);
                    }
                    if (!this.mListenerRegistered) {
                        Loader<Object> loader = this.mLoader;
                        int i = this.mId;
                        if (loader.mListener != null) {
                            throw new IllegalStateException("There is already a listener registered");
                        }
                        loader.mListener = this;
                        loader.mId = i;
                        Loader<Object> loader2 = this.mLoader;
                        if (loader2.mOnLoadCanceledListener != null) {
                            throw new IllegalStateException("There is already a listener registered");
                        }
                        loader2.mOnLoadCanceledListener = this;
                        this.mListenerRegistered = true;
                    }
                    Loader<Object> loader3 = this.mLoader;
                    loader3.mStarted = true;
                    loader3.mReset = false;
                    loader3.mAbandoned = false;
                    loader3.onStartLoading();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void stop() {
            if (LoaderManagerImpl.DEBUG) {
                Log.v("LoaderManager", "  Stopping: " + this);
            }
            this.mStarted = false;
            if (!this.mRetaining && this.mLoader != null && this.mListenerRegistered) {
                this.mListenerRegistered = false;
                this.mLoader.unregisterListener(this);
                this.mLoader.unregisterOnLoadCanceledListener(this);
                this.mLoader.mStarted = false;
            }
        }

        final void destroy() {
            while (true) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Destroying: " + this);
                }
                this.mDestroyed = true;
                boolean needReset = this.mDeliveredData;
                this.mDeliveredData = false;
                if (this.mCallbacks != null && this.mLoader != null && this.mHaveData && needReset) {
                    if (LoaderManagerImpl.DEBUG) {
                        Log.v("LoaderManager", "  Reseting: " + this);
                    }
                    String lastBecause = null;
                    if (LoaderManagerImpl.this.mHost != null) {
                        lastBecause = LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause;
                        LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = "onLoaderReset";
                    }
                    if (LoaderManagerImpl.this.mHost != null) {
                        LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = lastBecause;
                    }
                }
                this.mCallbacks = null;
                this.mData = null;
                this.mHaveData = false;
                if (this.mLoader != null) {
                    if (this.mListenerRegistered) {
                        this.mListenerRegistered = false;
                        this.mLoader.unregisterListener(this);
                        this.mLoader.unregisterOnLoadCanceledListener(this);
                    }
                    Loader<Object> loader = this.mLoader;
                    loader.mReset = true;
                    loader.mStarted = false;
                    loader.mAbandoned = false;
                    loader.mContentChanged = false;
                    loader.mProcessingChange = false;
                }
                if (this.mPendingLoader != null) {
                    this = this.mPendingLoader;
                } else {
                    return;
                }
            }
        }

        @Override // android.support.v4.content.Loader.OnLoadCanceledListener
        public final void onLoadCanceled$5dda1f52() {
            if (LoaderManagerImpl.DEBUG) {
                Log.v("LoaderManager", "onLoadCanceled: " + this);
            }
            if (this.mDestroyed) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Ignoring load canceled -- destroyed");
                }
            } else {
                if (LoaderManagerImpl.this.mLoaders.get(this.mId) != this) {
                    if (LoaderManagerImpl.DEBUG) {
                        Log.v("LoaderManager", "  Ignoring load canceled -- not active");
                        return;
                    }
                    return;
                }
                LoaderInfo pending = this.mPendingLoader;
                if (pending != null) {
                    if (LoaderManagerImpl.DEBUG) {
                        Log.v("LoaderManager", "  Switching to pending loader: " + pending);
                    }
                    this.mPendingLoader = null;
                    LoaderManagerImpl.this.mLoaders.put(this.mId, null);
                    destroy();
                    LoaderManagerImpl.this.installLoader(pending);
                }
            }
        }

        @Override // android.support.v4.content.Loader.OnLoadCompleteListener
        public final void onLoadComplete(Loader<Object> loader, Object data) {
            if (LoaderManagerImpl.DEBUG) {
                Log.v("LoaderManager", "onLoadComplete: " + this);
            }
            if (this.mDestroyed) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Ignoring load complete -- destroyed");
                    return;
                }
                return;
            }
            if (LoaderManagerImpl.this.mLoaders.get(this.mId) != this) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Ignoring load complete -- not active");
                    return;
                }
                return;
            }
            LoaderInfo pending = this.mPendingLoader;
            if (pending != null) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "  Switching to pending loader: " + pending);
                }
                this.mPendingLoader = null;
                LoaderManagerImpl.this.mLoaders.put(this.mId, null);
                destroy();
                LoaderManagerImpl.this.installLoader(pending);
                return;
            }
            if (this.mData != data || !this.mHaveData) {
                this.mData = data;
                this.mHaveData = true;
                if (this.mStarted) {
                    callOnLoadFinished(loader, data);
                }
            }
            LoaderInfo info = LoaderManagerImpl.this.mInactiveLoaders.get(this.mId);
            if (info != null && info != this) {
                info.mDeliveredData = false;
                info.destroy();
                LoaderManagerImpl.this.mInactiveLoaders.remove(this.mId);
            }
            if (LoaderManagerImpl.this.mHost != null && !LoaderManagerImpl.this.hasRunningLoaders()) {
                LoaderManagerImpl.this.mHost.mFragmentManager.startPendingDeferredFragments();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void callOnLoadFinished(Loader<Object> loader, Object data) {
            if (this.mCallbacks != null) {
                String lastBecause = null;
                if (LoaderManagerImpl.this.mHost != null) {
                    lastBecause = LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause;
                    LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = "onLoadFinished";
                }
                try {
                    if (LoaderManagerImpl.DEBUG) {
                        StringBuilder append = new StringBuilder("  onLoadFinished in ").append(loader).append(": ");
                        StringBuilder sb = new StringBuilder(64);
                        DebugUtils.buildShortClassTag(data, sb);
                        sb.append("}");
                        Log.v("LoaderManager", append.append(sb.toString()).toString());
                    }
                    this.mCallbacks.onLoadFinished$13079eae();
                    this.mDeliveredData = true;
                } finally {
                    if (LoaderManagerImpl.this.mHost != null) {
                        LoaderManagerImpl.this.mHost.mFragmentManager.mNoTransactionsBecause = lastBecause;
                    }
                }
            }
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("LoaderInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" #");
            sb.append(this.mId);
            sb.append(" : ");
            DebugUtils.buildShortClassTag(this.mLoader, sb);
            sb.append("}}");
            return sb.toString();
        }

        public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            while (true) {
                writer.print(prefix);
                writer.print("mId=");
                writer.print(this.mId);
                writer.print(" mArgs=");
                writer.println(this.mArgs);
                writer.print(prefix);
                writer.print("mCallbacks=");
                writer.println(this.mCallbacks);
                writer.print(prefix);
                writer.print("mLoader=");
                writer.println(this.mLoader);
                if (this.mLoader != null) {
                    this.mLoader.dump(prefix + ThemeManager.NO_PRICE, fd, writer, args);
                }
                if (this.mHaveData || this.mDeliveredData) {
                    writer.print(prefix);
                    writer.print("mHaveData=");
                    writer.print(this.mHaveData);
                    writer.print("  mDeliveredData=");
                    writer.println(this.mDeliveredData);
                    writer.print(prefix);
                    writer.print("mData=");
                    writer.println(this.mData);
                }
                writer.print(prefix);
                writer.print("mStarted=");
                writer.print(this.mStarted);
                writer.print(" mReportNextStart=");
                writer.print(this.mReportNextStart);
                writer.print(" mDestroyed=");
                writer.println(this.mDestroyed);
                writer.print(prefix);
                writer.print("mRetaining=");
                writer.print(this.mRetaining);
                writer.print(" mRetainingStarted=");
                writer.print(this.mRetainingStarted);
                writer.print(" mListenerRegistered=");
                writer.println(this.mListenerRegistered);
                if (this.mPendingLoader != null) {
                    writer.print(prefix);
                    writer.println("Pending Loader ");
                    writer.print(this.mPendingLoader);
                    writer.println(":");
                    this = this.mPendingLoader;
                    prefix = prefix + ThemeManager.NO_PRICE;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl(String who, FragmentHostCallback host, boolean started) {
        this.mWho = who;
        this.mHost = host;
        this.mStarted = started;
    }

    private LoaderInfo createAndInstallLoader$348a764a(LoaderManager.LoaderCallbacks<Object> callback) {
        try {
            this.mCreatingLoader = true;
            LoaderInfo info = new LoaderInfo(0, null, callback);
            info.mLoader = callback.onCreateLoader$e57f803();
            installLoader(info);
            return info;
        } finally {
            this.mCreatingLoader = false;
        }
    }

    final void installLoader(LoaderInfo info) {
        this.mLoaders.put(info.mId, info);
        if (this.mStarted) {
            info.start();
        }
    }

    @Override // android.support.v4.app.LoaderManager
    public final <D> Loader<D> initLoader$71be8de6(LoaderManager.LoaderCallbacks<D> loaderCallbacks) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = this.mLoaders.get(0);
        if (DEBUG) {
            Log.v("LoaderManager", "initLoader in " + this + ": args=" + ((Object) null));
        }
        if (loaderInfo == null) {
            loaderInfo = createAndInstallLoader$348a764a(loaderCallbacks);
            if (DEBUG) {
                Log.v("LoaderManager", "  Created new loader " + loaderInfo);
            }
        } else {
            if (DEBUG) {
                Log.v("LoaderManager", "  Re-using existing loader " + loaderInfo);
            }
            loaderInfo.mCallbacks = loaderCallbacks;
        }
        if (loaderInfo.mHaveData && this.mStarted) {
            loaderInfo.callOnLoadFinished(loaderInfo.mLoader, loaderInfo.mData);
        }
        return (Loader<D>) loaderInfo.mLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doStart() {
        if (DEBUG) {
            Log.v("LoaderManager", "Starting in " + this);
        }
        if (this.mStarted) {
            RuntimeException e = new RuntimeException("here");
            e.fillInStackTrace();
            Log.w("LoaderManager", "Called doStart when already started: " + this, e);
        } else {
            this.mStarted = true;
            for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
                this.mLoaders.valueAt(i).start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doStop() {
        if (DEBUG) {
            Log.v("LoaderManager", "Stopping in " + this);
        }
        if (!this.mStarted) {
            RuntimeException e = new RuntimeException("here");
            e.fillInStackTrace();
            Log.w("LoaderManager", "Called doStop when not started: " + this, e);
        } else {
            for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
                this.mLoaders.valueAt(i).stop();
            }
            this.mStarted = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doRetain() {
        if (DEBUG) {
            Log.v("LoaderManager", "Retaining in " + this);
        }
        if (!this.mStarted) {
            RuntimeException e = new RuntimeException("here");
            e.fillInStackTrace();
            Log.w("LoaderManager", "Called doRetain when not started: " + this, e);
            return;
        }
        this.mRetaining = true;
        this.mStarted = false;
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            LoaderInfo valueAt = this.mLoaders.valueAt(i);
            if (DEBUG) {
                Log.v("LoaderManager", "  Retaining: " + valueAt);
            }
            valueAt.mRetaining = true;
            valueAt.mRetainingStarted = valueAt.mStarted;
            valueAt.mStarted = false;
            valueAt.mCallbacks = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doReportNextStart() {
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            this.mLoaders.valueAt(i).mReportNextStart = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doReportStart() {
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            LoaderInfo valueAt = this.mLoaders.valueAt(i);
            if (valueAt.mStarted && valueAt.mReportNextStart) {
                valueAt.mReportNextStart = false;
                if (valueAt.mHaveData && !valueAt.mRetaining) {
                    valueAt.callOnLoadFinished(valueAt.mLoader, valueAt.mData);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void doDestroy() {
        if (!this.mRetaining) {
            if (DEBUG) {
                Log.v("LoaderManager", "Destroying Active in " + this);
            }
            for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
                this.mLoaders.valueAt(i).destroy();
            }
            this.mLoaders.clear();
        }
        if (DEBUG) {
            Log.v("LoaderManager", "Destroying Inactive in " + this);
        }
        for (int i2 = this.mInactiveLoaders.size() - 1; i2 >= 0; i2--) {
            this.mInactiveLoaders.valueAt(i2).destroy();
        }
        this.mInactiveLoaders.clear();
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("LoaderManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        DebugUtils.buildShortClassTag(this.mHost, sb);
        sb.append("}}");
        return sb.toString();
    }

    public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (this.mLoaders.size() > 0) {
            writer.print(prefix);
            writer.println("Active Loaders:");
            String innerPrefix = prefix + "    ";
            for (int i = 0; i < this.mLoaders.size(); i++) {
                LoaderInfo li = this.mLoaders.valueAt(i);
                writer.print(prefix);
                writer.print("  #");
                writer.print(this.mLoaders.keyAt(i));
                writer.print(": ");
                writer.println(li.toString());
                li.dump(innerPrefix, fd, writer, args);
            }
        }
        if (this.mInactiveLoaders.size() > 0) {
            writer.print(prefix);
            writer.println("Inactive Loaders:");
            String innerPrefix2 = prefix + "    ";
            for (int i2 = 0; i2 < this.mInactiveLoaders.size(); i2++) {
                LoaderInfo li2 = this.mInactiveLoaders.valueAt(i2);
                writer.print(prefix);
                writer.print("  #");
                writer.print(this.mInactiveLoaders.keyAt(i2));
                writer.print(": ");
                writer.println(li2.toString());
                li2.dump(innerPrefix2, fd, writer, args);
            }
        }
    }

    @Override // android.support.v4.app.LoaderManager
    public final boolean hasRunningLoaders() {
        boolean loadersRunning = false;
        int count = this.mLoaders.size();
        for (int i = 0; i < count; i++) {
            LoaderInfo li = this.mLoaders.valueAt(i);
            loadersRunning |= li.mStarted && !li.mDeliveredData;
        }
        return loadersRunning;
    }
}
