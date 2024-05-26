package com.nuance.swype.input.appspecific;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class AppSpecificBehavior {
    public static final boolean DEFAULT_AUTOSPACE_IN_URL_FIELD = false;
    public static final boolean DEFAULT_AUTOSPACE_ON_TEXT_EXTRACT_FAILURE = false;
    public static final boolean DEFAULT_AVOID_SET_COMPOSING_REGION = false;
    public static final boolean DEFAULT_BYPASS_INTERNAL_CACHE = false;
    public static final boolean DEFAULT_CHECK_SET_CURRRENT_CURSOR = false;
    public static final boolean DEFAULT_DISABLE_CANDIDATES_LIST = false;
    public static final boolean DEFAULT_DISABLE_DELETE_RECAPTURE_IN_ALL_FIELDS = false;
    public static final boolean DEFAULT_DISABLE_EMOJI_CANDIDATES_LIST = false;
    public static final boolean DEFAULT_DISABLE_RECAPTURE = false;
    public static final boolean DEFAULT_FILTER_INPUTVIEW_RESTARTS = false;
    public static final boolean DEFAULT_FORCE_TYPE_NULL_FOR_BACK_SPACE = false;
    public static final boolean DEFAULT_IGNORE_LOST_COMPOSING_TEXT = false;
    public static final boolean DEFAULT_IGNORE_TYPE_NULL_CHECK_FOR_BACKSPACE = false;
    public static final boolean DEFAULT_NO_CONTEXT_MENU_EDITING = false;
    public static final boolean DEFAULT_NO_REPLACING_RESELECTED_WORD_WHEN_MATCHING = false;
    public static final boolean DEFAULT_NO_SET_SELECTION = false;
    public static final boolean DEFAULT_RESTRICT_GET_TEXT_LENGTH_FROM_CURSOR = false;
    public static final boolean DEFAULT_SELECT_TEXT_TO_REPLACE = false;
    public static final boolean DEFAULT_SHOULD_CLEAR_COMPOSING_ADD_DELETE_SPACE = false;
    public static final boolean DEFAULT_SHOULD_SEND_RETURN_AS_KEY_EVENT = false;
    public static final boolean DEFAULT_SHOULD_SET_COMPOSING_SPAN = false;
    public static final boolean DEFAULT_SHOULD_USE_SEARCH_RECOGNIZER_TYPE_FOR_URL = false;
    private Map<String, BehaviorInfo> currentBehaviorOverride;
    private boolean mForceByPassCache;
    private String swypePackageName;
    private int mInputFieldImeOptions = 0;
    private int mInputFieldInputType = 0;
    private final Map<String, Map<String, BehaviorInfo>> behavior_map = new HashMap();

    /* JADX WARN: Removed duplicated region for block: B:15:0x005e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0176 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AppSpecificBehavior(android.content.Context r27) {
        /*
            Method dump skipped, instructions count: 377
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.appspecific.AppSpecificBehavior.<init>(android.content.Context):void");
    }

    public void onPackageChanged(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            Map<String, BehaviorInfo> behaviors = this.behavior_map.get(packageName);
            if (behaviors != null) {
                Iterator<BehaviorInfo> it = behaviors.values().iterator();
                while (it.hasNext()) {
                    it.next().updateEnabled(packageInfo.versionCode);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        this.mForceByPassCache = false;
        if (attribute.packageName != null) {
            this.currentBehaviorOverride = this.behavior_map.get(attribute.packageName);
            if (this.currentBehaviorOverride == null && this.swypePackageName.equalsIgnoreCase(attribute.packageName) && (attribute.inputType & 15) == 1 && (attribute.inputType & 4080) == 160) {
                this.mForceByPassCache = true;
            }
        }
        this.mInputFieldImeOptions = attribute.imeOptions;
        this.mInputFieldInputType = attribute.inputType;
    }

    public void onFinishInputView(boolean finishingInput) {
        if (!shouldEnablePredictionForPassword()) {
            this.currentBehaviorOverride = null;
        }
        this.mForceByPassCache = false;
    }

    public boolean shouldAutoSpaceOnTextExtractFailure() {
        return ((Boolean) getAppBehavior("shouldAutoSpaceOnTextExtractFailure", false)).booleanValue();
    }

    public boolean ignoreTypeNullCheckForBackspace() {
        return ((Boolean) getAppBehavior("ignoreTypeNullCheckForBackspace", false)).booleanValue();
    }

    public boolean forceTypeNullForBackspace() {
        return ((Boolean) getAppBehavior("forceTypeNullForBackspace", false)).booleanValue();
    }

    public boolean shouldAutoSpaceInUrlField() {
        return ((Boolean) getAppBehavior("shouldAutoSpaceInUrlField", false)).booleanValue();
    }

    public boolean shouldDisableRecapture() {
        return ((Boolean) getAppBehavior("shouldDisableRecapture", false)).booleanValue() || shouldSkipWrongStartInputView();
    }

    public boolean shouldDisableCandidatesList() {
        return ((Boolean) getAppBehavior("shouldDisableCandidatesList", false)).booleanValue();
    }

    public boolean shouldDisableEmojiCandidatesList() {
        return ((Boolean) getAppBehavior("shouldDisableEmojiCandidatesList", false)).booleanValue();
    }

    public boolean shouldIgnoreLostComposingText() {
        return ((Boolean) getAppBehavior("shouldIgnoreLostComposingText", false)).booleanValue();
    }

    public boolean shouldCheckCurrentCursor() {
        return ((Boolean) getAppBehavior("shouldCheckCurrentCursor", false)).booleanValue();
    }

    public boolean shouldByPassInternalCache() {
        return this.mForceByPassCache || ((Boolean) getAppBehavior("shouldByPassInternalCache", false)).booleanValue();
    }

    public boolean selectTextToReplace() {
        return ((Boolean) getAppBehavior("selectTextToReplace", false)).booleanValue();
    }

    public boolean shouldFilterInputViewRestarts() {
        return ((Boolean) getAppBehavior("shouldFilterInputViewRestarts", false)).booleanValue();
    }

    public boolean shouldUseSearchRecognizerTypeForUrl() {
        return ((Boolean) getAppBehavior("shouldUseSearchRecognizerTypeForUrl", false)).booleanValue();
    }

    public boolean shouldAvoidSetComposingRegion() {
        return ((Boolean) getAppBehavior("shouldAvoidSetComposingRegion", false)).booleanValue();
    }

    public boolean noReplacingReselectedWordWhenMatching() {
        return ((Boolean) getAppBehavior("noReplacingReselectedWordWhenMatching", false)).booleanValue();
    }

    public boolean shouldClearComposingAddDeleteSpace() {
        return ((Boolean) getAppBehavior("shouldClearComposingAddDeleteSpace", false)).booleanValue();
    }

    public boolean shouldSetComosingSpan() {
        return ((Boolean) getAppBehavior("shouldSetComposingSpan", false)).booleanValue();
    }

    public boolean noContextMenuEditing() {
        return ((Boolean) getAppBehavior("noContextMenuEditing", false)).booleanValue();
    }

    public boolean noSetSelection() {
        return ((Boolean) getAppBehavior("noSetSelection", false)).booleanValue();
    }

    private Object getAppBehavior(String behaviorName, Object defaultValue) {
        BehaviorInfo behavior = null;
        if (this.currentBehaviorOverride != null) {
            BehaviorInfo behavior2 = this.currentBehaviorOverride.get(behaviorName);
            behavior = behavior2;
        }
        Object value = behavior != null ? behavior.getValue() : defaultValue;
        return value != null ? value : defaultValue;
    }

    public boolean supportsGetTextWithStyles() {
        return ((Boolean) getAppBehavior("supportsGetTextWithStyles", true)).booleanValue();
    }

    public boolean shouldSendReturnAsKeyEvent() {
        return ((Boolean) getAppBehavior("shouldSendReturnAsKeyEvent", false)).booleanValue();
    }

    public boolean shouldDeleteSurroundingTextUsingKeyEvent() {
        return ((Boolean) getAppBehavior("shouldDeleteSurroundingTextUsingKeyEvent", false)).booleanValue();
    }

    public boolean shouldDeleteSurroundingBeforeTextCharByChar() {
        return ((Boolean) getAppBehavior("shouldDeleteSurroundingBeforeTextCharByChar", false)).booleanValue();
    }

    public boolean shouldSendBackSpaceToDeleteBreakLine() {
        return ((Boolean) getAppBehavior("shouldSendBackSpaceToDeleteBreakLine", false)).booleanValue();
    }

    public boolean shouldRemoveUpdateWCLMessage() {
        return ((Boolean) getAppBehavior("shouldRemoveUpdateWCLMessage", false)).booleanValue();
    }

    public boolean shouldTreatEditTextAsInvalidField() {
        return ((Boolean) getAppBehavior("shouldTreatEditTextAsInvalidField", false)).booleanValue();
    }

    public boolean shouldTreatEditTextAsWebSearchField() {
        return ((Boolean) getAppBehavior("shouldTreatEditTextAsWebSearchField", false)).booleanValue();
    }

    public boolean shouldSkipInvalidFieldIdEditor() {
        return ((Boolean) getAppBehavior("shouldSkipInvalidFieldIdEditor", false)).booleanValue();
    }

    public boolean shouldEnablePredictionForPassword() {
        return ((Boolean) getAppBehavior("shouldEnablePredictionForPassword", false)).booleanValue();
    }

    public boolean shouldEditModeUseAlternativeSelectMethod() {
        return ((Boolean) getAppBehavior("shouldEditModeUseAlternativeSelectMethod", false)).booleanValue();
    }

    public boolean shouldCheckSmileyWhenDeleting() {
        return ((Boolean) getAppBehavior("shouldCheckSmileyWhenDeleting", false)).booleanValue();
    }

    public boolean shouldSkipWrongStartInputView() {
        return ((Boolean) getAppBehavior("shouldSkipWrongStartInputView", false)).booleanValue() && this.mInputFieldImeOptions == 1140850694 && this.mInputFieldInputType == 131073;
    }

    public boolean shouldMonitoringCursorChange() {
        return ((Boolean) getAppBehavior("shouldMonitoringCursorChange", false)).booleanValue();
    }

    public boolean shouldRestrictGetTextLengthFromCursor() {
        return ((Boolean) getAppBehavior("shouldRestrictGetTextLengthFromCursor", false)).booleanValue();
    }

    public boolean useBackspaceKeyToClearHighlightedText() {
        return ((Boolean) getAppBehavior("useBackspaceKeyToClearHighlightedText", false)).booleanValue();
    }

    public boolean shouldRecaptureForSingleTapWhenFocusInURLField() {
        return ((Boolean) getAppBehavior("shouldRecaptureForSingleTapWhenFocusInURLField", false)).booleanValue();
    }

    public boolean shouldRecaptureForCaseEdit() {
        return ((Boolean) getAppBehavior("shouldRecaptureForCaseEdit", false)).booleanValue();
    }

    public boolean shouldRecaptureForSwypeKey() {
        return ((Boolean) getAppBehavior("shouldRecaptureForSwypeKey", false)).booleanValue();
    }

    public boolean shoudDisablePredictionForEmailAddress() {
        return ((Boolean) getAppBehavior("shoudDisablePredictionForEmailAddress", false)).booleanValue() && this.mInputFieldInputType == 33 && this.mInputFieldImeOptions == 134217733;
    }
}
