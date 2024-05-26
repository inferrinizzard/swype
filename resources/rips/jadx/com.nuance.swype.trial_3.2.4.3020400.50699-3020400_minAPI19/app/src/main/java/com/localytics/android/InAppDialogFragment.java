package com.localytics.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.localytics.android.Localytics;
import com.localytics.android.MarketingWebView;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(11)
/* loaded from: classes.dex */
public final class InAppDialogFragment extends DialogFragment implements Handler.Callback {
    private static final String AMP_DESCRIPTION = "amp_view";
    private static final String ARG_CAMPAIGN = "arg_campaign";
    private static final String CLOSE_BUTTON_DESCRIPTION = "close_button";
    private static final int CLOSE_BUTTON_ID = 1;
    static final String DIALOG_TAG = "marketing_dialog";
    private static final int WEB_VIEW_ID = 2;
    private static Bitmap sDismissButtonImage = null;
    private static Localytics.InAppMessageDismissButtonLocation sDismissButtonLocation = Localytics.InAppMessageDismissButtonLocation.LEFT;
    private InAppDialogCallback mCallback;
    private InAppCampaign mCampaign;
    private final AtomicBoolean mEnterAnimatable = new AtomicBoolean(true);
    private final AtomicBoolean mExitAnimatable = new AtomicBoolean(true);
    private InAppDialog mInAppDialog;
    private JavaScriptClient mJavaScriptClient;
    private MessagingListener mListener;
    private Handler mMessageHandler;
    private MarketingWebViewManager mWebViewManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InAppDialogFragment newInstance(InAppCampaign campaign) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CAMPAIGN, campaign);
        InAppDialogFragment fragment = new InAppDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setDismissButtonImage(Bitmap image) {
        if (sDismissButtonImage != null) {
            sDismissButtonImage.recycle();
        }
        sDismissButtonImage = image;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Localytics.InAppMessageDismissButtonLocation getInAppDismissButtonLocation() {
        return sDismissButtonLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setInAppDismissButtonLocation(Localytics.InAppMessageDismissButtonLocation buttonLocation) {
        sDismissButtonLocation = buttonLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dismissCampaign() {
        if (this.mInAppDialog != null) {
            if (this.mCampaign != null) {
                this.mWebViewManager.tagMarketingActionEventWithAction("X");
            }
            this.mInAppDialog.dismiss();
        }
    }

    @Override // android.app.Fragment
    public final void onAttach(Activity activity) {
        Localytics.Log.v("[InAppDialogFragment]: onAttach");
        super.onAttach(activity);
        if (this.mWebViewManager == null) {
            this.mWebViewManager = new MarketingWebViewManager(LocalyticsManager.getInstance());
        }
        this.mWebViewManager.setContext(activity);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onCreate(Bundle savedInstanceState) {
        Localytics.Log.v("[InAppDialogFragment]: onCreate");
        super.onCreate(savedInstanceState);
        this.mMessageHandler = new Handler(this);
        this.mWebViewManager.setMessageHandler(this.mMessageHandler);
        setRetainInstance(true);
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case 1:
                    this.mInAppDialog.dismissWithAnimation();
                    break;
                case 2:
                    this.mInAppDialog.mWebView.loadUrl((String) msg.obj);
                    break;
            }
        } catch (Exception e) {
            Localytics.Log.e(String.format("Main handler can't handle message %s", String.valueOf(msg.what)), e);
        }
        return true;
    }

    @Override // android.app.Fragment
    public final void onViewStateRestored(Bundle savedInstanceState) {
        Localytics.Log.v("[InAppDialogFragment]: onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override // android.app.DialogFragment
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        Localytics.Log.v("[InAppDialogFragment]: onCreateDialog");
        if (getArguments() != null) {
            this.mCampaign = (InAppCampaign) getArguments().getParcelable(ARG_CAMPAIGN);
            this.mWebViewManager.setCampaign(this.mCampaign);
            if (this.mCampaign != null) {
                this.mJavaScriptClient = this.mWebViewManager.getJavaScriptClient(this.mCampaign.getEventAttributes());
            }
        }
        InAppDialog inAppDialog = new InAppDialog(getActivity(), android.R.style.Theme.Dialog);
        this.mInAppDialog = inAppDialog;
        return inAppDialog;
    }

    @Override // android.app.Fragment
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Localytics.Log.v("[InAppDialogFragment]: onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onActivityCreated(Bundle arg0) {
        Localytics.Log.v("[InAppDialogFragment]: onActivityCreated");
        super.onActivityCreated(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStart() {
        Localytics.Log.v("[InAppDialogFragment]: onStart");
        super.onStart();
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialog) {
        Localytics.Log.v("[InAppDialogFragment]: onDismiss");
        try {
            if (this.mCampaign != null) {
                this.mWebViewManager.tagMarketingActionEventWithAction("X");
            }
        } catch (Exception e) {
            Localytics.Log.e("MarketingDialogFragment onDismiss", e);
        }
        super.onDismiss(dialog);
    }

    @Override // android.app.Fragment
    public final void onResume() {
        Localytics.Log.v("[InAppDialogFragment]: onResume");
        super.onResume();
    }

    @Override // android.app.Fragment
    public final void onPause() {
        Localytics.Log.v("[InAppDialogFragment]: onPause");
        super.onPause();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStop() {
        Localytics.Log.v("[InAppDialogFragment]: onStop");
        super.onStop();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onSaveInstanceState(Bundle arg0) {
        Localytics.Log.v("[InAppDialogFragment]: onSaveInstanceState");
        super.onSaveInstanceState(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDestroyView() {
        Localytics.Log.v("[InAppDialogFragment]: onDestroyView");
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override // android.app.Fragment
    public final void onDestroy() {
        Localytics.Log.v("[InAppDialogFragment]: onDestroy");
        if (this.mListener != null && !Constants.isTestModeEnabled()) {
            this.mListener.localyticsDidDismissInAppMessage();
        }
        if (this.mCallback != null) {
            this.mCallback.doneDisplayingCampaign();
        }
        super.onDestroy();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDetach() {
        Localytics.Log.v("[InAppDialogFragment]: onDetach");
        super.onDetach();
        this.mWebViewManager.setContext(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final InAppDialogFragment setMessagingListener(MessagingListener listener) {
        this.mListener = listener;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final InAppDialogFragment setDialogCallback(InAppDialogCallback callback) {
        this.mCallback = callback;
        return this;
    }

    /* loaded from: classes.dex */
    final class InAppDialog extends Dialog {
        private static final String LOCATION_BOTTOM = "bottom";
        private static final String LOCATION_CENTER = "center";
        private static final String LOCATION_FULL = "full";
        private static final String LOCATION_TOP = "top";
        private static final int MARGIN = 10;
        private static final int MAX_BANNER_WIDTH_DIP = 360;
        private TranslateAnimation mAnimBottomIn;
        private TranslateAnimation mAnimBottomOut;
        private TranslateAnimation mAnimCenterIn;
        private TranslateAnimation mAnimCenterOut;
        private TranslateAnimation mAnimFullIn;
        private TranslateAnimation mAnimFullOut;
        private TranslateAnimation mAnimTopIn;
        private TranslateAnimation mAnimTopOut;
        private CloseButton mBtnClose;
        private RelativeLayout mDialogLayout;
        private float mHeight;
        private DisplayMetrics mMetrics;
        private RelativeLayout mRootLayout;
        private MarketingWebView mWebView;
        private float mWidth;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class InAppWebViewClient extends MarketingWebView.MarketingWebViewClient {
            InAppWebViewClient(MarketingWebViewManager manager) {
                super(manager);
            }

            @Override // android.webkit.WebViewClient
            public final void onPageFinished(WebView view, String url) {
                int margin = InAppDialogFragment.this.mCampaign.getDisplayLocation().equals(InAppDialog.LOCATION_CENTER) ? ((int) ((10.0f * InAppDialog.this.mMetrics.density) + 0.5f)) << 1 : 0;
                int maxWidth = Math.min(InAppDialog.this.mMetrics.widthPixels, InAppDialog.this.mMetrics.heightPixels) - margin;
                int maxHeight = Math.max(InAppDialog.this.mMetrics.widthPixels, InAppDialog.this.mMetrics.heightPixels) - margin;
                float viewportWidth = Math.min(maxWidth, (int) ((InAppDialog.this.mWidth * InAppDialog.this.mMetrics.density) + 0.5f)) / InAppDialog.this.mMetrics.density;
                float viewportHeight = Math.min(maxHeight, (int) ((InAppDialog.this.mHeight * InAppDialog.this.mMetrics.density) + 0.5f)) / InAppDialog.this.mMetrics.density;
                String javascript = TextUtils.concat(InAppDialogFragment.this.mJavaScriptClient.getViewportAdjuster(viewportWidth, viewportHeight), ";", InAppDialogFragment.this.mJavaScriptClient.getJavaScriptBridge()).toString();
                view.loadUrl(javascript);
                InAppDialog.this.mRootLayout.setVisibility(0);
                if (InAppDialogFragment.this.mEnterAnimatable.getAndSet(false)) {
                    InAppDialog.this.enterWithAnimation();
                }
            }
        }

        InAppDialog(Context context, int theme) {
            super(context, theme);
            setupViews();
            createAnimations();
            adjustLayout();
            String url = InAppDialogFragment.this.mCampaign.getWebViewAttributes().get("html_url");
            if (!TextUtils.isEmpty(url)) {
                this.mWebView.loadUrl(url);
            }
        }

        private void setupViews() {
            this.mRootLayout = new RelativeLayout(getContext());
            this.mRootLayout.setVisibility(4);
            this.mRootLayout.setContentDescription(InAppDialogFragment.AMP_DESCRIPTION);
            this.mRootLayout.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
            this.mDialogLayout = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(13);
            this.mDialogLayout.setLayoutParams(params);
            this.mRootLayout.addView(this.mDialogLayout);
            LinearLayout.LayoutParams webViewParams = new LinearLayout.LayoutParams(-1, -1);
            webViewParams.gravity = 17;
            this.mWebView = new MarketingWebView(getContext(), webViewParams);
            this.mWebView.setId(2);
            this.mWebView.setHorizontalScrollBarEnabled(false);
            this.mWebView.setVerticalScrollBarEnabled(false);
            this.mWebView.setWebViewClient(new InAppWebViewClient(InAppDialogFragment.this.mWebViewManager));
            this.mWebView.addJavascriptInterface(InAppDialogFragment.this.mJavaScriptClient, "localytics");
            if (DatapointHelper.getApiLevel() >= 19) {
                this.mWebView.setLayerType(1, null);
            }
            this.mDialogLayout.addView(this.mWebView);
            this.mBtnClose = new CloseButton(getContext(), null);
            this.mBtnClose.setOnClickListener(new View.OnClickListener() { // from class: com.localytics.android.InAppDialogFragment.InAppDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (InAppDialogFragment.this.mExitAnimatable.getAndSet(false)) {
                        InAppDialog.this.dismissWithAnimation();
                    }
                }
            });
            if (InAppDialogFragment.sDismissButtonLocation == Localytics.InAppMessageDismissButtonLocation.RIGHT) {
                RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) this.mBtnClose.getLayoutParams();
                btnParams.addRule(7, this.mWebView.getId());
                this.mBtnClose.setLayoutParams(btnParams);
            }
            this.mDialogLayout.addView(this.mBtnClose);
            requestWindowFeature(1);
            setContentView(this.mRootLayout);
        }

        private void createAnimations() {
            this.mAnimCenterIn = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f);
            this.mAnimCenterIn.setDuration(500L);
            this.mAnimCenterOut = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f);
            this.mAnimCenterOut.setDuration(500L);
            this.mAnimTopIn = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, -1.0f, 2, 0.0f);
            this.mAnimTopIn.setDuration(500L);
            this.mAnimTopOut = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, -1.0f);
            this.mAnimTopOut.setDuration(500L);
            this.mAnimBottomIn = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f);
            this.mAnimBottomIn.setDuration(500L);
            this.mAnimBottomOut = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f);
            this.mAnimBottomOut.setDuration(500L);
            this.mAnimFullIn = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f);
            this.mAnimFullIn.setDuration(500L);
            this.mAnimFullOut = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f);
            this.mAnimFullOut.setDuration(500L);
            Animation.AnimationListener listenerOut = new Animation.AnimationListener() { // from class: com.localytics.android.InAppDialogFragment.InAppDialog.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                    if (InAppDialogFragment.this.mListener != null && !Constants.isTestModeEnabled()) {
                        InAppDialogFragment.this.mListener.localyticsWillDismissInAppMessage();
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    try {
                        InAppDialogFragment.this.dismiss();
                    } catch (Exception e) {
                        Localytics.Log.e("Localytics library threw an uncaught exception", e);
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            };
            this.mAnimCenterOut.setAnimationListener(listenerOut);
            this.mAnimTopOut.setAnimationListener(listenerOut);
            this.mAnimBottomOut.setAnimationListener(listenerOut);
            this.mAnimFullOut.setAnimationListener(listenerOut);
            Animation.AnimationListener listenerIn = new Animation.AnimationListener() { // from class: com.localytics.android.InAppDialogFragment.InAppDialog.3
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    if (InAppDialogFragment.this.mListener != null && !Constants.isTestModeEnabled()) {
                        InAppDialogFragment.this.mListener.localyticsDidDisplayInAppMessage();
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            };
            this.mAnimCenterIn.setAnimationListener(listenerIn);
            this.mAnimTopIn.setAnimationListener(listenerIn);
            this.mAnimBottomIn.setAnimationListener(listenerIn);
            this.mAnimFullIn.setAnimationListener(listenerIn);
        }

        @SuppressLint({"NewApi"})
        private void adjustLayout() {
            this.mMetrics = new DisplayMetrics();
            ((WindowManager) InAppDialogFragment.this.getActivity().getSystemService("window")).getDefaultDisplay().getMetrics(this.mMetrics);
            this.mWidth = Float.parseFloat(InAppDialogFragment.this.mCampaign.getWebViewAttributes().get("display_width"));
            this.mHeight = Float.parseFloat(InAppDialogFragment.this.mCampaign.getWebViewAttributes().get("display_height"));
            float aspectRatio = this.mHeight / this.mWidth;
            float maxWidth = Math.min(360.0f * this.mMetrics.density, Math.min(this.mMetrics.widthPixels, this.mMetrics.heightPixels));
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.setFlags(32, 32);
            String location = InAppDialogFragment.this.mCampaign.getDisplayLocation();
            if (location.equals(LOCATION_CENTER)) {
                window.setLayout(this.mMetrics.widthPixels, this.mMetrics.heightPixels);
                int margin = (int) ((10.0f * this.mMetrics.density) + 0.5f);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mWebView.getLayoutParams();
                params.width = ((int) Math.min(maxWidth - (margin << 1), (int) ((this.mWidth * this.mMetrics.density) + 0.5f))) + (margin << 1);
                params.height = ((int) (Math.min(maxWidth - (margin << 1), (int) ((this.mWidth * this.mMetrics.density) + 0.5f)) * aspectRatio)) + (margin << 1);
                params.setMargins(margin, margin, margin, margin);
                this.mWebView.setLayoutParams(params);
                this.mWebView.requestLayout();
                RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) this.mBtnClose.getLayoutParams();
                btnParams.setMargins(0, 0, -margin, 0);
                this.mBtnClose.setLayoutParams(btnParams);
                this.mBtnClose.requestLayout();
            } else if (location.equals(LOCATION_FULL)) {
                window.setLayout(this.mMetrics.widthPixels, this.mMetrics.heightPixels);
            } else if (location.equals(LOCATION_TOP)) {
                attributes.y = -268435455;
                attributes.dimAmount = 0.0f;
                window.setLayout((int) maxWidth, (int) ((maxWidth * aspectRatio) + 0.5f));
            } else if (location.equals(LOCATION_BOTTOM)) {
                attributes.y = 268435455;
                attributes.dimAmount = 0.0f;
                window.setLayout((int) maxWidth, (int) ((maxWidth * aspectRatio) + 0.5f));
            }
            window.setFlags(1024, 1024);
        }

        final void dismissWithAnimation() {
            InAppDialogFragment.this.mMessageHandler.post(new Runnable() { // from class: com.localytics.android.InAppDialogFragment.InAppDialog.4
                @Override // java.lang.Runnable
                public void run() {
                    String location = InAppDialogFragment.this.mCampaign.getDisplayLocation();
                    if (location.equals(InAppDialog.LOCATION_CENTER)) {
                        InAppDialog.this.mRootLayout.startAnimation(InAppDialog.this.mAnimCenterOut);
                        return;
                    }
                    if (location.equals(InAppDialog.LOCATION_FULL)) {
                        InAppDialog.this.mRootLayout.startAnimation(InAppDialog.this.mAnimFullOut);
                    } else if (location.equals(InAppDialog.LOCATION_TOP)) {
                        InAppDialog.this.mRootLayout.startAnimation(InAppDialog.this.mAnimTopOut);
                    } else if (location.equals(InAppDialog.LOCATION_BOTTOM)) {
                        InAppDialog.this.mRootLayout.startAnimation(InAppDialog.this.mAnimBottomOut);
                    }
                }
            });
        }

        final void enterWithAnimation() {
            String location = InAppDialogFragment.this.mCampaign.getDisplayLocation();
            if (location.equals(LOCATION_CENTER)) {
                this.mRootLayout.startAnimation(this.mAnimCenterIn);
                return;
            }
            if (location.equals(LOCATION_FULL)) {
                this.mRootLayout.startAnimation(this.mAnimFullIn);
            } else if (location.equals(LOCATION_TOP)) {
                this.mRootLayout.startAnimation(this.mAnimTopIn);
            } else if (location.equals(LOCATION_BOTTOM)) {
                this.mRootLayout.startAnimation(this.mAnimBottomIn);
            }
        }

        @Override // android.app.Dialog
        protected final void onStop() {
            if (this.mBtnClose != null) {
                this.mBtnClose.release();
            }
            super.onStop();
        }

        @Override // android.app.Dialog, android.view.KeyEvent.Callback
        public final boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode != 4) {
                return super.onKeyDown(keyCode, event);
            }
            if (InAppDialogFragment.this.mExitAnimatable.getAndSet(false)) {
                dismissWithAnimation();
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class CloseButton extends View {
            private Bitmap mBitmap;
            private final float mCenterX;
            private final float mCenterY;
            private final float mInnerRadius;
            private final float mOffset;
            private final Paint mPaint;
            private final float mRadius;
            private final Paint mShadowInnerPaint;
            private final Paint mShadowOuterPaint;
            private final float mStrokeWidth;

            CloseButton(Context context, AttributeSet attrs) {
                super(context, attrs);
                setId(1);
                setContentDescription(InAppDialogFragment.CLOSE_BUTTON_DESCRIPTION);
                if (DatapointHelper.getApiLevel() >= 14) {
                    setLayerType(1, null);
                }
                float dip = getResources().getDisplayMetrics().density;
                this.mCenterX = 13.0f * dip;
                this.mCenterY = 13.0f * dip;
                this.mRadius = 13.0f * dip;
                this.mOffset = 5.0f * dip;
                this.mStrokeWidth = 2.5f * dip;
                this.mInnerRadius = this.mRadius - (this.mStrokeWidth * 0.5f);
                this.mPaint = new Paint(1);
                this.mShadowInnerPaint = new Paint(1);
                this.mShadowInnerPaint.setMaskFilter(new BlurMaskFilter(this.mRadius - dip, BlurMaskFilter.Blur.INNER));
                this.mShadowOuterPaint = new Paint(1);
                this.mShadowOuterPaint.setMaskFilter(new BlurMaskFilter(2.0f * dip, BlurMaskFilter.Blur.OUTER));
                float buttonMargin = ((InAppDialogFragment.sDismissButtonImage == null ? 30.0f : 40.0f) * dip) + 0.5f;
                setLayoutParams(new RelativeLayout.LayoutParams((int) buttonMargin, (int) buttonMargin));
                this.mBitmap = Bitmap.createBitmap((int) ((26.0f * dip) + 0.5f), (int) ((26.0f * dip) + 0.5f), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.mBitmap);
                this.mPaint.setColor(-16777216);
                this.mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(this.mCenterX, this.mCenterY, this.mRadius, this.mPaint);
                this.mPaint.setColor(-1);
                this.mPaint.setStyle(Paint.Style.STROKE);
                this.mPaint.setStrokeWidth(this.mStrokeWidth);
                canvas.drawCircle(this.mCenterX, this.mCenterY, this.mInnerRadius, this.mPaint);
                this.mPaint.setStrokeWidth(4.5f * dip);
                canvas.drawLine(this.mCenterX - this.mOffset, this.mCenterY - this.mOffset, this.mCenterX + this.mOffset, this.mCenterY + this.mOffset, this.mPaint);
                canvas.drawLine(this.mCenterX - this.mOffset, this.mCenterY + this.mOffset, this.mCenterX + this.mOffset, this.mCenterY - this.mOffset, this.mPaint);
            }

            @Override // android.view.View
            protected final void onDraw(Canvas canvas) {
                float xOffset;
                float shadowOffset;
                Bitmap image;
                super.onDraw(canvas);
                float dip = getResources().getDisplayMetrics().density;
                if (InAppDialogFragment.sDismissButtonImage != null) {
                    image = InAppDialogFragment.sDismissButtonImage;
                    xOffset = (image.getHeight() - image.getWidth()) / 2.0f;
                } else if (this.mBitmap != null) {
                    if (InAppDialogFragment.sDismissButtonLocation == Localytics.InAppMessageDismissButtonLocation.LEFT) {
                        xOffset = 0.0f;
                        shadowOffset = dip;
                    } else {
                        xOffset = 4.0f * dip;
                        shadowOffset = 3.0f * dip;
                    }
                    canvas.drawCircle(this.mCenterX + shadowOffset, this.mCenterY + dip, this.mRadius - dip, this.mShadowInnerPaint);
                    canvas.drawCircle(this.mCenterX + shadowOffset, this.mCenterY + dip, this.mRadius - dip, this.mShadowOuterPaint);
                    image = this.mBitmap;
                } else {
                    return;
                }
                canvas.drawBitmap(image, xOffset, 0.0f, this.mPaint);
            }

            public final void release() {
                if (this.mBitmap != null) {
                    this.mBitmap.recycle();
                    this.mBitmap = null;
                }
            }
        }
    }
}
