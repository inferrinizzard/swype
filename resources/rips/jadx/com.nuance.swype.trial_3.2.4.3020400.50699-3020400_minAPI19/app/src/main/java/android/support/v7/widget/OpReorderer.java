package android.support.v7.widget;

import android.support.v7.widget.AdapterHelper;

/* loaded from: classes.dex */
final class OpReorderer {
    final Callback mCallback;

    /* loaded from: classes.dex */
    interface Callback {
        AdapterHelper.UpdateOp obtainUpdateOp(int i, int i2, int i3, Object obj);

        void recycleUpdateOp(AdapterHelper.UpdateOp updateOp);
    }

    public OpReorderer(Callback callback) {
        this.mCallback = callback;
    }
}
