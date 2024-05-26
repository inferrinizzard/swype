package com.crashlytics.android.answers;

import android.app.Activity;
import java.util.Collections;
import java.util.Map;

/* loaded from: classes.dex */
final class SessionEvent {
    public final Map<String, Object> customAttributes;
    public final String customType = null;
    public final Map<String, String> details;
    public final SessionEventMetadata sessionEventMetadata;
    private String stringRepresentation;
    public final long timestamp;
    public final Type type;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Type {
        CREATE,
        START,
        RESUME,
        SAVE_INSTANCE_STATE,
        PAUSE,
        STOP,
        DESTROY,
        ERROR,
        CRASH,
        INSTALL,
        CUSTOM
    }

    public static SessionEvent buildActivityLifecycleEvent(SessionEventMetadata metadata, Type type, Activity activity) {
        Map<String, String> details = Collections.singletonMap("activity", activity.getClass().getName());
        return buildEvent(metadata, type, details);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SessionEvent buildEvent(SessionEventMetadata metadata, Type type, Map<String, String> details) {
        return new SessionEvent(metadata, System.currentTimeMillis(), type, details, Collections.emptyMap());
    }

    private SessionEvent(SessionEventMetadata sessionEventMetadata, long timestamp, Type type, Map<String, String> details, Map<String, Object> customAttributes) {
        this.sessionEventMetadata = sessionEventMetadata;
        this.timestamp = timestamp;
        this.type = type;
        this.details = details;
        this.customAttributes = customAttributes;
    }

    public final String toString() {
        if (this.stringRepresentation == null) {
            StringBuilder sb = new StringBuilder("[").append(getClass().getSimpleName()).append(": timestamp=").append(this.timestamp).append(", type=").append(this.type).append(", details=").append(this.details.toString()).append(", customType=").append(this.customType).append(", customAttributes=").append(this.customAttributes.toString()).append(", metadata=[").append(this.sessionEventMetadata).append("]]");
            this.stringRepresentation = sb.toString();
        }
        return this.stringRepresentation;
    }
}
