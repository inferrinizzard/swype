package com.nuance.speech;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.speech.Dictation;
import com.nuance.speech.DictationEventListener;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.accessibility.AccessibilityScrubGestureView;
import com.nuance.swype.input.accessibility.IGestureViewListener;
import com.nuance.swype.util.LogManager;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class SpeechPopupView implements IGestureViewListener {
    static final int MSG_CANCEL_DICTATION = 2;
    static final int MSG_STOP_RECORDING = 1;
    static final int MSG_SWITCH_TO_PAUSED_VIEW = 3;
    static final int MSG_SWITCH_TO_PROCESSING_VIEW = 4;
    static final int MSG_SWITCH_VIEWS = 5;
    private static final int SPECTRUM_FRAMES_COUNT = 6;
    private static final String SPEECH_LOCK_TAG = "DRAGON_SPEECH_LOCK";
    private Button mChooseLanguageButton;
    private Dictation mDictation;
    private Button mDictationLabelButton;
    private View mDictationView;
    private int mGravity;
    private int mHeight;
    private ImageButton mKeyboardButton;
    private PopupWindow mPopupWindow;
    private Button mProcessingLabelButton;
    private ImageView mSpeechSpectrumView;
    private PowerManager.WakeLock mWakeLock;
    private int mWidth;
    private int mX;
    private int mY;
    private static final LogManager.Log log = LogManager.getLog("SpeechPopup");

    @SuppressLint({"PrivateResource"})
    private static final int[] SPECTRUM_FRAMES_RESIDS = {R.drawable.speech_vis01, R.drawable.speech_vis02, R.drawable.speech_vis03, R.drawable.speech_vis04, R.drawable.speech_vis05, R.drawable.speech_vis06};
    protected PopupViewState mPopupViewState = PopupViewState.PopupView_Connecting;
    private InputView mParentView = null;
    private StringBuilder dottingLine = new StringBuilder();
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.speech.SpeechPopupView.9
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SpeechPopupView.this.stopRecording();
                    return true;
                case 2:
                    SpeechPopupView.this.cancelDictation();
                    return true;
                case 3:
                    SpeechPopupView.this.switchToSpeechPausedView();
                    return true;
                case 4:
                    SpeechPopupView.this.switchToSpeechProcessingView();
                    return true;
                case 5:
                    SpeechPopupView.this.onDictationViewClicked();
                    return true;
                default:
                    return false;
            }
        }
    };
    final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);

    /* loaded from: classes.dex */
    public enum PopupViewState {
        PopupView_Connecting,
        PopupView_Ready,
        PopupView_Listening,
        PopupView_Processing,
        PopupView_Paused
    }

    public void setParentView(InputView parentView) {
        this.mParentView = parentView;
    }

    @TargetApi(14)
    private void createViews(int x, int y, int width, int height) {
        log.d("createViews...width..", Integer.valueOf(width), "..height..", Integer.valueOf(height));
        this.mX = x;
        this.mY = y;
        this.mWidth = width;
        this.mHeight = height;
        Context context = this.mParentView.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.mDictationView = inflater.inflate(R.layout.speech_recording, (ViewGroup) null);
        this.mDictationView.setClickable(true);
        this.mDictationView.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.speech.SpeechPopupView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SpeechPopupView.this.postDelaySwitchingViewsMsg();
            }
        });
        this.mSpeechSpectrumView = (ImageView) this.mDictationView.findViewById(R.id.spectrum_recording);
        this.mKeyboardButton = (ImageButton) this.mDictationView.findViewById(R.id.back_to_keyboard);
        this.mKeyboardButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.speech.SpeechPopupView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SpeechPopupView.this.onBackToKeyboardButtonClick();
            }
        });
        this.mDictationLabelButton = (Button) this.mDictationView.findViewById(R.id.recording_label);
        this.mDictationLabelButton.setClickable(false);
        this.mDictationLabelButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.speech.SpeechPopupView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (SpeechPopupView.this.mPopupViewState == PopupViewState.PopupView_Listening) {
                    SpeechPopupView.this.onTapToPauseButtonClicked();
                } else if (SpeechPopupView.this.mPopupViewState == PopupViewState.PopupView_Paused) {
                    SpeechPopupView.this.onTapToSpeakButtonClicked();
                } else if (SpeechPopupView.this.mPopupViewState == PopupViewState.PopupView_Processing) {
                    SpeechPopupView.this.onTapToSpeakButtonClicked();
                }
            }
        });
        this.mProcessingLabelButton = (Button) this.mDictationView.findViewById(R.id.processing_label);
        this.mProcessingLabelButton.setClickable(false);
        this.mChooseLanguageButton = (Button) this.mDictationView.findViewById(R.id.choose_language);
        this.mChooseLanguageButton.setText(getLanguageDisplayName());
        this.mChooseLanguageButton.setClickable(true);
        this.mChooseLanguageButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.speech.SpeechPopupView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SpeechPopupView.this.showDictationLanguages();
                SpeechPopupView.this.onTapToPauseButtonClicked();
            }
        });
        if (this.mPopupWindow != null) {
            this.mPopupWindow.dismiss();
            this.mPopupWindow = null;
        }
        this.mPopupWindow = new PopupWindow(this.mDictationView, this.mWidth, this.mHeight);
        this.mPopupWindow.update(this.mX, this.mY, this.mWidth, this.mHeight);
        if (this.mParentView != null && this.mParentView.getWindowToken() != null) {
            this.mPopupWindow.showAtLocation(this.mParentView, this.mGravity, this.mX, this.mY);
            this.mPopupWindow.setOnDismissListener(new PopupDismissListener(context));
        }
        IME ime = getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled()) {
            this.mDictationView.setOnHoverListener(new View.OnHoverListener() { // from class: com.nuance.speech.SpeechPopupView.5
                @Override // android.view.View.OnHoverListener
                public boolean onHover(View v, MotionEvent event) {
                    return SpeechPopupView.this.onHoverEvent(event);
                }
            });
        }
        switchToConnectingView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PopupDismissListener implements AudioManager.OnAudioFocusChangeListener, PopupWindow.OnDismissListener {
        private WeakReference<AudioManager> audioManagerRef;
        private WeakReference<AccessibilityManager> mgrRef;

        public PopupDismissListener(Context context) {
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            this.audioManagerRef = new WeakReference<>(audioManager);
            int result = audioManager.requestAudioFocus(this, 3, 2);
            SpeechPopupView.log.d(toString(), "..request audio focus result.." + result);
            AccessibilityManager mgr = (AccessibilityManager) context.getSystemService("accessibility");
            if (mgr != null) {
                this.mgrRef = new WeakReference<>(mgr);
            }
            if (mgr == null || !mgr.isEnabled()) {
                audioManager.setStreamMute(3, true);
            }
        }

        @Override // android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            AudioManager audioManager = this.audioManagerRef.get();
            if (audioManager != null) {
                AccessibilityManager mgr = null;
                if (this.mgrRef != null) {
                    AccessibilityManager mgr2 = this.mgrRef.get();
                    mgr = mgr2;
                }
                if (mgr == null || !mgr.isEnabled()) {
                    audioManager.setStreamMute(3, false);
                }
                audioManager.abandonAudioFocus(this);
                this.audioManagerRef.clear();
            }
        }

        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int arg0) {
        }
    }

    String getLanguageDisplayName() {
        return IMEApplication.from(this.mParentView.getContext()).getSpeechConfig().getLanguageAbbrDisplayName(this.mDictation.getCurrentLanguageName());
    }

    void onBackToKeyboardButtonClick() {
        clearAreaAboveTheKeyboard();
        if (this.mDictation != null) {
            if ((this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress || this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Inprogress) && !this.mHandler.hasMessages(2)) {
                this.mHandler.sendEmptyMessageDelayed(2, 5L);
            }
            this.mDictation.dispatchDictationEvent(DictationEventListener.DictationEvent.Speech_Session_Ended, null);
        }
    }

    void onTapToSpeakButtonClicked() {
        if (this.mDictation != null) {
            log.d("onTapToSpeakButtonClicked..mPopupViewState: " + this.mPopupViewState.toString());
            if (this.mHandler.hasMessages(2)) {
                if (this.mPopupViewState == PopupViewState.PopupView_Processing) {
                    postDelaySwitchingViewsMsg();
                    return;
                }
                return;
            }
            log.d("onTapToSpeakButtonClicked..mDictation.getDictationState(): " + this.mDictation.getDictationState().toString());
            if (this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Inprogress && !this.mHandler.hasMessages(2)) {
                this.mHandler.sendEmptyMessageDelayed(2, 5L);
                if (this.mPopupViewState == PopupViewState.PopupView_Processing) {
                    postDelaySwitchingViewsMsg();
                    return;
                }
                return;
            }
            IMEApplication.from(this.mParentView.getContext()).getSpeechWrapper().restartDictation();
        }
    }

    void onTapToPauseButtonClicked() {
        if (this.mDictation != null && this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 15L);
        }
    }

    void onDictationViewClicked() {
        log.d("onDictationViewClicked..");
        if (this.mPopupViewState == PopupViewState.PopupView_Listening) {
            onTapToPauseButtonClicked();
            return;
        }
        if (this.mPopupViewState == PopupViewState.PopupView_Processing) {
            onTapToSpeakButtonClicked();
        } else if (this.mPopupViewState == PopupViewState.PopupView_Paused) {
            onTapToSpeakButtonClicked();
        } else if (this.mPopupViewState == PopupViewState.PopupView_Ready) {
            onTapToPauseButtonClicked();
        }
    }

    void showDictationLanguages() {
        if (this.mDictation != null) {
            this.mDictation.dispatchDictationEvent(DictationEventListener.DictationEvent.Speech_Language_Change, null);
        }
    }

    void stopRecording() {
        clearSpectrum();
        if (this.mDictation != null && this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
            this.mDictation.stopRecording();
        } else {
            log.w("stopRecording() already stopped");
        }
    }

    void cancelDictation() {
        if (this.mDictation != null) {
            this.mDictation.cancel();
        }
    }

    public boolean show(int gravity, int x, int y, int width, int height) {
        if (this.mParentView == null) {
            return false;
        }
        this.mGravity = gravity;
        PowerManager pm = (PowerManager) this.mParentView.getContext().getSystemService("power");
        this.mWakeLock = pm.newWakeLock(805306394, SPEECH_LOCK_TAG);
        log.d("show...mWakeLock created...");
        createViews(x, y, width, height);
        return true;
    }

    public boolean isShowing() {
        return this.mPopupWindow != null && this.mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (isShowing()) {
            this.mPopupWindow.dismiss();
            if (this.mWakeLock != null && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
                log.d("dismiss... mWakeLock is released and set to null...");
                this.mWakeLock = null;
            }
        }
    }

    public void setDictationEngine(Dictation dictation) {
        this.mDictation = dictation;
    }

    public void refreshLanguageLabel() {
        if (this.mDictationView != null && this.mDictationView.isShown()) {
            this.mChooseLanguageButton.setText(getLanguageDisplayName());
        }
    }

    private void clearSpectrum() {
        setFrame(0);
    }

    public void setFrame(int index) {
        log.d("setFrame...index.." + index);
        if (index >= 0 && index < 6 && isShowing() && getPopupViewState() == PopupViewState.PopupView_Listening) {
            Context context = this.mParentView.getContext();
            this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(SPECTRUM_FRAMES_RESIDS[index]));
            this.mSpeechSpectrumView.invalidate();
        }
    }

    public void postSwitchSpeechPausedViewMsg() {
        this.mHandler.removeMessages(3);
        this.mHandler.sendEmptyMessageDelayed(3, 10L);
    }

    public void postSwitchSpeechProcessingViewMsg() {
        this.mHandler.removeMessages(4);
        this.mHandler.sendEmptyMessageDelayed(4, 10L);
    }

    public void postDelaySwitchingViewsMsg() {
        this.mHandler.removeMessages(5);
        this.mHandler.sendEmptyMessageDelayed(5, 10L);
    }

    public void startSpeech() {
        if (this.mDictationView != null && this.mDictationView.isShown()) {
            this.mDictationLabelButton.setClickable(true);
            switchToListeningView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToSpeechPausedView() {
        if (this.mDictation != null) {
            log.d("switchToSpeechPausedView");
            setPopupViewState(PopupViewState.PopupView_Paused);
            updateView(PopupViewState.PopupView_Paused);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToSpeechProcessingView() {
        if (this.mDictation != null) {
            log.d("switchToSpeechProcessingView");
            if (this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Inprogress) {
                log.d("switchToSpeechProcessingView");
                setPopupViewState(PopupViewState.PopupView_Processing);
                if (this.mDictation.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS) {
                    updateView(PopupViewState.PopupView_Processing);
                    return;
                }
                if (this.dottingLine.length() >= 5) {
                    this.dottingLine.setLength(0);
                }
                this.dottingLine.append('.');
                updateView(PopupViewState.PopupView_Processing);
                final Handler handler = new Handler();
                new Runnable() { // from class: com.nuance.speech.SpeechPopupView.6
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SpeechPopupView.this.mDictation != null && SpeechPopupView.this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped && SpeechPopupView.this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Inprogress) {
                            handler.postDelayed(this, 200L);
                            if (SpeechPopupView.this.dottingLine.length() >= 5) {
                                SpeechPopupView.this.dottingLine.setLength(0);
                            }
                            SpeechPopupView.this.dottingLine.append('.');
                            SpeechPopupView.this.updateView(PopupViewState.PopupView_Processing);
                        }
                    }
                }.run();
                return;
            }
            if (this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Stopped) {
                switchToSpeechPausedView();
            }
        }
    }

    private void switchToConnectingView() {
        if (this.mDictation != null) {
            log.d("switchToConnectingView...getRecordingState().." + this.mDictation.getRecordingState());
            if (this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped) {
                log.d("switchToConnectingView");
                setPopupViewState(PopupViewState.PopupView_Connecting);
                this.dottingLine.setLength(0);
                this.dottingLine.append('.');
                updateView(PopupViewState.PopupView_Connecting);
                final Handler handler = new Handler();
                new Runnable() { // from class: com.nuance.speech.SpeechPopupView.7
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SpeechPopupView.this.mDictation != null && SpeechPopupView.this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped && SpeechPopupView.this.mDictation.getDictationState() == Dictation.DictationState.Dictation_Stopped) {
                            handler.postDelayed(this, 50L);
                            if (SpeechPopupView.this.dottingLine.length() > 20) {
                                SpeechPopupView.this.dottingLine.setLength(0);
                            }
                            SpeechPopupView.this.dottingLine.append('.');
                            SpeechPopupView.this.updateView(PopupViewState.PopupView_Connecting);
                        }
                    }
                }.run();
                return;
            }
            if (this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
                switchToListeningView();
            }
        }
    }

    public void switchToReadyForSpeechView() {
        if (this.mDictation != null && this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Stopped) {
            log.d("switchToReadyForSpeechView");
            setPopupViewState(PopupViewState.PopupView_Ready);
            updateView(PopupViewState.PopupView_Ready);
        }
    }

    private void switchToListeningView() {
        if (this.mDictation != null) {
            log.d("switchToListeningView");
            setPopupViewState(PopupViewState.PopupView_Listening);
            if (this.mDictation.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS) {
                updateView(PopupViewState.PopupView_Listening);
                return;
            }
            this.dottingLine.setLength(0);
            this.dottingLine.append('.');
            updateView(PopupViewState.PopupView_Listening);
            final Handler handler = new Handler();
            new Runnable() { // from class: com.nuance.speech.SpeechPopupView.8
                @Override // java.lang.Runnable
                public void run() {
                    if (SpeechPopupView.this.mDictation != null && SpeechPopupView.this.mDictation.getRecordingState() == Dictation.RecordingState.Recording_Inprogress) {
                        handler.postDelayed(this, 200L);
                        if (SpeechPopupView.this.dottingLine.length() >= 5) {
                            SpeechPopupView.this.dottingLine.setLength(0);
                        }
                        SpeechPopupView.this.dottingLine.append('.');
                        SpeechPopupView.this.updateView(PopupViewState.PopupView_Listening);
                    }
                }
            }.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"PrivateResource"})
    public void updateView(PopupViewState state) {
        if (this.mParentView != null) {
            Context context = this.mParentView.getContext();
            LogManager.Log log2 = log;
            Object[] objArr = new Object[1];
            objArr[0] = "updateView...state..." + state + "mWakeLock is not null ? .." + (this.mWakeLock != null);
            log2.d(objArr);
            if (state != PopupViewState.PopupView_Listening && this.mWakeLock != null && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
                log.d("mWakeLock is released...");
            }
            if (state == PopupViewState.PopupView_Listening) {
                this.mDictationLabelButton.setText(R.string.voice_pause);
                if (this.mDictation == null || this.mDictation.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS) {
                    this.mProcessingLabelButton.setText(XMLResultsHandler.SEP_SPACE);
                } else {
                    this.mProcessingLabelButton.setText(this.dottingLine);
                }
                this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(R.drawable.speech_ready));
                coverAreaAboveTheKeyboard();
                if (this.mWakeLock != null && !this.mWakeLock.isHeld()) {
                    this.mWakeLock.acquire();
                    log.d("mWakeLock is acquired...");
                }
            } else if (state == PopupViewState.PopupView_Processing) {
                this.mDictationLabelButton.setText(R.string.voice_speak);
                if (this.mDictation == null || this.mDictation.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS) {
                    this.mProcessingLabelButton.setText(XMLResultsHandler.SEP_SPACE);
                } else {
                    this.mProcessingLabelButton.setText(this.dottingLine);
                }
                this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(R.drawable.speech_paused));
                ((BitmapDrawable) ((LayerDrawable) this.mSpeechSpectrumView.getDrawable()).getDrawable(1)).setAlpha(128);
                clearAreaAboveTheKeyboard();
            } else if (state == PopupViewState.PopupView_Paused) {
                this.mDictationLabelButton.setText(R.string.voice_speak);
                this.mProcessingLabelButton.setText(XMLResultsHandler.SEP_SPACE);
                this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(R.drawable.speech_paused));
                ((BitmapDrawable) ((LayerDrawable) this.mSpeechSpectrumView.getDrawable()).getDrawable(1)).setAlpha(128);
                clearAreaAboveTheKeyboard();
            } else if (state == PopupViewState.PopupView_Ready) {
                this.mDictationLabelButton.setText(R.string.voice_speak_now);
                this.mProcessingLabelButton.setText(XMLResultsHandler.SEP_SPACE);
                this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(R.drawable.speech_ready));
            } else {
                this.mDictationLabelButton.setText(this.dottingLine);
                this.mProcessingLabelButton.setText(XMLResultsHandler.SEP_SPACE);
                this.mSpeechSpectrumView.setImageDrawable(context.getResources().getDrawable(R.drawable.speech_connecting));
                ((BitmapDrawable) ((LayerDrawable) this.mSpeechSpectrumView.getDrawable()).getDrawable(1)).setAlpha(128);
            }
            this.mDictationLabelButton.invalidate();
            if (this.mProcessingLabelButton.isShown()) {
                this.mProcessingLabelButton.invalidate();
            }
            this.mSpeechSpectrumView.invalidate();
        }
    }

    private void clearAreaAboveTheKeyboard() {
        InputView currentView;
        AccessibilityScrubGestureView scrubGestureView;
        IME ime = getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled() && (currentView = ime.getCurrentInputView()) != null && (scrubGestureView = currentView.getScrubGesturView()) != null) {
            scrubGestureView.removeAllGestureViewListener();
            scrubGestureView.useForVoiceUI(false);
            currentView.cleanupScrubGesture();
        }
    }

    private void coverAreaAboveTheKeyboard() {
        InputView currentView;
        AccessibilityScrubGestureView scrubGestureView;
        IME ime = getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled() && (currentView = ime.getCurrentInputView()) != null && (scrubGestureView = currentView.getScrubGesturView()) != null) {
            scrubGestureView.addGestureViewListener(this);
            scrubGestureView.useForVoiceUI(true);
            currentView.showScrubGestureView();
        }
    }

    private IME getIME() {
        IMEApplication app;
        Context context = this.mParentView.getContext();
        if (context == null || (app = IMEApplication.from(context)) == null) {
            return null;
        }
        IME ime = app.getIME();
        return ime;
    }

    public PopupViewState getPopupViewState() {
        return this.mPopupViewState;
    }

    public void setPopupViewState(PopupViewState state) {
        this.mPopupViewState = state;
    }

    public PopupWindow getPopupWindow() {
        return this.mPopupWindow;
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (this.mDictation != null && this.mDictation.getRecordingState() != Dictation.RecordingState.Recording_Stopped) {
            onTapToPauseButtonClicked();
            return true;
        }
        return true;
    }

    @Override // com.nuance.swype.input.accessibility.IGestureViewListener
    public void onGestureViewHoverEvent(MotionEvent event) {
        onHoverEvent(event);
    }

    public void clearMessages() {
        this.mHandler.removeCallbacksAndMessages(null);
    }
}
