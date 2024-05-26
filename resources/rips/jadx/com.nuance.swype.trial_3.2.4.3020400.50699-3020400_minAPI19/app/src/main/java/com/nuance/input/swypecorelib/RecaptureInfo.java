package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class RecaptureInfo {
    static final int DEFAULT_WORD_INDEX = 1;
    public static final RecaptureInfo EMPTY_RECAPTURE_INFO = new RecaptureInfo(allocateRecaptureInfoFieldInfoArray(), null);
    static final int RECAPTURED_WORD_LENGTH_INDEX = 3;
    static final int RECAPTURE_INFO_FIELDS = 4;
    static final int START_RECAPTURED_WORD_POSITION_INDEX = 2;
    static final int TOTAL_WORD_INDEX = 0;
    private final String EMPTY_RECAPTURED_WORD = new String();
    public final int defaultWordIndex;
    final int[] recapturedFieldInfo;
    public final String recapturedWord;
    public final int recapturedWordLen;
    public final int startRecaptureWordPosition;
    public final int totalWord;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecaptureInfo(int[] recaptureInfo, char[] buffer) {
        this.recapturedFieldInfo = recaptureInfo;
        this.totalWord = recaptureInfo[0];
        this.defaultWordIndex = recaptureInfo[1];
        this.startRecaptureWordPosition = recaptureInfo[2];
        this.recapturedWordLen = recaptureInfo[3];
        if (buffer != null) {
            this.recapturedWord = new String(buffer, this.startRecaptureWordPosition, this.recapturedWordLen > 0 ? this.recapturedWordLen : buffer.length);
        } else {
            this.recapturedWord = this.EMPTY_RECAPTURED_WORD;
        }
    }

    public static RecaptureInfo makeRecaptureInfo(char[] word) {
        return new RecaptureInfo(allocateRecaptureInfoFieldInfoArray(), word);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] allocateRecaptureInfoFieldInfoArray() {
        return new int[4];
    }
}
