package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardStyle;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.util.DrawingUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.drawable.KeyboardBackgroundManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class KeyboardEx {
    public static final int EDGE_BOTTOM = 8;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    public static final int EDGE_SPLIT_LEFT = 16;
    public static final int EDGE_SPLIT_RIGHT = 32;
    public static final int EDGE_TOP = 4;
    public static final int GESTURE_KEYCODE_CASE_EDIT = -110;
    public static final int GESTURE_KEYCODE_GOOGLE_MAPS = -107;
    public static final int GESTURE_KEYCODE_HIDE_KEYBOARD = -108;
    public static final int GESTURE_KEYCODE_LAST_LANGUAGE = -111;
    public static final int GESTURE_KEYCODE_SEARCH = -112;
    public static final int GESTURE_KEYCODE_SUPPRESS_AUTOSPACE = -106;
    public static final int GESTURE_KEYCODE_WWW = -109;
    private static final int GRID_HEIGHT = 5;
    private static final int GRID_SIZE = 50;
    private static final int GRID_WIDTH = 10;
    public static final int KEYBOARDMODE_EMAIL_RESID = 2;
    public static final int KEYBOARDMODE_IM_RESID = 4;
    public static final int KEYBOARDMODE_NORMAL_RESID = 1;
    public static final int KEYBOARDMODE_NUMBER_RESID = 8;
    public static final float KEYBOARD_SCALE_MAX = 1.2f;
    public static final float KEYBOARD_SCALE_MIN = 0.8f;
    public static final int KEYC0DE_FULLWIDTH_PERIOD = 12290;
    public static final int KEYCODE_ABC_LAYER = 4078;
    public static final int KEYCODE_ACTIONONKEYBOARD_MENU = 6446;
    public static final int KEYCODE_ALTPOPUP = -200;
    public static final int KEYCODE_ARROW_DOWN = 4060;
    public static final int KEYCODE_ARROW_LEFT = 4029;
    public static final int KEYCODE_ARROW_RIGHT = 4059;
    public static final int KEYCODE_ARROW_UP = 4045;
    public static final int KEYCODE_BACK = 8;
    public static final int KEYCODE_CANGJIE_WILDCARD = 65311;
    public static final int KEYCODE_CLEAR_WORD = 4065;
    public static final int KEYCODE_COPY = 2898;
    public static final int KEYCODE_CUT = 2897;
    public static final int KEYCODE_DELETE = 8;
    public static final int KEYCODE_DELETE_WORD = 6431;
    public static final int KEYCODE_DELIMITER = 39;
    public static final int KEYCODE_DOT_COM = 43581;
    public static final int KEYCODE_DPAD_LEFT = 4061;
    public static final int KEYCODE_DPAD_RIGHT = 4062;
    public static final int KEYCODE_EDIT_LAYER = 6444;
    public static final int KEYCODE_EMOTICON = 4074;
    public static final int KEYCODE_ENTER = 10;
    public static final int KEYCODE_FULLWIDTH_QUESTIONMARK = 65311;
    public static final int KEYCODE_HANDWRITING = 2937;
    public static final int KEYCODE_HIRACYCLE = 43578;
    public static final int KEYCODE_INPUTMODE_MENU = 6447;
    public static final int KEYCODE_INVALID = 4063;
    public static final int KEYCODE_KANACONVERT = 43577;
    public static final int KEYCODE_KEYBOARD = 2936;
    public static final int KEYCODE_KEYBOARD_RESIZE = 2900;
    public static final int KEYCODE_LANGUAGE_MENU = 4087;
    public static final int KEYCODE_LANGUAGE_QUICK_SWITCH = 2939;
    public static final int KEYCODE_MODE_BACK = 43576;
    public static final int KEYCODE_MODE_CHANGE = 6462;
    public static final int KEYCODE_MULTITAP_DEADKEY = 2942;
    public static final int KEYCODE_MULTITAP_TOGGLE = 2940;
    public static final int KEYCODE_NUM = 4085;
    public static final int KEYCODE_PASTE = 2899;
    public static final int KEYCODE_PHONE_PAUSE = 4071;
    public static final int KEYCODE_PHONE_WAIT = 6430;
    public static final int KEYCODE_RANGE_CONVERT = -43;
    public static final int KEYCODE_RESIZE_FULL_SCREEN = -113;
    public static final int KEYCODE_RESIZE_MINILEFT_SCREEN = -114;
    public static final int KEYCODE_RESIZE_MINIMOVABLE_SCREEN = -117;
    public static final int KEYCODE_RESIZE_MINIRIGHT_SCREEN = -115;
    public static final int KEYCODE_RESIZE_SPLIT_SCREEN = -116;
    public static final int KEYCODE_SEGMENTATION = 43580;
    public static final int KEYCODE_SELECT = 43577;
    public static final int KEYCODE_SELECT_ALL = 2896;
    public static final int KEYCODE_SETTINGS = 2938;
    public static final int KEYCODE_SHIFT = 4068;
    public static final int KEYCODE_SHIFT_RIGHT = 6445;
    public static final int KEYCODE_SMILE_NO_RESIZE = -118;
    public static final int KEYCODE_SPACE = 32;
    public static final int KEYCODE_SPEECH = 6463;
    public static final int KEYCODE_SPEECH_APP = 2943;
    public static final int KEYCODE_SWITCH_WRITE_SCREEN = 43579;
    public static final int KEYCODE_SWYPE = 43575;
    public static final int KEYCODE_SYM_LAYER = 4077;
    public static final int KEYCODE_TAB = 9;
    public static final int KEYCODE_TONE1 = 177;
    public static final int KEYCODE_TONE2 = 178;
    public static final int KEYCODE_TONE3 = 179;
    public static final int KEYCODE_TONE4 = 180;
    public static final int KEYCODE_TONE5 = 181;
    public static final int KEYCODE_XT9_LANGUAGE_CYCLING = 2941;
    public static final int KEYCODE_ZERO_KEYPAD = 49;
    public static final int KEY_TYPE_FUNCTION = 4;
    public static final int KEY_TYPE_NONREGIONAL = 1;
    public static final int KEY_TYPE_REGIONAL = 0;
    public static final int KEY_TYPE_SMART_PUNCT = 2;
    public static final int KEY_TYPE_TEXT = 3;
    public static final int KEY_TYPE_UNSPECIFIED = -1;
    public static final int ROW_TYPE_NONNUMBER = 0;
    public static final int ROW_TYPE_NUMBER = 1;
    private static final float SEARCH_DISTANCE = 1.4f;
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_KEY = "Key";
    private static final String TAG_ROW = "Row";
    public Drawable background;
    int bottomMostKeyLocation;
    private boolean canSwypePopupCharacters;
    protected final Context context;
    private Key defaultKey;
    private boolean forcedSwypeable;
    private int iBot;
    private int iLeft;
    private int iRight;
    private int iTop;
    private EnumSet<KeyboardDockMode> invalidDockModes;
    private SparseArray<List<Key>> keyGroups;
    private EnumSet<KeyboardSettings> keyboardSettings;
    KeyboardStyle keyboardStyle;
    int leftMostKeyLocation;
    private int letterLanguageCategory;
    private int mCellHeight;
    private int mCellWidth;
    public final int mDisplayHeight;
    int mDisplayWidth;
    private boolean mForceBackgroundFromResource;
    private int mForcedHeight;
    private int[][] mGridNeighbors;
    private boolean mHasNumRow;
    protected boolean mIsPopup;
    private int mKdbId;
    private KeyboardDockMode mKeyboardDockMode;
    List<Row> mKeyboardLayout;
    private int mKeyboardModeId;
    protected final List<Key> mKeys;
    private KeyboardDockMode mPrimaryKeyboardDockMode;
    private int mTotalHeight;
    private int mTotalWidth;
    int rightMostKeyLocation;
    private boolean sanitizeFont;
    protected final List<Key> shiftKeys;
    private static final String TAG_KEYBOARD = "KeyboardEx";
    protected static final LogManager.Log log = LogManager.getLog(TAG_KEYBOARD);

    /* loaded from: classes.dex */
    public enum KeyboardLayerType {
        KEYBOARD_TEXT,
        KEYBOARD_SYMBOLS,
        KEYBOARD_PHONE,
        KEYBOARD_EDIT,
        KEYBOARD_NUM,
        KEYBOARD_NUM_PW,
        KEYBOARD_EMOJI,
        KEYBOARD_INVALID
    }

    /* loaded from: classes.dex */
    public enum KeyboardSettings {
        HANDWRITING(1),
        SPEECH(2),
        SWITCHABLE(4),
        SIZABLE(8),
        CURRENCY_DOLLAR(16),
        CURRENCY_POUND(32),
        CURRENCY_EURO(64),
        SPLITABLE(128),
        KEYBOARD(256),
        SPEECHPOP(512),
        EMOJI(1024);

        public final int value;

        KeyboardSettings(int value) {
            this.value = value;
        }

        public static EnumSet<KeyboardSettings> from(int flags) {
            EnumSet<KeyboardSettings> settings = EnumSet.noneOf(KeyboardSettings.class);
            for (KeyboardSettings setting : values()) {
                if ((setting.value & flags) != 0) {
                    settings.add(setting);
                }
            }
            return settings;
        }

        public static EnumSet<KeyboardSettings> current(Context context, InputMethods.Language lang) {
            EnumSet<KeyboardSettings> settings = EnumSet.noneOf(KeyboardSettings.class);
            if (UserPreferences.from(context).isHandwritingEnabled() && lang.supportsHwr() && !lang.getCurrentInputMode().mInputMode.equals(InputMethods.HANDWRITING_INPUT_MODE) && !lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN) && !lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN)) {
                settings.add(HANDWRITING);
            }
            if (KeyboardEx.shouldEnableSpeechKey(context) || lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_NINE_KEYS) || lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE) || lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS) || lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS) || lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS)) {
                settings.add(SPEECH);
            }
            if (lang.isKoreanLanguage() && lang.getCurrentInputMode().mInputMode.equals(InputMethods.HANDWRITING_INPUT_MODE)) {
                settings.add(KEYBOARD);
            }
            if (KeyboardEx.shouldEnableSpeechKey(context) && lang.isKoreanLanguage() && ((lang.getCurrentInputMode().getCurrentLayout() != null && lang.getCurrentInputMode().getCurrentLayout().mLayoutId == 2304) || (lang.getCurrentInputMode().getCurrentLayout() != null && lang.getCurrentInputMode().getCurrentLayout().mLayoutId == 3488))) {
                settings.add(SPEECHPOP);
            }
            if (IMEApplication.from(context).getIME() != null && !IMEApplication.from(context).getIME().isAccessibilitySupportEnabled()) {
                if (InputMethods.from(context).getFastSwitchedOffLanguage() != null || lang.isCJK()) {
                    settings.add(SWITCHABLE);
                }
                settings.add(SIZABLE);
                settings.add(EMOJI);
            }
            if (!lang.isCJK() || ((lang.getCurrentInputMode().getCurrentLayoutForCJK() == null || (lang.getCurrentInputMode().getCurrentLayoutForCJK().mLayoutId != 1536 && lang.getCurrentInputMode().getCurrentLayoutForCJK().mLayoutId != 1792 && lang.getCurrentInputMode().getCurrentLayoutForCJK().mLayoutId != 1808 && lang.getCurrentInputMode().getCurrentLayoutForCJK().mLayoutId != 1824)) && !lang.getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE))) {
                settings.add(SPLITABLE);
            }
            int currencyType = LocaleSettings.from(context).getCurrencyType(context.getResources().getConfiguration().locale);
            if (currencyType == 0) {
                currencyType = lang.currencyType;
            }
            settings.addAll(from(currencyType));
            return settings;
        }

        public static int matchCount(EnumSet<KeyboardSettings> set1, EnumSet<KeyboardSettings> set2) {
            int count = 0;
            Iterator it = set1.iterator();
            while (it.hasNext()) {
                KeyboardSettings setting = (KeyboardSettings) it.next();
                if (set2.contains(setting)) {
                    count++;
                }
            }
            return count;
        }
    }

    /* loaded from: classes.dex */
    public enum KeyboardDockMode {
        DOCK_FULL(1, 0, 0, R.string.kb_layout_fullscreen),
        DOCK_LEFT(8, 3, R.fraction.dock_side_keyboard_scale, R.string.kb_mini_left),
        DOCK_RIGHT(16, 4, R.fraction.dock_side_keyboard_scale, R.string.kb_mini_right),
        MOVABLE_MINI(4, 1, R.fraction.mini_keyboard_scale, R.string.kb_layout_movable),
        DOCK_SPLIT(2, 2, R.array.split_keyboard_scale, R.string.kb_layout_split);

        private final int flagValue;
        private final int levelValue;
        private final int nameResId;
        private float scaleLandscape;
        private float scalePortrait;
        private final int scaleResId;
        private float scaleText;

        KeyboardDockMode(int flagValue, int levelValue, int scaleResId, int nameResId) {
            this.flagValue = flagValue;
            this.levelValue = levelValue;
            this.scaleResId = scaleResId;
            if (scaleResId == 0) {
                this.scaleLandscape = 1.0f;
                this.scalePortrait = 1.0f;
            }
            this.nameResId = nameResId;
        }

        public final String getName(Resources res) {
            return res.getString(this.nameResId);
        }

        public final int getIconLevel(EnumSet<KeyboardDockMode> invalidModes) {
            KeyboardEx.log.d("getIconLevel...invalidModes: ", invalidModes);
            KeyboardDockMode dockMode = this;
            int modeCount = values().length;
            for (int count = 0; count < modeCount; count++) {
                KeyboardDockMode nextDockMode = values()[(dockMode.ordinal() + 1) % modeCount];
                if (invalidModes != null && !invalidModes.contains(nextDockMode)) {
                    break;
                }
                dockMode = nextDockMode;
            }
            KeyboardEx.log.d("getIconLevel...dockMode: ", dockMode, "...dockMode.levelValue: ", Integer.valueOf(dockMode.levelValue));
            return dockMode.levelValue;
        }

        public final int getTextSize(Resources res, int resId, KeyboardStyle style) {
            float scale = 1.0f;
            if (getScale(res) < 1.0f) {
                scale = this.scaleText;
            }
            return (int) (style.getDimensionPixelSize(resId, 0) * scale);
        }

        public final int getKeyboardWidth(Resources res) {
            initScales(res);
            DisplayMetrics dm = res.getDisplayMetrics();
            float scale = this == DOCK_SPLIT ? 1.0f : getScale(res);
            return (int) (dm.widthPixels * scale);
        }

        public final float getScale(Resources res) {
            initScales(res);
            return res.getConfiguration().orientation == 1 ? this.scalePortrait : this.scaleLandscape;
        }

        public final boolean isEnabled(Resources res, int orientation) {
            initScales(res);
            if (this == DOCK_FULL) {
                return true;
            }
            return (orientation == 1 ? this.scalePortrait : this.scaleLandscape) < 1.0f;
        }

        private void initScales(Resources res) {
            if (this.scalePortrait == 0.0f) {
                this.scalePortrait = getScaleForOrientation(res, 1);
                this.scaleLandscape = getScaleForOrientation(res, 2);
                this.scaleText = res.getFraction(R.fraction.text_scale, 1, 1);
            }
        }

        private float getScaleForOrientation(Resources res, int orientation) {
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            Configuration originalConfig = new Configuration(config);
            int currentOrientation = config.orientation;
            config.orientation = orientation;
            res.updateConfiguration(config, dm);
            TypedValue value = new TypedValue();
            float scale = 1.0f;
            if ("array".equalsIgnoreCase(res.getResourceTypeName(this.scaleResId))) {
                TypedArray a = res.obtainTypedArray(this.scaleResId);
                a.getValue(0, value);
                a.recycle();
            } else {
                res.getValue(this.scaleResId, value, true);
            }
            if (value.type == 6) {
                scale = value.getFraction(1.0f, 1.0f);
            } else if (value.type == 5) {
                float dimen = value.getDimension(dm);
                if (orientation == currentOrientation) {
                    scale = dimen / dm.widthPixels;
                } else {
                    scale = dimen / dm.heightPixels;
                }
                float keyWidth = res.getFraction(R.fraction.default_key_width, 1, 1);
                if (this == DOCK_SPLIT) {
                    keyWidth *= 2.0f;
                }
                float minScale = 1.0f - keyWidth;
                if (scale > minScale) {
                    scale = 1.0f;
                }
            }
            res.updateConfiguration(originalConfig, dm);
            return scale;
        }

        public static EnumSet<KeyboardDockMode> from(int flags) {
            EnumSet<KeyboardDockMode> settings = EnumSet.noneOf(KeyboardDockMode.class);
            for (KeyboardDockMode setting : values()) {
                if ((setting.flagValue & flags) != 0) {
                    settings.add(setting);
                }
            }
            return settings;
        }

        public static KeyboardDockMode fromInt(int value) {
            return (value < 0 || value >= values().length) ? DOCK_FULL : values()[value];
        }
    }

    /* loaded from: classes.dex */
    public enum ShiftTransition {
        DROP,
        DROP_SHOW,
        DROP_HIDE,
        SWAP;

        public static ShiftTransition convert(int value) {
            return (value < 0 || value >= values().length) ? DROP : values()[value];
        }
    }

    /* loaded from: classes.dex */
    public static class Row {
        private int iBot;
        private int iLeft;
        private int iRight;
        private int iTop;
        List<Key> mKeys;
        final KeyboardEx parent;
        public Drawable rowBackground;
        public int rowEdgeFlags;
        public int rowType;
        public int verticalGap;
        int visibleKeyCount;

        Row(Row row) {
            this.parent = row.parent;
            this.verticalGap = row.verticalGap;
            this.rowEdgeFlags = row.rowEdgeFlags;
        }

        public Row(KeyboardEx p) {
            this.parent = p;
        }

        public Row(Context context, KeyboardEx p, XmlResourceParser parser) {
            this.parent = p;
            this.verticalGap = 0;
            TypedArrayWrapper a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, parser, R.styleable.KeyboardEx_Row, 0, 0, 0, null);
            this.rowEdgeFlags = a.getInt(R.styleable.KeyboardEx_Row_rowEdgeFlags, 0);
            this.rowBackground = a.getDrawable(R.styleable.KeyboardEx_Row_rowBackground);
            this.rowType = a.getInt(R.styleable.KeyboardEx_Row_rowType, 0);
            a.recycle();
        }

        void addKey(Key key) {
            key.row = this;
            if (this.mKeys == null) {
                this.mKeys = new ArrayList();
            }
            this.mKeys.add(key);
        }

        void addKey(int index, Key key) {
            this.mKeys.add(index, key);
        }

        void remove(int index) {
            this.mKeys.remove(index);
        }

        public int keyCount() {
            if (this.mKeys != null) {
                return this.mKeys.size();
            }
            return 0;
        }

        public int getLeft() {
            return this.iLeft;
        }

        public int getRight() {
            return this.iRight;
        }

        public int getTop() {
            return this.iTop;
        }

        public int getBottom() {
            return this.iBot;
        }

        public int getHeight() {
            return this.iBot - this.iTop;
        }

        public void calcBounds() {
            int idxLast = this.mKeys.size() - 1;
            if (idxLast >= 0) {
                Key first = this.mKeys.get(0);
                Key last = this.mKeys.get(idxLast);
                this.iLeft = first.x;
                this.iTop = first.y;
                this.iRight = last.x + last.width;
                this.iBot = first.y + first.height;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class GridKeyInfo {
        public CharSequence text;
        public int width;

        public GridKeyInfo(int keyWidth, CharSequence txt) {
            this.width = keyWidth;
            this.text = txt;
        }
    }

    /* loaded from: classes.dex */
    public static class Key {
        public CharSequence accessibilityLabel;
        public int altCode;
        public Drawable altIcon;
        public int altIconGravity;
        public int altIconSize;
        public CharSequence altLabel;
        public CharSequence altLabelUpperCase;
        public int altPopupResId;
        public Drawable altPreviewIcon;
        public CharSequence altPreviewLabel;
        public CharSequence altText;
        public int altTextColor;
        public int altTextGravity;
        public int altTextSize;
        public boolean alwaysShowAltSymbol;
        public int baseline;
        public int[] codes;
        public List<Integer> dependentKeys;
        public boolean dimmed;
        public boolean disableMultitap;
        public int edgeFlags;
        public boolean focused;
        public int gap;
        public boolean hasMultilineLabel;
        public int height;
        public final int horizontalPadding;
        public Drawable icon;
        public Drawable iconPreview;
        public boolean immediatePopup;
        public boolean isAltPopupKept;
        public boolean isMiniKeyboardKey;
        public int keyFontTypeface;
        private int keyGroup;
        public int keyIconRecolor;
        public int keyTextColorPressed;
        public EnumSet<KeyboardSettings> keyboardSettings;
        public CharSequence label;
        public CharSequence labelUpperCase;
        public CharSequence leftAltLabel;
        public boolean lockable;
        public boolean locked;
        public int mComponentKeyTextColor;
        public int mDefaultStrokeCandidateColor;
        public Drawable mKeyBackground;
        public ColorStateList mKeyTextColor;
        public int mKeyTextSize;
        public int modeFlags;
        public boolean modifier;
        public int[] multitapChars;
        public boolean on;
        public CharSequence popupCharacters;
        public CharSequence popupCharactersSimplified;
        public CharSequence popupLabel;
        public int popupResId;
        public boolean pressed;
        public boolean repeatable;
        private boolean resizeWhenHidden;
        Row row;
        public int[] shiftChars;
        public int shiftCode;
        public CharSequence shiftText;
        public ShiftTransition shiftTransition;
        public Drawable shiftedIcon;
        public CharSequence shiftedLabel;
        public int shiftedPopupResId;
        public Drawable shiftedPreviewIcon;
        public boolean showPopup;
        public boolean sticky;
        public boolean supportFullSpaceSplit;
        public int tertiaryTextColor;
        public CharSequence text;
        public int type;
        public final int verticalPadding;
        public boolean visible;
        public int visibleIndex;
        public int width;
        public int x;
        public int y;
        private static final int[] KEY_STATE_NORMAL_OFF = new int[0];
        private static final int[] KEY_STATE_PRESSED_OFF = {android.R.attr.state_pressed};
        private static final int[] KEY_STATE_NORMAL_ON = {android.R.attr.state_checkable};
        private static final int[] KEY_STATE_PRESSED_ON = {android.R.attr.state_pressed, android.R.attr.state_checkable};
        private static final int[] KEY_STATE_NORMAL_LOCK = {android.R.attr.state_checked};
        private static final int[] KEY_STATE_PRESSED_LOCK = {android.R.attr.state_pressed, android.R.attr.state_checked};

        public String toString() {
            return "code: " + (this.codes != null ? Integer.valueOf(this.codes[0]) : "?") + "; rc: (" + this.x + "," + this.y + "), (" + this.width + "x" + this.height + ")";
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Key(Key key, int x, int y, int gap, int width, int height) {
            this.horizontalPadding = 0;
            this.verticalPadding = 0;
            this.isAltPopupKept = false;
            this.dependentKeys = null;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.gap = gap;
            this.altLabel = key.altLabel;
            this.altPreviewLabel = key.altPreviewLabel;
            this.leftAltLabel = key.leftAltLabel;
            this.altIcon = key.altIcon;
            this.codes = key.codes;
            this.altCode = key.altCode;
            this.altPopupResId = key.altPopupResId;
            this.altText = key.altText;
            this.baseline = key.baseline;
            this.altTextSize = key.altTextSize;
            this.altTextColor = key.altTextColor;
            this.keyTextColorPressed = key.keyTextColorPressed;
            this.tertiaryTextColor = key.tertiaryTextColor;
            this.altTextGravity = key.altTextGravity;
            this.dependentKeys = key.dependentKeys;
            this.resizeWhenHidden = key.resizeWhenHidden;
            this.edgeFlags = key.edgeFlags;
            this.icon = key.icon;
            this.iconPreview = key.iconPreview;
            this.immediatePopup = key.immediatePopup;
            this.label = key.label;
            this.hasMultilineLabel = key.hasMultilineLabel;
            this.locked = key.locked;
            this.lockable = key.lockable;
            this.keyboardSettings = key.keyboardSettings;
            this.mKeyBackground = key.mKeyBackground;
            this.mKeyTextColor = key.mKeyTextColor;
            this.mComponentKeyTextColor = key.mComponentKeyTextColor;
            this.mDefaultStrokeCandidateColor = key.mDefaultStrokeCandidateColor;
            this.mKeyTextSize = key.mKeyTextSize;
            this.modeFlags = key.modeFlags;
            this.on = key.on;
            this.popupCharacters = key.popupCharacters;
            this.popupCharactersSimplified = key.popupCharactersSimplified;
            this.popupLabel = key.popupLabel;
            this.popupResId = key.popupResId;
            this.pressed = key.pressed;
            this.repeatable = key.repeatable;
            this.row = key.row;
            this.sticky = key.sticky;
            this.visible = key.visible;
            this.type = key.type;
            this.text = key.text;
            this.keyFontTypeface = key.keyFontTypeface;
            this.shiftTransition = key.shiftTransition;
            this.shiftCode = key.shiftCode;
            this.shiftChars = key.shiftChars;
            this.shiftedIcon = key.shiftedIcon;
            this.shiftedLabel = key.shiftedLabel;
            this.shiftedPopupResId = key.shiftedPopupResId;
            this.shiftText = key.shiftText;
            this.showPopup = key.showPopup;
            this.supportFullSpaceSplit = key.supportFullSpaceSplit;
            this.alwaysShowAltSymbol = key.alwaysShowAltSymbol;
            this.disableMultitap = key.disableMultitap;
            if ((this.altLabel == null || this.altLabel.length() == 0) && this.popupCharacters != null && this.popupCharacters.length() > 0) {
                this.altLabel = this.popupCharacters.subSequence(0, 1);
            }
            this.isAltPopupKept = key.isAltPopupKept;
        }

        public Key(Row parent) {
            this.horizontalPadding = 0;
            this.verticalPadding = 0;
            this.isAltPopupKept = false;
            this.dependentKeys = null;
            this.row = parent;
        }

        public Key(Resources res, Row row, int x, int y, KeyboardStyle style) {
            this.horizontalPadding = 0;
            this.verticalPadding = 0;
            this.isAltPopupKept = false;
            this.dependentKeys = null;
            KeyboardEx keyboard = row.parent;
            this.row = row;
            this.x = x;
            this.y = y;
            this.width = style.getDimensionOrFraction(R.attr.keyWidth, keyboard.mDisplayWidth, 0);
            this.height = keyboard.getKeyHeight(style, keyboard.mDisplayHeight, 0);
            this.gap = style.getDimensionOrFraction(R.attr.horizontalGap, keyboard.mDisplayWidth, 0);
            this.x += this.gap;
            TypedValue codesValue = new TypedValue();
            style.getValue(R.attr.codes, codesValue);
            if (codesValue.type == 16 || codesValue.type == 17) {
                this.codes = new int[]{codesValue.data};
            } else if (codesValue.type == 3) {
                this.codes = parseCSV(codesValue.string.toString());
            }
            this.iconPreview = style.getDrawable(R.attr.iconPreview);
            if (this.iconPreview != null) {
                this.iconPreview.setBounds(0, 0, this.iconPreview.getIntrinsicWidth(), this.iconPreview.getIntrinsicHeight());
            }
            this.altIcon = style.getDrawable(R.attr.altIcon);
            if (this.altIcon != null) {
                this.altIcon.setBounds(0, 0, this.altIcon.getIntrinsicWidth(), this.altIcon.getIntrinsicHeight());
            }
            this.popupCharacters = style.getText(R.attr.popupCharacters);
            this.popupCharactersSimplified = style.getText(R.attr.popupCharactersSimplified);
            KeyboardStyle.StyleLevel level = KeyboardStyle.StyleLevel.KEY;
            if (this.popupCharacters != null && this.popupCharacters.length() > 0) {
                level = KeyboardStyle.StyleLevel.ALL;
            }
            this.popupResId = style.getResourceId(R.attr.popupKeyboard, level);
            this.altPopupResId = style.getResourceId(R.attr.altPopupKeyboard, KeyboardStyle.StyleLevel.KEY);
            this.shiftedPopupResId = style.getResourceId(R.attr.shiftedPopupKeyboard, KeyboardStyle.StyleLevel.KEY);
            this.repeatable = style.getBoolean(R.attr.isRepeatable, false);
            this.modifier = style.getBoolean(R.attr.isModifier, false);
            this.sticky = style.getBoolean(R.attr.isSticky, false);
            this.lockable = style.getBoolean(R.attr.isLockable, false);
            this.edgeFlags = style.getInt(R.attr.keyEdgeFlags, 0);
            this.type = style.getInt(R.attr.keyType, -1);
            this.modeFlags = style.getInt(R.attr.keyboardMode, 0);
            this.edgeFlags |= row.rowEdgeFlags;
            this.icon = style.getDrawable(R.attr.keyIcon);
            if (this.icon != null) {
                this.icon.setBounds(0, 0, this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight());
            }
            this.label = style.getText(R.attr.keyLabel);
            this.altLabel = style.getText(R.attr.altKeyLabel);
            this.altPreviewLabel = style.getText(R.attr.altPreviewLabel);
            this.leftAltLabel = style.getText(R.attr.leftAltKeyLabel);
            this.shiftedLabel = style.getText(R.attr.shiftedKeyLabel);
            this.shiftedIcon = style.getDrawable(R.attr.shiftedKeyIcon);
            this.shiftedPreviewIcon = style.getDrawable(R.attr.shiftedPreviewIcon);
            if (this.shiftedPreviewIcon == null) {
                this.shiftedPreviewIcon = this.shiftedIcon;
            }
            this.text = style.getText(R.attr.keyOutputText);
            this.shiftText = style.getText(R.attr.shiftedOutputText);
            this.altText = style.getText(R.attr.altKeyOutputText);
            this.popupLabel = style.getText(R.attr.popupLabel);
            this.disableMultitap = style.getBoolean(R.attr.disableMultitap, false);
            this.accessibilityLabel = style.getText(R.attr.accessibilitylabel);
            this.supportFullSpaceSplit = style.getBoolean(R.attr.supportFullSpaceSplit, true);
            if (this.label != null) {
                this.hasMultilineLabel = this.label.toString().split("\n").length > 1;
            }
            if (this.shiftedLabel != null && !this.hasMultilineLabel) {
                this.hasMultilineLabel = this.shiftedLabel.toString().split("\n").length > 1;
            }
            this.immediatePopup = style.getBoolean(R.attr.immediatePopup, false);
            this.alwaysShowAltSymbol = style.getBoolean(R.attr.alwaysShowAltSymbol, false);
            this.showPopup = !this.immediatePopup && style.getBoolean(R.attr.showPopup, style.getDefaultKeyStyleSetting(this.codes, R.attr.showPopup, true));
            this.keyIconRecolor = style.getInt(R.attr.keyIconRecolor, 0);
            TypedValue multitapCharsValue = new TypedValue();
            style.getValue(R.attr.keyMultitapChars, multitapCharsValue);
            if (!TextUtils.isEmpty(multitapCharsValue.string)) {
                this.multitapChars = parseCSV(multitapCharsValue.string.toString());
            } else {
                this.multitapChars = null;
            }
            resetVisibility();
            if (this.codes == null) {
                if (!TextUtils.isEmpty(this.text)) {
                    this.type = 3;
                } else if (!TextUtils.isEmpty(this.label)) {
                    this.codes = new int[]{this.label.charAt(0)};
                }
                this.codes = new int[]{KeyboardEx.KEYCODE_INVALID};
            }
            int codeDefault = KeyboardEx.KEYCODE_INVALID;
            TypedValue shiftsCharsValue = new TypedValue();
            style.getValue(R.attr.shiftCode, shiftsCharsValue);
            if (shiftsCharsValue.type == 16 || shiftsCharsValue.type == 17) {
                this.shiftChars = new int[]{shiftsCharsValue.data};
                this.shiftCode = this.shiftChars[0];
            } else if (shiftsCharsValue.type == 3) {
                this.shiftChars = parseCSV(shiftsCharsValue.string.toString());
                this.shiftCode = this.shiftChars[0];
            } else {
                this.shiftChars = null;
                if (this.shiftedLabel != null && this.shiftText == null) {
                    codeDefault = this.shiftedLabel.charAt(0);
                }
                this.shiftCode = codeDefault;
            }
            int codeDefault2 = KeyboardEx.KEYCODE_INVALID;
            if (this.altLabel != null && this.altText == null) {
                codeDefault2 = this.altLabel.charAt(0);
            }
            this.altCode = style.getInt(R.attr.altCode, codeDefault2);
            this.mKeyBackground = style.getDrawable(R.attr.keyBackground);
            this.mKeyTextSize = keyboard.getKeyTextSize(style, res);
            this.altTextSize = keyboard.getAlternateTextSize(style, res);
            this.altIconSize = keyboard.getAlternateIconSize(style, res);
            this.mKeyTextColor = style.getColorStateList(R.attr.keyTextColor, 0);
            this.mComponentKeyTextColor = style.getInt(R.attr.candidateComponent, 0);
            this.mDefaultStrokeCandidateColor = style.getInt(R.attr.defaultStrokeCandidateColor, 0);
            this.baseline = style.getDimensionOrFraction(R.attr.keyContentBaseline, this.height, 0);
            this.altTextColor = style.getInt(R.attr.altTextColor, 0);
            this.tertiaryTextColor = style.getInt(R.attr.tertiaryTextColor, 0);
            this.keyTextColorPressed = style.getInt(R.attr.keyTextColorPressed, 0);
            this.altTextGravity = style.getInt(R.attr.altTextGravity, 0);
            this.altIconGravity = style.getInt(R.attr.altIconGravity, 0);
            this.altIcon = style.getDrawable(R.attr.altIcon);
            this.altPreviewIcon = style.getDrawable(R.attr.altPreviewIcon);
            this.keyFontTypeface = style.getInt(R.attr.keyFontTypeface, 0);
            if (this.altPreviewIcon == null) {
                this.altPreviewIcon = this.altIcon;
            }
            if (this.altIcon != null && this.altIconGravity == 0) {
                this.altIconGravity = this.altTextGravity;
            }
            int dependentKeyResId = style.getResourceId(R.attr.dependentKey);
            if (dependentKeyResId != 0) {
                this.dependentKeys = new ArrayList();
                if (res.getResourceTypeName(dependentKeyResId).equals("array")) {
                    for (int keyCode : res.getIntArray(dependentKeyResId)) {
                        this.dependentKeys.add(Integer.valueOf(keyCode));
                    }
                } else {
                    this.dependentKeys.add(Integer.valueOf(res.getInteger(dependentKeyResId)));
                }
            }
            this.shiftTransition = ShiftTransition.convert(style.getInt(R.attr.shiftTransition, ShiftTransition.DROP.ordinal()));
            this.keyboardSettings = KeyboardSettings.from(style.getInt(R.attr.keyboardSettings, 0));
            this.resizeWhenHidden = style.getBoolean(R.attr.resizeWhenHidden, true);
            this.keyGroup = style.getResourceId(R.attr.keyGroup);
            this.isAltPopupKept = style.getBoolean(R.attr.keepAltKeyPopup, false);
        }

        public final void resetVisibility() {
            this.visible = this.width > 0 && this.height > 0;
        }

        public void onPressed() {
            this.pressed = !this.pressed;
        }

        public boolean isPressed() {
            return this.pressed;
        }

        public void onReleased(boolean inside) {
            this.pressed = !this.pressed;
            if (inside) {
                if (this.lockable) {
                    boolean lastLocked = this.locked;
                    this.locked = this.on;
                    this.on = (lastLocked || this.on) ? false : true;
                } else if (this.sticky) {
                    this.on = this.on ? false : true;
                }
            }
        }

        public boolean hasIconDescription() {
            return (this.icon == null || this.popupLabel == null) ? false : true;
        }

        protected static int[] parseCSV(String value) {
            int chr;
            int count = 0;
            int lastIndex = 0;
            if (value.length() > 0) {
                do {
                    count++;
                    lastIndex = value.indexOf(",", lastIndex + 1);
                } while (lastIndex > 0);
            }
            int[] values = new int[count];
            int count2 = 0;
            StringTokenizer st = new StringTokenizer(value, ",");
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                try {
                    chr = Integer.decode(token).intValue();
                } catch (NumberFormatException e) {
                    chr = token.charAt(0);
                }
                values[count2] = chr;
                count2++;
            }
            return values;
        }

        public boolean isInside(int x, int y) {
            boolean leftEdge = (this.edgeFlags & 1) == 1;
            boolean rightEdge = (this.edgeFlags & 2) == 2;
            boolean topEdge = (this.edgeFlags & 4) == 4;
            boolean bottomEdge = (this.edgeFlags & 8) == 8;
            if ((x >= this.x || (leftEdge && x <= this.x + this.width)) && ((x < this.x + this.width || (rightEdge && x >= this.x)) && (y >= this.y || (topEdge && y <= this.y + this.height)))) {
                if (y < this.y + this.height) {
                    return true;
                }
                if (bottomEdge && y >= this.y) {
                    return true;
                }
            }
            return false;
        }

        public boolean contains(int x, int y) {
            return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
        }

        public int squaredDistanceFrom(int x, int y) {
            int xDist = (this.x + (this.width / 2)) - x;
            int yDist = (this.y + (this.height / 2)) - y;
            return (xDist * xDist) + (yDist * yDist);
        }

        public int squaredDistanceToEdge(int x, int y) {
            int edgeX;
            int edgeY;
            int left = this.x;
            int right = left + this.width;
            int top = this.y;
            int bottom = top + this.height;
            if (x < left) {
                edgeX = left;
            } else {
                edgeX = x > right ? right : x;
            }
            if (y < top) {
                edgeY = top;
            } else {
                edgeY = y > bottom ? bottom : y;
            }
            int dx = x - edgeX;
            int dy = y - edgeY;
            return (dx * dx) + (dy * dy);
        }

        public int[] getCurrentDrawableState(boolean includePress) {
            boolean isPressed = this.pressed && includePress;
            if (this.on) {
                if (!isPressed) {
                    int[] states = KEY_STATE_NORMAL_ON;
                    return states;
                }
                int[] states2 = KEY_STATE_PRESSED_ON;
                return states2;
            }
            if (this.locked) {
                if (isPressed) {
                    int[] states3 = KEY_STATE_PRESSED_LOCK;
                    return states3;
                }
                int[] states4 = KEY_STATE_NORMAL_LOCK;
                return states4;
            }
            Context context = this.row.parent.getContext();
            if (UserPreferences.from(context).isHardwareKeyboardEnabled(context)) {
                if (this.focused) {
                    int[] states5 = KEY_STATE_PRESSED_ON;
                    return states5;
                }
                if (isPressed) {
                    int[] states6 = KEY_STATE_PRESSED_OFF;
                    return states6;
                }
                int[] states7 = KEY_STATE_NORMAL_OFF;
                return states7;
            }
            if (isPressed) {
                int[] states8 = KEY_STATE_PRESSED_OFF;
                return states8;
            }
            int[] states9 = KEY_STATE_NORMAL_OFF;
            return states9;
        }

        public Rect getVisibleBounds() {
            return new Rect(0, 0, this.width + 0, this.height + 0);
        }
    }

    private void makeSplitKeyboard(Context context) {
        Key leftKey;
        Key rightKey;
        int mid = this.mTotalWidth / 2;
        int leftShortestRowWidth = mid;
        int rightLongestRowWidth = mid;
        for (Row row : this.mKeyboardLayout) {
            boolean splitFound = false;
            int halfCount = (row.visibleKeyCount + 1) / 2;
            for (int i = 0; i < row.mKeys.size() && !splitFound; i++) {
                Key key = row.mKeys.get(i);
                if (key.visible && key.x <= mid && mid <= key.x + key.width) {
                    int splitIndex = i;
                    if (key.codes[0] != 32) {
                        if (key.visibleIndex == halfCount && i > 0) {
                            splitIndex = i - 1;
                            key = row.mKeys.get(splitIndex);
                        }
                        leftKey = key;
                        rightKey = row.mKeys.get(splitIndex + 1);
                    } else {
                        leftKey = key;
                        rightKey = row.mKeys.get(i + 1);
                    }
                    leftKey.edgeFlags |= 32;
                    rightKey.edgeFlags |= 16;
                    splitFound = true;
                } else if ((key.edgeFlags & 32) != 0) {
                    splitFound = true;
                }
                if (splitFound) {
                    int leftWidth = key.x + key.width;
                    if (leftShortestRowWidth > leftWidth) {
                        leftShortestRowWidth = leftWidth;
                    }
                    int rightWidth = this.mTotalWidth - leftWidth;
                    if (rightLongestRowWidth < rightWidth) {
                        rightLongestRowWidth = rightWidth;
                    }
                }
            }
        }
        float leftSplitScale = mid / leftShortestRowWidth;
        float rightSplitScale = mid / rightLongestRowWidth;
        split(leftSplitScale, rightSplitScale, context.getResources().getBoolean(R.bool.split_keyboard_for_phone));
        changeKeys(this.mKeyboardLayout);
    }

    public final KeyboardDockMode getKeyboardDockMode() {
        return this.mKeyboardDockMode;
    }

    public final boolean isKeyWidthReducedDockMode() {
        return this.mKeyboardDockMode == KeyboardDockMode.MOVABLE_MINI || this.mKeyboardDockMode == KeyboardDockMode.DOCK_LEFT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_RIGHT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_SPLIT;
    }

    public final boolean isKeyboardMiniDockMode() {
        return this.mKeyboardDockMode == KeyboardDockMode.DOCK_LEFT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_RIGHT;
    }

    public final boolean isKeyboardFullDockMode() {
        return this.mKeyboardDockMode == KeyboardDockMode.DOCK_FULL;
    }

    public void setKeyboardDockMode(KeyboardDockMode dockMode) {
        if (dockMode != this.mKeyboardDockMode) {
            Resources res = this.context.getResources();
            this.mKeyboardDockMode = dockMode;
            this.mPrimaryKeyboardDockMode = dockMode;
            this.mDisplayWidth = getKeyboardScaledWidth(res);
            this.mGridNeighbors = null;
            this.mKeys.clear();
            this.shiftKeys.clear();
            loadKeyboard(this.context, res.getXml(this.mKdbId), true);
        }
    }

    private void changeKeys(List<Row> keyboardLayout) {
        this.mGridNeighbors = null;
        this.mKeys.clear();
        this.shiftKeys.clear();
        Iterator<Row> it = keyboardLayout.iterator();
        while (it.hasNext()) {
            for (Key key : it.next().mKeys) {
                this.mKeys.add(key);
                if (isShiftKey(key.codes[0])) {
                    this.shiftKeys.add(key);
                }
            }
        }
    }

    private void scaleX(Key key, float xScale) {
        key.gap = (int) (key.gap * xScale);
        key.width = (int) (key.width * xScale);
    }

    private void split(float leftSplitScale, float rightSplitScale, boolean isSplitPhone) {
        this.rightMostKeyLocation = 0;
        this.leftMostKeyLocation = 0;
        this.bottomMostKeyLocation = 0;
        Resources res = this.context.getResources();
        for (Row row : this.mKeyboardLayout) {
            if (iSplitAbleRow(row, 32) && iSplitAbleRow(row, 16)) {
                float splitKeyboardWidthScale = this.mKeyboardDockMode != null ? this.mKeyboardDockMode.getScale(res) : KeyboardDockMode.DOCK_SPLIT.getScale(res);
                Key rightMostKeyBeforeSplit = horizontalSplitLeft(splitKeyboardWidthScale * leftSplitScale, row, isSplitPhone);
                Key leftMostKeyAfterSplit = horizontalSplitRight(splitKeyboardWidthScale * rightSplitScale, row, isSplitPhone);
                if (rightMostKeyBeforeSplit.codes[0] != 32 || !rightMostKeyBeforeSplit.supportFullSpaceSplit) {
                    if (rightMostKeyBeforeSplit.x + rightMostKeyBeforeSplit.width > this.rightMostKeyLocation) {
                        this.rightMostKeyLocation = rightMostKeyBeforeSplit.x + rightMostKeyBeforeSplit.width;
                    }
                    if (this.leftMostKeyLocation == 0 || this.leftMostKeyLocation > leftMostKeyAfterSplit.x - leftMostKeyAfterSplit.gap) {
                        this.leftMostKeyLocation = leftMostKeyAfterSplit.x - leftMostKeyAfterSplit.gap;
                    }
                    if (this.bottomMostKeyLocation == 0 || this.bottomMostKeyLocation < rightMostKeyBeforeSplit.y + rightMostKeyBeforeSplit.height) {
                        this.bottomMostKeyLocation = rightMostKeyBeforeSplit.y + rightMostKeyBeforeSplit.height;
                    }
                    if (this.bottomMostKeyLocation == 0 || this.bottomMostKeyLocation < leftMostKeyAfterSplit.y + leftMostKeyAfterSplit.height) {
                        this.bottomMostKeyLocation = leftMostKeyAfterSplit.y + leftMostKeyAfterSplit.height;
                    }
                }
            }
        }
    }

    private Key horizontalSplitLeft(float xScale, Row row, boolean isSplitPhone) {
        List<Key> keys = row.mKeys;
        Key rightMostKeyBeforeSplit = null;
        int x = 0;
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            Key key = keys.get(i);
            if ((key.edgeFlags & 16) != 0) {
                break;
            }
            if (key.visible) {
                scaleX(key, xScale);
                int x2 = x + key.gap;
                key.x = x2;
                x = x2 + key.width;
                rightMostKeyBeforeSplit = key;
            }
        }
        return rightMostKeyBeforeSplit;
    }

    private Key horizontalSplitRight(float xScale, Row row, boolean isSplitPhone) {
        List<Key> keys = row.mKeys;
        for (int i = keys.size() - 1; i >= 0 && !row.mKeys.get(i).visible; i--) {
        }
        int x = this.mTotalWidth;
        Key leftMostKeyAfterSplit = null;
        int i2 = keys.size();
        while (true) {
            i2--;
            if (i2 < 0) {
                break;
            }
            Key key = keys.get(i2);
            if ((key.edgeFlags & 32) != 0) {
                if (key.codes[0] == 32 && leftMostKeyAfterSplit.x > key.x) {
                    if (key.supportFullSpaceSplit) {
                        key.width = leftMostKeyAfterSplit.x - key.x;
                    } else {
                        key.width = leftMostKeyAfterSplit.width;
                    }
                }
            } else if (key.visible) {
                scaleX(key, xScale);
                int x2 = x - key.width;
                key.x = x2;
                x = x2 - key.gap;
                leftMostKeyAfterSplit = key;
            }
        }
        return leftMostKeyAfterSplit;
    }

    private boolean iSplitAbleRow(Row row, int flag) {
        Iterator<Key> it = row.mKeys.iterator();
        while (it.hasNext()) {
            if ((it.next().edgeFlags & flag) != 0) {
                return true;
            }
        }
        return false;
    }

    public KeyboardEx(Context context, int xmlLayoutResId, boolean fillScreen, boolean isPopup, boolean forceBackgroundFromResource) {
        this(context, xmlLayoutResId, 0, fillScreen, isPopup, UserPreferences.from(context).getKeyboardDockingMode(), forceBackgroundFromResource);
    }

    public KeyboardEx(Context context, int xmlLayoutResId, boolean fillScreen, boolean forceBackgroundFromResource) {
        this(context, xmlLayoutResId, 0, fillScreen, forceBackgroundFromResource);
    }

    public KeyboardEx(Context context, int xmlLayoutResId, int modeId, boolean fillScreen, boolean forceBackgroundFromResource) {
        this(context, xmlLayoutResId, modeId, fillScreen, false, UserPreferences.from(context).getKeyboardDockingMode(), forceBackgroundFromResource);
    }

    public KeyboardEx(Context context, int xmlLayoutResId, int modeId, boolean fillScreen, boolean isPopup, KeyboardDockMode dockMode, boolean forceBackgroundFromResource) {
        this.mKeyboardDockMode = KeyboardDockMode.DOCK_FULL;
        this.mPrimaryKeyboardDockMode = KeyboardDockMode.DOCK_FULL;
        this.mIsPopup = false;
        this.sanitizeFont = true;
        this.keyGroups = new SparseArray<>();
        this.keyboardSettings = EnumSet.noneOf(KeyboardSettings.class);
        this.context = IMEApplication.from(context).getThemedContext();
        Resources res = context.getResources();
        this.mPrimaryKeyboardDockMode = dockMode;
        this.mForceBackgroundFromResource = forceBackgroundFromResource;
        if (IMEApplication.from(context).getIME() != null && IMEApplication.from(context).getIME().isAccessibilitySupportEnabled()) {
            dockMode = KeyboardDockMode.DOCK_FULL;
        } else if (isPopup) {
            int orientation = context.getResources().getConfiguration().orientation;
            if (dockMode == KeyboardDockMode.DOCK_SPLIT) {
                if (KeyboardDockMode.MOVABLE_MINI.isEnabled(res, orientation)) {
                    dockMode = KeyboardDockMode.MOVABLE_MINI;
                } else {
                    dockMode = KeyboardDockMode.DOCK_LEFT;
                }
            } else if (dockMode == KeyboardDockMode.DOCK_FULL) {
                if (KeyboardDockMode.MOVABLE_MINI.isEnabled(res, orientation)) {
                    dockMode = KeyboardDockMode.MOVABLE_MINI;
                } else {
                    dockMode = KeyboardDockMode.DOCK_LEFT;
                }
            }
            this.mIsPopup = isPopup;
        }
        this.mKeyboardDockMode = dockMode;
        DisplayMetrics dm = res.getDisplayMetrics();
        this.mDisplayWidth = getKeyboardScaledWidth(res);
        this.mDisplayHeight = dm.heightPixels;
        this.mKeys = new ArrayList();
        this.shiftKeys = new ArrayList();
        this.mKeyboardModeId = modeId;
        this.mKdbId = xmlLayoutResId;
        loadKeyboard(this.context, res.getXml(xmlLayoutResId), fillScreen);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Key getDefaultKey() {
        return this.defaultKey;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDefaultKey(Key key) {
        this.defaultKey = key;
    }

    protected static void initKeyStyleValues(Key key, KeyboardStyle keyboardStyle) {
        key.mKeyTextSize = keyboardStyle.getDimensionPixelSize(R.attr.keyTextSize, 0);
        key.mKeyTextColor = keyboardStyle.getColorStateList(R.attr.keyTextColor, 0);
        key.mComponentKeyTextColor = keyboardStyle.getInt(R.attr.candidateComponent, 0);
        key.baseline = keyboardStyle.getDimensionOrFraction(R.attr.keyContentBaseline, key.height, 0);
        key.altTextSize = keyboardStyle.getDimensionPixelSize(R.attr.altTextSize, 0);
        key.altTextColor = keyboardStyle.getInt(R.attr.altTextColor, 0);
        key.tertiaryTextColor = keyboardStyle.getInt(R.attr.tertiaryTextColor, 0);
        key.keyTextColorPressed = keyboardStyle.getInt(R.attr.keyTextColorPressed, 0);
        key.altTextGravity = keyboardStyle.getInt(R.attr.altTextGravity, 0);
        key.altIconSize = keyboardStyle.getDimensionPixelSize(R.attr.altIconSize, 0);
        key.altIconGravity = keyboardStyle.getInt(R.attr.altIconGravity, 0);
        key.mKeyBackground = keyboardStyle.getDrawable(R.attr.keyBackground);
        key.keyFontTypeface = keyboardStyle.getInt(R.attr.keyFontTypeface, 0);
        key.mDefaultStrokeCandidateColor = keyboardStyle.getInt(R.attr.defaultStrokeCandidateColor, 0);
    }

    protected static Key createKey(KeyboardStyle keyboardStyle) {
        Key key = new Key(null);
        if (keyboardStyle != null) {
            initKeyStyleValues(key, keyboardStyle);
        }
        key.visible = true;
        return key;
    }

    protected static Key createCharKey(KeyboardStyle keyboardStyle, char ch) {
        Key key = createKey(keyboardStyle);
        key.label = String.valueOf(ch);
        key.codes = new int[]{ch};
        return key;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Key createIconKey(KeyboardStyle keyboardStyle, Drawable icon, int keyCode) {
        Key key = createKey(keyboardStyle);
        key.label = null;
        key.codes = new int[]{keyCode};
        key.iconPreview = null;
        key.shiftedIcon = icon;
        key.icon = icon;
        return key;
    }

    protected static void swapAltCharKeyboardKeyData(Key lhs, Key rhs) {
        CharSequence label = rhs.label;
        int[] codes = rhs.codes;
        int altCode = rhs.altCode;
        Drawable icon = rhs.icon;
        Drawable shiftedIcon = rhs.shiftedIcon;
        rhs.label = lhs.label;
        rhs.altCode = lhs.altCode;
        rhs.icon = lhs.icon;
        rhs.shiftedIcon = lhs.shiftedIcon;
        rhs.codes = lhs.codes;
        lhs.label = label;
        lhs.icon = icon;
        lhs.shiftedIcon = shiftedIcon;
        lhs.codes = codes;
        lhs.altCode = altCode;
    }

    public KeyboardEx(Context context, int layoutTemplateResId, CharSequence characters, Key extraKey, int idxDefault, int gravity, int columns, int horizontalPadding, boolean forceBackgroundFromResource) {
        this(context, layoutTemplateResId, false, forceBackgroundFromResource);
        int y = 0;
        int column = 0;
        this.mTotalWidth = 0;
        Character chDefault = null;
        int width = this.keyboardStyle.getDimensionOrFraction(R.attr.keyWidth, this.mDisplayWidth, this.mDisplayWidth / 10);
        int height = getKeyHeight(this.keyboardStyle, this.mDisplayHeight, 50);
        int horizontalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.horizontalGap, this.mDisplayWidth, 0);
        int verticalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.verticalGap, this.mDisplayHeight, 0);
        int hGrav = gravity & 7;
        boolean isGravityRight = hGrav == 5 || hGrav == 8388613;
        Row row = new Row(this);
        row.verticalGap = verticalGap;
        if (extraKey != null) {
            extraKey.row = row;
            extraKey.x = 0;
            extraKey.y = 0;
            extraKey.width = width;
            extraKey.height = height;
            extraKey.gap = horizontalGap;
            extraKey.edgeFlags = 1;
            initKeyStyleValues(extraKey, this.keyboardStyle);
            column = 0 + 1;
            int x = width + horizontalGap + 0;
            this.mTotalWidth = x;
            row.addKey(extraKey);
            if (-1 == idxDefault) {
                this.defaultKey = extraKey;
            }
        } else if (idxDefault != -1) {
            chDefault = Character.valueOf(characters.charAt(idxDefault));
        }
        if (chDefault != null) {
            StringBuilder slideMenuBuilder = new StringBuilder();
            slideMenuBuilder.append(chDefault);
            slideMenuBuilder.append(characters.subSequence(0, idxDefault));
            if (idxDefault + 1 < characters.length()) {
                slideMenuBuilder.append(characters.subSequence(idxDefault + 1, characters.length()));
            }
            characters = slideMenuBuilder.subSequence(0, slideMenuBuilder.length());
        }
        int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);
            if (column >= maxColumns) {
                this.mKeyboardLayout.add(row);
                if (row.mKeys.size() > 0) {
                    if (isGravityRight) {
                        row.mKeys.get(row.mKeys.size() - 1).edgeFlags = 1;
                    } else {
                        row.mKeys.get(row.mKeys.size() - 1).edgeFlags = 2;
                    }
                }
                row = new Row(this);
                row.verticalGap = verticalGap;
                column = 0;
            }
            Key key = createCharKey(this.keyboardStyle, c);
            key.row = row;
            row.addKey(key);
            if (row.mKeys.size() == 1) {
                if (isGravityRight) {
                    key.edgeFlags = 2;
                } else {
                    key.edgeFlags = 1;
                }
            }
            column++;
            if (chDefault != null && chDefault.equals(Character.valueOf(c))) {
                this.defaultKey = key;
            }
        }
        if (row.keyCount() > 0) {
            this.mKeyboardLayout.add(row);
            if (row.mKeys.size() > 0) {
                if (isGravityRight) {
                    row.mKeys.get(row.mKeys.size() - 1).edgeFlags = 1;
                } else {
                    row.mKeys.get(row.mKeys.size() - 1).edgeFlags = 2;
                }
            }
        }
        Collections.reverse(this.mKeyboardLayout);
        int rows = this.mKeyboardLayout.size();
        if (isGravityRight) {
            Iterator<Row> it = this.mKeyboardLayout.iterator();
            while (it.hasNext()) {
                Collections.reverse(it.next().mKeys);
            }
        }
        for (int i2 = 0; i2 < rows; i2++) {
            int x2 = 0;
            for (int j = 0; j < this.mKeyboardLayout.get(i2).mKeys.size(); j++) {
                Key key2 = this.mKeyboardLayout.get(i2).mKeys.get(j);
                key2.x = x2;
                key2.y = y;
                key2.width = width;
                key2.height = height;
                key2.gap = horizontalGap;
                key2.isMiniKeyboardKey = true;
                x2 += key2.width + key2.gap;
                this.mKeys.add(key2);
                this.mTotalWidth = Math.max(x2, this.mTotalWidth);
            }
            y += verticalGap + height;
        }
        this.mTotalHeight = y;
        if (hGrav != 0) {
            justifyRows(this.mKeyboardLayout, hGrav, this.mTotalWidth);
        }
        updateIconLevels();
    }

    public KeyboardEx(Context context, int layoutTemplateResId, List<CharSequence> words, int gravity, int width, int minHeight, int horizontalPadding) {
        this(context, layoutTemplateResId, words, gravity, width, minHeight, horizontalPadding, UserPreferences.from(context).getKeyboardDockingMode());
    }

    public KeyboardEx(Context context, int layoutTemplateResId, List<CharSequence> words, int gravity, int width, int minHeight, int horizontalPadding, KeyboardDockMode dockMode) {
        this(context, layoutTemplateResId, 0, false, false, dockMode, true);
        int x = 0;
        int y = 0;
        int column = 0;
        this.mTotalWidth = width;
        int keyHeight = getKeyHeight(this.keyboardStyle, this.mDisplayHeight, 50);
        int horizontalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.horizontalGap, this.mDisplayWidth, 0);
        int verticalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.verticalGap, this.mDisplayHeight, 0);
        Row row = new Row(this);
        row.verticalGap = verticalGap;
        row.rowEdgeFlags = 12;
        int keyTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.keyTextSize, 0);
        ColorStateList keyTextColor = this.keyboardStyle.getColorStateList(R.attr.keyTextColor, 0);
        int componentkeyTextColor = this.keyboardStyle.getInt(R.attr.candidateComponent, 0);
        int defaultStrokeCandidateColor = this.keyboardStyle.getInt(R.attr.defaultStrokeCandidateColor, 0);
        int baseline = this.keyboardStyle.getDimensionOrFraction(R.attr.keyContentBaseline, keyHeight, 0);
        int altTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altTextSize, 0);
        int altTextColor = this.keyboardStyle.getInt(R.attr.altTextColor, 0);
        int tertiaryTextColor = this.keyboardStyle.getInt(R.attr.tertiaryTextColor, 0);
        int keyTextColorPressed = this.keyboardStyle.getInt(R.attr.keyTextColorPressed, 0);
        int altTextGravity = this.keyboardStyle.getInt(R.attr.altTextGravity, 0);
        int altIconSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altIconSize, 0);
        int altIconGravity = this.keyboardStyle.getInt(R.attr.altIconGravity, 0);
        int keyFontTypeface = this.keyboardStyle.getInt(R.attr.keyFontTypeface, 0);
        Drawable keyBackground = this.keyboardStyle.getDrawable(R.attr.keyBackground);
        Paint paint = new Paint();
        paint.setTextSize(keyTextSize);
        int keyWidth = Math.max((width * minHeight) / (words.size() * keyHeight), this.keyboardStyle.getDimensionOrFraction(R.attr.keyWidth, this.mDisplayWidth, this.mDisplayWidth / 10));
        int singleWordLength = words.size() > 0 ? ((int) paint.measureText(words.get(0).toString())) / words.get(0).length() : 0;
        Iterator<CharSequence> it = words.iterator();
        while (it.hasNext()) {
            int wordLength = it.next().length();
            keyWidth = Math.max(keyWidth, wordLength * singleWordLength);
        }
        int columnCount = Math.max(1, width / (keyWidth + horizontalGap));
        int keyWidth2 = width / columnCount;
        int overhang = width % columnCount;
        for (CharSequence word : words) {
            if (column >= columnCount || x + keyWidth2 + horizontalPadding > this.mDisplayWidth) {
                x = 0;
                y += verticalGap + keyHeight;
                column = 0;
            }
            Key key = new Key(row);
            key.x = x;
            key.y = y;
            key.width = keyWidth2;
            key.height = keyHeight;
            key.gap = horizontalGap;
            key.mKeyTextSize = keyTextSize;
            key.mKeyTextColor = keyTextColor;
            key.mComponentKeyTextColor = componentkeyTextColor;
            key.mDefaultStrokeCandidateColor = defaultStrokeCandidateColor;
            key.baseline = baseline;
            key.altTextSize = altTextSize;
            key.altTextColor = altTextColor;
            key.tertiaryTextColor = tertiaryTextColor;
            key.keyTextColorPressed = keyTextColorPressed;
            key.altTextGravity = altTextGravity;
            key.altIconSize = altIconSize;
            key.altIconGravity = altIconGravity;
            key.keyFontTypeface = keyFontTypeface;
            key.mKeyBackground = keyBackground;
            key.label = word;
            key.text = word;
            key.codes = new int[]{0};
            key.visible = true;
            column++;
            x += key.width + key.gap;
            if (column < overhang) {
                x++;
                key.width++;
            }
            this.mKeys.add(key);
            this.mTotalWidth = Math.max(x, this.mTotalWidth);
        }
        this.mTotalHeight = y + keyHeight;
    }

    public KeyboardEx(Context context, int layoutTemplateResId, List<ArrayList<GridKeyInfo>> rows, int width, int minHeight) {
        this(context, layoutTemplateResId, false, true);
        int y = 0;
        this.mTotalWidth = width;
        int keyHeight = getKeyHeight(this.keyboardStyle, this.mDisplayHeight, 50);
        int verticalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.verticalGap, this.mDisplayHeight, 0);
        int keyTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.keyTextSize, 0);
        ColorStateList keyTextColor = this.keyboardStyle.getColorStateList(R.attr.keyTextColor, 0);
        int componentkeyTextColor = this.keyboardStyle.getInt(R.attr.candidateComponent, 0);
        int defaultStrokeCandidateColor = this.keyboardStyle.getInt(R.attr.defaultStrokeCandidateColor, 0);
        int baseline = this.keyboardStyle.getDimensionOrFraction(R.attr.keyContentBaseline, keyHeight, 0);
        int altTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altTextSize, 0);
        int altTextColor = this.keyboardStyle.getInt(R.attr.altTextColor, 0);
        int tertiaryTextColor = this.keyboardStyle.getInt(R.attr.tertiaryTextColor, 0);
        int keyTextColorPressed = this.keyboardStyle.getInt(R.attr.keyTextColorPressed, 0);
        int altTextGravity = this.keyboardStyle.getInt(R.attr.altTextGravity, 0);
        int altIconSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altIconSize, 0);
        int altIconGravity = this.keyboardStyle.getInt(R.attr.altIconGravity, 0);
        int keyFontTypeface = this.keyboardStyle.getInt(R.attr.keyFontTypeface, 0);
        Drawable keyBackground = this.keyboardStyle.getDrawable(R.attr.keyBackground);
        Iterator<ArrayList<GridKeyInfo>> it = rows.iterator();
        while (it.hasNext()) {
            List<GridKeyInfo> rowKeys = it.next();
            Row row = new Row(this);
            row.verticalGap = verticalGap;
            row.rowEdgeFlags = 12;
            int x = 0;
            y = rowKeys != rows.get(0) ? y + verticalGap + keyHeight : y;
            for (GridKeyInfo wordKey : rowKeys) {
                Key key = new Key(row);
                key.x = x;
                key.y = y;
                key.width = wordKey.width;
                key.height = keyHeight;
                key.gap = 0;
                key.mKeyTextSize = keyTextSize;
                key.mKeyTextColor = keyTextColor;
                key.mComponentKeyTextColor = componentkeyTextColor;
                key.mDefaultStrokeCandidateColor = defaultStrokeCandidateColor;
                key.baseline = baseline;
                key.altTextSize = altTextSize;
                key.altTextColor = altTextColor;
                key.tertiaryTextColor = tertiaryTextColor;
                key.keyTextColorPressed = keyTextColorPressed;
                key.altTextGravity = altTextGravity;
                key.altIconSize = altIconSize;
                key.altIconGravity = altIconGravity;
                key.keyFontTypeface = keyFontTypeface;
                key.mKeyBackground = keyBackground;
                key.label = wordKey.text;
                key.text = wordKey.text;
                key.codes = new int[]{0};
                key.visible = true;
                x += key.width + key.gap;
                this.mKeys.add(key);
                this.mTotalWidth = Math.max(x, this.mTotalWidth);
            }
            this.mTotalHeight = y + keyHeight;
        }
    }

    public KeyboardEx(Context context, int layoutTemplateResId, List<ArrayList<GridKeyInfo>> rows, int width, int minHeight, KeyboardDockMode dockMode) {
        this(context, layoutTemplateResId, rows, width, minHeight, dockMode, false);
    }

    public KeyboardEx(Context context, int layoutTemplateResId, List<ArrayList<GridKeyInfo>> rows, int width, int minHeight, KeyboardDockMode dockMode, boolean scalePreCreatedRows) {
        this(context, layoutTemplateResId, 0, false, false, dockMode, true);
        int y = 0;
        this.mTotalWidth = scalePreCreatedRows ? this.mDisplayWidth : width;
        int keyHeight = getKeyHeight(this.keyboardStyle, this.mDisplayHeight, 50);
        int verticalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.verticalGap, this.mDisplayHeight, 0);
        int keyTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.keyTextSize, 0);
        ColorStateList keyTextColor = this.keyboardStyle.getColorStateList(R.attr.keyTextColor, 0);
        int componentkeyTextColor = this.keyboardStyle.getInt(R.attr.candidateComponent, 0);
        int defaultStrokeCandidateColor = this.keyboardStyle.getInt(R.attr.defaultStrokeCandidateColor, 0);
        int baseline = this.keyboardStyle.getDimensionOrFraction(R.attr.keyContentBaseline, keyHeight, 0);
        int altTextSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altTextSize, 0);
        int altTextColor = this.keyboardStyle.getInt(R.attr.altTextColor, 0);
        int tertiaryTextColor = this.keyboardStyle.getInt(R.attr.tertiaryTextColor, 0);
        int keyTextColorPressed = this.keyboardStyle.getInt(R.attr.keyTextColorPressed, 0);
        int altTextGravity = this.keyboardStyle.getInt(R.attr.altTextGravity, 0);
        int altIconSize = this.keyboardStyle.getDimensionPixelSize(R.attr.altIconSize, 0);
        int altIconGravity = this.keyboardStyle.getInt(R.attr.altIconGravity, 0);
        int keyFontTypeface = this.keyboardStyle.getInt(R.attr.keyFontTypeface, 0);
        Drawable keyBackground = this.keyboardStyle.getDrawable(R.attr.keyBackground);
        Iterator<ArrayList<GridKeyInfo>> it = rows.iterator();
        while (it.hasNext()) {
            List<GridKeyInfo> rowKeys = it.next();
            Row row = new Row(this);
            row.verticalGap = verticalGap;
            row.rowEdgeFlags = 12;
            int x = 0;
            y = rowKeys != rows.get(0) ? y + verticalGap + keyHeight : y;
            for (GridKeyInfo wordKey : rowKeys) {
                Key key = new Key(row);
                key.x = x;
                key.y = y;
                key.width = (wordKey.width * this.mTotalWidth) / width;
                key.height = keyHeight;
                key.gap = 0;
                key.mKeyTextSize = keyTextSize;
                key.mKeyTextColor = keyTextColor;
                key.mComponentKeyTextColor = componentkeyTextColor;
                key.mDefaultStrokeCandidateColor = defaultStrokeCandidateColor;
                key.baseline = baseline;
                key.altTextSize = altTextSize;
                key.altTextColor = altTextColor;
                key.tertiaryTextColor = tertiaryTextColor;
                key.keyTextColorPressed = keyTextColorPressed;
                key.altTextGravity = altTextGravity;
                key.altIconSize = altIconSize;
                key.altIconGravity = altIconGravity;
                key.keyFontTypeface = keyFontTypeface;
                key.mKeyBackground = keyBackground;
                key.label = wordKey.text;
                key.text = wordKey.text;
                key.codes = new int[]{0};
                key.visible = true;
                x += key.width + key.gap;
                this.mKeys.add(key);
                this.mTotalWidth = Math.max(x, this.mTotalWidth);
            }
            this.mTotalHeight = y + keyHeight;
        }
    }

    public List<Key> getKeys() {
        return this.mKeys;
    }

    public List<Key> getShiftKeys() {
        return this.shiftKeys;
    }

    public int getHeight() {
        return this.mTotalHeight;
    }

    public int getMinWidth() {
        return this.mTotalWidth;
    }

    public int getDisplayWidth() {
        return this.mDisplayWidth;
    }

    public int getModeId() {
        return this.mKeyboardModeId;
    }

    private void computeNearestNeighbors() {
        this.mCellWidth = ((getMinWidth() + 10) - 1) / 10;
        this.mCellHeight = ((getHeight() + 5) - 1) / 5;
        int max = (int) (Math.max(this.mCellWidth, this.mCellHeight) * SEARCH_DISTANCE);
        int proximityThreshold = max * max;
        List<Key> visibleKeys = new ArrayList<>();
        for (Key key : this.mKeys) {
            if (key.visible) {
                visibleKeys.add(key);
            }
        }
        log.d("computeNearestNeighbors...mKeys.size(): ", Integer.valueOf(this.mKeys.size()), "visibleKeys.size(): ", Integer.valueOf(visibleKeys.size()));
        this.mGridNeighbors = new int[50];
        int[] indices = new int[visibleKeys.size()];
        int gridWidth = this.mCellWidth * 10;
        int gridHeight = this.mCellHeight * 5;
        int x = 0;
        while (x < gridWidth) {
            int y = 0;
            while (y < gridHeight) {
                int count = 0;
                for (int i = 0; i < visibleKeys.size(); i++) {
                    Key key2 = visibleKeys.get(i);
                    if (key2.squaredDistanceFrom(x, y) < proximityThreshold || key2.squaredDistanceFrom((this.mCellWidth + x) - 1, y) < proximityThreshold || key2.squaredDistanceFrom((this.mCellWidth + x) - 1, (this.mCellHeight + y) - 1) < proximityThreshold || key2.squaredDistanceFrom(x, (this.mCellHeight + y) - 1) < proximityThreshold) {
                        indices[count] = i;
                        count++;
                    }
                }
                int[] cell = new int[count];
                System.arraycopy(indices, 0, cell, 0, count);
                this.mGridNeighbors[((y / this.mCellHeight) * 10) + (x / this.mCellWidth)] = cell;
                y += this.mCellHeight;
            }
            x += this.mCellWidth;
        }
    }

    public int[] getNearestKeys(int x, int y) {
        int index;
        if (this.mGridNeighbors == null) {
            computeNearestNeighbors();
        }
        return (x < 0 || x >= getMinWidth() || y < 0 || y >= getHeight() || (index = ((y / this.mCellHeight) * 10) + (x / this.mCellWidth)) >= 50) ? new int[0] : this.mGridNeighbors[index];
    }

    public boolean isForcedSwypeable() {
        return this.forcedSwypeable;
    }

    public boolean canSwypePopupCharacters() {
        return this.canSwypePopupCharacters;
    }

    public boolean isIndianLetterCategory() {
        return this.letterLanguageCategory == 3;
    }

    public boolean isMLLetterCategory() {
        return this.letterLanguageCategory == 2;
    }

    public boolean isTALetterCategory() {
        return this.letterLanguageCategory == 4;
    }

    public boolean isKNLetterCategory() {
        return this.letterLanguageCategory == 1;
    }

    public boolean isKMLetterCategory() {
        return this.letterLanguageCategory == 5;
    }

    public boolean isGULetterCategory() {
        return this.letterLanguageCategory == 6;
    }

    public boolean isRussianOrUkrainLetterCategory() {
        return this.letterLanguageCategory == 7;
    }

    public boolean isBamarLetterCategory() {
        return this.letterLanguageCategory == 8;
    }

    public boolean isTHLetterCategory() {
        return this.letterLanguageCategory == 9;
    }

    protected Row createRowFromXml(Context context, XmlResourceParser parser) {
        return new Row(context, this, parser);
    }

    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, KeyboardStyle style) {
        return new Key(res, parent, x, y, style);
    }

    private int loadKeyboardIter(Context context, KeyboardStyle style, XmlResourceParser parser, boolean inKey, boolean inRow, boolean inInclude, int x, int y, Key key, Row currentRow) {
        int includeId;
        log.d("memory loadKeyboardIter start parse xml");
        Resources res = context.getResources();
        while (true) {
            try {
                try {
                    int event = parser.next();
                    if (event == 1) {
                        break;
                    }
                    String tag = parser.getName();
                    if (event == 2) {
                        if (TAG_ROW.equals(tag) && !inRow) {
                            inRow = true;
                            x = 0;
                            currentRow = createRowFromXml(context, parser);
                            if (currentRow.rowType == 1) {
                                if (IMEApplication.from(getContext()).getUserPreferences() == null) {
                                    inRow = false;
                                } else {
                                    inRow = IMEApplication.from(getContext()).getUserPreferences().getShowNumberRow();
                                }
                            }
                        } else if (TAG_KEY.equals(tag) && inRow) {
                            style.loadKey(parser);
                            key = createKeyFromXml(res, currentRow, x, y, style);
                            inKey = true;
                            if (key.keyGroup != 0) {
                                List<Key> group = this.keyGroups.get(key.keyGroup);
                                if (group == null) {
                                    group = new ArrayList<>();
                                    this.keyGroups.append(key.keyGroup, group);
                                }
                                group.add(key);
                            }
                            IMEApplication app = IMEApplication.from(context);
                            if (app.getIME() != null) {
                                InputMethods.Language currentLanguage = app.getIME().getInputMethods().getCurrentInputLanguage();
                                if (getKeyboardDockMode() == KeyboardDockMode.DOCK_SPLIT && currentLanguage.isChineseLanguage() && key.altCode == 6446) {
                                    key.altCode = KEYCODE_INVALID;
                                    key.altIcon = null;
                                }
                            }
                            this.mKeys.add(key);
                            if (isShiftKey(key.codes[0])) {
                                this.shiftKeys.add(key);
                            }
                            currentRow.addKey(key);
                        } else if (TAG_KEYBOARD.equals(tag)) {
                            style.loadKeyboard(parser, !inInclude);
                            parseKeyboardAttributes(context, style);
                        } else if (TAG_INCLUDE.equals(tag) && (includeId = parser.getAttributeResourceValue(R.styleable.KeyboardEx_include_src, 0)) != 0) {
                            XmlResourceParser includeParser = res.getXml(includeId);
                            y = loadKeyboardIter(context, style, includeParser, inKey, inRow, true, x, y, key, currentRow);
                        }
                    } else if (event == 3) {
                        if (TAG_KEY.equals(tag) && inKey && key != null) {
                            inKey = false;
                            x += key.gap + key.width;
                            this.mTotalWidth = Math.max(x, this.mTotalWidth);
                        } else if (TAG_ROW.equals(tag) && inRow && currentRow != null) {
                            this.mKeyboardLayout.add(currentRow);
                            inRow = false;
                            y = y + currentRow.verticalGap + key.height;
                        }
                    }
                } catch (Throwable e) {
                    log.e("Parse error", e);
                    if (parser != null) {
                        parser.close();
                    }
                }
            } catch (Throwable th) {
                if (parser != null) {
                    parser.close();
                }
                throw th;
            }
        }
        if (parser != null) {
            parser.close();
        }
        log.d("memory loadKeyboardIter end");
        return y;
    }

    private void loadKeyboard(Context context, XmlResourceParser parser, boolean fillScreen) {
        this.mKeyboardLayout = new ArrayList();
        this.keyboardStyle = new KeyboardStyle(context, R.style.SwypeReference);
        this.keyGroups.clear();
        int y = loadKeyboardIter(context, this.keyboardStyle, parser, false, false, false, 0, 0, null, null);
        this.keyboardStyle.clearKey();
        int defaultVerticalGap = this.keyboardStyle.getDimensionOrFraction(R.attr.verticalGap, this.mDisplayHeight, 0);
        this.mTotalHeight = y - defaultVerticalGap;
        updateKeyVisibilities();
        fillKeyGaps(this.mKeyboardLayout, 1, fillScreen);
        this.iRight = 0;
        this.iLeft = getMinWidth() - 1;
        this.iBot = 0;
        this.iTop = getHeight() - 1;
        for (Row row : this.mKeyboardLayout) {
            row.calcBounds();
            this.iLeft = Math.min(this.iLeft, row.getLeft());
            this.iRight = Math.max(this.iRight, row.getRight());
            this.iTop = Math.min(this.iTop, row.getTop());
            this.iBot = Math.max(this.iBot, row.getBottom());
        }
        updateIconLevels();
        if (getKeyboardDockMode() == KeyboardDockMode.DOCK_SPLIT) {
            makeSplitKeyboard(context);
        }
        this.sanitizeFont = this.keyboardStyle.getBoolean(R.attr.sanitizeFont, true);
        this.mHasNumRow = this.keyboardStyle.getBoolean(R.attr.hasNumRow, false);
        log.d("Keyboard has number row: ", Boolean.valueOf(this.mHasNumRow));
    }

    public boolean isNearOrBeyondBounds(int x, int y, int wiggle) {
        return x < this.iLeft + wiggle || x > (this.iRight + (-1)) - wiggle || y < this.iTop + wiggle || y > (this.iBot + (-1)) - wiggle;
    }

    public int constrainInternalBoundsX(int x) {
        return Math.max(this.iLeft, Math.min(x, this.iRight - 1));
    }

    public int constrainInternalBoundsY(int y) {
        return Math.max(this.iTop, Math.min(y, this.iBot - 1));
    }

    private boolean isGroupedKeyVisible(Key key) {
        for (int i = this.keyGroups.size() - 1; i >= 0; i--) {
            List<Key> keyGroup = this.keyGroups.valueAt(i);
            int index = keyGroup.indexOf(key);
            if (index == 0) {
                Iterator<Key> it = keyGroup.iterator();
                while (it.hasNext()) {
                    if (it.next().visible) {
                        return true;
                    }
                }
                return false;
            }
            if (index > 0) {
                return true;
            }
        }
        return false;
    }

    private List<Key> getVisibleDependents(Row row, Key key) {
        List<Key> deps = new ArrayList<>();
        if (key.dependentKeys != null) {
            for (Key other : row.mKeys) {
                if (key.dependentKeys.contains(Integer.valueOf(other.codes[0]))) {
                    if (other.width == 0) {
                        other.visible = true;
                    }
                    if (other.visible) {
                        deps.add(other);
                    }
                }
            }
        }
        return deps;
    }

    private static void allocateKeySpaceToOtherKeys(Key key, List<Key> keys) {
        allocateSpace(keys, key.width + key.gap);
    }

    private static void allocateSpace(List<Key> keys, int extra) {
        int count = keys.size();
        if (count != 0 && extra != 0) {
            int unit = extra > 0 ? 1 : -1;
            int add = extra / count;
            int weight = (Math.abs(Math.abs(extra % count)) * 100) + ((count - 1) / count);
            int accum = 0;
            for (int idx = 0; idx < count; idx++) {
                Key key = keys.get(idx);
                key.width += add;
                accum += weight;
                if (accum >= 100) {
                    key.width += unit;
                    accum -= 100;
                }
            }
        }
    }

    private static List<Key> getVisibleKeys(Row row) {
        List<Key> visibleKeys = new ArrayList<>(row.mKeys.size());
        int totalKeyCount = row.mKeys.size();
        for (int idx = 0; idx < totalKeyCount; idx++) {
            Key key = row.mKeys.get(idx);
            if (key.visible) {
                visibleKeys.add(key);
            }
        }
        return visibleKeys;
    }

    private static void adjustKeyWidths(Row row, int extra) {
        allocateSpace(getVisibleKeys(row), extra);
    }

    private void fillKeyGaps(List<Row> keyboardLayout, int gravity, boolean fillScreen) {
        int vTotalAdjustment;
        int targetWidth = fillScreen ? this.mDisplayWidth : this.mTotalWidth;
        if (keyboardLayout.size() > 0) {
            if (this.mForcedHeight > 0) {
                vTotalAdjustment = this.mForcedHeight - this.mTotalHeight;
            } else {
                vTotalAdjustment = 0;
            }
            int vHeightAdjustment = vTotalAdjustment / keyboardLayout.size();
            int vAdjustment = 0;
            for (Row row : keyboardLayout) {
                for (Key key : row.mKeys) {
                    if (!key.visible && !isGroupedKeyVisible(key)) {
                        List<Key> resizeKeys = getVisibleDependents(row, key);
                        if (resizeKeys.size() > 0) {
                            allocateKeySpaceToOtherKeys(key, resizeKeys);
                        }
                    }
                    key.y += vAdjustment;
                    key.height += vHeightAdjustment;
                }
                vAdjustment += vHeightAdjustment;
                int rowWidth = updateKeyPositions(row);
                int space = targetWidth - rowWidth;
                if (space < 0) {
                    adjustKeyWidths(row, space);
                }
                if (fillScreen) {
                    justifyRow(row, gravity, targetWidth);
                } else {
                    updateKeyPositions(row);
                }
            }
            int vAdjustment2 = vTotalAdjustment - vAdjustment;
            Iterator<Key> it = keyboardLayout.get(keyboardLayout.size() - 1).mKeys.iterator();
            while (it.hasNext()) {
                it.next().height += vAdjustment2;
            }
            this.mTotalHeight += vTotalAdjustment;
        }
        this.mTotalWidth = targetWidth;
    }

    private void updateIconLevels() {
        int level = getKeyboardDockMode().getIconLevel(this.invalidDockModes);
        Iterator<Row> it = this.mKeyboardLayout.iterator();
        while (it.hasNext()) {
            for (Key key : it.next().mKeys) {
                if (key.visible && key.icon != null) {
                    log.d("updateIconLevels...level: ", Integer.valueOf(level));
                    key.icon.setLevel(level);
                    if (key.iconPreview != null) {
                        key.iconPreview.setLevel(level);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getCurrentIconLevel() {
        return this.mKeyboardDockMode.getIconLevel(this.invalidDockModes);
    }

    protected void parseKeyboardAttributes(Context context, KeyboardStyle style) {
        this.invalidDockModes = KeyboardDockMode.from(style.getInt(R.attr.invalidDockModes, 0));
        for (KeyboardDockMode mode : KeyboardDockMode.values()) {
            if (!mode.isEnabled(context.getResources(), context.getResources().getConfiguration().orientation)) {
                this.invalidDockModes.add(mode);
            }
        }
        if (this.invalidDockModes.contains(this.mKeyboardDockMode) && (!this.mIsPopup || this.mKeyboardDockMode == KeyboardDockMode.MOVABLE_MINI || this.mKeyboardDockMode == KeyboardDockMode.DOCK_LEFT)) {
            this.mKeyboardDockMode = KeyboardDockMode.DOCK_FULL;
        }
        this.mForcedHeight = getKeyboardHeight(style, this.mDisplayHeight, -1);
        this.forcedSwypeable = style.getBoolean(R.attr.keyboardForcedSwypeable, false);
        this.canSwypePopupCharacters = style.getBoolean(R.attr.canSwypePopupCharacters, true);
        this.background = getBackground(style, this.mDisplayWidth, this.mForcedHeight);
        this.letterLanguageCategory = style.getInt(R.attr.letterLanguageCategory, 0);
    }

    private Drawable getBackground(KeyboardStyle style, final int width, final int height) {
        log.d("Getting background.. force from resources? " + this.mForceBackgroundFromResource);
        if (this.mForceBackgroundFromResource) {
            log.d("Force loading from the resources");
            return style.getDrawable(R.attr.background);
        }
        Context context = getContext();
        KeyboardDockMode dockMode = UserPreferences.from(context).getKeyboardDockingMode();
        int orientation = context.getResources().getConfiguration().orientation;
        ThemeManager.SwypeTheme theme = IMEApplication.from(context).getCurrentTheme();
        IME ime = IMEApplication.from(context).getIME();
        if (ime == null) {
            return null;
        }
        final KeyboardBackgroundManager kbm = ime.getKeyboardBackgroundManager();
        if (kbm.shouldLoadFromDisk(theme, getKeyboardDockMode())) {
            Drawable drawable = kbm.getCachedDrawable();
            if (drawable == null || kbm.mReloadRequiredFromResources) {
                Drawable drawable2 = kbm.loadDrawableFromResource(theme.getSku(), dockMode, orientation, this.keyboardStyle);
                if (width > 0 && height > 0) {
                    new Thread(new Runnable() { // from class: com.nuance.swype.util.drawable.KeyboardBackgroundManager.2
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyboardBackgroundManager.log.d("Saving cached bitmap");
                            KeyboardBackgroundManager.this.saveCachedDrawable(width, height);
                        }
                    }).start();
                    return drawable2;
                }
                KeyboardBackgroundManager.log.d("Cannot save the bitmap as width is: " + width + " and height is: " + height);
                return drawable2;
            }
            return drawable;
        }
        return kbm.loadDrawableFromResource(theme.getSku(), dockMode, orientation, this.keyboardStyle);
    }

    public void clearStickyKeys() {
        for (Key key : this.mKeys) {
            key.on = false;
            key.pressed = false;
            key.locked = false;
        }
    }

    public static boolean isShiftKey(int code) {
        return code == 4068 || code == 6445;
    }

    public static boolean isArrowKeys(int code) {
        return code == 4060 || code == 4045 || code == 4029 || code == 4059 || code == 4061 || code == 4062;
    }

    public static boolean isChangeKeyboardLayerKey(int code) {
        return code == 6444 || code == 4085 || code == 4077 || code == 4078;
    }

    public static boolean shouldEnableSpeechKey(Context context) {
        if (IMEApplication.from(context).isTrialExpired() || !UserPreferences.from(context).isShowVoiceKeyEnabled()) {
            return false;
        }
        IMEApplication app = IMEApplication.from(context);
        if (app.getSpeechProvider() == 99) {
            return false;
        }
        IME ime = app.getIME();
        if (ime == null) {
            return true;
        }
        InputFieldInfo inputFieldInfo = ime.getInputFieldInfo();
        if (inputFieldInfo != null && inputFieldInfo.isPasswordField() && !inputFieldInfo.isVoiceDisabled()) {
            return false;
        }
        if (inputFieldInfo != null && inputFieldInfo.isNoMicrophone()) {
            return false;
        }
        int currentSpeechProvider = app.getSpeechProvider();
        if (currentSpeechProvider == 0) {
            InputMethods inputMethod = app.getInputMethods();
            InputMethods.Language currentLanguage = inputMethod.getCurrentInputLanguage();
            if (currentLanguage != null && !currentLanguage.isCoreLanguage()) {
                currentLanguage = inputMethod.findCoreInputLanguage(currentLanguage.getCoreLanguageId());
            }
            if (currentLanguage == null || !app.getSpeechConfig().isLanguageSupported(currentLanguage.mEnglishName)) {
                return false;
            }
        } else if (currentSpeechProvider == 1 && app.getIME() != null && app.getIME().isTalkBackOn()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getKeyHeight(KeyboardStyle style, int displayHeight, int defValue) {
        int height;
        if (this.mKeyboardDockMode == KeyboardDockMode.MOVABLE_MINI || this.mKeyboardDockMode == KeyboardDockMode.DOCK_SPLIT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_LEFT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_RIGHT) {
            this.context.getResources();
            height = style.getDimensionOrFraction(R.attr.miniModeKeyHeight, displayHeight, defValue);
        } else {
            height = style.getDimensionOrFraction(R.attr.keyHeight, displayHeight, defValue);
        }
        return adjustHeight(height);
    }

    private int getKeyboardHeight(KeyboardStyle style, int displayHeight, int defValue) {
        int height = style.getDimensionOrFraction(R.attr.keyboardForcedHeight, displayHeight, -1);
        if (height != -1 && (this.mKeyboardDockMode == KeyboardDockMode.MOVABLE_MINI || this.mKeyboardDockMode == KeyboardDockMode.DOCK_SPLIT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_LEFT || this.mKeyboardDockMode == KeyboardDockMode.DOCK_RIGHT)) {
            height = this.context.getResources().getDimensionPixelSize(R.dimen.mini_keyboard_forced_height);
        }
        return adjustHeight(height);
    }

    private int adjustHeight(int height) {
        IMEApplication app = IMEApplication.from(this.context);
        float keyboardScale = DrawingUtils.getKeyboardScale(this.context);
        if (keyboardScale > 0.7f && keyboardScale < 1.3000001f) {
            height = (int) (height * keyboardScale);
        }
        DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
        if (height > dm.heightPixels) {
            height = dm.heightPixels;
        }
        if (Build.VERSION.SDK_INT == 21 && app.isScreenLayoutTablet()) {
            return (int) (height * 0.9d);
        }
        return height;
    }

    public int getKeyboardScaledWidth(Resources res) {
        return this.mKeyboardDockMode.getKeyboardWidth(res);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getKeyTextSize(KeyboardStyle style, Resources res) {
        return getKeyboardDockMode().getTextSize(res, R.attr.keyTextSize, style);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAlternateIconSize(KeyboardStyle style, Resources res) {
        return getKeyboardDockMode().getTextSize(res, R.attr.altIconSize, style);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAlternateTextSize(KeyboardStyle style, Resources res) {
        return getKeyboardDockMode().getTextSize(res, R.attr.altTextSize, style);
    }

    @SuppressLint({"RtlHardcoded"})
    private static int getJustifiedOffset(int rowWidth, int totalWidth, int gravity) {
        switch (gravity & 7) {
            case 1:
                int xRow = (totalWidth - rowWidth) / 2;
                return xRow;
            case 3:
            case 8388611:
                return 0;
            case 5:
            case 8388613:
                int xRow2 = totalWidth - rowWidth;
                return xRow2;
            default:
                log.w("WARNING: default to LEFT justification (unrecognized gravity)");
                return 0;
        }
    }

    private void justifyRow(Row row, int gravity, int totalWidth) {
        if (row.mKeys != null && row.mKeys.size() != 0) {
            int xRow = getJustifiedOffset(updateKeyPositions(row), totalWidth, gravity);
            int xFirst = row.mKeys.get(0).x;
            int count = row.mKeys.size();
            for (int idx = 0; idx < count; idx++) {
                Key rowKey = row.mKeys.get(idx);
                rowKey.x = (rowKey.x - xFirst) + xRow;
            }
        }
    }

    private void justifyRows(List<Row> keyboardLayout, int gravity, int totalWidth) {
        for (Row row : keyboardLayout) {
            justifyRow(row, gravity, totalWidth);
        }
    }

    public EnumSet<KeyboardSettings> getKeyboardSettings() {
        return this.keyboardSettings;
    }

    private void updateKeyVisibilities() {
        InputMethods.Language lang = IMEApplication.from(this.context).getCurrentLanguage();
        this.keyboardSettings = KeyboardSettings.current(this.context, lang);
        this.mGridNeighbors = null;
        for (Key key : this.mKeys) {
            if (!this.keyboardSettings.containsAll(key.keyboardSettings)) {
                key.visible = false;
            }
            if (key.modeFlags != 0 && (key.modeFlags & this.mKeyboardModeId) == 0) {
                key.visible = false;
            }
        }
        for (Key key2 : this.mKeys) {
            if (key2.codes != null) {
                int modeCount = KeyboardDockMode.values().length;
                for (int count = 0; count < modeCount; count++) {
                    KeyboardDockMode dockMode = KeyboardDockMode.values()[count];
                    int modeKeyCode = getKeyboardDockModeKeyCode(dockMode);
                    if (key2.codes[0] == modeKeyCode) {
                        if (IMEApplication.from(this.context).getIME() != null && IMEApplication.from(this.context).getIME().isAccessibilitySupportEnabled()) {
                            key2.dimmed = true;
                        } else if (dockMode == this.mPrimaryKeyboardDockMode) {
                            key2.dimmed = true;
                        } else if (!dockMode.isEnabled(this.context.getResources(), this.context.getResources().getConfiguration().orientation)) {
                            key2.dimmed = true;
                        }
                    }
                }
            }
        }
        int groupCount = this.keyGroups.size();
        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
            Key bestKey = null;
            int bestMatchCount = -1;
            for (Key key3 : this.keyGroups.valueAt(groupIndex)) {
                if (key3.visible) {
                    key3.visible = false;
                    int matchCount = KeyboardSettings.matchCount(key3.keyboardSettings, this.keyboardSettings);
                    if (bestKey == null || matchCount > bestMatchCount) {
                        bestKey = key3;
                        bestMatchCount = matchCount;
                    }
                }
            }
            if (bestKey != null) {
                bestKey.visible = true;
            }
        }
        this.mTotalWidth = 0;
        for (Row row : this.mKeyboardLayout) {
            row.visibleKeyCount = 0;
            for (Key key4 : row.mKeys) {
                if (key4.visible) {
                    int i = row.visibleKeyCount;
                    row.visibleKeyCount = i + 1;
                    key4.visibleIndex = i;
                }
            }
            int rowWidth = updateKeyPositions(row);
            if (this.mTotalWidth < rowWidth) {
                this.mTotalWidth = rowWidth;
            }
        }
    }

    protected int updateKeyPositions(Row row) {
        if (row.mKeys.size() == 0) {
            return 0;
        }
        int xRow = row.mKeys.get(0).x;
        int xNextKey = xRow;
        for (Key key : row.mKeys) {
            if (key.visible) {
                key.x = xNextKey;
                xNextKey += key.gap + key.width;
            }
        }
        return xNextKey - xRow;
    }

    private int getKeyboardDockModeKeyCode(KeyboardDockMode kbdDockMode) {
        if (kbdDockMode != KeyboardDockMode.DOCK_FULL) {
            if (kbdDockMode == KeyboardDockMode.DOCK_LEFT) {
                int keyCode = this.context.getResources().getInteger(R.integer.keycode_resize_minileft_screen);
                return keyCode;
            }
            if (kbdDockMode == KeyboardDockMode.DOCK_RIGHT) {
                int keyCode2 = this.context.getResources().getInteger(R.integer.keycode_resize_miniright_screen);
                return keyCode2;
            }
            if (kbdDockMode == KeyboardDockMode.MOVABLE_MINI) {
                int keyCode3 = this.context.getResources().getInteger(R.integer.keycode_resize_minimovable_screen);
                return keyCode3;
            }
            if (kbdDockMode == KeyboardDockMode.DOCK_SPLIT) {
                int keyCode4 = this.context.getResources().getInteger(R.integer.keycode_resize_split_screen);
                return keyCode4;
            }
        }
        int keyCode5 = this.context.getResources().getInteger(R.integer.keycode_resize_full_screen);
        return keyCode5;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return this.context;
    }

    public int getLayoutRowCount() {
        if (this.mKeyboardLayout != null) {
            return this.mKeyboardLayout.size();
        }
        return 0;
    }

    public EnumSet<KeyboardDockMode> getInvalidDockModes() {
        return this.invalidDockModes;
    }

    public Key findKey(int code) {
        for (Key key : getKeys()) {
            if (key.codes != null && key.codes.length > 0 && key.codes[0] == code) {
                return key;
            }
        }
        return null;
    }

    public boolean isSanitizeFont() {
        return this.sanitizeFont;
    }

    public boolean hasNumRow() {
        return this.mHasNumRow;
    }

    /* loaded from: classes.dex */
    public static class KeyboardTraits {
        private KeyboardTraits(Resources res) {
        }
    }
}
