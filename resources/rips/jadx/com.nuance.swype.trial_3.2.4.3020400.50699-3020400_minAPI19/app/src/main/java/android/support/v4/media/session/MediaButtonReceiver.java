package android.support.v4.media.session;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;

/* loaded from: classes.dex */
public class MediaButtonReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Intent queryIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        queryIntent.setPackage(context.getPackageName());
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(queryIntent, 0);
        if (resolveInfos.isEmpty()) {
            queryIntent.setAction("android.media.browse.MediaBrowserService");
            resolveInfos = pm.queryIntentServices(queryIntent, 0);
        }
        if (resolveInfos.isEmpty()) {
            throw new IllegalStateException("Could not find any Service that handles android.intent.action.MEDIA_BUTTON or a media browser service implementation");
        }
        if (resolveInfos.size() != 1) {
            throw new IllegalStateException("Expected 1 Service that handles " + queryIntent.getAction() + ", found " + resolveInfos.size());
        }
        ResolveInfo resolveInfo = resolveInfos.get(0);
        ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        intent.setComponent(componentName);
        context.startService(intent);
    }
}
