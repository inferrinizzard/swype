package com.nuance.swype.startup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class GettingStartedDelegate extends StartupDelegate {
    private final View.OnClickListener startSwypingButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.GettingStartedDelegate.1
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            GettingStartedDelegate.this.mActivityCallbacks.finishSequence(true);
        }
    };
    private final View.OnClickListener helpButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.GettingStartedDelegate.2
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            GettingStartedDelegate.this.mActivityCallbacks.finishSequence(false);
            IMEApplication.from(GettingStartedDelegate.this.getActivity()).showTutorial();
        }
    };
    private final View.OnClickListener gesturesButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.GettingStartedDelegate.3
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            GettingStartedDelegate.this.mActivityCallbacks.finishSequence(false);
            IMEApplication.from(GettingStartedDelegate.this.getActivity()).showGestures();
        }
    };
    private final View.OnClickListener settingsButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.GettingStartedDelegate.4
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            GettingStartedDelegate.this.mActivityCallbacks.finishSequence(false);
            IMEApplication.from(GettingStartedDelegate.this.getActivity()).showMainSettings();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GettingStartedDelegate newInstance(Bundle savedInstanceState) {
        GettingStartedDelegate f = new GettingStartedDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, R.layout.startup_getting_started, R.string.startup_header_1);
        ((ViewGroup) this.view.findViewById(R.id.gs_help)).setOnClickListener(this.helpButtonListener);
        ((ViewGroup) this.view.findViewById(R.id.gs_gestures)).setOnClickListener(this.gesturesButtonListener);
        ((ViewGroup) this.view.findViewById(R.id.gs_start_swyping)).setOnClickListener(this.startSwypingButtonListener);
        ((ViewGroup) this.view.findViewById(R.id.gs_settings)).setOnClickListener(this.settingsButtonListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onResume() {
        super.onResume();
        StartupSequenceInfo startupSequenceInfo = this.mStartupSequenceInfo;
        StartupSequenceInfo.log.d("clearFlags");
        startupSequenceInfo.setShowBackupSync(false);
        startupSequenceInfo.mShowTos = false;
        startupSequenceInfo.setShowCud(false);
        startupSequenceInfo.setShowGettingStarted(false);
        startupSequenceInfo.setShowSplash(false);
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        this.mActivityCallbacks.cancelSequence();
    }
}
