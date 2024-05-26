package com.nuance.swype.input.hardkey;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import com.nuance.android.compat.InputConnectionCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.internal.common.Document;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.swype.input.BilingualLanguage;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.ChineseInputView;
import com.nuance.swype.input.japanese.JapaneseInputView;
import com.nuance.swype.input.korean.KoreanInputView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class HardKeyboardManager {
    private static final String AZERTY_MODE = "AZERTY_LATIN";
    private static final int DELAY_TIME = 200;
    public static final String HardKB_SUFFIX = "_HKB";
    private static final int JA_SPECIAL_CODE = 12540;
    public static final int KEYCODE_CAPS_LOCK = 115;
    public static final int KEYCODE_ESCAPE = 111;
    public static final int KEYCODE_FORWARD_DEL = 112;
    public static final int KEYCODE_MOVE_END = 123;
    public static final int KEYCODE_MOVE_HOME = 122;
    public static final int KEYCODE_NUMPAD_0 = 144;
    public static final int KEYCODE_NUMPAD_9 = 153;
    public static final int KEYCODE_NUMPAD_DOT = 158;
    public static final int KEYCODE_NUMPAD_ENTER = 160;
    private static final int LONGPRESS_TIMEOUT = 0;
    private static final int META_ALL_MASK = 7827711;
    public static final int META_ALT_LEFT_ON = 16;
    public static final int META_ALT_LOCKED = 512;
    public static final int META_ALT_ON = 2;
    public static final int META_ALT_RIGHT_ON = 32;
    public static final int META_CAPS_LOCK_ON = 1048576;
    public static final int META_CAP_LOCKED = 256;
    public static final int META_CTRL_LEFT_ON = 8192;
    public static final int META_CTRL_ON = 4096;
    public static final int META_CTRL_RIGHT_ON = 16384;
    public static final int META_FUNCTION_ON = 8;
    private static final int META_LOCK_MASK = 7340032;
    public static final int META_META_LEFT_ON = 131072;
    public static final int META_META_ON = 65536;
    public static final int META_META_RIGHT_ON = 262144;
    private static final int META_MODIFIER_MASK = 487679;
    public static final int META_NUM_LOCK_ON = 2097152;
    public static final int META_SCROLL_LOCK_ON = 4194304;
    public static final int META_SELECTING = 2048;
    public static final int META_SHIFT_LEFT_ON = 64;
    public static final int META_SHIFT_ON = 1;
    public static final int META_SHIFT_RIGHT_ON = 128;
    public static final int META_SYM_LOCKED = 1024;
    public static final int META_SYM_ON = 4;
    private static final int MSG_LONGPRESS = 1005;
    private static final int QUICK_PRESS = 750;
    private static final String QWERTY_MODE = "QWERTY_LATIN";
    private static final String QWERTZ_MODE = "QWERTZ_LATIN";
    private static final int SEMICOLON = 59;
    public static final String SHORTCUT_ADD_TO_DICTIONARY = "DICTIONARY_HKB";
    public static final String SHORTCUT_SETTINGS = "SETTINGS_HKB";
    public static final String SHORTCUT_TO_SELECT_NWP = "NWP_HKB";
    private static final int[] resourceIDs = {R.array.zhuyin_qwerty_e0, R.array.zhuyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.zhuyin_qwerty_e0, R.array.zhuyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.pinyin_qwerty_e0, R.array.doublepinyin_e1, R.array.national_112, R.array.AZERTY_LATIN, R.array.QWERTZ_LATIN};
    private String currentLatinMode;
    private SparseArray<int[]> currentMap;
    private boolean isCapsLocked;
    protected boolean isNonQwertyPhisicalKeyboardLayoutSet;
    protected boolean mHardLangPopupMenuShownOnce;
    private IME mIme;
    private long mLastKeyTime;
    private int primaryUniCode;
    private List<CharSequence> supportedInputModes = new ArrayList();
    private SparseIntArray standardQwertyKeyCodeScanCodeMapping = new SparseIntArray();
    private HashMap<String, SparseArray<int[]>> specialKeyMappings = new HashMap<>();
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.hardkey.HardKeyboardManager.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HardKeyboardManager.MSG_LONGPRESS /* 1005 */:
                    if (HardKeyboardManager.this.mHandler.hasMessages(HardKeyboardManager.MSG_LONGPRESS)) {
                        return true;
                    }
                    InputView inputView = HardKeyboardManager.this.mIme.getCurrentInputView();
                    if (inputView == null || inputView.getInputFieldInfo() == null || (!inputView.getInputFieldInfo().isPhoneNumberField() && !inputView.getInputFieldInfo().isDateTimeField() && !inputView.getInputFieldInfo().isNumberField())) {
                        if (inputView != null) {
                            inputView.showHardKeyPopupKeyboard(((Integer) msg.obj).intValue());
                        }
                        HardKeyboardManager.this.mHandler.removeMessages(HardKeyboardManager.MSG_LONGPRESS);
                    }
                    return true;
                default:
                    return false;
            }
        }
    };
    private final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);

    public HardKeyboardManager(IME ime) {
        this.mIme = ime;
    }

    public boolean onKeyEvent(int keyCode, KeyEvent event) {
        String possibleMode;
        InputView inputView = this.mIme == null ? null : this.mIme.getCurrentInputView();
        if (inputView == null) {
            return false;
        }
        KeyboardSwitcher switcher = inputView.getKeyboardSwitcher();
        if (switcher != null) {
            if (!switcher.supportsAlphaMode()) {
                return false;
            }
            switcher.setKeyboardEntryLayerType(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
        }
        if (inputView.mSpeechWrapper != null && inputView.mSpeechWrapper.isSpeechViewShowing()) {
            inputView.mSpeechWrapper.stopSpeech();
        }
        if (event.getAction() == 0) {
            if (isCtrlPressed(event) && isShiftPressed(event)) {
                Handler handler = this.mIme.getHandler();
                handler.removeMessages(115);
                handler.sendEmptyMessageDelayed(115, 0L);
                handler.removeMessages(105);
                handler.sendEmptyMessageDelayed(105, 10L);
                return true;
            }
            if (isAltPressed(event) && event.getKeyCode() == 62) {
                if (inputView.getWindowToken() != null) {
                    if (!this.mHardLangPopupMenuShownOnce) {
                        this.mHardLangPopupMenuShownOnce = true;
                        this.mIme.showLanguagePopupMenu(null);
                    }
                    return true;
                }
                return false;
            }
            if (this.mIme.mCurrentInputLanguage.isChineseLanguage() || this.mIme.mCurrentInputLanguage.isKoreanLanguage() || this.mIme.mCurrentInputLanguage.isLatinLanguage()) {
                if (this.supportedInputModes.isEmpty()) {
                    for (String modeName : this.mIme.getResources().getStringArray(R.array.supported_input_modes)) {
                        this.supportedInputModes.add(modeName);
                    }
                }
                if (this.mIme.mCurrentInputLanguage.isLatinLanguage() && this.standardQwertyKeyCodeScanCodeMapping.size() == 0) {
                    String[] keyMap = this.mIme.getResources().getStringArray(R.array.KEYCODE_SCANCODE_QWERTY);
                    for (int item = 1; item < keyMap.length; item++) {
                        int[] chars = parseKeyMapping(keyMap[item]);
                        this.standardQwertyKeyCodeScanCodeMapping.put(Integer.valueOf(chars[0]).intValue(), chars[1]);
                    }
                }
                String languageID = Integer.toHexString(this.mIme.mCurrentInputLanguage.mCoreLanguageId);
                String mode = this.mIme.mCurrentInputLanguage.getCurrentInputMode().mInputMode;
                if (this.mIme.mCurrentInputLanguage.isLatinLanguage()) {
                    if (InputMethods.VIETNAMESE_INPUT_MODE_NATIONAL.equalsIgnoreCase(mode) && (mode = this.mIme.mCurrentInputLanguage.defaultGlobalInputMode) == null) {
                        mode = this.mIme.mCurrentInputLanguage.mDefaultInputMode;
                    }
                    if ("AZERTY".equals(mode)) {
                        possibleMode = AZERTY_MODE;
                    } else if ("QWERTZ".equals(mode)) {
                        possibleMode = QWERTZ_MODE;
                    } else {
                        possibleMode = QWERTY_MODE;
                    }
                    this.currentLatinMode = possibleMode;
                } else {
                    possibleMode = mode + Document.ID_SEPARATOR + languageID;
                }
                if (this.supportedInputModes.contains(possibleMode)) {
                    int index = this.supportedInputModes.indexOf(possibleMode);
                    SparseArray<int[]> modeMap = this.specialKeyMappings.get(possibleMode);
                    if (modeMap == null) {
                        modeMap = new SparseArray<>();
                        String[] keyMap2 = this.mIme.getResources().getStringArray(resourceIDs[index]);
                        if ("0".equals(keyMap2[0])) {
                            for (int item2 = 1; item2 < keyMap2.length; item2++) {
                                int[] chars2 = parseKeyMapping(keyMap2[item2]);
                                if (chars2.length > 2) {
                                    int[] multiChars = new int[chars2.length - 1];
                                    for (int i = 0; i < chars2.length - 1; i++) {
                                        multiChars[i] = chars2[i + 1];
                                    }
                                    modeMap.put(Integer.valueOf(chars2[0]).intValue(), multiChars);
                                } else {
                                    modeMap.put(Integer.valueOf(chars2[0]).intValue(), new int[]{chars2[1]});
                                }
                            }
                        } else if ("1".equals(keyMap2[0])) {
                            for (int item3 = 1; item3 < keyMap2.length; item3++) {
                                modeMap.put(Integer.valueOf(item3 + 96).intValue(), parseKeyMapping(keyMap2[item3]));
                            }
                        }
                        this.specialKeyMappings.put(possibleMode, modeMap);
                    }
                    this.currentMap = modeMap;
                } else if (QWERTY_MODE.equals(possibleMode)) {
                    this.currentMap = null;
                } else {
                    return false;
                }
            }
        }
        if (this.mIme.mCurrentInputLanguage.isLatinLanguage() || this.mIme.mCurrentInputLanguage.isJapaneseLanguage()) {
            switch (event.getAction()) {
                case 0:
                    return onHardKeyEventLatinDown(event);
                case 1:
                    return onHardKeyEventLatinUp(event);
                case 2:
                    return onHardKeyEventLatinMultiple(event);
                default:
                    return false;
            }
        }
        switch (event.getAction()) {
            case 0:
                return onHardKeyEventSpecialDown(event);
            default:
                return false;
        }
    }

    private int[] parseKeyMapping(String value) {
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

    public boolean onHardKeyEventSpecialDown(KeyEvent event) {
        InputView inputView = this.mIme.getCurrentInputView();
        if (this.mIme.mCurrentInputLanguage.isKoreanLanguage()) {
            KeyboardSwitcher switcher = inputView.getKeyboardSwitcher();
            if (switcher != null && switcher.isKeypadInput()) {
                return false;
            }
        } else if ((inputView instanceof ChineseInputView) && inputView.gridCandidateTableVisible) {
            ((ChineseInputView) inputView).closeGridCandidatesView();
        }
        if (inputView instanceof ChineseInputView) {
            this.isCapsLocked = isCapsLockOn(event);
            if (this.isCapsLocked) {
                inputView.setShiftState(isShiftPressed(event) ? Shift.ShiftState.OFF : Shift.ShiftState.ON);
            } else {
                inputView.setShiftState(isShiftPressed(event) ? Shift.ShiftState.ON : Shift.ShiftState.OFF);
            }
        }
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
                return inputView.handleHardKeyDirectionKey(keyCode);
            case 59:
            case 60:
                if (inputView instanceof ChineseInputView) {
                    inputView.handleHardkeyShiftKey(Shift.ShiftState.ON);
                    return false;
                }
                Shift.ShiftState state = Shift.ShiftState.OFF;
                if ((!isShiftPressed(event) && isCapsLockOn(event)) || (isShiftPressed(event) && !isCapsLockOn(event))) {
                    state = Shift.ShiftState.LOCKED;
                }
                inputView.handleHardkeyShiftKey(state);
                return false;
            case 62:
                long when = SystemClock.uptimeMillis();
                boolean quickPressed = when < this.mLastKeyTime + 750;
                this.mLastKeyTime = when;
                return inputView.handleHardkeySpace(quickPressed, 1);
            case 66:
            case 160:
                inputView.handleHardkeyEnter();
                return true;
            case 67:
                return inputView.handleHardkeyBackspace(0);
            case 111:
                return inputView.handleHardKeyEscapeKey();
            case 112:
                if (!TextUtils.isEmpty(InputConnectionCompat.getSelectedText(this.mIme.getCurrentInputConnection(), 0))) {
                    return inputView.handleHardkeyBackspace(0);
                }
                return inputView.handleHardkeyDelete(0);
            case 115:
                this.isCapsLocked = !isCapsLockOn(event);
                inputView.handleHardKeyCapsLock(this.isCapsLocked);
                return true;
            case 122:
                return inputView.handleHardKeyHomeKey();
            case 123:
                return inputView.handleHardKeyEndKey();
            default:
                this.mIme.getCurrentInputView().removeHardKeyboardRecaptureCandidates();
                this.primaryUniCode = event.getUnicodeChar();
                if (event.isSystem() || this.primaryUniCode == 0) {
                    return false;
                }
                if (this.mIme.mCurrentInputLanguage.isChineseLanguage() && handleChineseLanguage(event, this.primaryUniCode)) {
                    return true;
                }
                if (this.mIme.mCurrentInputLanguage.isKoreanLanguage()) {
                    if (isCapsLockOn(event) && !isShiftPressed(event)) {
                        this.primaryUniCode = Character.toLowerCase(this.primaryUniCode);
                    }
                    if (inputView instanceof KoreanInputView) {
                        if (inputView.getShiftState() == Shift.ShiftState.ON && !isShiftPressed(event)) {
                            this.primaryUniCode = Character.toUpperCase(this.primaryUniCode);
                            ((KoreanInputView) inputView).handleShiftKey();
                        }
                        if (isAlphabeticUpperCase(this.primaryUniCode) && !isKoreanDoubleJamoKey(this.primaryUniCode) && !(this.mIme.mCurrentInputLanguage instanceof BilingualLanguage)) {
                            this.primaryUniCode += 32;
                        }
                    }
                }
                int lastPrimaryCode = this.primaryUniCode;
                int[] convertedKeyCode = this.currentMap.get(this.primaryUniCode);
                if (convertedKeyCode != null) {
                    this.primaryUniCode = convertedKeyCode[0];
                }
                if (this.mIme.mCurrentInputLanguage.isKoreanLanguage() && ((isAlphabeticLowerCase(lastPrimaryCode) || (isKoreanDoubleJamoKey(lastPrimaryCode) && isAlphabeticUpperCase(lastPrimaryCode))) && event.getRepeatCount() > 0 && hasNoModifiers(event))) {
                    if (!this.mHandler.hasMessages(MSG_LONGPRESS) && !this.mHandler.hasMessages(MSG_LONGPRESS)) {
                        Message msg = this.mHandler.obtainMessage(MSG_LONGPRESS, Integer.valueOf(this.primaryUniCode));
                        this.mHandler.sendMessageDelayed(msg, 0L);
                    }
                    return true;
                }
                if (this.mHandler.hasMessages(MSG_LONGPRESS)) {
                    return true;
                }
                if (this.mIme.mCurrentInputLanguage.isKoreanLanguage() && (this.mIme.mCurrentInputLanguage instanceof BilingualLanguage) && isShiftPressed(event) && isKoreanDoubleJamoKey(lastPrimaryCode)) {
                    inputView.setShiftState(Shift.ShiftState.ON);
                }
                inputView.setHardkeyLayoutID(InputMethods.QWERTY_KEYBOARD_LAYOUT);
                if (event.getRepeatCount() > 0) {
                    long when2 = SystemClock.uptimeMillis();
                    if (!(when2 < this.mLastKeyTime)) {
                        inputView.handleHardkeyCharKey(this.primaryUniCode, convertedKeyCode, event, false);
                        this.mLastKeyTime = 200 + when2;
                    }
                } else {
                    inputView.handleHardkeyCharKey(this.primaryUniCode, convertedKeyCode, event, isShiftPressed(event));
                }
                return true;
        }
    }

    private boolean handleChineseLanguage(KeyEvent event, int primaryCode) {
        Message msg;
        if (this.mIme.mCurrentInputLanguage.isChineseLanguage() && !isCapsLockOn(event) && ((isAlphabeticLowerCase(this.primaryUniCode) || isAlphabeticUpperCase(this.primaryUniCode)) && event.getRepeatCount() > 0 && hasNoModifiers(event))) {
            if (this.mHandler.hasMessages(MSG_LONGPRESS) || this.mHandler.hasMessages(MSG_LONGPRESS)) {
                return true;
            }
            if (isAlphabeticUpperCase(this.primaryUniCode)) {
                msg = this.mHandler.obtainMessage(MSG_LONGPRESS, Integer.valueOf(this.primaryUniCode + 32));
            } else {
                msg = this.mHandler.obtainMessage(MSG_LONGPRESS, Integer.valueOf(this.primaryUniCode));
            }
            this.mHandler.sendMessageDelayed(msg, 0L);
            return true;
        }
        if (this.mHandler.hasMessages(MSG_LONGPRESS)) {
            return true;
        }
        if (this.mIme.mCurrentInputLanguage.isChineseLanguage() && isAlphabeticUpperCase(this.primaryUniCode)) {
            this.primaryUniCode += 32;
        }
        return false;
    }

    public boolean onHardKeyEventLatinDown(KeyEvent event) {
        KeyboardSwitcher switcher;
        int[] convertedKeyCode;
        InputView inputView = this.mIme.getCurrentInputView();
        if (this.mIme.mCurrentInputLanguage.isJapaneseLanguage() && (inputView instanceof JapaneseInputView)) {
            ((JapaneseInputView) inputView).dismissSegmentationPopup();
        }
        this.isCapsLocked = isCapsLockOn(event);
        if (this.isCapsLocked) {
            inputView.setShiftState(isShiftPressed(event) ? Shift.ShiftState.OFF : Shift.ShiftState.ON);
        } else {
            inputView.setShiftState(isShiftPressed(event) ? Shift.ShiftState.ON : Shift.ShiftState.OFF);
        }
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 19:
            case 21:
            case 22:
                return inputView.handleHardKeyDirectionKey(keyCode);
            case 20:
                return inputView.handleHardKeyDirectionKey(keyCode);
            case 59:
            case 60:
                return false;
            case 62:
                if (inputView.isShowingAddToDictionaryTip() && inputView.isAddToDictionaryTipHighlighted()) {
                    inputView.addHardKeyOOVToDictionary();
                    return true;
                }
                long when = SystemClock.uptimeMillis();
                boolean quickPressed = when < this.mLastKeyTime + 750;
                this.mLastKeyTime = when;
                return inputView.handleHardkeySpace(quickPressed, 1);
            case 66:
            case 160:
                if (inputView.isShowingAddToDictionaryTip() && inputView.isAddToDictionaryTipHighlighted()) {
                    inputView.addHardKeyOOVToDictionary();
                    return true;
                }
                inputView.handleHardkeyEnter();
                return true;
            case 67:
                return inputView.handleHardkeyBackspace(0);
            case 111:
                return inputView.handleHardKeyEscapeKey();
            case 112:
                if (!TextUtils.isEmpty(InputConnectionCompat.getSelectedText(this.mIme.getCurrentInputConnection(), 0))) {
                    return inputView.handleHardkeyBackspace(0);
                }
                return inputView.handleHardkeyDelete(0);
            case 115:
                this.isCapsLocked = !isCapsLockOn(event);
                if (this.mIme.mCurrentInputLanguage.isLatinLanguage()) {
                    this.mIme.showCapLockState(this.isCapsLocked);
                }
                inputView.handleHardKeyCapsLock(this.isCapsLocked);
                return true;
            case 122:
                return inputView.handleHardKeyHomeKey();
            case 123:
                return inputView.handleHardKeyEndKey();
            default:
                int primaryCode = event.getUnicodeChar();
                inputView.removeHardKeyboardRecaptureCandidates();
                if (event.isSystem() || primaryCode == 0) {
                    return false;
                }
                if (this.mIme.mCurrentInputLanguage.isLatinLanguage() && this.standardQwertyKeyCodeScanCodeMapping.indexOfKey(primaryCode) > 0 && event.getScanCode() != this.standardQwertyKeyCodeScanCodeMapping.get(primaryCode)) {
                    this.isNonQwertyPhisicalKeyboardLayoutSet = true;
                }
                if (!this.isNonQwertyPhisicalKeyboardLayoutSet && this.mIme.mCurrentInputLanguage.isLatinLanguage() && this.currentMap != null && ((this.currentLatinMode.equals(AZERTY_MODE) || this.currentLatinMode.equals(QWERTZ_MODE)) && (convertedKeyCode = this.currentMap.get(primaryCode)) != null)) {
                    primaryCode = convertedKeyCode[0];
                }
                if ((isAlphabeticLowerCase(primaryCode) || isAlphabeticUpperCase(primaryCode)) && event.getRepeatCount() > 0 && hasNoModifiers(event)) {
                    if (!this.mHandler.hasMessages(MSG_LONGPRESS) && !this.mHandler.hasMessages(MSG_LONGPRESS)) {
                        if (isAlphabeticUpperCase(primaryCode)) {
                            primaryCode += 32;
                        }
                        Message msg = this.mHandler.obtainMessage(MSG_LONGPRESS, Integer.valueOf(primaryCode));
                        this.mHandler.sendMessageDelayed(msg, 0L);
                    }
                    return true;
                }
                if (this.mHandler.hasMessages(MSG_LONGPRESS)) {
                    return true;
                }
                if (isAlphabeticUpperCase(primaryCode) && isShiftPressed(event)) {
                    inputView.handleHardkeyShiftKey(Shift.ShiftState.ON);
                    primaryCode += 32;
                }
                if (isAlphabeticLowerCase(primaryCode)) {
                    inputView.setHardKeyboardSystemSettings();
                }
                if (this.mIme.mCurrentInputLanguage.isJapaneseLanguage() && primaryCode == 59) {
                    primaryCode = JA_SPECIAL_CODE;
                }
                inputView.setHardkeyLayoutID(InputMethods.QWERTY_KEYBOARD_LAYOUT);
                if (this.isCapsLocked && this.mIme.mCurrentInputLanguage.isLatinLanguage()) {
                    if (isShiftPressed(event)) {
                        primaryCode = Character.toLowerCase(primaryCode);
                        inputView.handleHardkeyShiftKey(Shift.ShiftState.OFF);
                    } else {
                        inputView.handleHardKeyCapsLock(this.isCapsLocked);
                    }
                } else if (this.mIme.mCurrentInputLanguage.isJapaneseLanguage() && (switcher = inputView.getKeyboardSwitcher()) != null && switcher.isKeypadInput()) {
                    return false;
                }
                if (event.getRepeatCount() > 0) {
                    long when2 = SystemClock.uptimeMillis();
                    if (!(when2 < this.mLastKeyTime)) {
                        if (!inputView.handleKey(primaryCode, false, 1)) {
                            inputView.handleHardkeyCharKey(primaryCode, event, false);
                        }
                        this.mLastKeyTime = 200 + when2;
                    }
                } else if (!inputView.handleKey(primaryCode, false, 1)) {
                    inputView.handleHardkeyCharKey(primaryCode, event, false);
                }
                return true;
        }
    }

    private boolean onHardKeyEventLatinMultiple(KeyEvent event) {
        return false;
    }

    private boolean isAlphabeticLowerCase(int primaryCode) {
        return primaryCode >= 97 && primaryCode <= 122;
    }

    private boolean isKoreanDoubleJamoKey(int primaryCode) {
        return primaryCode == 81 || primaryCode == 87 || primaryCode == 69 || primaryCode == 82 || primaryCode == 84 || primaryCode == 79 || primaryCode == 80;
    }

    private boolean isAlphabeticUpperCase(int primaryCode) {
        return primaryCode >= 65 && primaryCode <= 90;
    }

    public boolean onHardKeyEventLatinUp(KeyEvent event) {
        return false;
    }

    public static boolean isCtrlPressed(KeyEvent event) {
        return (event.getMetaState() & META_CTRL_ON) != 0;
    }

    public static boolean isShiftPressed(KeyEvent event) {
        return (event.getMetaState() & 1) != 0;
    }

    public static boolean isAltPressed(KeyEvent event) {
        return (event.getMetaState() & 2) != 0;
    }

    public static boolean isCapsLockOn(KeyEvent event) {
        return (event.getMetaState() & 1048576) != 0;
    }

    public static boolean hasNoModifiers(KeyEvent event) {
        return (normalizeMetaState(event.getMetaState()) & META_MODIFIER_MASK) == 0;
    }

    public static int normalizeMetaState(int metaState) {
        if ((metaState & R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop) != 0) {
            metaState |= 1;
        }
        if ((metaState & 48) != 0) {
            metaState |= 2;
        }
        if ((metaState & 24576) != 0) {
            metaState |= META_CTRL_ON;
        }
        if ((393216 & metaState) != 0) {
            metaState |= 65536;
        }
        if ((metaState & 256) != 0) {
            metaState |= 1048576;
        }
        if ((metaState & 512) != 0) {
            metaState |= 2;
        }
        if ((metaState & 1024) != 0) {
            metaState |= 4;
        }
        return META_ALL_MASK & metaState;
    }

    public static boolean isNumLockOn(KeyEvent event) {
        return (event.getMetaState() & 2097152) != 0;
    }
}
