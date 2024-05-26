package com.localytics.android;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface ICreativeDownloadTask extends Comparable<ICreativeDownloadTask>, Runnable {

    /* loaded from: classes.dex */
    public enum Priority {
        NORMAL,
        HIGH
    }

    MarketingMessage getCampaign();

    Priority getPriority();

    int getSequence();

    void setPriority(Priority priority);
}
