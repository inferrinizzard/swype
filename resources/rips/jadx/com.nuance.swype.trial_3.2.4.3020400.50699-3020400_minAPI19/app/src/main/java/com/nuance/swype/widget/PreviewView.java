package com.nuance.swype.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.accessibility.AccessibilityNotification;

/* loaded from: classes.dex */
public class PreviewView extends FrameLayout {
    ImageView imageView;
    private boolean mEnabledForAccessibility;
    SimpleTextView textView;

    public PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.preview_view, this);
        this.imageView = (ImageView) findViewById(R.id.image);
        this.textView = (SimpleTextView) findViewById(R.id.text);
    }

    public void setTextSizePixels(float size) {
        this.textView.setTextSize(0, size);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTypeface(Typeface tf) {
        this.textView.setTypeface(tf);
    }

    public void setText(CharSequence text) {
        this.textView.setText(text);
        this.textView.setVisibility(0);
        this.imageView.setVisibility(8);
    }

    public CharSequence getText() {
        return this.textView.getText();
    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable instanceof StateListDrawable) {
            this.imageView.setImageDrawable(((StateListDrawable) drawable).getCurrent());
        } else {
            this.imageView.setImageDrawable(drawable);
        }
        this.imageView.setVisibility(0);
        this.textView.setVisibility(8);
    }

    public void setEnableAccessibilitySupport(boolean enabled) {
        this.mEnabledForAccessibility = enabled;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0 && this.mEnabledForAccessibility) {
            AccessibilityNotification.getInstance().speak(getContext(), getContentDescription());
        }
    }
}
