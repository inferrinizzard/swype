package com.nuance.swype.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.nuance.swype.input.ads.BillboardManager;
import com.nuance.swype.util.AdsUtil;

/* loaded from: classes.dex */
public class WordListViewContainer extends LinearLayout {
    ImageButton toggle;

    public WordListViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (AdsUtil.sAdsSupported) {
            setupAdsView();
        }
    }

    public void setupAdsView() {
        IMEApplication.from(getContext()).getBillboardManager().getBillboard().setup((ViewGroup) findViewById(R.id.billboard));
    }

    private ImageButton getToggleButton() {
        if (this.toggle == null) {
            this.toggle = (ImageButton) findViewById(R.id.toggle);
        }
        return this.toggle;
    }

    public void showTransliterationToggleButton(IME ime) {
        IMEApplication imeApp = IMEApplication.from(getContext());
        this.toggle = getToggleButton();
        this.toggle.setVisibility(0);
        int toggleButtonColor = imeApp.getThemedColor(R.attr.candidateOther);
        this.toggle.setColorFilter(toggleButtonColor);
        if (ime.mCurrentInputLanguage.getCurrentInputMode().mInputMode.equals(InputMethods.KEYBOARD_TRANSLITERATION)) {
            this.toggle.setImageResource(R.drawable.wcl_icon_translit_hindiqwerty_black);
            this.toggle.setBackground(imeApp.getThemedDrawable(R.attr.iconTranslitHiQwerty));
        } else {
            this.toggle.setImageResource(R.drawable.wcl_icon_translit_hindi_black);
            this.toggle.setBackground(imeApp.getThemedDrawable(R.attr.iconTranslitHiHindi));
        }
    }

    public void hideTransliterationToggleButton() {
        this.toggle = getToggleButton();
        this.toggle.setVisibility(8);
    }

    public void updateView() {
        if (AdsUtil.sAdsSupported) {
            BillboardManager bm = IMEApplication.from(getContext()).getBillboardManager();
            if (bm.canShowBillboard()) {
                bm.showView();
                bm.getBillboard().loadNewAd();
            } else {
                bm.hide();
            }
        }
        if (!UserPreferences.from(getContext()).isEmojiChoiceListEnabled()) {
            findViewById(R.id.emojilist_holder).setVisibility(8);
        }
    }
}
