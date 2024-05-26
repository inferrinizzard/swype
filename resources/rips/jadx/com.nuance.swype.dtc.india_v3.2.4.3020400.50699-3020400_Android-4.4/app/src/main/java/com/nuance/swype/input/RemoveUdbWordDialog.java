package com.nuance.swype.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

/* loaded from: classes.dex */
public class RemoveUdbWordDialog extends SwypeDialog {
    private final RemoveUdbWordListener listener;
    private final String word;

    /* loaded from: classes.dex */
    public interface RemoveUdbWordListener {
        void onHandleUdbWordRemoval(String str);
    }

    public RemoveUdbWordDialog(RemoveUdbWordListener listener, String word) {
        this.listener = listener;
        this.word = word;
    }

    @Override // com.nuance.swype.input.SwypeDialog
    protected Dialog onCreateDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme.DeviceDefault.Light.Dialog));
        builder.setTitle(R.string.dialog_udb_delete_title).setMessage(context.getString(R.string.dialog_udb_delete_body, this.word)).setIconAttribute(android.R.attr.alertDialogIcon).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.RemoveUdbWordDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                RemoveUdbWordDialog.this.listener.onHandleUdbWordRemoval(RemoveUdbWordDialog.this.word);
            }
        });
        return builder.create();
    }
}
