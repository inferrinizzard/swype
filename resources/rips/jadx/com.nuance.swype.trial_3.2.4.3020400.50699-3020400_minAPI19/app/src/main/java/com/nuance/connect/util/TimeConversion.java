package com.nuance.connect.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class TimeConversion {
    public static final long MILLIS_IN_DAY = 86400000;
    public static final long MILLIS_IN_HOUR = 3600000;
    public static final long MILLIS_IN_MINUTE = 60000;
    public static final long MILLIS_IN_SECOND = 1000;

    public static long convertDaysToMillis(int i) {
        return i * MILLIS_IN_DAY;
    }

    public static long convertDaysToTimeStamp(int i) {
        return convertMillisToTimeStamp(convertDaysToMillis(i));
    }

    public static long convertDaysToTimeStamp(int i, long j) {
        return convertMillisToTimeStamp(convertDaysToMillis(i), j);
    }

    public static long convertHoursToMillis(int i) {
        return i * MILLIS_IN_HOUR;
    }

    public static long convertHoursToTimeStamp(int i) {
        return convertMillisToTimeStamp(convertHoursToMillis(i));
    }

    public static long convertHoursToTimeStamp(int i, long j) {
        return convertMillisToTimeStamp(convertHoursToMillis(i), j);
    }

    public static long convertMillisToTimeStamp(long j) {
        return convertMillisToTimeStamp(j, getCurrentTime());
    }

    public static long convertMillisToTimeStamp(long j, long j2) {
        return j2 + j;
    }

    public static long convertMinutesToMillis(int i) {
        return i * MILLIS_IN_MINUTE;
    }

    public static long convertMinutesToTimeStamp(int i) {
        return convertMillisToTimeStamp(convertMinutesToMillis(i));
    }

    public static long convertMinutesToTimeStamp(int i, long j) {
        return convertMillisToTimeStamp(convertMinutesToMillis(i), j);
    }

    public static long convertSecondsToMillis(long j) {
        return 1000 * j;
    }

    public static long convertSecondsToTimeStamp(long j) {
        return convertMillisToTimeStamp(convertSecondsToMillis(j));
    }

    public static long convertSecondsToTimeStamp(long j, long j2) {
        return convertMillisToTimeStamp(convertSecondsToMillis(j), j2);
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String prettyDateFormat(long j) {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS", Locale.US).format(new Date(j));
    }
}
