package com.nuance.swype.input;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class XT9Keyboard extends KeyboardEx {
    public static final int XT9KEY_STATE_DISABLED = -1;
    public static final int XT9KEY_STATE_MT_TIMEOUT = 2;
    public static final int XT9KEY_STATE_OFF = 0;
    public static final int XT9KEY_STATE_ON = 1;
    private KeyboardEx.Key abcKey;
    private String enterKeyLabel;
    private List<KeyboardEx.Key> languageKeyLabels;
    private KeyboardEx.Key mABCKey;
    private Drawable mAltIcon;
    private KeyboardEx.Key mClearSequenceKey;
    private KeyboardEx.Key mDelimiterKey;
    private Drawable mDelimiterKeyIcon;
    private KeyboardEx.Key mEnterKey;
    private Drawable mQuickSwitchToChineseKeyIcon;
    private KeyboardEx.Key mSymbolKey;
    private KeyboardEx.Key mXT9Key;
    private KeyboardEx.Key speechKeyBackup;

    /* loaded from: classes.dex */
    public enum EnterKeyLabel {
        SEARCH,
        EMOJI,
        RETURN
    }

    public XT9Keyboard(Context context, int xmlLayoutResId, int mode) {
        super(context, xmlLayoutResId, mode, true, false);
        this.languageKeyLabels = new ArrayList();
        this.mAltIcon = null;
    }

    @Override // com.nuance.swype.input.KeyboardEx
    protected KeyboardEx.Key createKeyFromXml(Resources res, KeyboardEx.Row parent, int x, int y, KeyboardStyle style) {
        KeyboardEx.Key key = new KeyboardEx.Key(res, parent, x, y, style);
        switch (key.codes[0]) {
            case 10:
                if (this.mEnterKey != null) {
                    key.icon = this.mEnterKey.icon;
                    key.iconPreview = this.mEnterKey.iconPreview;
                    key.label = this.mEnterKey.label;
                    key.text = this.mEnterKey.text;
                    key.popupResId = this.mEnterKey.popupResId;
                }
                this.mEnterKey = key;
                break;
            case KeyboardEx.KEYCODE_MULTITAP_TOGGLE /* 2940 */:
                this.mXT9Key = key;
                break;
            case KeyboardEx.KEYCODE_SYM_LAYER /* 4077 */:
                this.mSymbolKey = key;
                break;
            case KeyboardEx.KEYCODE_ABC_LAYER /* 4078 */:
                this.mABCKey = key;
                break;
        }
        if (this.languageKeyLabels != null && !this.languageKeyLabels.isEmpty()) {
            if (key.codes[0] == 4087) {
                Iterator<KeyboardEx.Key> it = this.languageKeyLabels.iterator();
                while (true) {
                    if (it.hasNext()) {
                        KeyboardEx.Key langKey = it.next();
                        if (langKey.codes[0] == 4087) {
                            key.label = langKey.label;
                            key.repeatable = langKey.repeatable;
                        }
                    }
                }
            }
            if (key.altCode == 4087) {
                Iterator<KeyboardEx.Key> it2 = this.languageKeyLabels.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        KeyboardEx.Key langKey2 = it2.next();
                        if (langKey2.altCode == 4087) {
                            key.altLabel = langKey2.altLabel;
                            key.repeatable = langKey2.repeatable;
                        }
                    }
                }
            }
        }
        return key;
    }

    private void configureMessagingReturnKey(KeyboardEx.Key key, InputFieldInfo inputFieldInfo, KeyboardEx.KeyboardLayerType keyboardLayer) {
        IMEApplication app = IMEApplication.from(getContext());
        if (getKeyboardSettings().contains(KeyboardEx.KeyboardSettings.EMOJI) && (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS)) {
            if (inputFieldInfo.isShortMessageField()) {
                key.icon = app.getThemedDrawable(R.attr.emojiIconKeyFunction);
                key.iconPreview = app.getThemedDrawable(R.attr.emojiIconPopupKeypress);
                key.label = null;
                this.enterKeyLabel = String.valueOf(EnterKeyLabel.EMOJI);
                key.text = null;
                key.altIcon = app.getThemedDrawable(R.attr.symKeyboardReturnSecondary);
                key.codes = new int[]{KeyboardEx.KEYCODE_EMOTICON};
                key.popupResId = R.xml.popup_smileys_with_return;
                return;
            }
            if (inputFieldInfo.getActionLabel() != null) {
                key.label = inputFieldInfo.getActionLabel();
                this.enterKeyLabel = inputFieldInfo.getActionLabel().toString();
                key.icon = null;
                key.iconPreview = null;
            } else {
                key.icon = app.getResources().getDrawable(R.drawable.sym_keyboard_return);
                key.icon.setLevel(getCurrentIconLevel());
                key.iconPreview = app.getThemedDrawable(R.attr.symKeyboardFeedbackReturn);
                key.iconPreview.setLevel(getCurrentIconLevel());
                key.label = null;
                this.enterKeyLabel = String.valueOf(EnterKeyLabel.RETURN);
            }
            key.altIcon = app.getThemedDrawable(R.attr.emojiIconKeySecondary);
            key.codes = new int[]{10};
            key.popupResId = R.xml.popup_messaging_return_key;
            return;
        }
        if (inputFieldInfo.getActionLabel() != null) {
            key.label = inputFieldInfo.getActionLabel();
            this.enterKeyLabel = inputFieldInfo.getActionLabel().toString();
            key.icon = null;
            key.iconPreview = null;
            return;
        }
        key.icon = app.getResources().getDrawable(R.drawable.sym_keyboard_return);
        key.icon.setLevel(getCurrentIconLevel());
        key.iconPreview = app.getThemedDrawable(R.attr.symKeyboardFeedbackReturn);
        key.iconPreview.setLevel(getCurrentIconLevel());
        key.label = null;
        this.enterKeyLabel = String.valueOf(EnterKeyLabel.RETURN);
    }

    private void setReturnAltEmoji(IMEApplication app, KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
            this.mEnterKey.altIcon = app.getThemedDrawable(R.attr.emojiIconKeySecondary);
            this.mEnterKey.popupResId = R.xml.popup_messaging_return_key;
        }
    }

    public void setImeOptions(Context context, KeyboardEx.KeyboardLayerType keyboardLayer, int options, InputFieldInfo inputFieldInfo) {
        IMEApplication app = IMEApplication.from(context);
        if (this.mEnterKey != null) {
            this.mEnterKey.popupCharacters = null;
            this.mEnterKey.popupCharactersSimplified = null;
            this.mEnterKey.popupResId = 0;
            this.mEnterKey.text = null;
            this.mEnterKey.altIcon = null;
            this.mEnterKey.icon = null;
            this.mEnterKey.iconPreview = null;
            this.mEnterKey.label = null;
            this.mEnterKey.shiftedLabel = null;
            boolean isReturnIcon = false;
            switch (1073742079 & options) {
                case 3:
                    this.mEnterKey.icon = app.getResources().getDrawable(R.drawable.sym_keyboard_search);
                    this.mEnterKey.icon.setLevel(getCurrentIconLevel());
                    this.mEnterKey.iconPreview = app.getResources().getDrawable(R.drawable.sym_keyboard_search_feedback);
                    this.mEnterKey.iconPreview.setLevel(getCurrentIconLevel());
                    this.enterKeyLabel = String.valueOf(EnterKeyLabel.SEARCH);
                    setReturnAltEmoji(app, keyboardLayer);
                    break;
                default:
                    if (!AppPreferences.from(context).getDefaultBoolean(R.bool.display_return_key_as_emoticon_default) && IMEApplication.from(context).getIME() != null && !IMEApplication.from(context).getIME().isAccessibilitySupportEnabled()) {
                        configureMessagingReturnKey(this.mEnterKey, inputFieldInfo, keyboardLayer);
                        break;
                    } else if (inputFieldInfo.getActionLabel() != null) {
                        this.mEnterKey.label = inputFieldInfo.getActionLabel();
                        this.enterKeyLabel = inputFieldInfo.getActionLabel().toString();
                        this.mEnterKey.labelUpperCase = this.mEnterKey.label;
                        setReturnAltEmoji(app, keyboardLayer);
                        break;
                    } else {
                        this.mEnterKey.icon = app.getResources().getDrawable(R.drawable.sym_keyboard_return);
                        this.mEnterKey.icon.setLevel(getCurrentIconLevel());
                        this.mEnterKey.iconPreview = app.getResources().getDrawable(R.drawable.sym_keyboard_return_feedback);
                        this.mEnterKey.iconPreview.setLevel(getCurrentIconLevel());
                        this.enterKeyLabel = String.valueOf(EnterKeyLabel.RETURN);
                        setReturnAltEmoji(app, keyboardLayer);
                        isReturnIcon = true;
                        break;
                    }
                    break;
            }
            if (context.getResources().getBoolean(R.bool.highlight_action_key)) {
                this.mEnterKey.on = !isReturnIcon;
            }
            this.mEnterKey.labelUpperCase = this.mEnterKey.label;
            this.mEnterKey.shiftedLabel = null;
            if (this.mEnterKey.iconPreview != null) {
                this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
            }
        }
        if (app.getIME() != null && app.getIME().isDeviceExploreByTouchOn()) {
            if (this.mSymbolKey != null && (this.mSymbolKey.popupResId == R.xml.popup_layers || this.mSymbolKey.popupResId == R.xml.popup_layers_stroke)) {
                this.mSymbolKey.popupResId = R.xml.popup_layers_without_layouts;
            }
            if (this.mABCKey != null && this.mABCKey.popupResId == R.xml.popup_layers) {
                this.mABCKey.popupResId = R.xml.popup_layers_without_layouts;
            }
        }
        log.d("Return Key Label=" + this.enterKeyLabel);
    }

    public List<KeyboardEx.Key> setLanguageSwitchKey(InputMethods.Language language, int enabledLanguageCount) {
        InputMethods inputMethod;
        InputMethods.Language originalLang;
        this.languageKeyLabels.clear();
        Context context = getContext();
        IMEApplication imeApp = IMEApplication.from(context);
        for (KeyboardEx.Key key : this.mKeys) {
            if (key.codes[0] == 4087 || key.altCode == 4087) {
                this.languageKeyLabels.add(key);
            }
            if (key.codes[0] == 2939 && !language.isCJK() && (originalLang = (inputMethod = InputMethods.from(getContext())).getFastSwitchedOffLanguage()) != null) {
                if (originalLang.isKoreanLanguage()) {
                    key.icon = null;
                    key.label = inputMethod.getToggleLanguageName();
                } else if (originalLang.isChineseLanguage()) {
                    if (this.mQuickSwitchToChineseKeyIcon == null) {
                        this.mQuickSwitchToChineseKeyIcon = imeApp.getResources().getDrawable(R.drawable.sym_keyboard_chinese_toggle_english);
                    }
                    if (this.mQuickSwitchToChineseKeyIcon != null) {
                        key.icon = this.mQuickSwitchToChineseKeyIcon;
                        key.icon.setLevel(getCurrentIconLevel());
                        key.iconPreview = imeApp.getThemedDrawable(R.attr.cjkChineseFeedbackToggleEnglish);
                        key.label = null;
                    }
                } else if (originalLang.isJapaneseLanguage()) {
                    key.icon = imeApp.getResources().getDrawable(R.drawable.sym_keyboard_japanese_toggle_english);
                    key.icon.setLevel(getCurrentIconLevel());
                    key.iconPreview = imeApp.getThemedDrawable(R.attr.cjkJapaneseFeedbackToggleEnglish);
                    key.label = null;
                }
                key.labelUpperCase = key.label;
            }
        }
        if (!this.languageKeyLabels.isEmpty() && language != null) {
            boolean singleLanguageDisplayHide = context.getResources().getBoolean(R.bool.hide_globe_label_single_language);
            for (KeyboardEx.Key key2 : this.languageKeyLabels) {
                if (!imeApp.isMultipleEnabledSubtypeAvailable(enabledLanguageCount)) {
                    key2.label = null;
                    key2.repeatable = false;
                    if (singleLanguageDisplayHide) {
                        if (key2.altIcon != null) {
                            this.mAltIcon = key2.altIcon;
                        }
                        key2.altIcon = null;
                        key2.altLabel = null;
                    }
                } else if (key2.codes != null && key2.codes[0] == 4087) {
                    key2.label = language.mLanguageAbbr;
                    key2.repeatable = false;
                    if (singleLanguageDisplayHide && key2.altIcon == null) {
                        key2.altIcon = this.mAltIcon;
                    }
                } else if (key2.altCode == 4087) {
                    key2.alwaysShowAltSymbol = true;
                    key2.altLabel = language.mLanguageAbbr;
                    key2.repeatable = false;
                    if (singleLanguageDisplayHide && key2.altIcon == null) {
                        key2.altIcon = this.mAltIcon;
                    }
                    IMEApplication app = IMEApplication.from(context);
                    key2.altIcon = app.getThemedDrawable(R.attr.symKeyboardGlobe);
                }
            }
        }
        return this.languageKeyLabels;
    }

    public KeyboardEx.Key updateDelimiterKeyLabel(boolean isDelimiter, boolean isKeyPad, boolean isPinyinMode, boolean isStrokeMode, boolean isTablet) {
        if (isPinyinMode && isKeyPad) {
            Iterator<KeyboardEx.Key> it = this.mKeys.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                KeyboardEx.Key key = it.next();
                if (isPinyinMode && key.label != null && key.codes[0] == 49) {
                    this.mDelimiterKey = key;
                    break;
                }
            }
        } else {
            Iterator<KeyboardEx.Key> it2 = this.mKeys.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                KeyboardEx.Key key2 = it2.next();
                if (isKeyPad || isStrokeMode) {
                    if (key2.codes[0] == 2939) {
                        this.mDelimiterKey = key2;
                        break;
                    }
                } else if (key2.codes[0] == 2939) {
                    this.mDelimiterKey = key2;
                    break;
                }
            }
        }
        if (this.mDelimiterKey != null) {
            if (isDelimiter) {
                if (isKeyPad && this.mDelimiterKey.altLabel != null && !isPinyinMode) {
                    this.mDelimiterKey.altCode = 39;
                    this.mDelimiterKey.altLabel = null;
                }
                this.mDelimiterKey.codes[0] = 39;
                if (!isPinyinMode || (isPinyinMode && !isKeyPad)) {
                    this.mDelimiterKey.icon = null;
                    this.mDelimiterKey.iconPreview = null;
                    this.mDelimiterKey.label = "'";
                }
            } else if (isKeyPad || isStrokeMode) {
                if (!isPinyinMode) {
                    this.mDelimiterKey.codes[0] = 2939;
                    IMEApplication imeApp = IMEApplication.from(getContext());
                    if (this.mDelimiterKeyIcon == null) {
                        this.mDelimiterKeyIcon = imeApp.getResources().getDrawable(R.drawable.sym_keyboard_chinese_toggle_chinese);
                    }
                    if (this.mDelimiterKeyIcon != null) {
                        this.mDelimiterKey.icon = this.mDelimiterKeyIcon;
                        this.mDelimiterKey.icon.setLevel(getCurrentIconLevel());
                        this.mDelimiterKey.iconPreview = imeApp.getThemedDrawable(R.attr.cjkChineseFeedbackToggleChinese);
                        this.mDelimiterKey.label = null;
                    }
                }
            } else {
                this.mDelimiterKey.codes[0] = 2939;
                IMEApplication imeApp2 = IMEApplication.from(getContext());
                if (this.mDelimiterKeyIcon == null) {
                    this.mDelimiterKeyIcon = imeApp2.getResources().getDrawable(R.drawable.sym_keyboard_chinese_toggle_chinese);
                }
                if (this.mDelimiterKeyIcon != null) {
                    this.mDelimiterKey.icon = this.mDelimiterKeyIcon;
                    this.mDelimiterKey.icon.setLevel(getCurrentIconLevel());
                    this.mDelimiterKey.iconPreview = imeApp2.getThemedDrawable(R.attr.cjkChineseFeedbackToggleChinese);
                    this.mDelimiterKey.label = null;
                }
            }
        }
        return this.mDelimiterKey;
    }

    public KeyboardEx.Key updateClearKeyLabel(boolean isClearKey, boolean isKeyPad, boolean isPinyinMode, boolean isModeStroke, boolean isTablet) {
        if (isKeyPad || isModeStroke) {
            Iterator<KeyboardEx.Key> it = this.mKeys.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                KeyboardEx.Key key = it.next();
                if (key.codes[0] == 6463) {
                    this.mClearSequenceKey = key;
                    break;
                }
            }
        }
        if (this.speechKeyBackup == null && this.mClearSequenceKey != null) {
            this.speechKeyBackup = new KeyboardEx.Key(this.mClearSequenceKey, this.mClearSequenceKey.x, this.mClearSequenceKey.y, this.mClearSequenceKey.gap, this.mClearSequenceKey.width, this.mClearSequenceKey.height);
        }
        if (this.mClearSequenceKey != null && (isKeyPad || isModeStroke)) {
            Resources res = getContext().getResources();
            if (isClearKey) {
                this.mClearSequenceKey.codes[0] = 4065;
                this.mClearSequenceKey.icon = null;
                this.mClearSequenceKey.iconPreview = null;
                this.mClearSequenceKey.label = res.getText(R.string.clear_key_keypad);
            } else {
                this.mClearSequenceKey.codes[0] = 6463;
                this.mClearSequenceKey.icon = this.speechKeyBackup.icon;
                this.mClearSequenceKey.iconPreview = this.speechKeyBackup.iconPreview;
                this.mClearSequenceKey.label = null;
            }
        }
        return this.mClearSequenceKey;
    }

    public KeyboardEx.Key currentDelimiterKeyLabel() {
        if (this.mDelimiterKey != null) {
            return this.mDelimiterKey;
        }
        return null;
    }

    public KeyboardEx.Key setXT9KeyState(int state) {
        return this.mXT9Key;
    }

    public void updateABCLabel(InputMethods.Language language, KeyboardEx.KeyboardLayerType keyboardLayer) {
        if ((keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT) && language != null && language.isCJK()) {
            String abcKeyLabel = InputMethods.from(getContext()).getNativeLanguageNameForCJK(language);
            if (this.abcKey != null) {
                if (abcKeyLabel != null && !abcKeyLabel.equals(this.abcKey.label)) {
                    this.abcKey.label = abcKeyLabel;
                    return;
                }
                return;
            }
            for (KeyboardEx.Key key : this.mKeys) {
                if (key.codes[0] == 4078) {
                    this.abcKey = key;
                    this.abcKey.label = abcKeyLabel;
                    return;
                }
            }
        }
    }

    public void destroyKeyboard() {
        this.languageKeyLabels.clear();
        this.mEnterKey = null;
        this.mXT9Key = null;
        this.mDelimiterKey = null;
        this.mClearSequenceKey = null;
        this.abcKey = null;
        this.mSymbolKey = null;
        this.mABCKey = null;
        this.background = null;
    }
}
