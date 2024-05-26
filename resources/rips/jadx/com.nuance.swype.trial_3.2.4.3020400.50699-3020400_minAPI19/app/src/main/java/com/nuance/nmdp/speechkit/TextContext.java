package com.nuance.nmdp.speechkit;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/* loaded from: classes.dex */
public class TextContext {
    private final int mEndCursor;
    private final int mStartCursor;
    private final String mText;
    private boolean enabledCustomWordRecognition = true;
    public HashMap<String, String> mCustomStringLog = new HashMap<>();
    public HashMap<String, Integer> mCustomIntegerLog = new HashMap<>();

    public TextContext(String text, int start, int end) {
        this.mText = text;
        this.mStartCursor = start;
        this.mEndCursor = end;
    }

    public String getTextUTF8() {
        if (this.mText != null) {
            try {
                return new String(this.mText.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e("TextContext", e.toString());
            }
        }
        return "";
    }

    public String getText() {
        return this.mText;
    }

    public int getStartCursor() {
        return this.mStartCursor;
    }

    public int getEndCursor() {
        return this.mEndCursor;
    }

    public void addCustomLog(String name, String value) {
        try {
            this.mCustomStringLog.put(name, new String(value.getBytes(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
    }

    public void addCustomLog(String name, int value) {
        this.mCustomIntegerLog.put(name, Integer.valueOf(value));
    }

    public void enableCustomWordRecogniztion(boolean enabled) {
        this.enabledCustomWordRecognition = enabled;
    }

    public boolean needCustomWordRecogniztion() {
        return this.enabledCustomWordRecognition;
    }
}
