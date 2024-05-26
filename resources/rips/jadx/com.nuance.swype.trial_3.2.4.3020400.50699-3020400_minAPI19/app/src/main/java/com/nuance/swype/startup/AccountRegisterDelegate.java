package com.nuance.swype.startup;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.R;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.startup.StartupDelegate;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class AccountRegisterDelegate extends BackupAndSyncDelegate {
    private static final LogManager.Log log = LogManager.getLog("AccountRegisterDelegate");
    private final View.OnClickListener cancelButtonListener = new View.OnClickListener() { // from class: com.nuance.swype.startup.AccountRegisterDelegate.1
        @Override // android.view.View.OnClickListener
        public final void onClick(View arg0) {
            AccountRegisterDelegate.this.mActivityCallbacks.startNextScreen(AccountRegisterDelegate.this.mFlags, AccountRegisterDelegate.this.mResultData);
        }
    };
    private StartupDelegate.StartupListener mStartupListener = new StartupDelegate.StartupListener() { // from class: com.nuance.swype.startup.AccountRegisterDelegate.2
        @Override // com.nuance.swype.startup.StartupDelegate.StartupListener
        public final void onSkipListener() {
            AccountRegisterDelegate.log.d("onSkipListener");
            AccountRegisterDelegate.this.mActivityCallbacks.startNextScreen(AccountRegisterDelegate.this.mFlags, AccountRegisterDelegate.this.mResultData);
        }

        @Override // com.nuance.swype.startup.StartupDelegate.StartupListener
        public final void onCancelListener() {
            AccountRegisterDelegate.log.d("onCancelListener");
            AccountRegisterDelegate.this.mActivityCallbacks.startNextScreen(AccountRegisterDelegate.this.mFlags, AccountRegisterDelegate.this.mResultData);
        }

        @Override // com.nuance.swype.startup.StartupDelegate.StartupListener
        public final void onAcceptListener(Context context) {
            AccountRegisterDelegate.log.d("onAcceptListener");
            AccountRegisterDelegate.this.mStartupSequenceInfo.setHotWordsStatus$1385ff();
            if (AccountRegisterDelegate.this.register(context)) {
                AccountRegisterDelegate.this.mActivityCallbacks.unregisterListener();
                AccountRegisterDelegate.this.mActivityCallbacks.startNextScreen(AccountRegisterDelegate.this.mFlags, AccountRegisterDelegate.this.mResultData);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AccountRegisterDelegate newInstance(Bundle savedInstanceState) {
        AccountRegisterDelegate f = new AccountRegisterDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.BackupAndSyncDelegate
    protected final void setUpNegativeButton() {
        setupNegativeButton$411327c6(getActivity().getResources().getString(R.string.cancel_button), this.cancelButtonListener);
    }

    @Override // com.nuance.swype.startup.BackupAndSyncDelegate
    protected final boolean register(Context context) {
        String email = ((EditText) this.view.findViewById(R.id.emailSelect)).getText().toString();
        if (!Connect.from(context).getAccounts().isValidEmail(email)) {
            showDialog(this.invalidEmailDialog);
            return false;
        }
        if (this.mActivityCallbacks.getCurrentDelegate() == null) {
            return false;
        }
        boolean tosAccepted = this.mStartupSequenceInfo.isTosAccepted();
        boolean optInAccepted = this.mStartupSequenceInfo.isOptInAccepted();
        if (!tosAccepted || !optInAccepted) {
            StartupDelegate startupDelegate = null;
            String desScreen = null;
            if (!tosAccepted) {
                desScreen = ConnectTOSDelegate.class.getSimpleName();
            } else if (!optInAccepted) {
                desScreen = UsageOptInDelegate.class.getSimpleName();
            }
            if (desScreen != null) {
                startupDelegate = createDelegate(desScreen, getThemeID(), 6);
            }
            if (startupDelegate != null) {
                this.mActivityCallbacks.registerListener(this.mStartupListener);
                this.mActivityCallbacks.showDelegate(startupDelegate);
            }
            return false;
        }
        return Connect.from(context).getAccounts().createAccount(email, AccountUtil.isTablet(context), AccountUtil.buildDeviceName(context), true);
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        this.mActivityCallbacks.cancelSequence();
    }
}
