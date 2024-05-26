package com.nuance.swype.input.ads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ads.AdProvider;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class Billboard {
    private static final int MSG_SHOW_CLOSE_BUTTON = 1;
    private static final LogManager.Log log = LogManager.getLog("Billboard");
    private boolean mAdLoadRequested;
    private final AdProvider mAdProvider;
    private AdProvider.OnAdLoadStatusChangeListener mAdStatusChangeListener = new AdProvider.OnAdLoadStatusChangeListener() { // from class: com.nuance.swype.input.ads.Billboard.3
        @Override // com.nuance.swype.input.ads.AdProvider.OnAdLoadStatusChangeListener
        public void adLoadStatusChanged(AdProvider.AD_LOAD_STATUS status) {
            Billboard.log.d("Ad load status changed to " + status);
            Billboard.this.mAdLoadRequested = false;
            switch (AnonymousClass4.$SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[status.ordinal()]) {
                case 1:
                    Billboard.this.updateViewToShowAd();
                    IMEApplication.from(Billboard.this.mContext).getAdSessionTracker().resume();
                    return;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    Billboard.this.updateViewToShowPlaceholderAd();
                    Billboard.this.scheduleHidingCancelButton(Billboard.this.mWaitTimeToShowCloseButton);
                    return;
                default:
                    return;
            }
        }
    };
    private ViewGroup mBillboardView;
    private ImageView mCloseButton;
    private final Context mContext;
    private final Handler mHandler;
    private View mPlaceHolderAdView;
    private boolean mSetupDone;
    private final long mWaitTimeToShowCloseButton;

    public Billboard(Context context) {
        this.mContext = context;
        this.mWaitTimeToShowCloseButton = context.getResources().getInteger(R.integer.ad_config_wait_time_to_show_close_button) * 1000;
        this.mAdProvider = AdFactory.createAdForCandidatesView(context);
        this.mAdProvider.addOnAdLoadStatusChangeListener(this.mAdStatusChangeListener);
        this.mHandler = WeakReferenceHandler.create(new Handler.Callback() { // from class: com.nuance.swype.input.ads.Billboard.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Billboard.this.showCancelButton();
                        return true;
                    default:
                        Billboard.log.d("Unhandled message");
                        return false;
                }
            }
        });
    }

    public void setup(ViewGroup billboardView) {
        log.d("Setting up the billboard");
        this.mBillboardView = billboardView;
        this.mCloseButton = (ImageView) billboardView.findViewById(R.id.btn_close_billboard);
        this.mCloseButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.ads.Billboard.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Context unused = Billboard.this.mContext;
                UsageData.mAnalyticsImpl.tagEvent(UsageData.EventTag.AD_BILLBOARD_CLOSE.toString(), null);
                Billboard.this.requestHide();
            }
        });
        setupPlaceholder();
        setupAdProvider();
        updateViewToShowPlaceholderAd();
        this.mSetupDone = true;
    }

    /* renamed from: com.nuance.swype.input.ads.Billboard$4, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS = new int[AdProvider.AD_LOAD_STATUS.values().length];

        static {
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.FAILED_UNKNOWN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.FAILED_INTERNAL_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.FAILED_INVALID_REQUEST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.FAILED_NETWORK_ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$ads$AdProvider$AD_LOAD_STATUS[AdProvider.AD_LOAD_STATUS.FAILED_NO_FILL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private void setupAdProvider() {
        log.d("Setting up the ad provider");
        this.mAdProvider.setup(this.mBillboardView);
    }

    protected void updateViewToShowPlaceholderAd() {
        log.d("Updating view to show placeholder");
        hideAdsView();
        showPlaceholderView();
    }

    protected void updateViewToShowAd() {
        log.d("Updating view to show ad view");
        hidePlaceholderView();
        showAdsView();
    }

    protected void showAdsView() {
        log.d("Requesting provider to show ad view");
        this.mAdProvider.showAdView();
    }

    protected void hideAdsView() {
        log.d("Requesting provider to hide ad view");
        this.mAdProvider.hideAdView();
    }

    protected void hidePlaceholderView() {
        log.d("Hiding placeholder");
        this.mPlaceHolderAdView.setVisibility(4);
    }

    protected void showPlaceholderView() {
        log.d("Showing placeholder");
        this.mPlaceHolderAdView.setVisibility(0);
    }

    private void setupPlaceholder() {
        log.d("Setting up the placeholder");
        this.mPlaceHolderAdView = this.mBillboardView.findViewById(R.id.placeholder_ad);
        Spannable placeHolderContent = stringToSpannable(this.mContext.getString(R.string.press_hod_swype_key_for_swype_store));
        ((TextView) this.mPlaceHolderAdView).setText(placeHolderContent);
    }

    private Spannable stringToSpannable(String inputString) {
        SpannableString ss = new SpannableString(inputString);
        Matcher matcher = Pattern.compile(".*(%%(.+?)%%).*").matcher(inputString);
        if (matcher.matches() && matcher.groupCount() > 0) {
            Drawable icon = ContextCompat.getDrawable(this.mContext, R.drawable.sym_keyboard_swype_black);
            icon.setBounds(0, 0, (int) this.mContext.getResources().getDimension(R.dimen.ad_placeholder_icon_width), (int) this.mContext.getResources().getDimension(R.dimen.ad_placeholder_icon_height));
            ImageSpan imageSpan = new ImageSpan(icon);
            ss.setSpan(imageSpan, matcher.start(1), matcher.end(1), 33);
        }
        return ss;
    }

    public void loadNewAd() {
        if (this.mAdLoadRequested) {
            log.d("Ad load request already in progress.");
            return;
        }
        log.d("Requesting provider to load new ad");
        showPlaceholderView();
        this.mAdProvider.loadAd();
        this.mAdLoadRequested = true;
        hideCancelButton();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void show() {
        log.d("Showing billboard");
        this.mBillboardView.setVisibility(0);
    }

    private void hide() {
        log.d("Hiding billboard");
        hidePlaceholderView();
        hideAdsView();
        this.mBillboardView.setVisibility(8);
    }

    protected ViewGroup getView() {
        return this.mBillboardView;
    }

    protected void showCancelButton() {
        log.d("Showing cancel button");
        this.mCloseButton.setVisibility(0);
    }

    protected void hideCancelButton() {
        log.d("Hiding cancel button");
        this.mCloseButton.setVisibility(4);
    }

    protected void scheduleHidingCancelButton(long milliseconds) {
        log.d("Scheduling a ", Long.valueOf(milliseconds), " delayed for showing cancel button");
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), milliseconds);
    }

    public boolean isShowing() {
        return this.mBillboardView != null && this.mBillboardView.getVisibility() == 0;
    }

    public boolean isSetup() {
        return this.mSetupDone;
    }

    public void requestHide() {
        if (isSetup()) {
            hide();
        }
    }
}
