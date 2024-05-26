package com.nuance.swype.input.about;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.settings.ActionbarFragment;

@TargetApi(11)
/* loaded from: classes.dex */
public class AboutDragonFragment extends ActionbarFragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return AboutBuilders.buildAboutDragon(getActivity());
    }
}
