package com.nuance.swype.input.chinese;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteChinese;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.swype.input.FaddingStrokeQueue;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class HandWritingOnKeyboardHandler {
    protected static final int MSG_DELAY_RECOGNIZER = 201;
    protected static final int MSG_DELAY_RECOGNIZER_ADD_STROKE = 202;
    private static final int MSG_HANDLE_CHAR = 204;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 203;
    private static final int MSG_HANDLE_TEXT = 205;
    protected static final int STROKE_RECOGNITION_TIME_OUT = 100;
    private static final LogManager.Log log = LogManager.getLog("HandWritingOnKeyboardHandler");
    private ChineseInputView mChineseInputView;
    private FaddingStrokeQueue mFaddingStrokeQueue;
    private List<CharSequence> mHWRecognitionCandidates;
    private boolean mHandwritingOn;
    private IME mIME;
    private boolean mIsWriting;
    protected Paint mPntWrite;
    private Stroke mStroke;
    private T9WriteChinese mWriteChinese;
    private int minMoveDistance;
    protected List<List<Point>> mWriteStrokes = new ArrayList();
    protected List<Point> mVctWrite = new ArrayList();
    protected boolean mRepaintWrite = true;
    protected int mWriteColor = -16776961;
    protected List<Point> mCachedWritePoints = new ArrayList();
    protected Path mWritePath = new Path();
    protected boolean mWriteNewSession = false;
    private boolean mIsWritingRecognitionDone = true;
    private boolean mDelayFlushWriteInlineWord = false;
    private final Handler.Callback HWHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.HandWritingOnKeyboardHandler.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            char ch;
            switch (msg.what) {
                case 203:
                    if (msg.obj != null) {
                        HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates = ((T9WriteRecognizerListener.CandidatesWriteEvent) msg.obj).mChCandidates;
                        char chGesture = 0;
                        if ((HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates != null ? HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates.size() : 0) > 0) {
                            CharSequence seq = (CharSequence) HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates.get(0);
                            if (seq.length() == 1 && ((ch = seq.charAt(0)) == '\b' || ch == '\n' || ch == '\r' || ch == ' ' || ch == '\t')) {
                                HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates.clear();
                                HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates = null;
                                chGesture = ch;
                            }
                        }
                        if (chGesture != 0) {
                            HandWritingOnKeyboardHandler.this.mChineseInputView.flushInlineWord();
                            HandWritingOnKeyboardHandler.this.mChineseInputView.handleChar(chGesture);
                        }
                    }
                    if (!HandWritingOnKeyboardHandler.this.isPendingRecognizeMessage() && !HandWritingOnKeyboardHandler.this.isPendingRecognizeStrokeMessage()) {
                        HandWritingOnKeyboardHandler.this.setWritingRecognitionDone(true);
                    }
                    HandWritingOnKeyboardHandler.this.mChineseInputView.updateListViews(false, true);
                    if (HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates != null && HandWritingOnKeyboardHandler.this.mHWRecognitionCandidates.isEmpty()) {
                        HandWritingOnKeyboardHandler.this.mChineseInputView.setNotMatchToolTipSuggestion();
                    }
                    break;
                default:
                    return true;
            }
        }
    };
    Handler mHWHandler = WeakReferenceHandler.create(this.HWHandlerCallback);
    private final Handler.Callback delayWritingRecognizerHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.HandWritingOnKeyboardHandler.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 201:
                    HandWritingOnKeyboardHandler.this.performDelayWriteRecognition();
                    return true;
                case 202:
                    HandWritingOnKeyboardHandler.this.performDelayRecognitionStroke();
                    return true;
                default:
                    return false;
            }
        }
    };
    private final Handler mDelayWriteRecognizeHandler = WeakReferenceHandler.create(this.delayWritingRecognizerHandlerCallback);

    /* JADX INFO: Access modifiers changed from: package-private */
    public HandWritingOnKeyboardHandler(IME ime, ChineseInputView iv) {
        this.mIME = ime;
        this.mChineseInputView = iv;
    }

    public void onCreate(T9Write t9write) {
        this.mWriteChinese = (T9WriteChinese) t9write;
        this.minMoveDistance = this.mIME.getResources().getDimensionPixelSize(R.dimen.trace_redraw_threadshold);
    }

    public void onDestroy() {
        if (IMEApplication.from(this.mIME.getApplicationContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled() && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteChinese.removeRecognizeListener(this.mChineseInputView);
            this.mWriteChinese.finishSession();
        }
    }

    public void onStartInput() {
        if (this.mHWRecognitionCandidates != null) {
            this.mIME.getEditState().startSession();
            this.mHWRecognitionCandidates.clear();
        }
        cancelDelayRecognition();
    }

    public void onDelayStartInput() {
        if (this.mHandwritingOn && IMEApplication.from(this.mChineseInputView.getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled() && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteChinese.removeAllRecognizeListener();
            this.mWriteChinese.addRecognizeListener(this.mChineseInputView);
            this.mWriteChinese.clearCategory();
            this.mWriteChinese.addTextCategory();
            this.mWriteChinese.addLatinLetterCategory();
            this.mWriteChinese.addSymbolCategory();
            this.mWriteChinese.addPunctuationCategory();
            this.mWriteChinese.addNumberCategory();
            this.mWriteChinese.addGestureCategory();
            int penColor = InputPrefs.getPenColor(UserPreferences.from(this.mChineseInputView.getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, this.mChineseInputView.getContext());
            this.mFaddingStrokeQueue = new FaddingStrokeQueue(this.mChineseInputView.getContext(), penColor, this.mChineseInputView);
            if (this.mChineseInputView.getContext().getResources().getConfiguration().orientation == 2) {
                this.mWriteChinese.setWritingDirection(0);
            } else {
                this.mWriteChinese.setWritingDirection(3);
            }
            Resources res = this.mChineseInputView.getContext().getResources();
            int delay = InputPrefs.getAutoDelay(UserPreferences.from(this.mChineseInputView.getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, res.getInteger(R.integer.handwriting_auto_recognize_default_ms));
            this.mWriteChinese.setRecognizerDelay(delay);
            this.mWriteChinese.startSession(this.mIME.getInputMethods().getCurrentInputLanguage().getCoreLanguageId());
            this.mWriteChinese.clearCommonChar();
            this.mWriteChinese.applyChangedSettings();
            if (UserPreferences.from(this.mChineseInputView.getContext()).isHwrRotationEnabled()) {
                this.mWriteChinese.updateRotationSetting(true);
            } else {
                this.mWriteChinese.updateRotationSetting(false);
            }
            if (UserPreferences.from(this.mChineseInputView.getContext()).isAttachingCommonWordsLDBAllowed()) {
                this.mWriteChinese.updateAttachingCommonWordsLDB(true);
            } else {
                this.mWriteChinese.updateAttachingCommonWordsLDB(false);
            }
            if (this.mHWRecognitionCandidates != null) {
                this.mHWRecognitionCandidates.clear();
            }
        }
    }

    public void onFinishInput() {
        cancelDelayRecognition();
        if (this.mHandwritingOn && IMEApplication.from(this.mIME.getApplicationContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled() && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteChinese.removeRecognizeListener(this.mChineseInputView);
            this.mWriteChinese.finishSession();
        }
        this.mHWHandler.removeMessages(203);
    }

    public void setHandwritingOn(boolean on) {
        this.mHandwritingOn = on;
    }

    public boolean acceptHWRRecognitionWord() {
        if (!recognitionCandidatesAvailable()) {
            return false;
        }
        if (this.mHWRecognitionCandidates != null) {
            this.mHWRecognitionCandidates.clear();
            this.mHWRecognitionCandidates = null;
        }
        this.mChineseInputView.flushInlineWord();
        return true;
    }

    public boolean recognitionCandidatesAvailable() {
        return this.mHWRecognitionCandidates != null && this.mHWRecognitionCandidates.size() > 0;
    }

    public void clearInternalStatus() {
        if (recognitionCandidatesAvailable()) {
            this.mHWRecognitionCandidates.clear();
        }
    }

    public boolean onSendPlaceHolderKey() {
        if (!recognitionCandidatesAvailable() || this.mHWRecognitionCandidates == null) {
            return false;
        }
        this.mHWRecognitionCandidates.clear();
        return true;
    }

    public boolean onHandleBackspace() {
        if (!recognitionCandidatesAvailable()) {
            return false;
        }
        this.mHWRecognitionCandidates.clear();
        return true;
    }

    public void onHandleClose() {
        cancelDelayRecognition();
    }

    public void onHandleWrite(List<Point> write) {
        if (this.mWriteChinese != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteChinese.applyChangedSettings();
        }
        if (!ActivityManagerCompat.isUserAMonkey()) {
            int start = 0;
            for (int i = 0; i < write.size(); i++) {
                if (write.get(i).x == 0 && write.get(i).y == 0) {
                    if (start >= 0 && i > start) {
                        this.mWriteChinese.queueAddArcs(write.subList(start, i), null, null);
                    }
                    start = i + 1;
                }
            }
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueRecognition(null);
            }
        }
        write.clear();
    }

    public void onHandleSeparator() {
        if (recognitionCandidatesAvailable()) {
            this.mChineseInputView.flushInlineWord();
            this.mHWRecognitionCandidates.clear();
        }
    }

    public void onReadStyles(Context context) {
        this.mPntWrite = new Paint(1);
        this.mPntWrite.setStyle(Paint.Style.STROKE);
        this.mPntWrite.setStrokeJoin(Paint.Join.ROUND);
        this.mPntWrite.setStrokeCap(Paint.Cap.ROUND);
        this.mPntWrite.setColor(this.mWriteColor);
        this.mPntWrite.setStrokeWidth(context.getResources().getDisplayMetrics().density * 6.0f);
        this.mStroke = Stroke.create((int) (context.getResources().getDisplayMetrics().density * 6.0f));
    }

    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        switch (event.mType) {
            case 1:
                this.mHWHandler.sendMessageDelayed(this.mHWHandler.obtainMessage(203, event), 5L);
                return;
            case 2:
                this.mHWHandler.sendMessageDelayed(this.mHWHandler.obtainMessage(204, event), 5L);
                return;
            case 3:
                this.mHWHandler.sendMessageDelayed(this.mHWHandler.obtainMessage(205, event), 5L);
                return;
            default:
                return;
        }
    }

    public void removeAllMessages() {
        this.mHWHandler.removeCallbacksAndMessages(null);
        this.mDelayWriteRecognizeHandler.removeCallbacksAndMessages(null);
    }

    private void resetWrite() {
        if (this.mVctWrite != null) {
            this.mVctWrite.clear();
        }
        if (!this.mWritePath.isEmpty()) {
            this.mWritePath.reset();
        }
        if (this.mWriteStrokes != null) {
            this.mWriteStrokes.clear();
        }
        if (this.mCachedWritePoints != null) {
            this.mCachedWritePoints.clear();
        }
        this.mIsWriting = false;
        this.mWriteNewSession = true;
        this.mRepaintWrite = false;
        setWritingRecognitionDone(true);
        log.d("resetWrite");
    }

    private void removeDelayWriteRecognitionMsg() {
        this.mDelayWriteRecognizeHandler.removeMessages(201);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayWriteRecognition() {
        if (this.mWriteChinese != null) {
            this.mWriteChinese.endArcsAddingSequence();
        }
        this.mCachedWritePoints.clear();
        this.mFaddingStrokeQueue.stopFading();
        resetWrite();
        this.mChineseInputView.invalidate();
    }

    private void removeDelayRecognitionMsg() {
        this.mDelayWriteRecognizeHandler.removeMessages(201);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPendingRecognizeMessage() {
        return this.mDelayWriteRecognizeHandler.hasMessages(201);
    }

    private void removeDelayRecognitionStroke() {
        this.mDelayWriteRecognizeHandler.removeMessages(202);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPendingRecognizeStrokeMessage() {
        return this.mDelayWriteRecognizeHandler.hasMessages(202);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognitionStroke() {
        if (this.mChineseInputView.getOnKeyboardActionListener() != null) {
            this.mChineseInputView.getOnKeyboardActionListener().onWrite(this.mCachedWritePoints);
        }
        this.mCachedWritePoints.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isWritingRecognitionDone() {
        return this.mIsWritingRecognitionDone;
    }

    protected void setWritingRecognitionDone(boolean bWritingRecognitionDone) {
        this.mIsWritingRecognitionDone = bWritingRecognitionDone;
    }

    protected void setWritePenColor(int color) {
        this.mPntWrite.setColor(color);
    }

    protected void setWritePenSize(int size) {
        this.mPntWrite.setStrokeWidth(size);
    }

    private void cancelDelayRecognition() {
        removeDelayWriteRecognitionMsg();
        resetWrite();
    }

    public void onDraw(Canvas canvas) {
        if (this.mIsWriting && this.mRepaintWrite) {
            this.mFaddingStrokeQueue.draw(canvas);
            for (Path path : this.mStroke.getPaths()) {
                canvas.drawPath(path, this.mPntWrite);
            }
        }
    }

    public boolean isWriting() {
        return this.mIsWriting;
    }

    public void onWindowVisibilityChanged() {
        int penColor = InputPrefs.getPenColor(UserPreferences.from(this.mChineseInputView.getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, this.mChineseInputView.getContext());
        setWritePenColor(penColor);
        DisplayMetrics dm = this.mChineseInputView.getResources().getDisplayMetrics();
        boolean isTablet = IMEApplication.from(this.mChineseInputView.getContext()).isScreenLayoutTablet();
        int penSize = InputPrefs.getPenSize(UserPreferences.from(this.mChineseInputView.getContext()), UserPreferences.HWR_PEN_SIZE, (int) ((isTablet ? 7 : 5) * dm.density));
        setWritePenSize(penSize);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onClearKeyboardState() {
        resetWrite();
    }

    public boolean onTouchStarted(int pointerId) {
        if (!this.mHandwritingOn) {
            return false;
        }
        if (pointerId > 0) {
            return true;
        }
        log.d("onTouchStarted...mWriteNewSession: ", Boolean.valueOf(this.mWriteNewSession), "...mIsWriting: ", Boolean.valueOf(this.mIsWriting));
        if (this.mWriteNewSession) {
            this.mDelayFlushWriteInlineWord = true;
            resetWrite();
            this.mWriteNewSession = false;
        } else {
            this.mDelayFlushWriteInlineWord = false;
        }
        removeDelayWriteRecognitionMsg();
        removeDelayRecognitionStroke();
        this.mWriteChinese.startArcsAddingSequence();
        this.mFaddingStrokeQueue.pauseFading();
        if (!this.mIsWriting) {
            return false;
        }
        removeDelayWriteRecognitionMsg();
        ArrayList<Point> newStroke = new ArrayList<>();
        this.mWriteStrokes.add(newStroke);
        this.mVctWrite.clear();
        this.mChineseInputView.invalidate();
        return true;
    }

    private boolean isWriteDetected() {
        boolean writeDetected = false;
        if (this.mVctWrite.size() > 1) {
            Point pt1 = this.mVctWrite.get(0);
            Point pt2 = this.mVctWrite.get(this.mVctWrite.size() - 1);
            writeDetected = distance(pt1, pt2) > this.minMoveDistance;
        }
        log.d("isWriteDetected: ", Boolean.valueOf(writeDetected));
        return writeDetected;
    }

    private int distance(Point p1, Point p2) {
        return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    public boolean onTouchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
        if (!this.mHandwritingOn) {
            return false;
        }
        if (pointerId > 0) {
            return true;
        }
        if (!this.mIsWriting) {
            if (isWriteDetected()) {
                this.mRepaintWrite = true;
                this.mIsWriting = true;
                if (this.mDelayFlushWriteInlineWord) {
                    this.mChineseInputView.flushCurrentActiveWord();
                    this.mDelayFlushWriteInlineWord = false;
                }
                setWritingRecognitionDone(false);
            }
            onTouchMovedSub(pointerId, keyIndex, xcoords, ycoords, times, true);
            return true;
        }
        if (this.mIsWriting) {
            onTouchMovedSub(pointerId, keyIndex, xcoords, ycoords, times, false);
            return true;
        }
        return false;
    }

    private void onTouchMovedSub(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
        int vctbase = this.mVctWrite.size();
        int vctoffset = 0;
        if (xcoords.length > this.mVctWrite.size()) {
            vctoffset = xcoords.length - this.mVctWrite.size();
        }
        for (int i = 0; i < vctoffset; i++) {
            this.mVctWrite.add(new Point((int) xcoords[vctbase + i], (int) ycoords[vctbase + i]));
        }
        if (this.mWriteStrokes.isEmpty()) {
            List<Point> newStroke = new ArrayList<>();
            this.mWriteStrokes.add(newStroke);
        }
        List<Point> curStroke = this.mWriteStrokes.get(this.mWriteStrokes.size() - 1);
        int base = curStroke.size();
        int offset = 0;
        if (xcoords.length > curStroke.size()) {
            offset = xcoords.length - curStroke.size();
        }
        for (int i2 = 0; i2 < offset; i2++) {
            curStroke.add(new Point((int) xcoords[base + i2], (int) ycoords[base + i2]));
        }
        this.mStroke.handleTouchMoved(pointerId, keyIndex, xcoords, ycoords, times, canBeTraced);
        this.mChineseInputView.invalidate();
    }

    public boolean onTouchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced) {
        if (!this.mHandwritingOn) {
            return false;
        }
        if (pointerId > 0) {
            return true;
        }
        log.d("onTouchEnded...mIsWriting: ", Boolean.valueOf(this.mIsWriting));
        if (this.mIsWriting) {
            this.mStroke.handleTouchEnd(pointerId, keyIndex, keyType, isTraced);
            int size = this.mVctWrite.size();
            for (int i = 0; i < size; i++) {
                this.mCachedWritePoints.add(this.mVctWrite.get(i));
            }
            this.mCachedWritePoints.add(new Point(0, 0));
            for (Path path : this.mStroke.getPaths()) {
                this.mFaddingStrokeQueue.add(path);
            }
            if (this.mWriteChinese != null) {
                if (isPendingRecognizeStrokeMessage()) {
                    removeDelayRecognitionStroke();
                }
                this.mDelayWriteRecognizeHandler.sendEmptyMessageDelayed(202, 100L);
                if (isPendingRecognizeMessage()) {
                    removeDelayRecognitionMsg();
                }
                this.mDelayWriteRecognizeHandler.sendEmptyMessageDelayed(201, this.mWriteChinese.getRecognizerDelay());
            }
            this.mStroke.clear();
            this.mFaddingStrokeQueue.startActionFading();
            this.mFaddingStrokeQueue.startFading();
            this.mChineseInputView.invalidate();
            return true;
        }
        resetWrite();
        this.mStroke.clear();
        this.mChineseInputView.invalidate();
        return false;
    }

    public void clearRecognitionCandidates() {
        if (this.mHWRecognitionCandidates != null) {
            this.mHWRecognitionCandidates.clear();
        }
    }

    public List<CharSequence> getHWRecognitionCandidates() {
        return this.mHWRecognitionCandidates;
    }

    public T9WriteChinese getT9WriteChinese() {
        return this.mWriteChinese;
    }
}
