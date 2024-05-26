package com.nuance.swype.input.chinese;

import android.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.FunctionBarListView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;

/* loaded from: classes.dex */
class DefaultChineseFunctionBarHandler implements FunctionBarListView.OnFunctionBarListener {
    private IME ime;
    private InputView iv;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultChineseFunctionBarHandler(IME ime, InputView iv) {
        this.ime = ime;
        this.iv = iv;
    }

    @Override // com.nuance.swype.input.FunctionBarListView.OnFunctionBarListener
    public void selectFunctionBarFunction(int itemId) {
        if (this.ime != null && IMEApplication.from(this.ime).getIMEHandlerInstance() != null) {
            switch (itemId) {
                case 101:
                    this.iv.handleClose();
                    this.ime.requestHideSelf(0);
                    IMEApplication.from(this.ime).showSettingsPrefs();
                    return;
                case 102:
                case 105:
                case 106:
                case 107:
                case 108:
                default:
                    return;
                case 103:
                    this.ime.showLanguageMenu();
                    return;
                case 104:
                    IMEApplication.from(this.ime).getIMEHandlerInstance().showInputModeMenu();
                    return;
                case 109:
                    if (this.iv.getKeyboardSwitcher().isNumMode()) {
                        this.iv.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                        return;
                    } else {
                        this.iv.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_NUM);
                        return;
                    }
                case 110:
                    if (this.iv.getKeyboardSwitcher().isEditMode()) {
                        this.iv.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                        return;
                    } else {
                        this.iv.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT);
                        return;
                    }
                case 111:
                    this.iv.handleClose();
                    this.ime.requestHideSelf(0);
                    IMEApplication.from(this.ime).showThemes();
                    return;
                case 112:
                    if (this.ime.isValidBuild()) {
                        showAddOnDictionary();
                        return;
                    }
                    return;
                case 113:
                    this.iv.handleClose();
                    this.ime.requestHideSelf(0);
                    IMEApplication.from(this.ime).showChineseSettings();
                    return;
                case 114:
                    switchHWOnKeyboard();
                    return;
                case 115:
                    this.iv.handleClose();
                    this.ime.requestHideSelf(0);
                    return;
                case 116:
                    ((InputMethodManager) this.ime.getSystemService("input_method")).showInputMethodPicker();
                    return;
                case 117:
                    switchHWOnKeyboard();
                    return;
                case 118:
                    this.iv.handleEmotionKey();
                    return;
            }
        }
    }

    private void showAddOnDictionary() {
        if (this.ime.getResources().getBoolean(R.bool.enable_china_connect_special) && UserPreferences.from(this.ime).shouldShowNetworkAgreementDialog()) {
            AlertDialog cndlg = ChinaNetworkNotificationDialog.create(this.ime, new ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener() { // from class: com.nuance.swype.input.chinese.DefaultChineseFunctionBarHandler.1
                @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
                public boolean onPositiveButtonClick() {
                    DefaultChineseFunctionBarHandler.this.launchAddOnDictionary();
                    return false;
                }

                @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
                public boolean onNegativeButtonClick() {
                    return false;
                }
            });
            this.ime.attachDialog(cndlg);
            cndlg.show();
            return;
        }
        launchAddOnDictionary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchAddOnDictionary() {
        this.iv.handleClose();
        this.ime.requestHideSelf(0);
        IMEApplication.from(this.ime).showAddonDictionaries();
    }

    protected void switchHWOnKeyboard() {
    }
}
