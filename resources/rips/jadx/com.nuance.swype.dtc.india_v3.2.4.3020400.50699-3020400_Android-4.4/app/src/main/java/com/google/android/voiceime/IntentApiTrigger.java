package com.google.android.voiceime;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.google.android.voiceime.ServiceBridge;
import com.google.android.voiceime.ServiceHelper;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class IntentApiTrigger implements Trigger {
    private final Handler mHandler;
    final InputMethodService mInputMethodService;
    String mLastRecognitionResult;
    IBinder mToken;
    private final ServiceBridge mServiceBridge = new ServiceBridge(new Callback() { // from class: com.google.android.voiceime.IntentApiTrigger.1
        @Override // com.google.android.voiceime.IntentApiTrigger.Callback
        public final void onRecognitionResult(String recognitionResult) {
            IntentApiTrigger intentApiTrigger = IntentApiTrigger.this;
            intentApiTrigger.mLastRecognitionResult = recognitionResult;
            ((InputMethodManager) intentApiTrigger.mInputMethodService.getSystemService("input_method")).showSoftInputFromInputMethod(intentApiTrigger.mToken, 1);
        }
    });
    private Set<Character> mUpperCaseChars = new HashSet();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        void onRecognitionResult(String str);
    }

    public IntentApiTrigger(InputMethodService inputMethodService) {
        this.mInputMethodService = inputMethodService;
        this.mUpperCaseChars.add('.');
        this.mUpperCaseChars.add('!');
        this.mUpperCaseChars.add('?');
        this.mUpperCaseChars.add('\n');
        this.mHandler = new Handler();
    }

    @Override // com.google.android.voiceime.Trigger
    public final void startVoiceRecognition(String language) {
        this.mToken = this.mInputMethodService.getWindow().getWindow().getAttributes().token;
        ServiceBridge serviceBridge = this.mServiceBridge;
        InputMethodService inputMethodService = this.mInputMethodService;
        if (serviceBridge.mCurrentRequest != null) {
            inputMethodService.unbindService(serviceBridge.mCurrentRequest);
            serviceBridge.mCurrentRequest = null;
        }
        serviceBridge.mCurrentRequest = new ServiceBridge.ConnectionRequest(serviceBridge, language, (byte) 0);
        serviceBridge.mCurrentRequest.mServiceCallback = new ServiceHelper.Callback() { // from class: com.google.android.voiceime.ServiceBridge.1
            final /* synthetic */ Context val$context;

            public AnonymousClass1(Context inputMethodService2) {
                r2 = inputMethodService2;
            }

            @Override // com.google.android.voiceime.ServiceHelper.Callback
            public final void onResult(String recognitionResult) {
                ServiceBridge.this.mCallback.onRecognitionResult(recognitionResult);
                try {
                    r2.unbindService(ServiceBridge.this.mCurrentRequest);
                    ServiceBridge.this.mCurrentRequest = null;
                } catch (Exception e) {
                }
            }
        };
        inputMethodService2.bindService(new Intent(inputMethodService2, (Class<?>) ServiceHelper.class), serviceBridge.mCurrentRequest, 1);
    }

    @Override // com.google.android.voiceime.Trigger
    public final void onStartInputView() {
        Log.i("VoiceIntentApiTrigger", "#onStartInputView");
        if (this.mLastRecognitionResult == null) {
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.google.android.voiceime.IntentApiTrigger.2
            @Override // java.lang.Runnable
            public final void run() {
                IntentApiTrigger intentApiTrigger = IntentApiTrigger.this;
                if (intentApiTrigger.mLastRecognitionResult != null) {
                    String str = intentApiTrigger.mLastRecognitionResult;
                    InputConnection currentInputConnection = intentApiTrigger.mInputMethodService.getCurrentInputConnection();
                    if (currentInputConnection == null) {
                        Log.i("VoiceIntentApiTrigger", "Unable to commit recognition result, as the current input connection is null. Did someone kill the IME?");
                        return;
                    }
                    if (!currentInputConnection.beginBatchEdit()) {
                        Log.i("VoiceIntentApiTrigger", "Unable to commit recognition result, as a batch edit cannot start");
                        return;
                    }
                    try {
                        ExtractedTextRequest extractedTextRequest = new ExtractedTextRequest();
                        extractedTextRequest.flags = 1;
                        ExtractedText extractedText = currentInputConnection.getExtractedText(extractedTextRequest, 0);
                        if (extractedText == null) {
                            Log.i("VoiceIntentApiTrigger", "Unable to commit recognition result, as extracted text is null");
                            return;
                        }
                        if (extractedText.text != null) {
                            str = intentApiTrigger.format(extractedText, str);
                        }
                        if (!currentInputConnection.commitText(str, 0)) {
                            Log.i("VoiceIntentApiTrigger", "Unable to commit recognition result");
                        } else {
                            intentApiTrigger.mLastRecognitionResult = null;
                        }
                    } finally {
                        currentInputConnection.endBatchEdit();
                    }
                }
            }
        }, 15L);
    }

    final String format(ExtractedText et, String result) {
        int pos = et.selectionStart - 1;
        while (pos > 0 && Character.isWhitespace(et.text.charAt(pos))) {
            pos--;
        }
        if (pos == -1 || this.mUpperCaseChars.contains(Character.valueOf(et.text.charAt(pos)))) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        if (et.selectionStart - 1 > 0 && !Character.isWhitespace(et.text.charAt(et.selectionStart - 1))) {
            result = XMLResultsHandler.SEP_SPACE + result;
        }
        if (et.selectionEnd < et.text.length() && !Character.isWhitespace(et.text.charAt(et.selectionEnd))) {
            return result + XMLResultsHandler.SEP_SPACE;
        }
        return result;
    }

    @Override // com.google.android.voiceime.Trigger
    public final boolean hasRecognitionResultToCommit() {
        return this.mLastRecognitionResult != null;
    }

    @Override // com.google.android.voiceime.Trigger
    public final void onDestroy() {
        if (this.mServiceBridge != null) {
            ServiceBridge serviceBridge = this.mServiceBridge;
            InputMethodService inputMethodService = this.mInputMethodService;
            if (serviceBridge.mCurrentRequest != null && inputMethodService != null) {
                try {
                    inputMethodService.unbindService(serviceBridge.mCurrentRequest);
                    serviceBridge.mCurrentRequest = null;
                } catch (Exception e) {
                }
            }
        }
    }
}
