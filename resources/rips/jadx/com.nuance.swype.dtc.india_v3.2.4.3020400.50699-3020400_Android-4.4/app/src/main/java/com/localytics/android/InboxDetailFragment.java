package com.localytics.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.localytics.android.Localytics;

@TargetApi(11)
/* loaded from: classes.dex */
public final class InboxDetailFragment extends Fragment {
    private InboxWebViewController mController;

    public static InboxDetailFragment newInstance(InboxCampaign campaign) {
        Bundle args = new Bundle();
        args.putParcelable("arg_campaign", campaign);
        InboxDetailFragment fragment = new InboxDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public final InboxCampaign getCampaign() {
        if (this.mController != null) {
            return this.mController.getCampaign();
        }
        return null;
    }

    public final MarketingWebViewManager getWebViewManager() {
        if (this.mController != null) {
            return this.mController.getWebViewManager();
        }
        return null;
    }

    @Override // android.app.Fragment
    public final void onAttach(Context context) {
        Localytics.Log.v("[InboxDetailFragment]: onAttach");
        super.onAttach(context);
        createInboxWebViewController();
        this.mController.onAttach(context);
    }

    @Override // android.app.Fragment
    public final void onAttach(Activity activity) {
        Localytics.Log.v("[InboxDetailFragment]: onAttach");
        super.onAttach(activity);
        createInboxWebViewController();
        this.mController.onAttach(activity);
    }

    @Override // android.app.Fragment
    public final void onCreate(Bundle savedInstanceState) {
        Localytics.Log.v("[InboxDetailFragment]: onCreate");
        super.onCreate(savedInstanceState);
        createInboxWebViewController();
        this.mController.onCreate(this);
    }

    @Override // android.app.Fragment
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Localytics.Log.v("[InboxDetailFragment]: onCreateView");
        return this.mController.onCreateView(this);
    }

    @Override // android.app.Fragment
    public final void onDestroy() {
        Localytics.Log.v("[InboxDetailFragment]: onDestroy");
        super.onDestroy();
        this.mController.onDestroy(this);
    }

    @Override // android.app.Fragment
    public final void onDetach() {
        Localytics.Log.v("[InboxDetailFragment]: onDetach");
        super.onDetach();
        this.mController.onDetach();
    }

    private void createInboxWebViewController() {
        if (this.mController == null) {
            this.mController = new InboxWebViewController();
        }
    }
}
