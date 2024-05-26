package com.localytics.android;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.localytics.android.Localytics;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class InboxListAdapter extends ArrayAdapter<InboxCampaign> {
    private boolean mDownloadsThumbnails;

    /* loaded from: classes.dex */
    public interface Listener {
        void didFinishLoadingCampaigns();
    }

    public InboxListAdapter(Context context) {
        super(context, 0, new ArrayList());
        this.mDownloadsThumbnails = true;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new InboxListItem(getContext());
        }
        InboxListItem item = (InboxListItem) convertView;
        item.populateViews(getItem(position), this.mDownloadsThumbnails);
        return item;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.localytics.android.InboxListAdapter$1] */
    public void getData(final Listener listener) {
        new AsyncTask<Void, Void, List<InboxCampaign>>() { // from class: com.localytics.android.InboxListAdapter.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<InboxCampaign> doInBackground(Void... params) {
                return Localytics.getInboxCampaigns();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<InboxCampaign> inboxCampaigns) {
                try {
                    InboxListAdapter.this.setCampaigns(inboxCampaigns);
                    InboxListAdapter.this.refreshData(listener);
                } catch (Exception e) {
                    Localytics.Log.e("Exception while getting inbox data", e);
                }
            }
        }.execute(new Void[0]);
    }

    public void setDownloadsThumbnails(boolean downloadsThumbnails) {
        this.mDownloadsThumbnails = downloadsThumbnails;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCampaigns(List<InboxCampaign> inboxCampaigns) {
        setNotifyOnChange(false);
        clear();
        for (InboxCampaign campaign : inboxCampaigns) {
            add(campaign);
        }
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshData(final Listener listener) {
        Localytics.refreshInboxCampaigns(new InboxRefreshListener() { // from class: com.localytics.android.InboxListAdapter.2
            @Override // com.localytics.android.InboxRefreshListener
            public void localyticsRefreshedInboxCampaigns(List<InboxCampaign> campaigns) {
                InboxListAdapter.this.inboxRefreshed(campaigns, listener);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inboxRefreshed(List<InboxCampaign> campaigns, Listener listener) {
        setCampaigns(campaigns);
        if (this.mDownloadsThumbnails) {
            Localytics.downloadInboxThumbnails(campaigns);
        }
        if (listener != null) {
            listener.didFinishLoadingCampaigns();
        }
    }
}
