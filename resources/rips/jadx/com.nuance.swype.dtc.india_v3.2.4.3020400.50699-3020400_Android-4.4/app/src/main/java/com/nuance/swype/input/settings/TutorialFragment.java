package com.nuance.swype.input.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LocalizationUtils;

/* loaded from: classes.dex */
public class TutorialFragment extends ActionbarFragment {
    private Tutorial tutorial;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.tutorial == null) {
            this.tutorial = new Tutorial(getActivity());
        }
        return this.tutorial.getView();
    }

    @Override // com.nuance.swype.input.settings.ActionbarFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        String helpUrl = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            helpUrl = arguments.getString(Tutorial.EXTRA_HELP_URL);
        }
        if (helpUrl == null) {
            helpUrl = getActivity().getResources().getString(R.string.fullhelp_file);
        }
        String url = LocalizationUtils.getHtmlFileUrl(helpUrl, getActivity());
        if (!url.equals(this.tutorial.getLoadedUrl())) {
            this.tutorial.load(url);
        }
        super.onActivityCreated(savedInstanceState);
    }
}
