package android.support.v4.app;

/* loaded from: classes.dex */
public final class FragmentController {
    final FragmentHostCallback<?> mHost;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentController(FragmentHostCallback<?> callbacks) {
        this.mHost = callbacks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Fragment findFragmentByWho(String who) {
        return this.mHost.mFragmentManager.findFragmentByWho(who);
    }

    public final void noteStateNotSaved() {
        this.mHost.mFragmentManager.mStateSaved = false;
    }

    public final boolean execPendingActions() {
        return this.mHost.mFragmentManager.execPendingActions();
    }

    public final void doLoaderStart() {
        FragmentHostCallback<?> fragmentHostCallback = this.mHost;
        if (fragmentHostCallback.mLoadersStarted) {
            return;
        }
        fragmentHostCallback.mLoadersStarted = true;
        if (fragmentHostCallback.mLoaderManager != null) {
            fragmentHostCallback.mLoaderManager.doStart();
        } else if (!fragmentHostCallback.mCheckedForLoaderManager) {
            fragmentHostCallback.mLoaderManager = fragmentHostCallback.getLoaderManager("(root)", fragmentHostCallback.mLoadersStarted, false);
            if (fragmentHostCallback.mLoaderManager != null && !fragmentHostCallback.mLoaderManager.mStarted) {
                fragmentHostCallback.mLoaderManager.doStart();
            }
        }
        fragmentHostCallback.mCheckedForLoaderManager = true;
    }

    public final void doLoaderStop(boolean retain) {
        FragmentHostCallback<?> fragmentHostCallback = this.mHost;
        fragmentHostCallback.mRetainLoaders = retain;
        if (fragmentHostCallback.mLoaderManager == null || !fragmentHostCallback.mLoadersStarted) {
            return;
        }
        fragmentHostCallback.mLoadersStarted = false;
        if (retain) {
            fragmentHostCallback.mLoaderManager.doRetain();
        } else {
            fragmentHostCallback.mLoaderManager.doStop();
        }
    }
}
