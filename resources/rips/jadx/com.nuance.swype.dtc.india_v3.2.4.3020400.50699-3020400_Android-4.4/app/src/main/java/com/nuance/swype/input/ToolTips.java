package com.nuance.swype.input;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.util.LocalizationUtils;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class ToolTips {
    public static final String AUTO_ACCEPT_KEY_PREFIX = "AUTO_ACCEPT_TIP";
    public static final String AUTO_SPACE_KEY_PREFIX = "AUTO_SPACE_TIP";
    public static final String CAPITALIZATION_GESTURING_KEY_PREFIX = "CAPITALIZATION_GESTURING_TIP";
    public static final String DOUBLE_LETTERS_GESTURING_KEY_PREFIX = "DOUBLE_LETTERS_GESTURING_TIP";
    public static final int MAX_TIP_COUNT = 6;
    public static final int MAX_TIP_DISPLAY_COUNT = 3;
    public static final String PUNCTUATION_GESTURING_KEY_PREFIX = "PUNCTUATION_GESTURING_TIP";
    private final IMEApplication imeApp;
    private SwypeDialog mSwypeDialog;
    private final AppPreferences settings;
    private final TipTrigger tipAutoAccept = new TipTrigger(AUTO_ACCEPT_KEY_PREFIX);
    private final TipTrigger tipAutoSpace = new TipTrigger(AUTO_SPACE_KEY_PREFIX);
    private final TipTrigger tipPunctGesture = new TipTrigger(PUNCTUATION_GESTURING_KEY_PREFIX);
    private final TipTrigger tipDoubleLettersGesture = new TipTrigger(DOUBLE_LETTERS_GESTURING_KEY_PREFIX);
    private final TipTrigger tipCapitalGesture = new TipTrigger(CAPITALIZATION_GESTURING_KEY_PREFIX);

    public static ToolTips from(Context context) {
        return IMEApplication.from(context).getToolTips();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ToolTips(IMEApplication imeApp) {
        this.imeApp = imeApp;
        this.settings = imeApp.getAppPreferences();
    }

    public void dimissTip() {
        if (this.mSwypeDialog != null) {
            this.mSwypeDialog.dismiss();
            this.mSwypeDialog = null;
        }
        QuickToast.hide();
    }

    public void triggerAutoAcceptTip(View parent) {
        if (this.settings.showToolTip() && this.tipAutoAccept.trigger()) {
            showTip(parent, getString(R.string.tips_default_word_select));
        }
    }

    public void triggerAutoSpaceTip(View parent) {
        if (this.settings.showToolTip() && this.tipAutoSpace.trigger()) {
            showTip(parent, getString(R.string.tips_autospace));
        }
    }

    public void triggerPunctGestureTip(View parent) {
        if (this.settings.showToolTip() && this.tipPunctGesture.trigger()) {
            showTip(parent, getString(R.string.tips_punctuation));
        }
    }

    public void triggerDoubleLettersGestureTip(View parent) {
        if (this.settings.showToolTip() && this.tipDoubleLettersGesture.trigger()) {
            showTip(parent, getString(R.string.tips_double_letters));
        }
    }

    public void triggerCaptitalizationGestureTip(View parent) {
        if (this.settings.showToolTip() && this.tipCapitalGesture.trigger()) {
            showTip(parent, getString(R.string.tips_auto_caps));
        }
    }

    public void triggerPasswordTip(View parent) {
        if (this.settings.showToolTip() && !this.settings.isPasswordTipAlreadyShown()) {
            this.settings.setPassWordTipShown();
            showTip(parent, getString(R.string.tips_password));
        }
    }

    public void triggerEditGestureTip(View parent) {
        if (this.settings.showToolTip() && this.settings.isShowEditGestureTip()) {
            String gesturetipUrl = LocalizationUtils.getHtmlFileUrl(getString(R.string.gesturetip_file), this.imeApp);
            showHtmlTip(parent, gesturetipUrl);
        }
    }

    public Candidates createNoMatchTip() {
        Candidates candidates = new Candidates(Candidates.Source.TOOL_TIP);
        candidates.add(new WordCandidate(getString(R.string.tips_hwcl_no_match)));
        return candidates;
    }

    public Candidates createSwypeKeyTip() {
        String swypeKeyTip = getString(R.string.tips_hwcl_swype_key);
        if (ThemeManager.isDownloadableThemesEnabled()) {
            swypeKeyTip = getString(R.string.press_hod_swype_key_for_swype_store);
        }
        Candidates candidates = new Candidates(Candidates.Source.TOOL_TIP);
        Pattern iconToken = Pattern.compile("%%.*%%");
        if (!TextUtils.isEmpty(swypeKeyTip)) {
            String[] words = TextUtils.split(swypeKeyTip, iconToken);
            for (int i = 0; i < words.length - 1; i++) {
                if (!TextUtils.isEmpty(words[i])) {
                    candidates.add(new WordCandidate(words[i]));
                }
                candidates.add(new DrawableCandidate(this.imeApp.getThemedDrawable(R.attr.hwclSwypeKeyIcon)));
            }
            if (words.length > 0 && !TextUtils.isEmpty(words[words.length - 1])) {
                candidates.add(new WordCandidate(words[words.length - 1]));
            }
        }
        return candidates;
    }

    protected void showTip(View parent, String text) {
        if (this.imeApp != null && !this.imeApp.getIME().isHardKeyboardActive()) {
            QuickToast.show(parent.getContext(), text, 1, parent.getHeight());
        }
    }

    protected void showHtmlTip(View parent, String url) {
        if (this.imeApp != null && !this.imeApp.getIME().isHardKeyboardActive()) {
            if (this.mSwypeDialog != null) {
                this.mSwypeDialog.dismiss();
                this.mSwypeDialog = null;
            }
            this.mSwypeDialog = new HtmlTipDialog(url, null, null);
            Context context = parent.getContext();
            this.mSwypeDialog.createDialog(context);
            this.mSwypeDialog.show(parent.getWindowToken(), context);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class TipTrigger extends AbstractTipTrigger {
        public static final String DISPLAY_COUNT_SUFFIX = "_DISPLAY_COUNT";
        public static final String TIP_COUNT_SUFFIX = "_COUNT";
        public static final String TRIGGER_POINT_SUFFIX = "_TRIGGER_POINT";
        private static final int maxDisplayCount = 3;
        private static final int maxTipCount = 6;
        private final String keyDisplayCount;
        private final String keyTipCount;

        protected TipTrigger(String keyPrefix) {
            this.keyDisplayCount = keyPrefix + DISPLAY_COUNT_SUFFIX;
            this.keyTipCount = keyPrefix + TIP_COUNT_SUFFIX;
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected int getDisplayCount() {
            return ToolTips.this.getInt(this.keyDisplayCount, 0);
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected void incrementDisplayCount() {
            ToolTips.this.setInt(this.keyDisplayCount, getDisplayCount() + 1);
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected int getMaxDisplayCount() {
            return 3;
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected int incrementAndGetCount() {
            int count = ToolTips.this.getInt(this.keyTipCount, 0) + 1;
            ToolTips.this.setInt(this.keyTipCount, count);
            return count;
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected void resetCount() {
            ToolTips.this.setInt(this.keyTipCount, 0);
        }

        @Override // com.nuance.swype.input.AbstractTipTrigger
        protected int getTriggerPoint() {
            return 6;
        }
    }

    protected int getInt(String key, int defval) {
        return this.imeApp.getSharedPreferences("TOOL_TIPS", 0).getInt(key, defval);
    }

    protected void setInt(String key, int value) {
        SharedPreferencesEditorCompat.apply(this.imeApp.getSharedPreferences("TOOL_TIPS", 0).edit().putInt(key, value));
    }

    protected String getString(int resId) {
        return this.imeApp.getString(resId);
    }

    protected Drawable getDrawable(int resId) {
        return this.imeApp.getResources().getDrawable(resId);
    }
}
