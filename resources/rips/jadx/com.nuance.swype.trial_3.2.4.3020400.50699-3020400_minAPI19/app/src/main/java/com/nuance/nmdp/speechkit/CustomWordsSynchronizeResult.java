package com.nuance.nmdp.speechkit;

import com.facebook.GraphResponse;

/* loaded from: classes.dex */
public final class CustomWordsSynchronizeResult {
    public final String ActionType;
    public final int DeletedAllUserInformation;
    public final int FinalCount;
    public final int ForceUpload;
    public final String ID;
    public final String NewChecksum;
    public final boolean Status;

    public CustomWordsSynchronizeResult(String id, String actionType, String status, String newChecksum, int finalCount, int forceUpload, int deletedAllUserInformation) {
        this.ID = id;
        this.ActionType = actionType;
        this.Status = status.compareTo(GraphResponse.SUCCESS_KEY) == 0;
        this.NewChecksum = newChecksum;
        this.FinalCount = finalCount;
        this.ForceUpload = forceUpload;
        this.DeletedAllUserInformation = deletedAllUserInformation;
    }
}
