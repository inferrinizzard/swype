package com.nuance.swype.input.update;

import com.nuance.swype.input.IMEApplication;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class UpdateStatusManager {
    private final List<OnUpdateStatusChangeListener> listeners = new CopyOnWriteArrayList();

    /* loaded from: classes.dex */
    public interface OnUpdateStatusChangeListener {
        void onProgressChanged(int i, int i2, int i3);

        void onUpdateStatusChanged(int i, UpdateStatus updateStatus);
    }

    /* loaded from: classes.dex */
    public enum UpdateStatus {
        UPDATE_ATTEMPT,
        PENDING,
        FAILED,
        NO_CONNECTION,
        NO_UPDATE,
        NOT_MODIFIED,
        UPDATING,
        UPDATED,
        UNKNOWN
    }

    public UpdateStatusManager(IMEApplication imeApp) {
    }

    public long getLastUpdatedTime(int id) {
        return 0L;
    }

    public UpdateStatus getStatus(int mLanguageId) {
        return UpdateStatus.UNKNOWN;
    }

    public void registerOnStatusChangeListener(OnUpdateStatusChangeListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterOnStatusChangeListener(OnUpdateStatusChangeListener listener) {
        this.listeners.remove(listener);
    }
}
