package com.google.android.voiceime;

import android.annotation.TargetApi;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
@TargetApi(11)
/* loaded from: classes.dex */
public final class ImeTrigger implements Trigger {
    private final InputMethodService mInputMethodService;

    public ImeTrigger(InputMethodService inputMethodService) {
        this.mInputMethodService = inputMethodService;
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [com.google.android.voiceime.ImeTrigger$1] */
    @Override // com.google.android.voiceime.Trigger
    public final void startVoiceRecognition(String language) {
        final InputMethodSubtype subtype;
        final InputMethodManager inputMethodManager = getInputMethodManager(this.mInputMethodService);
        InputMethodInfo inputMethodInfo = getVoiceImeInputMethodInfo(inputMethodManager);
        if (inputMethodInfo != null) {
            final IBinder token = this.mInputMethodService.getWindow().getWindow().getAttributes().token;
            final String imiId = inputMethodInfo.getId();
            List<InputMethodSubtype> list = inputMethodManager.getShortcutInputMethodsAndSubtypes().get(inputMethodInfo);
            if (list != null && list.size() > 0) {
                subtype = list.get(0);
            } else {
                subtype = null;
            }
            new AsyncTask<Void, Void, Void>() { // from class: com.google.android.voiceime.ImeTrigger.1
                @Override // android.os.AsyncTask
                protected final /* bridge */ /* synthetic */ Void doInBackground(Void[] voidArr) {
                    inputMethodManager.setInputMethodAndSubtype(token, imiId, subtype);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InputMethodManager getInputMethodManager(InputMethodService inputMethodService) {
        return (InputMethodManager) inputMethodService.getSystemService("input_method");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InputMethodInfo getVoiceImeInputMethodInfo(InputMethodManager inputMethodManager) throws SecurityException, IllegalArgumentException {
        for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodList()) {
            for (int i = 0; i < inputMethodInfo.getSubtypeCount(); i++) {
                InputMethodSubtype subtype = inputMethodInfo.getSubtypeAt(i);
                if ("voice".equals(subtype.getMode()) && inputMethodInfo.getComponent().getPackageName().startsWith("com.google.android")) {
                    return inputMethodInfo;
                }
            }
        }
        return null;
    }

    @Override // com.google.android.voiceime.Trigger
    public final void onStartInputView() {
    }

    @Override // com.google.android.voiceime.Trigger
    public final boolean hasRecognitionResultToCommit() {
        return false;
    }

    @Override // com.google.android.voiceime.Trigger
    public final void onDestroy() {
    }
}
