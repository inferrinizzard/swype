package com.nuance.swype.input.about;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.settings.ActionbarFragment;

@TargetApi(11)
/* loaded from: classes.dex */
public class AboutSwypeFragment extends ActionbarFragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return AboutBuilders.buildAboutSwype(getActivity(), new View.OnClickListener() { // from class: com.nuance.swype.input.about.AboutSwypeFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AboutSwypeFragment.this.showOpenSourceAttribution(view.getContext());
            }
        });
    }

    protected void showOpenSourceAttribution(Context context) {
        context.startActivity(new Intent(context, (Class<?>) OpenSourceAttribution.class));
    }
}
