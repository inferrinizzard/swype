package com.nuance.swype.startup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class LegalDocsSplashDelegate extends StartupDelegate {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static LegalDocsSplashDelegate newInstance(Bundle savedInstanceState) {
        LegalDocsSplashDelegate f = new LegalDocsSplashDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, R.layout.startup_template_splash, R.layout.startup_legaldocs_splash, (String) null);
        ImageView acceptArrow = (ImageView) this.view.findViewById(R.id.accept_arrow);
        TextView docsChanged = (TextView) this.view.findViewById(R.id.docs_changed);
        TextView docsAcceptance = (TextView) this.view.findViewById(R.id.docs_acceptance);
        TextView eulaLink = (TextView) this.view.findViewById(R.id.doc_bullet_eula);
        TextView tosLink = (TextView) this.view.findViewById(R.id.doc_bullet_tos);
        acceptArrow.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.startup.LegalDocsSplashDelegate.1
            @Override // android.view.View.OnClickListener
            public final void onClick(View arg0) {
                if (LegalDocsSplashDelegate.this.showEulaLink()) {
                    StartupSequenceInfo startupSequenceInfo = LegalDocsSplashDelegate.this.mStartupSequenceInfo;
                    StartupSequenceInfo.log.d("acceptEula");
                    ConnectLegal.from(startupSequenceInfo.mContext).acceptEula();
                }
                if (LegalDocsSplashDelegate.this.showTosLink()) {
                    LegalDocsSplashDelegate.this.mStartupSequenceInfo.acceptTos();
                }
                LegalDocsSplashDelegate.this.mStartupSequenceInfo.setShowSplash(false);
                LegalDocsSplashDelegate.this.mActivityCallbacks.startNextScreen(LegalDocsSplashDelegate.this.mFlags, LegalDocsSplashDelegate.this.mResultData);
            }
        });
        if (showEulaLink() || showTosLink()) {
            docsAcceptance.setVisibility(0);
            if (showChangedNotice()) {
                docsChanged.setVisibility(0);
            }
            if (showEulaLink()) {
                eulaLink.setVisibility(0);
                eulaLink.setPaintFlags(eulaLink.getPaintFlags() | 8);
                eulaLink.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.startup.LegalDocsSplashDelegate.2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View v) {
                        StartupDelegate startupDelegate = StartupDelegate.createDelegate(EulaGooglePlayDelegate.class.getSimpleName(), LegalDocsSplashDelegate.this.getThemeID(), 32);
                        if (startupDelegate != null) {
                            LegalDocsSplashDelegate.this.mActivityCallbacks.showDelegate(startupDelegate);
                        }
                    }
                });
            }
            if (showTosLink()) {
                tosLink.setVisibility(0);
                tosLink.setPaintFlags(tosLink.getPaintFlags() | 8);
                tosLink.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.startup.LegalDocsSplashDelegate.3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View v) {
                        StartupDelegate startupDelegate = StartupDelegate.createDelegate(ConnectTOSDelegate.class.getSimpleName(), LegalDocsSplashDelegate.this.getThemeID(), 38);
                        if (startupDelegate != null) {
                            LegalDocsSplashDelegate.this.mActivityCallbacks.showDelegate(startupDelegate);
                        }
                    }
                });
            }
        }
        this.mActivityCallbacks.showKeyboardOnFinish$1385ff();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected boolean showEulaLink() {
        return !this.mStartupSequenceInfo.isEulaAccepted();
    }

    protected boolean showTosLink() {
        return !this.mStartupSequenceInfo.isTosAccepted();
    }

    protected boolean showChangedNotice() {
        return ConnectLegal.from(this.mStartupSequenceInfo.mContext).isEulaChanged() || this.mStartupSequenceInfo.isTosChanged();
    }
}
