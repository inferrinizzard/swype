package com.nuance.swype.preference;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.view.ContextThemeWrapper;
import com.nuance.swype.input.IMEApplication;

/* loaded from: classes.dex */
public abstract class ConnectionAwarePreferences {
    private final Context context;

    public abstract void showConnectDialog();

    public ConnectionAwarePreferences(Context ctx) {
        this.context = ctx;
    }

    public AlertDialog.Builder getConnectDialog() {
        return getConnectDialog(this.context);
    }

    public AlertDialog.Builder getConnectDialog(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.Theme.DeviceDefault.Light.Dialog.MinWidth));
        builder.setIcon(com.nuance.swype.input.R.drawable.icon_settings_error);
        builder.setTitle(com.nuance.swype.input.R.string.error_connection_title);
        builder.setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null);
        if (IMEApplication.from(ctx).getUserPreferences().connectUseCellularData()) {
            builder.setMessage(com.nuance.swype.input.R.string.error_connection_cellular_failure);
        } else {
            builder.setMessage(com.nuance.swype.input.R.string.error_connection_wifi_failure);
            builder.setNegativeButton(com.nuance.swype.input.R.string.error_connection_enable_data, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.preference.ConnectionAwarePreferences.1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialog, int whichButton) {
                    IMEApplication.from(ConnectionAwarePreferences.this.context).showSettingsPrefs();
                }
            });
        }
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConnectPreferenceClick() {
        showConnectDialog();
    }

    public Preference getConnectPreference() {
        Drawable d = this.context.getResources().getDrawable(com.nuance.swype.input.R.drawable.icon_settings_error);
        Preference pref = IconPreferenceScreen.createPreferenceWithIcon(this.context, d);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.preference.ConnectionAwarePreferences.2
            @Override // android.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                ConnectionAwarePreferences.this.onConnectPreferenceClick();
                return true;
            }
        });
        return pref;
    }
}
