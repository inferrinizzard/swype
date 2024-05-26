package com.nuance.swype.preference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class DialogPrefs {
    private final SparseArray<DialogCreator> dialogs = new SparseArray<>();
    private final HashMap<Integer, WeakReference<Dialog>> activeDialogRefs = new HashMap<>();

    /* loaded from: classes.dex */
    protected interface DialogCreator {
        Dialog doCreateDialog(Context context, Bundle bundle);
    }

    public void registerDialog(int dialogId, DialogCreator creator) {
        if (this.dialogs.get(dialogId) == null && creator != null) {
            this.dialogs.put(dialogId, creator);
        }
    }

    public void removeActiveRef(int id) {
        this.activeDialogRefs.remove(Integer.valueOf(id));
    }

    public Dialog getActiveDialog(int id) {
        WeakReference<Dialog> dialogRef = this.activeDialogRefs.get(Integer.valueOf(id));
        if (dialogRef != null) {
            return dialogRef.get();
        }
        return null;
    }

    public void cancelAllActiveDialogs() {
        Iterator<Map.Entry<Integer, WeakReference<Dialog>>> it = this.activeDialogRefs.entrySet().iterator();
        while (it.hasNext()) {
            Dialog d = it.next().getValue().get();
            if (d != null) {
                d.dismiss();
            }
        }
    }

    public Dialog doCreateDialog(int id, Context context, Bundle args) {
        if (args == null) {
            args = new Bundle();
        }
        if (this.dialogs.get(id) == null) {
            return null;
        }
        Dialog dialog = this.dialogs.get(id).doCreateDialog(context, args);
        this.activeDialogRefs.put(Integer.valueOf(id), new WeakReference<>(dialog));
        return dialog;
    }
}
