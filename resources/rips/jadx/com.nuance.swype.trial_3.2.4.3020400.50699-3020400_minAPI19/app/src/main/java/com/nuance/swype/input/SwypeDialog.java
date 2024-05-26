package com.nuance.swype.input;

import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public abstract class SwypeDialog {
    private Dialog mDialog;

    protected abstract Dialog onCreateDialog(Context context);

    public void createDialog(Context context) {
        this.mDialog = onCreateDialog(context.getApplicationContext());
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public void show(IBinder binder, Context context) {
        if (this.mDialog != null && !this.mDialog.isShowing()) {
            Window window = this.mDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.token = binder;
            lp.type = 1003;
            window.setAttributes(lp);
            window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
            try {
                this.mDialog.show();
            } catch (WindowManager.BadTokenException e) {
                LogManager.getLog(toString()).e("Token exception when showing dialog, dialog will not show");
            }
        }
    }

    public void dismiss() {
        if (isShowing()) {
            this.mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return this.mDialog != null && this.mDialog.isShowing();
    }
}
