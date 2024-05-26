package com.nuance.swype.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class ViewClickPreference extends Preference implements View.OnClickListener {
    private boolean focusable;
    public View mCurrentView;
    public View.OnKeyListener mKeyListener;
    private ViewClickPreferenceListener mViewClickPreferenceListener;
    private boolean setFocus;

    /* loaded from: classes.dex */
    public interface ViewClickPreferenceListener {
        void onViewClick(Preference preference);
    }

    public ViewClickPreference(Context context) {
        super(context);
        this.setFocus = false;
        this.focusable = false;
    }

    public ViewClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocus = false;
        this.focusable = false;
    }

    public ViewClickPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setFocus = false;
        this.focusable = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        this.mCurrentView = view;
        if (this.mViewClickPreferenceListener != null && this.mCurrentView != null) {
            this.mCurrentView.setOnClickListener(this);
        }
        if (this.setFocus) {
            setFocusable(this.focusable);
        }
        if (this.mKeyListener != null && this.mCurrentView != null) {
            this.mCurrentView.setOnKeyListener(this.mKeyListener);
        }
    }

    public final void setListener(ViewClickPreferenceListener listener) {
        this.mViewClickPreferenceListener = listener;
        if (this.mCurrentView != null) {
            this.mCurrentView.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mViewClickPreferenceListener != null) {
            this.mViewClickPreferenceListener.onViewClick(this);
        }
    }

    public final void setFocusable(boolean focusable) {
        if (this.mCurrentView != null) {
            this.mCurrentView.setFocusable(focusable);
        } else {
            this.setFocus = true;
            this.focusable = focusable;
        }
    }
}
