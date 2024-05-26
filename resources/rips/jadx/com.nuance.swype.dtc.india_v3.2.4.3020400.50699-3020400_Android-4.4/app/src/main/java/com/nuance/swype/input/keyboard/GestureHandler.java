package com.nuance.swype.input.keyboard;

import android.R;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.SparseArray;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.GestureManager;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.util.InputConnectionUtils;
import java.util.List;

/* loaded from: classes.dex */
public class GestureHandler {
    private final XT9CoreInput coreInput;
    private final SparseArray<XT9CoreInput.Gesture> gestureTable = new SparseArray<>();
    private final IME ime;
    private final InputView inputView;

    public GestureHandler(IME ime, InputView inputView, XT9CoreInput coreInput, GestureManager gestureManager) {
        this.ime = ime;
        this.inputView = inputView;
        this.coreInput = coreInput;
        if (gestureManager.loadGestures()) {
            List<XT9CoreInput.Gesture> gestures = gestureManager.getGestures();
            coreInput.loadGestures(gestures);
            for (XT9CoreInput.Gesture gesture : gestures) {
                this.gestureTable.put(gesture.returnValue, gesture);
            }
        }
    }

    public boolean isGesture(WordCandidate candidate) {
        return candidate.source() == WordCandidate.Source.WORD_SOURCE_GESTURE;
    }

    public boolean isCustomGesture(WordCandidate candidate) {
        if (isGesture(candidate)) {
            CharSequence gestureStr = candidate.toString();
            if (gestureStr.length() == 1) {
                return this.gestureTable.get((short) gestureStr.charAt(0)) != null;
            }
        }
        return false;
    }

    private void handleCustomGesture(int gestureCode) {
        InputView localInputView = this.inputView;
        IME localIME = this.ime;
        switch (gestureCode) {
            case KeyboardEx.GESTURE_KEYCODE_SEARCH /* -112 */:
                String term = localIME.getAppSpecificInputConnection() == null ? null : localIME.getAppSpecificInputConnection().getSelectedTextInEditor(localIME.getInputFieldInfo());
                if (!TextUtils.isEmpty(term)) {
                    Intent intent = new Intent("android.intent.action.WEB_SEARCH");
                    intent.addFlags(268468224);
                    intent.putExtra("query", term);
                    ActivityInfo info = intent.resolveActivityInfo(localIME.getPackageManager(), intent.getFlags());
                    if (info != null && info.exported) {
                        localIME.startActivity(intent);
                        break;
                    }
                }
                break;
            case KeyboardEx.GESTURE_KEYCODE_LAST_LANGUAGE /* -111 */:
                localIME.onKey(null, gestureCode, new int[0], null, 0L);
                break;
            case KeyboardEx.GESTURE_KEYCODE_CASE_EDIT /* -110 */:
                if (localIME.getRecaptureHandler() != null) {
                    localIME.getRecaptureHandler().onKey(gestureCode, 1);
                    break;
                }
                break;
            case KeyboardEx.GESTURE_KEYCODE_WWW /* -109 */:
                localInputView.onText("www.", 0L);
                break;
            case KeyboardEx.GESTURE_KEYCODE_HIDE_KEYBOARD /* -108 */:
                localIME.hideWindow();
                break;
            case KeyboardEx.GESTURE_KEYCODE_GOOGLE_MAPS /* -107 */:
                localIME.handleGoogleMapGesture();
                break;
            case KeyboardEx.GESTURE_KEYCODE_SUPPRESS_AUTOSPACE /* -106 */:
                localInputView.handleKey(gestureCode, false, 1);
                break;
            case KeyboardEx.KEYCODE_SELECT_ALL /* 2896 */:
                localIME.setUseEditLayer(true);
                if (localIME.getAppSpecificInputConnection() != null) {
                    InputConnectionUtils.selectAll(localIME.getAppSpecificInputConnection());
                    break;
                }
                break;
            case KeyboardEx.KEYCODE_CUT /* 2897 */:
                localIME.setUseEditLayer(true);
                if (!localIME.getInputFieldInfo().isPasswordField() && localIME.getAppSpecificInputConnection() != null && localIME.getAppSpecificInputConnection().hasSelection()) {
                    localIME.getAppSpecificInputConnection().performContextMenuAction(R.id.cut);
                    localInputView.clearCurrentActiveWord();
                    break;
                }
                break;
            case KeyboardEx.KEYCODE_COPY /* 2898 */:
                localIME.setUseEditLayer(true);
                if (!localIME.getInputFieldInfo().isPasswordField() && localIME.getAppSpecificInputConnection() != null && localIME.getAppSpecificInputConnection().hasSelection()) {
                    localIME.getAppSpecificInputConnection().performContextMenuAction(R.id.copy);
                    break;
                }
                break;
            case KeyboardEx.KEYCODE_PASTE /* 2899 */:
                if (localIME.getCurrentInputConnection().performContextMenuAction(R.id.paste)) {
                    localInputView.setLastInput(5);
                    break;
                }
                break;
            case KeyboardEx.KEYCODE_SYM_LAYER /* 4077 */:
            case KeyboardEx.KEYCODE_ABC_LAYER /* 4078 */:
            case KeyboardEx.KEYCODE_NUM /* 4085 */:
            case KeyboardEx.KEYCODE_EDIT_LAYER /* 6444 */:
                localIME.setRecaptureWhenSwitching(true);
                localIME.onKey(null, gestureCode, new int[0], null, 0L);
                break;
        }
        if (gestureCode != -110 && gestureCode != -111 && localInputView.isEmptyCandidateList() && !localInputView.isHandWritingInputView()) {
            localInputView.showNextWordPrediction();
        }
    }

    private void handlePunctGesture(WordCandidate candidate) {
        InputView localInputView = this.inputView;
        this.ime.getCurrentInputConnection().commitText(candidate.word(), 1);
        localInputView.clearSuggestions();
        localInputView.showNextWordPrediction();
    }

    public boolean handle(int gestureCode) {
        if (this.gestureTable.get((short) gestureCode) == null) {
            return false;
        }
        handleCustomGesture(gestureCode);
        return true;
    }

    public boolean handle(WordCandidate candidate) {
        if (!isGesture(candidate)) {
            return false;
        }
        if (isCustomGesture(candidate)) {
            this.inputView.clearSuggestions();
            handleCustomGesture((short) candidate.toString().charAt(0));
        } else {
            handlePunctGesture(candidate);
        }
        return true;
    }
}
