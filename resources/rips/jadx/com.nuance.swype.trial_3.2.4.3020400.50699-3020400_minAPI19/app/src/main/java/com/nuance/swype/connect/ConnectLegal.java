package com.nuance.swype.connect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.startup.StartupSequenceInfo;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACLegalDocuments;

/* loaded from: classes.dex */
public class ConnectLegal {
    private static final LogManager.Log log = LogManager.getLog("ConnectLegal");
    private final DocumentAcceptedCallback callback;
    private final ACLegalDocuments legal;

    /* loaded from: classes.dex */
    public interface DocumentAcceptedCallback {
        void documentAccepted(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectLegal(Connect connect, DocumentAcceptedCallback callback) {
        this.legal = connect.getSDKManager().getLegalDocuments();
        this.callback = callback;
    }

    public static ConnectLegal from(Context context) {
        return IMEApplication.from(context).getConnect().getLegal();
    }

    public boolean isTosAccepted() {
        boolean isTosAccepted = getDocument(1).isAccepted();
        log.d("isTosAccepted: " + isTosAccepted);
        return isTosAccepted;
    }

    public boolean isTosChanged() {
        boolean isTosChanged = getDocument(1).isChanged();
        log.d("isTosChanged: " + isTosChanged);
        return isTosChanged;
    }

    public boolean wasTosAccepted() {
        boolean wasTosAccepted = getDocument(1).wasAccepted();
        log.d("wasTosAccepted: " + wasTosAccepted);
        return wasTosAccepted;
    }

    public String getTos() {
        return getTranslation(1);
    }

    public void acceptTos() {
        log.d("acceptTos");
        acceptDocument(1);
        notifyCallback(1);
    }

    public boolean isEulaAccepted() {
        boolean isEulaAccepted = getDocument(2).isAccepted();
        log.d("isEulaAccepted: " + isEulaAccepted);
        return isEulaAccepted;
    }

    public boolean isEulaChanged() {
        boolean isEulaChanged = getDocument(2).isChanged();
        log.d("isEulaChanged: " + isEulaChanged);
        return isEulaChanged;
    }

    public String getEula() {
        return getTranslation(2);
    }

    public void acceptEula() {
        log.d("acceptEula");
        acceptDocument(2);
        notifyCallback(2);
    }

    public boolean isOptInAccepted() {
        boolean isOptInAccepted = getDocument(4).isAccepted();
        log.d("isOptInAccepted: " + isOptInAccepted);
        return isOptInAccepted;
    }

    public boolean isOptInChanged() {
        boolean isOptInChanged = getDocument(4).isChanged();
        log.d("isOptInChanged: " + isOptInChanged);
        return isOptInChanged;
    }

    public boolean wasOptInAccepted() {
        boolean wasOptInAccepted = getDocument(4).wasAccepted();
        log.d("wasOptInAccepted: " + wasOptInAccepted);
        return wasOptInAccepted;
    }

    public void resetOptInChangedFlag() {
        this.legal.resetChangedFlag(getDocument(4));
        log.d("resetOptInChangedFlag");
    }

    public String getOptIn() {
        return getTranslation(4);
    }

    public void acceptOptIn() {
        log.d("acceptOptIn");
        acceptDocument(4);
        notifyCallback(4);
    }

    private void notifyCallback(int documentType) {
        if (this.callback != null) {
            this.callback.documentAccepted(documentType);
        }
    }

    public static Intent getLegalActivitiesStartIntent(Context context, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        return StartupSequenceInfo.getLegalActivitiesStartIntent(context, tosRequired, optInRequired, resultData);
    }

    public static Intent getLegalCUDActivitiesStartIntent(Context context, boolean optInRequired, Bundle resultData) {
        return StartupSequenceInfo.getLegalCUDActivitiesStartIntent(context, optInRequired, resultData);
    }

    public static Intent getLegalActivitiesStartIntentForIntent(Intent i, Context context, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent r = getLegalActivitiesStartIntent(context, tosRequired, optInRequired, resultData);
        if (r == null) {
            return i;
        }
        r.putExtra("intent_last", i);
        return r;
    }

    private ACLegalDocuments.ACLegalDocument getDocument(int typeId) {
        try {
            return this.legal.getDocument(typeId);
        } catch (ACException e) {
            log.e("getDocument", e);
            throw new IllegalArgumentException(e);
        }
    }

    private void acceptDocument(int typeId) {
        ACLegalDocuments.ACLegalDocument doc = getDocument(typeId);
        try {
            this.legal.acceptDocument(doc);
        } catch (ACException e) {
            log.e("acceptDocument", e);
        }
    }

    private String getTranslation(int typeId) {
        try {
            return getDocument(typeId).getTranslation();
        } catch (ACException e) {
            log.e("getTranslation", e);
            return null;
        }
    }
}
