package com.google.android.voiceime;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.voiceime.ServiceBridge;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ActivityHelper extends Activity {
    private ServiceBridge mServiceBridge;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        String languageLocale;
        super.onCreate(bundle);
        this.mServiceBridge = new ServiceBridge();
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("calling_package", getClass().getPackage().getName());
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.MAX_RESULTS", 5);
        if (bundle != null && (languageLocale = bundle.getString("android.speech.extra.LANGUAGE")) != null) {
            intent.putExtra("android.speech.extra.LANGUAGE", languageLocale);
        }
        startActivityForResult(intent, 1);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null && data.hasExtra("android.speech.extra.RESULTS")) {
            ArrayList<String> results = data.getStringArrayListExtra("android.speech.extra.RESULTS");
            final String[] strArr = (String[]) results.toArray(new String[results.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme.Holo.Dialog.NoActionBar);
            builder.setItems(strArr, new DialogInterface.OnClickListener() { // from class: com.google.android.voiceime.ActivityHelper.1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialog, int which) {
                    ActivityHelper.this.notifyResult(strArr[which]);
                }
            });
            builder.setCancelable(true);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.google.android.voiceime.ActivityHelper.2
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialog) {
                    ActivityHelper.this.notifyResult(null);
                }
            });
            builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.google.android.voiceime.ActivityHelper.3
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialog, int which) {
                    ActivityHelper.this.notifyResult(null);
                }
            });
            builder.create().show();
            return;
        }
        notifyResult(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyResult(String result) {
        bindService(new Intent(this, (Class<?>) ServiceHelper.class), new ServiceBridge.ConnectionResponse(this.mServiceBridge, this, result, (byte) 0), 1);
        finish();
    }
}
