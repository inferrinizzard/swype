package com.nuance.dlm;

import android.os.Handler;
import android.os.Message;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACDLMConnector;
import com.nuance.swypeconnect.ac.ACException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class ACAlphaInput implements ACDLMConnector.ACAlphaDlmDb, ACDLMConnector.ACDlmEventCallbackObserver {
    private static final int SEND_EVENTS = 1;
    private static final int SEND_EVENTS_INTERVAL = 10000;
    private static final LogManager.Log log = LogManager.getLog("ACAlphaInput");
    private ACDLMConnector.ACAlphaDlmEventCallback callback;
    private ACDLMConnector connector;
    private InputHandler handler = new InputHandler(this);

    private native int acAlphaDeleteCategory(int i);

    private native int acAlphaDeleteCategoryLanguage(int i, int i2);

    private native int acAlphaExportAsEvent(boolean z, int i);

    private native byte[] acAlphaGetCachedEvents();

    public static native String acAlphaLegacySecretKey();

    private native int acAlphaProcessEvent(byte[] bArr);

    private native int acAlphaRegisterEventHandlerCallback();

    private native int acAlphaScanBuffer(char[] cArr, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2);

    private native int acAlphaScanSessionBegin(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2);

    private native int acAlphaScanSessionEnd();

    /* loaded from: classes.dex */
    private static class InputHandler extends Handler {
        private final WeakReference<ACAlphaInput> input;

        public InputHandler(ACAlphaInput input) {
            this.input = new WeakReference<>(input);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            ACAlphaInput i = this.input.get();
            if (i != null) {
                switch (msg.what) {
                    case 1:
                        i.sendEvents();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public ACAlphaInput(ACDLMConnector connector) {
        this.connector = connector;
        try {
            this.callback = this.connector.bindAlphaDlm(this);
            if (this.callback != null) {
                this.callback.registerObserver(this);
            }
        } catch (ACException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        acAlphaRegisterEventHandlerCallback();
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallbackObserver
    public void onYield() {
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallbackObserver
    public void onResume() {
        log.d("onResume");
        sendEvents();
        this.handler.removeMessages(1);
    }

    public void release() {
        sendEvents();
        if (this.callback != null) {
            this.callback.unregisterObserver(this);
        }
        this.connector.releaseAlphaDlm();
    }

    private void onEventCallback(byte[] event, boolean highPriority) {
        log.d("onEventCallback");
        if (this.callback != null) {
            this.callback.onAlphaDlmEvent(event, highPriority);
        }
    }

    private void onEventCacheFull() {
        log.d("onEventCacheFull");
        sendEvents();
        this.handler.removeMessages(1);
    }

    private void onFirstCachedEvent() {
        log.d("onFirstCachedEvent");
        this.handler.removeMessages(1);
        this.handler.sendEmptyMessageDelayed(1, IME.RETRY_DELAY_IN_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEvents() {
        log.d("sendEvents");
        if (this.callback != null) {
            this.callback.onAlphaDlmEvent(acAlphaGetCachedEvents(), false);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void processEvent(byte[] event) {
        acAlphaProcessEvent(event);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void exportAsEvents(boolean usingCategory, int category) {
        acAlphaExportAsEvent(usingCategory, category);
        sendEvents();
        this.handler.removeMessages(1);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void deleteCategory(int category) {
        acAlphaDeleteCategory(category);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void deleteCategoryLanguage(int category, int language) {
        acAlphaDeleteCategoryLanguage(category, language);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public boolean scanBuffer(char[] buffer, int len, int startPos, int endPos, int xt9LanguageId, int wordQuality, boolean sentenceBased, boolean rescanning) {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "scanBuffer buffer=";
        objArr[1] = String.valueOf(buffer);
        objArr[2] = "; wordQuality: ";
        objArr[3] = wordQuality != 0 ? "high" : UserPreferences.TRACE_FILTER_LOW;
        log2.d(objArr);
        return acAlphaScanBuffer(buffer, len, startPos, endPos, xt9LanguageId, wordQuality, sentenceBased, rescanning) == 0;
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void scanBegin(String context1, String context2, String context3, int userAction, int scanAction) {
        byte[] bytes;
        log.d("scanBegin context1=", context1);
        if (context1 != null) {
            try {
                bytes = context1.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.e(e.getMessage());
                return;
            }
        } else {
            bytes = null;
        }
        acAlphaScanSessionBegin(bytes, context2 != null ? context2.getBytes("UTF-8") : null, context3 != null ? context3.getBytes("UTF-8") : null, userAction, scanAction);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
    public void scanEnd() {
        acAlphaScanSessionEnd();
        log.d("scanEnd");
    }

    static int registerEventHandlerCallback() {
        return 0;
    }
}
