package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.os.PowerManager;
import android.util.SparseArray;

/* loaded from: classes.dex */
public abstract class WakefulBroadcastReceiver extends BroadcastReceiver {
    private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks = new SparseArray<>();
    private static int mNextId = 1;
}
