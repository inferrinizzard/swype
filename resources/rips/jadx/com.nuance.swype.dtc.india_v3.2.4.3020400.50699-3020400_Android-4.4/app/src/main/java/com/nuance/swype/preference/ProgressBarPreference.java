package com.nuance.swype.preference;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ProgressBarPreference extends Preference {
    private static final LogManager.Log log = LogManager.getLog("ProgressBarPreference");
    private AttributeSet attrs;
    public ImageButton cancel;
    public OnCancelListener cancelListener;
    private int lastReqMax;
    private int lastReqProgress;
    private ProgressBar mProgressBar;

    /* loaded from: classes.dex */
    public interface OnCancelListener {
        void onCancel(ProgressBarPreference progressBarPreference);
    }

    public ProgressBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.lastReqProgress = -1;
        this.lastReqMax = -1;
        this.attrs = attrs;
        this.mProgressBar = createProgressBar(getContext(), attrs);
    }

    private static ProgressBar createProgressBar(Context ctx, AttributeSet attrs) {
        ProgressBar progressBar = new ProgressBar(ctx, attrs, R.style.Widget.ProgressBar.Horizontal);
        Resources res = ctx.getResources();
        Drawable d = res.getDrawable(com.nuance.swype.input.R.drawable.progress_indeterminate_horizontal);
        if (d instanceof AnimationDrawable) {
            AnimationDrawable ad = (AnimationDrawable) d;
            for (int iFrame = 0; iFrame < ad.getNumberOfFrames(); iFrame++) {
                LayerDrawable ld = (LayerDrawable) ad.getFrame(iFrame);
                for (int iLayer = 0; iLayer < ld.getNumberOfLayers(); iLayer++) {
                    Drawable bd = ld.getDrawable(iLayer);
                    if (bd instanceof BitmapDrawable) {
                        bd.mutate();
                        ((BitmapDrawable) bd).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                    }
                }
            }
        }
        progressBar.setIndeterminateDrawable(d);
        progressBar.setIndeterminate(true);
        progressBar.setProgressDrawable(res.getDrawable(com.nuance.swype.input.R.drawable.progress_bar));
        return progressBar;
    }

    @Override // android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        try {
            ViewParent oldContainer = this.mProgressBar.getParent();
            View summary = view.findViewById(R.id.summary);
            ViewGroup newContainer = (ViewGroup) summary.getParent();
            if (oldContainer != newContainer) {
                if (oldContainer != null) {
                    this.mProgressBar = createProgressBar(getContext(), this.attrs);
                }
                this.cancel = new ImageButton(view.getContext());
                if (this.cancelListener != null) {
                    this.cancel.setBackgroundResource(0);
                    this.cancel.setImageResource(com.nuance.swype.input.R.drawable.icon_settings_cancel);
                    this.cancel.setId(999);
                    this.cancel.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.preference.ProgressBarPreference.1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View v) {
                            ProgressBarPreference.this.cancelListener.onCancel(ProgressBarPreference.this);
                        }
                    });
                    RelativeLayout.LayoutParams cancelParams = new RelativeLayout.LayoutParams(summary.getLayoutParams());
                    cancelParams.addRule(11);
                    cancelParams.addRule(15);
                    cancelParams.height = -2;
                    cancelParams.width = -2;
                    newContainer.addView(this.cancel, cancelParams);
                }
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) summary.getLayoutParams();
                params.width = -1;
                params.height = (int) getContext().getResources().getDimension(com.nuance.swype.input.R.dimen.progress_bar_preference_height);
                params.addRule(0, this.cancel.getId());
                newContainer.addView(this.mProgressBar, params);
                summary.setVisibility(8);
            }
        } catch (Exception ex) {
            log.d("onBindView", ex);
        }
        setProgress(this.lastReqProgress);
        setMax(this.lastReqMax);
    }

    public final void setProgress(int value) {
        if (this.mProgressBar != null) {
            if (value > 0 && this.mProgressBar.isIndeterminate()) {
                this.mProgressBar.setIndeterminate(false);
            }
            this.mProgressBar.setProgress(value);
        }
        this.lastReqProgress = value;
    }

    public final void setMax(int value) {
        if (this.mProgressBar != null) {
            int savedProgress = this.mProgressBar.getProgress();
            this.mProgressBar.setMax(0);
            this.mProgressBar.setMax(value);
            this.mProgressBar.setProgress(savedProgress);
        }
        this.lastReqMax = value;
    }
}
