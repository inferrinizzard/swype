package com.nuance.swype.input;

import android.content.Context;
import com.nuance.android.util.LruCache;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9KeyboardDatabase;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class KeyboardManager implements XT9CoreInput.KeyboardLoadCallback {
    private static final int EMPTY_KEYBOARD_ID = 0;
    private static final int ID_CORE_MASK = 65535;
    private static final int ID_DOCK_MODE_SHIFT = 20;
    private static final int ID_EMAIL_MODE = 262144;
    private static final int ID_IM_MODE = 393216;
    private static final int ID_NORMAL_MODE = 131072;
    private static final int ID_NUMBER_MODE = 524288;
    private static final int ID_ORIENTATION_FLAG = 65536;
    private static final int ID_URL_MODE = 655360;
    private static final int MAX_CACHE_COUNT = 4;
    protected static final LogManager.Log log = LogManager.getLog("KeyboardManager");
    private final Context context;
    private int currencyType;
    private KeyboardEx.KeyboardDockMode dockMode;
    private boolean forceSetKeyboardDatabase;
    private boolean handwritingEnabled;
    private boolean isOnlyThemeChanged;
    private float keyboardScaleLandscape;
    private float keyboardScalePortrait;
    private boolean keyboardSwitchable;
    private String locale;
    private boolean showVoiceKeyEnabled;
    private String swypeThemeName;
    private final LruCache<Integer, XT9Keyboard> keyboardCache = new LruCache<Integer, XT9Keyboard>(4) { // from class: com.nuance.swype.input.KeyboardManager.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.android.util.LruCache
        public void entryRemoved(boolean evicted, Integer key, XT9Keyboard oldKb, XT9Keyboard newKb) {
            if (oldKb != null) {
                oldKb.destroyKeyboard();
            }
        }
    };
    private int keyboardId = 0;
    private int modeId = 1;
    private boolean accessibility = false;
    private boolean isLanguageSupportingHwr = true;

    public static KeyboardManager from(Context context) {
        return IMEApplication.from(context).getKeyboardManager();
    }

    public KeyboardManager(Context context) {
        this.context = context;
        checkConfigChanged();
    }

    public XT9Keyboard getKeyboard(XT9CoreInput xt9coreinput) {
        if (this.keyboardId != 0) {
            if (checkConfigChanged()) {
                this.keyboardCache.evictAll();
                clearKeyboardViewsCache();
                return setKeyboard(xt9coreinput, this.keyboardId, this.modeId);
            }
            this.keyboardCache.get(Integer.valueOf(this.keyboardId));
        }
        return null;
    }

    public void forceSetKeyboardDatabase(boolean enable) {
        this.forceSetKeyboardDatabase = enable;
    }

    public XT9Keyboard setKeyboard(XT9CoreInput xt9coreinput, int keyboardResId, int keyboardModeId) {
        xt9coreinput.setKeyboardLoadCallback(this);
        if (checkConfigChanged()) {
            this.keyboardCache.evictAll();
            clearKeyboardViewsCache();
        }
        this.modeId = keyboardModeId;
        this.keyboardId = toCoreId(keyboardResId);
        boolean forceReload = false;
        Integer keyboardIdBoxed = Integer.valueOf(this.keyboardId);
        XT9Keyboard keyboard = this.keyboardCache.get(keyboardIdBoxed);
        if (keyboard == null) {
            keyboard = new XT9Keyboard(this.context, keyboardResId, keyboardModeId);
            log.d("Create new keyboardResId:" + keyboardResId + " keyboardIdBoxed:" + keyboardIdBoxed);
            this.keyboardCache.put(keyboardIdBoxed, keyboard);
            forceReload = true;
        }
        log.d("keyboardCache size:" + this.keyboardCache.size() + " keyboardId:" + this.keyboardId);
        if (!this.isOnlyThemeChanged || this.forceSetKeyboardDatabase) {
            xt9coreinput.setKeyboardDatabase(this.keyboardId, 0, forceReload);
            this.forceSetKeyboardDatabase = false;
        }
        return keyboard;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput.KeyboardLoadCallback
    public XT9KeyboardDatabase loadKeyboardDatabase(int kbdId, int pageNum) {
        InputView inputView;
        IME ime = IMEApplication.from(this.context).getIME();
        if (ime == null || (inputView = ime.getCurrentInputView()) == null) {
            return null;
        }
        XT9Keyboard keyboard = this.keyboardCache.get(Integer.valueOf(kbdId));
        if (keyboard == null) {
            log.e("keyboard return NULL, kdbId:" + kbdId);
            return null;
        }
        boolean usePopupCharacters = keyboard.canSwypePopupCharacters();
        int topMargin = inputView.getShiftGestureOffset();
        List<XT9KeyboardDatabase.Key> xt9Keys = new ArrayList<>();
        List<Character> codes = new ArrayList<>();
        List<Character> shiftCodes = new ArrayList<>();
        List<Character> multitapChars = new ArrayList<>();
        for (KeyboardEx.Key key : keyboard.getKeys()) {
            if (key.visible) {
                shiftCodes.clear();
                multitapChars.clear();
                codes.clear();
                for (int i : key.codes) {
                    Integer code = Integer.valueOf(i);
                    if (code.intValue() != 4063) {
                        codes.add(Character.valueOf((char) code.intValue()));
                    }
                }
                if (supportsTapCoordinates(key)) {
                    if (key.shiftCode != 4063 && key.shiftChars == null) {
                        shiftCodes.add(Character.valueOf((char) key.shiftCode));
                    } else if (key.altCode != 4063 && key.multitapChars == null) {
                        codes.add(Character.valueOf((char) key.altCode));
                    }
                    if (usePopupCharacters && key.popupCharacters != null && key.multitapChars == null) {
                        int popupCharCount = key.popupCharacters.length();
                        for (int i2 = 0; i2 < popupCharCount; i2++) {
                            Character nextCode = Character.valueOf(key.popupCharacters.charAt(i2));
                            if (Character.isLetter(nextCode.charValue()) && !codes.contains(nextCode)) {
                                codes.add(Character.valueOf(nextCode.charValue()));
                            }
                        }
                    }
                } else if (key.type == 3) {
                    int textLength = key.text.length();
                    for (int i3 = 0; i3 < textLength; i3++) {
                        codes.add(Character.valueOf(key.text.charAt(i3)));
                    }
                }
                if (key.multitapChars != null) {
                    for (int i4 : key.multitapChars) {
                        Integer multitapChar = Integer.valueOf(i4);
                        multitapChars.add(Character.valueOf((char) multitapChar.intValue()));
                    }
                }
                if (key.shiftChars != null) {
                    for (int i5 : key.shiftChars) {
                        Integer shiftChar = Integer.valueOf(i5);
                        shiftCodes.add(Character.valueOf((char) shiftChar.intValue()));
                    }
                }
                int codeCount = codes.size();
                char[] codesArray = new char[codeCount];
                for (int i6 = 0; i6 < codeCount; i6++) {
                    codesArray[i6] = codes.get(i6).charValue();
                }
                int shiftedCodeCount = shiftCodes.size();
                char[] shiftCodesArray = new char[shiftedCodeCount];
                for (int i7 = 0; i7 < shiftedCodeCount; i7++) {
                    shiftCodesArray[i7] = shiftCodes.get(i7).charValue();
                }
                int multitapCharCount = key.multitapChars == null ? 0 : multitapChars.size();
                char[] multitapCharsArray = null;
                if (multitapCharCount > 0) {
                    multitapCharsArray = new char[multitapCharCount];
                    for (int i8 = 0; i8 < multitapCharCount; i8++) {
                        multitapCharsArray[i8] = multitapChars.get(i8).charValue();
                    }
                }
                xt9Keys.add(new XT9KeyboardDatabase.Key(codesArray, shiftCodesArray, multitapCharsArray, key.type, key.x, key.y + topMargin, key.width, key.height));
            }
        }
        return new XT9KeyboardDatabase(keyboard.getMinWidth(), keyboard.getHeight() + topMargin, 1, xt9Keys);
    }

    public boolean supportsTapCoordinates(KeyboardEx.Key key) {
        return key.label != null && key.label.length() == 1 && key.label.charAt(0) == key.codes[0];
    }

    private boolean checkConfigChanged() {
        IMEApplication imeApp = IMEApplication.from(this.context);
        UserPreferences userPrefs = UserPreferences.from(this.context);
        boolean newHandwritingEnabled = userPrefs.isHandwritingEnabled();
        boolean newShowVoiceKeyEnabled = KeyboardEx.shouldEnableSpeechKey(this.context);
        float newKeyboardScalePortrait = userPrefs.getKeyboardScalePortrait();
        float newKeyboardScaleLandscape = userPrefs.getKeyboardScaleLandscape();
        boolean newKeyboardSwitchable = InputMethods.from(this.context).getFastSwitchedOffLanguage() != null;
        String newSwypeThemeName = imeApp.getCurrentTheme().getSku();
        InputMethods.Language newLanguage = imeApp.getCurrentLanguage();
        if (newLanguage == null) {
            return false;
        }
        int newCurrencyType = newLanguage.currencyType;
        boolean newAccessibility = this.accessibility;
        IME ime = imeApp.getIME();
        if (ime != null) {
            newAccessibility = ime.isAccessibilitySupportEnabled();
        }
        boolean newIsLanguageSupportingHwr = newLanguage.supportsHwr();
        KeyboardEx.KeyboardDockMode newDockMode = UserPreferences.from(this.context).getKeyboardDockingMode();
        Locale lc = this.context.getResources().getConfiguration().locale;
        String newLocale = lc != null ? lc.toString() : "";
        boolean configChanged = (newHandwritingEnabled == this.handwritingEnabled && newShowVoiceKeyEnabled == this.showVoiceKeyEnabled && newKeyboardSwitchable == this.keyboardSwitchable && newCurrencyType == this.currencyType && newAccessibility == this.accessibility && newIsLanguageSupportingHwr == this.isLanguageSupportingHwr && newKeyboardScalePortrait == this.keyboardScalePortrait && newKeyboardScaleLandscape == this.keyboardScaleLandscape && newDockMode == this.dockMode && newLocale.equalsIgnoreCase(this.locale)) ? false : true;
        this.handwritingEnabled = newHandwritingEnabled;
        this.showVoiceKeyEnabled = newShowVoiceKeyEnabled;
        this.keyboardSwitchable = newKeyboardSwitchable;
        this.currencyType = newCurrencyType;
        this.keyboardScalePortrait = newKeyboardScalePortrait;
        this.keyboardScaleLandscape = newKeyboardScaleLandscape;
        this.accessibility = newAccessibility;
        this.isLanguageSupportingHwr = newIsLanguageSupportingHwr;
        this.dockMode = newDockMode;
        this.locale = newLocale;
        if (!configChanged && !newSwypeThemeName.equals(this.swypeThemeName)) {
            this.isOnlyThemeChanged = true;
            configChanged = true;
        } else {
            this.isOnlyThemeChanged = false;
        }
        this.swypeThemeName = newSwypeThemeName;
        return configChanged;
    }

    public boolean isDockModeChanged() {
        KeyboardEx.KeyboardDockMode newDockMode = UserPreferences.from(this.context).getKeyboardDockingMode();
        boolean configChanged = newDockMode != this.dockMode;
        this.dockMode = newDockMode;
        return configChanged;
    }

    private int toCoreId(int swypeId) {
        KeyboardEx.KeyboardDockMode dockMode = UserPreferences.from(this.context).getKeyboardDockingMode();
        int orientation = this.context.getResources().getConfiguration().orientation;
        int id = swypeId & ID_CORE_MASK;
        if (orientation == 2) {
            id |= 65536;
        }
        switch (this.modeId) {
            case 1:
                id |= 131072;
                break;
            case 2:
                id |= 262144;
                break;
            case 4:
                id |= ID_IM_MODE;
                break;
            case 8:
                id |= ID_NUMBER_MODE;
                break;
        }
        return (dockMode.ordinal() << 20) | id;
    }

    public int getKeyboardId() {
        return this.keyboardId;
    }

    private void clearKeyboardViewsCache() {
        InputView inputView;
        IME ime = IMEApplication.from(this.context).getIME();
        if (ime != null && (inputView = ime.getCurrentInputView()) != null) {
            inputView.clearMiniKeyboardCache();
            inputView.clearDrawBufferCache();
        }
    }

    public void evictAll() {
        if (this.keyboardCache != null) {
            this.keyboardCache.evictAll();
        }
        clearKeyboardViewsCache();
    }
}
