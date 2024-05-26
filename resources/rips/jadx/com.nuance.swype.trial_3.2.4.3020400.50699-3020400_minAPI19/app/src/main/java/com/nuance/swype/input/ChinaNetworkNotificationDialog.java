package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/* loaded from: classes.dex */
public final class ChinaNetworkNotificationDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private static final int DIALOG_INTERVAL = 500;
    private boolean isShowFromKeyboard;
    private boolean mHasBeenShown;
    private ChinaNetworkNotificationDialogListener mListener;
    private static CheckBox mCheckBox = null;
    private static long lastBeShownTimestamp = 0;
    private static final Object lock = new Object();

    /* loaded from: classes.dex */
    public interface ChinaNetworkNotificationDialogListener {
        boolean onNegativeButtonClick();

        boolean onPositiveButtonClick();
    }

    protected ChinaNetworkNotificationDialog(Context context) {
        super(context);
        this.mHasBeenShown = false;
        this.isShowFromKeyboard = false;
    }

    @SuppressLint({"InflateParams"})
    public static final AlertDialog create(Context ctx, ChinaNetworkNotificationDialogListener listener) {
        Context wrapperCtx = new ContextThemeWrapper(ctx, android.R.style.Theme.DeviceDefault.Light.Dialog.MinWidth);
        ChinaNetworkNotificationDialog chinaNetworkNotificationDialog = new ChinaNetworkNotificationDialog(wrapperCtx);
        chinaNetworkNotificationDialog.mListener = listener;
        AppPreferences appPrefs = AppPreferences.from(ctx);
        UserPreferences prefs = UserPreferences.from(ctx);
        chinaNetworkNotificationDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.ChinaNetworkNotificationDialog.1
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 5 && event.getAction() == 1) {
                    dialog.dismiss();
                    return false;
                }
                return false;
            }
        });
        chinaNetworkNotificationDialog.setIcon(R.drawable.swype_logo);
        chinaNetworkNotificationDialog.setTitle(R.string.network_notification_title);
        chinaNetworkNotificationDialog.setMessage(ctx.getResources().getString(R.string.network_notification_message));
        chinaNetworkNotificationDialog.setButton(-2, ctx.getResources().getString(R.string.cancel_button), chinaNetworkNotificationDialog);
        chinaNetworkNotificationDialog.setButton(-1, ctx.getResources().getString(R.string.continue_button), chinaNetworkNotificationDialog);
        View view = ((LayoutInflater) wrapperCtx.getSystemService("layout_inflater")).inflate(R.layout.china_network_notification_dialog, (ViewGroup) null);
        boolean isChecked = true;
        if (mCheckBox != null) {
            isChecked = mCheckBox.isChecked();
        } else if (!appPrefs.getOnInstallFirstMessage()) {
            isChecked = !prefs.getShowNetworkDialogFromKeyboard();
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.not_ask_check_box);
        mCheckBox = checkBox;
        checkBox.setChecked(isChecked);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.nuance.swype.input.ChinaNetworkNotificationDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked2) {
                UserPreferences.from(buttonView.getContext()).setNetworkAgreementNotAsk(isChecked2);
            }
        });
        chinaNetworkNotificationDialog.setView(view);
        chinaNetworkNotificationDialog.setCanceledOnTouchOutside(false);
        return chinaNetworkNotificationDialog;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialog, int which) {
        if (which == -2) {
            UserPreferences prefs = UserPreferences.from(((AlertDialog) dialog).getContext());
            prefs.setNetworkAgreement(false);
            prefs.setNetworkAgreementNotAsk(false);
            if (mCheckBox != null && this.isShowFromKeyboard) {
                prefs.setShowNetworkDialogFromKeyboard(!mCheckBox.isChecked());
            }
            if (this.mListener != null) {
                this.mListener.onNegativeButtonClick();
            }
            IMEApplication.from(((AlertDialog) dialog).getContext()).getConnect().stop();
            return;
        }
        if (which == -1) {
            UserPreferences prefs2 = UserPreferences.from(((AlertDialog) dialog).getContext());
            if (!this.isShowFromKeyboard || (mCheckBox != null && mCheckBox.isChecked())) {
                prefs2.setNetworkAgreement(true);
            }
            if (mCheckBox != null) {
                prefs2.setNetworkAgreementNotAsk(mCheckBox.isChecked());
                if (this.isShowFromKeyboard || mCheckBox.isChecked()) {
                    prefs2.setShowNetworkDialogFromKeyboard(mCheckBox.isChecked() ? false : true);
                }
            }
            if (!this.isShowFromKeyboard || (mCheckBox != null && mCheckBox.isChecked())) {
                IMEApplication.from(((AlertDialog) dialog).getContext()).getConnect().doDelayedStart();
            }
            if (this.mListener != null) {
                this.mListener.onPositiveButtonClick();
            }
        }
    }

    @Override // android.app.Dialog
    public final void show() {
        long currentTimestamp = SystemClock.uptimeMillis();
        if (lastBeShownTimestamp == 0 || currentTimestamp - lastBeShownTimestamp >= 500 || !this.isShowFromKeyboard) {
            synchronized (lock) {
                this.mHasBeenShown = true;
            }
            super.show();
        }
    }

    @Override // android.app.Dialog
    protected final void onStop() {
        UserPreferences prefs = UserPreferences.from(getContext());
        if (!prefs.getNetworkAgreement()) {
            prefs.setNetworkAgreementNotAsk(false);
        }
        this.isShowFromKeyboard = false;
        this.mHasBeenShown = false;
        lastBeShownTimestamp = SystemClock.uptimeMillis();
        super.onStop();
    }

    public final boolean hasBeenShown() {
        return this.mHasBeenShown;
    }

    public final void resetStatics() {
        this.isShowFromKeyboard = false;
        this.mHasBeenShown = false;
    }

    public final void setShowFromKeyboard(boolean isFromKeyboard) {
        this.isShowFromKeyboard = isFromKeyboard;
    }

    @Override // android.app.Dialog
    public final Bundle onSaveInstanceState() {
        this.mHasBeenShown = false;
        return super.onSaveInstanceState();
    }
}
