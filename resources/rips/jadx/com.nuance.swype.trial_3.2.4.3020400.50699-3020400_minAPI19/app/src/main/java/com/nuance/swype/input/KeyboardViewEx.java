package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Printer;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.ArraysCompat;
import com.nuance.android.util.LruCache;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.common.Integers;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.AccessibilityScrubGestureView;
import com.nuance.swype.input.accessibility.SettingsChangeListener;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.input.accessibility.statemachine.AccessibilityStateManager;
import com.nuance.swype.input.accessibility.statemachine.ExplorationState;
import com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState;
import com.nuance.swype.input.accessibility.statemachine.LongPressState;
import com.nuance.swype.input.appspecific.AppSpecificBehavior;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.chinese.HighLightKeyArea;
import com.nuance.swype.input.chinese.TwoFingerGestureDetector;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.view.DragHelper;
import com.nuance.swype.input.view.InputLayout;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.BitmapUtil;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.DisplayUtils;
import com.nuance.swype.util.DrawingUtils;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.KeyPreviewManager;
import com.nuance.swype.view.OverlayView;
import com.nuance.swype.view.PopupViewManager;
import com.nuance.swype.view.ShadowDrawable;
import com.nuance.swype.widget.PreviewView;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

/* loaded from: classes.dex */
public class KeyboardViewEx extends View implements View.OnClickListener, TwoFingerGestureDetector.OnScrollListener, DragHelper.DragVisualizer, InputLayout.DragListener {
    private static final char COMPONENT_MARKER = 40959;
    protected static final int DEFAULT_DELAY_AFTER_PREVIEW = 60;
    private static final int DEFAULT_DELAY_BEFORE_PREVIEW = 30;
    private static final int DELAY_REMOVE_POPUPVIEW = 0;
    private static final int FADING_DELAY = 25;
    private static final int FIVE_ROW_LAYOUT = 5;
    private static final double GLOW_OVERFLOW = 0.25d;
    private static final long HOVER_TO_TOUCH_WAITING_TIME = 10;
    private static final int KEYBOARD_HEIGHT_THRESHOLD_VALUE = 100;
    private static final int MAX_CHAR_POPUP_CACHE = 4;
    private static final int MAX_TRACE_DRAWN_POINTS = 50;
    private static final float MAX_TRAIL_RADIUS = 17.0f;
    private static final float MIN_HIDE_SENCONDARY_KEYBOARD_SACLE = 0.91f;
    private static final int MIN_TRACE_WIDTH = 4;
    protected static final int MSG_DEFER_SENDKEY = 1008;
    protected static final int MSG_FADING = 1025;
    protected static final int MSG_LONGPRESS = 1005;
    protected static final int MSG_MULTITAP_TIMEOUT = 106;
    protected static final int MSG_PRESS_HOLD_FLICKR = 1007;
    private static final int MSG_REMOVE_POPUPVIEW = 1006;
    protected static final int MSG_REMOVE_PREVIEW = 1002;
    protected static final int MSG_REPEAT = 1003;
    protected static final int MSG_SHORT_LONGPRESS = 104;
    protected static final int MSG_SHOW_PREVIEW = 1001;
    protected static final int MSG_TRACE_COMPLETE = 1024;
    protected static final int MULTITAP_INTERVAL = 1250;
    public static final int NOT_A_KEY = -1;
    protected static final int REPEAT_INTERVAL = 50;
    protected static final int REPEAT_START_DELAY = 400;
    private static final int TRACE_POINTS_INIT_CAPACITY = 500;
    private int altCharsPopupMaxCol;
    private final Rect bounds;
    private DrawBufferManager bufferManager;
    protected CharacterUtilities charUtil;
    private final Rect dest;
    private Rect displaySize;
    private boolean doubleBuffered;
    private DragHelper dragHelper;
    boolean enableSimplifiedMode;
    private boolean enableTraceAlphaGradient;
    private final int glowPadding;
    private final Handler.Callback handlerCallback;
    protected boolean isContextCandidatesView;
    protected boolean isEmojiKeyboardShown;
    private boolean isEmojiStylePopupKeyboard;
    private boolean isPopupKeyboard;
    protected boolean isPressDownPreviewEnabled;
    private boolean isSlideSelectEnabled;
    protected boolean isTracedGesture;
    private ShadowProps keyPopupBackgroundShadowProps;
    private KeyPreviewManager keyPrevManager;
    private KeyPreviewManager.StyleParams keyPreviewStyleParams;
    private boolean keyboardBeingDragged;
    private boolean lowEndDeviceBuild;
    protected boolean mAbortKey;
    protected int mAltShadowColor;
    protected float mAltShadowRadius;
    protected PopupWindow mAlternateCharPopup;
    private PreviewView mAlternatePreviewView;
    private Bitmap mBuffer;
    private Canvas mCanvas;
    public int mCurrentIndex;
    public int mCurrentKey;
    private int mCurrentKeyIndex;
    private String mCursorString;
    private final Rect mDirtyRect;
    public int mFlickerPopup;
    private Drawable mGlow;
    public final Handler mHandler;
    private Handler mHandlerHoverExit;
    private Runnable mHoverExitRunnable;
    public SparseArray<Pointer> mHshPointers;
    private int mIconRecolor;
    private int mIconRecolorFunction;
    protected boolean mIgnoreDraw;
    public IME mIme;
    public boolean mInMultiTap;
    public int mInvalidIndex;
    public int mInvalidKey;
    public int mInvalidTapCount;
    public boolean mIsClearKeyLabelUpdated;
    public boolean mIsDelimiterKeyLabelUpdated;
    protected boolean mIsTracing;
    public Drawable mKeyBackground;
    private Drawable mKeyBorder;
    protected final int[] mKeyIndices;
    private final Paint mKeyPaint;
    public boolean mKeyRepeated;
    private KeyboardEx mKeyboard;
    public OnKeyboardActionListener mKeyboardActionListener;
    public KeyboardSwitcher mKeyboardSwitcher;
    public KeyboardEx.Key[] mKeys;
    private int mLastEvent;
    private boolean mLastHoverExitPending;
    private int mLastSentIndex;
    private long mLastTapTime;
    public int mLongPressTimeout;
    protected KeyboardViewEx mMiniKeyboard;
    private final SparseArray<LruCache<CharSequence, View>> mMiniKeyboardCache;
    private View mMiniKeyboardContainer;
    private int mMiniKeyboardOffsetX;
    private int mMiniKeyboardOffsetY;
    protected boolean mMiniKeyboardOnScreen;
    public int[] mOffsetInWindow;
    private final Rect mPadding;
    private int mPopupCharSize;
    private int mPopupKeyTextColor;
    private final PopupWindow mPopupKeyboard;
    public int mPopupLayout;
    protected View mPopupParent;
    public int mPopupTextColor;
    private int mPopupTextSize;
    protected boolean mPreTraceAlreadDispatched;
    private int mPreviewHeight;
    private final StringBuilder mPreviewLabel;
    private int mPreviewLayout;
    private int mPreviewOffset;
    protected PopupWindow mPreviewPopup;
    private PreviewView mPreviewView;
    private int mPreviousKeyIndex;
    private boolean mProximityCorrectOn;
    protected int mRepeatKeyIndex;
    private AccessibilityScrubGestureView mScrubGestureView;
    public int mShortLongPressTimeout;
    public int mTapCount;
    private Paint mTracePaint;
    protected int mTracePointerId;
    protected int mTraceWidth;
    private int[] mWindowOffset;
    private CoordUtils.CoordMapper mapper;
    protected float mfDecay;
    protected int miHighlightTextColor;
    public int miTraceColor;
    protected int multiTouchPointerCount;
    private final Rect outRect;
    private OverlayView overlayView;
    private final Queue<Integer> pendingMTDownPointerIds;
    private ShadowProps popupBackgroundShadowProps;
    private Map<CharSequence, CharSequence> popupCharsSimplifiedMap;
    private PopupViewManager popupViewManager;
    protected int pressHoldFlickrTimeOut;
    private int previewHideDelay;
    private Typeface previewKeyTypeface;
    private Typeface previewKeyTypefaceBold;
    private int previewShowDelay;
    public int repeatCount;
    private boolean sanitizingFont;
    private int secondary_padding_adjustment;
    protected boolean showCompleteTrace;
    private SlideSelectPopupManager slideSelectPopupManager;
    protected float swypeDistanceSum;
    private ShadowEffect textShadow;
    private final Typeface typefaceBold;
    private final Typeface typefaceNormal;
    private static final int KEY_ICON_SKIP_RECOLOR = Color.argb(255, 255, 255, 255);
    public static final LogManager.Log log = LogManager.getLog("KeyboardViewEx");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    private static final int[] ICON_STATE_PRIMARY = {android.R.attr.state_first};
    private static final int[] ICON_STATE_SECONDARY = {android.R.attr.state_middle};
    private static final int[] ICON_STATE_PREVIEW = {android.R.attr.state_last};
    private static String[] drawIconBelowAltExceptions = {"?", "?"};
    private static final Point keyboardPoint = new Point(0, 0);
    private static String EMPTY_TEXT = "";

    /* loaded from: classes.dex */
    public interface OnKeyboardActionListener {
        void onDoublePress(int i);

        void onHardwareCharKey(int i, int[] iArr);

        void onKey(Point point, int i, int[] iArr, KeyboardEx.Key key, long j);

        void onMultitapTimeout();

        void onPress(int i);

        void onRelease(int i);

        void onText(CharSequence charSequence, long j);

        void onTrace(TracePoints tracePoints);

        void onUpdateSpeechText(CharSequence charSequence, boolean z, boolean z2);

        void onWrite(List<Point> list);

        void swipeDown();

        void swipeLeft();

        void swipeRight();

        void swipeUp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum ShowKeyState {
        Pressed,
        Released,
        Moved
    }

    /* loaded from: classes.dex */
    public static abstract class KeyboardActionAdapter implements OnKeyboardActionListener {
        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onPress(int primaryCode) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onDoublePress(int primaryCode) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onRelease(int primaryCode) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onKey(Point point, int primaryCode, int[] keyCodes, KeyboardEx.Key key, long eventTime) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onHardwareCharKey(int primaryCode, int[] keyCodes) {
        }

        public void onText(CharSequence text) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeLeft() {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeRight() {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeDown() {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeUp() {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onMultitapTimeout() {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onTrace(TracePoints trace) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onWrite(List<Point> write) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onUpdateSpeechText(CharSequence text, boolean isStreaming, boolean isFinal) {
        }
    }

    /* loaded from: classes.dex */
    public static class TracePoints {
        private final List<Long> eventTimes;
        private final List<Point> path;

        TracePoints(int initCapacity) {
            this.path = new ArrayList(initCapacity);
            this.eventTimes = new ArrayList(initCapacity);
        }

        public void reset(List<Point> points, List<Long> times) {
            clear();
            if (points.size() != times.size()) {
                throw new IllegalArgumentException("List arguments must have same size");
            }
            if (points.size() > 0) {
                this.path.addAll(points);
                this.eventTimes.addAll(times);
            }
        }

        public boolean add(Point pt, long time) {
            if (!this.path.isEmpty()) {
                long lastTime = this.eventTimes.get(this.eventTimes.size() - 1).longValue();
                if (time < lastTime) {
                    KeyboardViewEx.log.d("add(): out of order timestamp: ", Long.valueOf(time), "<", Long.valueOf(lastTime));
                    return false;
                }
            }
            this.path.add(pt);
            this.eventTimes.add(Long.valueOf(time));
            return true;
        }

        public void clear() {
            this.path.clear();
            this.eventTimes.clear();
        }

        public int size() {
            return this.path.size();
        }

        public Point get(int index) {
            return this.path.get(index);
        }

        public Long getEventTime(int index) {
            return this.eventTimes.get(index);
        }

        public List<Point> getPoints() {
            return Collections.unmodifiableList(this.path);
        }

        public List<Long> getTimes() {
            return Collections.unmodifiableList(this.eventTimes);
        }
    }

    public static int distance(Point p1, Point p2) {
        return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    /* loaded from: classes.dex */
    public class Pointer {
        int currentIndex;
        int fading;
        boolean isTraceDetected;
        public int pointerId;
        float radius;
        final TracePoints renderingTracePoints;
        final TracePoints tracePoints;
        boolean wasPressed;

        Pointer(int pointerId) {
            this.currentIndex = -1;
            this.fading = 255;
            this.tracePoints = new TracePoints(500);
            this.renderingTracePoints = new TracePoints(500);
            this.pointerId = pointerId;
        }

        public Pointer(Pointer referencePointer) {
            this.currentIndex = -1;
            this.fading = 255;
            this.tracePoints = new TracePoints(500);
            this.renderingTracePoints = new TracePoints(500);
            this.pointerId = referencePointer.pointerId;
            this.isTraceDetected = referencePointer.isTraceDetected;
            this.tracePoints.reset(referencePointer.getPoints(), referencePointer.tracePoints.getTimes());
            this.currentIndex = referencePointer.currentIndex;
            this.wasPressed = referencePointer.wasPressed;
        }

        public void press(MotionEvent me) {
            this.currentIndex = getCurrentKeyIndex(me);
            if (this.currentIndex >= KeyboardViewEx.this.mKeys.length) {
                this.currentIndex = KeyboardViewEx.this.mKeys.length - 1;
            }
            if (this.currentIndex != -1) {
                KeyboardViewEx.this.pressKey(KeyboardViewEx.this.mKeys, this.currentIndex);
            }
            this.wasPressed = true;
            KeyboardViewEx.this.setKeyState(this.currentIndex, ShowKeyState.Pressed);
        }

        public List<Point> getPoints() {
            return this.tracePoints.getPoints();
        }

        public List<Long> getTimes() {
            return this.tracePoints.getTimes();
        }

        void clear() {
            this.tracePoints.clear();
        }

        public Point get(int idx) {
            return this.tracePoints.get(idx);
        }

        public int pathSize() {
            return this.tracePoints.size();
        }

        public int size() {
            return this.tracePoints.size();
        }

        boolean isPressed() {
            return this.wasPressed;
        }

        public void reset() {
            if (this.currentIndex != -1) {
                KeyboardViewEx.this.releaseKey(KeyboardViewEx.this.mKeys, this.currentIndex, false);
            }
            this.fading = 255;
            this.radius = KeyboardViewEx.MAX_TRAIL_RADIUS;
            this.tracePoints.clear();
            this.renderingTracePoints.clear();
            this.isTraceDetected = false;
            this.wasPressed = false;
            this.currentIndex = -1;
            KeyboardViewEx.this.setKeyState(-1, ShowKeyState.Released);
        }

        public void release(MotionEvent me) {
            if (this.currentIndex != -1) {
                KeyboardViewEx.this.releaseKey(KeyboardViewEx.this.mKeys, this.currentIndex, !KeyboardViewEx.this.isWriting());
            }
            reset();
        }

        private int getCurrentKeyIndex(MotionEvent me) {
            Point pt = getCurrentLocation();
            if (pt != null) {
                return KeyboardViewEx.this.getKeyIndices(pt.x, pt.y);
            }
            KeyboardViewEx.log.i("getCurrentKeyIndex returns null");
            return -1;
        }

        public int getCurrentKeyIndex() {
            return this.currentIndex;
        }

        public void setTraceDetected(boolean traceDetected) {
            this.isTraceDetected = traceDetected;
        }

        public boolean isTraceDetected() {
            return this.isTraceDetected;
        }

        public Point getCurrentLocation() {
            if (this.tracePoints.size() > 0) {
                return this.tracePoints.get(this.tracePoints.size() - 1);
            }
            KeyboardViewEx.log.i("getCurrentLocation returns null");
            return null;
        }

        public void add(Point pt, long time) {
            this.tracePoints.add(pt, time);
        }

        public int getPointerId() {
            return this.pointerId;
        }

        public long getCurrentTime() {
            return this.tracePoints.getEventTime(this.tracePoints.size() - 1).longValue();
        }
    }

    public boolean handleCallbackMessage(Message msg) {
        switch (msg.what) {
            case 104:
                int keyIndex = msg.arg1;
                int pointerId = msg.arg2;
                KeyboardEx.Key key = getKey(msg.arg1);
                if ((!isExploreByTouchOn() || AccessibilityInfo.isLongPressEnabled() || (getKeyboardAccessibilityState() == ExplorationState.getInstance() && ((key == null || key.codes.length <= 0 || key.codes[0] == 4068) && (this.mKeyboardSwitcher == null || this.mKeyboardSwitcher.isAlphabetMode())))) && key != null) {
                    this.mHandler.removeMessages(106);
                    this.mHandler.removeMessages(MSG_LONGPRESS);
                    onShortPress(key, keyIndex, pointerId);
                }
                return true;
            case 106:
                multitapTimeOut();
                return true;
            case MSG_SHOW_PREVIEW /* 1001 */:
                showKey(msg.arg1);
                return true;
            case MSG_REMOVE_PREVIEW /* 1002 */:
                dismissPreviewPopup();
                return true;
            case MSG_REPEAT /* 1003 */:
                if (repeatKey() && !this.mHandler.hasMessages(MSG_REPEAT)) {
                    this.mHandler.sendEmptyMessageDelayed(MSG_REPEAT, 50L);
                }
                return true;
            case MSG_LONGPRESS /* 1005 */:
                if (!isExploreByTouchOn()) {
                    popupMiniKeyboardOrLongPress();
                }
                return true;
            case MSG_REMOVE_POPUPVIEW /* 1006 */:
                dismissPopupKeyboard();
                return true;
            case MSG_DEFER_SENDKEY /* 1008 */:
                detectAndSendKey(msg.arg1, msg.arg2, ((Long) msg.obj).longValue());
                return true;
            case 1024:
                if (!this.showCompleteTrace) {
                    this.mHandler.removeMessages(1025);
                    this.mHandler.sendEmptyMessageDelayed(1025, 25L);
                }
                invalidate();
                if (this.mIme != null && ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null)) {
                    this.mIme.getInputContainerView().invalidateItem();
                    this.mIme.getInputContainerView().invalidate();
                }
                return true;
            case 1025:
                boolean postFading = false;
                for (int i = 0; i < this.mHshPointers.size(); i++) {
                    Pointer pointer = this.mHshPointers.get(i);
                    if (pointer != null && pointer.isTraceDetected() && pointer.renderingTracePoints.size() > 0) {
                        if (pointer.radius - this.mfDecay <= 0.0f) {
                            pointer.reset();
                        } else {
                            postFading = true;
                            pointer.fading -= 35;
                            pointer.radius -= this.mfDecay;
                        }
                    }
                }
                invalidate();
                if (postFading) {
                    this.mHandler.sendEmptyMessageDelayed(1025, 25L);
                }
                if (this.mIme != null && ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null)) {
                    this.mIme.getInputContainerView().invalidateItem();
                    this.mIme.getInputContainerView().invalidate();
                }
                return true;
            default:
                return false;
        }
    }

    public KeyboardViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private static int getLocalizedResourceId(Context context, String prefix, int defaultVal) {
        String localeString = IMEApplication.from(context).getCurrentLanguage().createLocale().toString();
        String resourceName = prefix + localeString.toLowerCase();
        int id = context.getResources().getIdentifier(resourceName, null, context.getPackageName());
        if (id == 0) {
            return defaultVal;
        }
        return id;
    }

    private static String getDefaultTypefaceName(Context context) {
        int id = getLocalizedResourceId(context, "string/custom_font_name_", R.string.custom_font_name);
        return context.getString(id);
    }

    public KeyboardViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        String assetFileName;
        TypedValue typedValue;
        this.isContextCandidatesView = false;
        this.pendingMTDownPointerIds = new ArrayDeque();
        this.mCurrentKeyIndex = -1;
        this.mPreviousKeyIndex = -1;
        this.doubleBuffered = true;
        this.enableSimplifiedMode = false;
        this.previewShowDelay = 30;
        this.previewHideDelay = 60;
        this.pressHoldFlickrTimeOut = 500;
        this.isPressDownPreviewEnabled = true;
        this.mCurrentKey = -1;
        this.mKeyIndices = new int[12];
        this.mRepeatKeyIndex = -1;
        this.mKeyRepeated = false;
        this.mPreviewLayout = 0;
        this.mLastHoverExitPending = false;
        this.mPreviewLabel = new StringBuilder(1);
        this.mInvalidKey = -1;
        this.mCurrentIndex = -1;
        this.mInvalidIndex = -1;
        this.mInvalidTapCount = -1;
        this.mDirtyRect = new Rect();
        this.outRect = new Rect();
        this.mfDecay = 0.5f;
        this.miTraceColor = -16776961;
        this.miHighlightTextColor = -16776961;
        this.mHshPointers = new SparseArray<>();
        this.multiTouchPointerCount = 0;
        this.mIsTracing = false;
        this.isTracedGesture = false;
        this.mIsDelimiterKeyLabelUpdated = false;
        this.mIsClearKeyLabelUpdated = false;
        this.showCompleteTrace = false;
        this.sanitizingFont = true;
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.KeyboardViewEx.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                return KeyboardViewEx.this.handleCallbackMessage(msg);
            }
        };
        this.textShadow = new ShadowEffect();
        this.dest = new Rect();
        this.bounds = new Rect();
        this.popupCharsSimplifiedMap = new HashMap();
        this.mapper = new CoordUtils.CoordMapper(this);
        this.charUtil = IMEApplication.from(context).getCharacterUtilities();
        this.mHandler = WeakReferenceHandler.create(this.handlerCallback);
        this.secondary_padding_adjustment = context.getResources().getDimensionPixelSize(R.dimen.key_secondary_character_padding_adjustment);
        setPadding(0, 0, 0, 0);
        TypedArrayWrapper a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, attrs, R.styleable.KeyboardViewEx, defStyle, R.style.SwypeReference, R.xml.defaults, "SwypeReference");
        this.mKeyPaint = new Paint();
        int n = a.delegateTypedArray.getIndexCount();
        int popupCharSizeAdjust = 0;
        this.previewShowDelay = getResources().getInteger(R.integer.preview_popup_show_delay);
        this.previewHideDelay = getResources().getInteger(R.integer.preview_popup_hide_delay);
        this.altCharsPopupMaxCol = getResources().getInteger(R.integer.alt_chars_popup_max_col);
        this.lowEndDeviceBuild = DisplayUtils.isLowEndDevice(getContext());
        Typeface typefaceCustom = null;
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.KeyboardViewEx_keyBackground) {
                this.mKeyBackground = a.getDrawable(attr);
            } else if (attr == R.styleable.KeyboardViewEx_keyBorder) {
                this.mKeyBorder = a.getDrawable(attr);
            } else if (attr == R.styleable.KeyboardViewEx_keyPreviewLayout) {
                this.mPreviewLayout = a.getResourceId(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_keyPreviewOffset) {
                this.mPreviewOffset = a.delegateTypedArray.getDimensionPixelOffset(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_keyPreviewHeight) {
                this.mPreviewHeight = a.getDimensionPixelSize(attr, 80);
            } else if (attr == R.styleable.KeyboardViewEx_popupLayout) {
                this.mPopupLayout = a.getResourceId(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_flickerPopup) {
                this.mFlickerPopup = a.getResourceId(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_keyPopupLabelTextColor) {
                this.mPopupTextColor = a.getColor(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_keyPopupLabelTextSize) {
                this.mPopupTextSize = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.KeyboardViewEx_keyPopupLabelCharSize) {
                this.mPopupCharSize = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.KeyboardViewEx_keyPopupLabelCharSizeAdjustment) {
                popupCharSizeAdjust = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.KeyboardViewEx_traceTrail) {
                this.miTraceColor = a.getColor(attr, -16776961);
            } else if (attr == R.styleable.KeyboardViewEx_highlightedKeyTextColor) {
                this.miHighlightTextColor = a.getColor(attr, -16776961);
            } else if (attr == R.styleable.KeyboardViewEx_traceWidth) {
                this.mTraceWidth = a.getDimensionPixelSize(attr, 16);
            } else if (attr == R.styleable.KeyboardViewEx_shadowStyle) {
                this.textShadow = new ShadowEffect(new ShadowProps(context, a, R.styleable.KeyboardViewEx_shadowStyle));
            } else if (attr == R.styleable.KeyboardViewEx_popupBackgroundShadowStyle) {
                this.popupBackgroundShadowProps = new ShadowProps(context, a, R.styleable.KeyboardViewEx_popupBackgroundShadowStyle);
            } else if (attr == R.styleable.KeyboardViewEx_keyPopupBackgroundShadowStyle) {
                this.keyPopupBackgroundShadowProps = new ShadowProps(context, a, R.styleable.KeyboardViewEx_keyPopupBackgroundShadowStyle);
            } else if (attr == R.styleable.KeyboardViewEx_altShadowRadius) {
                this.mAltShadowRadius = a.getFloat(attr, 0.0f);
            } else if (attr == R.styleable.KeyboardViewEx_altShadowColor) {
                this.mAltShadowColor = a.getColor(attr, 1275134209);
            } else if (attr != R.styleable.KeyboardViewEx_fontAssetFileName) {
                if (attr == R.styleable.KeyboardViewEx_isPopupKeyboard) {
                    this.isPopupKeyboard = a.getBoolean(attr, false);
                } else if (attr == R.styleable.KeyboardViewEx_isEmojiStylePopupKeyboard) {
                    this.isEmojiStylePopupKeyboard = a.getBoolean(attr, false);
                } else if (attr != R.styleable.KeyboardViewEx_background) {
                    if (attr == R.styleable.KeyboardViewEx_iconRecolorFunction) {
                        this.mIconRecolorFunction = a.getColor(attr, 0);
                    } else if (attr == R.styleable.KeyboardViewEx_iconRecolor) {
                        this.mIconRecolor = a.getColor(attr, 0);
                    }
                }
            } else {
                if (a.tValsStyledByApk != null && (typedValue = a.tValsStyledByApk.get(attr)) != null) {
                    ThemeLoader.getInstance();
                    CharSequence themedText = ThemeLoader.getThemedText(typedValue);
                    assetFileName = themedText != null ? themedText.toString() : null;
                } else {
                    assetFileName = a.delegateTypedArray.getString(attr);
                }
                if (assetFileName != null) {
                    typefaceCustom = Typeface.createFromAsset(getContext().getAssets(), assetFileName);
                }
            }
        }
        if (typefaceCustom == null) {
            String customFontName = getDefaultTypefaceName(context);
            if (!TextUtils.isEmpty(customFontName)) {
                this.typefaceNormal = Typeface.create(customFontName, 0);
                this.typefaceBold = Typeface.create(customFontName, 1);
            } else {
                this.typefaceNormal = Typeface.DEFAULT;
                this.typefaceBold = Typeface.DEFAULT_BOLD;
            }
        } else {
            this.typefaceBold = typefaceCustom;
            this.typefaceNormal = typefaceCustom;
        }
        this.previewKeyTypeface = this.typefaceNormal;
        this.previewKeyTypefaceBold = this.typefaceBold;
        this.keyPreviewStyleParams = new KeyPreviewManager.StyleParams(getContext(), a.delegateTypedArray);
        initKeyPrevManager();
        a.recycle();
        this.mPopupCharSize += popupCharSizeAdjust;
        IMEApplication app = IMEApplication.from(context);
        this.enableTraceAlphaGradient = app.getThemedBoolean(R.attr.traceAlphaGradientEnabled);
        this.mGlow = IMEApplication.from(context).getThemedDrawable(R.attr.glow);
        this.glowPadding = -((int) Math.ceil(app.getThemedDimension(R.attr.glowStretch)));
        int minTraceWidth = (int) (4.0f * getResources().getDisplayMetrics().density);
        if (this.mTraceWidth < minTraceWidth) {
            this.mTraceWidth = minTraceWidth;
        }
        this.mAlternateCharPopup = new PopupWindow(context);
        this.mAlternateCharPopup.setBackgroundDrawable(null);
        this.mAlternateCharPopup.setTouchable(false);
        prepareAltPopup();
        this.mPreviewPopup = new PopupWindow(context);
        if (this.mPreviewLayout != 0) {
            preparePreviewPopup();
        } else {
            this.isPressDownPreviewEnabled = false;
        }
        this.mPreviewPopup.setBackgroundDrawable(null);
        this.mPreviewPopup.setTouchable(false);
        this.mPopupKeyboard = new PopupWindow(context);
        this.mPopupKeyboard.setBackgroundDrawable(null);
        if (!AccessibilityInfo.isLongPressAllowed()) {
            this.mPopupKeyboard.setTouchable(true);
        } else {
            this.mPopupKeyboard.setTouchable(false);
        }
        this.mKeyPaint.setAntiAlias(true);
        this.mKeyPaint.setTextSize(0.0f);
        this.mKeyPaint.setTextAlign(Paint.Align.CENTER);
        this.mPadding = new Rect(1, 1, 1, 1);
        this.mMiniKeyboardCache = new SparseArray<>();
        this.mKeyBackground.getPadding(this.mPadding);
        this.isSlideSelectEnabled = getResources().getBoolean(R.bool.enable_slide_select_popup);
        this.mLongPressTimeout = UserPreferences.from(context).getLongPressDelay();
        this.mShortLongPressTimeout = this.mLongPressTimeout;
        this.mCursorString = getResources().getString(R.string.accessibility_note_Context);
        resetMultiTap();
        if (!getResources().getBoolean(R.bool.animate_fade_trace)) {
            this.mfDecay = MAX_TRAIL_RADIUS;
        }
        this.mPopupKeyTextColor = app.getThemedColor(R.attr.popupKeyTextColor);
        checkAccessibility();
    }

    private void initKeyPrevManager() {
        if (!this.isPopupKeyboard) {
            Context context = getContext();
            if (getResources().getBoolean(R.bool.enable_key_preview_manager)) {
                this.keyPrevManager = new KeyPreviewManager(context, this, getOverlayViewCreate(), this.keyPreviewStyleParams);
            }
        }
    }

    public final OverlayView getOverlayViewCreate() {
        if (this.overlayView == null && !this.isPopupKeyboard) {
            this.overlayView = new OverlayView(getContext(), null);
        }
        return this.overlayView;
    }

    public final OverlayView getOverlayView() {
        return this.overlayView;
    }

    public final PopupViewManager getPopupViewManagerCreate() {
        if (this.popupViewManager == null && !this.isPopupKeyboard) {
            this.popupViewManager = new PopupViewManager(getContext(), getOverlayViewCreate());
        }
        return this.popupViewManager;
    }

    public final PopupViewManager getPopupViewManager() {
        return this.popupViewManager;
    }

    private LayoutInflater getThemedLayoutInflater() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return IMEApplication.from(getContext()).getThemedLayoutInflater(inflater);
    }

    private void preparePreviewPopup() {
        LayoutInflater inflater = getThemedLayoutInflater();
        IMEApplication.from(getContext()).getThemeLoader().setLayoutInflaterFactory(inflater);
        this.mPreviewView = (PreviewView) inflater.inflate(this.mPreviewLayout, (ViewGroup) null);
        IMEApplication.from(getContext()).getThemeLoader().applyTheme(this.mPreviewView);
        this.mPreviewPopup.setContentView(this.mPreviewView);
    }

    @SuppressLint({"InflateParams"})
    private void prepareAltPopup() {
        LayoutInflater inflater = getThemedLayoutInflater();
        IMEApplication.from(getContext()).getThemeLoader().setLayoutInflaterFactory(inflater);
        this.mAlternatePreviewView = (PreviewView) inflater.inflate(R.layout.alternate_char_popup, (ViewGroup) null);
        IMEApplication.from(getContext()).getThemeLoader().applyTheme(this.mAlternatePreviewView);
        this.mAlternateCharPopup.setContentView(this.mAlternatePreviewView);
    }

    public synchronized void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        this.mKeyboardActionListener = listener;
    }

    public synchronized OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.mKeyboardActionListener;
    }

    public void setKeyboard(KeyboardEx keyboard) {
        this.mKeyboard = keyboard;
        if (this.mKeyboard != null) {
            setKeyState(-1, ShowKeyState.Released);
            hideKeyPreview(-1);
            for (KeyboardEx.Key key : this.mKeyboard.getKeys()) {
                if (key.pressed) {
                    key.onReleased(!key.sticky);
                }
            }
        }
        if (!this.isPopupKeyboard) {
            if (keyboard.background != null) {
                setBackgroundDrawable(keyboard.background);
            }
            if (keyboard.getKeyboardDockMode() != KeyboardEx.KeyboardDockMode.MOVABLE_MINI) {
                Resources res = getContext().getResources();
                setPadding((int) res.getDimension(R.dimen.keyboard_padding_left), (int) res.getDimension(R.dimen.keyboard_padding_top), (int) res.getDimension(R.dimen.keyboard_padding_right), (int) res.getDimension(R.dimen.keyboard_padding_bottom));
            } else {
                setPadding(0, 0, 0, 0);
            }
        }
        this.sanitizingFont = this.mKeyboard.isSanitizeFont();
        invalidateKeyboard(this.mKeyboard);
        setMicrophoneKeyIcon();
        resetAllPointers();
    }

    public void enableSimplifiedMode(boolean enable) {
        this.enableSimplifiedMode = enable;
    }

    protected KeyboardEx.Key[] getVisibleKeys(KeyboardEx keyboard) {
        List<KeyboardEx.Key> keys = keyboard.getKeys();
        List<KeyboardEx.Key> visibleKeys = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            KeyboardEx.Key key = keys.get(i);
            if (key.visible) {
                visibleKeys.add(key);
            }
        }
        return (KeyboardEx.Key[]) visibleKeys.toArray(new KeyboardEx.Key[visibleKeys.size()]);
    }

    public void invalidateKeyboard(KeyboardEx keyboard) {
        this.mKeys = getVisibleKeys(keyboard);
        requestLayout();
        invalidateKeyboardImage();
        this.mMiniKeyboardCache.clear();
        this.mCurrentKeyIndex = -1;
        this.mPreviousKeyIndex = -1;
    }

    public KeyboardEx getKeyboard() {
        return this.mKeyboard;
    }

    public boolean setShiftState(Shift.ShiftState shiftState) {
        if (this.mKeyboardSwitcher == null || !this.mKeyboardSwitcher.setShiftState(shiftState)) {
            return false;
        }
        invalidateKeyboardImage();
        return true;
    }

    public Shift.ShiftState getShiftState() {
        Shift.ShiftState shiftState = Shift.ShiftState.OFF;
        if (this.mKeyboardSwitcher != null) {
            Shift.ShiftState shiftState2 = this.mKeyboardSwitcher.getCurrentShiftState();
            return shiftState2;
        }
        return shiftState;
    }

    public boolean isShifted() {
        Shift.ShiftState shiftState = getShiftState();
        return shiftState == Shift.ShiftState.LOCKED || shiftState == Shift.ShiftState.ON;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Shift.ShiftState capsModeToShiftState(int capsMode) {
        switch (capsMode) {
            case HardKeyboardManager.META_CTRL_ON /* 4096 */:
                return Shift.ShiftState.LOCKED;
            case 8192:
            case HardKeyboardManager.META_CTRL_RIGHT_ON /* 16384 */:
                return Shift.ShiftState.ON;
            default:
                return Shift.ShiftState.OFF;
        }
    }

    public void setPressDownPreviewEnabled(boolean previewEnabled) {
        this.isPressDownPreviewEnabled = this.mPreviewLayout != 0 && previewEnabled;
    }

    public boolean isPressDownPreviewEnabled() {
        return this.isPressDownPreviewEnabled;
    }

    protected void setPopupParent(View v) {
        this.mPopupParent = v;
    }

    public void setPopupOffset(int x, int y) {
        this.mMiniKeyboardOffsetX = x;
        this.mMiniKeyboardOffsetY = y;
        dismissPreviewPopup();
    }

    public void setProximityCorrectionEnabled(boolean enabled) {
        this.mProximityCorrectOn = enabled;
    }

    public boolean isProximityCorrectionEnabled() {
        return this.mProximityCorrectOn;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        this.mHandler.sendEmptyMessage(MSG_REMOVE_POPUPVIEW);
        if (this.mIme != null) {
            this.mIme.dragLock(false);
        }
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mKeyboard == null) {
            setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
            return;
        }
        int width = this.mKeyboard.getMinWidth() + getPaddingLeft() + getPaddingRight();
        if (View.MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
            width = View.MeasureSpec.getSize(widthMeasureSpec);
        }
        setMeasuredDimension(width, this.mKeyboard.getHeight() + getPaddingTop() + getPaddingBottom());
    }

    @Override // android.view.View
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        log.d("onSizeChanged(): w: " + width + "; h: " + height);
        discardBackCanvas();
    }

    private void discardBackCanvas() {
        if (this.mBuffer != null) {
            if (this.bufferManager != null) {
                int key = (this.mBuffer.getWidth() << 16) + this.mBuffer.getHeight();
                this.bufferManager.removeObjectFromCache(key);
            }
            this.mBuffer.recycle();
            this.mBuffer = null;
        }
        this.mCanvas = null;
    }

    private void ensureBackCanvas(int width, int height) {
        if (this.doubleBuffered) {
            if (this.mBuffer != null && this.mBuffer.isRecycled()) {
                log.d("ensureBackCanvas(): WARNING: discarding recycled back buffer");
                this.mBuffer = null;
                this.mCanvas = null;
            }
            if (this.mCanvas == null) {
                log.d("ensureBackCanvas(): create buffer: " + width + "x" + height);
                if (this.bufferManager != null) {
                    int key = (width << 16) + height;
                    this.mBuffer = (Bitmap) this.bufferManager.getObjectFromCache(key);
                    if (this.mBuffer == null || this.mBuffer.isRecycled()) {
                        this.mBuffer = BitmapUtil.createDeviceOptimizedBitmap(width, height, this.lowEndDeviceBuild);
                        this.bufferManager.addObjectToCache(key, this.mBuffer);
                    }
                } else {
                    this.mBuffer = BitmapUtil.createDeviceOptimizedBitmap(width, height, this.lowEndDeviceBuild);
                }
                this.mDirtyRect.union(0, 0, width, height);
                this.mCanvas = new Canvas(this.mBuffer);
            }
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Paint paint = null;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0) {
            ensureBackCanvas(width, height);
            boolean applyDragPaintViaLayer = (this.doubleBuffered || this.dragHelper == null || !this.dragHelper.shouldUseDragPaint(this)) ? false : true;
            if (applyDragPaintViaLayer) {
                canvas.saveLayer(null, this.dragHelper.getDragPaint(), 16);
            }
            Canvas drawCanvas = this.doubleBuffered ? this.mCanvas : canvas;
            bufferDrawKeyboard(drawCanvas);
            drawPaddingBackground(drawCanvas);
            if (this.doubleBuffered) {
                if (!applyDragPaintViaLayer && this.dragHelper != null) {
                    paint = this.dragHelper.getDragPaint();
                }
                canvas.drawBitmap(this.mBuffer, 0.0f, 0.0f, paint);
            }
            if (applyDragPaintViaLayer) {
                canvas.restore();
            }
            if (isSwypingSupportedAndEnabled()) {
                bufferDrawTrace(canvas);
            }
        }
    }

    public void setDrawBufferManager(DrawBufferManager buffer) {
        this.bufferManager = buffer;
    }

    public boolean isDrawBufferManagerSet() {
        return this.bufferManager != null;
    }

    public void abortKey() {
        this.mAbortKey = true;
    }

    private float calcKeyTextScaleFactor(boolean isPortrait) {
        if (this.mKeyboard.isBamarLetterCategory()) {
            return 0.8f;
        }
        if (this.mKeyboard.isKeyWidthReducedDockMode()) {
            if (this.mKeyboard.isMLLetterCategory() || this.mKeyboard.isKNLetterCategory() || this.mKeyboard.isTALetterCategory()) {
                return 0.65f;
            }
            if (this.mKeyboard.isGULetterCategory()) {
                return 0.7f;
            }
            if (this.mKeyboard.isRussianOrUkrainLetterCategory()) {
                return 0.95f;
            }
            if (this.mKeyboard.isTHLetterCategory()) {
                if (!isPortrait) {
                    return 0.9f;
                }
            } else if (this.mKeyboard.isIndianLetterCategory() || this.mKeyboard.isKMLetterCategory()) {
                return 0.82f;
            }
        } else if (isPortrait) {
            if (this.mKeyboard.isMLLetterCategory() || this.mKeyboard.isTALetterCategory()) {
                return 0.78f;
            }
        } else if (this.mKeyboard.isTHLetterCategory()) {
            return 0.9f;
        }
        return 1.0f;
    }

    public Typeface getKeyTypeface() {
        return UserPreferences.from(getContext()).getKeyboardFontBold() ? this.typefaceBold : this.typefaceNormal;
    }

    private void bufferDrawKeyboard(Canvas canvas) {
        boolean z;
        if (this.mKeyboard != null) {
            if (!this.doubleBuffered || (canvas != null && this.mBuffer != null)) {
                KeyboardEx.Row currentRow = null;
                Paint paint = this.mKeyPaint;
                Rect padding = this.mPadding;
                int kbdPaddingLeft = getPaddingLeft();
                int kbdPaddingTop = getPaddingTop();
                KeyboardEx.Key[] keys = this.mKeys;
                if (!this.doubleBuffered) {
                    canvas.getClipBounds(this.mDirtyRect);
                }
                if (!this.mDirtyRect.isEmpty()) {
                    canvas.save();
                    paint.setTypeface(getKeyTypeface());
                    paint.setAlpha(255);
                    paint.setAntiAlias(true);
                    if (this.doubleBuffered) {
                        canvas.clipRect(this.mDirtyRect);
                        canvas.drawColor(-16777216, PorterDuff.Mode.CLEAR);
                    }
                    boolean hideSecondary = false;
                    if (IMEApplication.from(getContext()).getUserPreferences().getKeyboardHideSecondaries()) {
                        hideSecondary = true;
                    }
                    boolean showNumberRow = false;
                    if (IMEApplication.from(getContext()).getUserPreferences().getShowNumberRow() && this.mIme.getCurrentInputView() != null && this.mIme.getCurrentInputView().getKeyboard().hasNumRow()) {
                        showNumberRow = true;
                    }
                    boolean isPortrait = getResources().getConfiguration().orientation == 1;
                    UserPreferences userPrefs = IMEApplication.from(getContext()).getUserPreferences();
                    if (isPortrait) {
                        z = userPrefs.getKeyboardScalePortrait() <= MIN_HIDE_SENCONDARY_KEYBOARD_SACLE;
                    } else {
                        z = userPrefs.getKeyboardScaleLandscape() <= MIN_HIDE_SENCONDARY_KEYBOARD_SACLE;
                    }
                    if (z && (this.mKeyboard.getLayoutRowCount() == 5 || (getResources().getConfiguration().screenLayout & 15) == 1)) {
                        hideSecondary = true;
                    }
                    if (isDoublePinyinMode()) {
                        hideSecondary = false;
                    }
                    float keyTestSizeScaleFactor = calcKeyTextScaleFactor(isPortrait);
                    int overrideHorizontalPadding = -1;
                    if (this.mKeyboard.isKeyWidthReducedDockMode() && (this.mKeyboard.isIndianLetterCategory() || this.mKeyboard.isMLLetterCategory() || this.mKeyboard.isKNLetterCategory() || this.mKeyboard.isKMLetterCategory() || this.mKeyboard.isGULetterCategory() || this.mKeyboard.isTALetterCategory() || (!isPortrait && this.mKeyboard.isTHLetterCategory()))) {
                        overrideHorizontalPadding = 2;
                    }
                    float baseLineScaleAdjust = 1.0f;
                    if (!isPortrait && this.mKeyboard.isKeyboardFullDockMode() && this.mKeyboard.isTHLetterCategory()) {
                        baseLineScaleAdjust = 1.5f;
                    }
                    List<KeyboardEx.Key> pressedKeys = new ArrayList<>();
                    Configuration config = getResources().getConfiguration();
                    int length = keys.length;
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 >= length) {
                            break;
                        }
                        KeyboardEx.Key key = keys[i2];
                        if (hideSecondary) {
                            key.baseline = getResources().getDimensionPixelSize(R.dimen.key_content_no_baseline);
                        }
                        if (showNumberRow && !hideSecondary && isKeyboardHeightWithinThresholdValue() && ((config.screenLayout & 15) == 2 || (config.screenLayout & 15) == 1)) {
                            key.altTextSize = getResources().getDimensionPixelSize(R.dimen.alt_text_size_no_row_on);
                            key.altIconSize = getResources().getDimensionPixelSize(R.dimen.alt_emoji_icon_size_small);
                        }
                        if (currentRow != key.row) {
                            currentRow = key.row;
                            drawRow(canvas, currentRow);
                        }
                        if (key.pressed) {
                            pressedKeys.add(key);
                        } else {
                            drawKey(canvas, paint, key, hideSecondary, padding, overrideHorizontalPadding, kbdPaddingLeft, kbdPaddingTop, keyTestSizeScaleFactor, baseLineScaleAdjust);
                        }
                        i = i2 + 1;
                    }
                    Iterator<KeyboardEx.Key> it = pressedKeys.iterator();
                    while (it.hasNext()) {
                        drawKey(canvas, paint, it.next(), hideSecondary, padding, overrideHorizontalPadding, kbdPaddingLeft, kbdPaddingTop, keyTestSizeScaleFactor, baseLineScaleAdjust);
                    }
                    canvas.restore();
                }
                this.mDirtyRect.setEmpty();
            }
        }
    }

    public void updateKeyboardBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    private void drawRow(Canvas canvas, KeyboardEx.Row row) {
        Drawable rowBackground = row.rowBackground;
        if (rowBackground != null) {
            rowBackground.setBounds(0, row.getTop(), getRight(), row.getBottom());
            rowBackground.draw(canvas);
        }
    }

    protected void drawKey(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, boolean hideSecondary, Rect padding, int overrideHorPadding, int kbdPaddingLeft, int kbdPaddingTop, float keyTextSizeScaleFactor, float baseLineScaleAdjust) {
        Drawable keyBackground;
        if (key.visible && this.mDirtyRect.intersects(key.x, key.y, key.x + key.width, key.y + key.height)) {
            int highLightedKeyArea = getHighlightedKeyArea(key);
            if (this.isEmojiStylePopupKeyboard) {
                keyBackground = this.mKeyBackground != null ? this.mKeyBackground : key.mKeyBackground;
            } else {
                keyBackground = key.mKeyBackground != null ? key.mKeyBackground : this.mKeyBackground;
            }
            keyBackground.getPadding(padding);
            int[] drawableState = key.getCurrentDrawableState(!isWriting());
            keyBackground.setState(drawableState);
            Rect keyBounds = key.getVisibleBounds();
            keyboardCanvas.save();
            keyboardCanvas.translate(key.x + kbdPaddingLeft + keyBounds.left, key.y + kbdPaddingTop + keyBounds.top);
            drawKeyBackground(keyboardCanvas, key, keyBackground, keyBounds);
            drawKeyBorder(keyboardCanvas, key);
            this.bounds.set(keyBounds);
            GeomUtil.shrink(this.bounds, padding);
            if (displaysAltSymbol(key)) {
                CharSequence csAltLabel = getAltLabel(key);
                String altLabel = csAltLabel != null ? csAltLabel.toString() : null;
                paint.setTextSize(key.altTextSize);
                Paint.FontMetrics fm = paint.getFontMetrics();
                if (!hideSecondary) {
                    Drawable altIcon = getAltIcon(key);
                    if (altIcon != null) {
                        drawKeyAltIcon(keyboardCanvas, paint, key, highLightedKeyArea, altLabel, this.bounds, altIcon, fm);
                    } else if (!TextUtils.isEmpty(altLabel)) {
                        drawKeyAltLabel(keyboardCanvas, paint, key, highLightedKeyArea, altLabel, this.bounds, fm, 48);
                    }
                    String leftAltLabel = key.leftAltLabel != null ? key.leftAltLabel.toString() : null;
                    if (!TextUtils.isEmpty(leftAltLabel)) {
                        drawKeyLeftAltLabel(keyboardCanvas, paint, key, highLightedKeyArea, leftAltLabel, this.bounds, fm, 80);
                    }
                } else if (key.altCode == 4087) {
                    drawKeyAltLabel(keyboardCanvas, paint, key, highLightedKeyArea, altLabel, this.bounds, fm, 48);
                }
            }
            drawKeyLabel(keyboardCanvas, paint, key, padding, overrideHorPadding, keyTextSizeScaleFactor, baseLineScaleAdjust, highLightedKeyArea, drawableState, keyBounds);
            drawKeyIcon(keyboardCanvas, paint, key, padding, keyTextSizeScaleFactor, baseLineScaleAdjust, drawableState, keyBounds);
            keyboardCanvas.restore();
        }
    }

    @SuppressLint({"RtlHardcoded"})
    private void drawKeyAltLabel(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, int highLightKeyArea, String altLabel, Rect keyBounds, Paint.FontMetrics fm, int vGravity) {
        int iAltLabelY;
        int iAltLabelX;
        if (!TextUtils.isEmpty(altLabel)) {
            sanitizeFontSize(altLabel, paint, false);
            int availableWidth = keyBounds.width();
            shrinkTextToFit(altLabel, availableWidth, paint);
            paint.getTextBounds(altLabel, 0, altLabel.length(), this.outRect);
            int iAltLabelY2 = keyBounds.top;
            switch (vGravity & 112) {
                case 80:
                    iAltLabelY = (((keyBounds.height() - ((int) fm.descent)) - this.secondary_padding_adjustment) + iAltLabelY2) - DrawingUtils.getExcessBelowDescent(this.outRect, fm);
                    break;
                default:
                    iAltLabelY = ((int) (-fm.ascent)) + this.secondary_padding_adjustment + iAltLabelY2 + DrawingUtils.getExcessAboveAscent(this.outRect, fm);
                    break;
            }
            int iAltLabelX2 = keyBounds.left;
            switch (key.altTextGravity & 7) {
                case 3:
                case 8388611:
                    iAltLabelX = iAltLabelX2 + this.secondary_padding_adjustment;
                    paint.setTextAlign(Paint.Align.LEFT);
                    break;
                case 5:
                case 8388613:
                    iAltLabelX = iAltLabelX2 + (keyBounds.width() - this.secondary_padding_adjustment);
                    paint.setTextAlign(Paint.Align.RIGHT);
                    break;
                default:
                    iAltLabelX = iAltLabelX2 + (keyBounds.width() / 2);
                    paint.setTextAlign(Paint.Align.CENTER);
                    break;
            }
            int iAltLabelX3 = iAltLabelX + DrawingUtils.hAdjustCharAlignment(altLabel, this.outRect, key.altTextGravity, paint);
            if (this.mAltShadowRadius != 0.0f) {
                paint.setColor(this.mAltShadowColor);
                drawText(keyboardCanvas, altLabel, iAltLabelX3, iAltLabelY - (this.mAltShadowRadius * getContext().getResources().getDisplayMetrics().density), paint, false);
            }
            if ((HighLightKeyArea.VOWEL_ALTLABLE.getValue() & highLightKeyArea) == HighLightKeyArea.VOWEL_ALTLABLE.getValue() || (HighLightKeyArea.CONSONANT_ALTLABLE.getValue() & highLightKeyArea) == HighLightKeyArea.CONSONANT_ALTLABLE.getValue()) {
                paint.setColor(this.miHighlightTextColor);
            } else {
                paint.setColor(key.altTextColor);
            }
            drawText(keyboardCanvas, altLabel, iAltLabelX3, iAltLabelY, paint, false);
        }
    }

    @SuppressLint({"RtlHardcoded"})
    private void drawKeyLeftAltLabel(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, int highLightKeyArea, String leftAltLabel, Rect keyBounds, Paint.FontMetrics fm, int vGravity) {
        int iAltLabelY;
        int iAltLabelX;
        if (!TextUtils.isEmpty(leftAltLabel)) {
            if (this.mKeyboard.isBamarLetterCategory()) {
                paint.setTextSize(key.mKeyTextSize * 0.8f);
            }
            sanitizeFontSize(leftAltLabel, paint, false);
            int availableWidth = keyBounds.width();
            shrinkTextToFit(leftAltLabel, availableWidth, paint);
            paint.getTextBounds(leftAltLabel, 0, leftAltLabel.length(), this.outRect);
            int iAltLabelY2 = keyBounds.top;
            switch (vGravity & 112) {
                case 80:
                    iAltLabelY = (((keyBounds.height() - ((int) fm.descent)) - this.secondary_padding_adjustment) + iAltLabelY2) - DrawingUtils.getExcessBelowDescent(this.outRect, fm);
                    break;
                default:
                    iAltLabelY = ((int) (-fm.ascent)) + this.secondary_padding_adjustment + iAltLabelY2 + DrawingUtils.getExcessAboveAscent(this.outRect, fm);
                    break;
            }
            int iAltLabelX2 = keyBounds.left;
            switch (key.altTextGravity & 7) {
                case 3:
                case 8388611:
                    iAltLabelX = iAltLabelX2 + this.secondary_padding_adjustment;
                    paint.setTextAlign(Paint.Align.LEFT);
                    break;
                case 5:
                case 8388613:
                    iAltLabelX = iAltLabelX2 + (keyBounds.width() - this.secondary_padding_adjustment);
                    paint.setTextAlign(Paint.Align.RIGHT);
                    break;
                default:
                    iAltLabelX = iAltLabelX2 + (keyBounds.width() / 2);
                    paint.setTextAlign(Paint.Align.CENTER);
                    break;
            }
            int iAltLabelX3 = iAltLabelX + DrawingUtils.hAdjustCharAlignment(leftAltLabel, this.outRect, key.altTextGravity, paint);
            if (this.mAltShadowRadius != 0.0f) {
                paint.setColor(this.mAltShadowColor);
                drawText(keyboardCanvas, leftAltLabel, iAltLabelX3, iAltLabelY - (this.mAltShadowRadius * getContext().getResources().getDisplayMetrics().density), paint, false);
            }
            if ((HighLightKeyArea.VOWEL_LEFTALTLABLE.getValue() & highLightKeyArea) == HighLightKeyArea.VOWEL_LEFTALTLABLE.getValue() || (HighLightKeyArea.CONSONANT_LEFTALTLABLE.getValue() & highLightKeyArea) == HighLightKeyArea.CONSONANT_LEFTALTLABLE.getValue()) {
                paint.setColor(this.miHighlightTextColor);
            } else {
                paint.setColor(key.altTextColor);
            }
            drawText(keyboardCanvas, leftAltLabel, iAltLabelX3, iAltLabelY, paint, false);
        }
    }

    @SuppressLint({"RtlHardcoded"})
    private void drawKeyAltIcon(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, int highLightKeyArea, String altLabel, Rect keyBounds, Drawable altIcon, Paint.FontMetrics fm) {
        int iAltIconX;
        altIcon.setState(ICON_STATE_SECONDARY);
        int altIconWidth = altIcon.getIntrinsicWidth();
        int altIconHeight = altIcon.getIntrinsicHeight();
        if (key.altIconSize != 0) {
            paint.setTextSize(key.altIconSize);
        }
        double size = Math.ceil(paint.getTextSize() - paint.descent());
        int altIconWidth2 = (int) ((size / altIconHeight) * altIconWidth);
        int altIconHeight2 = (int) size;
        int w = keyBounds.width();
        if (altIconWidth2 > w) {
            altIconHeight2 = (w * altIconHeight2) / altIconWidth2;
            altIconWidth2 = w;
        }
        int horGravity = key.altTextGravity & 7;
        if (!TextUtils.isEmpty(altLabel) && key.altCode == 4087) {
            drawKeyAltLabel(keyboardCanvas, paint, key, highLightKeyArea, altLabel, keyBounds, fm, 48);
            horGravity = 8388613;
        }
        int altLabelBaselineOffset = Math.max(((int) (-fm.ascent)) + this.secondary_padding_adjustment, altIconHeight2);
        int iAltIconY = (keyBounds.top + altLabelBaselineOffset) - altIconHeight2;
        int iAltIconX2 = keyBounds.left;
        switch (horGravity) {
            case 3:
            case 8388611:
                iAltIconX = iAltIconX2 + this.secondary_padding_adjustment;
                break;
            case 5:
            case 8388613:
                iAltIconX = iAltIconX2 + ((keyBounds.width() - altIconWidth2) - this.secondary_padding_adjustment);
                break;
            default:
                iAltIconX = iAltIconX2 + ((keyBounds.width() - altIconWidth2) / 2);
                break;
        }
        altIcon.setBounds(iAltIconX, iAltIconY, iAltIconX + altIconWidth2, iAltIconY + altIconHeight2);
        if (key.dimmed) {
            altIcon.setAlpha(120);
        } else {
            altIcon.setAlpha(255);
        }
        altIcon.draw(keyboardCanvas);
    }

    private void drawKeyBackground(Canvas keyboardCanvas, KeyboardEx.Key key, Drawable keyBackground, Rect keyBounds) {
        boolean drawGlow = this.mGlow == null ? false : key.isPressed();
        if (drawGlow) {
            keyboardCanvas.save();
            int glowPaddingX = getGlowPaddingX(key);
            int glowPaddingY = getGlowPaddingY(key);
            keyboardCanvas.clipRect(-glowPaddingX, -glowPaddingY, key.width + glowPaddingX, key.height + glowPaddingY);
        }
        Rect bkgBounds = keyBackground.getBounds();
        if (keyBounds.width() != bkgBounds.right || keyBounds.height() != bkgBounds.bottom) {
            keyBackground.setBounds(0, 0, keyBounds.width(), keyBounds.height());
        }
        keyBackground.draw(keyboardCanvas);
        if (drawGlow) {
            keyboardCanvas.restore();
        }
    }

    private void drawKeyLabel(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, Rect padding, int overrideHorPadding, float keyTextSizeScaleFactor, float baseLineScaleAdjust, int highLightKeyArea, int[] drawableState, Rect keyBounds) {
        int iLabelY;
        CharSequence csLabel = getKeyLabel(key);
        String label = csLabel != null ? csLabel.toString() : null;
        if (label != null) {
            boolean isComponent = label.length() > 0 && label.charAt(0) == 40959;
            if (isComponent) {
                label = label.substring(1);
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(key.mKeyTextSize * keyTextSizeScaleFactor);
            Paint.FontMetrics fm = paint.getFontMetrics();
            int availableWidth = keyBounds.width();
            int horPadding = 0;
            if (padding.right == 0 && padding.left == 0) {
                if (this.mKeyboard.isKeyWidthReducedDockMode()) {
                    horPadding = padding.top * 2;
                }
            } else {
                horPadding = overrideHorPadding >= 0 ? overrideHorPadding : padding.right + padding.left;
            }
            shrinkLabelTextToFit(label, availableWidth - horPadding, paint);
            if (key.baseline > 0) {
                iLabelY = keyBounds.height() - ((int) (key.baseline * baseLineScaleAdjust));
            } else {
                iLabelY = (int) (((keyBounds.height() - fm.ascent) - fm.descent) / 2.0f);
            }
            paint.getTextBounds(label, 0, label.length(), this.outRect);
            int adjust = DrawingUtils.getExcessAboveAscent(this.outRect, fm);
            if (adjust == 0) {
                adjust = -DrawingUtils.getExcessBelowDescent(this.outRect, fm);
            }
            int iLabelY2 = iLabelY + adjust;
            int iLabelX = keyBounds.width() / 2;
            paint.getTextBounds(label, 0, label.length(), this.outRect);
            int iLabelX2 = iLabelX + DrawingUtils.hAdjustCharAlignment(label, this.outRect, 17, paint);
            if (!this.isPopupKeyboard || this.isContextCandidatesView) {
                this.textShadow.set(paint);
            }
            if (isComponent) {
                paint.setUnderlineText(false);
                paint.setColor(key.mDefaultStrokeCandidateColor);
            } else {
                paint.setUnderlineText(false);
                if (highLightKeyArea != 0 && (HighLightKeyArea.VOWEL_LABLE.getValue() & highLightKeyArea) == HighLightKeyArea.VOWEL_LABLE.getValue()) {
                    paint.setColor(this.miHighlightTextColor);
                } else if (key.isMiniKeyboardKey || (key.row != null && key.row.parent != null && key.row.parent.mIsPopup)) {
                    if (key.isPressed()) {
                        paint.setColor(key.keyTextColorPressed);
                    } else {
                        paint.setColor(key.tertiaryTextColor);
                    }
                } else {
                    paint.setColor(key.mKeyTextColor.getColorForState(drawableState, 0));
                }
            }
            drawText(keyboardCanvas, label, iLabelX2, iLabelY2, paint, key.hasMultilineLabel);
            if (!this.isPopupKeyboard || this.isContextCandidatesView) {
                this.textShadow.reset(paint);
            }
        }
    }

    private void drawKeyBorder(Canvas keyboardCanvas, KeyboardEx.Key key) {
        if (this.mKeyBorder != null) {
            keyboardCanvas.save();
            keyboardCanvas.translate(key.width - this.mKeyBorder.getIntrinsicWidth(), 0.0f);
            this.mKeyBorder.setBounds(0, 0, this.mKeyBorder.getIntrinsicWidth(), key.height);
            this.mKeyBorder.draw(keyboardCanvas);
            keyboardCanvas.restore();
            if (key.y + key.height <= this.mKeyboard.getHeight()) {
                keyboardCanvas.save();
                if (key.y + key.height < this.mKeyboard.getHeight()) {
                    keyboardCanvas.translate(0.0f, key.height - (this.mKeyBorder.getIntrinsicHeight() / 2));
                } else {
                    keyboardCanvas.translate(0.0f, key.height - this.mKeyBorder.getIntrinsicHeight());
                }
                this.mKeyBorder.setBounds(0, 0, key.width, this.mKeyBorder.getIntrinsicHeight());
                this.mKeyBorder.draw(keyboardCanvas);
                keyboardCanvas.restore();
            }
        }
    }

    private static boolean shouldDrawIconBelowAlt(KeyboardEx.Key key) {
        return false;
    }

    private void drawKeyIcon(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, Rect padding, float keyTextSizeScaleFactor, float baseLineScaleAdjust, int[] drawableState, Rect keyBounds) {
        int iconGrav;
        Drawable icon = getKeyIcon(key);
        if (icon != null) {
            this.bounds.set(keyBounds);
            GeomUtil.shrink(this.bounds, padding);
            int altHeight = key.altTextSize;
            boolean adjustBelowAlt = hasAltSymbol(key) && shouldDrawIconBelowAlt(key);
            if (key.baseline > 0) {
                if (adjustBelowAlt) {
                    this.bounds.top += altHeight;
                }
                this.bounds.bottom = keyBounds.height() - ((int) (key.baseline * baseLineScaleAdjust));
                iconGrav = 81;
            } else {
                if (adjustBelowAlt) {
                    this.bounds.top += altHeight;
                    this.bounds.bottom -= altHeight;
                }
                iconGrav = 17;
            }
            drawKeyIcon(keyboardCanvas, paint, key, keyTextSizeScaleFactor, drawableState, icon, this.bounds, iconGrav);
        }
    }

    private void drawKeyIcon(Canvas keyboardCanvas, Paint paint, KeyboardEx.Key key, float keyTextSizeScaleFactor, int[] drawableState, Drawable icon, Rect drawBounds, int iconGrav) {
        icon.setState(ArraysCompat.addAll(ICON_STATE_PRIMARY, drawableState));
        this.dest.set(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        if (icon instanceof NinePatchDrawable) {
            this.dest.right = drawBounds.width() / 2;
        } else {
            if (!TextUtils.isEmpty(getText(key))) {
                paint.setTextSize(key.mKeyTextSize * keyTextSizeScaleFactor);
                float fontHeight = paint.getTextSize();
                if ((this.dest.width() > fontHeight || this.dest.height() > fontHeight) && key.codes != null && key.codes[0] != 4074) {
                    double maxHeight = Math.ceil(fontHeight - paint.descent());
                    int scaledWidth = (int) ((maxHeight / this.dest.height()) * this.dest.width());
                    GeomUtil.setSize(this.dest, scaledWidth, (int) maxHeight);
                }
            }
            if (key.codes != null && key.codes[0] == 4074 && getKeyboard().isKeyWidthReducedDockMode() && !getKeyboard().mIsPopup) {
                paint.setTextSize(key.mKeyTextSize * keyTextSizeScaleFactor * 1.5f);
                float fontHeight2 = paint.getTextSize();
                if (this.dest.width() > fontHeight2 || this.dest.height() > fontHeight2) {
                    double maxHeight2 = Math.ceil(fontHeight2 - paint.descent());
                    int scaledWidth2 = (int) ((maxHeight2 / this.dest.height()) * this.dest.width());
                    GeomUtil.setSize(this.dest, scaledWidth2, (int) maxHeight2);
                }
            }
        }
        Rect rect = this.dest;
        int width = drawBounds.width();
        int height = drawBounds.height();
        int width2 = rect.width();
        int height2 = rect.height();
        int i = width2 - width;
        int i2 = height2 - height;
        if (i > i2) {
            if (i > 0) {
                height2 = (height2 * width) / width2;
                GeomUtil.setSize(rect, width, height2);
                width2 = width;
            }
        } else if (i2 > 0) {
            int i3 = (width2 * height) / height2;
            GeomUtil.setSize(rect, i3, height);
            width2 = i3;
            height2 = height;
        }
        GeomUtil.moveRectTo(rect, GeomUtil.getOffsetX(width, width2, iconGrav) + drawBounds.left, GeomUtil.getOffsetY(height, height2, iconGrav) + drawBounds.top);
        icon.setBounds(0, 0, this.dest.width(), this.dest.height());
        keyboardCanvas.save();
        keyboardCanvas.translate(this.dest.left, this.dest.top);
        if (key.dimmed) {
            icon.setAlpha(120);
        } else {
            icon.setAlpha(255);
        }
        if (key.isMiniKeyboardKey || (key.row != null && key.row.parent != null && key.row.parent.mIsPopup)) {
            if (key.isPressed()) {
                icon.mutate().setColorFilter(key.keyTextColorPressed, PorterDuff.Mode.SRC_ATOP);
            } else {
                icon.mutate().setColorFilter(key.tertiaryTextColor, PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            icon.clearColorFilter();
            recolorIconUsingTheme(icon, key.keyIconRecolor, key.type);
        }
        icon.draw(keyboardCanvas);
        keyboardCanvas.restore();
    }

    private void recolorIconUsingTheme(Drawable icon, int color, int keyType) {
        int finalColor;
        if ((color != 0 || this.mIconRecolor != 0 || this.mIconRecolorFunction != 0) && color != KEY_ICON_SKIP_RECOLOR) {
            if (color != 0) {
                finalColor = color;
            } else {
                finalColor = keyType == 4 ? this.mIconRecolorFunction : this.mIconRecolor;
            }
            recolorIcon(icon, finalColor);
        }
    }

    private void recolorIcon(Drawable icon, int color) {
        icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void recolorPopupIcon(Drawable icon) {
        recolorIcon(icon, this.mPopupKeyTextColor);
    }

    private void sanitizeFontSize(String text, Paint paint, boolean isLanguageMenuKey) {
        if (this.sanitizingFont) {
            float adjustment = 1.0f;
            if (!isLanguageMenuKey && (this.mKeyboard.isIndianLetterCategory() || this.mKeyboard.isKNLetterCategory() || this.mKeyboard.isMLLetterCategory() || this.mKeyboard.isTALetterCategory() || this.mKeyboard.isGULetterCategory() || this.mKeyboard.isKMLetterCategory() || this.mKeyboard.isBamarLetterCategory())) {
                if (this.mKeyboard.isKeyWidthReducedDockMode()) {
                    if (this.mKeyboard.isMLLetterCategory() || this.mKeyboard.isTALetterCategory() || this.mKeyboard.isKNLetterCategory()) {
                        adjustment = 0.95f;
                    } else if (this.mKeyboard.isKMLetterCategory()) {
                        adjustment = 0.85f;
                    } else if (this.mKeyboard.isGULetterCategory()) {
                        adjustment = 1.1f;
                    } else {
                        adjustment = 1.2f;
                    }
                } else if (this.mKeyboard.isKMLetterCategory()) {
                    adjustment = 0.95f;
                } else {
                    adjustment = 1.2f;
                }
                if (this.mKeyboard.isBamarLetterCategory()) {
                    adjustment = 0.8f;
                }
            } else if (text.length() == 1 && Character.getType(text.charAt(0)) == 6) {
                float maxWidth = paint.measureText("H") * 0.75f;
                Rect bounds = new Rect();
                paint.getTextBounds(text, 0, text.length(), bounds);
                adjustment = Math.min(2.0f, maxWidth / bounds.width());
            }
            if (adjustment != 1.0f) {
                float textSize = paint.getTextSize() * adjustment;
                paint.setTextSize(textSize);
            }
        }
    }

    public void bufferDrawTrace(Canvas canvas) {
        for (int i = 0; i < this.mHshPointers.size(); i++) {
            Pointer pointer = this.mHshPointers.get(i);
            if (pointer != null && pointer.isTraceDetected() && pointer.renderingTracePoints.size() > 0) {
                if (this.mTracePaint == null) {
                    this.mTracePaint = new Paint(1);
                    this.mTracePaint.setStyle(Paint.Style.STROKE);
                    this.mTracePaint.setStrokeCap(Paint.Cap.ROUND);
                    this.mTracePaint.setStrokeJoin(Paint.Join.ROUND);
                    this.mTracePaint.setStrokeWidth(this.mTraceWidth);
                }
                float fadeScale = pointer.fading / 255.0f;
                float widthScaled = this.mTraceWidth * fadeScale;
                if (widthScaled > 0.0f) {
                    Paint paint = this.mTracePaint;
                    paint.setColor(this.miTraceColor);
                    Path path = this.enableTraceAlphaGradient ? tracePath(pointer.renderingTracePoints.getPoints()) : bezierTracePath(pointer.renderingTracePoints.getPoints());
                    path.offset(getPaddingLeft(), getPaddingTop());
                    if (this.enableTraceAlphaGradient) {
                        float density = getResources().getDisplayMetrics().density;
                        float scale = (4.0f * density) / 2.0f;
                        float widthStart = scale * fadeScale;
                        paint.setAlpha(paint.getAlpha() / 2);
                        for (float width = widthStart; width <= widthScaled; width += scale) {
                            paint.setStrokeWidth(width);
                            canvas.drawPath(path, paint);
                            int alpha = Math.max(paint.getAlpha() - 4, 0);
                            paint.setAlpha(alpha);
                            if (alpha != 0) {
                            }
                        }
                    } else {
                        paint.setStrokeWidth(widthScaled);
                        canvas.drawPath(path, paint);
                    }
                }
            }
        }
    }

    private Path tracePath(List<Point> points) {
        Path path = new Path();
        int size = points.size();
        Point point = points.get(size - 1);
        path.moveTo(point.x, point.y);
        int numPoints = getNumTracePoints(points);
        int i = size - 2;
        for (int n = 1; i >= 0 && n < numPoints; n++) {
            Point point2 = points.get(i);
            path.lineTo(point2.x, point2.y);
            i--;
        }
        return path;
    }

    private Path bezierTracePath(List<Point> points) {
        Path path = new Path();
        int size = points.size();
        Point point = points.get(size - 1);
        path.moveTo(point.x, point.y);
        int numPoints = getNumTracePoints(points);
        int i = size - 2;
        for (int n = 1; i >= 0 && n < numPoints; n++) {
            Point nextPoint = points.get(i);
            float midX = (point.x + nextPoint.x) / 2;
            float midY = (point.y + nextPoint.y) / 2;
            if (n == 1) {
                path.lineTo(midX, midY);
            } else {
                path.quadTo(point.x, point.y, midX, midY);
            }
            point = nextPoint;
            i--;
        }
        path.lineTo(point.x, point.y);
        return path;
    }

    protected int getNumTracePoints(Collection<Point> points) {
        int numPoints = points.size();
        return this.showCompleteTrace ? numPoints : Math.min(numPoints, 50);
    }

    private void shrinkTextToFit(String text, int availableWidth, Paint paint) {
        float expectedWidth = paint.measureText(text);
        if (expectedWidth > availableWidth) {
            float textSize = (paint.getTextSize() * availableWidth) / expectedWidth;
            paint.setTextSize(textSize);
        }
    }

    private void shrinkLabelTextToFit(String text, int availableWidth, Paint paint) {
        float expectedWidth = paint.measureText(text);
        if (expectedWidth > availableWidth) {
            float textSize = (paint.getTextSize() * availableWidth) / expectedWidth;
            paint.setTextSize(textSize);
        }
    }

    private void drawText(Canvas canvas, String text, float x, float y, Paint paint, boolean isMultiline) {
        if (!isMultiline) {
            canvas.drawText(text, x, y, paint);
            return;
        }
        String[] lines = text.split("\n");
        float lineHeight = paint.getTextSize() + paint.getFontMetrics().leading;
        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i], x, (i * lineHeight) + y, paint);
        }
    }

    protected int getNearestKeyIndex(int x, int y) {
        int dist;
        int[] nearestKeyIndices = this.mKeyboard.getNearestKeys(x, y);
        int keyCount = nearestKeyIndices.length;
        int smallestDist = Integer.MAX_VALUE;
        int nearest = -1;
        for (int idx = 0; idx < keyCount; idx++) {
            KeyboardEx.Key key = this.mKeys[nearestKeyIndices[idx]];
            if (key.visible && (dist = key.squaredDistanceFrom(x, y)) < smallestDist) {
                smallestDist = dist;
                nearest = nearestKeyIndices[idx];
            }
        }
        return nearest;
    }

    protected int getKeyIndexStrict(int x, int y) {
        int[] nearestKeyIndices = this.mKeyboard.getNearestKeys(x, y);
        int keyCount = nearestKeyIndices.length;
        for (int i = 0; i < keyCount; i++) {
            KeyboardEx.Key key = this.mKeys[nearestKeyIndices[i]];
            if (key.contains(x, y) && key.visible) {
                return nearestKeyIndices[i];
            }
        }
        return -1;
    }

    protected int getKeyIndices(int x, int y) {
        KeyboardEx.Key[] keys = this.mKeys;
        int[] nearestKeyIndices = this.mKeyboard.getNearestKeys(x, y);
        int keyCount = nearestKeyIndices.length;
        for (int i = 0; i < keyCount; i++) {
            KeyboardEx.Key key = keys[nearestKeyIndices[i]];
            if (key.isInside(x, y) && key.visible) {
                int primaryIndex = nearestKeyIndices[i];
                return primaryIndex;
            }
        }
        return -1;
    }

    public KeyboardEx.Key getKey(int x, int y) {
        return getKey(getKeyIndices(x, y));
    }

    public KeyboardEx.Key getKey(int index) {
        if (index < 0 || index >= this.mKeys.length) {
            return null;
        }
        return this.mKeys[index];
    }

    protected int getDefaultForAltCharsPopup(KeyboardEx.Key key, CharSequence popupChars) {
        CharSequence altLabel = getAltLabel(key);
        int idx = (!this.enableSimplifiedMode || key.alwaysShowAltSymbol) ? indexOfSingleCharLabel(altLabel, popupChars) : -1;
        if (-1 == idx) {
            return indexOfSingleCharLabel(getKeyLabel(key), popupChars);
        }
        return idx;
    }

    protected static int indexOfSingleCharLabel(CharSequence label, CharSequence popupChars) {
        if (label == null || label.length() != 1) {
            return -1;
        }
        return TextUtils.indexOf(popupChars, label.toString().toLowerCase().charAt(0));
    }

    protected boolean isShortPressState(int pointerId) {
        if (this.keyPrevManager == null) {
            return this.mAlternateCharPopup.isShowing();
        }
        KeyPreviewManager.PreviewInfo previewInfo = this.keyPrevManager.getPreviewInfo(pointerId);
        return (previewInfo != null ? previewInfo.state : 0) == 2;
    }

    public void detectAndSendKeyWrapper(int x, int y, long eventTime) {
        detectAndSendKey(x, y, eventTime);
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b7, code lost:            if (r19.mTapCount == (-1)) goto L34;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void detectAndSendKey(int r20, int r21, long r22) {
        /*
            Method dump skipped, instructions count: 279
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.KeyboardViewEx.detectAndSendKey(int, int, long):void");
    }

    public int getShiftGestureOffset() {
        return 0;
    }

    public boolean isMultitapHandledInCore() {
        return true;
    }

    private CharSequence getPreviewText(KeyboardEx.Key key) {
        if (this.mKeyboard == null) {
            return null;
        }
        if (this.mInMultiTap) {
            this.mPreviewLabel.setLength(0);
            if (key.codes.length <= (this.mTapCount < 0 ? 0 : this.mTapCount)) {
                return null;
            }
            this.mPreviewLabel.append((char) key.codes[this.mTapCount >= 0 ? this.mTapCount : 0]);
            return adjustShift(this.mPreviewLabel);
        }
        CharSequence text = key.popupLabel;
        if (text == null) {
            CharSequence text2 = getKeyLabel(key);
            if (text2 != null && text2.length() > 3) {
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < text2.length(); i++) {
                    if (text2.charAt(i) != '\n') {
                        newText.append(text2.charAt(i));
                    }
                }
                return newText;
            }
            return text2;
        }
        return text;
    }

    public void releaseKey(KeyboardEx.Key[] keys, int keyIndex, boolean inside) {
        if (keys.length > keyIndex && keys[keyIndex].pressed) {
            keys[keyIndex].onReleased(inside);
            invalidateKeyByIndex(keyIndex);
        }
    }

    public void pressKey(KeyboardEx.Key[] keys, int keyIndex) {
        if (keyIndex < keys.length && !keys[keyIndex].pressed) {
            keys[keyIndex].onPressed();
            invalidateKeyByIndex(keyIndex);
        }
    }

    public synchronized void notifyKeyboardListenerOnText(CharSequence text, long eventTime) {
        if (text != null) {
            if ((this instanceof AlphaInputView) && text.length() == 1) {
                notifyKeyboardListenerOnKey(null, text.charAt(0), null, null, eventTime);
            } else if (this.mKeyboardActionListener != null) {
                this.mKeyboardActionListener.onText(text, eventTime);
                this.mKeyboardActionListener.onRelease(-1);
            }
        }
    }

    public synchronized void notifyKeyboardListenerOnKey(Point point, int primaryCode, int[] keyCodes, KeyboardEx.Key key, long eventTime) {
        if (this.mKeyboardActionListener != null) {
            this.mKeyboardActionListener.onKey(point, primaryCode, keyCodes, key, eventTime);
        }
    }

    public int setKeyState(int keyIndex, ShowKeyState state) {
        this.mPreviousKeyIndex = this.mCurrentKeyIndex;
        this.mCurrentKeyIndex = keyIndex;
        return this.mCurrentKeyIndex;
    }

    public void showPreviewKey(int currentKeyIndex, int pointerId) {
        boolean previewEnabled;
        if (!isExploreByTouchOn()) {
            boolean isNewKey = this.mPreviousKeyIndex != currentKeyIndex;
            this.mPreviousKeyIndex = currentKeyIndex;
            if (this.keyPrevManager != null) {
                if (isNewKey) {
                    showPreviewKeyNew(currentKeyIndex, pointerId, false);
                    return;
                }
                return;
            }
            PopupWindow previewPopup = this.mPreviewPopup;
            KeyboardEx.Key key = getKey(currentKeyIndex);
            if (key != null) {
                previewEnabled = canShowPreview(key, false);
            } else {
                previewEnabled = true;
            }
            if (previewEnabled) {
                if (isNewKey || currentKeyIndex == -1 || isMultitapping()) {
                    this.mHandler.removeMessages(MSG_SHOW_PREVIEW);
                    if (previewPopup.isShowing() && currentKeyIndex == -1 && !isMultitapping() && !this.mHandler.hasMessages(MSG_REMOVE_PREVIEW)) {
                        this.mHandler.sendEmptyMessageDelayed(MSG_REMOVE_PREVIEW, this.previewHideDelay);
                    }
                    if (key != null) {
                        if (previewPopup.isShowing() || this.previewShowDelay == 0) {
                            showKey(currentKeyIndex);
                        } else if (!this.mHandler.hasMessages(MSG_SHOW_PREVIEW)) {
                            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_SHOW_PREVIEW, currentKeyIndex, 0), this.previewShowDelay);
                        }
                    }
                }
            }
        }
    }

    public void recalculateOffsets() {
        this.mOffsetInWindow = new int[2];
        getLocationInWindow(this.mOffsetInWindow);
        int[] iArr = this.mOffsetInWindow;
        iArr[0] = iArr[0] + this.mMiniKeyboardOffsetX;
        int[] iArr2 = this.mOffsetInWindow;
        iArr2[1] = iArr2[1] + this.mMiniKeyboardOffsetY;
    }

    private void showKey(int keyIndex) {
        if (this.keyPrevManager == null) {
            dismissSingleAltCharPopup();
            if (this.mPopupParent != null && this.mPopupParent.getWindowToken() == null) {
                log.d("showKey - trying to popup when parent has null window token");
                return;
            }
            KeyboardEx.Key key = configureKeyPreview(this.mPreviewView, keyIndex, false);
            if (key != null) {
                showPopup(this.mPreviewPopup, key);
            }
        }
    }

    public void dismissPreviewPopup() {
        if (this.keyPrevManager == null && this.mPreviewPopup != null && this.mPreviewPopup.isShowing()) {
            this.mPreviewPopup.dismiss();
        }
    }

    public void dismissSingleAltCharPopup() {
        if (this.keyPrevManager == null && this.mAlternateCharPopup != null && this.mAlternateCharPopup.isShowing()) {
            this.mAlternateCharPopup.dismiss();
        }
    }

    protected void showPopup(PopupWindow popupWindow, KeyboardEx.Key key) {
        if (key.showPopup) {
            if (this.mPopupParent == null || this.mPopupParent.getWindowToken() != null) {
                Rect rc = getKeyPreviewRectInWindow(popupWindow.getContentView(), key, SettingsChangeListener.isScreenMagnificationOn());
                if (popupWindow.isShowing()) {
                    popupWindow.update(rc.left, rc.top, rc.width(), rc.height());
                } else if (this.mPopupParent != null) {
                    popupWindow.setWidth(rc.width());
                    popupWindow.setHeight(rc.height());
                    popupWindow.showAtLocation(this.mPopupParent, 0, rc.left, rc.top);
                }
            }
        }
    }

    public void invalidateKeyboardImage() {
        this.mDirtyRect.union(0, 0, getWidth(), getHeight());
        invalidate();
        if (this.mIme != null) {
            if ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null) {
                this.mIme.getInputContainerView().invalidateItem();
                this.mIme.getInputContainerView().invalidate();
            }
        }
    }

    public void invalidateKey(KeyboardEx.Key key) {
        int glowPaddingX = this.mGlow == null ? 0 : getGlowPaddingX(key);
        int glowPaddingY = this.mGlow == null ? 0 : getGlowPaddingY(key);
        Rect dirtyRect = new Rect((key.x + getPaddingLeft()) - glowPaddingX, (key.y + getPaddingTop()) - glowPaddingY, key.x + key.width + getPaddingLeft() + glowPaddingX, key.y + key.height + getPaddingTop() + glowPaddingY);
        Rect keyboardRect = new Rect(0, 0, getWidth(), getHeight());
        dirtyRect.setIntersect(dirtyRect, keyboardRect);
        this.mDirtyRect.union(dirtyRect);
        invalidate(dirtyRect);
        if (this.mIme != null) {
            if ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null) {
                this.mIme.getInputContainerView().invalidateItem();
                this.mIme.getInputContainerView().invalidate();
            }
        }
    }

    private void invalidateKeyByIndex(int keyIndex) {
        KeyboardEx.Key key = getKey(keyIndex);
        if (key != null) {
            invalidateKey(key);
        }
    }

    public void showAltSymbolPopup(KeyboardEx.Key key, int pointerId) {
        if (this.keyPrevManager != null) {
            showPreviewKeyNew(key, pointerId, true);
            return;
        }
        if (this.mPopupParent != null && this.mPopupParent.getWindowToken() == null) {
            log.d("showAltLabelPopup():  trying to popup when parent has null window token");
        } else if (configureKeyPreview(this.mAlternatePreviewView, key, true)) {
            showPopup(this.mAlternateCharPopup, key);
        }
    }

    public boolean popupMiniKeyboardOrLongPress() {
        if (isMultitapping()) {
            if (this.mTapCount >= 0) {
                multitapTimeOut();
            } else {
                this.mHandler.removeMessages(106);
                resetMultiTap();
            }
        }
        if (this.mPopupLayout == 0) {
            log.d("popupMiniKeyboardOrLongPress(): no layout");
            return false;
        }
        KeyboardEx.Key popupKey = getKey(this.mCurrentKey);
        if (popupKey == null) {
            log.d("popupMiniKeyboardOrLongPress(): invalid current key");
            return false;
        }
        if (popupKey != null) {
            boolean hasAltKeyEvent = popupKey.altCode != 4063;
            if (popupKey.popupCharacters == null && (!hasAltKeyEvent || popupKey.isAltPopupKept)) {
                return false;
            }
        }
        return onLongPress(popupKey);
    }

    private Rect getDisplaySize() {
        if (this.displaySize == null) {
            this.displaySize = IMEApplication.from(getContext()).getDisplayRectSize(new Rect());
        }
        return this.displaySize;
    }

    protected int getScreenWidth() {
        return getDisplaySize().width();
    }

    protected int getScreenHeight() {
        return getDisplaySize().height();
    }

    public boolean isLongPressableBackspaceKey(KeyboardEx.Key pressedkey) {
        if (this.mIme == null || this.mIme.getCurrentInputView() == null) {
            return false;
        }
        boolean isSupportingAutoSpace = this.mIme.getCurrentInputView().getCurrentInputLanguage().isAutoSpaceSupported();
        return 8 == pressedkey.codes[0] && isSupportingAutoSpace;
    }

    protected void resetDynamicDeleteKeyRepeatable(KeyboardEx.Key pressedkey) {
        if (this.mKeyboardSwitcher != null) {
            if ((this.mKeyboardSwitcher.isEditMode() || this.mKeyboardSwitcher.isNumMode() || this.mKeyboardSwitcher.isPhoneMode()) && 8 == pressedkey.codes[0]) {
                if (this.mIme.getCurrentInputView().getCurrentInputLanguage().isAutoSpaceSupported()) {
                    pressedkey.repeatable = false;
                } else {
                    pressedkey.repeatable = true;
                }
            }
        }
    }

    protected void handleLongPressableBackspaceKey(KeyboardEx.Key key, int keyIndex, int pointerId) {
        this.mHandler.sendEmptyMessageDelayed(MSG_REMOVE_PREVIEW, 60L);
        showAltSymbolPopup(key, pointerId);
        if (!this.mHandler.hasMessages(104)) {
            this.mKeyRepeated = true;
            this.repeatCount++;
            int timeOut = 100;
            if (!isExploreByTouchOn()) {
                int expValue = (int) Math.exp(this.repeatCount);
                if (expValue >= 0 && expValue < this.mShortLongPressTimeout) {
                    timeOut = this.mShortLongPressTimeout - expValue;
                }
            } else {
                timeOut = this.mShortLongPressTimeout;
            }
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(104, keyIndex, pointerId), timeOut);
        }
    }

    public int computeLongPressableTimeout() {
        this.mKeyRepeated = true;
        this.repeatCount++;
        int expValue = (int) Math.exp(this.repeatCount);
        if (expValue < 0 || expValue >= this.mShortLongPressTimeout) {
            return 100;
        }
        int timeOut = this.mShortLongPressTimeout - expValue;
        return timeOut;
    }

    public boolean onShortPress(KeyboardEx.Key key, int keyIndex, int pointerId) {
        boolean handled;
        log.d("onShortPress()");
        boolean handled2 = true;
        boolean hasAltKeyEvent = hasAltSymbolOrCode(key);
        boolean hasSymbolSelectPopup = hasSymbolSelectPopupResource(key);
        UsageManager usageMgr = UsageManager.from(getContext());
        if (usageMgr != null) {
            usageMgr.getKeyboardUsageScribe().recordKeycodeLongpress(key.codes[0]);
        }
        if (!hasAltKeyEvent) {
            if (hasSymbolSelectPopup) {
                handled = showSymbolSelectPopup(key);
            } else {
                handled = onLongPress(key);
            }
            return handled;
        }
        if (hasSymbolSelectPopup) {
            if (isSlideSelectEnabled()) {
                handled2 = showSymbolSelectPopup(key);
            } else if (hasAltSymbol(key)) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_LONGPRESS, keyIndex, 0), this.mLongPressTimeout);
                this.mHandler.sendEmptyMessageDelayed(MSG_REMOVE_PREVIEW, this.previewHideDelay);
                showAltSymbolPopup(key, pointerId);
            } else {
                handled2 = showSymbolSelectPopup(key);
            }
        } else if (hasAltSymbol(key) && key.showPopup) {
            this.mHandler.sendEmptyMessageDelayed(MSG_LONGPRESS, this.mLongPressTimeout);
            this.mHandler.sendEmptyMessageDelayed(MSG_REMOVE_PREVIEW, this.previewHideDelay);
            showAltSymbolPopup(key, pointerId);
        } else {
            handled2 = onLongPress(key);
        }
        return handled2;
    }

    public boolean onLongPress(KeyboardEx.Key popupKey) {
        boolean result = handleLongPress(popupKey);
        if (result && !isLongPressableBackspaceKey(popupKey) && (!isExploreByTouchOn() || !KeyboardEx.isShiftKey(popupKey.codes[0]))) {
            abortKey();
            clearKeyboardState();
        }
        return result;
    }

    public boolean handleLongPress(KeyboardEx.Key popupKey) {
        if (isSlideSelectEnabled()) {
            return false;
        }
        return showStaticSelectPopup(popupKey);
    }

    protected boolean movePointer(Pointer ptr, Point ptNew, long time) {
        ptr.add(ptNew, time);
        return true;
    }

    public boolean isSlideSelectEnabled() {
        return this.isSlideSelectEnabled;
    }

    public boolean showSymbolSelectPopup(KeyboardEx.Key popupKey) {
        return isSlideSelectEnabled() ? showSlideSelectPopup(popupKey) : showStaticSelectPopup(popupKey);
    }

    protected boolean showAltSelectPopup(KeyboardEx.Key popupKey) {
        return isSlideSelectEnabled() ? showAltSlideSelectPopup(popupKey) : showAltStaticSelectPopup(popupKey);
    }

    protected boolean showAltSlideSelectPopup(KeyboardEx.Key popupKey) {
        int res = getAltPopup(popupKey);
        if (res == 0) {
            return false;
        }
        if (this.slideSelectPopupManager == null) {
            this.slideSelectPopupManager = new SlideSelectPopupManager(this);
        }
        this.mPopupKeyboard.setTouchable(false);
        return showPopupKeyboardHelper(popupKey, res, "", null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean showAltStaticSelectPopup(KeyboardEx.Key popupKey) {
        int res = getAltPopup(popupKey);
        if (res != 0) {
            return showStaticSelectPopupHelper(popupKey, res, "");
        }
        return false;
    }

    protected boolean hasStandardPopup(KeyboardEx.Key key) {
        return getPopup(key) == key.popupResId && !TextUtils.isEmpty(key.popupCharacters);
    }

    protected boolean isSymbolSelectPopupEnabledForCurrentLayer() {
        return true;
    }

    protected CharSequence getPopupAdditionalChars(KeyboardEx.Key key) {
        CharSequence popupChars = "";
        if (hasStandardPopup(key)) {
            if (!isSymbolSelectPopupEnabledForCurrentLayer()) {
                return null;
            }
            if (this.enableSimplifiedMode) {
                popupChars = key.popupCharactersSimplified;
                if (TextUtils.isEmpty(popupChars) && !TextUtils.isEmpty(key.label)) {
                    popupChars = getDefaultSimpleAlternates(key.label);
                }
            } else {
                popupChars = key.popupCharacters;
            }
            if (TextUtils.isEmpty(popupChars)) {
                popupChars = null;
            }
        } else if (this.enableSimplifiedMode) {
            popupChars = null;
        }
        return popupChars;
    }

    private CharSequence getDefaultSimpleAlternates(CharSequence label) {
        CharSequence chars = this.popupCharsSimplifiedMap.get(label);
        if (chars == null) {
            Resources res = getContext().getResources();
            String packageName = getContext().getPackageName();
            String resourceName = "string/simple_alternates_for_" + ((Object) label);
            int id = res.getIdentifier(resourceName, null, packageName);
            if (id != 0) {
                chars = getContext().getString(id);
            }
            this.popupCharsSimplifiedMap.put(label, chars);
        }
        return chars;
    }

    public boolean showStaticSelectPopup(KeyboardEx.Key popupKey) {
        int res = getPopup(popupKey);
        if (res == 0) {
            return false;
        }
        CharSequence popupChars = "";
        if (res == popupKey.popupResId && popupKey.popupCharacters != null) {
            popupChars = popupKey.popupCharacters;
        }
        return showStaticSelectPopupHelper(popupKey, res, popupChars);
    }

    private boolean showStaticSelectPopupHelper(KeyboardEx.Key popupKey, int res, CharSequence popupChars) {
        boolean launched = false;
        if (res != 0) {
            this.slideSelectPopupManager = null;
            this.mPopupKeyboard.setTouchable(true);
            launched = showPopupKeyboardHelper(popupKey, res, popupChars, null);
            if (launched) {
                clearKeyboardState();
            }
        }
        return launched;
    }

    private boolean showSlideSelectPopup(KeyboardEx.Key popupKey) {
        CharSequence popupChars;
        int res = getSlidePopup(popupKey);
        if (res == 0 || (popupChars = getPopupAdditionalChars(popupKey)) == null) {
            return false;
        }
        if (this.slideSelectPopupManager == null) {
            this.slideSelectPopupManager = new SlideSelectPopupManager(this);
        }
        KeyboardEx.Key extraKey = null;
        Drawable altPreviewIcon = getAltPreviewIcon(popupKey);
        if (altPreviewIcon != null) {
            if (getAltPopup(popupKey) != 0) {
                extraKey = KeyboardEx.createIconKey(null, altPreviewIcon, KeyboardEx.KEYCODE_ALTPOPUP);
                extraKey.accessibilityLabel = "smiley";
                extraKey.text = ":-)";
            } else if (popupKey.altCode != 4063) {
                extraKey = KeyboardEx.createIconKey(null, altPreviewIcon, popupKey.altCode);
            }
        }
        this.mPopupKeyboard.setTouchable(false);
        return showPopupKeyboardHelper(popupKey, res, popupChars, extraKey);
    }

    private boolean showPopupKeyboardHelper(KeyboardEx.Key popupKey, int popupKeyboardId, CharSequence popupChars, KeyboardEx.Key extraKey) {
        Point pos;
        KeyboardAccessibilityState state;
        List<KeyboardEx.Key> keys;
        KeyboardEx keyboard;
        hideKeyPreview(-1);
        dismissSingleAltCharPopup();
        abortKey();
        int defaultPos = 0;
        if (popupChars != null && popupChars.length() > 0) {
            defaultPos = getDefaultForAltCharsPopup(popupKey, popupChars);
        }
        int orientation = getResources().getConfiguration().orientation;
        LruCache<CharSequence, View> keyPopups = this.mMiniKeyboardCache.get(popupKeyboardId + defaultPos + (orientation * 255));
        this.mMiniKeyboardContainer = keyPopups != null ? keyPopups.get(popupChars) : null;
        if (this.mMiniKeyboardContainer == null) {
            LayoutInflater inflater = getThemedLayoutInflater();
            IMEApplication.from(getContext()).getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mMiniKeyboardContainer = inflater.inflate(this.mPopupLayout, (ViewGroup) null);
            IMEApplication.from(getContext()).getThemeLoader().applyTheme(this.mMiniKeyboardContainer);
            this.mMiniKeyboard = (KeyboardViewEx) this.mMiniKeyboardContainer.findViewById(R.id.keyboardViewEx);
            this.mMiniKeyboard.setPressDownPreviewEnabled(false);
            this.mMiniKeyboard.setDoubleBuffered(false);
            this.mMiniKeyboard.setPressDownPreviewEnabled(isPressDownPreviewEnabled());
            this.mMiniKeyboard.setOnKeyboardActionListener(new KeyboardActionAdapter() { // from class: com.nuance.swype.input.KeyboardViewEx.2
                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onKey(Point point, int primaryCode, int[] keyCodes, KeyboardEx.Key key, long eventTime) {
                    KeyboardViewEx.this.notifyKeyboardListenerOnKey(null, primaryCode, keyCodes, key, eventTime);
                    if (primaryCode != 4074) {
                        KeyboardViewEx.this.mHandler.sendEmptyMessageDelayed(KeyboardViewEx.MSG_REMOVE_POPUPVIEW, 0L);
                    }
                    if (KeyboardViewEx.this.mIme != null) {
                        KeyboardViewEx.this.mIme.dragLock(false);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    KeyboardViewEx.this.notifyKeyboardListenerOnText(text, eventTime);
                    KeyboardViewEx.this.mHandler.sendEmptyMessageDelayed(KeyboardViewEx.MSG_REMOVE_POPUPVIEW, 0L);
                    if (KeyboardViewEx.this.mIme != null) {
                        KeyboardViewEx.this.mIme.dragLock(false);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onPress(int primaryCode) {
                    if (KeyboardViewEx.this.mKeyboardActionListener != null) {
                        KeyboardViewEx.this.mKeyboardActionListener.onPress(primaryCode);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    if (KeyboardViewEx.this.mKeyboardActionListener != null) {
                        KeyboardViewEx.this.mKeyboardActionListener.onRelease(primaryCode);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onTrace(TracePoints trace2) {
                    if (KeyboardViewEx.this.mKeyboardActionListener != null) {
                        KeyboardViewEx.this.mKeyboardActionListener.onTrace(trace2);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onWrite(List<Point> write) {
                    if (KeyboardViewEx.this.mKeyboardActionListener != null) {
                        KeyboardViewEx.this.mKeyboardActionListener.onWrite(write);
                    }
                }
            });
            if (!TextUtils.isEmpty(popupChars)) {
                if (this.slideSelectPopupManager != null) {
                    int idxDefault = extraKey == null ? defaultPos : -1;
                    keyboard = this.slideSelectPopupManager.createPopupCharsKeyboard(popupKeyboardId, popupChars, extraKey, idxDefault, popupKey, this.altCharsPopupMaxCol, getPaddingLeft() + getPaddingRight());
                } else {
                    keyboard = new KeyboardEx(getContext(), popupKeyboardId, popupChars, null, -1, 0, this.altCharsPopupMaxCol, getPaddingLeft() + getPaddingRight(), true);
                }
            } else if (this.slideSelectPopupManager != null) {
                keyboard = this.slideSelectPopupManager.createStaticKeyboard(popupKeyboardId, 0, popupKey);
            } else {
                keyboard = new KeyboardEx(getContext(), popupKeyboardId, false, true, true);
            }
            this.mMiniKeyboardOnScreen = true;
            this.mMiniKeyboard.setKeyboard(keyboard);
            this.mMiniKeyboard.setPopupParent(this);
            int gravity = SlideSelectPopupManager.calcGravity(this, popupKey);
            boolean isRightSide = (gravity & 5) == 5 || (8388613 & gravity) == 8388613;
            ShadowDrawable.addBackgroundShadow(getResources(), this.mMiniKeyboardContainer, this.popupBackgroundShadowProps, isRightSide);
            if (keyPopups == null) {
                keyPopups = new LruCache<>(4);
                this.mMiniKeyboardCache.put(popupKeyboardId + defaultPos + (orientation * 255), keyPopups);
            }
            keyPopups.put(popupChars, this.mMiniKeyboardContainer);
        } else {
            this.mMiniKeyboard = (KeyboardViewEx) this.mMiniKeyboardContainer.findViewById(R.id.keyboardViewEx);
            KeyboardEx keyboard2 = this.mMiniKeyboard.getKeyboard();
            if (keyboard2 != null) {
                keyboard2.clearStickyKeys();
            }
        }
        View closeButton = this.mMiniKeyboardContainer.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(this);
            if (this.slideSelectPopupManager != null) {
                closeButton.setVisibility(8);
            } else {
                closeButton.setVisibility(0);
            }
        }
        this.mMiniKeyboardContainer.measure(View.MeasureSpec.makeMeasureSpec(getScreenWidth(), Integers.STATUS_SUCCESS), View.MeasureSpec.makeMeasureSpec((getScreenHeight() * 3) / 4, Integers.STATUS_SUCCESS));
        if (this.mWindowOffset == null) {
            this.mWindowOffset = new int[2];
        }
        getLocationInWindow(this.mWindowOffset);
        this.mMiniKeyboard.mKeyboardSwitcher = this.mKeyboardSwitcher;
        this.mMiniKeyboard.mIme = this.mIme;
        this.mPopupKeyboard.setContentView(this.mMiniKeyboardContainer);
        if (this.slideSelectPopupManager != null) {
            this.mHandler.removeMessages(104);
            this.mHandler.removeMessages(MSG_LONGPRESS);
            Point pt = getExtendedPoint();
            this.mapper.map(pt);
            pos = this.slideSelectPopupManager.preparePopup(this.mMiniKeyboardContainer, this.mMiniKeyboard, popupKey, pt);
        } else if (this.mIme.isHardKeyboardActive()) {
            pos = calcHardkeyPopupKeyboardPos(popupKey);
        } else {
            pos = calcPopupKeyboardPos(popupKey);
            if (this.mIme != null) {
                this.mIme.dragLock(true);
            }
        }
        this.mMiniKeyboard.setPopupOffset(pos.x < 0 ? 0 : pos.x, pos.y);
        this.mPopupKeyboard.setWidth(this.mMiniKeyboardContainer.getMeasuredWidth());
        this.mPopupKeyboard.setHeight(this.mMiniKeyboardContainer.getMeasuredHeight());
        if (this.mIme.isHardKeyboardActive() && (keys = this.mMiniKeyboard.getKeyboard().getKeys()) != null && keys.size() > 0) {
            Iterator<KeyboardEx.Key> it = keys.iterator();
            while (it.hasNext()) {
                it.next().focused = false;
            }
        }
        if (getWindowToken() != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mPopupKeyboard.showAtLocation(this, 0, pos.x, pos.y);
        }
        if (isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.changeState(LongPressState.getInstance());
        }
        invalidate();
        if (this.mIme != null) {
            if ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null) {
                this.mIme.getInputContainerView().invalidateItem();
                this.mIme.getInputContainerView().invalidate();
                return true;
            }
            return true;
        }
        return true;
    }

    private void logMinikeyboardInfoForAutomation(KeyboardViewEx miniKeyboard, int offsetX, int offsetY) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Popup keyboard info: [ ");
        if (miniKeyboard != null) {
            List<KeyboardEx.Key> keys = miniKeyboard.getKeyboard().getKeys();
            String delimiter = "";
            for (KeyboardEx.Key key : keys) {
                String code = XMLResultsHandler.SEP_HYPHEN;
                if (key.codes != null && key.codes.length > 0) {
                    code = String.valueOf(key.codes[0]);
                }
                int x = key.x + offsetX;
                int y = key.y + offsetY;
                logMessage.append(delimiter).append(String.format("[%s,%s,%s,%s,%s] ", code, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(key.width), Integer.valueOf(key.height)));
                delimiter = ",";
            }
        } else {
            logMessage.append(" no keyboard info");
        }
        logMessage.append("]");
        log.d(logMessage.toString());
    }

    protected Point calcPopupKeyboardPos(KeyboardEx.Key popupKey) {
        int mPopupX = (((popupKey.x + getPaddingLeft()) + getLeft()) + popupKey.width) - this.mMiniKeyboardContainer.getMeasuredWidth();
        int mPopupY = (popupKey.y + getPaddingTop()) - this.mMiniKeyboardContainer.getMeasuredHeight();
        int x = this.mMiniKeyboardContainer.getPaddingRight() + mPopupX + this.mWindowOffset[0];
        int y = this.mMiniKeyboardContainer.getPaddingBottom() + mPopupY + this.mWindowOffset[1];
        return new Point(x, y - getPopupYAdjust(popupKey));
    }

    protected Point calcHardkeyPopupKeyboardPos(KeyboardEx.Key popupKey) {
        Rect rc = this.mIme.getInputContainerView().getVisibleWindowRect();
        int x = rc.left + ((rc.width() - this.mMiniKeyboardContainer.getMeasuredWidth()) / 2);
        int y = rc.top - this.mMiniKeyboardContainer.getMeasuredHeight();
        return new Point(x, y - getPopupYAdjust(popupKey));
    }

    public AppSpecificInputConnection getCurrentInputConnection() {
        if (this.mIme != null) {
            return this.mIme.getAppSpecificInputConnection();
        }
        return null;
    }

    public EditorInfo getCurrentInputEditorInfo() {
        if (this.mIme != null) {
            return this.mIme.getCurrentInputEditorInfo();
        }
        return null;
    }

    protected void releaseAllOtherPointers(Pointer excludedPointer) {
        for (int i = 0; i < this.mHshPointers.size(); i++) {
            Pointer pointer = this.mHshPointers.get(i);
            if (pointer != excludedPointer) {
                pointer.reset();
            }
        }
    }

    private void resetAllPointers() {
        for (int i = 0; i < this.mHshPointers.size(); i++) {
            Pointer pointer = this.mHshPointers.get(i);
            if (pointer != null) {
                pointer.reset();
            } else {
                log.d("mHshPointers " + i + " is null!");
            }
        }
    }

    private void handleTouchEventDown(MotionEvent me, boolean isMultiTouch) {
        int pointerIndex = me.getAction() >> 8;
        int pointerId = MotionEventWrapper.getPointerId(me, pointerIndex);
        int iOffsetX = -getPaddingLeft();
        int iOffsetY = -getPaddingTop();
        if (pointerIndex >= 0) {
            if (pointerIndex == 0 && !isMultiTouch) {
                this.multiTouchPointerCount = 1;
                this.mIsTracing = false;
            } else {
                this.multiTouchPointerCount++;
            }
            if (this.mHshPointers.get(pointerId) == null) {
                this.mHshPointers.put(pointerId, new Pointer(pointerId));
            }
            Pointer pointer = this.mHshPointers.get(pointerId);
            pointer.clear();
            for (int i = 0; i < me.getHistorySize(); i++) {
                pointer.add(new Point(((int) MotionEventWrapper.getHistoricalX(me, pointerIndex, i)) + iOffsetX, ((int) MotionEventWrapper.getHistoricalY(me, pointerIndex, i)) + iOffsetY), me.getHistoricalEventTime(i));
            }
            pointer.add(new Point(((int) MotionEventWrapper.getX(me, pointerIndex)) + iOffsetX, ((int) MotionEventWrapper.getY(me, pointerIndex)) + iOffsetY), me.getEventTime());
            handleTouchAction(me, pointer, 0);
        }
    }

    private void handleTouchEventMove(MotionEvent me) {
        int iPathSize;
        Point ptNew;
        int iOffsetX = -getPaddingLeft();
        int iOffsetY = -getPaddingTop();
        int pointerIndex = me.getAction() >> 8;
        int pointerId = MotionEventWrapper.getPointerId(me, pointerIndex);
        Pointer pointer = this.mHshPointers.get(pointerId);
        if (pointer != null && (iPathSize = pointer.size()) > 0) {
            int[] location = new int[2];
            getLocationOnScreen(location);
            if (((int) MotionEventWrapper.getRawY(me)) - location[1] < 0) {
                if (isExploreByTouchOn() && me.getAction() == 2) {
                    Point extendedPoint = getExtendedPoint();
                    ptNew = new Point((extendedPoint.x - location[0]) + iOffsetX, (extendedPoint.y - location[1]) + iOffsetY);
                } else {
                    ptNew = new Point(((int) MotionEventWrapper.getX(me, 0)) + iOffsetX, (((int) MotionEventWrapper.getRawY(me)) - location[1]) + iOffsetY);
                }
                clipTouchPoint(ptNew);
                movePointer(pointer, ptNew, me.getEventTime());
            } else {
                for (int j = 0; j < me.getHistorySize(); j++) {
                    Point ptNew2 = new Point(((int) MotionEventWrapper.getHistoricalX(me, 0, j)) + iOffsetX, ((int) MotionEventWrapper.getHistoricalY(me, 0, j)) + iOffsetY);
                    clipTouchPoint(ptNew2);
                    movePointer(pointer, ptNew2, me.getHistoricalEventTime(j));
                }
                Point ptNew3 = new Point(((int) MotionEventWrapper.getX(me, 0)) + iOffsetX, ((int) MotionEventWrapper.getY(me, 0)) + iOffsetY);
                clipTouchPoint(ptNew3);
                movePointer(pointer, ptNew3, me.getEventTime());
            }
            if (pointer.size() > iPathSize && this.multiTouchPointerCount == 1) {
                handleTouchAction(me, pointer, 2);
            }
        }
    }

    private void handleTouchEventUp(MotionEvent me) {
        cleanupScrubGesture();
        int pointerIndex = me.getAction() >> 8;
        int pointerId = MotionEventWrapper.getPointerId(me, pointerIndex);
        Pointer pointer = this.mHshPointers.get(pointerId);
        if (pointer != null && pointerIndex >= 0 && pointer.isPressed()) {
            handleTouchAction(me, pointer, 1);
        }
    }

    private void handleTouchEventCancel(MotionEvent me) {
        abortKey();
        handleTouchEventUp(me);
    }

    public MotionEvent getExtendedEvent(MotionEvent me) {
        return this.mIme.getExtendedEventForView(me, this);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent me) {
        TouchDelegate delegate = getTouchDelegate();
        if ((delegate == null || !delegate.onTouchEvent(getExtendedEvent(me))) && !this.keyboardBeingDragged) {
            switch (me.getAction() & 255) {
                case 0:
                    if (this.mLastEvent == 10 && this.mLastHoverExitPending) {
                        this.mHandlerHoverExit.removeCallbacks(this.mHoverExitRunnable);
                        this.mLastHoverExitPending = false;
                        break;
                    } else {
                        handleTouchEventDown(me, false);
                        break;
                    }
                    break;
                case 1:
                    handleTouchEventUp(me);
                    break;
                case 2:
                    handleTouchEventMove(me);
                    break;
                case 3:
                    handleTouchEventCancel(me);
                    break;
                case 5:
                    handleTouchEventDown(me, true);
                    break;
                case 6:
                    handleTouchEventUp(me);
                    break;
            }
            this.mLastEvent = me.getAction();
        }
        return true;
    }

    public boolean subHandleTouchInitialized(MotionEvent me, Pointer pointer, int action) {
        if (action == 0) {
            if (this.mAlternateCharPopup.isShowing() || isPopupKeyboardShowing()) {
                return false;
            }
            if (isTracing() && pointer.getPointerId() != this.mTracePointerId) {
                return false;
            }
        }
        if (this.slideSelectPopupManager == null && isPopupKeyboardShowing() && !AccessibilityInfo.isLongPressAllowed()) {
            if (this.keyPrevManager == null) {
                return false;
            }
            hidePreviewKeyNew(pointer.getPointerId());
            return false;
        }
        if (action == 0) {
            pointer.press(me);
        }
        return true;
    }

    private void onMultiTouchDown() {
        this.mHandler.removeMessages(MSG_SHOW_PREVIEW);
        this.mHandler.removeMessages(MSG_REPEAT);
        this.mHandler.removeMessages(104);
        this.mHandler.removeMessages(MSG_LONGPRESS);
        this.mHandler.removeMessages(MSG_PRESS_HOLD_FLICKR);
        this.mRepeatKeyIndex = -1;
        this.mKeyRepeated = false;
    }

    private boolean onMultiTouchUp(int pointerId, long eventTime) {
        return true;
    }

    public void subHandleTouchDown(MotionEvent me, Pointer pointer, int action) {
        int keyIndex = pointer.getCurrentKeyIndex();
        this.mCurrentKey = keyIndex;
        this.mAbortKey = false;
        if (keyIndex != -1 && !this.mHandler.hasMessages(104)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(104, keyIndex, pointer.getPointerId()), this.mShortLongPressTimeout);
        }
    }

    protected boolean touchMoveHandleBySlideSelectPopup(Pointer pointer) {
        if (this.slideSelectPopupManager == null) {
            return false;
        }
        Point first = pointer.get(0);
        Point cur = pointer.get(pointer.pathSize() - 1);
        int xOffset = cur.x - first.x;
        int yOffset = cur.y - first.y;
        Point pt = getExtendedPoint();
        this.mapper.map(pt);
        this.slideSelectPopupManager.onMove(pt.x, pt.y, xOffset, yOffset);
        return true;
    }

    public boolean touchUpHandleBySlideSelectPopup(Pointer pointer, UsageManager usageMgr) {
        if (this.slideSelectPopupManager == null) {
            return false;
        }
        showPreviewKey(-1, -1);
        Arrays.fill(this.mKeyIndices, -1);
        if (pointer != null) {
            pointer.reset();
        }
        this.mHandler.removeMessages(MSG_REMOVE_POPUPVIEW);
        boolean isShowing = this.mPopupKeyboard.isShowing();
        this.mPopupKeyboard.dismiss();
        if (isShowing) {
            this.slideSelectPopupManager.onUp();
            if (usageMgr != null) {
                usageMgr.getKeyboardUsageScribe().recordKeycodeLongpress(-1);
            }
            this.mMiniKeyboardOnScreen = false;
            if (this.mMiniKeyboard != null) {
                this.mMiniKeyboard.setPopupParent(null);
            }
            this.slideSelectPopupManager = null;
            return true;
        }
        this.mMiniKeyboardOnScreen = false;
        if (this.mMiniKeyboard != null) {
            this.mMiniKeyboard.setPopupParent(null);
        }
        this.slideSelectPopupManager.onCancel();
        this.slideSelectPopupManager = null;
        if (usageMgr == null) {
            return false;
        }
        usageMgr.getKeyboardUsageScribe().recordKeycodeLongpress(-1);
        return false;
    }

    public void subHandleTouchMove(MotionEvent me, Pointer pointer, int action) {
    }

    @SuppressLint({"NewApi"})
    public void subHandleTouchUp(MotionEvent me, Pointer pointer, int action) {
        int keyIndex = pointer.getCurrentKeyIndex();
        Point pt = pointer.getCurrentLocation();
        this.mHandler.removeMessages(104);
        pointer.release(me);
        if (!this.mAbortKey && keyIndex != -1 && keyIndex < this.mKeys.length) {
            detectAndSendKeyWrapper(pt.x, pt.y, 0L);
        }
    }

    public void handleTouchAction(MotionEvent me, Pointer pointer, int action) {
        if (subHandleTouchInitialized(me, pointer, action)) {
            switch (action) {
                case 0:
                    subHandleTouchDown(me, pointer, action);
                    return;
                case 1:
                    subHandleTouchUp(me, pointer, action);
                    return;
                case 2:
                    subHandleTouchMove(me, pointer, action);
                    return;
                default:
                    return;
            }
        }
    }

    private double calculateDistance(List<Point> path) {
        if (path.isEmpty()) {
            return 0.0d;
        }
        this.swypeDistanceSum = 0.0f;
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        float startX = path.get(0).x;
        float startY = path.get(0).y;
        for (int h = 1; h < path.size(); h++) {
            Point pt = path.get(h);
            float hx = pt.x;
            float hy = pt.y;
            float dx = (hx - startX) / display.xdpi;
            float dy = (hy - startY) / display.ydpi;
            this.swypeDistanceSum = (float) (this.swypeDistanceSum + Math.sqrt((dx * dx) + (dy * dy)));
            startX = hx;
            startY = hy;
        }
        return this.swypeDistanceSum;
    }

    private boolean repeatKey() {
        if (this.mRepeatKeyIndex == -1 || this.mRepeatKeyIndex >= this.mKeys.length) {
            return false;
        }
        KeyboardEx.Key key = this.mKeys[this.mRepeatKeyIndex];
        detectAndSendKey(key.x, key.y, this.mLastTapTime);
        this.mKeyRepeated = true;
        return true;
    }

    public void clearKeyboardState() {
        abortKey();
        dismissSingleAltCharPopup();
        dismissPreviewPopup();
        resetAllPointers();
        setKeyState(-1, ShowKeyState.Released);
        hideKeyPreview(-1);
        removeAllPendingActions();
        resetMultiTap();
    }

    public void removeAllPendingActions() {
        this.mHandler.removeMessages(MSG_REPEAT);
        this.mHandler.removeMessages(104);
        this.mHandler.removeMessages(MSG_LONGPRESS);
        this.mHandler.removeMessages(MSG_SHOW_PREVIEW);
        this.mHandler.removeMessages(106);
        this.mHandler.removeMessages(MSG_REMOVE_PREVIEW);
        this.mHandler.removeMessages(MSG_REMOVE_POPUPVIEW);
        this.mHandler.removeMessages(MSG_PRESS_HOLD_FLICKR);
    }

    public void closing() {
        releaseOverlayView();
        cleanupAccessibility();
        clearKeyboardState();
        dismissPopupKeyboard();
        this.mIgnoreDraw = true;
        if (this.mMiniKeyboard != null) {
            this.mMiniKeyboard.setPopupParent(null);
            this.mMiniKeyboard.closing();
            this.mMiniKeyboard = null;
        }
        this.keyboardBeingDragged = false;
        this.mMiniKeyboardCache.clear();
    }

    private void releaseOverlayView() {
        if (this.overlayView != null) {
            this.overlayView.detach();
            this.overlayView = null;
            this.popupViewManager = null;
            this.keyPrevManager = null;
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isPopupKeyboard) {
            getOverlayViewCreate();
            this.overlayView.attach(this);
            initKeyPrevManager();
        }
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    public void dismissPopupKeyboard() {
        if (this.mPopupKeyboard != null && this.mPopupKeyboard.isShowing()) {
            setVisibility(0);
            this.mPopupKeyboard.dismiss();
            this.mMiniKeyboardOnScreen = false;
            if (this.mMiniKeyboard != null) {
                this.mMiniKeyboard.setPopupParent(null);
            }
            invalidate();
            this.isEmojiKeyboardShown = false;
        }
    }

    public boolean handleBack() {
        if (!this.mPopupKeyboard.isShowing()) {
            return false;
        }
        dismissPopupKeyboard();
        return true;
    }

    public boolean isMultitapping() {
        return this.mInMultiTap;
    }

    public void resetMultiTap() {
        if (this.mInMultiTap) {
            dismissPreviewPopup();
        }
        this.mLastSentIndex = -1;
        this.mTapCount = 0;
        this.mLastTapTime = -1L;
        this.mInMultiTap = false;
    }

    public boolean allowsMoreKey() {
        return true;
    }

    public void multitapClearInvalid() {
        this.mInvalidKey = -1;
    }

    public boolean multitapIsInvalid() {
        return -1 != this.mInvalidKey;
    }

    public void multitapTimeOut() {
        if (this.mKeyboardActionListener != null) {
            this.mKeyboardActionListener.onMultitapTimeout();
        }
        resetMultiTap();
    }

    public void clearKeyOffsets() {
        this.mOffsetInWindow = null;
    }

    protected boolean isSwypingSupportedAndEnabled() {
        return isTraceInputEnabled() && isTraceInputPreferenceEnabled();
    }

    public boolean isTraceInputEnabled() {
        return false;
    }

    protected boolean isTraceInputPreferenceEnabled() {
        return UserPreferences.from(getContext()).isSwypingEnabled();
    }

    protected void handlePreTrace(TracePoints points) {
    }

    private int getGlowPaddingX(KeyboardEx.Key key) {
        return (int) Math.max(this.glowPadding, key.getVisibleBounds().width() * GLOW_OVERFLOW);
    }

    private int getGlowPaddingY(KeyboardEx.Key key) {
        return (int) Math.max(this.glowPadding, key.getVisibleBounds().height() * GLOW_OVERFLOW);
    }

    private void setMicrophoneKeyIcon() {
        KeyboardEx.Key speechKey;
        List<KeyboardEx.Key> keys = getKeyboard().getKeys();
        if (keys != null && (speechKey = findKeyByKeyCode(keys, KeyboardEx.KEYCODE_SPEECH)) != null) {
            int speechprovider = IMEApplication.from(getContext()).getSpeechProvider();
            Drawable icon = speechKey.icon.getCurrent();
            if (icon instanceof LevelListDrawable) {
                icon.setLevel(speechprovider);
            } else {
                speechKey.icon.setLevel(speechprovider);
            }
            Drawable icon2 = speechKey.iconPreview.getCurrent();
            if (icon2 instanceof LevelListDrawable) {
                icon2.setLevel(speechprovider);
            } else {
                speechKey.iconPreview.setLevel(speechprovider);
            }
            invalidateKey(speechKey);
        }
    }

    public KeyboardEx.Key findKeyByKeyCode(List<KeyboardEx.Key> keys, int keyCode) {
        for (KeyboardEx.Key key : keys) {
            if (key.codes[0] == keyCode) {
                return key;
            }
        }
        return null;
    }

    protected boolean hasShiftedSymbol(KeyboardEx.Key key) {
        return (key.shiftedIcon == null && key.shiftedLabel == null) ? false : true;
    }

    protected boolean hasAltSymbolsForPopup(KeyboardEx.Key key) {
        return key.popupCharacters != null && key.popupCharacters.length() > 0;
    }

    public boolean hasSymbolSelectPopupResource(KeyboardEx.Key key) {
        return getPopup(key) != 0;
    }

    public boolean hasAltSymbol(KeyboardEx.Key key) {
        return (getAltLabel(key) == null && getAltIcon(key) == null) ? false : true;
    }

    protected boolean hasPrimarySymbol(KeyboardEx.Key key) {
        return (getKeyLabel(key) == null && getKeyIcon(key) == null) ? false : true;
    }

    public boolean displaysAltSymbol(KeyboardEx.Key key) {
        if (hasAltSymbol(key)) {
            return key.alwaysShowAltSymbol || !this.enableSimplifiedMode;
        }
        return false;
    }

    public boolean hasAltSymbolOrCode(KeyboardEx.Key key) {
        return hasAltSymbol(key) || key.altCode != 4063;
    }

    private Drawable getPreviewIcon(KeyboardEx.Key key) {
        Drawable icon = key.iconPreview;
        if (useShiftAsPrimary(key)) {
            Drawable icon2 = key.shiftedPreviewIcon;
            if (icon2 == null) {
                return key.shiftedIcon;
            }
            return icon2;
        }
        if (icon == null) {
            return key.icon;
        }
        return icon;
    }

    private Drawable getAltPreviewIcon(KeyboardEx.Key key) {
        Drawable icon = key.altPreviewIcon;
        if (useShiftAsAlt(key)) {
            Drawable icon2 = key.shiftedPreviewIcon;
            if (icon2 == null) {
                return key.shiftedIcon;
            }
            return icon2;
        }
        if (icon == null) {
            return key.altIcon;
        }
        return icon;
    }

    private CharSequence getAltPreviewLabel(KeyboardEx.Key key) {
        return key.altPreviewLabel != null ? key.altPreviewLabel : getAltLabel(key);
    }

    private Drawable getKeyIcon(KeyboardEx.Key key) {
        return useShiftAsPrimary(key) ? key.shiftedIcon : key.icon;
    }

    private CharSequence getKeyLabel(KeyboardEx.Key key) {
        if (useShiftAsPrimary(key)) {
            return key.shiftedLabel;
        }
        if (isShifted() && key.label != null) {
            CharSequence label = key.labelUpperCase;
            if (label == null) {
                CharSequence label2 = upperCase(key.label);
                key.labelUpperCase = label2;
                return label2;
            }
            return label;
        }
        return key.label;
    }

    private static CharSequence filterKeyLabel(CharSequence label) {
        if (CharacterUtilities.isThaiCombiningMark(label)) {
            return XMLResultsHandler.SEP_SPACE + ((Object) label);
        }
        return label;
    }

    private boolean useShiftAsPrimary(KeyboardEx.Key key) {
        return isShifted() && hasShiftedSymbol(key) && key.shiftTransition != KeyboardEx.ShiftTransition.SWAP;
    }

    private Drawable getAltIcon(KeyboardEx.Key key) {
        return useShiftAsAlt(key) ? key.shiftedIcon : key.altIcon;
    }

    private CharSequence getAltLabel(KeyboardEx.Key key) {
        CharSequence label = null;
        if (useShiftAsAlt(key)) {
            CharSequence label2 = key.shiftedLabel;
            return label2;
        }
        if (!isShifted()) {
            CharSequence label3 = key.altLabel;
            return label3;
        }
        if (key.shiftTransition == KeyboardEx.ShiftTransition.DROP_HIDE || key.altLabel == null) {
            return null;
        }
        if (key.codes[0] != 32) {
            label = key.altLabelUpperCase;
        }
        if (label == null) {
            CharSequence label4 = upperCase(key.altLabel);
            key.altLabelUpperCase = label4;
            return label4;
        }
        return label;
    }

    private int getAltKeycode(KeyboardEx.Key key) {
        return useShiftAsAlt(key) ? key.shiftCode : key.altCode;
    }

    private int getAltPopup(KeyboardEx.Key key) {
        return useShiftAsAlt(key) ? key.shiftedPopupResId : key.altPopupResId;
    }

    private CharSequence getAltText(KeyboardEx.Key key) {
        return useShiftAsAlt(key) ? key.shiftText : key.altText;
    }

    public CharSequence getText(KeyboardEx.Key key) {
        return useShiftAsPrimary(key) ? key.shiftText : key.text;
    }

    public int getKeycode(KeyboardEx.Key key) {
        int code = useShiftAsPrimary(key) ? key.shiftCode : key.codes[0];
        if (code == 6430) {
            return 59;
        }
        if (code == 4071) {
            return 44;
        }
        return code;
    }

    private int getPopup(KeyboardEx.Key key) {
        return (!useShiftAsPrimary(key) || key.shiftedPopupResId == 0) ? key.popupResId : key.shiftedPopupResId;
    }

    private int getSlidePopup(KeyboardEx.Key key) {
        return (!useShiftAsPrimary(key) || key.shiftedPopupResId == 0) ? getPopupResId(key) : key.shiftedPopupResId;
    }

    public boolean useShiftAsAlt(KeyboardEx.Key key) {
        return !isShifted() && hasShiftedSymbol(key) && (key.shiftTransition == KeyboardEx.ShiftTransition.DROP_SHOW || key.shiftTransition == KeyboardEx.ShiftTransition.SWAP || !(key.shiftTransition == KeyboardEx.ShiftTransition.DROP_HIDE || (key.altLabel == null && key.altIcon == null)));
    }

    private CharSequence adjustShift(CharSequence label) {
        if (label != null && isShifted() && !Character.isUpperCase(label.charAt(0)) && currentLanguageSupportsCapitalization()) {
            return upperCase(label);
        }
        return label;
    }

    private CharSequence upperCase(CharSequence text) {
        Locale locale = this.mIme.getInputMethods().getCurrentInputLanguage().locale;
        String upperCaseStr = text.toString().toUpperCase(locale);
        StringBuilder upperCaseText = new StringBuilder(upperCaseStr);
        int length = upperCaseText.length();
        if (length > text.length()) {
            upperCaseText.delete(length - text.length(), length);
            length = text.length();
        }
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (this.charUtil.unicase.indexOf(ch) != -1) {
                upperCaseText.setCharAt(i, ch);
            }
        }
        return upperCaseText;
    }

    public boolean isWriteInputEnabled() {
        return false;
    }

    public boolean isTracing() {
        return this.mIsTracing;
    }

    public boolean isWriting() {
        return false;
    }

    public boolean isPressHoldFlickrMessage() {
        return false;
    }

    public boolean isPopupKeyboardShowing() {
        return this.mPopupKeyboard != null && this.mPopupKeyboard.isShowing();
    }

    public boolean handleScrollDown() {
        log.d("handleScrollDown");
        return false;
    }

    public boolean handleScrollUp() {
        return false;
    }

    public boolean handleScrollLeft() {
        return false;
    }

    public boolean handleScrollRight() {
        return false;
    }

    public boolean isSupportMultitouchGesture() {
        return false;
    }

    @Override // com.nuance.swype.input.chinese.TwoFingerGestureDetector.OnScrollListener
    public boolean onScrollDown() {
        return handleScrollDown();
    }

    @Override // com.nuance.swype.input.chinese.TwoFingerGestureDetector.OnScrollListener
    public boolean onScrollLeft() {
        return handleScrollLeft();
    }

    @Override // com.nuance.swype.input.chinese.TwoFingerGestureDetector.OnScrollListener
    public boolean onScrollRight() {
        return handleScrollRight();
    }

    @Override // com.nuance.swype.input.chinese.TwoFingerGestureDetector.OnScrollListener
    public boolean onScrollUp() {
        return handleScrollUp();
    }

    protected void clipTouchPoint(Point pt) {
        pt.y = pt.y > 0 ? pt.y : 0;
        pt.x = pt.x > 0 ? pt.x : 0;
        if (pt.x >= getWidth()) {
            pt.x = getWidth() - 1;
        }
    }

    protected boolean currentLanguageSupportsCapitalization() {
        return this.mKeyboardSwitcher.currentLanguageSupportsCapitalization();
    }

    public void setDoubleBuffered(boolean enabled) {
        this.doubleBuffered = enabled;
    }

    @Override // com.nuance.swype.input.view.DragHelper.DragVisualizer
    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    public void onBeginDrag() {
        log.d("onBeginDrag(): XXX");
        if (this.mIme != null) {
            this.mIme.vibrate();
        }
        clearKeyboardState();
        dismissPopupKeyboard();
        this.keyboardBeingDragged = true;
        invalidateKeyboardImage();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this, 255);
        this.keyboardBeingDragged = false;
        invalidateKeyboardImage();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
    }

    public boolean isHandlingTrace() {
        return this.mPreTraceAlreadDispatched;
    }

    public int getHighlightedKeyArea(KeyboardEx.Key key) {
        return HighLightKeyArea.NONE.getValue();
    }

    public boolean isAltKeyPopupAllowed(KeyboardEx.Key key) {
        return true;
    }

    public void checkAccessibility() {
        if (isExploreByTouchOn()) {
            changeAccessibilityState(ExplorationState.getInstance());
            SoundResources.getInstance(getContext());
            prepareScrubGesturePopup();
        }
    }

    public void changeAccessibilityState(KeyboardAccessibilityState state) {
        AccessibilityStateManager asm = AccessibilityStateManager.getInstance();
        asm.setCurrentView(this);
        asm.changeAccessibilityState(state);
    }

    public KeyboardAccessibilityState getKeyboardAccessibilityState() {
        return AccessibilityStateManager.getInstance().getKeyboardAccessibilityState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isExploreByTouchOn() {
        return this.mIme != null && this.mIme.isAccessibilitySupportEnabled();
    }

    @Override // android.view.View
    @TargetApi(14)
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        KeyboardAccessibilityState state;
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() != 128 && (state = getKeyboardAccessibilityState()) != null) {
            state.populateEventData(event);
        }
    }

    public boolean handleScrubGestureFrameHoverEventExit(MotionEvent me, boolean isTouchEventUp, boolean isExitFromBottom) {
        KeyboardAccessibilityState state = getKeyboardAccessibilityState();
        if (state != null) {
            if (isTouchEventUp) {
                cleanupScrubGesture();
                Pointer pointer = this.mHshPointers.get(me.getPointerId(me.getActionIndex()));
                state.handleActionUp(pointer, null);
                return true;
            }
            if (!isExitFromBottom) {
                state.changeState(ExplorationState.getInstance());
                return true;
            }
            return true;
        }
        return true;
    }

    public boolean handleScrubGestureFrameHoverEventMove(MotionEvent me) {
        Pointer pointer;
        Point pt;
        KeyboardAccessibilityState state = getKeyboardAccessibilityState();
        if (state != null && (pointer = this.mHshPointers.get(me.getPointerId(me.getActionIndex()))) != null) {
            int[] location = new int[2];
            getLocationOnScreen(location);
            if (me.getRawY() - location[1] < 0.0f) {
                pt = new Point((int) me.getX(), ((int) me.getRawY()) - location[1]);
            } else {
                pt = new Point((int) me.getX(), (int) me.getY());
            }
            movePointer(pointer, pt, me.getEventTime());
            state.handleActionMove(pointer, getKey(pt.x, pt.y));
        }
        return true;
    }

    public Point getExtendedPoint() {
        return this.mIme.getExtendedPoint();
    }

    @SuppressLint({"InflateParams"})
    private void prepareScrubGesturePopup() {
        if (this.mScrubGestureView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            this.mScrubGestureView = (AccessibilityScrubGestureView) inflater.inflate(R.layout.accessibility_scrub_gesture_view, (ViewGroup) null);
            this.mScrubGestureView.setKeyboardView(this);
        }
    }

    public void cleanupAccessibility() {
        if (isExploreByTouchOn() && this.mScrubGestureView != null) {
            if (this.mHandlerHoverExit != null && this.mLastHoverExitPending) {
                this.mHandlerHoverExit.removeCallbacks(this.mHoverExitRunnable);
                this.mLastHoverExitPending = false;
            }
            cleanupScrubGesture();
        }
    }

    public void cleanupScrubGesture() {
        if (isExploreByTouchOn() && this.mScrubGestureView != null) {
            this.mScrubGestureView.hideUpperScreenScrubGestureFrame(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateEmojiKeyboardPosition() {
        Rect rec;
        if (this.isEmojiKeyboardShown && this.mPopupKeyboard != null && this.mPopupKeyboard.isShowing() && this.mPopupParent != null && this.mPopupParent.getWindowToken() != null && !ActivityManagerCompat.isUserAMonkey() && (rec = this.mIme.getSpeechPopupRectInWindowCoord()) != null) {
            this.mPopupKeyboard.update(rec.left, rec.top, -1, -1);
        }
    }

    public boolean isEmojiKeyboardShown() {
        return this.isEmojiKeyboardShown;
    }

    public void showScrubGestureView() {
        if (this.mScrubGestureView != null) {
            this.mScrubGestureView.addUpperScreenScrubGestureFrame();
        }
    }

    public AccessibilityScrubGestureView getScrubGesturView() {
        return this.mScrubGestureView;
    }

    @Override // android.view.View
    @SuppressLint({"NewApi"})
    public boolean onHoverEvent(MotionEvent event) {
        if (!isExploreByTouchOn()) {
            return false;
        }
        log.d("onHoverEvent");
        if (event.getToolType(0) == 2) {
            return true;
        }
        boolean ret = false;
        switch (event.getAction()) {
            case 4:
                ret = true;
                break;
            case 9:
                showScrubGestureView();
            case 7:
                ret = true;
                break;
            case 10:
                int hover_exit_adjustment_width_offset = getKeyboardAccessibilityState() != null ? getKeyboardAccessibilityState().getHoverExitWidthOffset() : 0;
                int[] location = new int[2];
                getLocationOnScreen(location);
                DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
                if (event.getY() > 0.0f) {
                    cleanupScrubGesture();
                }
                dismissSingleAltCharPopup();
                log.d("keyboard_height: ", Integer.valueOf(getKeyboard().getHeight()));
                if (event.getY() > 0.0f && event.getY() < getKeyboard().getHeight() && event.getX() > hover_exit_adjustment_width_offset && event.getX() < display.widthPixels - hover_exit_adjustment_width_offset) {
                    this.mHandlerHoverExit = new Handler();
                    this.mHoverExitRunnable = new Runnable() { // from class: com.nuance.swype.input.KeyboardViewEx.3
                        @Override // java.lang.Runnable
                        public void run() {
                            KeyboardViewEx.this.mLastHoverExitPending = false;
                        }
                    };
                    this.mLastHoverExitPending = true;
                    this.mHandlerHoverExit.postDelayed(this.mHoverExitRunnable, HOVER_TO_TOUCH_WAITING_TIME);
                } else if (event.getY() >= getKeyboard().getHeight() || event.getX() <= hover_exit_adjustment_width_offset || event.getX() >= display.widthPixels - hover_exit_adjustment_width_offset) {
                    getKeyboardAccessibilityState().playSoundPlayedOnKeyboardExit();
                    removeAllPendingActions();
                    getKeyboardAccessibilityState().changeState(ExplorationState.getInstance());
                }
                ret = true;
                break;
        }
        this.mLastEvent = event.getAction();
        return ret;
    }

    public boolean isDoublePinyinMode() {
        return false;
    }

    private int getPopupResId(KeyboardEx.Key popupKey) {
        return popupKey.popupResId;
    }

    public void showHardKeyPopupKeyboard(int keyCode) {
        KeyboardEx.Key popupKey;
        log.d("showHardKeyPopupKeyboard(): " + keyCode);
        if (this.mIme.isHardKeyboardActive() && (popupKey = getKeyByKeyCode(keyCode)) != null && popupKey.popupResId != 0 && popupKey.popupCharacters != null) {
            showStaticSelectPopupHelper(popupKey, popupKey.popupResId, popupKey.popupCharacters);
            List<KeyboardEx.Key> keys = this.mMiniKeyboard.getKeyboard().getKeys();
            KeyboardEx.Key defaultKey = this.mMiniKeyboard.getKeyboard().getDefaultKey();
            int defaultIndex = 0;
            if (keys != null && keys.size() > 0) {
                for (int i = 0; i < keys.size(); i++) {
                    keys.get(i).focused = false;
                    keys.get(i).pressed = false;
                    if (keys.get(i).equals(defaultKey)) {
                        defaultIndex = i;
                    }
                }
                keys.get(defaultIndex).focused = true;
            }
            if (this.mPopupParent != null) {
                ((ImageButton) ((KeyboardViewEx) this.mPopupParent).mMiniKeyboardContainer.findViewById(R.id.closeButton)).setImageState(new int[0], false);
            }
            invalidate();
        }
    }

    private KeyboardEx.Key getKeyByKeyCode(int keyCode) {
        if (this.mKeys != null) {
            for (KeyboardEx.Key key : this.mKeys) {
                if (key.codes.length > 0 && key.codes[0] == keyCode) {
                    return key;
                }
            }
        }
        return null;
    }

    public boolean miniKeyboardOnKeyDown(int keyCode, KeyEvent event) {
        if (!this.mMiniKeyboardOnScreen) {
            return false;
        }
        if (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 66 || keyCode == 62) {
            return true;
        }
        if (keyCode != 67 && keyCode != 111 && keyCode != 112) {
            return true;
        }
        dismissPopupKeyboard();
        return true;
    }

    public boolean miniKeyboardOnKeyUp(int keyCode, KeyEvent event) {
        log.d("miniKeyboardOnKeyUp(): " + keyCode);
        if (!this.mMiniKeyboardOnScreen) {
            return false;
        }
        if (keyCode == 4 || keyCode == 111) {
            log.d("BACK or ESCAPE (dismiss popup)");
            dismissPopupKeyboard();
            return true;
        }
        if (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 66 || keyCode == 62) {
            return this.mMiniKeyboard.onKeyUp(keyCode, event);
        }
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int newkey;
        int focusedKey;
        int newKey;
        int newKey2;
        if (this.mPopupParent == null || this.mPopupParent == this) {
            return false;
        }
        int focusedKey2 = -1;
        int i = 0;
        while (true) {
            if (i >= this.mKeys.length) {
                break;
            }
            if (!this.mKeys[i].focused) {
                i++;
            } else {
                focusedKey2 = i;
                break;
            }
        }
        if (keyCode == 21 || keyCode == 22) {
            if (focusedKey2 == -1) {
                if (keyCode == 21) {
                    focusedKey = this.mKeys.length - 1;
                } else {
                    focusedKey = 0;
                }
                this.mKeys[focusedKey].focused = true;
                ((ImageButton) ((KeyboardViewEx) this.mPopupParent).mMiniKeyboardContainer.findViewById(R.id.closeButton)).setImageState(new int[0], false);
            } else {
                int newkey2 = focusedKey2;
                if (keyCode == 21) {
                    newkey = newkey2 - 1;
                } else {
                    newkey = newkey2 + 1;
                }
                if (newkey < 0 || newkey >= this.mKeys.length) {
                    this.mKeys[focusedKey2].focused = false;
                    ((ImageButton) ((KeyboardViewEx) this.mPopupParent).mMiniKeyboardContainer.findViewById(R.id.closeButton)).setImageState(new int[]{android.R.attr.state_pressed}, false);
                } else {
                    this.mKeys[focusedKey2].focused = false;
                    this.mKeys[newkey].focused = true;
                }
            }
            invalidate();
        } else if (keyCode == 20 || keyCode == 19) {
            List<KeyboardEx.Row> keyboardLayout = getKeyboard().mKeyboardLayout;
            int newKey3 = focusedKey2;
            if (focusedKey2 == -1) {
                newKey3 = 0;
            }
            if (keyboardLayout.size() == 1) {
                if (keyCode == 19) {
                    newKey2 = 0;
                } else {
                    newKey2 = this.mKeys.length - 1;
                }
                if (focusedKey2 == -1) {
                    ((ImageButton) ((KeyboardViewEx) this.mPopupParent).mMiniKeyboardContainer.findViewById(R.id.closeButton)).setImageState(new int[0], false);
                    this.mKeys[newKey2].focused = true;
                } else {
                    this.mKeys[focusedKey2].focused = false;
                    this.mKeys[newKey2].focused = true;
                }
            } else if (keyboardLayout.size() > 1) {
                int firstRowKeyCount = keyboardLayout.get(0).keyCount();
                int bottomRowKeyCount = keyboardLayout.get(keyboardLayout.size() - 1).keyCount();
                if (keyCode == 19) {
                    if (newKey3 < firstRowKeyCount) {
                        newKey = 0;
                    } else if (newKey3 < firstRowKeyCount + bottomRowKeyCount) {
                        newKey = newKey3 - firstRowKeyCount;
                        if (newKey >= firstRowKeyCount) {
                            newKey = firstRowKeyCount - 1;
                        }
                    } else {
                        newKey = newKey3 - bottomRowKeyCount;
                    }
                } else {
                    if (newKey3 < firstRowKeyCount) {
                        newKey = newKey3 + firstRowKeyCount;
                    } else {
                        newKey = newKey3 + bottomRowKeyCount;
                    }
                    if (newKey >= this.mKeys.length) {
                        newKey = this.mKeys.length - 1;
                    }
                }
                if (focusedKey2 == -1) {
                    ((ImageButton) ((KeyboardViewEx) this.mPopupParent).mMiniKeyboardContainer.findViewById(R.id.closeButton)).setImageState(new int[0], false);
                    this.mKeys[newKey].focused = true;
                } else {
                    this.mKeys[focusedKey2].focused = false;
                    this.mKeys[newKey].focused = true;
                }
            }
            invalidate();
        } else if (keyCode == 66 || keyCode == 62) {
            if (focusedKey2 != -1 && ((KeyboardViewEx) this.mPopupParent).mKeyboardActionListener != null) {
                this.mIme.getCurrentInputView().reconstructWord();
                this.mIme.getCurrentInputView().handleBackspace(0);
                if (this.mIme.mCurrentInputLanguage.isLatinLanguage() || this.mIme.mCurrentInputLanguage.isKoreanLanguage()) {
                    this.mIme.getCurrentInputView().onHardKeyText(this.mKeys[focusedKey2].text != null ? this.mKeys[focusedKey2].text : String.valueOf((char) this.mKeys[focusedKey2].codes[0]));
                } else {
                    ((KeyboardViewEx) this.mPopupParent).mKeyboardActionListener.onText(this.mKeys[focusedKey2].text != null ? this.mKeys[focusedKey2].text : String.valueOf((char) this.mKeys[focusedKey2].codes[0]), event.getEventTime());
                }
            }
            ((KeyboardViewEx) this.mPopupParent).dismissPopupKeyboard();
        }
        return true;
    }

    public KeyboardEx.Key configureKeyPreview(PreviewView previewView, int keyIndex, boolean isAlt) {
        KeyboardEx.Key key = getKey(keyIndex);
        if (configureKeyPreview(previewView, key, isAlt)) {
            return key;
        }
        return null;
    }

    public boolean configureKeyPreview(PreviewView previewView, KeyboardEx.Key key, boolean isAlt) {
        if (!canShowPreview(key, isAlt)) {
            return false;
        }
        if (isAlt) {
            configureAltKeyPreviewHelper(previewView, key);
        } else {
            configureKeyPreviewHelper(previewView, key);
        }
        ShadowDrawable.addBackgroundShadow(getResources(), previewView, this.keyPopupBackgroundShadowProps, false);
        return true;
    }

    public int getSingleTouchPressKeycode(KeyboardEx.Key key) {
        if (isShifted() || key.shiftCode == 4063) {
            return key.shiftTransition != KeyboardEx.ShiftTransition.DROP_HIDE ? key.codes[0] : KeyboardEx.KEYCODE_INVALID;
        }
        return key.shiftCode;
    }

    private void initPreviewText(PreviewView previewView, CharSequence label) {
        previewView.setText(filterKeyLabel(label));
        previewView.setTextColor(this.mPopupTextColor);
        previewView.setContentDescription(label);
        if (label.length() > 1) {
            previewView.setTextSizePixels(this.mPopupTextSize);
            previewView.setTypeface(this.previewKeyTypeface);
        } else {
            previewView.setTextSizePixels(this.mPopupCharSize);
            previewView.setTypeface(this.previewKeyTypefaceBold);
        }
    }

    private void configureAltKeyPreviewHelper(PreviewView previewView, KeyboardEx.Key key) {
        Drawable icon = getAltPreviewIcon(key);
        if (icon != null) {
            icon.setState(ICON_STATE_PREVIEW);
            recolorPopupIcon(icon);
            previewView.setImageDrawable(icon);
            previewView.setContentDescription("");
            return;
        }
        initPreviewText(previewView, getAltPreviewLabel(key));
    }

    private void configureKeyPreviewHelper(PreviewView previewView, KeyboardEx.Key key) {
        CharSequence label = getPreviewText(key);
        Drawable icon = getPreviewIcon(key);
        if (label == null && icon != null) {
            icon.setState(ArraysCompat.addAll(ICON_STATE_PREVIEW, key.getCurrentDrawableState(true)));
            recolorPopupIcon(icon);
            previewView.setImageDrawable(icon.getCurrent());
            previewView.setContentDescription("");
            return;
        }
        initPreviewText(previewView, getPreviewText(key));
    }

    public int getPopupYAdjust(KeyboardEx.Key key) {
        float adjustYFactor = getResources().getFraction(R.fraction.slide_select_popup_adjust_y_factor, 1, 1);
        int reference = key != null ? key.height : getMaxRowHeight();
        return (int) (reference * adjustYFactor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int[] calcKeyTopCenterInWindow(KeyboardEx.Key key, int[] pos) {
        if (pos == null) {
            pos = new int[2];
        }
        getLocationInWindow(pos);
        int xKeyCenter = key.x + (key.width / 2);
        pos[0] = pos[0] + getPaddingLeft() + xKeyCenter;
        pos[1] = pos[1] + getPaddingTop() + key.y;
        return pos;
    }

    public Rect getKeyPreviewRectInWindow(View view, KeyboardEx.Key key, boolean constrainTop) {
        view.measure(-2, -2);
        int popupWidth = Math.max(view.getMeasuredWidth(), key.width + view.getPaddingLeft() + view.getPaddingRight());
        int popupHeight = this.mPreviewHeight;
        int x = key.x + getPaddingLeft() + ((key.width - popupWidth) / 2);
        int y = (key.y - popupHeight) + this.mPreviewOffset;
        recalculateOffsets();
        if (constrainTop && this.mOffsetInWindow[1] + y < 0) {
            y = -this.mOffsetInWindow[1];
        }
        Rect rc = new Rect(x, y, x + popupWidth, y + popupHeight);
        int[] iArr = this.mOffsetInWindow;
        GeomUtil.moveRectBy(rc, iArr[0], iArr[1]);
        return rc;
    }

    private void hidePreviewKeyNew(int pointerId) {
        this.keyPrevManager.hide(pointerId);
    }

    protected boolean canShowPreview(KeyboardEx.Key key, boolean isAlt) {
        if (!isShown() || key == null) {
            return false;
        }
        if (isAlt) {
            return true;
        }
        return (isPressDownPreviewEnabled() && key.showPopup) || key.hasIconDescription();
    }

    protected void showPreviewKeyNew(KeyboardEx.Key key, int pointerId, boolean isAlt) {
        int i = 2;
        if (canShowPreview(key, isAlt)) {
            KeyPreviewManager keyPreviewManager = this.keyPrevManager;
            if (keyPreviewManager.enabled) {
                KeyPreviewManager.PreviewInfo previewInfoCreate = keyPreviewManager.getPreviewInfoCreate(pointerId);
                PreviewView previewView = previewInfoCreate.view;
                if (keyPreviewManager.keyboardView.configureKeyPreview(previewView, key, isAlt) && key.type != 4) {
                    Rect keyPreviewRectInWindow = keyPreviewManager.keyboardView.getKeyPreviewRectInWindow(previewView, key, false);
                    OverlayView overlayView = keyPreviewManager.overlayView;
                    int[] iArr = {keyPreviewRectInWindow.left, keyPreviewRectInWindow.top};
                    overlayView.windowToOverlay(iArr);
                    GeomUtil.moveRectTo(keyPreviewRectInWindow, iArr);
                    GeomUtil.confine(keyPreviewRectInWindow, overlayView.getDimsRect(), 2);
                    OverlayView.setGeometry(previewView, keyPreviewRectInWindow);
                    previewInfoCreate.timer.stop();
                    if (isAlt) {
                        previewInfoCreate.state = 2;
                    } else {
                        i = 1;
                    }
                    previewInfoCreate.state = i;
                    previewInfoCreate.view.setVisibility(0);
                    return;
                }
                keyPreviewManager.dismissNow(pointerId);
                return;
            }
            return;
        }
        this.keyPrevManager.hide(pointerId);
    }

    private void showPreviewKeyNew(int keyIndex, int pointerId, boolean isAlt) {
        showPreviewKeyNew(getKey(keyIndex), pointerId, isAlt);
    }

    public void hideKeyPreview(int pointerId) {
        if (this.keyPrevManager != null) {
            if (pointerId != -1) {
                hidePreviewKeyNew(pointerId);
                return;
            }
            KeyPreviewManager keyPreviewManager = this.keyPrevManager;
            int size = keyPreviewManager.previewInfo.size();
            for (int i = 0; i < size; i++) {
                keyPreviewManager.dismissNow(i);
            }
            return;
        }
        if (this.mPreviewPopup != null && this.mPreviewPopup.isShowing()) {
            showPreviewKey(-1, -1);
        }
    }

    protected void notifyHardwareKeyboardActionListener(int primaryCode, int[] keyCodes) {
        if (this.mKeyboardActionListener != null) {
            this.mKeyboardActionListener.onHardwareCharKey(primaryCode, keyCodes);
        }
    }

    public void redrawKeyboard() {
        invalidateKeyboardImage();
    }

    public void closePopup() {
        dismissPopupKeyboard();
    }

    public Pointer getFrozenPointerState(Pointer pointer) {
        return new Pointer(pointer);
    }

    public void setCurrentKeyInSlideSelectPopupManager(KeyboardEx.Key key) {
        log.d("setCurrentKeyInSlideSelectPopupManager(): key: ", key);
        this.slideSelectPopupManager.setCurrentKey(key);
    }

    public KeyboardEx.Key getDefaultKeyInSlideSelectPopup() {
        if (this.slideSelectPopupManager != null) {
            return this.slideSelectPopupManager.getDefaultKey();
        }
        return null;
    }

    public List<KeyboardEx.Key> getKeyListInSlideSelectPopup() {
        if (this.slideSelectPopupManager != null) {
            return this.slideSelectPopupManager.getKeyList();
        }
        return null;
    }

    private void SpeakContextAroundCursor(int numWordsBeforeCursor, int numWordsAfterCursor) {
        CharSequence cs_before;
        CharSequence cs_after;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (getAppSpecificBehavior() != null && getAppSpecificBehavior().shouldRestrictGetTextLengthFromCursor()) {
                cs_before = ic.getTextBeforeCursor(102, 0);
                cs_after = ic.getTextAfterCursor(102, 0);
            } else {
                cs_before = ic.getTextBeforeCursor(1024, 0);
                cs_after = ic.getTextAfterCursor(1024, 0);
            }
            String announcementBeforeCursor = "";
            String announcementAfterCursor = "";
            if (!TextUtils.isEmpty(cs_before)) {
                String[] parts = cs_before.toString().trim().split("\\s+");
                for (int i = parts.length < numWordsBeforeCursor ? parts.length : numWordsBeforeCursor; i > 0; i--) {
                    announcementBeforeCursor = announcementBeforeCursor + parts[parts.length - i] + XMLResultsHandler.SEP_SPACE;
                }
            }
            if (!TextUtils.isEmpty(cs_after)) {
                String[] parts2 = cs_after.toString().trim().split("\\s+");
                for (int i2 = 0; i2 < parts2.length && i2 < numWordsAfterCursor; i2++) {
                    announcementAfterCursor = announcementAfterCursor + parts2[i2] + XMLResultsHandler.SEP_SPACE;
                }
            }
            AccessibilityNotification.getInstance().announceNotification(getContext(), String.format(this.mCursorString, announcementBeforeCursor, announcementAfterCursor), true);
        }
    }

    public void clearMiniKeyboardCache() {
        if (this.mMiniKeyboardCache != null) {
            this.mMiniKeyboardCache.clear();
        }
    }

    public void clearDrawBufferCache() {
        if (this.bufferManager != null) {
            this.bufferManager.evictAll();
        }
    }

    public boolean tracedGesture() {
        return this.isTracedGesture;
    }

    public int getMaxRowHeight() {
        List<KeyboardEx.Row> rows = getKeyboard().mKeyboardLayout;
        int max = 0;
        for (KeyboardEx.Row row : rows) {
            max = Math.max(max, row.getHeight());
        }
        return max;
    }

    public boolean isFullScreenHandWritingView() {
        return false;
    }

    public KeyboardSwitcher getKeyboardSwitcher() {
        return this.mKeyboardSwitcher;
    }

    private void drawPaddingBackground(Canvas canvas) {
        if (getPaddingBottom() > 0) {
            Paint paint = new Paint();
            int color = getResources().getColor(R.color.padding_bottom);
            canvas.save();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawRect(0.0f, getHeight() - getPaddingBottom(), getWidth(), getHeight(), paint);
            canvas.restore();
        }
    }

    protected void dump(Printer out) {
    }

    private static String join(Object[] items, String delim) {
        StringBuilder builder = new StringBuilder();
        for (Object item : items) {
            if (builder.length() > 0) {
                builder.append(delim);
            }
            builder.append(item);
        }
        return builder.toString();
    }

    private static String listify(Object... items) {
        return "[" + join(items, ",") + "]";
    }

    private static void print(Printer out, CharSequence... items) {
        out.println(join(items, EMPTY_TEXT));
    }

    private static String getId(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    private void dumpViewInfo(Printer out, String subtag) {
        StringBuilder builder = new StringBuilder();
        if (subtag == null) {
            subtag = EMPTY_TEXT;
        }
        String tag = getClass().getSimpleName() + "[" + subtag + "]";
        builder.append("keyboardview: " + tag);
        builder.append("; kid: " + getId(getKeyboard()));
        builder.append("; shown: " + isShown());
        int[] loc = new int[2];
        getLocationOnScreen(loc);
        builder.append("; sp: " + listify(Integer.valueOf(loc[0]), Integer.valueOf(loc[1])));
        builder.append("; tl: " + listify(Integer.valueOf(getPaddingLeft()), Integer.valueOf(getPaddingTop())));
        print(out, builder);
    }

    private static void dumpKeys(Printer out, KeyboardEx keyboard, String subtag) {
        StringBuilder builder = new StringBuilder();
        if (subtag == null) {
            subtag = EMPTY_TEXT;
        }
        String tag = keyboard.getClass().getSimpleName() + "[" + subtag + "]";
        builder.append("keyboard: " + tag);
        builder.append("; kid: " + getId(keyboard));
        for (KeyboardEx.Key k : keyboard.getKeys()) {
            builder.append("; ");
            int code = (k.codes == null || k.codes.length <= 0) ? 4063 : k.codes[0];
            if (4063 != code) {
                builder.append(code);
            } else {
                builder.append("\"" + ((Object) k.label) + "\"");
            }
            builder.append(":" + listify(Integer.valueOf(k.x), Integer.valueOf(k.y), Integer.valueOf(k.width), Integer.valueOf(k.height)));
        }
        print(out, builder);
    }

    public void setContextCandidatesView(boolean isGridCandidatesView) {
        this.isContextCandidatesView = isGridCandidatesView;
    }

    public void onTouchHeldMoved(int pointerId, float[] xcoords, float[] ycoords, int[] times) {
        KeyboardAccessibilityState state;
        Pointer pointer = getPointerIdCreate(pointerId);
        pointer.clear();
        for (int i = 0; i < xcoords.length; i++) {
            pointer.add(new Point((int) xcoords[i], (int) ycoords[i]), times[i]);
        }
        if (touchMoveHandleBySlideSelectPopup(pointer) && isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.handleActionMove(pointer, null);
        }
    }

    public void onTouchMoved(int pointerId, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced, int keyIndex) {
        Point lastPoint;
        KeyboardAccessibilityState state;
        int traceWidth = this.mTraceWidth;
        Pointer pointer = getPointerIdCreate(pointerId);
        pointer.setTraceDetected(canBeTraced);
        pointer.clear();
        for (int i = 0; i < xcoords.length; i++) {
            pointer.add(new Point((int) xcoords[i], (int) ycoords[i]), times[i]);
        }
        TracePoints renderingTracePoints = pointer.renderingTracePoints;
        Collection<Point> newPoints = pointer.getPoints().subList(Math.min(renderingTracePoints.size(), pointer.size()), pointer.getPoints().size());
        if (renderingTracePoints.size() > 0) {
            lastPoint = renderingTracePoints.get(renderingTracePoints.size() - 1);
        } else {
            lastPoint = pointer.getPoints().get(0);
        }
        Rect dirtyRect = new Rect();
        for (Point point : newPoints) {
            dirtyRect.union(new Rect(Math.min(lastPoint.x, point.x) - traceWidth, Math.min(lastPoint.y, point.y) - traceWidth, Math.max(lastPoint.x, point.x) + traceWidth, Math.max(lastPoint.y, point.y) + traceWidth));
        }
        int numPoints = getNumTracePoints(pointer.getPoints());
        if (pointer.getPoints().size() > numPoints) {
            int growth = (pointer.getPoints().size() - renderingTracePoints.size()) + 1;
            int i2 = pointer.getPoints().size() - numPoints;
            for (int j = 0; i2 > 0 && i2 < pointer.getPoints().size() && j < growth; j++) {
                Point lastPoint2 = pointer.getPoints().get(i2);
                Point lastPoint3 = lastPoint2;
                Point point2 = pointer.getPoints().get(i2 - 1);
                dirtyRect.union(new Rect(Math.min(lastPoint3.x, point2.x) - traceWidth, Math.min(lastPoint3.y, point2.y) - traceWidth, Math.max(lastPoint3.x, point2.x) + traceWidth, Math.max(lastPoint3.y, point2.y) + traceWidth));
                i2--;
            }
        }
        getDrawingRect(this.outRect);
        if (!dirtyRect.intersect(this.outRect)) {
            dirtyRect.setEmpty();
        }
        renderingTracePoints.reset(pointer.getPoints(), pointer.getTimes());
        invalidate(dirtyRect);
        if (this.mIme != null && ((this.mIme.isFullscreenMode() || IMEApplication.from(this.mIme).isScreenLayoutTablet()) && this.mKeyboard.isKeyboardMiniDockMode() && this.mIme.getInputContainerView() != null)) {
            this.mIme.getInputContainerView().invalidateItem();
            this.mIme.getInputContainerView().invalidate();
        }
        if (isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.handleActionMove(pointer, getKey(keyIndex));
        }
    }

    public void finishTrace(int pointerId) {
        Pointer pointer = this.mHshPointers.get(pointerId);
        if (pointer != null && !this.showCompleteTrace && pointer.isTraceDetected && pointer.tracePoints.size() > 0) {
            invalidate();
            this.mHandler.removeMessages(1025);
            this.mHandler.sendEmptyMessageDelayed(1025, 50L);
        }
    }

    public void resetTrace(int pointerId) {
        if (this.showCompleteTrace) {
            resetAllPointers();
        } else {
            Pointer pointer = this.mHshPointers.get(pointerId);
            if (pointer != null) {
                pointer.reset();
            }
        }
        invalidate();
    }

    public void onTouchHeldEnded(int pointerId, KeyboardEx.Key key) {
        KeyboardAccessibilityState state;
        if (!touchUpHandleBySlideSelectPopup(this.mHshPointers.get(pointerId), null)) {
            notifyKeyboardListenerOnText(key.altLabel != null ? key.altLabel : key.label, 0L);
        }
        dismissPopupKeyboard();
        dismissSingleAltCharPopup();
        dismissPreviewPopup();
        if (isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.handleActionUp(null, null);
        }
        cleanupScrubGesture();
    }

    public void onTouchHeldEnded(int pointerId, int keyIndex) {
        onTouchHeldEnded(pointerId, getKey(keyIndex));
    }

    public boolean onShortPress(int keyIndex, int pointerId) {
        return onShortPress(getKey(keyIndex), keyIndex, pointerId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTouchStarted(int pointerId, int keyIndex, float x, float y, int eventTime) {
        KeyboardAccessibilityState state;
        Pointer pointer = getPointerIdCreate(pointerId);
        pointer.add(new Point((int) x, (int) y), eventTime);
        if (isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.handleActionDown(pointer, getKey(keyIndex));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTouchEnded(int pointerId, int keyIndex, float x, float y, int eventTime) {
        KeyboardAccessibilityState state;
        Pointer pointer = getPointerIdCreate(pointerId);
        pointer.add(new Point((int) x, (int) y), eventTime);
        if (isExploreByTouchOn() && (state = getKeyboardAccessibilityState()) != null) {
            state.handleActionUp(pointer, getKey(keyIndex));
        }
        cleanupScrubGesture();
    }

    private Pointer getPointerIdCreate(int pointerId) {
        Pointer pointer = this.mHshPointers.get(pointerId);
        if (pointer == null) {
            Pointer pointer2 = new Pointer(pointerId);
            this.mHshPointers.put(pointerId, pointer2);
            return pointer2;
        }
        return pointer;
    }

    public AppSpecificBehavior getAppSpecificBehavior() {
        return IMEApplication.from(getContext()).getAppSpecificBehavior();
    }

    private boolean isKeyboardHeightWithinThresholdValue() {
        UserPreferences userPrefs = IMEApplication.from(getContext()).getUserPreferences();
        int landscapeHeight = (int) (userPrefs.getKeyboardScaleLandscape() * 100.0f);
        return (getResources().getConfiguration().orientation == 2 && landscapeHeight <= 100) || ((int) (userPrefs.getKeyboardScalePortrait() * 100.0f)) <= 100;
    }

    public void setIme(IME ime) {
        this.mIme = ime;
    }
}
