package com.localytics.android;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class PushCampaign extends Campaign {
    public static final Parcelable.Creator<PushCampaign> CREATOR = new Parcelable.Creator<PushCampaign>() { // from class: com.localytics.android.PushCampaign.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final PushCampaign createFromParcel(Parcel source) {
            return new PushCampaign(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final PushCampaign[] newArray(int size) {
            return new PushCampaign[size];
        }
    };
    private final boolean mControlGroup;
    private final long mCreativeId;
    private final String mCreativeType;
    private final int mKillSwitch;
    private final String mMessage;
    private final String mSoundFilename;
    private final int mTestMode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PushCampaign(String message, String soundURI, Bundle data) throws JSONException {
        if (!data.containsKey("ll")) {
            throw new JSONException("'ll' key missing from push data");
        }
        JSONObject llObject = new JSONObject(data.getString("ll"));
        this.mCampaignId = llObject.getInt("ca");
        this.mSchemaVersion = llObject.optInt("v", 1);
        this.mMessage = message;
        this.mCreativeId = llObject.getLong("cr");
        this.mAbTest = String.valueOf(this.mCreativeId);
        this.mCreativeType = llObject.optString("t", null);
        this.mSoundFilename = soundURI;
        this.mControlGroup = "Control".equalsIgnoreCase(this.mCreativeType);
        this.mKillSwitch = llObject.optInt("x", 0);
        this.mTestMode = llObject.optInt("test_mode", 0);
        this.mAttributes = new HashMap();
        populateAttributes(data);
    }

    public long getCreativeId() {
        return this.mCreativeId;
    }

    public String getCreativeType() {
        return this.mCreativeType;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public String getSoundFilename() {
        return this.mSoundFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isControlGroup() {
        return this.mControlGroup;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getKillSwitch() {
        return this.mKillSwitch;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getTestMode() {
        return this.mTestMode;
    }

    private void populateAttributes(Bundle bundle) throws JSONException {
        for (String k : bundle.keySet()) {
            Object v = bundle.get(k);
            if ((v instanceof String) || (v instanceof Number) || (v instanceof Boolean)) {
                this.mAttributes.put(k, v.toString());
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mCampaignId);
        dest.writeLong(this.mCreativeId);
        dest.writeString(this.mCreativeType);
        dest.writeString(this.mMessage);
        dest.writeString(this.mSoundFilename);
        dest.writeInt(this.mKillSwitch);
        dest.writeInt(this.mTestMode);
        dest.writeString(this.mAbTest);
        dest.writeLong(this.mVersion);
        dest.writeInt(this.mControlGroup ? 1 : 0);
        dest.writeLong(this.mSchemaVersion);
        dest.writeMap(this.mAttributes);
    }

    private PushCampaign(Parcel in) {
        this.mCampaignId = in.readLong();
        this.mCreativeId = in.readLong();
        this.mCreativeType = in.readString();
        this.mMessage = in.readString();
        this.mSoundFilename = in.readString();
        this.mKillSwitch = in.readInt();
        this.mTestMode = in.readInt();
        this.mAbTest = in.readString();
        this.mVersion = in.readLong();
        this.mControlGroup = in.readInt() != 0;
        this.mSchemaVersion = in.readInt();
        this.mAttributes = in.readHashMap(null);
    }
}
