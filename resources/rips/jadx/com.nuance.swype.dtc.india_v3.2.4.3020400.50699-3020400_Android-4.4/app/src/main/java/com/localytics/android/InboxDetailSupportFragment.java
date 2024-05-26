package com.localytics.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.localytics.android.Localytics;

/* loaded from: classes.dex */
public final class InboxDetailSupportFragment extends Fragment {
    private InboxWebViewController mController;

    public static InboxDetailSupportFragment newInstance(InboxCampaign campaign) {
        Bundle args = new Bundle();
        args.putParcelable("arg_campaign", campaign);
        InboxDetailSupportFragment fragment = new InboxDetailSupportFragment();
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

    @Override // android.support.v4.app.Fragment
    public final void onAttach(Context context) {
        Localytics.Log.v("[InboxDetailSupportFragment]: onAttach");
        super.onAttach(context);
        createInboxWebViewController();
        this.mController.onAttach(context);
    }

    @Override // android.support.v4.app.Fragment
    public final void onAttach(Activity activity) {
        Localytics.Log.v("[InboxDetailSupportFragment]: onAttach");
        super.onAttach(activity);
        createInboxWebViewController();
        this.mController.onAttach(activity);
    }

    @Override // android.support.v4.app.Fragment
    public final void onCreate(Bundle savedInstanceState) {
        Localytics.Log.v("[InboxDetailSupportFragment]: onCreate");
        super.onCreate(savedInstanceState);
        this.mController.onCreate(this);
    }

    @Override // android.support.v4.app.Fragment
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Localytics.Log.v("[InboxDetailSupportFragment]: onCreateView");
        return this.mController.onCreateView(this);
    }

    @Override // android.support.v4.app.Fragment
    public final void onDestroy() {
        Localytics.Log.v("[InboxDetailSupportFragment]: onDestroy");
        super.onDestroy();
        this.mController.onDestroy(this);
    }

    @Override // android.support.v4.app.Fragment
    public final void onDetach() {
        Localytics.Log.v("[InboxDetailSupportFragment]: onDetach");
        super.onDetach();
        this.mController.onDetach();
    }

    private void createInboxWebViewController() {
        if (this.mController == null) {
            this.mController = new InboxWebViewController();
        }
    }
}
