package com.nuance.input.swypecorelib;

import java.util.List;

/* loaded from: classes.dex */
public final class T9WriteRecognizerListener {

    /* loaded from: classes.dex */
    public interface OnWriteRecognizerListener {
        void onHandleWriteEvent(WriteEvent writeEvent);
    }

    /* loaded from: classes.dex */
    public static class WriteEvent {
        public static final int TYPE_CANDIDATES = 1;
        public static final int TYPE_CHAR = 2;
        public static final int TYPE_INSTANT_GESTURE = 4;
        public static final int TYPE_KEY = 5;
        public static final int TYPE_SPEECH_RESULT = 6;
        public static final int TYPE_TEXT = 3;
        public final Object mData;
        public final int mType;

        public WriteEvent(int type, Object data) {
            this.mType = type;
            this.mData = data;
        }
    }

    /* loaded from: classes.dex */
    public static class CandidatesWriteEvent extends WriteEvent {
        public Candidates mCandidates;
        public List<CharSequence> mChCandidates;

        public CandidatesWriteEvent(List<CharSequence> candidates) {
            super(1, candidates);
            this.mChCandidates = candidates;
        }

        public CandidatesWriteEvent(Candidates candidates) {
            super(1, candidates);
            this.mCandidates = candidates;
        }
    }

    /* loaded from: classes.dex */
    public static class TextWriteEvent extends WriteEvent {
        public final CharSequence mText;

        public TextWriteEvent(CharSequence text) {
            super(3, text);
            this.mText = text;
        }
    }

    /* loaded from: classes.dex */
    public static class CharWriteEvent extends WriteEvent {
        public final char mChar;

        public CharWriteEvent(char ch) {
            super(2, Character.valueOf(ch));
            this.mChar = ch;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyWriteEvent extends WriteEvent {
        public final int mKey;

        public KeyWriteEvent(int key) {
            super(5, Integer.valueOf(key));
            this.mKey = key;
        }
    }

    /* loaded from: classes.dex */
    public static class InstantGestureWriteEvent extends WriteEvent {
        public final Candidates mCandidates;
        public final char mGestureChar;

        public InstantGestureWriteEvent(char gestureChar, Candidates candidates) {
            super(4, null);
            this.mGestureChar = gestureChar;
            this.mCandidates = candidates;
        }
    }
}
