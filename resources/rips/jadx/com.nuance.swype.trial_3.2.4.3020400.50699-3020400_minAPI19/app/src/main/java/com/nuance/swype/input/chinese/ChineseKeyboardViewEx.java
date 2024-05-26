package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.util.ContactUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class ChineseKeyboardViewEx extends KeyboardViewEx {
    private InputView inputView;
    List<CharSequence> symbolSourceList;
    List<AtomicInteger> wordSourceList;

    public ChineseKeyboardViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inputView = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        if (!isGridCandidateTableKey(key)) {
            return super.handleLongPress(key);
        }
        if (this.inputView == null || key == null || key.text == null || key.text.length() <= 1 || this.mCurrentKey == -1 || this.wordSourceList == null || this.wordSourceList.isEmpty() || this.mCurrentKey >= this.wordSourceList.size()) {
            return false;
        }
        int wdSource = this.wordSourceList.get(this.mCurrentKey).get();
        switch (wdSource) {
            case 5:
            case 14:
                this.inputView.showRemoveUdbWordDialog(key.text.toString(), wdSource);
                return true;
            case 6:
            case 9:
                ContactUtils.getContactNumberFromPhoneBook(getContext(), key.text.toString(), wdSource);
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getPointerCount() < 2) {
            return super.onTouchEvent(m);
        }
        clearKeyboardState();
        return true;
    }

    private boolean isGridCandidateTableKey(KeyboardEx.Key key) {
        return (this.inputView == null || !this.inputView.gridCandidateTableVisible || key == null || key.label == null || key.text == null) ? false : true;
    }

    public void setInputView(InputView view) {
        this.inputView = view;
    }

    public void setWordSource(List<AtomicInteger> aWordSourceList) {
        if (this.wordSourceList == null) {
            this.wordSourceList = new ArrayList();
        } else {
            this.wordSourceList.clear();
        }
        if (aWordSourceList != null) {
            for (int i = 0; i < aWordSourceList.size(); i++) {
                this.wordSourceList.add(aWordSourceList.get(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public Typeface getKeyTypeface() {
        return Typeface.DEFAULT;
    }

    public void setSymbolSource(List<CharSequence> aSymbolSourceList) {
        if (this.symbolSourceList == null) {
            this.symbolSourceList = new ArrayList();
        } else {
            this.symbolSourceList.clear();
        }
        if (aSymbolSourceList != null) {
            for (int i = 0; i < aSymbolSourceList.size(); i++) {
                this.symbolSourceList.add(aSymbolSourceList.get(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void invalidateKeyboardImage() {
        super.invalidateKeyboardImage();
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0040 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setGridCandidates(java.util.List<java.util.ArrayList<com.nuance.swype.input.KeyboardEx.GridKeyInfo>> r13, java.util.List<java.lang.CharSequence> r14, int r15) {
        /*
            Method dump skipped, instructions count: 533
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseKeyboardViewEx.setGridCandidates(java.util.List, java.util.List, int):void");
    }

    public void setKeyBackground(int resId) {
        this.mKeyBackground = getContext().getResources().getDrawable(resId);
    }
}
