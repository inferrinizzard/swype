package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.bumptech.glide.RequestManager;
import java.util.HashSet;

/* loaded from: classes.dex */
public final class SupportRequestManagerFragment extends Fragment {
    private final HashSet<SupportRequestManagerFragment> childRequestManagerFragments;
    public final ActivityFragmentLifecycle lifecycle;
    public RequestManager requestManager;
    public final RequestManagerTreeNode requestManagerTreeNode;
    private SupportRequestManagerFragment rootRequestManagerFragment;

    public SupportRequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @SuppressLint({"ValidFragment"})
    private SupportRequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
        this.requestManagerTreeNode = new SupportFragmentRequestManagerTreeNode(this, (byte) 0);
        this.childRequestManagerFragments = new HashSet<>();
        this.lifecycle = lifecycle;
    }

    @Override // android.support.v4.app.Fragment
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.rootRequestManagerFragment = RequestManagerRetriever.get().getSupportRequestManagerFragment(getActivity().getSupportFragmentManager());
            if (this.rootRequestManagerFragment == this) {
                return;
            }
            this.rootRequestManagerFragment.childRequestManagerFragments.add(this);
        } catch (IllegalStateException e) {
            if (Log.isLoggable("SupportRMFragment", 5)) {
                Log.w("SupportRMFragment", "Unable to register fragment with root", e);
            }
        }
    }

    @Override // android.support.v4.app.Fragment
    public final void onDetach() {
        super.onDetach();
        if (this.rootRequestManagerFragment == null) {
            return;
        }
        this.rootRequestManagerFragment.childRequestManagerFragments.remove(this);
        this.rootRequestManagerFragment = null;
    }

    @Override // android.support.v4.app.Fragment
    public final void onStart() {
        super.onStart();
        this.lifecycle.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public final void onStop() {
        super.onStop();
        this.lifecycle.onStop();
    }

    @Override // android.support.v4.app.Fragment
    public final void onDestroy() {
        super.onDestroy();
        this.lifecycle.onDestroy();
    }

    @Override // android.support.v4.app.Fragment, android.content.ComponentCallbacks
    public final void onLowMemory() {
        super.onLowMemory();
        if (this.requestManager == null) {
            return;
        }
        this.requestManager.glide.clearMemory();
    }

    /* loaded from: classes.dex */
    private class SupportFragmentRequestManagerTreeNode implements RequestManagerTreeNode {
        private SupportFragmentRequestManagerTreeNode() {
        }

        /* synthetic */ SupportFragmentRequestManagerTreeNode(SupportRequestManagerFragment x0, byte b) {
            this();
        }
    }
}
