package com.nuance.swype.input.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LocalizationUtils;

/* loaded from: classes.dex */
public class GesturesFragment extends ActionbarFragment {
    private Tutorial tutorial;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        if (this.tutorial == null) {
            this.tutorial = new Tutorial(getActivity());
        }
        return this.tutorial.getView();
    }

    @Override // com.nuance.swype.input.settings.ActionbarFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        String url = LocalizationUtils.getHtmlFileUrl(getActivity().getResources().getString(R.string.fullhelp_gestures_file), getActivity());
        if (!url.equals(this.tutorial.getLoadedUrl())) {
            this.tutorial.load(url);
            UsageData.recordScreenVisited(UsageData.Screen.GESTURES);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override // com.nuance.swype.input.settings.ActionbarFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
