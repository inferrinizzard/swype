package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.IMEHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardInputInflater;
import com.nuance.swype.input.R;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ChineseIMEHandler extends IMEHandler {
    protected static final LogManager.Log log = LogManager.getLog("ChineseIMEHandler");
    private XT9CoreChineseInput chineseInput;

    public ChineseIMEHandler(IME ime) {
        this.mIme = ime;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onDestroy() {
        this.mIme.removeAllPendingMsgs();
        destroyAllInputs(true);
        try {
            this.mIme.unregisterReceiver(this.mIme.mReceiver);
        } catch (IllegalArgumentException ex) {
            log.e(ex.getMessage());
        }
        this.mIme.mInputMethods = null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public View onCreateInputView() {
        if (this.mIme.mInputFieldInfo == null) {
            this.mIme.mInputFieldInfo = new InputFieldInfo(this.mIme);
        }
        destroyAllInputs(false);
        return null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishInput() {
        if (this.mIme != null && this.mIme.keyboardInputInflater != null && !this.mIme.keyboardInputInflater.isEmpty()) {
            this.mIme.getHandler().removeMessages(100);
            InputView inputView = this.mIme.getCurrentInputView();
            if (inputView != null) {
                inputView.finishInput();
                inputView.setOnKeyboardActionListener((IME) null);
            }
        }
    }

    public void destroyAllInputs(boolean destroyCore) {
        finishAllInputView();
        this.mIme.resetInputView(destroyCore);
        if (this.mIme.mOptionsDialog != null && this.mIme.mOptionsDialog.isShowing()) {
            this.mIme.mOptionsDialog.dismiss();
            this.mIme.mOptionsDialog = null;
        }
        this.mIme.mCurrentInputViewName = KeyboardInputInflater.NO_INPUTVIEW;
    }

    public void finishAllInputView() {
        this.mIme.keyboardInputInflater.callAllInputViewToFinish();
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void cyclingKeyboardInput() {
        InputMethods.InputMode currentInputMode = this.mIme.mCurrentInputLanguage.getCurrentInputMode();
        if (!currentInputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE) && !currentInputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)) {
            this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).flushCurrentActiveWord();
            InputMethods im = InputMethods.from(this.mIme);
            if (currentInputMode.mDefaultLayoutId == 2304) {
                im.setChineseCyclingKeyboardInputMode(InputMethods.KEYPAD_KEYBOARD_LAYOUT, currentInputMode.mInputMode, this.mIme.mCurrentInputLanguage);
            } else {
                im.setChineseCyclingKeyboardInputMode(InputMethods.QWERTY_KEYBOARD_LAYOUT, currentInputMode.mInputMode, this.mIme.mCurrentInputLanguage);
            }
            this.mIme.getHandler().removeMessages(100);
            this.mIme.getHandler().sendMessageDelayed(this.mIme.getHandler().obtainMessage(100), 5L);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    @SuppressLint({"PrivateResource"})
    public void showInputModeMenu() {
        if (!ActivityManagerCompat.isUserAMonkey() && !this.mIme.mInputFieldInfo.isPhoneNumberField()) {
            if (this.mIme.mOptionsDialog == null || !this.mIme.mOptionsDialog.isShowing()) {
                final InputModeListAdapter modeAdapter = new InputModeListAdapter(this.mIme.getApplicationContext(), this.mIme.mCurrentInputLanguage);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mIme.getApplicationContext());
                builder.setCancelable(true);
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.chinese.ChineseIMEHandler.1
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 5 && event.getAction() == 1) {
                            dialog.dismiss();
                            return false;
                        }
                        return false;
                    }
                });
                builder.setIcon(R.drawable.swype_logo);
                builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
                builder.setAdapter(modeAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.chinese.ChineseIMEHandler.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface di, int position) {
                        di.dismiss();
                        InputMethods.InputMode inputMode = (InputMethods.InputMode) modeAdapter.getItem(position);
                        if (inputMode != null && ChineseIMEHandler.this.mIme != null && ChineseIMEHandler.this.mIme.mCurrentInputLanguage != null && inputMode != ChineseIMEHandler.this.mIme.mCurrentInputLanguage.getCurrentInputMode()) {
                            if (ChineseIMEHandler.this.mIme.keyboardInputInflater != null && ChineseIMEHandler.this.mIme.keyboardInputInflater.getInputView(ChineseIMEHandler.this.mIme.mCurrentInputViewName) != null) {
                                ChineseIMEHandler.this.mIme.keyboardInputInflater.getInputView(ChineseIMEHandler.this.mIme.mCurrentInputViewName).finishInput();
                            }
                            ChineseIMEHandler.this.mIme.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
                            AppPreferences appPrefs = AppPreferences.from(ChineseIMEHandler.this.mIme);
                            if (appPrefs != null && InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN.equals(inputMode.mInputMode)) {
                                appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + ChineseIMEHandler.this.mIme.mCurrentInputLanguage.getCoreLanguageId(), true);
                            } else if (appPrefs != null && InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN.equals(inputMode.mInputMode)) {
                                appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + ChineseIMEHandler.this.mIme.mCurrentInputLanguage.getCoreLanguageId(), false);
                            }
                            inputMode.saveAsCurrent();
                            if (ChineseIMEHandler.this.mIme.getHandler() != null) {
                                ChineseIMEHandler.this.mIme.getHandler().removeMessages(101);
                                ChineseIMEHandler.this.mIme.getHandler().sendEmptyMessageDelayed(101, 5L);
                            }
                            ChineseIMEHandler.this.mIme.mWantToast = true;
                        }
                    }
                });
                builder.setTitle(this.mIme.getResources().getString(R.string.input_method));
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

    @Override // com.nuance.swype.input.IMEHandler
    public void toggleFullScreenHwr() {
        InputView inputView = this.mIme.getCurrentInputView();
        if (inputView != null) {
            inputView.finishInput();
            inputView.handleClose();
            InputMethods.InputMode inputMode = this.mIme.mCurrentInputLanguage.getCurrentInputMode();
            AppPreferences appPrefs = AppPreferences.from(this.mIme);
            if (InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN.compareTo(inputMode.mInputMode) == 0) {
                appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + this.mIme.mCurrentInputLanguage.getCoreLanguageId(), false);
                inputMode = this.mIme.mCurrentInputLanguage.getInputMode(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN);
            } else if (InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN.compareTo(inputMode.mInputMode) == 0) {
                appPrefs.setBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + this.mIme.mCurrentInputLanguage.getCoreLanguageId(), true);
                inputMode = this.mIme.mCurrentInputLanguage.getInputMode(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN);
            }
            inputMode.saveAsCurrent();
            this.mIme.getHandler().removeMessages(100);
            this.mIme.getHandler().sendMessageDelayed(this.mIme.getHandler().obtainMessage(100), 10L);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void handleNewWordsDelayScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
        if (!bucket.isBigramDlm()) {
            if (!this.mIme.isImeInUse()) {
                if (!bucket.isEmpty()) {
                    if (this.initializeCoreNeeded) {
                        initializeCore();
                        this.initializeCoreNeeded = false;
                    }
                    if (this.chineseInput != null) {
                        scan(bucket, getMaxItemToScan(bucket));
                        if (!bucket.isEmpty()) {
                            log.d("Chinese handleNewWordsDelayScanning() - schedule for next batch");
                            this.mIme.sendDelayNewWordsScanning(bucket, IME.NEXT_SCAN_IN_MILLIS);
                            return;
                        } else {
                            log.d("Chinese handleNewWordsDelayScanning() - done");
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            log.d("Chinese handleNewWordsDelayScanning() -- retry delay");
            this.mIme.sendDelayNewWordsScanning(bucket, IME.RETRY_DELAY_IN_MILLIS);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void scan(NewWordsBucketFactory.NewWordsBucket bucket, int maxItemToScan) {
        log.d("Chinese scan() itemsToScan = ", Integer.valueOf(maxItemToScan));
        int itemScanned = 0;
        while (itemScanned < maxItemToScan) {
            String words = bucket.remove();
            if (words == null) {
                break;
            }
            itemScanned++;
            if (bucket.scanActionCount == 1) {
                insertWordToUdb(words);
            }
        }
        log.d("Chinese scan() itemScanned = ", Integer.valueOf(itemScanned));
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void initializeCore() {
        log.d("Chinese initializeCore()");
        InputMethods im = this.mIme.getInputMethods();
        if (im != null && im.getCurrentInputLanguage().isChineseLanguage()) {
            this.chineseInput = IMEApplication.from(this.mIme).getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
            this.chineseInput.startSession();
            im.getCurrentInputLanguage().setLanguage(this.chineseInput);
            this.chineseInput.setInputMode(0);
            return;
        }
        this.chineseInput = null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public XT9Status checkCompatability() {
        log.d("Chinese checkCompatability()");
        XT9Status status = XT9Status.ET9STATUS_NONE;
        InputMethods im = this.mIme.getInputMethods();
        if (im != null && im.getCurrentInputLanguage().isChineseLanguage()) {
            this.chineseInput = IMEApplication.from(this.mIme).getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
            this.chineseInput.startSession();
            XT9Status status2 = im.getCurrentInputLanguage().setLanguage(this.chineseInput);
            return status2;
        }
        this.chineseInput = null;
        return status;
    }

    private void insertWordToUdb(String word) {
        int ret;
        StringBuilder spell = new StringBuilder();
        for (int i = word.length() - 1; i >= 0; i--) {
            StringBuffer tmpSpell = new StringBuffer();
            int CnChar = word.codePointAt(i);
            if (i == 0) {
                int j = 0;
                while (true) {
                    ret = this.chineseInput.getCharSpell(CnChar, j, 0, tmpSpell);
                    if (ret != 0) {
                        break;
                    }
                    if (tmpSpell.toString().trim().length() <= 0) {
                        log.e("Failed to add words, get empty char spell: " + word + "ret: " + ret);
                        break;
                    }
                    String FullName = tmpSpell.toString().trim().concat(spell.toString().trim());
                    if (j > 0) {
                        log.d("Add words multiple pronounciation word: ", word, "Spell:", FullName);
                    }
                    if (this.chineseInput.dlmAdd(word, FullName)) {
                        log.d("Add words: ", word, "Spell:", FullName);
                    } else {
                        log.e("Failed to add words: " + word + "Spell:" + FullName);
                    }
                    j++;
                    tmpSpell.delete(0, tmpSpell.length());
                }
                if (j == 0 && ret != 0) {
                    log.e("Failed to add words, cannot get char spell: " + word + "ret: " + ret);
                }
            } else {
                int ret2 = this.chineseInput.getCharSpell(CnChar, 0, 0, tmpSpell);
                if (ret2 == 0 && tmpSpell.toString().trim().length() > 0) {
                    spell.insert(0, tmpSpell.toString().trim());
                } else {
                    log.e("Failed to add words, cannot get char spell: " + word + "ret: " + ret2);
                    return;
                }
            }
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void handleXT9LanguageCyclingKey() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onCreate() {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public View onCreateCandidatesView() {
        return null;
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishCandidatesView(boolean finishingInput) {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onFinishInputView(boolean finishingInput) {
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void onStartInput(EditorInfo attribute, boolean restarting) {
    }
}
