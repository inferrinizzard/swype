package com.localytics.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nuance.connect.util.TimeConversion;
import java.io.File;
import java.util.Date;

/* loaded from: classes.dex */
public class InboxListItem extends LinearLayout {
    private long mCampaignId;
    private boolean mLoadDownloadedThumbnail;
    private Uri mLocalThumbnailUri;
    private BroadcastReceiver mMessageReceiver;
    private TextView mSummaryTextView;
    private ImageView mThumbnailImageView;
    private TextView mTimeTextView;
    private TextView mTitleTextView;
    private UnreadIndicatorView mUnreadIndicatorView;

    public InboxListItem(Context context) {
        this(context, null);
    }

    public InboxListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        this.mMessageReceiver = new BroadcastReceiver() { // from class: com.localytics.android.InboxListItem.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (InboxListItem.this.mCampaignId == intent.getLongExtra("campaign_id", 0L)) {
                    InboxListItem.this.loadThumbnail(InboxListItem.this.mLocalThumbnailUri);
                }
            }
        };
    }

    public TextView getTitleTextView() {
        return this.mTitleTextView;
    }

    public void setTitle(String title) {
        this.mTitleTextView.setText(title);
    }

    public TextView getSummaryTextView() {
        return this.mSummaryTextView;
    }

    public void setSummary(String summary) {
        if (!TextUtils.isEmpty(summary)) {
            this.mSummaryTextView.setVisibility(0);
            this.mSummaryTextView.setText(summary);
        } else {
            this.mSummaryTextView.setVisibility(8);
        }
    }

    public TextView getTimeTextView() {
        return this.mTimeTextView;
    }

    public void setTime(Date receivedDate) {
        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(receivedDate.getTime(), now, TimeConversion.MILLIS_IN_MINUTE, 524288);
        this.mTimeTextView.setText(capitalizeFirstCharacter(relativeTime));
    }

    public ImageView getThumbnailImageView() {
        return this.mThumbnailImageView;
    }

    private void setThumbnailImage(boolean hasThumbnail, Uri localThumbnailUri) {
        this.mLocalThumbnailUri = localThumbnailUri;
        if (hasThumbnail) {
            this.mThumbnailImageView.setVisibility(0);
            loadThumbnail(localThumbnailUri);
        } else {
            this.mThumbnailImageView.setVisibility(8);
        }
    }

    public UnreadIndicatorView getUnreadIndicatorView() {
        return this.mUnreadIndicatorView;
    }

    public void setUnreadState(boolean read) {
        if (read) {
            this.mUnreadIndicatorView.setVisibility(4);
        } else {
            this.mUnreadIndicatorView.setVisibility(0);
        }
    }

    public void populateViews(InboxCampaign campaign, boolean loadDownloadedThumbnail) {
        this.mLoadDownloadedThumbnail = loadDownloadedThumbnail;
        setCampaignId(campaign.getCampaignId());
        setUnreadState(campaign.isRead());
        if (loadDownloadedThumbnail) {
            setThumbnailImage(campaign.hasThumbnail(), campaign.getLocalThumbnailUri());
        }
        setTitle(campaign.getTitle());
        setSummary(campaign.getSummary());
        setTime(campaign.getReceivedDate());
    }

    protected void setCampaignId(long campaignId) {
        this.mCampaignId = campaignId;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mLoadDownloadedThumbnail) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mMessageReceiver, new IntentFilter("com.localytics.intent.action.THUMBNAIL_DOWNLOADED"));
            loadThumbnail(this.mLocalThumbnailUri);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mLoadDownloadedThumbnail) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mMessageReceiver);
        }
    }

    private void initViews() {
        int padding = Utils.dpToPx(8, getContext());
        int paddingSmall = Utils.dpToPx(4, getContext());
        int paddingTiny = Utils.dpToPx(2, getContext());
        int thumbnailSize = Utils.dpToPx(50, getContext());
        setOrientation(0);
        setPadding(padding, padding, padding, padding);
        setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        this.mUnreadIndicatorView = new UnreadIndicatorView(getContext());
        LinearLayout.LayoutParams unreadLayoutParams = new LinearLayout.LayoutParams(padding, padding);
        unreadLayoutParams.setMargins(0, padding, padding, 0);
        this.mUnreadIndicatorView.setLayoutParams(unreadLayoutParams);
        addView(this.mUnreadIndicatorView);
        this.mThumbnailImageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
        imageLayoutParams.setMargins(0, 0, padding, 0);
        this.mThumbnailImageView.setLayoutParams(imageLayoutParams);
        addView(this.mThumbnailImageView);
        LinearLayout titleSummaryLL = new LinearLayout(getContext());
        titleSummaryLL.setOrientation(1);
        titleSummaryLL.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.mTitleTextView = new TextView(getContext());
        this.mTitleTextView.setTextSize(2, 16.0f);
        this.mTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        this.mTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        titleSummaryLL.addView(this.mTitleTextView);
        this.mSummaryTextView = new TextView(getContext());
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.setMargins(0, paddingSmall, 0, 0);
        this.mSummaryTextView.setLayoutParams(descriptionParams);
        titleSummaryLL.addView(this.mSummaryTextView);
        addView(titleSummaryLL);
        this.mTimeTextView = new TextView(getContext());
        this.mTimeTextView.setTextSize(2, 12.0f);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(-2, -2);
        timeParams.setMargins(paddingSmall, paddingTiny, 0, 0);
        this.mTimeTextView.setLayoutParams(timeParams);
        addView(this.mTimeTextView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadThumbnail(Uri thumbnailUri) {
        if (thumbnailUri != null && new File(thumbnailUri.getPath()).exists()) {
            this.mThumbnailImageView.setImageURI(thumbnailUri);
        } else {
            this.mThumbnailImageView.setImageURI(null);
        }
    }

    private CharSequence capitalizeFirstCharacter(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return null;
        }
        StringBuilder builder = new StringBuilder().append(Character.toUpperCase(charSequence.charAt(0)));
        if (charSequence.length() > 1) {
            builder.append(charSequence.subSequence(1, charSequence.length()));
        }
        return builder.toString();
    }
}
