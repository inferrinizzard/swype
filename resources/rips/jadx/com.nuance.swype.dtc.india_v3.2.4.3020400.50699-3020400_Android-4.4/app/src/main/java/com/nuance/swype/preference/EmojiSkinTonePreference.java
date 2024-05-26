package com.nuance.swype.preference;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.SkinToneView;
import com.nuance.swype.input.emoji.util.Util;
import com.nuance.swype.input.settings.SettingsPrefs;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class EmojiSkinTonePreference extends Preference implements View.OnClickListener {
    private static final LogManager.Log log = LogManager.getLog("EmojiSkinTonePreference");
    private int emoji_scale;
    private boolean focusable;
    private Context mContext;
    public View mCurrentView;
    private View.OnKeyListener mKeyListener;
    public ViewClickPreferenceListener mViewClickPreferenceListener;
    private boolean setFocus;
    private SkinToneView tv_emoji_skin_tone;

    /* loaded from: classes.dex */
    public interface ViewClickPreferenceListener {
        void onViewClick(Preference preference);
    }

    public EmojiSkinTonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocus = false;
        this.focusable = false;
        this.emoji_scale = 20;
        setWidgetLayoutResource(R.layout.emoji_skin_tone_pref);
        this.mContext = context;
    }

    @Override // android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        this.tv_emoji_skin_tone = (SkinToneView) view.findViewById(R.id.emoji_skin_tone);
        setEmojiText(SettingsPrefs.getEmojiSkinToneCode(getContext()));
        this.mCurrentView = view;
        if (this.mViewClickPreferenceListener != null) {
            this.mCurrentView.setOnClickListener(this);
        }
        if (this.setFocus) {
            boolean z = this.focusable;
            if (this.mCurrentView != null) {
                this.mCurrentView.setFocusable(z);
            } else {
                this.setFocus = true;
                this.focusable = z;
            }
        }
        if (this.mKeyListener != null && this.mCurrentView != null) {
            this.mCurrentView.setOnKeyListener(this.mKeyListener);
        }
    }

    public final void setEmojiText(String emojiSkinToneCode) {
        log.d("setEmojiText()", " called >>>>> emojiSkinToneCode =" + emojiSkinToneCode);
        if (this.tv_emoji_skin_tone == null) {
            return;
        }
        Resources resources = this.mContext.getResources();
        float convertDpToPixel = Util.convertDpToPixel(this.emoji_scale);
        int dimension = (int) resources.getDimension(R.dimen.skin_tone_xpos_in_preference);
        int dimension2 = (int) resources.getDimension(R.dimen.skin_tone_ypos_in_preference);
        int dimension3 = (int) resources.getDimension(R.dimen.skin_tone_height_in_preference);
        this.tv_emoji_skin_tone.setPosition(dimension, dimension2);
        this.tv_emoji_skin_tone.setHeight(dimension3);
        this.tv_emoji_skin_tone.setPaintTextSize(convertDpToPixel);
        this.tv_emoji_skin_tone.setTextValue(emojiSkinToneCode);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mViewClickPreferenceListener != null) {
            this.mViewClickPreferenceListener.onViewClick(this);
        }
    }
}
