package com.nuance.swype.startup;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ContributeUsageDataDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("ContributeUsageDataDelegate");
    private final View.OnClickListener declineButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.ContributeUsageDataDelegate.1
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            if (ContributeUsageDataDelegate.this.getShowRegistrationWarning()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ContributeUsageDataDelegate.this.getActivity(), R.style.Theme.DeviceDefault.Light.Dialog));
                builder.setTitle(com.nuance.swype.input.R.string.legal_doc_cud_title).setMessage(com.nuance.swype.input.R.string.legal_doc_cud_warning).setNegativeButton(com.nuance.swype.input.R.string.legal_doc_decline, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.ContributeUsageDataDelegate.1.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int which) {
                        if (ContributeUsageDataDelegate.this.mStartupSequenceInfo.wasOptInAccepted()) {
                            ContributeUsageDataDelegate.log.d("declineButtonListener: disabling CUD features");
                            Connect.from(ContributeUsageDataDelegate.this.getActivity()).setContributeUsageData(false);
                        }
                        ConnectLegal.from(ContributeUsageDataDelegate.this.mStartupSequenceInfo.mContext).resetOptInChangedFlag();
                        ContributeUsageDataDelegate.this.mStartupSequenceInfo.setShowCud(false);
                        ContributeUsageDataDelegate.this.mActivityCallbacks.startNextScreen(ContributeUsageDataDelegate.this.mFlags, ContributeUsageDataDelegate.this.mResultData);
                    }
                }).setPositiveButton(com.nuance.swype.input.R.string.cancel_button, (DialogInterface.OnClickListener) null).create();
                builder.show();
            }
        }
    };
    private final View.OnClickListener acceptButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.ContributeUsageDataDelegate.2
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            ContributeUsageDataDelegate.this.enableUsageOption();
            String email = IMEApplication.from(ContributeUsageDataDelegate.this.getActivity()).getAppPreferences().getStartupSequenceAccountEmail();
            Context context = ContributeUsageDataDelegate.this.getActivity();
            Connect connect = Connect.from(context);
            connect.getAccounts().createAccount(email, AccountUtil.isTablet(context), AccountUtil.buildDeviceName(context), true);
            connect.getLivingLanguage(null).enable();
            ContributeUsageDataDelegate.this.mStartupSequenceInfo.setShowCud(false);
            ContributeUsageDataDelegate.this.mActivityCallbacks.startNextScreen(ContributeUsageDataDelegate.this.mFlags, ContributeUsageDataDelegate.this.mResultData);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ContributeUsageDataDelegate newInstance(Bundle savedInstanceState) {
        ContributeUsageDataDelegate f = new ContributeUsageDataDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadTemplateToContentView(inflater, com.nuance.swype.input.R.layout.startup_template, com.nuance.swype.input.R.layout.startup_contribute_usage_data, com.nuance.swype.input.R.string.usage_statistics_log_title);
        setupPositiveButton$411327c6(getActivity().getResources().getString(com.nuance.swype.input.R.string.accept_button), this.acceptButtonListener);
        setupNegativeButton$411327c6(getActivity().getResources().getString(com.nuance.swype.input.R.string.decline_button), this.declineButtonListener);
        ScrollView sv = new ScrollView(getActivity());
        if (this.mStartupSequenceInfo.isOptInChanged()) {
            ((TextView) this.view.findViewById(com.nuance.swype.input.R.id.changed_notice)).setVisibility(0);
        }
        WebView web = new WebView(getActivity());
        String cachedText = Connect.from(getActivity()).getLegal().getOptIn();
        if (cachedText != null) {
            web.loadDataWithBaseURL(null, cachedText, "text/html", "UTF-8", null);
        }
        sv.addView(web);
        ViewGroup vg = (ViewGroup) this.view.findViewById(com.nuance.swype.input.R.id.opt_in);
        if (vg != null) {
            vg.removeAllViews();
            vg.addView(sv);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
