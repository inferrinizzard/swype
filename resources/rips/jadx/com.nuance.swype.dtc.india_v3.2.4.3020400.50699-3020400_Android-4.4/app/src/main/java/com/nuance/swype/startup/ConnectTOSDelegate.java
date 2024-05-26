package com.nuance.swype.startup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.R;
import com.nuance.swype.startup.StartupDelegate;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ConnectTOSDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("ConnectTOSDelegate");
    private Dialog skipRegistrationDialog;
    private final View.OnClickListener declineButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.ConnectTOSDelegate.2
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            if (ConnectTOSDelegate.this.getShowRegistrationWarning()) {
                ConnectTOSDelegate.this.showDialog(ConnectTOSDelegate.this.skipRegistrationDialog);
                return;
            }
            if ((ConnectTOSDelegate.this.mFlags & 2) == 2) {
                if ((ConnectTOSDelegate.this.mFlags & 8) == 8) {
                    ConnectTOSDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACTIVITY_RESULT_CANCEL_LISTENER_KEY, ConnectTOSDelegate.this.mResultData);
                    ConnectTOSDelegate.this.removeFlag();
                    return;
                } else {
                    ConnectTOSDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.CANCEL_LISTENER_KEY, ConnectTOSDelegate.this.mResultData);
                    ConnectTOSDelegate.this.removeFlag();
                    return;
                }
            }
            ConnectTOSDelegate.this.mActivityCallbacks.startNextScreen(ConnectTOSDelegate.this.mFlags, ConnectTOSDelegate.this.mResultData);
            ConnectTOSDelegate.this.removeFlag();
        }
    };
    private final View.OnClickListener acceptTosButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.ConnectTOSDelegate.3
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            ConnectTOSDelegate.this.mStartupSequenceInfo.acceptTos();
            ConnectTOSDelegate.this.mStartupSequenceInfo.mShowTos = false;
            if ((ConnectTOSDelegate.this.mFlags & 2) == 2) {
                if ((ConnectTOSDelegate.this.mFlags & 8) == 8) {
                    if (!ConnectTOSDelegate.this.isNextScreenNull()) {
                        ConnectTOSDelegate.this.mActivityCallbacks.startNextScreen(ConnectTOSDelegate.this.mFlags, ConnectTOSDelegate.this.mResultData);
                        return;
                    } else {
                        ConnectTOSDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACTIVITY_RESULT_OK_LISTENER_KEY, ConnectTOSDelegate.this.mResultData);
                        ConnectTOSDelegate.this.removeFlag();
                        return;
                    }
                }
                ConnectTOSDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACCEPT_LISTENER_KEY, ConnectTOSDelegate.this.mResultData);
                ConnectTOSDelegate.this.removeFlag();
                return;
            }
            ConnectTOSDelegate.this.mActivityCallbacks.startNextScreen(ConnectTOSDelegate.this.mFlags, ConnectTOSDelegate.this.mResultData);
            ConnectTOSDelegate.this.removeFlag();
        }
    };
    private final View.OnClickListener closeTosButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.ConnectTOSDelegate.4
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            ConnectTOSDelegate.this.removeFlag();
            ConnectTOSDelegate.this.mActivityCallbacks.restartSequence(ConnectTOSDelegate.this.mFlags, ConnectTOSDelegate.this.mResultData);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConnectTOSDelegate newInstance(Bundle savedInstanceState) {
        ConnectTOSDelegate f = new ConnectTOSDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.skipRegistrationDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.agree_to_terms).setMessage(R.string.startup_tos_warning).setPositiveButton(R.string.skip_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.ConnectTOSDelegate.1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int which) {
                ConnectTOSDelegate.this.mStartupSequenceInfo.mShowTos = false;
                ConnectTOSDelegate.this.mActivityCallbacks.startNextScreen(ConnectTOSDelegate.this.mFlags, ConnectTOSDelegate.this.mResultData);
            }
        }).setNegativeButton(R.string.ok_button, (DialogInterface.OnClickListener) null).create();
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((this.mFlags & 32) == 32) {
            loadTemplateToContentView(inflater, R.layout.startup_template_one_button, R.layout.startup_legaldoc, R.string.terms_of_service_connect_title);
            setupPositiveButton$411327c6(getActivity().getResources().getString(R.string.startup_close), this.closeTosButtonListener);
        } else {
            loadTemplateToContentView(inflater, R.layout.startup_template, R.layout.startup_legaldoc, R.string.terms_of_service_connect_title);
            setupPositiveButton$411327c6(getActivity().getResources().getString(R.string.accept_button), this.acceptTosButtonListener);
            setupNegativeButton$411327c6(getActivity().getResources().getString(R.string.decline_button), this.declineButtonListener);
        }
        WebView web = (WebView) this.view.findViewById(R.id.message);
        String tos = ConnectLegal.from(getActivity()).getTos();
        if (tos != null) {
            web.loadDataWithBaseURL(null, tos, "text/html", "UTF-8", null);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFlag() {
        this.mFlags &= -43;
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        if ((this.mFlags & 32) == 32) {
            removeFlag();
            this.mActivityCallbacks.restartSequence(this.mFlags, this.mResultData);
        } else {
            this.mActivityCallbacks.cancelSequence();
        }
    }
}
