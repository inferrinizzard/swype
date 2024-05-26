package com.nuance.swype.connect;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.nuance.android.compat.NotificationCompat;
import com.nuance.swype.input.R;
import com.nuance.swype.input.settings.ConnectAccountDispatch;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class AccountCreationBroadcastReciever extends BroadcastReceiver {
    public static final String ACTION_NOTIFICATION = "com.example.android.receivers.NOTIFICATION_ALARM";
    protected static final int NOTIFICATION_ID = 55512212;
    protected static final LogManager.Log log = LogManager.getLog("AccountCreationBroadcastReciever");

    @Override // android.content.BroadcastReceiver
    @SuppressLint({"PrivateResource"})
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_NOTIFICATION.equals(action)) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                Resources res = context.getApplicationContext().getResources();
                if (res != null && notificationManager != null) {
                    notificationBuilder.setContentTitle(res.getString(R.string.back_and_sync_title)).setContentText(res.getString(R.string.notification_account_create_title)).setAutoCancel(true).setOngoing(false).setSmallIcon(R.drawable.swype_icon);
                    Intent resultIntent = new Intent(context, (Class<?>) ConnectAccountDispatch.class).setFlags(268435456).addCategory("android.intent.category.LAUNCHER");
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 134217728);
                    notificationBuilder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    Connect.from(context).dismissAccountAlarm();
                }
            }
        }
    }
}
