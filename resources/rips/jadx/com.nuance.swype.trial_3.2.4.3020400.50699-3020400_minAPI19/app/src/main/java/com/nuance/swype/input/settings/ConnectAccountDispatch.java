package com.nuance.swype.input.settings;

import android.content.Intent;
import android.os.Bundle;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACAccount;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/* loaded from: classes.dex */
public class ConnectAccountDispatch extends SettingsDispatch {
    public static final String DISPLAY_ACTIVATION_CODE = "DISPLAY_ACTIVATION_CODE";
    public static final String PASS_ACTIVATION_CODE_KEY = "PASS_ACTIVATION_CODE_KEY";
    public static final int PASS_ACTIVATION_CODE_VALUE = 100;
    private static final LogManager.Log log = LogManager.getLog("ConnectAccountDispatch");

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        String dataString;
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        int getValue = 0;
        if (data != null) {
            getValue = data.getInt(PASS_ACTIVATION_CODE_KEY);
        }
        if (!ActivityManagerCompat.isUserAMonkey()) {
            Intent intent = getIntent();
            if (intent != null && (dataString = intent.getDataString()) != null && dataString.length() > 0) {
                processAccountLinkRequest(dataString);
            }
            if (getValue == 100) {
                UserPreferences.from(this).setActivationCodePopupShown(true);
            }
            IMEApplication.from(this).showMyWordsPrefs();
        }
        finish();
    }

    @Override // com.nuance.swype.input.settings.SettingsDispatch, android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        String dataString;
        super.startActivity(intent);
        if (intent != null && (dataString = intent.getDataString()) != null && dataString.length() > 0) {
            processAccountLinkRequest(dataString);
        }
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        String dataString;
        super.startActivity(intent, options);
        if (intent != null && (dataString = intent.getDataString()) != null && dataString.length() > 0) {
            processAccountLinkRequest(dataString);
        }
    }

    protected void processAccountLinkRequest(String link) {
        String email;
        String code;
        if (link != null && link.contains("?") && link.contains("email=") && link.contains("code=")) {
            int positionEmail = link.indexOf("email=");
            int positionCode = link.indexOf("code=");
            if (positionCode > positionEmail) {
                code = link.substring(positionCode + 5);
                String modifiedLink = link.substring(0, positionCode);
                email = modifiedLink.substring(positionEmail + 6, modifiedLink.length() - 1);
            } else {
                email = link.substring(positionEmail + 6);
                String modifiedLink2 = link.substring(0, positionEmail);
                code = modifiedLink2.substring(positionCode + 5, modifiedLink2.length() - 1);
            }
            if (code.length() > 0 && code.contains("&")) {
                code = code.substring(0, code.indexOf("&"));
            }
            if (email.length() > 0 && email.contains("&")) {
                email = email.substring(0, code.indexOf("&"));
            }
            if (code.length() > 0 && email.length() > 0 && code.length() < 10) {
                try {
                    email = URLDecoder.decode(email, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    log.e("UnsupportedEncodingException" + ex.getMessage());
                }
                Connect connect = Connect.from(this);
                ACAccount a = connect.getAccounts().getActiveAccount();
                if (a != null && a.getIdentifier() != null && email.equals(a.getIdentifier())) {
                    connect.getAccounts().verifyAccount(code);
                }
            }
        }
    }
}
