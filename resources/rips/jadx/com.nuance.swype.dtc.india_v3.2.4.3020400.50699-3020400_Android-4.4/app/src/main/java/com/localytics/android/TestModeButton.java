package com.localytics.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import com.localytics.android.Localytics;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(11)
/* loaded from: classes.dex */
public final class TestModeButton extends DialogFragment {
    static final String TEST_MODE_BUTTON_TAG = "marketing_test_mode_button";
    private Map<Integer, MarketingCallable> mCallbacks;
    private final AtomicBoolean mEnterAnimatable = new AtomicBoolean(true);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TestModeButton newInstance() {
        TestModeButton fragment = new TestModeButton();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override // android.app.Fragment
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeButton]: onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // android.app.Fragment
    public final void onViewStateRestored(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeButton]: onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override // android.app.Fragment
    public final void onResume() {
        Localytics.Log.d("[TestModeButton]: onResume");
        super.onResume();
    }

    @Override // android.app.Fragment
    public final void onPause() {
        Localytics.Log.d("[TestModeButton]: onPause");
        super.onPause();
    }

    @Override // android.app.Fragment
    public final void onDestroy() {
        Localytics.Log.d("[TestModeButton]: onDestroy");
        super.onDestroy();
    }

    @Override // android.app.DialogFragment
    public final void show(FragmentManager manager, String tag) {
        this.mEnterAnimatable.set(true);
        super.show(manager, tag);
    }

    @Override // android.app.Fragment
    public final void onAttach(Activity activity) {
        Localytics.Log.d("[TestModeButton]: onAttach");
        super.onAttach(activity);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDetach() {
        Localytics.Log.d("[TestModeButton]: onDetach");
        super.onDetach();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onCreate(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeButton]: onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.DialogFragment
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeButton]: onCreateDialog");
        return new TestModeDialog(getActivity(), android.R.style.Theme.Dialog);
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialog) {
        Localytics.Log.d("[TestModeButton]: onDismiss");
        super.onDismiss(dialog);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onActivityCreated(Bundle arg0) {
        Localytics.Log.d("[TestModeButton]: onActivityCreated");
        super.onActivityCreated(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStart() {
        Localytics.Log.d("[TestModeButton]: onStart");
        super.onStart();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onSaveInstanceState(Bundle arg0) {
        Localytics.Log.d("[TestModeButton]: onSaveInstanceState");
        super.onSaveInstanceState(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStop() {
        Localytics.Log.d("[TestModeButton]: onStop");
        super.onStop();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDestroyView() {
        Localytics.Log.d("[TestModeButton]: onDestroyView");
        super.onDestroyView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TestModeButton setCallbacks(Map<Integer, MarketingCallable> callbacks) {
        this.mCallbacks = callbacks;
        return this;
    }

    /* loaded from: classes.dex */
    final class TestModeDialog extends Dialog {
        private static final int X_OFFSET = 0;
        private static final int Y_OFFSET = 85;
        private TranslateAnimation mAnimIn;
        private TranslateAnimation mAnimOut;
        private RelativeLayout mDialogLayout;
        private DisplayMetrics mMetrics;

        public TestModeDialog(Context context, int theme) {
            super(context, theme);
            setupViews();
            createAnimations();
            adjustLayout();
        }

        private void setupViews() {
            this.mDialogLayout = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(13);
            this.mDialogLayout.setLayoutParams(params);
            Button button = new Button(getContext(), null);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.localytics.android.TestModeButton.TestModeDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    MarketingCallable callable;
                    TestModeDialog.this.mDialogLayout.startAnimation(TestModeDialog.this.mAnimOut);
                    if (TestModeButton.this.mCallbacks != null && (callable = (MarketingCallable) TestModeButton.this.mCallbacks.get(8)) != null) {
                        callable.call(null);
                    }
                }
            });
            this.mDialogLayout.addView(button);
            requestWindowFeature(1);
            setContentView(this.mDialogLayout);
        }

        private void createAnimations() {
            this.mAnimIn = new TranslateAnimation(2, -1.0f, 2, 0.0f, 2, 0.0f, 2, 0.0f);
            this.mAnimIn.setDuration(250L);
            this.mAnimOut = new TranslateAnimation(2, 0.0f, 2, -1.0f, 2, 0.0f, 2, 0.0f);
            this.mAnimOut.setDuration(250L);
            this.mAnimOut.setAnimationListener(new Animation.AnimationListener() { // from class: com.localytics.android.TestModeButton.TestModeDialog.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    TestModeButton.this.dismiss();
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        @SuppressLint({"NewApi"})
        private void adjustLayout() {
            this.mMetrics = new DisplayMetrics();
            ((WindowManager) TestModeButton.this.getActivity().getSystemService("window")).getDefaultDisplay().getMetrics(this.mMetrics);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.setGravity(51);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = (int) TypedValue.applyDimension(1, 0.0f, this.mMetrics);
            params.y = (int) TypedValue.applyDimension(1, 85.0f, this.mMetrics);
            params.dimAmount = 0.0f;
            window.setAttributes(params);
            window.clearFlags(2);
            window.setFlags(32, 32);
            if (TestModeButton.this.mEnterAnimatable.getAndSet(false)) {
                this.mDialogLayout.startAnimation(this.mAnimIn);
            }
            window.setFlags(1024, 1024);
        }

        @Override // android.app.Dialog, android.view.KeyEvent.Callback
        public final boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                this.mDialogLayout.startAnimation(this.mAnimOut);
                Constants.setTestModeEnabled(false);
            }
            return super.onKeyDown(keyCode, event);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class Button extends View {
            private static final int HEIGHT = 60;
            private static final int WIDTH = 60;
            private final int BACKGROUND_COLOR;
            private final int BAR1_COLOR;
            private final int BAR2_COLOR;
            private final int BAR3_COLOR;
            private final int OVAL_COLOR;
            private final Paint mPaint;
            private final RectF mRectF;

            Button(Context context, AttributeSet attrs) {
                super(context, attrs);
                this.BACKGROUND_COLOR = Color.argb(255, 51, 51, 51);
                this.BAR1_COLOR = Color.argb(255, com.nuance.swype.input.R.styleable.ThemeTemplate_hwclTranslitHiHindi, com.nuance.swype.input.R.styleable.ThemeTemplate_hwclTranslitHiHindi, com.nuance.swype.input.R.styleable.ThemeTemplate_hwclTranslitHiHindi);
                this.BAR2_COLOR = Color.argb(255, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackground, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackground, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackground);
                this.BAR3_COLOR = Color.argb(255, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackgroundHwrContainer, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackgroundHwrContainer, com.nuance.swype.input.R.styleable.ThemeTemplate_keyboardBackgroundHwrContainer);
                this.OVAL_COLOR = Color.argb(255, 70, 70, 70);
                if (DatapointHelper.getApiLevel() >= 19) {
                    setLayerType(1, null);
                }
                float dip = getResources().getDisplayMetrics().density;
                this.mRectF = new RectF(55.0f * dip, 0.0f, 65.0f * dip, 60.0f * dip);
                setLayoutParams(new RelativeLayout.LayoutParams((int) ((65.0f * dip) + 0.5f), (int) ((60.0f * dip) + 0.5f)));
                this.mPaint = new Paint(1);
                this.mPaint.setColor(this.BACKGROUND_COLOR);
                this.mPaint.setStyle(Paint.Style.FILL);
            }

            @Override // android.view.View
            protected final void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float dip = getResources().getDisplayMetrics().density;
                this.mPaint.setColor(this.BACKGROUND_COLOR);
                canvas.drawRect(0.0f, 0.0f, 60.0f * dip, 60.0f * dip, this.mPaint);
                canvas.drawRoundRect(this.mRectF, 5.0f * dip, 5.0f * dip, this.mPaint);
                this.mPaint.setColor(this.BAR1_COLOR);
                canvas.drawRect(7.0f * dip, 42.0f * dip, 13.0f * dip, 48.0f * dip, this.mPaint);
                this.mPaint.setColor(this.BAR2_COLOR);
                canvas.drawRect(16.0f * dip, 36.0f * dip, 22.0f * dip, 48.0f * dip, this.mPaint);
                this.mPaint.setColor(this.BAR3_COLOR);
                canvas.drawRect(25.0f * dip, 27.0f * dip, 31.0f * dip, 48.0f * dip, this.mPaint);
                this.mPaint.setColor(-1);
                canvas.drawRect(34.0f * dip, 12.0f * dip, 40.0f * dip, 48.0f * dip, this.mPaint);
                this.mPaint.setColor(-1);
                canvas.drawRect(37.0f * dip, 42.0f * dip, 55.0f * dip, 48.0f * dip, this.mPaint);
                this.mPaint.setColor(this.OVAL_COLOR);
                canvas.drawOval(new RectF(59.0f * dip, 40.0f * dip, 63.0f * dip, 44.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(59.0f * dip, 30.0f * dip, 63.0f * dip, 34.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(59.0f * dip, 20.0f * dip, 63.0f * dip, 24.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(59.0f * dip, 10.0f * dip, 63.0f * dip, 14.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(54.0f * dip, 35.0f * dip, 58.0f * dip, 39.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(54.0f * dip, 25.0f * dip, 58.0f * dip, 29.0f * dip), this.mPaint);
                canvas.drawOval(new RectF(54.0f * dip, 15.0f * dip, 58.0f * dip, 19.0f * dip), this.mPaint);
            }
        }
    }
}
