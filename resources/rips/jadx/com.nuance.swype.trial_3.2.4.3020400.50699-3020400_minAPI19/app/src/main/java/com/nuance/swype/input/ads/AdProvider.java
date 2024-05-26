package com.nuance.swype.input.ads;

import android.content.Context;
import android.view.ViewGroup;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AdProvider {
    public static final LogManager.Log log = LogManager.getLog("AdProvider");
    private final List<OnAdLoadStatusChangeListener> mAdStatusChangeListeners = new ArrayList();
    public final Context mContext;
    private long mStartLoadTime;

    /* loaded from: classes.dex */
    public enum AD_LOAD_STATUS {
        SUCCESS,
        FAILED_NO_FILL,
        FAILED_INVALID_REQUEST,
        FAILED_INTERNAL_ERROR,
        FAILED_NETWORK_ERROR,
        FAILED_UNKNOWN
    }

    /* loaded from: classes.dex */
    public interface OnAdLoadStatusChangeListener {
        void adLoadStatusChanged(AD_LOAD_STATUS ad_load_status);
    }

    public abstract void hideAdView();

    public abstract void setup(ViewGroup viewGroup);

    public abstract void showAdView();

    public AdProvider(Context context) {
        this.mContext = context;
    }

    public void loadAd() {
        this.mStartLoadTime = System.currentTimeMillis();
    }

    public void addOnAdLoadStatusChangeListener(OnAdLoadStatusChangeListener listener) {
        log.d("Adding ad status change listener");
        this.mAdStatusChangeListeners.add(listener);
    }

    public void notifyAdLoadStatusChanged(AD_LOAD_STATUS status) {
        long loadTime = System.currentTimeMillis() - this.mStartLoadTime;
        UsageData.recordAdResult$7e29e9d3(status, loadTime);
        log.d("Notifying listeners about the ad status changed to: ", status);
        Iterator<OnAdLoadStatusChangeListener> it = this.mAdStatusChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().adLoadStatusChanged(status);
        }
    }
}
