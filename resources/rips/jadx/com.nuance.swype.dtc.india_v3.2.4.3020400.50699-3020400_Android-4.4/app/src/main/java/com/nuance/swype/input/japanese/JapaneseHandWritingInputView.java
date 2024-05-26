package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.common.Integers;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteJapanese;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.Composition;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.util.CharacterUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;
import jp.co.omronsoft.openwnn.WnnWord;

/* loaded from: classes.dex */
public class JapaneseHandWritingInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, HandWritingView.OnWritingAction, CJKCandidatesListView.OnWordSelectActionListener {
    public static final int HWR_123_MODE = 1;
    public static final int HWR_ABC_MODE = 2;
    public static final int HWR_JP_MODE = 0;
    static final int MSG_DELAY_RECOGNIZER = 1;
    static final int MSG_DELAY_RECOGNIZER_ADD_STROKE = 2;
    private static final int MSG_HANDLE_CHAR = 3;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 2;
    private static final int MSG_HANDLE_TEXT = 4;
    static final int MSG_SHOW_HOW_TO_TOAST = 505;
    Handler.Callback JpHwrInputViewCallback;
    Handler JpHwrInputViewHandler;
    private View candidatesPopup;
    Handler.Callback delayRecognizerHandlerCallback;
    private boolean gridViewFunctionButtonPressed;
    private List<Point> mCachedPoints;
    protected Composition mComposition;
    private JapaneseHandWritingView mCurrentWritingPad;
    Handler mDelayRecognizeHandler;
    private int mHandWritingMode;
    protected JapaneseHandWritingContainerView mJPHandWritingContainer;
    private XT9CoreJapaneseInput mJapaneseInput;
    private OpenWnnEngineJAJP mOpenwnnEngine;
    Handler mPopupViewHandler;
    private List<CharSequence> mRecognitionCandidates;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows;
    private boolean mShownCandidateList;
    protected CJKCandidatesListView mWordListView;
    protected JapaneseWordListViewContainer mWordListViewContainer;
    private T9WriteJapanese mWriteJapanese;
    private boolean mWritingOrRecognizing;
    Handler.Callback popupViewHandlerCallback;

    public JapaneseHandWritingInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JapaneseHandWritingInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHandWritingMode = 0;
        this.mRows = new ArrayList();
        this.mCachedPoints = new ArrayList();
        this.mWritingOrRecognizing = false;
        this.JpHwrInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0007, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r10) {
                /*
                    Method dump skipped, instructions count: 270
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.AnonymousClass1.handleMessage(android.os.Message):boolean");
            }
        };
        this.JpHwrInputViewHandler = WeakReferenceHandler.create(this.JpHwrInputViewCallback);
        this.delayRecognizerHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.2
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0006, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r3) {
                /*
                    r2 = this;
                    r1 = 1
                    int r0 = r3.what
                    switch(r0) {
                        case 1: goto L7;
                        case 2: goto L1e;
                        default: goto L6;
                    }
                L6:
                    return r1
                L7:
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$600(r0)
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                    com.nuance.swype.input.japanese.JapaneseHandWritingView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$200(r0)
                    if (r0 == 0) goto L6
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                    com.nuance.swype.input.japanese.JapaneseHandWritingView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$200(r0)
                    r0.setNewSession(r1)
                    goto L6
                L1e:
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                    com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$700(r0)
                    goto L6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.AnonymousClass2.handleMessage(android.os.Message):boolean");
            }
        };
        this.mDelayRecognizeHandler = WeakReferenceHandler.create(this.delayRecognizerHandlerCallback);
        this.popupViewHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.3
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        if (JapaneseHandWritingInputView.this.mSpeechWrapper != null && JapaneseHandWritingInputView.this.mSpeechWrapper.isResumable()) {
                            JapaneseHandWritingInputView.this.flushCurrentActiveWord();
                        }
                        JapaneseHandWritingInputView.this.setSpeechViewHost();
                        JapaneseHandWritingInputView.this.resumeSpeech();
                        return true;
                    case 505:
                        JapaneseHandWritingInputView.this.showHowToUseToast();
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mPopupViewHandler = WeakReferenceHandler.create(this.popupViewHandlerCallback);
        this.mComposition = new Composition();
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, t9write, speechWrapper);
        this.mWriteJapanese = (T9WriteJapanese) t9write;
        this.mJapaneseInput = (XT9CoreJapaneseInput) xt9coreinput;
        this.mKeyboardSwitcher = new KeyboardSwitcher(this.mIme, this.mJapaneseInput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(ime);
        this.mOpenwnnEngine = IMEApplication.from(getContext()).getSwypeCoreLibMgr().createOpenWnnEngineJAJP(IMEApplication.from(getContext()).getBuildInfo().getCoreLibName());
        this.mOpenwnnEngine.init();
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mWordListViewContainer == null) {
            IMEApplication app = IMEApplication.from(this.mIme);
            LayoutInflater inflater = IMEApplication.from(this.mIme).getThemedLayoutInflater(this.mIme.getLayoutInflater());
            app.getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mWordListViewContainer = (JapaneseWordListViewContainer) inflater.inflate(R.layout.japanese_handwriting_candidates, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mWordListViewContainer);
            this.mWordListViewContainer.initViews();
            this.mWordListView = (CJKCandidatesListView) this.mWordListViewContainer.findViewById(R.id.cjk_candidates);
        }
        this.mWordListView.setOnWordSelectActionListener(this);
        this.wordListViewContainerCJK = this.mWordListViewContainer;
        return this.mWordListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        this.mWriteJapanese.removeRecognizeListener(this);
        this.mKeyboardSwitcher = null;
        this.mCurrentWritingPad = null;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        this.mStarted = true;
        this.mHandWritingMode = 0;
        super.startInput(inputFieldInfo, restarting);
        if (this.mWordListView != null) {
            this.mWordListView.updateCandidatesSize();
        }
        if (this.mJPHandWritingContainer != null) {
            this.mJPHandWritingContainer.updateHandwritingPadSize();
        }
        this.mCompletionOn = false;
        this.mShownCandidateList = (this.mInputFieldInfo.isPasswordField() || this.mCompletionOn || (this.mInputFieldInfo.isCompletionField() && this.mInputFieldInfo.isURLField())) ? false : true;
        dismissPopupKeyboard();
        flushCurrentActiveWord();
        showLastSavedHandWritingScreen();
        this.mWriteJapanese.addRecognizeListener(this);
        cancelCurrentDefaultWord();
        this.mComposition.setInputConnection(getCurrentInputConnection());
        this.mWriteJapanese.clearCategory();
        this.mIme.getInputMethods().addTextCategory(this.mWriteJapanese, this.mCurrentInputLanguage);
        setKeyboardForTextEntry(inputFieldInfo);
        int lang = this.mCurrentInputLanguage.getCoreLanguageId();
        this.mWriteJapanese.startSession(lang);
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
        }
        if (UserPreferences.from(getContext()).isHwrScrmodeEnabled()) {
            this.mWriteJapanese.setRecognitionMode(0);
        } else {
            this.mWriteJapanese.setRecognitionMode(1);
            if (getContext().getResources().getConfiguration().orientation == 2) {
                this.mWriteJapanese.setWritingDirection(0);
            } else {
                this.mWriteJapanese.setWritingDirection(3);
            }
        }
        this.mWriteJapanese.applyChangedSettings();
        if (UserPreferences.from(getContext()).isHwrRotationEnabled()) {
            this.mWriteJapanese.updateRotationSetting(true);
        } else {
            this.mWriteJapanese.updateRotationSetting(false);
        }
        this.mWriteJapanese.updateAttachingCommonWordsLDB(false);
        Resources res = getResources();
        int delay = InputPrefs.getAutoDelay(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, res.getInteger(R.integer.handwriting_auto_recognize_default_ms));
        this.mWriteJapanese.setRecognizerDelay(delay);
        clearArcs();
        removeHowToUseToastMsg();
        postHowToUseToastMsg();
        updateCandidatesList(null);
        setLanguage(this.mJapaneseInput);
        postDelayResumeSpeech();
        this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(new DefaultHWRTouchKeyHandler(this.mIme, this, this.xt9coreinput, getDefaultKeyUIStateHandler()));
        this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
        this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
        this.xt9coreinput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(getDefaultInputContextHandler()));
    }

    private void postDelayResumeSpeech() {
        if (this.mPopupViewHandler.hasMessages(11)) {
            this.mPopupViewHandler.removeMessages(11);
        }
        this.mPopupViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    protected void showLastSavedHandWritingScreen() {
        setNormalHandScreenWritingFrame();
        showHandWritingView(false);
    }

    public void setNormalHandScreenWritingFrame() {
        this.mJPHandWritingContainer.setNormalHandScreenWritingFrame();
    }

    public void showHandWritingView(boolean animation) {
        AlphaAnimation animate = null;
        if (animation) {
            animate = new AlphaAnimation(0.0f, 1.0f);
            animate.setDuration(500L);
        }
        if (animate != null) {
            this.mJPHandWritingContainer.mHandwritingPadView.setAnimation(animate);
        }
        this.mJPHandWritingContainer.setNormalHandScreenWritingFrame();
    }

    public void setHandWritingView(HandWritingView view) {
        this.mCurrentWritingPad = (JapaneseHandWritingView) view;
    }

    public void setContainerView(JapaneseHandWritingContainerView container) {
        this.mJPHandWritingContainer = container;
    }

    private void setKeyboardForTextEntry(InputFieldInfo inputFieldInfo) {
        this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode(), !this.mCurrentInputLanguage.getCurrentInputMode().isHandwriting());
        if (inputFieldInfo.isNumericModeField()) {
            this.mWriteJapanese.addNumberOnlyCategory();
            this.mHandWritingMode = 1;
        } else if (inputFieldInfo.isPhoneNumberField()) {
            this.mCurrentWritingPad.setVisibility(8);
            this.mWriteJapanese.addPhoneNumberOnlyCategory();
            this.mHandWritingMode = 1;
        } else if (inputFieldInfo.isEmailAddressField()) {
            this.mWriteJapanese.addLatinLetterCategory();
            this.mHandWritingMode = 2;
        } else if (inputFieldInfo.isURLField()) {
            this.mWriteJapanese.addLatinLetterCategory();
            this.mHandWritingMode = 2;
        } else {
            this.mIme.getInputMethods().addTextCategory(this.mWriteJapanese, this.mCurrentInputLanguage);
            this.mHandWritingMode = 0;
        }
        this.mWriteJapanese.addGestureCategory();
        if (!ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteJapanese.applyChangedSettings();
        }
        toggleNextWritingMode(this.mHandWritingMode);
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        super.finishInput();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        this.mWritingOrRecognizing = false;
        this.mStarted = false;
        flushCurrentActiveWord();
        acceptCurrentDefaultWord();
        dismissPopupKeyboard();
        endArcsAddingSequence();
        resetWrite();
        this.mWriteJapanese.removeRecognizeListener(this);
        this.mWriteJapanese.finishSession();
        this.mRecognitionCandidates = null;
        this.mJPHandWritingContainer.requestLayout();
        this.mHandWritingMode = 0;
        this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
        this.xt9coreinput.setInputContextRequestListener(null);
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        super.handleClose();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        dismissPopupKeyboard();
        this.mHandWritingMode = 0;
        this.mStarted = false;
        resetWrite();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        hideHowToUseToast();
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                super.handleKey(primaryCode, quickPressed, repeatedCount);
                clearArcs();
                return true;
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                flushCurrentActiveWord();
                if (quickPressed) {
                    return true;
                }
                super.startSpeech();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        return this.mComposition != null && this.mComposition.length() > 0 && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        return this.mComposition != null && this.mComposition.length() > 0 && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleText(CharSequence text) {
        acceptCurrentDefaultWord();
        this.mComposition.insertText(text);
    }

    @Override // com.nuance.swype.input.InputView
    public void onText(CharSequence text, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        if (isShifted()) {
            this.mWriteJapanese.queueText(text.toString().toUpperCase(Locale.getDefault()));
        } else {
            this.mWriteJapanese.queueText(text);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    public int getMode() {
        return this.mHandWritingMode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void changeKeyboardMode() {
        boolean changed = false;
        if (this.mHandWritingMode == 0) {
            this.mHandWritingMode = 2;
            this.mWriteJapanese.addLatinLetterCategory();
            this.mWriteJapanese.addGestureCategory();
            changed = true;
        } else if (this.mHandWritingMode == 2) {
            this.mHandWritingMode = 1;
            this.mWriteJapanese.addNumberCategory();
            this.mWriteJapanese.addGestureCategory();
            changed = true;
        } else if (this.mHandWritingMode == 1) {
            this.mHandWritingMode = 0;
            this.mIme.getInputMethods().addTextCategory(this.mWriteJapanese, this.mCurrentInputLanguage);
            this.mWriteJapanese.addGestureCategory();
            changed = true;
        }
        if (changed && !ActivityManagerCompat.isUserAMonkey()) {
            if (isPendingRecognizeMessage()) {
                processPendingRecognizing();
            } else {
                acceptCurrentDefaultWord();
            }
            this.mWriteJapanese.applyChangedSettings();
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
        toggleNextWritingMode(this.mHandWritingMode);
    }

    @SuppressLint({"PrivateResource"})
    private void toggleNextWritingMode(int mode) {
        CharSequence currentMode;
        if (getKeyboard().getKeys() != null) {
            if (mode == 0) {
                currentMode = this.mIme.getResources().getText(R.string.japanese);
            } else if (mode == 1) {
                currentMode = this.mIme.getResources().getText(R.string.label_num_key);
            } else {
                currentMode = this.mIme.getResources().getText(R.string.label_alpha_key);
            }
            if (this.mCurrentWritingPad != null && currentMode != null) {
                String writingMode = currentMode.toString().toLowerCase(Locale.getDefault()) + "...";
                this.mCurrentWritingPad.setWritingMode(writingMode);
                this.mCurrentWritingPad.invalidate();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void setCandidatesViewShown(boolean shown) {
        super.setCandidatesViewShown(shown);
        if (this.mWordListViewContainer != null) {
            this.mWordListViewContainer.setVisibility(shown ? 0 : 8);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        if (isShifted()) {
            primaryCode = Character.toUpperCase(primaryCode);
        }
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteJapanese.queueChar((char) primaryCode);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleSpace() {
        acceptCurrentDefaultWord();
        sendSpace();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            this.mWriteJapanese.queueChar(' ');
        } else if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            this.mWriteJapanese.queueChar(' ');
        } else {
            acceptCurrentDefaultWord();
            boolean addSpace = true;
            if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn) {
                InputConnection ic = getCurrentInputConnection();
                ic.beginBatchEdit();
                CharSequence cSeqBefore = ic.getTextBeforeCursor(2, 0);
                CharacterUtilities charUtil = CharacterUtilities.from(getContext());
                if (cSeqBefore != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !charUtil.isPunctuationOrSymbol(cSeqBefore.charAt(0))) {
                    handleAutoPunct();
                    addSpace = false;
                }
                ic.endBatchEdit();
            }
            if (addSpace) {
                sendSpace();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        }
        return true;
    }

    private boolean handleBackspace() {
        if (!cancelCurrentDefaultWord()) {
            this.mIme.sendBackspace(0);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidatesList(null);
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            this.mWriteJapanese.queueChar('\b');
            return true;
        }
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            this.mWriteJapanese.queueChar('\b');
            return true;
        }
        handleBackspace();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        hideGridCandidatesView();
        this.mComposition.acceptCurrentInline();
        this.mRecognitionCandidates = null;
        clearArcs();
        if (this.mWordListView != null) {
            CJKCandidatesListView cJKCandidatesListView = this.mWordListView;
            this.mRecognitionCandidates = null;
            cJKCandidatesListView.setSuggestions(null, 0);
        }
        if (this.mWriteJapanese != null) {
            this.mWriteJapanese.noteSelectedCandidate(0);
        }
        syncCandidateDisplayStyleToMode();
        endArcsAddingSequence();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        updateDockModeForHandwriting(keyboard);
        super.setKeyboard(keyboard);
    }

    private boolean cancelCurrentDefaultWord() {
        if (this.mComposition == null || this.mWordListView == null || this.mWriteJapanese == null || this.mComposition.length() <= 0) {
            return false;
        }
        this.mComposition.clearCurrentInline();
        CJKCandidatesListView cJKCandidatesListView = this.mWordListView;
        this.mRecognitionCandidates = null;
        cJKCandidatesListView.setSuggestions(null, 0);
        syncCandidateDisplayStyleToMode();
        this.mWriteJapanese.noteSelectedCandidate(-1);
        endArcsAddingSequence();
        return true;
    }

    private boolean acceptCurrentDefaultWord() {
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            flushCurrentActiveWord();
            this.mWriteJapanese.noteSelectedCandidate(0);
            return true;
        }
        if (this.mComposition == null || this.mWordListView == null || this.mWriteJapanese == null || this.mComposition.length() <= 0) {
            return false;
        }
        this.mComposition.acceptCurrentInline();
        CJKCandidatesListView cJKCandidatesListView = this.mWordListView;
        this.mRecognitionCandidates = null;
        cJKCandidatesListView.setSuggestions(null, 0);
        syncCandidateDisplayStyleToMode();
        this.mWriteJapanese.noteSelectedCandidate(0);
        endArcsAddingSequence();
        return true;
    }

    public void setActiveCandidate(int n) {
        if (this.mRecognitionCandidates != null && n >= 0 && n < this.mRecognitionCandidates.size()) {
            this.mComposition.showInline(getPureCandidate(this.mRecognitionCandidates.get(n)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCandidatesList(List<CharSequence> candidates) {
        if (!this.mStarted) {
            this.mRecognitionCandidates = null;
            return;
        }
        this.mRecognitionCandidates = candidates;
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            this.mWordListView.setSuggestions(this.mRecognitionCandidates, 0);
            this.mComposition.showInline(getPureCandidate(this.mRecognitionCandidates.get(0)));
        } else {
            CJKCandidatesListView cJKCandidatesListView = this.mWordListView;
            this.mRecognitionCandidates = null;
            cJKCandidatesListView.setSuggestions(null, 0);
        }
        syncCandidateDisplayStyleToMode();
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penDown(View src) {
        dismissPopupKeyboard();
        removeDelayRecognitionMsg();
        removeDelayRecognitionStroke();
        if (this.mCurrentWritingPad == null || this.mCurrentWritingPad != src) {
            this.mCurrentWritingPad = (JapaneseHandWritingView) src;
            if (this.mWriteJapanese != null && !ActivityManagerCompat.isUserAMonkey() && (this.mCurrentWritingPad.getWidth() != this.mWriteJapanese.getWidth() || this.mCurrentWritingPad.getHeight() != this.mWriteJapanese.getHeight())) {
                this.mWriteJapanese.setWidth(this.mCurrentWritingPad.getWidth());
                this.mWriteJapanese.setHeight(this.mCurrentWritingPad.getHeight());
                this.mWriteJapanese.applyChangedSettings();
            }
        }
        if (!this.mWritingOrRecognizing) {
            acceptCurrentDefaultWord();
        }
        if (this.mWriteJapanese != null && !this.mWritingOrRecognizing) {
            this.mWriteJapanese.startArcsAddingSequence();
        }
        this.mWritingOrRecognizing = true;
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(List<Point> arc, View src) {
        this.mCachedPoints.addAll(arc);
        if (this.mWriteJapanese != null && this.mDelayRecognizeHandler != null) {
            if (isPendingRecognizeStrokeMessage()) {
                removeDelayRecognitionStroke();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            } else {
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            }
        }
        if (this.mWriteJapanese != null && this.mDelayRecognizeHandler != null) {
            if (isPendingRecognizeMessage()) {
                removeDelayRecognitionMsg();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteJapanese.getRecognizerDelay());
            } else {
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteJapanese.getRecognizerDelay());
            }
        }
    }

    public void startRecognizeNow(List<Point> arc) {
        this.mCachedPoints.addAll(arc);
        removeDelayRecognitionMsg();
        performDelayRecognition();
    }

    private void endArcsAddingSequence() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.invalidate();
        }
        if (this.mWriteJapanese != null) {
            this.mWriteJapanese.endArcsAddingSequence();
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void selectWord(int index, CharSequence word, View source) {
        this.mComposition.insertText(word);
        CJKCandidatesListView cJKCandidatesListView = this.mWordListView;
        this.mRecognitionCandidates = null;
        cJKCandidatesListView.setSuggestions(null, 0);
        syncCandidateDisplayStyleToMode();
        this.mWriteJapanese.noteSelectedCandidate(index);
        endArcsAddingSequence();
        if (this.mWriteJapanese.getRecognitionMode() == 1) {
            showNextWordPrediction(word.toString(), word.toString());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void displayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn && !this.mShownCandidateList) {
            this.mCompletions.update(completions);
            if (this.mCompletions.size() != 0) {
                updateCandidatesList(null);
                this.mWordListView.setSuggestions(this.mCompletions.getDisplayItems(), 0);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        if (this.mComposition != null && this.mComposition.length() > 0 && (newSelStart != candidatesIndices[1] || newSelEnd != candidatesIndices[1])) {
            acceptCurrentDefaultWord();
            updateCandidatesList(null);
        }
        if (this.mComposition != null) {
            this.mComposition.setSelection(newSelStart, newSelEnd);
        }
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mIme == null || isValidBuild()) {
            switch (event.mType) {
                case 1:
                    this.JpHwrInputViewHandler.sendMessageDelayed(this.JpHwrInputViewHandler.obtainMessage(2, event), 5L);
                    return;
                case 2:
                    this.JpHwrInputViewHandler.sendMessageDelayed(this.JpHwrInputViewHandler.obtainMessage(3, event), 5L);
                    return;
                case 3:
                    this.JpHwrInputViewHandler.sendMessageDelayed(this.JpHwrInputViewHandler.obtainMessage(4, event), 5L);
                    return;
                default:
                    return;
            }
        }
    }

    private void clearArcs() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearArcs();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChar(char ch) {
        if (ch == '\r') {
            ch = '\n';
        } else if (ch == '\t') {
            ch = ' ';
        }
        if (ch == '\b') {
            handleBackspace();
        } else if (ch == ' ') {
            handleSpace();
        } else {
            acceptCurrentDefaultWord();
            sendKeyChar(ch);
        }
        this.mRecognitionCandidates = null;
        updateCandidatesList(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.JpHwrInputViewHandler.removeMessages(2);
        this.JpHwrInputViewHandler.removeMessages(3);
        this.JpHwrInputViewHandler.removeMessages(4);
        for (int msg = 1; msg <= 19; msg++) {
            this.mPopupViewHandler.removeMessages(msg);
        }
        removeDelayRecognitionMsg();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        this.mKeyboardSwitcher.cycleShiftState();
        invalidateKeyboardImage();
    }

    private void removeDelayRecognitionMsg() {
        this.mDelayRecognizeHandler.removeMessages(1);
    }

    private boolean isPendingRecognizeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(1);
    }

    private void processPendingRecognizing() {
        this.mDelayRecognizeHandler.removeMessages(1);
        performDelayRecognition();
    }

    private void removeDelayRecognitionStroke() {
        this.mDelayRecognizeHandler.removeMessages(2);
    }

    private boolean isPendingRecognizeStrokeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognitionStroke() {
        int start = 0;
        for (int i = 0; i < this.mCachedPoints.size(); i++) {
            if (this.mCachedPoints.get(i).x == 0 && this.mCachedPoints.get(i).y == 0) {
                int end = i - 1;
                if (this.mWriteJapanese != null && start >= 0 && start < end) {
                    this.mWriteJapanese.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                }
                start = i + 1;
            }
        }
        if (this.mWriteJapanese != null) {
            this.mWriteJapanese.queueRecognition(null);
        }
        this.mCachedPoints.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognition() {
        if (this.mWriteJapanese != null) {
            if (!ActivityManagerCompat.isUserAMonkey()) {
                this.mWriteJapanese.endArcsAddingSequence();
            }
            this.mCachedPoints.clear();
            this.mWritingOrRecognizing = false;
            this.mCurrentWritingPad.mFaddingStrokeQueue.stopFading();
            this.mCurrentWritingPad.clearArcs();
        }
    }

    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null && this.mKeyboardSwitcher.isAlphabetMode()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            AppSpecificInputConnection ic = getCurrentInputConnection();
            if (this.mAutoCap && ei != null && ei.inputType != 0 && ic != null) {
                caps = ic.getCapsModeAtCursor(attr);
            }
            Shift.ShiftState currentShiftState = this.mKeyboardSwitcher.getCurrentShiftState();
            if (currentShiftState != Shift.ShiftState.LOCKED) {
                switch (caps) {
                    case HardKeyboardManager.META_CTRL_ON /* 4096 */:
                        currentShiftState = Shift.ShiftState.LOCKED;
                        break;
                    case 8192:
                    case HardKeyboardManager.META_CTRL_RIGHT_ON /* 16384 */:
                        currentShiftState = Shift.ShiftState.ON;
                        break;
                    default:
                        currentShiftState = Shift.ShiftState.OFF;
                        break;
                }
            }
            setShiftState(currentShiftState);
        }
    }

    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.deleteSurroundingText(1, 0);
            ic.commitText("。", 1);
        }
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(Stroke.Arc[] arcs, View Src) {
        penUp(arcs[0].mPoints, Src);
    }

    private void resetWrite() {
        if (this.mCachedPoints != null) {
            this.mCachedPoints.clear();
        }
        clearArcs();
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setNewSession(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"PrivateResource"})
    public void showHowToUseToast() {
        AppPreferences appPrefs = AppPreferences.from(getContext());
        if (appPrefs.getBoolean("show_how_to_use_hwr", true)) {
            appPrefs.setBoolean("show_how_to_use_hwr", false);
            QuickToast.show(getContext(), getResources().getString(R.string.how_to_use_hwr), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
        }
    }

    private void hideHowToUseToast() {
        QuickToast.hide();
    }

    private void postHowToUseToastMsg() {
        this.mPopupViewHandler.sendEmptyMessageDelayed(505, 10L);
    }

    private void removeHowToUseToastMsg() {
        this.mPopupViewHandler.removeMessages(505);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        return false;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void nextBtnPressed(CJKCandidatesListView aSource) {
        dismissPopupKeyboard();
        if (aSource == this.mWordListView && this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            showGridCandidatesView(this.mRecognitionCandidates);
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
        dismissPopupKeyboard();
        if (aSource == this.mWordListView && this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            showGridCandidatesView(this.mRecognitionCandidates);
        }
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    private void showGridCandidatesView(List<CharSequence> aDataList) {
        if (aDataList != null && !aDataList.isEmpty() && !this.mContextWindowShowing && !super.isSpeechViewShowing()) {
            int height = this.mJPHandWritingContainer.getHeight() + this.mWordListViewContainer.getHeight();
            log.d("showGridCandidatesView...mJPHandWritingContainer.getHeight()", Integer.valueOf(this.mJPHandWritingContainer.getHeight()));
            log.d("showGridCandidatesView...mWordListViewContainer.getHeight()", Integer.valueOf(this.mWordListViewContainer.getHeight()));
            log.d("showGridCandidatesView... height: ", Integer.valueOf(height));
            int width = getKeyboard().getMinWidth();
            this.mJPHandWritingContainer.setMinimumHeight(height);
            setCandidatesViewShown(false);
            if (this.candidatesPopup != null && this.candidatesPopup.getMeasuredWidth() != width) {
                log.d("recreate candidatesPopup....candidatesPopup.getMeasuredWidth(): ", Integer.valueOf(this.candidatesPopup.getMeasuredWidth()), "keyboard width: ", Integer.valueOf(width));
                this.candidatesPopup = null;
            }
            if (this.candidatesPopup == null) {
                LayoutInflater inflater = IMEApplication.from(getContext()).getThemedLayoutInflater(LayoutInflater.from(getContext()));
                IMEApplication.from(getContext()).getThemeLoader().setLayoutInflaterFactory(inflater);
                this.candidatesPopup = inflater.inflate(R.layout.candidates_popup, (ViewGroup) null);
                IMEApplication.from(getContext()).getThemeLoader().applyTheme(this.candidatesPopup);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
                this.candidatesPopup.setLayoutParams(lp);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, Integers.STATUS_SUCCESS);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, Integers.STATUS_SUCCESS);
                this.candidatesPopup.measure(widthMeasureSpec, heightMeasureSpec);
                ImageButton closeButton = (ImageButton) this.candidatesPopup.findViewById(R.id.closeButton);
                closeButton.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_popup_btn_close_cjk));
                closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        JapaneseHandWritingInputView.this.hideGridCandidatesView();
                        JapaneseHandWritingInputView.this.setCandidatesViewShown(true);
                    }
                });
                closeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.5
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    /* JADX WARN: Code restructure failed: missing block: B:3:0x0008, code lost:            return false;     */
                    @Override // android.view.View.OnTouchListener
                    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct code enable 'Show inconsistent code' option in preferences
                    */
                    public boolean onTouch(android.view.View r4, android.view.MotionEvent r5) {
                        /*
                            r3 = this;
                            r2 = 0
                            int r0 = r5.getAction()
                            switch(r0) {
                                case 0: goto L9;
                                case 1: goto L10;
                                case 2: goto L9;
                                case 3: goto L10;
                                default: goto L8;
                            }
                        L8:
                            return r2
                        L9:
                            com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                            r1 = 1
                            com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$1202(r0, r1)
                            goto L8
                        L10:
                            com.nuance.swype.input.japanese.JapaneseHandWritingInputView r0 = com.nuance.swype.input.japanese.JapaneseHandWritingInputView.this
                            com.nuance.swype.input.japanese.JapaneseHandWritingInputView.access$1202(r0, r2)
                            goto L8
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.AnonymousClass5.onTouch(android.view.View, android.view.MotionEvent):boolean");
                    }
                });
            }
            ScrollView scrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
            scrollView.scrollTo(0, 0);
            KeyboardViewEx keyboardViewEx = (KeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
            setGridCandidates(aDataList, scrollView.getMeasuredWidth());
            final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, scrollView.getMeasuredWidth(), height);
            keyboardViewEx.setKeyboard(keyboard);
            keyboardViewEx.setDoubleBuffered(false);
            keyboardViewEx.setContextCandidatesView(true);
            keyboardViewEx.setIme(this.mIme);
            keyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.japanese.JapaneseHandWritingInputView.6
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    if (!JapaneseHandWritingInputView.this.gridViewFunctionButtonPressed) {
                        JapaneseHandWritingInputView.this.hideGridCandidatesView();
                        JapaneseHandWritingInputView.this.setCandidatesViewShown(true);
                        JapaneseHandWritingInputView.this.selectWord(0, text, JapaneseHandWritingInputView.this.candidatesListViewCJK);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    keyboard.clearStickyKeys();
                }
            });
            this.candidatesPopup.setLayoutParams(new FrameLayout.LayoutParams(getKeyboard().getMinWidth(), height));
            this.mJPHandWritingContainer.showContextWindow(this.candidatesPopup, this.mWordListViewContainer.getHeight());
        }
    }

    @SuppressLint({"PrivateResource"})
    public void setGridCandidates(List<CharSequence> aLableList, int keyboardWidth) {
        ArrayList<KeyboardEx.GridKeyInfo> curRowKeys;
        ArrayList<Integer> curRowKeyWidth;
        int textSize = IMEApplication.from(this.mIme).getResources().getDimensionPixelSize(R.dimen.key_text_size);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        int paddingWidth = (textSize / 3) + 1;
        int curRowWidth = 0;
        int curRowNumber = 0;
        List<ArrayList<Integer>> rowKeyWidth = new ArrayList<>();
        this.mRows.clear();
        int i = 0;
        while (i < aLableList.size()) {
            if (rowKeyWidth.size() <= curRowNumber || this.mRows.size() <= curRowNumber) {
                curRowKeys = new ArrayList<>();
                this.mRows.add(curRowKeys);
                curRowKeyWidth = new ArrayList<>();
                rowKeyWidth.add(curRowKeyWidth);
            } else {
                curRowKeys = this.mRows.get(curRowNumber);
                curRowKeyWidth = rowKeyWidth.get(curRowNumber);
            }
            String str = getPureCandidate(aLableList.get(i)).toString();
            int keyWidth = ((int) paint.measureText(str)) + 2 + (paddingWidth * 2);
            if (keyWidth < (paddingWidth * 2) + textSize) {
                keyWidth = textSize + (paddingWidth * 2);
            }
            if (curRowWidth + keyWidth > keyboardWidth) {
                if (curRowKeyWidth.isEmpty() || curRowKeys.isEmpty()) {
                    curRowKeys.add(new KeyboardEx.GridKeyInfo(keyboardWidth, str));
                    curRowKeyWidth.add(Integer.valueOf(keyboardWidth));
                } else {
                    int addingWidth = (keyboardWidth - curRowWidth) / curRowKeyWidth.size();
                    int refreshedRowWidth = 0;
                    for (int j = 0; j < curRowKeyWidth.size(); j++) {
                        int newKeyWidth = curRowKeyWidth.get(j).intValue() + addingWidth;
                        refreshedRowWidth += newKeyWidth;
                        if (curRowKeyWidth.size() - 1 == j) {
                            newKeyWidth += keyboardWidth - refreshedRowWidth;
                        }
                        curRowKeyWidth.set(j, Integer.valueOf(newKeyWidth));
                        curRowKeys.get(j).width = newKeyWidth;
                    }
                    i--;
                }
                curRowWidth = 0;
                curRowNumber++;
            } else {
                curRowWidth += keyWidth;
                curRowKeys.add(new KeyboardEx.GridKeyInfo(keyWidth, str));
                curRowKeyWidth.add(Integer.valueOf(keyWidth));
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideGridCandidatesView() {
        if (this.mContextWindowShowing) {
            this.mJPHandWritingContainer.hideContextWindow(this.candidatesPopup);
            this.mJPHandWritingContainer.setMinimumHeight(0);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.mWordListViewContainer;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean onPressReleaseCandidate(int index, String word, int wdSource) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (this.mIme == null) {
            return false;
        }
        finishInput();
        this.mIme.requestHideSelf(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        if (this.mWordListView != null) {
            this.mWordListView.showSwypeTooltip();
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
        if (this.mWordListViewContainer != null) {
            this.mWordListViewContainer.updateScrollArrowVisibility();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isHandWritingInputView() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getPureCandidate(CharSequence candidate) {
        String cand = candidate.toString();
        int index = cand.lastIndexOf(36);
        return index <= 0 ? candidate : cand.substring(0, index);
    }

    private List<CharSequence> getPureCandidates(List<CharSequence> candidates) {
        if (candidates == null) {
            return null;
        }
        int size = candidates.size();
        List<CharSequence> pureCandidates = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pureCandidates.add(getPureCandidate(candidates.get(i)));
        }
        return pureCandidates;
    }

    private void showNextWordPrediction(CharSequence hiragana, CharSequence word) {
        WnnWord previousWord;
        List<String> predictionList;
        if (this.mOpenwnnEngine != null) {
            if (hiragana != null) {
                previousWord = this.mOpenwnnEngine.doExactSearch(hiragana.toString(), word.toString());
            } else {
                previousWord = this.mOpenwnnEngine.doExactSearch(word.toString(), word.toString());
            }
            if (previousWord != null && (predictionList = this.mOpenwnnEngine.doPredictionSearch(previousWord)) != null && predictionList.size() > 0) {
                List<CharSequence> words = new ArrayList<>();
                for (String aWord : predictionList) {
                    words.add(aWord);
                }
                this.mRecognitionCandidates = words;
                this.mWordListView.setSuggestions(words, 0);
            }
        }
    }
}
