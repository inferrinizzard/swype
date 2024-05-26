package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompatBase;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
final class NotificationCompatApi21 {

    /* loaded from: classes.dex */
    public static class Builder implements NotificationBuilderWithActions, NotificationBuilderWithBuilderAccessor {
        private Notification.Builder b;
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private Bundle mExtras;
        private RemoteViews mHeadsUpContentView;

        public Builder(Context context, Notification n, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, RemoteViews tickerView, int number, PendingIntent contentIntent, PendingIntent fullScreenIntent, Bitmap largeIcon, int progressMax, int progress, boolean progressIndeterminate, boolean showWhen, boolean useChronometer, int priority, CharSequence subText, boolean localOnly, String category, ArrayList<String> people, Bundle extras, int color, int visibility, Notification publicVersion, String groupKey, boolean groupSummary, String sortKey, RemoteViews contentView, RemoteViews bigContentView, RemoteViews headsUpContentView) {
            this.b = new Notification.Builder(context).setWhen(n.when).setShowWhen(showWhen).setSmallIcon(n.icon, n.iconLevel).setContent(n.contentView).setTicker(n.tickerText, tickerView).setSound(n.sound, n.audioStreamType).setVibrate(n.vibrate).setLights(n.ledARGB, n.ledOnMS, n.ledOffMS).setOngoing((n.flags & 2) != 0).setOnlyAlertOnce((n.flags & 8) != 0).setAutoCancel((n.flags & 16) != 0).setDefaults(n.defaults).setContentTitle(contentTitle).setContentText(contentText).setSubText(subText).setContentInfo(contentInfo).setContentIntent(contentIntent).setDeleteIntent(n.deleteIntent).setFullScreenIntent(fullScreenIntent, (n.flags & 128) != 0).setLargeIcon(largeIcon).setNumber(number).setUsesChronometer(useChronometer).setPriority(priority).setProgress(progressMax, progress, progressIndeterminate).setLocalOnly(localOnly).setGroup(groupKey).setGroupSummary(groupSummary).setSortKey(sortKey).setCategory(category).setColor(color).setVisibility(visibility).setPublicVersion(publicVersion);
            this.mExtras = new Bundle();
            if (extras != null) {
                this.mExtras.putAll(extras);
            }
            Iterator<String> it = people.iterator();
            while (it.hasNext()) {
                String person = it.next();
                this.b.addPerson(person);
            }
            this.mContentView = contentView;
            this.mBigContentView = bigContentView;
            this.mHeadsUpContentView = headsUpContentView;
        }

        @Override // android.support.v4.app.NotificationBuilderWithActions
        public final void addAction(NotificationCompatBase.Action action) {
            NotificationCompatApi20.addAction(this.b, action);
        }

        @Override // android.support.v4.app.NotificationBuilderWithBuilderAccessor
        public final Notification.Builder getBuilder() {
            return this.b;
        }

        @Override // android.support.v4.app.NotificationBuilderWithBuilderAccessor
        public final Notification build() {
            this.b.setExtras(this.mExtras);
            Notification notification = this.b.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                notification.bigContentView = this.mBigContentView;
            }
            if (this.mHeadsUpContentView != null) {
                notification.headsUpContentView = this.mHeadsUpContentView;
            }
            return notification;
        }
    }
}
