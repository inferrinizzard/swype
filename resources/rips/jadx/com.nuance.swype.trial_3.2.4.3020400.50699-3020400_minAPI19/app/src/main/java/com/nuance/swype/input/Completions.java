package com.nuance.swype.input;

import android.view.inputmethod.CompletionInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class Completions {
    private final StringBuilder builder;
    private final List<CharSequence> displayText;
    private final List<CompletionInfo> items;

    public Completions(CompletionInfo[] completions) {
        this.builder = new StringBuilder();
        this.items = new ArrayList();
        this.displayText = new ArrayList();
        update(completions);
    }

    public Completions() {
        this(null);
    }

    public void update(CompletionInfo[] completions) {
        this.items.clear();
        this.displayText.clear();
        if (completions != null && completions.length != 0) {
            for (CompletionInfo ci : completions) {
                CharSequence text = getCompletionText(ci);
                if (text != null) {
                    this.displayText.add(text);
                    this.items.add(ci);
                }
            }
        }
    }

    public void clear() {
        update(null);
    }

    public int size() {
        return this.items.size();
    }

    public CompletionInfo get(int idx) {
        return this.items.get(idx);
    }

    public List<CharSequence> getDisplayItems() {
        return Collections.unmodifiableList(this.displayText);
    }

    private CharSequence filterLineEndings(CharSequence text) {
        this.builder.setLength(0);
        boolean addSpace = false;
        for (int idx = 0; idx < text.length(); idx++) {
            char ch = text.charAt(idx);
            if (ch == '\r' || ch == '\n') {
                addSpace = true;
            } else {
                if (addSpace) {
                    this.builder.append(' ');
                    addSpace = false;
                }
                this.builder.append(ch);
            }
        }
        return this.builder.toString();
    }

    private CharSequence getCompletionText(CompletionInfo ci) {
        if (ci == null || ci.getText() == null || ci.getText().length() <= 0) {
            return null;
        }
        CharSequence text = filterLineEndings(ci.getText());
        if (text.length() == 0) {
            return null;
        }
        return text;
    }
}
