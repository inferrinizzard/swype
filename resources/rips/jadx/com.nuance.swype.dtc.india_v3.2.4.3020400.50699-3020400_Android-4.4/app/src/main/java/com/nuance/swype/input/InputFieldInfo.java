package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;
import android.view.inputmethod.EditorInfo;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressLint({"InlinedApi"})
/* loaded from: classes.dex */
public class InputFieldInfo {
    private static final boolean DEBUGLOG = false;
    public static final String INPUT_TYPE_DATE_TIME = "date time";
    public static final String INPUT_TYPE_EMAIL_ADDRESS = "email address";
    public static final String INPUT_TYPE_EMAIL_SUBJECT = "email subject";
    public static final String INPUT_TYPE_LONG_MESSAGE = "long message";
    public static final String INPUT_TYPE_NUMBERS = "numbers";
    public static final String INPUT_TYPE_PASSWORD = "password";
    public static final String INPUT_TYPE_PERSON_NAME = "person name";
    public static final String INPUT_TYPE_PHONE_NUMBER = "phone number";
    public static final String INPUT_TYPE_POSTAL_ADDRESS = "postal address";
    public static final String INPUT_TYPE_SHORT_MESSAGE = "short message";
    public static final String INPUT_TYPE_TEXT = "text";
    public static final String INPUT_TYPE_WEB_ADDRESS = "web address";
    public static final String INPUT_TYPE_WEB_SEARCH = "web search";
    public static final String INPUT_TYPE_WEB_TEXT = "web text";
    protected static final LogManager.Log log = LogManager.getLog(InputFieldInfo.class.getSimpleName());
    private final String[] WebTextEmailEntries;
    private final String[] WebTextPasswordEntries;
    private ApplicationInfo appInfo;
    EditorInfo mEditorInfo = new EditorInfo();
    private IME mIME;
    private Set<String> privateOptions;

    @SuppressLint({"InlinedApi"})
    private void logEditorInfo(EditorInfo ei) {
        log.d("[actionId]" + ei.actionId);
        log.d("[actionLabel]" + ((Object) ei.actionLabel));
        LogManager.Log log2 = log;
        Object[] objArr = new Object[1];
        objArr[0] = "[extras]" + (ei.extras != null ? ei.extras.toString() : "null");
        log2.d(objArr);
        log.d("[fieldId]" + ei.fieldId);
        log.d("[fieldName]" + ei.fieldName);
        log.d("[hintText]" + ((Object) ei.hintText));
        log.d("[imeOptions]0x" + Integer.toHexString(ei.imeOptions));
        log.d("[inputType]0x" + Integer.toHexString(ei.inputType));
        log.d("[inputType](class) 0x" + Integer.toHexString(ei.inputType & 15));
        log.d("[inputType](var) 0x" + Integer.toHexString(ei.inputType & 4080));
        log.d("[initialCapsMode]" + ei.initialCapsMode);
        log.d("[initialSelEnd]" + ei.initialSelEnd);
        log.d("[initialSelStart]" + ei.initialSelStart);
        log.d("[packageName]" + ei.packageName);
        log.d("[privateImeOptions]" + ei.privateImeOptions);
        boolean noExtractUi = (268435456 & ei.imeOptions) != 0;
        int i = ei.imeOptions;
        log.d("no extract ui: " + noExtractUi);
        log.d("pw: " + isPasswordField() + "; num: " + isNumberField() + "; ip: " + isIpField() + "; text: " + isInputTextClass() + "; email: " + isEmailAddressField() + "; url: " + isURLField() + "; comp: " + isCompletionField() + "; no sug: " + isNoSuggestionOnField() + "; ac on: " + isAutoCorrectionOnField() + "; ml " + isMultilineField());
        LogManager.Log log3 = log;
        Object[] objArr2 = new Object[1];
        objArr2[0] = "action id: " + getActionId() + "; no enter action: " + ((ei.imeOptions & 1073741824) != 0);
        log3.d(objArr2);
    }

    public InputFieldInfo(IME ime) {
        this.WebTextEmailEntries = ime.getResources().getStringArray(R.array.web_email_fields);
        this.WebTextPasswordEntries = ime.getResources().getStringArray(R.array.web_password_fields);
        this.mIME = ime;
        this.mEditorInfo.imeOptions = 0;
        this.mEditorInfo.inputType = 1;
        this.privateOptions = new HashSet();
    }

    public void setEditorInfo(EditorInfo editorInfo) {
        this.mEditorInfo = editorInfo;
        logEditorInfo(this.mEditorInfo);
        this.appInfo = null;
        if (this.mEditorInfo != null) {
            PackageManager pm = this.mIME.getPackageManager();
            try {
                this.appInfo = pm.getApplicationInfo(this.mEditorInfo.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
            }
            this.privateOptions.clear();
            if (!TextUtils.isEmpty(this.mEditorInfo.privateImeOptions)) {
                if (this.mEditorInfo.privateImeOptions.contains(";")) {
                    this.privateOptions = new HashSet(Arrays.asList(this.mEditorInfo.privateImeOptions.split(";")));
                } else {
                    this.privateOptions = new HashSet(Arrays.asList(this.mEditorInfo.privateImeOptions.split(",")));
                }
            }
        }
    }

    public ApplicationInfo getAppInfo() {
        return this.appInfo;
    }

    public boolean targetsAtLeast(int ver) {
        return this.appInfo != null && this.appInfo.targetSdkVersion >= ver;
    }

    public boolean isEquivalentTo(EditorInfo attribute) {
        return this.mEditorInfo != null && this.mEditorInfo.packageName != null && this.mEditorInfo.packageName.equals(attribute.packageName) && this.mEditorInfo.fieldId == attribute.fieldId && this.mEditorInfo.inputType == attribute.inputType;
    }

    public boolean isUDBSubstitutionField() {
        return this.privateOptions.contains("xt9.com.nuance.udbeditor.alpha.substitution");
    }

    public boolean isEditDictionaryField() {
        return this.privateOptions.contains("com.nuance.swype.type=edit_dictionary");
    }

    public boolean isShowCandidatesImmediately() {
        return this.privateOptions.contains("showCandidatesImmediately");
    }

    public boolean isIpField() {
        return this.privateOptions.contains("inputType=ipAddress");
    }

    public boolean isNoMicrophone() {
        return this.privateOptions.contains("nm") || this.privateOptions.contains("noMicrophoneKey");
    }

    public boolean isPredictionOffField() {
        return this.privateOptions.contains("inputType=PredictionOff");
    }

    public boolean isMonthEditText() {
        return this.privateOptions.contains("inputType=month_edittext") || ("com.google.android.calendar".equals(getPackageName()) && isNoSuggestionOnField() && isInputTextClass());
    }

    public boolean isSamsungSNote() {
        return "com.samsung.android.snote".equals(getPackageName());
    }

    public int getImeOptions() {
        return this.mEditorInfo.imeOptions;
    }

    public int getInputClass() {
        return this.mEditorInfo.inputType & 15;
    }

    public int getInputVariant() {
        return this.mEditorInfo.inputType & 4080;
    }

    public boolean isInputTypeNull() {
        return this.mEditorInfo.inputType == 0;
    }

    public boolean isKnownClass() {
        int cls = getInputClass();
        return cls == 1 || cls == 4 || cls == 2 || cls == 3;
    }

    public boolean isInputTextClass() {
        return getInputClass() == 1 || !isKnownClass();
    }

    public boolean textInputFieldWithSuggestionEnabled() {
        return !textInputFieldWithSuggestionDisabled();
    }

    public boolean textInputFieldWithSuggestionDisabled() {
        return isPasswordField() || isNumericModeField() || isPhoneNumberField() || isMonthEditText() || isInputTypeNull();
    }

    public boolean isNumericModeField() {
        return isNumberField() || isDateTimeField() || isIpField();
    }

    @SuppressLint({"InlinedApi"})
    public boolean isNumericPasswordField() {
        return isNumberField() && getInputVariant() == 16;
    }

    public boolean isTextPasswordField() {
        return (isInputTextClass() && (getInputVariant() == 128 || (getInputVariant() == 144 && !this.mIME.getAppSpecificBehavior().shouldEnablePredictionForPassword()))) || isLikelyWebTextPassword();
    }

    public boolean isPasswordField() {
        return isTextPasswordField() || isNumericPasswordField();
    }

    public boolean isInvisiblePasswordField() {
        return !(isInputTextClass() && getInputVariant() == 144 && !this.mIME.getAppSpecificBehavior().shouldEnablePredictionForPassword()) && isPasswordField();
    }

    public boolean isSearchField() {
        return isLikelySearchField();
    }

    @SuppressLint({"InlinedApi"})
    private boolean isLikelyWebTextEmail() {
        if (getInputVariant() == 208) {
            return true;
        }
        if (getFieldName() == null || !isWebEditText()) {
            return false;
        }
        boolean webEmail = matchUrlPrefixes(getFieldName(), this.WebTextEmailEntries);
        return webEmail;
    }

    @SuppressLint({"InlinedApi"})
    private boolean isLikelyWebTextPassword() {
        if (getInputVariant() == 224) {
            return true;
        }
        if (getFieldName() == null) {
            return false;
        }
        boolean webPassword = matchUrlPrefixes(getFieldName(), this.WebTextPasswordEntries);
        return webPassword;
    }

    private boolean isLikelySearchField() {
        boolean searchField = false;
        if (isWebSearchField()) {
            searchField = true;
        }
        if (isURLField()) {
            return true;
        }
        return searchField;
    }

    private boolean matchUrlPrefixes(String url, String[] prefixes) {
        for (String s : prefixes) {
            if (s.regionMatches(true, 0, getFieldName(), 0, s.length())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailAddressField() {
        return (isInputTextClass() && getInputVariant() == 32) || isLikelyWebTextEmail();
    }

    public boolean isURLField() {
        return isInputTextClass() && getInputVariant() == 16;
    }

    public boolean isURLWithSearchField() {
        return isAcitonGoField() && (this.mEditorInfo.inputType & 255) == 17 && isURLField();
    }

    public boolean isAutoCorrectionOnField() {
        return (this.mEditorInfo.inputType & 32768) != 0;
    }

    public boolean isNoSuggestionOnField() {
        return (this.mEditorInfo.inputType & 524288) != 0;
    }

    public boolean isAutoCorrectionOn() {
        return (this.mEditorInfo.inputType & 32768) != 0;
    }

    public boolean isCompletionField() {
        return (this.mEditorInfo.inputType & 65536) != 0;
    }

    public boolean isMultilineField() {
        return (this.mEditorInfo.inputType & HardKeyboardManager.META_META_LEFT_ON) != 0;
    }

    public boolean isFieldWithFilterList() {
        return isInputTextClass() && getInputVariant() == 176;
    }

    public boolean isNameField() {
        return isInputTextClass() && getInputVariant() == 96;
    }

    public boolean isPhoneticField() {
        return isInputTextClass() && getInputVariant() == 192;
    }

    public boolean isShortMessageField() {
        return isInputTextClass() && getInputVariant() == 64;
    }

    public boolean isLongMessageField() {
        return isInputTextClass() && getInputVariant() == 80;
    }

    public boolean isEmailSubjectField() {
        return isInputTextClass() && getInputVariant() == 48;
    }

    public boolean isPostalAddress() {
        return isInputTextClass() && getInputVariant() == 112;
    }

    public boolean isWebEditText() {
        return isInputTextClass() && getInputVariant() == 160;
    }

    public boolean isDateTimeField() {
        return getInputClass() == 4;
    }

    public boolean isNumberField() {
        return getInputClass() == 2;
    }

    public boolean isNumberPasswordField() {
        return getInputClass() == 2 && getInputVariant() == 16;
    }

    public boolean isPhoneNumberField() {
        return getInputClass() == 3;
    }

    public static boolean noFullscreenMode(EditorInfo info) {
        return (info == null || ((info.imeOptions & 268435456) == 0 && (33554432 & info.imeOptions) == 0)) ? false : true;
    }

    public int getImeActionType() {
        return this.mEditorInfo.imeOptions & 1073742079;
    }

    public String getFieldName() {
        return this.mEditorInfo.fieldName != null ? this.mEditorInfo.fieldName : "null";
    }

    public int getFieldId() {
        return this.mEditorInfo.fieldId;
    }

    private CharSequence getActionLabelFromResource(int actionId) {
        switch (actionId) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return this.mIME.getTextForImeAction(actionId);
            default:
                return null;
        }
    }

    public CharSequence getActionLabel() {
        int actionId = getActionId();
        if (1 != actionId) {
            if (!TextUtils.isEmpty(this.mEditorInfo.actionLabel)) {
                return this.mEditorInfo.actionLabel;
            }
            return getActionLabelFromResource(actionId);
        }
        return null;
    }

    public int getActionId() {
        int actionId = this.mEditorInfo.imeOptions & 255;
        if ((this.mEditorInfo.imeOptions & 1073741824) != 0) {
            return 1;
        }
        if (!TextUtils.isEmpty(this.mEditorInfo.actionLabel)) {
            int actionId2 = this.mEditorInfo.actionId;
            return actionId2;
        }
        return actionId;
    }

    public String getInputType() {
        if (isPasswordField()) {
            return INPUT_TYPE_PASSWORD;
        }
        if (isEmailAddressField()) {
            return INPUT_TYPE_EMAIL_ADDRESS;
        }
        if (isEmailSubjectField()) {
            return INPUT_TYPE_EMAIL_SUBJECT;
        }
        if (isURLField()) {
            return INPUT_TYPE_WEB_ADDRESS;
        }
        if (isNameField()) {
            return INPUT_TYPE_PERSON_NAME;
        }
        if (isShortMessageField()) {
            return INPUT_TYPE_SHORT_MESSAGE;
        }
        if (isLongMessageField()) {
            return INPUT_TYPE_LONG_MESSAGE;
        }
        if (isWebEditText()) {
            return INPUT_TYPE_WEB_TEXT;
        }
        if (isPostalAddress()) {
            return INPUT_TYPE_POSTAL_ADDRESS;
        }
        if (isDateTimeField()) {
            return INPUT_TYPE_DATE_TIME;
        }
        if (isNumberField()) {
            return INPUT_TYPE_NUMBERS;
        }
        if (isPhoneNumberField()) {
            return INPUT_TYPE_PHONE_NUMBER;
        }
        if (isWebSearchField()) {
            return INPUT_TYPE_WEB_SEARCH;
        }
        return INPUT_TYPE_TEXT;
    }

    public boolean isWebSearchField() {
        return getImeActionType() == 3;
    }

    public boolean isAcitonGoField() {
        return getImeActionType() == 2;
    }

    public String getPackageName() {
        return this.mEditorInfo.packageName != null ? this.mEditorInfo.packageName : "";
    }

    public String getPrivateImeOptions() {
        return this.mEditorInfo.privateImeOptions != null ? this.mEditorInfo.privateImeOptions : "";
    }

    public EditorInfo getEditorInfo() {
        return this.mEditorInfo;
    }

    public IME getIME() {
        return this.mIME;
    }

    public void dump() {
        this.mEditorInfo.dump(new LogCatDump(), "");
    }

    /* loaded from: classes.dex */
    private class LogCatDump implements Printer {
        private LogCatDump() {
        }

        @Override // android.util.Printer
        public void println(String x) {
            Log.i("TEST", x);
        }
    }

    public boolean isVoiceDisabled() {
        return this.privateOptions.contains("disableVoiceInput=true");
    }

    public boolean isEmoticonInputDisabled() {
        return this.privateOptions.contains("disableEmoticonInput=true");
    }
}
