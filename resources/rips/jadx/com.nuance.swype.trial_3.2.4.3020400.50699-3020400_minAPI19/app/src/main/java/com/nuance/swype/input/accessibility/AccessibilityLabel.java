package com.nuance.swype.input.accessibility;

import android.annotation.SuppressLint;
import android.content.Context;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.PlatformUtil;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.accessibility.KeycodeAccessibilityMapping;
import com.nuance.swype.service.impl.AccountUtil;
import com.nuance.swype.util.CharacterUtilities;

/* loaded from: classes.dex */
public class AccessibilityLabel {
    private static final int MAPPING_ID = R.xml.keycode_accessibility_label_mapping;
    private IMEApplication app;
    private KeycodeAccessibilityMapping mappingParser;
    private String strCapital;
    private String strPackageName;
    private StringBuilder label = new StringBuilder();
    private boolean bNeedCheckForAddCapital = false;

    public AccessibilityLabel(Context context) {
        this.mappingParser = new KeycodeAccessibilityMapping(context, MAPPING_ID);
        this.strCapital = context.getString(R.string.accessibility_label_for_Capital);
        this.app = IMEApplication.from(context);
        this.strPackageName = context.getPackageName();
    }

    public CharSequence getAccessibilityLabel(KeyboardEx.Key key, Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer, boolean checkCaps) {
        this.label.setLength(0);
        if (key.accessibilityLabel != null) {
            this.label.append(key.accessibilityLabel);
        } else {
            boolean isShiftOn = shiftState != Shift.ShiftState.OFF;
            CharSequence overiddenLabel = getAccessibilityLabelForKeyCode(key, shiftState, layer);
            if (overiddenLabel != null) {
                this.label.append(overiddenLabel);
            } else {
                switch (layer) {
                    case KEYBOARD_SYMBOLS:
                    case KEYBOARD_PHONE:
                        createLabelForSymbolsLayer(key, isShiftOn);
                        break;
                    case KEYBOARD_TEXT:
                        createLabelForTextLayer(key, isShiftOn, checkCaps);
                        break;
                    default:
                        this.label.append(getLabel(key));
                        break;
                }
            }
        }
        return this.label.toString();
    }

    private void createLabelForSymbolsLayer(KeyboardEx.Key key, boolean isShiftOn) {
        if (isShiftOn) {
            if (key.shiftedLabel != null) {
                this.label.append(getShiftedLabel(key));
                return;
            } else {
                this.label.append(getLabel(key));
                return;
            }
        }
        this.label.append(getLabel(key));
    }

    private void createLabelForTextLayer(KeyboardEx.Key key, boolean isShiftOn, boolean isFirstTime) {
        if (isFirstTime && isShiftOn && key.label != null) {
            this.bNeedCheckForAddCapital = true;
        }
        CharacterUtilities charUtils = this.app.getCharacterUtilities();
        CharSequence currentlabel = getLabel(key);
        if (this.bNeedCheckForAddCapital && currentlabel != null && currentlabel.length() == 1 && charUtils != null && !charUtils.isPunctuationOrSymbol(currentlabel.charAt(0)) && !CharacterUtilities.isDigit(currentlabel.charAt(0))) {
            this.label.append(String.format(this.strCapital, currentlabel));
        } else {
            this.label.append(currentlabel);
        }
        this.bNeedCheckForAddCapital = false;
    }

    private CharSequence getLabel(KeyboardEx.Key key) {
        CharSequence keyLabel = key.label;
        if (keyLabel == null) {
            keyLabel = "";
        } else {
            CharSequence replacementLabel = getReplacementLabel(keyLabel);
            if (replacementLabel != null) {
                return replacementLabel;
            }
        }
        return keyLabel;
    }

    private CharSequence getAltLabel(KeyboardEx.Key key) {
        CharSequence keyLabel = key.altLabel;
        if (keyLabel == null) {
            keyLabel = "";
        } else {
            CharSequence replacementLabel = getReplacementLabel(keyLabel);
            if (replacementLabel != null) {
                return replacementLabel;
            }
        }
        return keyLabel;
    }

    private CharSequence getShiftedLabel(KeyboardEx.Key key) {
        CharSequence keyLabel = key.shiftedLabel;
        if (keyLabel == null) {
            keyLabel = "";
        } else {
            CharSequence replacementLabel = getReplacementLabel(keyLabel);
            if (replacementLabel != null) {
                return replacementLabel;
            }
        }
        return keyLabel;
    }

    @SuppressLint({"DefaultLocale"})
    private CharSequence getReplacementLabel(CharSequence keyLabel) {
        IME ime;
        int code;
        String label_from_res;
        if (keyLabel.length() == 1 && (ime = this.app.getIME()) != null) {
            if (this.bNeedCheckForAddCapital) {
                code = keyLabel.toString().toUpperCase().charAt(0);
            } else {
                code = keyLabel.charAt(0);
            }
            String strId = "CharName_0x" + String.format("%04x", Integer.valueOf(code)).toUpperCase();
            int id = ime.getResources().getIdentifier(strId, "string", this.strPackageName);
            if (id != 0 && (label_from_res = ime.getResources().getString(id)) != null) {
                return label_from_res;
            }
        }
        return null;
    }

    public CharSequence getAccessibilityLabelForKeyCode(KeyboardEx.Key key, Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer) {
        return getAccessibilityLabelForKeyCode(key, shiftState, layer, false);
    }

    public CharSequence getAccessibilityAltLabelForKeyCode(KeyboardEx.Key key, Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer) {
        return getAccessibilityLabelForKeyCode(key, shiftState, layer, true);
    }

    public CharSequence getAccessibilityLabelForKeyCode(KeyboardEx.Key key, Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer, boolean altLabel) {
        CharSequence swypeLabel;
        KeycodeAccessibilityMapping.KeycodeAccessibilityLabelMapping mapping;
        if (key.codes.length > 0 && key.codes[0] == 10) {
            CharSequence enterLabel = getLabelForEnterKey();
            if (enterLabel != null) {
                return enterLabel;
            }
        } else if (key.codes.length > 0 && key.codes[0] == 43575 && (swypeLabel = getLabelForSwypeKey()) != null) {
            return swypeLabel;
        }
        boolean isShiftOn = shiftState != Shift.ShiftState.OFF;
        CharSequence mappedLabel = null;
        if (this.mappingParser != null && key.codes.length > 0 && (mapping = this.mappingParser.getAccessibilityMapping(key.codes[0])) != null) {
            switch (layer) {
                case KEYBOARD_SYMBOLS:
                    if (!isShiftOn) {
                        mappedLabel = mapping.getLabelOnSymbolsShifted();
                        break;
                    } else {
                        mappedLabel = mapping.getLabelOnSymbols();
                        break;
                    }
                case KEYBOARD_PHONE:
                    if (!isShiftOn) {
                        mappedLabel = mapping.getLableOnShiftedPhone();
                        break;
                    } else {
                        mappedLabel = mapping.getLableOnPhone();
                        break;
                    }
                case KEYBOARD_TEXT:
                    switch (shiftState) {
                        case OFF:
                            mappedLabel = mapping.getLabelOnTextShiftedCap();
                            break;
                        case ON:
                            mappedLabel = mapping.getLabelOnTextShiftedAllCap();
                            break;
                        case LOCKED:
                            mappedLabel = mapping.getLabelOnTextShiftedLowerCase();
                            break;
                    }
                default:
                    mappedLabel = getDefaultAccessbilityForKeyCode(mapping, isShiftOn, altLabel);
                    break;
            }
            if (mappedLabel == null) {
                mappedLabel = getDefaultAccessbilityForKeyCode(mapping, isShiftOn, altLabel);
            }
        }
        return mappedLabel;
    }

    private CharSequence getLabelForEnterKey() {
        IME ime;
        InputFieldInfo inputFieldInfo;
        if (this.app == null || (ime = this.app.getIME()) == null || (inputFieldInfo = ime.getInputFieldInfo()) == null) {
            return null;
        }
        if (inputFieldInfo.isShortMessageField() && !ime.isAccessibilitySupportEnabled()) {
            CharSequence label = ime.getResources().getString(R.string.accessibility_label_for_emoticons_smiley_key_long);
            return label;
        }
        CharSequence label2 = inputFieldInfo.getActionLabel();
        return label2;
    }

    private CharSequence getLabelForSwypeKey() {
        if (this.app == null) {
            return null;
        }
        PlatformUtil util = this.app.getPlatformUtil();
        Context context = this.app.getApplicationContext();
        IME ime = this.app.getIME();
        if (util == null || context == null || ime == null) {
            return null;
        }
        if (BuildInfo.from(context).isGoogleTrialBuild() || !util.checkForDataConnection() || !AccountUtil.deviceHasGoogleAccount(context)) {
            CharSequence label = ime.getResources().getString(R.string.store_my_themes);
            return label;
        }
        if (!ThemeManager.isDownloadableThemesEnabled()) {
            return null;
        }
        CharSequence label2 = ime.getResources().getString(R.string.swype_store);
        return label2;
    }

    private CharSequence getDefaultAccessbilityForKeyCode(KeycodeAccessibilityMapping.KeycodeAccessibilityLabelMapping mapping, boolean isShiftOn, boolean altLabel) {
        CharSequence mappedLabel = null;
        if (isShiftOn && altLabel) {
            mappedLabel = mapping.getShiftedAltLabel();
        }
        if (!isShiftOn || mappedLabel == null) {
            if (altLabel) {
                return mapping.getAltLabel();
            }
            return mapping.getLabel();
        }
        return mappedLabel;
    }

    public CharSequence getAltAccessibilityLabel(KeyboardEx.Key key, Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer) {
        this.label.delete(0, this.label.length());
        CharSequence overiddenLabel = getAccessibilityAltLabelForKeyCode(key, shiftState, layer);
        if (overiddenLabel != null) {
            this.label.append(overiddenLabel);
        } else {
            CharSequence altLabel = getAltLabel(key);
            this.label.append(altLabel);
        }
        return this.label.toString();
    }

    public CharSequence getLongPressAccessibilityLabelForShiftKey(Shift.ShiftState shiftState, KeyboardEx.KeyboardLayerType layer) {
        KeycodeAccessibilityMapping.KeycodeAccessibilityLabelMapping mapping = this.mappingParser.getAccessibilityMapping(KeyboardEx.KEYCODE_SHIFT);
        if (mapping == null) {
            return null;
        }
        switch (layer) {
            case KEYBOARD_TEXT:
                switch (shiftState) {
                    case OFF:
                        CharSequence mappedLabel = mapping.getLabelOnTextShiftedAllCap();
                        return mappedLabel;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
