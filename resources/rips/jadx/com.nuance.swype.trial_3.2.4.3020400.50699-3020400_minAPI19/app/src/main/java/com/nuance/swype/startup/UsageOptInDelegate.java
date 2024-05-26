package com.nuance.swype.startup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.R;
import com.nuance.swype.startup.StartupDelegate;

/* loaded from: classes.dex */
public class UsageOptInDelegate extends StartupDelegate {
    private Dialog skipRegistrationDialog;
    private final View.OnClickListener declineButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.UsageOptInDelegate.2
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            if (UsageOptInDelegate.this.getShowRegistrationWarning()) {
                UsageOptInDelegate.this.showDialog(UsageOptInDelegate.this.skipRegistrationDialog);
                return;
            }
            if ((UsageOptInDelegate.this.mFlags & 2) == 2) {
                if ((UsageOptInDelegate.this.mFlags & 8) == 8) {
                    UsageOptInDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACTIVITY_RESULT_CANCEL_LISTENER_KEY, UsageOptInDelegate.this.mResultData);
                    UsageOptInDelegate.access$000(UsageOptInDelegate.this);
                    return;
                } else {
                    UsageOptInDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.CANCEL_LISTENER_KEY, UsageOptInDelegate.this.mResultData);
                    UsageOptInDelegate.access$000(UsageOptInDelegate.this);
                    return;
                }
            }
            UsageOptInDelegate.this.mActivityCallbacks.startNextScreen(UsageOptInDelegate.this.mFlags, UsageOptInDelegate.this.mResultData);
            UsageOptInDelegate.access$000(UsageOptInDelegate.this);
        }
    };
    private final View.OnClickListener acceptButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.UsageOptInDelegate.3
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            UsageOptInDelegate.this.enableUsageOption();
            if ((UsageOptInDelegate.this.mFlags & 2) == 2) {
                if ((UsageOptInDelegate.this.mFlags & 8) == 8) {
                    if (!UsageOptInDelegate.this.isNextScreenNull()) {
                        UsageOptInDelegate.this.mActivityCallbacks.startNextScreen(UsageOptInDelegate.this.mFlags, UsageOptInDelegate.this.mResultData);
                        return;
                    } else {
                        UsageOptInDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACTIVITY_RESULT_OK_LISTENER_KEY, UsageOptInDelegate.this.mResultData);
                        UsageOptInDelegate.access$000(UsageOptInDelegate.this);
                        return;
                    }
                }
                UsageOptInDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.ACCEPT_LISTENER_KEY, UsageOptInDelegate.this.mResultData);
                UsageOptInDelegate.access$000(UsageOptInDelegate.this);
                return;
            }
            UsageOptInDelegate.this.mActivityCallbacks.startNextScreen(UsageOptInDelegate.this.mFlags, UsageOptInDelegate.this.mResultData);
            UsageOptInDelegate.access$000(UsageOptInDelegate.this);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UsageOptInDelegate newInstance(Bundle savedInstanceState) {
        UsageOptInDelegate f = new UsageOptInDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.skipRegistrationDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.agree_to_terms).setMessage(R.string.startup_tos_warning).setPositiveButton(R.string.skip_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.UsageOptInDelegate.1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int which) {
                if ((UsageOptInDelegate.this.mFlags & 2) == 2) {
                    UsageOptInDelegate.this.mActivityCallbacks.notifyStartupListener(StartupDelegate.StartupListener.LISTENER_KEY.SKIP_LISTENER_KEY, UsageOptInDelegate.this.mResultData);
                    UsageOptInDelegate.this.mActivityCallbacks.cancelSequence();
                } else {
                    UsageOptInDelegate.this.mActivityCallbacks.startNextScreen(UsageOptInDelegate.this.mFlags, UsageOptInDelegate.this.mResultData);
                }
                UsageOptInDelegate.access$000(UsageOptInDelegate.this);
            }
        }).setNegativeButton(R.string.ok_button, (DialogInterface.OnClickListener) null).create();
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, R.layout.startup_template, R.layout.startup_contribute_usage_data, R.string.usage_statistics_log_title);
        setupNegativeButton$411327c6(getActivity().getResources().getString(R.string.decline_button), this.declineButtonListener);
        setupPositiveButton$411327c6(getActivity().getResources().getString(R.string.accept_button), this.acceptButtonListener);
        ScrollView sv = new ScrollView(getActivity());
        WebView web = new WebView(getActivity());
        String optIn = ConnectLegal.from(getActivity()).getOptIn();
        if (optIn != null) {
            web.loadDataWithBaseURL(null, optIn, "text/html", "UTF-8", null);
        }
        sv.addView(web);
        ViewGroup vg = (ViewGroup) this.view.findViewById(R.id.opt_in);
        if (vg != null) {
            vg.removeAllViews();
            vg.addView(sv);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        this.mActivityCallbacks.cancelSequence();
    }

    static /* synthetic */ void access$000(UsageOptInDelegate x0) {
        x0.mFlags &= -11;
    }
}
