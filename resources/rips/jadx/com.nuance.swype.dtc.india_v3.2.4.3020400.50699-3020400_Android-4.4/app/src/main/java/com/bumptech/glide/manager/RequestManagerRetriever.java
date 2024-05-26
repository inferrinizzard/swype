package com.bumptech.glide.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.bumptech.glide.RequestManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class RequestManagerRetriever implements Handler.Callback {
    private static final RequestManagerRetriever INSTANCE = new RequestManagerRetriever();
    private volatile RequestManager applicationManager;
    final Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
    final Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
    private final Handler handler = new Handler(Looper.getMainLooper(), this);

    public static RequestManagerRetriever get() {
        return INSTANCE;
    }

    RequestManagerRetriever() {
    }

    public RequestManager getApplicationManager(Context context) {
        if (this.applicationManager == null) {
            synchronized (this) {
                if (this.applicationManager == null) {
                    this.applicationManager = new RequestManager(context.getApplicationContext(), new ApplicationLifecycle(), new EmptyRequestManagerTreeNode());
                }
            }
        }
        return this.applicationManager;
    }

    @TargetApi(17)
    public static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    @TargetApi(17)
    public final RequestManagerFragment getRequestManagerFragment(FragmentManager fm) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag("com.bumptech.glide.manager");
        if (current == null) {
            RequestManagerFragment current2 = this.pendingRequestManagerFragments.get(fm);
            if (current2 == null) {
                RequestManagerFragment current3 = new RequestManagerFragment();
                this.pendingRequestManagerFragments.put(fm, current3);
                fm.beginTransaction().add(current3, "com.bumptech.glide.manager").commitAllowingStateLoss();
                this.handler.obtainMessage(1, fm).sendToTarget();
                return current3;
            }
            return current2;
        }
        return current;
    }

    public final SupportRequestManagerFragment getSupportRequestManagerFragment(android.support.v4.app.FragmentManager fm) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag("com.bumptech.glide.manager");
        if (current == null) {
            SupportRequestManagerFragment current2 = this.pendingSupportRequestManagerFragments.get(fm);
            if (current2 == null) {
                SupportRequestManagerFragment current3 = new SupportRequestManagerFragment();
                this.pendingSupportRequestManagerFragments.put(fm, current3);
                fm.beginTransaction().add(current3, "com.bumptech.glide.manager").commitAllowingStateLoss();
                this.handler.obtainMessage(2, fm).sendToTarget();
                return current3;
            }
            return current2;
        }
        return current;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case 1:
                FragmentManager fm = (FragmentManager) message.obj;
                key = fm;
                removed = this.pendingRequestManagerFragments.remove(fm);
                break;
            case 2:
                android.support.v4.app.FragmentManager supportFm = (android.support.v4.app.FragmentManager) message.obj;
                key = supportFm;
                removed = this.pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && removed == null && Log.isLoggable("RMRetriever", 5)) {
            Log.w("RMRetriever", "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }
}
