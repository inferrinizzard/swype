package com.nuance.swype.startup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class EulaGooglePlayDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("EulaGooglePlayDelegate");
    private final View.OnClickListener closeEulaButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.EulaGooglePlayDelegate.1
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            EulaGooglePlayDelegate.this.removeFlag();
            EulaGooglePlayDelegate.this.mActivityCallbacks.restartSequence(EulaGooglePlayDelegate.this.mFlags, EulaGooglePlayDelegate.this.mResultData);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static EulaGooglePlayDelegate newInstance(Bundle savedInstanceState) {
        EulaGooglePlayDelegate f = new EulaGooglePlayDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, R.layout.startup_template_one_button, R.layout.startup_legaldoc, (String) null);
        WebView web = (WebView) this.view.findViewById(R.id.message);
        String eula = ConnectLegal.from(getActivity()).getEula();
        if (eula != null) {
            web.loadDataWithBaseURL(null, eula, "text/html", "UTF-8", null);
        }
        setupPositiveButton$411327c6(getActivity().getResources().getString(R.string.startup_close), this.closeEulaButtonListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFlag() {
        this.mFlags &= -33;
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        if ((this.mFlags & 32) == 32) {
            removeFlag();
            this.mActivityCallbacks.restartSequence(this.mFlags, this.mResultData);
        } else {
            super.onBackPressed();
        }
    }
}
