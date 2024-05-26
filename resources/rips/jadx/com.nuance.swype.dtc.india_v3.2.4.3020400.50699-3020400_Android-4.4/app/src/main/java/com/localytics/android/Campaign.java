package com.localytics.android;

import android.os.Parcelable;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class Campaign implements Parcelable {
    protected String mAbTest;
    protected Map<String, String> mAttributes;
    protected long mCampaignId;
    protected String mRuleName;
    protected long mSchemaVersion;
    protected long mVersion;

    public long getCampaignId() {
        return this.mCampaignId;
    }

    public String getName() {
        return getRuleName();
    }

    public Map<String, String> getAttributes() {
        return this.mAttributes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getRuleName() {
        return this.mRuleName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getAbTest() {
        return this.mAbTest;
    }

    protected long getVersion() {
        return this.mVersion;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getSchemaVersion() {
        return this.mSchemaVersion;
    }
}
