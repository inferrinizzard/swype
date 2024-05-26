package com.google.api.client.util;

import com.google.api.client.http.ExponentialBackOffPolicy;
import com.nuance.connect.util.TimeConversion;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class DateTime implements Serializable {
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final Pattern RFC3339_PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})([Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.\\d+)?)?([Zz]|([+-])(\\d{2}):(\\d{2}))?");
    private final boolean dateOnly;
    private final int tzShift;
    private final long value;

    public DateTime() {
        this(false, 0L, null);
    }

    private DateTime(boolean dateOnly, long value, Integer tzShift) {
        int offset;
        this.dateOnly = dateOnly;
        this.value = value;
        if (dateOnly) {
            offset = 0;
        } else {
            offset = tzShift == null ? TimeZone.getDefault().getOffset(value) / ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS : tzShift.intValue();
        }
        this.tzShift = offset;
    }

    public final String toStringRfc3339() {
        StringBuilder sb = new StringBuilder();
        Calendar dateTime = new GregorianCalendar(GMT);
        long localTime = this.value + (this.tzShift * TimeConversion.MILLIS_IN_MINUTE);
        dateTime.setTimeInMillis(localTime);
        appendInt(sb, dateTime.get(1), 4);
        sb.append('-');
        appendInt(sb, dateTime.get(2) + 1, 2);
        sb.append('-');
        appendInt(sb, dateTime.get(5), 2);
        if (!this.dateOnly) {
            sb.append('T');
            appendInt(sb, dateTime.get(11), 2);
            sb.append(':');
            appendInt(sb, dateTime.get(12), 2);
            sb.append(':');
            appendInt(sb, dateTime.get(13), 2);
            if (dateTime.isSet(14)) {
                sb.append('.');
                appendInt(sb, dateTime.get(14), 3);
            }
            if (this.tzShift == 0) {
                sb.append('Z');
            } else {
                int absTzShift = this.tzShift;
                if (this.tzShift > 0) {
                    sb.append('+');
                } else {
                    sb.append('-');
                    absTzShift = -absTzShift;
                }
                int tzHours = absTzShift / 60;
                int tzMinutes = absTzShift % 60;
                appendInt(sb, tzHours, 2);
                sb.append(':');
                appendInt(sb, tzMinutes, 2);
            }
        }
        return sb.toString();
    }

    public final String toString() {
        return toStringRfc3339();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DateTime)) {
            return false;
        }
        DateTime other = (DateTime) o;
        return this.dateOnly == other.dateOnly && this.value == other.value && this.tzShift == other.tzShift;
    }

    public final int hashCode() {
        long[] jArr = new long[3];
        jArr[0] = this.value;
        jArr[1] = this.dateOnly ? 1L : 0L;
        jArr[2] = this.tzShift;
        return Arrays.hashCode(jArr);
    }

    public static DateTime parseRfc3339(String str) throws NumberFormatException {
        int tzShift;
        Matcher matcher = RFC3339_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new NumberFormatException("Invalid date/time format: " + str);
        }
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2)) - 1;
        int day = Integer.parseInt(matcher.group(3));
        boolean isTimeGiven = matcher.group(4) != null;
        String tzShiftRegexGroup = matcher.group(9);
        boolean isTzShiftGiven = tzShiftRegexGroup != null;
        int hourOfDay = 0;
        int minute = 0;
        int second = 0;
        int milliseconds = 0;
        Integer tzShiftInteger = null;
        if (isTzShiftGiven && !isTimeGiven) {
            throw new NumberFormatException("Invalid date/time format, cannot specify time zone shift without specifying time: " + str);
        }
        if (isTimeGiven) {
            hourOfDay = Integer.parseInt(matcher.group(5));
            minute = Integer.parseInt(matcher.group(6));
            second = Integer.parseInt(matcher.group(7));
            if (matcher.group(8) != null) {
                int milliseconds2 = Integer.parseInt(matcher.group(8).substring(1));
                int fractionDigits = matcher.group(8).substring(1).length() - 3;
                milliseconds = (int) (milliseconds2 / Math.pow(10.0d, fractionDigits));
            }
        }
        Calendar dateTime = new GregorianCalendar(GMT);
        dateTime.set(year, month, day, hourOfDay, minute, second);
        dateTime.set(14, milliseconds);
        long value = dateTime.getTimeInMillis();
        if (isTimeGiven && isTzShiftGiven) {
            if (Character.toUpperCase(tzShiftRegexGroup.charAt(0)) == 'Z') {
                tzShift = 0;
            } else {
                tzShift = (Integer.parseInt(matcher.group(11)) * 60) + Integer.parseInt(matcher.group(12));
                if (matcher.group(10).charAt(0) == '-') {
                    tzShift = -tzShift;
                }
                value -= tzShift * TimeConversion.MILLIS_IN_MINUTE;
            }
            tzShiftInteger = Integer.valueOf(tzShift);
        }
        return new DateTime(!isTimeGiven, value, tzShiftInteger);
    }

    private static void appendInt(StringBuilder sb, int num, int numDigits) {
        if (num < 0) {
            sb.append('-');
            num = -num;
        }
        int x = num;
        while (x > 0) {
            x /= 10;
            numDigits--;
        }
        for (int i = 0; i < numDigits; i++) {
            sb.append('0');
        }
        if (num != 0) {
            sb.append(num);
        }
    }
}
