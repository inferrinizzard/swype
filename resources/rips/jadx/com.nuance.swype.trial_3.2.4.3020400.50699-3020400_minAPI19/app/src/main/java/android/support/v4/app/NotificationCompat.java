package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompatApi20;
import android.support.v4.app.NotificationCompatApi21;
import android.support.v4.app.NotificationCompatApi24;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.app.NotificationCompatIceCreamSandwich;
import android.support.v4.app.NotificationCompatJellybean;
import android.support.v4.app.NotificationCompatKitKat;
import android.support.v4.app.RemoteInputCompatBase;
import android.support.v4.os.BuildCompat;
import android.widget.RemoteViews;
import com.facebook.share.internal.ShareConstants;
import com.nuance.swype.input.InputFieldInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class NotificationCompat {
    private static final NotificationCompatImpl IMPL;

    /* loaded from: classes.dex */
    public static class BigPictureStyle extends Style {
        Bitmap mBigLargeIcon;
        boolean mBigLargeIconSet;
        Bitmap mPicture;
    }

    /* loaded from: classes.dex */
    public static class InboxStyle extends Style {
        ArrayList<CharSequence> mTexts = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface NotificationCompatImpl {
        Notification build$ab8b522(Builder builder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class BuilderExtender {
        protected BuilderExtender() {
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplBase implements NotificationCompatImpl {
        NotificationCompatImplBase() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public Notification build$ab8b522(Builder b) {
            Notification result = b.mNotification;
            result.setLatestEventInfo(b.mContext, b.mContentTitle, b.mContentText, b.mContentIntent);
            if (b.mPriority > 0) {
                result.flags |= 128;
            }
            if (b.mContentView != null) {
                result.contentView = b.mContentView;
            }
            return result;
        }

        public Bundle getExtras(Notification n) {
            return null;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplGingerbread extends NotificationCompatImplBase {
        NotificationCompatImplGingerbread() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public final Notification build$ab8b522(Builder b) {
            Notification result = b.mNotification;
            Context context = b.mContext;
            CharSequence charSequence = b.mContentTitle;
            CharSequence charSequence2 = b.mContentText;
            PendingIntent pendingIntent = b.mContentIntent;
            PendingIntent pendingIntent2 = b.mFullScreenIntent;
            result.setLatestEventInfo(context, charSequence, charSequence2, pendingIntent);
            result.fullScreenIntent = pendingIntent2;
            if (b.mPriority > 0) {
                result.flags |= 128;
            }
            if (b.mContentView != null) {
                result.contentView = b.mContentView;
            }
            return result;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplHoneycomb extends NotificationCompatImplBase {
        NotificationCompatImplHoneycomb() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public final Notification build$ab8b522(Builder b) {
            Context context = b.mContext;
            Notification notification = b.mNotification;
            CharSequence charSequence = b.mContentTitle;
            CharSequence charSequence2 = b.mContentText;
            CharSequence charSequence3 = b.mContentInfo;
            RemoteViews remoteViews = b.mTickerView;
            int i = b.mNumber;
            PendingIntent pendingIntent = b.mContentIntent;
            Notification notification2 = new Notification.Builder(context).setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 2) != 0).setOnlyAlertOnce((notification.flags & 8) != 0).setAutoCancel((notification.flags & 16) != 0).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(b.mFullScreenIntent, (notification.flags & 128) != 0).setLargeIcon(b.mLargeIcon).setNumber(i).getNotification();
            if (b.mContentView != null) {
                notification2.contentView = b.mContentView;
            }
            return notification2;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplIceCreamSandwich extends NotificationCompatImplBase {
        NotificationCompatImplIceCreamSandwich() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public final Notification build$ab8b522(Builder b) {
            Notification notification = new NotificationCompatIceCreamSandwich.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate).build();
            if (b.mContentView != null) {
                notification.contentView = b.mContentView;
            }
            return notification;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplJellybean extends NotificationCompatImplBase {
        NotificationCompatImplJellybean() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public Notification build$ab8b522(Builder b) {
            NotificationCompatJellybean.Builder builder = new NotificationCompatJellybean.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate, b.mUseChronometer, b.mPriority, b.mSubText, b.mLocalOnly, b.mExtras, b.mGroupKey, b.mGroupSummary, b.mSortKey, b.mContentView, b.mBigContentView);
            NotificationCompat.access$000(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = builder.build();
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(getExtras(notification));
            }
            return notification;
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplBase
        public Bundle getExtras(Notification n) {
            return NotificationCompatJellybean.getExtras(n);
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplKitKat extends NotificationCompatImplJellybean {
        NotificationCompatImplKitKat() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplJellybean, android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public Notification build$ab8b522(Builder b) {
            NotificationCompatKitKat.Builder builder = new NotificationCompatKitKat.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate, b.mShowWhen, b.mUseChronometer, b.mPriority, b.mSubText, b.mLocalOnly, b.mPeople, b.mExtras, b.mGroupKey, b.mGroupSummary, b.mSortKey, b.mContentView, b.mBigContentView);
            NotificationCompat.access$000(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            return builder.build();
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplJellybean, android.support.v4.app.NotificationCompat.NotificationCompatImplBase
        public final Bundle getExtras(Notification n) {
            return n.extras;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplApi20 extends NotificationCompatImplKitKat {
        NotificationCompatImplApi20() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplKitKat, android.support.v4.app.NotificationCompat.NotificationCompatImplJellybean, android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public Notification build$ab8b522(Builder b) {
            NotificationCompatApi20.Builder builder = new NotificationCompatApi20.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate, b.mShowWhen, b.mUseChronometer, b.mPriority, b.mSubText, b.mLocalOnly, b.mPeople, b.mExtras, b.mGroupKey, b.mGroupSummary, b.mSortKey, b.mContentView, b.mBigContentView);
            NotificationCompat.access$000(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = builder.build();
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplApi21 extends NotificationCompatImplApi20 {
        NotificationCompatImplApi21() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplApi20, android.support.v4.app.NotificationCompat.NotificationCompatImplKitKat, android.support.v4.app.NotificationCompat.NotificationCompatImplJellybean, android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public Notification build$ab8b522(Builder b) {
            NotificationCompatApi21.Builder builder = new NotificationCompatApi21.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate, b.mShowWhen, b.mUseChronometer, b.mPriority, b.mSubText, b.mLocalOnly, b.mCategory, b.mPeople, b.mExtras, b.mColor, b.mVisibility, b.mPublicVersion, b.mGroupKey, b.mGroupSummary, b.mSortKey, b.mContentView, b.mBigContentView, b.mHeadsUpContentView);
            NotificationCompat.access$000(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = builder.build();
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    /* loaded from: classes.dex */
    static class NotificationCompatImplApi24 extends NotificationCompatImplApi21 {
        NotificationCompatImplApi24() {
        }

        @Override // android.support.v4.app.NotificationCompat.NotificationCompatImplApi21, android.support.v4.app.NotificationCompat.NotificationCompatImplApi20, android.support.v4.app.NotificationCompat.NotificationCompatImplKitKat, android.support.v4.app.NotificationCompat.NotificationCompatImplJellybean, android.support.v4.app.NotificationCompat.NotificationCompatImplBase, android.support.v4.app.NotificationCompat.NotificationCompatImpl
        public final Notification build$ab8b522(Builder b) {
            NotificationCompatApi24.Builder builder = new NotificationCompatApi24.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, b.mContentInfo, b.mTickerView, b.mNumber, b.mContentIntent, b.mFullScreenIntent, b.mLargeIcon, b.mProgressMax, b.mProgress, b.mProgressIndeterminate, b.mShowWhen, b.mUseChronometer, b.mPriority, b.mSubText, b.mLocalOnly, b.mCategory, b.mPeople, b.mExtras, b.mColor, b.mVisibility, b.mPublicVersion, b.mGroupKey, b.mGroupSummary, b.mSortKey, b.mRemoteInputHistory, b.mContentView, b.mBigContentView, b.mHeadsUpContentView);
            NotificationCompat.access$000(builder, b.mActions);
            NotificationCompat.access$200(builder, b.mStyle);
            Notification notification = builder.build();
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addStyleToBuilderJellybean(NotificationBuilderWithBuilderAccessor builder, Style style) {
        if (style != null) {
            if (style instanceof BigTextStyle) {
                BigTextStyle bigTextStyle = (BigTextStyle) style;
                NotificationCompatJellybean.addBigTextStyle(builder, bigTextStyle.mBigContentTitle, bigTextStyle.mSummaryTextSet, bigTextStyle.mSummaryText, bigTextStyle.mBigText);
            } else if (style instanceof InboxStyle) {
                InboxStyle inboxStyle = (InboxStyle) style;
                NotificationCompatJellybean.addInboxStyle(builder, inboxStyle.mBigContentTitle, inboxStyle.mSummaryTextSet, inboxStyle.mSummaryText, inboxStyle.mTexts);
            } else if (style instanceof BigPictureStyle) {
                BigPictureStyle bigPictureStyle = (BigPictureStyle) style;
                NotificationCompatJellybean.addBigPictureStyle(builder, bigPictureStyle.mBigContentTitle, bigPictureStyle.mSummaryTextSet, bigPictureStyle.mSummaryText, bigPictureStyle.mPicture, bigPictureStyle.mBigLargeIcon, bigPictureStyle.mBigLargeIconSet);
            }
        }
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new NotificationCompatImplApi24();
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new NotificationCompatImplApi21();
            return;
        }
        if (Build.VERSION.SDK_INT >= 20) {
            IMPL = new NotificationCompatImplApi20();
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new NotificationCompatImplKitKat();
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new NotificationCompatImplJellybean();
            return;
        }
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new NotificationCompatImplIceCreamSandwich();
            return;
        }
        if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new NotificationCompatImplHoneycomb();
        } else if (Build.VERSION.SDK_INT >= 9) {
            IMPL = new NotificationCompatImplGingerbread();
        } else {
            IMPL = new NotificationCompatImplBase();
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        RemoteViews mBigContentView;
        String mCategory;
        public CharSequence mContentInfo;
        public PendingIntent mContentIntent;
        public CharSequence mContentText;
        public CharSequence mContentTitle;
        RemoteViews mContentView;
        public Context mContext;
        Bundle mExtras;
        PendingIntent mFullScreenIntent;
        String mGroupKey;
        boolean mGroupSummary;
        RemoteViews mHeadsUpContentView;
        public Bitmap mLargeIcon;
        public int mNumber;
        public ArrayList<String> mPeople;
        int mPriority;
        int mProgress;
        boolean mProgressIndeterminate;
        int mProgressMax;
        Notification mPublicVersion;
        public CharSequence[] mRemoteInputHistory;
        String mSortKey;
        public Style mStyle;
        public CharSequence mSubText;
        RemoteViews mTickerView;
        public boolean mUseChronometer;
        boolean mShowWhen = true;
        public ArrayList<Action> mActions = new ArrayList<>();
        boolean mLocalOnly = false;
        int mColor = 0;
        int mVisibility = 0;
        public Notification mNotification = new Notification();

        public Builder(Context context) {
            this.mContext = context;
            this.mNotification.when = System.currentTimeMillis();
            this.mNotification.audioStreamType = -1;
            this.mPriority = 0;
            this.mPeople = new ArrayList<>();
        }

        public final Builder setSmallIcon(int icon) {
            this.mNotification.icon = icon;
            return this;
        }

        public final Builder setContentTitle(CharSequence title) {
            this.mContentTitle = limitCharSequenceLength(title);
            return this;
        }

        public final Builder setContentText(CharSequence text) {
            this.mContentText = limitCharSequenceLength(text);
            return this;
        }

        public final Builder setContentIntent(PendingIntent intent) {
            this.mContentIntent = intent;
            return this;
        }

        public final Builder setTicker(CharSequence tickerText) {
            this.mNotification.tickerText = limitCharSequenceLength(tickerText);
            return this;
        }

        public final Builder setSound(Uri sound) {
            this.mNotification.sound = sound;
            this.mNotification.audioStreamType = -1;
            return this;
        }

        public final Builder setDefaults(int defaults) {
            this.mNotification.defaults = defaults;
            if ((defaults & 4) != 0) {
                this.mNotification.flags |= 1;
            }
            return this;
        }

        public final Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (this.mStyle != null) {
                    Style style2 = this.mStyle;
                    if (style2.mBuilder != this) {
                        style2.mBuilder = this;
                        if (style2.mBuilder != null) {
                            style2.mBuilder.setStyle(style2);
                        }
                    }
                }
            }
            return this;
        }

        public final Builder setPublicVersion(Notification n) {
            this.mPublicVersion = n;
            return this;
        }

        public final Notification build() {
            NotificationCompatImpl notificationCompatImpl = NotificationCompat.IMPL;
            new BuilderExtender();
            return notificationCompatImpl.build$ab8b522(this);
        }

        protected static CharSequence limitCharSequenceLength(CharSequence cs) {
            if (cs != null && cs.length() > 5120) {
                return cs.subSequence(0, 5120);
            }
            return cs;
        }

        public final Builder setAutoCancel$7abcb88d() {
            this.mNotification.flags |= 16;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Style {
        CharSequence mBigContentTitle;
        Builder mBuilder;
        CharSequence mSummaryText;
        boolean mSummaryTextSet = false;

        public void addCompatExtras(Bundle extras) {
        }
    }

    /* loaded from: classes.dex */
    public static class BigTextStyle extends Style {
        CharSequence mBigText;

        public final BigTextStyle bigText(CharSequence cs) {
            this.mBigText = Builder.limitCharSequenceLength(cs);
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static class MessagingStyle extends Style {
        CharSequence mConversationTitle;
        List<Message> mMessages = new ArrayList();
        CharSequence mUserDisplayName;

        /* loaded from: classes.dex */
        public static final class Message {
            String mDataMimeType;
            Uri mDataUri;
            final CharSequence mSender;
            final CharSequence mText;
            final long mTimestamp;
        }

        MessagingStyle() {
        }

        @Override // android.support.v4.app.NotificationCompat.Style
        public final void addCompatExtras(Bundle extras) {
            super.addCompatExtras(extras);
            if (this.mUserDisplayName != null) {
                extras.putCharSequence("android.selfDisplayName", this.mUserDisplayName);
            }
            if (this.mConversationTitle != null) {
                extras.putCharSequence("android.conversationTitle", this.mConversationTitle);
            }
            if (this.mMessages.isEmpty()) {
                return;
            }
            List<Message> list = this.mMessages;
            Parcelable[] parcelableArr = new Bundle[list.size()];
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Message message = list.get(i);
                Bundle bundle = new Bundle();
                if (message.mText != null) {
                    bundle.putCharSequence(InputFieldInfo.INPUT_TYPE_TEXT, message.mText);
                }
                bundle.putLong("time", message.mTimestamp);
                if (message.mSender != null) {
                    bundle.putCharSequence("sender", message.mSender);
                }
                if (message.mDataMimeType != null) {
                    bundle.putString("type", message.mDataMimeType);
                }
                if (message.mDataUri != null) {
                    bundle.putParcelable(ShareConstants.MEDIA_URI, message.mDataUri);
                }
                parcelableArr[i] = bundle;
            }
            extras.putParcelableArray("android.messages", parcelableArr);
        }
    }

    /* loaded from: classes.dex */
    public static class Action extends NotificationCompatBase.Action {
        public static final NotificationCompatBase.Action.Factory FACTORY = new NotificationCompatBase.Action.Factory() { // from class: android.support.v4.app.NotificationCompat.Action.1
        };
        public PendingIntent actionIntent;
        public int icon;
        private boolean mAllowGeneratedReplies;
        private final Bundle mExtras;
        private final RemoteInput[] mRemoteInputs;
        public CharSequence title;

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final int getIcon() {
            return this.icon;
        }

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final CharSequence getTitle() {
            return this.title;
        }

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final PendingIntent getActionIntent() {
            return this.actionIntent;
        }

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final Bundle getExtras() {
            return this.mExtras;
        }

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final boolean getAllowGeneratedReplies() {
            return this.mAllowGeneratedReplies;
        }

        @Override // android.support.v4.app.NotificationCompatBase.Action
        public final /* bridge */ /* synthetic */ RemoteInputCompatBase.RemoteInput[] getRemoteInputs() {
            return this.mRemoteInputs;
        }
    }

    static /* synthetic */ void access$000(NotificationBuilderWithActions x0, ArrayList x1) {
        Iterator it = x1.iterator();
        while (it.hasNext()) {
            x0.addAction((Action) it.next());
        }
    }

    static /* synthetic */ void access$200(NotificationBuilderWithBuilderAccessor x0, Style x1) {
        if (x1 != null) {
            if (x1 instanceof MessagingStyle) {
                MessagingStyle messagingStyle = (MessagingStyle) x1;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                for (MessagingStyle.Message message : messagingStyle.mMessages) {
                    arrayList.add(message.mText);
                    arrayList2.add(Long.valueOf(message.mTimestamp));
                    arrayList3.add(message.mSender);
                    arrayList4.add(message.mDataMimeType);
                    arrayList5.add(message.mDataUri);
                }
                NotificationCompatApi24.addMessagingStyle(x0, messagingStyle.mUserDisplayName, messagingStyle.mConversationTitle, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
                return;
            }
            addStyleToBuilderJellybean(x0, x1);
        }
    }
}
