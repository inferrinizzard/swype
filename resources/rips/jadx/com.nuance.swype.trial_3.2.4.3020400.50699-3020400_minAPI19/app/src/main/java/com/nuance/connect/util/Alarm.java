package com.nuance.connect.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class Alarm {
    public static final String ALARM_CLASS = "ALARM_CLASS";
    public static final String ALARM_TYPE = "ALARM_TYPE";
    private final int alarmId;
    private final String alarmType;
    private final Context context;
    private final Bundle extras;
    private final int flags;
    private final Class<?> handlingClass;
    private final boolean repeating;
    private final String requestingClassName;
    private boolean track;
    private final ExecutionTimeTracker tracker;
    private final long triggerTime;
    private final String uid;
    private final int wakeType;

    /* loaded from: classes.dex */
    public static class Builder {
        private final String alarmType;
        private final Context context;
        private Class<?> handlingClass;
        private final String requestingClassName;
        private ExecutionTimeTracker tracker;
        private String uid;
        private boolean wakeDevice;
        private long actualTriggerTime = 0;
        private int relativeTriggerDays = 0;
        private int relativeTriggerHours = 0;
        private int relativeTriggerMinutes = 0;
        private int relativeTriggerSeconds = 0;
        private int relativeTriggerMillis = 0;
        private Bundle extras = new Bundle();
        private int flags = 1073741824;
        private boolean repeating = false;
        private boolean track = true;

        public Builder(Context context, Class<?> cls, Class<?> cls2, ExecutionTimeTracker executionTimeTracker, String str) {
            this.context = context;
            this.requestingClassName = cls.toString();
            this.handlingClass = cls2;
            this.tracker = executionTimeTracker;
            this.alarmType = str;
            this.uid = "0";
            UserHandle myUserHandle = Process.myUserHandle();
            UserManager userManager = (UserManager) context.getSystemService("user");
            if (userManager != null) {
                this.uid = String.valueOf(userManager.getSerialNumberForUser(myUserHandle));
            }
        }

        public Alarm build() {
            return new Alarm(this);
        }

        public Builder days(int i) {
            this.relativeTriggerDays = i;
            return this;
        }

        public Builder extras(Bundle bundle) {
            this.extras = bundle;
            return this;
        }

        public Builder extras(Map<String, String> map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                this.extras.putString(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder flags(int i) {
            this.flags = i;
            return this;
        }

        public Builder hours(int i) {
            this.relativeTriggerHours = i;
            return this;
        }

        public Builder millis(int i) {
            this.relativeTriggerMillis = i;
            return this;
        }

        public Builder minutes(int i) {
            this.relativeTriggerMinutes = i;
            return this;
        }

        public Builder repeating(boolean z) {
            this.repeating = z;
            return this;
        }

        public Builder seconds(int i) {
            this.relativeTriggerSeconds = i;
            return this;
        }

        public Builder track(boolean z) {
            this.track = z;
            return this;
        }

        public Builder triggerTime(long j) {
            this.actualTriggerTime = j;
            return this;
        }

        public Builder wakeDevice(boolean z) {
            this.wakeDevice = z;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public interface ExecutionTimeTracker {
        void addAlarm(long j);
    }

    private Alarm(Builder builder) {
        this.extras = new Bundle();
        this.track = true;
        this.context = builder.context;
        this.requestingClassName = builder.requestingClassName;
        this.alarmType = builder.alarmType;
        this.uid = builder.uid;
        this.alarmId = generateAlarmId(this.requestingClassName, this.alarmType, this.uid);
        if (builder.actualTriggerTime == 0) {
            builder.actualTriggerTime = TimeConversion.convertMillisToTimeStamp(TimeConversion.convertDaysToMillis(builder.relativeTriggerDays) + TimeConversion.convertHoursToMillis(builder.relativeTriggerHours) + TimeConversion.convertMinutesToMillis(builder.relativeTriggerMinutes) + TimeConversion.convertSecondsToMillis(builder.relativeTriggerSeconds) + builder.relativeTriggerMillis);
        }
        this.triggerTime = builder.actualTriggerTime;
        if (builder.extras != null) {
            this.extras.putAll(builder.extras);
        }
        if (builder.wakeDevice) {
            this.wakeType = 0;
        } else {
            this.wakeType = 1;
        }
        this.flags = builder.flags;
        this.repeating = builder.repeating;
        this.track = builder.track;
        this.tracker = builder.tracker;
        this.handlingClass = builder.handlingClass;
    }

    private int generateAlarmId(String str, String str2, String str3) {
        int i = 23;
        for (byte b : (str + str2 + str3).getBytes(Charset.forName("UTF-8"))) {
            i = (i * 31) + b;
        }
        return i;
    }

    private PendingIntent generateIntent() {
        Intent intent = new Intent();
        if (this.handlingClass != null) {
            intent.setClass(this.context, this.handlingClass);
        }
        intent.putExtra("ALARM_CLASS", this.requestingClassName);
        intent.putExtra("ALARM_TYPE", this.alarmType);
        intent.putExtras(this.extras);
        return PendingIntent.getService(this.context, this.alarmId, intent, this.flags);
    }

    public void cancel() {
        ((AlarmManager) this.context.getSystemService("alarm")).cancel(generateIntent());
    }

    public void set() {
        ((AlarmManager) this.context.getSystemService("alarm")).set(this.wakeType, this.triggerTime, generateIntent());
        if (!this.track || this.tracker == null) {
            return;
        }
        this.tracker.addAlarm(this.triggerTime);
    }

    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS", Locale.US);
        StringBuilder sb = new StringBuilder("Alarm [");
        sb.append("alarmId=").append(this.alarmId).append(", ");
        sb.append("uid=").append(this.uid).append(", ");
        sb.append("launcher=").append(this.requestingClassName).append(", ");
        sb.append("alarm type=").append(this.alarmType).append(", ");
        sb.append("executes at=").append(simpleDateFormat.format(new Date(this.triggerTime)));
        sb.append("]");
        return sb.toString();
    }
}
