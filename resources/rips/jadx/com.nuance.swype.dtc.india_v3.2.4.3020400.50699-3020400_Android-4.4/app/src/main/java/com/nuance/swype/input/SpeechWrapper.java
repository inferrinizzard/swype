package com.nuance.swype.input;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.speech.CustomWordSynchronizer;
import com.nuance.speech.Dictation;
import com.nuance.speech.DictationEventListener;
import com.nuance.speech.SpeechPopupView;
import com.nuance.speech.SpeechResultTextBuffer;
import com.nuance.speech.dragon.DragonDictation;
import com.nuance.speech.dragon.DragonSpeechResultsTextBuffer;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

/* loaded from: classes.dex */
public class SpeechWrapper implements DictationEventListener {
    static final int MSG_DELAY_UPATE_SPEECH_RESULT = 1505;
    static final int MSG_RESUME_SPEECH_POPUP_VIEW = 1502;
    static final int MSG_RETRY_SPEECH = 1503;
    static final int MSG_SHOW_DICTATION_FAILED = 1500;
    static final int MSG_START_DICTATION = 1504;
    public static final int SPEECH_PROVIDER_DRAGON = 0;
    public static final int SPEECH_PROVIDER_GOOGLE = 1;
    public static final int SPEECH_PROVIDER_NONE = 99;
    static final int TIME_RESTORE_SPEECH_POPUP_DELAY = 1;
    private static final LogManager.Log log = LogManager.getLog("SpeechWrapper");
    private long lastSpeechResultUpdate;
    private AlertDialog mAlertMessageDialog;
    private Dictation mDictation;
    private WeakReference<InputView> mHostViewWeakRef;
    private Rect mRect;
    private State mState;
    private final int speechProvider;
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.SpeechWrapper.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1500:
                    SpeechWrapper.this.showDictationFailed((RecognitionStatus) msg.obj);
                    return true;
                case 1501:
                default:
                    return false;
                case SpeechWrapper.MSG_RESUME_SPEECH_POPUP_VIEW /* 1502 */:
                    SpeechWrapper.this.handleResumePopupView(msg);
                    return true;
                case SpeechWrapper.MSG_RETRY_SPEECH /* 1503 */:
                    SpeechWrapper.this.showPopupSpeech();
                    return true;
                case SpeechWrapper.MSG_START_DICTATION /* 1504 */:
                    SpeechWrapper.this.startDictation();
                    return true;
                case SpeechWrapper.MSG_DELAY_UPATE_SPEECH_RESULT /* 1505 */:
                    SpeechWrapper.this.updateDelaySpeechResult((Dictation.SpeechResult) msg.obj);
                    return true;
            }
        }
    };
    private final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);
    private SpeechPopupView mSpeechView = new SpeechPopupView();
    private final SpeechResultTextBuffer mSpeechResultTextBuffer = createSpeechResultTextBuffer();

    /* loaded from: classes.dex */
    public enum RecognitionStatus {
        UpdateResult,
        Canceled,
        Failed,
        Failed_Connection
    }

    /* loaded from: classes.dex */
    public enum State {
        Stopped,
        Started,
        Paused
    }

    public SpeechWrapper(Context context) {
        this.speechProvider = IMEApplication.from(context).getSpeechProvider();
    }

    public void setHost(InputView view) {
        this.mHostViewWeakRef = new WeakReference<>(view);
        if (getHostView() != null && this.mDictation != null && !this.mDictation.getCurrentLanguageName().equals(getCurrentDictationLanguage())) {
            this.mDictation.release();
            this.mDictation = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InputView getHostView() {
        if (this.mHostViewWeakRef != null) {
            return this.mHostViewWeakRef.get();
        }
        return null;
    }

    public Dictation getCurrentDictation() {
        return this.mDictation;
    }

    private void setSpeechWindowSize(Rect rect) {
        this.mRect = rect;
    }

    private Dictation getDictationNoCreate() {
        return this.mDictation;
    }

    private Dictation getDictation() {
        if (this.mDictation != null) {
            return this.mDictation;
        }
        InputView hostView = getHostView();
        if (hostView != null && hostView.mIme != null) {
            Dictation createDictation = createDictation(hostView.getContext(), getCurrentDictationLanguage(), hostView.mIme.isAccessibilitySupportEnabled());
            this.mDictation = createDictation;
            return createDictation;
        }
        return null;
    }

    private String getCurrentDictationLanguage() {
        InputView hostView = getHostView();
        IMEApplication imeApp = IMEApplication.from(hostView.getContext());
        String lang = imeApp.getUserPreferences().getCurrentDictationLanguageName();
        if (UserPreferences.DEFAULT_AUTOMATIC_DICTATION_LANGUAGE.equals(lang)) {
            if (hostView.getCurrentInputLanguage() != null) {
                int langId = hostView.getCurrentInputLanguage().getCoreLanguageId();
                InputMethods.Language coreLang = imeApp.getInputMethods().findCoreInputLanguage(langId);
                if (coreLang != null) {
                    return coreLang.mEnglishName;
                }
                return null;
            }
            return null;
        }
        return lang;
    }

    public void onChangeLanguage(Context context, String langName) {
        IMEApplication.from(context).getUserPreferences().setCurrentDictationLanguageName(langName);
        String langName2 = getCurrentDictationLanguage();
        if (this.mDictation != null && !langName2.equals(this.mDictation.getCurrentLanguageName())) {
            this.mDictation.cancel();
            this.mDictation.release();
            this.mDictation = null;
            InputView hostView = getHostView();
            this.mDictation = createDictation(context, langName2, hostView.mIme.isAccessibilitySupportEnabled());
            if (this.mDictation != null) {
                this.mDictation.setDictationResultTextBuffer(this.mSpeechResultTextBuffer);
                this.mSpeechView.setDictationEngine(this.mDictation);
                this.mDictation.setDictationResultTextBuffer(this.mSpeechResultTextBuffer);
                this.mDictation.setDictationEventListener(this);
                this.mDictation.setEndofSpeechDetection(UserPreferences.from(hostView.getContext()).isEndOfSpeechDetectionEnabled());
                this.mSpeechView.refreshLanguageLabel();
                restartDictation();
            }
        }
    }

    public void updateSelection(InputConnection ic, int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        if (this.mSpeechResultTextBuffer != null) {
            this.mSpeechResultTextBuffer.updateSelection(ic, oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        }
    }

    public void updateText(String text, int cursorBegin, int cursorEnd) {
        if (this.mSpeechResultTextBuffer != null) {
            this.mSpeechResultTextBuffer.updateText(text, cursorBegin, cursorEnd);
        }
    }

    public boolean isHighlightedTextSpeechDictated() {
        return this.mSpeechResultTextBuffer != null && this.mSpeechResultTextBuffer.isTextDictated();
    }

    public ArrayList<CharSequence> getAlternateCandidates() {
        if (this.mSpeechResultTextBuffer != null) {
            return this.mSpeechResultTextBuffer.getCandidates();
        }
        return null;
    }

    public void speechChooseCandidate(int index) {
        if (this.mSpeechResultTextBuffer != null) {
            this.mSpeechResultTextBuffer.chooseCandidate(index);
        }
    }

    public void onCreate() {
    }

    public void onDestroy() {
        if (this.mSpeechView != null) {
            this.mSpeechView.clearMessages();
            this.mSpeechView = null;
        }
        if (this.mDictation != null) {
            this.mDictation.release();
            this.mDictation = null;
        }
    }

    public boolean isSpeechDictationInProgress() {
        return (this.mDictation == null || this.mSpeechView == null || !this.mSpeechView.isShowing()) ? false : true;
    }

    public void startSpeech(InputView view, Rect rect) {
        setHost(view);
        setSpeechWindowSize(rect);
        if (this.mSpeechResultTextBuffer != null) {
            this.mSpeechResultTextBuffer.setFieldInputInfo(getHostView().mInputFieldInfo);
        }
        showPopupSpeech();
    }

    public void resumeSpeech(InputView view, Rect rect) {
        log.d("resumeSpeech");
        if (this.mState != null) {
            LogManager.Log log2 = log;
            Object[] objArr = new Object[2];
            objArr[0] = "mState is...";
            objArr[1] = this.mState.toString() != null ? this.mState.toString() : "Stopped";
            log2.d(objArr);
        }
        if (this.mState == State.Paused) {
            setHost(view);
            setSpeechWindowSize(rect);
            log.d("rect.left..", Integer.valueOf(rect.left), "..rect.top..", Integer.valueOf(rect.top), "..rect.right..", Integer.valueOf(rect.right), "..rect.bottom..", Integer.valueOf(rect.bottom));
            if (this.mSpeechResultTextBuffer != null) {
                this.mSpeechResultTextBuffer.setFieldInputInfo(getHostView().mInputFieldInfo);
            }
            postDelayResumePopupView(1);
        }
    }

    public boolean isResumable() {
        return this.mState == State.Paused;
    }

    public void stopSpeech() {
        if (this.mState == State.Started) {
            dimissAlertDialog();
            removeAllPopupSpeechViewMsg();
            cancelSpeechDictation();
            hidePopupSpeech();
            this.mState = State.Stopped;
        }
    }

    public void pauseSpeech() {
        log.d("pauseSpeech");
        if (this.mState == State.Started) {
            dimissAlertDialog();
            hidePopupSpeech();
            this.mState = State.Paused;
        }
    }

    public void cancelSpeech() {
        if (this.mState == State.Started || this.mState == State.Paused) {
            dimissAlertDialog();
            removeAllPopupSpeechViewMsg();
            cancelSpeechDictation();
            hidePopupSpeech();
            InputView hostView = getHostView();
            if (hostView != null) {
                hostView.onCancelSpeech();
            }
        }
        this.mState = State.Stopped;
    }

    private void cancelSpeechDictation() {
        Dictation dictation = getDictation();
        if (dictation != null) {
            if (dictation.getDictationState() == Dictation.DictationState.Dictation_Inprogress || dictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
                dictation.cancel();
            }
        }
    }

    protected void hidePopupSpeech() {
        if (this.mSpeechView != null && this.mSpeechView.isShowing()) {
            log.d("hidePopupSpeech");
            this.mSpeechView.dismiss();
            this.mSpeechView.setDictationEngine(null);
            Dictation dictation = getDictation();
            if (dictation != null) {
                dictation.setDictationResultTextBuffer(null);
                dictation.setDictationEventListener(null);
            }
            this.mSpeechView.setParentView(null);
            InputView hostView = getHostView();
            if (hostView != null) {
                UsageData.recordVoiceUsageEvent(this.mDictation == null ? "null" : this.mDictation.getCurrentLanguageName());
                hostView.onSpeechViewDismissed();
            }
        }
    }

    protected void showPopupSpeech() {
        log.d("showPopupSpeech");
        if (this.mSpeechView != null && this.mSpeechView.isShowing()) {
            boolean isSameSize = false;
            int w = this.mRect.right - this.mRect.left;
            int h = this.mRect.bottom - this.mRect.top;
            if (this.mSpeechView.getPopupWindow().getContentView().getWidth() == w && this.mSpeechView.getPopupWindow().getContentView().getHeight() == h) {
                isSameSize = true;
            }
            int[] location = new int[2];
            this.mSpeechView.getPopupWindow().getContentView().getLocationOnScreen(location);
            if (location[1] != 0 && isSameSize) {
                log.i("showPopupSpeech - already have one");
                return;
            }
            this.mSpeechView.dismiss();
        }
        Dictation dictation = getDictation();
        if (dictation != null) {
            if (this.mSpeechView == null) {
                this.mSpeechView = new SpeechPopupView();
            }
            InputView hostView = getHostView();
            this.mSpeechView.setDictationEngine(dictation);
            dictation.setDictationResultTextBuffer(this.mSpeechResultTextBuffer);
            dictation.setDictationEventListener(this);
            dictation.setEndofSpeechDetection(UserPreferences.from(hostView.getContext()).isEndOfSpeechDetectionEnabled());
            this.mSpeechView.setParentView(hostView);
            this.mState = State.Started;
            int w2 = this.mRect.right - this.mRect.left;
            int h2 = this.mRect.bottom - this.mRect.top;
            int x = this.mRect.left;
            int y = this.mRect.top;
            log.d("showPopupSpeech show popup");
            if (this.mSpeechView.show(0, x, y, w2, h2)) {
                hostView.onSpeechViewShowedUp();
                if (this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped) {
                    this.mHandler.sendEmptyMessageDelayed(MSG_START_DICTATION, 5L);
                }
                dictation.canelCustomWordSync();
            }
        }
    }

    private void postDelayResumePopupView(int delay) {
        if (getDictation() != null && this.mSpeechView != null) {
            this.mHandler.removeMessages(MSG_RESUME_SPEECH_POPUP_VIEW);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_RESUME_SPEECH_POPUP_VIEW, delay, 0), delay);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResumePopupView(Message msg) {
        InputView hostView = getHostView();
        if (hostView != null && hostView.getWindowToken() != null) {
            setSpeechWindowSize(hostView.getSpeechPopupRectInWindowCoord());
            showPopupSpeech();
        } else {
            postDelayResumePopupView(msg.arg1);
        }
    }

    void showDictationFailed(RecognitionStatus status) {
        switch (status) {
            case Canceled:
            case Failed:
            default:
                return;
            case Failed_Connection:
                showNoNetworkAvailableAlertDialog();
                return;
        }
    }

    private void dimissAlertDialog() {
        if (this.mAlertMessageDialog != null && this.mAlertMessageDialog.isShowing()) {
            this.mAlertMessageDialog.dismiss();
        }
    }

    private void showNoNetworkAvailableAlertDialog() {
        dimissAlertDialog();
        log.e("showNoNetworkAvailableAlertDialog");
        InputView hostView = getHostView();
        if (hostView != null) {
            Resources res = hostView.getContext().getResources();
            this.mAlertMessageDialog = createAlertDialog(res.getText(R.string.no_network_available), res.getText(R.string.no_network_try_again_msg));
            if (this.mAlertMessageDialog != null) {
                this.mAlertMessageDialog.setCancelable(false);
                this.mAlertMessageDialog.setButton(-2, res.getText(R.string.dismiss_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.SpeechWrapper.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        InputView hostView2 = SpeechWrapper.this.getHostView();
                        if (hostView2 != null) {
                            hostView2.mIme.hideDictationLanguageMenu();
                        }
                        SpeechWrapper.this.hidePopupSpeech();
                        SpeechWrapper.this.mState = State.Stopped;
                    }
                });
                this.mAlertMessageDialog.show();
            }
        }
    }

    private void showNoSpeechDetectionAlertDialog() {
        dimissAlertDialog();
        log.e("showNoSpeechDetectionAlertDialog");
        InputView hostView = getHostView();
        if (hostView != null) {
            Resources res = hostView.getContext().getResources();
            this.mAlertMessageDialog = createAlertDialog(res.getText(R.string.no_speech_detected), res.getText(R.string.no_speech_try_again_msg));
            if (this.mAlertMessageDialog != null) {
                this.mAlertMessageDialog.setButton(-2, res.getText(R.string.cancel_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.SpeechWrapper.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        InputView hostView2 = SpeechWrapper.this.getHostView();
                        if (hostView2 != null) {
                            hostView2.mIme.hideDictationLanguageMenu();
                        }
                        SpeechWrapper.this.hidePopupSpeech();
                        SpeechWrapper.this.mState = State.Stopped;
                    }
                });
                this.mAlertMessageDialog.setButton(-1, res.getText(R.string.try_again_button), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.SpeechWrapper.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        InputView hostView2 = SpeechWrapper.this.getHostView();
                        if (hostView2 != null) {
                            hostView2.mIme.hideDictationLanguageMenu();
                        }
                        SpeechWrapper.this.postRetrySpeechMsg();
                    }
                });
                this.mAlertMessageDialog.show();
            }
        }
    }

    private AlertDialog createAlertDialog(CharSequence title, CharSequence msg) {
        WindowManager.LayoutParams lp;
        InputView hostView = getHostView();
        if (hostView == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(hostView.getContext().getApplicationContext());
        builder.setCancelable(true);
        int iconResId = this.speechProvider == 0 ? R.drawable.speech_dragon_grey : R.drawable.sym_keyboard_speech_mic_white;
        builder.setIcon(iconResId);
        builder.setTitle(getHostView().getContext().getResources().getString(R.string.ime_name));
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        Window window = alertDialog.getWindow();
        if (window != null && (lp = window.getAttributes()) != null) {
            lp.token = hostView.getWindowToken();
            if (lp.token == null) {
                return null;
            }
            lp.type = 1003;
            window.setAttributes(lp);
            window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
            return alertDialog;
        }
        return null;
    }

    void postRetrySpeechMsg() {
        this.mHandler.removeMessages(MSG_RETRY_SPEECH);
        this.mHandler.sendEmptyMessageDelayed(MSG_RETRY_SPEECH, 5L);
    }

    private void removeAllPopupSpeechViewMsg() {
        this.mHandler.removeMessages(1500);
        this.mHandler.removeMessages(MSG_RESUME_SPEECH_POPUP_VIEW);
        this.mHandler.removeMessages(MSG_RETRY_SPEECH);
        this.mHandler.removeMessages(MSG_START_DICTATION);
        this.mHandler.removeMessages(MSG_DELAY_UPATE_SPEECH_RESULT);
    }

    private boolean viewStillActive() {
        InputView hostView = getHostView();
        return (hostView == null || hostView.mIme == null || hostView.mIme.getCurrentInputConnection() == null) ? false : true;
    }

    private void updateSpeechTextResult(Dictation.SpeechResult result) {
        InputView hostView = getHostView();
        if (viewStillActive()) {
            if (result.streaming) {
                this.mHandler.removeMessages(MSG_DELAY_UPATE_SPEECH_RESULT);
                long currentTime = System.currentTimeMillis();
                if (!result.finalResult) {
                    long msSinceLast = currentTime - this.lastSpeechResultUpdate;
                    if (msSinceLast >= 500) {
                        log.d("#updateSpeechTextResult: streaming ", Long.valueOf(msSinceLast), "ms >=", 500L);
                        this.lastSpeechResultUpdate = currentTime;
                    } else {
                        log.d("#updateSpeechTextResult: streaming ", Long.valueOf(msSinceLast), "ms < ", 500L);
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_DELAY_UPATE_SPEECH_RESULT, result), 500 - msSinceLast);
                        return;
                    }
                }
            } else if (result.finalResult && this.mSpeechView != null) {
                this.mSpeechView.postSwitchSpeechPausedViewMsg();
            }
            hostView.onUpdateSpeechText(result.text, result.streaming, result.finalResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDelaySpeechResult(Dictation.SpeechResult result) {
        if (viewStillActive()) {
            log.d("#updateDelaySpeechResult() - update result because of pausing fro ", Long.valueOf(System.currentTimeMillis() - this.lastSpeechResultUpdate));
            getHostView().onUpdateSpeechText(result.text, result.streaming, result.finalResult);
        }
    }

    private void updateDicationStatus(RecognitionStatus status) {
        if (!this.mHandler.hasMessages(1500)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1500, status), 5L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDictation() {
        if (this.mDictation == null) {
            log.e("startDictation() == null");
            updateDicationStatus(RecognitionStatus.Failed);
            return;
        }
        if (this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped && this.mDictation.getDictationState() != Dictation.DictationState.Dictation_Inprogress) {
            if (!this.mDictation.startRecording()) {
                log.e("startDictation().start() failed");
                updateDicationStatus(RecognitionStatus.Failed);
                return;
            } else {
                if (this.mSpeechView != null) {
                    this.mSpeechView.switchToReadyForSpeechView();
                    return;
                }
                return;
            }
        }
        this.mHandler.removeMessages(MSG_START_DICTATION);
        this.mHandler.sendEmptyMessageDelayed(MSG_START_DICTATION, 5L);
    }

    public void restartDictation() {
        if (this.mDictation == null) {
            updateDicationStatus(RecognitionStatus.Failed);
            return;
        }
        log.d("restartDictation.. mDictation.getRecordingState().." + this.mDictation.getRecordingState());
        log.d("restartDictation.. mDictation.getDictationState().." + this.mDictation.getDictationState());
        if (this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped && this.mDictation.getDictationState() != Dictation.DictationState.Dictation_Inprogress) {
            this.mDictation.cancel();
            this.mHandler.sendEmptyMessageDelayed(MSG_START_DICTATION, 5L);
        }
        this.mDictation.canelCustomWordSync();
    }

    @Override // com.nuance.speech.DictationEventListener
    public void handleDictationEvent(DictationEventListener.DictationEvent event, Object eventData) {
        this.mHandler.postDelayed(new DictationMessage(this, event, eventData), 5L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DictationMessage implements Runnable {
        final SpeechWrapper callback;
        final Object data;
        final DictationEventListener.DictationEvent event;

        DictationMessage(SpeechWrapper cb, DictationEventListener.DictationEvent e, Object d) {
            this.callback = cb;
            this.event = e;
            this.data = d;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.callback.handleDictationMsg(this);
        }
    }

    void handleDictationMsg(DictationMessage msg) {
        log.d("handleDictationMsg...msg.event.." + msg.event.toString());
        switch (msg.event) {
            case Connection_Failed:
                updateDicationStatus(RecognitionStatus.Failed_Connection);
                return;
            case Dictation_Started:
                if (this.mSpeechView != null) {
                    this.mSpeechView.postSwitchSpeechProcessingViewMsg();
                    return;
                }
                return;
            case Dictation_Stopped:
                if (this.mSpeechView != null) {
                    this.mSpeechView.postSwitchSpeechPausedViewMsg();
                    return;
                }
                return;
            case Dictation_UpdateResult:
                updateSpeechTextResult((Dictation.SpeechResult) msg.data);
                return;
            case Dictation_Failed:
                if (this.mSpeechView != null) {
                    this.mSpeechView.postSwitchSpeechPausedViewMsg();
                    return;
                }
                return;
            case Dictation_Canceled:
                updateDicationStatus(RecognitionStatus.Canceled);
                return;
            case Speech_Started:
                this.lastSpeechResultUpdate = 0L;
                if (this.mSpeechView != null) {
                    this.mSpeechView.startSpeech();
                    return;
                }
                return;
            case Speech_AudioLevel:
                if (this.mDictation != null) {
                    log.d("Speech_AudioLevel.. mDictation.getRecordingState().." + this.mDictation.getRecordingState());
                }
                if (this.mDictation != null && this.mSpeechView != null && this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
                    int level = ((Integer) msg.data).intValue();
                    this.mSpeechView.setFrame(level);
                    return;
                }
                return;
            case Speech_Language_Change:
                InputView hostView = getHostView();
                if (hostView != null) {
                    hostView.mIme.showDictationLanguageMenu();
                    return;
                }
                return;
            case Speech_Session_Ended:
                hidePopupSpeech();
                this.mState = State.Stopped;
                return;
            default:
                return;
        }
    }

    public void removeCustomWord(UserDictionaryIterator iterator, InputFieldInfo info, String word) {
        CustomWordSynchronizer cws = getCustomWordSynchronizer(iterator);
        if (cws != null) {
            cws.removeCustomWord(info, word);
        }
    }

    public void addCustomWord(UserDictionaryIterator iterator, InputFieldInfo info, String word) {
        CustomWordSynchronizer cws = getCustomWordSynchronizer(iterator);
        if (cws != null) {
            cws.addCustomWord(info, word);
        }
    }

    public void clearAllCustomWords(UserDictionaryIterator iterator) {
        CustomWordSynchronizer cws = getCustomWordSynchronizer(iterator);
        if (cws != null) {
            cws.clearAllCustomWords();
        }
    }

    public void removeCustomWords(UserDictionaryIterator iterator, Set<String> words) {
        CustomWordSynchronizer cws = getCustomWordSynchronizer(iterator);
        if (cws != null) {
            cws.removeCustomWords(words);
        }
    }

    private CustomWordSynchronizer getCustomWordSynchronizer(UserDictionaryIterator iterator) {
        Dictation dictation = getDictation();
        if (dictation != null) {
            return dictation.getCustomWordsSync(iterator);
        }
        return null;
    }

    private SpeechResultTextBuffer createSpeechResultTextBuffer() {
        return createSpeechResultTextBuffer(this.speechProvider);
    }

    private Dictation createDictation(Context context, String langName, boolean isExploredByTouch) {
        return createDictation(this.speechProvider, context, langName, isExploredByTouch);
    }

    protected static SpeechResultTextBuffer createSpeechResultTextBuffer(int speechProvider) {
        if (speechProvider != 0) {
            return null;
        }
        return new DragonSpeechResultsTextBuffer();
    }

    protected static Dictation createDictation(int speechProvider, Context context, String langName, boolean isExploredByTouch) {
        if (speechProvider != 0 || langName == null) {
            return null;
        }
        if (!IMEApplication.from(context).getSpeechConfig().isLanguageSupported(langName)) {
            log.e(langName + " is not a valid language");
            return null;
        }
        return new DragonDictation(context, langName, isExploredByTouch);
    }

    public boolean isSpeechViewShowing() {
        return this.mSpeechView != null && this.mSpeechView.isShowing();
    }

    public boolean isAllowedShowingLanguageMenu() {
        return this.mAlertMessageDialog == null || !this.mAlertMessageDialog.isShowing();
    }

    public boolean isStreamingDictation() {
        return isSpeechViewShowing() && this.mDictation != null && this.mDictation.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS;
    }
}
