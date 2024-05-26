package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.Util;
import java.util.HashSet;

@TargetApi(11)
/* loaded from: classes.dex */
public final class RequestManagerFragment extends Fragment {
    private final HashSet<RequestManagerFragment> childRequestManagerFragments;
    public final ActivityFragmentLifecycle lifecycle;
    public RequestManager requestManager;
    public final RequestManagerTreeNode requestManagerTreeNode;
    private RequestManagerFragment rootRequestManagerFragment;

    public RequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @SuppressLint({"ValidFragment"})
    private RequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
        this.requestManagerTreeNode = new FragmentRequestManagerTreeNode(this, (byte) 0);
        this.childRequestManagerFragments = new HashSet<>();
        this.lifecycle = lifecycle;
    }

    @Override // android.app.Fragment
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.rootRequestManagerFragment = RequestManagerRetriever.get().getRequestManagerFragment(getActivity().getFragmentManager());
            if (this.rootRequestManagerFragment == this) {
                return;
            }
            this.rootRequestManagerFragment.childRequestManagerFragments.add(this);
        } catch (IllegalStateException e) {
            if (Log.isLoggable("RMFragment", 5)) {
                Log.w("RMFragment", "Unable to register fragment with root", e);
            }
        }
    }

    @Override // android.app.Fragment
    public final void onDetach() {
        super.onDetach();
        if (this.rootRequestManagerFragment == null) {
            return;
        }
        this.rootRequestManagerFragment.childRequestManagerFragments.remove(this);
        this.rootRequestManagerFragment = null;
    }

    @Override // android.app.Fragment
    public final void onStart() {
        super.onStart();
        this.lifecycle.onStart();
    }

    @Override // android.app.Fragment
    public final void onStop() {
        super.onStop();
        this.lifecycle.onStop();
    }

    @Override // android.app.Fragment
    public final void onDestroy() {
        super.onDestroy();
        this.lifecycle.onDestroy();
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks2
    public final void onTrimMemory(int level) {
        if (this.requestManager == null) {
            return;
        }
        Glide glide = this.requestManager.glide;
        Util.assertMainThread();
        glide.memoryCache.trimMemory(level);
        glide.bitmapPool.trimMemory(level);
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public final void onLowMemory() {
        if (this.requestManager == null) {
            return;
        }
        this.requestManager.glide.clearMemory();
    }

    /* loaded from: classes.dex */
    private class FragmentRequestManagerTreeNode implements RequestManagerTreeNode {
        private FragmentRequestManagerTreeNode() {
        }

        /* synthetic */ FragmentRequestManagerTreeNode(RequestManagerFragment x0, byte b) {
            this();
        }
    }
}
