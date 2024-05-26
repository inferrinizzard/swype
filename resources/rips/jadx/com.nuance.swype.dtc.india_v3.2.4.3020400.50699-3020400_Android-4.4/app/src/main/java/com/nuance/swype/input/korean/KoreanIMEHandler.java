package com.nuance.swype.input.korean;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.XT9CoreKoreanInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.IMEHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardInputInflater;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.InputModeListAdapter;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class KoreanIMEHandler extends IMEHandler {
    private static final LogManager.Log log = LogManager.getLog("KoreanIMEHandler");
    private XT9CoreKoreanInput koreanInput;

    public KoreanIMEHandler(IME ime) {
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

    private void destroyAllInputs(boolean destroyCore) {
        finishAllInputView();
        this.mIme.resetInputView(destroyCore);
        this.mIme.mCurrentInputViewName = KeyboardInputInflater.NO_INPUTVIEW;
    }

    private void finishAllInputView() {
        this.mIme.keyboardInputInflater.callAllInputViewToFinish();
    }

    @Override // com.nuance.swype.input.IMEHandler
    @SuppressLint({"PrivateResource"})
    public void showInputModeMenu() {
        if (!this.mIme.mInputFieldInfo.isPhoneNumberField()) {
            if (this.mIme.mOptionsDialog == null || !this.mIme.mOptionsDialog.isShowing()) {
                final InputModeListAdapter modeAdapter = new InputModeListAdapter(this.mIme.getApplicationContext(), this.mIme.mCurrentInputLanguage);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mIme.getApplicationContext());
                builder.setCancelable(true);
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.korean.KoreanIMEHandler.1
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
                builder.setAdapter(modeAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.korean.KoreanIMEHandler.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface di, int position) {
                        di.dismiss();
                        if (((InputMethods.InputMode) modeAdapter.getItem(position)) != KoreanIMEHandler.this.mIme.mCurrentInputLanguage.getCurrentInputMode()) {
                            Candidates.Source src = KoreanIMEHandler.this.mIme.getCurrentInputView().getCurrentWordCandidatesSource();
                            if (src != Candidates.Source.CAPS_EDIT && src != Candidates.Source.TOOL_TIP && src != Candidates.Source.UDB_EDIT) {
                                IME.setLastActiveWord(KoreanIMEHandler.this.mIme.getCurrentInputView().getCurrentExactWord());
                            } else {
                                IME.setLastActiveWord(null);
                            }
                            KoreanIMEHandler.this.mIme.toggleHwrAndKeyboardInputMode();
                        }
                        KoreanIMEHandler.this.mIme.mWantToast = true;
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
    @SuppressLint({"PrivateResource"})
    public void cyclingKeyboardInput() {
        if (!isEditNumorSymMode()) {
            InputMethods.InputMode currentInputMode = this.mIme.mCurrentInputLanguage.getCurrentInputMode();
            InputMethods.Layout currentLayout = currentInputMode.getCurrentLayout();
            InputMethods.Layout nextLayout = currentInputMode.getNextLayout();
            if (nextLayout.mLayoutId == this.mIme.getResources().getInteger(R.integer.symbols_keyboard_id)) {
                nextLayout = currentInputMode.getNextLayout();
            } else if (nextLayout.mLayoutId == this.mIme.getResources().getInteger(R.integer.bilingual_keyboard_id)) {
                nextLayout = currentInputMode.getNextLayout();
            }
            if (!currentLayout.equals(nextLayout)) {
                this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).flushCurrentActiveWord();
                IMEApplication.from(this.mIme).getKeyboardManager().evictAll();
                this.mIme.switchInputViewAsync();
            }
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void toggleFullScreenHwr() {
        InputView inputView = this.mIme.getCurrentInputView();
        if (inputView != null && (inputView instanceof KoreanHandWritingInputView)) {
            KoreanHandWritingInputView hwrInputView = (KoreanHandWritingInputView) inputView;
            hwrInputView.acceptInlineAndClearCandidates();
            hwrInputView.toggleHandWritingFrame();
            this.mIme.updateInputViewShown();
            hwrInputView.postDelayShowingFullScreenMsg();
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void handleNewWordsDelayScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
        if (!bucket.isBigramDlm()) {
            log.d("Korean handleNewWordsDelayScanning()");
            if (!this.mIme.isImeInUse()) {
                if (!bucket.isEmpty()) {
                    if (this.initializeCoreNeeded) {
                        initializeCore();
                        this.initializeCoreNeeded = false;
                    }
                    if (this.koreanInput != null) {
                        scan(bucket, getMaxItemToScan(bucket));
                        if (!bucket.isEmpty()) {
                            log.d("Korean handleNewWordsDelayScanning() - schedule for next batch");
                            this.mIme.sendDelayNewWordsScanning(bucket, IME.NEXT_SCAN_IN_MILLIS);
                            return;
                        } else {
                            log.d("Korean handleNewWordsDelayScanning() - done");
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            log.d("Korean handleNewWordsDelayScanning() -- ime in use");
            this.initializeCoreNeeded = true;
            this.mIme.sendDelayNewWordsScanning(bucket, IME.RETRY_DELAY_IN_MILLIS);
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void scan(NewWordsBucketFactory.NewWordsBucket bucket, int maxItemToScan) {
        log.d("Korean scan() itemsToScan = ", Integer.valueOf(maxItemToScan));
        int itemScanned = 0;
        while (itemScanned < maxItemToScan) {
            String words = bucket.remove();
            if (words == null) {
                break;
            }
            itemScanned++;
            this.koreanInput.dlmScanBuf(words, bucket.isHighQualityWord, true, false);
        }
        log.d("Korean scan() itemScanned = ", Integer.valueOf(itemScanned));
    }

    @Override // com.nuance.swype.input.IMEHandler
    public void initializeCore() {
        log.d("Korean initializeCore()");
        InputMethods im = this.mIme.getInputMethods();
        if (im != null && im.getCurrentInputLanguage().isKoreanLanguage()) {
            this.koreanInput = IMEApplication.from(this.mIme).getSwypeCoreLibMgr().getXT9CoreKoreanInputSession();
            this.koreanInput.startSession();
        } else {
            this.koreanInput = null;
        }
    }

    @Override // com.nuance.swype.input.IMEHandler
    public XT9Status checkCompatability() {
        log.d("Korean checkCompatability()");
        XT9Status status = XT9Status.ET9STATUS_NONE;
        InputMethods im = this.mIme.getInputMethods();
        if (im != null && im.getCurrentInputLanguage().isKoreanLanguage()) {
            this.koreanInput = IMEApplication.from(this.mIme).getSwypeCoreLibMgr().getXT9CoreKoreanInputSession();
            this.koreanInput.startSession();
            XT9Status status2 = im.getCurrentInputLanguage().setLanguage(this.koreanInput);
            return status2;
        }
        this.koreanInput = null;
        return status;
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
