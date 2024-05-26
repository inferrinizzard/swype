package com.facebook.appevents;

import android.content.Context;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AccessTokenAppIdPair;
import com.facebook.appevents.AppEvent;
import com.facebook.appevents.internal.AppEventUtility;
import com.facebook.internal.Utility;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppEventStore {
    private static final String PERSISTED_EVENTS_FILENAME = "AppEventsLogger.persistedevents";
    private static final String TAG = AppEventStore.class.getName();

    AppEventStore() {
    }

    public static synchronized void persistEvents(AccessTokenAppIdPair accessTokenAppIdPair, SessionEventsState appEvents) {
        synchronized (AppEventStore.class) {
            AppEventUtility.assertIsNotMainThread();
            PersistedEvents persistedEvents = readAndClearStore();
            if (persistedEvents.containsKey(accessTokenAppIdPair)) {
                persistedEvents.get(accessTokenAppIdPair).addAll(appEvents.getEventsToPersist());
            } else {
                persistedEvents.addEvents(accessTokenAppIdPair, appEvents.getEventsToPersist());
            }
            saveEventsToDisk(persistedEvents);
        }
    }

    public static synchronized void persistEvents(AppEventCollection eventsToPersist) {
        synchronized (AppEventStore.class) {
            AppEventUtility.assertIsNotMainThread();
            PersistedEvents persistedEvents = readAndClearStore();
            for (AccessTokenAppIdPair accessTokenAppIdPair : eventsToPersist.keySet()) {
                SessionEventsState sessionEventsState = eventsToPersist.get(accessTokenAppIdPair);
                persistedEvents.addEvents(accessTokenAppIdPair, sessionEventsState.getEventsToPersist());
            }
            saveEventsToDisk(persistedEvents);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0035 A[Catch: all -> 0x0060, TRY_ENTER, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:10:0x0025, B:12:0x0028, B:16:0x0035, B:23:0x003d, B:44:0x0085, B:46:0x0088, B:47:0x0092, B:50:0x0094, B:36:0x006c, B:38:0x006f, B:41:0x007b, B:27:0x0048, B:29:0x004b, B:32:0x0057), top: B:3:0x0003, inners: #2, #4, #6, #8 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static synchronized com.facebook.appevents.PersistedEvents readAndClearStore() {
        /*
            java.lang.Class<com.facebook.appevents.AppEventStore> r9 = com.facebook.appevents.AppEventStore.class
            monitor-enter(r9)
            com.facebook.appevents.internal.AppEventUtility.assertIsNotMainThread()     // Catch: java.lang.Throwable -> L60
            r5 = 0
            r7 = 0
            android.content.Context r1 = com.facebook.FacebookSdk.getApplicationContext()     // Catch: java.lang.Throwable -> L60
            java.lang.String r8 = "AppEventsLogger.persistedevents"
            java.io.FileInputStream r4 = r1.openFileInput(r8)     // Catch: java.io.FileNotFoundException -> L47 java.lang.Exception -> L63 java.lang.Throwable -> L84
            com.facebook.appevents.AppEventStore$MovedClassObjectInputStream r6 = new com.facebook.appevents.AppEventStore$MovedClassObjectInputStream     // Catch: java.io.FileNotFoundException -> L47 java.lang.Exception -> L63 java.lang.Throwable -> L84
            java.io.BufferedInputStream r8 = new java.io.BufferedInputStream     // Catch: java.io.FileNotFoundException -> L47 java.lang.Exception -> L63 java.lang.Throwable -> L84
            r8.<init>(r4)     // Catch: java.io.FileNotFoundException -> L47 java.lang.Exception -> L63 java.lang.Throwable -> L84
            r6.<init>(r8)     // Catch: java.io.FileNotFoundException -> L47 java.lang.Exception -> L63 java.lang.Throwable -> L84
            java.lang.Object r8 = r6.readObject()     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La0 java.io.FileNotFoundException -> La3
            r0 = r8
            com.facebook.appevents.PersistedEvents r0 = (com.facebook.appevents.PersistedEvents) r0     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La0 java.io.FileNotFoundException -> La3
            r7 = r0
            com.facebook.internal.Utility.closeQuietly(r6)     // Catch: java.lang.Throwable -> L60
            java.lang.String r8 = "AppEventsLogger.persistedevents"
            java.io.File r8 = r1.getFileStreamPath(r8)     // Catch: java.lang.Exception -> L3c java.lang.Throwable -> L60
            r8.delete()     // Catch: java.lang.Exception -> L3c java.lang.Throwable -> L60
            r5 = r6
        L33:
            if (r7 != 0) goto L3a
            com.facebook.appevents.PersistedEvents r7 = new com.facebook.appevents.PersistedEvents     // Catch: java.lang.Throwable -> L60
            r7.<init>()     // Catch: java.lang.Throwable -> L60
        L3a:
            monitor-exit(r9)
            return r7
        L3c:
            r3 = move-exception
            java.lang.String r8 = com.facebook.appevents.AppEventStore.TAG     // Catch: java.lang.Throwable -> L60
            java.lang.String r10 = "Got unexpected exception when removing events file: "
            android.util.Log.w(r8, r10, r3)     // Catch: java.lang.Throwable -> L60
            r5 = r6
            goto L33
        L47:
            r8 = move-exception
        L48:
            com.facebook.internal.Utility.closeQuietly(r5)     // Catch: java.lang.Throwable -> L60
            java.lang.String r8 = "AppEventsLogger.persistedevents"
            java.io.File r8 = r1.getFileStreamPath(r8)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L60
            r8.delete()     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L60
            goto L33
        L56:
            r3 = move-exception
            java.lang.String r8 = com.facebook.appevents.AppEventStore.TAG     // Catch: java.lang.Throwable -> L60
            java.lang.String r10 = "Got unexpected exception when removing events file: "
            android.util.Log.w(r8, r10, r3)     // Catch: java.lang.Throwable -> L60
            goto L33
        L60:
            r8 = move-exception
            monitor-exit(r9)
            throw r8
        L63:
            r2 = move-exception
        L64:
            java.lang.String r8 = com.facebook.appevents.AppEventStore.TAG     // Catch: java.lang.Throwable -> L84
            java.lang.String r10 = "Got unexpected exception while reading events: "
            android.util.Log.w(r8, r10, r2)     // Catch: java.lang.Throwable -> L84
            com.facebook.internal.Utility.closeQuietly(r5)     // Catch: java.lang.Throwable -> L60
            java.lang.String r8 = "AppEventsLogger.persistedevents"
            java.io.File r8 = r1.getFileStreamPath(r8)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L7a
            r8.delete()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L7a
            goto L33
        L7a:
            r3 = move-exception
            java.lang.String r8 = com.facebook.appevents.AppEventStore.TAG     // Catch: java.lang.Throwable -> L60
            java.lang.String r10 = "Got unexpected exception when removing events file: "
            android.util.Log.w(r8, r10, r3)     // Catch: java.lang.Throwable -> L60
            goto L33
        L84:
            r8 = move-exception
        L85:
            com.facebook.internal.Utility.closeQuietly(r5)     // Catch: java.lang.Throwable -> L60
            java.lang.String r10 = "AppEventsLogger.persistedevents"
            java.io.File r10 = r1.getFileStreamPath(r10)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L93
            r10.delete()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L93
        L92:
            throw r8     // Catch: java.lang.Throwable -> L60
        L93:
            r3 = move-exception
            java.lang.String r10 = com.facebook.appevents.AppEventStore.TAG     // Catch: java.lang.Throwable -> L60
            java.lang.String r11 = "Got unexpected exception when removing events file: "
            android.util.Log.w(r10, r11, r3)     // Catch: java.lang.Throwable -> L60
            goto L92
        L9d:
            r8 = move-exception
            r5 = r6
            goto L85
        La0:
            r2 = move-exception
            r5 = r6
            goto L64
        La3:
            r8 = move-exception
            r5 = r6
            goto L48
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.appevents.AppEventStore.readAndClearStore():com.facebook.appevents.PersistedEvents");
    }

    private static void saveEventsToDisk(PersistedEvents eventsToPersist) {
        ObjectOutputStream oos = null;
        Context context = FacebookSdk.getApplicationContext();
        try {
            try {
                ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(context.openFileOutput(PERSISTED_EVENTS_FILENAME, 0)));
                try {
                    oos2.writeObject(eventsToPersist);
                    Utility.closeQuietly(oos2);
                } catch (Exception e) {
                    e = e;
                    oos = oos2;
                    Log.w(TAG, "Got unexpected exception while persisting events: ", e);
                    try {
                        context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                    } catch (Exception e2) {
                    }
                    Utility.closeQuietly(oos);
                } catch (Throwable th) {
                    th = th;
                    oos = oos2;
                    Utility.closeQuietly(oos);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MovedClassObjectInputStream extends ObjectInputStream {
        private static final String ACCESS_TOKEN_APP_ID_PAIR_SERIALIZATION_PROXY_V1_CLASS_NAME = "com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair$SerializationProxyV1";
        private static final String APP_EVENT_SERIALIZATION_PROXY_V1_CLASS_NAME = "com.facebook.appevents.AppEventsLogger$AppEvent$SerializationProxyV1";

        public MovedClassObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override // java.io.ObjectInputStream
        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();
            if (resultClassDescriptor.getName().equals(ACCESS_TOKEN_APP_ID_PAIR_SERIALIZATION_PROXY_V1_CLASS_NAME)) {
                return ObjectStreamClass.lookup(AccessTokenAppIdPair.SerializationProxyV1.class);
            }
            if (resultClassDescriptor.getName().equals(APP_EVENT_SERIALIZATION_PROXY_V1_CLASS_NAME)) {
                return ObjectStreamClass.lookup(AppEvent.SerializationProxyV1.class);
            }
            return resultClassDescriptor;
        }
    }
}
