package com.nuance.swype.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.view.inputmethod.EditorInfo;
import com.nuance.swype.startup.StartupActivity;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ShowFirstTimeStartupMessages {
    private static final LogManager.Log log = LogManager.getLog("ShowFirstTimeStartupMessages");
    private final IMEApplication imeApp;
    private SwypeDialog mSwypeDialog;
    private final Whitelist whitelist;

    public ShowFirstTimeStartupMessages(Context context, Whitelist whitelist) {
        log.d("ShowFirstTimeStartupMessages");
        this.imeApp = IMEApplication.from(context);
        this.whitelist = whitelist;
    }

    public boolean canShow(EditorInfo info) {
        return this.whitelist == null || (info != null && this.whitelist.allows(info.packageName));
    }

    public void dismiss() {
        if (this.mSwypeDialog != null) {
            this.mSwypeDialog.dismiss();
            this.mSwypeDialog = null;
        }
    }

    public boolean isDialogShowing() {
        return this.mSwypeDialog != null && this.mSwypeDialog.isShowing();
    }

    public void checkAndShowTrialExpired(IBinder binder) {
        if (this.imeApp.getBuildInfo().isTrialBuild() && this.imeApp.isTrialExpired()) {
            if (this.imeApp.getBuildInfo().isExpireDialogRequired() && !this.imeApp.getBuildInfo().isGooglePlayChina()) {
                showAlertDialogMessage(binder, R.string.trial_period_expiration_msg, R.string.license_buy_now, R.string.label_back, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.ShowFirstTimeStartupMessages.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        int urlId = ShowFirstTimeStartupMessages.this.imeApp.getBuildInfo().getPaidVersionUrl();
                        Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(ShowFirstTimeStartupMessages.this.imeApp.getString(urlId)));
                        marketIntent.addFlags(268435456);
                        ShowFirstTimeStartupMessages.this.imeApp.startActivity(marketIntent);
                    }
                });
            } else {
                showAlertDialogMessage(binder, R.string.trial_period_expiration_msg, android.R.string.ok, 0, null);
            }
        }
    }

    public boolean showStartup(boolean fromSettingsDispatch, boolean needToClearPreviousTask, EditorInfo editorInfo, InputFieldInfo inputFieldInfo) {
        if (this.imeApp.isTrialExpired()) {
            log.d("showStandardStartup - trial expired");
            return false;
        }
        if (!this.imeApp.isUserUnlockFinished()) {
            log.d("showStandardStartup - unlock is still in progress with direct boot mode");
            return false;
        }
        if (!this.imeApp.getStartupSequenceInfo().shouldShowStartup(editorInfo, inputFieldInfo)) {
            return false;
        }
        if (this.imeApp.getIME() != null) {
            this.imeApp.getIME().close();
        }
        Intent i = new Intent(this.imeApp, (Class<?>) StartupActivity.class);
        i.putExtra("launch_to_settings", fromSettingsDispatch);
        i.putExtra("launch_mode", "startup_sequence");
        int flag = 268435456;
        if (needToClearPreviousTask) {
            flag = 268468224;
        }
        i.setFlags(flag);
        this.imeApp.startActivity(i);
        return true;
    }

    private void showAlertDialogMessage(IBinder binder, int resId, int positiveLabelId, int negativeLabelId, DialogInterface.OnClickListener listener) {
        dismiss();
        this.mSwypeDialog = new DialogMessages(resId, positiveLabelId, negativeLabelId, listener);
        this.mSwypeDialog.createDialog(this.imeApp);
        this.mSwypeDialog.show(binder, this.imeApp);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DialogMessages extends SwypeDialog {
        final int negativeLabelId;
        final DialogInterface.OnClickListener onButtonClickListener;
        final int positiveLabelId;
        final int resMsgId;

        public DialogMessages(int resId, int positiveLabelId, int negativeLabelId, DialogInterface.OnClickListener listener) {
            this.resMsgId = resId;
            this.onButtonClickListener = listener;
            this.positiveLabelId = positiveLabelId;
            this.negativeLabelId = negativeLabelId;
        }

        @Override // com.nuance.swype.input.SwypeDialog
        protected Dialog onCreateDialog(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.swype_logo);
            builder.setTitle(context.getResources().getString(R.string.ime_name));
            builder.setPositiveButton(this.positiveLabelId, this.onButtonClickListener);
            if (this.negativeLabelId != 0) {
                builder.setNegativeButton(this.negativeLabelId, (DialogInterface.OnClickListener) null);
            }
            builder.setMessage(this.resMsgId);
            return builder.create();
        }
    }

    public void onInstallMessage(IBinder binder) {
        if (this.imeApp.getBuildInfo().isTrialBuild() && this.imeApp.getIME().isTrialBuildFirstMessage()) {
            showAlertDialogMessage(binder, R.string.trial_welcome_msg, R.string.license_buy_now, R.string.dismiss_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.ShowFirstTimeStartupMessages.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    int urlId = ShowFirstTimeStartupMessages.this.imeApp.getBuildInfo().getPaidVersionUrl();
                    Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(ShowFirstTimeStartupMessages.this.imeApp.getString(urlId)));
                    marketIntent.addFlags(268435456);
                    ShowFirstTimeStartupMessages.this.imeApp.startActivity(marketIntent);
                }
            });
        }
    }
}
