package com.nuance.swype.input;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.chinese.ActionOnKeyboardListAdapter;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public abstract class IMEHandler {
    protected static final int ARROW_KEYS_ACCELERATE_AT = 20;
    protected static final int DELETE_ACCELERATE_AT = 20;
    public static final int DLM_TEXT_SCAN_ACTION_DELAY_REORDER = 0;
    protected static final int POS_METHOD = 1;
    protected static final int POS_SETTINGS = 0;
    protected static final int QUICK_PRESS = 750;
    public static final LogManager.Trace trace = LogManager.getTrace();
    public boolean initializeCoreNeeded = true;
    public IME mIme;

    public abstract void cyclingKeyboardInput();

    public abstract void handleXT9LanguageCyclingKey();

    public abstract void onCreate();

    public abstract View onCreateCandidatesView();

    public abstract View onCreateInputView();

    public abstract void onDestroy();

    public abstract void onFinishCandidatesView(boolean z);

    public abstract void onFinishInput();

    public abstract void onFinishInputView(boolean z);

    public abstract void onStartInput(EditorInfo editorInfo, boolean z);

    public abstract void toggleFullScreenHwr();

    public void onStartInputView(EditorInfo attribute, boolean restarting) {
    }

    public void onInitializeInterface() {
    }

    public void handleNewWordsDelayScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
    }

    public void scan(NewWordsBucketFactory.NewWordsBucket bucket, int maxItemToScan) {
    }

    public void initializeCore() {
    }

    public XT9Status checkCompatability() {
        return XT9Status.ET9STATUS_NONE;
    }

    public int getMaxItemToScan(NewWordsBucketFactory.NewWordsBucket bucket) {
        return 1;
    }

    public void switchToHandwritingModeCJK() {
        InputMethods.InputMode inputMode;
        if (this.mIme.mCurrentInputLanguage != null && (inputMode = this.mIme.mCurrentInputLanguage.switchToHandwritingModeCJK()) != null) {
            if (!inputMode.isHandwriting()) {
                this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).finishInput();
            }
            this.mIme.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
            this.mIme.switchInputViewAsync();
        }
    }

    public void handleXT9LanguageCyclingKeyForCJK() {
    }

    public boolean isEditNumorSymMode() {
        InputView inputView;
        KeyboardSwitcher keyboardSwitcher;
        if (this.mIme == null || (inputView = this.mIme.getCurrentInputView()) == null || (keyboardSwitcher = inputView.getKeyboardSwitcher()) == null) {
            return false;
        }
        return keyboardSwitcher.isEditMode() || keyboardSwitcher.isNumMode() || keyboardSwitcher.isSymbolMode();
    }

    public void showInputModeMenu() {
    }

    public void showActionOnKeyboardOption() {
        if (this.mIme.mOptionsDialog == null || !this.mIme.mOptionsDialog.isShowing()) {
            final ActionOnKeyboardListAdapter listAdapter = new ActionOnKeyboardListAdapter(this.mIme.getApplicationContext(), this.mIme.mCurrentInputLanguage.getCurrentInputMode());
            if (listAdapter.mActionOnKeyboard.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mIme.getApplicationContext());
                builder.setCancelable(true);
                builder.setIcon(R.drawable.swype_logo);
                builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.IMEHandler.1
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 5 && event.getAction() == 1) {
                            dialog.dismiss();
                            return false;
                        }
                        return false;
                    }
                });
                builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IMEHandler.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface di, int position) {
                        di.dismiss();
                        String str = listAdapter.getItem(position).actionName;
                        InputMethods.InputMode im = IMEHandler.this.mIme.mCurrentInputLanguage.getCurrentInputMode();
                        AppPreferences appPrefs = AppPreferences.from(IMEHandler.this.mIme);
                        String cvOff = IMEHandler.this.mIme.getResources().getString(R.string.action_on_keyboard_off);
                        String cvHwr = IMEHandler.this.mIme.getResources().getString(R.string.handwriting_on_chinese_keyboard);
                        String cvTrace = IMEHandler.this.mIme.getResources().getString(R.string.trace_on_chinese_keyboard);
                        if (cvOff.equals(str)) {
                            appPrefs.setBoolean(im.getHandwritingOnKeyboardKey(), false);
                            appPrefs.setBoolean(im.getTraceOnKeyboardKey(), false);
                        } else if (cvHwr.equals(str)) {
                            appPrefs.setBoolean(im.getHandwritingOnKeyboardKey(), true);
                            appPrefs.setBoolean(im.getTraceOnKeyboardKey(), false);
                        } else if (cvTrace.equals(str)) {
                            appPrefs.setBoolean(im.getHandwritingOnKeyboardKey(), false);
                            appPrefs.setBoolean(im.getTraceOnKeyboardKey(), true);
                        }
                        IMEHandler.this.mIme.keyboardInputInflater.getInputView(IMEHandler.this.mIme.mCurrentInputViewName).finishInput();
                        IMEHandler.this.mIme.keyboardInputInflater.getInputView(IMEHandler.this.mIme.mCurrentInputViewName).startInput(IMEHandler.this.mIme.mInputFieldInfo, false);
                    }
                });
                builder.setTitle(this.mIme.getResources().getString(R.string.handwriting_trace_toggle));
                this.mIme.mOptionsDialog = builder.create();
                Window window = this.mIme.mOptionsDialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.token = this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).getWindowToken();
                lp.type = 1003;
                window.setAttributes(lp);
                window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
                this.mIme.mOptionsDialog.show();
            }
        }
    }

    public boolean onKey(Point point, int primaryCode, int[] keyCodes) {
        InputView inputView = this.mIme.getCurrentInputView();
        if (inputView == null) {
            return false;
        }
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH /* 2939 */:
                inputView.handleKey(KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH, false, 1);
                break;
            case KeyboardEx.KEYCODE_ACTIONONKEYBOARD_MENU /* 6446 */:
                showActionOnKeyboardOption();
                break;
            case KeyboardEx.KEYCODE_INPUTMODE_MENU /* 6447 */:
                if (this.mIme.mCurrentInputLanguage.isCJK()) {
                    inputView.flushCurrentActiveWord();
                }
                showInputModeMenu();
                break;
            case KeyboardEx.KEYCODE_SWITCH_WRITE_SCREEN /* 43579 */:
                this.mIme.getHandler().removeMessages(107);
                this.mIme.getHandler().sendEmptyMessageDelayed(107, 5L);
                break;
            default:
                if (this.mIme.mCurrentInputLanguage.isCJK()) {
                    return false;
                }
                break;
        }
        return true;
    }
}
